package Domain.Transportation;

import DTO.TransportFileDTO;

import java.time.LocalDate;
import java.util.*;

public class Transport {

    private final int id;
    private final LocalDate departureTime;
    private int truckId;
    private final int driverId;
    private final int sourceId;
    private final Map<Integer, Map<Integer, Integer>> supplierAllocationIds;
    private final TransportFile transportFile;

    public Transport(int id, LocalDate departureTime, int truckId, int driverId, int sourceId,
                     Map<Integer, Map<Integer, Integer>> supplierAllocationIds,
                     String truckInfo, String driverInfo, String sourceInfo) {
        this.id = id;
        this.departureTime = departureTime;
        this.truckId = truckId;
        this.driverId = driverId;
        this.sourceId = sourceId;
        this.supplierAllocationIds = new HashMap<>(supplierAllocationIds);
        this.transportFile = new TransportFile(departureTime, truckInfo, driverInfo, sourceInfo);
    }


    public int getDriverId() {
        return driverId;
    }

    public Map<Integer, Map<Integer, Integer>> getSupplierAllocationIds() {
        return supplierAllocationIds;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDepartureTime() {
        return departureTime;
    }

    public int getTruckId() {
        return truckId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void replaceTruck(int newTruckId, int truckCapacity, String truckInfo) {
        this.truckId = newTruckId;
        this.transportFile.changeTruck(truckCapacity, truckInfo);
    }

    public void removeSupplier(int supplierId) {
        supplierAllocationIds.remove(supplierId);
    }

    public int getFirstSupplierId() {
        return supplierAllocationIds.keySet().iterator().next();
    }

    public void UpdateArriveAtSupplier(String supplierName) {
        transportFile.arriveAtSupplier(supplierName);
    }

    public void UpdateLeaveSupplier(String supplierName, String supplierInfo, int truckWeight) {
        transportFile.leaveSupplier(supplierName, supplierInfo, truckWeight);
    }

    public void UpdateArriveAtRequest(String requestName) {
        transportFile.arriveAtRequest(requestName);
    }

    public void UpdateLeaveRequest(String requestContactName, String requestInfo) {
        transportFile.leaveRequest(requestContactName, requestInfo);
    }

    public void UpdateSkipSupplier(String supplierName, String reason) {
        transportFile.skipSupplier(supplierName, reason);
    }

    public void removeItems(Map<Integer, Integer> thingsToRemove) {
        for (Map.Entry<Integer, Integer> entry : thingsToRemove.entrySet()) {
            int productId = entry.getKey();
            int amount = entry.getValue();
            supplierAllocationIds.get(productId).put(productId, supplierAllocationIds.get(productId).get(productId) - amount);
        }
    }

    public TransportFile getTransportFile() {
        return transportFile;
    }

    public void UpdateSkipRequest(String requestContactName, String reason) {
        transportFile.skipRequest(requestContactName, reason);
    }
}