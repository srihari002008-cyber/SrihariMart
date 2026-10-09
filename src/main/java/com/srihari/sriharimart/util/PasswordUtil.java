package com.srihari.sriharimart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a plaintext password using BCrypt with a secure salt.
     *
     * @param plainPassword Plaintext password string
     * @return Hashed password string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies a candidate plaintext password against an existing BCrypt hash.
     *
     * @param candidatePassword Plaintext candidate password
     * @param hashedPassword Expected BCrypt hash
     * @return true if candidate matches hash, false otherwise
     */
    public static boolean checkPassword(String candidatePassword, String hashedPassword) {
        if (candidatePassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(candidatePassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
