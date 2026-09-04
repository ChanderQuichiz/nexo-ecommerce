import { Injectable, signal } from '@angular/core';
import { SessionUser, UserRole } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<SessionUser>({ id: '', name: '', email: '', role: 'cliente' });
  readonly isAuthenticated = signal(false);

  login(email: string, password: string): boolean {
    if (!email.trim() || password.length < 8) return false;
    const role = this.roleForAccount(email);
    const names: Record<UserRole, string> = {
      cliente: 'María González',
      operador: 'Carlos Ruiz',
      administrador: 'Ana Torres',
    };
    this.user.set({ id: `${role}-account`, name: names[role], email: email.trim(), role });
    this.isAuthenticated.set(true);
    return true;
  }

  private roleForAccount(email: string): UserRole {
    const account = email.trim().toLowerCase();
    if (account.startsWith('admin@')) return 'administrador';
    if (account.startsWith('operador@')) return 'operador';
    return 'cliente';
  }

  register(name: string, email: string, password: string): boolean {
    if (!name.trim() || !email.includes('@') || password.length < 8) return false;
    this.user.set({
      id: `customer-${Date.now()}`,
      name: name.trim(),
      email: email.trim(),
      role: 'cliente',
    });
    this.isAuthenticated.set(true);
    return true;
  }

  requestPasswordReset(email: string): boolean {
    return email.includes('@');
  }
  safeReturnUrl(value: string | null): string | null {
    if (!value || !value.startsWith('/') || value.startsWith('//') || value.includes('://'))
      return null;
    return value;
  }
  updateProfile(name: string, email: string): boolean {
    if (!name.trim() || !email.includes('@')) return false;
    this.user.update((user) => ({ ...user, name: name.trim(), email: email.trim() }));
    return true;
  }

  logout(): void {
    this.isAuthenticated.set(false);
  }

  hasRole(roles: UserRole[]): boolean {
    return this.isAuthenticated() && roles.includes(this.user().role);
  }
}
