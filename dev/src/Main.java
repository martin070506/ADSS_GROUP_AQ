import Presentation.AdminConsole;
import Service.*;

public class Main {
    public static void main(String[] args) {

        DriverService driverService = new DriverService();
        LocationService locationService = new LocationService();
        RequestService requestService = new RequestService();
        ProductService productService = new ProductService();
        SupplierService supplierService = new SupplierService();
        TransportService transportService = new TransportService();
        TruckService truckService = new TruckService();

        CompanyManager companyManager = CompanyManager.getInstance(driverService, locationService, requestService,
                productService, supplierService, transportService, truckService);

        AdminConsole appUI = new AdminConsole(companyManager);
        appUI.start();
    }
}