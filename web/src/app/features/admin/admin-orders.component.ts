import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/order.service';
import { Order, OrderStatus } from '../../core/models';
import { OrderDetailsDrawerComponent } from './order-details-drawer.component';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, OrderDetailsDrawerComponent],
  template: `
    <div class="flex h-screen overflow-hidden">
      <!-- Main Content -->
      <div class="flex-1 overflow-y-auto p-8">
        <div class="space-y-8">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-slate-800">Gestión de Pedidos</h2>
            <div class="flex gap-2">
              <span class="bg-slate-200 text-slate-700 px-3 py-1 rounded-full text-sm font-medium">
                Total Pedidos: {{ orders().length }}
              </span>
            </div>
          </div>

          <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <table class="w-full text-left border-collapse">
              <thead class="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600">ID Pedido</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600">Fecha</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600">Cliente</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-center">Items</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-right">Total</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-center">Estado</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200">
                @for (o of orders(); track o.id) {
                  <tr class="hover:bg-slate-50 transition-colors cursor-pointer" (click)="selectedOrder.set(o)">
                    <td class="px-6 py-4">
                      <span class="font-mono text-xs font-bold text-blue-600"
                        >#{{ o.id.substring(0, 8) }}</span
                      >
                    </td>
                    <td class="px-6 py-4 text-slate-600 text-sm">
                      {{ o.date | date: 'mediumDate' }}
                    </td>
                    <td class="px-6 py-4 text-slate-600 text-sm">
                      {{ o.userId }}
                    </td>
                    <td class="px-6 py-4 text-center text-slate-600 text-sm">
                      {{ o.items.length }}
                    </td>
                    <td class="px-6 py-4 text-right font-semibold text-slate-900">
                      {{ o.total | currency }}
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span
                        [class]="getStatusClass(o.status)"
                        class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider shadow-sm"
                      >
                        {{ o.status }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Drawer (Conditional display) -->
      @if (selectedOrder()) {
        <div class="w-96 border-l border-slate-200 bg-white overflow-y-auto">
          <app-order-details-drawer 
            [order]="selectedOrder()" 
            (close)="selectedOrder.set(null)"
            (statusChange)="updateStatus(selectedOrder()!.id, $event)"
          />
        </div>
      }
    </div>
  `,
})
export class AdminOrdersComponent {
  private orderService = inject(OrderService);
  orders = signal<Order[]>([]);
  selectedOrder = signal<Order | null>(null);

  constructor() {
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.getAllOrders().subscribe((os) => this.orders.set(os));
  }

  updateStatus(orderId: string, newStatus: OrderStatus) {
    this.orderService.updateOrderStatus(orderId, newStatus).subscribe(() => {
      this.loadOrders();
      // Update selected order in drawer if open
      const updatedOrder = this.orders().find(o => o.id === orderId);
      if (updatedOrder) {
        this.selectedOrder.set(updatedOrder);
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'pending':
        return 'bg-amber-100 text-amber-700';
      case 'Pagado':
        return 'bg-blue-100 text-blue-700';
      case 'En preparación':
        return 'bg-indigo-100 text-indigo-700';
      case 'Enviado':
        return 'bg-purple-100 text-purple-700';
      case 'Entregado':
        return 'bg-green-100 text-green-700';
      case 'cancelled':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-slate-100 text-slate-700';
    }
  }
}
