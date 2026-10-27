package com.taskallocator.model;

/**
 * Enumeration representing the user's registered role in the system.
 * The application has exactly two roles: PROJECT_LEADER and TEAM_MEMBER.
 * There is no Admin role in accordance with the project specification.
 */
public enum UserRole {
    PROJECT_LEADER("Project Leader", "PROJECT_LEADER"),
    TEAM_MEMBER("Team Member", "TEAM_MEMBER");

    private final String displayName;
    private final String dbValue;

    UserRole(String displayName, String dbValue) {
        this.displayName = displayName;
        this.dbValue = dbValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database ENUM string into a UserRole safely.
     *
     * @param dbString the database string representation
     * @return the matching UserRole enum constant
     * @throws IllegalArgumentException if the value is unrecognized
     */
    public static UserRole fromDbValue(String dbString) {
        if (dbString == null) {
            throw new IllegalArgumentException("Database role value cannot be null.");
        }
        for (UserRole role : values()) {
            if (role.dbValue.equalsIgnoreCase(dbString.trim()) || 
                role.name().equalsIgnoreCase(dbString.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role in database: " + dbString);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
