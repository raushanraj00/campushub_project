package com.campushub.controller;

import com.campushub.dao.AcademicDAO;
import com.campushub.models.Faculty;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    @GetMapping
    public ResponseEntity<?> getFaculty(
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String search,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).build();
        }
        List<Faculty> all = AcademicDAO.getRosterFaculty();
        List<Map<String, Object>> result = all.stream()
            .filter(f -> dept == null || dept.isEmpty() || f.getDepartment().equalsIgnoreCase(dept))
            .filter(f -> search == null || search.isEmpty() ||
                    f.getName().toLowerCase().contains(search.toLowerCase()) ||
                    f.getEmpCode().toLowerCase().contains(search.toLowerCase()))
            .map(f -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", f.getFacultyId());
                map.put("facultyId", f.getFacultyId());
                map.put("empCode", f.getEmpCode());
                map.put("name", f.getName());
                map.put("email", f.getEmail());
                map.put("department", f.getDepartment());
                map.put("designation", f.getDesignation());
                map.put("privileges", f.getPrivileges() != null ? f.getPrivileges() : "");
                return map;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addFaculty(@RequestBody Map<String, Object> body, HttpSession session) {
        com.campushub.models.User user = (com.campushub.models.User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        try {
            String emp = (String) body.get("empCode");
            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String dept = (String) body.get("department");
            String desig = (String) body.get("designation");
            if (emp == null || emp.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Employee Code and Name are required"));
            }
            emp = emp.trim().toUpperCase();
            name = name.trim();
            if (email == null || email.trim().isEmpty()) {
                email = emp.toLowerCase().replace("-", ".") + "@campus.edu";
            }
            int id = AcademicDAO.getRosterFaculty().size() + 1;
            String username = emp.toLowerCase().replace("-", "_");
            Faculty f = new Faculty(id, emp, name, email, "987654" + String.format("%04d", id), dept, desig, 100 + id, username,
                    com.campushub.services.AuthService.DEMO_FACULTY_HASH,
                    com.campushub.services.AuthService.DEMO_SALT);
            f.setPrivileges("UPLOAD_MARKS,VIEW_STUDENTS,CLASS_SCHEDULE,BOOK_LABS");
            AcademicDAO.addFaculty(f);

            // Synchronize with CREDENTIALS_AND_PASSWORDS.txt file
            try {
                com.campushub.utilities.CredentialsExporter.exportMasterCredentials(
                        AcademicDAO.getRosterStudents(), AcademicDAO.getRosterFaculty());
            } catch (Exception ex) {
                System.err.println("[FacultyController] Credentials export note: " + ex.getMessage());
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Faculty added successfully", "facultyId", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{empCode}")
    public ResponseEntity<?> deleteFaculty(@PathVariable String empCode, HttpSession session) {
        com.campushub.models.User user = (com.campushub.models.User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Permission Denied: Admin / HOD level required"));
        }
        boolean ok = AcademicDAO.deleteFaculty(empCode);
        if (ok) {
            try {
                com.campushub.utilities.CredentialsExporter.exportMasterCredentials(
                        AcademicDAO.getRosterStudents(), AcademicDAO.getRosterFaculty());
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok(Map.of("success", ok));
    }
}

