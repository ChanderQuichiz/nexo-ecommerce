import { Injectable, signal } from '@angular/core';

export interface CustomerProfile {
  name: string;
  email: string;
  address: string;
  city: string;
  zip: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  readonly profile = signal<CustomerProfile>({
    name: '',
    email: '',
    address: '',
    city: '',
    zip: '',
  });
  save(data: CustomerProfile): void {
    this.profile.set(data);
  }
}
