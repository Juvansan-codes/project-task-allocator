package com.taskallocator.exception;

/**
 * Custom exception representing database access or connectivity failures.
 */
public class DatabaseException extends AppException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
