package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.EmailAlreadyRegisteredException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvisionAdminUserUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;

    private ProvisionAdminUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProvisionAdminUserUseCase(
                userRepository,
                passwordHasher,
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void createsAdminOnlyThroughTrustedBootstrap() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash("a-long-admin-password")).thenReturn("bcrypt-hash");

        boolean created = useCase.execute(
                "Nexo Admin", "admin@example.com", "a-long-admin-password");

        assertThat(created).isTrue();
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
                user -> user.role() == UserRole.ADMIN
                        && user.email().value().equals("admin@example.com")));
    }

    @Test
    void doesNotChangeCredentialsWhenAdminAlreadyExists() {
        User existingAdmin = new User(
                UUID.randomUUID(), new EmailAddress("admin@example.com"), "Admin",
                "existing-hash", UserRole.ADMIN, Instant.now());
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingAdmin));

        boolean created = useCase.execute(
                "Nexo Admin", "admin@example.com", "a-long-admin-password");

        assertThat(created).isFalse();
        verify(userRepository, never()).save(any());
    }

    @Test
    void willNotPromoteAnExistingClientAccount() {
        User existingClient = new User(
                UUID.randomUUID(), new EmailAddress("admin@example.com"), "Client",
                "existing-hash", UserRole.CLIENT, Instant.now());
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingClient));

        assertThatThrownBy(() -> useCase.execute(
                "Nexo Admin", "admin@example.com", "a-long-admin-password"))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void acceptsEightCharacterBootstrapPassword() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash("12345678")).thenReturn("bcrypt-hash");

        boolean created = useCase.execute("Admin", "admin@example.com", "12345678");

        assertThat(created).isTrue();
        verify(userRepository).save(any());
    }

    @Test
    void rejectsBootstrapPasswordShorterThanEightCharacters() {
        assertThatThrownBy(() -> useCase.execute("Admin", "admin@example.com", "1234567"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
