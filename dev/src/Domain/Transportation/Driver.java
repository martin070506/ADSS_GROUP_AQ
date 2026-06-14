package Domain.Transportation;

public class Driver {
    private final String driverName;
    private final int license;
    private boolean isAvailable;
    private int id;

    public Driver(int id,String driverName, int license) {
        this.driverName = driverName;
        this.license = license;
        this.id=id;
        isAvailable = true;
    }
    public int getId() {
        return id;
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
        return String.format("ID: %d Driver: %s (License: %d)", id,driverName, license);
    }
}