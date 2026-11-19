package com.campushub.exceptions;

/**
 * UNIT 1: EXCEPTION HANDLING (Custom Checked Exception)
 * Thrown when an operation attempts to issue a book whose available quantity is zero.
 */
public class BookNotAvailableException extends Exception {

    private final int bookId;

    public BookNotAvailableException(String message) {
        super(message);
        this.bookId = -1;
    }

    public BookNotAvailableException(int bookId, String message) {
        super("Book [ID: " + bookId + "] is unavailable: " + message);
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }
}

