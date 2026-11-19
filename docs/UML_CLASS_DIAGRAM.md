# CampusHub – UML Diagrams & Architecture

This document contains complete UML representations of **CampusHub – Smart Campus Management System** rendered via standard GitHub Mermaid format.

---

## 1. Class Inheritance & Interface Implementation Hierarchy

```mermaid
classDiagram
    direction TB

    class Serializable {
        <<interface>>
    }

    class Authenticatable {
        <<interface>>
        +authenticate(String password) boolean
        +getRole() String
    }

    class BookOperations {
        <<interface>>
        +issueBook(int bookId) void
        +returnBook(int bookId) void
    }

    class BookingOperations {
        <<interface>>
        +reserveRoom(int roomId, LocalDateTime start, LocalDateTime end, String purpose) boolean
        +cancelReservation(int bookingId) boolean
    }

    class Person {
        <<abstract>>
        #String name
        #String email
        +Person()
        +Person(String name, String email)
        +getDetails()* String
        +getName() String
        +getEmail() String
    }

    class User {
        <<abstract>>
        #int userId
        #String username
        #String passwordHash
        #String salt
        #String role
        +User()
        +User(int id, String name, String email, String username, String hash, String salt, String role)
        +getRole()* String
        +getDetails() String
        +authenticate(String password) boolean
    }

    class Student {
        -int studentId
        -String uid
        -String phone
        -String department
        -int semester
        -String section
        -LocalDate enrollmentDate
        +Student()
        +Student(String uid, String name, ...)
        +Student(Student source)
        +getRole() String
        +getDetails() String
        +issueBook(int bookId) void
        +returnBook(int bookId) void
    }

    class Faculty {
        -int facultyId
        -String empCode
        -String phone
        -String department
        -String designation
        +Faculty()
        +Faculty(String empCode, String name, ...)
        +getRole() String
        +getDetails() String
    }

    class Admin {
        -String adminLevel
        +Admin()
        +Admin(int id, String name, String email, ...)
        +getRole() String
        +getDetails() String
    }

    class Book {
        -int bookId
        -String title
        -String author
        -String isbn
        -String category
        -int totalQuantity
        -int availableQuantity
        +isAvailable() boolean
        +issueCopy() void
        +returnCopy() void
    }

    class Room {
        -int roomId
        -String roomName
        -String roomType
        -String building
        -int capacity
        -boolean available
        +bookRoom() synchronized boolean
        +releaseRoom() synchronized void
    }

    class Booking {
        -int bookingId
        -int userId
        -int roomId
        -LocalDateTime startTime
        -LocalDateTime endTime
        -String purpose
        -String status
        +getBookingDetails() BookingDetails
    }

    class BookingDetails {
        <<inner class>>
        -String generatedTimestamp
        +getReservationPass() String
        +getOuterBooking() Booking
    }

    %% Relationships
    Serializable <|.. Person
    Serializable <|.. Book
    Serializable <|.. Room
    Serializable <|.. Booking

    Person <|-- User
    Authenticatable <|.. User

    User <|-- Student
    User <|-- Faculty
    User <|-- Admin

    BookOperations <|.. Student
    Booking +-- BookingDetails
```

---

## 2. Layered Application Architecture Diagram

```mermaid
flowchart TD
    subgraph Presentation_Layer["1. Presentation Layer (Java Swing GUI)"]
        UI1["LoginFrame"]
        UI2["DashboardFrame"]
        UI3["StudentPanel"]
        UI4["FacultyPanel"]
        UI5["LibraryPanel"]
        UI6["BookingPanel"]
        UI7["SystemToolsPanel"]
    end

    subgraph Service_Layer["2. Service Layer (Business Logic & Validation)"]
        S1["AuthService (PBKDF2 Hashing)"]
        S2["StudentService"]
        S3["FacultyService"]
        S4["LibraryService (Fine Calculation)"]
        S5["BookingService (Concurrency Engine)"]
    end

    subgraph DAO_Layer["3. DAO Layer (JDBC CRUD & Transactions)"]
        D1["UserDAO"]
        D2["StudentDAO"]
        D3["FacultyDAO"]
        D4["BookDAO (Method Overloading)"]
        D5["BookingDAO (SQL Transactions)"]
        DConn["DBConnection (Singleton & Static Nested Config)"]
    end

    subgraph IO_Multithreading["4. IO Package & Multithreading Layer"]
        IO1["BackupManager (Object Serialization & Filter)"]
        IO2["ActivityLogger (Buffered Streams & Files)"]
        TH1["BackupThread (extends Thread)"]
        TH2["NotificationThread (implements Runnable)"]
        TH3["ReportProducer / Consumer (Piped Streams)"]
    end

    subgraph Database_Layer["5. MySQL Database Layer (campushub_db)"]
        DB1[("users")]
        DB2[("students")]
        DB3[("faculty")]
        DB4[("books")]
        DB5[("rooms")]
        DB6[("bookings")]
        DB7[("issued_books")]
        DB8[("activity_logs")]
    end

    Presentation_Layer --> Service_Layer
    Service_Layer --> DAO_Layer
    Presentation_Layer --> IO_Multithreading
    DAO_Layer --> DConn
    DConn --> Database_Layer
```

---

## 3. Database Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    users ||--o| students : "user_id"
    users ||--o| faculty : "user_id"
    users ||--o{ bookings : "user_id"
    rooms ||--o{ bookings : "room_id"
    students ||--o{ issued_books : "student_id"
    books ||--o{ issued_books : "book_id"

    users {
        int user_id PK
        string username UK
        string password_hash
        string salt
        string role
        timestamp created_at
    }

    students {
        int student_id PK
        string uid UK
        string full_name
        string email UK
        string phone
        string department
        int semester
        string section
        date enrollment_date
        int user_id FK
    }

    faculty {
        int faculty_id PK
        string emp_code UK
        string full_name
        string email UK
        string phone
        string department
        string designation
        int user_id FK
    }

    books {
        int book_id PK
        string title
        string author
        string isbn UK
        string category
        int total_quantity
        int available_quantity
    }

    rooms {
        int room_id PK
        string room_name UK
        string room_type
        string building
        int capacity
        boolean is_available
    }

    bookings {
        int booking_id PK
        int user_id FK
        int room_id FK
        datetime start_time
        datetime end_time
        string purpose
        string status
        timestamp created_at
    }

    issued_books {
        int issue_id PK
        int student_id FK
        int book_id FK
        date issue_date
        date due_date
        date return_date
        double fine_amount
        string status
    }

    activity_logs {
        int log_id PK
        string action
        string details
        string username
        timestamp timestamp
    }
```

