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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.OPTIONS;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestResponseLogger extends OncePerRequestFilter {

    private final ObjectMapper mapper;
    private final Set<String> headers2sanitize = Set.of("authorization");
    private final Set<String> ignoreSavingResponseBodies = Set.of("/api/ver/v1/countries", "/api/ver/v1/country/UY/document-types");
    private final Set<String> ignoreSavingMethods = Set.of(OPTIONS.name(), HEAD.name());

    private final RequestResponseRepository requestResponseRepository;
    private int maxPayloadLength = 10000;

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

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        if (!ignoreSavingMethods.contains(request.getMethod())) {

            if (!ignoreSavingResponseBodies.contains(request.getRequestURI())) {
                String requestBody = getMessagePayload(wrappedRequest);
                if (requestBody != null && requestBody.length() > 0) {
                    requestResponse.setRequestBody(mapper.valueToTree(requestBody));
                }
            }

            requestResponse.setResponseHeaders(buildHeaders(wrappedResponse));
            requestResponse.setResponseStatusCode(response.getStatus());
            requestResponse.setExecutionTimeInMillis(System.currentTimeMillis() - startTime);

            String responseBodyAsString = getResponsePayload(wrappedResponse); // save response body
            requestResponse.setResponseBody(mapper.valueToTree(responseBodyAsString));

            requestResponse.setException(mapException(request));

            requestResponseRepository.save(requestResponse);
        }

        wrappedResponse.copyBodyToResponse();  // IMPORTANT: copy content of response back into original response
    }

    // https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl

    private JsonNode mapException(HttpServletRequest request) {
        if (request.getAttribute("exception") != null) {
            Exception ex = (Exception) request.getAttribute("exception");
            Map<String, String> exceptionWrapper = new HashMap<>();
            exceptionWrapper.put("stackTrace", ExceptionUtils.getStackTrace(ex));
            return mapper.valueToTree(exceptionWrapper);
        }
        return null;
    }

    /**
     * COPY FROM CommonsRequestLoggingFilter > AbstractRequestLoggingFilter
     */
    protected String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, maxPayloadLength);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }

    /**
     * COPY FROM CommonsRequestLoggingFilter > AbstractRequestLoggingFilter
     */
    protected String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, maxPayloadLength);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }

    private JsonNode buildHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Iterator<String> it = request.getHeaderNames().asIterator();
        while (it.hasNext()) {
            String header = it.next();
            headers.put(header, headers2sanitize.contains(header) ? List.of("***") : List.of(request.getHeader(header)));
        }
        return mapper.valueToTree(headers); // https://www.baeldung.com/jackson-json-node-tree-model
    }

    private JsonNode buildHeaders(HttpServletResponse response) {
        Map<String, List<String>> headers = new HashMap<>();
        Iterator<String> it = response.getHeaderNames().iterator();
        while (it.hasNext()) {
            String header = it.next();
            headers.put(header, headers2sanitize.contains(header) ? List.of("***") : List.of(response.getHeader(header)));
        }
        // FIXME returns always empty!!
        return mapper.valueToTree(headers);
    }


}
