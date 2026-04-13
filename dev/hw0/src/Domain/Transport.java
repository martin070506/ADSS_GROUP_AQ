package Domain;

import java.time.LocalDateTime;
import java.util.*;

public class Transport {

    private Date departureTime;
    private Truck truck;
    private Location source;
    private List<Destination> destinations;
    private List<Supplier> suppliers;
    private TransportFile transportFile;
    private Driver driver;
    private List<Truck> replacementTrucks;
    private Map<String,ProductPair> currentThingsHeld;
    private int currentThingsHeldWeight;
    private Map<String,Integer> totalItemsNeeded;



    public Transport(Truck truck,Driver driver,List<Destination> destinations,List<Truck> replacementTrucks) {
        this.replacementTrucks=replacementTrucks;
        this.truck = truck;
        this.driver=driver;
        this.destinations = destinations;
        this.departureTime = new Date();
        this.transportFile=new TransportFile();
        this.currentThingsHeld=new HashMap<>();
        this.currentThingsHeldWeight=0;
        this.suppliers=new ArrayList<>();
        this.totalItemsNeeded=calculateTotalThingsNeeded(destinations);

    }
    private Map<String,Integer> calculateTotalThingsNeeded(List<Destination> destinations) {
        // 1. Create the map to store totals
        Map<String, Integer> totals = new HashMap<>();

        // 2. Loop through every destination
        for (Destination dest : destinations) {
            // Assuming Destination has a getProducts() method that returns List<ProductPair>
            List<ProductPair> neededItems = dest.getProducts();

            if (neededItems != null) {
                for (ProductPair pair : neededItems) {
                    String productName = pair.getProduct().getName();
                    int amount = pair.getAmount();

                    // 3. Update the map:
                    // If the name exists, add the new amount to the old one.
                    // If it doesn't exist, start at 0 and add the amount.
                    totals.put(productName, totals.getOrDefault(productName, 0) + amount);
                }
            }
        }

        // 4. Display the results nicely
        return totals;
    }

    public void chooseSuppliersToVisit(List<Supplier> allSuppliers) {
        List<Supplier> selected = new ArrayList<>();
        Scanner reader = new Scanner(System.in);

        // 1. Calculate exactly what we need for the whole trip
        Map<String, Integer> requirements = this.totalItemsNeeded;

        displaySuppliers(allSuppliers);
        System.out.println("\nEnter supplier indices one by one. Type 'exit' to finish:");

        while (true) {
            System.out.print("Enter index or 'exit': ");
            String input = reader.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                // 2. CHECK: Before we let them exit, do these suppliers have enough stuff?
                if (hasEnoughStock(selected, requirements)) {
                    break; // Everything is good, exit the loop
                } else {
                    System.out.println("Wait! Your selected suppliers don't have enough items to fulfill the destinations.");
                    System.out.println("Please add more suppliers or type 'exit' again to force exit anyway.");
                    // Optional: You could ask them if they want to force exit here.
                    continue;
                }
            }

            processInput(input, allSuppliers, selected);
        }

        this.suppliers = selected;
    }

    // New Helper Method for Validation
    private boolean hasEnoughStock(List<Supplier> selectedSuppliers, Map<String, Integer> requirements) {
        // A. Calculate the total inventory of the SELECTED suppliers
        Map<String, Integer> availableStock = new HashMap<>();
        for (Supplier s : selectedSuppliers) {
            for (ProductPair pp : s.getProductsAvailable()) {
                String name = pp.getProduct().getName();
                availableStock.put(name, availableStock.getOrDefault(name, 0) + pp.getAmount());
            }
        }

        // B. Compare available vs required
        boolean isSufficient = true;
        System.out.println("\n--- Stock Verification ---");

        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
            String product = entry.getKey();
            int needed = entry.getValue();
            int available = availableStock.getOrDefault(product, 0);

            if (available < needed) {
                System.out.println("CRITICAL: " + product + " | Need: " + needed + " | Selected: " + available + " (MISSING: " + (needed - available) + ")");
                isSufficient = false;
            } else {
                System.out.println("OK: " + product + " (" + available + "/" + needed + ")");
            }
        }

        return isSufficient;
    }

    // Helper 1: Handles the UI display
    private void displaySuppliers(List<Supplier> suppliers) {
        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier s = suppliers.get(i);
            System.out.println(("[" + i + "] " + s.getSupplierLocation().getContactName()) + " (" + s.getSupplierLocation().getAddress() + ")");
            s.printInventory();
        }
    }

    // Helper 2: Handles the logic and validation
    private void processInput(String input, List<Supplier> all, List<Supplier> selected) {
        try {
            int index = Integer.parseInt(input);
            if (isValidIndex(index, all.size())) {
                Supplier choice = all.get(index);
                addIfNotDuplicate(choice, selected);
            } else {
                System.out.println("Error: Index out of bounds.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number or 'exit'.");
        }
    }

    // Helper 3: Specific logic checks (Very clean!)
    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    private void addIfNotDuplicate(Supplier choice, List<Supplier> selected) {
        if (!selected.contains(choice)) {
            selected.add(choice);
            System.out.println("Added: " + choice.getSupplierLocation().getContactName());
        } else {
            System.out.println("Supplier already selected.");
        }
    }
    public Truck getTruck() {
        return truck;
    }
    public int getCurrentThingsHeldWeight() {
        return currentThingsHeldWeight;
    }


    public void startShipmentProcess(){
        transportFile.addDate(this.departureTime);

        for (Supplier supplier : suppliers) {
            boolean isSuccess;
            isSuccess=supplier.handleShipment(this);
            if(!isSuccess){
                handleOverWeight(supplier);
            }
            transportFile.leaveSupplier(truck.getTruckWeight()+getCurrentThingsHeldWeight());
        }
        for(Destination destination : destinations){
            destination.handleShipment(this);
        }
    }

    private void handleOverWeight(Supplier problematicSupplier) {
        transportFile.overWeightAlert(truck.getTruckWeight()+getCurrentThingsHeldWeight());
        Scanner reader = new Scanner(System.in);
        System.out.println("!!! ALERT: Truck is overweight at " + problematicSupplier.getSupplierLocation().getContactName());
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
        System.out.println("Supplier " + supplier.getSupplierLocation().getContactName() + " skipped.");
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
