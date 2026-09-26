import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { OrderService } from '../../core/order.service';
import { Order } from '../../core/models';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-10 max-w-5xl">
      <div class="flex justify-between items-center mb-8">
        <div>
          <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">Mis Pedidos</h1>
          <p class="text-sm text-gray-500 mt-1">Historial y gestión de tus compras en Nexo</p>
        </div>
        <a
          routerLink="/catalog"
          class="bg-indigo-600 text-white px-5 py-2.5 rounded-xl font-bold hover:bg-indigo-700 transition-all shadow-md shadow-indigo-100 flex items-center gap-2 text-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Seguir Comprando
        </a>
      </div>

      @if (errorMessage()) {
        <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded-xl shadow-sm">
          <p class="text-sm text-red-700 font-medium">{{ errorMessage() }}</p>
        </div>
      }

      @if (orders().length > 0) {
        <div class="grid grid-cols-1 gap-6">
          @for (order of orders(); track order.id) {
            <div class="bg-white rounded-2xl shadow-sm hover:shadow-md border border-gray-100 transition-all duration-200 overflow-hidden flex flex-col">
              <!-- Card Header -->
              <div class="bg-gray-50/70 px-6 py-4 border-b border-gray-100 flex flex-wrap justify-between items-center gap-4">
                <div class="flex items-center gap-3">
                  <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Pedido ID:</span>
                  <span class="font-mono text-sm text-indigo-600 font-bold bg-indigo-50 px-2.5 py-1 rounded-lg">
                    #{{ order.id }}
                  </span>
                </div>
                <div class="flex items-center gap-4">
                  <span class="text-xs text-gray-500 font-medium">
                    {{ order.date | date: 'medium' }}
                  </span>
                  <span
                    [class]="getStatusClass(order.status)"
                    class="px-3 py-1 rounded-full text-xs font-extrabold tracking-wide uppercase shadow-2xs"
                  >
                    {{ order.status }}
                  </span>
                </div>
              </div>

              <!-- Card Body -->
              <div class="p-6 grid grid-cols-1 md:grid-cols-3 gap-6 items-center">
                <!-- Shipping & Summary -->
                <div class="md:col-span-2 space-y-3">
                  <div>
                    <h4 class="text-xs font-bold uppercase text-gray-400 tracking-wider mb-1">Dirección de Envío</h4>
                    <p class="text-sm text-gray-800 font-medium">
                      {{ order.shippingAddress?.address }}, {{ order.shippingAddress?.city }}
                    </p>
                    <p class="text-xs text-gray-500">Teléfono: {{ order.shippingAddress?.phone }}</p>
                  </div>

                  <div>
                    <h4 class="text-xs font-bold uppercase text-gray-400 tracking-wider mb-1">Artículos ({{ order.items.length }})</h4>
                    <div class="flex flex-wrap gap-2">
                      @for (item of order.items; track item.product.id) {
                        <span class="inline-flex items-center bg-gray-100 text-gray-700 text-xs px-2.5 py-1 rounded-md font-medium">
                          {{ item.quantity }}x {{ item.product.name }}
                        </span>
                      }
                    </div>
                  </div>
                </div>

                <!-- Price & Actions -->
                <div class="flex flex-col md:items-end justify-between border-t md:border-t-0 md:border-l border-gray-100 pt-4 md:pt-0 md:pl-6 gap-4">
                  <div class="text-left md:text-right">
                    <span class="text-xs text-gray-400 font-semibold block uppercase">Total a pagar</span>
                    <span class="text-2xl font-black text-gray-900">{{ order.total | currency }}</span>
                  </div>

                  <div class="flex flex-wrap gap-2.5 w-full md:w-auto">
                    <button
                      (click)="viewDetails(order)"
                      class="flex-1 md:flex-initial bg-white border border-gray-200 text-gray-700 px-4 py-2.5 rounded-xl hover:bg-gray-50 transition-all font-semibold text-xs shadow-2xs flex items-center justify-center gap-1.5"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                      Detalles
                    </button>

                    @if (order.status === 'PENDING' || order.status === 'pending') {
                      <button
                        (click)="openPaymentModal(order)"
                        class="flex-1 md:flex-initial bg-green-600 text-white px-5 py-2.5 rounded-xl hover:bg-green-700 transition-all font-bold text-xs shadow-md shadow-green-100 flex items-center justify-center gap-1.5"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                        </svg>
                        Realizar Pago
                      </button>
                    }
                  </div>
                </div>
              </div>
            </div>
          }
        </div>
      } @else {
        <div class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <div class="w-20 h-20 bg-indigo-50 text-indigo-500 rounded-full flex items-center justify-center mx-auto mb-5 shadow-inner">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
          <h2 class="text-xl font-bold text-gray-800 mb-2">No tienes pedidos aún</h2>
          <p class="text-gray-500 mb-8 max-w-sm mx-auto text-sm">Explora nuestro catálogo, añade productos a tu carrito y realiza tu primera compra.</p>
          <a
            routerLink="/catalog"
            class="inline-block bg-indigo-600 text-white px-8 py-3 rounded-xl font-bold hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-100 text-sm"
          >
            Explorar Catálogo
          </a>
        </div>
      }

      <!-- Order Details Modal -->
      @if (selectedOrder()) {
        <div class="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-fade-in">
          <div class="bg-white rounded-3xl max-w-xl w-full p-8 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            <div class="flex justify-between items-start mb-6">
              <div>
                <span class="text-xs font-bold text-indigo-600 uppercase tracking-widest bg-indigo-50 px-2.5 py-1 rounded-md">Detalles</span>
                <h2 class="text-2xl font-black text-gray-900 mt-2">Pedido #{{ selectedOrder()?.id }}</h2>
              </div>
              <button (click)="closeDetails()" class="text-gray-400 hover:text-gray-600 bg-gray-50 hover:bg-gray-100 p-2.5 rounded-full transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div class="space-y-6 mb-6 overflow-y-auto pr-2">
              <div class="bg-gray-50 p-5 rounded-2xl border border-gray-100 text-sm space-y-2">
                <div class="flex justify-between"><span class="text-gray-500 font-medium">Estado:</span> <span class="font-bold uppercase" [class]="getStatusClass(selectedOrder()?.status || '')">{{ selectedOrder()?.status }}</span></div>
                <div class="flex justify-between"><span class="text-gray-500 font-medium">Fecha:</span> <span class="text-gray-800 font-medium">{{ selectedOrder()?.date | date: 'medium' }}</span></div>
                <div class="flex justify-between"><span class="text-gray-500 font-medium">Dirección:</span> <span class="text-gray-800 font-medium">{{ selectedOrder()?.shippingAddress?.address }}</span></div>
                <div class="flex justify-between"><span class="text-gray-500 font-medium">Ciudad:</span> <span class="text-gray-800 font-medium">{{ selectedOrder()?.shippingAddress?.city }}</span></div>
                <div class="flex justify-between"><span class="text-gray-500 font-medium">Teléfono:</span> <span class="text-gray-800 font-medium">{{ selectedOrder()?.shippingAddress?.phone }}</span></div>
              </div>

              <div>
                <h3 class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-3">Productos del Pedido</h3>
                <div class="space-y-3">
                  @for (item of selectedOrder()?.items; track item.product.id) {
                    <div class="flex items-center gap-4 p-3 bg-gray-50/50 rounded-xl border border-gray-100">
                      <img [src]="item.product.imageUrl" [alt]="item.product.name" class="w-12 h-12 object-cover rounded-lg bg-gray-200" />
                      <div class="flex-grow">
                        <p class="font-bold text-sm text-gray-800">{{ item.product.name }}</p>
                        <p class="text-xs text-gray-500">{{ item.quantity }} x {{ item.product.price | currency }}</p>
                      </div>
                      <span class="font-bold text-sm text-gray-900">{{ item.product.price * item.quantity | currency }}</span>
                    </div>
                  }
                </div>
              </div>
            </div>

            <div class="bg-indigo-50/70 p-5 rounded-2xl flex justify-between items-center mt-auto border border-indigo-100">
              <div>
                <span class="text-xs text-indigo-600 font-bold uppercase tracking-wider block">Total a Pagar</span>
                <span class="text-2xl font-black text-indigo-900">{{ selectedOrder()?.total | currency }}</span>
              </div>
              <div class="flex gap-3">
                @if (selectedOrder()?.status === 'PENDING' || selectedOrder()?.status === 'pending') {
                  <button
                    (click)="openPaymentModal(selectedOrder()!); closeDetails()"
                    class="bg-green-600 text-white px-5 py-2.5 rounded-xl font-bold hover:bg-green-700 transition-all text-sm shadow-md shadow-green-100"
                  >
                    Realizar Pago
                  </button>
                }
                <button (click)="closeDetails()" class="bg-white border border-gray-200 text-gray-700 px-5 py-2.5 rounded-xl font-bold hover:bg-gray-50 transition-all text-sm">
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- Stripe Simulated Payment Modal -->
      @if (paymentModalOpen() && orderToPay()) {
        <div class="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-fade-in">
          <div class="bg-white rounded-3xl max-w-md w-full p-8 shadow-2xl overflow-hidden">
            <div class="flex justify-between items-center mb-6">
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 bg-indigo-600 text-white rounded-lg flex items-center justify-center font-bold text-sm">S</div>
                <h3 class="text-lg font-extrabold text-gray-900">Checkout Seguro Stripe</h3>
              </div>
              <button (click)="closePaymentModal()" class="text-gray-400 hover:text-gray-600 bg-gray-50 p-2 rounded-full transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div class="bg-indigo-50/50 p-4 rounded-2xl border border-indigo-100 mb-6 text-sm flex justify-between items-center">
              <div>
                <p class="text-xs text-gray-500 font-medium">Pedido #{{ orderToPay()?.id }}</p>
                <p class="font-extrabold text-indigo-900 text-lg">{{ orderToPay()?.total | currency }}</p>
              </div>
              <span class="text-xs bg-indigo-100 text-indigo-700 px-2.5 py-1 rounded-md font-bold">Stripe Elements</span>
            </div>

            <form [formGroup]="paymentForm" class="space-y-4">
              <div>
                <label class="block text-xs font-bold uppercase text-gray-500 tracking-wider mb-1">Nombre en la tarjeta</label>
                <input type="text" formControlName="cardName" class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none text-sm font-medium" placeholder="Ej: Juan Pérez" />
              </div>

              <div>
                <label class="block text-xs font-bold uppercase text-gray-500 tracking-wider mb-1">Número de tarjeta</label>
                <div class="relative">
                  <input type="text" formControlName="cardNumber" class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none text-sm font-mono" placeholder="4242 •••• •••• 4242" />
                  <span class="absolute right-3 top-2.5 text-xs bg-gray-100 font-bold px-2 py-1 rounded text-gray-500">VISA</span>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div>
                  <label class="block text-xs font-bold uppercase text-gray-500 tracking-wider mb-1">Expiración</label>
                  <input type="text" formControlName="cardExp" class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none text-sm font-mono" placeholder="MM/AA" />
                </div>
                <div>
                  <label class="block text-xs font-bold uppercase text-gray-500 tracking-wider mb-1">CVC / CWD</label>
                  <input type="password" maxlength="4" formControlName="cardCvc" class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none text-sm font-mono" placeholder="123" />
                </div>
              </div>

              <div class="pt-4">
                <button
                  type="button"
                  (click)="confirmPayment()"
                  [disabled]="!paymentForm.valid || processing()"
                  class="w-full bg-green-600 text-white py-3.5 rounded-xl font-bold hover:bg-green-700 transition-all disabled:opacity-50 text-sm shadow-lg shadow-green-100 flex items-center justify-center gap-2"
                >
                  @if (processing()) {
                    <span class="animate-spin inline-block h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                    Confirmando pago seguro...
                  } @else {
                    Pagar {{ orderToPay()?.total | currency }}
                  }
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .animate-fade-in {
        animation: fadeIn 0.25s ease-out;
      }
      @keyframes fadeIn {
        from { opacity: 0; transform: scale(0.97); }
        to { opacity: 1; transform: scale(1); }
      }
    `,
  ],
})
export class OrderHistoryComponent implements OnInit {
  orders = signal<Order[]>([]);
  selectedOrder = signal<Order | null>(null);
  
  // Payment Modal State
  paymentModalOpen = signal(false);
  orderToPay = signal<Order | null>(null);
  processing = signal(false);
  errorMessage = signal('');

  paymentForm: FormGroup;

  constructor(
    private orderService: OrderService,
    private fb: FormBuilder,
  ) {
    this.paymentForm = this.fb.group({
      cardName: ['Juan Pérez', Validators.required],
      cardNumber: ['4242 4242 4242 4242', [Validators.required, Validators.minLength(15)]],
      cardExp: ['12/28', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\\/([0-9]{2})$')]],
      cardCvc: ['123', [Validators.required, Validators.minLength(3)]],
    });
  }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getUserOrders().subscribe((orders) => {
      this.orders.set(orders);
    });
  }

  getStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PAID':
      case 'PAGADO':
        return 'bg-green-50 text-green-700 border border-green-200';
      case 'PENDING':
      case 'pending':
        return 'bg-amber-50 text-amber-700 border border-amber-200';
      case 'CANCELLED':
      case 'CANCELADO':
        return 'bg-red-50 text-red-700 border border-red-200';
      default:
        return 'bg-indigo-50 text-indigo-700 border border-indigo-200';
    }
  }

  viewDetails(order: Order): void {
    this.selectedOrder.set(order);
  }

  closeDetails(): void {
    this.selectedOrder.set(null);
  }

  openPaymentModal(order: Order): void {
    this.orderToPay.set(order);
    this.paymentModalOpen.set(true);
    this.errorMessage.set('');
  }

  closePaymentModal(): void {
    this.paymentModalOpen.set(false);
    this.orderToPay.set(null);
    this.processing.set(false);
  }

  async confirmPayment(): Promise<void> {
    const order = this.orderToPay();
    if (!order || !this.paymentForm.valid) return;

    this.processing.set(true);
    this.errorMessage.set('');

    try {
      // 1. Crear Intent de Pago simulado (/orders/intent-payment)
      const intentRes = await firstValueFrom(
        this.orderService.createIntentPayment(order.id)
      );

      // 2. Simular webhook de pago exitoso de Stripe (/webhooks/stripe -> PAID)
      await firstValueFrom(
        this.orderService.simulateStripeWebhook(intentRes.intentId, 'PAID')
      );

      // Recargar lista y cerrar modal
      this.loadOrders();
      this.closePaymentModal();
    } catch (err: any) {
      this.errorMessage.set('Error al procesar el pago con Stripe.');
      this.processing.set(false);
    }
  }
}
