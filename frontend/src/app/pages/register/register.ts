import { Component } from '@angular/core';

import { CommonModule }
from '@angular/common';

import { FormsModule }
from '@angular/forms';

import { HttpClient } from '@angular/common/http';

import { Router }
from '@angular/router';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-register',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './register.html',

  styleUrls: ['./register.css']
})

export class Register {

  firstName = '';

  lastName = '';

  username = '';

  email = '';

  password = '';

  confirmPassword = '';

  loading = false;

  errorMessage = '';

  successMessage = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  register(): void {

    this.errorMessage = '';

    this.successMessage = '';

    if (
      !this.firstName ||
      !this.lastName ||
      !this.username ||
      !this.email ||
      !this.password ||
      !this.confirmPassword
    ) {

      this.errorMessage =
        'Please fill all fields';

      return;
    }

    if (
      this.password !==
      this.confirmPassword
    ) {

      this.errorMessage =
        'Passwords do not match';

      return;
    }

    this.loading = true;

    const payload = {

      firstName: this.firstName,

      lastName: this.lastName,

      username: this.username,

      email: this.email,

      password: this.password
    };

    this.http.post<any>(
      `${environment.apiUrl}/auth/register`,
      payload
    ).subscribe({

      next: (response) => {

        alert(
          response.message ||
          'Registration successful. Wait for admin approval.'
        );

        this.successMessage =
          'Registration submitted for admin approval';

        this.loading = false;

        setTimeout(() => {

          this.router.navigate(['/']);

        }, 2000);
      },

      error: (error: any) => {

        console.error(error);

        this.errorMessage =
          error?.error?.message ||
          'Registration failed';

        this.loading = false;
      }
    });
  }

  goToLogin(): void {

    this.router.navigate(['/']);
  }
}