package Service;

import Domain.BranchManager;
import Domain.Location;
import java.util.ArrayList;
import java.util.List;

public class BranchService {
    private final List<BranchManager> branches;

    public BranchService(List<BranchManager> branches) {
        this.branches = branches;
    }
    public BranchService() {
        this.branches = new ArrayList<>();
    }

    public List<BranchManager> getBranches() {
        return branches;
    }
    public void addBranch(BranchManager branch) {
        branches.add(branch);
    }

    public void addBranch(Location l) {
        addBranch(new BranchManager(l));
    }


    // FIXED: Lookup by integer position instead of string matching
    public BranchManager getBranchById(int id) {
        for  (BranchManager branch : branches) {
            if(branch.getLocation().id() == id) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Branch index out of bounds: " + id);
    }

    public void removeBranch(BranchManager branch) {
        branches.remove(branch);
    }

    public List<String> getBranchesDisplay() {
        List<String> branchesDisplay = new ArrayList<>();
        for (BranchManager branch : branches) {
            branchesDisplay.add(branch.toString());
        }
        return branchesDisplay;
    }
}