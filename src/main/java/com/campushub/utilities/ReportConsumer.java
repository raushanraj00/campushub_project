package com.campushub.utilities;

import java.io.IOException;
import java.io.PipedInputStream;
import java.nio.charset.StandardCharsets;

/**
 * UNIT 2: PIPE STREAMS (CONSUMER)
 * ReportConsumer reads streaming report bytes from a PipedInputStream.
 */
public class ReportConsumer implements Runnable {

    private final PipedInputStream pipedIn;
    private final StringBuilder resultBuilder;

    public ReportConsumer(PipedInputStream pipedIn) {
        this.pipedIn = pipedIn;
        this.resultBuilder = new StringBuilder();
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[256];
            int bytesRead;
            while ((bytesRead = pipedIn.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                resultBuilder.append(chunk);
            }
        } catch (IOException e) {
            System.err.println("[ReportConsumer] Error reading pipe: " + e.getMessage());
        } finally {
            try {
                pipedIn.close();
            } catch (IOException ignored) {}
        }
    }

    public String getConsumedReport() {
        return resultBuilder.toString();
    }
}

