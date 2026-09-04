import { Component, inject } from '@angular/core';
import { OrdersService } from '../../core/services/orders.service';

@Component({
  selector: 'app-order-management',
  template: `<section class="inner-page">
    <p class="eyebrow">ADMINISTRACIÓN / PEDIDOS</p>
    <h1>Supervisión global</h1>
    <div class="orders-table">
      <div class="table-head">
        <span>Pedido</span><span>Cliente</span><span>Total</span><span>Estado</span
        ><span>Acción</span>
      </div>
      @for (order of orders.orders(); track order.id) {
        <div class="table-row">
          <strong>#{{ order.id }}</strong
          ><span>{{ order.customer }}</span
          ><span>{{ order.total }}</span
          ><span class="status" [class.delivered]="order.status === 'Entregado'">{{
            order.status
          }}</span
          ><button
            class="small-button"
            [disabled]="order.status === 'Entregado' || order.status === 'Cancelado'"
            (click)="orders.cancel(order.id)"
          >
            Cancelar
          </button>
        </div>
      }
    </div>
  </section>`,
})
export class OrderManagementComponent {
  protected readonly orders = inject(OrdersService);
}
