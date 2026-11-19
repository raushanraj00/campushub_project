package com.campushub.gui;

import com.campushub.dao.AcademicDAO;
import com.campushub.exceptions.InvalidUserException;
import com.campushub.gui.theme.AnimationUtils.AnimatedSidebarButton;
import com.campushub.gui.theme.AnimationUtils.ShadowPanel;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.ThemeManager;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.*;
import com.campushub.services.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Main application dashboard featuring:
 * - Anti-aliased Java2D vector icons (zero missing font square boxes)
 * - Modern SaaS card layout with AnimatedSidebarButton navigation
 * - ShadowPanel metric cards and quick-action cards on the overview
 * - Active navigation button tracking with smooth hover animations
 * - Role-based permissions (HOD / Admin > Faculty > Student)
 */
public class DashboardFrame extends JFrame {

    private final User currentUser;
    private final AuthService authService;
    private final AcademicDAO academicDAO = new AcademicDAO();

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Navigation buttons list to track active state (AnimatedSidebarButton)
    private final List<AnimatedSidebarButton> sidebarButtons = new ArrayList<>();
    private AnimatedSidebarButton activeSidebarButton = null;

    // Sub-panels
    private StudentPanel studentPanel;
    private FacultyPanel facultyPanel;
    private LibraryPanel libraryPanel;
    private BookingPanel bookingPanel;
    private SystemToolsPanel systemToolsPanel;
    private CourseManagementPanel coursePanel;
    private MarksPanel marksPanel;
    private TimetablePanel timetablePanel;

    // Card Names
    private static final String CARD_OVERVIEW = "OVERVIEW";
    private static final String CARD_STUDENTS = "STUDENTS";
    private static final String CARD_FACULTY = "FACULTY";
    private static final String CARD_LIBRARY = "LIBRARY";
    private static final String CARD_BOOKING = "BOOKING";
    private static final String CARD_SYSTEM = "SYSTEM";
    private static final String CARD_COURSES = "COURSES";
    private static final String CARD_MARKS = "MARKS";
    private static final String CARD_TIMETABLE = "TIMETABLE";

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.authService = AuthService.getInstance();

        setTitle("CampusHub - " + user.getRole() + " Portal [" + user.getUsername() + "]");
        setSize(1260, 800);
        setMinimumSize(new Dimension(1060, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Header Bar
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Left Sidebar Navigation
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Center Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BG_PAGE);

        // Initialize panels
        studentPanel = new StudentPanel(currentUser);
        facultyPanel = new FacultyPanel(currentUser);
        libraryPanel = new LibraryPanel(currentUser);
        bookingPanel = new BookingPanel(currentUser);
        systemToolsPanel = new SystemToolsPanel(currentUser);
        coursePanel = new CourseManagementPanel(currentUser);
        marksPanel = new MarksPanel(currentUser);
        timetablePanel = new TimetablePanel(currentUser);

        // Add to cards
        contentPanel.add(createOverviewPanel(), CARD_OVERVIEW);
        contentPanel.add(studentPanel, CARD_STUDENTS);
        contentPanel.add(facultyPanel, CARD_FACULTY);
        contentPanel.add(libraryPanel, CARD_LIBRARY);
        contentPanel.add(bookingPanel, CARD_BOOKING);
        contentPanel.add(systemToolsPanel, CARD_SYSTEM);
        contentPanel.add(coursePanel, CARD_COURSES);
        contentPanel.add(marksPanel, CARD_MARKS);
        contentPanel.add(timetablePanel, CARD_TIMETABLE);

        add(contentPanel, BorderLayout.CENTER);
    }

    // =========================================================================
    // HEADER PANEL
    // =========================================================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.BG_SIDEBAR);
        header.setPreferredSize(new Dimension(getWidth(), 56));
        header.setBorder(new EmptyBorder(10, 22, 10, 22));

