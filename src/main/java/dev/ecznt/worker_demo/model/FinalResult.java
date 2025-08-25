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
@Document(indexName = "final_query_results")
public class FinalResult {
    @Id
    private String ticketId;

    @Field(type = FieldType.Date)
    private Instant processedAt;

    @Field(type = FieldType.Keyword)
    private String originalQueryParam;

    @Field(type = FieldType.Keyword)
    private String dateRange;

    // This field stores the large, merged JSON result from Oracle.
    // 'enabled = false' prevents Elasticsearch from indexing the content of this field,
    // saving resources, as we only want to store it, not search within it.
    @Field(type = FieldType.Object, enabled = false)
    private Object mergedData;
}
