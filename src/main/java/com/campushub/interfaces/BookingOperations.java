package com.campushub.interfaces;

import com.campushub.exceptions.BookingConflictException;
import java.time.LocalDateTime;

/**
 * UNIT 1: INTERFACES
 * BookingOperations specifies classroom and lab reservation contracts.
 */
public interface BookingOperations {

    /**
     * Reserve a campus room for a specific time window.
     * @param roomId Target room ID.
     * @param start Starting timestamp.
     * @param end Ending timestamp.
     * @param purpose Reason for reservation.
     * @return true if reservation succeeds.
     * @throws BookingConflictException Checked exception if slot overlaps an existing booking.
     */
    boolean reserveRoom(int roomId, LocalDateTime start, LocalDateTime end, String purpose) 
            throws BookingConflictException;

    /**
     * Cancel an existing active booking.
     * @param bookingId Target booking ID.
     * @return true if successfully cancelled.
     */
    boolean cancelReservation(int bookingId);
}

