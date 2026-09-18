import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Order, OrderStatus } from '../../core/models';

@Component({
  selector: 'app-order-details-drawer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (order) {
      <div class="p-6 h-full flex flex-col">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-slate-800">Pedido #{{ order.id.substring(0, 8) }}</h3>
          <button (click)="close.emit()" class="text-slate-500 hover:text-slate-700 font-bold">✕</button>
        </div>
        
        <div class="space-y-6 flex-1">
          <div class="bg-slate-50 p-4 rounded-lg">
            <h4 class="font-semibold text-slate-800 mb-2">Estado del pedido</h4>
            <select
              [(ngModel)]="bufferedStatus"
              class="bg-white border border-slate-300 text-slate-700 text-sm rounded-lg w-full p-2.5"
            >
              <option value="pending">Pendiente</option>
              <option value="Pagado">Pagado</option>
              <option value="En preparación">En preparación</option>
              <option value="Enviado">Enviado</option>
              <option value="Entregado">Entregado</option>
              <option value="cancelled">Cancelado</option>
            </select>
            
            @if (bufferedStatus !== order.status) {
              <div class="flex gap-2 mt-3">
                <button (click)="onConfirm()" class="bg-blue-600 text-white px-3 py-1.5 rounded text-sm hover:bg-blue-700">Confirmar</button>
                <button (click)="onCancel()" class="bg-slate-200 text-slate-700 px-3 py-1.5 rounded text-sm hover:bg-slate-300">Cancelar</button>
              </div>
            }
          </div>

          <div>
            <h4 class="font-semibold text-slate-800 border-b pb-2">Dirección de envío</h4>
            <p class="text-sm text-slate-600 mt-2">{{ order.shippingAddress?.address }}</p>
            <p class="text-sm text-slate-600">{{ order.shippingAddress?.city }}</p>
            <p class="text-sm text-slate-600">Tel: {{ order.shippingAddress?.phone }}</p>
          </div>
          
          <div>
            <h4 class="font-semibold text-slate-800 border-b pb-2">Productos</h4>
            <ul class="text-sm text-slate-600 mt-2 space-y-2">
              @for (item of order.items; track item.product.id) {
                <li class="flex justify-between">
                  <span>{{ item.product.name }} (x{{ item.quantity }})</span>
                  <span>{{ (item.product.price * item.quantity) | currency }}</span>
                </li>
              }
            </ul>
          </div>
        </div>
      </div>
    }
  `
})
export class OrderDetailsDrawerComponent {
  @Input() order: Order | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() statusChange = new EventEmitter<OrderStatus>();

  bufferedStatus: OrderStatus | null = null;

  ngOnChanges() {
    this.bufferedStatus = this.order?.status || null;
  }

  onConfirm() {
    if (this.bufferedStatus) {
      this.statusChange.emit(this.bufferedStatus);
    }
  }

  onCancel() {
    this.bufferedStatus = this.order?.status || null;
  }
}