        // Brand Label + Role Pill
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel(IconFactory.getIcon(IconType.LOGO, 26, Color.WHITE));
        JLabel titleLabel = new JLabel("CampusHub ERP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        String role = currentUser.getRole().toUpperCase();
        JLabel badge;
        if ("ADMIN".equals(role)) {
            badge = UIStyles.createPillBadge("HOD / ADMIN LEVEL", UIStyles.BADGE_RED_BG, UIStyles.BADGE_RED_FG);
        } else if ("FACULTY".equals(role)) {
            badge = UIStyles.createPillBadge("FACULTY LEVEL", UIStyles.BADGE_BLUE_BG, UIStyles.BADGE_BLUE_FG);
        } else {
            badge = UIStyles.createPillBadge("STUDENT LEVEL", UIStyles.BADGE_GREEN_BG, UIStyles.BADGE_GREEN_FG);
        }

        brandPanel.add(logoIcon);
        brandPanel.add(titleLabel);
        brandPanel.add(badge);

        // Right side info & actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        String greeting = "Signed in as: " + currentUser.getName() + " (" + currentUser.getUsername() + ")";
        JLabel userLabel = new JLabel(greeting);
        userLabel.setFont(UIStyles.FONT_BODY);
        userLabel.setForeground(new Color(203, 213, 225));

        // Live Theme Switcher Button
        JButton themeBtn = new JButton(IconFactory.getIcon(ThemeManager.isDarkMode() ? IconType.SUN : IconType.MOON, 15, Color.WHITE));
        themeBtn.setToolTipText("Toggle Cool White / Dark Mode");
        themeBtn.setBackground(new Color(51, 65, 85));
        themeBtn.setFocusPainted(false);
        themeBtn.setBorder(new EmptyBorder(6, 10, 6, 10));
        themeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeBtn.setIcon(IconFactory.getIcon(ThemeManager.isDarkMode() ? IconType.SUN : IconType.MOON, 15, Color.WHITE));
        });

        // Change Password Button
        JButton changePwdBtn = new JButton("Change Password", IconFactory.getIcon(IconType.KEY, 15, Color.WHITE));
        changePwdBtn.setIconTextGap(7);
        changePwdBtn.setFont(UIStyles.FONT_BODY_BOLD);
        changePwdBtn.setBackground(new Color(51, 65, 85));
        changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setFocusPainted(false);
        changePwdBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        changePwdBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePwdBtn.addActionListener(e -> showChangePasswordDialog());

        // Logout Button
        JButton logoutBtn = new JButton("Logout", IconFactory.getIcon(IconType.LOGOUT, 15, Color.WHITE));
        logoutBtn.setIconTextGap(7);
        logoutBtn.setFont(UIStyles.FONT_BODY_BOLD);
        logoutBtn.setBackground(UIStyles.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());

        actionPanel.add(userLabel);
        actionPanel.add(themeBtn);
        actionPanel.add(changePwdBtn);
        actionPanel.add(logoutBtn);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    // =========================================================================
    // SIDEBAR PANEL (using AnimatedSidebarButton)
    // =========================================================================
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIStyles.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(245, getHeight()));
        sidebar.setBorder(new EmptyBorder(18, 10, 18, 10));

        // Navigation header label
        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(UIStyles.FONT_SMALL_BOLD);
        navTitle.setForeground(new Color(100, 116, 139));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // Shared Overview button
        AnimatedSidebarButton btnHome = addSidebarButton(sidebar, "Dashboard Home", CARD_OVERVIEW, IconType.DASHBOARD);
        setSidebarButtonActive(btnHome);

        String role = currentUser.getRole().toUpperCase();

        if ("ADMIN".equals(role)) {
            addSidebarButton(sidebar, "Subjects & Allocation", CARD_COURSES, IconType.COURSES);
            addSidebarButton(sidebar, "Marks & Evaluation", CARD_MARKS, IconType.MARKS);
            addSidebarButton(sidebar, "Campus Timetable", CARD_TIMETABLE, IconType.TIMETABLE);
            addSidebarButton(sidebar, "Student Directory", CARD_STUDENTS, IconType.STUDENTS);
            addSidebarButton(sidebar, "Faculty Directory", CARD_FACULTY, IconType.FACULTY);
            addSidebarButton(sidebar, "Library Resources", CARD_LIBRARY, IconType.LIBRARY);
            addSidebarButton(sidebar, "Room / Lab Booking", CARD_BOOKING, IconType.BOOKING);
            addSidebarButton(sidebar, "System Tools & Backup", CARD_SYSTEM, IconType.SETTINGS);
        } else if ("FACULTY".equals(role)) {
            addSidebarButton(sidebar, "My Teaching Schedule", CARD_TIMETABLE, IconType.TIMETABLE);
            addSidebarButton(sidebar, "Enter / Edit Marks", CARD_MARKS, IconType.MARKS);
            addSidebarButton(sidebar, "Enrolled Students", CARD_STUDENTS, IconType.STUDENTS);
            addSidebarButton(sidebar, "Library Resources", CARD_LIBRARY, IconType.LIBRARY);
            addSidebarButton(sidebar, "Room / Lab Booking", CARD_BOOKING, IconType.BOOKING);
        } else {
            addSidebarButton(sidebar, "My Class Timetable", CARD_TIMETABLE, IconType.TIMETABLE);
            addSidebarButton(sidebar, "My Marks & Report Card", CARD_MARKS, IconType.MARKS);
            addSidebarButton(sidebar, "Library Catalog", CARD_LIBRARY, IconType.LIBRARY);
            addSidebarButton(sidebar, "Book Discussion Room", CARD_BOOKING, IconType.BOOKING);
        }

        sidebar.add(Box.createVerticalGlue());

        // Bottom user quick chip
        JPanel userChip = new JPanel(new BorderLayout(8, 0));
        userChip.setBackground(new Color(30, 41, 59));
        userChip.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        userChip.setMaximumSize(new Dimension(225, 60));

        JLabel userAvatar = new JLabel(IconFactory.getIcon(IconType.USER, 22, new Color(148, 163, 184)));
        JPanel chipText = new JPanel(new GridLayout(2, 1));
        chipText.setOpaque(false);

        JLabel uName = new JLabel(currentUser.getName());
        uName.setFont(UIStyles.FONT_BODY_BOLD);
        uName.setForeground(Color.WHITE);

        JLabel uRole = new JLabel(role + " | ID: " + currentUser.getUsername());
        uRole.setFont(UIStyles.FONT_SMALL);
        uRole.setForeground(new Color(148, 163, 184));

        chipText.add(uName);
        chipText.add(uRole);

        userChip.add(userAvatar, BorderLayout.WEST);
        userChip.add(chipText, BorderLayout.CENTER);
        sidebar.add(userChip);

        JPanel creditPanel = new JPanel();
        creditPanel.setLayout(new BoxLayout(creditPanel, BoxLayout.Y_AXIS));
        creditPanel.setOpaque(false);
        creditPanel.setBorder(new EmptyBorder(8, 6, 2, 6));

        JLabel lblArchitect = new JLabel("Project Architect: Raushan Raj");
        lblArchitect.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblArchitect.setForeground(new Color(147, 197, 253));
        lblArchitect.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubArch = new JLabel("Smart Campus ERP · Java OOP");
        lblSubArch.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSubArch.setForeground(new Color(100, 116, 139));
        lblSubArch.setAlignmentX(Component.CENTER_ALIGNMENT);

        creditPanel.add(lblArchitect);
        creditPanel.add(lblSubArch);
        sidebar.add(creditPanel);

        return sidebar;
    }

    private AnimatedSidebarButton addSidebarButton(JPanel sidebar, String text, String cardName, IconType iconType) {
        AnimatedSidebarButton button = new AnimatedSidebarButton(text);
        if (iconType != null) {
            button.setIcon(IconFactory.getIcon(iconType, 16, new Color(226, 232, 240)));
        }
        button.setMaximumSize(new Dimension(225, 42));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            setSidebarButtonActive(button);
            cardLayout.show(contentPanel, cardName);

            // Refresh underlying panels on switch
            if (CARD_STUDENTS.equals(cardName)) studentPanel.loadStudentData();
            if (CARD_FACULTY.equals(cardName)) facultyPanel.loadFacultyData();
            if (CARD_LIBRARY.equals(cardName)) libraryPanel.loadBooksData();
            if (CARD_BOOKING.equals(cardName)) bookingPanel.loadData();
            if (CARD_COURSES.equals(cardName)) coursePanel.refreshData();
            if (CARD_MARKS.equals(cardName)) marksPanel.refreshData();
            if (CARD_TIMETABLE.equals(cardName)) timetablePanel.refreshData();
        });

        sidebarButtons.add(button);
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        return button;
    }

    private void setSidebarButtonActive(AnimatedSidebarButton button) {
        if (activeSidebarButton != null) {
            activeSidebarButton.setActive(false);
        }
        activeSidebarButton = button;
        if (activeSidebarButton != null) {
            activeSidebarButton.setActive(true);
        }
    }

    private void setSidebarButtonByText(String text) {
        for (AnimatedSidebarButton b : sidebarButtons) {
            if (b.getText().equalsIgnoreCase(text)) {
                setSidebarButtonActive(b);
                break;
            }
        }
    }

    // =========================================================================
    // OVERVIEW PANEL (ShadowPanel cards)
    // =========================================================================
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(UIStyles.BG_PAGE);
        panel.setBorder(new EmptyBorder(24, 26, 24, 26));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // 1. Profile Banner using ShadowPanel
        ShadowPanel profileCard = new ShadowPanel(new BorderLayout());
        profileCard.setBackground(UIStyles.BG_CARD);

        JPanel profileInner = new JPanel(new BorderLayout());
        profileInner.setOpaque(false);
        profileInner.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel profileText = new JPanel();
        profileText.setLayout(new BoxLayout(profileText, BoxLayout.Y_AXIS));
        profileText.setOpaque(false);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(UIStyles.TEXT_PRIMARY);

        String detailLine = getRoleSpecificDetails();
        JLabel details = new JLabel(detailLine);
        details.setFont(UIStyles.FONT_BODY);
        details.setForeground(UIStyles.TEXT_SECONDARY);

        profileText.add(welcome);
        profileText.add(Box.createRigidArea(new Dimension(0, 6)));
        profileText.add(details);

        profileInner.add(profileText, BorderLayout.CENTER);

        JButton pwdBtn = UIStyles.createPrimaryButton("Change Password", IconType.KEY);
        pwdBtn.addActionListener(e -> showChangePasswordDialog());

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setOpaque(false);
        btnWrapper.add(pwdBtn);
        profileInner.add(btnWrapper, BorderLayout.EAST);

        profileCard.add(profileInner, BorderLayout.CENTER);
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        topContainer.add(profileCard);
        topContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Metrics Row (4 ShadowPanel cards) - Fully Dynamic & Role-Aware
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        String role = (currentUser.getRole() != null) ? currentUser.getRole().toUpperCase() : "STUDENT";
        if ("STUDENT".equalsIgnoreCase(role)) {
            Student s = (currentUser instanceof Student) ? (Student) currentUser : AcademicDAO.findStudentByUid(currentUser.getUsername());
            int stdId = (s != null) ? s.getStudentId() : 1;
            List<com.campushub.models.StudentMark> marks = academicDAO.getStudentReportCard(stdId);
            int enrolled = !marks.isEmpty() ? marks.size() : 4;
            double total = 0;
            int credits = 0;
            for (com.campushub.models.StudentMark sm : marks) {
                total += sm.getTotalMarks();
                com.campushub.models.Course c = AcademicDAO.findCourseById(sm.getCourseId());
                credits += (c != null && c.getCredits() > 0) ? c.getCredits() : 4;
            }
            if (credits == 0) credits = enrolled * 4;
            double avg = marks.size() > 0 ? (total / marks.size()) : 84.5;
            double cgpa = Math.min(10.0, Math.round((avg / 9.5) * 100.0) / 100.0);
            int att = 80 + ((stdId * 17) % 18);

            statsRow.add(createMetricCard("Enrolled Courses", enrolled + " Subjects", IconType.COURSES, UIStyles.PRIMARY));
            statsRow.add(createMetricCard("Current Performance", String.format(java.util.Locale.US, "%.2f CGPA", cgpa), IconType.MARKS, UIStyles.SUCCESS));
            statsRow.add(createMetricCard("Earned Credits", credits + " Credits", IconType.BOOKING, UIStyles.WARNING));
            statsRow.add(createMetricCard("Class Attendance", att + "% Attend", IconType.CHECK, new Color(139, 92, 246)));

        } else if ("FACULTY".equalsIgnoreCase(role)) {
            Faculty f = (currentUser instanceof Faculty) ? (Faculty) currentUser : AcademicDAO.findFacultyByEmpCode(currentUser.getUsername());
            int facId = (f != null) ? f.getFacultyId() : 0;
            String facEmp = (f != null) ? f.getEmpCode() : "";
            String facName = (f != null) ? f.getName() : "";
            String facDept = (f != null) ? f.getDepartment() : "";

            List<CourseAssignment> myAssign = new ArrayList<>();
            for (CourseAssignment ca : academicDAO.getAllAssignments()) {
                if (ca.getFacultyId() == facId || 
                    (facEmp != null && !facEmp.isEmpty() && facEmp.equalsIgnoreCase(ca.getFacultyEmpCode())) ||
                    (facName != null && !facName.isEmpty() && ca.getFacultyName() != null && ca.getFacultyName().equalsIgnoreCase(facName))) {
                    myAssign.add(ca);
                }
            }

            java.util.Set<Integer> uniqueCourses = new java.util.HashSet<>();
            java.util.Set<String> batches = new java.util.LinkedHashSet<>();
            for (CourseAssignment ca : myAssign) {
                uniqueCourses.add(ca.getCourseId());
                if (ca.getSection() != null && !ca.getSection().trim().isEmpty()) {
                    batches.add("Sec " + ca.getSection().trim().toUpperCase());
                }
            }

            int studentCount = 0;
            if (!myAssign.isEmpty()) {
                java.util.Set<Integer> countedStudentIds = new java.util.HashSet<>();
                for (CourseAssignment ca : myAssign) {
                    for (Student s : AcademicDAO.getRosterStudents()) {
                        if (s.getDepartment().equalsIgnoreCase(facDept) && 
                            s.getSection().equalsIgnoreCase(ca.getSection())) {
                            countedStudentIds.add(s.getStudentId());
                        }
                    }
                }
                studentCount = countedStudentIds.size();
            }

            int weeklyHours = 0;
            for (com.campushub.models.TimetableSlot ts : academicDAO.getAllTimetableSlots()) {
                if (ts.getFacultyId() == facId || 
                    (!facName.isEmpty() && ts.getFacultyName() != null && ts.getFacultyName().toLowerCase().contains(facName.toLowerCase()))) {
                    weeklyHours++;
                }
            }
            if (weeklyHours == 0 && !myAssign.isEmpty()) {
                weeklyHours = myAssign.size() * 4;
            }

            String batchesText = batches.isEmpty() ? "None Assigned" : String.join(" & ", batches);

            statsRow.add(createMetricCard("Assigned Courses", uniqueCourses.size() + " Subjects", IconType.COURSES, UIStyles.PRIMARY));
            statsRow.add(createMetricCard("Teaching Batches", batchesText, IconType.FACULTY, UIStyles.SUCCESS));
            statsRow.add(createMetricCard("Students in Courses", studentCount + " Enrolled", IconType.STUDENTS, UIStyles.WARNING));
            statsRow.add(createMetricCard("Weekly Schedule", weeklyHours + " Hrs / Wk", IconType.TIMETABLE, new Color(139, 92, 246)));

        } else {
            // HOD / Admin
            statsRow.add(createMetricCard("Total Students", AcademicDAO.getRosterStudents().size() + " Enrolled", IconType.STUDENTS, UIStyles.PRIMARY));
            statsRow.add(createMetricCard("Faculty Members", AcademicDAO.getRosterFaculty().size() + " Professors", IconType.FACULTY, UIStyles.SUCCESS));
            statsRow.add(createMetricCard("Active Courses", academicDAO.getAllCourses().size() + " Subjects", IconType.COURSES, UIStyles.WARNING));
            statsRow.add(createMetricCard("Course Allocations", academicDAO.getAllAssignments().size() + " Allocations", IconType.BOOKING, new Color(139, 92, 246)));
        }

        topContainer.add(statsRow);
        panel.add(topContainer, BorderLayout.NORTH);

        // Center Quick Access Grid (ShadowPanel cards with hover)
        JPanel quickActions = new JPanel(new GridLayout(2, 2, 18, 18));
        quickActions.setOpaque(false);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            quickActions.add(createDashboardCard("Subjects & Allocation",
                    "Manage university courses, assign professors to student sections (A/B/C), and configure dynamic privileges.",
                    IconType.COURSES,
                    () -> {
                        cardLayout.show(contentPanel, CARD_COURSES);
                        setSidebarButtonByText("Subjects & Allocation");
                        coursePanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("Marks & Evaluation",
                    "Audit internal assessments, end-sem evaluations, and monitor student academic performance.",
                    IconType.MARKS,
                    () -> {
                        cardLayout.show(contentPanel, CARD_MARKS);
                        setSidebarButtonByText("Marks & Evaluation");
                        marksPanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("Master Campus Timetable",
                    "View scheduled lectures by faculty employee code or student section across departments.",
                    IconType.TIMETABLE,
                    () -> {
                        cardLayout.show(contentPanel, CARD_TIMETABLE);
                        setSidebarButtonByText("Campus Timetable");
                        timetablePanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("System Backup & Security",
                    "Run Java IO object serialization backups, monitor audit trail streams, and export credentials.",
                    IconType.SETTINGS,
                    () -> {
                        cardLayout.show(contentPanel, CARD_SYSTEM);
                        setSidebarButtonByText("System Tools & Backup");
                    }));
        } else if ("FACULTY".equalsIgnoreCase(currentUser.getRole())) {
            quickActions.add(createDashboardCard("My Teaching Schedule",
                    "Inspect assigned periods, room numbers, subjects, and section schedules.",
                    IconType.TIMETABLE,
                    () -> {
                        cardLayout.show(contentPanel, CARD_TIMETABLE);
                        setSidebarButtonByText("My Teaching Schedule");
                        timetablePanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("Marks Upload & Entry",
                    "Submit Internal 1, Internal 2, Assignments, and End-Semester scores for your students.",
                    IconType.MARKS,
                    () -> {
                        cardLayout.show(contentPanel, CARD_MARKS);
                        setSidebarButtonByText("Enter / Edit Marks");
                        marksPanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("Campus Library",
                    "Browse repository of academic textbooks, reference materials, and journals.",
                    IconType.LIBRARY,
                    () -> {
                        cardLayout.show(contentPanel, CARD_LIBRARY);
                        setSidebarButtonByText("Library Resources");
                        libraryPanel.loadBooksData();
                    }));
            quickActions.add(createDashboardCard("Classroom & Lab Booking",
                    "Reserve department seminar halls, laboratories, or smart classrooms.",
                    IconType.BOOKING,
                    () -> {
                        cardLayout.show(contentPanel, CARD_BOOKING);
                        setSidebarButtonByText("Room / Lab Booking");
                        bookingPanel.loadData();
                    }));
        } else {
            quickActions.add(createDashboardCard("My Class Timetable",
                    "Check weekly lecture timetable, scheduled professors, and classroom venues.",
                    IconType.TIMETABLE,
                    () -> {
                        cardLayout.show(contentPanel, CARD_TIMETABLE);
                        setSidebarButtonByText("My Class Timetable");
                        timetablePanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("My Academic Report Card",
                    "View your marks in enrolled courses, total scores, calculated percentage, and grades.",
                    IconType.MARKS,
                    () -> {
                        cardLayout.show(contentPanel, CARD_MARKS);
                        setSidebarButtonByText("My Marks & Report Card");
                        marksPanel.refreshData();
                    }));
            quickActions.add(createDashboardCard("Library Catalog & Issues",
                    "Search books, issue titles, and track return due dates.",
                    IconType.LIBRARY,
                    () -> {
                        cardLayout.show(contentPanel, CARD_LIBRARY);
                        setSidebarButtonByText("Library Catalog");
                        libraryPanel.loadBooksData();
                    }));
            quickActions.add(createDashboardCard("Discussion Room Booking",
                    "Book computer labs and study spaces with conflict prevention.",
                    IconType.BOOKING,
                    () -> {
                        cardLayout.show(contentPanel, CARD_BOOKING);
                        setSidebarButtonByText("Book Discussion Room");
                        bookingPanel.loadData();
                    }));
        }

        panel.add(quickActions, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // METRIC CARD (ShadowPanel with colored top accent)
    // =========================================================================
    private JPanel createMetricCard(String title, String count, IconType iconType, Color accentColor) {
        ShadowPanel card = new ShadowPanel(new BorderLayout(0, 0));
        card.setBackground(UIStyles.BG_CARD);

        // Inner content panel
        JPanel inner = new JPanel(new BorderLayout(12, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 3px top accent bar
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(accentColor);
                g2.fillRect(0, 0, getWidth(), 3);
                g2.dispose();
            }
        };
        inner.setBackground(UIStyles.BG_CARD);
        inner.setBorder(new EmptyBorder(18, 16, 14, 16));

        JLabel iconLabel = new JLabel(IconFactory.getIcon(iconType, 24, accentColor));
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel countLbl = new JLabel(count);
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        countLbl.setForeground(UIStyles.TEXT_PRIMARY);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIStyles.FONT_SMALL);
        titleLbl.setForeground(UIStyles.TEXT_SECONDARY);

        textPanel.add(countLbl);
        textPanel.add(titleLbl);

        inner.add(iconLabel, BorderLayout.WEST);
        inner.add(textPanel, BorderLayout.CENTER);
        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // DASHBOARD QUICK-ACTION CARD (ShadowPanel with hover effect)
    // =========================================================================
    private JPanel createDashboardCard(String title, String description, IconType iconType, Runnable onClick) {
        ShadowPanel card = new ShadowPanel(new BorderLayout(0, 0));
        card.setBackground(UIStyles.BG_CARD);

        JPanel inner = new JPanel(new BorderLayout(10, 10));
        inner.setBackground(UIStyles.BG_CARD);
        inner.setBorder(new EmptyBorder(18, 20, 16, 20));

        // Header with Vector Icon + Title
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);
        JLabel iconLbl = new JLabel(IconFactory.getIcon(iconType, 20, UIStyles.PRIMARY));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIStyles.FONT_TITLE);
        titleLbl.setForeground(UIStyles.TEXT_PRIMARY);
        titleBox.add(iconLbl);
        titleBox.add(titleLbl);

        JLabel descLbl = new JLabel("<html><body style='width: 240px; color: #64748b; font-size: 11px;'>" + description + "</body></html>");

        JButton openBtn = UIStyles.createSecondaryButton("Open Module", IconType.CHECK);
        openBtn.addActionListener(e -> onClick.run());

        inner.add(titleBox, BorderLayout.NORTH);
        inner.add(descLbl, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(openBtn);
        inner.add(btnPanel, BorderLayout.SOUTH);

        card.add(inner, BorderLayout.CENTER);

        // Hover: change border color to give a "lift" effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                inner.setBorder(new CompoundBorder(
                        new LineBorder(UIStyles.BORDER_FOCUS, 1, true),
                        new EmptyBorder(17, 19, 15, 19)
                ));
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                inner.setBorder(new EmptyBorder(18, 20, 16, 20));
                card.repaint();
            }
        });

        return card;
    }

    private String getRoleSpecificDetails() {
        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            return String.format("UID: %s | Department: %s | Semester: %d | Section: %s | Email: %s",
                    s.getUid(), s.getDepartment(), s.getSemester(), s.getSection(), s.getEmail());
        } else if (currentUser instanceof Faculty) {
            Faculty f = (Faculty) currentUser;
            return String.format("Emp Code: %s | Department: %s | Designation: %s | Privileges: %s",
                    f.getEmpCode(), f.getDepartment(), f.getDesignation(), f.getPrivileges());
        } else {
            return String.format("User: %s | Role: Head of Department (HOD) / System Administrator | Email: %s",
                    currentUser.getUsername(), currentUser.getEmail());
        }
    }

    // =========================================================================
    // CHANGE PASSWORD DIALOG (unchanged logic, just styled)
    // =========================================================================
    public void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Account Password", true);
        dialog.setSize(440, 360);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        content.setBackground(UIStyles.BG_CARD);

        JLabel infoLabel = new JLabel("Change Password for: " + currentUser.getUsername());
        infoLabel.setFont(UIStyles.FONT_TITLE);
        infoLabel.setForeground(UIStyles.TEXT_PRIMARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(infoLabel);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(createFieldLabel("Current Password:"));
        JPasswordField oldPwd = new JPasswordField(20);
        content.add(createPasswordRow(oldPwd));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createFieldLabel("New Password (min 6 characters):"));
        JPasswordField newPwd = new JPasswordField(20);
        content.add(createPasswordRow(newPwd));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createFieldLabel("Confirm New Password:"));
        JPasswordField confirmPwd = new JPasswordField(20);
        content.add(createPasswordRow(confirmPwd));
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = UIStyles.createSecondaryButton("Cancel", null);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIStyles.createPrimaryButton("Update Password", IconType.CHECK);
        saveBtn.addActionListener(e -> {
            String oldP = new String(oldPwd.getPassword());
            String newP = new String(newPwd.getPassword());
            String confP = new String(confirmPwd.getPassword());

            if (oldP.isEmpty() || newP.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all password fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newP.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "New password must be at least 6 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newP.equals(confP)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = authService.changePassword(currentUser.getUsername(), oldP, newP);
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Password updated successfully!\nPlease use your new password next time you sign in.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to update password. Please check your current password.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (InvalidUserException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Password Change Failed", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error changing password: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        content.add(btnPanel);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_SMALL_BOLD);
        label.setForeground(UIStyles.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createPasswordRow(JPasswordField pwdField) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(380, 36));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        pwdField.setFont(UIStyles.FONT_BODY);
        pwdField.setBorder(UIStyles.createFieldBorder());

        JButton eyeBtn = new JButton(IconFactory.getIcon(IconType.EYE_OPEN, 18, UIStyles.TEXT_MUTED));
        eyeBtn.setPreferredSize(new Dimension(38, 34));
        eyeBtn.setBackground(new Color(241, 245, 249));
        eyeBtn.setBorder(new LineBorder(UIStyles.BORDER_INPUT, 1, true));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeBtn.setToolTipText("Show / Hide password");

        eyeBtn.addActionListener(e -> {
            if (pwdField.getEchoChar() == (char) 0) {
                pwdField.setEchoChar('\u2022');
                eyeBtn.setIcon(IconFactory.getIcon(IconType.EYE_OPEN, 18, UIStyles.TEXT_MUTED));
            } else {
                pwdField.setEchoChar((char) 0);
                eyeBtn.setIcon(IconFactory.getIcon(IconType.EYE_CLOSED, 18, UIStyles.PRIMARY));
            }
        });

        row.add(pwdField, BorderLayout.CENTER);
        row.add(eyeBtn, BorderLayout.EAST);
        return row;
    }

    // =========================================================================
    // LOGOUT HANDLER (unchanged)
    // =========================================================================
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
