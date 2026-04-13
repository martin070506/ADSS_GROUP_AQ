package Service;

import Domain.*;

import java.util.List;

public class ShipmentFacade {

    private int shipmentNumber;
    private List<Supplier> suppliers;

    public ShipmentFacade(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }


    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void startShipment(Truck truck, Driver driver, List<Destination> dropOffDestinations, List<Truck> trucks){
        Transport transport=new Transport(truck,driver,dropOffDestinations,trucks);
        transport.chooseSuppliersToVisit(suppliers);
        transport.startShipmentProcess();
    }

}
