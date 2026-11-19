package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.models.Course;
import com.campushub.models.CourseAssignment;
import com.campushub.models.Faculty;
import com.campushub.models.Student;
import com.campushub.models.StudentMark;
import com.campushub.models.TimetableSlot;
import com.campushub.utilities.CredentialsExporter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UNIT 1 & UNIT 3: ACADEMIC DATA ACCESS OBJECT
 * Manages Courses, Faculty Subject Assignments, Student Marks, and Timetable Schedules.
 * Supports both live MySQL persistence and in-memory high-capacity seed repository.
 */
public class AcademicDAO {

    // In-memory repositories for high-capacity demo (250+ students, 25+ faculty)
    private static final List<Course> MEMORY_COURSES = new ArrayList<>();
    private static final List<CourseAssignment> MEMORY_ASSIGNMENTS = new ArrayList<>();
    private static final List<StudentMark> MEMORY_MARKS = new ArrayList<>();
    private static final List<TimetableSlot> MEMORY_TIMETABLE = new ArrayList<>();
    private static final List<Student> ROSTER_STUDENTS = new ArrayList<>();
    private static final List<Faculty> ROSTER_FACULTY = new ArrayList<>();

    private static boolean initialized = false;

    static {
        bootstrapAcademicData();
    }

    public static synchronized void bootstrapAcademicData() {
        if (initialized) return;

        // 1. Generate Courses Catalog
        initCourses();

        // 2. Generate 25 Faculty Members
        initFaculties();

        // 3. Generate 260 Students with 25BCS, 25BET, 25BBE, 25MCA UIDs
        initStudents();

        // 4. Generate Course Assignments (HOD assigning courses to faculties)
        initAssignments();

        // 5. Generate Weekly Timetable
        initTimetables();

        // 6. Generate Baseline Student Marks
        initMarks();

        // 7. Export master password reference file in main directory
        CredentialsExporter.exportMasterCredentials(ROSTER_STUDENTS, ROSTER_FACULTY);

        initialized = true;
    }

    private static void initCourses() {
        MEMORY_COURSES.add(new Course(1, "CS201", "Data Structures & Algorithms", 4, "Computer Science", 3));
        MEMORY_COURSES.add(new Course(2, "CS202", "Object-Oriented Programming (Java)", 4, "Computer Science", 4));
        MEMORY_COURSES.add(new Course(3, "CS301", "Database Management Systems", 4, "Computer Science", 4));
        MEMORY_COURSES.add(new Course(4, "CS302", "Operating Systems", 4, "Computer Science", 4));
        MEMORY_COURSES.add(new Course(5, "CS401", "Computer Networks", 4, "Computer Science", 5));
        MEMORY_COURSES.add(new Course(6, "CS402", "Software Engineering", 3, "Computer Science", 5));

        MEMORY_COURSES.add(new Course(7, "EC201", "Digital Electronics & Logic Design", 4, "Electronics & Telecom", 3));
        MEMORY_COURSES.add(new Course(8, "EC202", "Signals and Systems", 4, "Electronics & Telecom", 4));
        MEMORY_COURSES.add(new Course(9, "EC301", "Microprocessors & Microcontrollers", 4, "Electronics & Telecom", 4));
        MEMORY_COURSES.add(new Course(10, "EC302", "VLSI Design", 4, "Electronics & Telecom", 5));

        MEMORY_COURSES.add(new Course(11, "BE201", "Biomaterials & Biomechanics", 4, "Bio-Engineering", 3));
        MEMORY_COURSES.add(new Course(12, "BE202", "Biomedical Instrumentation", 4, "Bio-Engineering", 4));

        MEMORY_COURSES.add(new Course(13, "MCA101", "Advanced Java & Web Frameworks", 4, "Computer Applications", 2));
        MEMORY_COURSES.add(new Course(14, "MCA102", "Cloud Computing & DevOps", 4, "Computer Applications", 2));
    }

