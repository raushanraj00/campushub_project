package com.campushub.controller;

import com.campushub.exceptions.InvalidUserException;
import com.campushub.models.Faculty;
import com.campushub.models.Student;
import com.campushub.models.User;
import com.campushub.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String username = body.get("username");
            String password = body.get("password");
            User user = AuthService.login(username, password);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            resp.put("success", true);
            resp.put("name", user.getName());
            resp.put("username", user.getUsername());
            resp.put("role", user.getRole());
            resp.put("email", user.getEmail());

            if (user instanceof Student) {
                Student s = (Student) user;
                resp.put("uid", s.getUid());
                resp.put("department", s.getDepartment());
                resp.put("semester", s.getSemester());
                resp.put("section", s.getSection());
            } else if (user instanceof Faculty) {
                Faculty f = (Faculty) user;
                resp.put("empCode", f.getEmpCode());
                resp.put("department", f.getDepartment());
                resp.put("designation", f.getDesignation());
                resp.put("privileges", f.getPrivileges());
            }
            return ResponseEntity.ok(resp);
        } catch (InvalidUserException e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(401).body(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        AuthService.logout();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/logout")
    public void logoutGet(HttpSession session, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        session.invalidate();
        AuthService.logout();
        response.sendRedirect("/login");
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.put("success", false);
            resp.put("message", "Not logged in");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            String oldPwd = body.get("oldPassword");
            String newPwd = body.get("newPassword");
            AuthService.changePassword(user.getUsername(), oldPwd, newPwd);
            resp.put("success", true);
            resp.put("message", "Password changed successfully!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(400).body(resp);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.put("loggedIn", false);
            return ResponseEntity.ok(resp);
        }
        resp.put("loggedIn", true);
        resp.put("name", user.getName());
        resp.put("username", user.getUsername());
        resp.put("role", user.getRole());
        resp.put("email", user.getEmail());
        if (user instanceof Student) {
            Student s = (Student) user;
            resp.put("uid", s.getUid());
            resp.put("department", s.getDepartment());
            resp.put("semester", s.getSemester());
            resp.put("section", s.getSection());
        } else if (user instanceof Faculty) {
            Faculty f = (Faculty) user;
            resp.put("empCode", f.getEmpCode());
            resp.put("department", f.getDepartment());
            resp.put("designation", f.getDesignation());
            resp.put("privileges", f.getPrivileges());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/directory")
    public ResponseEntity<Map<String, Object>> directory() {
        Map<String, Object> resp = new HashMap<>();
        
        // 1. Admins
        java.util.List<Map<String, String>> admins = new java.util.ArrayList<>();
        admins.add(Map.of("username", "admin", "name", "Dr. A. K. Sharma (HOD / Admin)", "role", "HOD / Admin", "dept", "Computer Science", "pwd", AuthService.getActivePassword("admin", "Admin@123")));
        admins.add(Map.of("username", "hod_ece", "name", "Dr. S. K. Roy (HOD ECE)", "role", "HOD", "dept", "Electronics & Telecom", "pwd", AuthService.getActivePassword("hod_ece", "HOD@123")));
        resp.put("admins", admins);

        // 2. Faculty
        java.util.List<Map<String, String>> faculties = new java.util.ArrayList<>();
        for (Faculty f : com.campushub.dao.AcademicDAO.getRosterFaculty()) {
            Map<String, String> fm = new HashMap<>();
            fm.put("username", f.getUsername());
            fm.put("empCode", f.getEmpCode());
            fm.put("name", f.getName());
            fm.put("dept", f.getDepartment());
            fm.put("designation", f.getDesignation());
            fm.put("pwd", AuthService.getActivePassword(f.getUsername(), "Faculty@123"));
            faculties.add(fm);
        }
        resp.put("faculties", faculties);

        // 3. Students
        java.util.List<Map<String, String>> students = new java.util.ArrayList<>();
        for (Student s : com.campushub.dao.AcademicDAO.getRosterStudents()) {
            Map<String, String> sm = new HashMap<>();
            sm.put("username", s.getUsername());
            sm.put("uid", s.getUid());
            sm.put("name", s.getName());
            sm.put("dept", s.getDepartment());
            sm.put("sem", String.valueOf(s.getSemester()));
            sm.put("sec", s.getSection());
            sm.put("pwd", AuthService.getActivePassword(s.getUsername(), "Student@123"));
            students.add(sm);
        }
        resp.put("students", students);
        resp.put("totalUsers", admins.size() + faculties.size() + students.size());

        return ResponseEntity.ok(resp);
    }
}

