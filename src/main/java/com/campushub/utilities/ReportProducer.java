package com.campushub.utilities;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * UNIT 2: PIPE STREAMS (PRODUCER)
 * ReportProducer writes campus report stream data into a PipedOutputStream.
 */
public class ReportProducer implements Runnable {

    private final PipedOutputStream pipedOut;
    private final String reportTitle;

    public ReportProducer(PipedOutputStream pipedOut, String reportTitle) {
        this.pipedOut = pipedOut;
        this.reportTitle = reportTitle;
    }

    @Override
    public void run() {
        try {
            String header = "=== CAMPUSHUB COMPILED REPORT: " + reportTitle + " ===\n"
                          + "Generated Timestamp: " + LocalDateTime.now() + "\n"
                          + "----------------------------------------------------\n";
            pipedOut.write(header.getBytes(StandardCharsets.UTF_8));

            String[] sections = {
                "SECTION 1: Academic Enrollment Summary - Total Verified Students active.\n",
                "SECTION 2: Library Circulation Summary - Inventory balanced.\n",
                "SECTION 3: Facility Reservation Metrics - Scheduling active.\n",
                "=== END OF REPORT STREAM ===\n"
            };

            for (String section : sections) {
                pipedOut.write(section.getBytes(StandardCharsets.UTF_8));
                Thread.sleep(100); // simulate stream generation delay
            }

            pipedOut.flush();
        } catch (IOException | InterruptedException e) {
            System.err.println("[ReportProducer] Error writing to pipe: " + e.getMessage());
        } finally {
            try {
                pipedOut.close(); // Signals EOF to connected consumer
            } catch (IOException ignored) {}
        }
    }
}

