package com.taskallocator.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Cryptographic utility providing secure password hashing and verification.
 * Implements PBKDF2 with HMAC-SHA256 and unique random salting using standard Java libraries.
 * Plaintext passwords are never stored, logged, or exposed.
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Generates a cryptographically secure random salt.
     *
     * @return 16-byte random salt encoded as a 32-character hexadecimal string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Computes the PBKDF2 hash of a password using a hex-encoded salt.
     *
     * @param password the password character array
     * @param saltHex  the 32-character hexadecimal salt string
     * @return 64-character hexadecimal representation of the 256-bit derived key hash
     */
    public static String hashPassword(char[] password, String saltHex) {
        if (password == null || saltHex == null) {
            throw new IllegalArgumentException("Password and salt cannot be null.");
        }
        byte[] salt = hexToBytes(saltHex);
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cryptographic hashing algorithm unavailable: " + ALGORITHM, e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Verifies an entered password against a stored salt and hash.
     * Uses constant-time comparison to prevent timing side-channel attacks.
     *
     * @param enteredPassword the password entered by the user
     * @param storedSaltHex   the hexadecimal salt stored in the database
     * @param storedHashHex   the hexadecimal hash stored in the database
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(char[] enteredPassword, String storedSaltHex, String storedHashHex) {
        if (enteredPassword == null || storedSaltHex == null || storedHashHex == null) {
            return false;
        }
        try {
            byte[] storedHash = hexToBytes(storedHashHex);
            byte[] salt = hexToBytes(storedSaltHex);

            PBEKeySpec spec = new PBEKeySpec(enteredPassword, salt, ITERATION_COUNT, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] computedHash = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();

            // Constant-time byte comparison prevents timing attack
            return MessageDigest.isEqual(storedHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converts a byte array to a lowercase hexadecimal string.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Converts a hexadecimal string back to a byte array.
     */
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length: " + len);
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Clears sensitive password data in memory.
     */
    public static void wipe(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}
