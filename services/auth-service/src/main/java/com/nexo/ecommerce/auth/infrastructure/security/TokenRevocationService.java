package com.nexo.ecommerce.auth.infrastructure.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    public void revokeToken(String token) {
        if (token != null && !token.isBlank()) {
            revokedTokens.add(token);
        }
    }

    public boolean isRevoked(String token) {
        return token != null && revokedTokens.contains(token);
    }
}
