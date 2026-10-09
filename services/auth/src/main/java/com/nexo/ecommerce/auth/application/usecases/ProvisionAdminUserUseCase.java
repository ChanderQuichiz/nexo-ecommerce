package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.EmailAlreadyRegisteredException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.UUID;

public class ProvisionAdminUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public ProvisionAdminUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public boolean execute(String name, String email, String password) {
        if (name == null || name.isBlank() || name.trim().length() > 120) {
            throw new IllegalArgumentException("Admin name must contain between 1 and 120 characters");
        }
        if (password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Admin password must contain at least 8 characters and at most 72 UTF-8 bytes");
        }
        EmailAddress address = new EmailAddress(email);
        if (userRepository.existsByEmail(address)) {
            User existingUser = userRepository.findByEmail(address)
                    .orElseThrow(() -> new IllegalStateException(
                            "Admin account exists but could not be loaded"));
            if (existingUser.role() == UserRole.ADMIN) {
                return false;
            }
            throw new EmailAlreadyRegisteredException();
        }
        User admin = new User(
                UUID.randomUUID(),
                address,
                name.trim(),
                passwordHasher.hash(password),
                UserRole.ADMIN,
                clock.instant());
        userRepository.save(admin);
        return true;
    }
}
