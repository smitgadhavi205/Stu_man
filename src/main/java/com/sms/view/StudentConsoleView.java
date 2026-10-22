package com.sms.view;

import com.sms.config.DBConnection;
import com.sms.exception.DatabaseException;
import com.sms.exception.StudentNotFoundException;
import com.sms.exception.ValidationException;
import com.sms.model.Student;
import com.sms.service.StudentService;
import com.sms.util.TablePrinter;
import com.sms.util.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console user interface handling interactive menus, user prompts, input validation, and rendering.
 */
public class StudentConsoleView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StudentService studentService;
    private final Scanner scanner;

    public StudentConsoleView(StudentService studentService) {
        this.studentService = studentService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleAddStudent();
                case "2" -> handleViewAllStudents();
                case "3" -> handleSearchStudents();
                case "4" -> handleUpdateStudent();
                case "5" -> handleDeleteStudent();
                case "6" -> handleViewStatistics();
                case "7" -> handleTestConnection();
                case "8" -> {
                    System.out.println("\nThank you for using the Student Record Management System. Goodbye!\n");
                    running = false;
                }
                default -> System.out.println("\n[!] Invalid selection. Please enter a number between 1 and 8.\n");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("===============================================================");
        System.out.println("            STUDENT RECORD MANAGEMENT SYSTEM                   ");
        System.out.println("===============================================================");
        System.out.println(" [1] Add New Student");
        System.out.println(" [2] View All Students");
        System.out.println(" [3] Search Student Record");
        System.out.println(" [4] Update Student Record");
        System.out.println(" [5] Delete Student Record");
        System.out.println(" [6] View Student Statistics");
        System.out.println(" [7] Test Database Connection");
        System.out.println(" [8] Exit");
        System.out.println("===============================================================");
        System.out.print("Select an option (1-8): ");
    }

    private void handleAddStudent() {
        System.out.println("\n--- [ Add New Student Record ] ---");
        System.out.println("(Type 'cancel' at any prompt to abort)\n");

        String roll = promptValid("Roll Number (e.g. STU-101): ", Validator::isValidRollNumber,
                "Invalid roll number! Must be 2-20 alphanumeric characters (hyphens allowed).");
        if (roll == null) return;

        String firstName = promptValid("First Name: ", Validator::isValidName,
                "Invalid first name! Must be 2-50 letters.");
        if (firstName == null) return;

        String lastName = promptValid("Last Name: ", Validator::isValidName,
                "Invalid last name! Must be 2-50 letters.");
        if (lastName == null) return;

        String email = promptValid("Email Address: ", Validator::isValidEmail,
                "Invalid email format! Example: alice@example.com");
        if (email == null) return;

        String phone = promptValid("Phone Number (10-15 digits): ", Validator::isValidPhone,
                "Invalid phone number! Must be 10-15 digits.");
        if (phone == null) return;

        String course = promptValid("Course (e.g. Computer Science): ", Validator::isValidCourse,
                "Course cannot be empty and must be <= 100 characters.");
        if (course == null) return;

        Double gpa = promptGpa();
        if (gpa == null) return;

        LocalDate enrollmentDate = promptEnrollmentDate();
        if (enrollmentDate == null) return;

        Student student = new Student(roll, firstName, lastName, email, phone, course, gpa, enrollmentDate);

        try {
            Student saved = studentService.addStudent(student);
            System.out.println("\n[OK] Student successfully added with ID: " + saved.getId() + "\n");
            TablePrinter.printStudentCard(saved);
        } catch (ValidationException e) {
            System.out.println("\n[X] Validation Error: " + e.getMessage() + "\n");
        } catch (DatabaseException e) {
            System.out.println("\n[X] Database Error: " + e.getMessage() + "\n");
        }
    }

    private void handleViewAllStudents() {
        System.out.println("\n--- [ All Registered Students ] ---\n");
        try {
            List<Student> students = studentService.getAllStudents();
            TablePrinter.printStudents(students);
        } catch (DatabaseException e) {
            System.out.println("\n[X] Error retrieving students: " + e.getMessage() + "\n");
        }
    }

    private void handleSearchStudents() {
        System.out.println("\n--- [ Search Students ] ---");
        System.out.println(" [1] Search by ID");
        System.out.println(" [2] Search by Roll Number");
        System.out.println(" [3] General Search (Keyword across name, email, roll, course)");
        System.out.println(" [4] Filter by Course");
        System.out.print("Choose search method (1-4): ");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter Student ID: ");
                    String idStr = scanner.nextLine().trim();
                    try {
                        int id = Integer.parseInt(idStr);
                        Student s = studentService.getStudentById(id);
                        System.out.println("\n[OK] Student Record Found:\n");
                        TablePrinter.printStudentCard(s);
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid ID format. Must be an integer.\n");
                    }
                }
                case "2" -> {
                    System.out.print("Enter Roll Number: ");
                    String roll = scanner.nextLine().trim();
                    Student s = studentService.getStudentByRollNumber(roll);
                    System.out.println("\n[OK] Student Record Found:\n");
                    TablePrinter.printStudentCard(s);
                }
                case "3" -> {
                    System.out.print("Enter search keyword: ");
                    String keyword = scanner.nextLine().trim();
                    List<Student> results = studentService.searchStudents(keyword);
                    System.out.println("\nSearch results for: '" + keyword + "'\n");
                    TablePrinter.printStudents(results);
                }
                case "4" -> {
                    System.out.print("Enter Course name: ");
                    String course = scanner.nextLine().trim();
                    List<Student> results = studentService.getStudentsByCourse(course);
                    System.out.println("\nStudents enrolled in course: '" + course + "'\n");
                    TablePrinter.printStudents(results);
                }
                default -> System.out.println("[!] Invalid search choice.\n");
            }
        } catch (StudentNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("\n[X] Error during search: " + e.getMessage() + "\n");
        }
    }

    private void handleUpdateStudent() {
        System.out.println("\n--- [ Update Student Record ] ---");
        System.out.print("Enter Student Roll Number to update: ");
        String roll = scanner.nextLine().trim();

        Student existing;
        try {
            existing = studentService.getStudentByRollNumber(roll);
        } catch (StudentNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage() + "\n");
            return;
        }

        System.out.println("\nCurrent Student Details:");
        TablePrinter.printStudentCard(existing);
        System.out.println("(Press Enter to keep current value, or type new value)");

        System.out.print("Roll Number [" + existing.getRollNumber() + "]: ");
        String newRoll = scanner.nextLine().trim();
        if (!newRoll.isEmpty()) existing.setRollNumber(newRoll);

        System.out.print("First Name [" + existing.getFirstName() + "]: ");
        String newFirst = scanner.nextLine().trim();
        if (!newFirst.isEmpty()) existing.setFirstName(newFirst);

        System.out.print("Last Name [" + existing.getLastName() + "]: ");
        String newLast = scanner.nextLine().trim();
        if (!newLast.isEmpty()) existing.setLastName(newLast);

        System.out.print("Email [" + existing.getEmail() + "]: ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) existing.setEmail(newEmail);

        System.out.print("Phone [" + existing.getPhone() + "]: ");
        String newPhone = scanner.nextLine().trim();
        if (!newPhone.isEmpty()) existing.setPhone(newPhone);

        System.out.print("Course [" + existing.getCourse() + "]: ");
        String newCourse = scanner.nextLine().trim();
        if (!newCourse.isEmpty()) existing.setCourse(newCourse);

        System.out.print("GPA [" + existing.getGpa() + "]: ");
        String newGpa = scanner.nextLine().trim();
        if (!newGpa.isEmpty()) {
            try {
                existing.setGpa(Double.parseDouble(newGpa));
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid GPA value, keeping original: " + existing.getGpa());
            }
        }

        try {
            boolean updated = studentService.updateStudent(existing);
            if (updated) {
                System.out.println("\n[OK] Student record updated successfully!\n");
                TablePrinter.printStudentCard(existing);
            } else {
                System.out.println("\n[!] No changes made.\n");
            }
        } catch (ValidationException e) {
            System.out.println("\n[X] Validation Error: " + e.getMessage() + "\n");
        } catch (DatabaseException e) {
            System.out.println("\n[X] Database Error: " + e.getMessage() + "\n");
        }
    }

    private void handleDeleteStudent() {
        System.out.println("\n--- [ Delete Student Record ] ---");
        System.out.print("Enter Student Roll Number to delete: ");
        String roll = scanner.nextLine().trim();

        Student student;
        try {
            student = studentService.getStudentByRollNumber(roll);
        } catch (StudentNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage() + "\n");
            return;
        }

        System.out.println("\nRecord to be deleted:");
        TablePrinter.printStudentCard(student);

        System.out.print("Are you sure you want to PERMANENTLY delete this record? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            try {
                boolean deleted = studentService.deleteStudentByRollNumber(roll);
                if (deleted) {
                    System.out.println("\n[OK] Student record successfully deleted.\n");
                } else {
                    System.out.println("\n[!] Could not delete record.\n");
                }
            } catch (DatabaseException e) {
                System.out.println("\n[X] Database Error: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("\n[i] Deletion cancelled.\n");
        }
    }

    private void handleViewStatistics() {
        System.out.println("\n--- [ Student Records Statistics & Analytics ] ---\n");
        try {
            StudentService.StudentStatistics stats = studentService.getStatistics();
            System.out.println("+-------------------------------------------------------------+");
            System.out.printf("| Total Enrolled Students : %-33d |%n", stats.totalStudents());
            System.out.printf("| Average GPA             : %-33.2f |%n", stats.averageGpa());
            if (stats.topStudent() != null) {
                System.out.printf("| Top Student             : %-33s |%n",
                        stats.topStudent().getFullName() + " (GPA " + stats.topStudent().getGpa() + ")");
            }
            System.out.println("+-------------------------------------------------------------+");
            System.out.println("| Enrollment Breakdown by Course:                             |");
            if (stats.studentsPerCourse().isEmpty()) {
                System.out.println("|  (No course data available)                                 |");
            } else {
                stats.studentsPerCourse().forEach((course, count) ->
                        System.out.printf("|   * %-30s : %4d students |%n", course, count));
            }
            System.out.println("+-------------------------------------------------------------+\n");
        } catch (DatabaseException e) {
            System.out.println("\n[X] Database Error: " + e.getMessage() + "\n");
        }
    }

    private void handleTestConnection() {
        System.out.println("\n--- [ Database Connection Diagnostic ] ---");
        System.out.println("Target URL : " + DBConnection.getUrl());
        System.out.println("User       : " + DBConnection.getUsername());
        System.out.print("Testing connection... ");

        boolean ok = DBConnection.testConnection();
        if (ok) {
            System.out.println("[ SUCCESS ] MySQL is reachable and connected!\n");
        } else {
            System.out.println("[ FAILED ] Cannot connect to MySQL.");
            System.out.println("Please make sure:");
            System.out.println(" 1. WAMP / MySQL service is running.");
            System.out.println(" 2. Port 3306 is open.");
            System.out.println(" 3. Credentials in src/main/resources/db.properties match your setup.\n");
        }
    }

    // Helper prompt loops with validation
    private String promptValid(String prompt, java.util.function.Predicate<String> validator, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                System.out.println("[i] Operation aborted.");
                return null;
            }
            if (validator.test(input)) {
                return input;
            }
            System.out.println(" [!] " + errorMsg + " (or type 'cancel' to abort)");
        }
    }

    private Double promptGpa() {
        while (true) {
            System.out.print("GPA (0.00 - 10.00): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                System.out.println("[i] Operation aborted.");
                return null;
            }
            try {
                double gpa = Double.parseDouble(input);
                if (Validator.isValidGpa(gpa)) {
                    return gpa;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println(" [!] GPA must be a valid number between 0.00 and 10.00.");
        }
    }

    private LocalDate promptEnrollmentDate() {
        while (true) {
            System.out.print("Enrollment Date (yyyy-MM-dd) [Leave blank for today]: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                System.out.println("[i] Operation aborted.");
                return null;
            }
            if (input.isEmpty()) {
                return LocalDate.now();
            }
            try {
                LocalDate date = LocalDate.parse(input, DATE_FORMAT);
                if (Validator.isValidEnrollmentDate(date)) {
                    return date;
                }
                System.out.println(" [!] Enrollment date cannot be in the future.");
            } catch (DateTimeParseException e) {
                System.out.println(" [!] Invalid date format! Please use yyyy-MM-dd (e.g. 2024-09-01).");
            }
        }
    }
}
