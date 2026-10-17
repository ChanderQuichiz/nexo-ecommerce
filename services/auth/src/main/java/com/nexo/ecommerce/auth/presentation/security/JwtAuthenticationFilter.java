package com.nexo.ecommerce.auth.presentation.security;

import com.nexo.ecommerce.auth.application.ports.AccessTokenProvider;
import com.nexo.ecommerce.auth.application.usecases.ValidateAccessTokenUseCase;
import com.nexo.ecommerce.auth.domain.exception.InvalidAccessTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final ValidateAccessTokenUseCase validateAccessTokenUseCase;
    private final String internalApiKey;

    public JwtAuthenticationFilter(
            ValidateAccessTokenUseCase validateAccessTokenUseCase,
            String internalApiKey) {
        this.validateAccessTokenUseCase = validateAccessTokenUseCase;
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "/auth/login".equals(path)
                || "/auth/register".equals(path)
                || "/actuator/health".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isValidInternalRequest(request)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }
        if (authorization != null && authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            String token = authorization.substring(BEARER_PREFIX.length()).trim();
            try {
                AccessTokenProvider.AccessTokenClaims claims = validateAccessTokenUseCase.execute(token);
                AuthPrincipal principal = new AuthPrincipal(claims.userId(), claims.email(), claims.role());
                var authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InvalidAccessTokenException exception) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":401,\"message\":\"Invalid access token\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidInternalRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth/users/")
                && internalApiKey != null
                && !internalApiKey.isBlank()
                && internalApiKey.equals(request.getHeader("X-Internal-Api-Key"));
    }
}
