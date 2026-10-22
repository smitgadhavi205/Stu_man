package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.dao.StudentDAOImpl;
import com.sms.exception.StudentNotFoundException;
import com.sms.exception.ValidationException;
import com.sms.model.Student;
import com.sms.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer coordinating business rules, validation, and data persistence.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this(new StudentDAOImpl());
    }

    // Dependency injection constructor (useful for unit testing with mocks)
    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = Objects.requireNonNull(studentDAO, "StudentDAO cannot be null");
    }

    /**
     * Adds a new student record after performing validation and duplicate checks.
     */
    public Student addStudent(Student student) {
        Validator.validateStudent(student);

        if (studentDAO.existsByRollNumber(student.getRollNumber(), null)) {
            throw new ValidationException("A student with roll number '" + student.getRollNumber() + "' already exists.");
        }

        if (studentDAO.existsByEmail(student.getEmail(), null)) {
            throw new ValidationException("A student with email '" + student.getEmail() + "' already exists.");
        }

        return studentDAO.add(student);
    }

    /**
     * Retrieves student by ID. Throws StudentNotFoundException if not found.
     */
    public Student getStudentById(int id) {
        return studentDAO.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
    }

    /**
     * Retrieves student by Roll Number. Throws StudentNotFoundException if not found.
     */
    public Student getStudentByRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new ValidationException("Roll number cannot be empty.");
        }
        return studentDAO.findByRollNumber(rollNumber.trim())
                .orElseThrow(() -> new StudentNotFoundException("Student with roll number '" + rollNumber + "' not found."));
    }

    /**
     * Retrieves all student records.
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Searches students by keyword matching roll number, name, email, or course.
     */
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentDAO.search(keyword.trim());
    }

    /**
     * Retrieves students enrolled in a specific course.
     */
    public List<Student> getStudentsByCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            throw new ValidationException("Course name cannot be empty.");
        }
        return studentDAO.findByCourse(course.trim());
    }

    /**
     * Updates an existing student record after validating fields and uniqueness.
     */
    public boolean updateStudent(Student student) {
        if (student.getId() == null) {
            throw new ValidationException("Cannot update student without a valid ID.");
        }

        // Check if student exists
        studentDAO.findById(student.getId())
                .orElseThrow(() -> new StudentNotFoundException("Cannot update: Student with ID " + student.getId() + " does not exist."));

        // Validate data
        Validator.validateStudent(student);

        // Check roll number uniqueness for other students
        if (studentDAO.existsByRollNumber(student.getRollNumber(), student.getId())) {
            throw new ValidationException("Another student with roll number '" + student.getRollNumber() + "' already exists.");
        }

        // Check email uniqueness for other students
        if (studentDAO.existsByEmail(student.getEmail(), student.getId())) {
            throw new ValidationException("Another student with email '" + student.getEmail() + "' already exists.");
        }

        return studentDAO.update(student);
    }

    /**
     * Deletes a student by primary key ID.
     */
    public boolean deleteStudentById(int id) {
        studentDAO.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Cannot delete: Student with ID " + id + " does not exist."));
        return studentDAO.deleteById(id);
    }

    /**
     * Deletes a student by roll number.
     */
    public boolean deleteStudentByRollNumber(String rollNumber) {
        Student student = studentDAO.findByRollNumber(rollNumber)
                .orElseThrow(() -> new StudentNotFoundException("Cannot delete: Student with roll number '" + rollNumber + "' does not exist."));
        return studentDAO.deleteByRollNumber(student.getRollNumber());
    }

    /**
     * Computes statistics summary across all students.
     */
    public StudentStatistics getStatistics() {
        List<Student> students = studentDAO.findAll();
        if (students.isEmpty()) {
            return new StudentStatistics(0, 0.0, null, Collections.emptyMap());
        }

        double avgGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);

        Student topStudent = students.stream()
                .max(Comparator.comparingDouble(Student::getGpa))
                .orElse(null);

        Map<String, Long> courseCounts = students.stream()
                .collect(Collectors.groupingBy(Student::getCourse, Collectors.counting()));

        return new StudentStatistics(students.size(), avgGpa, topStudent, courseCounts);
    }

    /**
     * Record holding calculated statistical metrics.
     */
    public record StudentStatistics(
            int totalStudents,
            double averageGpa,
            Student topStudent,
            Map<String, Long> studentsPerCourse
    ) {}
}
