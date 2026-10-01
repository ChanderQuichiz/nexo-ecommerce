package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.application.usecases.dto.AuthResponse;
import com.nexo.ecommerce.auth.application.usecases.dto.UserView;
import com.nexo.ecommerce.auth.domain.exception.EmailAlreadyRegisteredException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.UUID;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenProvider accessTokenProvider;
    private final Clock clock;

    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.accessTokenProvider = accessTokenProvider;
        this.clock = clock;
    }

    public AuthResponse execute(String name, String email, String password) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty() || normalizedName.length() > 120) {
            throw new IllegalArgumentException("Name must contain between 1 and 120 characters");
        }
        if (password == null
                || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must contain 8 to 72 UTF-8 bytes");
        }

        EmailAddress emailAddress = new EmailAddress(email);
        if (userRepository.existsByEmail(emailAddress)) {
            throw new EmailAlreadyRegisteredException();
        }

        User newUser = User.register(
                UUID.randomUUID(),
                emailAddress,
                normalizedName,
                passwordHasher.hash(password),
                clock.instant());
        User savedUser = userRepository.save(newUser);
        AccessTokenProvider.IssuedAccessToken issuedToken = accessTokenProvider.issue(savedUser);
        return new AuthResponse(UserView.from(savedUser), issuedToken.value());
    }
}
