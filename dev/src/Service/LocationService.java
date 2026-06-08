package Service;

import Domain.Location;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();
    private int counter=0;
    public void addLocation(Location location) {
        locations.add(location);
    }
    public Location addLocation(String addr, String phone, String contact) {
        Location l = new Location(counter++,addr, phone, contact);
        addLocation(l);
        return l;
    }

    // FIXED: Direct index lookup
    public Location getLocationById(int id) {
        for (Location location : locations) {
            if(location.id()==id) return location;
        }
        throw new IllegalArgumentException("Location index out of bounds: " + id);
    }

    public List<String> getLocationsDisplay() {
        List<String> locationsDisplay = new ArrayList<>();
        for (Location location : locations)
            locationsDisplay.add(location.toString());
        return locationsDisplay;
    }
}