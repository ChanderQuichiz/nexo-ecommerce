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
  createCheckout(_intent: PaymentIntent): PaymentResult {
    throw new Error('Payment provider is not configured. Connect the backend checkout session.');
  }
}
