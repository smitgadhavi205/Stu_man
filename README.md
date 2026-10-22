# Student Record Management System (Java, JDBC, MySQL)

A robust, console-based Student Record Management System built using **Java 17+**, **JDBC**, and **MySQL**, adhering to a clean **Layered Architecture** (Model, DAO, Service, View).

---

## 🚀 Key Features

- **Full CRUD Operations**:
  - **Add Student**: Insert new records with complete input validation.
  - **Search Student**: Search by primary ID, Roll Number, Course, or general keyword across names, email, and course.
  - **Update Student**: Modify student details with pre-populated values and conflict checking.
  - **Delete Student**: Remove records by Roll Number with confirmation safety checks.
  - **Analytics & Summary**: Total enrollments, average GPA, top student, and course distribution.
- **Layered Architecture**:
  - `model`: Clean domain entities (POJOs) with encapsulation.
  - `dao`: Data Access Object layer utilizing JDBC `PreparedStatement` (prevents SQL injection).
  - `service`: Business logic layer enforcing duplicate prevention and business constraints.
  - `view`: User interface with ASCII formatted tabular output and interactive prompts.
  - `util`: Input validators with regex checks and custom exception handling.
  - `config`: Externalized database configuration (`db.properties`) with auto-table initialization.
- **Security & Reliability**:
  - `PreparedStatement` with parameterized queries (`?`).
  - Automatic resource management via `try-with-resources`.
  - Comprehensive unit test coverage with JUnit 5 & Mockito.

---

## 📁 Project Structure

```text
stu_man/
├── pom.xml                                   # Maven configuration
├── README.md                                 # Documentation
├── src/
│   ├── main/
│   │   ├── java/com/sms/
│   │   │   ├── Main.java                     # Application entry point & banner
│   │   │   ├── config/
│   │   │   │   └── DBConnection.java         # JDBC Connection manager & schema init
│   │   │   ├── dao/
│   │   │   │   ├── StudentDAO.java           # DAO Interface
│   │   │   │   └── StudentDAOImpl.java       # JDBC PreparedStatement Implementation
│   │   │   ├── exception/
│   │   │   │   ├── DatabaseException.java    # Database runtime wrapper
│   │   │   │   ├── StudentNotFoundException.java
│   │   │   │   └── ValidationException.java  # Input validation errors
│   │   │   ├── model/
│   │   │   │   └── Student.java              # Student POJO
│   │   │   ├── service/
│   │   │   │   └── StudentService.java       # Business logic & uniqueness checks
│   │   │   ├── util/
│   │   │   │   ├── TablePrinter.java         # Formatted ASCII tables & cards
│   │   │   │   └── Validator.java            # Regex & range validations
│   │   │   └── view/
│   │   │       └── StudentConsoleView.java   # Interactive CLI menus & flows
│   │   └── resources/
│   │       ├── db.properties                 # MySQL connection properties
│   │       └── schema.sql                    # MySQL Table DDL
│   └── test/java/com/sms/
│       ├── service/
│       │   └── StudentServiceTest.java       # Mockito unit tests for Service
│       └── util/
│           └── ValidatorTest.java            # Parameterized tests for Validator
```

---

## 🛠️ Prerequisites

1. **Java JDK 17** or higher (`java -version`)
2. **Apache Maven 3.8+** (`mvn -v`)
3. **MySQL Server** (e.g. WampServer, XAMPP, or standalone MySQL on port `3306`)

---

## ⚙️ Database Configuration

Edit `src/main/resources/db.properties` if your MySQL username/password differs from the defaults:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=
```

> **Note**: The application automatically creates the `student_db` database and `students` table on startup!

---

## 📦 How to Build and Run

### 1. Compile and Run with Maven
```bash
mvn compile exec:java -Dexec.mainClass="com.sms.Main"
```

### 2. Package into an Executable Fat JAR
```bash
mvn clean package
```
This generates a self-contained JAR inside `target/student-management-system-1.0.0.jar`.

### 3. Run the JAR
```bash
java -jar target/student-management-system-1.0.0.jar
```

### 4. Run Unit Tests
```bash
mvn test
```
