import { Injectable, signal } from '@angular/core';
import { Product } from './models';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private products = signal<Product[]>([
    {
      id: 1,
      name: 'Laptop Pro',
      description: 'High-end laptop for professional work',
      price: 1500,
      stock: 10,
      imageUrl:
        'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80',
      category: 'Electronics',
      active: true,
    },
    {
      id: 2,
      name: 'Smartphone Z',
      description: 'Latest generation smartphone with OLED display',
      price: 800,
      stock: 25,
      imageUrl:
        'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80',
      category: 'Electronics',
      active: true,
    },
    {
      id: 3,
      name: 'Headphones',
      description: 'Premium noise cancelling headphones',
      price: 200,
      stock: 50,
      imageUrl:
        'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80',
      category: 'Audio',
      active: true,
    },
    {
      id: 4,
      name: 'Mechanical Keyboard',
      description: 'RGB customizable mechanical keyboard',
      price: 120,
      stock: 15,
      imageUrl:
        'https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?auto=format&fit=crop&w=800&q=80',
      category: 'Peripherals',
      active: true,
    },
  ]);

  getProducts(): Observable<Product[]> {
    return of(this.products()).pipe(delay(300));
  }

  getAvailableProducts(): Observable<Product[]> {
    return of(this.products().filter((p) => p.active)).pipe(delay(300));
  }

  getProductById(id: number): Observable<Product | undefined> {
    return of(this.products().find((p) => p.id === id)).pipe(delay(200));
  }

  addProduct(product: Omit<Product, 'id'>, imageFile?: File): Observable<Product> {
    // Simulate AWS S3 upload logic: In a real app, you'd append imageFile to FormData
    // For now, if a file is provided, we use a temporary object URL
    const imageUrl = imageFile
      ? URL.createObjectURL(imageFile)
      : product.imageUrl ||
        'https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=800&q=80';

    const newProduct = {
      ...product,
      id: Date.now(),
      imageUrl,
    };

    this.products.update((prods) => [...prods, newProduct]);
    return of(newProduct).pipe(delay(800));
  }

  updateProduct(product: Product): Observable<Product> {
    this.products.update((prods) => prods.map((p) => (p.id === product.id ? product : p)));
    return of(product).pipe(delay(500));
  }

  toggleActive(productId: number): void {
    this.products.update((prods) =>
      prods.map((p) => (p.id === productId ? { ...p, active: !p.active } : p)),
    );
  }

  updateStock(productId: number, quantity: number): void {
    this.products.update((prods) =>
      prods.map((p) => (p.id === productId ? { ...p, stock: p.stock - quantity } : p)),
    );
  }

  checkStock(productId: number, quantity: number): boolean {
    const product = this.products().find((p) => p.id === productId);
    return !!product && product.stock >= quantity;
  }
}
