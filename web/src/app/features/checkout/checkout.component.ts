import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CartService } from '../../core/cart.service';
import { OrderService } from '../../core/order.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-4xl">
      <h1 class="text-3xl font-bold mb-8 text-gray-800 text-center">Finalizar Compra</h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Form Section -->
        <div class="md:col-span-2">
          @if (errorMessage()) {
            <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded-md">
              <p class="text-sm text-red-700">{{ errorMessage() }}</p>
            </div>
          }

          <form [formGroup]="checkoutForm" class="space-y-6">
            <div formGroupName="shipping" class="space-y-6 bg-white p-6 rounded-xl border border-gray-100 shadow-sm">
              <h2 class="text-xl font-semibold mb-4 border-b pb-2">Información de Envío</h2>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Dirección *</label>
                <input
                  type="text"
                  formControlName="address"
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                  placeholder="Calle, Número, Apto"
                />
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Ciudad *</label>
                  <input
                    type="text"
                    formControlName="city"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    placeholder="Ej: Lima"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Teléfono *</label>
                  <input
                    type="tel"
                    formControlName="phone"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    placeholder="Ej: +51999888777"
                  />
                </div>
              </div>
              <div class="pt-4">
                <button
                  type="button"
                  (click)="createOrder()"
                  [disabled]="!checkoutForm.get('shipping')?.valid || loading()"
                  class="w-full bg-indigo-600 text-white py-3 rounded-lg font-bold hover:bg-indigo-700 transition-colors disabled:opacity-50 flex items-center justify-center shadow-lg shadow-indigo-100"
                >
                  @if (loading()) {
                    <span class="animate-spin inline-block mr-2 h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                    Creando Pedido...
                  } @else {
                    Confirmar y Crear Pedido
                  }
                </button>
              </div>
            </div>
          </form>
        </div>

        <!-- Summary Section -->
        <div class="bg-gray-50 p-6 rounded-xl border border-gray-200 h-fit">
          <h2 class="text-lg font-bold mb-4">Resumen del Pedido</h2>
          <div class="space-y-4 mb-6">
            @for (item of cartItems(); track item.product.id) {
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">{{ item.quantity }}x {{ item.product.name }}</span>
                <span class="font-medium">{{ item.product.price * item.quantity | currency }}</span>
              </div>
            }
          </div>
          <div class="border-t pt-4 space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Subtotal</span>
              <span>{{ subtotal() | currency }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Envío</span>
              <span>{{ shippingCost | currency }}</span>
            </div>
            <div class="flex justify-between text-xl font-bold pt-2 text-indigo-700">
              <span>Total</span>
              <span>{{ total() | currency }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class CheckoutComponent implements OnInit {
  loading = signal(false);
  errorMessage = signal('');
  shippingCost = 10;

  cartItems: any;
  subtotal: any;
  total = signal(0);

  checkoutForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router,
  ) {
    this.cartItems = this.cartService.items;
    this.subtotal = this.cartService.totalPrice;

    this.checkoutForm = this.fb.group({
      shipping: this.fb.group({
        address: ['', Validators.required],
        city: ['', Validators.required],
        phone: ['', [Validators.required, Validators.pattern('^[0-9+ ]{7,15}$')]],
      }),
    });
  }

  ngOnInit(): void {
    if (this.cartItems().length === 0) {
      this.router.navigate(['/catalog']);
      return;
    }
    this.total.set(this.subtotal() + this.shippingCost);
  }

  async createOrder(): Promise<void> {
    if (!this.checkoutForm.get('shipping')?.valid) return;

    this.loading.set(true);
    this.errorMessage.set('');

    try {
      const shippingInfo = this.checkoutForm.get('shipping')?.value;
      // 1. Crear la orden (POST /orders -> Estado PENDING)
      await firstValueFrom(
        this.orderService.createOrder(this.cartItems(), this.total(), shippingInfo)
      );

      this.cartService.clearCart();
      this.loading.set(false);

      // 2. Redirigir a "Mis Pedidos" (Order History) donde el usuario puede ver su pedido y realizar el pago
      this.router.navigate(['/orders']);
    } catch (err: any) {
      this.errorMessage.set(err.message || 'Error al crear el pedido.');
      this.loading.set(false);
    }
  }
}
