package com.nexo.ecommerce.notifications.application.ports;

import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;

import java.util.UUID;

public interface UserQueryPort {

    UserResponse getUserById(UUID userId);
}