    private static void initFaculties() {
        String[][] facData = {
            {"FAC-CSE-01", "Dr. Ramesh Kumar", "ramesh.kumar@campus.edu", "Computer Science", "Professor & HOD"},
            {"FAC-CSE-02", "Dr. Anita Desai", "anita.desai@campus.edu", "Computer Science", "Professor"},
            {"FAC-CSE-03", "Prof. Vikram Malhotra", "vikram.m@campus.edu", "Computer Science", "Associate Professor"},
            {"FAC-CSE-04", "Prof. Neha Singhal", "neha.s@campus.edu", "Computer Science", "Assistant Professor"},
            {"FAC-CSE-05", "Prof. Rajesh Verma", "rajesh.v@campus.edu", "Computer Science", "Assistant Professor"},
            {"FAC-CSE-06", "Prof. Sunita Rao", "sunita.r@campus.edu", "Computer Science", "Assistant Professor"},
            {"FAC-CSE-07", "Prof. Manoj Joshi", "manoj.j@campus.edu", "Computer Science", "Assistant Professor"},
            {"FAC-CSE-08", "Prof. Deepa Nair", "deepa.n@campus.edu", "Computer Science", "Lecturer"},
            {"FAC-CSE-09", "Prof. Alok Tripathi", "alok.t@campus.edu", "Computer Science", "Lecturer"},
            {"FAC-CSE-10", "Prof. Kavita Reddy", "kavita.r@campus.edu", "Computer Science", "Assistant Professor"},

            {"FAC-ECE-01", "Dr. S. K. Roy", "sk.roy@campus.edu", "Electronics & Telecom", "Professor & HOD"},
            {"FAC-ECE-02", "Dr. Manisha Sen", "manisha.s@campus.edu", "Electronics & Telecom", "Professor"},
            {"FAC-ECE-03", "Prof. Rakesh Ghosh", "rakesh.g@campus.edu", "Electronics & Telecom", "Associate Professor"},
            {"FAC-ECE-04", "Prof. Tanvi Mehra", "tanvi.m@campus.edu", "Electronics & Telecom", "Assistant Professor"},
            {"FAC-ECE-05", "Prof. Arun Saxena", "arun.s@campus.edu", "Electronics & Telecom", "Assistant Professor"},
            {"FAC-ECE-06", "Prof. Pallavi Sharma", "pallavi.s@campus.edu", "Electronics & Telecom", "Assistant Professor"},

            {"FAC-BBE-01", "Dr. Vandana Paul", "vandana.p@campus.edu", "Bio-Engineering", "Professor & HOD"},
            {"FAC-BBE-02", "Prof. Harish Bhat", "harish.b@campus.edu", "Bio-Engineering", "Associate Professor"},
            {"FAC-BBE-03", "Prof. Ritu Kapoor", "ritu.k@campus.edu", "Bio-Engineering", "Assistant Professor"},
            {"FAC-BBE-04", "Prof. Sanjay Mishra", "sanjay.m@campus.edu", "Bio-Engineering", "Assistant Professor"},

            {"FAC-MCA-01", "Dr. Praveen Bansal", "praveen.b@campus.edu", "Computer Applications", "Professor & HOD"},
            {"FAC-MCA-02", "Prof. Archana Bose", "archana.b@campus.edu", "Computer Applications", "Associate Professor"},
            {"FAC-MCA-03", "Prof. Gourav Mehta", "gourav.m@campus.edu", "Computer Applications", "Assistant Professor"},
            {"FAC-MCA-04", "Prof. Divya Krishnan", "divya.k@campus.edu", "Computer Applications", "Assistant Professor"},
            {"FAC-MCA-05", "Prof. Tarun Aggarwal", "tarun.a@campus.edu", "Computer Applications", "Assistant Professor"}
        };

        for (int i = 0; i < facData.length; i++) {
            String[] d = facData[i];
            String username = d[0].toLowerCase().replace("-", "_");
            Faculty f = new Faculty(i + 1, d[0], d[1], d[2], "98765432" + (10 + i), d[3], d[4],
                    100 + i, username, 
                    com.campushub.services.AuthService.DEMO_FACULTY_HASH, 
                    com.campushub.services.AuthService.DEMO_SALT);
            f.setPrivileges("UPLOAD_MARKS,VIEW_STUDENTS,CLASS_SCHEDULE,BOOK_LABS");
            ROSTER_FACULTY.add(f);
        }
    }

