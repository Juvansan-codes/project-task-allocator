package com.taskallocator.exception;

/**
 * Custom exception representing domain validation or input format failures.
 */
public class ValidationException extends AppException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
