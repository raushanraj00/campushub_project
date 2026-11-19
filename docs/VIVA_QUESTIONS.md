# CampusHub – University Practical Viva Questions & Answers (50+ Questions)

This comprehensive question-and-answer guide is tailored specifically for your **End-Semester Java OOP Practical Examination**. Every answer explains both the theoretical concept and the exact place it is implemented in **CampusHub**.

---

## Part 1: Java Fundamentals, JVM, JDK & Bytecode

### 1. What is the difference between JDK, JRE, and JVM?
- **JVM (Java Virtual Machine)**: The abstract runtime engine that executes compiled Java bytecode.
- **JRE (Java Runtime Environment)**: JVM + core standard libraries needed to run Java applications.
- **JDK (Java Development Kit)**: Complete development package containing JRE + developer tools (`javac`, `jar`, `javadoc`). In our project, we used OpenJDK 17.

### 2. What is Java Bytecode and why is Java called platform-independent?
When Java source code (`.java`) is compiled by `javac`, it generates an intermediate machine-independent format called **bytecode** (`.class` files). Any operating system with a compatible JVM can execute this bytecode without recompilation (*"Write Once, Run Anywhere"*).

### 3. What is the purpose of the `main()` method signature in Java?
`public static void main(String[] args)`:
- `public`: Accessible by the JVM from outside the class package.
- `static`: Can be invoked without creating an instance of the enclosing class.
- `void`: Returns no value.
- `String[] args`: Accepts command-line parameters. Implemented in `src/main/java/com/campushub/Main.java`.

---

## Part 2: Classes, Objects, Constructors & OOP Core

### 4. What is the difference between a Class and an Object?
A **class** is a blueprint/template defining variables and methods (e.g., `Student.java`). An **object** is a concrete instance of that class allocated in memory with distinct attribute states (e.g., student with UID `"23CS101"`).

### 5. What are the different types of constructors in Java?
1. **Default Constructor**: Parameterless constructor provided implicitly or explicitly.
2. **Parameterized Constructor**: Accepts arguments to initialize specific instance state.
3. **Copy Constructor**: Initializes a new object by copying fields from an existing object (`Student(Student source)`).

### 6. What is Constructor Chaining?
Calling one constructor from another constructor within the same class using `this()` or in the superclass using `super()`. In `Student.java`, `Student()` calls `this(0, "Pending UID", ...)`.

### 7. What is the difference between `this` and `super`?
- `this`: Refers to the current object instance (used to resolve naming conflicts like `this.uid = uid;` or call overloaded constructors).
- `super`: Refers to the immediate parent superclass (used to invoke parent constructors `super(name, email);` or parent methods).

### 8. What is Encapsulation and how did you implement it?
Encapsulation binds data and code into a single unit while restricting direct field access using `private` modifiers. Public getters and setters enforce invariants (e.g., validating that `email` contains `'@'` and quantity is not negative in `Book.java`).

---

## Part 3: Inheritance & Polymorphism

### 9. What types of inheritance does Java support?
- **Single Inheritance**: One child extends one parent (`User extends Person`).
- **Multilevel Inheritance**: A chain of inheritance (`Person` $\rightarrow$ `User` $\rightarrow$ `Student`).
- **Hierarchical Inheritance**: Multiple children extend one parent (`User` extended by `Student`, `Faculty`, and `Admin`).
- Java **does NOT** support multiple class inheritance (to prevent the Diamond Problem), but supports multiple inheritance of type through **interfaces**.

### 10. What is the Diamond Problem and why doesn't Java allow multiple class inheritance?
If class `D` extends both `B` and `C`, and both inherit an overridden method from `A`, the compiler cannot determine which method `D` should inherit. Java avoids this ambiguity by prohibiting multiple class extension while allowing multiple interface implementation.

### 11. What is the difference between Method Overloading and Method Overriding?
- **Method Overloading (Compile-Time Polymorphism)**: Methods in the same class sharing the same name but with different parameter signatures (`BookDAO.searchBook(int)` vs `searchBook(String)`).
- **Method Overriding (Runtime Polymorphism)**: A subclass provides its own specific implementation of a method already declared in its superclass (`Student.getRole()` overriding `User.getRole()`).

### 12. What is Dynamic Method Dispatch (Runtime Polymorphism)?
When an overridden method is called through a parent superclass reference variable, Java resolves which method implementation to execute at runtime based on the actual object type created in memory (`User user = new Student(...); user.getDetails()`).

### 13. What is an Abstract Class?
A class declared with the `abstract` keyword that cannot be instantiated directly with `new`. It can contain both abstract methods (without bodies) and concrete methods. In our project, `Person` and `User` are abstract classes.

### 14. What is an Interface and how does it differ from an Abstract Class?
An interface is a contract containing abstract method declarations. A class can implement multiple interfaces (`implements BookOperations, Authenticatable`), whereas it can extend only one abstract class. Interfaces define capabilities (*"what"*), while classes define state and implementation (*"how"*).

