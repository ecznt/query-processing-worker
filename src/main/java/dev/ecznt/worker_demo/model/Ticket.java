package dev.ecznt.worker_demo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Builder
@Document(indexName = "tickets_status") // Maps this class to an Elasticsearch index
public class Ticket {
    @Id
    private String id; // Ticket ID will be the document ID

    @Field(type = FieldType.Keyword)
    private Status status;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant updatedAt;

    @Field(type = FieldType.Text)
    private String errorMessage;

    public enum Status {
        SUBMITTED, PROCESSING, COMPLETED, ERROR
    }
}