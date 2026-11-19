package com.campushub.controller;

import com.campushub.dao.AcademicDAO;
import com.campushub.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/academic")
public class AcademicController {

    private final AcademicDAO dao = new AcademicDAO();

    // ---- Courses ----
    @GetMapping("/courses")
    public ResponseEntity<?> getCourses(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        List<Course> courses = dao.getAllCourses();
        List<Map<String, Object>> result = courses.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getCourseId());
            m.put("code", c.getCourseCode());
            m.put("name", c.getCourseName());
            m.put("credits", c.getCredits());
            m.put("department", c.getDepartment());
            m.put("semester", c.getSemester());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ---- Assignments ----
    @GetMapping("/assignments")
    public ResponseEntity<?> getAssignments(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        List<CourseAssignment> list = dao.getAllAssignments();
        List<Map<String, Object>> result = list.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getAssignmentId());
            m.put("facultyName", a.getFacultyName() != null ? a.getFacultyName() : "");
            m.put("empCode", a.getFacultyEmpCode() != null ? a.getFacultyEmpCode() : "");
            m.put("courseCode", a.getCourseCode() != null ? a.getCourseCode() : "");
            m.put("courseName", a.getCourseName() != null ? a.getCourseName() : "");
            m.put("section", a.getSection() != null ? a.getSection() : "");
            m.put("semester", a.getSemester());
            m.put("academicYear", a.getAcademicYear() != null ? a.getAcademicYear() : "2025-2026");
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ---- Marks ----
    @GetMapping("/marks")
    public ResponseEntity<?> getMarks(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String section,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        User user = (User) session.getAttribute("user");
        List<StudentMark> list;
        if ("STUDENT".equalsIgnoreCase(user.getRole()) && user instanceof Student) {
            Student s = (Student) user;
            list = dao.getStudentReportCard(s.getStudentId());
        } else {
            int cId = (courseId != null) ? courseId : 2;
            String sec = (section != null) ? section : "A";
            list = dao.getMarksByCourseAndSection(cId, sec);
        }
        List<Map<String, Object>> result = list.stream().map(m -> {
            Map<String, Object> mp = new LinkedHashMap<>();
            mp.put("markId", m.getMarkId());
            mp.put("studentUid", m.getStudentUid() != null ? m.getStudentUid() : "");
            mp.put("studentName", m.getStudentName() != null ? m.getStudentName() : "");
            mp.put("courseCode", m.getCourseCode() != null ? m.getCourseCode() : "");
            mp.put("courseName", m.getCourseName() != null ? m.getCourseName() : "");
            mp.put("internal1", m.getInternal1());
            mp.put("internal2", m.getInternal2());
            mp.put("assignment", m.getAssignment());
            mp.put("endSem", m.getEndSem());
            mp.put("total", m.getTotalMarks());
            mp.put("grade", m.getGrade() != null ? m.getGrade() : "");
            mp.put("section", m.getSection() != null ? m.getSection() : "");
            return mp;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ---- Timetable ----
    @GetMapping("/timetable")
    public ResponseEntity<?> getTimetable(
            @RequestParam(required = false) String facultyCode,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        User user = (User) session.getAttribute("user");
        List<TimetableSlot> list = dao.getAllTimetableSlots();

        Faculty f = null;
        Student s = null;
        String role = (user.getRole() != null) ? user.getRole().toUpperCase() : "";
        if ("FACULTY".equals(role) || user instanceof Faculty) {
            f = (user instanceof Faculty) ? (Faculty) user : AcademicDAO.findFacultyByEmpCode(user.getUsername());
        } else if ("STUDENT".equals(role) || user instanceof Student) {
            s = (user instanceof Student) ? (Student) user : AcademicDAO.findStudentByUid(user.getUsername());
        }

        int facId = (f != null) ? f.getFacultyId() : -1;
        String facName = (f != null && f.getName() != null) ? f.getName().toLowerCase() : "";
        String stdSec = (s != null && s.getSection() != null) ? s.getSection().trim() : "";
        int stdSem = (s != null) ? s.getSemester() : -1;

        List<Map<String, Object>> result = new ArrayList<>(list.size());
        for (TimetableSlot t : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", t.getSlotId());
            m.put("day", t.getDayOfWeek() != null ? t.getDayOfWeek() : "");
            m.put("timeSlot", t.getTimeSlot() != null ? t.getTimeSlot() : "");
            m.put("courseCode", t.getCourseCode() != null ? t.getCourseCode() : "");
            m.put("courseName", t.getCourseName() != null ? t.getCourseName() : "");
            m.put("facultyName", t.getFacultyName() != null ? t.getFacultyName() : "");
            m.put("roomName", t.getRoomName() != null ? t.getRoomName() : "");
            m.put("section", t.getSection() != null ? t.getSection() : "");
            m.put("semester", t.getSemester());
            
            boolean isMine = false;
            if (f != null) {
                if (t.getFacultyId() == facId || 
                    (!facName.isEmpty() && t.getFacultyName() != null && t.getFacultyName().toLowerCase().contains(facName))) {
                    isMine = true;
                }
            } else if (s != null) {
                if (t.getSemester() == stdSem && (stdSec.isEmpty() || stdSec.equalsIgnoreCase(t.getSection()))) {
                    isMine = true;
                }
            }
            m.put("isMine", isMine);
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    // ---- Dashboard Stats (Role-Aware Calculation) ----
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        User user = (User) session.getAttribute("user");
        Map<String, Object> stats = new LinkedHashMap<>();
        String role = (user.getRole() != null) ? user.getRole().toUpperCase() : "STUDENT";
        stats.put("role", role);

        if ("STUDENT".equalsIgnoreCase(role) || user instanceof Student) {
            Student s = (user instanceof Student) ? (Student) user : AcademicDAO.findStudentByUid(user.getUsername());
            if (s == null && session.getAttribute("username") != null) {
                s = AcademicDAO.findStudentByUid(session.getAttribute("username").toString());
            }
            int stdId = (s != null) ? s.getStudentId() : 1;
            String dept = (s != null) ? s.getDepartment() : "Computer Science";

            List<StudentMark> myMarks = dao.getStudentReportCard(stdId);
            List<Course> deptCourses = new ArrayList<>();
            for (Course c : dao.getAllCourses()) {
                if (c.getDepartment().equalsIgnoreCase(dept)) {
                    deptCourses.add(c);
                }
            }
            int enrolledCount = !myMarks.isEmpty() ? myMarks.size() : (!deptCourses.isEmpty() ? deptCourses.size() : 4);

            double totalScore = 0;
            int markCount = 0;
            int earnedCredits = 0;
            for (StudentMark sm : myMarks) {
                totalScore += sm.getTotalMarks();
                markCount++;
                Course c = AcademicDAO.findCourseById(sm.getCourseId());
                earnedCredits += (c != null && c.getCredits() > 0) ? c.getCredits() : 4;
            }
            if (earnedCredits == 0) earnedCredits = enrolledCount * 4;

            double avg = markCount > 0 ? (totalScore / markCount) : 84.5;
            double cgpa = Math.min(10.0, Math.round((avg / 9.5) * 100.0) / 100.0);

            // Deterministic personalized attendance based on studentId
            int att = 80 + ((stdId * 17) % 18); // 80% to 97%

            stats.put("c1_val", String.valueOf(enrolledCount));
            stats.put("c1_lbl", "Enrolled Courses");
            stats.put("c2_val", String.format(Locale.US, "%.2f CGPA", cgpa));
            stats.put("c2_lbl", "Current Performance");
            stats.put("c3_val", earnedCredits + " Credits");
            stats.put("c3_lbl", "Earned Credits");
            stats.put("c4_val", att + "% Attend");
            stats.put("c4_lbl", "Class Attendance");

        } else if ("FACULTY".equalsIgnoreCase(role) || user instanceof Faculty) {
            Faculty f = (user instanceof Faculty) ? (Faculty) user : AcademicDAO.findFacultyByEmpCode(user.getUsername());
            if (f == null && session.getAttribute("username") != null) {
                f = AcademicDAO.findFacultyByEmpCode(session.getAttribute("username").toString());
            }
            int facId = (f != null) ? f.getFacultyId() : 0;
            String facEmp = (f != null) ? f.getEmpCode() : "";
            String facName = (f != null) ? f.getName() : "";
            String facDept = (f != null) ? f.getDepartment() : "";

            List<CourseAssignment> myAssign = new ArrayList<>();
            for (CourseAssignment ca : dao.getAllAssignments()) {
                if (ca.getFacultyId() == facId || 
                    (facEmp != null && !facEmp.isEmpty() && facEmp.equalsIgnoreCase(ca.getFacultyEmpCode())) ||
                    (facName != null && !facName.isEmpty() && ca.getFacultyName() != null && ca.getFacultyName().equalsIgnoreCase(facName))) {
                    myAssign.add(ca);
                }
            }

            // 1. Assigned Courses: count distinct courses
            Set<Integer> uniqueCourses = new HashSet<>();
            Set<String> batches = new LinkedHashSet<>();
            for (CourseAssignment ca : myAssign) {
                uniqueCourses.add(ca.getCourseId());
                if (ca.getSection() != null && !ca.getSection().trim().isEmpty()) {
                    batches.add("Sec " + ca.getSection().trim().toUpperCase());
                }
            }
            stats.put("c1_val", String.valueOf(uniqueCourses.size()));
            stats.put("c1_lbl", "Assigned Courses");

            // 2. Teaching Batches: dynamic list of sections (e.g. "Sec A", "Sec A & B", "None Assigned")
            String batchesText = batches.isEmpty() ? "None Assigned" : String.join(" & ", batches);
            stats.put("c2_val", batchesText);
            stats.put("c2_lbl", "Teaching Batches");

            // 3. Students in Courses: actual count of enrolled students in the sections & department taught
            int studentCount = 0;
            if (!myAssign.isEmpty()) {
                Set<Integer> countedStudentIds = new HashSet<>();
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
            stats.put("c3_val", studentCount + " Enrolled");
            stats.put("c3_lbl", "Students in Courses");

            // 4. Weekly Schedule: calculate actual lecture & lab hours scheduled
            int weeklyHours = 0;
            for (TimetableSlot ts : dao.getAllTimetableSlots()) {
                if (ts.getFacultyId() == facId || 
                    (!facName.isEmpty() && ts.getFacultyName() != null && ts.getFacultyName().toLowerCase().contains(facName.toLowerCase()))) {
                    weeklyHours++;
                }
            }
            if (weeklyHours == 0 && !myAssign.isEmpty()) {
                weeklyHours = myAssign.size() * 4;
            }
            stats.put("c4_val", weeklyHours + " Hrs / Wk");
            stats.put("c4_lbl", "Weekly Schedule");

        } else {
            // HOD / Admin: Real-time University Aggregate Metrics
            stats.put("c1_val", String.valueOf(AcademicDAO.getRosterStudents().size()));
            stats.put("c1_lbl", "Total Students");
            stats.put("c2_val", String.valueOf(AcademicDAO.getRosterFaculty().size()));
            stats.put("c2_lbl", "Faculty Members");
            stats.put("c3_val", String.valueOf(dao.getAllCourses().size()));
            stats.put("c3_lbl", "Active Courses");
            stats.put("c4_val", String.valueOf(dao.getAllAssignments().size()));
            stats.put("c4_lbl", "Course Allocations");
        }
        return ResponseEntity.ok(stats);
    }

    // ---- Role-Based Management Endpoints ----
    @PostMapping("/courses")
    public ResponseEntity<?> addCourse(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        try {
            String code = (String) body.get("code");
            String name = (String) body.get("name");
            String dept = (String) body.get("department");
            int credits = Integer.parseInt(body.get("credits").toString());
            int sem = Integer.parseInt(body.get("semester").toString());
            Course c = new Course(0, code, name, credits, dept, sem);
            dao.addCourse(c);
            return ResponseEntity.ok(Map.of("success", true, "message", "Course added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        boolean ok = dao.deleteCourse(id);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/assignments")
    public ResponseEntity<?> addAssignment(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        try {
            String facRef = body.get("facultyId").toString();
            Faculty f = null;
            try {
                int facId = Integer.parseInt(facRef);
                f = AcademicDAO.findFacultyById(facId);
            } catch (NumberFormatException ignored) {}
            if (f == null) {
                f = AcademicDAO.findFacultyByEmpCode(facRef);
            }
            if (f == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Faculty not found: " + facRef));
            }
            int courseId = Integer.parseInt(body.get("courseId").toString());
            String section = (String) body.get("section");
            dao.assignCourseToFaculty(f.getFacultyId(), courseId, section);
            return ResponseEntity.ok(Map.of("success", true, "message", "Assignment created for " + f.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        boolean ok = dao.deleteAssignment(id);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/marks/save")
    public ResponseEntity<?> saveMarks(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || "STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Students cannot edit marks"));
        }
        try {
            String uid = (String) body.get("studentUid");
            int courseId = Integer.parseInt(body.get("courseId").toString());
            double in1 = Double.parseDouble(body.get("internal1").toString());
            double in2 = Double.parseDouble(body.get("internal2").toString());
            double ass = Double.parseDouble(body.get("assignment").toString());
            double end = Double.parseDouble(body.get("endSem").toString());

            Student s = AcademicDAO.findStudentByUid(uid);
            if (s == null) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Student not found"));

            Course c = AcademicDAO.findCourseById(courseId);
            StudentMark mark = new StudentMark(0, s.getStudentId(), courseId, in1, in2, ass, end, 1);
            mark.setStudentUid(s.getUid());
            mark.setStudentName(s.getName());
            if (c != null) {
                mark.setCourseCode(c.getCourseCode());
                mark.setCourseName(c.getCourseName());
            }
            mark.setSection(s.getSection());
            dao.saveOrUpdateMark(mark);
            return ResponseEntity.ok(Map.of("success", true, "total", mark.getTotalMarks(), "grade", mark.getGrade()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
