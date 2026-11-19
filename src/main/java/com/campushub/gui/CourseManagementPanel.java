package com.campushub.gui;

import com.campushub.dao.AcademicDAO;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Course;
import com.campushub.models.CourseAssignment;
import com.campushub.models.Faculty;
import com.campushub.models.Student;
import com.campushub.models.StudentMark;
import com.campushub.models.User;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * UNIT 3: JAVA SWING GUI - HOD COURSE ALLOCATION & FACULTY PRIVILEGES
 * Enables Head of Department (HOD) to manage course offerings, assign subjects
 * and sections to faculties, configure faculty privileges, and look up records by Unique ID.
 */
public class CourseManagementPanel extends JPanel {

    private final User currentUser;
    private final AcademicDAO academicDAO;

    private JTable assignmentTable;
    private DefaultTableModel assignmentModel;

    // Faculty privileges controls
    private JTextField txtSearchEmpCode;
    private JLabel lblFacNameInfo;
    private JLabel lblFacDeptInfo;
    private JCheckBox chkUploadMarks;
    private JCheckBox chkViewStudents;
    private JCheckBox chkClassSchedule;
    private JCheckBox chkBookLabs;
    private Faculty currentSelectedFaculty = null;

    // Universal search controls
    private JTextField txtUniversalId;
    private JTextArea txtSearchResult;

