package com.campushub.interfaces;

/**
 * UNIT 1: INTERFACES
 * Authenticatable defines the contract for any identity in the system
 * that can undergo credential authentication and provide role information.
 */
public interface Authenticatable {
    
    /**
     * Authenticate user credentials.
     * @param plainPassword Raw password entered by user.
     * @return true if credentials match; false otherwise.
     */
    boolean authenticate(String plainPassword);

    /**
     * Returns the authorized role of this identity.
     * @return Role string (e.g., "ADMIN", "STUDENT", "FACULTY").
     */
    String getRole();
}

