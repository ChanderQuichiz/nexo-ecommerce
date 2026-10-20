import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthResponse, User } from './models';
import { AppConfigService } from './app-config.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private static readonly tokenStorageKey = 'nexo.accessToken';
  private static readonly userStorageKey = 'nexo.currentUser';
  private currentUserSignal = signal<User | null>(null);
  private accessToken: string | null = null;
  currentUser = this.currentUserSignal.asReadonly();

  constructor(
    private http: HttpClient,
    private configService: AppConfigService
  ) {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem(AuthService.tokenStorageKey);
      const userJson = localStorage.getItem(AuthService.userStorageKey);
      if (token && userJson) {
        try {
          const user = JSON.parse(userJson) as User;
          if (user?.id && user.email && user.role) {
            this.accessToken = token;
            this.currentUserSignal.set(user);
          } else {
            this.clearSession();
          }
        } catch {
          this.clearSession();
        }
      }
    }
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.configService.apiUrl}/auth/login`, { email, password })
      .pipe(tap((response) => this.setSession(response)));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.configService.apiUrl}/auth/register`, { name, email, password })
      .pipe(tap((response) => this.setSession(response)));
  }

  logout(): void {
    if (this.accessToken) {
      this.http.post<void>(`${this.configService.apiUrl}/auth/logout`, {}).subscribe({ error: () => {} });
    }
    this.clearSession();
  }

  clearSession(): void {
    this.accessToken = null;
    this.currentUserSignal.set(null);
    if (typeof window !== 'undefined') {
      localStorage.removeItem(AuthService.tokenStorageKey);
      localStorage.removeItem(AuthService.userStorageKey);
    }
  }

  private setSession(response: AuthResponse): void {
    this.accessToken = response.token;
    this.currentUserSignal.set(response.user);
    if (typeof window !== 'undefined') {
      localStorage.setItem(AuthService.tokenStorageKey, response.token);
      localStorage.setItem(AuthService.userStorageKey, JSON.stringify(response.user));
    }
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'Admin';
  }

  isAuthenticated(): boolean {
    return this.accessToken !== null;
  }

  getToken(): string | null {
    return this.accessToken;
  }
}
