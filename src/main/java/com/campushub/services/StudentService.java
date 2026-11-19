package com.campushub.services;

import com.campushub.dao.StudentDAO;
import com.campushub.models.Student;
import java.sql.SQLException;
import java.util.List;

/**
 * UNIT 1: CONTROL STRUCTURES & VALIDATION
 * StudentService enforces campus enrollment policies and validation rules.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public boolean registerStudent(Student student) throws SQLException, IllegalArgumentException {
        // Validation with control structures
        if (student == null) {
            throw new IllegalArgumentException("Student payload cannot be null.");
        }
        if (student.getUid() == null || student.getUid().trim().isEmpty()) {
            throw new IllegalArgumentException("Student UID is mandatory.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student Full Name is required.");
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            throw new IllegalArgumentException("A valid campus email address is required.");
        }
        if (student.getSemester() < 1 || student.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }

        // Check UID uniqueness
        Student existing = studentDAO.getStudentByUid(student.getUid().trim());
        if (existing != null) {
            throw new IllegalArgumentException("Student UID '" + student.getUid() + "' is already registered.");
        }

        return studentDAO.insertStudent(student);
    }

    public boolean updateStudent(Student student) throws SQLException, IllegalArgumentException {
        if (student == null || student.getStudentId() <= 0) {
            throw new IllegalArgumentException("Invalid student ID for update.");
        }
        return studentDAO.updateStudent(student);
    }

    public boolean removeStudent(int studentId) throws SQLException {
        return studentDAO.deleteStudent(studentId);
    }

    public List<Student> listAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentDAO.getAllStudents();
        }
        return studentDAO.searchStudents(keyword.trim());
    }

    public int getTotalStudentCount() {
        return studentDAO.countStudents();
    }
}

