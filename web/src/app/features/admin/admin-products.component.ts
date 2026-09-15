import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/product.service';
import { Product } from '../../core/models';
import { ProductDetailsDrawerComponent } from './product-details-drawer.component';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductDetailsDrawerComponent],
  template: `
    <div class="flex h-screen overflow-hidden">
      <!-- Main Content -->
      <div class="flex-1 overflow-y-auto p-8">
        <div class="space-y-8">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-slate-800">Gestión de Productos</h2>
            <button
              (click)="showAddForm.set(!showAddForm())"
              class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
            >
              {{ showAddForm() ? 'Cerrar' : 'Nuevo Producto' }}
            </button>
          </div>

          <!-- New Product Form -->
          @if (showAddForm()) {
            <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
              <h3 class="text-lg font-semibold mb-4 text-slate-700">Añadir Nuevo Producto</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div class="space-y-4">
                  <input
                    [(ngModel)]="newProduct.name"
                    placeholder="Nombre del producto"
                    class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                  />
                  <div class="grid grid-cols-2 gap-4">
                    <input
                      [(ngModel)]="newProduct.price"
                      type="number"
                      placeholder="Precio"
                      class="px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                    />
                    <input
                      [(ngModel)]="newProduct.stock"
                      type="number"
                      placeholder="Stock"
                      class="px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                    />
                  </div>
                </div>

                <div class="space-y-4">
                  <label class="block text-sm font-medium text-slate-600"
                    >Imagen del Producto</label
                  >
                  <div class="flex items-center justify-center w-full">
                    <label
                      class="flex flex-col items-center justify-center w-full h-32 border-2 border-slate-300 border-dashed rounded-lg cursor-pointer bg-slate-50 hover:bg-slate-100 transition-colors"
                    >
                      <div class="flex flex-col items-center justify-center pt-5 pb-6">
                        <p class="mb-2 text-sm text-slate-500">
                          <span class="font-semibold">Click para subir</span>
                        </p>
                      </div>
                      <input
                        type="file"
                        (change)="onFileSelected($event)"
                        class="hidden"
                        accept="image/*"
                      />
                    </label>
                  </div>
                </div>

                <div
                  class="flex flex-col items-center justify-center bg-slate-50 rounded-lg border border-slate-200 p-2"
                >
                  @if (imagePreview()) {
                    <img
                      [src]="imagePreview()"
                      class="h-24 w-24 object-cover rounded-md shadow-sm mb-2"
                    />
                  } @else {
                    <div
                      class="h-24 w-24 bg-slate-200 rounded-md flex items-center justify-center text-slate-400 text-xs"
                    >
                      Sin vista previa
                    </div>
                  }
                </div>

                <button
                  (click)="addProduct()"
                  [disabled]="!newProduct.name || newProduct.price <= 0 || loading()"
                  class="md:col-span-2 lg:col-span-3 bg-blue-600 text-white py-3 rounded-lg font-bold hover:bg-blue-700 transition-colors shadow-lg disabled:opacity-50"
                >
                  Guardar y Publicar Producto
                </button>
              </div>
            </div>
          }

          <!-- Product List -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <table class="w-full text-left border-collapse">
              <thead class="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600">Producto</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600">Categoría</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-right">Precio</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-right">Stock</th>
                  <th class="px-6 py-4 text-sm font-semibold text-slate-600 text-center">Estado</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200">
                @for (p of products(); track p.id) {
                  <tr class="hover:bg-slate-50 transition-colors cursor-pointer" (click)="selectedProduct.set(p)">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <img [src]="p.imageUrl" [alt]="p.name" class="w-10 h-10 rounded object-cover border border-slate-200" />
                        <span class="font-medium text-slate-900">{{ p.name }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-slate-600 text-sm">{{ p.category }}</td>
                    <td class="px-6 py-4 text-right">{{ p.price | currency }}</td>
                    <td class="px-6 py-4 text-right">{{ p.stock }}</td>
                    <td class="px-6 py-4 text-center">
                      <span [class]="p.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'" class="px-2 py-1 rounded-full text-xs font-bold uppercase tracking-wider">
                        {{ p.active ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Drawer -->
      @if (selectedProduct()) {
        <div class="w-96 border-l border-slate-200 bg-white overflow-y-auto">
          <app-product-details-drawer 
            [product]="selectedProduct()"
            (close)="selectedProduct.set(null)"
            (save)="handleSave($event)"
          />
        </div>
      }
    </div>
  `,
})
export class AdminProductsComponent {
  private productService = inject(ProductService);
  products = signal<Product[]>([]);
  showAddForm = signal(false);
  selectedProduct = signal<Product | null>(null);
  
  loading = signal(false);
  imagePreview = signal<string | null>(null);
  selectedFile: File | null = null;

  newProduct = {
    name: '',
    description: '',
    price: 0,
    stock: 0,
    imageUrl: '',
    category: 'General',
    active: true,
  };

  constructor() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe((prods) => this.products.set(prods));
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview.set(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  }

  addProduct() {
    if (!this.newProduct.name || this.newProduct.price <= 0) return;

    this.loading.set(true);
    this.productService
      .addProduct(this.newProduct, this.selectedFile || undefined)
      .subscribe(() => {
        this.loadProducts();
        this.resetForm();
        this.loading.set(false);
        this.showAddForm.set(false);
      });
  }

  private resetForm() {
    this.newProduct = {
      name: '',
      description: '',
      price: 0,
      stock: 0,
      imageUrl: '',
      category: 'General',
      active: true,
    };
    this.selectedFile = null;
    this.imagePreview.set(null);
  }

  handleSave(product: Product) {
    this.productService.updateProduct(product).subscribe(() => {
      this.loadProducts();
      this.selectedProduct.set(null);
    });
  }
}
