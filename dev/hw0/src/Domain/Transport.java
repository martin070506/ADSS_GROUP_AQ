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
            transportFile.leaveSupplier(truck.getCurrentWeight());
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


    public void removeItems(List<ProductPair> outgoingItems, int weightToRemove) throws Exceptions.ProductNotFoundOnTruckException {
        if (outgoingItems == null || outgoingItems.isEmpty())
            return;

        canRemoveAll(outgoingItems);

        this.truck.setCurrentWeight(truck.getCurrentWeight() - weightToRemove);
        this.truck.removeProducts(outgoingItems);
    }


    private void canRemoveAll(List<ProductPair> outgoingItems) throws Exceptions.ProductNotFoundOnTruckException {
        for (ProductPair outgoing : outgoingItems) {
            String name = outgoing.product.name();

            if (!truck.getProductPairs().containsKey(name))
                throw new Exceptions.ProductNotFoundOnTruckException(name);

            int availableAmount = truck.getProductPairs().get(name).getAmount();
            int requestedAmount = outgoing.getAmount();

            if (availableAmount < requestedAmount)
                throw new Exceptions.ProductNotFoundOnTruckException(name, requestedAmount, availableAmount);
        }
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
