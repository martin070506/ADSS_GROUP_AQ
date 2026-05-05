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

    public Transport createTransport(Truck truck, Driver driver, Location source,
                                     List<Destination> destinations, List<Truck> replacementTrucks,
                                     Map<Supplier, List<ProductPair>> supplierAllocations) {

        List<Supplier> suppliersAsList = new ArrayList<>(supplierAllocations.keySet());
        return new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks, suppliersAsList);
    }
    public boolean containsSupplier(Supplier supplier) {
        return suppliers.contains(supplier);
    }
    public boolean containsTruck(Truck truck) {
        return availableTrucks.contains(truck);
    }
    public boolean containsDriver(Driver driver) {
        return availableDrivers.contains(driver);
    }
    public void addDriver(Driver driver) {
        availableDrivers.add(driver);
    }
    public void addTruck(Truck truck) {
        availableTrucks.add(truck);
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }
    public void processTransport(Transport transport) throws Exception {
        transport.processShipment();
    }
    public void finishShipment(Truck truck, Driver driver) {
        System.out.println("jahjjafghafghafhyfhfhyafyagfyhgyh");
        truck.emptyTruck();
        availableTrucks.add(truck);
        availableDrivers.add(driver);
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }
}
