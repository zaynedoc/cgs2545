import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final String JDBC_URL =
            "jdbc:mysql://127.0.0.1:3306/project4?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "zaynedoc";
    private static final String PASSWORD = "Zayne05!"; // not my actual password for anything on the internet..

    private DatabaseManager() {
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection connection = openConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            System.out.println("Database connection failed.");
            System.out.println(exception.getMessage());
            return false;
        }
    }
}