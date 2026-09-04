import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CatalogService } from '../../core/services/catalog.service';
import { AdminService } from '../../core/services/admin.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-admin',
  imports: [FormsModule],
  template: `<section class="inner-page">
    <p class="eyebrow">ADMINISTRACIÓN / {{ view().toUpperCase() }}</p>
    <div class="page-title">
      <div>
        <h1>Panel administrativo</h1>
        <p class="muted">Gestiona el negocio desde un único lugar.</p>
      </div>
      @if (view() === 'productos') {
        <button class="primary-button compact" (click)="showForm.set(!showForm())">
          ＋ Nuevo producto
        </button>
      }
    </div>
    <div class="admin-tabs">
      <button [class.tab-active]="view() === 'productos'" (click)="view.set('productos')">
        Productos <span>{{ products().length }}</span></button
      ><button [class.tab-active]="view() === 'categorias'" (click)="view.set('categorias')">
        Categorías <span>{{ admin.categories().length }}</span></button
      ><button [class.tab-active]="view() === 'usuarios'" (click)="view.set('usuarios')">
        Usuarios <span>{{ admin.users().length }}</span>
      </button>
    </div>
    @if (view() === 'productos') {
      @if (showForm()) {
        <form class="admin-form" (ngSubmit)="create()">
          <h2>Nuevo producto</h2>
          <input [(ngModel)]="draft.name" name="name" placeholder="Nombre" required /><select
            [(ngModel)]="draft.category"
            name="category"
          >
            @for (category of admin.categories(); track category) {
              <option [value]="category">{{ category }}</option>
            }</select
          ><input
            [(ngModel)]="draft.price"
            name="price"
            type="number"
            placeholder="Precio"
            required
          /><input
            [(ngModel)]="draft.stock"
            name="stock"
            type="number"
            placeholder="Stock"
            required
          /><button class="primary-button compact">Guardar</button>
        </form>
      }
      <div class="orders-table">
        <div class="table-head">
          <span>Producto</span><span>Categoría</span><span>Stock</span><span>Precio</span
          ><span>Acción</span>
        </div>
        @for (product of products(); track product.id) {
          <div class="table-row">
            <strong>{{ product.name }}</strong
            ><span>{{ product.category }}</span
            ><span [class.warning]="product.stock < 8">{{ product.stock }} u.</span
            ><span>{{ product.price }}</span
            ><button class="small-button" (click)="edit(product)">
              {{ editingId() === product.id ? 'Editando' : 'Editar' }}</button
            ><button class="small-button" (click)="catalog.remove(product.id)">Eliminar</button>
          </div>
        }
      </div>
    } @else if (view() === 'categorias') {
      <div class="profile-form">
        <h2>Categorías planas</h2>
        @for (category of admin.categories(); track category) {
          <div class="table-row">
            <strong>{{ category }}</strong
            ><span>Activa</span>
          </div>
        }
        <form class="admin-form" (ngSubmit)="addCategory()">
          <input
            [(ngModel)]="newCategory"
            name="category"
            placeholder="Nueva categoría"
            required
          /><button class="primary-button compact">Agregar</button>
        </form>
      </div>
    } @else {
      <form class="admin-form" (ngSubmit)="createUser()">
        <h2>Nuevo usuario</h2>
        <input
          [(ngModel)]="newUser.name"
          name="userName"
          placeholder="Nombre completo"
          required
        /><input
          [(ngModel)]="newUser.email"
          name="userEmail"
          type="email"
          placeholder="Correo"
          required
        /><select [(ngModel)]="newUser.role" name="userRole">
          <option value="cliente">Cliente</option>
          <option value="operador">Operador</option>
          <option value="administrador">Administrador</option></select
        ><button class="primary-button compact">Crear usuario</button>
      </form>
      <div class="orders-table">
        <div class="table-head">
          <span>Usuario</span><span>Correo</span><span>Rol</span><span>Estado</span
          ><span>Acción</span>
        </div>
        @for (user of admin.users(); track user.id) {
          <div class="table-row">
            <strong>{{ user.name }}</strong
            ><span>{{ user.email }}</span
            ><span>{{ user.role }}</span
            ><span>{{ user.active ? 'Activo' : 'Bloqueado' }}</span
            ><button class="small-button" (click)="admin.cycleRole(user.id)">Cambiar rol</button
            ><button class="small-button" (click)="admin.toggleUser(user.id)">
              {{ user.active ? 'Bloquear' : 'Activar' }}
            </button>
          </div>
        }
      </div>
    }
  </section>`,
})
export class AdminComponent {
  protected readonly catalog = inject(CatalogService);
  protected readonly admin = inject(AdminService);
  protected readonly products = this.catalog.products;
  protected readonly view = signal<'productos' | 'categorias' | 'usuarios'>('productos');
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected newCategory = '';
  protected newUser = { name: '', email: '', role: 'cliente' as const };
  protected draft = { name: '', category: 'Audio' as any, price: 0, stock: 0 };
  protected create(): void {
    if (!this.draft.name || this.draft.price <= 0 || this.draft.stock < 0) return;
    if (this.editingId()) {
      const current = this.catalog.byId(this.editingId()!);
      if (current) this.catalog.update({ ...current, ...this.draft });
      this.editingId.set(null);
      this.showForm.set(false);
      return;
    }
    this.catalog.add({
      ...this.draft,
      id: Date.now(),
      rating: 0,
      image:
        'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=800&q=80',
    });
    this.draft = { name: '', category: 'Audio', price: 0, stock: 0 };
    this.showForm.set(false);
  }
  protected edit(product: Product): void {
    this.draft = {
      name: product.name,
      category: product.category,
      price: product.price,
      stock: product.stock,
    };
    this.editingId.set(product.id);
    this.showForm.set(true);
  }
  protected addCategory(): void {
    this.admin.addCategory(this.newCategory);
    this.newCategory = '';
  }
  protected createUser(): void {
    if (this.admin.createUser(this.newUser.name, this.newUser.email, this.newUser.role))
      this.newUser = { name: '', email: '', role: 'cliente' };
  }
}
