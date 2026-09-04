import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  template: `<main class="auth-page">
    <section class="auth-card">
      <a class="brand" routerLink="/shop"
        ><span class="brand-mark">N</span>nexo<span class="brand-dot">.</span></a
      >
      <p class="eyebrow">NUEVA CUENTA</p>
      <h1>Crear cuenta</h1>
      <p class="auth-copy">Completa tus datos para comprar en nexo.</p>
      <label
        >Nombre completo<input [(ngModel)]="name" name="name" placeholder="María González" /></label
      ><label
        >Correo electrónico<input
          autocomplete="email"
          [(ngModel)]="email"
          name="email"
          type="email"
          placeholder="tu@email.com" /></label
      ><label
        >Contraseña<input
          autocomplete="new-password"
          [(ngModel)]="password"
          name="password"
          type="password"
          placeholder="Mínimo 8 caracteres" /></label
      ><button class="primary-button" (click)="register()">Crear mi cuenta</button>
      @if (message) {
        <p class="form-message">{{ message }}</p>
      }
      <p class="auth-footer">¿Ya tienes cuenta? <a routerLink="/login">Ingresar</a></p>
    </section>
  </main>`,
})
export class RegisterComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected name = '';
  protected email = '';
  protected password = '';
  protected message = '';
  protected register(): void {
    if (this.auth.register(this.name, this.email, this.password))
      this.router.navigateByUrl('/shop');
    else this.message = 'Completa los datos y usa una contraseña de al menos 8 caracteres.';
  }
}
