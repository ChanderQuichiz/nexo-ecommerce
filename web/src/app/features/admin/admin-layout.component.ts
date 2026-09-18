import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="fixed inset-0 flex bg-slate-100 font-sans z-50">
      <!-- Sidebar -->
      <aside class="w-64 bg-slate-900 text-slate-300 flex flex-col shadow-xl">
        <div class="p-6 border-b border-slate-800">
          <h1 class="text-2xl font-bold text-white flex items-center gap-2">
            <svg
              class="h-8 w-8 text-blue-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              />
            </svg>
            <span>E-Admin</span>
          </h1>
        </div>

        <nav class="flex-1 px-4 py-6 space-y-1">
          <a
            routerLink="/admin/products"
            routerLinkActive="bg-blue-600 text-white"
            class="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-slate-800 transition-colors"
          >
            <svg
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
              />
            </svg>
            <span>Productos</span>
          </a>
          <a
            routerLink="/admin/orders"
            routerLinkActive="bg-blue-600 text-white"
            class="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-slate-800 transition-colors"
          >
            <svg
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"
              />
            </svg>
            <span>Pedidos</span>
          </a>
        </nav>

        <div class="p-4 border-t border-slate-800">
          <p class="text-[10px] text-slate-500 uppercase font-bold text-center tracking-widest">
            v1.0.0 Refined
          </p>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 flex flex-col overflow-hidden">
        <!-- Header -->
        <header
          class="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8 shadow-sm relative z-30"
        >
          <h2 class="text-lg font-semibold text-slate-800">Panel de Administración</h2>

          <div class="relative">
            <button
              (click)="toggleDropdown()"
              class="flex items-center gap-4 hover:bg-slate-50 p-2 rounded-lg transition-colors focus:outline-none"
            >
              <div class="text-right hidden sm:block">
                <p class="text-sm font-medium text-slate-900 leading-none mb-1">
                  {{ currentUser()?.name }}
                </p>
                <p class="text-[10px] text-slate-500 uppercase tracking-wider font-bold">Admin</p>
              </div>
              <div
                class="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-bold shadow-inner border-2 border-white"
              >
                {{ currentUser()?.name?.charAt(0) }}
              </div>
            </button>

            <!-- Dropdown -->
            @if (isProfileOpen()) {
              <div
                class="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-2xl border border-slate-200 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200"
              >
                <div class="px-4 py-3 border-b border-slate-100 mb-1">
                  <p class="text-xs text-slate-400 font-bold uppercase tracking-tighter mb-1">
                    Sesión iniciada como
                  </p>
                  <p class="text-sm font-semibold text-slate-900 truncate">
                    {{ currentUser()?.email }}
                  </p>
                </div>

                <a
                  routerLink="/admin/products"
                  (click)="closeDropdown()"
                  class="flex items-center px-4 py-2 text-sm text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                >
                  <svg class="h-4 w-4 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
                    />
                  </svg>
                  Gestión Productos
                </a>

                <a
                  routerLink="/admin/orders"
                  (click)="closeDropdown()"
                  class="flex items-center px-4 py-2 text-sm text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                >
                  <svg class="h-4 w-4 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"
                    />
                  </svg>
                  Gestión Pedidos
                </a>

                <button
                  (click)="logout()"
                  class="w-full flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors border-t border-slate-100 mt-1"
                >
                  <svg class="h-4 w-4 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
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
            }

            <!-- Backdrop -->
            @if (isProfileOpen()) {
              <div (click)="closeDropdown()" class="fixed inset-0 z-40 bg-transparent"></div>
            }
          </div>
        </header>

        <!-- Content Area -->
        <section class="flex-1 overflow-y-auto p-8 bg-slate-50">
          <div class="max-w-7xl mx-auto">
            <router-outlet></router-outlet>
          </div>
        </section>
      </main>
    </div>
  `,
  styles: [],
})
export class AdminLayoutComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  currentUser = this.authService.currentUser;
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
