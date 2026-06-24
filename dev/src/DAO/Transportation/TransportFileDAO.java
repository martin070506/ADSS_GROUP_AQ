package DAO.Transportation;

import DTO.Transportation.TransportFileDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransportFileDAO {
    private final Connection connection;

    public TransportFileDAO(Connection connection) {
        this.connection = connection;
    }

    public void addTransportFile(TransportFileDTO transportFileDTO) throws SQLException {
        String sql = "INSERT INTO TransportFile (transport_id, text_content) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transportFileDTO.id());
            stmt.setString(2, transportFileDTO.transportText());
            stmt.executeUpdate();
        }
    }

    public int getMaxFileNumber() {
        String sql = "SELECT MAX(transport_id) AS max_id FROM TransportFile";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // מביא את המספר הגדול ביותר. אם הטבלה ריקה, זה יחזיר 0.
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0; // במקרה חריג שהטבלה לא קיימת או אין תוצ
    }
}