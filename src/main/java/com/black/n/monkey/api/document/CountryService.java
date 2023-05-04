package com.black.n.monkey.api.document;

import com.neovisionaries.i18n.CountryCode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CountryService {

    private final Set<CountryResponse> countries = new HashSet<>();

    @PostConstruct
    void postConstruct() {
        for (CountryCode code : CountryCode.values()) {
            countries.add(new CountryResponse(code.getAlpha2(), code.getName()));
        }
    }

    public Set<CountryResponse> getCountries() {
        return countries;
    }
}

record CountryResponse(String alpha2, String name) {
}
