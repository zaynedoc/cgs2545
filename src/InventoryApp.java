public class InventoryApp {
    public static void main(String[] args) {
        System.out.println("Attempting connection to project4 db...");

        if (DatabaseManager.testConnection()) {
            System.out.println("Database connection success");
        } else {
            System.out.println("Database connection failed");
        }
    }
}