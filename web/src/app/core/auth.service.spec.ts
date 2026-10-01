import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../environments/environment';
import { AuthResponse } from './models';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('sends login to the auth API and persists the real response', () => {
    const response: AuthResponse = {
      user: { id: 'user-1', email: 'user@example.com', name: 'Nexo User', role: 'Client' },
      token: 'signed-jwt',
    };
    let received: AuthResponse | undefined;

    service.login('user@example.com', 'secure-pass-123').subscribe((value) => (received = value));

    const request = http.expectOne(`${environment.authApiUrl}/login`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      email: 'user@example.com',
      password: 'secure-pass-123',
    });
    request.flush(response);

    expect(received).toEqual(response);
    expect(service.currentUser()).toEqual(response.user);
    expect(service.getToken()).toBe('signed-jwt');
  });

  it('validates a saved token with /me before restoring the user', () => {
    localStorage.setItem('token', 'saved-jwt');
    const currentUser = {
      id: 'user-2',
      email: 'admin@example.com',
      name: 'Admin',
      role: 'Admin' as const,
    };

    service.initializeSession().subscribe();

    const request = http.expectOne(`${environment.authApiUrl}/me`);
    expect(request.request.method).toBe('GET');
    request.flush(currentUser);
    expect(service.currentUser()).toEqual(currentUser);
    expect(service.isAdmin()).toBe(true);
  });

  it('clears a session rejected by the auth API', () => {
    localStorage.setItem('token', 'expired-jwt');
    localStorage.setItem('user', JSON.stringify({ id: 'old-user' }));

    service.initializeSession().subscribe();
    http.expectOne(`${environment.authApiUrl}/me`).flush(
      { status: 401, message: 'Token revocado' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(service.currentUser()).toBeNull();
    expect(service.getToken()).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });

  it('revokes token on logout and clears local session', () => {
    const response: AuthResponse = {
      user: { id: 'user-3', email: 'user@example.com', name: 'User', role: 'Client' },
      token: 'logout-jwt',
    };
    service.login(response.user.email, 'password').subscribe();
    http.expectOne(`${environment.authApiUrl}/login`).flush(response);

    service.logout().subscribe();
    const request = http.expectOne(`${environment.authApiUrl}/logout`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush(null);

    expect(service.currentUser()).toBeNull();
    expect(service.getToken()).toBeNull();
  });
});
