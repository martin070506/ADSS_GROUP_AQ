package Service.Transportation;

import Domain.Transportation.*;
import Exceptions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportManagerService {

    private final List<Transport> transports;
    private int transportIdCounter;
    private TruckService truckService;

    public TransportManagerService() {
        this.transports = new ArrayList<>();
        this.transportIdCounter = 1;
    }

    public void setTruckService(TruckService truckService) {
        this.truckService = truckService;
    }

    public int createTransport(Truck truck, Driver driver, Location source,
                               List<Request> requests,
                               Map<Supplier, Map<Product, Integer>> supplierAllocations) {
        Transport transport = new Transport(
                transportIdCounter++,
                java.time.LocalDate.now(),
                truck,
                driver,
                source,
                requests,
                supplierAllocations
        );
        transports.add(transport);
        return transport.getId();
    }

    public Transport getTransportById(int id) {
        for (Transport transport : transports)
            if (transport.getId() == id)
                return transport;

        throw new DomainException("Transport not found");
    }

    public void removeTransportById(int transportById) {
        Transport transport = getTransportById(transportById);
        transports.remove(transport);
    }

    // === NEW CLEAN INITIALIZATION STEP ===
    public void prepareTruckForDeparture(int transportId) {
        Transport transport = getTransportById(transportId);
        transport.getTruck().emptyTruck(); // Clears it safely BEFORE the lifecycle retries ever start
    }

    // === FINISH CLEANUP STEP ===
    public void finishShipment(int transportId) {
        Transport transport = getTransportById(transportId);
        transport.getTruck().emptyTruck(); // Guarantees a fresh truck at the end
    }

    // === LIVE STATE ENGINE OPERATIONS ===
    public void processTransportLifecycle(int transportId) {
        Transport transport = getTransportById(transportId);

        // 1. Process Suppliers Ingestion Loop
        while (!transport.getSupplierAllocations().isEmpty()) {
            Supplier currentSupplier = transport.getFirstSupplier();
            Map<Product, Integer> itemsToLoad = transport.getSupplierAllocations().get(currentSupplier);

            // Keep the supplier in the map until it successfully loads without throwing an OverweightException
            currentSupplier.handleShipment(itemsToLoad, transport.getTruck());

            // Finalize state records only on successful ingestion completion
            transport.getTransportFile().arriveAtSupplier(currentSupplier);
            transport.getTransportFile().leaveSupplier(currentSupplier, transport.getTruck().getCurrentWeight());
            transport.getSupplierAllocations().remove(currentSupplier);
        }

        // 2. Process Branch Delivery Drops Loop
        while (!transport.getRequests().isEmpty()) {
            Request currentRequest = transport.getRequests().getFirst();
            try {
                transport.getTransportFile().arriveAtRequest(currentRequest);
                currentRequest.handleShipment(transport.getTruck());
                transport.getTransportFile().leaveRequest(currentRequest);
                transport.removeRequest(currentRequest);
            } catch (Exceptions.ProductNotFoundOnTruckException itse) {
                System.out.println("Skipped Destination: " + itse.getMessage());
                skipRequest(transportId);
            }
        }
    }

    public void handleStockException(int transportId, DomainException ise) {
        if (ise instanceof InsufficientSupplierStockException) {
            skipSupplier(transportId);
        } else if (ise instanceof InsufficientTruckStockException) {
            skipRequest(transportId);
        }
    }

    public void resolveOverweightIssue(int transportId, String choice) {
        switch (choice) {
            case "2" -> {
                performEmergencyDropOff(transportId);
                finalizeCurrentSupplierLoading(transportId);
            }
            case "4" -> {
                int maxWeight = getTruckWeightByTransportId(transportId);
                int currentDriverLicense = getDriverLicense(transportId);

                for (int i = 0; i < truckService.getAvailableTrucksDisplay().size(); i++) {
                    Truck newTruck = truckService.getAvailableTruckById(i);

                    if (newTruck.getMaxWeight() > maxWeight && newTruck.getMinLicense() <= currentDriverLicense) {
                        replaceTruck(transportId, newTruck);
                        System.out.println("Dynamic truck replacement execution complete.");
                        finalizeCurrentSupplierLoading(transportId);
                        return;
                    }
                }
                System.out.println("Mitigation failed: No alternative vehicle matches criteria. Skipping supplier.");
                skipSupplier(transportId);
            }
            default -> skipSupplier(transportId);
        }
    }

    public void resolveOverweightWithFineTuning(int transportId, int UIProductIndex, int amountToRemove) {
        Transport transport = getTransportById(transportId);
        List<Product> loadedProducts = new ArrayList<>(transport.getTruck().getLoadedProducts().keySet());

        if (UIProductIndex >= 0 && UIProductIndex < loadedProducts.size()) {
            Product targetProduct = loadedProducts.get(UIProductIndex);

            Map<Product, Integer> itemsToRemove = new HashMap<>();
            itemsToRemove.put(targetProduct, amountToRemove);

            manualRemoveItems(transport, itemsToRemove);
        }
    }

    public void finalizeCurrentSupplierWithFineTune(int transportId) {
        finalizeCurrentSupplierLoading(transportId);
    }

    private void finalizeCurrentSupplierLoading(int transportId) {
        Transport transport = getTransportById(transportId);
        if (!transport.getSupplierAllocations().isEmpty()) {
            Supplier supplier = transport.getFirstSupplier();
            transport.getTransportFile().arriveAtSupplier(supplier);
            transport.getTransportFile().leaveSupplier(supplier, transport.getTruck().getCurrentWeight());
            transport.getSupplierAllocations().remove(supplier);
        }
    }

    // === CORE LOGISTICS MUTATORS ===
    public void skipSupplier(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        if (transport.getSupplierAllocations().isEmpty()) return;

        Supplier supplier = transport.getFirstSupplier();
        transport.getTransportFile().skipSupplier(supplier);
        Map<Product, Integer> thingsToRemove = transport.getSupplierAllocations().get(supplier);

        try {
            transport.removeItems(thingsToRemove);
            for (Map.Entry<Product, Integer> entry : thingsToRemove.entrySet()) {
                supplier.addStock(entry.getKey(), entry.getValue());
            }
        } catch (ProductNotFoundOnTruckException e) {
            System.out.println(e.getMessage());
        }
        transport.getSupplierAllocations().remove(supplier);
    }

    public void skipRequest(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        if (transport.getRequests().isEmpty()) return;
        Request request = transport.getRequests().getFirst();
        transport.getTransportFile().skipRequest(request);
        transport.removeRequest(request);
    }

    public void manualRemoveItems(Transport transport, Map<Product, Integer> itemsToRemove) {
        if (itemsToRemove == null || itemsToRemove.isEmpty()) return;

        try {
            transport.removeItems(itemsToRemove);
            Supplier currentSupplier = transport.getFirstSupplier();
            Map<Product, Integer> pendingAllocation = transport.getSupplierAllocations().get(currentSupplier);

            if (pendingAllocation != null) {
                for (Map.Entry<Product, Integer> entry : itemsToRemove.entrySet()) {
                    Product product = entry.getKey();
                    int qtyToRemove = entry.getValue();

                    if (pendingAllocation.containsKey(product)) {
                        int updatedQty = pendingAllocation.get(product) - qtyToRemove;
                        if (updatedQty <= 0) {
                            pendingAllocation.remove(product);
                        } else {
                            pendingAllocation.put(product, updatedQty);
                        }
                    }
                    currentSupplier.addStock(product, qtyToRemove);
                }
            }
        } catch (ProductNotFoundOnTruckException e) {
            System.out.println("Fine-tune failed: " + e.getMessage());
        }
    }

    public void performEmergencyDropOff(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        if (transport.getRequests().isEmpty()) {
            throw new NoDestinationForEmergencyDropOffException();
        }
        Request request = transport.getRequests().getFirst();
        transport.getTransportFile().arriveAtRequest(request);
        request.handleShipment(transport.getTruck());
        transport.getTransportFile().leaveRequest(request);
        transport.removeRequest(request);
    }

    public void replaceTruck(int transportId, Truck newTruck) {
        Transport transport = getTransportById(transportId);
        transport.getTruck().transferHoldingsToOtherTruck(newTruck);
        transport.replaceTruck(newTruck);
    }

    public int getTruckWeightByTransportId(int transportIndex) {
        return getTransportById(transportIndex).getTruck().getCurrentWeight();
    }

    public int getDriverLicense(int transportIndex) {
        return getTransportById(transportIndex).getDriver().getLicense();
    }
}