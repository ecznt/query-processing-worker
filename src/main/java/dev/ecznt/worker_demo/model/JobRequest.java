package dev.ecznt.worker_demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class JobRequest {
    private UUID ticketId;
    private String dt1;
    private String dt2;
    private String queryParam; // The string parameter for the heavy query
}
