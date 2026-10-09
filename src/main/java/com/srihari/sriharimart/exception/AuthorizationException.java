package com.srihari.sriharimart.exception;

/**
 * Exception thrown when an authenticated user lacks required privileges.
 */
public class AuthorizationException extends AppException {
    public AuthorizationException(String message) {
        super(message);
    }
}
