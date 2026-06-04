package Service;

import Domain.Location;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        locations.add(location);
    }
    public Location addLocation(String addr, String phone, String contact) {
        Location l = new Location(addr, phone, contact);
        addLocation(l);
        return l;
    }

    // FIXED: Direct index lookup
    public Location getLocationByIndex(int index) {
        if (index >= 0 && index < locations.size()) {
            return locations.get(index);
        }
        throw new IllegalArgumentException("Location index out of bounds: " + index);
    }

    public List<String> getLocationsDisplay() {
        List<String> locationsDisplay = new ArrayList<>();
        for (Location location : locations)
            locationsDisplay.add(location.toString());
        return locationsDisplay;
    }
}