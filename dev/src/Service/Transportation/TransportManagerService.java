package Service.Transportation;

import DAO.TransportFileDAO;
import DTO.TransportFileDTO;
import Domain.Transportation.Transport;
import Domain.Transportation.TransportFile;
import Exceptions.*;
import Service.Workers.ShiftJobsService;
import Service.Workers.WorkersService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TransportManagerService {

    private Transport transport;
    private int transportIdCounter;
    private final TruckService truckService;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final WorkersService workers_service;
    private final ShiftJobsService shiftJobsService; // תוקן לאות קטנה
    private final TransportFileDAO transportFileDAO;


    public TransportManagerService(TruckService truckService, SupplierService supplierService, RequestService requestService, WorkersService workersService, ShiftJobsService shiftJobsService, TransportFileDAO transportFileDAO) {
        this.truckService = truckService;
        this.supplierService = supplierService;
        this.requestService = requestService;
        this.shiftJobsService = shiftJobsService; // תוקן לאות קטנה
        this.transportFileDAO = transportFileDAO;
        this.transport = null;
        this.transportIdCounter = 1;
        this.workers_service = workersService;
    }

    public void createTransport(int truckId, int driverId, int sourceId, List<Integer> requests,
                                Map<Integer, Map<Integer, Integer>> supplierAllocations,
                                String truckInfo, String sourceInfo) {
        transport = new Transport(
                transportIdCounter++,
                java.time.LocalDate.now(),
                truckId,
                driverId,
                sourceId,
                requests,
                supplierAllocations,
                truckInfo,
                "Driver: " + workers_service.getName(driverId) + ", License: " + workers_service.getLicense(driverId),
                sourceInfo);
    }

    public void finishShipment() {
        truckService.emptyTruck(getTruckId());

        try {
            TransportFileDTO dto = new TransportFileDTO(transport.getId(), transport.getTransportFile().toString());
            transportFileDAO.addTransportFile(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        transport = null;
    }

    // === LIVE STATE ENGINE OPERATIONS ===
    public void processTransportLifecycle(boolean isMorning) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        while (!transport.getSupplierAllocationIds().isEmpty()) {
            int supplierId = transport.getFirstSupplierId();
            Map<Integer, Integer> itemsToLoad = transport.getSupplierAllocationIds().get(supplierId);

            supplierService.handleShipment(supplierId, itemsToLoad);
            handleSupplierFileChange(supplierId);
        }

        // 2. Process Branch Delivery Drops Loop
        while (!transport.getRequestIds().isEmpty()) {
            int requestId = transport.getRequestIds().getFirst();
            transport.UpdateArriveAtRequest(requestService.getRequestContactName(requestId));
            if (shiftJobsService.hashShopKeeper(transport.getDepartureTime(), isMorning, requestService.getRequestLocation(requestId)))
                handleRequestLeaveFileChange(requestId);
            else
                throw new MissingShopKeeper(requestService.getRequestContactName(requestId));
        }
    }

    private void handleSupplierFileChange(int supplierId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        transport.UpdateArriveAtSupplier(supplierService.getSupplierName(supplierId));
        transport.UpdateLeaveSupplier(supplierService.getSupplierName(supplierId), supplierService.getSupplierDisplay(supplierId), truckService.getTruckWeight(transport.getTruckId()));
        transport.removeSupplier(supplierId);
    }

    private void handleRequestLeaveFileChange(int requestId) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        truckService.removeProducts(requestService.getProducts(requestId), transport.getTruckId());
        transport.UpdateLeaveRequest(requestService.getRequestContactName(requestId), requestService.getRequestDisplay(requestId));
        transport.removeRequest(requestId);
    }

    public void handleStockException(DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            skipSupplier();
        } else if (ise instanceof InsufficientTruckStockException) {
            skipRequest();
        }
    }

    public void resolveOverweightIssue(int choice) {
        if (choice == 2) {
            performEmergencyDropOff();
            finalizeCurrentSupplierLoading();
        } else
            skipSupplier();
    }

    public void resolveOverweightWithFineTuning(int productId, int amountToRemove) {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (transport.getSupplierAllocationIds().isEmpty()) {
            skipSupplier();
        } else {
            int truckId = transport.getTruckId();
            Map<Integer, Integer> itemsToRemove = Map.of(productId, amountToRemove);
            truckService.removeProducts(itemsToRemove, truckId);
            transport.removeItems(itemsToRemove);
        }
    }

    public void finalizeCurrentSupplierLoading(){
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (!transport.getSupplierAllocationIds().isEmpty()) {
            int supplierId = transport.getFirstSupplierId();
            transport.UpdateArriveAtSupplier(supplierService.getSupplierName(supplierId));
            transport.UpdateLeaveSupplier(supplierService.getSupplierName(supplierId),
                    supplierService.getSupplierDisplay(supplierId),
                    truckService.getTruckWeight(transport.getTruckId()));
            transport.getSupplierAllocationIds().remove(supplierId);
        }
    }

    // === CORE LOGISTICS MUTATORS ===
    public void skipSupplier(){
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");
        if (transport.getSupplierAllocationIds().isEmpty())
            return;

        int supplierId = transport.getFirstSupplierId();
        transport.UpdateSkipSupplier(supplierService.getSupplierName(supplierId));
        Map<Integer, Integer> thingsToRemove = transport.getSupplierAllocationIds().get(supplierId);

        try {
            transport.removeItems(thingsToRemove);
            for (Map.Entry<Integer, Integer> entry : thingsToRemove.entrySet())
                supplierService.resupplySupplier(supplierId, entry.getKey(), entry.getValue());

        } catch (ProductNotFoundOnTruckException e) {
            System.out.println(e.getMessage());
        }
        transport.getSupplierAllocationIds().remove(supplierId);
    }

    public void skipRequest() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (transport.getRequestIds().isEmpty())
            return;
        int requestId = transport.getRequestIds().getFirst();
        transport.UpdateSkipSupplier(requestService.getRequestContactName(requestId));
        transport.removeRequest(requestId);
    }

    public void performEmergencyDropOff() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        if (transport.getRequestIds().isEmpty()) {
            throw new NoDestinationForEmergencyDropOffException();
        }
        int requestId = transport.getRequestIds().getFirst();
        transport.UpdateArriveAtRequest(requestService.getRequestContactName(requestId));
        requestService.handleShipment(requestId, truckService.getTruckProducts(transport.getTruckId()));
        truckService.removeProducts(requestService.getProducts(requestId), transport.getTruckId());
        transport.UpdateLeaveRequest(requestService.getRequestContactName(requestId),
                requestService.getRequestDisplay(requestId));
        transport.removeRequest(requestId);
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

    public String getTransportFileDisplay() {
        if (transport == null)
            throw new IllegalArgumentException("No Transport is Currently in Progress.");

        return transport.toString();
    }
}