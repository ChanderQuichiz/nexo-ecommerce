package com.nexo.ecommerce.auth.application;

import com.nexo.ecommerce.auth.application.dto.AuthResponse;
import com.nexo.ecommerce.auth.application.dto.LoginRequest;
import com.nexo.ecommerce.auth.application.dto.RegisterRequest;
import com.nexo.ecommerce.auth.application.dto.UserResponse;
import com.nexo.ecommerce.auth.domain.Role;
import com.nexo.ecommerce.auth.domain.User;
import com.nexo.ecommerce.auth.domain.UserRepository;
import com.nexo.ecommerce.auth.infrastructure.security.JwtTokenService;
import com.nexo.ecommerce.auth.infrastructure.security.TokenRevocationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final TokenRevocationService tokenRevocationService;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager,
                      JwtTokenService jwtTokenService,
                      TokenRevocationService tokenRevocationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Este correo ya está registrado");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.CLIENT
        );

        User savedUser = userRepository.save(user);
        String token = jwtTokenService.generateToken(toResponse(savedUser));
        return new AuthResponse(toResponse(savedUser), token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        String token = jwtTokenService.generateToken(toResponse(user));
        return new AuthResponse(toResponse(user), token);
    }

    public UserResponse me(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toResponse(user);
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token Bearer faltante");
        }
        String token = authorizationHeader.substring(7);
        tokenRevocationService.revokeToken(token);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }
}
