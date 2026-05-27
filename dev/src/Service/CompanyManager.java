package Service;

import Domain.*;
import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;
import Exceptions.InsufficientTruckStockException;

import java.util.*;

public class CompanyManager {
    private static CompanyManager instance;
    private final DriverService driverService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final TransportService transportService;
    private final TruckService truckService;

    private CompanyManager(DriverService driverService, LocationService locationService,
                           RequestService requestService, ProductService productService,
                           SupplierService supplierService, TransportService transportService,
                           TruckService truckService) {
        this.driverService = driverService;
        this.locationService = locationService;
        this.requestService = requestService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.transportService = transportService;
        this.truckService = truckService;
    }

    public static CompanyManager getInstance(DriverService driverService, LocationService locationService,
                                             RequestService requestService, ProductService productService,
                                             SupplierService supplierService, TransportService transportService,
                                             TruckService truckService) {
        if (instance == null) {
            instance = new CompanyManager(driverService, locationService, requestService,
                    productService, supplierService, transportService, truckService);
        }
        return instance;
    }

    public static CompanyManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CompanyManager is not initialized yet.");
        }
        return instance;
    }

    public void addProductToCatalog(String name, int weight) {
        productService.addProduct(name, weight);
    }

    public void addBranch(String addr, String phone, String contact) {
        locationService.addLocation(new Location(addr, phone, contact));
    }

    public void registerSupplier(String addr, String ph, String contact, Map<String, Integer> stockIndices) {
        Location loc = new Location(addr, ph, contact);
        locationService.addLocation(loc);

        Map<Product, Integer> productMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : stockIndices.entrySet()) {
            productMap.put(productService.getProduct(entry.getKey()), entry.getValue());
        }

        supplierService.addSupplier(loc, productMap);
    }

    public int createTransportAndGetId(String truckName, String driverName, String sourceName,
                                       Map<Integer, Map<String, Integer>> supplierAllocationsIndices) {
        Truck truck = truckService.getAvailableTruck(truckName);
        Driver driver = driverService.getAvailableDriver(driverName);
        Location source = locationService.getLocation(sourceName);

        List<Supplier> allSuppliers = supplierService.getSuppliers();

        Map<Supplier, Map<String, Integer>> intermediateMap =
                Supplier.mapIndexesToSuppliers(allSuppliers, supplierAllocationsIndices);

        Map<Supplier, Map<Product, Integer>> domainAllocations = new HashMap<>();
        for (Map.Entry<Supplier, Map<String, Integer>> entry : intermediateMap.entrySet())
            domainAllocations.put(entry.getKey(), productService.mapStringsToProducts(entry.getValue()));

        List<Request> requests = requestService.getAllRequests();

        return transportService.createTransport(truck, driver, source, requests, domainAllocations);
    }

    public List<String> getSupplierProductsDisplay(int supplierIndex) {
        List<String> productsDisplay = new ArrayList<>();
        Supplier supplier = supplierService.getSupplierByIndex(supplierIndex);

        for (Map.Entry<Product, Integer> entry : supplier.getProductsAvailable().entrySet())
            productsDisplay.add(entry.getKey().name() + " (Qty: " + entry.getValue() + ")");

        return productsDisplay;
    }

    public void processTransport(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        transportService.processTransport(transport);
    }

    public void handleStockException(int transportId, DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            transportService.skipSupplier(transportId);
        } else if (ise instanceof InsufficientTruckStockException) {
            transportService.skipRequest(transportId);
        }
    }

    public void resolveOverweightWithFineTuning(int transportId, Map<String, Integer> itemsToRemoveIndices) {
        Transport transport = transportService.getTransportById(transportId);
        Map<Product, Integer> itemsToRemove = productService.mapStringsToProducts(itemsToRemoveIndices);
        transportService.manualRemoveItems(transport, itemsToRemove);
    }

    public void resolveOverweightIssue(int transportId, String choice) {

        switch (choice) {
            case "2" -> transportService.performEmergencyDropOff(transportId);
            case "4" -> {
                List<String> available = truckService.getAvailableTrucksDisplay(transportService.getDriverLicense(transportId));
                int maxWeight = transportService.getTruckWeightByTransportId(transportId);
                for (String truckName : available) {
                    Truck newTruck = truckService.getAvailableTruck(truckName);
                    if (newTruck.getMaxWeight() > maxWeight) {
                        transportService.replaceTruck(transportId, newTruck);
                    }
                }
            }
            default -> transportService.skipSupplier(transportId);
        }
    }

    public void loadDemoData() {
        addProductToCatalog("Apple", 150);
        addProductToCatalog("Banana", 120);
        addProductToCatalog("Milk", 6);

        addTruck(101, "Isuzu Sumo", 3500, 7500, 2);
        addDriver("Bob", 2);
        addBranch("Ashdod", "08-222", "Grace");

        try {
            Map<String, Integer> demoStock = new HashMap<>();
            List<String> cat = productService.getProductsDisplay();
            if (cat.size() >= 2) {
                demoStock.put(cat.get(0), 100);
                demoStock.put(cat.get(1), 100);
            }
            registerSupplier("Tel Aviv", "03-123", "Alice", demoStock);
        } catch (Exception ignored) {
        }
    }

    public List<String> getAllLocationsDisplay() {
        return locationService.getLocationsDisplay();
    }

    public List<String> getProductCatalogDisplay() {
        return productService.getProductsDisplay();
    }

    public void addRequest(String locationName, Map<String, Integer> requestedProductsIndices) {
        Map<Product, Integer> products = productService.mapStringsToProducts(requestedProductsIndices);
        Location location = locationService.getLocation(locationName);
        requestService.addRequest(location, products);
    }

    public Map<Integer, String> getAllSuppliersDisplay() {
        Map<Integer, String> map = new HashMap<>();
        List<Supplier> sups = supplierService.getSuppliers();
        for (int i = 0; i < sups.size(); i++) {
            map.put(i, sups.get(i).getName());
        }
        return map;
    }

    public void resupplySupplier(int sIndex, String productName, int qty) {
        Supplier supplier = supplierService.getSuppliers().get(sIndex);
        Product product = productService.getProduct(productName);
        supplier.addStock(product, qty);
    }

    public List<String> getActiveRequestLocationsDisplay() {
        return requestService.getActiveRequestLocations();
    }


    public void updateRequestAddProduct(String requestName, String productName, int qty) {
        Request req = requestService.getRequest(requestName);
        Product product = productService.getProduct(productName);
        req.addProduct(product, qty);
    }

    public Map<String, Integer> getProductsInRequestDisplay(String requestName) {
        Map<String, Integer> productsDisplay = new HashMap<>();
        Request req = requestService.getRequest(requestName);

        Map<Product, Integer> requestProducts = req.getProducts();

        for (Map.Entry<Product, Integer> entry : requestProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            productsDisplay.put(product.toString(), quantity);
        }
        return productsDisplay;
    }

    public void updateRequestRemoveProduct(String requestName , String productName, int qty) {
        Request req = requestService.getRequest(requestName);
        Product product = productService.getProduct(productName);

        req.removeProduct(product, qty);
    }

    public void addDriver(String name, int lic) {
        driverService.addDriver(new Driver(name, lic));
    }

    public void addTruck(int id, String model, int weight, int max, int lic) {
        truckService.addTruck(new Truck(id, model, weight, max, lic));
    }

    public List<String> getAvailableDriversDisplay() {
        return driverService.getAvailableDriversDisplay();
    }

    public List<String> getAvailableTrucksDisplay() {
        return truckService.getAvailableTrucksDisplay();
    }

    public Map<String, Integer> getLoadedProductsDisplay(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        Truck truck = transport.getTruck();
        Map<Product, Integer> loadedProducts = truck.getLoadedProducts();
        Map<String, Integer> productsDisplay = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : loadedProducts.entrySet()) {
            productsDisplay.put(entry.getKey().name(), entry.getValue());
        }
        return productsDisplay;
    }

    public void removeRequest(String request) {
        requestService.removeRequest(request);
    }

    public void addRequest(Location storeLocation, Map<Product, Integer> neededItems) {
        requestService.addRequest(storeLocation, neededItems);
    }

    public String getFirstSupplierName(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        return transport.getSupplierAllocations().keySet().iterator().next().getName();
    }
}