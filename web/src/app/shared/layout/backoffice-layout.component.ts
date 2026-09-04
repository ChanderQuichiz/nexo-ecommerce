import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-backoffice-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './backoffice-layout.component.html',
})
export class BackofficeLayoutComponent {
  protected readonly auth = inject(AuthService);
}
