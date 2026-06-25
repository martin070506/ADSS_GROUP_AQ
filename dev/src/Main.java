import DAO.Transportation.*;
import DB.DatabaseManager;
import Domain.Workers.ShiftCanidatesWorkersFacade;
import Domain.Workers.ShiftJobsFacade;
import Domain.Workers.ShiftPlacmentFacade;
import Domain.Workers.WorkersFacade;
import Presentation.Transportation.AdminConsole;
import Presentation.Workers.ServiceControl;
import Service.Transportation.*;
import Service.Workers.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection dbConnection = DatabaseManager.getConnection();

        // ============================================================
        ProductDAO productDB = new ProductDAO(dbConnection);
        ProductCatalogService productCatalogService = new ProductCatalogService(productDB);

        TruckDAO truckDB = new TruckDAO(dbConnection);
        TruckService truckService = new TruckService(productCatalogService, truckDB);

        LocationDAO locationDB = new LocationDAO(dbConnection);
        LocationService locationService = new LocationService(locationDB);

        BranchService branchService = new BranchService(locationService);
        SupplierAllocationDAO allocationDAO = new SupplierAllocationDAO(dbConnection);
        SupplierService supplierService = new SupplierService(locationService, allocationDAO);

        RequestDAO requestDB = new RequestDAO(dbConnection);
        RequestService requestService = new RequestService(locationService, requestDB);

        TransportFileDAO transportFileDAO = new TransportFileDAO(dbConnection);

        WorkersFacade workers_facade = new WorkersFacade();
        ShiftJobsFacade jobs_facade = new ShiftJobsFacade(locationService);
        ShiftCanidatesWorkersFacade candidates_facade = new ShiftCanidatesWorkersFacade(workers_facade, locationService);
        ShiftPlacmentFacade placement_facade = new ShiftPlacmentFacade(workers_facade, jobs_facade, candidates_facade, locationService);

        WorkersService workers_service = new WorkersService(workers_facade);
        ShiftJobsService jobs_service = new ShiftJobsService(jobs_facade);
        ShiftWorkersCanidatesService candidates_service = new ShiftWorkersCanidatesService(candidates_facade);
        ShiftPlacementService placement_service = new ShiftPlacementService(placement_facade);

        TransportManagerService transportManagerService = new TransportManagerService(truckService, supplierService, requestService, workers_service, jobs_service, transportFileDAO);
