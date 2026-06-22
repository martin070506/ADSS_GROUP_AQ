package DAO;

import DTO.TruckDTO;
import Domain.Transportation.Truck;

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
        String sql = "SELECT 1 FROM Truck WHERE truck_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if the truck exists
            }
        }
    }

    public int getHighestTruckID() throws SQLException {
        String sql = "SELECT MAX(truck_id) FROM Truck";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Gets the value of the first column in the result
            }
        }
        return 0; // Fallback default if nothing is found
    }

    /**
     * Adds a new truck record to the database with all 6 fields.
     */
    public void addTruck(TruckDTO truck) throws SQLException {
        // Fixed: Added 6 placeholders (?, ?, ?, ?, ?, ?) to match the 6 columns
        String sql = "INSERT INTO Truck (truck_id, truck_model, required_license, truck_number, startWeight, maxWeight) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truck.id());          // Assumed truckID() based on your record definition
            stmt.setString(2, truck.model());    // Assumed truckModel() based on your record definition
            stmt.setInt(3, truck.minLicense());  // Assumed requiredLicense() based on your record definition
            stmt.setInt(4, truck.truckNumber());
            stmt.setInt(5, truck.startWeight());
            stmt.setInt(6, truck.maxWeight());
            stmt.executeUpdate();
        }
        System.out.println("Truck added successfully");
    }

    /**
     * Loads all trucks from the database into a List of DTOs with all 6 fields.
     */
    public List<Truck> loadAllTrucks() throws SQLException {
        List<Truck> trucks = new ArrayList<>();
        String sql = "SELECT truck_id, truck_model, required_license, truck_number, startWeight, maxWeight FROM Truck";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Adjust this constructor call if your TruckDTO record requires all 6 parameters
                trucks.add(new Truck(
                        rs.getInt("truck_id"),
                        rs.getInt("truck_number"),
                        rs.getString("truck_model"),
                        rs.getInt("startWeight"),
                        rs.getInt("maxWeight"),
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
        String sql = "DELETE FROM Truck WHERE truck_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            stmt.executeUpdate();
        }
    }
}