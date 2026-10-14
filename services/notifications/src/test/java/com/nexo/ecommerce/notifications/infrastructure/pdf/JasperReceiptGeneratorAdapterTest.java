package com.nexo.ecommerce.notifications.infrastructure.pdf;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderItemResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JasperReceiptGeneratorAdapterTest {

    private final JasperReceiptGeneratorAdapter adapter =
            new JasperReceiptGeneratorAdapter(new ReceiptProperties("PEN"));

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void shouldGeneratePdfInMemoryWithOneOrSeveralItems(int itemCount) throws Exception {
        long pdfFilesBefore = pdfFileCount();
        OrderResponse order = order(items(itemCount));
        UserResponse user = user(order.userId());

        byte[] result = adapter.generateReceipt(order, user);

        assertPdf(result);
        assertEquals(pdfFilesBefore, pdfFileCount());
    }

    @Test
    void shouldGenerateValidPdfWithEmptyItemList() {
        OrderResponse order = order(List.of());

        byte[] result = adapter.generateReceipt(order, user(order.userId()));

        assertPdf(result);
    }

    @Test
    void shouldTranslateInvalidTemplateFailure() {
        ByteArrayInputStream invalidTemplate = new ByteArrayInputStream(
                "not-valid-jrxml".getBytes(StandardCharsets.UTF_8));

        assertThrows(
                ReceiptGenerationException.class,
                () -> new JasperReceiptGeneratorAdapter(
                        new ReceiptProperties("PEN"),
                        invalidTemplate));
    }

    private void assertPdf(byte[] result) {
        assertNotNull(result);
        assertFalse(result.length == 0);
        assertArrayEquals(
                "%PDF".getBytes(StandardCharsets.US_ASCII),
                java.util.Arrays.copyOf(result, 4));
    }

    private List<OrderItemResponse> items(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(index -> new OrderItemResponse(
                        "product-" + index,
                        index,
                        BigDecimal.valueOf(12.50 + index)))
                .toList();
    }

    private OrderResponse order(List<OrderItemResponse> items) {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        return new OrderResponse(
                orderId.toString(),
                userId.toString(),
                "Av. Principal 123",
                "999999999",
                LocalDateTime.of(2026, 10, 13, 12, 30),
                "PAID",
                items,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(18),
                BigDecimal.valueOf(128),
                List.of("pi_test"),
                BigDecimal.TEN,
                "Lima");
    }

    private UserResponse user(String userId) {
        return new UserResponse(UUID.fromString(userId), "Ada Lovelace", "ada@example.com");
    }

    private long pdfFileCount() throws Exception {
        try (var paths = Files.walk(Path.of("."))) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".pdf"))
                    .count();
        }
    }
}
