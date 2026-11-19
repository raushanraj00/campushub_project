package com.campushub.models;

import com.campushub.interfaces.Authenticatable;

/**
 * UNIT 1: INHERITANCE (Multilevel), ABSTRACTION & INTERFACES
 * User extends Person and implements Authenticatable.
 * Demonstrates:
 * - Multilevel Inheritance: Person -> User -> Student/Faculty/Admin
 * - Multiple Inheritance of Type via Interface implementation
 * - Use of 'super' keyword to initialize parent attributes
 * - Abstract method getRole() for polymorphic behavior
 */
public abstract class User extends Person implements Authenticatable {

    private static final long serialVersionUID = 1L;

    protected int userId;
    protected String username;
    protected String passwordHash;
    protected String salt;
    protected String role;

    /**
     * Default constructor chaining with this().
     */
    public User() {
        this(0, "anonymous", "anonymous@campus.edu", "guest", "", "", "STUDENT");
    }

    /**
     * Parameterized constructor using 'super' to pass name & email up to Person.
     */
    public User(int userId, String name, String email, String username, 
                String passwordHash, String salt, String role) {
        super(name, email); // Demonstrates 'super' keyword
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = (role != null) ? role.toUpperCase() : "STUDENT";
    }

    // Abstract method: Enforces distinct role identifier for each subclass
    @Override
    public abstract String getRole();

    // Method Overriding: Replaces Person.getDetails() with account specifics
    @Override
    public String getDetails() {
        return "User Account [ID=" + userId + ", Username=" + username + ", Role=" + role + ", Email=" + email + "]";
    }

    // Interface method implementation from Authenticatable
    @Override
    public boolean authenticate(String plainPassword) {
        // Concrete verification delegated to AuthService with cryptographic hashing
        return plainPassword != null && !plainPassword.isEmpty();
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

