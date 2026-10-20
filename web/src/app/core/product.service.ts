import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from './models';
import { AppConfigService } from './app-config.service';

@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(
    private http: HttpClient,
    private configService: AppConfigService
  ) {}

  private get catalogUrl(): string {
    return `${this.configService.apiUrl}/catalog`;
  }

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
