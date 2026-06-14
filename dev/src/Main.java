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

        WorkersFacade workers_facade = new WorkersFacade();
        ShiftJobsFacade jobs_facade = new ShiftJobsFacade();
        ShiftCanidatesWorkersFacade candidates_facade = new ShiftCanidatesWorkersFacade(workers_facade);
        ShiftPlacmentFacade placement_facade = new ShiftPlacmentFacade(workers_facade, jobs_facade, candidates_facade);

        WorkersService workers_service = new WorkersService(workers_facade);
        ShiftJobsService jobs_service = new ShiftJobsService(jobs_facade);
        ShiftWorkersCanidatesService candidates_service = new ShiftWorkersCanidatesService(candidates_facade);
        ShiftPlacementService placement_service = new ShiftPlacementService(placement_facade);

        RequestService requestService = new RequestService();
        ProductCatalogService productService = new ProductCatalogService();
        SupplierService supplierService = new SupplierService();
        TransportManagerService transportService = new TransportManagerService();
        TruckService truckService = new TruckService();
        BranchService branchService = new BranchService();
        LocationService locationService = new LocationService();


        CompanyManager companyManager = CompanyManager.getInstance(locationService, requestService, productService, supplierService,
                transportService, truckService, branchService, workers_service);

        AdminConsole appUI = new AdminConsole(companyManager, productService, transportService, supplierService,
                requestService, truckService, branchService, locationService, workers_service, candidates_service,
                placement_service, jobs_service);

        ServiceControl service = new ServiceControl(locationService, workers_service, jobs_service, candidates_service, placement_service);




        boolean exit = false;
        while(!exit){
            System.out.println("Which system would you like to enter?");
            System.out.println("1. Transport System");
            System.out.println("2. Employee System");
            System.out.println("3. Exit Program");
            System.out.print("Please enter your choice (1 or 2 or 3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    appUI.start();
                }
                case "2" -> {
                    service.run();
                }
                case "3" -> exit = true;
                default -> System.out.println("Invalid choice. trying again.");
            }
        }

        scanner.close();
    }
}