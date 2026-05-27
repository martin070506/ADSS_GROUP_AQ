package Presentation;

import Exceptions.*;
import Service.CompanyManager;

import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;

    public MainConsole(CompanyManager companyManager) {
        this.companyManager = companyManager;
        System.out.println("Welcome to the main console");
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

            initiateShipment();
        }
    }

    public void initiateShipment() {
        String truckIndex = chooseTruck();
        if (truckIndex == null) return;

        String driverIndex = chooseDriver();
        if (driverIndex == null) return;

        String sourceIndex = selectSourceLocation();
        if (sourceIndex == null) return;

        Map<Integer, Map<String, Integer>> supplierAllocationsIds = chooseSuppliersAndProductsIndices();
        if (supplierAllocationsIds.isEmpty()) return;

        try {
            int transportId = companyManager.createTransportAndGetId(truckIndex, driverIndex, sourceIndex, supplierAllocationsIds);
            processShipmentFlow(transportId);
        } catch (DomainException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Critical System Error: " + e.getMessage());
        }
    }

    private void processShipmentFlow(int transportId) {
        boolean shipmentFinish = false;
        while (!shipmentFinish) {
            try {
                companyManager.processTransport(transportId);
                shipmentFinish = true;
            } catch (OverweightException oe) {
                handleOverweightUI(transportId);
            } catch (InsufficientSupplierStockException | InsufficientTruckStockException ise) {
                System.out.println("Stock Problem: " + ise.getMessage());
                try {
                    companyManager.handleStockException(transportId, ise);
                } catch (Exception e) {
                    System.out.println("Error handling stock: " + e.getMessage());
                }
            } catch (DomainException de) {
                System.out.println("General Domain Error: " + de.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("General Error: " + e.getMessage());
                break;
            }
        }
        if (shipmentFinish) {
            System.out.println("Shipment finished successfully!");
        }
    }

    public void handleOverweightUI(int transportId) {
        String supplierName = companyManager.getFirstSupplierName(transportId);
        System.out.println("Truck is overweight at " + supplierName);
        System.out.println("1. Skip this supplier");
        System.out.println("2. Emergency Drop-off");
        System.out.println("3. Fine-tune: Remove specific items");
        System.out.println("4. Switch Truck");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        try {
            if (choice.equals("3")) {
                Map<String, Integer> itemsToRemoveIds = getItemsToRemoveUI(transportId);
                companyManager.resolveOverweightWithFineTuning(transportId, itemsToRemoveIds);
            } else {
                companyManager.resolveOverweightIssue(transportId, choice);
            }
        } catch (NoDestinationForEmergencyDropOffException e) {
            System.out.println("No destination available for emergency drop-off.");
        } catch (Exception e) {
            System.out.println("Action failed: " + e.getMessage());
        }
    }

    private Map<String, Integer> getItemsToRemoveUI(int transportId) {
        Map<String, Integer> itemsToRemove = new HashMap<>();
        Map<String, Integer> currentItemsDisplay = companyManager.getLoadedProductsDisplay(transportId);

        while (true) {
            if (currentItemsDisplay.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            System.out.println("\nCurrent loaded items:");
            int i = 0;
            List<String> productNames = new ArrayList<>(currentItemsDisplay.keySet());
            for (String productName : productNames) {
                System.out.println("[" + i++ + "] " + productName + " (" + currentItemsDisplay.get(productName) + " units)");
            }

            System.out.print("Enter product ID to remove (or type 'done'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int productId = Integer.parseInt(input);
                String productName = productNames.get(productId);
                if (currentItemsDisplay.containsKey(productName)) {
                    int amt = promptInt("Amount to remove: ");
                    if (amt > 0) {
                        itemsToRemove.put(productName, itemsToRemove.getOrDefault(productName, 0) + amt);
                        System.out.println("Added to removal list.");
                    } else {
                        System.out.println("Invalid amount.");
                    }
                } else {
                    System.out.println("Product ID not found on truck.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return itemsToRemove;
    }

    private String chooseTruck() {
        List<String> trucks = companyManager.getAvailableTrucksDisplay();
        if (trucks.isEmpty()) {
            System.out.println("No trucks available.");
            return null;
        }

        System.out.println("\n--- Available Trucks ---");
        for (int i = 0; i < trucks.size(); i++)
            System.out.println("[" + i + "] " + trucks.get(i));
        while (true) {
            int choice = promptInt("Enter Truck ID: ");
            if (choice >= 0 && choice < trucks.size()) return trucks.get(choice);
            System.out.println("Invalid Truck ID.");
        }
    }

    private String chooseDriver() {
        List<String> drivers = companyManager.getAvailableDriversDisplay();
        if (drivers.isEmpty()) {
            System.out.println("No eligible drivers available for this truck.");
            return null;
        }

        System.out.println("\n--- Available Drivers ---");
        for (int i = 0; i < drivers.size(); i++)
            System.out.println("[" + i + "] " + drivers.get(i));
        while (true) {
            int choice = promptInt("Enter Driver ID: ");
            if (choice >= 0 && choice < drivers.size()) return drivers.get(choice);
            System.out.println("Invalid Driver ID.");
        }
    }

    private String selectSourceLocation() {
        List<String> locations = companyManager.getAllLocationsDisplay();
        if (locations.isEmpty()) {
            System.out.println("No locations available.");
            return null;
        }

        System.out.println("\n--- Select Source Location ---");
        for (int i = 0; i < locations.size(); i++)
            System.out.println("[" + i + "] " + locations.get(i));
        while (true) {
            int choice = promptInt("Enter Location ID: ");
            if (choice >= 0 && choice < locations.size()) return locations.get(choice);
            System.out.println("Invalid Location ID.");
        }
    }

    private Map<Integer, Map<String, Integer>> chooseSuppliersAndProductsIndices() {
        Map<Integer, Map<String, Integer>> allocations = new HashMap<>();
        Map<Integer, String> suppliers = companyManager.getAllSuppliersDisplay();

        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return allocations;
        }

        System.out.println("\n--- Available Suppliers ---");
        suppliers.forEach((idx, display) -> System.out.println("[Index: " + idx + "] " + display));

        System.out.print("\nSelect Supplier Indices (comma separated, e.g., '0, 2' or 'all'): ");
        String input = scanner.nextLine().trim();
        List<Integer> selectedSupplierIndices = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            selectedSupplierIndices.addAll(suppliers.keySet());
        } else {
            for (String part : input.split(",\\s*")) {
                try {
                    int idx = Integer.parseInt(part);
                    if (suppliers.containsKey(idx)) {
                        selectedSupplierIndices.add(idx);
                    }
                } catch (Exception ignored) {}
            }
        }

        for (Integer supplierIdx : selectedSupplierIndices) {
            System.out.println("\nProducts for Supplier: " + suppliers.get(supplierIdx));

            List<String> supplierCatalog = companyManager.getSupplierProductsDisplay(supplierIdx);

            if (supplierCatalog.isEmpty()) {
                System.out.println("This supplier has no products in stock.");
            }

            Map<String, Integer> productsToBuy = new HashMap<>();
            while (true) {
                System.out.println("\nAvailable Products at this supplier:");
                for (int i = 0; i < supplierCatalog.size(); i++)
                    System.out.println("[" + i + "] " + supplierCatalog.get(i));

                System.out.print("Enter Product Index (or type 'done'): ");
                String prodInput = scanner.nextLine().trim();
                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int pIdx = Integer.parseInt(prodInput);
                    if (pIdx >= 0 && pIdx < supplierCatalog.size()) {
                        int qty = promptInt("Quantity: ");
                        if (qty > 0) {
                            String productName = supplierCatalog.get(pIdx);
                            productsToBuy.put(productName, productsToBuy.getOrDefault(productName, 0) + qty);
                        } else {
                            System.out.println("Quantity must be greater than 0.");
                        }
                    } else {
                        System.out.println("Invalid Product Index. This supplier does not carry this item.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                }
            }
            if (!productsToBuy.isEmpty()) allocations.put(supplierIdx, productsToBuy);
        }
        return allocations;
    }

    private int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }
}