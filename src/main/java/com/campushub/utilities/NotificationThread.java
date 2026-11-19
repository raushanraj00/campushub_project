package com.campushub.utilities;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UNIT 2: MULTITHREADING (Implementing Runnable)
 * NotificationThread demonstrates thread creation via the Runnable interface.
 * Operates as a daemon worker generating periodic campus operational alerts.
 */
public class NotificationThread implements Runnable {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final int intervalMillis;
    private final NotificationListener listener;

    public interface NotificationListener {
        void onNotification(String alert);
    }

    public NotificationThread(int intervalMillis, NotificationListener listener) {
        this.intervalMillis = Math.max(10, intervalMillis);
        this.listener = listener;
    }

    @Override
    public void run() {
        System.out.println("[NotificationThread] Runnable worker started in thread: " 
                + Thread.currentThread().getName());

        int count = 1;
        while (running.get()) {
            try {
                Thread.sleep(intervalMillis); // Enters TIMED_WAITING

                String message = "System Notification #" + count++ + ": Background health check OK. Active session active.";
                if (listener != null) {
                    listener.onNotification(message);
                }
            } catch (InterruptedException e) {
                System.out.println("[NotificationThread] Interrupted. Stopping cleanly.");
                running.set(false);
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[NotificationThread] Worker completed execution.");
    }

    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
