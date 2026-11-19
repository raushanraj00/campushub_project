# CampusHub – Multithreading & Synchronization Demonstration Guide

This document explains where and how **Unit 2: Multithreading & Synchronization** concepts are implemented and proven in **CampusHub**.

---

## 1. Thread Lifecycle Implementation & States

In Java, a thread exists in one of six states defined by `Thread.State`. Here is how they are verified in CampusHub:

```
                  Java Thread Lifecycle in CampusHub
                  
       [ NEW ] ─────────────────────────► [ RUNNABLE ]
 (new BackupThread())                     (worker.start())
                                                │
                                                ▼
                                         [ TIMED_WAITING ]
                                        (Thread.sleep(800))
                                                │
                                                ▼
     [ BLOCKED ] ◄────────────── (Competing on synchronized lock)
(Blocked on Room monitor)                       │
                                                ▼
                                         [ TERMINATED ]
                                    (run() method finishes)
```

| Thread State | How Triggered in CampusHub | Verification / Source Code |
| :--- | :--- | :--- |
| **NEW** | `Thread t = new Thread(worker);` | Prior to calling `.start()`, verified in `MultithreadingTest#testThreadCreationAndLifecycle`. |
| **RUNNABLE** | `worker.start();` | When JVM thread scheduler assigns CPU execution time. |
| **TIMED_WAITING** | `Thread.sleep(millis)` in `NotificationThread` and `BackupThread`. | Tested in `MultithreadingTest` during sleep intervals. |
| **BLOCKED** | Concurrent thread attempts to enter `synchronized boolean bookRoom()` while another thread holds the object monitor. | Verified in `BookingPanel#runConcurrencyTest` and `Room.java#L47`. |
| **WAITING** | Calling `producerThread.join()` in `ReportManager.java`. | Caller thread pauses until the target worker thread completes. |
| **TERMINATED** | Thread's `run()` method finishes. | Verified in `MultithreadingTest#testThreadCreationAndLifecycle`. |

---

## 2. Two Distinct Ways of Creating Threads

### 1. Extending `java.lang.Thread`
- **Class**: `src/main/java/com/campushub/utilities/BackupThread.java`
- **Declaration**:
  ```java
  public class BackupThread extends Thread {
      @Override
      public void run() {
          // Asynchronously serializes campus records without freezing Swing EDT
      }
  }
  ```
- **Usage**:
  ```java
  BackupThread backupWorker = new BackupThread(filePath, callback);
  backupWorker.start();
  ```

### 2. Implementing `java.lang.Runnable`
- **Class**: `src/main/java/com/campushub/utilities/NotificationThread.java`
- **Declaration**:
  ```java
  public class NotificationThread implements Runnable {
      @Override
      public void run() {
          // Executes periodic background campus audit alerts
      }
  }
  ```
- **Usage**:
  ```java
  NotificationThread worker = new NotificationThread(60000, listener);
  Thread daemonThread = new Thread(worker, "CampusHub-DaemonNotifier");
  daemonThread.setDaemon(true);
  daemonThread.start();
  ```

---

## 3. Thread Synchronization: Preventing Race Conditions

### The Race Condition Problem
When two students or faculty members attempt to book the same room for the same time slot simultaneously:
- Without synchronization, both threads could check `isAvailable() == true` concurrently.
- Both threads write their booking into the database, causing a **double reservation collision**.

### CampusHub's Multi-Tier Protection
1. **Java Monitor Lock (`synchronized` method)**:
   ```java
   // In Room.java:
   public synchronized boolean bookRoom() {
       if (this.available) {
           this.available = false;
           return true; // Granted
       }
       return false; // Rejected
   }
   ```
   Only one thread can hold the intrinsic lock of the `Room` instance at a time. The competing thread is placed into the **BLOCKED** state until the first thread exits the synchronized method.

2. **Database Transaction & Pessimistic Locking**:
   In `BookingDAO.java`, SQL transactions use `FOR UPDATE` queries to lock conflicting rows:
   ```sql
   SELECT booking_id FROM bookings 
   WHERE room_id = ? AND status = 'CONFIRMED' 
   AND (start_time < ? AND end_time > ?) FOR UPDATE;
   ```
   If an overlap is detected, the transaction executes `conn.rollback()` and throws `BookingConflictException`.

---

## 4. How to Demonstrate to Your Examiner

1. Navigate to **"Rooms & Bookings"** panel.
2. Select any room from the top table (e.g., `Lab 101`).
3. Click the red button: **"⚡ Demo: Test Concurrent Booking Collision"**.
4. A diagnostic console window will immediately execute two competing background threads (`BookingThread-User1` and `BookingThread-User2`) attempting to reserve the exact same room at the exact same overlapping time window.
5. **Show your examiner the result**:
   - Exactly **one thread succeeds** and commits its transaction.
   - The second thread is **blocked/rejected**, catches `BookingConflictException`, and rolls back.
   - Database integrity is 100% maintained with zero double-bookings.

