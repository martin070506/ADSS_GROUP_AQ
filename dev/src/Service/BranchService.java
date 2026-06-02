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
    public BranchManager getBranchByIndex(int index) {
        if (index >= 0 && index < branches.size()) {
            return branches.get(index);
        }
        throw new IllegalArgumentException("Branch index out of bounds: " + index);
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