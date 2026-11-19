package com.campushub.utilities;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * UNIT 2: PIPE STREAMS ORCHESTRATOR
 * Wires PipedOutputStream and PipedInputStream to transfer data between threads.
 */
public class ReportManager {

    public static String generatePipedReport(String title) {
        try {
            PipedOutputStream pipedOut = new PipedOutputStream();
            PipedInputStream pipedIn = new PipedInputStream(pipedOut); // Connect streams

            ReportProducer producer = new ReportProducer(pipedOut, title);
            ReportConsumer consumer = new ReportConsumer(pipedIn);

            Thread producerThread = new Thread(producer, "PipeProducerThread");
            Thread consumerThread = new Thread(consumer, "PipeConsumerThread");

            producerThread.start();
            consumerThread.start();

            // Wait for both threads to finish transferring stream bytes
            producerThread.join();
            consumerThread.join();

            return consumer.getConsumedReport();
        } catch (IOException | InterruptedException e) {
            return "Failed to generate report via Pipe Streams: " + e.getMessage();
        }
    }
}

