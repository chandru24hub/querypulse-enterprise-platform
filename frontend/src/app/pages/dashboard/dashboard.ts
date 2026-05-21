import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { AdminService }
from '../../services/admin.service';

@Component({
  selector: 'app-dashboard',

  standalone: true,

  imports: [CommonModule],

  templateUrl: './dashboard.html',

  styleUrls: ['./dashboard.css']
})
export class Dashboard
implements OnInit {

  pendingUsers: any[] = [];

  loading = false;

  constructor(
    private adminService: AdminService
  ) {}

  ngOnInit(): void {

    this.loadPendingUsers();
  }

  loadPendingUsers(): void {

    this.loading = true;

    this.adminService
      .getPendingUsers()
      .subscribe({

        next: (response) => {

          this.pendingUsers = response.data;

          this.loading = false;
        },

        error: (error) => {

          console.log(error);

          this.loading = false;
        }
      });
  }

  approveUser(
    userId: string
  ): void {

    this.adminService
      .approveUser(userId)
      .subscribe({

        next: () => {

          alert(
            'User approved successfully'
          );

          this.loadPendingUsers();
        },

        error: (error) => {

          console.log(error);
        }
      });
  }
}