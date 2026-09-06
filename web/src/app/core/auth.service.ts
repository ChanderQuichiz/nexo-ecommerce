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
    // Mock login logic
    if (email === 'admin@ecommerce.com' && password === 'admin123') {
      const response: AuthResponse = {
        user: { id: '1', email, name: 'Admin User', role: 'Admin' },
        token: 'mock-jwt-token-admin',
      };
      this.setSession(response);
      return of(response).pipe(delay(500));
    } else if (email === 'client@ecommerce.com' && password === 'client123') {
      const response: AuthResponse = {
        user: { id: '2', email, name: 'Client User', role: 'Client' },
        token: 'mock-jwt-token-client',
      };
      this.setSession(response);
      return of(response).pipe(delay(500));
    }
    return throwError(() => new Error('Invalid credentials'));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    // Mock registration logic
    const response: AuthResponse = {
      user: { id: Date.now().toString(), email, name, role: 'Client' },
      token: 'mock-jwt-token-new',
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
}
