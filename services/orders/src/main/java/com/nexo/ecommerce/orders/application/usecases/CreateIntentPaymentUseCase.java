package com.nexo.ecommerce.orders.application.usecases;

import java.math.BigDecimal;

import com.nexo.ecommerce.orders.application.ports.PaymentPort;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentResponse;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.PaymentIntentId;

public class CreateIntentPaymentUseCase {
        private final OrderRepository orderRepository;
        private final PaymentPort paymentPort;
        public CreateIntentPaymentUseCase(OrderRepository orderRepository, PaymentPort paymentPort) {
            this.orderRepository = orderRepository;
            this.paymentPort = paymentPort;
        }

        public CreateIntentPaymentResponse execute(CreateIntentPaymentRequest request) {
            // Lógica para crear el intent de pago
            Order existingOrder = orderRepository.findById(request.orderId());
            if (existingOrder == null) {
                throw new IllegalArgumentException("Order not found with ID: " + request.orderId());
            }
            BigDecimal amount = existingOrder.getTotal().value();
            String currency = "USD";

            String intentId = paymentPort.createPaymentIntent(request.orderId(), amount, currency);
            PaymentIntentId paymentIntentId = new PaymentIntentId(intentId);
            existingOrder.addPaymentIntentId(paymentIntentId);
            orderRepository.save(existingOrder);

            return new CreateIntentPaymentResponse(intentId);
        }

}
