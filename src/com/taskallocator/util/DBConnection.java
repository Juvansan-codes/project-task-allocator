package com.taskallocator.util;

import com.taskallocator.exception.DatabaseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages JDBC connections to the MySQL database.
 * Supports configurable connection properties from config/db.properties,
 * environment variables, or fallback local development defaults.
 */
public final class DBConnection {

    private static final String DEFAULT_URL = 
        "jdbc:mysql://localhost:3306/task_allocator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String url;
    private static String user;
    private static String password;

    static {
        loadConfiguration();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL Connector/J driver class not found in classpath.");
        }
    }

    private DBConnection() {
        // Prevent instantiation of utility class
    }

    /**
     * Loads database credentials with progressive fallback:
     * 1. config/db.properties file (local file system)
     * 2. Classpath resource db.properties
     * 3. Environment variables (DB_URL, DB_USER, DB_PASSWORD)
     * 4. Safe default local development credentials
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
        } else {
            try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in != null) {
                    props.load(in);
                }
            } catch (IOException ignored) {
            }
        }

        url = System.getenv("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = props.getProperty("db.url", DEFAULT_URL);
        }

        user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = props.getProperty("db.user", DEFAULT_USER);
        }

        password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = props.getProperty("db.password", DEFAULT_PASSWORD);
        }
    }

    /**
     * Obtains a new JDBC connection from DriverManager.
     *
     * @return a valid active Connection object
     * @throws DatabaseException if connection fails
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL database at: " + url + 
                " (User: " + user + "). Ensure MySQL is running and credentials in config/db.properties are correct.", e);
        }
    }

    /**
     * Safely closes one or more JDBC/I/O resources.
     *
     * @param resources AutoCloseable resources (Connection, Statement, ResultSet, etc.)
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
     * Quick test utility to verify connectivity.
     *
     * @return true if connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (DatabaseException e) {
            System.err.println("[DBConnection Test Failed] " + e.getMessage());
            return false;
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
