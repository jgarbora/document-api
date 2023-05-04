package com.black.n.monkey.api.document.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Builder
public class RequestResponse {

    private Map<String, List<String>> requestHeaders;

    private String requestURI;

    private Map<String, List<String>> responseHeaders;

    private String httpVerb;

    private Integer responseStatusCode;

    private String requestBody;

    private String responseBody;

    private long executionTimeInMillis;

    private LocalDateTime requestDateAtUtc;

    private String exception;

    private String clientId;
}
