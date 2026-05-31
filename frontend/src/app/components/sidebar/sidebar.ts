import { Component } from '@angular/core';

import { CommonModule }
from '@angular/common';

import { RouterModule }
from '@angular/router';

import { Router }
from '@angular/router';

@Component({
  selector: 'app-sidebar',

  standalone: true,

  imports: [
    CommonModule,
    RouterModule
  ],

  templateUrl: './sidebar.html',

  styleUrls: ['./sidebar.css']
})

export class Sidebar {

  constructor(
    private router: Router
  ) {}

  logout(): void {

  localStorage.removeItem('token');

  localStorage.removeItem('role');

  this.router.navigate(['/']);
}
}