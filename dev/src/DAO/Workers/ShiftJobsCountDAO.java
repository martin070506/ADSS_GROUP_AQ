package DAO.Workers;

import DTO.Workers.ShiftJobsCountDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftJobsCountDAO {
    private final Connection connection;

    public ShiftJobsCountDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(ShiftJobsCountDTO dto) {
        String sql = "INSERT INTO shift_jobs_count (job, date, is_morning_shift, location_id, count) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dto.job());
            ps.setDate(2, Date.valueOf(dto.date()));
            ps.setBoolean(3, dto.is_morning_shift());
            ps.setInt(4, dto.locationId());
            ps.setInt(5, dto.count());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public void update(ShiftJobsCountDTO dto){
        String sql = "UPDATE shift_jobs_count SET count = ? WHERE job = ? AND date = ? AND is_morning_shift = ? AND location_id = ? ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,dto.count());
            ps.setInt(2, dto.job());
            ps.setDate(3, Date.valueOf(dto.date()));
            ps.setBoolean(4, dto.is_morning_shift());
            ps.setInt(5, dto.locationId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public void remove(ShiftJobsCountDTO dto) {
        String sql = "DELETE FROM shift_jobs_count WHERE job = ? AND date = ? AND is_morning_shift = ? AND location_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dto.job());
            ps.setDate(2, Date.valueOf(dto.date()));
            ps.setBoolean(3, dto.is_morning_shift());
            ps.setInt(4, dto.locationId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ShiftJobsCountDTO> loadAll() {
        List<ShiftJobsCountDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_jobs_count";
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
                list.add(new ShiftJobsCountDTO(
                        rs.getInt("job"),
                        localDate,
                        rs.getBoolean("is_morning_shift"),
                        rs.getInt("location_id"),
                        rs.getInt("count")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}