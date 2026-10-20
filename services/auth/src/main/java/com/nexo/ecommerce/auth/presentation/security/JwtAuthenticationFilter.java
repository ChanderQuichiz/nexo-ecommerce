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
        String uri = request.getRequestURI();

        // Evalúa tanto servletPath como requestURI para cubrir stripped paths de API Gateway
        return path.endsWith("/login") || path.endsWith("/register")
                || uri.endsWith("/login") || uri.endsWith("/register")
                || path.contains("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Si es una petición interna válida con API Key
        if (isValidInternalRequest(request)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            String token = authorization.substring(BEARER_PREFIX.length()).trim();
            if (!token.isEmpty()) {
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
                } catch (Exception exception) {
                    // Previene que excepciones inesperadas generen un 500 no controlado
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":401,\"message\":\"Authentication failed\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidInternalRequest(HttpServletRequest request) {
        String path = request.getServletPath();
        return (path.startsWith("/auth/users/") || path.startsWith("/users/"))
                && internalApiKey != null
                && !internalApiKey.isBlank()
                && internalApiKey.equals(request.getHeader("X-Internal-Api-Key"));
    }
}