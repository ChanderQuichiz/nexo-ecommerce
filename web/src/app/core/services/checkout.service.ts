import { Injectable, inject, signal } from '@angular/core';
import { CartService } from './cart.service';
import { PaymentService } from './payment.service';

@Injectable({ providedIn: 'root' })
export class CheckoutService {
  private readonly cart = inject(CartService);
  private readonly payments = inject(PaymentService);
  readonly error = signal('');
  readonly completed = signal(false);

  startPayment(): boolean {
    this.error.set('');
    if (!this.cart.items().length) {
      this.error.set('Agrega al menos un producto antes de pagar.');
      return false;
    }
    try {
      this.payments.createCheckout({
        orderId: `checkout-${Date.now()}`,
        amount: this.cart.total(),
        currency: 'USD',
      });
      this.completed.set(true);
      return true;
    } catch (error) {
      this.error.set(error instanceof Error ? error.message : 'No se pudo iniciar el pago.');
      return false;
    }
  }
}
