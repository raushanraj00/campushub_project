# CampusHub – Smart Campus Management System: Project Overview

## 1. Executive Summary

**CampusHub** is an enterprise-grade desktop management application built in **Java (JDK 17 LTS)** with **Java Swing GUI**, **MySQL Database**, **JDBC Architecture**, and the standard **Java IO & Multithreading packages**.

It was engineered specifically as a university end-semester practical project to fulfill and visually demonstrate every single concept across **Units 1, 2, and 3** of the Java Object-Oriented Programming syllabus.

---

## 2. Problem Statement & Motivation

Traditional university practical submissions often consist of trivial console-based scripts (such as basic calculators or 50-line bank account simulations). While such scripts may demonstrate basic syntax, they fail to demonstrate how advanced enterprise concepts like **Multilevel Inheritance, Dynamic Method Dispatch, Thread Synchronization, Pipe Streams, Object Serialization, and Database Transactions** operate together in real-world software.

**CampusHub** solves this by unifying all campus operations into an integrated desktop suite:
- **Academic Enrollment Management**
- **Faculty Directory**
- **Library Inventory & Transactional Checkouts**
- **Classroom & Computer Lab Scheduling with Race Condition Protection**
- **Audit Logging & Background Data Serialization**

---

## 3. Technology Stack

- **Programming Language**: Java (OpenJDK 17 LTS)
- **GUI Framework**: Java Swing (`JFrame`, `JTable`, `CardLayout`, `GridBagLayout`)
- **Theme & Styling**: FlatLaf (Modern Clean Look and Feel)
- **Database Engine**: MySQL Server 8.0+
- **Database Connectivity**: JDBC via MySQL Connector/J 8.3.0
- **Testing Framework**: JUnit 5 (Jupiter 5.10.2)
- **Build System**: Apache Maven 3.9.6

---

## 4. Role-Based Access Control (RBAC) Matrix

| Feature / Action | Admin | Faculty | Student |
| :--- | :---: | :---: | :---: |
| Overview Dashboard & Statistics | ✓ | ✓ | ✓ |
| View Enrolled Students | ✓ | ✓ | ✓ |
| Add / Edit / Delete Students | ✓ | ✗ | ✗ |
| View Faculty Directory | ✓ | ✓ | ✗ |
| Add / Delete Faculty | ✓ | ✗ | ✗ |
| Search Library Catalog | ✓ | ✓ | ✓ |
| Issue Book to Student | ✓ | ✓ | ✓ |
| Return Book & Calculate Late Fine | ✓ | ✓ | ✓ |
| Add / Delete Books in Catalog | ✓ | ✗ | ✗ |
| View Campus Rooms & Laboratories | ✓ | ✓ | ✓ |
| Reserve Classroom / Lab Slot | ✓ | ✓ | ✓ |
| Cancel Booking | ✓ | ✓ (Own) | ✓ (Own) |
| Run Concurrent Collision Simulation | ✓ | ✓ | ✓ |
| Print Inner Class Reservation Pass | ✓ | ✓ | ✓ |
| Export Binary Backup (Serialization) | ✓ | ✗ | ✗ |
| Restore Backup (Deserialization + Filter) | ✓ | ✗ | ✗ |
| View File-based Audit Logs | ✓ | ✗ | ✗ |

