package Service;

import Domain.*;
import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;
import Exceptions.InsufficientTruckStockException;
import Exceptions.OverweightException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShipmentFacade {

    private int shipmentNumber;
    private final List<Supplier> suppliers;

    public ShipmentFacade(List<Supplier> suppliers) {

        this.suppliers = suppliers;
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void startShipment(Truck truck, Driver driver, Location source,
                              List<Destination> destinations, List<Truck> replacementTrucks) {

        Map<Supplier, List<ProductPair>> supplierAllocations = chooseSuppliersAndProducts();
        List<Supplier> suppliersAsList = new ArrayList<>(supplierAllocations.keySet());
        Transport transport = new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks,suppliersAsList);

        boolean shipmentFinish = false;
        while (!shipmentFinish)
            try {
                transport.processShipment();
                shipmentFinish = true;
            } catch (Exception e) {
                switch (e) {
                    case OverweightException oe -> {
                        System.out.println("⚠️ Overweight Problem: " + oe.getMessage());
                        /// problematic supplier is suppliers.getFirst()
                        handleOverWeight(suppliersAsList.getFirst(),transport);
                    }

                    case InsufficientSupplierStockException ise -> {
                        System.out.println("⚠️ Stock Problem: " + ise.getMessage());
                        handleInsufficientSupplierStock();
                    }

                    case InsufficientTruckStockException ise -> {
                        System.out.println("⚠️ Stock Problem: " + ise.getMessage());
                        handleInsufficientTruckStock();
                    }

                    case DomainException de -> {
                        System.out.println("General Domain Error: " + de.getMessage());
                        throw de;
                    }

                    default -> {
                        System.out.println("Critical System Error: " + e.getMessage());
                        throw e;
                    }
                }
            }
    }


    private Map<Supplier, List<ProductPair>> chooseSuppliersAndProducts() {
        Map<Supplier, List<ProductPair>> supplierAllocations = new LinkedHashMap<>();
        Scanner reader = new Scanner(System.in);

        showRequiredQuantities();
        displaySuppliers(suppliers);

        System.out.println("\nStep 1: Select Suppliers to visit");
        System.out.print("Enter indexes '1, 3, 4'.. or 'all': ");
        String input = reader.nextLine().trim();

        List<Supplier> selectedSuppliers = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            selectedSuppliers.addAll(suppliers);
        } else {
            String[] parts = input.split(",\\s*");
            for (String part : parts) {
                try {
                    int index = Integer.parseInt(part) - 1;
                    if (index >= 0 && index < suppliers.size()) {
                        Supplier s = suppliers.get(index);
                        if (!selectedSuppliers.contains(s)) selectedSuppliers.add(s);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        for (Supplier supplier : selectedSuppliers) {
            System.out.println(supplier.toString());

            List<ProductPair> productsToBuy = new ArrayList<>();

            while (true) {
                System.out.print("Enter product index (or 'done' to finish with this supplier): ");
                String prodInput = reader.nextLine().trim();

                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int prodIndex = Integer.parseInt(prodInput) - 1;
                    Product product = supplier.getProductByIndex(prodIndex);

                    System.out.print("Enter quantity for " + product.name() + ": ");
                    int amount = Integer.parseInt(reader.nextLine().trim());

                    if (amount > 0) {
                        productsToBuy.add(new ProductPair(product, amount));
                        System.out.println("Added to list.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input, please try again.");
                }
            }

            if (!productsToBuy.isEmpty()) {
                supplierAllocations.put(supplier, productsToBuy);
            }
        }

        return supplierAllocations;
    }


    private void showRequiredQuantities() {

    }

    private void handleOverWeight(Supplier problematicSupplier,Transport transport) {
        transport.getTransportFile().overWeightAlert(transport.getTruck().getCurrentWeight());
        Scanner reader = new Scanner(System.in);
        System.out.println("!!! ALERT: Truck is overweight at " + problematicSupplier.supplierLocation().contactName());
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
                    skipSupplier(problematicSupplier,transport);
                }
                case "2" ->{
                    visitDestinationEarly(transport);
                }
                case "3" ->{
                    manuallyRemoveItems(transport);
                }
                case "4" -> {
                    boolean swapped = replaceTruck(transport);
                    if (swapped) {
                        // Now that we have a bigger truck, try adding the items again
                        // If it's STILL overweight, the loop will run again automatically

                        if (transport.getTruck().getCurrentWeight() <= transport.getTruck().getMaxWeight()) {
                            resolved = true;
                            transport.removeSupplierFromTransport(problematicSupplier);
                        }
                    } else {
                        System.out.println("Returning to main menu...");
                        // resolved remains false, so the while loop shows options 1, 2, 3, 4 again
                    }
                }
                default -> {
                    System.out.println("Invalid choice. Skipping supplier by default to ensure safety.");
                    skipSupplier(problematicSupplier,transport);
                }
            }
            transport.processShipment();
        }

    }

    private void skipSupplier(Supplier supplier,Transport transport) {
        System.out.println("Supplier " + supplier.supplierLocation().contactName() + " skipped.");
        transport.removeSupplierFromTransport(supplier);
    }

    private void visitDestinationEarly(Transport transport) {
        Scanner reader = new Scanner(System.in);
        List<Destination> destinations = transport.getDestinations();
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
                Destination target = destinations.get(index);
                transport.removeDestinationFromTransport(target);// Removes from the future list
                target.handleShipment(transport.getTruck()); // Unloads items and reduces weight
                System.out.println("Emergency drop-off completed at " + target.getContactName());
            }
        } catch (Exception e) {
            System.out.println("Invalid selection. No drop-off performed.");
        }
    }

    private void manuallyRemoveItems(Transport transport) {
        Scanner reader = new Scanner(System.in);
        Map<String,ProductPair> currentThingsHeld=transport.getProductPairs();
        while (true) {

            if (currentThingsHeld.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            // 1. Show current inventory and status
            System.out.println("\n--- Truck Status: " + transport.getTruck().getCurrentWeight() + " / " + transport.getTruck().getMaxWeight() + " ---");
            currentThingsHeld.forEach((name, pair) ->
                    System.out.println("- " + name + ": " + pair.getAmount() + " units")
            );

            System.out.print("\nEnter product name to remove (or type 'done' to stop): ");
            String productName = reader.nextLine().trim();

            if (productName.equalsIgnoreCase("done")&& transport.getTruck().getCurrentWeight()<= transport.getTruck().getMaxWeight()) {
                break;
            }
            if (productName.equalsIgnoreCase("done")&& !(transport.getTruck().getCurrentWeight()<= transport.getTruck().getMaxWeight())) {
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
                        toRemove.add(new ProductPair(existing.product, amountToRemove));

                        int weightReduction = amountToRemove * existing.product.weight();

                        transport.removeItems(toRemove,weightReduction);
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

    private boolean replaceTruck(Transport transport) {
        Scanner reader = new Scanner(System.in);

        // 1. Filter trucks that are actually better than the current one
        List<Truck> candidates = new ArrayList<>();
        List<Truck> replacementTrucks = transport.getReplacementTrucks();
        for (Truck t : replacementTrucks) {
            if (t.getMaxWeight() > transport.getTruck().getMaxWeight()) {
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
                replacementTrucks.add(transport.getTruck());

                transport.getTruck().transferHoldingsToOtherTruck(newTruck);
                transport.replaceTruck(newTruck);

                replacementTrucks.remove(newTruck);


                System.out.println("Truck swapped successfully! New capacity: " + transport.getTruck().getMaxWeight());
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }

        return false;
    }

    private void handleInsufficientSupplierStock() {

    }

    private void handleInsufficientTruckStock() {

    }

    private void displaySuppliers(List<Supplier> supplierAllocations) {
        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < supplierAllocations.size(); i++) {
            Supplier supplier = supplierAllocations.get(i);
            System.out.println(("[" + (i + 1) + "] " + supplier.toString()));
        }
    }
}
