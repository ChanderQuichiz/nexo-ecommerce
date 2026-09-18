import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../core/product.service';
import { CartService } from '../../core/cart.service';
import { Product } from '../../core/models';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-8 text-gray-800">Catálogo de Productos</h1>

      @if (products$ | async; as products) {
        @if (products.length > 0) {
          <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            @for (product of products; track product.id) {
              <div
                class="bg-white rounded-lg shadow-md overflow-hidden flex flex-col border border-gray-100 transition-transform hover:-translate-y-1"
              >
                <a
                  [routerLink]="['/product', product.id]"
                  class="block h-48 overflow-hidden bg-gray-200"
                >
                  <img
                    [src]="product.imageUrl"
                    [alt]="product.name"
                    class="w-full h-full object-cover"
                  />
                </a>
                <div class="p-4 flex-grow flex flex-col">
                  <h2 class="text-lg font-semibold mb-2 text-gray-800">{{ product.name }}</h2>
                  <p class="text-gray-600 text-sm mb-4 line-clamp-2 flex-grow">
                    {{ product.description }}
                  </p>
                  <div class="flex items-center justify-between mb-4">
                    <span class="text-xl font-bold text-indigo-600">{{
                      product.price | currency
                    }}</span>
                    @if (product.stock === 0) {
                      <span class="text-xs font-semibold px-2 py-1 bg-red-100 text-red-600 rounded"
                        >Agotado</span
                      >
                    } @else {
                      <span
                        class="text-xs font-semibold px-2 py-1 bg-green-100 text-green-600 rounded"
                        >Stock: {{ product.stock }}</span
                      >
                    }
                  </div>
                  <button
                    (click)="addToCart(product)"
                    [disabled]="product.stock === 0"
                    class="w-full py-2 px-4 rounded font-medium transition-colors"
                    [class.bg-indigo-600]="product.stock > 0"
                    [class.hover:bg-indigo-700]="product.stock > 0"
                    [class.text-white]="product.stock > 0"
                    [class.bg-gray-200]="product.stock === 0"
                    [class.text-gray-500]="product.stock === 0"
                    [class.cursor-not-allowed]="product.stock === 0"
                  >
                    Añadir al carrito
                  </button>
                </div>
              </div>
            }
          </div>
        } @else {
          <div
            class="text-center py-20 bg-gray-50 rounded-xl border-2 border-dashed border-gray-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-16 w-16 mx-auto text-gray-400 mb-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
              />
            </svg>
            <h2 class="text-xl font-medium text-gray-600">El catálogo está vacío</h2>
            <p class="text-gray-400 mt-2">Vuelve pronto para ver nuestras novedades.</p>
          </div>
        }
      } @else {
        <div class="flex justify-center items-center h-64">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      }
    </div>
  `,
})
export class CatalogComponent implements OnInit {
  products$!: Observable<Product[]>;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
  ) {}

  ngOnInit(): void {
    this.products$ = this.productService.getAvailableProducts();
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
  }
}
