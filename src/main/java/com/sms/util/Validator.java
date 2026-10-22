package com.sms.util;

import com.sms.exception.ValidationException;
import com.sms.model.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for validating student attributes against business rules.
 */
public class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    private static final Pattern ROLL_NUMBER_PATTERN =
            Pattern.compile("^[A-Za-z0-9\\-_]{2,20}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z\\s.'-]{2,50}$");

    private Validator() {
        // Prevent instantiation
    }

    /**
     * Validates an entire Student entity. Throws ValidationException if any field is invalid.
     */
    public static void validateStudent(Student student) {
        if (student == null) {
            throw new ValidationException("Student object cannot be null.");
        }

        List<String> errors = new ArrayList<>();

        if (!isValidRollNumber(student.getRollNumber())) {
            errors.add("Roll number must be 2-20 alphanumeric characters (hyphens and underscores allowed).");
        }

        if (!isValidName(student.getFirstName())) {
            errors.add("First name must contain 2-50 letters.");
        }

        if (!isValidName(student.getLastName())) {
            errors.add("Last name must contain 2-50 letters.");
        }

        if (!isValidEmail(student.getEmail())) {
            errors.add("Email must be in a valid format (e.g., student@example.com).");
        }

        if (!isValidPhone(student.getPhone())) {
            errors.add("Phone number must contain 10-15 digits (optional leading +).");
        }

        if (!isValidCourse(student.getCourse())) {
            errors.add("Course name cannot be blank and must be under 100 characters.");
        }

        if (!isValidGpa(student.getGpa())) {
            errors.add("GPA must be between 0.00 and 10.00.");
        }

        if (!isValidEnrollmentDate(student.getEnrollmentDate())) {
            errors.add("Enrollment date cannot be null or in the future.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("\n - ", errors));
        }
    }

    public static boolean isValidRollNumber(String rollNumber) {
        return rollNumber != null && ROLL_NUMBER_PATTERN.matcher(rollNumber.trim()).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleaned = phone.trim().replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidCourse(String course) {
        return course != null && !course.trim().isEmpty() && course.trim().length() <= 100;
    }

    public static boolean isValidGpa(Double gpa) {
        return gpa != null && gpa >= 0.0 && gpa <= 10.0;
    }

    public static boolean isValidEnrollmentDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
}
