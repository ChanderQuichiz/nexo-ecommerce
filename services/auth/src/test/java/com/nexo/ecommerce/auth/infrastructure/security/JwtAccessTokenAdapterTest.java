package com.nexo.ecommerce.auth.infrastructure.security;

import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAccessTokenAdapterTest {
    private static final String TEST_SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Test
    void issuesAndVerifiesSignedTokenWithExpectedClaims() {
        Instant now = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS);
        JwtAccessTokenAdapter adapter = adapter(Clock.fixed(now, ZoneOffset.UTC));
        User user = new User(UUID.randomUUID(), new EmailAddress("ada@example.com"),
                "Ada", "bcrypt-hash", UserRole.CLIENT, now);

        var issued = adapter.issue(user);
        var claims = adapter.verify(issued.value());

        assertThat(claims.userId()).isEqualTo(user.id());
        assertThat(claims.email()).isEqualTo("ada@example.com");
        assertThat(claims.role()).isEqualTo(UserRole.CLIENT);
        assertThat(claims.tokenId()).isEqualTo(issued.tokenId());
        assertThat(claims.expiresAt()).isEqualTo(now.plusSeconds(900));
    }

    @Test
    void rejectsTamperedToken() {
        JwtAccessTokenAdapter adapter = adapter(Clock.systemUTC());
        User user = new User(UUID.randomUUID(), new EmailAddress("ada@example.com"),
                "Ada", "bcrypt-hash", UserRole.CLIENT, Instant.now());
        String token = adapter.issue(user).value();
        String tamperedToken = "x" + token.substring(1);

        assertThatThrownBy(() -> adapter.verify(tamperedToken))
                .isInstanceOf(InvalidAccessTokenException.class);
    }

    private JwtAccessTokenAdapter adapter(Clock clock) {
        return new JwtAccessTokenAdapter(
                new JwtProperties(TEST_SECRET, java.time.Duration.ofMinutes(15), "nexo-ecommerce"),
                clock);
    }
}
