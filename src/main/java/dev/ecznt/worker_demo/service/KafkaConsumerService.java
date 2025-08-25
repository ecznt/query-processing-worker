package dev.ecznt.worker_demo.service;

import dev.ecznt.worker_demo.model.JobRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JobProcessingService jobProcessingService;

    /**
     * This method listens to the 'job_requests' topic.
     * Spring Kafka and Spring Boot automatically handle the complexities of message
     * deserialization, connection management, and error handling.
     * Each message received will be processed by calling the JobProcessingService.
     */
    @KafkaListener(topics = "${app.kafka.topic.job-requests}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenToJobRequests(JobRequest jobRequest) {
        log.info("Received new job request from Kafka: {}", jobRequest.getTicketId());

        // The entire processing logic is delegated to the orchestrator service.
        // This keeps the consumer clean and focused on one task: receiving messages.
        jobProcessingService.processJob(jobRequest);
    }
}
