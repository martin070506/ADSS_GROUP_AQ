import DAO.*;
import Domain.Transportation.*;
import Domain.Workers.ShiftCanidatesWorkersFacade;
import Domain.Workers.ShiftJobsFacade;
import Domain.Workers.ShiftPlacmentFacade;
import Domain.Workers.WorkersFacade;
import Presentation.Transportation.AdminConsole;
import Presentation.Workers.ServiceControl;
import Service.Transportation.*;
import Service.Workers.ShiftJobsService;
import Service.Workers.ShiftPlacementService;
import Service.Workers.ShiftWorkersCanidatesService;
import Service.Workers.WorkersService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        Connection dbConnection = DatabaseManager.getConnection();

        // ==========================================
        // 1. PRODUCTS TEST
        // ==========================================
        ProductDAO productDB = new ProductDAO(dbConnection);
        ProductCatalogService productCatalogService = new ProductCatalogService(productDB);
        productCatalogService.loadAllProductsFromDB();
        System.out.println("--- CHECKING PRODUCTS ---");
        List<Integer> l0 = productCatalogService.getProductsId();
        for(Integer i : l0){
            System.out.println(productCatalogService.getProductDisplay(i));
        }

        // ==========================================
        // 2. TRUCKS TEST
        // ==========================================
        TruckDAO truckDB = new TruckDAO(dbConnection);
        TruckService truckService = new TruckService(productCatalogService, truckDB);
        truckService.loadAllTrucksFromDB();
        System.out.println("\n--- CHECKING TRUCKS ---");
        List<Integer> l1 = truckService.getAvailableTruckIds();
        for(Integer i : l1){
            System.out.println(truckService.getTruckDisplay(i));
        }

        // ==========================================
        // 3. LOCATIONS TEST
        // ==========================================
        LocationDAO locationDB = new LocationDAO(dbConnection);
        LocationService locationService = new LocationService(locationDB);
        locationService.loadLocationsFromDB();
        System.out.println("\n--- CHECKING LOCATIONS ---");
        List<Integer> l2 = locationService.getLocationIds();
        for(Integer i : l2){
            System.out.println(locationService.getLocation(i).toString());
        }

        // ==========================================
        // 4. BRANCHES TEST
        // ==========================================
        BranchService branchService = new BranchService(locationService);
        branchService.loadBranchesFromDB();
        branchService.addBranch("New Branch St", "050-123", "Dani");
        System.out.println("\n--- CHECKING BRANCHES ---");
        List<Integer> l3 = branchService.getBranchesId();
        for(Integer i : l3){
            System.out.println(branchService.getBranchDisplay(i));
        }

        // ==========================================
        // 5. SUPPLIERS TEST
        // ==========================================
        SupplierService supplierService = new SupplierService(locationService, new SupplierAllocationDAO(dbConnection));
        supplierService.loadSuppliersFromDB();
        supplierService.addSupplier("Supplier St", "052-456", "Avi", new HashMap<>());
        System.out.println("\n--- CHECKING SUPPLIERS ---");
        List<Integer> l4 = supplierService.getSupplierIds();
        for(Integer i : l4){
            System.out.println(supplierService.getSupplierDisplay(i));
        }

        // ==========================================
        // 6. REQUESTS TEST (3 TABLES DB)
        // ==========================================
        System.out.println("\n=================================");
        System.out.println("   TESTING REQUESTS (3 TABLES)   ");
        System.out.println("=================================");

        RequestDAO requestDB = new RequestDAO(dbConnection, locationDB);
        RequestService requestService = new RequestService(locationService, requestDB);

        // שלב 1: טעינה והצגה של הבקשות הפעילות כרגע
        requestService.loadRequestsFromDB();
        System.out.println("1. Current ACTIVE Requests Loaded From DB:");
        List<Integer> activeRequestIds = requestService.getRequestsIds();
        if (activeRequestIds.isEmpty()) {
            System.out.println("   No active requests found in DB.");
        } else {
            for(Integer locId : activeRequestIds){
                System.out.println("   " + requestService.getRequestDisplay(locId));
            }
        }

        // קוד טסט: יצירת בקשה חדשה לגמרי דרך ה-Service
        System.out.println("\n2. Simulating: Creating a new Active Request through Service...");

        int testLocationId = 1; // ודאו שסניף מספר 1 קיים אצלכם!

        // יצירת מילון מוצרים לבקשה (למשל מוצר 0 ומוצר 1)
        Map<Integer, Integer> testItems = new HashMap<>();
        testItems.put(0, 100);
        testItems.put(1, 250);

        // הוספת הבקשה למערכת (זה ייצר את הקובץ וישמור ב-3 הטבלאות ב-DB אוטומטית)
        requestService.addRequest(testLocationId, testItems);
        System.out.println("   -> Success! Request added for Location " + testLocationId);

        // טעינה מחדש כדי לראות שהמערכת באמת קולטת את זה מה-DB
        System.out.println("\n3. Reloading Active Requests from DB to verify:");
        requestService.loadRequestsFromDB();
        for(Integer locId : requestService.getRequestsIds()){
            System.out.println("   " + requestService.getRequestDisplay(locId));
        }

        /*
        // שלב 4: סימולציית סיום הובלה (מחיקה רק מטבלת Active_Requests)
        System.out.println("\n4. Simulating: Transport Completed...");
        requestService.removeRequest(testLocationId);
        System.out.println("   -> Request completed and removed from active view, history preserved in DB.");
        */






//        WorkersFacade workers_facade = new WorkersFacade();
//        ShiftJobsFacade jobs_facade = new ShiftJobsFacade();
//        ShiftCanidatesWorkersFacade candidates_facade = new ShiftCanidatesWorkersFacade(workers_facade);
//        ShiftPlacmentFacade placement_facade = new ShiftPlacmentFacade(workers_facade, jobs_facade, candidates_facade);
//
//        WorkersService workers_service = new WorkersService(workers_facade);
//        ShiftJobsService jobs_service = new ShiftJobsService(jobs_facade);
//        ShiftWorkersCanidatesService candidates_service = new ShiftWorkersCanidatesService(candidates_facade);
//        ShiftPlacementService placement_service = new ShiftPlacementService(placement_facade);
//

//        RequestService requestService = new RequestService(locationService);

//        TransportManagerService transportService = new TransportManagerService(truckService, supplierService, requestService, workers_service);






        //truckService.addTruck(800,"MERCEDES",1000,3000,2);

//        AdminConsole appUI = new AdminConsole(productService, transportService, supplierService,
//                requestService, truckService, branchService, locationService, workers_service, candidates_service,
//                placement_service, jobs_service);
//
//        ServiceControl service = new ServiceControl(locationService, workers_service, jobs_service, candidates_service, placement_service);
//
//
//
//
//        boolean exit = false;
//        while(!exit){
//            System.out.println("Which system would you like to enter?");
//            System.out.println("1. Transport System");
//            System.out.println("2. Employee System");
//            System.out.println("3. Exit Program");
//            System.out.print("Please enter your choice (1 or 2 or 3): ");
//
//            String choice = scanner.nextLine();
//
//            switch (choice) {
//                case "1" -> {
//                    appUI.start();
//                }
//                case "2" -> {
//                    service.run();
//                }
//                case "3" -> exit = true;
//                default -> System.out.println("Invalid choice. trying again.");
//            }
//        }

        scanner.close();
    }
}