package com.campushub.exceptions;

/**
 * UNIT 1 & UNIT 2: EXCEPTION HANDLING & MULTITHREADING CONFLICTS
 * Thrown when an overlapping booking is detected or concurrent reservation conflict occurs.
 */
public class BookingConflictException extends Exception {

    private final int roomId;

    public BookingConflictException(String message) {
        super(message);
        this.roomId = -1;
    }

    public BookingConflictException(int roomId, String message) {
        super("Room [ID: " + roomId + "] booking conflict: " + message);
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }
}