    public CourseManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.academicDAO = new AcademicDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadAssignments();
    }

    private void initComponents() {
        // Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("HOD Control: Subject Allocation, Privileges & Faculty Tasks");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Subject Allocation & Faculty Assignments
        JPanel tabAssignments = createAssignmentsTab();
        tabbedPane.addTab("Subject Allocations & Courses", tabAssignments);

        // Tab 2: Faculty Privileges Management
        JPanel tabPrivileges = createPrivilegesTab();
        tabbedPane.addTab("Faculty Privileges & Tasks", tabPrivileges);

        // Tab 3: Universal Search by Unique ID (Student UID or Faculty EmpCode)
        JPanel tabSearch = createUniversalSearchTab();
        tabbedPane.addTab("Universal Unique ID Search", tabSearch);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAssignmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        toolbar.setBackground(Color.WHITE);

        JButton btnAddCourse = UIStyles.createPrimaryButton("Add Subject", IconType.PLUS);
        btnAddCourse.addActionListener(e -> showAddCourseDialog());

        JButton btnAssign = UIStyles.createPrimaryButton("Assign Subject to Faculty", IconType.CHECK);
        btnAssign.addActionListener(e -> showAssignSubjectDialog());

        JButton btnRefresh = UIStyles.createSecondaryButton("Refresh", IconType.DASHBOARD);
        btnRefresh.addActionListener(e -> loadAssignments());

        toolbar.add(btnAddCourse);
        toolbar.add(btnAssign);
        toolbar.add(btnRefresh);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Course Code", "Course Name", "Semester", "Section", "Assigned Faculty", "Emp Code", "Year"};
        assignmentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        assignmentTable = new JTable(assignmentModel);
        UIStyles.styleModernTable(assignmentTable);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(assignmentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPrivilegesTab() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Search Bar on top
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        searchBar.setBackground(Color.WHITE);
        searchBar.add(new JLabel("Enter Faculty Employee Code (e.g. FAC-CSE-01):"));

        txtSearchEmpCode = new JTextField("FAC-CSE-01", 14);
        txtSearchEmpCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnFindFac = new JButton("Load Faculty");
        btnFindFac.setBackground(new Color(37, 99, 235));
        btnFindFac.setForeground(Color.WHITE);
        btnFindFac.addActionListener(e -> loadFacultyForPrivileges());

        searchBar.add(txtSearchEmpCode);
        searchBar.add(btnFindFac);
        panel.add(searchBar, BorderLayout.NORTH);

        // Center card with details & checkboxes
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        lblFacNameInfo = new JLabel("Faculty Name: -");
        lblFacNameInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 0;
        card.add(lblFacNameInfo, gbc);

        lblFacDeptInfo = new JLabel("Department & Designation: -");
        lblFacDeptInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 1;
        card.add(lblFacDeptInfo, gbc);

        JLabel lblPrivTitle = new JLabel("Configurable Faculty Permissions & Task Privileges:");
        lblPrivTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrivTitle.setForeground(new Color(30, 58, 138));
        gbc.gridy = 2;
        card.add(lblPrivTitle, gbc);

        chkUploadMarks = new JCheckBox("UPLOAD_MARKS: Permission to enter and edit student marks for assigned courses");
        chkUploadMarks.setSelected(true);
        gbc.gridy = 3;
        card.add(chkUploadMarks, gbc);

        chkViewStudents = new JCheckBox("VIEW_STUDENTS: Access to student academic profiles and personal details");
        chkViewStudents.setSelected(true);
        gbc.gridy = 4;
        card.add(chkViewStudents, gbc);

        chkClassSchedule = new JCheckBox("CLASS_SCHEDULE: Permission to manage teaching timetable and reschedule slots");
        chkClassSchedule.setSelected(true);
        gbc.gridy = 5;
        card.add(chkClassSchedule, gbc);

        chkBookLabs = new JCheckBox("BOOK_LABS: Permission to reserve computer labs and specialized equipment");
        chkBookLabs.setSelected(true);
        gbc.gridy = 6;
        card.add(chkBookLabs, gbc);

        JButton btnSavePriv = new JButton("Save Privileges for Faculty");
        btnSavePriv.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSavePriv.setBackground(new Color(22, 163, 74));
        btnSavePriv.setForeground(Color.WHITE);
        btnSavePriv.setPreferredSize(new Dimension(240, 36));
        btnSavePriv.addActionListener(e -> saveFacultyPrivileges());
        gbc.gridy = 7;
        card.add(btnSavePriv, gbc);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUniversalSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        searchBar.setBackground(Color.WHITE);
        searchBar.add(new JLabel("Search by Unique ID (Student UID e.g. '25BCS0001' or Faculty EmpCode 'FAC-CSE-01'):"));

        txtUniversalId = new JTextField(16);
        txtUniversalId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnSearch = new JButton("Inspect Record");
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> performUniversalSearch());

        searchBar.add(txtUniversalId);
        searchBar.add(btnSearch);
        panel.add(searchBar, BorderLayout.NORTH);

        txtSearchResult = new JTextArea("Enter a Student UID or Faculty Employee Code above and click 'Inspect Record'...\n");
        txtSearchResult.setEditable(false);
        txtSearchResult.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtSearchResult.setBackground(new Color(15, 23, 42));
        txtSearchResult.setForeground(new Color(241, 245, 249));
        txtSearchResult.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(txtSearchResult), BorderLayout.CENTER);
        return panel;
    }

    public void refreshData() {
        loadAssignments();
    }

    public void loadAssignments() {
        new Thread(() -> {
            List<CourseAssignment> list = academicDAO.getAllAssignments();
            SwingUtilities.invokeLater(() -> {
                assignmentModel.setRowCount(0);
                for (CourseAssignment ca : list) {
                    assignmentModel.addRow(new Object[]{
                        ca.getAssignmentId(),
                        ca.getCourseCode(),
                        ca.getCourseName(),
                        ca.getSemester(),
                        ca.getSection(),
                        ca.getFacultyName(),
                        ca.getFacultyEmpCode(),
                        ca.getAcademicYear()
                    });
                }
            });
        }).start();
    }

    private void showAssignSubjectDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Assign Subject to Faculty (HOD)", true);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Course> courses = academicDAO.getAllCourses();
        JComboBox<Course> cbCourses = new JComboBox<>(courses.toArray(new Course[0]));

        List<Faculty> faculties = AcademicDAO.getRosterFaculty();
        JComboBox<Faculty> cbFaculty = new JComboBox<>(faculties.toArray(new Faculty[0]));

        JComboBox<String> cbSection = new JComboBox<>(new String[]{"Section A", "Section B", "Section C"});

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Select Subject:"), gbc);
        gbc.gridx = 1; panel.add(cbCourses, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Assign to Faculty:"), gbc);
        gbc.gridx = 1; panel.add(cbFaculty, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Student Section:"), gbc);
        gbc.gridx = 1; panel.add(cbSection, gbc);

        JButton btnConfirm = new JButton("Confirm Subject Assignment");
        btnConfirm.setBackground(new Color(22, 163, 74));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.addActionListener(e -> {
            Course selCourse = (Course) cbCourses.getSelectedItem();
            Faculty selFac = (Faculty) cbFaculty.getSelectedItem();
            String sec = ((String) cbSection.getSelectedItem()).replace("Section ", "");

            if (selCourse != null && selFac != null) {
                academicDAO.assignCourseToFaculty(selFac.getFacultyId(), selCourse.getCourseId(), sec);
                JOptionPane.showMessageDialog(dialog, 
                        "Subject " + selCourse.getCourseCode() + " successfully assigned to " 
                        + selFac.getName() + " for Section " + sec + "!", 
                        "Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAssignments();
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnConfirm, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Subject to Campus Catalog", true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtCode = new JTextField("CS501", 15);
        JTextField txtName = new JTextField("Machine Learning & Artificial Intelligence", 15);
        JTextField txtCredits = new JTextField("4", 15);
        JComboBox<String> cbDept = new JComboBox<>(new String[]{"Computer Science", "Electronics & Telecom", "Bio-Engineering", "Computer Applications"});
        JTextField txtSem = new JTextField("5", 15);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1; panel.add(txtCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1; panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1; panel.add(txtCredits, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; panel.add(cbDept, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Semester (1-8):"), gbc);
        gbc.gridx = 1; panel.add(txtSem, gbc);

        JButton btnSave = new JButton("Save Subject to Catalog");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.addActionListener(e -> {
            try {
                int cr = Integer.parseInt(txtCredits.getText().trim());
                int sm = Integer.parseInt(txtSem.getText().trim());
                Course c = new Course(txtCode.getText().trim(), txtName.getText().trim(), cr, 
                        (String) cbDept.getSelectedItem(), sm);
                academicDAO.addCourse(c);
                JOptionPane.showMessageDialog(dialog, "Course " + c.getCourseCode() + " added!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAssignments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void loadFacultyForPrivileges() {
        String emp = txtSearchEmpCode.getText().trim();
        Faculty f = AcademicDAO.findFacultyByEmpCode(emp);
        if (f == null) {
            JOptionPane.showMessageDialog(this, "No faculty found with Employee Code: " + emp, "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentSelectedFaculty = f;
        lblFacNameInfo.setText("Faculty Name: " + f.getName() + " (" + f.getEmpCode() + ")");
        lblFacDeptInfo.setText("Department: " + f.getDepartment() + " | Designation: " + f.getDesignation());

        chkUploadMarks.setSelected(f.hasPrivilege("UPLOAD_MARKS"));
        chkViewStudents.setSelected(f.hasPrivilege("VIEW_STUDENTS"));
        chkClassSchedule.setSelected(f.hasPrivilege("CLASS_SCHEDULE"));
        chkBookLabs.setSelected(f.hasPrivilege("BOOK_LABS"));
    }

    private void saveFacultyPrivileges() {
        if (currentSelectedFaculty == null) {
            JOptionPane.showMessageDialog(this, "Please load a faculty member first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (chkUploadMarks.isSelected()) sb.append("UPLOAD_MARKS,");
        if (chkViewStudents.isSelected()) sb.append("VIEW_STUDENTS,");
        if (chkClassSchedule.isSelected()) sb.append("CLASS_SCHEDULE,");
        if (chkBookLabs.isSelected()) sb.append("BOOK_LABS,");

        currentSelectedFaculty.setPrivileges(sb.toString());
        JOptionPane.showMessageDialog(this, 
                "Privileges updated for " + currentSelectedFaculty.getName() + "!\nActive privileges: " + currentSelectedFaculty.getPrivileges(), 
                "Privileges Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void performUniversalSearch() {
        String query = txtUniversalId.getText().trim();
        if (query.isEmpty()) return;

        // 1. Try finding Student
        Student s = AcademicDAO.findStudentByUid(query);
        if (s != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("=================================================================\n");
            sb.append("                 STUDENT PROFILE INSPECTION (HOD)                \n");
            sb.append("=================================================================\n");
            sb.append(" Student UID      : ").append(s.getUid()).append("\n");
            sb.append(" Full Name        : ").append(s.getName()).append("\n");
            sb.append(" Admission Year   : ").append(s.getAdmissionYear()).append("\n");
            sb.append(" Academic Branch  : ").append(s.getBranchCode()).append(" (").append(s.getDepartment()).append(")\n");
            sb.append(" Current Semester : ").append(s.getSemester()).append(" | Section: ").append(s.getSection()).append("\n");
            sb.append(" Campus Email     : ").append(s.getEmail()).append("\n");
            sb.append(" Phone Number     : ").append(s.getPhone()).append("\n\n");
            sb.append("--- ACADEMIC PERFORMANCE & MARKS REPORT ---\n");

            List<StudentMark> marks = academicDAO.getStudentReportCard(s.getStudentId());
            if (marks.isEmpty()) {
                sb.append(" (No marks published for this student yet)\n");
            } else {
                for (StudentMark sm : marks) {
                    sb.append(String.format(" %-10s %-32s | Int1: %4.1f | Int2: %4.1f | EndSem: %4.1f | Total: %5.1f | Grade: %s%n",
                            sm.getCourseCode(), sm.getCourseName(), sm.getInternal1(), sm.getInternal2(), 
                            sm.getEndSem(), sm.getTotalMarks(), sm.getGrade()));
                }
            }
            sb.append("=================================================================\n");
            txtSearchResult.setText(sb.toString());
            return;
        }

        // 2. Try finding Faculty
        Faculty f = AcademicDAO.findFacultyByEmpCode(query.toUpperCase().replace("_", "-"));
        if (f != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("=================================================================\n");
            sb.append("                 FACULTY DOSSIER & TASKS (HOD)                   \n");
            sb.append("=================================================================\n");
            sb.append(" Employee Code    : ").append(f.getEmpCode()).append("\n");
            sb.append(" Professor Name   : ").append(f.getName()).append("\n");
            sb.append(" Department       : ").append(f.getDepartment()).append("\n");
            sb.append(" Designation      : ").append(f.getDesignation()).append("\n");
            sb.append(" Official Email   : ").append(f.getEmail()).append("\n");
            sb.append(" Phone Contact    : ").append(f.getPhone()).append("\n");
            sb.append(" Assigned Rights  : ").append(f.getPrivileges()).append("\n\n");

            sb.append("--- ASSIGNED COURSES & SECTIONS ---\n");
            List<CourseAssignment> assignments = academicDAO.getFacultyAssignments(f.getFacultyId());
            if (assignments.isEmpty()) {
                sb.append(" (No courses currently assigned to this faculty member)\n");
            } else {
                for (CourseAssignment ca : assignments) {
                    sb.append(" • ").append(ca.getCourseCode()).append(" - ").append(ca.getCourseName())
                      .append(" (Section ").append(ca.getSection()).append(", Sem ").append(ca.getSemester()).append(")\n");
                }
            }
            sb.append("=================================================================\n");
            txtSearchResult.setText(sb.toString());
            return;
        }

        txtSearchResult.setText("No Student or Faculty found with ID: " + query + "\n\nFormat Examples:\n • Student: 25BCS0001, 25BET0010, 25BBE0005, 25MCA0002\n • Faculty: FAC-CSE-01, FAC-ECE-02, FAC-BBE-01");
    }
}
