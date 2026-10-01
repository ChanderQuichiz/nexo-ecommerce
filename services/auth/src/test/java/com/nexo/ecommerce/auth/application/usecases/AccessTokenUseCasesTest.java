package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.RevokedTokenStore;
import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
//hola
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenUseCasesTest {
    @Mock
    private AccessTokenProvider accessTokenProvider;
    @Mock
    private RevokedTokenStore revokedTokenStore;

    @Test
    void validatesSignedTokenThatHasNotBeenRevoked() {
        var claims = claims();
        when(accessTokenProvider.verify("signed.jwt")).thenReturn(claims);
        when(revokedTokenStore.isRevoked("token-id")).thenReturn(false);

        assertThat(new ValidateAccessTokenUseCase(accessTokenProvider, revokedTokenStore)
                .execute("signed.jwt")).isEqualTo(claims);
    }

    @Test
    void rejectsRevokedToken() {
        when(accessTokenProvider.verify("signed.jwt")).thenReturn(claims());
        when(revokedTokenStore.isRevoked("token-id")).thenReturn(true);

        assertThatThrownBy(() -> new ValidateAccessTokenUseCase(accessTokenProvider, revokedTokenStore)
                .execute("signed.jwt")).isInstanceOf(InvalidAccessTokenException.class);
    }

    @Test
    void logoutAddsTokenToRevocationStoreUntilItsExpiry() {
        var claims = claims();
        when(accessTokenProvider.verify("signed.jwt")).thenReturn(claims);

        new LogoutUseCase(accessTokenProvider, revokedTokenStore).execute("signed.jwt");

        verify(revokedTokenStore).revoke("token-id", claims.expiresAt());
    }

    private AccessTokenProvider.AccessTokenClaims claims() {
        return new AccessTokenProvider.AccessTokenClaims(
                UUID.randomUUID(),
                "ada@example.com",
                "Ada Lovelace",
                UserRole.CLIENT,
                "token-id",
                Instant.parse("2027-01-01T00:00:00Z"));
    }
}
