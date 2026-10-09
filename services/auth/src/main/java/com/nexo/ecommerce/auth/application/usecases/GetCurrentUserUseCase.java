package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.application.usecases.dto.UserView;
import com.nexo.ecommerce.auth.domain.exception.UserNotFoundException;

import java.util.UUID;

public class GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserView execute(UUID userId) {
        return userRepository.findById(userId)
                .map(UserView::from)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
