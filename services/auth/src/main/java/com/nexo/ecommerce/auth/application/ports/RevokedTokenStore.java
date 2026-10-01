package com.nexo.ecommerce.auth.application.ports;

import java.time.Instant;

public interface RevokedTokenStore {
    boolean isRevoked(String tokenId);

    void revoke(String tokenId, Instant expiresAt);
}
