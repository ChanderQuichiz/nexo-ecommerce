package com.nexo.ecommerce.auth.application.usecases.dto;

public record AuthResponse(UserView user, String token) {
}
