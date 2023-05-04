package com.black.n.monkey.api.document;

import com.black.n.monkey.api.document.dto.CountriesResponse;
import com.black.n.monkey.api.document.dto.Country;
import com.neovisionaries.i18n.CountryCode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CountryService {

    private final Set<Country> countries = new HashSet<>();

    @PostConstruct
    void postConstruct() {
        for (CountryCode code : CountryCode.values()) {
            countries.add(new Country(code.getAlpha2(), code.getName()));
        }
    }

    public CountriesResponse getCountries() {
        return new CountriesResponse(countries);
    }
}

