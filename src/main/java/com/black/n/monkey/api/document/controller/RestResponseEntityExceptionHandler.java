package com.black.n.monkey.api.document.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Clock;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exceptionHandler(Exception ex, WebRequest request) {

        HttpServletRequest servletWebRequest = ((ServletWebRequest) request).getRequest();
        servletWebRequest.setAttribute("exception", ex);

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity(new ResponseError(ex.getMessage(),
                LocalDateTime.now(Clock.systemUTC()),
                servletWebRequest.getRequestURI()),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

record ResponseError(String message, LocalDateTime date, String path) {
}