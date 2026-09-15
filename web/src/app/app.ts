import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { GlobalNotificationsComponent } from './components/global-notifications.component';

@Component({
  imports: [RouterOutlet, GlobalNotificationsComponent],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {}
