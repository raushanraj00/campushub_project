package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.exceptions.BookingConflictException;
import com.campushub.models.Booking;
import com.campushub.models.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 3: DATABASE TRANSACTIONS & CONFLICT RESOLUTION
 * BookingDAO handles room and laboratory scheduling with concurrency safeguards.
 */
public class BookingDAO {

    private static final List<Room> MEMORY_ROOMS = new ArrayList<>();
    static {
        MEMORY_ROOMS.add(new Room(1, "Lab 101 (Java & OOP)", "LAB", "Science Block A", 45, true));
        MEMORY_ROOMS.add(new Room(2, "Lab 202 (Database Systems)", "LAB", "Science Block A", 40, true));
        MEMORY_ROOMS.add(new Room(3, "LH-301 (Lecture Hall)", "CLASSROOM", "Academic Block B", 80, true));
        MEMORY_ROOMS.add(new Room(4, "Seminar Hall - 1", "SEMINAR_HALL", "Central Block", 150, true));
    }

    /**
     * UNIT 3: DATABASE TRANSACTION WITH CONFLICT CHECKING
     * Reserves a room ensuring no conflicting overlapping bookings exist.
     */
    public synchronized boolean reserveRoomTransaction(int userId, int roomId, 
                                                      LocalDateTime start, LocalDateTime end, 
                                                      String purpose) 
            throws SQLException, BookingConflictException {
        
        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Conflict detection query: checks for overlapping intervals
            // Interval overlap logic: (ExistingStart < RequestedEnd) AND (ExistingEnd > RequestedStart)
            String overlapSql = "SELECT booking_id FROM bookings "
                              + "WHERE room_id = ? AND status = 'CONFIRMED' "
                              + "AND (start_time < ? AND end_time > ?) FOR UPDATE";

            try (PreparedStatement checkPs = conn.prepareStatement(overlapSql)) {
                checkPs.setInt(1, roomId);
                checkPs.setTimestamp(2, Timestamp.valueOf(end));
                checkPs.setTimestamp(3, Timestamp.valueOf(start));

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        int conflictingId = rs.getInt("booking_id");
                        throw new BookingConflictException(roomId, 
                                "Time slot overlaps with existing Booking #" + conflictingId);
                    }
                }
            }

            // 2. Insert new confirmed booking
            String insertSql = "INSERT INTO bookings (user_id, room_id, start_time, end_time, purpose, status) "
                             + "VALUES (?, ?, ?, ?, ?, 'CONFIRMED')";
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setInt(1, userId);
                insertPs.setInt(2, roomId);
                insertPs.setTimestamp(3, Timestamp.valueOf(start));
                insertPs.setTimestamp(4, Timestamp.valueOf(end));
                insertPs.setString(5, purpose != null ? purpose : "Academic Session");
                insertPs.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (Exception e) {
            conn.rollback(); // Rollback on conflict or database failure
            if (e instanceof BookingConflictException) {
                throw (BookingConflictException) e;
            }
            throw new SQLException("Reservation failed: " + e.getMessage(), e);
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean cancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, r.room_name, u.username FROM bookings b "
                   + "JOIN rooms r ON b.room_id = r.room_id "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "ORDER BY b.booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking b = new Booking(
                    rs.getInt("booking_id"),
                    rs.getInt("user_id"),
                    rs.getInt("room_id"),
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getTimestamp("end_time").toLocalDateTime(),
                    rs.getString("purpose"),
                    rs.getString("status")
                );
                b.setRoomName(rs.getString("room_name"));
                b.setUserName(rs.getString("username"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error loading bookings: " + e.getMessage());
        }
        return list;
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getString("room_type"),
                    rs.getString("building"),
                    rs.getInt("capacity"),
                    rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Using memory rooms (MySQL offline): " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.addAll(MEMORY_ROOMS);
        }
        return list;
    }

    public boolean addRoom(Room r) throws SQLException {
        String sql = "INSERT INTO rooms (room_name, room_type, building, capacity, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getRoomName());
            ps.setString(2, r.getRoomType());
            ps.setString(3, r.getBuilding());
            ps.setInt(4, r.getCapacity());
            ps.setBoolean(5, r.isAvailable());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setRoomId(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public int countRooms() {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int c = rs.getInt(1);
                if (c > 0) return c;
            }
        } catch (SQLException ignored) {}
        return MEMORY_ROOMS.size();
    }

    public int countActiveBookings() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) {}
        return 0;
    }
}

