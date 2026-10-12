package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.application.usecases.dto.UserContactView;
import com.nexo.ecommerce.auth.domain.exception.UserNotFoundException;

import java.util.UUID;

public class GetUserByIdUseCase {
    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserContactView execute(UUID userId) {
        return userRepository.findById(userId)
                .map(UserContactView::from)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
