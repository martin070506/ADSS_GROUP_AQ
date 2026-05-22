package Service;

import Domain.Driver;
import Domain.Truck;
import Domain.TruckFacade;
import java.util.List;

public class TruckService {
    private TruckFacade truckFacade;

    public TruckService(TruckFacade TruckFacade) {
        this.truckFacade = TruckFacade;
    }

    public List<Truck> getAvailableTrucks() {
        return truckFacade.getAvailableTrucks();
    }

    public Truck reserveTruck(int index) {
        Truck truck = truckFacade.getAvailableTrucks().get(index);
        truckFacade.takeTruck(truck);
        return truck;

    }
    public void addTruck(Truck truck) {
        truckFacade.addTruck(truck);
    }
    public void removeTruck(Truck truck) {
        truckFacade.removeTruck(truck);
    }
    public boolean canTruckTakeDriver(Truck truck, Driver driver) {
        return driver.license()>=truck.getMinLicense();
    }
}