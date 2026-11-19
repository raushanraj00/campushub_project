package com.campushub;

import com.campushub.models.Room;
import com.campushub.services.LibraryService;
import com.campushub.utilities.NotificationThread;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UNIT 2: JUNIT 5 TEST SUITE FOR MULTITHREADING & SYNCHRONIZATION
 */
public class MultithreadingTest {

    @Test
    public void testThreadCreationAndLifecycle() throws InterruptedException {
        // Runnable implementation
        AtomicInteger notificationCount = new AtomicInteger(0);
        NotificationThread worker = new NotificationThread(50, alert -> notificationCount.incrementAndGet());

        Thread thread = new Thread(worker, "TestWorkerThread");
        Assertions.assertEquals(Thread.State.NEW, thread.getState(), "Thread should be NEW prior to start()");

        thread.start();
        Assertions.assertTrue(thread.getState() == Thread.State.RUNNABLE || thread.getState() == Thread.State.TIMED_WAITING);

        Thread.sleep(160);
        worker.stop();
        thread.interrupt();
        thread.join(1000);

        Assertions.assertEquals(Thread.State.TERMINATED, thread.getState(), "Thread should be TERMINATED after finish");
        Assertions.assertTrue(notificationCount.get() > 0, "Background worker must have triggered notifications");
    }

    @Test
    public void testRoomThreadSynchronization() throws InterruptedException {
        // Shared Room resource
        Room sharedRoom = new Room(10, "Lab 505", "LAB", "Central", 30, true);

        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger rejectedBookings = new AtomicInteger(0);

        // Spawn 2 parallel threads attempting to book the exact same single-capacity room
        Thread t1 = new Thread(() -> {
            if (sharedRoom.bookRoom()) {
                successfulBookings.incrementAndGet();
            } else {
                rejectedBookings.incrementAndGet();
            }
        });

        Thread t2 = new Thread(() -> {
            if (sharedRoom.bookRoom()) {
                successfulBookings.incrementAndGet();
            } else {
                rejectedBookings.incrementAndGet();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Exactly one thread must succeed, and the other must be rejected
        Assertions.assertEquals(1, successfulBookings.get(), "Only 1 thread can book a synchronized Room");
        Assertions.assertEquals(1, rejectedBookings.get(), "The concurrent competing thread must be blocked/rejected");
        Assertions.assertFalse(sharedRoom.isAvailable(), "Room availability must now be false");
    }

    @Test
    public void testFineCalculation() {
        LibraryService service = new LibraryService();
        LocalDate due = LocalDate.now().minusDays(4); // 4 days late
        double fine = service.calculateOverdueFine(due, LocalDate.now());
        Assertions.assertEquals(20.0, fine, 0.001, "4 days @ ₹5/day = ₹20.0 fine");
    }
}

