package com.nexo.ecommerce.auth.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("auth.jwt.secret",
                () -> "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        registry.add("auth.admin.name", () -> "Bootstrap Admin");
        registry.add("auth.admin.email", () -> "bootstrap-admin@example.com");
        registry.add("auth.admin.password", () -> "Bootstrap-Admin-Password-2026!");
    }

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void registerLoginGetMeAndLogoutRevokesAccessToken() {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        ResponseEntity<JsonNode> registration = post(
                "/auth/register",
                Map.of("name", "Nexo User", "email", email, "password", "secure-pass-123"),
                null,
                JsonNode.class);

        assertThat(registration.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registration.getBody().path("user").path("email").asText()).isEqualTo(email);
        assertThat(registration.getBody().path("user").path("role").asText()).isEqualTo("Client");
        String registeredToken = registration.getBody().path("token").asText();
        assertThat(registeredToken).isNotBlank();

        ResponseEntity<JsonNode> currentUser = get("/auth/me", registeredToken, JsonNode.class);
        assertThat(currentUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentUser.getBody().path("name").asText()).isEqualTo("Nexo User");

        ResponseEntity<Void> logout = post(
                "/auth/logout", Map.of(), registeredToken, Void.class);
        assertThat(logout.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> afterLogout = get("/auth/me", registeredToken, String.class);
        assertThat(afterLogout.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<JsonNode> login = post(
                "/auth/login",
                Map.of("email", email, "password", "secure-pass-123"),
                null,
                JsonNode.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody().path("token").asText()).isNotEqualTo(registeredToken);
    }

    @Test
    void rejectsDuplicateRegistrationAndInvalidCredentials() {
        String email = "duplicate-" + UUID.randomUUID() + "@example.com";
        Map<String, String> request = Map.of(
                "name", "Nexo User", "email", email, "password", "secure-pass-123");

        assertThat(post("/auth/register", request, null, String.class).getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(post("/auth/register", request, null, String.class).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<String> invalidLogin = post(
                "/auth/login",
                Map.of("email", email, "password", "wrong-pass-123"),
                null,
                String.class);
        assertThat(invalidLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedEndpointRequiresBearerToken() {
        assertThat(get("/auth/me", null, String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void bootstrapsAdminWithoutAllowingPublicRoleSelection() {
        ResponseEntity<JsonNode> adminLogin = post(
                "/auth/login",
                Map.of(
                        "email", "bootstrap-admin@example.com",
                        "password", "Bootstrap-Admin-Password-2026!"),
                null,
                JsonNode.class);

        assertThat(adminLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(adminLogin.getBody().path("user").path("role").asText()).isEqualTo("Admin");

        ResponseEntity<JsonNode> publicRegistration = post(
                "/auth/register",
                Map.of(
                        "name", "Public User",
                        "email", "public-" + UUID.randomUUID() + "@example.com",
                        "password", "secure-pass-123",
                        "role", "Admin"),
                null,
                JsonNode.class);

        assertThat(publicRegistration.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(publicRegistration.getBody().path("user").path("role").asText()).isEqualTo("Client");
    }

    private <T> ResponseEntity<T> post(String path, Object body, String token, Class<T> responseType) {
        WebClient.RequestBodySpec request = webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            request.headers(headers -> headers.setBearerAuth(token));
        }
        return Objects.requireNonNull(request.bodyValue(body)
                .exchangeToMono(response -> response.toEntity(responseType))
                .block());
    }

    private <T> ResponseEntity<T> get(String path, String token, Class<T> responseType) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(path);
        if (token != null) {
            request.headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        }
        return Objects.requireNonNull(request.exchangeToMono(response -> response.toEntity(responseType)).block());
    }
}
