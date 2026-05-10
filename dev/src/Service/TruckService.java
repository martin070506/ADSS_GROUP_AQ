package Service;

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
}