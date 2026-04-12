package Service;

import Domain.Location;
import Domain.ProductPair;

import java.util.List;

public class BranchManager {
    private Location storeLocation;
    private final CompanyManager companyManager;


    public BranchManager(Location storeLocation) {
        this.storeLocation = storeLocation;
        companyManager = CompanyManager.getInstance();
    }

    public void requestShipment(List<ProductPair> neededItems){
        companyManager.addDestination(storeLocation, neededItems);
    }
}
