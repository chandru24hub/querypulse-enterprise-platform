import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Router } from '@angular/router';

import { AuthService }
from '../../services/auth.service';

@Component({
    selector: 'app-login',

    standalone: true,

    imports: [
        CommonModule,
        FormsModule
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
        private authService: AuthService,
        private router: Router
    ) {

    }

    onLogin() {

        this.loading = true;

        this.errorMessage = '';

        const payload = {

            email: this.email,

            password: this.password
        };

        this.authService
            .login(payload)
            .subscribe({

                next: (response) => {

                    localStorage.setItem(
                        'token',
                        response.data.token
                    );

                    localStorage.setItem(
                        'role',
                        response.data.role
                    );

                    alert('Login Successful');

                    this.router.navigate([
                        '/dashboard'
                    ]);
                },

                error: (error) => {

                    this.errorMessage =
                        error.error.message;

                    this.loading = false;
                },

                complete: () => {

                    this.loading = false;
                }
            });
    }
}