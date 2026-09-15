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
  private orders = signal<Order[]>([
    {
      id: 'mock-1',
      userId: 'user-1',
      items: [{ product: { id: 1, name: 'Laptop Pro', description: '', price: 1200, stock: 10, imageUrl: '', category: 'Tech', active: true }, quantity: 1 }],
      total: 1250,
      date: new Date(),
      status: 'pending',
      shippingAddress: { address: 'Calle Falsa 123', city: 'Madrid', phone: '600111222' }
    },
    {
      id: 'mock-2',
      userId: 'user-2',
      items: [{ product: { id: 2, name: 'Auriculares Wireless', description: '', price: 150, stock: 5, imageUrl: '', category: 'Audio', active: true }, quantity: 2 }],
      total: 320,
      date: new Date(Date.now() - 86400000),
      status: 'Pagado',
      shippingAddress: { address: 'Av. Siempre Viva 742', city: 'Barcelona', phone: '600333444' }
    }
  ]);
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
