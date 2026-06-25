package DAO.Workers;

import DTO.Workers.WorkerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerDAO {
    private final Connection connection;

    /**
     * Constructor to pass your active database connection.
     */
    public WorkerDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks if a worker ID already exists in the database.
     */
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM workers WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Adds a new worker (or driver) record to the database.
     */
    public void addWorker(WorkerDTO worker) throws SQLException {
        String sql = "INSERT INTO workers (name, id, bank_info, salary, salary_condition, start_job_date, is_shift_manager, is_driver, license) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, worker.name());
            stmt.setInt(2, worker.id());
            stmt.setString(3, worker.bank_info());
            stmt.setDouble(4, worker.salary());
            stmt.setString(5, worker.salary_condition());

            // Convert LocalDate to java.sql.Date safely
            if (worker.start_job_date() != null) {
                stmt.setDate(6, Date.valueOf(worker.start_job_date()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setBoolean(7, worker.is_shift_manager());
            stmt.setBoolean(8, worker.isDriver());
            stmt.setInt(9, worker.license());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a worker entirely from the database by their ID.
     */
    public void deleteWorker(int id) throws SQLException {
        String sql = "DELETE FROM workers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ==========================================
    // FIELD-SPECIFIC EDIT/UPDATE METHODS
    // ==========================================

    public void updateName(int id, String name) throws SQLException {
        String sql = "UPDATE workers SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateBankInfo(int id, String bankInfo) throws SQLException {
        String sql = "UPDATE workers SET bank_info = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bankInfo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateSalary(int id, double salary) throws SQLException {
        String sql = "UPDATE workers SET salary = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, salary);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateSalaryCondition(int id, String salaryCondition) throws SQLException {
        String sql = "UPDATE workers SET salary_condition = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, salaryCondition);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateStartJobDate(int id, java.time.LocalDate startDate) throws SQLException {
        String sql = "UPDATE workers SET start_job_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (startDate != null) {
                stmt.setDate(1, Date.valueOf(startDate));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateIsShiftManager(int id, boolean isShiftManager) throws SQLException {
        String sql = "UPDATE workers SET is_shift_manager = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isShiftManager);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateIsDriver(int id, boolean isDriver) throws SQLException {
        String sql = "UPDATE workers SET is_driver = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isDriver);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateLicense(int id, int license) throws SQLException {
        String sql = "UPDATE workers SET license = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, license);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
    public List<WorkerDTO> loadAll() {
        List<WorkerDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Worker";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String dateStr = rs.getString("start_job_date");
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
                list.add(new WorkerDTO(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getString("bank_info"),
                        rs.getDouble("salary"),
                        rs.getString("salary_condition"),
                        localDate,
                        rs.getBoolean("is_shift_manager"),
                        rs.getBoolean("is_driver"),
                        rs.getInt("license")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}