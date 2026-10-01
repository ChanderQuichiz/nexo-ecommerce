package com.nexo.ecommerce.auth.infrastructure.security;

import com.nexo.ecommerce.auth.application.ports.RevokedTokenStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
public class RedisRevokedTokenAdapter implements RevokedTokenStore {
    private static final String KEY_PREFIX = "auth:revoked:";

    private final StringRedisTemplate redisTemplate;
    private final Clock clock;

    public RedisRevokedTokenAdapter(StringRedisTemplate redisTemplate, Clock clock) {
        this.redisTemplate = redisTemplate;
        this.clock = clock;
    }

    @Override
    public boolean isRevoked(String tokenId) {
        Boolean revoked = redisTemplate.hasKey(KEY_PREFIX + tokenId);
        if (revoked == null) {
            throw new IllegalStateException("Redis returned no result while checking token revocation");
        }
        return revoked;
    }

    @Override
    public void revoke(String tokenId, Instant expiresAt) {
        Duration remaining = Duration.between(clock.instant(), expiresAt);
        if (remaining.isNegative() || remaining.isZero()) {
            return;
        }
        redisTemplate.opsForValue().set(KEY_PREFIX + tokenId, "revoked", remaining);
    }
}
