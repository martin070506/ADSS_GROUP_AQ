package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SupplierAllocationDAO {
    private final Connection connection;

    public SupplierAllocationDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean exists(int locationId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM SupplierAllocation WHERE location_id = ? AND product_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addAllocation(int locationId, int productId, int amount) throws SQLException {
        if (exists(locationId, productId)) {
            String updateSql = "UPDATE SupplierAllocation SET amount_of_product = ? WHERE location_id = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, amount);
                stmt.setInt(2, locationId);
                stmt.setInt(3, productId);
                stmt.executeUpdate();
            }
        } else {
            String insertSql = "INSERT INTO SupplierAllocation (location_id, product_id, amount_of_product) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, locationId);
                stmt.setInt(2, productId);
                stmt.setInt(3, amount);
                stmt.executeUpdate();
            }
        }
    }

    public Map<Integer, Integer> getAllocations(int locationId) throws SQLException {
        Map<Integer, Integer> allocations = new HashMap<>();
        String sql = "SELECT product_id, amount_of_product FROM SupplierAllocation WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allocations.put(rs.getInt("product_id"), rs.getInt("amount_of_product"));
                }
            }
        }
        return allocations;
    }

    public void removeAllocation(int locationId, int productId) throws SQLException {
        String sql = "DELETE FROM SupplierAllocation WHERE location_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public void removeAllocation(int locationId) throws SQLException {
        String sql = "DELETE FROM SupplierAllocation WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    public void updateAllocation(int supplierId, int productId, int updatedStock) throws SQLException {
        addAllocation(supplierId, productId, updatedStock);
    }
}