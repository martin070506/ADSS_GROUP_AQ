package DAO;

import DTO.ShiftPlacementJobsWorkersDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftPlacementJobsWorkersDAO {
    private final Connection connection;

    public ShiftPlacementJobsWorkersDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(ShiftPlacementJobsWorkersDTO dto) {
        String sql = "INSERT INTO shift_placement_jobs_workers (job, date, is_morning_shift, location_id, worker_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dto.job());
            ps.setDate(2, Date.valueOf(dto.date()));
            ps.setBoolean(3, dto.is_morning_shift());
            ps.setInt(4, dto.locationId());
            ps.setInt(5, dto.worker_id());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void remove(ShiftPlacementJobsWorkersDTO dto) {
        String sql = "DELETE FROM shift_placement_jobs_workers WHERE job = ? AND date = ? AND is_morning_shift = ? AND location_id = ? AND worker_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dto.job());
            ps.setDate(2, Date.valueOf(dto.date()));
            ps.setBoolean(3, dto.is_morning_shift());
            ps.setInt(4, dto.locationId());
            ps.setInt(5, dto.worker_id());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ShiftPlacementJobsWorkersDTO> loadAll() {
        List<ShiftPlacementJobsWorkersDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_placement_jobs_workers";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ShiftPlacementJobsWorkersDTO(
                        rs.getInt("job"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_morning_shift"),
                        rs.getInt("location_id"),
                        rs.getInt("worker_id")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
