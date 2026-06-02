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

    public int createTransportAndGetId(int truckUiIdx, int driverUiIdx, int sourceUiIdx,
                                       Map<Integer, Map<Integer, Integer>> supplierAllocations) {

        Truck truck = truckService.getAvailableTruckByIndex(truckUiIdx);
        Driver driver = driverService.getAvailableDriverByIndex(driverUiIdx);
        Location source = locationService.getLocationByIndex(sourceUiIdx);

        List<Request> requests = requestService.getAllRequests();

        Map<Supplier, Map<Product, Integer>> supplierAllocationsMap = new HashMap<>();
        for (Map.Entry<Supplier, Map<Integer, Integer>> entry : supplierService.mapIndicesToSuppliers(supplierAllocations).entrySet()){
            supplierAllocationsMap.put(entry.getKey(), productService.mapIndicesToProducts(entry.getValue()));
        }
        return transportService.createTransport(truck, driver, source, requests, supplierAllocationsMap);
    }

    public List<String> getSupplierProductsDisplay(int supplierIndex) {
        List<String> productsDisplay = new ArrayList<>();
        Supplier supplier = supplierService.getSupplierByIndex(supplierIndex);
        for (Map.Entry<Product, Integer> entry : supplier.getProductsAvailable().entrySet())
            productsDisplay.add(entry.getKey().toString());
        return productsDisplay;
    }

    private Product getSupplierProductByDisplayIndex(int supplierIndex, int productUiIndex) {
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
            Request currentRequest = transport.getRequests().getFirst();

            try {
                transport.getTransportFile().arriveAtRequest(currentRequest);
                currentRequest.handleShipment(transport.getTruck());
                transport.getTransportFile().leaveRequest(currentRequest);
                transport.removeRequest(currentRequest);
            } catch (Exceptions.ProductNotFoundOnTruckException itse) {
                System.out.println("Skipped Destination:" + itse.getMessage());
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

    public void loadDemoData() {

        productService.addProduct("Apple", 150);
        productService.addProduct("Banana", 120);
        productService.addProduct("Milk", 6);
        productService.addProduct("Bread", 15);
        productService.addProduct("Eggs", 30);
        productService.addProduct("Cheese", 45);
        productService.addProduct("Chicken", 80);

        truckService.addTruck(101, "Isuzu Sumo", 3500, 7500, 2);
        truckService.addTruck(102, "Mercedes Sprinter", 2000, 4000, 1);
        truckService.addTruck(103, "Volvo FH", 8000, 25000, 3);
        truckService.addTruck(104, "Renault Master", 2200, 4500, 1);

        driverService.addDriver("Bob", 2);
        driverService.addDriver("Charlie", 1);
        driverService.addDriver("David", 3);
        driverService.addDriver("Eve", 2);

        addBranch("Ashdod", "08-222-9900", "Grace");
        addBranch("Jerusalem", "02-555-8800", "Heidi");
        addBranch("Haifa", "04-333-7700", "Ivan");
        addBranch("Eilat", "08-999-6600", "Judy");

        try {
            Map<Integer, Integer> stockTelAviv = new HashMap<>();
            stockTelAviv.put(0, 100); // Apple
            stockTelAviv.put(1, 100); // Banana
            stockTelAviv.put(2, 50);  // Milk
            registerSupplierByIndices("Tel Aviv", "03-123-9901", "Alice", stockTelAviv);

            Map<Integer, Integer> stockPetahTikva = new HashMap<>();
            stockPetahTikva.put(3, 200); // Bread
            stockPetahTikva.put(4, 150); // Eggs
            registerSupplierByIndices("Petah Tikva", "03-555-4433", "Frank", stockPetahTikva);

            Map<Integer, Integer> stockRishon = new HashMap<>();
            stockRishon.put(5, 80);  // Cheese
            stockRishon.put(6, 120); // Chicken
            registerSupplierByIndices("Rishon LeZion", "03-888-2211", "George", stockRishon);

        } catch (Exception e) {
            System.err.println("Error loading supplier demo data: " + e.getMessage());
        }
    }

    // FIXED: Appends new tracking allocations using target integers purely
    public void addRequest(int storeLocationId, Map<Integer, Integer> selectedItems) {
        Map<Product, Integer> newMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : selectedItems.entrySet()) {
            newMap.put(productService.getProductByIndex(entry.getKey()), entry.getValue());
        }
        requestService.addRequest(branchService.getBranchByIndex(storeLocationId).getLocation(), newMap);
    }

    public void resupplySupplier(int sIndex, int catalogProductIndex, int qty) {
        Supplier supplier = supplierService.getSupplierByIndex(sIndex);
        Product product = productService.getProductByIndex(catalogProductIndex);
        supplier.addStock(product, qty);
    }

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

    public String getFirstSupplierNameByTransportId(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        return transport.getSupplierAllocations().keySet().iterator().next().getName();
    }

    public String getTransportFileDisplayById(int transportId) {
        Transport transport = transportService.getTransportById(transportId);
        return transport.getTransportFile().toString();
    }

    public boolean checkDriverTruck(int driverIndex, int truckIndex) {
        Driver driver = driverService.getAvailableDriverByIndex(driverIndex);
        Truck truck = truckService.getAvailableTruckByIndex(truckIndex);
        return driver.getLicense() >= truck.getMinLicense();
    }
}