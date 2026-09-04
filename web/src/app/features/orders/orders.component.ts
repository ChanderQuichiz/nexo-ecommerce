import { Component, inject } from '@angular/core';
import { OrdersService } from '../../core/services/orders.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-orders',
  template: `<section class="inner-page">
    <p class="eyebrow">CUENTA / HISTORIAL</p>
    <h1>Mis pedidos</h1>
    <div class="orders-table">
      <div class="table-head">
        <span>Pedido</span><span>Fecha</span><span>Total</span><span>Estado</span>
      </div>
      @for (order of orders(); track order.id) {
        <div class="table-row">
          <strong>#{{ order.id }}</strong
          ><span>{{ order.date }}</span
          ><span>{{ order.total }}</span
          ><b class="status" [class.delivered]="order.status === 'Entregado'">{{ order.status }}</b
          ><button
            class="small-button"
            [disabled]="order.status !== 'En preparación'"
            (click)="cancel(order.id)"
          >
            Cancelar
          </button>
        </div>
      }
    </div>
  </section>`,
})
export class OrdersComponent {
  private readonly ordersService = inject(OrdersService);
  private readonly auth = inject(AuthService);
  protected readonly orders = this.ordersService.forCustomer(this.auth.user().name);
  protected cancel(id: string): void {
    this.ordersService.cancel(id);
  }
}
