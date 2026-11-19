package com.campushub.models;

import java.io.Serializable;

/**
 * UNIT 1: ABSTRACT CLASSES & ENCAPSULATION
 * Top-level abstract root of the campus identity hierarchy.
 * Demonstrates:
 * - Abstraction (cannot be instantiated directly)
 * - Encapsulation (protected and private fields with public getters/setters)
 * - Constructor types and 'this' keyword
 * - Serialization support (implements Serializable)
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    // Encapsulated fields accessible by derived subclasses
    protected String name;
    protected String email;

    /**
     * Default constructor.
     */
    public Person() {
        this("Unknown", "unknown@campus.edu");
    }

    /**
     * Parameterized constructor using 'this' keyword to resolve naming collisions.
     * @param name Full name of the person.
     * @param email Valid campus email.
     */
    public Person(String name, String email) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Unknown";
        this.email = (email != null && !email.trim().isEmpty()) ? email.trim() : "unknown@campus.edu";
    }

    // Abstract method: Every subclass MUST provide its own concrete representation
    public abstract String getDetails();

    // Getters and Setters (Encapsulation)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be null or blank");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    @Override
    public String toString() {
        return "Person[name=" + name + ", email=" + email + "]";
    }
}

