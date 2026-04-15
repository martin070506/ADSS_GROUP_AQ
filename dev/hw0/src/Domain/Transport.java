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

        for(Supplier supplier : suppliers) {
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






//
//    private Map<String,Integer> sumQuantities(List<Destination> destinations) {
//
//        Map<String, Integer> totals = new HashMap<>();
//
//        for (Destination dest : destinations) {
//            List<ProductPair> requiredProducts = dest.getProducts();
//
//            if (requiredProducts != null)
//                for (ProductPair pair : requiredProducts) {
//                    String productName = pair.getProduct().getName();
//                    int amount = pair.getAmount();
//
//                    totals.put(productName, totals.getOrDefault(productName, 0) + amount);
//                }
//        }
//
//        return totals;
//    }

//    // New Helper Method for Validation
//    private boolean hasEnoughStock(List<Supplier> selectedSuppliers, Map<String, Integer> requirements) {
//        // A. Calculate the total inventory of the SELECTED supplierAllocations
//        Map<String, Integer> availableStock = new HashMap<>();
//        for (Supplier s : selectedSuppliers) {
//            for (ProductPair pp : s.productsAvailable()) {
//                String name = pp.getProduct().getName();
//                availableStock.put(name, availableStock.getOrDefault(name, 0) + pp.getAmount());
//            }
//        }
//
//        // B. Compare available vs required
//        boolean isSufficient = true;
//        System.out.println("\n--- Stock Verification ---");
//
//        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
//            String product = entry.getKey();
//            int needed = entry.getValue();
//            int available = availableStock.getOrDefault(product, 0);
//
//            if (available < needed) {
//                System.out.println("CRITICAL: " + product + " | Need: " + needed + " | Selected: " + available + " (MISSING: " + (needed - available) + ")");
//                isSufficient = false;
//            } else {
//                System.out.println("OK: " + product + " (" + available + "/" + needed + ")");
//            }
//        }
//
//        return isSufficient;
//    }

//    // Helper 1: Handles the UI display
//    private void displaySuppliers(List<Supplier> supplierAllocations) {
//        System.out.println("\n--- Available Suppliers ---");
//        for (int i = 0; i < supplierAllocations.size(); i++) {
//            Supplier s = supplierAllocations.get(i);
//            System.out.println(("[" + i + "] " + s.supplierLocation().getContactName()) + " (" + s.supplierLocation().getAddress() + ")");
//            s.printInventory();
//        }
//    }

//    private void processInput(String input, List<Supplier> all, List<Supplier> selected) {
//        try {
//            int index = Integer.parseInt(input);
//            if (isValidIndex(index, all.size())) {
//                Supplier choice = all.get(index);
//                addIfNotDuplicate(choice, selected);
//            }
//            else
//                System.out.println("Error: Index out of bounds.");
//
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid input! Please enter a number or 'exit'.");
//        }
//    }
//
//    // Helper 3: Specific logic checks (Very clean!)
//    private boolean isValidIndex(int index, int size) {
//        return index >= 0 && index < size;
//    }

//    private void addIfNotDuplicate(Supplier choice, List<Supplier> selected) {
//        if (!selected.contains(choice)) {
//            selected.add(choice);
//            System.out.println("Added: " + choice.supplierLocation().getContactName());
//        }
//        else
//            System.out.println("Supplier already selected.");
//
//    }
    public Truck getTruck() {
        return truck;
    }











    public boolean removeItems(List<ProductPair> outgoingItems, int weightToRemove) {
        if (outgoingItems == null || outgoingItems.isEmpty()) return false;

        // 1. Use helper to verify the whole set exists
        if (!canRemoveAll(outgoingItems)) {
            return false; // Helper already printed the specific error message
        }

        // 2. Execution: Since we know it's safe, perform the subtraction
        this.truck.setCurrentWeight(truck.getCurrentWeight() - weightToRemove);
        this.truck.removeProducts(outgoingItems);

        return true;
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
        supplierAllocations.remove(supplier);
        suppliers.remove(supplier);
        transportFile.removeLocation(supplier);
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
