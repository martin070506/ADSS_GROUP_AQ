package DAO;

import DTO.SupplierAllocationDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SupplierAllocationDAO {
    private final Connection connection;

    /**
     * Constructor to pass your active database connection.
     */
    public SupplierAllocationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks if a specific supplier location and product pairing already exists.
     */
    public boolean exists(int locationId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM supplier_allocations WHERE location_id = ? AND product_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if this pairing already exists
            }
        }
    }

    /**
     * Persists a SupplierAllocationDTO to the database.
     * If the location_id and product_id combination already exists, it updates/overwrites the amount.
     */
    public void addAllocation(SupplierAllocationDTO allocationDto) throws SQLException {
        if (exists(allocationDto.LocationID(), allocationDto.productID())) {
            // Update/Overwrite amount if the key combination exists
            String updateSql = "UPDATE supplier_allocations SET amount_of_product = ? WHERE location_id = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, allocationDto.amountOfProduct());
                stmt.setInt(2, allocationDto.LocationID());
                stmt.setInt(3, allocationDto.productID());
                stmt.executeUpdate();
            }
        } else {
            // Insert brand new record if it doesn't exist
            String insertSql = "INSERT INTO supplier_allocations (location_id, product_id, amount_of_product) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, allocationDto.LocationID());
                stmt.setInt(2, allocationDto.productID());
                stmt.setInt(3, allocationDto.amountOfProduct());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Fetches all allocations for a specific supplier location ID.
     * Maps Product ID to Product Amount. Returns an empty map if no allocations are found.
     */
    public Map<Integer, Integer> loadSupplierAllocations(int locationId) throws SQLException {
        Map<Integer, Integer> allocations = new HashMap<>();
        String sql = "SELECT product_id, amount_of_product FROM supplier_allocations WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int amount = rs.getInt("amount_of_product");
                    allocations.put(productId, amount);
                }
            }
        }
        return allocations;
    }

    /**
     * Overloaded Deletion: Wipes out a SPECIFIC product row for a specific supplier location ID.
     */
    public void removeAllocation(int locationId, int productId) throws SQLException {
        String sql = "DELETE FROM supplier_allocations WHERE location_id = ? AND product_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    /**
     * Overloaded Deletion: Wipes out ALL allocation records tied to a specific location ID.
     */
    public void removeAllocation(int locationId) throws SQLException {
        String sql = "DELETE FROM supplier_allocations WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }
}