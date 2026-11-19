package com.campushub.models;

/**
 * UNIT 1: HIERARCHICAL INHERITANCE
 * Faculty extends User as a parallel branch beside Student and Admin.
 * Demonstrates:
 * - Hierarchical Inheritance
 * - Method Overriding
 * - Super constructor invocation
 */
public class Faculty extends User {

    private static final long serialVersionUID = 1L;

    private int facultyId;
    private String empCode;
    private String phone;
    private String department;
    private String designation;
    private String privileges = "UPLOAD_MARKS,VIEW_STUDENTS,CLASS_SCHEDULE,BOOK_LABS";

    public Faculty() {
        this(0, "FAC-000", "Unknown Faculty", "faculty@campus.edu", "0000000000", 
             "General", "Lecturer", 0, "guest_fac", "", "");
    }

    public Faculty(int facultyId, String empCode, String name, String email, String phone,
                   String department, String designation, int userId, String username, 
                   String passwordHash, String salt) {
        super(userId, name, email, username, passwordHash, salt, "FACULTY");
        this.facultyId = facultyId;
        this.empCode = empCode;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.privileges = "UPLOAD_MARKS,VIEW_STUDENTS,CLASS_SCHEDULE,BOOK_LABS";
    }

    public Faculty(String empCode, String name, String email, String phone, String department, String designation) {
        this(0, empCode, name, email, phone, department, designation, 0, empCode.toLowerCase(), "", "");
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges != null ? privileges : "VIEW_STUDENTS,CLASS_SCHEDULE";
    }

    public boolean hasPrivilege(String privilege) {
        if (privileges == null || privilege == null) return false;
        return privileges.contains(privilege.trim().toUpperCase());
    }

    @Override
    public String getRole() {
        return "FACULTY";
    }

    @Override
    public String getDetails() {
        return String.format("Faculty [EmpCode=%s, Name=%s, Dept=%s, Designation=%s, Email=%s]",
                empCode, name, department, designation, email);
    }

    // Getters and Setters
    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}

