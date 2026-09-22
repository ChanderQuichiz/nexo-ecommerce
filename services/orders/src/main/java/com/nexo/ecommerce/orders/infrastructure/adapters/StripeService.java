package com.nexo.ecommerce.orders.infrastructure.adapters;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

import com.nexo.ecommerce.orders.application.ports.PaymentPort;

@Service
public class StripeService implements PaymentPort {

    @Value("${stripe.apiKey}")
    private String secretKey;

    @Value("${constants.currency}")
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    @Override
    public String createPaymentIntent(String orderId, BigDecimal amount, String currency) {
        try {
            long cents = amount.multiply(new BigDecimal("100")).longValue();
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(cents)
                    .setCurrency(currency != null ? currency.toLowerCase() : "usd")
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                    )
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getId();
        } catch (StripeException e) {
            throw new RuntimeException("Error creating payment intent", e);
        }
    }

}