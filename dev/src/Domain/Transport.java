package Domain;

import java.time.LocalDate;
import java.util.*;
import Exceptions.ProductNotFoundOnTruckException;

public class Transport {

    private final int id;
    private final LocalDate departureTime;
    private Truck truck;
    private Driver driver;
    private final Location source;
    private final List<Request> requests;
    private final Map<Supplier, Map<Product, Integer>> supplierAllocations;
    private final TransportFile transportFile;

    public Transport(int id, LocalDate departureTime, Truck truck, Driver driver, Location source,
                     List<Request> requests,
                     Map<Supplier, Map<Product, Integer>> supplierAllocations) {
        this.id = id;
        this.departureTime = departureTime;
        this.truck = truck;
        this.driver = driver;
        this.source = source;
        this.requests = requests;
        this.supplierAllocations = new HashMap<>(supplierAllocations);
        this.transportFile = new TransportFile(departureTime, truck, driver, source);
    }



    public int getId() {
        return id;
    }

    public LocalDate getDepartureTime() {
        return departureTime;
    }

    public Truck getTruck() {
        return truck;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getSource() {
        return source;
    }

    public Map<Supplier, Map<Product, Integer>> getSupplierAllocations() {
        return supplierAllocations;
    }

    public TransportFile getTransportFile() {
        return transportFile;
    }

    public void replaceTruck(Truck newTruck) {
        this.truck = newTruck;
        this.transportFile.changeTruck(newTruck);
    }

    public void removeSupplier(Supplier supplier) {
        this.supplierAllocations.remove(supplier);
    }

    public void removeRequest(Request request) {
        this.requests.remove(request);
    }

    public void removeItems(Map<Product, Integer> outgoingItems) throws ProductNotFoundOnTruckException {
        if (outgoingItems == null || outgoingItems.isEmpty()) {
            return;
        }

        truck.removeProducts(outgoingItems);
    }

    public Supplier getFirstSupplier() {
        return supplierAllocations.keySet().iterator().next();
    }

    public List<Request> getRequests() {
        return requests;
    }
}