import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../core/order.service';
import { Order } from '../../core/models';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-5xl">
      <h1 class="text-3xl font-bold mb-8 text-gray-800">Mis Pedidos</h1>

      @if (orders().length > 0) {
        <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="bg-gray-50 text-gray-600 uppercase text-xs font-bold">
                  <th class="px-6 py-4">ID Pedido</th>
                  <th class="px-6 py-4">Fecha</th>
                  <th class="px-6 py-4">Estado</th>
                  <th class="px-6 py-4">Total</th>
                  <th class="px-6 py-4 text-center">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                @for (order of orders(); track order.id) {
                  <tr class="hover:bg-gray-50 transition-colors">
                    <td class="px-6 py-4 font-mono text-sm text-indigo-600 font-semibold">
                      #{{ order.id }}
                    </td>
                    <td class="px-6 py-4 text-gray-600">{{ order.date | date: 'mediumDate' }}</td>
                    <td class="px-6 py-4">
                      <span
                        [class]="getStatusClass(order.status)"
                        class="px-2 py-1 rounded-full text-xs font-bold uppercase"
                      >
                        {{ order.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 font-bold text-gray-800">{{ order.total | currency }}</td>
                    <td class="px-6 py-4 text-center">
                      <button
                        (click)="viewDetails(order)"
                        class="bg-indigo-50 text-indigo-600 px-4 py-2 rounded-lg hover:bg-indigo-100 transition-colors font-medium text-sm"
                      >
                        Ver Detalles
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      } @else {
        <div class="text-center py-20 bg-white rounded-xl shadow-sm border border-gray-100">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-16 w-16 mx-auto text-gray-300 mb-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
            />
          </svg>
          <p class="text-gray-500 mb-6 text-lg">Aún no has realizado ningún pedido.</p>
          <a
            routerLink="/catalog"
            class="inline-block bg-indigo-600 text-white px-8 py-3 rounded-lg font-bold hover:bg-indigo-700 transition-all"
          >
            Explorar Productos
          </a>
        </div>
      }

      <!-- Order Details Modal -->
      @if (selectedOrder()) {
        <div
          class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50 animate-fade-in"
        >
          <div
            class="bg-white rounded-2xl max-w-2xl w-full p-8 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]"
          >
            <div class="flex justify-between items-start mb-6 shrink-0">
              <div>
                <h2 class="text-2xl font-bold text-gray-800">Detalles del Pedido</h2>
                <p class="text-indigo-500 font-mono font-semibold">#{{ selectedOrder()?.id }}</p>
              </div>
              <button
                (click)="closeDetails()"
                class="text-gray-400 hover:text-gray-600 transition-colors p-2"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>

            <div class="space-y-4 mb-6 overflow-y-auto pr-2">
              <div class="bg-gray-50 p-4 rounded-xl mb-4 text-sm">
                <h3 class="font-bold text-gray-700 mb-2 uppercase text-xs">Información de Envío</h3>
                <p class="text-gray-600">{{ selectedOrder()?.shippingAddress?.address }}</p>
                <p class="text-gray-600">{{ selectedOrder()?.shippingAddress?.city }}</p>
                <p class="text-gray-600">Tel: {{ selectedOrder()?.shippingAddress?.phone }}</p>
              </div>

              <h3 class="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">
                Artículos
              </h3>
              @for (item of selectedOrder()?.items; track item.product.id) {
                <div class="flex items-center gap-4 py-3 border-b border-gray-50">
                  <img
                    [src]="item.product.imageUrl"
                    [alt]="item.product.name"
                    class="w-14 h-14 object-cover rounded-lg bg-gray-100"
                  />
                  <div class="flex-grow">
                    <p class="font-semibold text-gray-800">{{ item.product.name }}</p>
                    <p class="text-xs text-gray-500">
                      {{ item.quantity }} unidades x {{ item.product.price | currency }}
                    </p>
                  </div>
                  <span class="font-bold text-gray-800">{{
                    item.product.price * item.quantity | currency
                  }}</span>
                </div>
              }
            </div>

            <div
              class="bg-indigo-50 p-6 rounded-xl flex justify-between items-center mt-auto shrink-0"
            >
              <div>
                <span class="text-indigo-600 text-xs font-bold uppercase block mb-1"
                  >Total Pagado</span
                >
                <span class="text-3xl font-black text-indigo-700">{{
                  selectedOrder()?.total | currency
                }}</span>
              </div>
              <div class="text-right">
                <span class="text-gray-500 text-xs block mb-1">Estado</span>
                <span
                  [class]="getStatusClass(selectedOrder()?.status || '')"
                  class="px-3 py-1 rounded-full text-xs font-bold uppercase"
                >
                  {{ selectedOrder()?.status }}
                </span>
              </div>
            </div>

            <button
              (click)="closeDetails()"
              class="w-full mt-8 bg-gray-900 text-white py-3 rounded-lg font-bold hover:bg-black transition-colors shrink-0"
            >
              Cerrar
            </button>
          </div>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .animate-fade-in {
        animation: fadeIn 0.2s ease-out;
      }
      @keyframes fadeIn {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }
    `,
  ],
})
export class OrderHistoryComponent implements OnInit {
  orders = signal<Order[]>([]);
  selectedOrder = signal<Order | null>(null);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getUserOrders().subscribe((orders) => {
      this.orders.set(orders);
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'completed':
        return 'bg-green-100 text-green-700';
      case 'pending':
        return 'bg-yellow-100 text-yellow-700';
      case 'cancelled':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  }

  viewDetails(order: Order): void {
    this.selectedOrder.set(order);
  }

  closeDetails(): void {
    this.selectedOrder.set(null);
  }
}
