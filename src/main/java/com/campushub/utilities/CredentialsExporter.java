package com.campushub.utilities;

import com.campushub.models.Faculty;
import com.campushub.models.Student;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 2: IO PACKAGE & FILE WRITING
 * CredentialsExporter generates and updates the master credentials reference file
 * 'CREDENTIALS_AND_PASSWORDS.txt' in the main workspace directory.
 */
public class CredentialsExporter {

    private static final String EXPORT_FILE = "CREDENTIALS_AND_PASSWORDS.txt";

    /**
     * Writes the complete campus roster of users, UIDs, and login passwords to the text file.
     */
    public static synchronized void exportMasterCredentials(List<Student> students, List<Faculty> faculties) {
        File file = new File(EXPORT_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));

            writer.write("========================================================================================\n");
            writer.write("           CAMPUSHUB - MASTER DIRECTORY OF PASSWORDS & CREDENTIALS                      \n");
            writer.write("           Generated on: " + timestamp + "                                            \n");
            writer.write("========================================================================================\n");
            writer.write("NOTE: All passwords in this file can be used to log in as any HOD, Faculty, or Student. \n");
            writer.write("After login, any user can change their password using the 'Change Password' option.      \n");
            writer.write("========================================================================================\n\n");

            // 1. HOD / Administrator Accounts
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write("1. HEAD OF DEPARTMENT (HOD) & ADMINISTRATIVE ACCOUNTS                                   \n");
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write(String.format("%-15s | %-24s | %-12s | %-15s | %-15s | %-15s%n", 
                    "ROLE", "FULL NAME", "USERNAME", "PASSWORD", "EMP CODE", "DEPARTMENT"));
            writer.write("----------------------------------------------------------------------------------------\n");
            String adminPass = com.campushub.services.AuthService.getActivePassword("admin", "Admin@123");
            String hodEcePass = com.campushub.services.AuthService.getActivePassword("hod_ece", "HOD@123");
            writer.write(String.format("%-15s | %-24s | %-12s | %-15s | %-15s | %-15s%n",
                    "HOD / ADMIN", "Dr. A. K. Sharma (HOD)", "admin", adminPass, "HOD-CSE-01", "Computer Science"));
            writer.write(String.format("%-15s | %-24s | %-12s | %-15s | %-15s | %-15s%n",
                    "HOD (ECE)", "Dr. S. K. Roy (HOD)", "hod_ece", hodEcePass, "HOD-ECE-01", "Electronics & Tel"));
            writer.write("\n\n");

            // 2. Faculty Accounts
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write("2. FACULTY MEMBERS (" + (faculties != null ? faculties.size() : 0) + " REGISTERED PROFESSORS)                                  \n");
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write(String.format("%-12s | %-24s | %-14s | %-14s | %-18s | %-18s%n",
                    "EMP CODE", "FACULTY NAME", "USERNAME", "PASSWORD", "DEPARTMENT", "DESIGNATION"));
            writer.write("----------------------------------------------------------------------------------------\n");
            if (faculties != null) {
                for (Faculty f : faculties) {
                    String pass = com.campushub.services.AuthService.getActivePassword(f.getUsername(), "Faculty@123");
                    writer.write(String.format("%-12s | %-24s | %-14s | %-14s | %-18s | %-18s%n",
                            f.getEmpCode(), f.getName(), f.getUsername(), pass, f.getDepartment(), f.getDesignation()));
                }
            }
            writer.write("\n\n");

            // 3. Student Accounts
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write("3. STUDENTS (" + (students != null ? students.size() : 0) + " ENROLLED STUDENTS ACROSS SECTIONS)                                  \n");
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write(String.format("%-12s | %-22s | %-14s | %-14s | %-16s | %-4s | %-4s%n",
                    "UID", "STUDENT NAME", "USERNAME", "PASSWORD", "DEPARTMENT", "SEM", "SEC"));
            writer.write("----------------------------------------------------------------------------------------\n");
            if (students != null) {
                for (Student s : students) {
                    String pass = com.campushub.services.AuthService.getActivePassword(s.getUsername(), "Student@123");
                    writer.write(String.format("%-12s | %-22s | %-14s | %-14s | %-16s | %-4d | %-4s%n",
                            s.getUid(), s.getName(), s.getUsername(), pass, s.getDepartment(), s.getSemester(), s.getSection()));
                }
            }

            writer.flush();
            System.out.println("[CredentialsExporter] Exported " 
                    + (faculties != null ? faculties.size() : 0) + " faculty and " 
                    + (students != null ? students.size() : 0) + " student credentials to " + EXPORT_FILE);
        } catch (IOException e) {
            System.err.println("[CredentialsExporter] Error writing credentials file: " + e.getMessage());
        }
    }

    /**
     * Updates master credentials file whenever a user changes password.
     */
    public static synchronized void recordPasswordChange(String username, String newPassword) {
        exportMasterCredentials(com.campushub.dao.AcademicDAO.getRosterStudents(), com.campushub.dao.AcademicDAO.getRosterFaculty());
        File file = new File(EXPORT_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("%n[LAST_PASSWORD_UPDATE] %s | User: %-15s | New Password: %s%n", 
                    timestamp, username, newPassword));
            writer.flush();
        } catch (IOException e) {
            System.err.println("[CredentialsExporter] Error recording password update: " + e.getMessage());
        }
    }

    public static String getCredentialsFilePath() {
        return new File(EXPORT_FILE).getAbsolutePath();
    }
}

