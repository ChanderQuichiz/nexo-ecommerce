package com.nexo.ecommerce.auth.application.ports;

import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsByEmail(EmailAddress email);

    User save(User user);

    Optional<User> findByEmail(EmailAddress email);

    Optional<User> findById(UUID id);
}
