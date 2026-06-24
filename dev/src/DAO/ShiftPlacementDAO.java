package DAO;

import DTO.ShiftPlacementDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftPlacementDAO {
    private final Connection connection;

    public ShiftPlacementDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(ShiftPlacementDTO dto) {
        String sql = "INSERT INTO shift_placement (date, is_morning_shift, location_id, shift_manager_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.setInt(4, dto.shift_manager_id());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void remove(ShiftPlacementDTO dto) {
        String sql = "DELETE FROM shift_placement WHERE date = ? AND is_morning_shift = ? AND location_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dto.date()));
            ps.setBoolean(2, dto.is_morning_shift());
            ps.setInt(3, dto.locationId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ShiftPlacementDTO> loadAll() {
        List<ShiftPlacementDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_placement";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String dateStr = rs.getString("date");
                java.time.LocalDate localDate = null;

                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        long millis = Long.parseLong(dateStr);
                        localDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    } catch (NumberFormatException e) {
                        localDate = java.time.LocalDate.parse(dateStr);
                    }
                }
                list.add(new ShiftPlacementDTO(
                        localDate,
                        rs.getBoolean("is_morning_shift"),
                        rs.getInt("location_id"),
                        rs.getInt("shift_manager_id")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
