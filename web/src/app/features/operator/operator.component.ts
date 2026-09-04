import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrdersService } from '../../core/services/orders.service';

@Component({
  selector: 'app-operator',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">OPERADOR / PEDIDOS PENDIENTES</p>
    <h1>Centro de operaciones</h1>
    <div class="ops-grid">
      <div class="metric">
        <small>Por preparar</small><strong>{{ pending }}</strong
        ><span class="warning">bajo control</span>
      </div>
      <div class="metric">
        <small>Preparados y listos</small><strong>{{ ready }}</strong
        ><span class="positive">para despacho</span>
      </div>
      <div class="metric">
        <small>Incidencias</small><strong>{{ issuesCount }}</strong
        ><span>requieren atención</span>
      </div>
    </div>
    <div class="orders-table">
      <div class="table-head">
        <span>Pedido</span><span>Cliente</span><span>Estado</span><span>Incidencia</span
        ><span>Acción</span>
      </div>
      @for (order of orders(); track order.id) {
        <div class="table-row">
          <strong>#{{ order.id }}</strong
          ><span>{{ order.customer }}</span
          ><span class="status delivered">{{ order.status }}</span
          ><input
            class="issue-input"
            [(ngModel)]="issues[order.id]"
            placeholder="Merma / problema"
          /><button class="small-button" (click)="advance(order.id, order.status)">
            {{ action(order.status) }}
          </button>
        </div>
      }
    </div>
  </section>`,
})
export class OperatorComponent {
  protected readonly ordersService = inject(OrdersService);
  protected readonly orders = this.ordersService.orders;
  protected issues: Record<string, string> = {};
  protected get pending(): number {
    return this.orders().filter((order) => order.status === 'En preparación').length;
  }
  protected get ready(): number {
    return this.orders().filter(
      (order) => order.status === 'Preparado' || order.status === 'Enviado',
    ).length;
  }
  protected get issuesCount(): number {
    return this.orders().filter((order) => !!order.issue).length;
  }
  protected action(status: string): string {
    return status === 'En preparación'
      ? 'Preparar'
      : status === 'Preparado'
        ? 'Enviar'
        : status === 'Enviado'
          ? 'Entregar'
          : 'Listo';
  }
  protected advance(id: string, status: string): void {
    if (this.issues[id]) this.ordersService.registerIssue(id, this.issues[id]);
    const next =
      status === 'En preparación' ? 'Preparado' : status === 'Preparado' ? 'Enviado' : 'Entregado';
    if (status !== 'Entregado' && status !== 'Cancelado') this.ordersService.updateStatus(id, next);
  }
}
