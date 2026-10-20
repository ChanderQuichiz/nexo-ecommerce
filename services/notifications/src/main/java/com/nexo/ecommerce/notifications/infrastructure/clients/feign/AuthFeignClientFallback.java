package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    @Override
    public UserResponse getUserById(UUID userId) {
        // Fallback cuando Auth Service no está disponible
        return new UserResponse(userId, "Cliente Fallback", "cliente.fallback@example.com");
    }
}
