package Service.Transportation;

import DAO.LocationDAO;
import DTO.LocationDTO;
import Domain.Transportation.BranchManager;
import Domain.Transportation.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchService {
    private final List<BranchManager> branches;
    private final LocationService locationService;
    private final LocationDAO locationDAO;


    public BranchService(LocationService locationService, LocationDAO locationDAO) {
        this.locationService = locationService;
        this.branches = new ArrayList<>();
        this.locationDAO = locationDAO;
    }
    public void loadBranchesFromDB() throws SQLException {
        branches.addAll(locationDAO.loadAllBranches(locationService));
    }

    public List<BranchManager> getBranches() {
        return branches;
    }

    public void addBranch(String addr, String phone, String contact) throws SQLException {

        int locationId = locationService.addLocation(addr, phone, contact);
        LocationDTO lDTO=new LocationDTO(locationId,contact,addr,phone,true,false);
        branches.add(new BranchManager(locationService.getLocation(locationId)));
        locationDAO.addLocation(lDTO);
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