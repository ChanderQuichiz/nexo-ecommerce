import { computed, Injectable, signal } from '@angular/core';

export type OrderStatus = 'En preparación' | 'Preparado' | 'Enviado' | 'Entregado' | 'Cancelado';
export interface Order {
  id: string;
  customer: string;
  date: string;
  total: string;
  status: OrderStatus;
  issue?: string;
}

@Injectable({ providedIn: 'root' })
export class OrdersService {
  private readonly orderState = signal<Order[]>([
    {
      id: 'NEX-1048',
      customer: 'María González',
      date: '28 ago 2026',
      total: '$219.00',
      status: 'En preparación',
    },
    {
      id: 'NEX-0981',
      customer: 'María González',
      date: '16 ago 2026',
      total: '$164.40',
      status: 'Entregado',
    },
    {
      id: 'NEX-0912',
      customer: 'María González',
      date: '04 ago 2026',
      total: '$34.90',
      status: 'Entregado',
    },
  ]);
  readonly orders = this.orderState.asReadonly();

  forCustomer(customer: string) {
    return computed(() => this.orders().filter((order) => order.customer === customer));
  }
  updateStatus(id: string, status: OrderStatus): void {
    this.orderState.update((orders) =>
      orders.map((order) => (order.id === id ? { ...order, status } : order)),
    );
  }
  registerIssue(id: string, issue: string): void {
    this.orderState.update((orders) =>
      orders.map((order) =>
        order.id === id ? { ...order, issue: issue.trim() || 'Merma registrada' } : order,
      ),
    );
  }
  cancel(id: string): void {
    this.updateStatus(id, 'Cancelado');
  }
  create(customer: string, total: number): Order {
    const order = {
      id: `NEX-${1053 + this.orderState().length}`,
      customer,
      date: '03 sep 2026',
      total: `$${total.toFixed(2)}`,
      status: 'En preparación' as const,
    };
    this.orderState.update((orders) => [order, ...orders]);
    return order;
  }
}
