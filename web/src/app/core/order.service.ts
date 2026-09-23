import { Product, CartItem, OrderStatus, Order } from './models';
import { Injectable, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ProductService } from './product.service';
import { AuthService } from './auth.service';

export interface OrdenCreateRequestDto {
  userId: string;
  address: string;
  city: string;
  phone: string;
  items: Array<{
    productId: string;
    price: number;
    quantity: number;
  }>;
}

export interface OrdenCreateResponseDto {
  message: string;
}

export interface CreateIntentPaymentRequest {
  orderId: string;
}

export interface CreateIntentPaymentResponse {
  intentId: string;
}

export interface UpdateStatusRequest {
  status: string;
  orderId: string;
}

export interface UpdateStatusResponse {
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private orders = signal<Order[]>([
    {
      id: 'f602e78a-371f-4da7-9719-689958a92852',
      userId: 'user-1',
      items: [{ product: { id: 1, name: 'Laptop Pro', description: '', price: 1200, stock: 10, imageUrl: '', category: 'Tech', active: true }, quantity: 1 }],
      total: 1250,
      subTotal: 1200,
      shippingFee: 0,
      tax: 50,
      date: new Date(),
      status: 'PENDING',
      shippingAddress: { address: 'Calle Falsa 123', city: 'Madrid', phone: '600111222' },
      paymentIntentId: ['pi_mock_123']
    },
    {
      id: 'a1b2c3d4-e5f6-7890-abcd-ef0123456789',
      userId: 'user-2',
      items: [{ product: { id: 2, name: 'Auriculares Wireless', description: '', price: 150, stock: 5, imageUrl: '', category: 'Audio', active: true }, quantity: 2 }],
      total: 320,
      subTotal: 300,
      shippingFee: 10,
      tax: 10,
      date: new Date(Date.now() - 86400000),
      status: 'PAID',
      shippingAddress: { address: 'Av. Siempre Viva 742', city: 'Barcelona', phone: '600333444' },
      paymentIntentId: ['pi_mock_456']
    }
  ]);
  history = this.orders.asReadonly();

  constructor(
    private productService: ProductService,
    private authService: AuthService,
  ) {}

  // 1. POST /orders
  createOrder(
    items: CartItem[],
    total: number,
    shippingInfo: { address: string; city: string; phone: string },
  ): Observable<OrdenCreateResponseDto> {
    const user = this.authService.currentUser();
    if (!user) {
      throw new Error('User must be logged in to create an order');
    }

    const subTotal = items.reduce((acc, i) => acc + i.product.price * i.quantity, 0);
    const tax = subTotal * 0.07;
    const shippingFee = 0;

    const newOrder: Order = {
      id: crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substr(2, 9),
      userId: user.id,
      items: [...items],
      total: subTotal + tax + shippingFee,
      subTotal,
      shippingFee,
      tax,
      date: new Date(),
      status: 'PENDING',
      shippingAddress: shippingInfo,
      paymentIntentId: []
    };

    // Update stock for each product locally
    items.forEach((item) => {
      this.productService.updateStock(item.product.id, item.quantity);
    });

    this.orders.update((prev) => [newOrder, ...prev]);
    return of({ message: `Orden creada exitosamente con ID: ${newOrder.id}` }).pipe(delay(600));
  }

  // 2. GET /orders
  getAllOrders(): Observable<Order[]> {
    return of(this.orders()).pipe(delay(400));
  }

  // 3. GET /orders/me?userId=...
  getOrdersByUserId(userId: string): Observable<Order[]> {
    return of(this.orders().filter((o) => o.userId === userId)).pipe(delay(400));
  }

  getUserOrders(): Observable<Order[]> {
    const user = this.authService.currentUser();
    if (!user) return of([]);
    return this.getOrdersByUserId(user.id);
  }

  // 4. GET /orders/{id}
  getOrderById(id: string): Observable<Order | undefined> {
    const order = this.orders().find(o => o.id === id);
    return of(order).pipe(delay(300));
  }

  // 5. PATCH /orders/{id}/status
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<UpdateStatusResponse> {
    this.orders.update((prev) => prev.map((o) => (o.id === orderId ? { ...o, status } : o)));
    return of({ message: `Estado de la orden ${orderId} actualizado a ${status}` }).pipe(delay(400));
  }

  // 6. POST /orders/intent-payment
  createIntentPayment(orderId: string): Observable<CreateIntentPaymentResponse> {
    const intentId = 'pi_test_' + Math.random().toString(36).substring(2, 9);
    this.orders.update((prev) => prev.map((o) => {
      if (o.id === orderId) {
        return {
          ...o,
          paymentIntentId: [...(o.paymentIntentId || []), intentId]
        };
      }
      return o;
    }));
    return of({ intentId }).pipe(delay(600));
  }

  // 7. POST /webhooks/stripe (simulated local webhook trigger)
  simulateStripeWebhook(intentId: string, status: 'PAID' | 'FAILED'): Observable<string> {
    this.orders.update((prev) => prev.map((o) => {
      if (o.paymentIntentId?.includes(intentId)) {
        return { ...o, status };
      }
      return o;
    }));
    return of('Webhook received successfully').pipe(delay(400));
  }
}
