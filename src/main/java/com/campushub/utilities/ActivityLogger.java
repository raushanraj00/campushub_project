package com.campushub.utilities;

import com.campushub.database.DBConnection;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 2: IO PACKAGE & FILE STREAMS
 * ActivityLogger implements persistent audit logging using both disk files and database.
 * Demonstrates:
 * - File creation, existence checks and directory creation
 * - FileOutputStream & BufferedOutputStream (Filter Streams)
 * - FileReader and BufferedReader for reading file records
 * - Resource management with try-with-resources
 */
public class ActivityLogger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "logs/campus_activity.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Ensure log directory exists
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Appends an audit log message to both file stream and database.
     */
    public static synchronized void log(String action, String details, String username) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] USER: %s | ACTION: %s | DETAILS: %s%n",
                timestamp, (username != null ? username : "SYSTEM"), action, details);

        // 1. File Writing with Filter Streams (FileOutputStream + BufferedOutputStream)
        File file = new File(LOG_FILE);
        try (FileOutputStream fos = new FileOutputStream(file, true); // true = append mode
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] bytes = logEntry.getBytes(StandardCharsets.UTF_8);
            bos.write(bytes);
            bos.flush();
        } catch (IOException e) {
            System.err.println("[ActivityLogger] File logging failed: " + e.getMessage());
        }

        // 2. Database audit logging
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                String sql = "INSERT INTO activity_logs (action, details, username) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, action);
                    ps.setString(2, details);
                    ps.setString(3, username != null ? username : "SYSTEM");
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ignored) {
            // Tolerant to offline DB during testing
        }
    }

    /**
     * UNIT 2: READING IO FILES
     * Reads recent activity logs from the log file using BufferedReader.
     */
    public static List<String> readRecentLogs(int maxLines) {
        List<String> lines = new ArrayList<>();
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (lines.size() > maxLines) {
                    lines.remove(0); // keep most recent
                }
            }
        } catch (IOException e) {
            System.err.println("[ActivityLogger] Error reading logs: " + e.getMessage());
        }
        return lines;
    }

    public static String getLogFilePath() {
        return new File(LOG_FILE).getAbsolutePath();
    }
}

