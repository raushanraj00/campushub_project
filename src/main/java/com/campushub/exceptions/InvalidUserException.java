package com.campushub.exceptions;

/**
 * UNIT 1: EXCEPTION HANDLING
 * Thrown when credentials fail or an unrecognized user attempts access.
 */
public class InvalidUserException extends Exception {

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

