# CampusHub – JDBC Concept Mapping Document

This document maps all database connectivity topics from **Unit 3: GUI and Database Connectivity (JDBC)** to their exact implementation in **CampusHub**.

---

## 1. JDBC Architecture in CampusHub

```
┌────────────────────────────────────────────────────────┐
│              Java Application (CampusHub)              │
│       (Swing GUI -> Service Layer -> DAO Layer)        │
└───────────────────────────┬────────────────────────────┘
                            │ Calls JDBC interfaces
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Java JDBC API                        │
│ (DriverManager, Connection, Statement, PreparedStatement, ResultSet)
└───────────────────────────┬────────────────────────────┘
                            │ Driver SPI interface
                            ▼
┌────────────────────────────────────────────────────────┐
│          MySQL Connector/J Driver (8.3.0)              │
│            (com.mysql.cj.jdbc.Driver)                  │
└───────────────────────────┬────────────────────────────┘
                            │ TCP/IP Socket (Port 3306)
                            ▼
┌────────────────────────────────────────────────────────┐
│                 MySQL Server Database                  │
│                     (campushub_db)                     │
└────────────────────────────────────────────────────────┘
```

---

## 2. JDBC Syllabus Topics & Implementation Table

| JDBC Syllabus Topic | Interface / Class Used | Source Code Location | Concrete Implementation in CampusHub |
| :--- | :--- | :--- | :--- |
| **Driver Registration** | `Class.forName()` | `DBConnection.java#L80` | `Class.forName("com.mysql.cj.jdbc.Driver");` loads MySQL 8.x Connector into JVM memory. |
| **Connection Establishment** | `DriverManager` | `DBConnection.java#L84` | `DriverManager.getConnection(url, user, password)` establishes TCP socket connection to MySQL. |
| **Connection Interface** | `java.sql.Connection` | `DBConnection.java` | Manages connection lifecycle, transaction auto-commit states, and cleanup. |
| **Statement Interface** | `java.sql.Statement` | `DatabaseInitializer.java#L29` | Executes static DDL queries (`CREATE TABLE IF NOT EXISTS`) and batch seed statements. |
| **PreparedStatement Interface** | `java.sql.PreparedStatement` | `StudentDAO.java#L26`, `BookDAO.java#L78` | Executes parameterized SQL statements using `?` placeholders. Completely prevents SQL Injection attacks. |
| **ResultSet Interface** | `java.sql.ResultSet` | `StudentDAO.java#L64`, `UserDAO.java#L32` | Cursor navigating returned rows via `while(rs.next())`, mapping columns (`rs.getString`, `rs.getInt`). |
| **Transaction Management** | `conn.setAutoCommit(false)`, `conn.commit()`, `conn.rollback()` | `BookDAO.java#L162`, `BookingDAO.java#L32` | Multi-step inventory checkout and room reservations are executed atomically. Rolls back on error. |
| **SQLException Handling** | `java.sql.SQLException` | All DAOs & `DBConnection` | Catches database errors, inspects error codes, logs issues, and recovers gracefully. |
| **Resource Management** | `AutoCloseable` / `try-with-resources` | All DAOs | `try (Connection conn = ...; PreparedStatement ps = ...)` guarantees all cursors, statements, and connections are closed. |

---

## 3. Demonstration of JDBC CRUD Operations

### 1. CREATE (INSERT)
- **File**: `src/main/java/com/campushub/dao/StudentDAO.java#insertStudent()`
- **Code**:
  ```java
  String sql = "INSERT INTO students (uid, full_name, email, phone, department, semester, section, enrollment_date, user_id) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
  try (Connection conn = DBConnection.getConnection();
       PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, s.getUid());
      ps.setString(2, s.getName());
      ps.setString(3, s.getEmail());
      ...
      ps.executeUpdate();
  }
  ```

### 2. READ (SELECT)
- **File**: `src/main/java/com/campushub/dao/StudentDAO.java#getAllStudents()`
- **Code**:
  ```java
  String sql = "SELECT * FROM students ORDER BY student_id ASC";
  try (Connection conn = DBConnection.getConnection();
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
          list.add(mapResultSetToStudent(rs));
      }
  }
  ```

### 3. UPDATE
- **File**: `src/main/java/com/campushub/dao/StudentDAO.java#updateStudent()`
- **Code**:
  ```java
  String sql = "UPDATE students SET full_name = ?, email = ?, phone = ?, department = ?, semester = ?, section = ? WHERE student_id = ?";
  try (Connection conn = DBConnection.getConnection();
       PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, s.getName());
      ...
      ps.setInt(7, s.getStudentId());
      ps.executeUpdate();
  }
  ```

### 4. DELETE
- **File**: `src/main/java/com/campushub/dao/StudentDAO.java#deleteStudent()`
- **Code**:
  ```java
  String sql = "DELETE FROM students WHERE student_id = ?";
  try (Connection conn = DBConnection.getConnection();
       PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, studentId);
      ps.executeUpdate();
  }
  ```

### 5. TRANSACTION (Atomicity & Rollback)
- **File**: `src/main/java/com/campushub/dao/BookDAO.java#issueBookTransaction()`
- **Code**:
  ```java
  conn.setAutoCommit(false); // 1. Start transaction
  try {
      // Step A: Decrement available inventory copy
      updateQuantityPs.executeUpdate();
      // Step B: Record checkout in issued_books table
      insertIssuePs.executeUpdate();

      conn.commit(); // 2. Commit transaction atomically
  } catch (Exception e) {
      conn.rollback(); // 3. Rollback if any step fails
      throw e;
  } finally {
      conn.setAutoCommit(true);
  }
  ```

---

## 4. How to Show Your Examiner

1. **Show Architecture & Driver**: Open `DBConnection.java` and explain `Class.forName("com.mysql.cj.jdbc.Driver")` and `DriverManager.getConnection()`.
2. **Show PreparedStatement**: Open `StudentDAO.java#insertStudent()` and explain how `?` parameter placeholders prevent SQL Injection.
3. **Show Transactions**: Open `BookDAO.java#issueBookTransaction()` and show `setAutoCommit(false)`, `commit()`, and `rollback()`.
4. **Show Live GUI Execution**:
   - In `StudentPanel`, click **"+ Add Student"**, enter details, and click Save.
   - Run MySQL query: `SELECT * FROM students;` in MySQL Workbench to prove live persistence.

