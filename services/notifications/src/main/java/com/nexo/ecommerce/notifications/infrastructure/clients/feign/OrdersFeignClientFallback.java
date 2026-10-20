package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrdersFeignClientFallback implements OrdersFeignClient {

    private static final Logger log = LoggerFactory.getLogger(OrdersFeignClientFallback.class);

    @Override
    public OrderResponse getOrderById(String orderId) {
        log.error("Circuit Breaker Abierto: Orders Service no esta disponible para obtener orden {}", orderId);
        throw new IllegalStateException("El servicio de Ordenes no esta disponible actualmente para obtener detalles del pedido con ID: " + orderId);
    }
}
