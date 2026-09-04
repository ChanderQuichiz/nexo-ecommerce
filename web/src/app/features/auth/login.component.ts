import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  template: `<main class="auth-page">
    <section class="auth-card">
      <a class="brand" routerLink="/shop"
        ><span class="brand-mark">N</span>nexo<span class="brand-dot">.</span></a
      >
      <p class="eyebrow">ACCESO SEGURO</p>
      <h1>Bienvenido de nuevo</h1>
      <p class="auth-copy">Ingresa a tu cuenta para continuar.</p>
      <label
        >Correo electrónico<input
          type="email"
          autocomplete="username"
          [(ngModel)]="email"
          placeholder="tu@email.com" /></label
      ><label
        >Contraseña<input
          type="password"
          autocomplete="current-password"
          [(ngModel)]="password"
          placeholder="••••••••" /></label
      ><button class="primary-button" (click)="login()">Ingresar</button
      ><a class="text-button" routerLink="/forgot-password">¿Olvidaste tu contraseña?</a>
      @if (message) {
        <p class="form-message">{{ message }}</p>
      }
      <p class="auth-footer">¿No tienes cuenta? <a routerLink="/register">Crear cuenta</a></p>
    </section>
  </main>`,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected email = '';
  protected password = '';
  protected message = '';
  protected login(): void {
    const accepted = this.auth.login(this.email, this.password);
    if (!accepted) {
      this.message = 'Ingresa un correo válido y una contraseña de al menos 8 caracteres.';
      return;
    }
    const role = this.auth.user().role;
    const returnUrl = this.auth.safeReturnUrl(this.route.snapshot.queryParamMap.get('returnUrl'));
    this.router.navigateByUrl(
      returnUrl ||
        (role === 'cliente'
          ? '/shop'
          : role === 'operador'
            ? '/operator/orders'
            : '/admin/catalog'),
    );
  }
  protected requestReset(): void {
    this.message = this.auth.requestPasswordReset(this.email)
      ? 'Si el correo existe, recibirás instrucciones.'
      : 'Ingresa un correo válido.';
  }
}
