import DAO.LocationDAO;
import DAO.ProductDAO;
import DAO.TruckDAO;
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
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        Connection dbConnection = DatabaseManager.getConnection();
        //TODO REMEMBER WE ALWAYS LOAD FROM THE DB FIRST, WE DONT NEED A MANUAL LOAD, WE STILL HAVE MANUAL ADDITION
        ProductDAO productDB=new ProductDAO(dbConnection);
        ProductCatalogService productCatalogService = new ProductCatalogService(productDB);
        productCatalogService.loadAllProductsFromDB();
        System.out.println("CHECKING PRODUCTS");
        List<Integer> l0=productCatalogService.getProductsId();
        for(Integer i:l0){
            System.out.println(productCatalogService.getProductDisplay(i));
        }

        TruckDAO truckDB=new TruckDAO(DatabaseManager.getConnection());
        TruckService truckService=new TruckService(productCatalogService,truckDB);
        truckService.loadAllTrucksFromDB();
        System.out.println("CHECKING TRUCKS");
        List<Integer> l1=truckService.getAvailableTruckIds();
        for(Integer i:l1){
            System.out.println(truckService.getTruckDisplay(i));
        }

        //TODO ALWAYS REMEMBER TO LOAD LOCATIONS BEFORE BRANCHES/SUPPLIERS
        LocationDAO locationDB=new LocationDAO(dbConnection);

        LocationService locationService=new LocationService(locationDB);
        locationService.loadLocationsFromDB();
        System.out.println("CHECKING Locations");
        List<Integer> l2=locationService.getLocationIds();
        for(Integer i:l2){
            System.out.println(locationService.getLocation(i).toString());
        }

        BranchService branchService=new BranchService(locationService,locationDB);
        branchService.loadBranchesFromDB();
        System.out.println("CHECKING BRANCHES");
        List<Integer> l3=branchService.getBranchesId();
        for(Integer i:l3){
            System.out.println(branchService.getBranchDisplay(i));
        }

        SupplierService supplierService=new SupplierService(locationService,locationDB);
        supplierService.loadSuppliersFromDB();
        System.out.println("CHECKING Suppliers");
        List<Integer> l4=supplierService.getSupplierIds();
        for(Integer i:l4){
            System.out.println(supplierService.getSupplierDisplay(i));
        }

















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