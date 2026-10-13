package com.nexo.ecommerce.gateway;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "eureka.client.enabled=false")
class GatewayRoutingIT {
    private static final HttpServer backend = startBackend();

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void backendRoutes(DynamicPropertyRegistry registry) {
        String uri = "http://127.0.0.1:" + backend.getAddress().getPort();
        registry.add("AUTH_ROUTE_URI", () -> uri);
        registry.add("ORDERS_ROUTE_URI", () -> uri);
        registry.add("CATALOG_ROUTE_URI", () -> uri);
    }

    @AfterAll
    static void stopBackend() {
        backend.stop(0);
    }

    @Test
    void routesAuthOrdersAndWebhookWithoutChangingTheirPaths() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest login = HttpRequest.newBuilder(uri("/api/auth/login"))
                .POST(HttpRequest.BodyPublishers.ofString("login-body")).build();
        assertThat(client.send(login, HttpResponse.BodyHandlers.ofString()).body())
                .isEqualTo("POST|/auth/login|null|login-body");

        HttpRequest orders = HttpRequest.newBuilder(uri("/api/orders/me"))
                .header("Authorization", "Bearer example").GET().build();
        assertThat(client.send(orders, HttpResponse.BodyHandlers.ofString()).body())
                .isEqualTo("GET|/orders/me|Bearer example|");

        HttpRequest catalog = HttpRequest.newBuilder(uri("/api/catalog/42")).GET().build();
        assertThat(client.send(catalog, HttpResponse.BodyHandlers.ofString()).body())
                .isEqualTo("GET|/catalog/42|null|");

        HttpRequest webhook = HttpRequest.newBuilder(uri("/api/webhooks/stripe"))
                .POST(HttpRequest.BodyPublishers.ofString("webhook-body")).build();
        assertThat(client.send(webhook, HttpResponse.BodyHandlers.ofString()).body())
                .isEqualTo("POST|/webhooks/stripe|null|webhook-body");
    }

    @Test
    void respondsToBrowserPreflight() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/auth/login"))
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin"))
                .contains("http://localhost:4200");
    }

    private static HttpServer startBackend() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                String authorization = exchange.getRequestHeaders().getFirst("Authorization");
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String body = exchange.getRequestMethod() + "|" + exchange.getRequestURI().getPath()
                        + "|" + authorization + "|" + requestBody;
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(bytes);
                }
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }
}
