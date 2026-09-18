import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../core/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-8 text-gray-800">Tu Carrito</h1>

      @if (cartService.items().length > 0) {
        <div class="flex flex-col lg:flex-row gap-8">
          <!-- Items List -->
          <div class="lg:w-2/3">
            <div class="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
              <ul class="divide-y divide-gray-100">
                @for (item of cartService.items(); track item.product.id) {
                  <li class="p-6 flex flex-col sm:flex-row items-center gap-6">
                    <img
                      [src]="item.product.imageUrl"
                      [alt]="item.product.name"
                      class="w-24 h-24 object-cover rounded-lg"
                    />

                    <div class="flex-grow text-center sm:text-left">
                      <h3 class="text-lg font-bold text-gray-800">{{ item.product.name }}</h3>
                      <p class="text-gray-500 text-sm">{{ item.product.category }}</p>
                      <div class="mt-2 text-indigo-600 font-semibold">
                        {{ item.product.price | currency }}
                      </div>
                    </div>

                    <div class="flex items-center gap-4">
                      <div
                        class="flex items-center border border-gray-200 rounded-lg overflow-hidden"
                      >
                        <button
                          (click)="updateQuantity(item.product.id, item.quantity - 1)"
                          class="px-3 py-1 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors"
                        >
                          -
                        </button>
                        <span
                          class="px-4 py-1 text-gray-800 font-medium min-w-[3rem] text-center"
                          >{{ item.quantity }}</span
                        >
                        <button
                          (click)="updateQuantity(item.product.id, item.quantity + 1)"
                          class="px-3 py-1 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors"
                          [disabled]="item.quantity >= item.product.stock"
                          [class.opacity-50]="item.quantity >= item.product.stock"
                        >
                          +
                        </button>
                      </div>

                      <button
                        (click)="removeItem(item.product.id)"
                        class="p-2 text-red-500 hover:bg-red-50 rounded-full transition-colors"
                        title="Eliminar item"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-6 w-6"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                          />
                        </svg>
                      </button>
                    </div>

                    <div class="text-right min-w-[100px]">
                      <div class="text-xs text-gray-400 uppercase font-bold mb-1">Subtotal</div>
                      <div class="text-lg font-bold text-gray-900">
                        {{ item.product.price * item.quantity | currency }}
                      </div>
                    </div>
                  </li>
                }
              </ul>
            </div>

            <div class="mt-6 flex justify-between items-center">
              <a
                routerLink="/catalog"
                class="text-indigo-600 hover:underline font-medium flex items-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 mr-1"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M10 19l-7-7m0 0l7-7m-7 7h18"
                  />
                </svg>
                Continuar comprando
              </a>
              <button
                (click)="clearCart()"
                class="text-gray-500 hover:text-red-500 text-sm font-medium"
              >
                Vaciar carrito
              </button>
            </div>
          </div>

          <!-- Summary -->
          <div class="lg:w-1/3">
            <div class="bg-white rounded-xl shadow-md border border-gray-100 p-8 sticky top-8">
              <h2 class="text-xl font-bold text-gray-800 mb-6">Resumen del pedido</h2>

              <div class="space-y-4 mb-6">
                <div class="flex justify-between text-gray-600">
                  <span>Productos ({{ cartService.totalItems() }})</span>
                  <span>{{ cartService.totalPrice() | currency }}</span>
                </div>
                <div class="flex justify-between text-gray-600">
                  <span>Envío</span>
                  <span class="text-green-600 font-medium">Gratis</span>
                </div>
                <div class="border-t border-gray-100 pt-4 mt-4 flex justify-between items-center">
                  <span class="text-xl font-bold text-gray-800">Total</span>
                  <span class="text-2xl font-black text-indigo-600">{{
                    cartService.totalPrice() | currency
                  }}</span>
                </div>
              </div>

              <a
                routerLink="/checkout"
                class="block w-full text-center bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 px-6 rounded-lg transition-colors shadow-lg shadow-indigo-200"
              >
                Finalizar Compra
              </a>

              <p class="text-center text-xs text-gray-400 mt-6">
                Impuestos incluidos. Pago seguro 100%.
              </p>
            </div>
          </div>
        </div>
      } @else {
        <div class="text-center py-24 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div
            class="bg-gray-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-12 w-12 text-gray-300"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-800 mb-2">Tu carrito está vacío</h2>
          <p class="text-gray-500 mb-8 max-w-xs mx-auto">
            Parece que aún no has añadido nada. ¡Explora nuestro catálogo y encuentra algo
            increíble!
          </p>
          <a
            routerLink="/catalog"
            class="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-8 rounded-lg transition-colors"
          >
            Ir al catálogo
          </a>
        </div>
      }
    </div>
  `,
})
export class CartComponent {
  constructor(public cartService: CartService) {}

  updateQuantity(productId: number, quantity: number): void {
    this.cartService.updateQuantity(productId, quantity);
  }

  removeItem(productId: number): void {
    this.cartService.removeFromCart(productId);
  }

  clearCart(): void {
    this.cartService.clearCart();
  }
}
