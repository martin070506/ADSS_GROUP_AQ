public class Driver {

    private int driverName;
    private int license;

    public Driver(int driverName, int license) {
        this.driverName = driverName;
        this.license = license;
    }

    public int getDriverName() {
        return driverName;
    }
    public int getLicense() {
        return license;
    }
    public void setDriverName(int driverName) {
        this.driverName = driverName;
    }
    public void setLicense(int license) {
        this.license = license;
    }

}
