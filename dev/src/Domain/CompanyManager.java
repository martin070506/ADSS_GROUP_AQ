package Domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompanyManager {
    private int globalFileNumber = 0;
    private final TruckFacade truckFacade;
    private final DriverFacade driverFacade;
    private final TransportManager shipmentManager;
    private final List<Destination> dropOffDestinations;
    private final List<Location> locations;
    private final  List<BranchManager> branchManagers;


    private static CompanyManager instance = null;

    private CompanyManager(List<Truck> trucks, List<Driver> drivers, List<Supplier> suppliers,
                           List<Location> locations,List<BranchManager> branchManagers) {
        this.truckFacade = new TruckFacade(trucks);
        this.driverFacade = new DriverFacade(drivers);
        this.shipmentManager = new TransportManager(suppliers,trucks,drivers);
        this.dropOffDestinations = new LinkedList<>();
        this.locations = locations;
        this.branchManagers = branchManagers;
    }

    public static CompanyManager getInstance(List<Truck> trucks,List<Driver> drivers,
                                             List<Supplier> suppliers, List<Location> locations,List<BranchManager> branchManagers) {
        if (instance == null)
            instance = new CompanyManager(trucks, drivers, suppliers, locations, branchManagers);

        return instance;
    }

    public static CompanyManager getInstance() {
        return getInstance(List.of(), List.of(), List.of(), List.of(), List.of());
    }

    public void addLocation(Location location) {
        if (!locations.contains(location))
            locations.add(location);
    }

    public void addBranchManager(BranchManager branchManager) {
        if (!branchManagers.contains(branchManager))
            branchManagers.add(branchManager);
    }

    public void addSupplier(Supplier supplier) {
        if(!shipmentManager.containsSupplier(supplier))
            shipmentManager.addSupplier(supplier);
    }
    public void addDriver(Driver driver) {
        if(!shipmentManager.containsDriver(driver))
            shipmentManager.addDriver(driver);
    }
    public void addTruck(Truck truck) {
        if(!shipmentManager.containsTruck(truck))
            shipmentManager.addTruck(truck);
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
        driverFacade.takeDriver(driver);

        return shipmentManager.createTransport(truck, driver, source, dropOffDestinations, truckFacade.getTrucks(), supplierAllocations);
    }

    public void processTransport(Transport transport) throws Exception {
        shipmentManager.processTransport(transport);
    }

    public void finishShipment(Truck truck, Driver driver) {
        shipmentManager.finishShipment(truck, driver);
    }


    public TransportManager getShipmentManager() {
        return shipmentManager;
    }

    public TruckFacade getTruckFacade() {
        return truckFacade;
    }

    public DriverFacade getDriverFacade() {
        return driverFacade;
    }

    public List<Location> getLocations() {
        return locations;
    }
}
