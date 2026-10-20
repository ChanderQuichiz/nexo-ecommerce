import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product } from './models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly catalogUrl = `${environment.apiUrl}/catalog`;

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.catalogUrl}/admin`);
  }

  getAvailableProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.catalogUrl);
  }

  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.catalogUrl}/${id}`);
  }

  addProduct(product: Omit<Product, 'id'>, imageFile?: File): Observable<Product> {
    const formData = new FormData();
    formData.append(
      'product',
      new Blob(
        [
          JSON.stringify({
            name: product.name,
            description: product.description,
            price: product.price,
            stock: product.stock,
            category: product.category,
          }),
        ],
        { type: 'application/json' }
      )
    );
    if (imageFile) formData.append('image', imageFile);
    return this.http.post<Product>(this.catalogUrl, formData);
  }

  updateProduct(product: Product, imageFile?: File): Observable<Product> {
    const formData = new FormData();
    formData.append(
      'product',
      new Blob(
        [
          JSON.stringify({
            name: product.name,
            description: product.description,
            price: product.price,
            stock: product.stock,
            category: product.category,
          }),
        ],
        { type: 'application/json' }
      )
    );
    if (imageFile) formData.append('image', imageFile);
    return this.http.put<Product>(`${this.catalogUrl}/${product.id}`, formData);
  }

  toggleActive(productId: string): Observable<void> {
    return this.http.patch<void>(`${this.catalogUrl}/${productId}/active`, {});
  }
}
