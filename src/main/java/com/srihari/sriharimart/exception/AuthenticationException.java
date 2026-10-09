package com.srihari.sriharimart.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message);
    }
}
