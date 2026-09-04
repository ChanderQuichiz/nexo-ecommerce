import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';
import { AdminUser } from '../../core/services/admin.service';

@Component({
  selector: 'app-admin-users',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">ADMINISTRACIÓN / EQUIPO Y USUARIOS</p>
    <div class="page-title">
      <div>
        <h1>Usuarios y empleados</h1>
        <p class="muted">Crea cuentas y asigna permisos de acceso.</p>
      </div>
    </div>
    <div class="admin-user-layout">
      <form class="profile-form" (ngSubmit)="create()">
        <h2>Crear empleado o usuario</h2>
        <label
          >Nombre completo<input
            [(ngModel)]="draft.name"
            name="name"
            required
            placeholder="Nombre y apellido" /></label
        ><label
          >Correo electrónico<input
            [(ngModel)]="draft.email"
            name="email"
            type="email"
            required
            placeholder="correo@empresa.com" /></label
        ><label
          >Rol de acceso<select [(ngModel)]="draft.role" name="role">
            <option value="cliente">Cliente</option>
            <option value="operador">Operador / vendedor</option>
            <option value="administrador">Administrador</option>
          </select></label
        ><button class="primary-button">Crear cuenta</button>
        @if (message) {
          <p class="form-message">{{ message }}</p>
        }
      </form>
      <div class="orders-table">
        <div class="table-head">
          <span>Nombre</span><span>Correo</span><span>Rol</span><span>Estado</span
          ><span>Acciones</span>
        </div>
        @for (user of admin.users(); track user.id) {
          <div class="table-row">
            <strong>{{ user.name }}</strong
            ><span>{{ user.email }}</span
            ><span>{{ user.role }}</span
            ><span>{{ user.active ? 'Activo' : 'Bloqueado' }}</span>
            <div class="row-actions">
              <button class="small-button" (click)="admin.cycleRole(user.id)">Cambiar rol</button
              ><button class="small-button" (click)="admin.toggleUser(user.id)">
                {{ user.active ? 'Bloquear' : 'Activar' }}
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  </section>`,
})
export class UsersComponent {
  protected readonly admin = inject(AdminService);
  protected draft: { name: string; email: string; role: AdminUser['role'] } = {
    name: '',
    email: '',
    role: 'cliente',
  };
  protected message = '';
  protected create(): void {
    this.message = this.admin.createUser(this.draft.name, this.draft.email, this.draft.role)
      ? 'Cuenta creada correctamente.'
      : 'Completa el nombre y un correo válido.';
    if (this.message.startsWith('Cuenta')) this.draft = { name: '', email: '', role: 'cliente' };
  }
}
