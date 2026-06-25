package DAO.Workers;

import DTO.Workers.ShiftPlacementJobsWorkersDTO;

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
                list.add(new ShiftPlacementJobsWorkersDTO(
                        rs.getInt("job"),
                        localDate,
                        rs.getBoolean("is_morning_shift"),
                        rs.getInt("location_id"),
                        rs.getInt("worker_id")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
    public void updateWorkerInShift(ShiftPlacementJobsWorkersDTO oldPlacement, int newWorkerId) {
        String deleteSql = "DELETE FROM shift_placement_jobs_workers WHERE date = ? AND is_morning_shift = ? AND location_id = ? AND worker_id = ?";
        String insertSql = "INSERT INTO shift_placement_jobs_workers (job, date, is_morning_shift, location_id, worker_id) VALUES (?, ?, ?, ?, ?)";

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement delPs = connection.prepareStatement(deleteSql)) {
                delPs.setDate(1, Date.valueOf(oldPlacement.date()));
                delPs.setBoolean(2, oldPlacement.is_morning_shift());
                delPs.setInt(3, oldPlacement.locationId());
                delPs.setInt(4, oldPlacement.worker_id());
                delPs.executeUpdate();
            }
            try (PreparedStatement insPs = connection.prepareStatement(insertSql)) {
                insPs.setInt(1, oldPlacement.job());
                insPs.setDate(2, Date.valueOf(oldPlacement.date()));
                insPs.setBoolean(3, oldPlacement.is_morning_shift());
                insPs.setInt(4, oldPlacement.locationId());
                insPs.setInt(5, newWorkerId);
                insPs.executeUpdate();
            }
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException("Failed to update worker in shift. Transaction rolled back.", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
