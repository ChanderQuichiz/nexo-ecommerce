package com.nexo.ecommerce.auth.application.dto;

public record AuthResponse(UserResponse user, String token) {
}
