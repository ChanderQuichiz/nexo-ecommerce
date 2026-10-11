import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { authInterceptor } from './auth.interceptor';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('uses the gateway for login and sends the token on logout', () => {
    service.login('client@example.com', 'password123').subscribe();
    const login = http.expectOne(`${environment.apiUrl}/auth/login`);
    expect(login.request.method).toBe('POST');
    expect(login.request.body).toEqual({ email: 'client@example.com', password: 'password123' });
    login.flush({
      user: { id: 'client-1', email: 'client@example.com', name: 'Client', role: 'Client' },
      token: 'signed-token',
    });
    expect(service.isAuthenticated()).toBe(true);
    expect(service.getToken()).toBe('signed-token');

    service.logout();
    const logout = http.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(logout.request.method).toBe('POST');
    expect(logout.request.headers.get('Authorization')).toBe('Bearer signed-token');
    logout.flush(null);
    expect(service.isAuthenticated()).toBe(false);
  });

  it('registers a client through the gateway', () => {
    service.register('Client', 'client@example.com', 'password123').subscribe();
    const registration = http.expectOne(`${environment.apiUrl}/auth/register`);
    expect(registration.request.body).toEqual({
      name: 'Client', email: 'client@example.com', password: 'password123',
    });
    registration.flush({
      user: { id: 'client-2', email: 'client@example.com', name: 'Client', role: 'Client' },
      token: 'registered-token',
    });
    expect(service.currentUser()?.role).toBe('Client');
  });
});
