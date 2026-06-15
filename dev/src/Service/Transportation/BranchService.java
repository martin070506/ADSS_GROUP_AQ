package Service.Transportation;

import Domain.Transportation.BranchManager;
import Domain.Transportation.Location;
import java.util.ArrayList;
import java.util.List;

public class BranchService {
    private final List<BranchManager> branches;
    private final LocationService locationService;


    public BranchService(LocationService locationService) {
        this.locationService = locationService;
        this.branches = new ArrayList<>();
    }

    public List<BranchManager> getBranches() {
        return branches;
    }

    public void addBranch(String addr, String phone, String contact) {
        int locationId = locationService.addLocation(addr, phone, contact);
        branches.add(new BranchManager(locationService.getLocation(locationId)));
    }

    public void removeBranch(BranchManager branch) {
        branches.remove(branch);
    }

    public List<Integer> getBranchesId() {
        List<Integer> branchesId = new ArrayList<>();
        for (BranchManager branch : branches)
            branchesId.add(branch.getLocation().id());

        return branchesId;
    }

    public String getBranchDisplay(Integer branchId) {
        for (BranchManager branch : branches)
            if (branch.getLocation().id() == branchId)
                return branch.toString();

        throw new IllegalArgumentException("Branch ID not found: " + branchId);
    }
}