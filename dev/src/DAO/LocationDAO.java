package DAO;

import DTO.LocationDTO;
import Domain.Transportation.BranchManager;
import Domain.Transportation.Location;
import Domain.Transportation.Supplier;
import Service.Transportation.BranchService;
import Service.Transportation.LocationService;
import Service.Transportation.SupplierService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String sql = "SELECT 1 FROM Location WHERE location_id = ? LIMIT 1";
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
        String sql = "INSERT INTO Location (location_id, contact_name, address, phone_number, is_branch, is_supplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.locationId());
            stmt.setString(2, location.contactName());
            stmt.setString(3, location.address());
            stmt.setString(4, location.phoneNumber());

            // Convert Java boolean to upper-case String ("TRUE" / "FALSE") for your TEXT columns
            stmt.setString(5, location.isBranch() ? "TRUE" : "FALSE");
            stmt.setString(6, location.isSupplier() ? "TRUE" : "FALSE");

            stmt.executeUpdate();
        }
    }

    /**
     * Helper method to map a single ResultSet row into a LocationDTO.
     */

    private BranchManager mapRowToBranch(ResultSet rs,LocationService locationService) throws SQLException {
        // Read the TEXT values from the DB and convert them back to a Java boolean
        boolean isBranch = "TRUE".equalsIgnoreCase(rs.getString("is_branch"));
        boolean isSupplier = "TRUE".equalsIgnoreCase(rs.getString("is_supplier"));

        return new BranchManager(
                locationService.getLocation(rs.getInt("location_id"))
        );
    }
    private Supplier mapRowToEmptySupplier(ResultSet rs ,LocationService locationService) throws SQLException {
        // Read the TEXT values from the DB and convert them back to a Java boolean
        boolean isBranch = "TRUE".equalsIgnoreCase(rs.getString("is_branch"));
        boolean isSupplier = "TRUE".equalsIgnoreCase(rs.getString("is_supplier"));

        return new Supplier(
                locationService.getLocation(rs.getInt("location_id")), new HashMap<>()
        );
    }
    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        return new Location(rs.getInt("location_id"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getString("contact_name"));
    }





    /**
     * Loads ONLY locations that are marked as branches.
     */
    public List<BranchManager> loadAllBranches(LocationService locationService) throws SQLException {
        List<BranchManager> branches = new ArrayList<>();
        // Updated: Filters using the string literal 'TRUE'
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM Location WHERE is_branch = 'TRUE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                branches.add(mapRowToBranch(rs,locationService));
            }
        }
        return branches;
    }
    public List<Location> loadAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        // Updated: Filters using the string literal 'TRUE'
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM Location";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }

    /**
     * Loads ONLY locations that are marked as suppliers.
     */
    public List<Supplier> loadAllSuppliersAsEmpty(LocationService locationService) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        // Updated: Filters using the string literal 'TRUE'
        String sql = "SELECT location_id, contact_name, address, phone_number, is_branch, is_supplier FROM Location WHERE is_supplier = 'TRUE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapRowToEmptySupplier(rs,locationService));
            }
        }
        return suppliers;
    }

    /**
     * Removes a location from the database by its primary key ID.
     */
    public void removeLocation(int locationId) throws SQLException {
        String sql = "DELETE FROM Location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds and returns the highest location_id currently in the database.
     * Returns 0 if the table is empty.
     */
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
}