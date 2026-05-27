package Service;

import Domain.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverService {

    private final List<Driver> drivers;

    public DriverService() {
        this.drivers = new ArrayList<>();
    }

    public DriverService(List<Driver> drivers) {
        this.drivers = new ArrayList<>(drivers);
    }

    public void addDriver(Driver driver) {
        if (driver == null) return;
        drivers.add(driver);
    }

    public void removeDriver(Driver driver) {
        if (driver == null) return;
        drivers.remove(driver);
    }

    public void returnDriver(Driver driver) {
        if (driver != null && drivers.contains(driver))
            driver.setAvailable(true);
    }

    private List<Driver> getAvailableDrivers() {
        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver driver : drivers)
            if (driver.isAvailable())
                availableDrivers.add(driver);

        return availableDrivers;
    }

    public Driver getAvailableDriver(String driverName) {
        for (Driver driver : drivers)
            if (driver.toString().equals(driverName) && driver.isAvailable())
                return driver;
        return null;
    }

    public List<String> getAvailableDriversDisplay() {
        List<String> availableDriversDisplay = new ArrayList<>();
        for (Driver driver : getAvailableDrivers())
            availableDriversDisplay.add(driver.toString());
        return availableDriversDisplay;
    }
}