package Service;

import Domain.*;
import Domain.BranchManager;


import java.util.*;

public class CompanyManager {
    private final List<Location> allLocations = new ArrayList<>();
    private final List<Product> masterProductCatalog = new ArrayList<>();
    private final List<BranchManager> branches = new ArrayList<>();// Store your branches here
    private final List<Destination> currentRequests = new ArrayList<>();
    private static  CompanyManager instance;
    private final TruckService truckService;
    private final DriverService driverService;
    private final ShipmentService shipmentService;
    private final ProductService productService;
    private final TransportationFacade transportationFacade;
    private int requestId=0;

    private CompanyManager(TruckService ts, DriverService ds, ShipmentService ss, TransportationFacade tf,ProductService ps) {
        this.truckService = ts;
        this.driverService = ds;
        this.shipmentService = ss;
        this.transportationFacade = tf;
        this.productService = ps;
    }
    public static CompanyManager getInstance(TruckService ts, DriverService ds, ShipmentService ss, TransportationFacade tf,ProductService ps){
        if (instance==null){
            instance=new CompanyManager(ts,ds,ss,tf,ps);
        }
        return instance;
    }
    public static CompanyManager getInstance(){
        return  instance;
    }

    // --- Master Catalog Management ---
    public void addProductToCatalog(String name, double weight) {
        productService.addProduct(new Product(name, weight));
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
        shipmentService.addSupplier(s);
    }
    public void finalizeShipment(Transport transport) {
        currentRequests.clear();
        shipmentService.finalizeShipment(transport.getTruck(),transport.getDriver());
    }

    public List<Destination> getRequestsByBranch(Location loc) {
        List<Destination> destinations = new ArrayList<>();
        for (Destination d : currentRequests) {
            if(d.getLocation()==loc) destinations.add(d);
        }
        return destinations;
    }

    public void removeRequestsByBranch(Destination d) {
        currentRequests.remove(d);
    }

    // --- Getters ---
    public List<Product> getMasterProductCatalog() { return productService.getProducts(); }
    public List<Domain.BranchManager> getBranches() { return branches; }
    public List<Location> getAllLocations() { return allLocations; }
    public TruckService getTruckService() { return truckService; }
    public DriverService getDriverService() { return driverService; }
    public List<Transport> getActiveShipments() { return transportationFacade.getActiveTransports(); }
    public void addDestination(Location location,List<ProductPair> products) {
        currentRequests.add(new Destination(location,new ProductFile(products,requestId++)));
    }
    public List<Destination> getCurrentRequests() { return currentRequests; }
    public ShipmentService getShipmentService() {
        return shipmentService;
    }

    public List<Location> getActiveRequestBranches(){
        List<Location> activeRequestBranches = new ArrayList<>();
        for (Destination d : currentRequests) {
            if(!activeRequestBranches.contains(d.getLocation())) activeRequestBranches.add(d.getLocation());
        }
        return activeRequestBranches;
    }
}