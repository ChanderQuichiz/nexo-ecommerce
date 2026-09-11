package com.nexo.ecommerce.orders.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {

    @Value("${stripe.apiKey}")
    private String secretKey;

    @Value("${constants.currency}")
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


public PaymentIntent createPaymentIntent(Long amount) throws StripeException {
    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amount) // Ejemplo: 1000L = $10.00
            .setCurrency(currency) // "usd", "pen", etc.
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
            )
            .build();

    return PaymentIntent.create(params);
}

}