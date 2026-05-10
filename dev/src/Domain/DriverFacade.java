package Domain;

import java.util.List;

public class DriverFacade {

    private List<Driver> drivers;
    private List<Driver> availableDrivers;

    public DriverFacade(List<Driver> drivers) {
        this.drivers = drivers;
        this.availableDrivers = drivers;
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
