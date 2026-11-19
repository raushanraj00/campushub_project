package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1: ENCAPSULATION
 * Represents the assignment of a Course to a Faculty member for a specific student section.
 */
public class CourseAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int assignmentId;
    private int facultyId;
    private int courseId;
    private String section;       // e.g. "A", "B", "C"
    private String academicYear;  // e.g. "2025-2026"

    // Display helpers
    private String courseCode;
    private String courseName;
    private String facultyName;
    private String facultyEmpCode;
    private int semester;

    public CourseAssignment() {
        this(0, 0, 0, "A", "2025-2026");
    }

    public CourseAssignment(int assignmentId, int facultyId, int courseId, String section, String academicYear) {
        this.assignmentId = assignmentId;
        this.facultyId = facultyId;
        this.courseId = courseId;
        this.section = section != null ? section.trim().toUpperCase() : "A";
        this.academicYear = academicYear != null ? academicYear.trim() : "2025-2026";
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyEmpCode() {
        return facultyEmpCode;
    }

    public void setFacultyEmpCode(String facultyEmpCode) {
        this.facultyEmpCode = facultyEmpCode;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return courseCode + " (" + courseName + ") - Section " + section + " [" + facultyName + "]";
    }
}

