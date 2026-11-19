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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * UNIT 1 & UNIT 3: SWING GUI - STUDENT MARKS UPLOAD & EVALUATION
 * Role-Based Marks Interface:
 * - Faculty: Uploads and edits component marks (Internal 1, Internal 2, Assignment, End Sem)
 * - Student: Views their official academic grades and performance report card
 * - HOD / Admin: Audits and edits marks across all courses and sections
 */
public class MarksPanel extends JPanel {

    private final User currentUser;
    private final AcademicDAO academicDAO;

    private JComboBox<Course> cbCourses;
    private JComboBox<String> cbSections;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblContextInfo;

    public MarksPanel(User currentUser) {
        this.currentUser = currentUser;
        this.academicDAO = new AcademicDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        String title = "STUDENT".equalsIgnoreCase(currentUser.getRole())
                ? "My Academic Marks & Grade Report Card"
                : "Faculty Marks Management & Grade Computation";

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        // Filter / Selector controls for Faculty & Admin
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(Color.WHITE);

        if (!"STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            controls.add(new JLabel("Subject:"));
            List<Course> courses = academicDAO.getAllCourses();
            cbCourses = new JComboBox<>(courses.toArray(new Course[0]));
            cbCourses.setPreferredSize(new Dimension(220, 32));
            cbCourses.addActionListener(e -> loadMarksData());
            controls.add(cbCourses);

            controls.add(new JLabel("Section:"));
            cbSections = new JComboBox<>(new String[]{"A", "B", "C"});
            cbSections.setPreferredSize(new Dimension(60, 32));
            cbSections.addActionListener(e -> loadMarksData());
            controls.add(cbSections);

            JButton btnLoad = new JButton("Load Students");
            btnLoad.setBackground(new Color(37, 99, 235));
            btnLoad.setForeground(Color.WHITE);
            btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnLoad.addActionListener(e -> loadMarksData());
            controls.add(btnLoad);
        }

        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = "STUDENT".equalsIgnoreCase(currentUser.getRole())
                ? new String[]{"Course Code", "Course Name", "Internal 1 (20)", "Internal 2 (20)", "Assignment (10)", "End Sem (50)", "Total (100)", "Letter Grade"}
                : new String[]{"Student UID", "Student Name", "Section", "Internal 1 (20)", "Internal 2 (20)", "Assignment (10)", "End Sem (50)", "Total (100)", "Grade"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Students cannot edit their report card!
                if ("STUDENT".equalsIgnoreCase(currentUser.getRole())) return false;
                // For Faculty/HOD: columns 3, 4, 5, 6 (Internal1, Internal2, Ass, EndSem) are editable
                return col >= 3 && col <= 6;
            }
        };

        table = new JTable(tableModel);
        UIStyles.styleModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1));
        add(scroll, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UIStyles.BG_PAGE);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));

        lblContextInfo = new JLabel("Grading Scale: A+ (>=90) | A (>=80) | B+ (>=70) | B (>=60) | C (>=50) | P (>=40) | F (<40)");
        lblContextInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblContextInfo.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblContextInfo, BorderLayout.WEST);

        if (!"STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btnPanel.setBackground(UIStyles.BG_PAGE);

            JButton btnSaveMarks = UIStyles.createPrimaryButton("Save & Calculate Grades", IconType.CHECK);
            btnSaveMarks.addActionListener(e -> saveEditedMarks());
            btnPanel.add(btnSaveMarks);

            bottomBar.add(btnPanel, BorderLayout.EAST);
        }

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void refreshData() {
        loadInitialData();
    }

    private void loadInitialData() {
        if ("STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            loadStudentReportCard();
        } else {
            loadMarksData();
        }
    }

    private void loadStudentReportCard() {
        new Thread(() -> {
            Student student = (currentUser instanceof Student) 
                    ? (Student) currentUser 
                    : AcademicDAO.findStudentByUid(currentUser.getUsername());
            
            int stdId = (student != null) ? student.getStudentId() : 1;
            List<StudentMark> marks = academicDAO.getStudentReportCard(stdId);

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                double totalAgg = 0.0;
                for (StudentMark sm : marks) {
                    tableModel.addRow(new Object[]{
                        sm.getCourseCode(),
                        sm.getCourseName(),
                        sm.getInternal1(),
                        sm.getInternal2(),
                        sm.getAssignment(),
                        sm.getEndSem(),
                        sm.getTotalMarks(),
                        sm.getGrade()
                    });
                    totalAgg += sm.getTotalMarks();
                }

                if (!marks.isEmpty()) {
                    double gpa = (totalAgg / marks.size()) / 10.0;
                    lblContextInfo.setText(String.format("Report Card for %s (UID: %s) | Overall Percentage: %.2f%% | Estimated GPA: %.2f / 10.0",
                            (student != null ? student.getName() : currentUser.getUsername()),
                            (student != null ? student.getUid() : "N/A"),
                            (totalAgg / marks.size()), gpa));
                }
            });
        }).start();
    }

    private void loadMarksData() {
        Course course = (Course) cbCourses.getSelectedItem();
        String section = (String) cbSections.getSelectedItem();
        if (course == null) return;

        new Thread(() -> {
            List<StudentMark> list = academicDAO.getMarksByCourseAndSection(course.getCourseId(), section);
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (StudentMark sm : list) {
                    tableModel.addRow(new Object[]{
                        sm.getStudentUid(),
                        sm.getStudentName(),
                        sm.getSection(),
                        sm.getInternal1(),
                        sm.getInternal2(),
                        sm.getAssignment(),
                        sm.getEndSem(),
                        sm.getTotalMarks(),
                        sm.getGrade()
                    });
                }
                lblContextInfo.setText("Loaded " + list.size() + " enrolled students in " 
                        + course.getCourseCode() + " (Section " + section + "). Edit values in table and click Save.");
            });
        }).start();
    }

    private void saveEditedMarks() {
        Course course = (Course) cbCourses.getSelectedItem();
        if (course == null) return;

        int rowCount = tableModel.getRowCount();
        int updated = 0;

        for (int r = 0; r < rowCount; r++) {
            String uid = (String) tableModel.getValueAt(r, 0);
            Student s = AcademicDAO.findStudentByUid(uid);
            if (s != null) {
                try {
                    double in1 = Double.parseDouble(tableModel.getValueAt(r, 3).toString().trim());
                    double in2 = Double.parseDouble(tableModel.getValueAt(r, 4).toString().trim());
                    double ass = Double.parseDouble(tableModel.getValueAt(r, 5).toString().trim());
                    double end = Double.parseDouble(tableModel.getValueAt(r, 6).toString().trim());

                    StudentMark mark = new StudentMark(0, s.getStudentId(), course.getCourseId(), 
                            in1, in2, ass, end, 
                            currentUser instanceof Faculty ? ((Faculty) currentUser).getFacultyId() : 1);
                    mark.setStudentUid(s.getUid());
                    mark.setStudentName(s.getName());
                    mark.setCourseCode(course.getCourseCode());
                    mark.setCourseName(course.getCourseName());
                    mark.setSection(s.getSection());

                    academicDAO.saveOrUpdateMark(mark);

                    // Update computed Total & Grade in table view
                    tableModel.setValueAt(mark.getTotalMarks(), r, 7);
                    tableModel.setValueAt(mark.getGrade(), r, 8);
                    updated++;
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, 
                            "Invalid numeric mark for student: " + uid, "Format Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, 
                "Successfully saved and recalculated grades for " + updated + " students in " + course.getCourseCode() + "!", 
                "Grades Published", JOptionPane.INFORMATION_MESSAGE);
    }
}
