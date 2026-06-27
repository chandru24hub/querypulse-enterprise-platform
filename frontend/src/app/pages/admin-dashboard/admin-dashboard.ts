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
  HttpErrorResponse
} from '@angular/common/http';

import { Sidebar }
from '../../components/sidebar/sidebar';

import {
  ToastComponent
} from '../../shared/toast/toast';

import {
  ToastService
} from '../../shared/toast/toast.service';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-admin-dashboard',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    Sidebar,
    ToastComponent
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

  private adminUrl = `${environment.apiUrl}/admin`;

  showApprovalModal = false;

  showRejectModal = false;

  selectedUserId = '';

  selectedUserForApproval: any = null;

  approvalRole = 'USER';

  approvalMessage = '';

  rejectionReason = '';

  pendingSearch = '';

  filteredPendingUsers: any[] = [];

  constructor(

    private http: HttpClient,

    private cdr: ChangeDetectorRef,

    private toastService: ToastService

  ) {}

  ngOnInit(): void {

    this.loadPendingUsers();

    this.loadApprovedUsers();

    this.loadRejectedUsers();
  }

  /*
    LOAD PENDING USERS
  */

  loadPendingUsers(): void {

    this.isLoading = true;

    this.http.get<any[]>(

      `${this.adminUrl}/pending-users`

    ).subscribe({

      next: (response: any) => {

        this.pendingUsers =
        Array.isArray(response)
       ? response
      : response.data ?? [];

        this.filteredPendingUsers =
       [...this.pendingUsers];
        this.isLoading = false;

        this.cdr.detectChanges();
      },

      error: (

        error: HttpErrorResponse

      ) => {

        console.error(error);

        this.errorMessage =

          'Failed to load pending users';

        this.toastService.showError(
          'Failed to load pending users'
        );

        this.isLoading = false;
      }
    });
  }

  /*
    LOAD APPROVED USERS
  */

  loadApprovedUsers(): void {

    this.http.get<any[]>(

      `${this.adminUrl}/approved-users`

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

        this.toastService.showError(
          'Failed to load approved users'
        );
      }
    });
  }

  /*
    LOAD REJECTED USERS
  */

  loadRejectedUsers(): void {

    this.http.get<any[]>(

      `${this.adminUrl}/rejected-users`

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

        this.toastService.showError(
          'Failed to load rejected users'
        );
      }
    });
  }

  /*
    OPEN APPROVAL MODAL
  */

  openApprovalModal(user: any): void {

    this.selectedUserForApproval = user;

    this.selectedUserId = user.id;

    this.approvalRole = 'USER';

    this.approvalMessage = '';

    this.showApprovalModal = true;
  }

  /*
    CLOSE APPROVAL MODAL
  */

  closeApprovalModal(): void {

    this.showApprovalModal = false;

    this.selectedUserForApproval = null;

    this.approvalRole = 'USER';

    this.approvalMessage = '';
  }

  /*
    APPROVE USER
  */

  approveUser(): void {

    if (!this.selectedUserId) {
      this.toastService.showWarning('No user selected');
      return;
    }

    const approvalData = {
      role: this.approvalRole,
      message: this.approvalMessage || 'Welcome to QueryPulse!'
    };

    this.http.post(

      `${this.adminUrl}/approve-user/${this.selectedUserId}`,

      approvalData,

      {
        responseType: 'text'
      }

    ).subscribe({

      next: (response) => {

        this.toastService.showSuccess(
          'User approved successfully'
        );

        this.closeApprovalModal();

        this.refreshAllData();
      },

      error: (error) => {

        console.error(error);

        this.toastService.showError(
          'Failed to approve user'
        );
      }
    });
  }

  /*
    OPEN REJECT MODAL
  */

  openRejectModal(userId: string): void {

    this.selectedUserId = userId;

    this.showRejectModal = true;
  }

  /*
    CLOSE REJECT MODAL
  */

  closeRejectModal(): void {

    this.showRejectModal = false;

    this.rejectionReason = '';
  }

  /*
    REJECT USER
  */

  rejectUser(): void {

    if (!this.rejectionReason.trim()) {

      this.toastService.showWarning(
        'Please enter rejection reason'
      );

      return;
    }

    const rejectionData = {
      message: this.rejectionReason
    };

    this.http.post(

      `${this.adminUrl}/reject-user/${this.selectedUserId}`,

      rejectionData,

      {
        responseType: 'text'
      }

    ).subscribe({

      next: (response) => {

        this.toastService.showSuccess(
          'User rejected successfully'
        );

        this.closeRejectModal();

        this.refreshAllData();
      },

      error: (error) => {

        console.error(error);

        this.toastService.showError(
          'Failed to reject user'
        );
      }
    });
  }

 filterPendingUsers(): void {

  const search =
    this.pendingSearch
      .toLowerCase()
      .trim();

  this.filteredPendingUsers =
    this.pendingUsers.filter(user =>

      user.firstName
        ?.toLowerCase()
        .includes(search)

      ||

      user.lastName
        ?.toLowerCase()
        .includes(search)

      ||

      user.username
        ?.toLowerCase()
        .includes(search)

      ||

      user.email
        ?.toLowerCase()
        .includes(search)
    );
}

  refreshAllData(): void {

    this.loadPendingUsers();

    this.loadApprovedUsers();

    this.loadRejectedUsers();
  }
}