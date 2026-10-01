package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.InvalidCredentialsException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private AccessTokenProvider accessTokenProvider;

    private LoginUseCase useCase;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(userRepository, passwordHasher, accessTokenProvider);
        user = new User(UUID.randomUUID(), new EmailAddress("ada@example.com"),
                "Ada Lovelace", "bcrypt-hash", UserRole.CLIENT, Instant.now());
    }

    @Test
    void authenticatesUserAndIssuesToken() {
        when(userRepository.findByEmail(new EmailAddress("ada@example.com"))).thenReturn(Optional.of(user));
        when(passwordHasher.matches("secure-pass-123", "bcrypt-hash")).thenReturn(true);
        when(accessTokenProvider.issue(user))
                .thenReturn(new AccessTokenProvider.IssuedAccessToken("signed.jwt", "jti", Instant.MAX));

        var response = useCase.execute("ADA@example.com", "secure-pass-123");

        assertThat(response.user().id()).isEqualTo(user.id());
        assertThat(response.token()).isEqualTo("signed.jwt");
    }

    @Test
    void rejectsUnknownEmailWithGenericCredentialError() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("unknown@example.com", "secure-pass-123"))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(passwordHasher, never()).matches(any(), any());
    }

    @Test
    void rejectsIncorrectPassword() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong-pass-123", "bcrypt-hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute("ada@example.com", "wrong-pass-123"))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(accessTokenProvider, never()).issue(any());
    }
}
