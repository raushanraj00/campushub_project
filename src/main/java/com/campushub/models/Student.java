package com.campushub.models;

import com.campushub.exceptions.BookNotAvailableException;
import com.campushub.interfaces.BookOperations;
import java.time.LocalDate;

/**
 * UNIT 1: HIERARCHICAL INHERITANCE, COPY CONSTRUCTOR & INTERFACE IMPLEMENTATION
 * Student extends User (which extends Person) and implements BookOperations.
 * Demonstrates:
 * - Constructor types (Default, Parameterized, Copy Constructor)
 * - Constructor chaining ('this()' and 'super()')
 * - Method Overriding (@Override getRole, getDetails, issueBook, returnBook)
 * - Polymorphism in action
 */
public class Student extends User implements BookOperations {

    private static final long serialVersionUID = 1L;

    private int studentId;
    private String uid;
    private String phone;
    private String department;
    private int semester;
    private String section;
    private LocalDate enrollmentDate;

    /**
     * Default constructor (demonstrates constructor chaining via this()).
     */
    public Student() {
        this(0, "Pending UID", "Unknown Student", "student@campus.edu", "0000000000", 
             "General", 1, "A", LocalDate.now(), 0, "guest_std", "", "");
    }

    /**
     * Parameterized Constructor with full attributes.
     */
    public Student(int studentId, String uid, String name, String email, String phone,
                   String department, int semester, String section, LocalDate enrollmentDate,
                   int userId, String username, String passwordHash, String salt) {
        super(userId, name, email, username, passwordHash, salt, "STUDENT");
        this.studentId = studentId;
        this.uid = uid;
        this.phone = phone;
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.enrollmentDate = (enrollmentDate != null) ? enrollmentDate : LocalDate.now();
    }

    /**
     * Simplified Constructor for academic registration.
     */
    public Student(String uid, String name, String email, String phone, String department, int semester, String section) {
        this(0, uid, name, email, phone, department, semester, section, LocalDate.now(), 0, uid.toLowerCase(), "", "");
    }

    /**
     * Copy Constructor: Creates a clone of an existing Student object.
     * Demonstrates User-defined Copy Constructor in Java OOP.
     */
    public Student(Student source) {
        this(source.studentId, source.uid, source.name, source.email, source.phone,
             source.department, source.semester, source.section, source.enrollmentDate,
             source.userId, source.username, source.passwordHash, source.salt);
    }

    // Method Overriding (Dynamic Method Dispatch)
    @Override
    public String getRole() {
        return "STUDENT";
    }

    /**
     * Parses admission year from UID format (e.g., '25BCS0012' -> 2025).
     */
    public String getAdmissionYear() {
        if (uid != null && uid.length() >= 2 && Character.isDigit(uid.charAt(0)) && Character.isDigit(uid.charAt(1))) {
            return "20" + uid.substring(0, 2);
        }
        return "N/A";
    }

    /**
     * Parses academic branch code (e.g., 'BCS', 'BET', 'BBE', 'MCA').
     */
    public String getBranchCode() {
        if (uid != null && uid.length() >= 5) {
            return uid.replaceAll("[0-9]", "");
        }
        return "GEN";
    }

    @Override
    public String getDetails() {
        return String.format("Student [UID=%s, Name=%s, Dept=%s, Sem=%d, Section=%s, Email=%s]",
                uid, name, department, semester, section, email);
    }

    // BookOperations Interface Implementation
    @Override
    public void issueBook(int bookId) throws BookNotAvailableException {
        // Business logic verified via LibraryService
        System.out.println("Student " + uid + " requested issue for Book ID: " + bookId);
    }

    @Override
    public void returnBook(int bookId) {
        System.out.println("Student " + uid + " returned Book ID: " + bookId);
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}

