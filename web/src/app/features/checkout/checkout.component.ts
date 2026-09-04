import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CheckoutService } from '../../core/services/checkout.service';

@Component({
  selector: 'app-checkout',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">COMPRA / PAGO SEGURO</p>
    <h1>Finalizar compra</h1>
    <div class="checkout-layout">
      <form class="profile-form" (ngSubmit)="pay()">
        <h2>Datos de entrega</h2>
        <label
          >Dirección<input
            [(ngModel)]="address"
            name="address"
            required
            placeholder="Av. Corrientes 1234" /></label
        ><label
          >Ciudad<input [(ngModel)]="city" name="city" required placeholder="Buenos Aires" /></label
        ><label
          >Código postal<input [(ngModel)]="zip" name="zip" required placeholder="C1043"
        /></label>
        <h2>Medio de pago</h2>
        <p class="secure-payment">
          Serás redirigido a la página segura del proveedor de pagos. Los datos de tu tarjeta nunca
          pasan por esta aplicación.
        </p>
        @if (checkout.error()) {
          <p class="form-message">{{ checkout.error() }}</p>
        }
        <button class="primary-button">Pagar {{ format(cart.total()) }}</button>
      </form>
      <aside class="summary">
        <h2>Tu pedido</h2>
        @for (item of cart.items(); track item.id) {
          <div>
            <span>{{ item.quantity }} × {{ item.name }}</span
            ><strong>{{ format(item.price * item.quantity) }}</strong>
          </div>
        }
        <div class="total">
          <span>Total</span><strong>{{ format(cart.total()) }}</strong>
        </div>
      </aside>
    </div>
  </section>`,
})
export class CheckoutComponent {
  protected readonly cart = inject(CartService);
  protected readonly checkout = inject(CheckoutService);
  private readonly router = inject(Router);
  protected address = '';
  protected city = '';
  protected zip = '';
  protected pay(): void {
    if (this.checkout.startPayment()) this.router.navigateByUrl('/orders');
  }
  protected format(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
}
