import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '../models/auth.model';
import { AuthService } from '../services/auth.service';

export const roleGuard =
  (roles: UserRole[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.isAuthenticated())
      return router.createUrlTree(['/login'], { queryParams: { returnUrl: router.url } });
    return auth.hasRole(roles) ? true : router.createUrlTree(['/shop']);
  };
