import { Injectable, signal } from '@angular/core';
import { CATALOG } from '../data/catalog.data';
import { CartItem, Product, ProductCategory } from '../models/product.model';

export interface CatalogQuery {
  search: string;
  category: 'Todas' | ProductCategory;
  maxPrice: number;
  sort?: 'relevance' | 'priceAsc' | 'priceDesc';
}

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly catalog = signal<Product[]>(CATALOG);
  readonly products = this.catalog.asReadonly();
  readonly searchTerm = signal('');
  readonly favorites = signal<number[]>([]);

  find(query: CatalogQuery): Product[] {
    const results = this.products().filter(
      (product) =>
        product.active !== false &&
        product.name.toLowerCase().includes(query.search.toLowerCase()) &&
        (query.category === 'Todas' || product.category === query.category) &&
        product.price <= query.maxPrice,
    );
    if (query.sort === 'priceAsc') return results.sort((a, b) => a.price - b.price);
    if (query.sort === 'priceDesc') return results.sort((a, b) => b.price - a.price);
    return results;
  }
  setSearch(value: string): void {
    this.searchTerm.set(value);
  }
  toggleFavorite(id: number): void {
    this.favorites.update((items) =>
      items.includes(id) ? items.filter((item) => item !== id) : [...items, id],
    );
  }

  replace(products: Product[]): void {
    this.catalog.set(products);
  }
  update(product: Product): void {
    this.catalog.update((items) => items.map((item) => (item.id === product.id ? product : item)));
  }
  add(product: Product): void {
    this.catalog.update((items) => [...items, product]);
  }
  remove(productId: number): void {
    this.catalog.update((items) =>
      items.map((item) => (item.id === productId ? { ...item, active: false } : item)),
    );
  }
  byId(id: number): Product | undefined {
    return this.products().find((product) => product.id === id);
  }
  adjustStock(id: number, amount: number): boolean {
    const product = this.byId(id);
    if (!product || product.stock + amount < 0) return false;
    this.update({ ...product, stock: product.stock + amount });
    return true;
  }
  reserve(items: CartItem[]): boolean {
    const enough = items.every(
      (cartItem) => (this.byId(cartItem.id)?.stock ?? 0) >= cartItem.quantity,
    );
    if (!enough) return false;
    items.forEach((cartItem) => this.adjustStock(cartItem.id, -cartItem.quantity));
    return true;
  }
}
