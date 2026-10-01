import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { AuthService } from './auth.service';
import { LoadingService } from './loading.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const loadingService = inject(LoadingService);
  const isPublicAuthenticationRequest =
    request.url.endsWith('/auth/login') || request.url.endsWith('/auth/register');
  const token = isPublicAuthenticationRequest ? null : authService.getToken();
  const authenticatedRequest = token
    ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : request;

  loadingService.show();
  return next(authenticatedRequest).pipe(finalize(() => loadingService.hide()));
};
