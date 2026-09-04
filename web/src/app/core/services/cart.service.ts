import { Injectable, computed, signal } from '@angular/core';
import { CartItem, Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  readonly items = signal<CartItem[]>([]);
  readonly count = computed(() => this.items().reduce((total, item) => total + item.quantity, 0));
  readonly subtotal = computed(() =>
    this.items().reduce((total, item) => total + item.price * item.quantity, 0),
  );
  readonly couponCode = signal('');
  readonly discount = computed(() => (this.couponCode() === 'NUEVO10' ? this.subtotal() * 0.1 : 0));
  readonly total = computed(() => this.subtotal() - this.discount());

  add(product: Product): void {
    this.items.update((items) =>
      items.some((item) => item.id === product.id)
        ? items.map((item) =>
            item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item,
          )
        : [...items, { ...product, quantity: 1 }],
    );
  }

  change(item: CartItem, amount: number): void {
    this.items.update((items) =>
      items.flatMap((current) => {
        if (current.id !== item.id) return [current];
        const quantity = current.quantity + amount;
        return quantity > 0 ? [{ ...current, quantity }] : [];
      }),
    );
  }

  applyCoupon(code: string): boolean {
    const accepted = code.trim().toUpperCase() === 'NUEVO10';
    this.couponCode.set(accepted ? 'NUEVO10' : '');
    return accepted;
  }
  clear(): void {
    this.items.set([]);
    this.couponCode.set('');
  }
}
