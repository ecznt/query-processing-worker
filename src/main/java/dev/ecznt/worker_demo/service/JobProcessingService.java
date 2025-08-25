package dev.ecznt.worker_demo.service;

import dev.ecznt.worker_demo.model.FinalResult;
import dev.ecznt.worker_demo.model.JobRequest;
import dev.ecznt.worker_demo.model.Ticket;
import dev.ecznt.worker_demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobProcessingService {

    private final CacheService cacheService;
    private final MockOracleService oracleService;
    private final TicketRepository ticketRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Value("${app.elasticsearch.index.final-results}")
    private String finalResultsIndex;

    public void processJob(JobRequest request) {
        log.info("Starting processing for ticket: {}", request.getTicketId());
        updateTicketStatus(request.getTicketId().toString(), Ticket.Status.PROCESSING, null);

        try {
            // 1. Check the high-speed cache (Redis) first.
            Optional<Object> cachedResult = cacheService.getQueryResult(request);

            Object oracleResult;
            if (cachedResult.isPresent()) {
                log.info("Cache HIT for ticket: {}", request.getTicketId());
                oracleResult = cachedResult.get();
            } else {
                log.info("Cache MISS for ticket: {}. Querying database.", request.getTicketId());
                // 2. If cache miss, execute the heavy Oracle query.
                oracleResult = oracleService.executeHeavyQuery(request);
                // 3. Store the fresh result in the cache for subsequent requests.
                cacheService.cacheQueryResult(request, oracleResult);
            }

            // 4. Simulate merging records (in a real app, this could be complex logic).
            Object finalMergedData = mergeData(oracleResult);

            // 5. Store the final, searchable document in Elasticsearch.
            storeFinalResult(request, finalMergedData);

            // 6. Mark the ticket as completed.
            updateTicketStatus(request.getTicketId().toString(), Ticket.Status.COMPLETED, null);
            log.info("Successfully completed processing for ticket: {}", request.getTicketId());

        } catch (Exception e) {
            log.error("Error processing ticket {}: {}", request.getTicketId(), e.getMessage(), e);
            updateTicketStatus(request.getTicketId().toString(), Ticket.Status.ERROR, e.getMessage());
        }
    }

    private void updateTicketStatus(String ticketId, Ticket.Status status, String errorMessage) {
        // Find the existing ticket or create a new one if it's the first update.
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(Ticket.builder().id(ticketId).createdAt(Instant.now()).build());

        ticket.setStatus(status);
        ticket.setUpdatedAt(Instant.now());
        ticket.setErrorMessage(errorMessage);

        ticketRepository.save(ticket);
    }

    private Object mergeData(Object rawData) {
        log.info("Merging data...");
        // This is a placeholder for your business logic to merge/transform the data.
        return Map.of("final_data", rawData, "merge_timestamp", Instant.now());
    }

    private void storeFinalResult(JobRequest request, Object mergedData) {
        FinalResult finalResult = FinalResult.builder()
                .ticketId(request.getTicketId().toString())
                .processedAt(Instant.now())
                .originalQueryParam(request.getQueryParam())
                .dateRange(request.getDt1() + " to " + request.getDt2())
                .mergedData(mergedData)
                .build();

        // Use ElasticsearchOperations to save to the correct index
        elasticsearchOperations.save(finalResult);
        log.info("Stored final merged result in Elasticsearch for ticket {}", request.getTicketId());
    }
}
