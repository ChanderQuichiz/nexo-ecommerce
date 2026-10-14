import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly notificationsUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  downloadReceipt(orderId: string): Observable<Blob> {
    return this.http.get(`${this.notificationsUrl}/generate-receipt/${orderId}`, {
      responseType: 'blob',
    });
  }
}
