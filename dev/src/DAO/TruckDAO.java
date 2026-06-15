package DAO;

import DTO.TruckDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TruckDAO {
    private final Connection connection;

    // Constructor to pass your active database connection
    public TruckDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks if a truck ID already exists in the database.
     */
    public boolean exists(int truckID) throws SQLException {
        String sql = "SELECT 1 FROM trucks WHERE truck_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if the truck exists
            }
        }
    }

    /**
     * Adds a new truck record to the database.
     */
    public void addTruck(TruckDTO truck) throws SQLException {
        String sql = "INSERT INTO trucks (truck_id, truck_model, required_license) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truck.truckID());
            stmt.setString(2, truck.truckModel());
            stmt.setInt(3, truck.requiredLicense());
            stmt.executeUpdate();
        }
    }

    /**
     * Loads all trucks from the database into a List of DTOs.
     */
    public List<TruckDTO> loadAllTrucks() throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT truck_id, truck_model, required_license FROM trucks";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trucks.add(new TruckDTO(
                        rs.getInt("truck_id"),
                        rs.getString("truck_model"),
                        rs.getInt("required_license")
                ));
            }
        }
        return trucks;
    }

    /**
     * Removes a truck from the database by its primary key ID.
     */
    public void removeTruck(int truckID) throws SQLException {
        String sql = "DELETE FROM trucks WHERE truck_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            stmt.executeUpdate();
        }
    }
}