### 15. Where have you used a Non-Static Inner Class?
In `src/main/java/com/campushub/models/Booking.java`, the non-static inner class `BookingDetails` directly accesses its enclosing outer instance variables (`Booking.this.bookingId`, `Booking.this.purpose`) to format the official reservation pass.

### 16. What is a Static Nested Class?
A static class declared inside an outer class. Unlike inner classes, it does not hold a reference to an outer instance. We used `DBConnection.DatabaseConfig` to encapsulate MySQL connection URLs and credentials.

---

## Part 4: Exception Handling

### 17. What is the difference between Checked and Unchecked Exceptions?
- **Checked Exceptions**: Subclasses of `java.lang.Exception` (excluding `RuntimeException`). Checked at compile-time and must be caught or declared (`throws`). Examples: `SQLException`, `IOException`, `BookNotAvailableException`.
- **Unchecked Exceptions**: Subclasses of `java.lang.RuntimeException`. Occur due to programming logic errors. Example: `NullPointerException`, `IllegalArgumentException`.

### 18. What is the difference between `throw` and `throws`?
- `throw`: Used explicitly inside a method body to instantiate and trigger an exception (`throw new BookNotAvailableException(...);`).
- `throws`: Used in the method signature to declare that the method might throw exceptions to its caller.

### 19. What is the purpose of the `finally` block?
Code inside `finally` is guaranteed to execute regardless of whether an exception was thrown or caught. Commonly used to release critical system resources.

### 20. What is Try-With-Resources?
Introduced in Java 7, any resource implementing `java.lang.AutoCloseable` declared within `try (...)` is automatically closed at the end of the block without needing an explicit `finally` block. We used this across all DAOs and File Streams.

---

## Part 5: Java IO Package & Streams

### 21. What is a Stream in Java?
A stream is a continuous sequence of data elements. In Java, streams are divided into:
- **Byte Streams** (`InputStream` / `OutputStream`): Read and write raw binary 8-bit bytes.
- **Character Streams** (`Reader` / `Writer`): Read and write 16-bit Unicode characters.

### 22. What are Filter Streams?
Filter streams wrap existing underlying streams to provide buffered performance or additional processing. `BufferedOutputStream` wraps `FileOutputStream` to avoid writing single bytes to disk repeatedly.

### 23. What is Object Serialization?
Converting the in-memory state of a Java object into a portable byte stream using `java.io.ObjectOutputStream`. The class must implement `java.io.Serializable`. Implemented in `BackupManager.java#exportBackup()`.

### 24. What is Deserialization?
The reverse process of reading a byte stream using `ObjectInputStream` to reconstruct the live Java object in memory.

### 25. What is `serialVersionUID` and why is it important?
A universal version identifier for a `Serializable` class. It ensures that the deserializer verifies that the sender and receiver of a serialized object have loaded classes for that object that are compatible.

### 26. What is `ObjectInputFilter`?
A security mechanism in modern Java that whitelists which classes are permitted during deserialization, preventing arbitrary code execution attacks from malicious serialized data.

### 27. What are Pipe Streams in Java?
`PipedOutputStream` and `PipedInputStream` connected together allow two separate threads to communicate directly by passing byte streams (Producer-Consumer pattern). Implemented in `ReportProducer` and `ReportConsumer`.

---

## Part 6: Multithreading & Synchronization

### 28. What are the two ways to create a thread in Java?
1. Extending `java.lang.Thread` and overriding `run()` (demonstrated in `BackupThread.java`).
2. Implementing `java.lang.Runnable` and passing it to a `Thread` instance (demonstrated in `NotificationThread.java`).

### 29. What are the states in the Java Thread Lifecycle?
1. **NEW**: Instantiated but `start()` has not been called.
2. **RUNNABLE**: Executing in JVM or eligible for CPU scheduler execution.
3. **BLOCKED**: Waiting to acquire a monitor lock held by another thread.
4. **WAITING**: Waiting indefinitely for another thread (e.g., `join()`).
5. **TIMED_WAITING**: Sleeping for a specified interval (`Thread.sleep()`).
6. **TERMINATED**: `run()` execution has finished.

### 30. What is a Race Condition?
A bug where multiple threads access shared mutable state simultaneously, and the final outcome depends on the unpredictable timing/interleaving of thread execution.

### 31. How does Synchronization solve race conditions?
By using the `synchronized` keyword, only one thread can hold the intrinsic lock (monitor) of the object at any time. In `Room.java`, `synchronized boolean bookRoom()` prevents two threads from booking the same room concurrently.

### 32. What is a Daemon Thread?
A low-priority background thread that runs to support user threads. When all user threads terminate, the JVM exits immediately without waiting for daemon threads to finish. We used a daemon thread for `NotificationThread`.

