package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.EmailAlreadyRegisteredException;
import com.nexo.ecommerce.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private AccessTokenProvider accessTokenProvider;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(
                userRepository,
                passwordHasher,
                accessTokenProvider,
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void registersClientAndReturnsAccessToken() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash("secure-pass-123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accessTokenProvider.issue(any(User.class)))
                .thenReturn(new AccessTokenProvider.IssuedAccessToken("signed.jwt", "jti", Instant.MAX));

        var response = useCase.execute("  Ada Lovelace ", " ADA@example.com ", "secure-pass-123");

        assertThat(response.user().email()).isEqualTo("ada@example.com");
        assertThat(response.user().name()).isEqualTo("Ada Lovelace");
        assertThat(response.user().role()).isEqualTo("Client");
        assertThat(response.token()).isEqualTo("signed.jwt");
        verify(passwordHasher).hash("secure-pass-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void rejectsDuplicateEmailBeforeHashingPassword() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("Ada", "ada@example.com", "secure-pass-123"))
                .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectsShortPasswordBeforePersisting() {
        assertThatThrownBy(() -> useCase.execute("Ada", "ada@example.com", "short"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
    }
}
