package Domain;

public class Driver {

    private String driverName;
    private int license;

    public Driver(String driverName, int license) {
        this.driverName = driverName;
        this.license = license;
    }

    public String getDriverName() {
        return driverName;
    }
    public int getLicense() {
        return license;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public void setLicense(int license) { this.license = license; }
}
