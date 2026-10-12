package com.nexo.ecommerce.auth.application.usecases.dto;

import com.nexo.ecommerce.auth.domain.model.User;

import java.util.UUID;

public record UserContactView(UUID id, String name, String email) {
    public static UserContactView from(User user) {
        return new UserContactView(user.id(), user.name(), user.email().value());
    }
}
