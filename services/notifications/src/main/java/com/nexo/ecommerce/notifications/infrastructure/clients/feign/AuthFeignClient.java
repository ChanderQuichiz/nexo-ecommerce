package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "auth-service",
        configuration = AuthFeignConfig.class,
        fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {

    @GetMapping("/auth/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") UUID userId);
}
