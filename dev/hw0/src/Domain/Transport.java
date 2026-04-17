package Domain;

import java.time.LocalDate;
import java.util.*;

public class Transport {

    private final LocalDate departureTime;
    private Truck truck;
    private Driver driver;
    private final Location source;
    private List<Destination> destinations;
    private Map<Supplier, List<ProductPair>> supplierAllocations; // אנחנו לא צריכים לקחת את כל ה Available שיש לו, רק את מה שאנחנו צריכים
    private List<Truck> replacementTrucks;
    private TransportFile transportFile;
    private List<Supplier> suppliers;



    public TransportFile getTransportFile() {
        return transportFile;
    }

    public Transport(LocalDate departureTime, Truck truck, Driver driver, Location source,
                     List<Destination> destinations, Map<Supplier, List<ProductPair>> supplierAllocations,
                     List<Truck> replacementTrucks, List<Supplier> suppliers) {

        this.departureTime = departureTime;
        this.truck = truck;
        this.driver = driver;
        this.source = source;
        this.destinations = destinations;
        this.supplierAllocations = supplierAllocations;
        this.replacementTrucks = replacementTrucks;
        this.suppliers = suppliers;
        this.transportFile = new TransportFile(departureTime,this);

    }

    public void processShipment() {

        while(!suppliers.isEmpty()) {
            Supplier supplier = suppliers.getFirst();
            supplier.handleShipment(supplierAllocations.get(supplier),truck);
            suppliers.remove(supplier);
            supplierAllocations.remove(supplier);

        }

        while (!destinations.isEmpty()) {
            destinations.getFirst().handleShipment(truck);
            destinations.removeFirst();
        }
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }
    public Map<Supplier, List<ProductPair>> getSupplierAllocations() {
        return supplierAllocations;
    }

    public Location getSource() {
        return source;
    }


    public Truck getTruck() {
        return truck;
    }


    public void removeItems(List<ProductPair> outgoingItems, int weightToRemove) {
        if (outgoingItems == null || outgoingItems.isEmpty()) return;

        // 1. Use helper to verify the whole set exists
        if (!canRemoveAll(outgoingItems)) {
            return; // Helper already printed the specific error message
        }

        // 2. Execution: Since we know it's safe, perform the subtraction
        this.truck.setCurrentWeight(truck.getCurrentWeight() - weightToRemove);
        this.truck.removeProducts(outgoingItems);

    }


    private boolean canRemoveAll(List<ProductPair> outgoingItems) {
        for (ProductPair outgoing : outgoingItems) {
            String name = outgoing.product.name();

            // Check if item exists at all
            if (truck.getProductPairs().containsKey(name)) {
                System.out.println("Error: Product '" + name + "' not found on truck.");
                return false;
            }

            // Check if we have enough quantity
            if (truck.getProductPairs().get(name).getAmount() < outgoing.getAmount()) {
                System.out.println("Error: Not enough quantity for '" + name + "'.");
                return false;
            }
        }
        return true; // All items passed the test
    }



    public void removeSupplierFromTransportAndFile(Supplier supplier) {
        suppliers.remove(supplier);
        transportFile.removeLocation(supplier);
        transportFile.removeProductsFromAggregate(supplierAllocations.get(supplier));
        supplierAllocations.remove(supplier);
    }

    public void removeSupplierFromTransportButNotFile(Supplier supplier) {
        supplierAllocations.remove(supplier);
        suppliers.remove(supplier);
    }

    public void removeDestinationFromTransport(Destination destination) {
        destinations.remove(destination);
        transportFile.removeDestination(destination);
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public Driver getDriver() {
        return driver;
    }

    public Map<String,ProductPair> getProductPairs() {
        return truck.getProductPairs();
    }

    public List<Truck> getReplacementTrucks() {
        return replacementTrucks;
    }

    public void replaceTruck(Truck truck) {
        this.truck = truck;
        transportFile.changeTruck(truck);
    }
}
