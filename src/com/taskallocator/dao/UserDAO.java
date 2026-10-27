package com.taskallocator.dao;

import com.taskallocator.exception.DatabaseException;
import com.taskallocator.model.User;
import com.taskallocator.model.UserRole;
import com.taskallocator.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User persistence.
 * Implements CRUD and authentication query operations using JDBC PreparedStatements.
 * Encapsulates all SQL logic away from business services and Swing GUI frames.
 */
public class UserDAO {

    /**
     * Finds a user by their registered email address (case-insensitive).
     *
     * @param email user email address
     * @return User object if found, or null if no user matches
     * @throws DatabaseException if a database access error occurs
     */
    public User findByEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT user_id, full_name, email, password_hash, salt, role, created_at " +
                     "FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email: " + email, e);
        }
    }

    /**
     * Checks whether an email address is already registered in the system.
     *
     * @param email email to check
     * @return true if email exists, false otherwise
     * @throws DatabaseException if a database access error occurs
     */
    public boolean emailExists(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to verify email existence for: " + email, e);
        }
    }

    /**
     * Inserts a new user record into the database.
     * Sets the auto-generated primary key ID and database created_at timestamp on the User object.
     *
     * @param user the User entity to persist
     * @return the persisted User with generated ID populated
     * @throws DatabaseException if insertion fails
     */
    public User insert(User user) throws DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        String sql = "INSERT INTO users (full_name, email, password_hash, salt, role) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFullName().trim());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getRole().getDbValue());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained.");
                }
            }

            // Reload created_at from database to ensure exact timestamp consistency
            User saved = findById(user.getId());
            if (saved != null) {
                user.setCreatedAt(saved.getCreatedAt());
            }

            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert user: " + user.getEmail(), e);
        }
    }

    /**
     * Finds a user by their primary key user_id.
     *
     * @param userId user identifier
     * @return User object if found, null otherwise
     * @throws DatabaseException if database access fails
     */
    public User findById(int userId) throws DatabaseException {
        String sql = "SELECT user_id, full_name, email, password_hash, salt, role, created_at " +
                     "FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by ID: " + userId, e);
        }
    }

    /**
     * Retrieves all registered users in the system.
     *
     * @return list of User entities
     * @throws DatabaseException if retrieval fails
     */
    public List<User> findAll() throws DatabaseException {
        String sql = "SELECT user_id, full_name, email, password_hash, salt, role, created_at " +
                     "FROM users ORDER BY full_name ASC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user list.", e);
        }
    }

    /**
     * Maps a ResultSet row to a User object.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                UserRole.fromDbValue(rs.getString("role")),
                rs.getTimestamp("created_at")
        );
    }
}
