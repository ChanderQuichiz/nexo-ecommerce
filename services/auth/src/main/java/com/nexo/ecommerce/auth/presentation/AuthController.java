package com.nexo.ecommerce.auth.presentation;

import com.nexo.ecommerce.auth.application.usecases.GetCurrentUserUseCase;
import com.nexo.ecommerce.auth.application.usecases.LoginUseCase;
import com.nexo.ecommerce.auth.application.usecases.LogoutUseCase;
import com.nexo.ecommerce.auth.application.usecases.RegisterUserUseCase;
import com.nexo.ecommerce.auth.application.usecases.dto.AuthResponse;
import com.nexo.ecommerce.auth.application.usecases.dto.UserView;
import com.nexo.ecommerce.auth.presentation.dto.LoginRequest;
import com.nexo.ecommerce.auth.presentation.dto.RegisterRequest;
import com.nexo.ecommerce.auth.presentation.security.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase,
            LogoutUseCase logoutUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUserUseCase.execute(
                request.name(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.execute(request.email(), request.password());
    }

    @GetMapping("/me")
    public UserView me(@AuthenticationPrincipal AuthPrincipal principal) {
        return getCurrentUserUseCase.execute(principal.userId());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.substring("Bearer ".length()).trim();
        logoutUseCase.execute(token);
        return ResponseEntity.noContent().build();
    }
}
