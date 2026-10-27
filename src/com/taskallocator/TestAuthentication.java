package com.taskallocator;

import com.taskallocator.dao.UserDAO;
import com.taskallocator.exception.AuthenticationException;
import com.taskallocator.exception.DatabaseException;
import com.taskallocator.exception.ValidationException;
import com.taskallocator.model.User;
import com.taskallocator.model.UserRole;
import com.taskallocator.model.UserSession;
import com.taskallocator.service.AuthService;
import com.taskallocator.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Automated test suite for Member 1: Authentication and Student Management.
 * Tests registration, validation, PBKDF2 password hashing, database persistence,
 * login verification, security boundaries, and session context against live MySQL.
 */
public class TestAuthentication {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println(" PROJECT TASK & COLLABORATION ALLOCATOR — TEST SUITE");
        System.out.println(" Member 1: Authentication & Student Management");
        System.out.println("=================================================================\n");

        AuthService authService = new AuthService();
        UserDAO userDAO = new UserDAO();

        // Unique test run timestamp to keep test emails clean across repeated runs
        long timestamp = System.currentTimeMillis();
        String leaderEmail = "alice.leader." + timestamp + "@college.edu";
        String memberEmail = "bob.member." + timestamp + "@college.edu";
        String validPassword = "SecurePassword123!";

        // 1. Database Connectivity
        test("1. Database connection is active and responsive", () -> {
            assertTrue(DBConnection.testConnection(), "DBConnection.testConnection() returned false");
        });

        // 2. Input Validation - Empty Full Name
        test("2. Registration rejects empty full name", () -> {
            assertThrows(ValidationException.class, () -> {
                authService.register("", leaderEmail, validPassword.toCharArray(),
                        validPassword.toCharArray(), UserRole.PROJECT_LEADER);
            });
        });

        // 3. Input Validation - Invalid Email Format
        test("3. Registration rejects malformed email address", () -> {
            assertThrows(ValidationException.class, () -> {
                authService.register("Alice Leader", "not-an-email", validPassword.toCharArray(),
                        validPassword.toCharArray(), UserRole.PROJECT_LEADER);
            });
        });

        // 4. Input Validation - Short Password (< 6 chars)
        test("4. Registration rejects short password (< 6 chars)", () -> {
            assertThrows(ValidationException.class, () -> {
                authService.register("Alice Leader", leaderEmail, "123".toCharArray(),
                        "123".toCharArray(), UserRole.PROJECT_LEADER);
            });
        });

        // 5. Input Validation - Mismatched Password Confirmation
        test("5. Registration rejects mismatched password confirmation", () -> {
            assertThrows(ValidationException.class, () -> {
                authService.register("Alice Leader", leaderEmail, "Password123!".toCharArray(),
                        "DifferentPass123!".toCharArray(), UserRole.PROJECT_LEADER);
            });
        });

        // 6. Successful Registration of Project Leader
        test("6. Successful registration of Project Leader with PBKDF2 hash", () -> {
            User registered = authService.register("Alice Leader", leaderEmail,
                    validPassword.toCharArray(), validPassword.toCharArray(), UserRole.PROJECT_LEADER);

            assertNotNull(registered, "Registered user must not be null");
            assertTrue(registered.getId() > 0, "Generated user ID must be positive");
            assertEquals("Alice Leader", registered.getFullName(), "Full name mismatch");
            assertEquals(leaderEmail, registered.getEmail(), "Email mismatch");
            assertEquals(UserRole.PROJECT_LEADER, registered.getRole(), "Role must be PROJECT_LEADER");
            assertTrue(registered.isProjectLeader(), "isProjectLeader() must return true");
        });

        // 7. Successful Registration of Team Member
        test("7. Successful registration of Team Member", () -> {
            User member = authService.register("Bob Member", memberEmail,
                    validPassword.toCharArray(), validPassword.toCharArray(), UserRole.TEAM_MEMBER);

            assertNotNull(member, "Registered member must not be null");
            assertTrue(member.getId() > 0, "Generated member ID must be positive");
            assertEquals(UserRole.TEAM_MEMBER, member.getRole(), "Role must be TEAM_MEMBER");
            assertTrue(member.isTeamMember(), "isTeamMember() must return true");
        });

        // 8. Duplicate Email Registration Rejection
        test("8. Registration rejects duplicate email address", () -> {
            assertThrows(ValidationException.class, () -> {
                authService.register("Alice Duplicate", leaderEmail,
                        validPassword.toCharArray(), validPassword.toCharArray(), UserRole.PROJECT_LEADER);
            });
        });

