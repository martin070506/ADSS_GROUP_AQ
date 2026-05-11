package Service;

import Domain.TransportManager;
import Domain.Transport;
import Domain.Truck;
import Domain.Driver;
import Domain.Location;
import Domain.Destination;
import Domain.ProductPair;
import Domain.Supplier;
import java.util.List;
import java.util.Map;

public class ShipmentService {
    private TransportManager transportManager;

    public ShipmentService(TransportManager transportManager) {
        this.transportManager = transportManager;
    }

    public Transport executeCreateTransport(Truck truck, Driver driver, Location source,
                                            List<Destination> destinations, List<Truck> replacementTrucks,
                                            Map<Supplier, List<ProductPair>> supplierAllocations) {

        return transportManager.createTransport(truck, driver, source, destinations,
                replacementTrucks, supplierAllocations);
    }

    public void finalizeShipment(Truck truck, Driver driver) {
        transportManager.finishShipment(truck, driver);
    }

    public void addSupplier(Supplier supplier) {
        transportManager.addSupplier(supplier);
    }
}