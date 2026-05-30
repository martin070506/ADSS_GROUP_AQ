import Presentation.AdminConsole;
import Service.*;

public class Main {
    public static void main(String[] args) {

        DriverService driverService = new DriverService();
        LocationService locationService = new LocationService();
        RequestService requestService = new RequestService();
        ProductCatalogService productService = new ProductCatalogService();
        SupplierService supplierService = new SupplierService();
        TransportManagerService transportService = new TransportManagerService();
        TruckService truckService = new TruckService();
        BranchService branchService = new BranchService();

        CompanyManager companyManager = CompanyManager.getInstance(driverService, locationService, requestService,
                productService, supplierService, transportService, truckService,branchService);

        AdminConsole appUI = new AdminConsole(companyManager);
        appUI.start();
    }
}