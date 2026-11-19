package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.models.Faculty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 3: JDBC CRUD OPERATIONS
 * FacultyDAO performs persistence actions on the 'faculty' table.
 */
public class FacultyDAO {

    private static final List<Faculty> MEMORY_FACULTY = new ArrayList<>();
    static {
        MEMORY_FACULTY.addAll(AcademicDAO.getRosterFaculty());
    }

    public boolean insertFaculty(Faculty f) throws SQLException {
        if (f.getUsername() == null || f.getUsername().isEmpty()) {
            f.setUsername(f.getEmpCode().toLowerCase().replace("-", "_"));
        }
        f.setPasswordHash(com.campushub.services.AuthService.DEMO_FACULTY_HASH);
        f.setSalt(com.campushub.services.AuthService.DEMO_SALT);
        f.setPrivileges("UPLOAD_MARKS,VIEW_STUDENTS,CLASS_SCHEDULE,BOOK_LABS");

        String sql = "INSERT INTO faculty (emp_code, full_name, email, phone, department, designation, user_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getEmpCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getPhone());
            ps.setString(5, f.getDepartment());
            ps.setString(6, f.getDesignation());
            if (f.getUserId() > 0) {
                ps.setInt(7, f.getUserId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        f.setFacultyId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            // Memory mode fallback
            if (f.getFacultyId() <= 0) {
                f.setFacultyId(AcademicDAO.getRosterFaculty().size() + 1);
            }
        }

        AcademicDAO.addFaculty(f);
        if (!MEMORY_FACULTY.contains(f)) {
            MEMORY_FACULTY.add(f);
        }

        try {
            com.campushub.utilities.CredentialsExporter.exportMasterCredentials(
                    AcademicDAO.getRosterStudents(), AcademicDAO.getRosterFaculty());
        } catch (Exception ignored) {}

        return true;
    }

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT * FROM faculty ORDER BY faculty_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToFaculty(rs));
            }
        } catch (SQLException e) {
            System.err.println("[FacultyDAO] Using memory records (MySQL offline): " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.addAll(MEMORY_FACULTY);
        }
        return list;
    }

    public boolean updateFaculty(Faculty f) throws SQLException {
        String sql = "UPDATE faculty SET full_name = ?, email = ?, phone = ?, department = ?, designation = ? "
                   + "WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getPhone());
            ps.setString(4, f.getDepartment());
            ps.setString(5, f.getDesignation());
            ps.setInt(6, f.getFacultyId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteFaculty(int facultyId) throws SQLException {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countFaculty() {
        String sql = "SELECT COUNT(*) FROM faculty";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int c = rs.getInt(1);
                if (c > 0) return c;
            }
        } catch (SQLException ignored) {}
        return MEMORY_FACULTY.size();
    }

    private Faculty mapResultSetToFaculty(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setFacultyId(rs.getInt("faculty_id"));
        f.setEmpCode(rs.getString("emp_code"));
        f.setName(rs.getString("full_name"));
        f.setEmail(rs.getString("email"));
        f.setPhone(rs.getString("phone"));
        f.setDepartment(rs.getString("department"));
        f.setDesignation(rs.getString("designation"));
        f.setUserId(rs.getInt("user_id"));
        return f;
    }
}

