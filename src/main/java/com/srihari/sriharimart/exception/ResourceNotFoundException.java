package com.srihari.sriharimart.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
