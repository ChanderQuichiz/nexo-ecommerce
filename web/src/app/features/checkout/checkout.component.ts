import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CheckoutService } from '../../core/services/checkout.service';
import { OrdersService } from '../../core/services/orders.service';
import { CatalogService } from '../../core/services/catalog.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-checkout',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">COMPRA / PAGO SEGURO</p>
    <h1>Finalizar compra</h1>
    <div class="checkout-layout">
      <form class="profile-form" (ngSubmit)="pay()">
        <h2>Datos de entrega</h2>
        <div class="checkout-fields">
          <label
            >Dirección<input
              [(ngModel)]="address"
              name="address"
              required
              placeholder="Av. Corrientes 1234" /></label
          ><label
            >Ciudad<input
              [(ngModel)]="city"
              name="city"
              required
              placeholder="Buenos Aires" /></label
          ><label
            >Código postal<input [(ngModel)]="zip" name="zip" required placeholder="C1043"
          /></label>
        </div>

        <h2>Medio de pago</h2>
        <div class="card-form">
          <label
            >Número de tarjeta
            <input name="card" required placeholder="0000 0000 0000 0000" maxlength="19" />
          </label>
          <div class="checkout-fields">
            <label
              >Vencimiento <input name="exp" required placeholder="MM/YY" maxlength="5"
            /></label>
            <label>CVV <input name="cvv" required placeholder="000" maxlength="3" /></label>
          </div>
        </div>

        <p class="secure-payment">
          Pago procesado de forma segura. Los datos de tu tarjeta están protegidos por encriptación
          bancaria.
        </p>
        @if (checkout.error()) {
          <p class="form-message">{{ checkout.error() }}</p>
        }
        <button class="primary-button" [disabled]="cart.items().length === 0">
          Confirmar y Pagar {{ format(cart.total()) }}
        </button>
      </form>
      <aside class="summary">
        <h2>Tu pedido</h2>
        @for (item of cart.items(); track item.id) {
          <div class="summary-item">
            <span>{{ item.quantity }} × {{ item.name }}</span>
            <strong>{{ format(item.price * item.quantity) }}</strong>
          </div>
        }
        @if (cart.discount() > 0) {
          <div class="summary-item discount">
            <span>Descuento (NUEVO10)</span>
            <strong>- {{ format(cart.discount()) }}</strong>
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
  private readonly orders = inject(OrdersService);
  private readonly catalog = inject(CatalogService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected address = '';
  protected city = '';
  protected zip = '';

  protected pay(): void {
    if (this.cart.items().length === 0) return;

    // Simulate stock check and reduction
    const stockAvailable = this.catalog.reserve(this.cart.items());
    if (!stockAvailable) {
      alert('Lo sentimos, algunos productos ya no tienen stock suficiente.');
      return;
    }

    if (this.checkout.startPayment()) {
      this.orders.create(this.auth.user()?.name || 'Cliente', this.cart.total());
      this.cart.clear();
      this.router.navigateByUrl('/orders');
    }
  }

  protected format(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
}
