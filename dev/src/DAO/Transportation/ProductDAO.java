package DAO.Transportation;

import DTO.Transportation.ProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean exists(int id) {
        String sql = "SELECT 1 FROM Product WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHighestProductID() {
        String sql = "SELECT MAX(id) FROM Product";
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

    public void addProduct(ProductDTO productDTO) {
        String sql = "INSERT INTO Product (id, name, weight) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productDTO.id());
            stmt.setString(2, productDTO.name());
            stmt.setInt(3, productDTO.weight());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductDTO> loadAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT id, name, weight FROM Product";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapRowToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    private ProductDTO mapRowToProductDTO(ResultSet rs) {
        try {
            return new ProductDTO(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("weight")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeProduct(int id) {
        String sql = "DELETE FROM Product WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}