package com.black.n.monkey.api.document.controller;

import com.black.n.monkey.api.document.CountryService;
import com.black.n.monkey.api.document.dto.CountriesResponse;
import com.black.n.monkey.api.document.util.UruguayanCiTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

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
    public ResponseEntity<CountriesResponse> countries() {
        return ResponseEntity.ok(countryService.getCountries());
    }

    @GetMapping("/country/{country}/document-types")
    public ResponseEntity<CountryTypesResponse> documentTypes(@PathVariable String country) {

        if (CountryCode.UY.getAlpha2().equals(country.toUpperCase())) {
            return ResponseEntity.ok(new CountryTypesResponse(Set.of("IDE")));
        }

        return new ResponseEntity("no documents for this country yet", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/country/{country}/validate-check-digit")
    public ResponseEntity<ValidateCheckDigitResponse> validateCheckDigit(@PathVariable String country,
                                                                         @RequestParam("document-type") String documentType,
                                                                         @RequestParam("document-number") String documentNumber,
                                                                         @RequestParam("check-digit") String checkDigit) {

        if (isAnUruguayanCI(country, documentType)) {
            if (UruguayanCiTool.isValidWithoutException(documentNumber + checkDigit)) {
                return ResponseEntity.ok(new ValidateCheckDigitResponse("valid CI", true));
            } else {
                return ResponseEntity.ok(new ValidateCheckDigitResponse("not valid CI", false));
            }
        }

        return new ResponseEntity("no country / document validations for this yet", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/country/{country}/find-check-digit")
    public ResponseEntity<FindCheckDigitResponse> findCheckDigit(@PathVariable String country,
                                                                 @RequestParam("document-type") String documentType,
                                                                 @RequestParam("document-number") String documentNumber) {

        if (isAnUruguayanCI(country, documentType)) {
            return ResponseEntity.ok(new FindCheckDigitResponse(UruguayanCiTool.findCheckDigit(documentNumber)));
        }

        return new ResponseEntity("no country / document validations for this yet", HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/whatever", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity whatever(@RequestBody JsonNode request) {
        return ResponseEntity.ok(request);
    }

    @PostMapping(value = "/throw-an-exception", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity throwAnException(@RequestBody JsonNode request) {
        throw new RuntimeException("for dev purpose");
    }

    private boolean isAnUruguayanCI(String countryCodeAlpha2, String documentType) {
        return CountryCode.UY.getAlpha2().equals(countryCodeAlpha2.toUpperCase()) && "IDE".equals(documentType.toUpperCase());
    }
}

record CountryTypesResponse(Set<String> documentTypes) {
}

record ValidateCheckDigitResponse(String message, Boolean isValid) {
}

record FindCheckDigitResponse(Integer checkDigit) {
}