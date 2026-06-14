package Domain.Transportation;

public class BranchManager {
    private final Location storeLocation;

    public BranchManager(Location storeLocation) {
        this.storeLocation = storeLocation;
    }



    public Location getLocation(){
        return storeLocation;
    }

    @Override
    public String toString() {
        return storeLocation.toString();
    }
}
