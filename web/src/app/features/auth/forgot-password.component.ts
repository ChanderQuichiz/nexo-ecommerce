import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, RouterLink],
  template: `<main class="auth-page">
    <section class="auth-card">
      <a class="brand" routerLink="/login"
        ><span class="brand-mark">N</span>nexo<span class="brand-dot">.</span></a
      >
      <p class="eyebrow">RECUPERAR ACCESO</p>
      <h1>Restablecer contraseña</h1>
      <p class="auth-copy">Te enviaremos un enlace para crear una nueva contraseña.</p>
      <label
        >Correo electrónico<input
          [(ngModel)]="email"
          type="email"
          autocomplete="email"
          placeholder="tu@email.com" /></label
      ><button class="primary-button" (click)="send()">Enviar instrucciones</button>
      @if (message) {
        <p class="form-message">{{ message }}</p>
      }
      <p class="auth-footer"><a routerLink="/login">Volver al inicio de sesión</a></p>
    </section>
  </main>`,
})
export class ForgotPasswordComponent {
  private readonly auth = inject(AuthService);
  protected email = '';
  protected message = '';
  protected send(): void {
    this.message = this.auth.requestPasswordReset(this.email)
      ? 'Si el correo existe, recibirás instrucciones.'
      : 'Ingresa un correo válido.';
  }
}
