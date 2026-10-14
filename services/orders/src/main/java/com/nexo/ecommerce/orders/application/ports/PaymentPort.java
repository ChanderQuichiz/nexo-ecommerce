package com.nexo.ecommerce.orders.application.ports;

import java.math.BigDecimal;

public interface PaymentPort {
    PaymentIntentResult createPaymentIntent(String orderId, BigDecimal amount, String currency);

    record PaymentIntentResult(String id, String clientSecret) {
    }
}