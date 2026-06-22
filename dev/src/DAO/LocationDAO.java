package DAO;

import Domain.Transportation.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    private final Connection connection;

    public LocationDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean exists(int locationId) throws SQLException {
        String sql = "SELECT 1 FROM Location WHERE location_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Location> loadAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }

    public void addLocation(Location location) throws SQLException {
        addLocation(location, null);
    }

    public void addBranch(Location location) throws SQLException {
        addLocation(location, "Branch");
    }

    public void addSupplier(Location location) throws SQLException {
        addLocation(location, "Supplier");
    }

    private void addLocation(Location location, String locationType) throws SQLException {
        // Updated column name from locationType to location_type for consistency
        String sql = "INSERT INTO Location (location_id, contact_name, address, phone_number, location_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.id());
            stmt.setString(2, location.contactName());
            stmt.setString(3, location.address());
            stmt.setString(4, location.phoneNumber());
            stmt.setString(5, locationType);
            stmt.executeUpdate();
        }
    }

    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        return new Location(
                rs.getInt("location_id"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getString("contact_name")
        );
    }

    public void removeLocation(int locationId) throws SQLException {
        String sql = "DELETE FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    public int getHighestLocationID() throws SQLException {
        String sql = "SELECT MAX(location_id) FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Location getLocation(int locationId) throws SQLException {
        String sql = "SELECT * FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLocation(rs);
                }
            }
        }
        throw new SQLException("Location not found: " + locationId);
    }

    public List<Location> loadAllBranches() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Branch'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }

    public List<Location> loadAllSuppliers() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Supplier'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }
}