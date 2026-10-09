package com.nexo.ecommerce.auth.infrastructure.config;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.RevokedTokenStore;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.application.usecases.GetCurrentUserUseCase;
import com.nexo.ecommerce.auth.application.usecases.LoginUseCase;
import com.nexo.ecommerce.auth.application.usecases.LogoutUseCase;
import com.nexo.ecommerce.auth.application.usecases.ProvisionAdminUserUseCase;
import com.nexo.ecommerce.auth.application.usecases.RegisterUserUseCase;
import com.nexo.ecommerce.auth.application.usecases.ValidateAccessTokenUseCase;
import com.nexo.ecommerce.auth.infrastructure.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AdminBootstrapProperties.class})
public class UseCasesConfig {
    @Bean
    Clock systemClock() {
        return Clock.systemUTC();
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            Clock clock) {
        return new RegisterUserUseCase(userRepository, passwordHasher, accessTokenProvider, clock);
    }

    @Bean
    ProvisionAdminUserUseCase provisionAdminUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            Clock clock) {
        return new ProvisionAdminUserUseCase(userRepository, passwordHasher, clock);
    }

    @Bean
    LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider) {
        return new LoginUseCase(userRepository, passwordHasher, accessTokenProvider);
    }

    @Bean
    GetCurrentUserUseCase getCurrentUserUseCase(UserRepository userRepository) {
        return new GetCurrentUserUseCase(userRepository);
    }

    @Bean
    ValidateAccessTokenUseCase validateAccessTokenUseCase(
            AccessTokenProvider accessTokenProvider,
            RevokedTokenStore revokedTokenStore) {
        return new ValidateAccessTokenUseCase(accessTokenProvider, revokedTokenStore);
    }

    @Bean
    LogoutUseCase logoutUseCase(
            AccessTokenProvider accessTokenProvider,
            RevokedTokenStore revokedTokenStore) {
        return new LogoutUseCase(accessTokenProvider, revokedTokenStore);
    }
}
