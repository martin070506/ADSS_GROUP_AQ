import Domain.*;
import Domain.BranchManager;
import Domain.TruckFacade;
import Service.*;
import Presentation.MainConsole;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static CompanyManager companyManager;

    public static void main(String[] args) {
        // --- 1. INITIALIZE INFRASTRUCTURE (FACADES) ---
        TruckFacade truckFacade = new TruckFacade();
        DriverFacade driverFacade = new DriverFacade();
        TransportationFacade transportFacade = new TransportationFacade();
        TransportManager transportManager=new TransportManager(new ArrayList<>(),truckFacade.getAvailableTrucks(),driverFacade.getAvailableDrivers());
        // --- 2. INITIALIZE SERVICES ---
        TruckService truckService = new TruckService(truckFacade);
        DriverService driverService = new DriverService(driverFacade);
        ProductService productService = new ProductService();

        ShipmentService shipmentService = new ShipmentService(transportManager);

        // --- 3. INITIALIZE ORCHESTRATOR ---
        companyManager = CompanyManager.getInstance(truckService, driverService, shipmentService, transportFacade, productService);

        System.out.println("=== LOGISTICS MANAGEMENT SYSTEM ===");
        System.out.println("1. Load Automatic Demo Data");
        System.out.println("2. Manual Data Entry");
        System.out.print("Choice: ");

        if (scanner.nextLine().equals("1")) {
            fillListsWithDemoData();
        } else {
            manualSetup();
        }

        // --- 4. START UI ---
        MainConsole console = new MainConsole(companyManager);

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
                case "7" -> runShipmentCycle(console);
                case "8" -> deleteBranchRequest();
                case "9" -> updateBranchRequest();
                case "0" -> {
                    System.out.println("Exiting system...");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void displayMenu() {
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

    // --- REFACTORED OPERATIONAL LOGIC ---

    private static void addBranchRequest() {
        List<BranchManager> branches = companyManager.getBranches();
        if (branches.isEmpty()) {
            System.out.println("Error: No branch locations available.");
            return;
        }
        System.out.println("\nSelect Branch:");
        for (int i = 0; i < branches.size(); i++) {
            System.out.println("[" + i + "] " + branches.get(i).getLocation().address());
        }
        int choice = promptInt("Choice Index: ");
        BranchManager bm = branches.get(choice);

        List<ProductPair> requested = new LinkedList<>();
        List<Product> catalog = companyManager.getMasterProductCatalog();
        while (true) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < catalog.size(); i++) System.out.println("[" + i + "] " + catalog.get(i).name());
            System.out.print("Select Product Index (or 'done'): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) break;

            int pIdx = Integer.parseInt(input);
            int qty = promptInt("Quantity needed: ");
            requested.add(new ProductPair(catalog.get(pIdx), qty));
        }
        bm.requestShipment(requested);
        System.out.println("Request added for branch.");
    }

    private static void resupplySupplier() {
        List<Supplier> suppliers = companyManager.getShipmentService().getSuppliers();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }
        System.out.println("\nSelect Supplier to Restock:");
        for (int i = 0; i < suppliers.size(); i++) {
            System.out.println("[" + i + "] " + suppliers.get(i).supplierLocation().address());
        }
        int sIdx = promptInt("Supplier Index: ");
        Supplier s = suppliers.get(sIdx);

        List<Product> catalog = companyManager.getMasterProductCatalog();
        System.out.println("Select Product:");
        for (int i = 0; i < catalog.size(); i++) System.out.println("[" + i + "] " + catalog.get(i).name());
        int pIdx = promptInt("Product Index: ");
        int qty = promptInt("Amount to add: ");

        s.addStock(catalog.get(pIdx), qty);
        System.out.println("Stock updated.");
    }

    private static void runShipmentCycle(MainConsole console) {
        try {
            console.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteBranchRequest() {
        System.out.println("Which branch would you like to delete?");

        Location l=selectLocation(companyManager.getActiveRequestBranches());
        List<Destination> chosenBranchRequests=companyManager.getRequestsByBranch(l);
        if (chosenBranchRequests.isEmpty()) {
            System.out.println("No branch locations available.");
        }
        else {
            System.out.println("\nBranch Request To View:");
            for (int i = 0; i < chosenBranchRequests.size(); i++) {
                System.out.println("["+i+"]: " +chosenBranchRequests.get(i).toString());
            }
            int choice=-1;
            while(choice>=chosenBranchRequests.size()||choice<0){
                System.out.println("Choose Request for Deletion:");
                System.out.println("Choose Request for Updating:");
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                }
                catch (Exception e) {
                    System.out.println("please enter a valid input");
                }
            }
            Destination d=chosenBranchRequests.get(choice);
            companyManager.removeRequestsByBranch(d);
            System.out.println("Request removed for branch.");


        }
    }



    public static void updateBranchRequest() {
        System.out.println("Which branch would you like to delete?");
        Location l=selectLocation(companyManager.getAllLocations());
        List<Destination> chosenBranchRequests=companyManager.getRequestsByBranch(l);
        if (chosenBranchRequests.isEmpty()) {
            System.out.println("No branch locations available.");
        }
        else {
            System.out.println("\nSelect Request for Updating:");
            for (int i = 0; i < chosenBranchRequests.size(); i++) {
                System.out.println("["+i+"]: " +chosenBranchRequests.get(i).toString());
            }
            int choice=-1;
            while(choice>=chosenBranchRequests.size()||choice<0){
                System.out.println("Choose Request for Updating:");
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                }
                catch (Exception e) {
                    System.out.println("please enter a valid input");
                }
            }
            Destination d=chosenBranchRequests.get(choice);
            String actionInput="";
            List<Product> catalog = companyManager.getMasterProductCatalog();
            while (!actionInput.equalsIgnoreCase("done")) {
                actionInput = scanner.nextLine();

                if (!actionInput.equalsIgnoreCase("add Product") && !actionInput.equalsIgnoreCase("remove Product")) {
                    if (!actionInput.equalsIgnoreCase("done")) {
                        System.out.println("Please enter a valid input");
                    }
                }

                // --- ADD PRODUCT ---
                else if (actionInput.equalsIgnoreCase("add Product")) {
                    System.out.println("Select Product:");
                    for (int i = 0; i < catalog.size(); i++) {
                        System.out.println("[" + i + "] " + catalog.get(i).name());
                    }

                    // Validate Product Index
                    int pIdx = -1;
                    while (true) {
                        pIdx = promptInt("Product Index: ");
                        if (pIdx >= 0 && pIdx < catalog.size()) {
                            break; // Valid index, exit loop
                        }
                        System.out.println("Invalid index. Please choose a number between 0 and " + (catalog.size() - 1));
                    }

                    // Validate Quantity
                    int qty = -1;
                    while (true) {
                        qty = promptInt("Amount to add: ");
                        if (qty > 0) {
                            break; // Valid quantity, exit loop
                        }
                        System.out.println("Quantity must be greater than 0.");
                    }

                    Product p = catalog.get(pIdx);
                    ProductPair pp = new ProductPair(p, qty);
                    d.getProductFile().addProduct(pp);
                }

                // --- REMOVE PRODUCT ---
                else if (actionInput.equalsIgnoreCase("remove Product")) {
                    System.out.println("Select Product:");
                    List<Product> productsInRequest = new ArrayList<>();
                    for (ProductPair pp : d.getProductFile().getProducts()) {
                        productsInRequest.add(pp.product);
                    }

                    if (productsInRequest.isEmpty()) {
                        System.out.println("No products available to remove.");
                        continue;
                    }

                    for (int i = 0; i < productsInRequest.size(); i++) {
                        System.out.println("[" + i + "] " + productsInRequest.get(i).name());
                    }

                    // Validate Product Index
                    int pIdx = -1;
                    while (true) {
                        pIdx = promptInt("Product Index: ");
                        if (pIdx >= 0 && pIdx < productsInRequest.size()) {
                            break;
                        }
                        System.out.println("Invalid index. Please choose a number between 0 and " + (productsInRequest.size() - 1));
                    }
                    // Validate Quantity
                    int qty = -1;
                    while (true) {
                        qty = promptInt("Amount to remove [if you remove more than available, product will be removed altogether]: ");
                        if (qty > 0) {
                            break;
                        }
                        System.out.println("Quantity must be greater than 0.");
                    }


                    Product p = productsInRequest.get(pIdx);
                    ProductPair pp = new ProductPair(p, qty);
                    d.getProductFile().removeProduct(pp);
                }
            }


        }
    }

    private static Location selectLocation(List<Location> locations) {
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


    // --- MANUAL SETUP MODULES ---

    private static void manualSetup() {
        manualProductSetup();
        manualDriverSetup();
        manualTruckSetup();
        manualLocationAndSupplierSetup();
    }

    private static void manualProductSetup() {
        System.out.println("\n> Define Product Catalog");
        while (true) {
            System.out.print("Product Name (or 'done'): ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("done")) break;
            double weight = promptDouble("Unit Weight: ");
            companyManager.addProductToCatalog(name, weight);
        }
    }

    private static void manualDriverSetup() {
        System.out.print("\nAdd Driver? (y/n): ");
        while (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            int lic = promptInt("License Level (1-3): ");
            companyManager.getDriverService().addDriver(new Driver(name, lic));
            System.out.print("Add another driver? (y/n): ");
        }
    }

    private static void manualTruckSetup() {
        System.out.print("\nAdd Truck? (y/n): ");
        while (scanner.nextLine().equalsIgnoreCase("y")) {
            int id = promptInt("Truck ID: ");
            System.out.print("Model: ");
            String model = scanner.nextLine();
            int weight = promptInt("Net Weight: ");
            int max = promptInt("Max Capacity: ");
            int lic = promptInt("License Required (1-3): ");
            companyManager.getTruckService().addTruck(new Truck(id, model, weight, max, lic));
            System.out.print("Add another truck? (y/n): ");
        }
    }

    private static void addManualStoreLocation() {
        System.out.print("Address: ");
        String addr = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Contact: ");
        String contact = scanner.nextLine();
        companyManager.addBranch(addr, phone, contact);
    }

    private static void manualSupplierSetup() {
        System.out.print("Supplier Address: ");
        String addr = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Contact: ");
        String contact = scanner.nextLine();

        List<ProductPair> stock = new LinkedList<>();
        for (Product p : companyManager.getMasterProductCatalog()) {
            System.out.print("Supply " + p.name() + "? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                int qty = promptInt("Quantity: ");
                stock.add(new ProductPair(p, qty));
            }
        }
        companyManager.registerSupplier("SUP", addr, phone, contact, stock);
    }

    private static void manualLocationAndSupplierSetup() {
        System.out.println("Setting up basic locations...");
        // This is a combination helper for the manual entry path
    }

    // --- UTILS ---
    private static int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) { System.out.println("Invalid input."); }
        }
    }

    private static double promptDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (Exception e) { System.out.println("Invalid input."); }
        }
    }

    public static void fillListsWithDemoData() {
        companyManager.addProductToCatalog("Apple", 150.0);
        companyManager.addProductToCatalog("Banana", 120.0);
        companyManager.addProductToCatalog("Milk", 6.0);

        List<Product> cat = companyManager.getMasterProductCatalog();

        companyManager.registerSupplier("FruitVendor", "Tel Aviv", "03-123", "Alice",
                new LinkedList<>(List.of(new ProductPair(cat.get(0), 100), new ProductPair(cat.get(1), 100))));

        companyManager.addBranch("Ashdod", "08-222", "Grace");

        companyManager.getTruckService().addTruck(new Truck(101, "Isuzu Sumo", 3500, 7500, 2));
        companyManager.getDriverService().addDriver(new Driver("Bob", 2));

        System.out.println("Demo Data Loaded.");
    }
}