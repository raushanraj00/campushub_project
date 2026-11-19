package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1 & UNIT 2: ENCAPSULATION & THREAD SYNCHRONIZATION
 * Room models campus facilities (classrooms, computer labs, seminar halls).
 * Demonstrates:
 * - Thread Synchronization: synchronized methods prevent race conditions
 *   when two concurrent threads attempt to reserve the same room at the same time.
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private int roomId;
    private String roomName;
    private String roomType; // CLASSROOM, LAB, SEMINAR_HALL
    private String building;
    private int capacity;
    private boolean available;

    public Room() {
        this(0, "Room-000", "CLASSROOM", "Main Block", 30, true);
    }

    public Room(int roomId, String roomName, String roomType, String building, int capacity, boolean available) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.building = building;
        this.capacity = capacity;
        this.available = available;
    }

    public Room(String roomName, String roomType, String building, int capacity) {
        this(0, roomName, roomType, building, capacity, true);
    }

    /**
     * UNIT 2: THREAD SYNCHRONIZATION
     * Synchronized method prevents two threads from booking this room concurrently.
     * If Thread A enters, Thread B enters BLOCKED state on the Room monitor lock.
     * @return true if room was available and is now booked; false if already booked.
     */
    public synchronized boolean bookRoom() {
        if (this.available) {
            this.available = false;
            return true;
        }
        return false;
    }

    /**
     * UNIT 2: THREAD SYNCHRONIZATION
     * Releasing the room safely updates the shared state.
     */
    public synchronized void releaseRoom() {
        this.available = true;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean isAvailable() {
        return available;
    }

    public synchronized void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, Cap: %d) - %s", 
                roomName, roomType, capacity, available ? "Available" : "Occupied");
    }
}

