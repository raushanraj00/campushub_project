package com.campushub.database;

import com.campushub.services.AuthService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * UNIT 3: JDBC STATEMENTS & DDL EXECUTION
 * Automatically sets up database tables and baseline demo data if they do not exist.
 * Demonstrates:
 * - java.sql.Statement for static DDL execution (CREATE TABLE)
 * - java.sql.PreparedStatement for parameterized seed insertion
 * - java.sql.ResultSet for checking metadata and table existence
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("[DatabaseInitializer] Skipping initialization: No DB connection.");
                return false;
            }

            try (Statement stmt = conn.createStatement()) {
                // 1. Users Table
                stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                        + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "username VARCHAR(50) NOT NULL UNIQUE,"
                        + "password_hash VARCHAR(256) NOT NULL,"
                        + "salt VARCHAR(64) NOT NULL,"
                        + "role ENUM('ADMIN', 'STUDENT', 'FACULTY') NOT NULL,"
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ") ENGINE=InnoDB");

                // 2. Students Table
                stmt.execute("CREATE TABLE IF NOT EXISTS students ("
                        + "student_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "uid VARCHAR(20) NOT NULL UNIQUE,"
                        + "full_name VARCHAR(100) NOT NULL,"
                        + "email VARCHAR(100) NOT NULL UNIQUE,"
                        + "phone VARCHAR(20),"
                        + "department VARCHAR(50) NOT NULL,"
                        + "semester INT NOT NULL DEFAULT 1,"
                        + "section VARCHAR(10) DEFAULT 'A',"
                        + "enrollment_date DATE,"
                        + "user_id INT NULL,"
                        + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL"
                        + ") ENGINE=InnoDB");

                // 3. Faculty Table
                stmt.execute("CREATE TABLE IF NOT EXISTS faculty ("
                        + "faculty_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "emp_code VARCHAR(20) NOT NULL UNIQUE,"
                        + "full_name VARCHAR(100) NOT NULL,"
                        + "email VARCHAR(100) NOT NULL UNIQUE,"
                        + "phone VARCHAR(20),"
                        + "department VARCHAR(50) NOT NULL,"
                        + "designation VARCHAR(50) NOT NULL,"
                        + "user_id INT NULL,"
                        + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL"
                        + ") ENGINE=InnoDB");

                // 4. Books Table
                stmt.execute("CREATE TABLE IF NOT EXISTS books ("
                        + "book_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "title VARCHAR(150) NOT NULL,"
                        + "author VARCHAR(100) NOT NULL,"
                        + "isbn VARCHAR(25) NOT NULL UNIQUE,"
                        + "category VARCHAR(50) NOT NULL,"
                        + "total_quantity INT NOT NULL DEFAULT 1,"
                        + "available_quantity INT NOT NULL DEFAULT 1"
                        + ") ENGINE=InnoDB");

                // 5. Rooms Table
                stmt.execute("CREATE TABLE IF NOT EXISTS rooms ("
                        + "room_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "room_name VARCHAR(50) NOT NULL UNIQUE,"
                        + "room_type ENUM('CLASSROOM', 'LAB', 'SEMINAR_HALL') NOT NULL,"
                        + "building VARCHAR(50) NOT NULL,"
                        + "capacity INT NOT NULL,"
                        + "is_available BOOLEAN NOT NULL DEFAULT TRUE"
                        + ") ENGINE=InnoDB");

                // 6. Bookings Table
                stmt.execute("CREATE TABLE IF NOT EXISTS bookings ("
                        + "booking_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "user_id INT NOT NULL,"
                        + "room_id INT NOT NULL,"
                        + "start_time DATETIME NOT NULL,"
                        + "end_time DATETIME NOT NULL,"
                        + "purpose VARCHAR(255) NOT NULL,"
                        + "status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'CONFIRMED',"
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                        + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,"
                        + "FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE"
                        + ") ENGINE=InnoDB");

                // 7. Issued Books Table
                stmt.execute("CREATE TABLE IF NOT EXISTS issued_books ("
                        + "issue_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "student_id INT NOT NULL,"
                        + "book_id INT NOT NULL,"
                        + "issue_date DATE NOT NULL,"
                        + "due_date DATE NOT NULL,"
                        + "return_date DATE NULL,"
                        + "fine_amount DOUBLE NOT NULL DEFAULT 0.0,"
                        + "status ENUM('ISSUED', 'RETURNED') NOT NULL DEFAULT 'ISSUED',"
                        + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,"
                        + "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE"
                        + ") ENGINE=InnoDB");

                // 8. Activity Logs Table
                stmt.execute("CREATE TABLE IF NOT EXISTS activity_logs ("
                        + "log_id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "action VARCHAR(100) NOT NULL,"
                        + "details TEXT,"
                        + "username VARCHAR(50),"
                        + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ") ENGINE=InnoDB");
            }

            // Seed default users if users table is empty
            seedInitialUsers(conn);
            seedInitialInventory(conn);

            System.out.println("[DatabaseInitializer] Database verified and initialized successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Database init warning: " + e.getMessage());
            return false;
        }
    }

    private static void seedInitialUsers(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert Admin: admin / Admin@123
                AuthService.SaltAndHash adminHash = AuthService.generateHash("Admin@123");
                insertUser(conn, "admin", adminHash.getHash(), adminHash.getSalt(), "ADMIN");

                // Insert Student: student1 / Student@123
                AuthService.SaltAndHash studentHash = AuthService.generateHash("Student@123");
                int studentUserId = insertUser(conn, "student1", studentHash.getHash(), studentHash.getSalt(), "STUDENT");

                // Insert Faculty: faculty1 / Faculty@123
                AuthService.SaltAndHash facultyHash = AuthService.generateHash("Faculty@123");
                int facultyUserId = insertUser(conn, "faculty1", facultyHash.getHash(), facultyHash.getSalt(), "FACULTY");

                // Insert associated student record
                String stdSql = "INSERT INTO students (uid, full_name, email, phone, department, semester, section, enrollment_date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(), ?)";
                try (PreparedStatement ps = conn.prepareStatement(stdSql)) {
                    ps.setString(1, "23CS101");
                    ps.setString(2, "Aarav Sharma");
                    ps.setString(3, "aarav.sharma@campus.edu");
                    ps.setString(4, "9876543210");
                    ps.setString(5, "Computer Science");
                    ps.setInt(6, 4);
                    ps.setString(7, "A");
                    ps.setInt(8, studentUserId);
                    ps.executeUpdate();
                }

                // Insert associated faculty record
                String facSql = "INSERT INTO faculty (emp_code, full_name, email, phone, department, designation, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(facSql)) {
                    ps.setString(1, "FAC-CS-01");
                    ps.setString(2, "Dr. Ramesh Kumar");
                    ps.setString(3, "ramesh.kumar@campus.edu");
                    ps.setString(4, "9123456780");
                    ps.setString(5, "Computer Science");
                    ps.setString(6, "Professor & HOD");
                    ps.setInt(7, facultyUserId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private static void seedInitialInventory(Connection conn) throws SQLException {
        String checkBooks = "SELECT COUNT(*) FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkBooks)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String bookSql = "INSERT INTO books (title, author, isbn, category, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(bookSql)) {
                    Object[][] books = {
                        {"Core Java: Fundamentals", "Cay S. Horstmann", "978-0135114063", "Computer Science", 10, 10},
                        {"Effective Java", "Joshua Bloch", "978-0134685991", "Computer Science", 5, 5},
                        {"Database System Concepts", "Abraham Silberschatz", "978-0078022159", "Database", 8, 8},
                        {"Operating System Concepts", "Peter B. Galvin", "978-1118063330", "Systems", 6, 6}
                    };
                    for (Object[] b : books) {
                        ps.setString(1, (String) b[0]);
                        ps.setString(2, (String) b[1]);
                        ps.setString(3, (String) b[2]);
                        ps.setString(4, (String) b[3]);
                        ps.setInt(5, (Integer) b[4]);
                        ps.setInt(6, (Integer) b[5]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }

        String checkRooms = "SELECT COUNT(*) FROM rooms";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkRooms)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String roomSql = "INSERT INTO rooms (room_name, room_type, building, capacity, is_available) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(roomSql)) {
                    Object[][] rooms = {
                        {"Lab 101 (Java & OOP)", "LAB", "Science Block A", 45, true},
                        {"Lab 202 (Database Systems)", "LAB", "Science Block A", 40, true},
                        {"LH-301 (Lecture Hall)", "CLASSROOM", "Academic Block B", 80, true},
                        {"Seminar Hall - 1", "SEMINAR_HALL", "Central Block", 150, true}
                    };
                    for (Object[] r : rooms) {
                        ps.setString(1, (String) r[0]);
                        ps.setString(2, (String) r[1]);
                        ps.setString(3, (String) r[2]);
                        ps.setInt(4, (Integer) r[3]);
                        ps.setBoolean(5, (Boolean) r[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    private static int insertUser(Connection conn, String username, String hash, String salt, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}

