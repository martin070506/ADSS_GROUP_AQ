package Service.Transportation;

import Domain.Transportation.*;
import Service.Workers.WorkersService;

import java.util.*;

public class CompanyManager {
    private static CompanyManager instance;
    private final LocationService locationService;
    private final RequestService requestService;
    private final ProductCatalogService productService;
    private final SupplierService supplierService;
    private final TransportManagerService transportService;
    private final TruckService truckService;
    private final BranchService branchService;
    private final WorkersService workers_service;

    private CompanyManager(LocationService locationService,
                           RequestService requestService, ProductCatalogService productService,
                           SupplierService supplierService, TransportManagerService transportService,
                           TruckService truckService, BranchService branchService, WorkersService workers_service) {
        this.locationService = locationService;
        this.requestService = requestService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.transportService = transportService;
        this.transportService.setService(truckService, workers_service);
        this.truckService = truckService;
        this.branchService = branchService;
        this.workers_service = workers_service;
    }

    public static CompanyManager getInstance(LocationService ls, RequestService rs, ProductCatalogService pcs, SupplierService ss,
                                             TransportManagerService tms, TruckService ts, BranchService bs, WorkersService ws) {
        if (instance == null) {
            instance = new CompanyManager(ls, rs, pcs, ss, tms, ts, bs, ws);
        }
        return instance;
    }

    public static CompanyManager getInstance() {
        if (instance == null) throw new IllegalStateException("CompanyManager is not initialized.");
        return instance;
    }

    // === ADMINISTRATIVE CONFIGURATION DELEGATIONS ===


    public void registerSupplierByIndices(String addr, String ph, String contact, Map<Integer, Integer> stockIndices) {
        Location loc = locationService.addLocation(addr, ph, contact);
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : stockIndices.entrySet()) {
            productMap.put(productService.getProductById(entry.getKey()), entry.getValue());
        }
        supplierService.addSupplier(loc, productMap);
    }

    public void addBranchLocation(String addr,String ph,String contact){
        Location loc = locationService.addLocation(addr, ph, contact);
        branchService.addBranch(loc);
    }

    public void addRequest(int storeLocationId, Map<Integer, Integer> selectedItems) {
        Map<Product, Integer> newMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : selectedItems.entrySet()) {
            newMap.put(productService.getProductById(entry.getKey()), entry.getValue());
        }
        requestService.addRequest(branchService.getBranchById(storeLocationId).getLocation(), newMap);
    }

    public void resupplySupplier(int sIndex, int catalogProductIndex, int qty) {
        supplierService.getSupplierById(sIndex).addStock(productService.getProductById(catalogProductIndex), qty);
    }

    public void updateRequestAddProduct(int requestUiIdx, int catalogProductUiIdx, int qty) {
        requestService.getRequestByIndex(requestUiIdx).addProduct(productService.getProductById(catalogProductUiIdx), qty);
    }

    public void updateRequestRemoveProduct(int requestUiIdx, int requestProductUiIdx, int qty) {
        Request req = requestService.getRequestByIndex(requestUiIdx);
        List<Product> productsInRequest = new ArrayList<>(req.getProducts().keySet());
        if (requestProductUiIdx >= 0 && requestProductUiIdx < productsInRequest.size()) {
            req.removeProduct(productsInRequest.get(requestProductUiIdx), qty);
        }
    }

    // === LIVE SHIPMENT WORKFLOW DELEGATIONS ===

    public int createTransportAndGetId(int truckUiIdx, int driverId, int sourceUiIdx,
                                       Map<Integer, Map<Integer, Integer>> supplierAllocations) {
        Truck truck = truckService.getAvailableTruckById(truckUiIdx);
        // Driver driver = workers_service.getAvailableDriverByIndex(driverUiIdx);
        Location source = locationService.getLocationById(sourceUiIdx);
        List<Request> requests = requestService.getAllRequests();

        Map<Supplier, Map<Product, Integer>> supplierAllocationsMap = new HashMap<>();
        for (Map.Entry<Supplier, Map<Integer, Integer>> entry : supplierService.mapIndicesToSuppliers(supplierAllocations).entrySet()){
            supplierAllocationsMap.put(entry.getKey(), productService.mapIndicesToProducts(entry.getValue()));
        }
        return transportService.createTransport(truck, driverId, source, requests, supplierAllocationsMap);
    }




    // === DISPLAY READ-ONLY PASSTHROUGHS ===

    public Map<String, Integer> getProductsInRequestDisplay(int requestUiIdx) {
        Map<String, Integer> productsDisplay = new LinkedHashMap<>();
        for (Map.Entry<Product, Integer> entry : requestService.getRequestByIndex(requestUiIdx).getProducts().entrySet()) {
            productsDisplay.put(entry.getKey().toString(), entry.getValue());
        }
        return productsDisplay;
    }

    public Map<String, Integer> getLoadedProductsDisplay(int transportId) {
        Map<String, Integer> productsDisplay = new LinkedHashMap<>();
        for (Map.Entry<Product, Integer> entry : transportService.getTransportById(transportId).getTruck().getLoadedProducts().entrySet()) {
            productsDisplay.put(entry.getKey().toString(), entry.getValue());
        }
        return productsDisplay;
    }

    public String getFirstSupplierNameByTransportId(int transportId) {
        return transportService.getTransportById(transportId).getSupplierAllocations().keySet().iterator().next().getName();
    }

    public String getTransportFileDisplayById(int transportId) {
        return transportService.getTransportById(transportId).getTransportFile().toString();
    }

    public boolean checkDriverTruck(int driverId, int truckId) {
        return workers_service.getLicense(driverId) >= truckService.getAvailableTruckById(truckId).getMinLicense();
    }
}