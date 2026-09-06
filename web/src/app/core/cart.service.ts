import { Injectable, signal, computed } from '@angular/core';
import { CartItem, Product } from './models';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cartItems = signal<CartItem[]>([]);

  items = this.cartItems.asReadonly();

  totalItems = computed(() => this.cartItems().reduce((acc, item) => acc + item.quantity, 0));

  totalPrice = computed(() =>
    this.cartItems().reduce((acc, item) => acc + item.product.price * item.quantity, 0),
  );

  constructor(private productService: ProductService) {}

  addToCart(product: Product, quantity: number = 1): boolean {
    if (!this.productService.checkStock(product.id, quantity)) {
      return false;
    }

    this.cartItems.update((items) => {
      const existingItem = items.find((item) => item.product.id === product.id);
      if (existingItem) {
        return items.map((item) =>
          item.product.id === product.id ? { ...item, quantity: item.quantity + quantity } : item,
        );
      }
      return [...items, { product, quantity }];
    });
    return true;
  }

  removeFromCart(productId: number): void {
    this.cartItems.update((items) => items.filter((item) => item.product.id !== productId));
  }

  updateQuantity(productId: number, quantity: number): boolean {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return true;
    }

    const item = this.cartItems().find((i) => i.product.id === productId);
    if (!item) return false;

    if (!this.productService.checkStock(productId, quantity)) {
      return false;
    }

    this.cartItems.update((items) =>
      items.map((item) => (item.product.id === productId ? { ...item, quantity } : item)),
    );
    return true;
  }

  clearCart(): void {
    this.cartItems.set([]);
  }
}
