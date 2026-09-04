import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { Product, ProductCategory } from '../../core/models/product.model';

@Component({
  selector: 'app-catalog',
  imports: [FormsModule, RouterLink],
  templateUrl: './catalog.component.html',
})
export class CatalogComponent {
  protected readonly catalog = inject(CatalogService);
  private readonly route = inject(ActivatedRoute);
  protected readonly offersOnly = this.route.snapshot.data['offers'] === true;
  private readonly cart = inject(CartService);
  protected readonly search = this.catalog.searchTerm;
  protected readonly category = signal('Todas');
  protected readonly maxPrice = signal(250);
  protected readonly sort = signal<'relevance' | 'priceAsc' | 'priceDesc'>('relevance');
  protected readonly categories: Array<'Todas' | ProductCategory> = [
    'Todas',
    'Audio',
    'Accesorios',
    'Oficina',
    'Hogar',
  ];
  protected readonly products = this.catalog.products;
  protected readonly filtered = computed(() =>
    this.catalog
      .find({
        search: this.search(),
        category: this.category() as 'Todas' | ProductCategory,
        maxPrice: this.maxPrice(),
        sort: this.sort(),
      })
      .filter((product) => !this.offersOnly || !!product.badge),
  );
  protected formatPrice(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
  protected addToCart(product: Product): void {
    this.cart.add(product);
  }
}
