export type UserRole = 'cliente' | 'operador' | 'administrador';

export interface SessionUser {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}
