package com.nexo.ecommerce.auth.application.ports;

public interface PasswordHasher {
    String hash(String plainText);

    boolean matches(String plainText, String passwordHash);
}
