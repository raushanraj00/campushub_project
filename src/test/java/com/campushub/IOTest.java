package com.campushub;

import com.campushub.models.Book;
import com.campushub.models.Room;
import com.campushub.models.Student;
import com.campushub.utilities.ActivityLogger;
import com.campushub.utilities.BackupManager;
import com.campushub.utilities.ReportManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 2: JUNIT 5 TEST SUITE FOR JAVA IO PACKAGE
 * Tests Serialization, Deserialization, Filter Streams, Pipe Streams, and File Logging.
 */
public class IOTest {

    @Test
    public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
        String testBackupFile = "backups/test_backup.dat";

        List<Student> students = new ArrayList<>();
        students.add(new Student("23CS001", "Test Student", "test@campus.edu", "9876543210", "CSE", 1, "A"));

        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Clean Code", "Robert C. Martin", "978-0132350884", "Software", 5, 5));

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, "Lab 101", "LAB", "Block A", 40, true));

        BackupManager.BackupData originalData = new BackupManager.BackupData(students, books, rooms);

        // 1. Serialization (ObjectOutputStream + BufferedOutputStream + FileOutputStream)
        boolean exported = BackupManager.exportBackup(originalData, testBackupFile);
        Assertions.assertTrue(exported);
        Assertions.assertTrue(new File(testBackupFile).exists());

        // 2. Deserialization (ObjectInputStream + BufferedInputStream + FileInputStream + ObjectInputFilter)
        BackupManager.BackupData restoredData = BackupManager.importBackup(testBackupFile);
        Assertions.assertNotNull(restoredData);
        Assertions.assertEquals(1, restoredData.getStudents().size());
        Assertions.assertEquals("23CS001", restoredData.getStudents().get(0).getUid());
        Assertions.assertEquals(1, restoredData.getBooks().size());
        Assertions.assertEquals("Clean Code", restoredData.getBooks().get(0).getTitle());
        Assertions.assertEquals(1, restoredData.getRooms().size());

        // Cleanup
        new File(testBackupFile).delete();
    }

    @Test
    public void testPipeStreamsProducerConsumer() {
        // Connected PipedOutputStream and PipedInputStream
        String report = ReportManager.generatePipedReport("Unit Test Pipe Transfer");
        Assertions.assertNotNull(report);
        Assertions.assertTrue(report.contains("CAMPUSHUB COMPILED REPORT"));
        Assertions.assertTrue(report.contains("SECTION 1"));
        Assertions.assertTrue(report.contains("END OF REPORT STREAM"));
    }

    @Test
    public void testActivityFileLogging() {
        ActivityLogger.log("JUNIT_TEST", "Verified IO File streams", "TEST_RUNNER");
        List<String> logs = ActivityLogger.readRecentLogs(10);
        Assertions.assertFalse(logs.isEmpty());
        Assertions.assertTrue(logs.stream().anyMatch(l -> l.contains("JUNIT_TEST")));
    }
}

