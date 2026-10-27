package com.taskallocator.model;

/**
 * Manages the current authenticated user's session context in memory.
 * Provides thread-safe access to session state across GUI windows and services.
 */
public final class UserSession {

    private static volatile User currentUser;

    private UserSession() {
        // Prevent instantiation
    }

    /**
     * Sets the active authenticated user for this application run.
     *
     * @param user the authenticated User entity
     */
    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @return current User or null if no user is authenticated
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether an active user is logged in.
     *
     * @return true if an active user session exists, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the active user is a Project Leader.
     *
     * @return true if logged in as a leader, false otherwise
     */
    public static boolean isLeader() {
        return currentUser != null && currentUser.isProjectLeader();
    }

    /**
     * Clears the current user session upon logout.
     */
    public static synchronized void clear() {
        currentUser = null;
    }
}
