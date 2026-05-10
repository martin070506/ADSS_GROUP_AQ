package Service;

import Domain.Driver;
import Domain.DriverFacade;

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

}
