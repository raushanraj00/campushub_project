package com.campushub.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UNIT 1: INNER CLASSES (Non-Static Member Inner Class)
 * Booking models a reservation of a classroom or laboratory.
 * Demonstrates:
 * - Non-static member inner class BookingDetails
 * - Inner class accessing enclosing outer class instance via Booking.this
 */
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookingId;
    private int userId;
    private int roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String status; // CONFIRMED, CANCELLED, COMPLETED
    private LocalDateTime createdAt;

    // Additional display metadata
    private String roomName;
    private String userName;

    public Booking() {
        this(0, 0, 0, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "General Lecture", "CONFIRMED");
    }

    public Booking(int bookingId, int userId, int roomId, 
                   LocalDateTime startTime, LocalDateTime endTime, 
                   String purpose, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = (status != null) ? status : "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Factory method creating the non-static inner class instance.
     */
    public BookingDetails getBookingDetails() {
        return new BookingDetails();
    }

    // =========================================================================
    // UNIT 1: NON-STATIC MEMBER INNER CLASS
    // =========================================================================
    /**
     * BookingDetails demonstrates a non-static member inner class.
     * It has direct access to all outer instance fields of Booking.
     */
    public class BookingDetails {

        private final String generatedTimestamp;

        public BookingDetails() {
            this.generatedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        /**
         * Formats an official campus reservation pass using outer class variables.
         */
        public String getReservationPass() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
            return "====================================================\n"
                 + "        CAMPUSHUB OFFICIAL RESERVATION PASS         \n"
                 + "====================================================\n"
                 + " Booking ID   : #" + Booking.this.bookingId + "\n"
                 + " Room ID      : " + Booking.this.roomId + " (" + (roomName != null ? roomName : "N/A") + ")\n"
                 + " Reserved For : " + (userName != null ? userName : ("User #" + Booking.this.userId)) + "\n"
                 + " Purpose      : " + Booking.this.purpose + "\n"
                 + " Time Slot    : " + Booking.this.startTime.format(dtf) + " to " + Booking.this.endTime.format(dtf) + "\n"
                 + " Status       : " + Booking.this.status + "\n"
                 + " Issued At    : " + generatedTimestamp + "\n"
                 + "====================================================";
        }

        public Booking getOuterBooking() {
            return Booking.this; // Demonstrates referencing outer instance
        }
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

