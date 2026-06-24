package Service.Transportation;

import DAO.LocationDAO;
import DTO.LocationDTO;
import Domain.Transportation.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();
    private int counter = 0;
    private final LocationDAO locationDAO;

    public LocationService(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
        try {
            this.counter = locationDAO.getHighestLocationID() + 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadLocationsFromDB();
    }

    public void loadLocationsFromDB() {
        try {
            locations.clear(); // מונע כפילויות בטעינה חוזרת
            List<LocationDTO> dtos = locationDAO.loadAllLocations();
            for (LocationDTO dto : dtos) {
                locations.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        try {
            LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
            locationDAO.addLocation(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public int addBranchLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        try {
            LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
            locationDAO.addBranch(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public int addSupplierLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        try {
            // המרה ל-DTO ושליחה ל-DAO
            LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
            locationDAO.addSupplier(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public List<Integer> getLocationIds() {
        List<Integer> locationIds = new ArrayList<>();
        for (Location location : locations) {
            locationIds.add(location.id());
        }
        return locationIds;
    }

    public String getLocationDisplay(int locationId) {
        for (Location location : locations) {
            if (location.id() == locationId) {
                return location.toString();
            }
        }
        throw new IllegalArgumentException("Location not found: " + locationId);
    }

    public Location getLocation(int locationId) {
        for (Location location : locations) {
            if (location.id() == locationId) {
                return location;
            }
        }
        throw new IllegalArgumentException("Location not found: " + locationId);
    }

    public List<Location> loadAllBranches() {
        try {
            List<LocationDTO> dtos = locationDAO.loadAllBranches();
            List<Location> branches = new ArrayList<>();
            for (LocationDTO dto : dtos) {
                branches.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));
            }
            return branches;
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
            List<LocationDTO> dtos = locationDAO.loadAllSuppliers();
            List<Location> suppliers = new ArrayList<>();
            for (LocationDTO dto : dtos) {
                suppliers.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));
            }
            return suppliers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}