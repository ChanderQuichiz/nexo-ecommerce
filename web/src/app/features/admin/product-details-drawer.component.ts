import { Component, EventEmitter, Input, Output, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../core/models';

@Component({
  selector: 'app-product-details-drawer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (product) {
      <div class="p-6 h-full flex flex-col">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-slate-800">Editar Producto</h3>
          <button (click)="close.emit()" class="text-slate-500 hover:text-slate-700 font-bold">✕</button>
        </div>
        
        <div class="space-y-6 flex-1">
          <div>
            <label class="block text-sm font-medium text-slate-700">Nombre</label>
            <input [(ngModel)]="bufferedProduct.name" class="mt-1 block w-full border border-slate-300 rounded-md p-2" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-slate-700">Precio</label>
            <input type="number" [(ngModel)]="bufferedProduct.price" class="mt-1 block w-full border border-slate-300 rounded-md p-2" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700">Stock</label>
            <input type="number" [(ngModel)]="bufferedProduct.stock" class="mt-1 block w-full border border-slate-300 rounded-md p-2" />
          </div>
          
          @if (isDirty()) {
            <div class="flex gap-2 pt-4">
              <button (click)="onConfirm()" class="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700">Guardar Cambios</button>
              <button (click)="onCancel()" class="bg-slate-200 text-slate-700 px-4 py-2 rounded text-sm hover:bg-slate-300">Cancelar</button>
            </div>
          }
        </div>
      </div>
    }
  `
})
export class ProductDetailsDrawerComponent implements OnChanges {
  @Input() product: Product | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<Product>();

  bufferedProduct: Product = {} as Product;

  ngOnChanges() {
    if (this.product) {
      this.bufferedProduct = { ...this.product };
    }
  }

  isDirty(): boolean {
    return JSON.stringify(this.product) !== JSON.stringify(this.bufferedProduct);
  }

  onConfirm() {
    this.save.emit(this.bufferedProduct);
  }

  onCancel() {
    if (this.product) {
      this.bufferedProduct = { ...this.product };
    }
  }
}
