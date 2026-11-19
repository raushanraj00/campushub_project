package com.campushub.models;

/**
 * UNIT 1: HIERARCHICAL INHERITANCE
 * Admin represents privileged system administrators.
 */
public class Admin extends User {

    private static final long serialVersionUID = 1L;

    private String adminLevel;

    public Admin() {
        this(0, "System Admin", "admin@campus.edu", "admin", "", "", "SUPER_ADMIN");
    }

    public Admin(int userId, String name, String email, String username, 
                 String passwordHash, String salt, String adminLevel) {
        super(userId, name, email, username, passwordHash, salt, "ADMIN");
        this.adminLevel = (adminLevel != null) ? adminLevel : "STANDARD";
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDetails() {
        return String.format("Admin [User=%s, Name=%s, Level=%s, Email=%s]",
                username, name, adminLevel, email);
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
}

