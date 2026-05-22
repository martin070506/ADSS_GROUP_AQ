package Domain;

import java.util.ArrayList;
import java.util.List;

public class DriverFacade {

    private List<Driver> drivers;
    private List<Driver> availableDrivers;

    public DriverFacade(List<Driver> drivers) {
        this.drivers = drivers;
        this.availableDrivers = drivers;
    }
    public DriverFacade() {
        this.drivers = new ArrayList<>();
        this.availableDrivers = new ArrayList<>();
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

    public List<Driver> getAvailableDrivers() {
        return availableDrivers;
    }
}
