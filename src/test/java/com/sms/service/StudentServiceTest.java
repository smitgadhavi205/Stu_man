package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.exception.StudentNotFoundException;
import com.sms.exception.ValidationException;
import com.sms.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentDAO studentDAO;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student(
                1,
                "STU-100",
                "John",
                "Doe",
                "john.doe@example.com",
                "1234567890",
                "Computer Science",
                3.75,
                LocalDate.now()
        );
    }

    @Test
    @DisplayName("Should successfully add student when valid and unique")
    void testAddStudentSuccess() {
        when(studentDAO.existsByRollNumber("STU-100", null)).thenReturn(false);
        when(studentDAO.existsByEmail("john.doe@example.com", null)).thenReturn(false);
        when(studentDAO.add(any(Student.class))).thenReturn(testStudent);

        Student result = studentService.addStudent(testStudent);
        assertNotNull(result);
        assertEquals("STU-100", result.getRollNumber());
        verify(studentDAO).add(testStudent);
    }

    @Test
    @DisplayName("Should throw ValidationException on duplicate roll number")
    void testAddStudentDuplicateRollNumber() {
        when(studentDAO.existsByRollNumber("STU-100", null)).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> studentService.addStudent(testStudent));
        assertTrue(ex.getMessage().contains("roll number"));
        verify(studentDAO, never()).add(any());
    }

    @Test
    @DisplayName("Should throw ValidationException on duplicate email")
    void testAddStudentDuplicateEmail() {
        when(studentDAO.existsByRollNumber("STU-100", null)).thenReturn(false);
        when(studentDAO.existsByEmail("john.doe@example.com", null)).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> studentService.addStudent(testStudent));
        assertTrue(ex.getMessage().contains("email"));
        verify(studentDAO, never()).add(any());
    }

    @Test
    @DisplayName("Should retrieve student by ID if exists")
    void testGetStudentByIdFound() {
        when(studentDAO.findById(1)).thenReturn(Optional.of(testStudent));

        Student found = studentService.getStudentById(1);
        assertEquals("John", found.getFirstName());
    }

    @Test
    @DisplayName("Should throw StudentNotFoundException when ID does not exist")
    void testGetStudentByIdNotFound() {
        when(studentDAO.findById(99)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(99));
    }

    @Test
    @DisplayName("Should compute correct statistics")
    void testGetStatistics() {
        Student s1 = new Student("S1", "Alice", "A", "a@test.com", "1234567890", "CS", 3.5, LocalDate.now());
        Student s2 = new Student("S2", "Bob", "B", "b@test.com", "1234567891", "CS", 4.0, LocalDate.now());
        Student s3 = new Student("S3", "Charlie", "C", "c@test.com", "1234567892", "EE", 3.0, LocalDate.now());

        when(studentDAO.findAll()).thenReturn(List.of(s1, s2, s3));

        StudentService.StudentStatistics stats = studentService.getStatistics();
        assertEquals(3, stats.totalStudents());
        assertEquals(3.5, stats.averageGpa(), 0.01);
        assertEquals("Bob", stats.topStudent().getFirstName());
        assertEquals(2, stats.studentsPerCourse().get("CS"));
        assertEquals(1, stats.studentsPerCourse().get("EE"));
    }
}
