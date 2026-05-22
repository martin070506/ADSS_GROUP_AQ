package Domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportManager {

    private final List<Supplier> suppliers;
    private final List<Truck> trucks;
    private final List<Driver> drivers;

    int globalId=0;

    public TransportManager(List<Supplier> suppliers, List<Truck> trucks, List<Driver> drivers) {
        this.drivers = drivers;
        this.trucks = trucks;
        this.suppliers = suppliers;
    }

    public Transport createTransport(Truck truck, Driver driver, Location source,
                                     List<Destination> destinations, List<Truck> replacementTrucks,
                                     Map<Supplier, List<ProductPair>> supplierAllocations) {

        List<Supplier> suppliersAsList = new ArrayList<>(supplierAllocations.keySet());
        return new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks, suppliersAsList,globalId++);
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void processTransport(Transport transport) throws Exception {
        transport.processShipment();
    }
    public void finishShipment(Truck truck,Driver driver) {
        truck.emptyTruck();
        trucks.add(truck);
        drivers.add(driver);
    }
    public void removeSupplier(Supplier supplier) {
        suppliers.remove(supplier);
    }



    public List<Supplier> getSuppliers() {
        return suppliers;
    }
}
