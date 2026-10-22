package com.sms.exception;

/**
 * Thrown when student input data fails validation rules.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
