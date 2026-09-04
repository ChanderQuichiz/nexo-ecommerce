import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { CheckoutService } from '../../core/services/checkout.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cart',
  imports: [FormsModule, RouterLink],
  template: `<section class="inner-page">
    <p class="eyebrow">TU COMPRA</p>
    <h1>Carrito</h1>
    <div class="cart-page">
      <div class="cart-list">
        @for (item of cart.items(); track item.id) {
          <div class="cart-row">
            <img [src]="item.image" [alt]="item.name" />
            <div>
              <strong>{{ item.name }}</strong
              ><small>{{ item.category }}</small>
            </div>
            <div class="quantity">
              <button (click)="cart.change(item, -1)">−</button><span>{{ item.quantity }}</span
              ><button (click)="cart.change(item, 1)">＋</button>
            </div>
            <strong>{{ format(item.price * item.quantity) }}</strong>
          </div>
        } @empty {
          <p class="empty-state">
            Tu carrito está vacío. Vuelve a la tienda para agregar productos.
          </p>
        }
      </div>
      <aside class="summary">
        <h2>Resumen</h2>
        <div>
          <span>Subtotal</span><strong>{{ format(cart.subtotal()) }}</strong>
        </div>
        <div><span>Envío</span><strong>Gratis</strong></div>
        <div class="coupon">
          <input [(ngModel)]="coupon" placeholder="Código de cupón" /><button
            (click)="applyCoupon()"
          >
            Aplicar
          </button>
        </div>
        @if (applied()) {
          <p class="coupon-ok">Cupón aplicado: 10% OFF</p>
        }
        <div class="total">
          <span>Total</span><strong>{{ format(cart.total()) }}</strong>
        </div>
        <button class="checkout" routerLink="/checkout">Continuar al pago →</button>
        @if (checkout.error()) {
          <p class="form-message">{{ checkout.error() }}</p>
        }
        @if (checkout.completed()) {
          <p class="coupon-ok">Pago listo para redirigir al proveedor.</p>
        }
      </aside>
    </div>
  </section>`,
})
export class CartComponent {
  protected readonly cart = inject(CartService);
  protected readonly checkout = inject(CheckoutService);
  protected coupon = '';
  protected readonly applied = signal(false);
  protected applyCoupon(): void {
    this.applied.set(this.cart.applyCoupon(this.coupon));
  }
  protected format(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
}
