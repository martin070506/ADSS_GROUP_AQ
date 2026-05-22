package Presentation;

import Domain.*;
import Exceptions.*;
import Service.CompanyManager;

import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;

    // We fetch these from the services via the manager
    private final List<Truck> availableTrucks;
    private final List<Driver> availableDrivers;
    private final List<Location> allLocations;
    private final List<Supplier> allSuppliers;
    private final GlobalStorage globalStorage;

    public MainConsole(CompanyManager companyManager) {
        this.companyManager = companyManager;

        // These calls assume your CompanyManager has the "bridge" methods
        // to reach into the services/facades
        this.availableTrucks = companyManager.getTruckService().getAvailableTrucks();
        this.availableDrivers = companyManager.getDriverService().getAvailableDrivers();
        this.allLocations = companyManager.getAllLocations();
        this.allSuppliers = companyManager.getShipmentService().getSuppliers();

        this.globalStorage = new GlobalStorage();

        System.out.println("Welcome to the main console");
        System.out.println(availableDrivers.size() + " drivers available");
    }

    public void run() {
        System.out.println("Welcome to the Shipment System!");
        while (true) {
            System.out.println("\n----------------------------------");
            System.out.print("Would you like to start a new shipment? (yes/no): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) {
                System.out.println("Exiting the system. Goodbye!");
                throw new ConsoleEndException("Exiting the system. Goodbye!");
            } else if (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("y")) {
                System.out.println("Invalid input, please type 'yes' or 'no'.");
                continue;
            }

            Transport transport = initiateShipment();

            if (transport == null)
                continue;

            globalStorage.add(transport);
        }
    }

    public Transport initiateShipment() {
        boolean truckAndDriverMatch = false;
        Truck truck = null;
        Driver driver = null;

        while (!truckAndDriverMatch) {
            System.out.println("\n--- Select Truck ---");
            truck = chooseTruck(companyManager.getTruckService().getAvailableTrucks());

            System.out.println("\n--- Select Driver ---");
            driver = chooseDriver(companyManager.getDriverService().getAvailableDrivers());

            if (truck == null || driver == null) {
                System.out.println("Operation cancelled.");
                return null;
            }

            // Delegation to the service layer for validation
            truckAndDriverMatch = companyManager.getTruckService().canTruckTakeDriver(truck, driver);

            if (!truckAndDriverMatch) {
                System.out.println("Error: Driver's license does not match Truck min license. Try again.");
                availableTrucks.add(truck);
                availableDrivers.add(driver);
            }
        }

        Location source = selectSourceLocation(this.allLocations);
        Map<Supplier, List<ProductPair>> supplierAllocations = chooseSuppliersAndProducts(this.allSuppliers);

        // Call Service to create the actual Transport object
        Transport transport = companyManager.getShipmentService().executeCreateTransport(truck, driver, source,companyManager.getCurrentRequests(),companyManager.getTruckService().getAvailableTrucks(),  supplierAllocations);

        boolean shipmentFinish = false;
        while (!shipmentFinish) {
            try {
                // Use the manager to delegate processing logic
                companyManager.getShipmentService().processTransport(transport);
                shipmentFinish = true;

            } catch (OverweightException oe) {
                Supplier problematicSupplier = transport.getSuppliers().getFirst();
                handleOverWeight(transport, problematicSupplier, oe.getAddedProducts());

            } catch (InsufficientSupplierStockException ise) {
                System.out.println("Stock Problem: " + ise.getMessage());
                Supplier problematicSupplier = transport.getSuppliers().getFirst();
                System.out.println("Skipping supplier " + problematicSupplier.getName() + " due to insufficient stock.\n");
                transport.getTransportFile().skipSupplier(problematicSupplier.getName());
                transport.removeSupplierFromTransportAndFile(problematicSupplier);

            } catch (InsufficientTruckStockException ise) {
                System.out.println("Truck Stock Problem: " + ise.getMessage());
                Destination problematicDestination = transport.getDestinations().getFirst();
                System.out.println("Skipping destination " + problematicDestination.getContactName() + " due to missing items.");
                transport.getTransportFile().skipDestination(problematicDestination.getContactName());
                transport.removeDestinationFromTransport(problematicDestination);

            } catch (DomainException de) {
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

            // Release resources via services
            companyManager.finalizeShipment(transport);

            System.out.println("\nShipment completed successfully!");
        }

        return transport;
    }

    // --- OVERWEIGHT HANDLING LOGIC ---

    private void handleOverWeight(Transport transport, Supplier problematicSupplier, List<ProductPair> addedProducts) {
        transport.getTransportFile().overWeightAlert(transport.getTruck().getCurrentWeight());
        System.out.println("Truck is overweight at " + problematicSupplier.getName());

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
                    visitDestinationEarly(transport, addedProducts);
                    if (transport.getTruck().getCurrentWeight() <= transport.getTruck().getMaxWeight()) {
                        resolved = true;
                        transport.getTransportFile().leaveSupplier(problematicSupplier.getName(), transport.getTruck().getCurrentWeight());
                        transport.removeSupplierFromTransportButNotFile(problematicSupplier);
                    } else {
                        System.out.println("Truck is still overweight");
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
                    } else {
                        System.out.println("Truck is still overweight");
                    }
                }
                default -> {
                    System.out.println("Invalid choice. Skipping supplier.");
                    skipSupplier(problematicSupplier, transport);
                    resolved = true;
                }
            }
        }
    }

    private void skipSupplier(Supplier supplier, Transport transport) {
        System.out.println("Supplier " + supplier.getName() + " skipped.");
        transport.getTransportFile().skipSupplier(supplier.getName());
        List<ProductPair> thingsToRemove = transport.getSupplierAllocations().get(supplier);

        double weight = 0;
        for (ProductPair pair : thingsToRemove) {
            weight += pair.getAmount() * pair.product.weight();
        }

        try {
            transport.removeItems(thingsToRemove, weight);
        } catch (ProductNotFoundOnTruckException e) {
            System.out.println("Note: Items were not yet on truck.");
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
                        double weightReduction = amountToRemove * existing.product.weight();

                        try {
                            transport.removeItems(toRemove, weightReduction);
                            transport.getTransportFile().removeProductsFromAggregate(toRemove);
                            transport.getTransportFile().removeItemFromTruck(amountToRemove, productName);
                            System.out.println("Successfully removed " + amountToRemove + " units.");
                        } catch (ProductNotFoundOnTruckException e) {
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

    private void visitDestinationEarly(Transport transport, List<ProductPair> addedProducts) {
        List<Destination> destinations = transport.getDestinations();
        if (destinations.isEmpty()) {
            System.out.println("No destinations left to visit!");
            return;
        }

        System.out.println("Choose a destination to visit now:");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + destinations.get(i).getContactName());
        }

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < destinations.size()) {
                Destination target = destinations.get(index);
                transport.getTransportFile().arriveAtDestination(target.getContactName());
                target.handleShipment(transport.getTruck(), addedProducts);
                transport.getTransportFile().leaveDestination(target.getContactName());
                transport.removeDestinationFromTransport(target);
                System.out.println("Emergency drop-off completed at " + target.getContactName());
            } else {
                System.out.println("Destination not found!");
            }
        } catch (Exception e) {
            System.out.println("Invalid selection. No drop-off performed.");
        }
    }

    private boolean replaceTruck(Transport transport) {
        List<Truck> candidates = new ArrayList<>();
        // Fetch candidates from TruckService via manager
        List<Truck> replacementTrucks = companyManager.getTruckService().getAvailableTrucks();

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
            System.out.println("[" + (i + 1) + "] ID: " + candidates.get(i).getTruckNumber() + " | Capacity: " + candidates.get(i).getMaxWeight());
        }

        System.out.print("Type index to swap, or 'exit': ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) return false;

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < candidates.size()) {
                Truck newTruck = candidates.get(index);
                if (!companyManager.getTruckService().canTruckTakeDriver(newTruck, transport.getDriver())) {
                    System.out.println("Driver isn't eligible for this truck.");
                    return false;
                }

                // Swap logic
                this.availableTrucks.add(transport.getTruck());
                this.availableTrucks.remove(newTruck);

                transport.getTruck().transferHoldingsToOtherTruck(newTruck);
                transport.replaceTruck(newTruck);

                System.out.println("Truck swapped! New capacity: " + transport.getTruck().getMaxWeight());
                return transport.getTruck().getMaxWeight() >= transport.getTruck().getCurrentWeight();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
        return false;
    }

    // --- SELECTION HELPERS ---

    private Map<Supplier, List<ProductPair>> chooseSuppliersAndProducts(List<Supplier> suppliers) {
        Map<Supplier, List<ProductPair>> supplierAllocations = new LinkedHashMap<>();
        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < suppliers.size(); i++)
            System.out.println("[" + (i + 1) + "] " + suppliers.get(i).toString());

        System.out.print("\nSelect Suppliers (e.g., '1, 3' or 'all'): ");
        String input = scanner.nextLine().trim();
        List<Supplier> selected = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            selected.addAll(suppliers);
        } else {
            for (String part : input.split(",\\s*")) {
                try {
                    int idx = Integer.parseInt(part) - 1;
                    if (idx >= 0 && idx < suppliers.size()) selected.add(suppliers.get(idx));
                } catch (Exception ignored) {}
            }
        }

        for (Supplier s : selected) {
            System.out.println("\nProducts for: " + s.getName());
            List<ProductPair> productsToBuy = new ArrayList<>();
            while (true) {
                System.out.print("Product index (or 'done'): ");
                String prodInput = scanner.nextLine().trim();
                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int pIdx = Integer.parseInt(prodInput) - 1;
                    Product p = s.getProductByIndex(pIdx);
                    System.out.print("Quantity: ");
                    int qty = Integer.parseInt(scanner.nextLine().trim());
                    if (qty > 0) productsToBuy.add(new ProductPair(p, qty));
                } catch (Exception e) { System.out.println("Invalid input."); }
            }
            if (!productsToBuy.isEmpty()) supplierAllocations.put(s, productsToBuy);
        }
        return supplierAllocations;
    }

    private Truck chooseTruck(List<Truck> trucks) {
        if (trucks.isEmpty()) return null;
        for (int i = 0; i < trucks.size(); i++) {
            Truck t = trucks.get(i);
            System.out.println("[" + (i + 1) + "] Truck " + t.getTruckNumber() + " | Cap: " + t.getMaxWeight());
        }
        while (true) {
            System.out.print("Truck index: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= trucks.size()) return companyManager.getTruckService().reserveTruck(choice-1);
            } catch (Exception ignored) {}
        }
    }

    private Driver chooseDriver(List<Driver> drivers) {
        if (drivers.isEmpty()) return null;
        for (int i = 0; i < drivers.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + drivers.get(i).driverName() + " (Lic: " + drivers.get(i).license() + ")");
        }
        while (true) {
            System.out.print("Driver index: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= drivers.size()) {
                    return companyManager.getDriverService().selectDriver(choice - 1);
                }
            } catch (Exception ignored) {}
        }
    }

    private Location selectSourceLocation(List<Location> locations) {
        for (int i = 0; i < locations.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + locations.get(i).address());
        }
        while (true) {
            System.out.print("Location index: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (choice >= 0 && choice < locations.size()) return locations.get(choice);
            } catch (Exception ignored) {}
        }
    }
}