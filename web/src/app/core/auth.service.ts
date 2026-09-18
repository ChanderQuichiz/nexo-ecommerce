import { Injectable, signal } from '@angular/core';
import { AuthResponse, User } from './models';
import { Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSignal = signal<User | null>(null);
  currentUser = this.currentUserSignal.asReadonly();
  private readonly USERS_KEY = 'mock_users';

  constructor() {
    // Check localStorage for existing session
    if (typeof window !== 'undefined') {
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        this.currentUserSignal.set(JSON.parse(savedUser));
      }
    }
  }

  login(email: string, password: string): Observable<AuthResponse> {
    const demoUsers = this.getStoredUsers();
    const storedUser = demoUsers.find((user) => user.email.toLowerCase() === email.toLowerCase());

    if (storedUser && storedUser.password === password) {
      const response: AuthResponse = {
        user: { id: storedUser.id, email: storedUser.email, name: storedUser.name, role: storedUser.role },
        token: `mock-jwt-token-${storedUser.id}`,
      };
      this.setSession(response);
      return of(response).pipe(delay(500));
    }

    if (email === 'admin@ecommerce.com' && password === 'admin123') {
      const response: AuthResponse = {
        user: { id: '1', email, name: 'Usuario Administrador', role: 'Admin' },
        token: 'mock-jwt-token-admin',
      };
      this.setSession(response);
      return of(response).pipe(delay(500));
    } else if (email === 'client@ecommerce.com' && password === 'client123') {
      const response: AuthResponse = {
        user: { id: '2', email, name: 'Usuario Cliente', role: 'Client' },
        token: 'mock-jwt-token-client',
      };
      this.setSession(response);
      return of(response).pipe(delay(500));
    }
    return throwError(() => new Error('Credenciales inválidas'));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    if (!password || password.length < 8) {
      return throwError(() => new Error('La contraseña debe tener al menos 8 caracteres'));
    }

    const users = this.getStoredUsers();
    const alreadyExists = users.some((user) => user.email.toLowerCase() === email.toLowerCase());

    if (alreadyExists) {
      return throwError(() => new Error('Este correo ya está registrado'));
    }

    const newUser = {
      id: Date.now().toString(),
      email,
      name,
      password,
      role: 'Client' as const,
    };

    users.push(newUser);
    localStorage.setItem(this.USERS_KEY, JSON.stringify(users));

    const response: AuthResponse = {
      user: { id: newUser.id, email, name, role: 'Client' },
      token: `mock-jwt-token-${newUser.id}`,
    };
    this.setSession(response);
    return of(response).pipe(delay(500));
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
    this.currentUserSignal.set(null);
  }

  private getStoredUsers(): Array<{ id: string; email: string; name: string; password: string; role: 'Client' | 'Admin' }> {
    if (typeof window === 'undefined') {
      return [];
    }

    const savedUsers = localStorage.getItem(this.USERS_KEY);
    if (!savedUsers) {
      return [];
    }

    try {
      return JSON.parse(savedUsers) as Array<{ id: string; email: string; name: string; password: string; role: 'Client' | 'Admin' }>;
    } catch {
      return [];
    }
  }

  private setSession(auth: AuthResponse): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem('user', JSON.stringify(auth.user));
      localStorage.setItem('token', auth.token);
    }
    this.currentUserSignal.set(auth.user);
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'Admin';
  }

  isAuthenticated(): boolean {
    return !!this.currentUser();
  }

  getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('token');
    }
    return null;
  }
}
