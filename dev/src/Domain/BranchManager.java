package Domain;

import Service.CompanyManager;

import java.util.Map;

public class BranchManager {
    private final Location storeLocation;

    public BranchManager(Location storeLocation) {
        this.storeLocation = storeLocation;
    }

    public void requestShipment(Map<Product, Integer> neededItems){
        CompanyManager.getInstance().addRequest(storeLocation, neededItems);
    }

    public Location getLocation(){
        return storeLocation;
    }
}
