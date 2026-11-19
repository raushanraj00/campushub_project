package com.campushub.gui;

import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Student;
import com.campushub.models.User;
import com.campushub.services.StudentService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * UNIT 3: JAVA SWING GUI - STUDENT MANAGEMENT
 * Displays student records in a JTable and provides full CRUD dialogs.
 */
public class StudentPanel extends JPanel {

    private final StudentService studentService;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public StudentPanel(User currentUser) {
        this.currentUser = currentUser;
        this.studentService = new StudentService();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadStudentData();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("Student Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchControls.setBackground(Color.WHITE);

        txtSearch = new JTextField(16);
        txtSearch.setPreferredSize(new Dimension(180, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search UID or Name...");

        JButton btnSearch = UIStyles.createSecondaryButton("Search", IconType.SEARCH);
        btnSearch.addActionListener(e -> performSearch());

        JButton btnRefresh = UIStyles.createSecondaryButton("Refresh", IconType.DASHBOARD);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData();
        });

        searchControls.add(txtSearch);
        searchControls.add(btnSearch);
        searchControls.add(btnRefresh);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnAdd = UIStyles.createPrimaryButton("Add Student", IconType.PLUS);
            btnAdd.addActionListener(e -> showAddStudentDialog());
            searchControls.add(btnAdd);
        }

        topBar.add(searchControls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "UID", "Full Name", "Email", "Phone", "Department", "Semester", "Section"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        UIStyles.styleModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setBackground(UIStyles.BG_PAGE);

        JButton btnDetails = UIStyles.createSecondaryButton("View Polymorphic Details", IconType.USER);
        btnDetails.addActionListener(e -> viewSelectedStudentDetails());
        bottomBar.add(btnDetails);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnEdit = UIStyles.createSecondaryButton("Edit Student", IconType.EDIT);
            btnEdit.addActionListener(e -> editSelectedStudent());
            bottomBar.add(btnEdit);

            JButton btnDelete = UIStyles.createDangerButton("Delete Student", IconType.DELETE);
            btnDelete.addActionListener(e -> deleteSelectedStudent());
            bottomBar.add(btnDelete);
        }

        add(bottomBar, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 32));
        return btn;
    }

    public void loadStudentData() {
        new Thread(() -> {
            List<Student> students = studentService.listAllStudents();
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (Student s : students) {
                    tableModel.addRow(new Object[]{
                        s.getStudentId(),
                        s.getUid(),
                        s.getName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getDepartment(),
                        s.getSemester(),
                        s.getSection()
                    });
                }
            });
        }).start();
    }

    private void performSearch() {
        String query = txtSearch.getText().trim();
        new Thread(() -> {
            List<Student> results = studentService.searchStudents(query);
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (Student s : results) {
                    tableModel.addRow(new Object[]{
                        s.getStudentId(),
                        s.getUid(),
                        s.getName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getDepartment(),
                        s.getSemester(),
                        s.getSection()
                    });
                }
            });
        }).start();
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Register New Student", true);
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JTextField txtUid = new JTextField(15);
        JTextField txtName = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtPhone = new JTextField(15);
        JTextField txtDept = new JTextField("Computer Science", 15);
        JTextField txtSem = new JTextField("1", 15);
        JTextField txtSec = new JTextField("A", 15);

        addField(panel, gbc, 0, "Student UID:", txtUid);
        addField(panel, gbc, 1, "Full Name:", txtName);
        addField(panel, gbc, 2, "Email Address:", txtEmail);
        addField(panel, gbc, 3, "Phone Number:", txtPhone);
        addField(panel, gbc, 4, "Department:", txtDept);
        addField(panel, gbc, 5, "Semester (1-8):", txtSem);
        addField(panel, gbc, 6, "Section:", txtSec);

        JButton btnSave = new JButton("Save Student");
        btnSave.setBackground(new Color(22, 163, 74));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                int sem = Integer.parseInt(txtSem.getText().trim());
                Student s = new Student(
                    txtUid.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtDept.getText().trim(),
                    sem,
                    txtSec.getText().trim()
                );
                studentService.registerStudent(s);
                JOptionPane.showMessageDialog(dialog, "Student successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStudentData();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Semester must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void editSelectedStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(row, 0);
        String uid = (String) tableModel.getValueAt(row, 1);
        String currentName = (String) tableModel.getValueAt(row, 2);
        String currentEmail = (String) tableModel.getValueAt(row, 3);
        String currentPhone = (String) tableModel.getValueAt(row, 4);
        String currentDept = (String) tableModel.getValueAt(row, 5);
        int currentSem = (int) tableModel.getValueAt(row, 6);
        String currentSec = (String) tableModel.getValueAt(row, 7);

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Student: " + uid, true);
        dialog.setSize(400, 440);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JTextField txtName = new JTextField(currentName, 15);
        JTextField txtEmail = new JTextField(currentEmail, 15);
        JTextField txtPhone = new JTextField(currentPhone, 15);
        JTextField txtDept = new JTextField(currentDept, 15);
        JTextField txtSem = new JTextField(String.valueOf(currentSem), 15);
        JTextField txtSec = new JTextField(currentSec, 15);

        addField(panel, gbc, 0, "Full Name:", txtName);
        addField(panel, gbc, 1, "Email Address:", txtEmail);
        addField(panel, gbc, 2, "Phone Number:", txtPhone);
        addField(panel, gbc, 3, "Department:", txtDept);
        addField(panel, gbc, 4, "Semester (1-8):", txtSem);
        addField(panel, gbc, 5, "Section:", txtSec);

        JButton btnUpdate = new JButton("Update Record");
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> {
            try {
                int sem = Integer.parseInt(txtSem.getText().trim());
                Student s = new Student(studentId, uid, txtName.getText().trim(), txtEmail.getText().trim(),
                        txtPhone.getText().trim(), txtDept.getText().trim(), sem, txtSec.getText().trim(),
                        null, 0, uid, "", "");
                studentService.updateStudent(s);
                JOptionPane.showMessageDialog(dialog, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStudentData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(btnUpdate, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete student: " + name + " (ID: " + studentId + ")?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentService.removeStudent(studentId);
                JOptionPane.showMessageDialog(this, "Student record deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelectedStudentDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student row to view details.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Student s = new Student(
            (int) tableModel.getValueAt(row, 0),
            (String) tableModel.getValueAt(row, 1),
            (String) tableModel.getValueAt(row, 2),
            (String) tableModel.getValueAt(row, 3),
            (String) tableModel.getValueAt(row, 4),
            (String) tableModel.getValueAt(row, 5),
            (int) tableModel.getValueAt(row, 6),
            (String) tableModel.getValueAt(row, 7),
            null, 0, "", "", ""
        );

        // Demonstrates Polymorphic call: User.getDetails() overridden by Student.getDetails()
        User polyRef = s; // Upcasting to parent User
        JOptionPane.showMessageDialog(this, 
                polyRef.getDetails() + "\nRole: " + polyRef.getRole(), 
                "Polymorphic Method Overriding Demonstration", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addField(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(field, g);
    }
}
