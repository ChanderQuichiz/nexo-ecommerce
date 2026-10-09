package com.nexo.ecommerce.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        EmailAddress email,
        String name,
        String passwordHash,
        UserRole role,
        Instant createdAt) {

    public User {
        if (id == null || email == null || role == null || createdAt == null) {
            throw new IllegalArgumentException("User identity, email, role and creation time are required");
        }
        if (name == null || name.isBlank() || name.trim().length() > 120) {
            throw new IllegalArgumentException("Name must contain between 1 and 120 characters");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
        name = name.trim();
    }

    public static User register(
            UUID id, EmailAddress email, String name, String passwordHash, Instant createdAt) {
        return new User(id, email, name, passwordHash, UserRole.CLIENT, createdAt);
    }
}
