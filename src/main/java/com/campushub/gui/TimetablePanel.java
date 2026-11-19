package com.campushub.gui;

import com.campushub.dao.AcademicDAO;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Course;
import com.campushub.models.Faculty;
import com.campushub.models.Student;
import com.campushub.models.TimetableSlot;
import com.campushub.models.User;
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
import javax.swing.JComboBox;
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
 * UNIT 3: JAVA SWING GUI - CAMPUS TIMETABLE & LECTURE SCHEDULES
 * Provides role-specific lecture timetables:
 * - Faculty: Classes to be taken (Teaching Schedule)
 * - Student: Classes to attend (Enrolled Schedule by Semester & Section)
 * - HOD / Admin: Master campus scheduler
 */
public class TimetablePanel extends JPanel {

    private final User currentUser;
    private final AcademicDAO academicDAO;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFilterDay;
    private JComboBox<String> cbFilterSem;
    private JComboBox<String> cbFilterSec;
    private JLabel lblScheduleInfo;

    public TimetablePanel(User currentUser) {
        this.currentUser = currentUser;
        this.academicDAO = new AcademicDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadTimetable();
    }

    private void initComponents() {
        // Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        String title;
        if ("FACULTY".equalsIgnoreCase(currentUser.getRole())) {
            title = "My Teaching Schedule (Lectures to Conduct)";
        } else if ("STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            title = "My Weekly Class Attendance Timetable";
        } else {
            title = "Campus Master Timetable & Lecture Scheduling (HOD)";
        }

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        // Filter Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(Color.WHITE);

        controls.add(new JLabel("Day:"));
        cbFilterDay = new JComboBox<>(new String[]{"All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        cbFilterDay.setPreferredSize(new Dimension(100, 32));
        cbFilterDay.addActionListener(e -> loadTimetable());
        controls.add(cbFilterDay);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "HOD".equalsIgnoreCase(currentUser.getRole())) {
            controls.add(new JLabel("Sem:"));
            cbFilterSem = new JComboBox<>(new String[]{"All", "2", "3", "4", "5"});
            cbFilterSem.setPreferredSize(new Dimension(60, 32));
            cbFilterSem.addActionListener(e -> loadTimetable());
            controls.add(cbFilterSem);

            controls.add(new JLabel("Sec:"));
            cbFilterSec = new JComboBox<>(new String[]{"All", "A", "B"});
            cbFilterSec.setPreferredSize(new Dimension(60, 32));
            cbFilterSec.addActionListener(e -> loadTimetable());
            controls.add(cbFilterSec);

            JButton btnAddSlot = UIStyles.createPrimaryButton("Add Lecture Slot", IconType.PLUS);
            btnAddSlot.addActionListener(e -> showAddSlotDialog());
            controls.add(btnAddSlot);
        }

        JButton btnRefresh = UIStyles.createSecondaryButton("Refresh", IconType.DASHBOARD);
        btnRefresh.addActionListener(e -> loadTimetable());
        controls.add(btnRefresh);

        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"Day", "Time Slot", "Course Code", "Course Name", "Faculty Instructor", "Section", "Room / Lab Location"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        UIStyles.styleModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1));
        add(scroll, BorderLayout.CENTER);

        // Footer
        lblScheduleInfo = new JLabel("Class hours: 09:00 AM to 04:00 PM (Monday through Friday)");
        lblScheduleInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblScheduleInfo.setForeground(new Color(100, 116, 139));
        add(lblScheduleInfo, BorderLayout.SOUTH);
    }

    public void refreshData() {
        loadTimetable();
    }

    private void loadTimetable() {
        String filterDay = (String) cbFilterDay.getSelectedItem();

        new Thread(() -> {
            List<TimetableSlot> slots;

            if ("FACULTY".equalsIgnoreCase(currentUser.getRole())) {
                Faculty f = (currentUser instanceof Faculty) 
                        ? (Faculty) currentUser 
                        : AcademicDAO.findFacultyByEmpCode(currentUser.getUsername().replace("_", "-"));
                int facId = f != null ? f.getFacultyId() : 1;
                slots = academicDAO.getFacultyTimetable(facId);
            } else if ("STUDENT".equalsIgnoreCase(currentUser.getRole())) {
                Student s = (currentUser instanceof Student)
                        ? (Student) currentUser
                        : AcademicDAO.findStudentByUid(currentUser.getUsername());
                int sem = s != null ? s.getSemester() : 4;
                String sec = s != null ? s.getSection() : "A";
                slots = academicDAO.getStudentTimetable(sem, sec);
            } else {
                slots = academicDAO.getAllTimetableSlots();
            }

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (TimetableSlot ts : slots) {
                    if ("All Days".equals(filterDay) || ts.getDayOfWeek().equalsIgnoreCase(filterDay)) {
                        tableModel.addRow(new Object[]{
                            ts.getDayOfWeek(),
                            ts.getTimeSlot(),
                            ts.getCourseCode(),
                            ts.getCourseName(),
                            ts.getFacultyName(),
                            "Section " + ts.getSection(),
                            ts.getRoomName()
                        });
                    }
                }
                lblScheduleInfo.setText("Displaying " + tableModel.getRowCount() 
                        + " scheduled sessions. Teaching venues equipped with audio-visual smart podiums.");
            });
        }).start();
    }

    private void showAddSlotDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Schedule New Lecture Slot", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbDay = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        JComboBox<String> cbTime = new JComboBox<>(new String[]{"09:00 - 10:00", "10:00 - 11:00", "11:15 - 12:15", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00"});
        
        List<Course> courses = academicDAO.getAllCourses();
        JComboBox<Course> cbCourse = new JComboBox<>(courses.toArray(new Course[0]));

        List<Faculty> faculties = AcademicDAO.getRosterFaculty();
        JComboBox<Faculty> cbFac = new JComboBox<>(faculties.toArray(new Faculty[0]));

        JTextField txtRoom = new JTextField("LH-301", 15);
        JComboBox<String> cbSec = new JComboBox<>(new String[]{"A", "B", "C"});

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Day of Week:"), gbc);
        gbc.gridx = 1; panel.add(cbDay, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Time Slot:"), gbc);
        gbc.gridx = 1; panel.add(cbTime, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; panel.add(cbCourse, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1; panel.add(cbFac, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Room / Lab:"), gbc);
        gbc.gridx = 1; panel.add(txtRoom, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Section:"), gbc);
        gbc.gridx = 1; panel.add(cbSec, gbc);

        JButton btnSave = new JButton("Confirm Lecture Slot");
        btnSave.setBackground(new Color(22, 163, 74));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.addActionListener(e -> {
            Course c = (Course) cbCourse.getSelectedItem();
            Faculty f = (Faculty) cbFac.getSelectedItem();
            if (c != null && f != null) {
                TimetableSlot slot = new TimetableSlot(0, 
                        (String) cbDay.getSelectedItem(), 
                        (String) cbTime.getSelectedItem(), 
                        c.getCourseId(), f.getFacultyId(), 1, c.getSemester(), 
                        (String) cbSec.getSelectedItem());
                slot.setCourseCode(c.getCourseCode());
                slot.setCourseName(c.getCourseName());
                slot.setFacultyName(f.getName());
                slot.setRoomName(txtRoom.getText().trim());
                
                academicDAO.getAllTimetableSlots().add(slot);
                JOptionPane.showMessageDialog(dialog, "Class lecture slot scheduled!", "Scheduled", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadTimetable();
            }
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
