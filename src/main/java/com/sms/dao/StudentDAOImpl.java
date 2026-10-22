package com.sms.dao;

import com.sms.config.DBConnection;
import com.sms.exception.DatabaseException;
import com.sms.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of StudentDAO using PreparedStatement and DBConnection.
 */
public class StudentDAOImpl implements StudentDAO {

    @Override
    public Student add(Student student) {
        String sql = """
            INSERT INTO students (roll_number, first_name, last_name, email, phone, course, gpa, enrollment_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getRollNumber());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getCourse());
            stmt.setDouble(7, student.getGpa());
            stmt.setDate(8, Date.valueOf(student.getEnrollmentDate()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to add student, no rows affected.", null);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                }
            }

            return student;
        } catch (SQLException e) {
            throw new DatabaseException("Database error adding student with roll number: " + student.getRollNumber(), e);
        }
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding student by ID: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Student> findByRollNumber(String rollNumber) {
        String sql = "SELECT * FROM students WHERE roll_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding student by roll number: " + rollNumber, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY id ASC";
        List<Student> students = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving all students.", e);
        }

        return students;
    }

    @Override
    public List<Student> search(String keyword) {
        String sql = """
            SELECT * FROM students 
            WHERE roll_number LIKE ? 
               OR first_name LIKE ? 
               OR last_name LIKE ? 
               OR email LIKE ? 
               OR course LIKE ?
            ORDER BY id ASC
        """;
        List<Student> students = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error searching students with keyword: " + keyword, e);
        }

        return students;
    }

    @Override
    public List<Student> findByCourse(String course) {
        String sql = "SELECT * FROM students WHERE course = ? ORDER BY id ASC";
        List<Student> students = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding students by course: " + course, e);
        }

        return students;
    }

    @Override
    public boolean update(Student student) {
        String sql = """
            UPDATE students 
            SET roll_number = ?, first_name = ?, last_name = ?, email = ?, 
                phone = ?, course = ?, gpa = ?, enrollment_date = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getRollNumber());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getCourse());
            stmt.setDouble(7, student.getGpa());
            stmt.setDate(8, Date.valueOf(student.getEnrollmentDate()));
            stmt.setInt(9, student.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error updating student ID: " + student.getId(), e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting student by ID: " + id, e);
        }
    }

    @Override
    public boolean deleteByRollNumber(String rollNumber) {
        String sql = "DELETE FROM students WHERE roll_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting student by roll number: " + rollNumber, e);
        }
    }

    @Override
    public boolean existsByRollNumber(String rollNumber, Integer excludeId) {
        String sql = excludeId == null 
            ? "SELECT 1 FROM students WHERE roll_number = ? LIMIT 1"
            : "SELECT 1 FROM students WHERE roll_number = ? AND id != ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNumber);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error checking existence of roll number: " + rollNumber, e);
        }
    }

    @Override
    public boolean existsByEmail(String email, Integer excludeId) {
        String sql = excludeId == null 
            ? "SELECT 1 FROM students WHERE email = ? LIMIT 1"
            : "SELECT 1 FROM students WHERE email = ? AND id != ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error checking existence of email: " + email, e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM students";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving student count.", e);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("roll_number"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("course"),
            rs.getDouble("gpa"),
            rs.getDate("enrollment_date").toLocalDate()
        );
    }
}