### 33. What is the difference between `start()` and `run()`?
- `start()`: Allocates a new operating system thread and registers it with the JVM scheduler, which then calls `run()` on the new thread.
- `run()`: Executes the method synchronously in the caller's current thread like an ordinary method call.

---

## Part 7: JDBC & Database Architecture

### 34. What is JDBC?
Java Database Connectivity is a standardized API that enables Java applications to interact with relational databases independently of database vendors.

### 35. Explain the JDBC Architecture.
Java Application $\rightarrow$ JDBC API (`java.sql.*`) $\rightarrow$ JDBC Driver (`com.mysql.cj.jdbc.Driver`) $\rightarrow$ Relational Database (MySQL).

### 36. What are the four types of JDBC drivers?
1. Type-1: JDBC-ODBC Bridge driver.
2. Type-2: Native-API driver (partly Java).
3. Type-3: Network-Protocol driver.
4. Type-4: Thin Driver (Direct-to-Database pure Java driver). MySQL Connector/J 8.3 is a Type-4 driver.

### 37. What is the difference between `Statement` and `PreparedStatement`?
- **Statement**: Compiles SQL queries at runtime on every execution. Vulnerable to SQL Injection. Used in `DatabaseInitializer.java` for static DDL table creation.
- **PreparedStatement**: Precompiles SQL queries on the database server using `?` placeholders. Faster for repeated execution and completely immune to SQL Injection. Used for all CRUD operations in CampusHub.

### 38. What is a `ResultSet`?
An object that maintains a cursor pointing to a table of data returned from a database query. Initially the cursor is positioned before the first row. Calling `rs.next()` moves the cursor to the next row and returns `false` when no more rows exist.

### 39. What is an SQL Transaction in JDBC?
A sequence of database operations executed as a single logical unit of work satisfying ACID properties (Atomicity, Consistency, Isolation, Durability).
- `conn.setAutoCommit(false)`: Disables automatic saving.
- `conn.commit()`: Permanently saves changes.
- `conn.rollback()`: Undoes all changes if an error occurs.

### 40. Where did you implement transactions in CampusHub?
In `BookDAO.java#issueBookTransaction()` (decrementing available inventory and inserting checkout record atomically) and `BookingDAO.java#reserveRoomTransaction()` (pessimistic locking and reservation).

### 41. What is SQL Injection and how did you prevent it?
An attack where malicious SQL statements are inserted into entry fields. We prevented it by strictly using parameterized `PreparedStatement` across all database calls instead of string concatenation.

---

## Part 8: Java Swing GUI

### 42. What is Java Swing and how does it differ from AWT?
AWT (Abstract Window Toolkit) components are heavy-weight, platform-dependent, and rely on native OS controls. Swing is built on top of AWT, but its components are 100% written in pure Java (light-weight), fully customizable, and platform-independent.

### 43. What is the Event Dispatch Thread (EDT)?
Swing's dedicated single background thread that handles all UI events, repainting, and component updates. All GUI modifications should be executed on the EDT using `SwingUtilities.invokeLater()`.

### 44. What happens if you run long-running tasks on the EDT?
The GUI freezes and becomes unresponsive (*"Application Not Responding"*). We offload database lookups, background backups, and report generation to worker threads so the Swing UI remains smooth.

### 45. What Layout Managers did you use?
- `BorderLayout`: Main frames and card headers (North, South, East, West, Center).
- `GridLayout`: Metrics cards and dual tables in `BookingPanel`.
- `GridBagLayout`: Dialog forms (`StudentPanel`, `LoginFrame`) for precise component alignment.
- `CardLayout`: Central display in `DashboardFrame` for switching sidebar navigation screens.

### 46. What is FlatLaf?
A clean, modern Look and Feel library for Java Swing inspired by IntelliJ and modern desktop OS designs.

---

## Part 9: Security & Application Architecture

### 47. Why should passwords never be stored in plain text?
If the database is leaked, plain passwords expose all user accounts. We use **PBKDF2 with HMAC-SHA256** and cryptographically generated random salt (`AuthService.java`) so passwords cannot be cracked using rainbow tables.

### 48. What is Salt in password hashing?
A sequence of random bytes generated for each user and concatenated with the password before hashing. It ensures that two users with identical passwords will produce completely different hashes.

### 49. What is the Layered Architecture pattern?
Separating code into distinct logical tiers:
1. **Presentation Layer (GUI)**: Handles screens, forms, and user clicks (`gui/`).
2. **Service Layer**: Business rules, fine calculations, and validation (`services/`).
3. **Data Access Object (DAO) Layer**: Database SQL queries and transactions (`dao/`).
4. **Database Layer**: Persistent MySQL storage.

### 50. Why did you use this project instead of a basic console project?
A console project only tests isolated code syntax. CampusHub integrates every concept into a meaningful, enterprise-grade architecture: user roles model inheritance and polymorphism, reservations model thread synchronization, backups demonstrate serialization, and records demonstrate JDBC CRUD transactions.

