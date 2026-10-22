package com.sms.exception;

/**
 * Thrown when an operation is performed on a student ID or roll number that does not exist.
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
