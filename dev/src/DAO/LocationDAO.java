package DAO;

import DTO.LocationDTO;

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

    public List<LocationDTO> loadAllLocations() throws SQLException {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        }
        return locations;
    }

    public void addLocation(LocationDTO location) throws SQLException {
        addLocation(location, null);
    }

    public void addBranch(LocationDTO location) throws SQLException {
        addLocation(location, "Branch");
    }

    public void addSupplier(LocationDTO location) throws SQLException {
        addLocation(location, "Supplier");
    }

    private void addLocation(LocationDTO location, String locationType) throws SQLException {
        String sql = "INSERT INTO Location (location_id, contact_name, address, phone_number, location_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.locationId());
            stmt.setString(2, location.contactName());
            stmt.setString(3, location.address());
            stmt.setString(4, location.phoneNumber());
            stmt.setString(5, locationType);
            stmt.executeUpdate();
        }
    }

    private LocationDTO mapRowToLocationDTO(ResultSet rs) throws SQLException {
        return new LocationDTO(
                rs.getInt("location_id"),
                rs.getString("contact_name"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getString("location_type")
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

    public LocationDTO getLocation(int locationId) throws SQLException {
        String sql = "SELECT * FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLocationDTO(rs);
                }
            }
        }
        throw new SQLException("Location not found: " + locationId);
    }

    public List<LocationDTO> loadAllBranches() throws SQLException {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Branch'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        }
        return locations;
    }

    public List<LocationDTO> loadAllSuppliers() throws SQLException {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Supplier'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        }
        return locations;
    }
}