    private static void initStudents() {
        String[] firstNames = {
            "Aarav", "Diya", "Rohan", "Ananya", "Ishaan", "Pooja", "Aditya", "Sneha", "Kunal", "Meera",
            "Varun", "Rhea", "Kabir", "Tanvi", "Siddharth", "Simran", "Aryan", "Nisha", "Harsh", "Kritika",
            "Nikhil", "Shreya", "Gaurav", "Priyanka", "Akash", "Anushka", "Dev", "Isha", "Rahul", "Swati"
        };
        String[] lastNames = {
            "Sharma", "Patel", "Gupta", "Verma", "Singh", "Reddy", "Kumar", "Deshmukh", "Chopra", "Iyer",
            "Bhatia", "Joshi", "Kapoor", "Saxena", "Mehta", "Nair", "Pandey", "Mishra", "Das", "Menon"
        };

        int studentCount = 1;

        // 1. BCS 2025 batch (80 students across Sec A & B)
        for (int i = 1; i <= 80; i++) {
            String uid = String.format("25BCS%04d", i);
            String name = firstNames[(i * 3) % firstNames.length] + " " + lastNames[(i * 7) % lastNames.length];
            String sec = (i <= 40) ? "A" : "B";
            ROSTER_STUDENTS.add(new Student(studentCount, uid, name, uid.toLowerCase() + "@campus.edu",
                    "981100" + String.format("%04d", i), "Computer Science", 2, sec,
                    LocalDate.of(2025, 8, 1), 1000 + studentCount, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT));
            studentCount++;
        }

        // 2. BCS 2024 batch (60 students across Sec A & B, Semester 4)
        for (int i = 1; i <= 60; i++) {
            String uid = String.format("24BCS%04d", i);
            String name = firstNames[(i * 5) % firstNames.length] + " " + lastNames[(i * 3) % lastNames.length];
            String sec = (i <= 30) ? "A" : "B";
            ROSTER_STUDENTS.add(new Student(studentCount, uid, name, uid.toLowerCase() + "@campus.edu",
                    "982200" + String.format("%04d", i), "Computer Science", 4, sec,
                    LocalDate.of(2024, 8, 1), 1000 + studentCount, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT));
            studentCount++;
        }

        // 3. BET 2025 batch (50 students across Sec A & B, Electronics & Telecom)
        for (int i = 1; i <= 50; i++) {
            String uid = String.format("25BET%04d", i);
            String name = firstNames[(i * 2) % firstNames.length] + " " + lastNames[(i * 5) % lastNames.length];
            String sec = (i <= 25) ? "A" : "B";
            ROSTER_STUDENTS.add(new Student(studentCount, uid, name, uid.toLowerCase() + "@campus.edu",
                    "983300" + String.format("%04d", i), "Electronics & Telecom", 2, sec,
                    LocalDate.of(2025, 8, 1), 1000 + studentCount, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT));
            studentCount++;
        }

        // 4. BBE 2025 batch (40 students, Bio-Engineering)
        for (int i = 1; i <= 40; i++) {
            String uid = String.format("25BBE%04d", i);
            String name = firstNames[(i * 4) % firstNames.length] + " " + lastNames[(i * 2) % lastNames.length];
            ROSTER_STUDENTS.add(new Student(studentCount, uid, name, uid.toLowerCase() + "@campus.edu",
                    "984400" + String.format("%04d", i), "Bio-Engineering", 2, "A",
                    LocalDate.of(2025, 8, 1), 1000 + studentCount, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT));
            studentCount++;
        }

        // 5. MCA 2025 batch (30 students, Computer Applications)
        for (int i = 1; i <= 30; i++) {
            String uid = String.format("25MCA%04d", i);
            String name = firstNames[(i * 7) % firstNames.length] + " " + lastNames[(i * 4) % lastNames.length];
            ROSTER_STUDENTS.add(new Student(studentCount, uid, name, uid.toLowerCase() + "@campus.edu",
                    "985500" + String.format("%04d", i), "Computer Applications", 2, "A",
                    LocalDate.of(2025, 8, 1), 1000 + studentCount, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT));
            studentCount++;
        }
    }

    private static void initAssignments() {
        // Assign courses to faculties
        addAssignment(1, 1, 2, "A"); // Dr. Ramesh Kumar -> CS202 (OOP Java) Sec A
        addAssignment(2, 2, 2, "B"); // Dr. Anita Desai -> CS202 (OOP Java) Sec B
        addAssignment(3, 3, 1, "A"); // Prof. Vikram -> CS201 (DSA) Sec A
        addAssignment(4, 4, 1, "B"); // Prof. Neha -> CS201 (DSA) Sec B
        addAssignment(5, 5, 3, "A"); // Prof. Rajesh -> CS301 (DBMS) Sec A
        addAssignment(6, 6, 4, "A"); // Prof. Sunita -> CS302 (OS) Sec A

        addAssignment(7, 11, 7, "A"); // Dr. S. K. Roy -> EC201 Sec A
        addAssignment(8, 12, 7, "B"); // Dr. Manisha Sen -> EC201 Sec B
        addAssignment(9, 13, 8, "A"); // Prof. Rakesh Ghosh -> EC202 Sec A

        addAssignment(10, 17, 11, "A"); // Dr. Vandana Paul -> BE201 Sec A
        addAssignment(11, 21, 13, "A"); // Dr. Praveen Bansal -> MCA101 Sec A
    }

