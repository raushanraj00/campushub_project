package com.campushub.controller;

import com.campushub.dao.AcademicDAO;
import com.campushub.models.Student;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @GetMapping
    public ResponseEntity<?> getStudents(
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String search,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        List<Student> all = AcademicDAO.getRosterStudents();
        List<Map<String, Object>> result = all.stream()
            .filter(s -> dept == null || dept.isEmpty() || s.getDepartment().equalsIgnoreCase(dept))
            .filter(s -> section == null || section.isEmpty() || s.getSection().equalsIgnoreCase(section))
            .filter(s -> search == null || search.isEmpty() ||
                    s.getName().toLowerCase().contains(search.toLowerCase()) ||
                    s.getUid().toLowerCase().contains(search.toLowerCase()))
            .map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", s.getUid());
                map.put("name", s.getName());
                map.put("email", s.getEmail());
                map.put("department", s.getDepartment());
                map.put("semester", s.getSemester());
                map.put("section", s.getSection());
                return map;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Map<String, Object> body, HttpSession session) {
        com.campushub.models.User user = (com.campushub.models.User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        try {
            String uid = (String) body.get("uid");
            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String dept = (String) body.get("department");
            int sem = Integer.parseInt(body.get("semester").toString());
            String sec = (String) body.get("section");
            int id = AcademicDAO.getRosterStudents().size() + 1;
            Student s = new Student(id, uid, name, email, "981100" + id, dept, sem, sec,
                    java.time.LocalDate.now(), 1000 + id, uid.toLowerCase(),
                    com.campushub.services.AuthService.DEMO_STUDENT_HASH,
                    com.campushub.services.AuthService.DEMO_SALT);
            AcademicDAO.addStudent(s);
            try {
                com.campushub.utilities.CredentialsExporter.exportMasterCredentials(
                        AcademicDAO.getRosterStudents(), AcademicDAO.getRosterFaculty());
            } catch (Exception ignored) {}
            return ResponseEntity.ok(Map.of("success", true, "message", "Student created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteStudent(@PathVariable String uid, HttpSession session) {
        com.campushub.models.User user = (com.campushub.models.User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        boolean ok = AcademicDAO.deleteStudent(uid);
        if (ok) {
            try {
                com.campushub.utilities.CredentialsExporter.exportMasterCredentials(
                        AcademicDAO.getRosterStudents(), AcademicDAO.getRosterFaculty());
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok(Map.of("success", ok));
    }
}