        // 9. Verify Password Hashes in MySQL (Never Plaintext)
        test("9. MySQL stores PBKDF2 hash and salt, never plaintext password", () -> {
            String sql = "SELECT password_hash, salt FROM users WHERE email = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, leaderEmail);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "User record must exist in MySQL");
                    String storedHash = rs.getString("password_hash");
                    String storedSalt = rs.getString("salt");

                    assertNotNull(storedHash, "Stored hash must not be null");
                    assertNotNull(storedSalt, "Stored salt must not be null");
                    assertEquals(64, storedHash.length(), "PBKDF2 SHA-256 hash must be 64 hex characters (256 bits)");
                    assertEquals(32, storedSalt.length(), "Random salt must be 32 hex characters (16 bytes)");
                    assertFalse(storedHash.contains(validPassword), "Stored hash must never contain the plaintext password!");
                }
            }
        });

        // 10. Successful Login with Correct Credentials
        test("10. Successful login establishes active session", () -> {
            User loggedIn = authService.login(leaderEmail, validPassword.toCharArray());

            assertNotNull(loggedIn, "Logged in user must not be null");
            assertEquals(leaderEmail, loggedIn.getEmail(), "Authenticated email mismatch");
            assertTrue(UserSession.isLoggedIn(), "UserSession.isLoggedIn() must be true");
            assertEquals(loggedIn.getId(), UserSession.getCurrentUser().getId(), "Session user ID mismatch");
            assertTrue(UserSession.isLeader(), "UserSession.isLeader() must be true for Alice");
        });

        // 11. Login Rejection on Incorrect Password
        test("11. Login rejects incorrect password with generic message", () -> {
            AuthenticationException ex = assertThrows(AuthenticationException.class, () -> {
                authService.login(leaderEmail, "WrongPassword999!".toCharArray());
            });
            assertEquals("Invalid email or password.", ex.getMessage(), "Must use generic error message");
        });

        // 12. Login Rejection on Unknown Email
        test("12. Login rejects non-existent email with generic message", () -> {
            AuthenticationException ex = assertThrows(AuthenticationException.class, () -> {
                authService.login("nonexistent." + timestamp + "@college.edu", validPassword.toCharArray());
            });
            assertEquals("Invalid email or password.", ex.getMessage(), "Must use generic error message");
        });

        // 13. Logout Clears Session Context
        test("13. Logout successfully clears active session context", () -> {
            authService.logout();
            assertFalse(UserSession.isLoggedIn(), "UserSession.isLoggedIn() must be false after logout");
            assertNull(UserSession.getCurrentUser(), "UserSession.getCurrentUser() must be null after logout");
        });

        // 14. UserDAO Direct Lookup Verification
        test("14. UserDAO findByEmail and findById retrieve accurate user state", () -> {
            User found = userDAO.findByEmail(leaderEmail);
            assertNotNull(found, "UserDAO.findByEmail must find registered user");
            assertEquals("Alice Leader", found.getFullName(), "Full name mismatch");

            User byId = userDAO.findById(found.getId());
            assertNotNull(byId, "UserDAO.findById must find registered user");
            assertEquals(found.getId(), byId.getId(), "User IDs must match");
        });

        // Summary Report
        System.out.println("\n=================================================================");
        System.out.println(" TEST RESULTS SUMMARY");
        System.out.println(" Total Tests Run : " + testsRun);
        System.out.println(" Passed          : " + testsPassed);
        System.out.println(" Failed          : " + testsFailed);
        System.out.println(" Status          : " + (testsFailed == 0 ? "ALL TESTS PASSED [100%]" : "FAILURES DETECTED"));
        System.out.println("=================================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // --- Lightweight test assertion helpers ---

    @FunctionalInterface
    interface TestBlock {
        void run() throws Throwable;
    }

    private static void test(String name, TestBlock block) {
        testsRun++;
        try {
            block.run();
            testsPassed++;
            System.out.println("  [PASS] " + name);
        } catch (Throwable t) {
            testsFailed++;
            System.err.println("  [FAIL] " + name);
            System.err.println("         Reason: " + t.getMessage());
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) throw new AssertionError(message);
    }

    private static void assertNull(Object obj, String message) {
        if (obj != null) throw new AssertionError(message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
    }

    private static <T extends Throwable> T assertThrows(Class<T> expectedType, TestBlock block) {
        try {
            block.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return expectedType.cast(t);
            }
            throw new AssertionError("Expected exception " + expectedType.getSimpleName() + 
                    " but caught " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        throw new AssertionError("Expected exception " + expectedType.getSimpleName() + " was not thrown.");
    }
}
