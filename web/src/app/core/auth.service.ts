import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, catchError, defer, map, of, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, User } from './models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly currentUserSignal = signal<User | null>(null);
  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.authApiUrl}/login`, { email, password })
      .pipe(tap((response) => this.setSession(response)));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.authApiUrl}/register`, { name, email, password })
      .pipe(tap((response) => this.setSession(response)));
  }

  initializeSession(): Observable<void> {
    const token = this.getToken();
    if (!token) {
      this.clearSession();
      return of(void 0);
    }

    return this.http.get<User>(`${environment.authApiUrl}/me`).pipe(
      tap((user) => this.currentUserSignal.set(user)),
      map(() => void 0),
      catchError(() => {
        this.clearSession();
        return of(void 0);
      }),
    );
  }

  logout(): Observable<void> {
    return defer(() => {
      if (!this.getToken()) {
        return of(void 0);
      }
      return this.http.post<void>(`${environment.authApiUrl}/logout`, {});
    }).pipe(
      tap(() => this.clearSession()),
      catchError((error: unknown) => {
        this.clearSession();
        return throwError(() => error);
      }),
    );
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'Admin';
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  getToken(): string | null {
    return typeof localStorage === 'undefined' ? null : localStorage.getItem('token');
  }

  getErrorMessage(error: unknown, fallback: string): string {
    if (!(error instanceof HttpErrorResponse)) {
      return fallback;
    }
    if (error.status === 0) {
      return 'No se pudo conectar con el servicio de autenticación. Verifica que Auth esté iniciado.';
    }
    if (error.status === 401) {
      return 'El correo o la contraseña no son correctos.';
    }

    const backendMessage =
      typeof error.error === 'object' &&
      error.error !== null &&
      'message' in error.error &&
      typeof error.error.message === 'string'
        ? error.error.message.toLowerCase()
        : '';
    if (backendMessage.includes('already registered')) {
      return 'Ese correo ya está registrado.';
    }
    if (error.status === 400) {
      return 'Revisa los datos ingresados. La contraseña debe tener al menos 8 caracteres.';
    }
    return fallback;
  }

  private setSession(auth: AuthResponse): void {
    localStorage.setItem('user', JSON.stringify(auth.user));
    localStorage.setItem('token', auth.token);
    this.currentUserSignal.set(auth.user);
  }

  private clearSession(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
    this.currentUserSignal.set(null);
  }
}
