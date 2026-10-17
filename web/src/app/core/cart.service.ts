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

  constructor(private productService: ProductService) {
    if (typeof window !== 'undefined') {
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        this.cartItems.set(JSON.parse(savedCart));
      }
    }
  }

  private saveCart() {
    if (typeof window !== 'undefined') {
      localStorage.setItem('cart', JSON.stringify(this.cartItems()));
    }
  }

  addToCart(product: Product, quantity: number = 1): boolean {
    const existingItem = this.cartItems().find((item) => item.product.id === product.id);
    const requestedQuantity = (existingItem?.quantity ?? 0) + quantity;
    if (product.stock < requestedQuantity) {
      return false;
    }

    this.cartItems.update((items) => {
      if (existingItem) {
        return items.map((item) =>
          item.product.id === product.id ? { ...item, quantity: item.quantity + quantity } : item,
        );
      }
      return [...items, { product, quantity }];
    });
    this.saveCart();
    return true;
  }

  removeFromCart(productId: string): void {
    this.cartItems.update((items) => items.filter((item) => item.product.id !== productId));
    this.saveCart();
  }

  updateQuantity(productId: string, quantity: number): boolean {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return true;
    }

    const item = this.cartItems().find((i) => i.product.id === productId);
    if (!item) return false;

    if (item.product.stock < quantity) {
      return false;
    }

    this.cartItems.update((items) =>
      items.map((item) => (item.product.id === productId ? { ...item, quantity } : item)),
    );
    this.saveCart();
    return true;
  }

  clearCart(): void {
    this.cartItems.set([]);
    this.saveCart();
  }
}
