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

    // Constructor to pass your active database connection
    public LocationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks if a location ID already exists in the database.
     */
    public boolean exists(int locationId) throws SQLException {
        String sql = "SELECT 1 FROM locations WHERE location_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if the location exists
            }
        }
    }

    /**
     * Adds a new location record to the database.
     */
    public void addLocation(LocationDTO location) throws SQLException {
        String sql = "INSERT INTO locations (location_id, contact_name, address, phone_number, is_branch, is_supplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.locationId());
            stmt.setString(2, location.contactName());
            stmt.setString(3, location.address());
            stmt.setString(4, location.phoneNumber());
            stmt.setBoolean(5, location.isBranch());
            stmt.setBoolean(6, location.isSupplier());
            stmt.executeUpdate();
        }
    }

    /**
     * Helper method to map a single ResultSet row into a LocationDTO.
     */
    private LocationDTO mapRowToLocation(ResultSet rs) throws SQLException {
        return new LocationDTO(
                rs.getInt("location_id"),
                rs.getString("contact_name"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getBoolean("is_branch"),
                rs.getBoolean("is_supplier")
        );
    }

    /**
     * Loads all locations from the database into a List of DTOs.
     */
    public List<LocationDTO> loadAllLocations() throws SQLException {
        List<LocationDTO> locations = new ArrayList<>();
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM locations";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }

    /**
     * Loads ONLY locations that are marked as branches.
     */
    public List<LocationDTO> loadAllBranches() throws SQLException {
        List<LocationDTO> branches = new ArrayList<>();
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM locations WHERE is_branch = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                branches.add(mapRowToLocation(rs));
            }
        }
        return branches;
    }

    /**
     * Loads ONLY locations that are marked as suppliers.
     */
    public List<LocationDTO> loadAllSuppliers() throws SQLException {
        List<LocationDTO> suppliers = new ArrayList<>();
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM locations WHERE is_supplier = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapRowToLocation(rs));
            }
        }
        return suppliers;
    }

    /**
     * Removes a location from the database by its primary key ID.
     */
    public void removeLocation(int locationId) throws SQLException {
        String sql = "DELETE FROM locations WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }
}