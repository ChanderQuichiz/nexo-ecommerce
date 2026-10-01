import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toast = signal<{ message: string; type: ToastType } | null>(null);

  show(message: string, type: ToastType = 'success') {
    this.toast.set({ message, type });
    setTimeout(() => this.toast.set(null), 3000);
  }
}
