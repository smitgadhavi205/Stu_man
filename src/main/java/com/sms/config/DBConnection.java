package com.sms.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database connection manager using Singleton-style configuration loaded from db.properties.
 */
public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/student_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String url;
    private static String username;
    private static String password;

    static {
        loadProperties();
    }

    private DBConnection() {
        // Utility class, prevent instantiation
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                url = props.getProperty("db.url", DEFAULT_URL);
                username = props.getProperty("db.user", DEFAULT_USER);
                password = props.getProperty("db.password", DEFAULT_PASSWORD);
                String driver = props.getProperty("db.driver", DEFAULT_DRIVER);
                Class.forName(driver);
            } else {
                url = DEFAULT_URL;
                username = DEFAULT_USER;
                password = DEFAULT_PASSWORD;
                Class.forName(DEFAULT_DRIVER);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load db.properties, using defaults. (" + e.getMessage() + ")");
            url = DEFAULT_URL;
            username = DEFAULT_USER;
            password = DEFAULT_PASSWORD;
        }
    }

    /**
     * Establishes and returns a new JDBC Connection.
     * The caller is responsible for closing the connection (typically using try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Automatically creates the database table if it doesn't already exist.
     */
    public static void initializeDatabase() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                id INT AUTO_INCREMENT PRIMARY KEY,
                roll_number VARCHAR(20) NOT NULL UNIQUE,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                phone VARCHAR(20) NOT NULL,
                course VARCHAR(100) NOT NULL,
                gpa DECIMAL(3, 2) NOT NULL,
                enrollment_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_roll (roll_number),
                INDEX idx_email (email),
                INDEX idx_course (course)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Tests the database connection and returns a diagnostic message.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Getters for configuration inspection
    public static String getUrl() { return url; }
    public static String getUsername() { return username; }
}
