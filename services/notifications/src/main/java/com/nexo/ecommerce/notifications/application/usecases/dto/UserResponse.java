package com.nexo.ecommerce.notifications.application.usecases.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email) {
}
