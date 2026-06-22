package DAO;

import DTO.TransportFileDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransportFileDAO {
    private final Connection connection;

    public TransportFileDAO(Connection connection) {
        this.connection = connection;
    }

    public void addTransportFile(TransportFileDTO transportFileDTO) throws SQLException {
        String sql = "INSERT INTO transport_files (transport_id, text_content) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transportFileDTO.id());
            stmt.setString(2, transportFileDTO.transportText());
            stmt.executeUpdate();
        }
    }
}