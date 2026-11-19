package com.campushub.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * UNIT 1 & UNIT 3: STATIC NESTED CLASS & JDBC ARCHITECTURE
 * Manages database connectivity to MySQL Server.
 * Demonstrates:
 * - Static Nested Class: DatabaseConfig encapsulates credentials and URL.
 * - JDBC Architecture: Java Application -> JDBC API -> MySQL Connector/J Driver -> MySQL Server
 * - Connection Interface management & Singleton connection lifecycle
 * - Exception Handling (SQLException, ClassNotFoundException)
 */
public class DBConnection {

    private static Connection connection = null;

    // =========================================================================
    // UNIT 1: STATIC NESTED CLASS
    // =========================================================================
    /**
     * Static nested class holding configuration parameters.
     * Demonstrates proper static nested class usage in Java.
     */
    public static class DatabaseConfig {
        private static String url;
        private static String user;
        private static String password;
        private static boolean loaded = false;

        static {
            loadConfig();
        }

        public static void loadConfig() {
            Properties props = new Properties();
            try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (in != null) {
                    props.load(in);
                    url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/campushub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&connectTimeout=2000&socketTimeout=3000");
                    user = props.getProperty("db.user", "root");
                    password = props.getProperty("db.password", "root");
                } else {
                    setDefaultConfig();
                }
            } catch (Exception e) {
                setDefaultConfig();
            }
            loaded = true;
        }

        private static void setDefaultConfig() {
            url = "jdbc:mysql://localhost:3306/campushub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&connectTimeout=2000&socketTimeout=3000";
            user = "root";
            password = "password";
        }

        public static boolean saveAndApplyConfig(String newUrl, String newUser, String newPassword) {
            url = newUrl != null ? newUrl.trim() : url;
            user = newUser != null ? newUser.trim() : user;
            password = newPassword != null ? newPassword : password;
            closeConnection();

            try {
                java.io.File propFile = new java.io.File("src/main/resources/application.properties");
                Properties props = new Properties();
                if (propFile.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(propFile)) {
                        props.load(fis);
                    }
                }
                props.setProperty("db.url", url);
                props.setProperty("db.user", user);
                props.setProperty("db.password", password);
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(propFile)) {
                    props.store(fos, "Updated from CampusHub GUI Settings");
                }
            } catch (Exception e) {
                System.err.println("[DBConnection] Could not write application.properties: " + e.getMessage());
            }

            return testConnection();
        }

        public static String getUrl() { return url; }
        public static void setUrl(String newUrl) { url = newUrl; }
        public static String getUser() { return user; }
        public static void setUser(String newUser) { user = newUser; }
        public static String getPassword() { return password; }
        public static void setPassword(String newPassword) { password = newPassword; }
        public static boolean isLoaded() { return loaded; }
    }

    // Explicit private constructor to enforce Singleton utility pattern
    private DBConnection() {}

    /**
     * UNIT 3: ESTABLISHING CONNECTIVITY
     * Obtains or reuses an active JDBC Connection using DriverManager.
     * @return java.sql.Connection object
     * @throws SQLException If database connection fails
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure MySQL 8.x Connector driver is registered
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found on classpath", e);
            }

            connection = DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
            );
        }
        return connection;
    }

    /**
     * Verifies if MySQL is reachable with the current configured credentials.
     * @return true if connectivity succeeded; false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("[DBConnection] Connection test warning: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely closes the active connection when application terminates.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}

