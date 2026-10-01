package com.nexo.ecommerce.auth.application.ports;

import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;

import java.time.Instant;
import java.util.UUID;

public interface AccessTokenProvider {
    IssuedAccessToken issue(User user);

    AccessTokenClaims verify(String token);

    record IssuedAccessToken(String value, String tokenId, Instant expiresAt) {
    }

    record AccessTokenClaims(
            UUID userId,
            String email,
            String name,
            UserRole role,
            String tokenId,
            Instant expiresAt) {
    }
}
