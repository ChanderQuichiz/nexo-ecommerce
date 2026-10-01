package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.RevokedTokenStore;
import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;

public class ValidateAccessTokenUseCase {
    private final AccessTokenProvider accessTokenProvider;
    private final RevokedTokenStore revokedTokenStore;

    public ValidateAccessTokenUseCase(
            AccessTokenProvider accessTokenProvider,
            RevokedTokenStore revokedTokenStore) {
        this.accessTokenProvider = accessTokenProvider;
        this.revokedTokenStore = revokedTokenStore;
    }

    public AccessTokenProvider.AccessTokenClaims execute(String token) {
        AccessTokenProvider.AccessTokenClaims claims = accessTokenProvider.verify(token);
        if (revokedTokenStore.isRevoked(claims.tokenId())) {
            throw new InvalidAccessTokenException();
        }
        return claims;
    }
}
