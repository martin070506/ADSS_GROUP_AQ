package Service.Transportation;

import DAO.LocationDAO;
import Domain.Transportation.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();
    private int counter = 0;
    private final LocationDAO locationDAO;

    public LocationService(LocationDAO locationDAO) {
        try {
            this.counter = locationDAO.getHighestLocationID()+1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.locationDAO=locationDAO;
    }
    public  void loadLocationsFromDB() {
        try {
            locations.addAll(locationDAO.loadAllLocations());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addLocation(String addr, String phone, String contact) {
        Location location = new Location(counter++,addr, phone, contact);
        locations.add(location);
        try {
            locationDAO.addLocation(location);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return location.id();
    }

    public int addBranchLocation(String addr, String phone, String contact) {
        int newId = 0;
        try {
            newId = locationDAO.getHighestLocationID() + 1;
            Location location = new Location(newId, addr, phone, contact);
            locations.add(location);
            locationDAO.addBranch(location);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public int addSupplierLocation(String addr, String phone, String contact) {
        int newId = 0;
        try {
            newId = locationDAO.getHighestLocationID() + 1;
            Location location = new Location(newId, addr, phone, contact);
            locations.add(location);
            locationDAO.addSupplier(location);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public List<Integer> getLocationIds() {
        List<Integer> locationIds = new ArrayList<>();
        for (Location location : locations)
            locationIds.add(location.id());

        return locationIds;
    }


    public String getLocationDisplay(int locationId) {
        for (Location location : locations)
            if (location.id() == locationId)
                return location.toString();

        throw new IllegalArgumentException("Location not found: " + locationId);
    }

    public Location getLocation(int locationId) {
        for (Location location : locations)
            if (location.id() == locationId)
                return location;

        throw new IllegalArgumentException("Location not found: " + locationId);
    }


    public List<Location> loadAllBranches() {
        try {
            return locationDAO.loadAllBranches();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLocation(int branchId) {
        locations.removeIf(location -> location.id() == branchId);
        try {
            locationDAO.removeLocation(branchId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Location> loadAllSuppliers() {
        try {
            return locationDAO.loadAllSuppliers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}