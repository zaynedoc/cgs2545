import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseManager {
    // each Java action calls a matching stored procedure in MySQL
    private static final String REGISTER_NEW_USER_SQL = "{CALL registerNewUser(?, ?)}";
    private static final String LOGIN_WITH_CREDS_SQL = "{CALL loginWithCreds(?, ?)}";
    private static final String JDBC_URL =
            "jdbc:mysql://127.0.0.1:3306/project4?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "zaynedoc";
    private static final String PASSWORD = "zaynedoc123!"; // not my actual password for anything on the internet..

    private DatabaseManager() {
    }

    // open a new connection for the current database action
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

    public static void registerNewUser(String username, String password) throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(REGISTER_NEW_USER_SQL)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        }
    }

    public static SessionContext loginWithCreds(String username, String password) throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(LOGIN_WITH_CREDS_SQL)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);

            boolean hasResults = statement.execute();
            if (!hasResults) {
                return null;
            }

            // a matching login returns exactly one user row from the action
            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new SessionContext(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getInt("userRole")
                );
            }
        }
    }
}