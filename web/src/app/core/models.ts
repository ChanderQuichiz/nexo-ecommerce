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
  'pending' | 'Pagado' | 'En preparación' | 'Enviado' | 'Entregado' | 'cancelled';

export interface Order {
  id: string;
  userId: string;
  items: CartItem[];
  total: number;
  date: Date;
  status: OrderStatus;
  shippingAddress?: {
    address: string;
    city: string;
    phone: string;
  };
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
