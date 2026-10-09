package com.nexo.ecommerce.auth.presentation.security;

import com.nexo.ecommerce.auth.domain.model.UserRole;

import java.util.UUID;

public record AuthPrincipal(UUID userId, String email, UserRole role) {
}
