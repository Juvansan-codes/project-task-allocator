package com.taskallocator.exception;

/**
 * Custom exception representing authentication failures,
 * such as invalid credentials or unauthorized session state.
 */
public class AuthenticationException extends AppException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
