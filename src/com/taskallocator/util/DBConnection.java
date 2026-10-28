package com.taskallocator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * =====================================================================
 * Member 2 Responsibility: Database Foundation & JDBC Configuration
 * =====================================================================
 * DBConnection provides a centralized way to obtain JDBC connections
 * to the MySQL database. It reads database credentials from config/db.properties
 * and returns standard java.sql.Connection objects.
 */
public final class DBConnection {

    private static final String DEFAULT_URL = 
        "jdbc:mysql://localhost:3306/task_allocator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String url;
    private static String user;
    private static String password;

    // Static block executes once when the class is loaded into memory
    static {
        loadConfiguration();
        try {
            // Register MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL Connector/J driver class not found in classpath.");
        }
    }

    private DBConnection() {
        // Private constructor prevents direct instantiation of utility class
    }

    /**
     * Reads database connection settings from config/db.properties.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        File configFile = new File("config/db.properties");

        if (configFile.exists() && configFile.canRead()) {
            try (InputStream in = new FileInputStream(configFile)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("[DBConnection] Warning: Could not read config/db.properties: " + e.getMessage());
            }
        }

        url = props.getProperty("db.url", DEFAULT_URL);
        user = props.getProperty("db.user", DEFAULT_USER);
        password = props.getProperty("db.password", DEFAULT_PASSWORD);
    }

    /**
     * Establishes and returns a live connection to the MySQL database.
     * Uses standard java.sql.SQLException so callers can handle database errors easily.
     *
     * @return an active java.sql.Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Helper method to safely close JDBC resources (Connection, Statement, ResultSet).
     *
     * @param resources AutoCloseable objects to close
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) return;
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Quick test method to verify database connectivity.
     *
     * @return true if connected successfully, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection Test Failed] " + e.getMessage());
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }
}
