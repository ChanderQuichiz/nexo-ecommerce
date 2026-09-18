package com.nexo.ecommerce.auth.infrastructure.rest;

import com.nexo.ecommerce.auth.application.AuthService;
import com.nexo.ecommerce.auth.application.dto.AuthResponse;
import com.nexo.ecommerce.auth.application.dto.LoginRequest;
import com.nexo.ecommerce.auth.application.dto.RegisterRequest;
import com.nexo.ecommerce.auth.application.dto.UserResponse;
import com.nexo.ecommerce.auth.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
class ApiExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<String> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.me(user.getId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.noContent().build();
    }
}
