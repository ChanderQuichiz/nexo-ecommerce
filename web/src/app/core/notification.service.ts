import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConfigService } from './app-config.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(
    private http: HttpClient,
    private configService: AppConfigService
  ) {}

  private get notificationsUrl(): string {
    return `${this.configService.apiUrl}/notifications`;
  }

  downloadReceipt(orderId: string): Observable<Blob> {
    return this.http.get(`${this.notificationsUrl}/generate-receipt/${orderId}`, {
      responseType: 'blob',
    });
  }
}
