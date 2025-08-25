package dev.ecznt.worker_demo.service;


import dev.ecznt.worker_demo.model.JobRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class MockOracleService {

    /**
     * This method simulates a long-running, I/O-bound database query.
     * In a real application, this would use JDBC or JPA to call the Oracle database.
     * The use of virtual threads in Java 21 means that while this thread is sleeping (or waiting
     * for the database), the underlying OS thread is released to do other work,
     * dramatically increasing throughput.
     */
    public Object executeHeavyQuery(JobRequest request) {
        log.info("Executing heavy Oracle query for ticket {}...", request.getTicketId());
        try {
            // Simulate a 5-second database query
            Thread.sleep(Duration.ofSeconds(5));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Oracle query was interrupted", e);
        }
        log.info("Oracle query finished for ticket {}.", request.getTicketId());
        // Return a large, complex object, simulating a big result set.
        return Map.of(
                "query_details", request,
                "result_summary", "This is a large result set with millions of rows.",
                "data_payload", "{\"field1\": \"value1\", \"nested\": {\"key\": 123}}".repeat(1000)
        );
    }
}
