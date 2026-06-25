package DAO.Transportation;

import DB.DatabaseManager;
import DTO.Transportation.LocationDTO;

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

    public boolean exists(int locationId) {
        String sql = "SELECT 1 FROM Location WHERE location_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LocationDTO> loadAllLocations() {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return locations;
    }

    public void addLocation(LocationDTO location) {
        addLocation(location, null);
    }

    public void addBranch(LocationDTO location) {
        addLocation(location, "Branch");
    }

    public void addSupplier(LocationDTO location) {
        addLocation(location, "Supplier");
    }

    private void addLocation(LocationDTO location, String locationType) {
        String sql = "INSERT INTO Location (location_id, contact_name, address, phone_number, location_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.locationId());
            stmt.setString(2, location.contactName());
            stmt.setString(3, location.address());
            stmt.setString(4, location.phoneNumber());
            stmt.setString(5, locationType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static LocationDTO mapRowToLocationDTO(ResultSet rs) {
        try {
            return new LocationDTO(
                    rs.getInt("location_id"),
                    rs.getString("contact_name"),
                    rs.getString("address"),
                    rs.getString("phone_number"),
                    rs.getString("location_type")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLocation(int locationId) {
        String sql = "DELETE FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHighestLocationID() {
        String sql = "SELECT MAX(location_id) FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public LocationDTO getLocation(int locationId) {
        String sql = "SELECT * FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLocationDTO(rs);
                }
            }
            throw new RuntimeException("Location not found with ID: " + locationId);
        } catch (SQLException e) {
            throw new RuntimeException("Location not found with ID: " + e);
        }
    }

    public List<LocationDTO> loadAllBranches() {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Branch'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return locations;
    }

    public List<LocationDTO> loadAllSuppliers() {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location WHERE location_type = 'Supplier'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocationDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return locations;
    }
}

