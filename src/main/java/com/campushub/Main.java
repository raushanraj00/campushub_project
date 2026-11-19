package com.campushub;

import com.campushub.database.DBConnection;
import com.campushub.gui.LoginFrame;
import com.campushub.utilities.NotificationThread;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * =============================================================================
 * CAMPUSHUB - SMART CAMPUS MANAGEMENT SYSTEM
 * Java OOP End-Semester Practical Project
 * Demonstrates:
 * - UNIT 1: Classes, Objects, Inheritance, Polymorphism, Interfaces, Abstraction,
 *           Constructors, Exceptions, Inner & Static Nested Classes
 * - UNIT 2: IO Package (Streams, Serialization, Piped Streams) & Multithreading
 * - UNIT 3: Swing GUI & JDBC Architecture with MySQL
 * =============================================================================
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  CampusHub: Smart Campus Management System                      ");
        System.out.println("  Java OOP University End-Semester Practical Project             ");
        System.out.println("  Units 1, 2, and 3 Fully Implemented & Demonstrable             ");
        System.out.println("=================================================================");

        // 1. Initialize Modern FlatLaf Look and Feel with fallback
        setupLookAndFeel();

        // Initialize academic data model & export root credentials file
        try {
            com.campushub.dao.AcademicDAO.bootstrapAcademicData();
        } catch (Exception ex) {
            System.err.println("[Main] Academic bootstrap notice: " + ex.getMessage());
        }

        // 2. UNIT 2: Start background daemon notification thread (Runnable interface)
        NotificationThread notificationWorker = new NotificationThread(60000, alert -> {
            // Periodic background daemon check
            System.out.println("[Background Notification] " + alert);
        });
        Thread bgThread = new Thread(notificationWorker, "CampusHub-DaemonNotifier");
        bgThread.setDaemon(true);
        bgThread.start();

        // 3. Register shutdown hook to clean database connection on program exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[CampusHub] Shutting down application and releasing JDBC resources...");
            DBConnection.closeConnection();
        }));

        // 4. UNIT 3: Launch Swing GUI safely on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    private static void setupLookAndFeel() {
        try {
            com.campushub.gui.theme.ThemeManager.initializeTheme();
        } catch (Throwable t) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }
}

