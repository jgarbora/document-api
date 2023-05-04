package com.black.n.monkey.api.document.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;


public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    private String audience;

    @Override
    public OAuth2TokenValidatorResult validate(final Jwt jwt) {

        OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing!", null);

        if (jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(error);

    }

}
