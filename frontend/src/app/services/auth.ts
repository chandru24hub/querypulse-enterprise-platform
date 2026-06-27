import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';

/**
 * Single source of truth for authentication: login, token/role storage,
 * and JWT-derived display helpers. Components should depend on this service
 * rather than reading localStorage or decoding the token themselves.
 */
@Injectable({
  providedIn: 'root',
})
export class Auth {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data);
  }

  /** Persists the JWT and the role decoded from it. */
  storeSession(token: string): void {
    localStorage.setItem('token', token);
    const role = this.decodeToken(token)?.role ?? '';
    localStorage.setItem('role', role);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string {
    return localStorage.getItem('role') ?? '';
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /** Friendly first name derived from the email subject in the JWT. */
  getUserName(): string {
    const sub: string = this.decodeToken(this.getToken())?.sub ?? '';
    const name = sub.includes('@') ? sub.split('@')[0] : sub;
    return name ? name.charAt(0).toUpperCase() + name.slice(1) : 'there';
  }

  getInitials(): string {
    return (this.getUserName().charAt(0) || 'U').toUpperCase();
  }

  private decodeToken(token: string | null): any | null {
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }
}
