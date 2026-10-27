package com.taskallocator.exception;

/**
 * Base custom checked exception for the Project Task & Collaboration Allocator.
 * Demonstrates inheritance and user-defined exception handling in Java.
 */
public class AppException extends Exception {
    
    public AppException(String message) {
        super(message);
    }
    
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
