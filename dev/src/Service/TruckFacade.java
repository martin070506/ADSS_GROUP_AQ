package Service;

import Domain.Driver;
import Domain.Truck;

import java.util.*;

public class TruckFacade {
    private List<Truck> trucks;
    private List<Truck> availableTrucks;
    private List<Driver> drivers;
    private List<Driver> availableDrivers;

    public TruckFacade(List<Truck> trucks, List<Driver> drivers) {
        this.trucks = trucks;
        this.drivers = drivers;
        this.availableTrucks = new ArrayList<>(this.trucks);
        this.availableDrivers = new ArrayList<>(this.drivers);
    }



    public List<Truck> getTrucks(){
        return trucks;
    }
    public void addTruck(Truck truck){
        trucks.add(truck);
        availableTrucks.add(truck);
    }
    public void removeTruck(Truck truck){
        trucks.remove(truck);
        availableTrucks.remove(truck);
    }
    public void takeTruck(Truck truck){
        availableTrucks.remove(truck);
    }
    public void takeDriver(Driver driver){
        availableDrivers.remove(driver);
    }
    public void addDriver(Driver driver){
        drivers.add(driver);
        availableDrivers.add(driver);
    }
    public void removeDriver(Driver driver){
        drivers.remove(driver);
        availableDrivers.remove(driver);
    }

    public List<Truck> getAvailableTrucks() {
        return availableTrucks;
    }

    public List<Driver> getAvailableDrivers() {
        return availableDrivers;
    }
}
