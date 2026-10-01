package com.nexo.ecommerce.auth.infrastructure.security;

import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public String hash(String plainText) {
        return encoder.encode(plainText);
    }

    @Override
    public boolean matches(String plainText, String passwordHash) {
        return encoder.matches(plainText, passwordHash);
    }
}
