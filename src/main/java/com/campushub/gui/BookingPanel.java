package com.campushub.gui;

import com.campushub.exceptions.BookingConflictException;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Booking;
import com.campushub.models.Room;
import com.campushub.models.User;
import com.campushub.services.BookingService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * UNIT 1 (INNER CLASSES) & UNIT 2 (THREAD SYNCHRONIZATION) & UNIT 3 (JDBC TRANSACTIONS)
 * BookingPanel manages campus classroom and computer laboratory bookings.
 */
public class BookingPanel extends JPanel {

    private final BookingService bookingService;
    private final User currentUser;
    private JTable roomTable;
    private DefaultTableModel roomTableModel;
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;

    public BookingPanel(User currentUser) {
        this.currentUser = currentUser;
        this.bookingService = new BookingService();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("Classroom, Computer Lab & Seminar Hall Bookings");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(Color.WHITE);

        JButton btnRefresh = UIStyles.createSecondaryButton("Refresh", IconType.DASHBOARD);
        btnRefresh.addActionListener(e -> loadData());
        controls.add(btnRefresh);

        JButton btnReserve = UIStyles.createPrimaryButton("Book Room", IconType.PLUS);
        btnReserve.addActionListener(e -> showBookRoomDialog());
        controls.add(btnReserve);

        // Teacher Demonstration Button: Multithreading & Synchronization Race Condition Test
        JButton btnThreadSim = UIStyles.createDangerButton("Demo: Test Concurrent Collision", IconType.ALERT);
        btnThreadSim.setToolTipText("Spawns 2 concurrent threads attempting overlapping reservation to test synchronization & rollback");
        btnThreadSim.addActionListener(e -> runConcurrencyTest());
        controls.add(btnThreadSim);

        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Split view: Top Table (Rooms), Bottom Table (Bookings)
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 12, 12));
        tablesPanel.setBackground(UIStyles.BG_PAGE);

        // 1. Rooms Table
        JPanel roomSection = new JPanel(new BorderLayout(4, 4));
        roomSection.setBackground(Color.WHITE);
        roomSection.setBorder(BorderFactory.createTitledBorder("Campus Facilities & Rooms"));

        String[] roomCols = {"Room ID", "Facility Name", "Type", "Building", "Capacity", "Status"};
        roomTableModel = new DefaultTableModel(roomCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(roomTableModel);
        UIStyles.styleModernTable(roomTable);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomSection.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        tablesPanel.add(roomSection);

        // 2. Bookings Table
        JPanel bookingSection = new JPanel(new BorderLayout(4, 4));
        bookingSection.setBackground(Color.WHITE);
        bookingSection.setBorder(BorderFactory.createTitledBorder("Active Reservations & Schedule"));

        String[] bookCols = {"Booking ID", "Facility", "Reserved By", "Start Time", "End Time", "Purpose", "Status"};
        bookingTableModel = new DefaultTableModel(bookCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        UIStyles.styleModernTable(bookingTable);
        bookingTable.setRowHeight(26);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingSection.add(new JScrollPane(bookingTable), BorderLayout.CENTER);
        tablesPanel.add(bookingSection);

        add(tablesPanel, BorderLayout.CENTER);

        // Bottom Actions: Inner Class Demonstration & Cancel
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setBackground(new Color(248, 250, 252));

        JButton btnInnerClassPass = createButton("Print Reservation Pass (Inner Class Demo)", new Color(79, 70, 229));
        btnInnerClassPass.addActionListener(e -> showReservationPass());
        bottomBar.add(btnInnerClassPass);

        JButton btnCancel = createButton("Cancel Booking", new Color(220, 38, 38));
        btnCancel.addActionListener(e -> cancelSelectedBooking());
        bottomBar.add(btnCancel);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void loadData() {
        new Thread(() -> {
            List<Room> rooms = bookingService.listAllRooms();
            List<Booking> bookings = bookingService.listAllBookings();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

            SwingUtilities.invokeLater(() -> {
                roomTableModel.setRowCount(0);
                for (Room r : rooms) {
                    roomTableModel.addRow(new Object[]{
                        r.getRoomId(),
                        r.getRoomName(),
                        r.getRoomType(),
                        r.getBuilding(),
                        r.getCapacity(),
                        r.isAvailable() ? "Available" : "Occupied"
                    });
                }

                bookingTableModel.setRowCount(0);
                for (Booking b : bookings) {
                    bookingTableModel.addRow(new Object[]{
                        b.getBookingId(),
                        b.getRoomName(),
                        b.getUserName(),
                        b.getStartTime().format(dtf),
                        b.getEndTime().format(dtf),
                        b.getPurpose(),
                        b.getStatus()
                    });
                }
            });
        }).start();
    }

    private void showBookRoomDialog() {
        int roomRow = roomTable.getSelectedRow();
        int initialRoomId = (roomRow != -1) ? (int) roomTableModel.getValueAt(roomRow, 0) : 1;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Reserve Classroom / Laboratory", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtRoomId = new JTextField(String.valueOf(initialRoomId), 15);
        JTextField txtStart = new JTextField(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).toString(), 15);
        JTextField txtEnd = new JTextField(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).toString(), 15);
        JTextField txtPurpose = new JTextField("OOP Java Laboratory Practical", 15);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Room ID:"), gbc);
        gbc.gridx = 1; panel.add(txtRoomId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Start Time (ISO):"), gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("End Time (ISO):"), gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Purpose:"), gbc);
        gbc.gridx = 1; panel.add(txtPurpose, gbc);

        JButton btnConfirm = new JButton("Confirm Reservation");
        btnConfirm.setBackground(new Color(37, 99, 235));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> {
            try {
                int rId = Integer.parseInt(txtRoomId.getText().trim());
                LocalDateTime s = LocalDateTime.parse(txtStart.getText().trim());
                LocalDateTime end = LocalDateTime.parse(txtEnd.getText().trim());
                int userId = (currentUser != null && currentUser.getUserId() > 0) ? currentUser.getUserId() : 1;

                bookingService.createBooking(userId, rId, s, end, txtPurpose.getText().trim());
                JOptionPane.showMessageDialog(dialog, "Room reserved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            } catch (BookingConflictException bce) {
                JOptionPane.showMessageDialog(dialog, bce.getMessage(), "Reservation Conflict", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnConfirm, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    /**
     * UNIT 2 & UNIT 3: DEMONSTRATING CONCURRENT BOOKING COLLISION & SYNCHRONIZATION
     */
    private void runConcurrencyTest() {
        int roomRow = roomTable.getSelectedRow();
        int roomId = (roomRow != -1) ? (int) roomTableModel.getValueAt(roomRow, 0) : 1;

        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        JDialog progressDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Concurrent Thread Collision Test", true);
        progressDialog.setSize(540, 360);
        progressDialog.setLocationRelativeTo(this);

        JTextArea txtReport = new JTextArea("Launching concurrent test threads...\n");
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReport.setBackground(new Color(15, 23, 42)); // Slate dark
        txtReport.setForeground(new Color(241, 245, 249));
        progressDialog.add(new JScrollPane(txtReport));

        new Thread(() -> {
            String report = bookingService.runConcurrentBookingSimulation(roomId, 1, 2, start, end);
            SwingUtilities.invokeLater(() -> {
                txtReport.setText(report);
                loadData();
            });
        }).start();

        progressDialog.setVisible(true);
    }

    /**
     * UNIT 1: DEMONSTRATING NON-STATIC MEMBER INNER CLASS
     * Uses Booking.BookingDetails to generate formatted pass.
     */
    private void showReservationPass() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a confirmed booking from the table to print pass.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(row, 0);
        String facility = (String) bookingTableModel.getValueAt(row, 1);
        String userName = (String) bookingTableModel.getValueAt(row, 2);
        String purpose = (String) bookingTableModel.getValueAt(row, 5);

        Booking b = new Booking(bookingId, 1, 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), purpose, "CONFIRMED");
        b.setRoomName(facility);
        b.setUserName(userName);

        // Instantating the non-static inner class:
        Booking.BookingDetails pass = b.getBookingDetails();

        JTextArea area = new JTextArea(pass.getReservationPass());
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Inner Class (Booking.BookingDetails) Demonstration", JOptionPane.PLAIN_MESSAGE);
    }

    private void cancelSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Cancel Booking #" + bookingId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.cancelBooking(bookingId);
                JOptionPane.showMessageDialog(this, "Booking cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

