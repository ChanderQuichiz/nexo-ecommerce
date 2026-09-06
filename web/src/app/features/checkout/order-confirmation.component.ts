import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Order } from '../../core/models';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-16 max-w-2xl text-center">
      <div class="bg-white p-8 rounded-2xl shadow-xl border border-gray-100 animate-fade-in">
        <div
          class="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-10 w-10"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>

        <h1 class="text-3xl font-bold text-gray-800 mb-2">¡Pedido Confirmado!</h1>
        <p class="text-gray-600 mb-8">Gracias por tu compra. Tu pedido está en camino.</p>

        @if (order) {
          <div class="bg-gray-50 rounded-xl p-6 text-left mb-8">
            <div class="flex justify-between mb-4 border-b border-gray-200 pb-2">
              <span class="text-gray-500">Orden #:</span>
              <span class="font-bold text-gray-800">{{ order.id }}</span>
            </div>
            <div class="flex justify-between mb-4 border-b border-gray-200 pb-2">
              <span class="text-gray-500">Fecha:</span>
              <span class="font-medium">{{ order.date | date: 'short' }}</span>
            </div>

            <div class="mt-4">
              <h3 class="font-semibold mb-2">Productos:</h3>
              <ul class="space-y-2">
                @for (item of order.items; track item.product.id) {
                  <li class="flex justify-between text-sm">
                    <span>{{ item.quantity }}x {{ item.product.name }}</span>
                    <span>{{ item.product.price * item.quantity | currency }}</span>
                  </li>
                }
              </ul>
            </div>

            <div
              class="mt-4 pt-4 border-t border-gray-200 flex justify-between font-bold text-lg text-indigo-700"
            >
              <span>Total Pagado:</span>
              <span>{{ order.total | currency }}</span>
            </div>
          </div>
        }

        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button
            (click)="downloadPdf()"
            class="flex-1 bg-white border-2 border-indigo-600 text-indigo-600 py-3 px-6 rounded-lg font-bold hover:bg-indigo-50 transition-colors flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-2"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
              />
            </svg>
            Descargar PDF
          </button>
          <a
            routerLink="/catalog"
            class="flex-1 bg-indigo-600 text-white py-3 px-6 rounded-lg font-bold hover:bg-indigo-700 transition-colors flex items-center justify-center"
          >
            Seguir Comprando
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .animate-fade-in {
        animation: fadeIn 0.5s ease-out;
      }
      @keyframes fadeIn {
        from {
          opacity: 0;
          transform: scale(0.95);
        }
        to {
          opacity: 1;
          transform: scale(1);
        }
      }
    `,
  ],
})
export class OrderConfirmationComponent implements OnInit {
  order?: Order;

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    this.order = navigation?.extras?.state?.['order'];
  }

  ngOnInit(): void {
    if (!this.order) {
      // Could redirect to catalog if accessed directly
    }
  }

  downloadPdf(): void {
    alert('Simulando descarga de PDF para la orden ' + this.order?.id);
  }
}
