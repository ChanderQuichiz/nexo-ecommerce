import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth.service';
import { CartService } from '../../core/cart.service';

@Component({
  selector: 'app-store-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="bg-white border-b border-gray-200 sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <a routerLink="/" class="flex-shrink-0 flex items-center group">
              <span
                class="text-2xl font-extrabold text-indigo-600 tracking-tight group-hover:text-indigo-700 transition-colors"
                >E-Commerce</span
              >
            </a>
            <div class="hidden sm:ml-8 sm:flex sm:space-x-8">
              <a
                routerLink="/catalog"
                routerLinkActive="border-indigo-500 text-gray-900"
                class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
              >
                Catálogo
              </a>
            </div>
          </div>

          <div class="flex items-center">
            <!-- Cart -->
            <a
              routerLink="/cart"
              class="relative p-2 text-gray-400 hover:text-gray-500 mr-2 transition-colors"
            >
              <span class="sr-only">View cart</span>
              <svg
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007ZM8.625 10.5a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm7.5 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z"
                />
              </svg>
              <span
                *ngIf="cartItemCount() > 0"
                class="absolute top-1 right-1 block h-5 w-5 rounded-full bg-indigo-600 text-white text-[10px] font-bold flex items-center justify-center transform translate-x-1/2 -translate-y-1/2 shadow-sm"
              >
                {{ cartItemCount() }}
              </span>
            </a>

            <!-- Auth Section -->
            <div class="flex items-center space-x-4 border-l border-gray-200 pl-4 ml-2">
              <ng-container *ngIf="currentUser() as user; else guest">
                <div class="relative">
                  <button
                    (click)="toggleDropdown()"
                    class="flex items-center space-x-2 text-sm font-medium text-gray-700 hover:text-indigo-600 transition-colors focus:outline-none"
                  >
                    <div
                      class="w-8 h-8 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold"
                    >
                      {{ user.name.charAt(0) }}
                    </div>
                    <span class="hidden md:inline">{{ user.name }}</span>
                    <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M19 9l-7 7-7-7"
                      />
                    </svg>
                  </button>

                  <!-- Dropdown Menu -->
                  <div
                    *ngIf="isProfileOpen()"
                    class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-xl border border-gray-100 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200"
                  >
                    <div class="px-4 py-2 border-b border-gray-50 mb-1">
                      <p class="text-xs text-gray-400 uppercase font-bold">Mi Cuenta</p>
                      <p class="text-sm font-medium text-gray-900 truncate">{{ user.email }}</p>
                    </div>

                    <a
                      routerLink="/orders"
                      (click)="closeDropdown()"
                      class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors"
                    >
                      <svg
                        class="h-4 w-4 mr-3"
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
                      Mis Pedidos
                    </a>

                    <button
                      (click)="logout()"
                      class="w-full flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors border-t border-gray-50 mt-1"
                    >
                      <svg
                        class="h-4 w-4 mr-3"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                        />
                      </svg>
                      Cerrar Sesión
                    </button>
                  </div>

                  <!-- Dropdown Backdrop -->
                  <div
                    *ngIf="isProfileOpen()"
                    (click)="closeDropdown()"
                    class="fixed inset-0 z-40 bg-transparent"
                  ></div>
                </div>
              </ng-container>

              <ng-template #guest>
                <div class="flex items-center space-x-4">
                  <a
                    routerLink="/login"
                    class="text-sm font-medium text-gray-700 hover:text-indigo-600 transition-colors"
                    >Login</a
                  >
                  <a
                    routerLink="/register"
                    class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all"
                  >
                    Register
                  </a>
                </div>
              </ng-template>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <router-outlet />
    </main>
  `,
})
export class StoreLayoutComponent {
  private authService = inject(AuthService);
  private cartService = inject(CartService);
  private router = inject(Router);

  currentUser = this.authService.currentUser;
  cartItemCount = this.cartService.totalItems;
  isProfileOpen = signal(false);

  toggleDropdown() {
    this.isProfileOpen.update((v) => !v);
  }

  closeDropdown() {
    this.isProfileOpen.set(false);
  }

  logout() {
    this.closeDropdown();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