//
        // ============================================================
        System.out.println("\n[SYSTEM BOOT] Loading existing data from Database...");

        try {
            productCatalogService.loadAllProductsFromDB();
            locationService.loadLocationsFromDB();

            truckService.loadAllTrucksFromDB();

            branchService.loadBranchesFromDB();
            supplierService.loadSuppliersFromDB();

            requestService.loadRequestsFromDB();
            transportManagerService.loadCountFromDB();



            System.out.println("[SYSTEM BOOT] All data loaded successfully into memory. Ready to go!");
        } catch (Exception e) {
            System.err.println("[SYSTEM BOOT] CRITICAL ERROR loading data from DB: " + e.getMessage());
            e.printStackTrace();
            return;
        }


        // ============================================================
        AdminConsole transportUI = new AdminConsole(productCatalogService, transportManagerService, supplierService,
                requestService, truckService, branchService, locationService, workers_service, candidates_service,
                placement_service, jobs_service);

        ServiceControl workersUI = new ServiceControl(locationService, workers_service, jobs_service, candidates_service, placement_service);

        boolean exit = false;
        System.out.println("\n========================================");
        System.out.println("   WELCOME TO ADSS LOGISTICS SYSTEM   ");
        System.out.println("========================================");

        while (!exit) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Transport & Logistics System");
            System.out.println("2. Employee & Shift Management System");
            System.out.println("3. Load Automatic Demo Data (Full Ecosystem)");
            System.out.println("4. ADMIN: Delete All System Data (Wipe DB)");
            System.out.println("5. Exit Program");
            System.out.print("Please enter your choice (1-4): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> transportUI.start();
                case "2" -> workersUI.run();
                case "3" -> {
                    loadRichDemoData(productCatalogService, truckService, supplierService, requestService, branchService);
                    load_data(workers_service, jobs_service, candidates_service, placement_service, locationService);
                }
                case "4" -> {
                    System.out.println("\nWARNING: This will permanently delete ALL data in the database!");
                    System.out.print("Are you absolutely sure? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("y") || confirm.equals("yes")) {
                        clearDatabase(dbConnection);
                        System.out.println("Database completely wiped!");
                        System.out.println("PLEASE RESTART THE PROGRAM to clear the in-memory cache.");
                        exit = true;
                    } else {
                        System.out.println("Aborted. Data is safe.");
                    }
                }
                case "5" -> {
                    System.out.println("Exiting System. Goodbye!");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void clearDatabase(Connection conn) {
        String[] tables = {
                "SupplierAllocation",
                "ProductFile_Items",
                "Request",
                "ProductFile",
                "Location",
                "Truck",
                "Product",
                "TransportFile",
                "workers",
                "shift_placement_jobs_workers",
                "shift_placement",
                "shift_jobs_count",
                "shift_jobs",
                "shift_candidates",
                "shift_candidate_ids"
        };

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("PRAGMA foreign_keys = OFF;");
            for (String table : tables) {
                try {
                    stmt.executeUpdate("DELETE FROM " + table);
                } catch (SQLException e) {
                    System.err.println("Note: Table " + table + " skip/error: " + e.getMessage());
                }
            }
            // איפוס מונים אוטומטיים של SQLite
            stmt.executeUpdate("DELETE FROM sqlite_sequence;");
            stmt.executeUpdate("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            throw new RuntimeException("Error wiping database: " + e.getMessage());
        }
    }

    private static void loadRichDemoData(ProductCatalogService productService, TruckService truckService, SupplierService supplierService, RequestService requestService, BranchService branchService) {
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
        requestService.addRequest(b.get(1), reqTlv);

        // בקשה לחיפה
        Map<Integer, Integer> reqHaifa = new HashMap<>();
        reqHaifa.put(p.get(2), 100); // בשר
        reqHaifa.put(p.get(5), 40);  // תפוחים
        reqHaifa.put(p.get(3), 80);  // קולה
        requestService.addRequest(b.get(2), reqHaifa);
    }

    public static void load_data(WorkersService workers_service, ShiftJobsService jobs_service, ShiftWorkersCanidatesService canidates_service, ShiftPlacementService placement_service, LocationService locationService){

        System.out.println("-> Seeding Workers...");
        System.out.println("-> Seeding Jobs...");
        System.out.println("-> Seeding Candidates...");
        System.out.println("-> Seeding Placements...");

        workers_service.addDriver("Marko", 10, "discount", 33.7, "above avg",  LocalDate.parse("2011-11-11"), false,2);
        workers_service.addWorker("Mark11", 11, "discount", 33.7, "above avg",  LocalDate.parse("2011-11-11"), false);
        workers_service.addWorker("Mark12", 12, "discount", 33.7, "above avg",  LocalDate.parse("2011-11-11"), true);


        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(1),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(1),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(1),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(1),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(1),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(1),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(4),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(4),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(4),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(4),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(4),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(4),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(5),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(5),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(5),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(5),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(5),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(5),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(6),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(6),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),true, locationService.getLocation(6),12);

        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(6),10);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(6),11);
        canidates_service.addCandidate(LocalDate.now().plusDays(1),false, locationService.getLocation(6),12);

        jobs_service.addJob(LocalDate.now().plusDays(1),true,locationService.getLocation(1),2);
        jobs_service.addJob(LocalDate.now().plusDays(1),false,locationService.getLocation(1),2);

        jobs_service.addJob(LocalDate.now().plusDays(1),true,locationService.getLocation(4),1);
        jobs_service.addJob(LocalDate.now().plusDays(1),false,locationService.getLocation(4),1);

        jobs_service.addJob(LocalDate.now().plusDays(1),true,locationService.getLocation(5),1);
        jobs_service.addJob(LocalDate.now().plusDays(1),false,locationService.getLocation(5),1);

        jobs_service.addJob(LocalDate.now().plusDays(1),true,locationService.getLocation(6),1);
        jobs_service.addJob(LocalDate.now().plusDays(1),false,locationService.getLocation(6),1);

        placement_service.addPlacement(LocalDate.now().plusDays(1), true, locationService.getLocation(1), 12, List.of(10), List.of(2));
        placement_service.addPlacement(LocalDate.now().plusDays(1), false, locationService.getLocation(1), 12, List.of(10), List.of(2));

        placement_service.addPlacement(LocalDate.now().plusDays(1), true, locationService.getLocation(4), 12, List.of(11), List.of(1));
        placement_service.addPlacement(LocalDate.now().plusDays(1), false, locationService.getLocation(4), 12, List.of(11), List.of(1));

        placement_service.addPlacement(LocalDate.now().plusDays(1), true, locationService.getLocation(5), 12, List.of(11), List.of(1));
        placement_service.addPlacement(LocalDate.now().plusDays(1), false, locationService.getLocation(5), 12, List.of(11), List.of(1));

        placement_service.addPlacement(LocalDate.now().plusDays(1), true, locationService.getLocation(6), 12, List.of(11), List.of(1));
        placement_service.addPlacement(LocalDate.now().plusDays(1), false, locationService.getLocation(6), 12, List.of(11), List.of(1));
    }
}