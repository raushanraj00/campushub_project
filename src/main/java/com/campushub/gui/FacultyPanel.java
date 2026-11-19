package com.campushub.gui;

import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Faculty;
import com.campushub.models.User;
import com.campushub.services.FacultyService;
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
 * UNIT 3: JAVA SWING GUI - FACULTY MANAGEMENT
 */
public class FacultyPanel extends JPanel {

    private final FacultyService facultyService;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public FacultyPanel(User currentUser) {
        this.currentUser = currentUser;
        this.facultyService = new FacultyService();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadFacultyData();
    }

    private void initComponents() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("Faculty Directory & Staff Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(Color.WHITE);

        JButton btnRefresh = UIStyles.createSecondaryButton("Refresh", IconType.DASHBOARD);
        btnRefresh.addActionListener(e -> loadFacultyData());
        controls.add(btnRefresh);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnAdd = UIStyles.createPrimaryButton("Add Faculty", IconType.PLUS);
            btnAdd.addActionListener(e -> showAddFacultyDialog());
            controls.add(btnAdd);
        }

        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Emp Code", "Full Name", "Email", "Phone", "Department", "Designation"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        UIStyles.styleModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1));
        add(scroll, BorderLayout.CENTER);

        // Actions
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            bottomBar.setBackground(UIStyles.BG_PAGE);

            JButton btnDelete = UIStyles.createDangerButton("Delete Faculty", IconType.DELETE);
            btnDelete.addActionListener(e -> deleteSelectedFaculty());
            bottomBar.add(btnDelete);

            add(bottomBar, BorderLayout.SOUTH);
        }
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

    public void loadFacultyData() {
        new Thread(() -> {
            List<Faculty> list = facultyService.listAllFaculty();
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (Faculty f : list) {
                    tableModel.addRow(new Object[]{
                        f.getFacultyId(),
                        f.getEmpCode(),
                        f.getName(),
                        f.getEmail(),
                        f.getPhone(),
                        f.getDepartment(),
                        f.getDesignation()
                    });
                }
            });
        }).start();
    }

    private void showAddFacultyDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Register Faculty Member", true);
        dialog.setSize(380, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtEmp = new JTextField(15);
        JTextField txtName = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtPhone = new JTextField(15);
        JTextField txtDept = new JTextField("Computer Science", 15);
        JTextField txtDesig = new JTextField("Assistant Professor", 15);

        addField(panel, gbc, 0, "Employee Code:", txtEmp);
        addField(panel, gbc, 1, "Full Name:", txtName);
        addField(panel, gbc, 2, "Email Address:", txtEmail);
        addField(panel, gbc, 3, "Phone Number:", txtPhone);
        addField(panel, gbc, 4, "Department:", txtDept);
        addField(panel, gbc, 5, "Designation:", txtDesig);

        JButton btnSave = new JButton("Save Faculty");
        btnSave.setBackground(new Color(22, 163, 74));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                Faculty f = new Faculty(
                    txtEmp.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtDept.getText().trim(),
                    txtDesig.getText().trim()
                );
                facultyService.registerFaculty(f);
                JOptionPane.showMessageDialog(dialog, "Faculty successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadFacultyData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedFaculty() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a faculty record to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int facId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete faculty member: " + name + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facultyService.deleteFaculty(facId);
                JOptionPane.showMessageDialog(this, "Record deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadFacultyData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

