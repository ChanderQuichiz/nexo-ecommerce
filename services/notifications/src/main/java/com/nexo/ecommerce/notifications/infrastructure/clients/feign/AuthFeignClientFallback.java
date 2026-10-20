package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    @Override
    public UserResponse getUserById(UUID userId) {
        // En lugar de datos ficticios/mock corruptos, lanzamos una excepción para que RabbitMQ reintente la notificación cuando Auth Service vuelva a estar disponible
        throw new IllegalStateException("El servicio de Autenticación no está disponible actualmente para obtener datos del usuario con ID: " + userId);
    }
}
