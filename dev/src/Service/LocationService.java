package Service;

import Domain.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private final List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        locations.add(location);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Location getLocation(String locationName) {
        for (Location location : locations)
            if (location.toString().equals(locationName))
                return location;

        throw new IllegalArgumentException("Location not found");
    }

    public List<String> getLocationsDisplay() {
        List<String> locationsDisplay = new ArrayList<>();
        for (Location location : locations)
            locationsDisplay.add(location.toString());
        return locationsDisplay;
    }
}
