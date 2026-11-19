package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.models.Student;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 3: JDBC CRUD OPERATIONS (CREATE, READ, UPDATE, DELETE)
 * StudentDAO executes database interactions on the 'students' table.
 */
public class StudentDAO {

    private static final List<Student> MEMORY_STUDENTS = new ArrayList<>();
    static {
        MEMORY_STUDENTS.addAll(AcademicDAO.getRosterStudents());
    }

    /**
     * CREATE: Registers a new student record into MySQL.
     */
    public boolean insertStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (uid, full_name, email, phone, department, semester, section, enrollment_date, user_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, s.getUid());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getDepartment());
            ps.setInt(6, s.getSemester());
            ps.setString(7, s.getSection());
            ps.setDate(8, s.getEnrollmentDate() != null ? Date.valueOf(s.getEnrollmentDate()) : new Date(System.currentTimeMillis()));
            if (s.getUserId() > 0) {
                ps.setInt(9, s.getUserId());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        s.setStudentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Memory fallback for demo mode
            s.setStudentId(MEMORY_STUDENTS.size() + 1);
            MEMORY_STUDENTS.add(s);
            return true;
        }
        return false;
    }

    /**
     * READ: Fetches all students currently stored in MySQL.
     */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Using memory records (MySQL offline): " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.addAll(MEMORY_STUDENTS);
        }
        return list;
    }

    /**
     * READ / SEARCH: Finds a student by UID.
     */
    public Student getStudentByUid(String uid) {
        String sql = "SELECT * FROM students WHERE uid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error searching by UID: " + e.getMessage());
        }
        return null;
    }

    /**
     * UPDATE: Modifies academic details of an existing student.
     */
    public boolean updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET full_name = ?, email = ?, phone = ?, department = ?, semester = ?, section = ? "
                   + "WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getSemester());
            ps.setString(6, s.getSection());
            ps.setInt(7, s.getStudentId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * DELETE: Removes a student by student ID.
     */
    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * SEARCH: Filters students by keyword in name, UID or department.
     */
    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE uid LIKE ? OR full_name LIKE ? OR department LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Search error: " + e.getMessage());
        }
        return list;
    }

    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int c = rs.getInt(1);
                if (c > 0) return c;
            }
        } catch (SQLException ignored) {}
        return MEMORY_STUDENTS.size();
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
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
        s.setUserId(rs.getInt("user_id"));
        return s;
    }
}

