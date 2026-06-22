package DAO;

import DTO.ProductFileDTO;
import DTO.ProductFile_ItemsDTO;
import DTO.RequestDTO;
import Domain.Transportation.Location;
import Domain.Transportation.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RequestDAO {
    private final Connection connection;
    private final LocationDAO locationDAO;

    public RequestDAO(Connection connection, LocationDAO locationDAO) {
        this.connection = connection;
        this.locationDAO = locationDAO;
    }

    private int getActiveFileNumber(int locationId) throws SQLException {
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

    public boolean exists(int locationId, int productId) throws SQLException {
        int fileNumber = getActiveFileNumber(locationId);
        if (fileNumber == -1) return false;

        String sql = "SELECT 1 FROM ProductFile_Items WHERE file_number = ? AND product_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileNumber);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Request getRequestByLocation(Location location) throws SQLException {
        int fileNumber = getActiveFileNumber(location.id());
        if (fileNumber == -1) return null;

        Map<Integer, Integer> neededItems = new HashMap<>();
        String sql = "SELECT product_id, amount FROM ProductFile_Items WHERE file_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    neededItems.put(rs.getInt("product_id"), rs.getInt("amount"));
                }
            }
        }

        return new Request(location, fileNumber, neededItems);
    }

    public void addProductFile(ProductFileDTO fileDto) throws SQLException {
        String sql = "INSERT INTO ProductFile (file_number, location_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fileDto.fileNumber());
            stmt.setInt(2, fileDto.locationId());
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

    public void addRequest(Request request) throws SQLException {
        RequestDTO requestDTO = new RequestDTO(request);
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

    public Collection<Request> loadAllRequests() throws SQLException {
        Map<Integer, Map<Integer, Integer>> productsByLocation = new HashMap<>();
        Map<Integer, Integer> fileNumberByLocation = new HashMap<>();

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

                fileNumberByLocation.put(locationId, fileNumber);
                productsByLocation.putIfAbsent(locationId, new HashMap<>());
                productsByLocation.get(locationId).put(productId, amount);
            }
        }

        List<Request> allRequests = new ArrayList<>();

        for (int locationId : fileNumberByLocation.keySet()) {
            Location location = locationDAO.getLocation(locationId);

            if (location != null) {
                int fileNumber = fileNumberByLocation.get(locationId);
                Map<Integer, Integer> neededItems = productsByLocation.get(locationId);
                allRequests.add(new Request(location, fileNumber, neededItems));
            }
        }

        return allRequests;
    }
}