package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.UserNotFoundException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Test
    void returnsOnlyPublicUserProperties() {
        UUID id = UUID.randomUUID();
        User user = new User(id, new EmailAddress("ada@example.com"),
                "Ada", "must-not-be-returned", UserRole.ADMIN, Instant.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var result = new GetCurrentUserUseCase(userRepository).execute(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo("ada@example.com");
        assertThat(result.role()).isEqualTo("Admin");
    }

    @Test
    void reportsMissingUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new GetCurrentUserUseCase(userRepository).execute(id))
                .isInstanceOf(UserNotFoundException.class);
    }
}
