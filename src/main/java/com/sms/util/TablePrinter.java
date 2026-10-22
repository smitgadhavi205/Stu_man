package com.sms.util;

import com.sms.model.Student;

import java.util.List;

/**
 * Utility for rendering student lists in a neat ASCII table.
 */
public class TablePrinter {

    private TablePrinter() {
        // Prevent instantiation
    }

    public static void printStudents(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("\n[!] No student records found.\n");
            return;
        }

        String format = "| %-4s | %-12s | %-22s | %-26s | %-14s | %-20s | %-5s | %-12s |%n";
        String border = "+------+--------------+------------------------+----------------------------+----------------+----------------------+-------+--------------+";

        System.out.println(border);
        System.out.printf(format, "ID", "Roll No", "Full Name", "Email", "Phone", "Course", "GPA", "Enrolled");
        System.out.println(border);

        for (Student s : students) {
            System.out.printf(format,
                    s.getId() != null ? s.getId() : "-",
                    truncate(s.getRollNumber(), 12),
                    truncate(s.getFullName(), 22),
                    truncate(s.getEmail(), 26),
                    truncate(s.getPhone(), 14),
                    truncate(s.getCourse(), 20),
                    String.format("%.2f", s.getGpa()),
                    s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "-"
            );
        }
        System.out.println(border);
        System.out.printf("Total records: %d%n%n", students.size());
    }

    public static void printStudentCard(Student s) {
        if (s == null) {
            System.out.println("\n[!] Student is null.\n");
            return;
        }
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Student ID       : %-40s |%n", s.getId() != null ? s.getId() : "-");
        System.out.printf("| Roll Number      : %-40s |%n", s.getRollNumber());
        System.out.printf("| Full Name        : %-40s |%n", s.getFullName());
        System.out.printf("| Email Address    : %-40s |%n", s.getEmail());
        System.out.printf("| Phone Number     : %-40s |%n", s.getPhone());
        System.out.printf("| Enrolled Course  : %-40s |%n", s.getCourse());
        System.out.printf("| GPA              : %-40.2f |%n", s.getGpa());
        System.out.printf("| Enrollment Date  : %-40s |%n", s.getEnrollmentDate());
        System.out.println("+-------------------------------------------------------------+");
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
