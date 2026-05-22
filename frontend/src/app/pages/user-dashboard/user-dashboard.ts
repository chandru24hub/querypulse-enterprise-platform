import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { Router } from '@angular/router';

@Component({
  selector: 'app-user-dashboard',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl: './user-dashboard.html',

  styleUrl: './user-dashboard.css'
})
export class UserDashboard {

  constructor(
    private router: Router
  ) {}

  logout(): void {

    localStorage.removeItem('token');

    localStorage.removeItem('role');

    this.router.navigate(['/']);
  }
}