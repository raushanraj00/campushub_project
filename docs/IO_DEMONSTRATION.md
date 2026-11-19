# CampusHub – Java IO Package Demonstration Guide

This document details where and how every topic from **Unit 2: IO Package and Streams** is implemented in **CampusHub**.

---

## 1. Overview of IO Concepts Implemented

```
                     java.io Streams Hierarchy in CampusHub
                                      │
            ┌─────────────────────────┴─────────────────────────┐
            ▼                                                   ▼
     Byte Input Streams                                Byte Output Streams
  (FileInputStream)                                   (FileOutputStream)
            │                                                   │
            ▼                                                   ▼
     Filter Streams                                      Filter Streams
 (BufferedInputStream)                               (BufferedOutputStream)
            │                                                   │
            ▼                                                   ▼
    Object Deserialization                              Object Serialization
   (ObjectInputStream)                                 (ObjectOutputStream)
            │                                                   │
            ▼                                                   ▼
Restored Models in Memory                         Target File: backups/campus_backup.dat
 (Students, Books, Rooms)
```

---

## 2. Topic-by-Topic Implementation Details

### A. Input Streams & Output Streams
- **Output Streams**: `FileOutputStream` and `BufferedOutputStream` in `src/main/java/com/campushub/utilities/ActivityLogger.java` and `BackupManager.java`.
- **Input Streams**: `FileInputStream` and `BufferedInputStream` in `BackupManager.java` and `ActivityLogger.java`.

### B. Object Serialization
- **Class**: `src/main/java/com/campushub/utilities/BackupManager.java#exportBackup()`
- **Pipeline Used**:
  ```java
  try (FileOutputStream fos = new FileOutputStream(file);
       BufferedOutputStream bos = new BufferedOutputStream(fos);
       ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(data);
      oos.flush();
  }
  ```
- **Serializable Models**: `Person`, `User`, `Student`, `Faculty`, `Admin`, `Book`, `Room`, `Booking` all implement `java.io.Serializable` with defined `serialVersionUID`.

### C. Object Deserialization & ObjectInputFilter
- **Class**: `src/main/java/com/campushub/utilities/BackupManager.java#importBackup()`
- **Pipeline Used**:
  ```java
  try (FileInputStream fis = new FileInputStream(file);
       BufferedInputStream bis = new BufferedInputStream(fis);
       ObjectInputStream ois = new ObjectInputStream(bis)) {

      // Security Whitelist Filter: Blocks unauthorized arbitrary class deserialization
      ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
              "com.campushub.utilities.BackupManager$BackupData;"
            + "com.campushub.models.*;"
            + "java.util.*;"
            + "java.time.*;"
            + "!*" // Reject any unexpected classes
      );
      ois.setObjectInputFilter(filter);
      BackupData restored = (BackupData) ois.readObject();
  }
  ```

### D. Filter Streams (Buffered Streams)
- `BufferedOutputStream`: Buffers byte writes to reduce physical disk I/O operations.
- `BufferedInputStream`: Reads chunks of bytes into memory buffer for performance.
- Implemented in `BackupManager.java` and `ActivityLogger.java`.

### E. Pipe Streams (Producer-Consumer Inter-Thread Data Transfer)
- **Classes**:
  - `src/main/java/com/campushub/utilities/ReportProducer.java` (Writes to `PipedOutputStream`)
  - `src/main/java/com/campushub/utilities/ReportConsumer.java` (Reads from `PipedInputStream`)
  - `src/main/java/com/campushub/utilities/ReportManager.java` (Wires streams together)
- **Code**:
  ```java
  PipedOutputStream pipedOut = new PipedOutputStream();
  PipedInputStream pipedIn = new PipedInputStream(pipedOut); // Connects streams

  Thread producerThread = new Thread(new ReportProducer(pipedOut, title));
  Thread consumerThread = new Thread(new ReportConsumer(pipedIn));

  producerThread.start();
  consumerThread.start();
  ```

### F. IO Files & Activity Logging
- **Class**: `src/main/java/com/campushub/utilities/ActivityLogger.java`
- Demonstrates:
  - Checking directory existence: `new File("logs").mkdirs()`
  - Checking file existence: `file.exists()`
  - Appending to file: `new FileOutputStream(file, true)`
  - Line-by-line reading: `BufferedReader` wrapping `FileReader`

---

## 3. Practical Demonstration Instructions for the Examiner

1. **Demonstrate Serialization**:
   - Log in as `admin` (`Admin@123`).
   - Click **"System & OOP Tools"** in the sidebar.
   - Click the blue button: **"Run Background Serialization Thread"**.
   - Show the examiner the generated binary file created at `backups/campus_backup.dat`.

2. **Demonstrate Deserialization**:
   - Click the green button: **"Deserialize & Validate Backup File"**.
   - Show the console output displaying the exact count of restored `Student`, `Book`, and `Room` objects reconstructed directly from the binary stream.

3. **Demonstrate Pipe Streams**:
   - Click the purple button: **"Run Producer-Consumer Pipe Stream Test"**.
   - Show the examiner the live output streamed byte-by-byte from the `ReportProducer` thread through the connected pipe into the `ReportConsumer` thread.

4. **Demonstrate File Logging**:
   - Click the amber button: **"Read Activity Log File"**.
   - Show the audit trail read from `logs/campus_activity.log`.

