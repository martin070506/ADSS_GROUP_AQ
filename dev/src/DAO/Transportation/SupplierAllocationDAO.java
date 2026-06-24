package DAO.Transportation;

import DTO.Transportation.SupplierAllocationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // הפונקציה עכשיו מקבלת DTO
    public void addAllocation(SupplierAllocationDTO dto) throws SQLException {
        if (exists(dto.LocationID(), dto.productID())) {
            String updateSql = "UPDATE SupplierAllocation SET amount_of_product = ? WHERE location_id = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, dto.amountOfProduct());
                stmt.setInt(2, dto.LocationID());
                stmt.setInt(3, dto.productID());
                stmt.executeUpdate();
            }
        } else {
            String insertSql = "INSERT INTO SupplierAllocation (location_id, product_id, amount_of_product) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, dto.LocationID());
                stmt.setInt(2, dto.productID());
                stmt.setInt(3, dto.amountOfProduct());
                stmt.executeUpdate();
            }
        }
    }

    // מחזירה עכשיו רשימה של DTOs במקום Map (תפקיד ה-DAO זה להחזיר שורות מידע)
    public List<SupplierAllocationDTO> getAllocations(int locationId) throws SQLException {
        List<SupplierAllocationDTO> allocations = new ArrayList<>();
        String sql = "SELECT product_id, amount_of_product FROM SupplierAllocation WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allocations.add(new SupplierAllocationDTO(
                            locationId,
                            rs.getInt("product_id"),
                            rs.getInt("amount_of_product")
                    ));
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

    // מקבלת DTO בדיוק כמו ה-add
    public void updateAllocation(SupplierAllocationDTO dto) throws SQLException {
        addAllocation(dto);
    }
}