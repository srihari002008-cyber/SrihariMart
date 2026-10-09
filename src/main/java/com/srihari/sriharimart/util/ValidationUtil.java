package com.srihari.sriharimart.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for validating user and business domain inputs.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Checks if string is not null and not blank.
     *
     * @param str Candidate string
     * @return true if non-null and contains non-whitespace characters
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Checks if email address has a valid format.
     *
     * @param email Candidate email
     * @return true if valid format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Checks if password meets minimum length requirement (at least 6 characters).
     *
     * @param password Candidate password
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        return isNotBlank(password) && password.length() >= 6;
    }

    /**
     * Checks if a monetary amount is positive and non-null.
     *
     * @param amount Monetary amount
     * @return true if amount > 0
     */
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if a quantity is greater than 0.
     *
     * @param qty Candidate quantity
     * @return true if qty > 0
     */
    public static boolean isPositiveQuantity(int qty) {
        return qty > 0;
    }

    /**
     * Checks if a stock quantity is non-negative (>= 0).
     *
     * @param qty Candidate stock quantity
     * @return true if qty >= 0
     */
    public static boolean isNonNegativeQuantity(int qty) {
        return qty >= 0;
    }
}
