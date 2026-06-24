package DAO;
import DTO.ShiftCandidatesDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftCandidatesDAO {
    private final Connection connection;

    public ShiftCandidatesDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(ShiftCandidatesDTO dto) {
        String sql = "INSERT INTO shift_candidates (date, is_morning_shift, location_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void remove(ShiftCandidatesDTO dto) {
        String sql = "DELETE FROM shift_candidates WHERE date = ? AND is_morning_shift = ? AND location_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ShiftCandidatesDTO> loadAll() {
        List<ShiftCandidatesDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_candidates";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ShiftCandidatesDTO(
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_morning_shift"),
                        rs.getInt("location_id")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}