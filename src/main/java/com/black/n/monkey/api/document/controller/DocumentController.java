package com.black.n.monkey.api.document.controller;

import com.black.n.monkey.api.document.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/ver/v1")
@RequiredArgsConstructor
public class DocumentController {

    private final CountryService countryService;

    @GetMapping("/date")
    public String time() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @GetMapping("/countries")
    public ResponseEntity countries() {
        return ResponseEntity.ok(countryService.getCountries());
    }

}
