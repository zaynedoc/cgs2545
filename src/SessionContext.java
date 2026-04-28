/**
 * Stores login data for current user session
 */
public final class SessionContext {
    private final int userId;
    private final String username;
    private final int role;

    /**
     * Creates session data from successful login
     *
     * @param userId database id of the logged in user
     * @param username username of the logged in user
     * @param role role value returned by the users table
     */
    public SessionContext(int userId, String username, int role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    /**
     * Returns the logged in user id
     *
     * @return user id from the users table
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the logged in username
     *
     * @return username from the users table
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the stored role value
     *
     * @return role from the users table
     */
    public int getRole() {
        return role;
    }

    /**
     * Checks whether the current session belongs to the admin user
     *
     * @return true when the role value is 1
     */
    public boolean isAdmin() {
        return role == 1;
    }
}