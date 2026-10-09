package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.RevokedTokenStore;
import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;

public class LogoutUseCase {
    private final AccessTokenProvider accessTokenProvider;
    private final RevokedTokenStore revokedTokenStore;

    public LogoutUseCase(
            AccessTokenProvider accessTokenProvider,
            RevokedTokenStore revokedTokenStore) {
        this.accessTokenProvider = accessTokenProvider;
        this.revokedTokenStore = revokedTokenStore;
    }

    public void execute(String token) {
        AccessTokenProvider.AccessTokenClaims claims = accessTokenProvider.verify(token);
        revokedTokenStore.revoke(claims.tokenId(), claims.expiresAt());
    }
}
