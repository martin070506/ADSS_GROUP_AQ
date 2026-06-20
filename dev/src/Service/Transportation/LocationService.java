package Service.Transportation;

import DAO.LocationDAO;
import Domain.Transportation.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();
    private int counter = 0;
    private LocationDAO locationDAO;

    public LocationService(LocationDAO locationDAO) throws SQLException {
        this.counter=locationDAO.getHighestLocationID()+1;
        this.locationDAO=locationDAO;
    }
    public  void loadLocationsFromDB() throws SQLException {
        for(Location l :locationDAO.loadAllLocations()){
            locations.add(l);
        }
    }
    public int addLocation(String addr, String phone, String contact) {
        Location location = new Location(counter++,addr, phone, contact);
        locations.add(location);
        return location.id();
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
}