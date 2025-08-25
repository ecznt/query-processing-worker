package dev.ecznt.worker_demo.controller;

import dev.ecznt.worker_demo.model.JobRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final KafkaTemplate<String, JobRequest> kafkaTemplate;

    @Value("${app.kafka.topic.job-requests}")
    private String jobRequestsTopic;

    @PostMapping
    public UUID createJob(@RequestBody JobRequest request) {
        // In a real application, you would validate the request body here.
        UUID ticketId = UUID.randomUUID();
        request.setTicketId(ticketId);

        log.info("Received API request. Assigning ticketId {} and publishing to Kafka topic '{}'", ticketId, jobRequestsTopic);

        // This simulates the API Gateway/Ticket Service from the architecture diagram.
        // It receives a request, assigns a ticket ID, and publishes it to Kafka.
        kafkaTemplate.send(jobRequestsTopic, request);

        // It immediately returns the ticket ID to the client, making the API non-blocking.
        return ticketId;
    }
}
