package Domain;

import java.util.List;

public class BranchManager {
    private Location storeLocation;



    public BranchManager(Location storeLocation) {
        this.storeLocation = storeLocation;

    }

    public void requestShipment(List<ProductPair> neededItems){
        CompanyManager.getInstance().addDestination(storeLocation, neededItems);
    }

    public Location getLocation(){
        return storeLocation;
    }
}
