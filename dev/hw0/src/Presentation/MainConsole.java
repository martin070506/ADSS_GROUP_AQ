package Presentation;

import Domain.*;
import Service.CompanyManager;

import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;
    private final List<Truck> availableTrucks;
    private final List<Driver> availableDrivers;
    private final List<Location> allLocations;
    private final List<Supplier> allSuppliers;

    public MainConsole(CompanyManager companyManager, List<Truck> availableTrucks,
                       List<Driver> availableDrivers, List<Location> allLocations,
                       List<Supplier> allSuppliers) {
        this.companyManager = companyManager;
        this.availableTrucks = availableTrucks;
        this.availableDrivers = availableDrivers;
        this.allLocations = allLocations;
        this.allSuppliers = allSuppliers;
    }

    public Transport run() {
        System.out.println("Welcome to the Shipment System!");
        return initiateShipment();
    }

    private Transport initiateShipment() {
        boolean truckAndDriverMatch = false;
        Truck truck = null;
        Driver driver = null;

        while (!truckAndDriverMatch) {
            System.out.println("\n--- Select Truck ---");
            truck = chooseTruck(this.availableTrucks);

            System.out.println("\n--- Select Driver ---");
            driver = chooseDriver(this.availableDrivers);

            if (truck == null || driver == null) {
                System.out.println("Operation cancelled.");
                return null;
            }

            truckAndDriverMatch = truck.getMinLicense() <= driver.license();
            if (!truckAndDriverMatch)
                System.out.println("Error: Driver's license does not match Truck min license. Try again.");
        }

        Location source = selectSourceLocation(this.allLocations);

        Map<Supplier, List<ProductPair>> supplierAllocations = chooseSuppliersAndProducts(this.allSuppliers);

        Transport transport = companyManager.createShipment(truck, driver, source, supplierAllocations);

        boolean shipmentFinish = false;
        while (!shipmentFinish) {
            try {
                companyManager.processTransport(transport);
                shipmentFinish = true;

            } catch (Exceptions.OverweightException oe) {
                Supplier problematicSupplier = transport.getSuppliers().getFirst();
                handleOverWeight(transport, problematicSupplier);

            } catch (Exceptions.InsufficientSupplierStockException ise) {
                System.out.println("⚠️ Stock Problem: " + ise.getMessage());
                Supplier problematicSupplier = transport.getSuppliers().getFirst();
                System.out.println("Skipping supplier " + problematicSupplier.supplierLocation().contactName() + " due to insufficient stock.\n");
                transport.getTransportFile().skipSupplier(problematicSupplier.supplierLocation().contactName());
                transport.removeSupplierFromTransportAndFile(problematicSupplier);

            } catch (Exceptions.InsufficientTruckStockException ise) {
                System.out.println("⚠️ Truck Stock Problem: " + ise.getMessage());
                Destination problematicDestination = transport.getDestinations().getFirst();
                System.out.println("Skipping destination " + problematicDestination.getContactName() + " due to missing items.");
                transport.getTransportFile().skipDestination(problematicDestination.getContactName());

                transport.removeDestinationFromTransport(problematicDestination);
            } catch (Exceptions.DomainException de) {
                System.out.println("General Domain Error: " + de.getMessage());
                break;

            } catch (Exception e) {
                System.out.println("Critical System Error: " + e.getMessage());
                break;
            }
        }

        if (shipmentFinish) {
            System.out.println("Shipment finished." + "\n\n\nTransport File : \n");
            System.out.println(transport.getTransportFile().toString());
            companyManager.finishShipment(truck, driver);
            System.out.println("\n✅ Shipment completed successfully!");
        }
        return transport;
    }

    private void handleOverWeight(Transport transport, Supplier problematicSupplier) {
        transport.getTransportFile().overWeightAlert(transport.getTruck().getCurrentWeight());
        System.out.println("\uD83D\uDED1 Truck is overweight at " + problematicSupplier.supplierLocation().contactName());

        boolean resolved = false;
        while (!resolved) {
            System.out.println("1. Skip this supplier (Remove its items from truck)");
            System.out.println("2. Emergency Drop-off (Visit a destination now to unload)");
            System.out.println("3. Fine-tune: Remove specific items");
            System.out.println("4. Switch Truck");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    skipSupplier(problematicSupplier, transport);
                    resolved = true;
                }
                case "2" -> {
                    visitDestinationEarly(transport);
                    if (transport.getTruck().getCurrentWeight() <= transport.getTruck().getMaxWeight()) {
                        resolved = true;
                        transport.getTransportFile().leaveSupplier(problematicSupplier.getName(), transport.getTruck().getCurrentWeight());
                        transport.removeSupplierFromTransportButNotFile(problematicSupplier);
                    } else {
                        System.out.println("After visiting this location early, truck is still overweight");
                    }
                }
                case "3" -> {
                    manuallyRemoveItems(transport);
                    transport.removeSupplierFromTransportButNotFile(problematicSupplier);
                    transport.getTransportFile().leaveSupplier(problematicSupplier.getName(), transport.getTruck().getCurrentWeight());
                    resolved = true;
                }
                case "4" -> {
                    boolean swapped = replaceTruck(transport);
                    if (swapped && transport.getTruck().getCurrentWeight() <= transport.getTruck().getMaxWeight()) {
                        resolved = true;
                        transport.getTransportFile().leaveSupplier(problematicSupplier.getName(), transport.getTruck().getCurrentWeight());
                        transport.removeSupplierFromTransportButNotFile(problematicSupplier);
                    }
                }
                default -> {
                    System.out.println("Invalid choice. Skipping supplier by default to ensure safety.");
                    skipSupplier(problematicSupplier, transport);
                    resolved = true;
                }
            }
            System.out.println();
        }
    }

    private void skipSupplier(Supplier supplier, Transport transport) {

        System.out.println("Supplier " + supplier.supplierLocation().contactName() + " skipped.");
        transport.getTransportFile().skipSupplier(supplier.supplierLocation().contactName());
        List<ProductPair> thingsToRemove = transport.getSupplierAllocations().get(supplier);

        int weight = 0;
        for (ProductPair pair : thingsToRemove) {
            weight += pair.getAmount() * pair.product.weight();
        }

        try {
            transport.removeItems(thingsToRemove, weight);
        } catch (Exceptions.ProductNotFoundOnTruckException e) {
            System.out.println("Note: Items were not yet on truck, skipping physical removal.");
        }

        transport.removeSupplierFromTransportAndFile(supplier);
    }

    private void manuallyRemoveItems(Transport transport) {
        Map<String, ProductPair> currentThingsHeld = transport.getProductPairs();
        while (true) {
            if (currentThingsHeld.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            System.out.println("\n--- Truck Status: " + transport.getTruck().getCurrentWeight() + " / " + transport.getTruck().getMaxWeight() + " ---");
            currentThingsHeld.forEach((name, pair) ->
                    System.out.println("- " + name + ": " + pair.getAmount() + " units")
            );

            System.out.print("\nEnter product name to remove (or type 'done'): ");
            String productName = scanner.nextLine().trim();

            if (productName.equalsIgnoreCase("done")) {
                if (transport.getTruck().getCurrentWeight() <= transport.getTruck().getMaxWeight()) break;
                else System.out.println("Cannot finish while still overweight");
                continue;
            }

            if (currentThingsHeld.containsKey(productName)) {
                ProductPair existing = currentThingsHeld.get(productName);
                System.out.print("How many to remove? (Max " + existing.getAmount() + "): ");
                try {
                    int amountToRemove = Integer.parseInt(scanner.nextLine().trim());
                    if (amountToRemove > 0 && amountToRemove <= existing.getAmount()) {
                        List<ProductPair> toRemove = new ArrayList<>();
                        toRemove.add(new ProductPair(existing.product, amountToRemove));
                        int weightReduction = amountToRemove * existing.product.weight();

                        try {
                            transport.removeItems(toRemove, weightReduction);
                            transport.getTransportFile().removeProductsFromAggregate(toRemove);
                            System.out.println("Successfully removed " + amountToRemove + " units of " + productName + ".");
                            transport.getTransportFile().removeItemFromTruck(amountToRemove, productName);
                        } catch (Exceptions.ProductNotFoundOnTruckException e) {
                            System.out.println(e.getMessage());
                        }

                    } else {
                        System.out.println("Invalid amount.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            } else {
                System.out.println("Product not found on truck.");
            }
        }
    }

    private void visitDestinationEarly(Transport transport) {
        List<Destination> destinations = transport.getDestinations();
        if (destinations.isEmpty()) {
            System.out.println("No destinations left to visit!");
            return;
        }

        System.out.println("Choose a destination to visit now:");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println("[" + (i+1) + "] " + destinations.get(i).getContactName());
        }

        try {
            int index = Integer.parseInt(scanner.nextLine().trim())-1;
            if (index >= 0 && index < destinations.size()) {
                Destination target = destinations.get(index);
                transport.getTransportFile().arriveAtDestination(target.getContactName());
                target.handleShipment(transport.getTruck());
                transport.getTransportFile().leaveDestination(target.getContactName());
                transport.removeDestinationFromTransport(target);
                System.out.println("Emergency drop-off completed at " + target.getContactName());
            }
        } catch (Exception e) {
            System.out.println("Invalid selection. No drop-off performed.");
        }
    }

    private boolean replaceTruck(Transport transport) {
        List<Truck> candidates = new ArrayList<>();
        List<Truck> replacementTrucks = transport.getReplacementTrucks();
        for (Truck t : replacementTrucks) {
            if (t.getMaxWeight() > transport.getTruck().getMaxWeight()) {
                candidates.add(t);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("No larger trucks available.");
            return false;
        }

        System.out.println("\n--- Available Larger Trucks ---");
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println("[" + (i+1) + "] ID: " + candidates.get(i).getTruckNumber() + " | Capacity: " + candidates.get(i).getMaxWeight());
        }
        System.out.println("Type index to swap, or 'exit':");

        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) return false;

        try {
            int index = Integer.parseInt(input)-1;
            if (index >= 0 && index < candidates.size()) {
                Truck newTruck = candidates.get(index);

                replacementTrucks.add(transport.getTruck());
                transport.getTruck().transferHoldingsToOtherTruck(newTruck);

                this.availableTrucks.add(transport.getTruck());
                this.availableTrucks.remove(newTruck);

                transport.replaceTruck(newTruck);
                replacementTrucks.remove(newTruck);

                System.out.println("Truck swapped! New capacity: " + transport.getTruck().getMaxWeight());

                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
        return false;
    }
    private Map<Supplier, List<ProductPair>> chooseSuppliersAndProducts(List<Supplier> suppliers) {
        Map<Supplier, List<ProductPair>> supplierAllocations = new LinkedHashMap<>();

        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < suppliers.size(); i++)
            System.out.println("[" + (i + 1) + "] " + suppliers.get(i).toString());


        System.out.println("\nStep 1: Select Suppliers to visit");
        System.out.print("Enter indexes '1, 3, 4'.. or 'all': ");
        String input = scanner.nextLine().trim();

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
            System.out.println("\nSelected Supplier: " + supplier.supplierLocation().contactName());
            List<ProductPair> productsToBuy = new ArrayList<>();

            while (true) {
                System.out.print("Enter product index (or 'done' to finish with this supplier): ");
                String prodInput = scanner.nextLine().trim();

                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int prodIndex = Integer.parseInt(prodInput) - 1;
                    Product product = supplier.getProductByIndex(prodIndex);

                    System.out.print("Enter quantity for " + product.name() + ": ");
                    int amount = Integer.parseInt(scanner.nextLine().trim());

                    if (amount > 0) {
                        boolean productExists = false;
                        for (int i = 0; i < productsToBuy.size(); i++) {
                            ProductPair currentPair = productsToBuy.get(i);
                            if (currentPair.product.equals(product)) {
                                int updatedAmount = currentPair.getAmount() + amount;
                                productsToBuy.set(i, new ProductPair(product, updatedAmount));
                                productExists = true;
                                break;
                            }
                        }
                        if (!productExists) {
                            productsToBuy.add(new ProductPair(product, amount));
                        }
                    }
                    else
                        System.out.println("Invalid input, Enter positive quantity.");
                } catch (Exception e) {
                    System.out.println("Invalid input, please try again.");
                }
            }
            if (!productsToBuy.isEmpty()) {
                supplierAllocations.put(supplier, productsToBuy);
            }
        }

        System.out.println("\n\n");
        return supplierAllocations;
    }
    private Truck chooseTruck(List<Truck> availableTrucks) {
        if (availableTrucks.isEmpty()) {
            System.out.println("There are no trucks available.");
            return null;
        }

        for (int i = 0; i < availableTrucks.size(); i++) {
            Truck t = availableTrucks.get(i);
            System.out.println("[" + (i + 1) + "] Truck " + t.getTruckNumber() + " | Model: " + t.getModel() +
                    " | Capacity: " + (t.getMaxWeight() - t.getCurrentWeight()) + " | Min License: " + t.getMinLicense());
        }

        while (true) {
            System.out.print("Enter Truck index: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= availableTrucks.size()) {
                    return availableTrucks.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice, try again.");
        }
    }
    private Driver chooseDriver(List<Driver> availableDrivers) {
        if (availableDrivers.isEmpty()) {
            System.out.println("There are no Drivers available.");
            return null;
        }

        for (int i = 0; i < availableDrivers.size(); i++) {
            Driver d = availableDrivers.get(i);
            // שורה אחת לכל נהג
            System.out.println("[" + (i + 1) + "] " + d.driverName() + " | License Level: " + d.license());
        }

        while (true) {
            System.out.print("Enter Driver index: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= availableDrivers.size()) {
                    return availableDrivers.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice, try again.");
        }
    }
    private Location selectSourceLocation(List<Location> locations) {
        System.out.println("\n--- Select Source Location ---");
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            // נניח שיש למחלקה Location מתודות כמו שצריך (אם לא, תחליף ל-toString() שלך)
            System.out.println("[" + (i + 1) + "] " + loc.address() + " (Contact: " + loc.contactName() + ", Phone: " + loc.phoneNumber() + ")");
        }

        while (true) {
            System.out.print("Enter location index: ");
            try {
                int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (index >= 0 && index < locations.size()-1)
                    return locations.get(index);
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid input! Please enter a valid number.");
        }
    }
}