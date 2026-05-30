package Service;

import Domain.Driver;
import java.util.ArrayList;
import java.util.List;

public class DriverService {
    private final List<Driver> drivers;

    public DriverService() { this.drivers = new ArrayList<>(); }
    public DriverService(List<Driver> drivers) { this.drivers = new ArrayList<>(drivers); }

    public void addDriver(String driverName,int license) {
        if(driverName == null || driverName.isEmpty()) throw new IllegalArgumentException("Driver name cannot be empty");
        if(license < 0) throw new IllegalArgumentException("License must be greater than 0");
        drivers.add(new Driver(driverName,license));
    }

    // FIXED: Direct lookup using choice indices mapping
    public Driver getAvailableDriverByIndex(int index) {
        List<Driver> available = getAvailableDrivers();
        if (index >= 0 && index < available.size()) {
            return available.get(index);
        }
        throw new IllegalArgumentException("Driver choice out of bounds.");
    }

    private List<Driver> getAvailableDrivers() {
        List<Driver> available = new ArrayList<>();
        for (Driver d : drivers) if (d.isAvailable()) available.add(d);
        return available;
    }

    public List<String> getAvailableDriversDisplay() {
        List<String> display = new ArrayList<>();
        for (Driver d : getAvailableDrivers()) display.add(d.toString());
        return display;
    }
}