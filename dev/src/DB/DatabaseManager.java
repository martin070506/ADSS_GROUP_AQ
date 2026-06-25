package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static String getDbPath() {
        String userDir = System.getProperty("user.dir");
        // If running from inside 'release', step out one folder using "../"
        // If running from Project Root (IntelliJ), look directly in the root
        return userDir.endsWith("release") ? "../databaseReference.db" : "databaseReference.db";
    }

    private static final String URL = "jdbc:sqlite:" + getDbPath();


    /**
     * This method opens a connection to your SQLite database.
     * Your DAO builders will call this to get their connection.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}