import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CatalogService } from '../../core/services/catalog.service';

@Component({
  selector: 'app-inventory',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">OPERADOR / INVENTARIO</p>
    <h1>Gestión de stock</h1>
    <div class="orders-table">
      <div class="table-head">
        <span>Producto</span><span>Stock actual</span><span>Ajuste</span><span>Resultado</span
        ><span>Acción</span>
      </div>
      @for (product of catalog.products(); track product.id) {
        <div class="table-row">
          <strong>{{ product.name }}</strong
          ><span [class.warning]="product.stock < 8">{{ product.stock }} unidades</span
          ><input
            class="stock-input"
            type="number"
            [(ngModel)]="adjustments[product.id]"
            min="-{{ product.stock }}"
          /><span>Manual</span
          ><button class="small-button" (click)="adjust(product.id)">Actualizar</button>
        </div>
      }
    </div>
    @if (message) {
      <p class="form-message">{{ message }}</p>
    }
  </section>`,
})
export class InventoryComponent {
  protected readonly catalog = inject(CatalogService);
  protected adjustments: Record<number, number> = {};
  protected message = '';
  protected adjust(id: number): void {
    const amount = Number(this.adjustments[id] || 0);
    this.message = this.catalog.adjustStock(id, amount)
      ? 'Stock actualizado correctamente.'
      : 'El ajuste no puede dejar stock negativo.';
    this.adjustments[id] = 0;
  }
}
