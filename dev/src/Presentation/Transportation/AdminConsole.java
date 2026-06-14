package Presentation.Transportation;


import Service.Transportation.*;

import java.util.*;

public class AdminConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;
    private final ProductCatalogService productService;
    private final TransportManagerService transportService;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final BranchService branchService;
    private final LocationService locationService;

    public AdminConsole(CompanyManager companyManager, ProductCatalogService productService, TransportManagerService transportService, SupplierService supplierService, RequestService requestService, TruckService truckService, DriverService driverService, BranchService branchService, LocationService locationService) {
        this.companyManager = companyManager;
        this.productService = productService;
        this.transportService = transportService;
        this.supplierService = supplierService;
        this.requestService = requestService;
        this.truckService = truckService;
        this.driverService = driverService;
        this.branchService = branchService;
        this.locationService = locationService;
    }

    public void start() {
        System.out.println("=== LOGISTICS MANAGEMENT SYSTEM ===");
        System.out.println("1. Load Automatic Demo Data");
        System.out.println("2. Manual Data Entry");
        System.out.print("Choice: ");

        if (scanner.nextLine().trim().equals("1")) {
            DemoDataLoader.load(companyManager,productService,truckService,driverService,branchService);
            System.out.println("Demo Data Loaded Successfully.");
        } else { manualSetup(); }

        boolean running = true;
        while (running) {
            displayMenu();

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addBranchRequest();
                case "2" -> resupplySupplier();
                case "3" -> manualSupplierSetup();
                case "4" -> addManualBranch();
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
        System.out.println("[4] Add New Branch");
        System.out.println("[5] Add New Driver");
        System.out.println("[6] Add New Truck");
        System.out.println("[7] START SHIPMENT CONSOLE");
        System.out.println("[8] Delete Request for Branch");
        System.out.println("[9] Update Request for Branch");
        System.out.println("[0] Exit");
        System.out.print(">> Select Option: ");
    }

    private void runShipmentCycle() {
        MainConsole shipmentConsole = new MainConsole(companyManager, transportService, supplierService, productService,
                truckService, driverService, locationService);
        try { shipmentConsole.initiateShipment(); }
        catch (Exceptions.ConsoleEndException e) { System.out.println("Returned to Admin Menu."); }
        catch (Exception e) { System.out.println("Shipment Console Error: " + e.getMessage()); }
    }

    private void addBranchRequest() {
        List<String> branches = branchService.getBranchesDisplay();
        if (branches.isEmpty()) {
            System.out.println("Error: No branch locations available.");
            return;
        }

        int branchIndex;
        while (true) {
            System.out.println("\nSelect Branch Id:");
            for (int i = 0; i < branches.size(); i++) {
                System.out.println(branches.get(i));
            }
            branchIndex = promptInt("Enter Branch Id: ") ;

            if (branchIndex > -1) {
                break;
            }
            System.out.println("Invalid Branch Index. Please try again.");
        }

        // FIX: Keep the map matching primitive IDs/Indices in the UI layer
        Map<Integer, Integer> requestedProductQuantities = new HashMap<>();
        List<String> productCatalog = productService.getProductsDisplay();

        while (true) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productCatalog.size(); i++) {
                System.out.println( productCatalog.get(i));
            }

            System.out.print("Select Products (or 'done'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int productId = Integer.parseInt(input);
                if (productId >= 0 && productId < productCatalog.size()) {
                    int qty = promptInt("Quantity needed: ");
                    if (qty > 0) {
                        requestedProductQuantities.put(productId, requestedProductQuantities.getOrDefault(productId, 0) + qty);
                    } else {
                        System.out.println("Quantity must be greater than 0.");
                    }
                } else {
                    System.out.println("Invalid Product Index.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }

        if (!requestedProductQuantities.isEmpty()) {
            try {
                // FIX: Pass raw index values down. Service layer maps these to real objects.
                companyManager.addRequest(branchIndex, requestedProductQuantities);
                System.out.println("Request successfully executed.");
            } catch (Exception e) {
                System.out.println("Failed to execute request: " + e.getMessage());
            }
        }
    }

    private void resupplySupplier() {
        List<String> suppliers = supplierService.getSuppliersDisplay();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }

        System.out.println("\nSelect Supplier to Restock:");
        for (int i = 0; i < suppliers.size(); i++)
            System.out.println( suppliers.get(i));
        int sId = promptInt("Enter Supplier: ");

        List<String> catalog = productService.getProductsDisplay();
        System.out.println("Select Product:");
        for (int i = 0; i < catalog.size(); i++)
            System.out.println(catalog.get(i));
        int pId = promptInt("Enter Product: ");

        int qty;
        while (true) {
            qty = promptInt("Amount to add: ");
            if (qty > 0) break;
            System.out.println("Quantity must be greater than 0.");
        }

        try {
            companyManager.resupplySupplier(sId, pId, qty);
            System.out.println("Stock updated.");
        } catch (Exception e) { System.out.println("Failed to update stock: " + e.getMessage()); }
    }

    private void deleteBranchRequest() {
        List<String> activeBranches = requestService.getActiveRequestLocations();
        if (activeBranches.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        int branchIndex;
        while (true) {
            System.out.println("Which request would you like to remove?");
            for (int i = 0; i < activeBranches.size(); i++)
                System.out.println( activeBranches.get(i));
            branchIndex = promptInt("Enter Branch: ") ;
            if (branchIndex >= 0 && branchIndex < activeBranches.size()) break;
            System.out.println("Invalid Branch Index. Please try again.");
        }
        System.out.println("\nBranch Request:" + activeBranches.get(branchIndex));
        try {
            requestService.removeRequestByIndex(branchIndex);
            System.out.println("Request removed.");
        } catch (Exception e) { System.out.println("Failed to remove request: " + e.getMessage()); }
    }

    private void updateBranchRequest() {
        List<String> activeRequestsLocation = requestService.getActiveRequestLocations();
        if (activeRequestsLocation.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        int branchIndex;
        do {
            System.out.println("Which request would you like to update?");
            for (int i = 0; i < activeRequestsLocation.size(); i++)
                System.out.println(activeRequestsLocation.get(i));
            branchIndex = promptInt("Enter Branch: ");
        } while (!requestService.isValidActiveRequestIndex(branchIndex));

        List<String> catalog = productService.getProductsDisplay();

        while (true) {
            System.out.print("\nType 'add', 'remove', or 'done': ");
            String action = scanner.nextLine().trim();
            if (action.equalsIgnoreCase("done")) break;

            if (action.equalsIgnoreCase("add")) {
                System.out.println("Select Product:");
                for (int i = 0; i < catalog.size(); i++)
                    System.out.println(catalog.get(i));
                int pId = promptInt("Product: ");
                int qty = promptInt("Amount to add: ");

                try {
                    companyManager.updateRequestAddProduct(branchIndex, pId, qty);
                    System.out.println("Added.");
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }

            } else if (action.equalsIgnoreCase("remove")) {
                Map<String, Integer> productsInRequest = companyManager.getProductsInRequestDisplay(branchIndex);
                if (productsInRequest.isEmpty()) {
                    System.out.println("No products available to remove.");
                    continue;
                }

                System.out.println("Select Product to remove:");
                List<String> products = new ArrayList<>(productsInRequest.keySet());
                int i = 1;
                for (String product : products)
                    System.out.println("[" + i++ + "] " + product + " (" + productsInRequest.get(product) + " units left)");
                int pId = promptInt("Product: ");
                int qty = promptInt("Amount to remove: ");

                try {
                    companyManager.updateRequestRemoveProduct(branchIndex, pId, qty);
                    System.out.println("Removed.");
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
            } else { System.out.println("Invalid command."); }
        }
    }

    private void manualSetup() {
        System.out.println("Add Products -");
        while (manualProductSetup());
        System.out.println("Add Drivers -");
        do {
            manualDriverSetup();
            System.out.print("Add another driver? (y/n): ");
        } while (scanner.nextLine().trim().equalsIgnoreCase("y"));
        System.out.println("Add Trucks -");
        do {
            manualTruckSetup();
            System.out.print("Add another truck? (y/n): ");
        } while (scanner.nextLine().trim().equalsIgnoreCase("y"));
    }

    private boolean manualProductSetup() {
        System.out.print("Product Name (or 'done'): ");
        String name = scanner.nextLine().trim();
        if (name.equalsIgnoreCase("done")) return false;
        int weight = promptInt("Unit Weight: ");
        try {
            productService.addProduct(name, weight);
            System.out.println("Product added.");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

    private void manualDriverSetup() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        int lic = promptInt("License Level: ");
        try {
            driverService.addDriver(name, lic);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add driver: " + e.getMessage());
        }
    }

    private void manualTruckSetup() {
        int id = promptInt("Enter Truck Number: ");
        System.out.print("Model: ");
        String model = scanner.nextLine().trim();
        int weight = promptInt("Net Weight: ");
        int max = promptInt("Max Capacity: ");
        int lic = promptInt("License Required: ");
        try {
            truckService.addTruck(id, model, weight, max, lic);
            System.out.println("Truck added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addManualBranch() {
        System.out.print("Address: ");
        String addr = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Contact: ");
        String contact = scanner.nextLine().trim();
        try {
            //WE ADD A BRANCH (LOCATION) to BOTH SERVICES IT'S OUR RESPONSIBILITY THAT EACH SERVICE DOES IT ON ITS OWN BUT WE CALL THEM TOGETHER ALWAYS
            companyManager.addBranchLocation(addr, phone, contact);
            System.out.println("Store added.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void manualSupplierSetup() {
        System.out.print("Address: ");
        String addr = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Contact: ");
        String contact = scanner.nextLine().trim();

        Map<Integer, Integer> stockIds = new HashMap<>();
        List<String> catalog = productService.getProductsDisplay();

        for (int i = 0; i < catalog.size(); i++) {
            System.out.print("Supply " + catalog.get(i) + "? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                int qty = promptInt("Quantity: ");
                stockIds.put(i, qty);
            }
        }

        try {
            companyManager.registerSupplierByIndices(addr, phone, contact, stockIds);
            System.out.println("Supplier registered.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) { System.out.println("Invalid input. Please enter an integer."); }
        }
    }
}