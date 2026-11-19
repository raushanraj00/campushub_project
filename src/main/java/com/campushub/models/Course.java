package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1: ENCAPSULATION & SERIALIZATION
 * Course models an academic subject offered in a department.
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private int courseId;
    private String courseCode; // e.g. "CS201", "CS202", "EC101"
    private String courseName; // e.g. "Object-Oriented Programming using Java"
    private int credits;       // e.g. 4
    private String department; // e.g. "Computer Science"
    private int semester;      // 1 to 8

    public Course() {
        this(0, "CS101", "Introduction to Computing", 4, "Computer Science", 1);
    }

    public Course(int courseId, String courseCode, String courseName, int credits, String department, int semester) {
        this.courseId = courseId;
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : "COURSE000";
        this.courseName = courseName != null ? courseName.trim() : "Untitled Course";
        this.credits = Math.max(1, credits);
        this.department = department != null ? department.trim() : "General";
        this.semester = Math.max(1, Math.min(8, semester));
    }

    public Course(String courseCode, String courseName, int credits, String department, int semester) {
        this(0, courseCode, courseName, credits, department, semester);
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
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

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + credits + " Credits)";
    }
}

