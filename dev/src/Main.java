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
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection dbConnection = DatabaseManager.getConnection();

        // ============================================================
        // שלב 1: ניקוי בסיס הנתונים (מתבצע בכל הרצה מחדש)
        // ============================================================

        // ============================================================
        // שלב 2: אתחול כל ה-DAOs וה-Services
        // ============================================================
        // תשתית הובלה
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

        // תשתית עובדים (בהנחה והמחלקות קיימות בפרויקט שלך)
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
        // Boot Sequence: טעינת כל הנתונים מה-DB לזיכרון של המערכת
        // ============================================================
        System.out.println("\n[SYSTEM BOOT] Loading existing data from Database...");

        try {
            // 1. קודם כל דברים עצמאיים שלא תלויים באף אחד
            productCatalogService.loadAllProductsFromDB();
            locationService.loadLocationsFromDB();

            // 2. משאיות (תלויות במוצרים כדי לחשב משקל)
            truckService.loadAllTrucksFromDB();

            // 3. סניפים וספקים (תלויים במיקומים ובמוצרים)
            branchService.loadBranchesFromDB();
            supplierService.loadSuppliersFromDB();

            // 4. בקשות והובלות (הכי מורכבים, תלויים בסניפים ובמוצרים)
            requestService.loadRequestsFromDB();
            transportManagerService.loadCountFromDB();


            // הערה: אם יש לך Service שצריך לטעון הובלות פעילות שנקטעו באמצע, זה הזמן לטעון גם אותו.

            System.out.println("[SYSTEM BOOT] All data loaded successfully into memory. Ready to go!");
        } catch (Exception e) {
            System.err.println("[SYSTEM BOOT] CRITICAL ERROR loading data from DB: " + e.getMessage());
            e.printStackTrace();
            return; // עוצרים את התוכנית אם אי אפשר לטעון נתונים
        }


        // ============================================================
        // שלב 4: ניתוב למערכות (UI Main Loop)
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
            System.out.println("3. ADMIN: Delete All System Data (Wipe DB)");
            System.out.println("4. Exit Program");
            System.out.print("Please enter your choice (1-4): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> transportUI.start();
                case "2" -> workersUI.run();
                case "3" -> {
                    System.out.println("\n⚠️ WARNING: This will permanently delete ALL data in the database! ⚠️");
                    System.out.print("Are you absolutely sure? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("y") || confirm.equals("yes")) {
                        clearDatabase(dbConnection);
                        System.out.println("✅ Database completely wiped!");
                        System.out.println("🔄 PLEASE RESTART THE PROGRAM to clear the in-memory cache.");
                        exit = true; // יוצאים מהתוכנית כדי להכריח איפוס זיכרון נקי
                    } else {
                        System.out.println("Aborted. Data is safe.");
                    }
                }
                case "4" -> {
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
}