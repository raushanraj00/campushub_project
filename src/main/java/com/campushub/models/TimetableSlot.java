package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1: ENCAPSULATION
 * Models a single scheduled class lecture in the campus weekly timetable.
 */
public class TimetableSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private int slotId;
    private String dayOfWeek;  // "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    private String timeSlot;   // e.g. "09:00 - 10:00", "10:00 - 11:00", etc.
    private int courseId;
    private int facultyId;
    private int roomId;
    private int semester;
    private String section;    // "A", "B", "C"

    // Display helpers
    private String courseCode;
    private String courseName;
    private String facultyName;
    private String roomName;

    public TimetableSlot() {
        this(0, "Monday", "09:00 - 10:00", 0, 0, 0, 1, "A");
    }

    public TimetableSlot(int slotId, String dayOfWeek, String timeSlot, 
                         int courseId, int facultyId, int roomId, 
                         int semester, String section) {
        this.slotId = slotId;
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek.trim() : "Monday";
        this.timeSlot = timeSlot != null ? timeSlot.trim() : "09:00 - 10:00";
        this.courseId = courseId;
        this.facultyId = facultyId;
        this.roomId = roomId;
        this.semester = semester;
        this.section = section != null ? section.trim().toUpperCase() : "A";
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}

