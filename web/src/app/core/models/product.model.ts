export type ProductCategory = 'Audio' | 'Accesorios' | 'Oficina' | 'Hogar';

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
}

export interface CartItem extends Product {
  quantity: number;
}
