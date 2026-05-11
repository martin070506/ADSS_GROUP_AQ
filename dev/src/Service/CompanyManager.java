package Service;

import Domain.*;
import Domain.BranchManager;

import java.util.*;

public class CompanyManager {
    private final List<Location> allLocations = new ArrayList<>();
    private final List<Product> masterProductCatalog = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<BranchManager> branches = new ArrayList<>();// Store your branches here

    private final TruckService truckService;
    private final DriverService driverService;
    private final ShipmentService shipmentService;
    private final TransportationFacade transportationFacade;

    public CompanyManager(TruckService ts, DriverService ds, ShipmentService ss, TransportationFacade tf) {
        this.truckService = ts;
        this.driverService = ds;
        this.shipmentService = ss;
        this.transportationFacade = tf;
    }

    // --- Master Catalog Management ---
    public void addProductToCatalog(String name, double weight) {
        masterProductCatalog.add(new Product(name, weight));
    }

    public void addBranch(String addr, String phone, String contact) {
        Location loc = new Location(addr, phone, contact);
        allLocations.add(loc);
        branches.add(new Domain.BranchManager(loc));
    }

    public void registerSupplier(String name, String addr, String ph, String contact, List<ProductPair> stock) {
        Location loc = new Location(addr, ph, contact);
        allLocations.add(loc);
        Supplier s = new Supplier(loc, stock);
        suppliers.add(s);
        shipmentService.addSupplier(s);
    }

    // --- Getters ---
    public List<Product> getMasterProductCatalog() { return masterProductCatalog; }
    public List<Domain.BranchManager> getBranches() { return branches; }
    public List<Supplier> getSuppliers() { return suppliers; }
    public List<Location> getAllLocations() { return allLocations; }
    public TruckService getTruckService() { return truckService; }
    public DriverService getDriverService() { return driverService; }
    public List<Transport> getActiveShipments() { return transportationFacade.getActiveTransports(); }

    public ShipmentService getShipmentService() {
        return shipmentService;
    }
}