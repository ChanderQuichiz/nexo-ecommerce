import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { CartItem, Order, OrderStatus } from './models';
import { AuthService } from './auth.service';
import { ProductService } from './product.service';

export interface OrdenCreateRequestDto {
  userId: string;
  address: string;
  city: string;
  phone: string;
  items: Array<{ productId: string; price: number; quantity: number }>;
}

export interface OrdenCreateResponseDto {
  message: string;
}

export interface CreateIntentPaymentResponse {
  intentId: string;
  clientSecret: string;
}

export interface UpdateStatusRequest {
  status: string;
  orderId: string;
}

export interface UpdateStatusResponse {
  message: string;
}

interface ApiOrder {
  id: string;
  userId: string;
  subTotal: number;
  shippingFee: number;
  tax: number;
  total: number;
  status: OrderStatus;
  date: string;
  items: Array<{ productId: string; price: number; quantity: number }>;
  address: string;
  city: string;
  phone: string;
  paymentIntentId?: string[];
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly ordersUrl = `${environment.apiUrl}/orders`;

  constructor(
    private http: HttpClient,
    private productService: ProductService,
    private authService: AuthService
  ) {}

  createOrder(
    items: CartItem[],
    _total: number,
    shippingInfo: { address: string; city: string; phone: string }
  ): Observable<OrdenCreateResponseDto> {
    const user = this.authService.currentUser();
    if (!user) throw new Error('User must be logged in to create an order');
    return this.http.post<OrdenCreateResponseDto>(this.ordersUrl, {
      userId: user.id,
      ...shippingInfo,
      phone: shippingInfo.phone.replace(/\s/g, ''),
      items: items.map((item) => ({
        productId: item.product.id,
        price: item.product.price,
        quantity: item.quantity,
      })),
    } satisfies OrdenCreateRequestDto);
  }

  getAllOrders(): Observable<Order[]> {
    return this.http.get<ApiOrder[]>(this.ordersUrl).pipe(switchMap((orders) => this.toOrders(orders)));
  }

  getOrdersByUserId(userId: string): Observable<Order[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http
      .get<ApiOrder[]>(`${this.ordersUrl}/me`, { params })
      .pipe(switchMap((orders) => this.toOrders(orders)));
  }

  getUserOrders(): Observable<Order[]> {
    const user = this.authService.currentUser();
    return user ? this.getOrdersByUserId(user.id) : of([]);
  }

  getOrderById(id: string): Observable<Order> {
    return this.http.get<ApiOrder>(`${this.ordersUrl}/${id}`).pipe(switchMap((order) => this.toOrder(order)));
  }

  updateOrderStatus(orderId: string, status: OrderStatus): Observable<UpdateStatusResponse> {
    return this.http.patch<UpdateStatusResponse>(`${this.ordersUrl}/${orderId}/status`, {
      status,
      orderId,
    } satisfies UpdateStatusRequest);
  }

  createIntentPayment(orderId: string): Observable<CreateIntentPaymentResponse> {
    return this.http.post<CreateIntentPaymentResponse>(`${this.ordersUrl}/intent-payment`, { orderId });
  }

  private toOrders(orders: ApiOrder[]): Observable<Order[]> {
    return forkJoin(orders.map((order) => this.toOrder(order)));
  }

  private toOrder(order: ApiOrder): Observable<Order> {
    return forkJoin(
      order.items.map((item) =>
        this.productService
          .getProductById(item.productId)
          .pipe(map((product) => ({ product: { ...product, price: item.price }, quantity: item.quantity })))
      )
    ).pipe(
      map((items) => ({
        id: order.id,
        userId: order.userId,
        items,
        total: order.total,
        subTotal: order.subTotal,
        shippingFee: order.shippingFee,
        tax: order.tax,
        date: new Date(order.date),
        status: order.status,
        shippingAddress: { address: order.address, city: order.city, phone: order.phone },
        paymentIntentId: order.paymentIntentId ?? [],
      }))
    );
  }
}
