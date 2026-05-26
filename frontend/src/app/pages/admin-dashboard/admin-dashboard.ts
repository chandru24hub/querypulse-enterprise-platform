import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule }
from '@angular/common';

import { FormsModule }
from '@angular/forms';

import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse
} from '@angular/common/http';

import { Sidebar }
from '../../components/sidebar/sidebar';

@Component({
  selector: 'app-admin-dashboard',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    Sidebar
  ],

  templateUrl: './admin-dashboard.html',

  styleUrls: ['./admin-dashboard.css']
})

export class AdminDashboard
implements OnInit {

  pendingUsers: any[] = [];

  approvedUsers: any[] = [];

  rejectedUsers: any[] = [];

  isLoading = false;

  errorMessage = '';

  showRejectModal = false;

  selectedUserId = '';

  rejectionReason = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.loadPendingUsers();

    this.loadApprovedUsers();

    this.loadRejectedUsers();
  }

  getHeaders(): HttpHeaders {

    const token =
      localStorage.getItem('token');

    return new HttpHeaders({
      Authorization:
        `Bearer ${token}`
    });
  }

  loadPendingUsers(): void {

    this.isLoading = true;

    this.http.get<any[]>(
      'http://localhost:8080/api/admin/pending-users',
      {
        headers: this.getHeaders()
      }
    ).subscribe({

      next: (response: any) => {

        this.pendingUsers =
          Array.isArray(response)
          ? response
          : response.data ?? [];

        this.isLoading = false;

        this.cdr.detectChanges();
      },

      error: (
        error: HttpErrorResponse
      ) => {

        console.error(error);

        this.errorMessage =
          'Failed to load pending users';

        this.isLoading = false;
      }
    });
  }

  loadApprovedUsers(): void {

    this.http.get<any[]>(
      'http://localhost:8080/api/admin/approved-users',
      {
        headers: this.getHeaders()
      }
    ).subscribe({

      next: (response: any) => {

        this.approvedUsers =
          Array.isArray(response)
          ? response
          : response.data ?? [];

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(error);
      }
    });
  }

  loadRejectedUsers(): void {

    this.http.get<any[]>(
      'http://localhost:8080/api/admin/rejected-users',
      {
        headers: this.getHeaders()
      }
    ).subscribe({

      next: (response: any) => {

        this.rejectedUsers =
          Array.isArray(response)
          ? response
          : response.data ?? [];

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(error);
      }
    });
  }

  approveUser(userId: string): void {

    this.http.post(
      `http://localhost:8080/api/admin/approve-user/${userId}`,
      {},
      {
        headers: this.getHeaders(),

        responseType: 'text'
      }
    ).subscribe({

      next: () => {

        alert(
          'User Approved Successfully'
        );

        this.refreshAllData();
      },

      error: (error) => {

        console.error(error);

        alert(
          'Failed to approve user'
        );
      }
    });
  }

  openRejectModal(userId: string): void {

    this.selectedUserId = userId;

    this.showRejectModal = true;
  }

  closeRejectModal(): void {

    this.showRejectModal = false;

    this.rejectionReason = '';
  }

  rejectUser(): void {

    if (!this.rejectionReason.trim()) {

      alert(
        'Please enter rejection reason'
      );

      return;
    }

    this.http.post(
      `http://localhost:8080/api/admin/reject-user/${this.selectedUserId}?reason=${this.rejectionReason}`,
      {},
      {
        headers: this.getHeaders(),

        responseType: 'text'
      }
    ).subscribe({

      next: () => {

        alert(
          'User Rejected Successfully'
        );

        this.closeRejectModal();

        this.refreshAllData();
      },

      error: (error) => {

        console.error(error);

        alert(
          'Failed to reject user'
        );
      }
    });
  }

  refreshAllData(): void {

    this.loadPendingUsers();

    this.loadApprovedUsers();

    this.loadRejectedUsers();
  }
}