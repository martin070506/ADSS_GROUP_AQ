package Service.Transportation;

import DAO.Transportation.TransportFileDAO;
import DTO.Transportation.TransportFileDTO;
import Domain.Transportation.Transport;
import Exceptions.*;
import Service.Workers.ShiftJobsService;
import Service.Workers.WorkersService;

import java.time.LocalDate;
import java.util.Map;

public class TransportManagerService {

    private Transport transport;
    private int transportIdCounter;
    private final TruckService truckService;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final WorkersService workers_service;
    private final ShiftJobsService shiftJobsService;
    private final TransportFileDAO transportFileDAO;


    public TransportManagerService(TruckService truckService, SupplierService supplierService, RequestService requestService, WorkersService workersService, ShiftJobsService shiftJobsService, TransportFileDAO transportFileDAO) {
        this.truckService = truckService;
        this.supplierService = supplierService;
        this.requestService = requestService;
        this.shiftJobsService = shiftJobsService;
        this.transportFileDAO = transportFileDAO;
        this.transport = null;
        this.transportIdCounter = -1;
        this.workers_service = workersService;
    }

    public void createTransport(int truckId, int driverId, int sourceId,
                                Map<Integer, Map<Integer, Integer>> supplierAllocations,
                                String truckInfo, String sourceInfo) {
        transport = new Transport(
                transportIdCounter++,
                java.time.LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth() + 1),
                truckId,
                driverId,
                sourceId,
                supplierAllocations,
                truckInfo,
                "Driver: " + workers_service.getName(driverId) + ", License: " + workers_service.getLicense(driverId),
                sourceInfo);
    }

    public void loadCountFromDB() {
        transportIdCounter = transportFileDAO.getMaxFileNumber() + 1;
    }

    public void finishShipment() {
        truckService.emptyTruck(getTruckId());

        TransportFileDTO dto = new TransportFileDTO(transport.getId(), transport.getTransportFile().toString());
        transportFileDAO.addTransportFile(dto);

        transport = null;
    }

    // === LIVE STATE ENGINE OPERATIONS ===
    public void processTransportLifecycle(boolean isMorning) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        while (transport.hasSuppliers()) {
            int supplierId = transport.getFirstSupplierId();
            Map<Integer, Integer> itemsToLoad = transport.getSupplierAllocations(supplierId);

            transport.UpdateArriveAtSupplier(supplierService.getSupplierName(supplierId));
            supplierService.checkAvailability(supplierId, itemsToLoad);
            truckService.addProductToTruck(transport.getTruckId(), itemsToLoad);
            supplierService.handleShipment(supplierId, itemsToLoad);
            leaveSupplierFileChange(supplierId);
        }

        // 2. Process Branch Delivery Drops Loop
        while (requestService.hasRequests()) {
            int requestId = requestService.getFirstRequestId();
            transport.UpdateArriveAtRequest(requestService.getRequestContactName(requestId));
            if (shiftJobsService.hashShopKeeper(transport.getDepartureTime(), isMorning, requestService.getRequestLocation(requestId)))
                handleRequest(requestId);
            else
                throw new MissingShopKeeper(requestService.getRequestContactName(requestId));
        }
    }

    private void leaveSupplierFileChange(int supplierId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        transport.UpdateLeaveSupplier(supplierService.getSupplierName(supplierId), supplierService.getSupplierDisplay(supplierId), truckService.getTruckWeight(transport.getTruckId()));
        transport.removeSupplierAllocations(supplierId);
    }

    private void handleRequest(int requestId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        try {
            truckService.removeProducts(requestService.getProducts(requestId), transport.getTruckId());
        } catch (ProductNotFoundOnTruckException | InsufficientTruckStockException e) {
            transport.UpdateSkipRequest(requestService.getRequestContactName(requestId), "Insufficient Truck Stock");
            requestService.setUnactive(requestId);
            throw new IgnoreException();
        }
        transport.UpdateLeaveRequest(requestService.getRequestContactName(requestId), requestService.getRequestDisplay(requestId));
        requestService.setUnactive(requestId);
    }

    public void handleStockException(DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            skipSupplier("Insufficient Supplier Stock");
        } else if (ise instanceof InsufficientTruckStockException) {
            skipRequest("Insufficient Truck Stock");
        }
    }

    public void resolveOverweightWithFineTuning(int productId, int amountToRemove, String productName) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");


        Map<Integer, Integer> itemsToRemove = Map.of(productId, amountToRemove);
        truckService.removeProducts(itemsToRemove, transport.getTruckId());
        transport.UpdateDropOff(productName, amountToRemove);
    }

    // === CORE LOGISTICS MUTATORS ===
    public void skipSupplier(String reason){
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        int supplierId = transport.getFirstSupplierId();
        transport.UpdateSkipSupplier(supplierService.getSupplierName(supplierId), reason);
        transport.removeSupplierAllocations(supplierId);
    }

    public void skipRequest(String reason) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (!requestService.hasRequests())
            return;
        int requestId = requestService.getFirstRequestId();
        transport.UpdateSkipRequest(requestService.getRequestContactName(requestId), reason);
        requestService.setUnactive(requestId);
    }

    public void performEmergencyDropOff(int requestId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (!requestService.hasRequests())
            throw new NoDestinationForEmergencyDropOffException();

        transport.UpdateLeaveSupplier(supplierService.getSupplierName(transport.getFirstSupplierId()),
                supplierService.getSupplierDisplay(transport.getFirstSupplierId()), truckService.getTruckWeight(transport.getTruckId()));
        transport.UpdateArriveAtRequest(requestService.getRequestContactName(requestId));
        transport.UpdateLeaveRequest(requestService.getRequestContactName(requestId),
                requestService.getRequestDisplay(requestId));
        truckService.removeProducts(requestService.getProducts(requestId), transport.getTruckId());
        requestService.setUnactive(requestId);
    }

    public void replaceTruck(int newTruckId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        truckService.replaceTrucks(transport.getTruckId(), newTruckId);
        transport.replaceTruck(newTruckId, truckService.getTruckWeight(newTruckId),
                truckService.getTruckDisplay(newTruckId));
    }

    public int getTruckId(){
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        return transport.getTruckId();
    }

    public void startShipment() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        truckService.emptyTruck(getTruckId());
    }

    public int getDriverId() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        return transport.getDriverId();
    }

    public String getFirstSupplierName() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        return supplierService.getSupplierName(transport.getFirstSupplierId());
    }

    public String getTransportFileDisplay(String itemsLeft) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        return transport.getTransportFile().toString(itemsLeft);
    }

    public void skipSupplierAndDropProductsFromTruck(String overweightDefault) {

        Map<Integer, Integer> thingsToRemove = transport.getSupplierAllocations(transport.getFirstSupplierId());
        truckService.removeProducts(thingsToRemove, transport.getTruckId());
        skipSupplier(overweightDefault);
    }

    public void removeLastSupplierProductsFromTruck() {
        Map<Integer, Integer> thingsToRemove = transport.getSupplierAllocations(transport.getFirstSupplierId());
        truckService.removeProducts(thingsToRemove, transport.getTruckId());
    }

    public void leaveFirstSupplier() {
        leaveSupplierFileChange(transport.getFirstSupplierId());
    }
}