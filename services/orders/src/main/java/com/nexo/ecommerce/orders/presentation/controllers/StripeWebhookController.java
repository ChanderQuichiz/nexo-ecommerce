package com.nexo.ecommerce.orders.presentation.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexo.ecommerce.orders.application.usecases.ProcessPaymentWebhookUseCase;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    public StripeWebhookController(ProcessPaymentWebhookUseCase processPaymentWebhookUseCase) {
        this.processPaymentWebhookUseCase = processPaymentWebhookUseCase;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        Event event;

        try {
            if (webhookSecret != null && !webhookSecret.isEmpty() && sigHeader != null) {
                // Validación estricta con la firma de Stripe (Requerido en producción)
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                // Modo desarrollo/prueba sin secreto configurado
                event = com.stripe.net.ApiResource.GSON.fromJson(payload, Event.class);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }

        String paymentIntentId = null;
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                paymentIntentId = paymentIntent.getId();
            }
        } catch (Exception ignored) {
        }

        if (paymentIntentId == null) {
            try {
                JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
                JsonObject dataObj = jsonObject.getAsJsonObject("data").getAsJsonObject("object");
                paymentIntentId = dataObj.get("id").getAsString();
            } catch (Exception ignored) {
            }
        }

        // Manejar los tipos de eventos de Stripe de forma desacoplada
        switch (event.getType()) {
            case "payment_intent.succeeded":
                if (paymentIntentId != null) {
                    processPaymentWebhookUseCase.execute(paymentIntentId, "PAID");
                }
                break;
            case "payment_intent.payment_failed":
                if (paymentIntentId != null) {
                    processPaymentWebhookUseCase.execute(paymentIntentId, "FAILED");
                }
                break;
            default:
                // Evento no manejado, se retorna 200 OK para que Stripe no reintente innecesariamente
                break;
        }

        return ResponseEntity.ok("Webhook received successfully");
    }
}
