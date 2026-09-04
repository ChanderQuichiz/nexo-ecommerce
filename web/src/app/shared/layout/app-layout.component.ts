import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CatalogService } from '../../core/services/catalog.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, FormsModule],
  templateUrl: './app-layout.component.html',
})
export class AppLayoutComponent {
  protected readonly auth = inject(AuthService);
  protected readonly catalog = inject(CatalogService);
  protected readonly cart = inject(CartService);
  private readonly router = inject(Router);

  protected get searchTerm(): string {
    return this.catalog.searchTerm();
  }
  protected set searchTerm(value: string) {
    this.catalog.setSearch(value);
  }

  protected searchProducts(): void {
    this.router.navigateByUrl('/shop');
  }
}
