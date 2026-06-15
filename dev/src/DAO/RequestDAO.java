package DAO;

import DTO.RequestDTO;
import Domain.Transportation.Location;
import Domain.Transportation.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RequestDAO {
    private final Connection connection;
    private final Random random = new Random(); // Used to generate the unique int fileNumber

    /**
     * Constructor to pass your active database connection.
     */
    public RequestDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks if any request entry exists for a given location ID.
     */
    public boolean exists(int locationId) throws SQLException {
        String sql = "SELECT 1 FROM requests WHERE location_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a specific location-product pairing request entry exists.
     */
    public boolean exists(int locationId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM requests WHERE location_id = ? AND product_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Fetches all requested products for a given location, builds a Map,
     * generates a unique integer file number, and constructs a rich Request object.
     */
    public Request getRequestByLocation(Location location) throws SQLException {
        Map<Integer, Integer> neededItems = new HashMap<>();
        String sql = "SELECT product_id, amount FROM requests WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, location.id());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int amount = rs.getInt("amount");
                    neededItems.put(productId, amount);
                }
            }
        }

        // Return null if no records were found in the database for this location
        if (neededItems.isEmpty()) {
            return null;
        }

        int uniqueFileNumber = generateUniqueFileNumber();
        return new Request(location, uniqueFileNumber, neededItems);
    }

    /**
     * Persists a RequestDTO to the database.
     * If the location_id and product_id combination already exists, it overwrites the amount.
     */
    public void addRequest(RequestDTO requestDto) throws SQLException {
        // If the key combination exists, update it. Otherwise, insert it.
        if (exists(requestDto.locationID(), requestDto.productID())) {
            String updateSql = "UPDATE requests SET amount = ? WHERE location_id = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, requestDto.amount());
                stmt.setInt(2, requestDto.locationID());
                stmt.setInt(3, requestDto.productID());
                stmt.executeUpdate();
            }
        } else {
            String insertSql = "INSERT INTO requests (location_id, product_id, amount) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, requestDto.locationID());
                stmt.setInt(2, requestDto.productID());
                stmt.setInt(3, requestDto.amount());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Overloaded Deletion: Wipes out ALL request rows tied to a specific location ID.
     */
    public void removeAllRequestsForLocationID(int locationId) throws SQLException {
        String sql = "DELETE FROM requests WHERE location_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    /**
     * Overloaded Deletion: Wipes out a SPECIFIC product row for a specific location ID.
     */
    public void removeRequestPair(int locationId, int productId) throws SQLException {
        String sql = "DELETE FROM requests WHERE location_id = ? AND product_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    /**
     * Helper method to generate a positive unique integer identifier.
     */
    private int generateUniqueFileNumber() {
        return random.nextInt(1_000_000_000) & Integer.MAX_VALUE;
    }
}