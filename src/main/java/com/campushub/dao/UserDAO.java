package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.models.Admin;
import com.campushub.models.Faculty;
import com.campushub.models.Student;
import com.campushub.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * UNIT 3: JDBC PREPAREDSTATEMENT & RESULTSET
 * UserDAO performs database operations on the 'users' table.
 * Demonstrates:
 * - PreparedStatement with parameter placeholders (?)
 * - ResultSet traversal and column mapping
 * - Polymorphism: Returns User reference instantiated as Student, Faculty, or Admin
 */
public class UserDAO {

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        // 1. Instant resolution against in-memory roster (supports all 260 students, 25 faculty, admins)
        User fallbackUser = getDemoFallbackUser(username);

        // 2. Query MySQL database if available to get updated passwords or persistence
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String uname = rs.getString("username");
                    String hash = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    String role = rs.getString("role");

                    // Map into concrete polymorphic subclasses
                    switch (role.toUpperCase()) {
                        case "ADMIN":
                            return new Admin(userId, "System Admin", "admin@campus.edu", uname, hash, salt, "SUPER_ADMIN");
                        case "STUDENT":
                            Student s = new Student();
                            s.setUserId(userId);
                            s.setUsername(uname);
                            s.setPasswordHash(hash);
                            s.setSalt(salt);
                            populateStudentDetails(conn, s);
                            return s;
                        case "FACULTY":
                            Faculty f = new Faculty();
                            f.setUserId(userId);
                            f.setUsername(uname);
                            f.setPasswordHash(hash);
                            f.setSalt(salt);
                            populateFacultyDetails(conn, f);
                            return f;
                        default:
                            return new Admin(userId, uname, uname + "@campus.edu", uname, hash, salt, "STANDARD");
                    }
                }
            }
        } catch (Throwable e) {
            // MySQL offline or table not present; safely rely on fallback in-memory roster
        }
        return fallbackUser;
    }

    private User getDemoFallbackUser(String username) {
        if (username == null) return null;
        String u = username.trim().toLowerCase();
        if ("admin".equals(u) || "hod".equals(u) || "hod_cse".equals(u)) {
            return new Admin(1, "Dr. A. K. Sharma (HOD / Admin)", "admin@campus.edu", "admin", 
                    com.campushub.services.AuthService.DEMO_ADMIN_HASH, 
                    com.campushub.services.AuthService.DEMO_SALT, "SUPER_ADMIN");
        } else if ("hod_ece".equals(u)) {
            return new Admin(2, "Dr. S. K. Roy (HOD ECE)", "sk.roy@campus.edu", "hod_ece",
                    com.campushub.services.AuthService.DEMO_ADMIN_HASH,
                    com.campushub.services.AuthService.DEMO_SALT, "HOD");
        } else if ("student1".equals(u)) {
            return AcademicDAO.getRosterStudents().get(0);
        } else if ("faculty1".equals(u)) {
            return AcademicDAO.getRosterFaculty().get(0);
        }

        // Check if username matches any Student UID (e.g. '25BCS0001', '25BET0010')
        Student stdMatch = AcademicDAO.findStudentByUid(username);
        if (stdMatch != null) {
            return stdMatch;
        }

        // Check if username matches any Faculty Employee Code (e.g. 'FAC-CSE-01', 'fac_cse_01')
        String normalizedEmp = username.trim().toUpperCase().replace("_", "-");
        Faculty facMatch = AcademicDAO.findFacultyByEmpCode(normalizedEmp);
        if (facMatch != null) {
            return facMatch;
        }

        return null;
    }

    private void populateStudentDetails(Connection conn, Student s) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s.setStudentId(rs.getInt("student_id"));
                    s.setUid(rs.getString("uid"));
                    s.setName(rs.getString("full_name"));
                    s.setEmail(rs.getString("email"));
                    s.setPhone(rs.getString("phone"));
                    s.setDepartment(rs.getString("department"));
                    s.setSemester(rs.getInt("semester"));
                    s.setSection(rs.getString("section"));
                    if (rs.getDate("enrollment_date") != null) {
                        s.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
                    }
                }
            }
        } catch (SQLException ignored) {}
    }

    private void populateFacultyDetails(Connection conn, Faculty f) {
        String sql = "SELECT * FROM faculty WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    f.setFacultyId(rs.getInt("faculty_id"));
                    f.setEmpCode(rs.getString("emp_code"));
                    f.setName(rs.getString("full_name"));
                    f.setEmail(rs.getString("email"));
                    f.setPhone(rs.getString("phone"));
                    f.setDepartment(rs.getString("department"));
                    f.setDesignation(rs.getString("designation"));
                }
            }
        } catch (SQLException ignored) {}
    }

    public int createUser(String username, String passwordHash, String salt, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
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

    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int c = rs.getInt(1);
                if (c > 0) return c;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] count error: " + e.getMessage());
        }
        return 3; // admin, student1, faculty1
    }
}

