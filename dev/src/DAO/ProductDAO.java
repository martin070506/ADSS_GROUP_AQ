package DAO;

import DTO.ProductDTO;
import Domain.Transportation.Product;

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
        String sql = "SELECT 1 FROM Product WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a row was found
            }
        }
    }
    public int getHighestProductID() throws SQLException {
        String sql = "SELECT MAX(id) FROM Product";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void addProduct(Product product) throws SQLException {
        ProductDTO productDTO = new ProductDTO(product.id(), product.name(), product.weight());
        String sql = "INSERT INTO Product (id, name, weight) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productDTO.id());
            stmt.setString(2, productDTO.name());
            stmt.setInt(3, productDTO.weight());
            stmt.executeUpdate();
        }
    }

    public List<Product> loadAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, weight FROM Product";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("weight")
                ));
            }
        }
        return products;
    }

    public void removeProduct(int id) throws SQLException {
        String sql = "DELETE FROM Product WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}