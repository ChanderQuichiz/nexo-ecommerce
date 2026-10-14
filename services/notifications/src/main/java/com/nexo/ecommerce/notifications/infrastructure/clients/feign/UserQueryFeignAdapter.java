package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.UserNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserQueryFeignAdapter implements UserQueryPort {

    private final AuthFeignClient authFeignClient;

    public UserQueryFeignAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        try {
            return authFeignClient.getUserById(userId);
        } catch (FeignException exception) {
            if (exception.status() == 404) {
                throw new UserNotFoundException(userId);
            }
            throw new ExternalServiceUnavailableException("Auth Service", exception);
        }
    }
}
