package DAO;

import DTO.ShiftCandidateIdDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftCandidateIdDAO {
    private final Connection connection;

    public ShiftCandidateIdDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(ShiftCandidateIdDTO dto) {
        String sql = "INSERT INTO shift_candidate_ids (date, is_morning_shift, location_id, worker_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.setInt(4, dto.worker_id());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void remove(ShiftCandidateIdDTO dto) {
        String sql = "DELETE FROM shift_candidate_ids WHERE date = ? AND is_morning_shift = ? AND location_id = ? AND worker_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.setInt(4, dto.worker_id());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ShiftCandidateIdDTO> loadAll() {
        List<ShiftCandidateIdDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_candidate_ids";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ShiftCandidateIdDTO(
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
