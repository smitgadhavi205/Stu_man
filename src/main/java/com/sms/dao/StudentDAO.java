package com.sms.dao;

import com.sms.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Student entity.
 */
public interface StudentDAO {

    /**
     * Inserts a new student record into MySQL and returns the student with generated primary key.
     */
    Student add(Student student);

    /**
     * Finds a student by primary key ID.
     */
    Optional<Student> findById(int id);

    /**
     * Finds a student by unique roll number.
     */
    Optional<Student> findByRollNumber(String rollNumber);

    /**
     * Retrieves all student records ordered by ID.
     */
    List<Student> findAll();

    /**
     * Searches students by keyword matching roll number, first name, last name, email, or course.
     */
    List<Student> search(String keyword);

    /**
     * Finds students belonging to a specific course.
     */
    List<Student> findByCourse(String course);

    /**
     * Updates an existing student record in MySQL.
     */
    boolean update(Student student);

    /**
     * Deletes a student by primary key ID.
     */
    boolean deleteById(int id);

    /**
     * Deletes a student by roll number.
     */
    boolean deleteByRollNumber(String rollNumber);

    /**
     * Checks if a roll number already exists, optionally excluding a student ID (for updates).
     */
    boolean existsByRollNumber(String rollNumber, Integer excludeId);

    /**
     * Checks if an email already exists, optionally excluding a student ID (for updates).
     */
    boolean existsByEmail(String email, Integer excludeId);

    /**
     * Returns total count of students.
     */
    int count();
}
