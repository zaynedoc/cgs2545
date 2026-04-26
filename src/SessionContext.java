public final class SessionContext {
    // holds the user data returned by the login stored procedure
    private final int userId;
    private final String username;
    private final int role;

    public SessionContext(int userId, String username, int role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getRole() {
        return role;
    }

    // role 1 is the seeded admin account in the schema script
    public boolean isAdmin() {
        return role == 1;
    }
}