package Presentation.Transportation;


import Service.Transportation.*;
import Service.Workers.ShiftJobsService;
import Service.Workers.ShiftPlacementService;
import Service.Workers.ShiftWorkersCanidatesService;
import Service.Workers.WorkersService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class AdminConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final ProductCatalogService productService;
    private final TransportManagerService transportService;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final TruckService truckService;
    private final BranchService branchService;
    private final LocationService locationService;
    private final WorkersService workers_service;
    private final ShiftWorkersCanidatesService candidates_service;
    private final ShiftPlacementService placement_service;

    public AdminConsole(ProductCatalogService productService, TransportManagerService transportService,
                        SupplierService supplierService, RequestService requestService, TruckService truckService,
                        BranchService branchService, LocationService locationService, WorkersService workers_service,
                        ShiftWorkersCanidatesService candidates_service, ShiftPlacementService placement_service,
                        ShiftJobsService jobs_service) {
        this.productService = productService;
        this.transportService = transportService;
        this.supplierService = supplierService;
        this.requestService = requestService;
        this.truckService = truckService;
        this.branchService = branchService;
        this.locationService = locationService;
        this.workers_service = workers_service;
        this.candidates_service = candidates_service;
        this.placement_service = placement_service;
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== LOGISTICS MANAGEMENT SYSTEM ===");
            System.out.println("1. Load Automatic Demo Data (Full Ecosystem)");
            System.out.println("2. Manual Data Entry");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choice: ");

            String choice1 = scanner.nextLine().trim();

            if (choice1.equals("1")) {
                loadRichDemoData();
                System.out.println("\n✅ Full Demo Ecosystem Loaded Successfully.");
            } else if (choice1.equals("2")) {
                manualSetup();
            } else if (choice1.equals("3")) {
                exit = true;
            } else {
                System.out.println("Invalid choice. Try again.");
            }

            while (!exit) {
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
                        exit = true;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
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

        if (requestService.getActiveRequestLocationIds().isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        MainConsole shipmentConsole = new MainConsole(transportService, supplierService, productService,
                truckService, locationService, workers_service, candidates_service, placement_service);
        try {shipmentConsole.initiateShipment(); }
        catch (Exceptions.ConsoleEndException e) { System.out.println("Returned to Admin Menu."); }
        catch (Exception e) { System.out.println("Shipment Console Error: " + e.getMessage()); }
    }

    private void addBranchRequest() {
        List<Integer> branchIds = branchService.getBranchesId();
        if (branchIds.isEmpty()) {
            System.out.println("Error: No branch locations available.");
            return;
        }

        int branchId;
        while (true) {
            System.out.println("\nSelect Branch ID:");
            for (Integer branch : branchIds)
                System.out.println(branchService.getBranchDisplay(branch));

            branchId = promptInt("Enter Branch ID: ") ;

            if (branchIds.contains(branchId))
                break;

            System.out.println("Invalid Branch ID. Please try again.");
        }

        Map<Integer, Integer> requestedProducts = new HashMap<>();
        List<Integer> productIds = productService.getProductsId();

        while (true) {
            System.out.println("\nAvailable Products:");
            for (Integer productId : productIds)
                System.out.println(productService.getProductDisplay(productId));

            int productId = promptInt("Select Products (or '-1'): ");

            if (productId == -1)
                break;

            if (!productIds.contains(productId)) {
                System.out.println("Invalid Product ID.");
                continue;
            }

            int amount = promptInt("Quantity needed: ");
            if (amount > 0)
                requestedProducts.put(productId, requestedProducts.getOrDefault(productId, 0) + amount);
            else
                System.out.println("Quantity must be greater than 0.");
        }

        if (!requestedProducts.isEmpty())
            try {
                requestService.addRequest(branchId, requestedProducts);
                System.out.println("Request successfully executed.");
            } catch (Exception e) {
                System.out.println("Failed to execute request: " + e.getMessage());
            }
    }

    private void resupplySupplier() {
        List<Integer> suppliers = supplierService.getSupplierIds();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }

        int sId;
        while (true) {
            System.out.println("\nSelect Supplier to Restock:");
            for (Integer supplier : suppliers)
                System.out.println(supplierService.getSupplierDisplay(supplier));
            sId = promptInt("Enter Supplier: ");
            if (suppliers.contains(sId)) break;
            System.out.println("Invalid Supplier ID. Please try again.");
        }

        int pId;
        while (true) {
            List<Integer> catalog = productService.getProductsId();
            System.out.println("Select Product:");
            for (Integer integer : catalog)
                System.out.println(productService.getProductDisplay(integer));
            pId = promptInt("Enter Product: ");
            if (catalog.contains(pId)) break;
            System.out.println("Invalid Product ID. Please try again.");
        }

        int qty;
        while (true) {
            qty = promptInt("Amount to add: ");
            if (qty > 0) break;
            System.out.println("Quantity must be greater than 0.");
        }

        try {
            supplierService.resupplySupplier(sId, pId, qty);
            System.out.println("Stock updated.");
        } catch (Exception e) { System.out.println("Failed to update stock: " + e.getMessage()); }
    }

    private void deleteBranchRequest() {
        List<Integer> activeBranches = requestService.getActiveRequestLocationIds();
        if (activeBranches.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        int branchId;
        while (true) {
            System.out.println("Which request would you like to remove?");
            for (Integer activeBranch : activeBranches)
                System.out.println(branchService.getBranchDisplay(activeBranch));
            branchId = promptInt("Enter Branch: ") ;
            if (activeBranches.contains(branchId)) 
                break;
            System.out.println("Invalid Branch Index. Please try again.");
        }
        try {
            requestService.removeRequest(branchId, true);
            System.out.println("Request removed.");
        } catch (Exception e) { System.out.println("Failed to remove request: " + e.getMessage()); }
    }

    private void updateBranchRequest() {
        List<Integer> activeRequestsLocation = requestService.getActiveRequestLocationIds();
        if (activeRequestsLocation.isEmpty()) {
            System.out.println("No active requests available.");
            return;
        }

        int branchId;
        while (true){
            System.out.println("Which request would you like to update?");
            for (Integer integer : activeRequestsLocation) 
                System.out.println(branchService.getBranchDisplay(integer));
            branchId = promptInt("Enter Branch ID: ");
            if (activeRequestsLocation.contains(branchId)) 
                break;
            System.out.println("Invalid Branch ID. Please try again.");
        } 

        List<Integer> catalog = productService.getProductsId();

        while (true) {
            System.out.print("\nType 'add', 'remove', or 'done': ");
            String action = scanner.nextLine().trim();
            if (action.equalsIgnoreCase("done")) break;

            if (action.equalsIgnoreCase("add")) {
                int pId;
                int qty;
                while (true) {
                    System.out.println("Select Product:");
                    for (Integer integer : catalog)
                        System.out.println(productService.getProductDisplay(integer));

                    pId = promptInt("Product: ");
                    qty = promptInt("Amount to add: ");
                    if (!catalog.contains(pId)) {
                        System.out.println("Invalid Product ID.");
                        continue;
                    }
                    if (qty > 0) 
                        break;
                    System.out.println("Quantity must be greater than 0.");
                }
                try {
                    requestService.updateRequestAddProduct(branchId, pId, qty);

                    System.out.println("Added.");
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }

            } else if (action.equalsIgnoreCase("remove")) {
                Map<Integer, Integer> productsInRequest = requestService.getProducts(branchId);
                if (productsInRequest.isEmpty()) {
                    System.out.println("No products available to remove.");
                    continue;
                }

                int pId;
                int qty;
                while (true) {
                    System.out.println("Select Product to remove:");
                    List<Integer> products = new ArrayList<>(productsInRequest.keySet());
                    int i = 1;
                    for (Integer product : products)
                        System.out.println(productService.getProductDisplay(product) + " (" + productsInRequest.get(product) + " units left)");
                    pId = promptInt("Product: ");
                    qty = promptInt("Amount to remove: ");
                    if (!products.contains(pId)) {
                        System.out.println("Invalid Product ID.");
                        continue;
                    }
                    if (qty > productsInRequest.get(pId)) {
                        System.out.println("Quantity exceeds available stock.");
                        continue;
                    }
                    if (qty > 0)
                        break;
                }
                try {
                    requestService.updateRequestRemoveProduct(branchId, pId, qty);
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
        try {
            System.out.println("enter new worker name: ");
            String name = scanner.nextLine();

            System.out.println("enter new worker id: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter new worker bank account info: ");
            String bank_account = scanner.nextLine();

            System.out.println("enter new worker salary: ");
            float salary = scanner.nextFloat();
            scanner.nextLine();

            System.out.println("enter new worker salary condisions: ");
            String salary_condision = scanner.nextLine();

            System.out.print("Enter new worker start date in this format (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            LocalDate start_date = LocalDate.parse(dateInput);

            System.out.println("enter if new worker can be shift manager: (false/true) ");
            boolean is_shift_manager = scanner.nextBoolean();
            scanner.nextLine();

            System.out.println("enter driver license number: ");
            int license = scanner.nextInt();
            scanner.nextLine();

            workers_service.addDriver(name, id, bank_account, salary, salary_condision, start_date, is_shift_manager, license);
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
            branchService.addBranch(addr, phone, contact);
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
        List<Integer> catalog = productService.getProductsId();

        for (Integer productId : catalog)
            System.out.println(productService.getProductDisplay(productId));

        while (true) {
            int id = promptInt("Enter Product ID (-1 to finish): ");

            if (id == -1)
                break;

            int amount = promptInt("Quantity: ");
            stockIds.put(id, amount);
        }

        try {

            supplierService.addSupplier(addr,phone,contact, stockIds);
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

    private void loadRichDemoData() {
        System.out.println("\n[SYSTEM] Initializing complete logistics ecosystem...");

        // --- 1. Products ---
        System.out.println("-> Seeding Products...");
        productService.addProduct("Milk 3% (Carton)", 1);
        productService.addProduct("Whole Wheat Bread", 2);
        productService.addProduct("Frozen Entrecote", 10);
        productService.addProduct("Coca-Cola 1.5L", 2);
        productService.addProduct("Toilet Paper (32 rolls)", 5);
        productService.addProduct("Apples (Box)", 15);
        productService.addProduct("Bamba Peanut Snack", 1);
        productService.addProduct("Mineral Water (6-pack)", 12);

        // שולפים את המזהים שנוצרו כדי להשתמש בהם בהמשך הקוד ללא ניחושים
        List<Integer> p = productService.getProductsId();

        // --- 2. Trucks ---
        System.out.println("-> Seeding Fleet (Trucks)...");
        truckService.addTruck(10101, "Mercedes Sprinter", 3000, 4500, 2);
        truckService.addTruck(20202, "Volvo FH16", 8000, 24000, 3);
        truckService.addTruck(30303, "Isuzu Sumo", 4000, 8000, 2);
        truckService.addTruck(40404, "Scania R-Series", 7500, 20000, 3);

        // --- 3. Suppliers (With their stock) ---
        System.out.println("-> Seeding Suppliers & Allocations...");
        Map<Integer, Integer> tnuvaStock = new HashMap<>();
        tnuvaStock.put(p.get(0), 1500); // 1500 יחידות חלב
        tnuvaStock.put(p.get(2), 500);  // 500 יחידות בשר
        supplierService.addSupplier("Rehovot Industrial Zone", "03-999-1111", "Yossi (Tnuva)", tnuvaStock);

        Map<Integer, Integer> osemStock = new HashMap<>();
        osemStock.put(p.get(6), 2000);  // 2000 במבה
        osemStock.put(p.get(1), 800);   // 800 לחם
        supplierService.addSupplier("Shoham Logistics Park", "03-888-2222", "Gabi (Osem)", osemStock);

        Map<Integer, Integer> colaStock = new HashMap<>();
        colaStock.put(p.get(3), 3000);  // 3000 קולה
        colaStock.put(p.get(7), 2500);  // 2500 מים
        supplierService.addSupplier("Bnei Brak Factory", "03-777-3333", "Rami (Coca-Cola)", colaStock);

        // --- 4. Branches & Requests ---
        System.out.println("-> Seeding Branches & Active Requests...");
        branchService.addBranch("Herzl 1, Gedera", "08-123-4567", "Liav Parehi");
        branchService.addBranch("Dizengoff Center, Tel Aviv", "03-555-8888", "Moti");
        branchService.addBranch("Carmel Center, Haifa", "04-444-9999", "Erez");

        // שולפים את מזהי הסניפים מהשירות
        List<Integer> b = branchService.getBranchesId();

        // בקשה ענקית לסניף גדרה
        Map<Integer, Integer> reqGedera = new HashMap<>();
        reqGedera.put(p.get(4), 100); // 100 נייר טואלט
        reqGedera.put(p.get(7), 50);  // 50 מים
        reqGedera.put(p.get(6), 30);  // 30 במבה
        requestService.addRequest(b.get(0), reqGedera);

        // בקשה לתל אביב
        Map<Integer, Integer> reqTlv = new HashMap<>();
        reqTlv.put(p.get(0), 200); // חלב
        reqTlv.put(p.get(1), 100); // לחם
        reqTlv.put(p.get(2), 50);  // בשר
        reqTlv.put(p.get(3), 150); // קולה
        requestService.addRequest(b.get(1), reqTlv);

        // בקשה לחיפה
        Map<Integer, Integer> reqHaifa = new HashMap<>();
        reqHaifa.put(p.get(2), 100); // בשר
        reqHaifa.put(p.get(5), 40);  // תפוחים
        reqHaifa.put(p.get(3), 80);  // קולה
        requestService.addRequest(b.get(2), reqHaifa);
    }
}
