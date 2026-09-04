import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password.component').then((m) => m.ForgotPasswordComponent),
  },
  {
    path: '',
    loadComponent: () =>
      import('./shared/layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'shop' },
      {
        path: 'shop',
        loadComponent: () =>
          import('./features/catalog/catalog.component').then((m) => m.CatalogComponent),
      },
      {
        path: 'shop/offers',
        data: { offers: true },
        loadComponent: () =>
          import('./features/catalog/catalog.component').then((m) => m.CatalogComponent),
      },
      {
        path: 'product/:id',
        loadComponent: () =>
          import('./features/catalog/product-detail.component').then(
            (m) => m.ProductDetailComponent,
          ),
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/orders/orders.component').then((m) => m.OrdersComponent),
        canActivate: [roleGuard(['cliente'])],
      },
      {
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then((m) => m.CartComponent),
      },
      {
        path: 'checkout',
        loadComponent: () =>
          import('./features/checkout/checkout.component').then((m) => m.CheckoutComponent),
        canActivate: [roleGuard(['cliente'])],
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then((m) => m.ProfileComponent),
        canActivate: [roleGuard(['cliente'])],
      },
    ],
  },
  {
    path: 'operator',
    loadComponent: () =>
      import('./shared/layout/backoffice-layout.component').then(
        (m) => m.BackofficeLayoutComponent,
      ),
    canActivate: [roleGuard(['operador', 'administrador'])],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'orders' },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/operator/operator.component').then((m) => m.OperatorComponent),
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./features/operator/inventory.component').then((m) => m.InventoryComponent),
      },
    ],
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./shared/layout/backoffice-layout.component').then(
        (m) => m.BackofficeLayoutComponent,
      ),
    canActivate: [roleGuard(['administrador'])],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'catalog' },
      {
        path: 'catalog',
        loadComponent: () =>
          import('./features/admin/admin.component').then((m) => m.AdminComponent),
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/admin/order-management.component').then(
            (m) => m.OrderManagementComponent,
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users.component').then((m) => m.UsersComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'shop' },
];
