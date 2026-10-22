package com.sms.util;

import com.sms.exception.ValidationException;
import com.sms.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private Student createValidStudent() {
        return new Student(
                "STU-001",
                "Alice",
                "Johnson",
                "alice.johnson@example.com",
                "9876543210",
                "Computer Science",
                3.85,
                LocalDate.now()
        );
    }

    @Test
    @DisplayName("Should pass validation for a well-formed student")
    void testValidStudent() {
        Student s = createValidStudent();
        assertDoesNotThrow(() -> Validator.validateStudent(s));
    }

    @Test
    @DisplayName("Should fail when student is null")
    void testNullStudent() {
        assertThrows(ValidationException.class, () -> Validator.validateStudent(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@domain.com", "test@domain"})
    @DisplayName("Should reject invalid email formats")
    void testInvalidEmails(String email) {
        Student s = createValidStudent();
        s.setEmail(email);
        assertThrows(ValidationException.class, () -> Validator.validateStudent(s));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "phone-number", "123456789012345678"})
    @DisplayName("Should reject invalid phone numbers")
    void testInvalidPhoneNumbers(String phone) {
        Student s = createValidStudent();
        s.setPhone(phone);
        assertThrows(ValidationException.class, () -> Validator.validateStudent(s));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 10.1, 15.0})
    @DisplayName("Should reject GPA outside 0.0 - 10.0")
    void testInvalidGpa(double gpa) {
        Student s = createValidStudent();
        s.setGpa(gpa);
        assertThrows(ValidationException.class, () -> Validator.validateStudent(s));
    }

    @Test
    @DisplayName("Should reject future enrollment date")
    void testFutureEnrollmentDate() {
        Student s = createValidStudent();
        s.setEnrollmentDate(LocalDate.now().plusDays(10));
        assertThrows(ValidationException.class, () -> Validator.validateStudent(s));
    }
}
