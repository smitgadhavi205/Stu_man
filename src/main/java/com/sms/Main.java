package com.sms;

import com.sms.config.DBConnection;
import com.sms.service.StudentService;
import com.sms.view.StudentConsoleView;

import java.sql.SQLException;

/**
 * Main application entrypoint.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        // Attempt automatic schema migration/initialization
        try {
            DBConnection.initializeDatabase();
            System.out.println("[✓] Connected to MySQL database successfully. Table verified.");
        } catch (SQLException e) {
            System.err.println("[!] Warning: Could not connect to MySQL during startup (" + e.getMessage() + ")");
            System.err.println("    Please ensure WAMP/MySQL is running. You can check option [7] in the menu for diagnostics.\n");
        }

        StudentService studentService = new StudentService();
        StudentConsoleView view = new StudentConsoleView(studentService);
        view.start();
    }

    private static void printBanner() {
        System.out.println("""
            ===============================================================
               ____  _             _            _     ____  __  __ ____  
              / ___|| |_ _   _  __| | ___ _ __ | |_  / ___||  \\/  / ___| 
              \\___ \\| __| | | |/ _` |/ _ \\ '_ \\| __| \\___ \\| |\\/| \\___ \\ 
               ___) | |_| |_| | (_| |  __/ | | | |_   ___) | |  | |___) |
              |____/ \\__|\\__,_|\\__,_|\\___|_| |_|\\__| |____/|_|  |_|____/ 
                                                                         
                      STUDENT RECORD MANAGEMENT SYSTEM v1.0
                         (Java 17+ • JDBC • MySQL)
            ===============================================================
        """);
    }
}
