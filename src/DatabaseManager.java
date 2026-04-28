import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles JDBC calls for the inventory app
 */
public final class DatabaseManager {
    private static final String REGISTER_NEW_USER_SQL = "{CALL registerNewUser(?, ?)}";
    private static final String LOGIN_WITH_CREDS_SQL = "{CALL loginWithCreds(?, ?)}";
    private static final String SUBMIT_NEW_PRODUCT_SQL = "{CALL submitNewProduct(?, ?)}";
    private static final String GET_ALL_PRODUCTS_SQL = "{CALL getAllProducts()}";
    private static final String EDIT_EXISTING_PRODUCT_SQL = "{CALL editExistingProduct(?, ?, ?, ?)}";
    private static final String GET_SALES_TOTAL_SQL = "{CALL getSalesTotal()}";
    private static final String SUBMIT_ORDER_SQL = "{CALL submitOrder(?, ?, ?, ?, ?)}";
    private static final String VIEW_CUSTOMER_ORDERS_SQL = "{CALL viewCustomerOrders(?)}";
    private static final String CANCEL_ORDER_SQL = "{CALL cancelOrder(?, ?, ?)}";
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/project4?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    /**
     * Prevents utility class instantiation
     */
    private DatabaseManager() {
    }

    /**
     * Opens a database connection for the current action
     *
     * @return open JDBC connection to project4
     * @throws SQLException when the connection cannot be created
     */
    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    /**
     * Tests whether the database can be reached
     *
     * @return true when the connection succeeds
     */
    public static boolean testConnection() {
        try (Connection connection = openConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            System.out.println("Database connection failed.");
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * Registers a new customer account
     *
     * @param username username to store in the users table
     * @param password password to store in the users table
     * @throws SQLException when the procedure call fails
     */
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

    /**
     * Attempts to log in a user with stored credentials
     *
     * @param username username entered by the user
     * @param password password entered by the user
     * @return logged-in session data or null when no match is found
     * @throws SQLException when the procedure call fails
     */
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

    /**
     * Inserts a new product row
     *
     * @param productName product name entered by the admin
     * @param productPrice product price entered by the admin
     * @throws SQLException when the procedure call fails
     */
    public static void submitNewProduct(String productName, BigDecimal productPrice) throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(SUBMIT_NEW_PRODUCT_SQL)
        ) {
            statement.setString(1, productName);
            statement.setBigDecimal(2, productPrice);
            statement.execute();
        }
    }

    /**
     * Updates an existing product row
     *
     * @param productId id of the product to update
     * @param productName new product name
     * @param productPrice new product price
     * @return true when a product row was updated
     * @throws SQLException when the procedure call fails
     */
    public static boolean editExistingProduct(int productId, String productName, BigDecimal productPrice)
            throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(EDIT_EXISTING_PRODUCT_SQL)
        ) {
            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setBigDecimal(3, productPrice);
            statement.registerOutParameter(4, Types.INTEGER);
            statement.execute();
            return statement.getInt(4) > 0;
        }
    }

    /**
     * Loads the current sum of all sale totals
     *
     * @return total sales amount across every sale row
     * @throws SQLException when the procedure call fails
     */
    public static BigDecimal getSalesTotal() throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(GET_SALES_TOTAL_SQL)
        ) {
            boolean hasResults = statement.execute();
            if (!hasResults) {
                return new BigDecimal("0.00");
            }

            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return new BigDecimal("0.00");
                }

                BigDecimal salesTotal = resultSet.getBigDecimal("salesTotal");
                return salesTotal == null ? new BigDecimal("0.00") : salesTotal;
            }
        }
    }

    /**
     * Inserts a new sale row for the current customer
     *
     * @param productId product id selected by the customer
     * @param userId id of the logged in customer
     * @param quantity quantity entered for the order
     * @return order total when the insert succeeds or null when no product is found
     * @throws SQLException when the procedure call fails
     */
    public static BigDecimal submitOrder(int productId, int userId, int quantity) throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(SUBMIT_ORDER_SQL)
        ) {
            statement.setInt(1, productId);
            statement.setInt(2, userId);
            statement.setInt(3, quantity);
            statement.registerOutParameter(4, Types.INTEGER);
            statement.registerOutParameter(5, Types.DECIMAL);
            statement.execute();

            if (statement.getInt(4) <= 0) {
                return null;
            }

            return statement.getBigDecimal(5);
        }
    }

    /**
     * Loads every order row for the current customer
     *
     * @param userId id of the logged in customer
     * @return all order rows returned for that customer
     * @throws SQLException when the procedure call fails
     */
    public static List<CustomerOrderRecord> viewCustomerOrders(int userId) throws SQLException {
        List<CustomerOrderRecord> orders = new ArrayList<>();

        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(VIEW_CUSTOMER_ORDERS_SQL)
        ) {
            statement.setInt(1, userId);

            boolean hasResults = statement.execute();
            if (!hasResults) {
                return orders;
            }

            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    orders.add(new CustomerOrderRecord(
                            resultSet.getInt("saleID"),
                            resultSet.getString("prodName"),
                            resultSet.getInt("qty"),
                            resultSet.getBigDecimal("total")
                    ));
                }
            }
        }

        return orders;
    }

    /**
     * Deletes one order row owned by the current customer
     *
     * @param saleId sale id selected for cancellation
     * @param userId id of the logged in customer
     * @return true when an order row was deleted
     * @throws SQLException when the procedure call fails
     */
    public static boolean cancelOrder(int saleId, int userId) throws SQLException {
        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(CANCEL_ORDER_SQL)
        ) {
            statement.setInt(1, saleId);
            statement.setInt(2, userId);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.execute();
            return statement.getInt(3) > 0;
        }
    }

    /**
     * Loads every product for the shared product list view
     *
     * @return all product rows returned by the procedure
     * @throws SQLException when the procedure call fails
     */
    public static List<ProductRecord> getAllProducts() throws SQLException {
        List<ProductRecord> products = new ArrayList<>();

        try (
                Connection connection = openConnection();
                CallableStatement statement = connection.prepareCall(GET_ALL_PRODUCTS_SQL)
        ) {
            boolean hasResults = statement.execute();
            if (!hasResults) {
                return products;
            }

            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    products.add(new ProductRecord(
                            resultSet.getInt("id"),
                            resultSet.getString("prodName"),
                            resultSet.getBigDecimal("price")
                    ));
                }
            }
        }

        return products;
    }
}