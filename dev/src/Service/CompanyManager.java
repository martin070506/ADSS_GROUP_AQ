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
    private final ProductCatalogService productService;
    private final SupplierService supplierService;
    private final TransportManagerService transportService;
    private final TruckService truckService;
    private final BranchService branchService;

    private CompanyManager(DriverService driverService, LocationService locationService,
                           RequestService requestService, ProductCatalogService productService,
                           SupplierService supplierService, TransportManagerService transportService,
                           TruckService truckService, BranchService branchService) {
        this.driverService = driverService;
        this.locationService = locationService;
        this.requestService = requestService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.transportService = transportService;
        this.truckService = truckService;
        this.branchService = branchService;
    }

    public static CompanyManager getInstance(DriverService driverService, LocationService locationService,
                                             RequestService requestService, ProductCatalogService productService,
                                             SupplierService supplierService, TransportManagerService transportService,
                                             TruckService truckService, BranchService branchService) {
        if (instance == null) {
            instance = new CompanyManager(driverService, locationService, requestService,
                    productService, supplierService, transportService, truckService, branchService);
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
        Location l = locationService.addLocation(addr, phone, contact);
        branchService.addBranch(l);
    }

    public void registerSupplierByIndices(String addr, String ph, String contact, Map<Integer, Integer> stockIndices) {
        Location loc = new Location(addr, ph, contact);
        locationService.addLocation(loc);

        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : stockIndices.entrySet()) {
            productMap.put(productService.getProductByIndex(entry.getKey()), entry.getValue());
        }
        supplierService.addSupplier(loc, productMap);
    }

    public void finishShipment(int transportId) {
        transportService.removeTransport(transportService.getTransportById(transportId));
    }

    // FIXED: All parameters resolved cleanly from standard UI array list options
    public int createTransportAndGetId(int truckUiIdx, int driverUiIdx, int sourceUiIdx,
                                       Map<Supplier, Map<Product, Integer>> supplierAllocations) {

        Truck truck = truckService.getAvailableTruckByIndex(truckUiIdx);
        Driver driver = driverService.getAvailableDriverByIndex(driverUiIdx);
        Location source = locationService.getLocationByIndex(sourceUiIdx);

        List<Supplier> allSuppliers = supplierService.getSuppliers();
        List<Request> requests = requestService.getAllRequests();
        return transportService.createTransport(truck, driver, source, requests, supplierAllocations);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierService.getSuppliers();
    }

    public BranchManager getBranchManagerByIndex(int branchIndex) {
        return branchService.getBranchByIndex(branchIndex);
    }

    public List<String> getSupplierProductsDisplay(int supplierIndex) {
        List<String> productsDisplay = new ArrayList<>();
        Supplier supplier = supplierService.getSupplierByIndex(supplierIndex);
        for (Map.Entry<Product, Integer> entry : supplier.getProductsAvailable().entrySet())
            productsDisplay.add(entry.getKey().toString());
        return productsDisplay;
    }

    public Product getSupplierProductByDisplayIndex(int supplierIndex, int productUiIndex) {
        Supplier supplier = supplierService.getSupplierByIndex(supplierIndex);
        int counter = 0;
        for (Product product : supplier.getProductsAvailable().keySet()) {
            if (counter == productUiIndex) return product;
            counter++;
        }
        throw new IllegalArgumentException("Product sub-index lookup error.");
    }

    public List<String> getSupplierAmountsDisplay(int supplierIndex) {
        List<String> amountsDisplay = new ArrayList<>();
        Supplier supplier = supplierService.getSupplierByIndex(supplierIndex);
        for (Map.Entry<Product, Integer> entry : supplier.getProductsAvailable().entrySet()){
            amountsDisplay.add("Qty: " + entry.getValue().toString());
        }
        return amountsDisplay;
    }

    public void processTransport(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        System.out.println("Got ID: " + transportId);

        // CRITICAL FIX: Every time this method retries, we must reset the truck's physical inventory count.
        // This stops items that were loaded during a failed attempt from staying on the truck.
        transport.getTruck().emptyTruck();

        while (!transport.getSupplierAllocations().isEmpty()) {
            Supplier currentSupplier = transport.getFirstSupplier();
            Map<Product, Integer> itemsToLoad = transport.getSupplierAllocations().get(currentSupplier);

            transport.getSupplierAllocations().remove(currentSupplier);

            try {
                currentSupplier.handleShipment(itemsToLoad, transport.getTruck());
                transport.getTransportFile().arriveAtSupplier(currentSupplier);
                transport.getTransportFile().leaveSupplier(currentSupplier, transport.getTruck().getCurrentWeight());
            } catch (RuntimeException e) {
                transport.getSupplierAllocations().put(currentSupplier, itemsToLoad);
                throw e;
            }
        }

        while (!transport.getRequests().isEmpty()) {
            Request currentRequest = transport.getRequests().get(0);

            try {
                transport.getTransportFile().arriveAtRequest(currentRequest);
                currentRequest.handleShipment(transport.getTruck());
                transport.getTransportFile().leaveRequest(currentRequest);
                transport.removeRequest(currentRequest);
            } catch (Exceptions.ProductNotFoundOnTruckException itse) {
                System.out.println("\n[SKIPPED DESTINATION] " + itse.getMessage());
                transportService.skipRequest(transportId);
            }
        }
    }

    public void handleStockException(int transportId, DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            transportService.skipSupplier(transportId);
        } else if (ise instanceof InsufficientTruckStockException) {
            transportService.skipRequest(transportId);
        }
    }

    // FIXED: Target specific components inside the active shipment cleanly by key index mappings
    // Inside CompanyManager
    public void resolveOverweightWithFineTuning(int transportId, int UIProductIndex, int amountToRemove) {
        Transport transport = transportService.getTransportById(transportId);

        // Convert the sequential console list index into the target Product object reference
        List<Product> loadedProducts = new ArrayList<>(transport.getTruck().getLoadedProducts().keySet());
        Product targetProduct = loadedProducts.get(UIProductIndex);

        // Prepare the delta payload mapping
        Map<Product, Integer> itemsToRemove = new HashMap<>();
        itemsToRemove.put(targetProduct, amountToRemove);

        // Execute the unified service call
        transportService.manualRemoveItems(transport, itemsToRemove);
    }

    public Transport getTransportById(int transportId) {
        return transportService.getTransportById(transportId);
    }

    public void resolveOverweightIssue(int transportId, String choice) {
        switch (choice) {
            case "2" -> transportService.performEmergencyDropOff(transportId);
            case "4" -> {
                int maxWeight = transportService.getTruckWeightByTransportId(transportId);
                // System replaces it safely using programmatic lookup verification loops
                for (int i = 0; i < truckService.getAvailableTrucksDisplay().size(); i++) {
                    Truck newTruck = truckService.getAvailableTruckByIndex(i);
                    if (newTruck.getMaxWeight() > maxWeight && newTruck.getMinLicense() <= transportService.getDriverLicense(transportId)) {
                        transportService.replaceTruck(transportId, newTruck);
                        break;
                    }
                }
            }
            default -> transportService.skipSupplier(transportId);
        }
    }
    public void addTruck(int truckNumber,String model,int truckWeight,int MaxWeight,int requiredLicense) {
        truckService.addTruck(truckNumber,model,truckWeight,MaxWeight,requiredLicense);
    }
    public void addDriver(String driverName,int license) {
        driverService.addDriver(driverName,license);
    }

    public void loadDemoData() {
        addProductToCatalog("Apple", 150);
        addProductToCatalog("Banana", 120);
        addProductToCatalog("Milk", 6);

        addTruck(101, "Isuzu Sumo", 3500, 7500, 2);
        addDriver("Bob", 2);
        addBranch("Ashdod", "08-222", "Grace");

        try {
            Map<Integer, Integer> demoStock = new HashMap<>();
            demoStock.put(0, 100);
            demoStock.put(1, 100);
            registerSupplierByIndices("Tel Aviv", "03-123", "Alice", demoStock);
        } catch (Exception ignored) {}
    }
    public List<String> getAvailableTrucksDisplay() {return truckService.getAvailableTrucksDisplay();}
    public List<String> getAvailableDriversDisplay() {return driverService.getAvailableDriversDisplay();}
    public List<String> getAllLocationsDisplay() { return locationService.getLocationsDisplay(); }
    public List<String> getProductCatalogDisplay() { return productService.getProductsDisplay(); }
    public List<String> getBranchesDisplay() { return branchService.getBranchesDisplay(); }

    // FIXED: Appends new tracking allocations using target integers purely
    public void addRequest(int storeLocationId, Map<Integer, Integer> selectedItems) {
        Map<Product, Integer> newMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : selectedItems.entrySet()) {
            newMap.put(productService.getProductByIndex(entry.getKey()), entry.getValue());
        }
        requestService.addRequest(branchService.getBranchByIndex(storeLocationId).getLocation(), newMap);
    }

    public Map<Integer, String> getAllSuppliersDisplay() {
        Map<Integer, String> map = new HashMap<>();
        List<Supplier> sups = supplierService.getSuppliers();
        for (int i = 0; i < sups.size(); i++) {
            map.put(i, sups.get(i).getName());
        }
        return map;
    }

    public void resupplySupplier(int sIndex, int catalogProductIndex, int qty) {
        Supplier supplier = supplierService.getSupplierByIndex(sIndex);
        Product product = productService.getProductByIndex(catalogProductIndex);
        supplier.addStock(product, qty);
    }

    public List<String> getActiveRequestLocationsDisplay() {
        return requestService.getActiveRequestLocations();
    }

    // FIXED: Safe positional list manipulation modifiers
    public void updateRequestAddProduct(int requestUiIdx, int catalogProductUiIdx, int qty) {
        Request req = requestService.getRequestByIndex(requestUiIdx);
        Product product = productService.getProductByIndex(catalogProductUiIdx);
        req.addProduct(product, qty);
    }

    public Map<String, Integer> getProductsInRequestDisplay(int requestUiIdx) {
        Map<String, Integer> productsDisplay = new LinkedHashMap<>();
        Request req = requestService.getRequestByIndex(requestUiIdx);
        for (Map.Entry<Product, Integer> entry : req.getProducts().entrySet()) {
            productsDisplay.put(entry.getKey().toString(), entry.getValue());
        }
        return productsDisplay;
    }

    public void updateRequestRemoveProduct(int requestUiIdx, int requestProductUiIdx, int qty) {
        Request req = requestService.getRequestByIndex(requestUiIdx);
        List<Product> productsInRequest = new ArrayList<>(req.getProducts().keySet());
        if (requestProductUiIdx >= 0 && requestProductUiIdx < productsInRequest.size()) {
            req.removeProduct(productsInRequest.get(requestProductUiIdx), qty);
        }
    }

    public Map<String, Integer> getLoadedProductsDisplay(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        Truck truck = transport.getTruck();
        Map<String, Integer> productsDisplay = new LinkedHashMap<>();
        for (Map.Entry<Product, Integer> entry : truck.getLoadedProducts().entrySet()) {
            productsDisplay.put(entry.getKey().toString(), entry.getValue());
        }
        return productsDisplay;
    }

    public void removeRequestByIndex(int index) {
        requestService.removeRequestByIndex(index);
    }

    public String getFirstSupplierName(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        return transport.getSupplierAllocations().keySet().iterator().next().getName();
    }

    public Product getProductByIndex(int requestProductUiIdx) {
        return productService.getProductByIndex(requestProductUiIdx);
    }

    public boolean isValidBranchIndex(int branchIndex) {
        return branchService.getBranchByIndex(branchIndex) != null;
    }

}