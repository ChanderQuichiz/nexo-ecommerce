import { Injectable, signal } from '@angular/core';

export interface AdminUser {
  id: string;
  name: string;
  email: string;
  role: 'cliente' | 'operador' | 'administrador';
  active: boolean;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  readonly categories = signal(['Audio', 'Accesorios', 'Oficina', 'Hogar']);
  readonly users = signal<AdminUser[]>([
    { id: 'u-1', name: 'María González', email: 'maria@nexo.com', role: 'cliente', active: true },
    { id: 'u-2', name: 'Carlos Ruiz', email: 'carlos@nexo.com', role: 'operador', active: true },
    { id: 'u-3', name: 'Ana Torres', email: 'ana@nexo.com', role: 'administrador', active: true },
  ]);
  toggleUser(id: string): void {
    this.users.update((items) =>
      items.map((user) => (user.id === id ? { ...user, active: !user.active } : user)),
    );
  }
  addCategory(name: string): void {
    if (name.trim() && !this.categories().includes(name.trim()))
      this.categories.update((items) => [...items, name.trim()]);
  }
  cycleRole(id: string): void {
    this.users.update((items) =>
      items.map((user) =>
        user.id === id
          ? {
              ...user,
              role:
                user.role === 'cliente'
                  ? 'operador'
                  : user.role === 'operador'
                    ? 'administrador'
                    : 'cliente',
            }
          : user,
      ),
    );
  }
  createUser(name: string, email: string, role: AdminUser['role']): boolean {
    if (!name.trim() || !email.includes('@')) return false;
    this.users.update((items) => [
      ...items,
      { id: `u-${Date.now()}`, name: name.trim(), email: email.trim(), role, active: true },
    ]);
    return true;
  }
}
