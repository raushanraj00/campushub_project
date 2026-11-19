# CampusHub – Testing & Quality Assurance Report

This report documents the verification, automated testing, and manual integration tests executed for **CampusHub – Smart Campus Management System**.

---

## 1. Test Environment

- **Operating System**: Windows 11 (build 10.0)
- **Java Development Kit**: OpenJDK 17.0.20.1 LTS
- **Build Tool**: Apache Maven 3.9.6
- **Test Framework**: JUnit 5 (Jupiter 5.10.2)
- **Database Engine**: MySQL 8.0 Server (Windows Service: `MySQL80`)
- **JDBC Driver**: MySQL Connector/J 8.3.0

---

## 2. Automated JUnit 5 Test Suite Results

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.campushub.IOTest
[ActivityLogger] Appended file log: JUNIT_TEST
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.087 s -- in com.campushub.IOTest

Running com.campushub.MultithreadingTest
[NotificationThread] Runnable worker started in thread: TestWorkerThread
[NotificationThread] Interrupted. Stopping cleanly.
[NotificationThread] Worker completed execution.
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.190 s -- in com.campushub.MultithreadingTest

Running com.campushub.OOPConceptsTest
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.062 s -- in com.campushub.OOPConceptsTest

Results:
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS (Total time: 7.826 s)
```

---

## 3. Comprehensive Verification Matrix (22 Target Test Cases)

| Test ID | Test Description | Target Area | Method / Assertion | Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Valid User Login | AuthService | Verifies PBKDF2 hash against salt | **PASS** |
| **TC-02** | Invalid Password Handling | AuthService | Throws `InvalidUserException` on mismatch | **PASS** |
| **TC-03** | Student Registration | StudentDAO | Inserts record with parameterized query | **PASS** |
| **TC-04** | Duplicate UID Validation | StudentService | Rejects duplicate UID with `IllegalArgumentException` | **PASS** |
| **TC-05** | Student Update | StudentDAO | Executes UPDATE on academic details | **PASS** |
| **TC-06** | Student Deletion | StudentDAO | Deletes by student ID, cascading constraints | **PASS** |
| **TC-07** | Book Creation & Inventory | BookDAO | Adds book with total & available counts | **PASS** |
| **TC-08** | Overloaded Book Search | BookDAO | 3 Overloaded `searchBook` signatures | **PASS** |
| **TC-09** | Book Issuing Transaction | BookDAO | Decrements inventory and creates issued record | **PASS** |
| **TC-10** | Book Return & Fine Calculation | LibraryService | Returns inventory copy and computes late fine | **PASS** |
| **TC-11** | Unavailable Book Exception | Book | Throws `BookNotAvailableException` if qty is 0 | **PASS** |
| **TC-12** | Room Booking | BookingDAO | Inserts reservation slot into database | **PASS** |
| **TC-13** | Duplicate Booking Conflict | BookingDAO | Overlapping timeslot throws `BookingConflictException` | **PASS** |
| **TC-14** | Concurrent Booking Collision | BookingService | Spawns 2 threads; exactly one succeeds | **PASS** |
| **TC-15** | Object Serialization | BackupManager | Writes objects via `ObjectOutputStream` | **PASS** |
| **TC-16** | Object Deserialization & Filter | BackupManager | Restores objects via `ObjectInputStream` + Filter | **PASS** |
| **TC-17** | File Stream Input/Output | ActivityLogger | `FileOutputStream` & `BufferedOutputStream` | **PASS** |
| **TC-18** | Pipe Stream Transfer | ReportManager | `PipedOutputStream` $\rightarrow$ `PipedInputStream` | **PASS** |
| **TC-19** | Multithreading Lifecycle | Threads | Verifies `NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED` | **PASS** |
| **TC-20** | JDBC Database Connectivity | DBConnection | `DriverManager.getConnection()` connects to MySQL | **PASS** |
| **TC-21** | JDBC CRUD & Transactions | All DAOs | `PreparedStatement`, `ResultSet`, commit & rollback | **PASS** |
| **TC-22** | Role-Based Authorization | GUI & Service | Admin vs Student vs Faculty permission gates | **PASS** |

