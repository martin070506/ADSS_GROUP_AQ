package Service;

import Domain.*;
import Exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportManagerService {

    private final List<Transport> transports;
    private int transportIdCounter;

    public TransportManagerService() {
        this.transports = new ArrayList<>();
        this.transportIdCounter = 1;
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

    public Transport getTransportById (int id) {
        boolean found=false;
        for (Transport t : transports) {
            System.out.println(t.getId());
            if (t.getId()==id){
                found=true;
                return t;
            }
        }
        if(!found){
            throw new DomainException("Transport not found");
        }
        return null;
    }

    public void removeTransport(Transport transport) {
        if (transport != null) {
            transports.remove(transport);
        }
    }



    public void skipSupplier(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        Supplier supplier = transport.getFirstSupplier();
        transport.getTransportFile().skipSupplier(supplier);
        Map<Product, Integer> thingsToRemove = transport.getSupplierAllocations().get(supplier);

        try {
            transport.removeItems(thingsToRemove);
        } catch (ProductNotFoundOnTruckException e) {
            System.out.println(e.getMessage());
        }

        transport.removeSupplier(supplier);
    }

    public void logOverweightAlert(Transport transport) {
        transport.getTransportFile().overWeightAlert(transport.getTruck().getCurrentWeight());
    }

    public void leaveSupplier(Transport transport, Supplier supplier) {
        transport.getTransportFile().leaveSupplier(supplier, transport.getTruck().getCurrentWeight());
        transport.getSupplierAllocations().remove(supplier);
    }

    public void skipRequest(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        Request request = transport.getRequests().getFirst();
        transport.getTransportFile().skipRequest(request);
        transport.removeRequest(request);
    }

    public void manualRemoveItems(Transport transport, Map<Product, Integer> itemsToRemove) {
        if (itemsToRemove == null || itemsToRemove.isEmpty()) return;

        try {
            // 1. Deduct the items from the physical truck weight
            transport.removeItems(itemsToRemove);

            // 2. Identify who the current supplier is
            Supplier currentSupplier = transport.getFirstSupplier();
            Map<Product, Integer> pendingAllocation = transport.getSupplierAllocations().get(currentSupplier);

            if (pendingAllocation != null) {
                for (Map.Entry<Product, Integer> entry : itemsToRemove.entrySet()) {
                    Product product = entry.getKey();
                    int qtyToRemove = entry.getValue();

                    // 3. Deduct the fine-tuned amount from the upcoming transport plan
                    if (pendingAllocation.containsKey(product)) {
                        int updatedQty = pendingAllocation.get(product) - qtyToRemove;
                        if (updatedQty <= 0) {
                            pendingAllocation.remove(product);
                        } else {
                            pendingAllocation.put(product, updatedQty);
                        }
                    }

                    // 4. Return the items to the supplier's inventory stock
                    currentSupplier.addStock(product, qtyToRemove);
                }
            }

        } catch (ProductNotFoundOnTruckException e) {
            System.out.println("Fine-tune failed: " + e.getMessage());
        }
    }

    public void performEmergencyDropOff(int transportIndex) {
        Transport transport = getTransportById(transportIndex);
        Request request = transport.getRequests().getFirst();
        if (request == null)
            throw new NoDestinationForEmergencyDropOffException();
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

    public void processTransport(Transport transport) {

    }

    public int gotIndexOf(Transport transport) {
        return transports.indexOf(transport);
    }

    public int getTruckWeightByTransportId(int transportIndex) {
        return getTransportById(transportIndex).getTruck().getCurrentWeight();
    }

    public int getDriverLicense(int transportIndex) {
        return getTransportById(transportIndex).getDriver().getLicense();
    }
}