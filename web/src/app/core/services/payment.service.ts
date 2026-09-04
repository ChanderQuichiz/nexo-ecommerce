import { Injectable } from '@angular/core';

export interface PaymentIntent {
  orderId: string;
  amount: number;
  currency: string;
}
export interface PaymentResult {
  checkoutUrl: string;
}

/** Adapter boundary. The backend must create the hosted checkout session. */
@Injectable({ providedIn: 'root' })
export class PaymentService {
  /**
   * Simula la creación de una sesión de pago.
   * En producción, esto debería llamar a un backend real (Stripe, MercadoPago, etc.)
   */
  createCheckout(intent: PaymentIntent): PaymentResult {
    console.log('Iniciando pago para la orden:', intent.orderId);

    // Si el monto es mayor a 1000, simulamos un error de validación del proveedor
    if (intent.amount > 1000) {
      throw new Error('Límite de monto excedido para pagos de prueba.');
    }

    return {
      checkoutUrl: `/checkout/success?orderId=${intent.orderId}`
    };
  }
}
