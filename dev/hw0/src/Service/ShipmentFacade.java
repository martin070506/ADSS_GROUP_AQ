package Service;

import Domain.*;
import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;
import Exceptions.InsufficientTruckStockException;
import Exceptions.OverweightException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShipmentFacade {

    private int shipmentNumber;
    private final List<Supplier> suppliers;
    private List<Truck> availableTrucks;
    private List<Driver> availableDrivers;

    public ShipmentFacade(List<Supplier> suppliers,List<Truck> availableTrucks,List<Driver> availableDrivers) {

        this.suppliers = suppliers;
        this.availableTrucks = availableTrucks;
        this.availableDrivers = availableDrivers;
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public Transport createTransport(Truck truck, Driver driver, Location source,
                                     List<Destination> destinations, List<Truck> replacementTrucks,
                                     Map<Supplier, List<ProductPair>> supplierAllocations) {

        List<Supplier> suppliersAsList = new ArrayList<>(supplierAllocations.keySet());
        return new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks, suppliersAsList);
    }

    public void processTransport(Transport transport) throws Exception {
        transport.processShipment();
    }

    // פונקציה 3: משחררת את המשאית והנהג בסוף
    public void finishShipment(Truck truck, Driver driver) {
        availableTrucks.add(truck);
        availableDrivers.add(driver);
    }
}
