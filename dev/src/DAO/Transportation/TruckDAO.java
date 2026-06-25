package DAO.Transportation;

import DTO.Transportation.TruckDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TruckDAO {
    private final Connection connection;

    public TruckDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean exists(int truckID) {
        String sql = "SELECT 1 FROM Truck WHERE truck_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHighestTruckID() {
        String sql = "SELECT MAX(truck_id) FROM Truck";
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

    public void addTruck(TruckDTO truckDTO) {
        String sql = "INSERT INTO Truck (truck_id, truck_model, required_license, truck_number, startWeight, maxWeight) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckDTO.id());
            stmt.setString(2, truckDTO.model());
            stmt.setInt(3, truckDTO.minLicense());
            stmt.setInt(4, truckDTO.truckNumber());
            stmt.setInt(5, truckDTO.startWeight());
            stmt.setInt(6, truckDTO.maxWeight());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TruckDTO> loadAllTrucks() {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT truck_id, truck_model, required_license, truck_number, startWeight, maxWeight FROM Truck";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trucks.add(mapRowToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return trucks;
    }

    private TruckDTO mapRowToTruckDTO(ResultSet rs) {
        try {
            return new TruckDTO(
                    rs.getInt("truck_id"),
                    rs.getInt("truck_number"),
                    rs.getString("truck_model"),
                    rs.getInt("startWeight"),
                    rs.getInt("maxWeight"),
                    rs.getInt("required_license")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeTruck(int truckID) {
        String sql = "DELETE FROM Truck WHERE truck_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, truckID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}