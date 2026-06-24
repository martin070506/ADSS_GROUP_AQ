package Service.Transportation;

import Domain.Transportation.BranchManager;
import Domain.Transportation.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchService {
    private final List<BranchManager> branches;
    private final LocationService locationService;

    public BranchService(LocationService locationService) {
        this.locationService = locationService;
        this.branches = new ArrayList<>();
        loadBranchesFromDB();
    }

    public void loadBranchesFromDB() {
        branches.clear();
        List<Location> branchLocations = locationService.loadAllBranches();
        for (Location loc : branchLocations) {
            branches.add(new BranchManager(loc));
        }
    }

    public List<BranchManager> getBranches() {
        return branches;
    }

    public void addBranch(String addr, String phone, String contact) {
        int locationId = locationService.addBranchLocation(addr, phone, contact);
        Location newLocation = locationService.getLocation(locationId);
        branches.add(new BranchManager(newLocation));
    }

    public void removeBranch(int branchId) {
        locationService.removeLocation(branchId);
        branches.removeIf(branch -> branch.getLocation().id() == branchId);
    }

    public List<Integer> getBranchesId() {
        List<Integer> branchesId = new ArrayList<>();
        for (BranchManager branch : branches)
            branchesId.add(branch.getLocation().id());

        return branchesId;
    }

    public String getBranchDisplay(int branchId) { // שונה ל-int
        for (BranchManager branch : branches) {
            if (branch.getLocation().id() == branchId) {
                return branch.toString();
            }
        }
        throw new IllegalArgumentException("Branch ID not found: " + branchId);
    }
}