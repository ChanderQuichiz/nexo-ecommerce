package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class OrdersFeignClientFallback implements OrdersFeignClient {

    @Override
    public OrderResponse getOrderById(String orderId) {
        // Fallback cuando Orders Service no está disponible
        return new OrderResponse(
                orderId,
                "unknown-user",
                "N/A",
                "N/A",
                LocalDateTime.now(),
                "PENDING",
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Collections.emptyList(),
                BigDecimal.ZERO,
                "N/A"
        );
    }
}
