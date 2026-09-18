import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../core/loading.service';
import { ToastService } from '../core/toast.service';

@Component({
  selector: 'app-global-notifications',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (loadingService.isLoading()) {
      <div class="fixed inset-0 bg-white/50 z-[9999] flex items-center justify-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    }

    @if (toastService.toast(); as t) {
      <div class="fixed bottom-5 right-5 z-[10000] p-4 rounded-lg shadow-lg text-white font-medium"
           [class.bg-green-600]="t.type === 'success'"
           [class.bg-red-600]="t.type === 'error'">
        {{ t.message }}
      </div>
    }
  `
})
export class GlobalNotificationsComponent {
  loadingService = inject(LoadingService);
  toastService = inject(ToastService);
}