    private static void addAssignment(int id, int facId, int courseId, String sec) {
        CourseAssignment ca = new CourseAssignment(id, facId, courseId, sec, "2025-2026");
        Course c = findCourseById(courseId);
        Faculty f = findFacultyById(facId);
        if (c != null) {
            ca.setCourseCode(c.getCourseCode());
            ca.setCourseName(c.getCourseName());
            ca.setSemester(c.getSemester());
        }
        if (f != null) {
            ca.setFacultyName(f.getName());
            ca.setFacultyEmpCode(f.getEmpCode());
        }
        MEMORY_ASSIGNMENTS.add(ca);
    }

    private static void initTimetables() {
        int slotId = 1;
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"09:00 - 10:00", "10:00 - 11:00", "11:15 - 12:15", "13:00 - 14:00", "14:00 - 15:00"};

        // Create weekly schedule slots across CS, EC, BE, MCA for Semester 2 and Semester 4
        for (int d = 0; d < days.length; d++) {
            // CS Sem 4 (24BCS Students)
            addSlot(slotId++, days[d], times[0], 2, 1, 1, 4, "A", "LH-301"); // Dr. Ramesh Kumar (CS202 OOP Java)
            addSlot(slotId++, days[d], times[0], 2, 2, 2, 4, "B", "LH-302"); // Dr. Anita Desai (CS202 OOP Java)
            addSlot(slotId++, days[d], times[1], 1, 3, 1, 4, "A", "LH-301"); // Prof. Vikram (CS201 DSA)
            addSlot(slotId++, days[d], times[1], 1, 4, 2, 4, "B", "LH-302"); // Prof. Neha (CS201 DSA)
            addSlot(slotId++, days[d], times[2], 3, 5, 2, 4, "A", "LH-302"); // Prof. Rajesh (CS301 DBMS)
            addSlot(slotId++, days[d], times[2], 4, 6, 3, 4, "A", "LH-303"); // Prof. Sunita Rao (CS302 OS)

            // CS Sem 2 (25BCS Students e.g. Aarav Bhatia)
            addSlot(slotId++, days[d], times[0], 1, 3, 1, 2, "A", "LH-101"); // Prof. Vikram (CS201 DSA)
            addSlot(slotId++, days[d], times[1], 2, 1, 2, 2, "A", "LH-102"); // Dr. Ramesh Kumar (CS202 OOP)
            addSlot(slotId++, days[d], times[2], 2, 2, 2, 2, "B", "LH-103"); // Dr. Anita Desai (CS202 OOP)
            addSlot(slotId++, days[d], times[3], 4, 6, 3, 2, "A", "Lab 101 (OS & Systems)"); // Prof. Sunita Rao
            addSlot(slotId++, days[d], times[4], 3, 5, 2, 2, "A", "Lab 202 (Database Systems)"); // Prof. Rajesh

            // EC Sem 2 (25BET Students)
            addSlot(slotId++, days[d], times[0], 7, 11, 4, 2, "A", "EC-Lab 1"); // Dr. S. K. Roy (EC201)
            addSlot(slotId++, days[d], times[1], 8, 13, 5, 2, "A", "EC-LH 2"); // Prof. Rakesh Ghosh (EC202)

            // BE & MCA Sem 2 (25BBE & 25MCA Students)
            addSlot(slotId++, days[d], times[3], 11, 17, 6, 2, "A", "BE-Hall 1"); // Dr. Vandana Paul (BE201)
            addSlot(slotId++, days[d], times[4], 13, 21, 7, 2, "A", "MCA-Lab 2"); // Dr. Praveen Bansal (MCA101)
        }
    }

    private static void addSlot(int id, String day, String time, int cId, int fId, int rId, int sem, String sec, String rName) {
        TimetableSlot slot = new TimetableSlot(id, day, time, cId, fId, rId, sem, sec);
        Course c = findCourseById(cId);
        Faculty f = findFacultyById(fId);
        if (c != null) {
            slot.setCourseCode(c.getCourseCode());
            slot.setCourseName(c.getCourseName());
        }
        if (f != null) slot.setFacultyName(f.getName());
        slot.setRoomName(rName);
        MEMORY_TIMETABLE.add(slot);
    }

    private static void initMarks() {
        int markId = 1;
        // Generate comprehensive marks for ALL 260 students across multiple department subjects
        for (Student s : ROSTER_STUDENTS) {
            List<Course> deptCourses = new ArrayList<>();
            for (Course c : MEMORY_COURSES) {
                if (c.getDepartment().equalsIgnoreCase(s.getDepartment())) {
                    deptCourses.add(c);
                }
            }
            if (deptCourses.isEmpty()) {
                deptCourses.add(findCourseById(1));
                deptCourses.add(findCourseById(2));
                deptCourses.add(findCourseById(3));
            }

            for (Course c : deptCourses) {
                if (c == null) continue;
                int seed = s.getStudentId() * 11 + c.getCourseId() * 7;
                double in1 = 14.0 + (seed % 7);                       // 14.0 - 20.0
                double in2 = 13.0 + ((seed * 3) % 7);                 // 13.0 - 19.0
                double ass = 7.5 + ((seed * 5) % 3);                  // 7.5 - 9.5
                double end = 31.0 + ((seed * 7) % 18);                // 31.0 - 48.0

                StudentMark sm = new StudentMark(markId++, s.getStudentId(), c.getCourseId(), in1, in2, ass, end, 1);
                sm.setStudentUid(s.getUid());
                sm.setStudentName(s.getName());
                sm.setCourseCode(c.getCourseCode());
                sm.setCourseName(c.getCourseName());
                sm.setSection(s.getSection());
                sm.calculateTotalAndGrade();
                MEMORY_MARKS.add(sm);
            }
        }
    }

    // =========================================================================
    // PUBLIC API METHODS
    // =========================================================================

    public List<Course> getAllCourses() {
        return new ArrayList<>(MEMORY_COURSES);
    }

    public boolean addCourse(Course c) {
        if (c == null) return false;
        c.setCourseId(MEMORY_COURSES.size() + 1);
        MEMORY_COURSES.add(c);
        return true;
    }

    public boolean deleteCourse(int courseId) {
        return MEMORY_COURSES.removeIf(c -> c.getCourseId() == courseId);
    }

    public boolean deleteAssignment(int assignmentId) {
        return MEMORY_ASSIGNMENTS.removeIf(a -> a.getAssignmentId() == assignmentId);
    }

    public static boolean deleteStudent(String uid) {
        if (uid == null) return false;
        return ROSTER_STUDENTS.removeIf(s -> s.getUid().equalsIgnoreCase(uid) || s.getUsername().equalsIgnoreCase(uid));
    }

    public static boolean addStudent(Student s) {
        if (s == null) return false;
        ROSTER_STUDENTS.add(s);
        return true;
    }

    public static boolean deleteFaculty(String empCode) {
        if (empCode == null) return false;
        return ROSTER_FACULTY.removeIf(f -> f.getEmpCode().equalsIgnoreCase(empCode) || f.getUsername().equalsIgnoreCase(empCode));
    }

    public static boolean addFaculty(Faculty f) {
        if (f == null) return false;
        ROSTER_FACULTY.add(f);
        return true;
    }

    public static Course findCourseById(int id) {
        for (Course c : MEMORY_COURSES) {
            if (c.getCourseId() == id) return c;
        }
        return null;
    }

    public static Faculty findFacultyById(int id) {
        for (Faculty f : ROSTER_FACULTY) {
            if (f.getFacultyId() == id) return f;
        }
        return null;
    }

    public static Faculty findFacultyByEmpCode(String code) {
        if (code == null) return null;
        String clean = code.trim();
        for (Faculty f : ROSTER_FACULTY) {
            if (f.getEmpCode().equalsIgnoreCase(clean) 
                    || f.getUsername().equalsIgnoreCase(clean) 
                    || f.getEmpCode().replace("-", "_").equalsIgnoreCase(clean)) {
                return f;
            }
        }
        return null;
    }

    public static Student findStudentByUid(String uid) {
        if (uid == null) return null;
        String clean = uid.trim();
        for (Student s : ROSTER_STUDENTS) {
            if (s.getUid().equalsIgnoreCase(clean) || s.getUsername().equalsIgnoreCase(clean)) {
                return s;
            }
        }
        return null;
    }

    public List<CourseAssignment> getAllAssignments() {
        return new ArrayList<>(MEMORY_ASSIGNMENTS);
    }

    public List<CourseAssignment> getFacultyAssignments(int facultyId) {
        List<CourseAssignment> list = new ArrayList<>();
        Faculty f = findFacultyById(facultyId);
        for (CourseAssignment ca : MEMORY_ASSIGNMENTS) {
            if (ca.getFacultyId() == facultyId || 
                (f != null && ca.getFacultyEmpCode() != null && ca.getFacultyEmpCode().equalsIgnoreCase(f.getEmpCode()))) {
                list.add(ca);
            }
        }
        return list;
    }

    public boolean assignCourseToFaculty(int facultyId, int courseId, String section) {
        addAssignment(MEMORY_ASSIGNMENTS.size() + 1, facultyId, courseId, section);
        Course c = findCourseById(courseId);
        int sem = (c != null) ? c.getSemester() : 2;
        String[] days = {"Monday", "Wednesday", "Friday"};
        String sec = (section != null && !section.isEmpty()) ? section.trim().toUpperCase() : "A";
        for (String day : days) {
            addSlot(MEMORY_TIMETABLE.size() + 1, day, "10:00 - 11:00", courseId, facultyId, 1, sem, sec, "LH-" + (300 + (facultyId % 10)));
        }
        return true;
    }

    public List<StudentMark> getMarksByCourseAndSection(int courseId, String section) {
        List<StudentMark> list = new ArrayList<>();
        for (StudentMark sm : MEMORY_MARKS) {
            if (sm.getCourseId() == courseId && (section == null || section.equalsIgnoreCase(sm.getSection()))) {
                list.add(sm);
            }
        }
        // If marks not yet created for enrolled students in this section, auto-populate baseline
        if (list.isEmpty()) {
            for (Student s : ROSTER_STUDENTS) {
                if (section == null || section.equalsIgnoreCase(s.getSection())) {
                    StudentMark sm = new StudentMark(MEMORY_MARKS.size() + 1, s.getStudentId(), courseId, 0, 0, 0, 0, 1);
                    sm.setStudentUid(s.getUid());
                    sm.setStudentName(s.getName());
                    Course c = findCourseById(courseId);
                    if (c != null) {
                        sm.setCourseCode(c.getCourseCode());
                        sm.setCourseName(c.getCourseName());
                    }
                    sm.setSection(s.getSection());
                    MEMORY_MARKS.add(sm);
                    list.add(sm);
                }
            }
        }
        return list;
    }

    public boolean saveOrUpdateMark(StudentMark mark) {
        if (mark == null) return false;
        mark.calculateTotalAndGrade();
        for (int i = 0; i < MEMORY_MARKS.size(); i++) {
            StudentMark existing = MEMORY_MARKS.get(i);
            if (existing.getStudentId() == mark.getStudentId() && existing.getCourseId() == mark.getCourseId()) {
                MEMORY_MARKS.set(i, mark);
                return true;
            }
        }
        mark.setMarkId(MEMORY_MARKS.size() + 1);
        MEMORY_MARKS.add(mark);
        return true;
    }

    public List<StudentMark> getStudentReportCard(int studentId) {
        List<StudentMark> list = new ArrayList<>();
        for (StudentMark sm : MEMORY_MARKS) {
            if (sm.getStudentId() == studentId) {
                list.add(sm);
            }
        }
        return list;
    }

    public List<TimetableSlot> getFacultyTimetable(int facultyId) {
        List<TimetableSlot> list = new ArrayList<>();
        for (TimetableSlot ts : MEMORY_TIMETABLE) {
            if (ts.getFacultyId() == facultyId) {
                list.add(ts);
            }
        }
        return list;
    }

    public List<TimetableSlot> getStudentTimetable(int semester, String section) {
        List<TimetableSlot> list = new ArrayList<>();
        for (TimetableSlot ts : MEMORY_TIMETABLE) {
            if (ts.getSemester() == semester && (section == null || section.equalsIgnoreCase(ts.getSection()))) {
                list.add(ts);
            }
        }
        return list;
    }

    public List<TimetableSlot> getAllTimetableSlots() {
        return new ArrayList<>(MEMORY_TIMETABLE);
    }

    public static List<Student> getRosterStudents() {
        return ROSTER_STUDENTS;
    }

    public static List<Faculty> getRosterFaculty() {
        return ROSTER_FACULTY;
    }
}

