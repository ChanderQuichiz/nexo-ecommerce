import { Injectable, signal } from '@angular/core';
import { Order, CartItem, OrderStatus } from './models';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ProductService } from './product.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private orders = signal<Order[]>([]);
  history = this.orders.asReadonly();

  constructor(
    private productService: ProductService,
    private authService: AuthService,
  ) {}

  createOrder(
    items: CartItem[],
    total: number,
    shippingInfo: { address: string; city: string; phone: string },
  ): Observable<Order> {
    const user = this.authService.currentUser();
    if (!user) {
      throw new Error('User must be logged in to create an order');
    }

    const newOrder: Order = {
      id: Math.random().toString(36).substr(2, 9),
      userId: user.id,
      items: [...items],
      total,
      date: new Date(),
      status: 'pending',
      shippingAddress: shippingInfo,
    };

    // Update stock for each product
    items.forEach((item) => {
      this.productService.updateStock(item.product.id, item.quantity);
    });

    this.orders.update((prev) => [newOrder, ...prev]);
    return of(newOrder).pipe(delay(800));
  }

  getAllOrders(): Observable<Order[]> {
    return of(this.orders()).pipe(delay(500));
  }

  updateOrderStatus(orderId: string, status: OrderStatus): Observable<void> {
    this.orders.update((prev) => prev.map((o) => (o.id === orderId ? { ...o, status } : o)));
    return of(undefined).pipe(delay(500));
  }

  getUserOrders(): Observable<Order[]> {
    const user = this.authService.currentUser();
    if (!user) return of([]);

    return of(this.orders().filter((o) => o.userId === user.id)).pipe(delay(500));
  }
}
