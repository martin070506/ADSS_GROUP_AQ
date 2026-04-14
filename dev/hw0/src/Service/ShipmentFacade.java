package Service;

import Domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ShipmentFacade {

    private int shipmentNumber;
    private List<Supplier> suppliers;

    public ShipmentFacade(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }


    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void startShipment(Truck truck, Driver driver, Location source,
                              List<Destination> destinations, List<Supplier> suppliers,
                              List<Truck> replacementTrucks){


        Map<Supplier, List<ProductPair>> supplierAllocations = chooseSuppliersToVisit(suppliers);

        Transport transport = new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks);

        transport.startShipmentProcess();
    }

}
