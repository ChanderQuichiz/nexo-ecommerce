package com.nexo.ecommerce.auth.infrastructure.security;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtAccessTokenAdapter implements AccessTokenProvider {
    private final JwtProperties properties;
    private final Clock clock;
    private final SecretKey signingKey;

    public JwtAccessTokenAdapter(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public IssuedAccessToken issue(User user) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(properties.expiration());
        String tokenId = UUID.randomUUID().toString();
        String value = Jwts.builder()
                .issuer(properties.issuer())
                .subject(user.id().toString())
                .id(tokenId)
                .claim("email", user.email().value())
                .claim("name", user.name())
                .claim("role", user.role().apiValue())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
        return new IssuedAccessToken(value, tokenId, expiresAt);
    }

    @Override
    public AccessTokenClaims verify(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String roleClaim = claims.get("role", String.class);
            String subject = claims.getSubject();
            String tokenId = claims.getId();
            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);
            Date expiration = claims.getExpiration();
            if (roleClaim == null || subject == null || tokenId == null
                    || email == null || name == null || expiration == null) {
                throw new InvalidAccessTokenException();
            }
            UserRole role = switch (roleClaim) {
                case "Admin" -> UserRole.ADMIN;
                case "Client" -> UserRole.CLIENT;
                default -> throw new InvalidAccessTokenException();
            };
            return new AccessTokenClaims(
                    UUID.fromString(subject),
                    email,
                    name,
                    role,
                    tokenId,
                    expiration.toInstant());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidAccessTokenException();
        }
    }
}
