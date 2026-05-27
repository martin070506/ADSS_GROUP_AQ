package Domain;

public class Driver {
    private final String driverName;
    private final int license;
    private boolean isAvailable;

    public Driver(String driverName, int license) {
        this.driverName = driverName;
        this.license = license;
        isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available){
        isAvailable = true;
    }

    public int getLicense() {
        return license;
    }

    public String getDriverName() {
        return driverName;
    }

    @Override
    public String toString() {
        return String.format("Driver: %s (License: %d)", driverName, license);
    }
}