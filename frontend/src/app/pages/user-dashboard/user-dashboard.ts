import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { DatabaseService } from '../../services/database.service';
import { ToastComponent } from '../../shared/toast/toast';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.css',
})
export class UserDashboard implements OnInit {
  databases: any[] = [];
  isLoading = false;

  selectedDb: any = null;
  selectedHealth: any = null;
  selectedAlerts: any[] = [];
  detailLoading = false;

  constructor(
    private databaseService: DatabaseService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadDatabases();
  }

  /* -------- Greeting from JWT -------- */
  get userName(): string {
    try {
      const token = localStorage.getItem('token');
      if (!token) return 'there';
      const payload = JSON.parse(atob(token.split('.')[1]));
      const sub: string = payload.sub || '';
      // Use the part before "@" of the email as a friendly name
      const name = sub.includes('@') ? sub.split('@')[0] : sub;
      return name ? name.charAt(0).toUpperCase() + name.slice(1) : 'there';
    } catch {
      return 'there';
    }
  }

  get initials(): string {
    const n = this.userName;
    return (n.charAt(0) || 'U').toUpperCase();
  }

  /* -------- Aggregate stats -------- */
  get connectedCount(): number {
    return this.databases.filter((d) => d.connectionStatus === 'CONNECTED').length;
  }
  get issuesCount(): number {
    return this.databases.filter((d) => d.connectionStatus === 'FAILED').length;
  }
  get monitoredCount(): number {
    return this.databases.filter((d) => d.monitoringEnabled).length;
  }
  get healthLabel(): string {
    if (this.databases.length === 0) return '—';
    if (this.issuesCount > 0) return 'Degraded';
    if (this.connectedCount === 0) return 'Unknown';
    return 'Operational';
  }
  get healthOk(): boolean {
    return this.databases.length > 0 && this.issuesCount === 0 && this.connectedCount > 0;
  }

  /* -------- Data loading -------- */
  loadDatabases(): void {
    this.isLoading = true;
    this.databaseService.getAllDatabases().subscribe({
      next: (response: any) => {
        this.databases = Array.isArray(response) ? response : response?.data ?? [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
        this.toastService.showError('Failed to load databases');
      },
    });
  }

  viewHealth(db: any): void {
    this.selectedDb = db;
    this.selectedHealth = null;
    this.selectedAlerts = [];
    this.detailLoading = true;

    let healthLoaded = false;
    let alertsLoaded = false;

    this.databaseService.getHealth(db.id).subscribe({
      next: (response) => {
        this.selectedHealth = response;
        healthLoaded = true;
        if (alertsLoaded) this.detailLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Health load failed:', error);
        healthLoaded = true;
        this.selectedHealth = null;
        if (alertsLoaded) this.detailLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.databaseService.getAlerts(db.id).subscribe({
      next: (response: any) => {
        this.selectedAlerts = Array.isArray(response) ? response : [];
        alertsLoaded = true;
        if (healthLoaded) this.detailLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Alerts load failed:', error);
        this.selectedAlerts = [];
        alertsLoaded = true;
        if (healthLoaded) this.detailLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  closeDetail(): void {
    this.selectedDb = null;
    this.selectedHealth = null;
    this.selectedAlerts = [];
  }

  downloadPdf(databaseId: string): void {
    this.databaseService.exportPdf(databaseId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${this.selectedDb?.displayName}-report.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.toastService.showSuccess('PDF exported successfully');
      },
      error: (error) => {
        console.error('PDF export failed', error);
        this.toastService.showError('Failed to export PDF');
      },
    });
  }

  downloadExcel(databaseId: string): void {
    this.databaseService.exportExcel(databaseId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${this.selectedDb?.displayName}-report.xlsx`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.toastService.showSuccess('Excel exported successfully');
      },
      error: (error) => {
        console.error('Excel export failed', error);
        this.toastService.showError('Failed to export Excel');
      },
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/']);
  }
}
