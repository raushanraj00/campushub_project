package com.campushub.utilities;

import com.campushub.dao.BookDAO;
import com.campushub.dao.BookingDAO;
import com.campushub.dao.StudentDAO;
import com.campushub.models.Book;
import com.campushub.models.Room;
import com.campushub.models.Student;
import java.io.IOException;
import java.util.List;

/**
 * UNIT 2: MULTITHREADING & THREAD LIFECYCLE
 * BackupThread demonstrates thread creation by EXTENDING Thread.
 * Executes background serialization without freezing the Swing UI.
 * Thread Lifecycle States Demonstrated:
 * - NEW: When instantiated: new BackupThread()
 * - RUNNABLE: When thread.start() is invoked
 * - TIMED_WAITING: During Thread.sleep() intervals
 * - TERMINATED: Upon completing run() method
 */
public class BackupThread extends Thread {

    public interface BackupCallback {
        void onComplete(boolean success, String message);
    }

    private final String backupFilePath;
    private final BackupCallback callback;
    private boolean completed = false;

    public BackupThread(String backupFilePath, BackupCallback callback) {
        super("CampusHub-BackupWorker");
        this.backupFilePath = backupFilePath;
        this.callback = callback;
    }

    @Override
    public void run() {
        System.out.println("[BackupThread] Started in state: " + this.getState());
        try {
            // Simulate brief background processing time
            Thread.sleep(800); // TIMED_WAITING state

            // Collect active memory / DB records
            List<Student> students = new StudentDAO().getAllStudents();
            List<Book> books = new BookDAO().getAllBooks();
            List<Room> rooms = new BookingDAO().getAllRooms();

            BackupManager.BackupData data = new BackupManager.BackupData(students, books, rooms);
            boolean success = BackupManager.exportBackup(data, backupFilePath);

            completed = true;
            if (callback != null) {
                callback.onComplete(success, "Backup successfully written to " + backupFilePath);
            }
        } catch (InterruptedException e) {
            System.err.println("[BackupThread] Interrupted: " + e.getMessage());
            if (callback != null) callback.onComplete(false, "Backup interrupted.");
        } catch (IOException e) {
            System.err.println("[BackupThread] IO Failure: " + e.getMessage());
            if (callback != null) callback.onComplete(false, "Backup IO Error: " + e.getMessage());
        }
        System.out.println("[BackupThread] Finished in state: " + this.getState());
    }

    public boolean isCompleted() {
        return completed;
    }
}

