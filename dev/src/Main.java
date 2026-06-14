import Presentation.Transportation.AdminConsole;
import Presentation.Workers.ServiceControl;
import Service.Transportation.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Which system would you like to enter?");
        System.out.println("1. Transport System");
        System.out.println("2. Employee System");
        System.out.print("Please enter your choice (1 or 2): ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            DriverService driverService = new DriverService();
            LocationService locationService = new LocationService();
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

        } else if (choice.equals("2")) {
            ServiceControl.main(args);

        } else {
            System.out.println("Invalid choice. Exiting program.");
        }

        scanner.close();
    }
}