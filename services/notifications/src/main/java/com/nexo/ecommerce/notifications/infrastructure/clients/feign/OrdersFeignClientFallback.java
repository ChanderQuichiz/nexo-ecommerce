package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrdersFeignClientFallback implements OrdersFeignClient {

    @Override
    public OrderResponse getOrderById(String orderId) {
        // En lugar de un comprobante vacío/ficticio, lanzamos una excepción para que RabbitMQ reintente la notificación cuando Orders Service esté activo
        throw new IllegalStateException("El servicio de Órdenes no está disponible actualmente para obtener detalles del pedido con ID: " + orderId);
    }
}
