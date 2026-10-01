package com.nexo.ecommerce.orders.application.ports;

import java.math.BigDecimal;

public interface PaymentPort {
    String createPaymentIntent(String orderId, BigDecimal amount, String currency);
}