package Service;

import Domain.Driver;
import Domain.Truck;

import java.util.ArrayList;
import java.util.List;

public class TruckService {
    private final List<Truck> trucks;

    public TruckService() {
        this.trucks = new ArrayList<>();
    }

    public TruckService(List<Truck> trucks) {
        this.trucks = new ArrayList<>(trucks);
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public List<String> getAvailableTrucksDisplay() {
        return getAvailableTrucksDisplay(Integer.MAX_VALUE);
    }

    public List<String> getAvailableTrucksDisplay(int minLicense) {
        List<String> availableTrucks = new ArrayList<>();
        for (Truck truck : trucks)
            if (truck.isAvailable() && truck.getMinLicense() <= minLicense)
                availableTrucks.add(truck.toString());

        return availableTrucks;
    }

    public Truck getAvailableTruck(String truckName) {
        for (Truck truck : trucks)
            if (truck.toString().equals(truckName) && truck.isAvailable())
                return truck;
        return null;
    }

    public void addTruck(Truck truck) {
        if (truck == null)
            return;
        trucks.add(truck);
    }

    public void removeTruck(Truck truck) {
        if (truck == null) return;
        trucks.remove(truck);
    }

    public boolean canTruckTakeDriver(Truck truck, Driver driver) {
        if (truck == null || driver == null) return false;
        return driver.getLicense() >= truck.getMinLicense();
    }
}