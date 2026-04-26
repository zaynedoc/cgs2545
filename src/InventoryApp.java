import java.sql.SQLException;
import java.util.Scanner;

public class InventoryApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    // stores the logged-in user after a successful credential check
    private static SessionContext currentSession;

    public static void main(String[] args) {
        System.out.println("Attempting connection to project4...");

        if (!DatabaseManager.testConnection()) {
            System.out.println("Database connection failed.");
            return;
        }

        System.out.println("Database connection success.");
        runWelcomeMenu();
    }

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

    private static void handleLoginWithCreds() {
        String username = promptForRequiredText("What is your username?");
        String password = promptForRequiredText("What is your password?");

        try {
            currentSession = DatabaseManager.loginWithCreds(username, password);

            if (currentSession == null) {
                System.out.println("Login failed. Invalid username or password.");
                return;
            }

            System.out.println("Welcome, " + currentSession.getUsername() + ".");

            if (currentSession.isAdmin()) {
                System.out.println("Welcome, admin. What do you want to do?");
            } else {
                System.out.println("Invalid credentials, please try again.");
            }
        } catch (SQLException exception) {
            System.out.println("Unable to complete login.");
            System.out.println(exception.getMessage());
        }
    }

    private static String promptForRequiredText(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.print(">> ");

            // reuse one prompt helper so username and password validation stay consistent
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be blank.");
        }
    }
}