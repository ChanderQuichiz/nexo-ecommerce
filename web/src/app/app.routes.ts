import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { rootGuard } from './core/guards/root.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Auth routes isolated from any layout
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register.component').then((m) => m.RegisterComponent),
  },

  // Store Layout (strictly for Clients/Guests)
  {
    path: '',
    canActivate: [rootGuard],
    loadComponent: () =>
      import('./features/layout/store-layout.component').then((m) => m.StoreLayoutComponent),
    children: [
      { path: '', redirectTo: 'catalog', pathMatch: 'full' },
      {
        path: 'catalog',
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
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then((m) => m.CartComponent),
      },
      {
        path: 'checkout',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/checkout/checkout.component').then((m) => m.CheckoutComponent),
      },
      {
        path: 'order-confirmation',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/checkout/order-confirmation.component').then(
            (m) => m.OrderConfirmationComponent,
          ),
      },
      {
        path: 'orders',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/orders/order-history.component').then((m) => m.OrderHistoryComponent),
      },
    ],
  },

  // Admin Layout (Strictly for Admins)
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/admin-layout.component').then((m) => m.AdminLayoutComponent),
    children: [
      {
        path: 'products',
        loadComponent: () =>
          import('./features/admin/admin-products.component').then((m) => m.AdminProductsComponent),
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/admin/admin-orders.component').then((m) => m.AdminOrdersComponent),
      },
      { path: '', redirectTo: 'products', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '' },
];
