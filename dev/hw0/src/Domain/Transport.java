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
    // private Map<String,Integer> requiredQuantities; // זה כבר רשום אצל ה supplierAllocations


    public Transport(LocalDate departureTime, Truck truck, Driver driver, Location source,
                     List<Destination> destinations, Map<Supplier, List<ProductPair>> supplierAllocations,
                     List<Truck> replacementTrucks) {

        this.departureTime = departureTime;
        this.truck = truck;
        this.driver = driver;
        this.source = source;
        this.destinations = destinations;
        this.supplierAllocations = supplierAllocations;
        this.replacementTrucks = replacementTrucks;
        this.transportFile = new TransportFile(departureTime);
        // this.requiredQuantities = sumQuantities(destinations);
    }

    public void processShipment() {

        supplierAllocations.entrySet().removeIf(entry -> {
            entry.getKey().handleShipment(entry.getValue(), truck);
            return true;
        });

        while (!destinations.isEmpty()) {
            destinations.getFirst().handleShipment(truck);
            destinations.removeFirst();
        }
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

    private void handleOverWeight(Supplier problematicSupplier) {
        transportFile.overWeightAlert(truck.getTruckWeight()+getCurrentThingsHeldWeight());
        Scanner reader = new Scanner(System.in);
        System.out.println("!!! ALERT: Truck is overweight at " + problematicSupplier.supplierLocation().getContactName());
        System.out.println("1. Skip this supplier (Remove its items from truck)");
        System.out.println("2. Emergency Drop-off (Visit a destination now to unload)");
        System.out.println("3. Fine-tune: Remove specific items");
        System.out.println("4. Switch Truck");

        boolean resolved=false;
        while (!resolved) {
            System.out.print("Choose an option: ");
            String choice = reader.nextLine();

            switch (choice) {
                case "1" -> {
                    skipSupplier(problematicSupplier);
                }
                case "2" ->{
                    visitDestinationEarly();
                    problematicSupplier.handleShipment(this);
                }
                case "3" ->{
                    manuallyRemoveItems();
                    problematicSupplier.handleShipment(this);
                }
                case "4" -> {
                    boolean swapped = replaceTruck();
                    if (swapped) {
                        // Now that we have a bigger truck, try adding the items again
                        // If it's STILL overweight, the loop will run again automatically
                        problematicSupplier.handleShipment(this);
                        if (currentThingsHeldWeight <= truck.getAllowedWeight()) {
                            resolved = true;
                        }
                    } else {
                        System.out.println("Returning to main menu...");
                        // resolved remains false, so the while loop shows options 1, 2, 3, 4 again
                    }
                }
                default -> {
                    System.out.println("Invalid choice. Skipping supplier by default to ensure safety.");
                    skipSupplier(problematicSupplier);
                }
            }
        }

    }

    private boolean replaceTruck() {
        Scanner reader = new Scanner(System.in);

        // 1. Filter trucks that are actually better than the current one
        List<Truck> candidates = new ArrayList<>();
        for (Truck t : replacementTrucks) {
            if (t.getAllowedWeight() > this.currentThingsHeldWeight) {
                candidates.add(t);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("No larger trucks are currently available in the fleet.");
            return false;
        }

        // 2. Display candidates
        System.out.println("\n--- Available Larger Trucks ---");
        for (int i = 0; i < candidates.size(); i++) {
            Truck t = candidates.get(i);
            System.out.println("[" + i + "] ID: " + t.getTruckNumber() + " | Capacity: " + t.getMaxWeight());
        }
        System.out.println("Type the index to swap, or 'exit' to go back.");

        // 3. Handle selection
        String input = reader.nextLine();
        if (input.equalsIgnoreCase("exit")) return false;

        try {
            int index = Integer.parseInt(input);
            if (index >= 0 && index < candidates.size()) {
                Truck newTruck = candidates.get(index);

                // Swap logic: Put the old truck back in available, take the new one out
                this.replacementTrucks.add(this.truck);
                this.truck = newTruck;
                this.replacementTrucks.remove(newTruck);


                System.out.println("Truck swapped successfully! New capacity: " + this.truck.getMaxWeight());
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }

        return false;
    }

    private void skipSupplier(Supplier supplier) {
        System.out.println("Supplier " + supplier.supplierLocation().getContactName() + " skipped.");
    }

    private void visitDestinationEarly() {
        Scanner reader = new Scanner(System.in);
        if (destinations.isEmpty()) {
            System.out.println("No destinations left to visit!");
            return;
        }

        System.out.println("Choose a destination to visit now:");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println("[" + i + "] " + destinations.get(i).getContactName());
        }

        try {
            int index = Integer.parseInt(reader.nextLine());
            if (index >= 0 && index < destinations.size()) {
                Destination target = destinations.remove(index); // Removes from the future list
                target.handleShipment(this); // Unloads items and reduces weight
                System.out.println("Emergency drop-off completed at " + target.getContactName());
            }
        } catch (Exception e) {
            System.out.println("Invalid selection. No drop-off performed.");
        }
    }

    private void manuallyRemoveItems() {
        Scanner reader = new Scanner(System.in);
        while (true) {
            if (currentThingsHeld.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            // 1. Show current inventory and status
            System.out.println("\n--- Truck Status: " + currentThingsHeldWeight + " / " + truck.getAllowedWeight() + " ---");
            currentThingsHeld.forEach((name, pair) ->
                    System.out.println("- " + name + ": " + pair.getAmount() + " units")
            );

            System.out.print("\nEnter product name to remove (or type 'done' to stop): ");
            String productName = reader.nextLine().trim();

            if (productName.equalsIgnoreCase("done")&& currentThingsHeldWeight<= getTruck().getAllowedWeight()) {
                break;
            }
            if (productName.equalsIgnoreCase("done")&& !(currentThingsHeldWeight<= getTruck().getAllowedWeight())) {
                System.out.println("Cannot finish while still overweight");
            }

            if (currentThingsHeld.containsKey(productName)) {
                ProductPair existing = currentThingsHeld.get(productName);
                System.out.print("How many " + productName + " to remove? (Max " + existing.getAmount() + "): ");

                try {
                    int amountToRemove = Integer.parseInt(reader.nextLine());

                    if (amountToRemove > 0 && amountToRemove <= existing.getAmount())
                    {
                        List<ProductPair> toRemove = new ArrayList<>();
                        toRemove.add(new ProductPair(existing.getProduct(), amountToRemove));

                        int weightReduction = amountToRemove * existing.getProduct().getWeight();

                        removeItems(toRemove, weightReduction);
                    } else {
                        System.out.println("Invalid amount. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number for the amount.");
                }
            } else {
                System.out.println("Product '" + productName + "' not found on the truck.");
            }
        }

    }

    public boolean removeItems(List<ProductPair> outgoingItems, int weightToRemove) {
        if (outgoingItems == null || outgoingItems.isEmpty()) return false;

        // 1. Use helper to verify the whole set exists
        if (!canRemoveAll(outgoingItems)) {
            return false; // Helper already printed the specific error message
        }

        // 2. Execution: Since we know it's safe, perform the subtraction
        this.currentThingsHeldWeight -= weightToRemove;

        for (ProductPair outgoing : outgoingItems) {
            String name = outgoing.getProduct().getName();
            ProductPair existing = currentThingsHeld.get(name);

            int newAmount = existing.getAmount() - outgoing.getAmount();

            if (newAmount <= 0) {
                currentThingsHeld.remove(name);
                System.out.println("Removed " + name + " entirely from truck.");
            } else {
                existing.setAmount(newAmount);
                System.out.println("Reduced " + name + " to: " + newAmount);
            }
        }

        return true;
    }


    private boolean canRemoveAll(List<ProductPair> outgoingItems) {
        for (ProductPair outgoing : outgoingItems) {
            String name = outgoing.getProduct().getName();

            // Check if item exists at all
            if (!currentThingsHeld.containsKey(name)) {
                System.out.println("Error: Product '" + name + "' not found on truck.");
                return false;
            }

            // Check if we have enough quantity
            if (currentThingsHeld.get(name).getAmount() < outgoing.getAmount()) {
                System.out.println("Error: Not enough quantity for '" + name + "'.");
                return false;
            }
        }
        return true; // All items passed the test
    }

    public void addItems(List<ProductPair> incomingItems,int weight) {
        if (incomingItems == null) return;
        currentThingsHeldWeight+=weight;
        for (ProductPair incoming : incomingItems) {
            String name = incoming.getProduct().getName();

            // The "Smart" part:
            if (currentThingsHeld.containsKey(name)) {
                // 1. Get the existing pair
                ProductPair existing = currentThingsHeld.get(name);

                // 2. Update the amount inside the existing object
                int newAmount = existing.getAmount() + incoming.getAmount();
                existing.setAmount(newAmount);

                System.out.println("Updated " + name + " total to: " + newAmount);
            } else {
                // 3. If it's new, add a NEW ProductPair so we don't mess with the original reference
                ProductPair newPair = new ProductPair(incoming.getProduct(), incoming.getAmount());
                currentThingsHeld.put(name, newPair);

                System.out.println("Added new product: " + name + " (" + incoming.getAmount() + ")");
            }
        }
    }


}
