import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartService } from '../../core/cart.service';
import { OrderService } from '../../core/order.service';
import { ProductService } from '../../core/product.service';
import { CartItem } from '../../core/models';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-4xl">
      <h1 class="text-3xl font-bold mb-8 text-gray-800 text-center">Finalizar Compra</h1>

      <!-- Progress Bar -->
      <div class="mb-12">
        <div class="flex items-center justify-center space-x-4">
          <div class="flex items-center">
            <div
              [class.bg-indigo-600]="step() >= 1"
              class="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold transition-colors"
            >
              1
            </div>
            <span class="ml-2 font-medium" [class.text-indigo-600]="step() === 1">Envío</span>
          </div>
          <div class="w-16 h-px bg-gray-300"></div>
          <div class="flex items-center">
            <div
              [class.bg-indigo-600]="step() >= 2"
              [class.bg-gray-300]="step() < 2"
              class="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold transition-colors"
            >
              2
            </div>
            <span class="ml-2 font-medium" [class.text-indigo-600]="step() === 2">Pago</span>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Form Section -->
        <div class="md:col-span-2">
          @if (errorMessage()) {
            <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded-md">
              <p class="text-sm text-red-700">{{ errorMessage() }}</p>
            </div>
          }

          <form [formGroup]="checkoutForm">
            <!-- Step 1: Address & Shipping -->
            @if (step() === 1) {
              <div formGroupName="shipping" class="space-y-6 animate-fade-in">
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
                      placeholder="Ej: Bogotá"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Teléfono *</label>
                    <input
                      type="tel"
                      formControlName="phone"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                      placeholder="Ej: 3001234567"
                    />
                  </div>
                </div>
                <div class="pt-4">
                  <button
                    type="button"
                    (click)="nextStep()"
                    [disabled]="!checkoutForm.get('shipping')?.valid"
                    class="w-full bg-indigo-600 text-white py-3 rounded-lg font-bold hover:bg-indigo-700 transition-colors disabled:opacity-50"
                  >
                    Continuar al Pago
                  </button>
                </div>
              </div>
            }

            <!-- Step 2: Payment -->
            @if (step() === 2) {
              <div formGroupName="payment" class="space-y-6 animate-fade-in">
                <h2 class="text-xl font-semibold mb-4 border-b pb-2">Información de Pago</h2>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1"
                    >Nombre en la Tarjeta</label
                  >
                  <input
                    type="text"
                    formControlName="cardName"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    placeholder="Nombre completo"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1"
                    >Número de Tarjeta</label
                  >
                  <input
                    type="text"
                    formControlName="cardNumber"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    placeholder="16 dígitos"
                  />
                </div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Vencimiento</label>
                    <input
                      type="text"
                      formControlName="expiry"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                      placeholder="MM/YY"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">CVV</label>
                    <input
                      type="password"
                      formControlName="cvv"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                      placeholder="123"
                    />
                  </div>
                </div>

                <div class="flex gap-4 pt-4">
                  <button
                    type="button"
                    (click)="prevStep()"
                    class="flex-1 bg-gray-100 text-gray-700 py-3 rounded-lg font-bold hover:bg-gray-200 transition-colors"
                  >
                    Regresar
                  </button>
                  <button
                    type="button"
                    (click)="processPayment()"
                    [disabled]="!checkoutForm.get('payment')?.valid || loading()"
                    class="flex-1 bg-indigo-600 text-white py-3 rounded-lg font-bold hover:bg-indigo-700 transition-colors disabled:opacity-50"
                  >
                    @if (loading()) {
                      <span
                        class="animate-spin inline-block mr-2 h-4 w-4 border-2 border-white border-t-transparent rounded-full"
                      ></span>
                      Procesando...
                    } @else {
                      Confirmar Compra
                    }
                  </button>
                </div>
              </div>
            }
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
  styles: [
    `
      .animate-fade-in {
        animation: fadeIn 0.3s ease-out;
      }
      @keyframes fadeIn {
        from {
          opacity: 0;
          transform: translateY(10px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }
    `,
  ],
})
export class CheckoutComponent implements OnInit {
  step = signal(1);
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
    private productService: ProductService,
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
      payment: this.fb.group({
        cardName: ['', Validators.required],
        cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
        expiry: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\/([0-9]{2})$')]],
        cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]],
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

  nextStep(): void {
    if (this.checkoutForm.get('shipping')?.valid) {
      this.step.set(2);
    }
  }

  prevStep(): void {
    this.step.set(1);
  }

  processPayment(): void {
    if (this.checkoutForm.get('payment')?.invalid) return;

    this.loading.set(true);
    this.errorMessage.set('');

    // HU-13: Validate stock again before payment
    const outOfStock = this.cartItems().filter(
      (item: CartItem) => !this.productService.checkStock(item.product.id, item.quantity),
    );

    if (outOfStock.length > 0) {
      this.loading.set(false);
      this.errorMessage.set(
        `Lo sentimos, los siguientes productos ya no tienen stock suficiente: ${outOfStock.map((i: CartItem) => i.product.name).join(', ')}`,
      );
      return;
    }

    // HU-07-CA2: Simulate Payment Rejected (e.g. if card number starts with '0000')
    const cardNumber = this.checkoutForm.get('payment.cardNumber')?.value;
    if (cardNumber.startsWith('0000')) {
      setTimeout(() => {
        this.loading.set(false);
        this.errorMessage.set(
          'El pago fue rechazado por la entidad financiera. Por favor intente con otra tarjeta.',
        );
      }, 1500);
      return;
    }

    // HU-07: Successful payment
    const shippingInfo = this.checkoutForm.get('shipping')?.value;
    this.orderService.createOrder(this.cartItems(), this.total(), shippingInfo).subscribe({
      next: (order) => {
        this.cartService.clearCart();
        this.router.navigate(['/order-confirmation'], { state: { order } });
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Hubo un error al procesar el pedido. Intente de nuevo.');
      },
    });
  }
}
