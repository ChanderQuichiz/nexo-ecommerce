import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { loadStripe, Stripe, StripeElements, StripePaymentElement } from '@stripe/stripe-js';
import { environment } from '../../../environments/environment';
import { OrderService } from '../../core/order.service';
import { Order } from '../../core/models';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="min-h-[calc(100vh-5rem)] bg-white px-4 py-8 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-6xl">
        <a routerLink="/orders" class="mb-6 inline-flex items-center gap-2 text-sm font-semibold text-slate-600 transition hover:text-black">
          <span aria-hidden="true">←</span> Volver a mis pedidos
        </a>

        @if (errorMessage()) {
          <div class="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-medium text-red-700">
            {{ errorMessage() }}
          </div>
        }

        @if (order(); as currentOrder) {
          <div class="grid items-start gap-8 lg:grid-cols-[minmax(0,0.85fr)_minmax(0,1.15fr)]">
            <section class="top-20 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm lg:sticky sm:p-8">
              <p class="text-xs font-bold uppercase tracking-[0.2em] text-slate-500">Pago seguro</p>
              <h1 class="mt-3 text-3xl font-black tracking-tight text-black">Finaliza tu compra</h1>
              <p class="mt-2 text-sm leading-6 text-slate-500">Tu información de pago es procesada de forma segura por Stripe.</p>

              <div class="mt-8 border-t border-slate-200 pt-6">
                <p class="text-xs font-bold uppercase tracking-wider text-slate-500">Pedido</p>
                <p class="mt-1 break-all font-mono text-sm text-slate-900">{{ currentOrder.id }}</p>
                <div class="mt-6 space-y-3 text-sm">
                  @for (item of currentOrder.items; track item.product.id) {
                    <div class="flex justify-between gap-4">
                      <span class="text-slate-600">{{ item.quantity }} × {{ item.product.name }}</span>
                      <span class="font-bold text-slate-900">{{ item.product.price * item.quantity | currency }}</span>
                    </div>
                  }
                </div>
              </div>

              <div class="mt-8 border-t border-slate-200 pt-5">
                <div class="flex items-center justify-between">
                  <span class="text-lg font-bold text-slate-900">Total</span>
                  <span class="text-3xl font-black text-black">{{ currentOrder.total | currency }}</span>
                </div>
              </div>
            </section>

            <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-8">
              <div class="mb-6">
                <h2 class="text-2xl font-black text-black">Método de pago</h2>
                <p class="mt-1 text-sm text-slate-500">Completa los datos para confirmar el pago.</p>
              </div>

              <div id="stripe-payment-element" class="min-h-24"></div>

              <button
                type="button"
                (click)="confirmPayment()"
                [disabled]="processing() || !paymentElementReady()"
                class="mt-8 flex w-full items-center justify-center gap-2 rounded-xl bg-black px-5 py-4 text-sm font-bold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
              >
                @if (processing()) {
                  <span class="inline-block h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></span>
                  Confirmando pago...
                } @else {
                  Pagar {{ currentOrder.total | currency }}
                }
              </button>

              <p class="mt-4 text-center text-xs leading-5 text-slate-500">
                Tus datos de tarjeta no pasan por nuestro servidor.
              </p>
            </section>
          </div>
        }
      </div>
    </main>
  `,
})
export class PaymentComponent implements OnInit {
  order = signal<Order | null>(null);
  processing = signal(false);
  paymentElementReady = signal(false);
  errorMessage = signal('');

  private stripe: Stripe | null = null;
  private elements: StripeElements | null = null;
  private paymentElement: StripePaymentElement | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('orderId');
    if (!orderId) {
      this.errorMessage.set('No se encontró el pedido que deseas pagar.');
      return;
    }

    this.orderService.getOrderById(orderId).subscribe({
      next: order => {
        if (order.status !== 'PENDING' && order.status !== 'pending') {
          this.errorMessage.set('Este pedido ya no está pendiente de pago.');
          return;
        }
        this.order.set(order);
        void this.initializePaymentElement();
      },
      error: () => this.errorMessage.set('No se pudo cargar el pedido.'),
    });
  }

  async confirmPayment(): Promise<void> {
    if (!this.stripe || !this.elements) return;
    this.processing.set(true);
    this.errorMessage.set('');

    try {
      const result = await this.stripe.confirmPayment({
        elements: this.elements,
        redirect: 'if_required',
      });
      if (result.error) {
        this.errorMessage.set(result.error.message ?? 'No se pudo procesar el pago.');
        this.processing.set(false);
        return;
      }
      this.errorMessage.set('Pago confirmado. Verificando el estado de la orden...');
      this.processing.set(false);
      this.waitForPaidOrder();
    } catch {
      this.errorMessage.set('Error al procesar el pago con Stripe.');
      this.processing.set(false);
    }
  }

  private waitForPaidOrder(): void {
    const orderId = this.order()?.id;
    if (!orderId) return;

    let attempts = 0;
    const checkStatus = () => {
      this.orderService.getOrderById(orderId).subscribe({
        next: order => {
          this.order.set(order);
          if (order.status === 'PAID') {
            void this.router.navigate(['/orders'], {
              queryParams: { payment: 'success' },
            });
            return;
          }
          if (attempts++ < 10) {
            setTimeout(checkStatus, 1000);
            return;
          }
          this.errorMessage.set(
            'El pago fue confirmado, pero la orden todavía está esperando el webhook de Stripe.',
          );
        },
        error: () => {
          if (attempts++ < 10) {
            setTimeout(checkStatus, 1000);
            return;
          }
          this.errorMessage.set('El pago fue confirmado, pero no se pudo actualizar la orden.');
        },
      });
    };

    checkStatus();
  }

  private async initializePaymentElement(): Promise<void> {
    try {
      this.stripe = await loadStripe(environment.stripePublicKey);
      const order = this.order();
      if (!this.stripe || !order) throw new Error('Stripe no está disponible');
      const intentRes = await firstValueFrom(this.orderService.createIntentPayment(order.id));
      if (!intentRes.clientSecret) throw new Error('No se recibió el client secret de Stripe');
      this.elements = this.stripe.elements({ clientSecret: intentRes.clientSecret });
      this.paymentElement = this.elements.create('payment');
      await new Promise<void>(resolve => setTimeout(resolve, 0));
      this.paymentElement.mount('#stripe-payment-element');
      this.paymentElementReady.set(true);
    } catch {
      this.errorMessage.set('No se pudo inicializar el pago con Stripe.');
    }
  }
}
