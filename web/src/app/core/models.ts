export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl: string;
  category: string;
  active: boolean;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type OrderStatus =
  | 'PENDING'
  | 'PAID'
  | 'FAILED'
  | 'CANCELLED'
  | 'pending'
  | 'Pagado'
  | 'En preparación'
  | 'Enviado'
  | 'Entregado';

export interface Order {
  id: string;
  userId: string;
  items: CartItem[];
  total: number;
  subTotal?: number;
  shippingFee?: number;
  tax?: number;
  date: Date;
  status: OrderStatus;
  shippingAddress?: {
    address: string;
    city: string;
    phone: string;
  };
  paymentIntentId?: string[];
}

export type UserRole = 'Admin' | 'Client';

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export interface AuthResponse {
  user: User;
  token: string;
}
