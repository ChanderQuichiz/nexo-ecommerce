package com.nexo.ecommerce.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "auth.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET must be configured");
        }
        if (expiration == null || expiration.isZero() || expiration.isNegative()) {
            throw new IllegalArgumentException("JWT expiration must be positive");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer is required");
        }
    }
}
