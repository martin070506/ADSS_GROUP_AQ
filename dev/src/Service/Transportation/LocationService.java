package Service.Transportation;

import DAO.Transportation.LocationDAO;
import DTO.Transportation.LocationDTO;
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
        this.counter = locationDAO.getHighestLocationID() + 1;
    }

    public void loadLocationsFromDB() {
        locations.clear();
        List<LocationDTO> dtos = locationDAO.loadAllLocations();
        for (LocationDTO dto : dtos)
            locations.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));
    }

    public int addLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
        locationDAO.addLocation(dto);
        return newId;
    }

    public int addBranchLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
        locationDAO.addBranch(dto);
        return newId;
    }

    public int addSupplierLocation(String addr, String phone, String contact) {
        int newId = counter++;
        Location location = new Location(newId, addr, phone, contact);
        locations.add(location);

        LocationDTO dto = new LocationDTO(newId, contact, addr, phone, null);
        locationDAO.addSupplier(dto);
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
        List<LocationDTO> dtos = locationDAO.loadAllBranches();
        List<Location> branches = new ArrayList<>();
        for (LocationDTO dto : dtos)
            branches.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));

        return branches;
    }

    public void removeLocation(int branchId) {
        locations.removeIf(location -> location.id() == branchId);
        locationDAO.removeLocation(branchId);
    }

    public List<Location> loadAllSuppliers() {
        List<LocationDTO> dtos = locationDAO.loadAllSuppliers();
        List<Location> suppliers = new ArrayList<>();
        for (LocationDTO dto : dtos) {
            suppliers.add(new Location(dto.locationId(), dto.address(), dto.phoneNumber(), dto.contactName()));
        }
        return suppliers;
    }
}