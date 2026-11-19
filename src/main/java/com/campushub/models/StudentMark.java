package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1: CONTROL STRUCTURES & OPERATORS
 * StudentMark models academic evaluation of a student in a specific course.
 * Automatically computes total marks and letter grades based on component scores.
 */
public class StudentMark implements Serializable {

    private static final long serialVersionUID = 1L;

    private int markId;
    private int studentId;
    private int courseId;
    private double internal1;  // Max: 20
    private double internal2;  // Max: 20
    private double assignment; // Max: 10
    private double endSem;     // Max: 50
    private double totalMarks; // Max: 100
    private String grade;      // A+, A, B+, B, C, P, F
    private int evaluatedBy;   // Faculty ID

    // Display helpers
    private String studentUid;
    private String studentName;
    private String courseCode;
    private String courseName;
    private String section;

    public StudentMark() {
        this(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0);
    }

    public StudentMark(int markId, int studentId, int courseId, 
                       double internal1, double internal2, double assignment, 
                       double endSem, int evaluatedBy) {
        this.markId = markId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.internal1 = clamp(internal1, 0, 20);
        this.internal2 = clamp(internal2, 0, 20);
        this.assignment = clamp(assignment, 0, 10);
        this.endSem = clamp(endSem, 0, 50);
        this.evaluatedBy = evaluatedBy;
        calculateTotalAndGrade();
    }

    /**
     * Calculates total marks and assigns letter grades using control structures.
     */
    public void calculateTotalAndGrade() {
        this.totalMarks = this.internal1 + this.internal2 + this.assignment + this.endSem;
        
        // Grading scale
        if (this.totalMarks >= 90.0) {
            this.grade = "A+";
        } else if (this.totalMarks >= 80.0) {
            this.grade = "A";
        } else if (this.totalMarks >= 70.0) {
            this.grade = "B+";
        } else if (this.totalMarks >= 60.0) {
            this.grade = "B";
        } else if (this.totalMarks >= 50.0) {
            this.grade = "C";
        } else if (this.totalMarks >= 40.0) {
            this.grade = "P";
        } else {
            this.grade = "F";
        }
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    // Getters and Setters
    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getInternal1() {
        return internal1;
    }

    public void setInternal1(double internal1) {
        this.internal1 = clamp(internal1, 0, 20);
        calculateTotalAndGrade();
    }

    public double getInternal2() {
        return internal2;
    }

    public void setInternal2(double internal2) {
        this.internal2 = clamp(internal2, 0, 20);
        calculateTotalAndGrade();
    }

    public double getAssignment() {
        return assignment;
    }

    public void setAssignment(double assignment) {
        this.assignment = clamp(assignment, 0, 10);
        calculateTotalAndGrade();
    }

    public double getEndSem() {
        return endSem;
    }

    public void setEndSem(double endSem) {
        this.endSem = clamp(endSem, 0, 50);
        calculateTotalAndGrade();
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public String getGrade() {
        return grade;
    }

    public int getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(int evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}

