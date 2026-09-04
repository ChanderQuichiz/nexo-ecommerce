import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { CustomerProfile, ProfileService } from '../../core/services/profile.service';

@Component({
  selector: 'app-profile',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">CUENTA / PERFIL</p>
    <h1>Mis datos</h1>
    <div class="profile-grid">
      <form class="profile-form" (ngSubmit)="savePersonal()">
        <h2>Información personal</h2>
        <label>Nombre completo<input [(ngModel)]="form.name" name="name" required /></label
        ><label
          >Correo electrónico<input
            [(ngModel)]="form.email"
            name="email"
            type="email"
            required /></label
        ><button class="primary-button compact">Guardar cambios</button>
      </form>
      <form class="profile-form" (ngSubmit)="saveAddress()">
        <h2>Dirección principal</h2>
        <label
          >Dirección<input
            [(ngModel)]="form.address"
            name="address"
            required
            placeholder="Av. Corrientes 1234" /></label
        ><label
          >Ciudad<input
            [(ngModel)]="form.city"
            name="city"
            required
            placeholder="Buenos Aires" /></label
        ><label
          >Código postal<input
            [(ngModel)]="form.zip"
            name="zip"
            required
            placeholder="C1043" /></label
        ><button class="primary-button compact">Guardar dirección</button>
      </form>
    </div>
    @if (message) {
      <p class="form-message">{{ message }}</p>
    }
  </section>`,
})
export class ProfileComponent {
  private readonly auth = inject(AuthService);
  private readonly profile = inject(ProfileService);
  protected form: CustomerProfile = {
    name: this.auth.user().name,
    email: this.auth.user().email,
    address: this.profile.profile().address,
    city: this.profile.profile().city,
    zip: this.profile.profile().zip,
  };
  protected message = '';
  protected savePersonal(): void {
    this.message = this.auth.updateProfile(this.form.name, this.form.email)
      ? 'Datos personales guardados.'
      : 'Revisa el nombre y el correo.';
    if (this.message.startsWith('Datos')) this.profile.save(this.form);
  }
  protected saveAddress(): void {
    this.profile.save(this.form);
    this.message = 'Dirección principal guardada.';
  }
}
