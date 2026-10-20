package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    private static final Logger log = LoggerFactory.getLogger(AuthFeignClientFallback.class);

    @Override
    public UserResponse getUserById(UUID userId) {
        log.error("Circuit Breaker Abierto: Auth Service no esta disponible para obtener usuario {}", userId);
        throw new IllegalStateException("El servicio de Autenticacion no esta disponible actualmente para obtener datos del usuario con ID: " + userId);
    }
}
