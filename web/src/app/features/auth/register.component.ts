import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div
      class="min-h-[80vh] flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8"
    >
      <div
        class="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-xl border border-gray-100"
      >
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Crear Cuenta</h2>
          <p class="mt-2 text-center text-sm text-gray-600">
            ¿Ya tienes cuenta?
            <a
              routerLink="/login"
              class="font-medium text-indigo-600 hover:text-indigo-500 underline"
            >
              Inicia sesión aquí
            </a>
          </p>
        </div>

        <form class="mt-8 space-y-6" (ngSubmit)="onSubmit()">
          <div class="rounded-md shadow-sm -space-y-px">
            <div>
              <label for="name" class="sr-only">Nombre Completo</label>
              <input
                id="name"
                name="name"
                type="text"
                required
                [(ngModel)]="name"
                class="appearance-none rounded-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-lg focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Nombre Completo"
              />
            </div>
            <div>
              <label for="email-address" class="sr-only">Correo Electrónico</label>
              <input
                id="email-address"
                name="email"
                type="email"
                autocomplete="email"
                required
                [(ngModel)]="email"
                class="appearance-none rounded-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Correo Electrónico"
              />
            </div>
            <div>
              <label for="password" class="sr-only">Contraseña</label>
              <input
                id="password"
                name="password"
                type="password"
                required
                [(ngModel)]="password"
                class="appearance-none rounded-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Contraseña"
              />
            </div>
            <div>
              <label for="confirm-password" class="sr-only">Confirmar Contraseña</label>
              <input
                id="confirm-password"
                name="confirmPassword"
                type="password"
                required
                [(ngModel)]="confirmPassword"
                class="appearance-none rounded-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-lg focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Confirmar Contraseña"
              />
            </div>
          </div>

          @if (password && confirmPassword && password !== confirmPassword) {
            <p class="text-xs text-red-500 italic mt-1">Las contraseñas no coinciden</p>
          }

          <div>
            <button
              type="submit"
              [disabled]="loading || password !== confirmPassword"
              class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all shadow-lg shadow-indigo-100 disabled:opacity-50"
            >
              @if (loading) {
                <span
                  class="animate-spin mr-2 h-5 w-5 border-2 border-white border-t-transparent rounded-full"
                ></span>
                Registrando...
              } @else {
                Registrarse
              }
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  confirmPassword = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit(): void {
    if (!this.name || !this.email || !this.password || this.password !== this.confirmPassword)
      return;

    this.loading = true;

    this.authService.register(this.name, this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/catalog']);
      },
      error: () => {
        this.loading = false;
        // In a real app, show error
      },
    });
  }
}
