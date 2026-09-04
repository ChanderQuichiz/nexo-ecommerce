export type ProductCategory = string;

export interface Review {
  id: number;
  user: string;
  rating: number;
  comment: string;
  date: string;
}

export interface Product {
  id: number;
  name: string;
  category: ProductCategory;
  price: number;
  stock: number;
  rating: number;
  image: string;
  badge?: string;
  active?: boolean;
  reviews?: Review[];
}

export interface CartItem extends Product {
  quantity: number;
}
