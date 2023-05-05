package com.black.n.monkey.api.document.controller.util;

import com.black.n.monkey.api.document.domain.RequestResponse;
import com.black.n.monkey.api.document.repository.RequestResponseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestResponseLogger extends OncePerRequestFilter {

    private final ObjectMapper mapper;
    private final Set<String> headers2sanitize = Set.of("authorization");

    private final RequestResponseRepository requestResponseRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        RequestResponse requestResponse = RequestResponse.builder()
                .requestHeaders(buildHeaders(request))
                .requestUri(request.getRequestURI())
                .requestDateAtUtc(LocalDateTime.now(Clock.systemUTC()))
                .httpVerb(request.getMethod())
                .clientId(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous")
                .build();

        filterChain.doFilter(request, response);

        requestResponse.setResponseHeaders(buildHeaders(response));
        requestResponse.setResponseStatusCode(response.getStatus());
        requestResponse.setExecutionTimeInMillis(System.currentTimeMillis() - startTime);

        // TODO
        // map exception
        // map request body
        // map response body
        requestResponseRepository.save(requestResponse);
    }

    // https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl


    private JsonNode buildHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Iterator<String> it = request.getHeaderNames().asIterator();
        while (it.hasNext()) {
            String header = it.next();
            headers.put(header, headers2sanitize.contains(header) ? List.of("***") : List.of(request.getHeader(header)));
        }
        return mapper.valueToTree(headers);
    }

    private JsonNode buildHeaders(HttpServletResponse response) {
        Map<String, List<String>> headers = new HashMap<>();
        Iterator<String> it = response.getHeaderNames().iterator();
        while (it.hasNext()) {
            String header = it.next();

            headers.put(header, headers2sanitize.contains(header) ? List.of("***") : List.of(response.getHeader(header)));
        }
        return mapper.valueToTree(headers);
    }


}
