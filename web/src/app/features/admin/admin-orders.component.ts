import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/order.service';
import { Order, OrderStatus } from '../../core/models';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
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
              <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-right">Total</th>
              <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-center">Estado</th>
              <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-right">Gestión</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-200">
            @for (o of orders(); track o.id) {
              <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4">
                  <span class="font-mono text-xs font-bold text-blue-600"
                    >#{{ o.id.substring(0, 8) }}</span
                  >
                </td>
                <td class="px-6 py-4 text-slate-600 text-sm">
                  {{ o.date | date: 'medium' }}
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
                <td class="px-6 py-4 text-right min-w-[200px]">
                  <select
                    [ngModel]="o.status"
                    (ngModelChange)="updateStatus(o.id, $event)"
                    class="bg-slate-50 border border-slate-300 text-slate-700 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 cursor-pointer hover:bg-white transition-colors"
                  >
                    <option value="pending">Pendiente</option>
                    <option value="Pagado">Pagado</option>
                    <option value="En preparación">En preparación</option>
                    <option value="Enviado">Enviado</option>
                    <option value="Entregado">Entregado</option>
                    <option value="cancelled">Cancelado</option>
                  </select>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
})
export class AdminOrdersComponent {
  private orderService = inject(OrderService);
  orders = signal<Order[]>([]);

  constructor() {
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.getAllOrders().subscribe((os) => this.orders.set(os));
  }

  updateStatus(orderId: string, newStatus: string) {
    this.orderService.updateOrderStatus(orderId, newStatus as OrderStatus).subscribe(() => {
      this.loadOrders();
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
