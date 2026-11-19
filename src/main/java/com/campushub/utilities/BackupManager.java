package com.campushub.utilities;

import com.campushub.models.Book;
import com.campushub.models.Room;
import com.campushub.models.Student;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 2: OBJECT SERIALIZATION, DESERIALIZATION & FILTER STREAMS
 * BackupManager demonstrates complete object serialization lifecycle:
 * - FileOutputStream, BufferedOutputStream, ObjectOutputStream
 * - FileInputStream, BufferedInputStream, ObjectInputStream
 * - ObjectInputFilter for secure class validation during deserialization
 */
public class BackupManager {

    private static final String DEFAULT_BACKUP_PATH = "backups/campus_backup.dat";

    /**
     * DTO containing all serializable application state.
     */
    public static class BackupData implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Student> students;
        private final List<Book> books;
        private final List<Room> rooms;
        private final long timestamp;

        public BackupData(List<Student> students, List<Book> books, List<Room> rooms) {
            this.students = (students != null) ? new ArrayList<>(students) : new ArrayList<>();
            this.books = (books != null) ? new ArrayList<>(books) : new ArrayList<>();
            this.rooms = (rooms != null) ? new ArrayList<>(rooms) : new ArrayList<>();
            this.timestamp = System.currentTimeMillis();
        }

        public List<Student> getStudents() { return students; }
        public List<Book> getBooks() { return books; }
        public List<Room> getRooms() { return rooms; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * SERIALIZATION: Serializes student, book and room lists into a binary file.
     */
    public static synchronized boolean exportBackup(BackupData data, String filePath) throws IOException {
        String targetPath = (filePath != null && !filePath.trim().isEmpty()) ? filePath : DEFAULT_BACKUP_PATH;
        File file = new File(targetPath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // Serialization Pipeline: FileOutputStream -> BufferedOutputStream -> ObjectOutputStream
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            
            oos.writeObject(data);
            oos.flush();
            ActivityLogger.log("BACKUP_EXPORT", "Exported backup to " + file.getName() 
                    + " (" + data.getStudents().size() + " students, " 
                    + data.getBooks().size() + " books)", "SYSTEM");
            return true;
        }
    }

    /**
     * DESERIALIZATION: Reads and restores Java objects from a binary backup file.
     * Applies ObjectInputFilter for class validation.
     */
    public static synchronized BackupData importBackup(String filePath) throws IOException, ClassNotFoundException {
        String targetPath = (filePath != null && !filePath.trim().isEmpty()) ? filePath : DEFAULT_BACKUP_PATH;
        File file = new File(targetPath);
        if (!file.exists()) {
            throw new IOException("Backup file not found at: " + file.getAbsolutePath());
        }

        // Deserialization Pipeline: FileInputStream -> BufferedInputStream -> ObjectInputStream
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            // ObjectInputFilter: Whitelist allowed classes during deserialization for security
            ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
                    "com.campushub.utilities.BackupManager$BackupData;"
                  + "com.campushub.models.*;"
                  + "java.util.*;"
                  + "java.lang.*;"
                  + "java.time.*;"
                  + "!*" // reject any other class
            );
            ois.setObjectInputFilter(filter);

            Object obj = ois.readObject();
            if (obj instanceof BackupData) {
                BackupData restored = (BackupData) obj;
                ActivityLogger.log("BACKUP_IMPORT", "Imported backup from " + file.getName() 
                        + " containing " + restored.getStudents().size() + " students.", "SYSTEM");
                return restored;
            } else {
                throw new IOException("Unexpected serialized object type: " + obj.getClass().getName());
            }
        }
    }

    public static String getDefaultBackupPath() {
        return DEFAULT_BACKUP_PATH;
    }
}

