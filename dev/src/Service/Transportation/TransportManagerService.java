package Service.Transportation;

import Domain.Transportation.Transport;
import Exceptions.*;
import Service.Workers.WorkersService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportManagerService {

    private final List<Transport> transports;
    private int transportIdCounter;
    private final TruckService truckService;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final WorkersService workers_service;


    public TransportManagerService(TruckService truckService, TruckService truckService1, SupplierService supplierService, RequestService requestService, WorkersService workersService) {
        this.truckService = truckService1;
        this.supplierService = supplierService;
        this.requestService = requestService;
        this.transports = new ArrayList<>();
        this.transportIdCounter = 1;
        this.workers_service = workersService;
    }

    public int createTransport(int truckId, int driverId, int sourceId, List<Integer> requests,
                               Map<Integer, Map<Integer, Integer>> supplierAllocations,
                               String truckInfo, String sourceInfo) {
        Transport transport = new Transport(
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

        transports.add(transport);
        return transport.getId();
    }

//    public Transport getTransportById(int id) {
//        for (Transport transport : transports)
//            if (transport.getId() == id)
//                return transport;
//
//        throw new DomainException("Transport not found");
//    }

    public void removeTransportById(int transportById) {
        transports.removeIf(transport -> transport.getId() == transportById);
    }

    public void finishShipment(int transportId) {
        truckService.emptyTruck(getTruckId(transportId));
    }

    // === LIVE STATE ENGINE OPERATIONS ===
    public void processTransportLifecycle(int transportId) {
        Transport transport = getTransport(transportId);

        while (!transport.getSupplierAllocationIds().isEmpty()) {
            int supplierId = transport.getFirstSupplierId();
            Map<Integer, Integer> itemsToLoad = transport.getSupplierAllocationIds().get(supplierId);

            supplierService.handleShipment(supplierId, itemsToLoad);

            transport.UpdateArriveAtSupplier(supplierService.getSupplierName(supplierId));
            transport.UpdateLeaveSupplier(supplierService.getSupplierName(supplierId),
                    supplierService.getSupplierDisplay(supplierId), truckService.getTruckWeight(transport.getTruckId()));
            transport.removeSupplier(supplierId);
        }

        // 2. Process Branch Delivery Drops Loop
        while (!transport.getRequestIds().isEmpty()) {
            int requestId = transport.getRequestIds().getFirst();
            try {
                transport.UpdateArriveAtRequest(requestService.getRequestContactName(requestId));
                truckService.removeProducts(requestService.getProducts(requestId), transport.getTruckId());
                transport.UpdateLeaveRequest(requestService.getRequestContactName(requestId), requestService.getRequestDisplay(requestId));
                transport.removeRequest(requestId);
            } catch (Exceptions.ProductNotFoundOnTruckException its) {
                System.out.println("Skipped Destination: " + its.getMessage()); // TODO: remove print
                skipRequest(transportId);
            }
        }
    }

    private Transport getTransport(int transportId) {
        for (Transport transport : transports)
            if (transport.getId() == transportId)
                return transport;

        throw new IllegalArgumentException("Transport not found: " + transportId);
    }

    public void handleStockException(int transportId, DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            skipSupplier(transportId);
        } else if (ise instanceof InsufficientTruckStockException) {
            skipRequest(transportId);
        }
    }

    public void resolveOverweightIssue(int transportId, int choice) {
        if (choice == 2) {
            performEmergencyDropOff(transportId);
            finalizeCurrentSupplierLoading(transportId);
        } else
            skipSupplier(transportId);
    }

    public void resolveOverweightWithFineTuning(int transportId, int productId, int amountToRemove) {
        Transport transport = getTransport(transportId);
        if (transport.getSupplierAllocationIds().isEmpty()) {
            skipSupplier(transportId);
        } else {
            int truckId = transport.getTruckId();
            Map<Integer, Integer> itemsToRemove = Map.of(productId, amountToRemove);
            truckService.removeProducts(itemsToRemove, truckId);
            transport.removeItems(itemsToRemove);
        }
    }

    public void finalizeCurrentSupplierWithFineTune(int transportId) {
        finalizeCurrentSupplierLoading(transportId);
    }
//
public void finalizeCurrentSupplierLoading(int transportId){
            Transport transport = getTransport(transportId);
            if (!transport.getSupplierAllocationIds().isEmpty()) {
                int supplierId = transport.getFirstSupplierId();
                transport.UpdateArriveAtSupplier(supplierService.getSupplierName(supplierId));
                transport.UpdateLeaveSupplier(supplierService.getSupplierName(supplierId),
                        supplierService.getSupplierDisplay(supplierId),
                        truckService.getTruckWeight(transport.getTruckId()));
                transport.getSupplierAllocationIds().remove(supplierId);
            }
        }
//
//    // === CORE LOGISTICS MUTATORS ===
        public void skipSupplier ( int transportIndex){
            Transport transport = getTransport(transportIndex);
            if (transport.getSupplierAllocationIds().isEmpty())
                return;

            int supplierId = transport.getFirstSupplierId();
            transport.UpdateSkipSupplier(supplierService.getSupplierName(supplierId));
            Map<Integer, Integer> thingsToRemove = transport.getSupplierAllocationIds().get(supplierId);

            try {
                transport.removeItems(thingsToRemove);
                for (Map.Entry<Integer, Integer> entry : thingsToRemove.entrySet())
                    supplierService.addStock(entry.getKey(), entry.getValue(), supplierId);

            } catch (ProductNotFoundOnTruckException e) {
                System.out.println(e.getMessage());
            }
            transport.getSupplierAllocationIds().remove(supplierId);
        }

        public void skipRequest ( int transportIndex){
            Transport transport = getTransport(transportIndex);
            if (transport.getRequestIds().isEmpty())
                return;
            int requestId = transport.getRequestIds().getFirst();
            transport.UpdateSkipSupplier(requestService.getRequestContactName(requestId));
            transport.removeRequest(requestId);
        }

//    public void manualRemoveItems(Transport transport, Map<Product, Integer> itemsToRemove) {
//        if (itemsToRemove == null || itemsToRemove.isEmpty()) return;
//
//        try {
//            transport.removeItems(itemsToRemove);
//            Supplier currentSupplier = transport.getFirstSupplierId();
//            Map<Product, Integer> pendingAllocation = transport.getSupplierAllocations().get(currentSupplier);
//
//            if (pendingAllocation != null) {
//                for (Map.Entry<Product, Integer> entry : itemsToRemove.entrySet()) {
//                    Product product = entry.getKey();
//                    int qtyToRemove = entry.getValue();
//
//                    if (pendingAllocation.containsKey(product)) {
//                        int updatedQty = pendingAllocation.get(product) - qtyToRemove;
//                        if (updatedQty <= 0) {
//                            pendingAllocation.remove(product);
//                        } else {
//                            pendingAllocation.put(product, updatedQty);
//                        }
//                    }
//                    currentSupplier.addStock(product, qtyToRemove);
//                }
//            }
//        } catch (ProductNotFoundOnTruckException e) {
//            System.out.println("Fine-tune failed: " + e.getMessage());
//        }
//    }

        public void performEmergencyDropOff (int transportIndex){
            Transport transport = getTransport(transportIndex);
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

        public void replaceTruck(int transportId, int newTruckId) {
            Transport transport = getTransport(transportId);
            truckService.replaceTrucks(transport.getTruckId(), newTruckId);
            transport.replaceTruck(newTruckId, truckService.getTruckWeight(newTruckId),
                    truckService.getTruckDisplay(newTruckId));
        }
//
//    public int getTruckWeightByTransportId(int transportIndex) {
//        return getTransportById(transportIndex).getTruck().getCurrentWeight();
//    }
//
//    public int getDriverLicense(int transportIndex) {
//        return workers_service.getLicense(getTransportById(transportIndex).getDriverId());
//    }

        public int getTruckId(int transportId){
            for (Transport transport : transports)
                if (transport.getId() == transportId)
                    return transport.getTruckId();

            throw new IllegalArgumentException("Transport not found: " + transportId);
        }

        public void startShipment(int transportId) {
            truckService.emptyTruck(getTruckId(transportId));
        }

    public int getDriverId(int transportId) {
        for (Transport transport : transports)
            if (transport.getId() == transportId)
                return transport.getDriverId();

        throw new IllegalArgumentException("Transport not found: " + transportId);
    }

    public String getFirstSupplierName(int transportId) {
        for (Transport transport : transports)
            if (transport.getId() == transportId)
                return supplierService.getSupplierName(transport.getFirstSupplierId());

        throw new IllegalArgumentException("Transport not found: " + transportId);
    }

    public String getTransportFileDisplay(int transportId) {
        for (Transport transport : transports)
            if (transport.getId() == transportId)
                return transport.toString();

        throw new IllegalArgumentException("Transport not found: " + transportId);
    }
}