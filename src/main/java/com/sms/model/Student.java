package com.sms.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Student Model (POJO) representing a student entity in the database.
 */
public class Student {
    private Integer id;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String course;
    private Double gpa;
    private LocalDate enrollmentDate;

    // Default constructor
    public Student() {
    }

    // Constructor without id (used when creating a new record before MySQL assigns an auto-increment ID)
    public Student(String rollNumber, String firstName, String lastName,
                   String email, String phone, String course, Double gpa, LocalDate enrollmentDate) {
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.gpa = gpa;
        this.enrollmentDate = enrollmentDate;
    }

    // Constructor with id (used when fetching existing records from the database)
    public Student(Integer id, String rollNumber, String firstName, String lastName,
                   String email, String phone, String course, Double gpa, LocalDate enrollmentDate) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.gpa = gpa;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) || Objects.equals(rollNumber, student.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rollNumber);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", rollNumber='" + rollNumber + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", course='" + course + '\'' +
                ", gpa=" + gpa +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }
}
