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
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while(!exit){
            System.out.println("Which system would you like to enter?");
            System.out.println("1. Transport System");
            System.out.println("2. Employee System");
            System.out.println("3. Exit Program");
            System.out.print("Please enter your choice (1 or 2 or 3): ");

            String choice = scanner.nextLine();
            LocationService locationService = new LocationService();

            switch (choice) {
                case "1" -> {
                    DriverService driverService = new DriverService();
                    RequestService requestService = new RequestService();
                    ProductCatalogService productService = new ProductCatalogService();
                    SupplierService supplierService = new SupplierService();
                    TransportManagerService transportService = new TransportManagerService();
                    TruckService truckService = new TruckService();
                    BranchService branchService = new BranchService();

                    CompanyManager companyManager = CompanyManager.getInstance(driverService, locationService, requestService,
                            productService, supplierService, transportService, truckService, branchService);

                    AdminConsole appUI = new AdminConsole(companyManager, productService, transportService, supplierService,
                            requestService, truckService, driverService, branchService, locationService);
                    appUI.start();
                }
                case "2" -> {
                    WorkersFacade workers_facade = new WorkersFacade();
                    WorkersService workers_service = new WorkersService(workers_facade);

                    ShiftJobsFacade jobs_facade = new ShiftJobsFacade();
                    ShiftJobsService jobs_service = new ShiftJobsService(jobs_facade);

                    ShiftCanidatesWorkersFacade candidates_facade = new ShiftCanidatesWorkersFacade();
                    ShiftWorkersCanidatesService candidates_service = new ShiftWorkersCanidatesService(candidates_facade);

                    ShiftPlacmentFacade placement_facade = new ShiftPlacmentFacade(workers_facade, jobs_facade, candidates_facade);
                    ShiftPlacementService placement_service = new ShiftPlacementService(placement_facade);
                    ServiceControl service = new ServiceControl(locationService, workers_service, jobs_service, candidates_service, placement_service);

                    service.run();
                }
                case "3" -> exit = true;
                default -> System.out.println("Invalid choice. trying again.");
            }
        }

        scanner.close();
    }
}