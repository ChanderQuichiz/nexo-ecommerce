import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CatalogService } from '../../core/services/catalog.service';

@Component({
  selector: 'app-product-detail',
  imports: [RouterLink],
  template: `<section class="detail-page">
    @if (product) {
      <a routerLink="/shop" class="back-link">← Volver al catálogo</a>
      <div class="detail-grid">
        <div class="detail-image"><img [src]="product.image" [alt]="product.name" /></div>
        <div class="detail-copy">
          <p class="eyebrow">{{ product.category.toUpperCase() }} / DETALLE</p>
          <h1>{{ product.name }}</h1>
          <div class="rating">
            ★ <span>{{ product.rating }}</span> <i>24 reseñas</i>
          </div>
          <p class="detail-description">
            Producto seleccionado de calidad garantizada. Diseño funcional, materiales durables y
            soporte incluido para que puedas usarlo todos los días.
          </p>
          <strong class="detail-price">{{ format(product.price) }}</strong>
          <p class="stock" [class.low]="product.stock < 8">
            ● {{ product.stock }} unidades disponibles
          </p>
          <button class="primary-button detail-button" (click)="cart.add(product)">
            Agregar al carrito</button
          ><a routerLink="/cart" class="secondary-button">Ver carrito</a>
        </div>
      </div>
    } @else {
      <div class="empty-state">
        Producto no encontrado. <a routerLink="/shop">Volver al catálogo</a>
      </div>
    }
  </section>`,
})
export class ProductDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly catalog = inject(CatalogService);
  protected readonly cart = inject(CartService);
  protected readonly product = this.catalog.byId(Number(this.route.snapshot.paramMap.get('id')));
  protected format(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
}
