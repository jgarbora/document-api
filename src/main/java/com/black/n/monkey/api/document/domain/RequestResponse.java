package com.black.n.monkey.api.document.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    private UUID id;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode requestHeaders;

    private String requestUri;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode responseHeaders;

    private String httpVerb;

    private Integer responseStatusCode;

    private String requestBody;

    private String responseBody;

    private long executionTimeInMillis;

    private LocalDateTime requestDateAtUtc;

    private String exception;

    private String clientId;
}
