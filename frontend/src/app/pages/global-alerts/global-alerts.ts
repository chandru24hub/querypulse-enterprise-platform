import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { DatabaseService } from '../../services/database.service';
import { ToastComponent } from '../../shared/toast/toast';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-global-alerts',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  templateUrl: './global-alerts.html',
  styleUrl: './global-alerts.css',
})
export class GlobalAlertsComponent implements OnInit {
  allAlerts: any[] = [];
  filteredAlerts: any[] = [];
  isLoading = false;

  severityFilter = 'ALL';
  alertTypeFilter = 'ALL';
  searchTerm = '';
  sortBy = 'date_desc';

  severities = ['ALL', 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];
  alertTypes: string[] = [];
  currentPage = 1;
  pageSize = 20;

  constructor(
    private databaseService: DatabaseService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadAllAlerts();
  }

  get userName(): string {
    try {
      const token = localStorage.getItem('token');
      if (!token) return 'there';
      const payload = JSON.parse(atob(token.split('.')[1]));
      const sub: string = payload.sub || '';
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

  get criticalCount(): number {
    return this.allAlerts.filter((a) => a.severity === 'CRITICAL').length;
  }

  get highCount(): number {
    return this.allAlerts.filter((a) => a.severity === 'HIGH').length;
  }

  get totalCount(): number {
    return this.allAlerts.length;
  }

  get paginatedAlerts(): any[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredAlerts.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredAlerts.length / this.pageSize);
  }

  loadAllAlerts(): void {
    this.isLoading = true;
    this.databaseService.getAllAlerts().subscribe({
      next: (response: any) => {
        this.allAlerts = Array.isArray(response) ? response : response?.data ?? [];
        this.extractAlertTypes();
        this.applyFilters();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
        this.toastService.showError('Failed to load alerts');
      },
    });
  }

  extractAlertTypes(): void {
    const types = new Set(this.allAlerts.map((a) => a.alertType));
    this.alertTypes = ['ALL', ...Array.from(types)];
  }

  applyFilters(): void {
    let filtered = [...this.allAlerts];

    if (this.severityFilter !== 'ALL') {
      filtered = filtered.filter((a) => a.severity === this.severityFilter);
    }

    if (this.alertTypeFilter !== 'ALL') {
      filtered = filtered.filter((a) => a.alertType === this.alertTypeFilter);
    }

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (a) =>
          a.databaseName.toLowerCase().includes(term) ||
          a.message.toLowerCase().includes(term) ||
          a.alertType.toLowerCase().includes(term),
      );
    }

    if (this.sortBy === 'date_desc') {
      filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    } else if (this.sortBy === 'date_asc') {
      filtered.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
    } else if (this.sortBy === 'severity') {
      const severityOrder: Record<string, number> = { CRITICAL: 0, HIGH: 1, MEDIUM: 2, LOW: 3 };
      filtered.sort(
        (a, b) => (severityOrder[a.severity] || 4) - (severityOrder[b.severity] || 4),
      );
    }

    this.filteredAlerts = filtered;
    this.currentPage = 1;
    this.cdr.detectChanges();
  }

  onSeverityChange(severity: string): void {
    this.severityFilter = severity;
    this.applyFilters();
  }

  onAlertTypeChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.alertTypeFilter = value;
    this.applyFilters();
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.applyFilters();
  }

  onSortChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.sortBy = value;
    this.applyFilters();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.cdr.detectChanges();
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/']);
  }
}
