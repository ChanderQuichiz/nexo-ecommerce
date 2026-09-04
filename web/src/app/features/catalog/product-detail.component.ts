import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { CatalogService } from '../../core/services/catalog.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-product-detail',
  imports: [RouterLink, FormsModule],
  template: `<section class="detail-page">
    @if (product()) {
      <a routerLink="/shop" class="back-link">← Volver al catálogo</a>
      <div class="detail-grid">
        <div class="detail-image"><img [src]="product()?.image" [alt]="product()?.name" /></div>
        <div class="detail-copy">
          <p class="eyebrow">{{ product()?.category?.toUpperCase() }} / DETALLE</p>
          <h1>{{ product()?.name }}</h1>
          <div class="rating">
            ★ <span>{{ product()?.rating }}</span> <i>{{ product()?.reviews?.length || 0 }} reseñas</i>
            <button
              class="heart-detail"
              (click)="catalog.toggleFavorite(product()!.id)"
              [class.selected]="catalog.favorites().includes(product()!.id)"
            >
              {{ catalog.favorites().includes(product()!.id) ? '♥' : '♡' }}
            </button>
          </div>
          <p class="detail-description">
            Producto seleccionado de calidad garantizada. Diseño funcional, materiales durables y
            soporte incluido para que puedas usarlo todos los días.
          </p>
          <strong class="detail-price">{{ format(product()?.price || 0) }}</strong>
          <p class="stock" [class.low]="(product()?.stock || 0) < 8">
            ●
            {{
              product()?.stock === 0
                ? 'Sin stock'
                : (product()?.stock || 0) < 8
                  ? 'Últimas unidades'
                  : 'En stock'
            }}
            ({{ product()?.stock }} unidades)
          </p>
          <div class="detail-actions">
            <button class="primary-button detail-button" (click)="cart.add(product()!)">
              Agregar al carrito
            </button>
            <a routerLink="/cart" class="secondary-button">Ver carrito</a>
          </div>
        </div>
      </div>

      <div class="reviews-section">
        <div class="reviews-header">
          <h2>Reseñas de clientes</h2>
          <button class="small-button" (click)="showReviewForm.set(!showReviewForm())">
            {{ showReviewForm() ? 'Cancelar' : 'Escribir reseña' }}
          </button>
        </div>

        @if (showReviewForm()) {
          <form class="review-form" (ngSubmit)="submitReview()">
            <h3>Tu opinión importa</h3>
            <div class="rating-input">
              <span>Calificación:</span>
              <select [(ngModel)]="newReview.rating" name="rating">
                <option [value]="5">5 - Excelente</option>
                <option [value]="4">4 - Muy bueno</option>
                <option [value]="3">3 - Bueno</option>
                <option [value]="2">2 - Regular</option>
                <option [value]="1">1 - Malo</option>
              </select>
            </div>
            <textarea
              [(ngModel)]="newReview.comment"
              name="comment"
              placeholder="Cuéntanos tu experiencia..."
              required
            ></textarea>
            <button type="submit" class="primary-button compact">Publicar reseña</button>
          </form>
        }

        <div class="reviews-list">
          @for (review of product()?.reviews || []; track review.id) {
            <div class="review-item">
              <div class="review-meta">
                <strong>{{ review.user }}</strong>
                <span>{{ review.date }}</span>
                <span class="rating">★ {{ review.rating }}</span>
              </div>
              <p>{{ review.comment }}</p>
            </div>
          } @empty {
            <p class="muted">Aún no hay reseñas para este producto.</p>
          }
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
  protected readonly catalog = inject(CatalogService);
  private readonly auth = inject(AuthService);
  protected readonly cart = inject(CartService);
  protected readonly product = computed(() =>
    this.catalog.byId(Number(this.route.snapshot.paramMap.get('id'))),
  );
  protected readonly showReviewForm = signal(false);
  protected newReview = { rating: 5, comment: '' };

  protected submitReview(): void {
    const p = this.product();
    if (!p || !this.newReview.comment.trim()) return;

    this.catalog.addReview(p.id, {
      user: this.auth.user()?.name || 'Cliente anónimo',
      rating: Number(this.newReview.rating),
      comment: this.newReview.comment,
    });

    this.newReview = { rating: 5, comment: '' };
    this.showReviewForm.set(false);
  }

  protected format(value: number): string {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'USD' }).format(value);
  }
}
