# CampusHub – Database Design & Schema Specification

This document details the relational database design for **CampusHub – Smart Campus Management System** hosted on **MySQL 8.0+**.

---

## 1. Database Specifications

- **Database Name**: `campushub_db`
- **RDBMS Engine**: MySQL 8.0+ / InnoDB
- **Character Set**: `utf8mb4`
- **Collation**: `utf8mb4_unicode_ci`
- **Default Port**: 3306

---

## 2. Table Schemas & Constraints

### 1. `users` Table
Handles user credentials, password hashes, cryptographically generated salts, and role authorization.
- `user_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `username` (VARCHAR(50), NOT NULL, UNIQUE)
- `password_hash` (VARCHAR(256), NOT NULL)
- `salt` (VARCHAR(64), NOT NULL)
- `role` (ENUM('ADMIN', 'STUDENT', 'FACULTY'), NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- *Index*: `idx_username (username)`

### 2. `students` Table
Stores academic enrollment details.
- `student_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `uid` (VARCHAR(20), NOT NULL, UNIQUE)
- `full_name` (VARCHAR(100), NOT NULL)
- `email` (VARCHAR(100), NOT NULL, UNIQUE)
- `phone` (VARCHAR(20), NULL)
- `department` (VARCHAR(50), NOT NULL)
- `semester` (INT, NOT NULL, DEFAULT 1)
- `section` (VARCHAR(10), DEFAULT 'A')
- `enrollment_date` (DATE)
- `user_id` (INT, NULL, FOREIGN KEY REFERENCES `users(user_id)` ON DELETE SET NULL)
- *Indexes*: `idx_student_uid (uid)`, `idx_student_name (full_name)`

### 3. `faculty` Table
Stores staff and professor records.
- `faculty_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `emp_code` (VARCHAR(20), NOT NULL, UNIQUE)
- `full_name` (VARCHAR(100), NOT NULL)
- `email` (VARCHAR(100), NOT NULL, UNIQUE)
- `phone` (VARCHAR(20), NULL)
- `department` (VARCHAR(50), NOT NULL)
- `designation` (VARCHAR(50), NOT NULL)
- `user_id` (INT, NULL, FOREIGN KEY REFERENCES `users(user_id)` ON DELETE SET NULL)
- *Index*: `idx_faculty_emp (emp_code)`

### 4. `books` Table
Maintains catalog inventory and live available copies.
- `book_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `title` (VARCHAR(150), NOT NULL)
- `author` (VARCHAR(100), NOT NULL)
- `isbn` (VARCHAR(25), NOT NULL, UNIQUE)
- `category` (VARCHAR(50), NOT NULL)
- `total_quantity` (INT, NOT NULL, DEFAULT 1)
- `available_quantity` (INT, NOT NULL, DEFAULT 1)
- *Indexes*: `idx_book_title (title)`, `idx_book_author (author)`

### 5. `rooms` Table
Tracks classrooms, computer laboratories, and seminar halls.
- `room_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `room_name` (VARCHAR(50), NOT NULL, UNIQUE)
- `room_type` (ENUM('CLASSROOM', 'LAB', 'SEMINAR_HALL'), NOT NULL)
- `building` (VARCHAR(50), NOT NULL)
- `capacity` (INT, NOT NULL)
- `is_available` (BOOLEAN, DEFAULT TRUE)

### 6. `bookings` Table
Stores room scheduling intervals with conflict detection indices.
- `booking_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `user_id` (INT, NOT NULL, FOREIGN KEY REFERENCES `users(user_id)` ON DELETE CASCADE)
- `room_id` (INT, NOT NULL, FOREIGN KEY REFERENCES `rooms(room_id)` ON DELETE CASCADE)
- `start_time` (DATETIME, NOT NULL)
- `end_time` (DATETIME, NOT NULL)
- `purpose` (VARCHAR(255), NOT NULL)
- `status` (ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED'), DEFAULT 'CONFIRMED')
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- *Index*: `idx_booking_dates (room_id, start_time, end_time)`

### 7. `issued_books` Table
Tracks checkout transactions, due dates, and fine calculations.
- `issue_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `student_id` (INT, NOT NULL, FOREIGN KEY REFERENCES `students(student_id)` ON DELETE CASCADE)
- `book_id` (INT, NOT NULL, FOREIGN KEY REFERENCES `books(book_id)` ON DELETE CASCADE)
- `issue_date` (DATE, NOT NULL)
- `due_date` (DATE, NOT NULL)
- `return_date` (DATE, NULL)
- `fine_amount` (DOUBLE, DEFAULT 0.0)
- `status` (ENUM('ISSUED', 'RETURNED'), DEFAULT 'ISSUED')

### 8. `activity_logs` Table
Audit trail logging logins, updates, and backups.
- `log_id` (INT, AUTO_INCREMENT, PRIMARY KEY)
- `action` (VARCHAR(100), NOT NULL)
- `details` (TEXT)
- `username` (VARCHAR(50))
- `timestamp` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

---

## 3. How to Set Up in MySQL Workbench

1. Open **MySQL Workbench**.
2. Connect to your local MySQL instance (port `3306`).
3. Click **File -> Open SQL Script...** (or press `Ctrl + Shift + O`).
4. Select the file:
   ```
   D:\OOPS project\database\campushub.sql
   ```
5. Click the lightning bolt icon (**Execute**) to run the entire script.
6. Verify in the Schemas panel that `campushub_db` appears with all 8 tables and baseline demo seed data.

