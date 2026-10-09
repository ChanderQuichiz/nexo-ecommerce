package com.nexo.ecommerce.auth.application.usecases.dto;

import com.nexo.ecommerce.auth.domain.model.User;

import java.util.UUID;

public record UserView(UUID id, String email, String name, String role) {
    public static UserView from(User user) {
        return new UserView(
                user.id(),
                user.email().value(),
                user.name(),
                user.role().apiValue());
    }
}
