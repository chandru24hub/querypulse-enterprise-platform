import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RouterModule }
from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
  CommonModule,
 FormsModule,
  HttpClientModule,
  RouterModule
],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  onLogin(): void {

    this.loading = true;
    this.errorMessage = '';

    const payload = {
      email: this.email,
      password: this.password
    };

    this.http.post<any>(
      'http://localhost:8080/api/auth/login',
      payload
    ).subscribe({

      next: (response) => {

        const token = response.data.token;

        localStorage.setItem('token', token);

        const payloadBase64 = token.split('.')[1];

        const decodedPayload = JSON.parse(atob(payloadBase64));

        const role = decodedPayload.role;

        localStorage.setItem('role', role);

        this.loading = false;

        if (role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.router.navigate(['/user-dashboard']);
        }
      },

      error: (error: any) => {
        this.errorMessage =
          error?.error?.message || 'Login failed. Please try again.';
        this.loading = false;
      }
    });
  }
}