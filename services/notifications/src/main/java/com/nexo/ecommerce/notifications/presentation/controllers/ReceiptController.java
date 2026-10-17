package com.nexo.ecommerce.notifications.presentation.controllers;

import com.nexo.ecommerce.notifications.application.usecases.GenerateReceiptUseCase;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class ReceiptController {

    private final GenerateReceiptUseCase generateReceiptUseCase;

    public ReceiptController(GenerateReceiptUseCase generateReceiptUseCase) {
        this.generateReceiptUseCase = generateReceiptUseCase;
    }

    @GetMapping(value = "/generate-receipt/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateReceipt(@PathVariable String orderId) {
        OrderId validatedOrderId = parseOrderId(orderId);
        String normalizedOrderId = validatedOrderId.value().toString();
        byte[] receipt = generateReceiptUseCase.execute(normalizedOrderId);
        String disposition = ContentDisposition.attachment()
                .filename("receipt-" + normalizedOrderId + ".pdf")
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(receipt);
    }

    private OrderId parseOrderId(String value) {
        UUID uuid = UUID.fromString(value);
        if (!uuid.toString().equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("Order ID must be a canonical UUID");
        }
        return new OrderId(uuid);
    }
}
