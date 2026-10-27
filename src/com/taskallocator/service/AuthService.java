package com.taskallocator.service;

import com.taskallocator.dao.UserDAO;
import com.taskallocator.exception.AuthenticationException;
import com.taskallocator.exception.DatabaseException;
import com.taskallocator.exception.ValidationException;
import com.taskallocator.model.User;
import com.taskallocator.model.UserRole;
import com.taskallocator.model.UserSession;
import com.taskallocator.util.PasswordUtil;

import java.util.regex.Pattern;

/**
 * Service orchestrating student registration, credential validation,
 * secure password verification, and session lifecycle management.
 * Keeps all business rules and validation independent of Swing GUI classes.
 */
public class AuthService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 64;

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new student account after thorough validation.
     *
     * @param fullName        student's full name
     * @param email           student's college email address
     * @param password        plaintext password array
     * @param confirmPassword confirmation password array
     * @param role            registered role (PROJECT_LEADER or TEAM_MEMBER)
     * @return the persisted User entity with assigned ID
     * @throws ValidationException if any input fails domain constraints or email is taken
     * @throws DatabaseException   if database write fails
     */
    public User register(String fullName, String email, char[] password, char[] confirmPassword, UserRole role)
            throws ValidationException, DatabaseException {

        try {
            validateRegistrationInputs(fullName, email, password, confirmPassword, role);

            String normalizedEmail = email.trim().toLowerCase();

            if (userDAO.emailExists(normalizedEmail)) {
                throw new ValidationException("An account with email '" + normalizedEmail + "' already exists.");
            }

            // Cryptographic salting and PBKDF2 hashing
            String salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(password, salt);

            User newUser = new User(fullName.trim(), normalizedEmail, passwordHash, salt, role);
            return userDAO.insert(newUser);

        } finally {
            PasswordUtil.wipe(password);
            PasswordUtil.wipe(confirmPassword);
        }
    }

    /**
     * Authenticates a user against stored salt and PBKDF2 hash.
     * Sets the active user in UserSession upon successful verification.
     *
     * @param email    user email
     * @param password user password array
     * @return authenticated User entity
     * @throws ValidationException     if email or password is empty
     * @throws AuthenticationException if credentials do not match (safe generic message)
     * @throws DatabaseException       if database error occurs
     */
    public User login(String email, char[] password)
            throws ValidationException, AuthenticationException, DatabaseException {

        try {
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("Email address is required.");
            }
            if (password == null || password.length == 0) {
                throw new ValidationException("Password is required.");
            }

            String normalizedEmail = email.trim().toLowerCase();
            User user = userDAO.findByEmail(normalizedEmail);

            // Generic error message prevents account enumeration
            if (user == null) {
                throw new AuthenticationException("Invalid email or password.");
            }

            boolean passwordMatches = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash());
            if (!passwordMatches) {
                throw new AuthenticationException("Invalid email or password.");
            }

            // Establish session context
            UserSession.setCurrentUser(user);
            return user;

        } finally {
            PasswordUtil.wipe(password);
        }
    }

    /**
     * Terminates the current active session.
     */
    public void logout() {
        UserSession.clear();
    }

    /**
     * Retrieves the current authenticated user from session.
     */
    public User getCurrentUser() {
        return UserSession.getCurrentUser();
    }

    /**
     * Checks if a user is currently authenticated.
     */
    public boolean isAuthenticated() {
        return UserSession.isLoggedIn();
    }

    /**
     * Validates all registration parameters against business constraints.
     */
    private void validateRegistrationInputs(String fullName, String email, char[] password,
                                           char[] confirmPassword, UserRole role) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        if (fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            throw new ValidationException("Full name must be between 2 and 100 characters.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is required.");
        }
        String cleanEmail = email.trim();
        if (cleanEmail.length() > 100) {
            throw new ValidationException("Email address cannot exceed 100 characters.");
        }
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("Please enter a valid email address (e.g., student@college.edu).");
        }

        if (password == null || password.length == 0) {
            throw new ValidationException("Password is required.");
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (password.length > MAX_PASSWORD_LENGTH) {
            throw new ValidationException("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters.");
        }

        if (confirmPassword == null || confirmPassword.length == 0) {
            throw new ValidationException("Please confirm your password.");
        }
        if (!passwordsMatch(password, confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        if (role == null) {
            throw new ValidationException("Please select a valid role (Project Leader or Team Member).");
        }
    }

    /**
     * Compares two password character arrays for equality.
     */
    private boolean passwordsMatch(char[] a, char[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
