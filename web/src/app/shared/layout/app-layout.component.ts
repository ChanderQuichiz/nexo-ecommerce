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
  protected search = this.catalog.searchTerm();
  protected searchProducts(): void {
    this.catalog.setSearch(this.search);
    this.router.navigateByUrl('/shop');
  }
}
