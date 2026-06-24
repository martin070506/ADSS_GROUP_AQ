package DAO;

import DTO.ProductFileDTO;
import DTO.ProductFile_ItemsDTO;
import DTO.RequestDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestDAO {
    private final Connection connection;

    public RequestDAO(Connection connection) {
        this.connection = connection;
    }

    public int getMaxFileNumber() throws SQLException {
        String sql = "SELECT MAX(file_number) AS max_id FROM ProductFile";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // מביא את המספר הגדול ביותר. אם הטבלה ריקה, זה יחזיר 0.
                return rs.getInt("max_id");
            }
        }
        return 0; // במקרה חריג שהטבלה לא קיימת או אין תוצאה
    }

    public int getActiveFileNumber(int locationId) throws SQLException {
        String sql = "SELECT file_number FROM Request WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("file_number");
                }
            }
        }
        return -1;
    }

    public boolean exists(int locationId) throws SQLException {
        return getActiveFileNumber(locationId) != -1;
    }

    public void addProductFile(ProductFileDTO fileDto) throws SQLException {
        String sql = "INSERT INTO ProductFile (file_number, location_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileDto.fileNumber());
            stmt.setInt(2, fileDto.locationId());
            stmt.setString(3, fileDto.status());
            stmt.executeUpdate();
        }
    }

    public void addProductFileItem(ProductFile_ItemsDTO itemDto) throws SQLException {
        String checkSql = "SELECT 1 FROM ProductFile_Items WHERE file_number = ? AND product_id = ? LIMIT 1";
        boolean itemExists;
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, itemDto.fileNumber());
            checkStmt.setInt(2, itemDto.productId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                itemExists = rs.next();
            }
        }

        if (itemExists) {
            String updateSql = "UPDATE ProductFile_Items SET amount = ? WHERE file_number = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, itemDto.amount());
                stmt.setInt(2, itemDto.fileNumber());
                stmt.setInt(3, itemDto.productId());
                stmt.executeUpdate();
            }
        } else {
            String insertSql = "INSERT INTO ProductFile_Items (file_number, product_id, amount) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, itemDto.fileNumber());
                stmt.setInt(2, itemDto.productId());
                stmt.setInt(3, itemDto.amount());
                stmt.executeUpdate();
            }
        }
    }

    public void addRequest(RequestDTO requestDTO) throws SQLException {
        if (exists(requestDTO.locationID())) {
            String updateSql = "UPDATE Request SET file_number = ? WHERE location_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setInt(1, requestDTO.fileNumber());
                stmt.setInt(2, requestDTO.locationID());
                stmt.executeUpdate();
            }
        } else {
            String insertSql = "INSERT INTO Request (location_id, file_number) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, requestDTO.locationID());
                stmt.setInt(2, requestDTO.fileNumber());
                stmt.executeUpdate();
            }
        }
    }

    public void removeAllRequestsForLocationID(int locationId) throws SQLException {
        String sql = "DELETE FROM Request WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    public void removeRequestPair(int locationId, int productId) throws SQLException {
        int fileNumber = getActiveFileNumber(locationId);
        if (fileNumber != -1) {
            String sql = "DELETE FROM ProductFile_Items WHERE file_number = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, fileNumber);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
            }
        }
    }

    // המיפוי החדש: DAO מחזיר מפה של RequestDTO (הכותרת) ורשימה של פריטים (ItemsDTO)
    public Map<RequestDTO, List<ProductFile_ItemsDTO>> loadAllRequests() throws SQLException {
        Map<RequestDTO, List<ProductFile_ItemsDTO>> requestsMap = new HashMap<>();

        String sql = "SELECT ar.location_id, ar.file_number, pfi.product_id, pfi.amount " +
                "FROM Request ar " +
                "JOIN ProductFile_Items pfi ON ar.file_number = pfi.file_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int locationId = rs.getInt("location_id");
                int fileNumber = rs.getInt("file_number");
                int productId = rs.getInt("product_id");
                int amount = rs.getInt("amount");

                RequestDTO requestDTO = new RequestDTO(locationId, fileNumber);

                requestsMap.putIfAbsent(requestDTO, new ArrayList<>());
                requestsMap.get(requestDTO).add(new ProductFile_ItemsDTO(fileNumber, productId, amount));
            }
        }
        return requestsMap;
    }

    public void removeProductFileItems(int fileNumber) throws SQLException {
        String sql = "DELETE FROM ProductFile_Items WHERE file_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileNumber);
            stmt.executeUpdate();
        }
    }

    public void removeProductFile(int fileNumber) throws SQLException {
        String sql = "DELETE FROM ProductFile WHERE file_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileNumber);
            stmt.executeUpdate();
        }
    }

    public void setRequestInactive(int locationId, int fileNumber) throws SQLException {
        String sql1 = "DELETE FROM Request WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }

        String sql2 = "UPDATE ProductFile SET status = 'Inactive' WHERE file_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql2)) {
            stmt.setInt(1, fileNumber);
            stmt.executeUpdate();
        }
    }
}