package DAO;

import DTO.ProductDTO;
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

    /**
     * Checks if a product ID already exists in the database.
     */
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a row was found
            }
        }
    }

    public void addProduct(ProductDTO product) throws SQLException {
        String sql = "INSERT INTO products (id, name, weight) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product.id());
            stmt.setString(2, product.name());
            stmt.setInt(3, product.weight());
            stmt.executeUpdate();
        }
    }

    public List<ProductDTO> loadAllProducts() throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT id, name, weight FROM products";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(new ProductDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("weight")
                ));
            }
        }
        return products;
    }

    public void removeProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}