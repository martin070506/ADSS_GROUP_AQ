package DAO;

import DTO.TransportFileDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransportFileDAO {
    private final Connection connection;

    /**
     * Constructor to pass your active database connection.
     */
    public TransportFileDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a basic TransportFile record into the database.
     */
    public void addTransportFile(TransportFileDTO transportFileDto) throws SQLException {
        String sql = "INSERT INTO transport_files (transport_id, text_content) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transportFileDto.id());
            stmt.setString(2, transportFileDto.transportText());
            stmt.executeUpdate();
        }
    }
}