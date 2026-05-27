package Presentation;

import Service.CompanyManager;
import java.util.*;

public class AdminConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;

    public AdminConsole(CompanyManager companyManager) {
        this.companyManager = companyManager;
    }

    public void start() {
        System.out.println("=== LOGISTICS MANAGEMENT SYSTEM ===");
        System.out.println("1. Load Automatic Demo Data");
        System.out.println("2. Manual Data Entry");
        System.out.print("Choice: ");

        if (scanner.nextLine().trim().equals("1")) {
            companyManager.loadDemoData();
            System.out.println("Demo Data Loaded Successfully.");
        } else {
            manualSetup();
        }

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBranchRequest();
                case "2" -> resupplySupplier();
                case "3" -> manualSupplierSetup();
                case "4" -> addManualStoreLocation();
                case "5" -> manualDriverSetup();
                case "6" -> manualTruckSetup();
                case "7" -> runShipmentCycle();
                case "8" -> deleteBranchRequest();
                case "9" -> updateBranchRequest();
                case "0" -> {
                    System.out.println("Exiting system... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===============================");
        System.out.println("       OPERATIONS MENU");
        System.out.println("===============================");
        System.out.println("[1] Add Request for Branch");
        System.out.println("[2] Resupply a Supplier");
        System.out.println("[3] Add New Supplier");
        System.out.println("[4] Add New Store Location");
        System.out.println("[5] Add New Driver");
        System.out.println("[6] Add New Truck");
        System.out.println("[7] START SHIPMENT CONSOLE");
        System.out.println("[8] Delete Request for Branch");
        System.out.println("[9] Update Request for Branch");
        System.out.println("[0] Exit");
        System.out.print(">> Select Option: ");
    }

    private void runShipmentCycle() {
        MainConsole shipmentConsole = new MainConsole(companyManager);
        try {
            shipmentConsole.run();
        } catch (Exceptions.ConsoleEndException e) {
            System.out.println("Returned to Admin Menu.");
        } catch (Exception e) {
            System.out.println("Shipment Console Error: " + e.getMessage());
        }
    }

    private void addBranchRequest() {
        List<String> branches = companyManager.getAllLocationsDisplay();
        if (branches.isEmpty()) {
            System.out.println("Error: No branch locations available.");
            return;
        }

        System.out.println("\nSelect Branch:");
        for (int i = 0; i < branches.size(); i++)
            System.out.println("[" + i + "] " + branches.get(i));
        int locationId = promptInt("Enter Location ID: ");
        String locationName = branches.get(locationId);

        Map<String, Integer> requestedProductsIds = new HashMap<>();
        List<String> catalog = companyManager.getProductCatalogDisplay();

        while (true) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < catalog.size(); i++)
                System.out.println("[" + i + "] " + catalog.get(i));

            System.out.print("Select Product ID (or 'done'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int productId = Integer.parseInt(input);
                if (productId >= 0 && productId < catalog.size()) {
                    int qty = promptInt("Quantity needed: ");
                    String productName = catalog.get(productId);
                    requestedProductsIds.put(productName, requestedProductsIds.getOrDefault(productName, 0) + qty);
                } else {
                    System.out.println("Invalid Product ID.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }

        if (!requestedProductsIds.isEmpty()) {
            try {
                companyManager.addRequest(locationName, requestedProductsIds);
                System.out.println("Request added for branch.");
            } catch (Exception e) {
                System.out.println("Failed to add request: " + e.getMessage());
            }
        }
    }

    private void resupplySupplier() {
        Map<Integer, String> suppliers = companyManager.getAllSuppliersDisplay();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }

        System.out.println("\nSelect Supplier to Restock:");
        suppliers.forEach((id, name) -> System.out.println("[ID: " + id + "] " + name));
        int sId = promptInt("Enter Supplier ID: ");

        List<String> catalog = companyManager.getProductCatalogDisplay();
        System.out.println("Select Product:");
        for (int i = 0; i < catalog.size(); i++)
            System.out.println("[" + i + "] " + catalog.get(i));
        int pId = promptInt("Enter Product ID: ");
        int qty = promptInt("Amount to add: ");

        try {
            companyManager.resupplySupplier(sId, catalog.get(pId), qty);
            System.out.println("Stock updated.");
        } catch (Exception e) {
            System.out.println("Failed to update stock: " + e.getMessage());
        }
    }

    private void deleteBranchRequest() {
        List<String> activeBranches = companyManager.getActiveRequestLocationsDisplay();
        if (activeBranches.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        System.out.println("Which branch would you like to view?");
        for (int i = 0; i < activeBranches.size(); i++)
            System.out.println("[" + i + "] " + activeBranches.get(i));
        int locationId = promptInt("Enter Location ID: ");

        String request = activeBranches.get(locationId);
        if (request == null) {
            System.out.println("No request found for this branch.");
            return;
        }

        System.out.println("\nBranch Request:" + request);

        try {
            companyManager.removeRequest(request);
            System.out.println("Request removed.");
        } catch (Exception e) {
            System.out.println("Failed to remove request: " + e.getMessage());
        }
    }

    private void updateBranchRequest() {
        List<String> activeBranches = companyManager.getActiveRequestLocationsDisplay();
        if (activeBranches.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        System.out.println("Which branch would you like to view?");
        for (int i = 0; i < activeBranches.size(); i++)
            System.out.println("[" + i + "] " + activeBranches.get(i));
        int locationId = promptInt("Enter Location ID: ");

        String request = activeBranches.get(locationId);

        List<String> catalog = companyManager.getProductCatalogDisplay();

        while (true) {
            System.out.print("\nType 'add', 'remove', or 'done': ");
            String action = scanner.nextLine().trim();

            if (action.equalsIgnoreCase("done")) break;

            if (action.equalsIgnoreCase("add")) {
                System.out.println("Select Product:");
                for (int i = 0; i < catalog.size(); i++)
                    System.out.println("[" + i + "] " + catalog.get(i));
                int pId = promptInt("Product ID: ");
                int qty = promptInt("Amount to add: ");

                try {
                    companyManager.updateRequestAddProduct(request, catalog.get(pId), qty);
                    System.out.println("Added.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

            } else if (action.equalsIgnoreCase("remove")) {
                Map<String, Integer> productsInRequest = companyManager.getProductsInRequestDisplay(request);
                if (productsInRequest.isEmpty()) {
                    System.out.println("No products available to remove.");
                    continue;
                }

                System.out.println("Select Product to remove:");
                List<String> products = new ArrayList<>(productsInRequest.keySet());
                int i = 0;
                for (String product : products)
                    System.out.println("[" + i++ + "] " + product + " (" + productsInRequest.get(product) + " units left)");
                int pId = promptInt("Product ID: ");
                int qty = promptInt("Amount to remove: ");

                try {
                    companyManager.updateRequestRemoveProduct(request, products.get(pId), qty);
                    System.out.println("Removed.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    // --- MANUAL SETUP MODULES ---

    private void manualSetup() {
        manualProductSetup();
        manualDriverSetup();
        manualTruckSetup();
    }

    private void manualProductSetup() {
        System.out.println("\n> Define Product Catalog");
        while (true) {
            System.out.print("Product Name (or 'done'): ");
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("done")) break;
            int weight = promptInt("Unit Weight: ");
            companyManager.addProductToCatalog(name, weight);
        }
    }

    private void manualDriverSetup() {
        System.out.print("\nAdd Driver? (y/n): ");
        while (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            int lic = promptInt("License Level (1-3): ");
            companyManager.addDriver(name, lic); // פניה ל-Manager בלבד
            System.out.print("Add another driver? (y/n): ");
        }
    }

    private void manualTruckSetup() {
        System.out.print("\nAdd Truck? (y/n): ");
        while (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            int id = promptInt("Truck ID: ");
            System.out.print("Model: ");
            String model = scanner.nextLine().trim();
            int weight = promptInt("Net Weight: ");
            int max = promptInt("Max Capacity: ");
            int lic = promptInt("License Required (1-3): ");
            companyManager.addTruck(id, model, weight, max, lic); // פניה ל-Manager בלבד
            System.out.print("Add another truck? (y/n): ");
        }
    }

    private void addManualStoreLocation() {
        System.out.print("Address: ");
        String addr = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Contact: ");
        String contact = scanner.nextLine().trim();
        companyManager.addBranch(addr, phone, contact);
        System.out.println("Store added.");
    }

    private void manualSupplierSetup() {
        System.out.print("Address: ");
        String addr = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Contact: ");
        String contact = scanner.nextLine().trim();

        Map<String, Integer> stockIds = new HashMap<>();
        List<String> catalog = companyManager.getProductCatalogDisplay();

        catalog.forEach((pName) -> {
            System.out.print("Supply " + pName + "? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                int qty = promptInt("Quantity: ");
                stockIds.put(pName, qty);
            }
        });

        try {
            companyManager.registerSupplier(addr, phone, contact, stockIds);
            System.out.println("Supplier registered.");
        } catch (Exception e) {
            System.out.println("Failed to register supplier: " + e.getMessage());
        }
    }

    // --- UTILS ---
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