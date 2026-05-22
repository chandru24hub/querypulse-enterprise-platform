import {
  Component,
  OnInit,
  ChangeDetectorRef,
  ChangeDetectionStrategy
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse
} from '@angular/common/http';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {

  pendingUsers: any[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPendingUsers();
  }

  loadPendingUsers(): void {

    const token = localStorage.getItem('token');

    if (!token) {
      this.isLoading = false;
      this.errorMessage = 'Not logged in. Please log in again.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any>(
      'http://localhost:8080/api/admin/pending-users',
      { headers }
    ).subscribe({

      next: (response: any) => {
        this.pendingUsers = Array.isArray(response)
          ? response
          : (response.data ?? response.users ?? []);
        this.isLoading = false;
        this.cdr.detectChanges();  // ← force UI update
      },

      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        if (error.status === 401) {
          this.errorMessage = 'Session expired. Please log in again.';
        } else if (error.status === 403) {
          this.errorMessage = 'Access denied. Admin only.';
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot reach server. Is the backend running?';
        } else {
          this.errorMessage = `Error ${error.status}: Could not load users.`;
        }
        this.cdr.detectChanges();  // ← force UI update on error too
        console.error('API ERROR:', error);
      }
    });
  }

  approveUser(userId: any): void {

    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.post(
      `http://localhost:8080/api/admin/approve-user/${userId}`,
      {},
      { headers }
    ).subscribe({
      next: () => {
        this.pendingUsers = this.pendingUsers.filter(u => u.id !== userId);
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('Failed to approve user. Please try again.');
        console.error(err);
      }
    });
  }
}