package com.campushub.gui;

import com.campushub.models.User;
import com.campushub.utilities.ActivityLogger;
import com.campushub.utilities.BackupManager;
import com.campushub.utilities.BackupThread;
import com.campushub.utilities.ReportManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * UNIT 2: IO PACKAGE & MULTITHREADING DEMONSTRATION PANEL
 * Provides interactive GUI triggers to test and demonstrate:
 * - Object Serialization & Deserialization
 * - Inter-thread Pipe Streams (PipedOutputStream & PipedInputStream)
 * - File I/O Activity Logging
 * - Background Thread execution with Swing responsiveness
 */
public class SystemToolsPanel extends JPanel {

    private final User currentUser;
    private JTextArea txtOutput;

    public SystemToolsPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("System Administration, IO Package & Multithreading Tools");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);

        // Center split: Action Cards on Left/Top, Output Terminal on Center/Bottom
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(new Color(248, 250, 252));

        // Action Buttons Grid
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        buttonsGrid.setBackground(new Color(248, 250, 252));

        // 1. Serialization Card
        JPanel card1 = createToolCard(
            "1. Object Serialization (IO Package)",
            "Exports model objects to binary 'backups/campus_backup.dat' via ObjectOutputStream in a background thread.",
            "Run Background Serialization Thread",
            new Color(37, 99, 235),
            e -> runSerializationDemo()
        );

        // 2. Deserialization Card
        JPanel card2 = createToolCard(
            "2. Object Deserialization & Filter",
            "Restores serialized Java objects using ObjectInputStream and ObjectInputFilter class validation.",
            "Deserialize & Validate Backup File",
            new Color(16, 185, 129),
            e -> runDeserializationDemo()
        );

        // 3. Pipe Streams Card
        JPanel card3 = createToolCard(
            "3. Inter-Thread Pipe Streams",
            "Demonstrates ReportProducer writing to PipedOutputStream and ReportConsumer reading from PipedInputStream.",
            "Run Producer-Consumer Pipe Stream Test",
            new Color(147, 51, 234),
            e -> runPipeStreamsDemo()
        );

        // 4. Activity File Logger Card
        JPanel card4 = createToolCard(
            "4. File I/O & Audit Logging",
            "Reads latest operations from 'logs/campus_activity.log' written using FileOutputStream & BufferedOutputStream.",
            "Read Activity Log File",
            new Color(217, 119, 6),
            e -> runReadLogFileDemo()
        );

        buttonsGrid.add(card1);
        buttonsGrid.add(card2);
        buttonsGrid.add(card3);
        buttonsGrid.add(card4);

        mainContent.add(buttonsGrid, BorderLayout.NORTH);

        // Output Terminal Console
        JPanel consolePanel = new JPanel(new BorderLayout(4, 4));
        consolePanel.setBackground(Color.WHITE);
        consolePanel.setBorder(BorderFactory.createTitledBorder("Educational Execution Output Console"));

        txtOutput = new JTextArea("Click any tool above to trigger and view execution output here...\n");
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtOutput.setBackground(new Color(15, 23, 42)); // Dark Navy Slate
        txtOutput.setForeground(new Color(241, 245, 249));
        txtOutput.setRows(10);

        consolePanel.add(new JScrollPane(txtOutput), BorderLayout.CENTER);
        mainContent.add(consolePanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createToolCard(String title, String desc, String btnText, Color btnColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblDesc = new JLabel("<html><body style='width: 240px; color: #64748B; font-size: 11px;'>" + desc + "</body></html>");

        JButton btn = new JButton(btnText);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(btnColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private void runSerializationDemo() {
        txtOutput.setText("[DEMO] Initializing BackupThread (extending Thread)...\n");
        txtOutput.append("[DEMO] Target File: " + BackupManager.getDefaultBackupPath() + "\n");

        BackupThread worker = new BackupThread(BackupManager.getDefaultBackupPath(), (success, msg) -> {
            SwingUtilities.invokeLater(() -> {
                txtOutput.append("[RESULT] " + msg + "\n");
                txtOutput.append("[OOP] java.io.ObjectOutputStream successfully serialized student, book & room records.\n");
                JOptionPane.showMessageDialog(this, msg, "Serialization Completed", JOptionPane.INFORMATION_MESSAGE);
            });
        });

        txtOutput.append("[THREAD STATE] Before start(): " + worker.getState() + "\n");
        worker.start();
        txtOutput.append("[THREAD STATE] After start(): " + worker.getState() + "\n");
    }

    private void runDeserializationDemo() {
        txtOutput.setText("[DEMO] Reading from " + BackupManager.getDefaultBackupPath() + " using ObjectInputStream...\n");
        new Thread(() -> {
            try {
                BackupManager.BackupData data = BackupManager.importBackup(BackupManager.getDefaultBackupPath());
                SwingUtilities.invokeLater(() -> {
                    txtOutput.append("[SUCCESS] Deserialization completed with ObjectInputFilter security whitelist!\n");
                    txtOutput.append("Restored Students count: " + data.getStudents().size() + "\n");
                    txtOutput.append("Restored Books count: " + data.getBooks().size() + "\n");
                    txtOutput.append("Restored Rooms count: " + data.getRooms().size() + "\n");
                    txtOutput.append("Backup Original Timestamp: " + data.getTimestamp() + "\n");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    txtOutput.append("[ERROR] Deserialization error: " + ex.getMessage() + "\n");
                    JOptionPane.showMessageDialog(this, 
                            "Could not deserialize backup. Please run Serialization first!\n" + ex.getMessage(), 
                            "Deserialization Notice", JOptionPane.WARNING_MESSAGE);
                });
            }
        }).start();
    }

    private void runPipeStreamsDemo() {
        txtOutput.setText("[DEMO] Setting up PipedOutputStream and PipedInputStream...\n");
        new Thread(() -> {
            String report = ReportManager.generatePipedReport("Campus Operational Diagnostic Report");
            SwingUtilities.invokeLater(() -> {
                txtOutput.append("[SUCCESS] Inter-thread pipe transfer completed!\n\n");
                txtOutput.append(report);
            });
        }).start();
    }

    private void runReadLogFileDemo() {
        txtOutput.setText("[DEMO] Reading 'logs/campus_activity.log' using BufferedReader...\n");
        List<String> logs = ActivityLogger.readRecentLogs(15);
        if (logs.isEmpty()) {
            txtOutput.append("Log file is currently empty or not yet generated.\n");
        } else {
            txtOutput.append("--- RECENT AUDIT LOGS ---\n");
            for (String l : logs) {
                txtOutput.append(l + "\n");
            }
        }
    }
}

