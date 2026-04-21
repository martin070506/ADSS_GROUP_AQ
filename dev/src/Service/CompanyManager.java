package Service;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CompanyManager {
    private int globalFileNumber = 0;
    private final TruckFacade truckFacade;
    private final ShipmentFacade shipmentFacade;
    private final List<Destination> dropOffDestinations;
    private final List<Location> locations;


    private static CompanyManager instance = null;

    private CompanyManager(List<Truck> trucks, List<Driver> drivers, List<Supplier> suppliers,
                           List<Location> locations) {
        this.truckFacade = new TruckFacade(trucks, drivers);
        this.shipmentFacade = new ShipmentFacade(suppliers,trucks,drivers);
        this.dropOffDestinations = new LinkedList<>();
        this.locations = locations;
    }

    public static CompanyManager getInstance(List<Truck> trucks,List<Driver> drivers,
                                             List<Supplier> suppliers, List<Location> locations) {
        if (instance == null)
            instance = new CompanyManager(trucks, drivers, suppliers, locations);

        return instance;
    }

    public static CompanyManager getInstance() {
        return getInstance(List.of(), List.of(), List.of(), List.of());
    }
    public void addLocation(Location location) {
        if (!locations.contains(location))
            locations.add(location);
    }
    public static void resetInstance() {
        instance = null;
    }

    public void addDestination(Location storeLocation, List<ProductPair> neededItems){
        checkValidListOfItems(neededItems);
        ProductFile productFile = new ProductFile(neededItems, ++globalFileNumber);
        Destination destination = new Destination(storeLocation, productFile);
        dropOffDestinations.add(destination);

    }
    private void checkValidListOfItems(List<ProductPair> neededItems){
        if(new LinkedList<>(neededItems).isEmpty()){
            throw new IllegalArgumentException("List of destinations is empty");
        }
    }

    public Transport createShipment(Truck truck, Driver driver, Location source, Map<Supplier, List<ProductPair>> supplierAllocations) {
        truckFacade.takeTruck(truck);
        truckFacade.takeDriver(driver);

        return shipmentFacade.createTransport(truck, driver, source, dropOffDestinations, truckFacade.getTrucks(), supplierAllocations);
    }

    public void processTransport(Transport transport) throws Exception {
        shipmentFacade.processTransport(transport);
    }

    public void finishShipment(Truck truck, Driver driver) {
        shipmentFacade.finishShipment(truck, driver);
    }
}
