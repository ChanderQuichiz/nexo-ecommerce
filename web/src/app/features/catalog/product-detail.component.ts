import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../core/product.service';
import { CartService } from '../../core/cart.service';
import { Product } from '../../core/models';
import { Observable, switchMap } from 'rxjs';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      @if (product$ | async; as product) {
        <nav class="mb-8 text-sm">
          <a routerLink="/catalog" class="text-indigo-600 hover:text-indigo-800">Catálogo</a>
          <span class="mx-2 text-gray-400">/</span>
          <span class="text-gray-600">{{ product.name }}</span>
        </nav>

        <div class="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
          <div class="md:flex">
            <div class="md:w-1/2 lg:w-2/5">
              <img
                [src]="product.imageUrl"
                [alt]="product.name"
                class="w-full h-96 md:h-full object-cover"
              />
            </div>
            <div class="p-8 md:w-1/2 lg:w-3/5">
              <div class="uppercase tracking-wide text-sm text-indigo-500 font-semibold mb-1">
                {{ product.category }}
              </div>
              <h1 class="text-3xl font-bold text-gray-900 mb-4">{{ product.name }}</h1>
              <p class="text-gray-600 text-lg mb-6 leading-relaxed">{{ product.description }}</p>

              <div class="flex items-center mb-8">
                <span class="text-3xl font-bold text-gray-900 mr-4">{{
                  product.price | currency
                }}</span>
                @if (product.stock > 0) {
                  <span
                    class="bg-green-100 text-green-800 text-xs px-2.5 py-0.5 rounded-full font-medium"
                  >
                    {{ product.stock }} en stock
                  </span>
                } @else {
                  <span
                    class="bg-red-100 text-red-800 text-xs px-2.5 py-0.5 rounded-full font-medium"
                  >
                    Agotado
                  </span>
                }
              </div>

              <div class="flex flex-col space-y-4">
                <button
                  (click)="addToCart(product)"
                  [disabled]="product.stock === 0"
                  class="flex items-center justify-center w-full py-4 px-6 rounded-lg font-bold text-lg transition-all"
                  [class.bg-indigo-600]="product.stock > 0"
                  [class.hover:bg-indigo-700]="product.stock > 0"
                  [class.text-white]="product.stock > 0"
                  [class.bg-gray-200]="product.stock === 0"
                  [class.text-gray-500]="product.stock === 0"
                  [class.cursor-not-allowed]="product.stock === 0"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-6 w-6 mr-2"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                    />
                  </svg>
                  Añadir al carrito
                </button>

                <a
                  routerLink="/catalog"
                  class="text-center text-gray-500 hover:text-gray-700 font-medium py-2"
                >
                  Seguir comprando
                </a>
              </div>
            </div>
          </div>
        </div>
      } @else {
        <div class="flex justify-center items-center h-96">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      }
    </div>
  `,
})
export class ProductDetailComponent implements OnInit {
  product$!: Observable<Product | undefined>;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
  ) {}

  ngOnInit(): void {
    this.product$ = this.route.paramMap.pipe(
      switchMap((params) => {
        const id = Number(params.get('id'));
        return this.productService.getProductById(id);
      }),
    );
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
  }
}
