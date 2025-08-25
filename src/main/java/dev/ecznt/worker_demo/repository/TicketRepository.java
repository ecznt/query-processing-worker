package dev.ecznt.worker_demo.repository;

import dev.ecznt.worker_demo.model.Ticket;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends ElasticsearchRepository<Ticket, String> {
}
