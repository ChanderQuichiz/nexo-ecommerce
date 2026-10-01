package com.nexo.ecommerce.auth.application.usecases;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.ports.PasswordHasher;
import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.application.usecases.dto.AuthResponse;
import com.nexo.ecommerce.auth.application.usecases.dto.UserView;
import com.nexo.ecommerce.auth.domain.exception.InvalidCredentialsException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;

public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenProvider accessTokenProvider;

    public LoginUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.accessTokenProvider = accessTokenProvider;
    }

    public AuthResponse execute(String email, String password) {
        final EmailAddress emailAddress;
        try {
            emailAddress = new EmailAddress(email);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(emailAddress)
                .filter(found -> password != null && passwordHasher.matches(password, found.passwordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        AccessTokenProvider.IssuedAccessToken issuedToken = accessTokenProvider.issue(user);
        return new AuthResponse(UserView.from(user), issuedToken.value());
    }
}
