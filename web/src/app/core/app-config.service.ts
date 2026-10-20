import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export interface AppConfig {
  apiUrl: string;
  stripePublicKey: string;
  shippingFee: number;
}

@Injectable({
  providedIn: 'root',
})
export class AppConfigService {
  private config: AppConfig | null = null;

  constructor(private http: HttpClient) {}

  /**
   * Carga sincrónicamente/asincrónicamente el archivo assets/config.json
   * antes del arranque de la aplicación usando APP_INITIALIZER.
   */
  loadConfig(): Promise<AppConfig> {
    return firstValueFrom(
      this.http.get<AppConfig>('/assets/config.json')
    )
      .then((data) => {
        this.config = data;
        return data;
      })
      .catch((error) => {
        console.warn('No se pudo cargar assets/config.json, usando valores fallback:', error);
        const fallback: AppConfig = {
          apiUrl: 'http://localhost:8082/api',
          stripePublicKey: '',
          shippingFee: 0,
        };
        this.config = fallback;
        return fallback;
      });
  }

  get apiUrl(): string {
    return this.config?.apiUrl ?? 'http://localhost:8082/api';
  }

  get stripePublicKey(): string {
    return this.config?.stripePublicKey ?? '';
  }

  get shippingFee(): number {
    return this.config?.shippingFee ?? 0;
  }
}
