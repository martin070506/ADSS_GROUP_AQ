package Service;

import Domain.Driver;
import Domain.DriverFacade;

import java.util.List;

public class DriverService {

    private DriverFacade driverFacade;

    public DriverService(DriverFacade driverFacade) {
        this.driverFacade = driverFacade;
    }

    public Driver selectDriver(int index) {
        Driver driver = driverFacade.getAvailableDrivers().get(index);
        driverFacade.takeDriver(driver);
        return driver;
    }

    public void takeDriver(Driver driver) {
        driverFacade.takeDriver(driver);
    }
    public void addDriver(Driver driver) {
        driverFacade.addDriver(driver);
    }
    public void removeDriver(Driver driver) {
        driverFacade.removeDriver(driver);
    }
    public List<Driver> getAvailableDrivers() {
        return driverFacade.getAvailableDrivers();
    }

}
