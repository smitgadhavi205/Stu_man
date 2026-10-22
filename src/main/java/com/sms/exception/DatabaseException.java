package com.sms.exception;

/**
 * Wraps checked SQLExceptions into unchecked runtime exceptions for clean service boundaries.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
