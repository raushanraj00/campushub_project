package com.campushub.services;

import com.campushub.dao.UserDAO;
import com.campushub.exceptions.InvalidUserException;
import com.campushub.models.User;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * UNIT 1 & UNIT 3: SECURITY & AUTHENTICATION SERVICE
 * Implements PBKDF2 with HMAC-SHA256 password hashing.
 * Demonstrates:
 * - Runtime Polymorphism (User reference holding Student, Faculty or Admin)
 * - Exception Handling (InvalidUserException)
 * - Session Management
 */
public class AuthService {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Standard demo salts and hashes for seamless offline/standalone demo support
    public static final String DEMO_SALT = "CampusHubFixedSalt2026==";
    public static final String DEMO_ADMIN_HASH = hashWithSalt("Admin@123", DEMO_SALT);
    public static final String DEMO_STUDENT_HASH = hashWithSalt("Student@123", DEMO_SALT);
    public static final String DEMO_FACULTY_HASH = hashWithSalt("Faculty@123", DEMO_SALT);

    // Active in-memory password store to reflect password updates across UI & directories
    public static final java.util.Map<String, String> ACTIVE_PASSWORDS = new java.util.concurrent.ConcurrentHashMap<>();

    public static String getActivePassword(String username, String defaultPwd) {
        if (username == null) return defaultPwd;
        return ACTIVE_PASSWORDS.getOrDefault(username.trim().toLowerCase(), defaultPwd);
    }

    // Active logged-in session user
    private static User currentUser = null;
    private static final AuthService INSTANCE = new AuthService();

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public static class SaltAndHash {
        private final String salt;
        private final String hash;

        public SaltAndHash(String salt, String hash) {
            this.salt = salt;
            this.hash = hash;
        }

        public String getSalt() { return salt; }
        public String getHash() { return hash; }
    }

    /**
     * Hashes password using PBKDF2WithHmacSHA256 and cryptographically random salt.
     */
    public static SaltAndHash generateHash(String plainPassword) {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String hash = hashWithSalt(plainPassword, salt);
        return new SaltAndHash(salt, hash);
    }

    public static String hashWithSalt(String plainPassword, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error computing cryptographic hash: " + e.getMessage(), e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash, String storedSalt) {
        if (plainPassword == null || storedHash == null || storedSalt == null) return false;
        String calculated = hashWithSalt(plainPassword, storedSalt);
        return calculated.equals(storedHash);
    }

    /**
     * Authenticates credentials against database and sets active session.
     */
    public static User login(String username, String password) throws InvalidUserException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidUserException("Username and password cannot be empty.");
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new InvalidUserException("User '" + username + "' not found in database.");
        }

        if (!verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            throw new InvalidUserException("Invalid password. Please check your credentials.");
        }

        currentUser = user;
        return user;
    }

    /**
     * Changes password for any user after verifying their current password.
     */
    public static boolean changePassword(String username, String oldPassword, String newPassword) 
            throws InvalidUserException, IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new IllegalArgumentException("Current password is required.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new InvalidUserException("User not found: " + username);
        }

        if (!verifyPassword(oldPassword, user.getPasswordHash(), user.getSalt())) {
            throw new InvalidUserException("Current password is incorrect.");
        }

        // Generate new hash and salt
        SaltAndHash newCredentials = generateHash(newPassword);

        // Update database if online
        try (java.sql.Connection conn = com.campushub.database.DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE username = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newCredentials.getHash());
                    ps.setString(2, newCredentials.getSalt());
                    ps.setString(3, username.trim());
                    ps.executeUpdate();
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[AuthService] DB update skipped, applying to active session: " + e.getMessage());
        }

        // Update in-memory session user & active password map
        ACTIVE_PASSWORDS.put(username.trim().toLowerCase(), newPassword);
        user.setPasswordHash(newCredentials.getHash());
        user.setSalt(newCredentials.getSalt());

        // Update roster references so next login immediately picks up the new credentials
        com.campushub.models.Student std = com.campushub.dao.AcademicDAO.findStudentByUid(username);
        if (std != null) {
            std.setPasswordHash(newCredentials.getHash());
            std.setSalt(newCredentials.getSalt());
        }
        com.campushub.models.Faculty fac = com.campushub.dao.AcademicDAO.findFacultyByEmpCode(username);
        if (fac != null) {
            fac.setPasswordHash(newCredentials.getHash());
            fac.setSalt(newCredentials.getSalt());
        }

        if (currentUser != null && currentUser.getUsername().equalsIgnoreCase(username)) {
            currentUser.setPasswordHash(newCredentials.getHash());
            currentUser.setSalt(newCredentials.getSalt());
        }

        // Update the password in CREDENTIALS_AND_PASSWORDS.txt file
        com.campushub.utilities.CredentialsExporter.recordPasswordChange(username, newPassword);

        com.campushub.utilities.ActivityLogger.log("PASSWORD_CHANGED", 
                "Password updated for user " + username, username);

        return true;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}

