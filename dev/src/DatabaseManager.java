import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {


    private static final String URL = "jdbc:sqlite:databaseReference.db";

    /**
     * This method opens a connection to your SQLite database.
     * Your DAO builders will call this to get their connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }


}