import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/*
CGS 2545, SP 26
by Zayne Dockery

Terminal-based inventory app using MySQL Schema w/ JDBC

GitHub Repo: https://github.com/zaynedoc/project4
*/

/**
 * Runs the menu-driven inventory application
 */
public class InventoryApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static SessionContext currentSession;

    /**
     * Typical main method for terminal app
     *
     * @param args unused command line args
     */
    public static void main(String[] args) {
        System.out.println("Attempting connection to project4...");

        if (!DatabaseManager.testConnection()) {
            System.out.println("Database connection failed.");
            return;
        }

        System.out.println("Database connection success.");
        runWelcomeMenu();
    }

    /**
     * Runs the entry menu before login
     */
    private static void runWelcomeMenu() {
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println();
            System.out.println("Welcome. Please choose an option.");
            System.out.println("1 = Register New User");
            System.out.println("2 = Login with Existing Account");
            System.out.println("0 = Exit");
            System.out.print(">> ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    handleRegisterNewUser();
                    break;
                case "2":
                    handleLoginWithCreds();
                    break;
                case "0":
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }

        System.out.println("Goodbye.");
    }

    /**
     * Prompts for registration details and creates a user
     */
    private static void handleRegisterNewUser() {
        String username = promptForRequiredText("What is your desired username?");
        String password = promptForRequiredText("What is your desired password?");

        try {
            DatabaseManager.registerNewUser(username, password);
            System.out.println("Account created successfully.");
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 1062) {
                System.out.println("That username already exists.");
                return;
            }

            System.out.println("Unable to create the account.");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Prompts for login credentials and routes by role
     */
    private static void handleLoginWithCreds() {
        String username = promptForRequiredText("What is your username?");
        String password = promptForRequiredText("What is your password?");

        try {
            currentSession = DatabaseManager.loginWithCreds(username, password);

            if (currentSession == null) {
                System.out.println("Login failed. Invalid username or password.");
                return;
            }

            if (currentSession.isAdmin()) {
                runAdminMenu();
            } else {
                runCustomerMenu();
            }
        } catch (SQLException exception) {
            System.out.println("Unable to complete login.");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Runs the admin menu after a successful admin login
     */
    private static void runAdminMenu() {
        boolean keepRunning = true;

        while (keepRunning && currentSession != null) {
            System.out.println();
            System.out.println("Welcome, " + currentSession.getUsername() + ". What do you want to do?");
            System.out.println("1 = Add New Product");
            System.out.println("2 = Edit Existing Product");
            System.out.println("3 = See All Products");
            System.out.println("4 = View Sales Total");
            System.out.println("5 = Logout");
            System.out.print(">> ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    handleSubmitNewProduct();
                    break;
                case "2":
                    handleEditExistingProduct();
                    break;
                case "4":
                    System.out.println("Placeholder for future implementation.");
                    break;
                case "3":
                    handleGetAllProducts();
                    break;
                case "5":
                    currentSession = null;
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
    }

    /**
     * Runs the customer menu after a successful customer login
     */
    private static void runCustomerMenu() {
        boolean keepRunning = true;

        while (keepRunning && currentSession != null) {
            System.out.println();
            System.out.println("Welcome, " + currentSession.getUsername() + ". What do you want to do?");
            System.out.println("1 = Submit New Order");
            System.out.println("2 = Cancel Existing Order");
            System.out.println("3 = View My Orders");
            System.out.println("4 = See All Products");
            System.out.println("5 = Logout");
            System.out.print(">> ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                case "2":
                case "3":
                    System.out.println("Placeholder for future implementation.");
                    break;
                case "4":
                    handleGetAllProducts();
                    break;
                case "5":
                    currentSession = null;
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
    }

    /**
     * Prompts for product details and inserts a product
     */
    private static void handleSubmitNewProduct() {
        String productName = promptForRequiredText("What is the product name?");
        BigDecimal productPrice = promptForPrice("What is the product price?");

        try {
            DatabaseManager.submitNewProduct(productName, productPrice);
            System.out.println("Product added successfully.");
        } catch (SQLException exception) {
            System.out.println("Unable to add the product.");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Prompts for updated product data and saves it by id
     */
    private static void handleEditExistingProduct() {
        int productId = promptForPositiveInt("What product ID do you want to edit?");
        String productName = promptForRequiredText("What is the product name?");
        BigDecimal productPrice = promptForPrice("What is the product price?");

        try {
            boolean wasUpdated = DatabaseManager.editExistingProduct(productId, productName, productPrice);

            if (wasUpdated) {
                System.out.println("Product updated successfully.");
            } else {
                System.out.println("No product with that ID was found.");
            }
        } catch (SQLException exception) {
            System.out.println("Unable to update the product.");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Loads and prints the shared product list
     */
    private static void handleGetAllProducts() {
        try {
            List<ProductRecord> products = DatabaseManager.getAllProducts();

            System.out.println("Now displaying all products.");
            if (products.isEmpty()) {
                System.out.println("No products found.");
                return;
            }

            for (ProductRecord product : products) {
                System.out.println(product.id() + " " + product.productName() + " " + product.price());
            }
        } catch (SQLException exception) {
            System.out.println("Unable to load products.");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Prompts until a non-empty value is entered
     *
     * @param prompt text shown before reading input
     * @return non-empty trimmed text
     */
    private static String promptForRequiredText(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.print(">> ");

            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be blank.");
        }
    }

    /**
     * Prompts until a valid positive price is entered
     *
     * @param prompt text shown before reading input
     * @return positive price value
     */
    private static BigDecimal promptForPrice(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.print(">> ");

            String value = SCANNER.nextLine().trim();

            try {
                BigDecimal price = new BigDecimal(value);
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    return price;
                }
            } catch (NumberFormatException exception) {
            }

            System.out.println("Please enter a valid price greater than 0.");
        }
    }

    /**
     * Prompts until a positive integer is entered
     *
     * @param prompt text shown before reading input
     * @return positive integer value
     */
    private static int promptForPositiveInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.print(">> ");

            String value = SCANNER.nextLine().trim();

            try {
                int parsedValue = Integer.parseInt(value);
                if (parsedValue > 0) {
                    return parsedValue;
                }
            } catch (NumberFormatException exception) {
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}