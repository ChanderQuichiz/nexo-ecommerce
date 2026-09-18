package com.nexo.ecommerce.auth.unit;

import com.nexo.ecommerce.auth.application.dto.UserResponse;
import com.nexo.ecommerce.auth.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        JwtTokenService jwtTokenService = new JwtTokenService("test-secret-key-with-enough-length-for-jwt-123", "nexo-ecommerce");

        UserResponse user = new UserResponse("u-1", "admin@demo.com", "Admin User", "Admin");

        String token = jwtTokenService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenService.validateToken(token)).isTrue();
        assertThat(jwtTokenService.extractUserId(token)).isEqualTo("u-1");
        assertThat(jwtTokenService.extractEmail(token)).isEqualTo("admin@demo.com");
    }
}
