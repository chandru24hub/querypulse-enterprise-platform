import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  ChangeDetectorRef,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { DatabaseService } from '../../services/database.service';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-database-fleet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './database-fleet.html',
  styleUrl: './database-fleet.css',
})
export class DatabaseFleet implements OnInit {
  /** Emits the full database object whenever "Health" is clicked. */
  @Output() viewHealth = new EventEmitter<any>();

  /** Emits the current database list whenever it changes (load/create/delete). */
  @Output() databasesChanged = new EventEmitter<any[]>();

  databases: any[] = [];
  isLoading = false;
  isSubmitting = false;
  testingId: string | null = null;

  showForm = false;
  deleteTarget: any = null;
  isDeleting = false;

  databaseForm = {
    displayName: '',
    databaseType: 'POSTGRESQL',
    host: '',
    port: 5432,
    databaseName: '',
    username: '',
    password: '',
  };

  constructor(
    private databaseService: DatabaseService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadDatabases();
  }

  engineMeta(type: string): { label: string; cls: string } {
    switch (type) {
      case 'POSTGRESQL':
        return { label: 'PG', cls: 'fleet__engine--pg' };
      case 'AURORA_POSTGRESQL':
        return { label: 'AU', cls: 'fleet__engine--aurora' };
      case 'ORACLE':
        return { label: 'OR', cls: 'fleet__engine--oracle' };
      case 'MYSQL':
        return { label: 'MY', cls: 'fleet__engine--mysql' };
      default:
        return { label: '?', cls: 'fleet__engine--neutral' };
    }
  }

  loadDatabases(): void {
    this.isLoading = true;
    this.databaseService.getAllDatabases().subscribe({
      next: (response: any) => {
        this.databases = Array.isArray(response) ? response : response?.data ?? [];
        this.isLoading = false;
        this.databasesChanged.emit(this.databases);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
        this.toastService.showError('Failed to load databases');
      },
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  createDatabase(): void {
    if (
      !this.databaseForm.displayName ||
      !this.databaseForm.host ||
      !this.databaseForm.databaseName ||
      !this.databaseForm.username ||
      !this.databaseForm.password
    ) {
      this.toastService.showWarning('Please fill all required fields');
      return;
    }

    this.isSubmitting = true;

    this.databaseService.createDatabase(this.databaseForm).subscribe({
      next: () => {
        this.toastService.showSuccess('Database registered successfully');
        this.resetForm();
        this.isSubmitting = false;
        this.showForm = false;
        this.loadDatabases();
      },
      error: (error) => {
        console.error(error);
        this.isSubmitting = false;
        this.toastService.showError(
          error?.error?.message || 'Failed to register database',
        );
      },
    });
  }

  testConnection(db: any): void {
    this.testingId = db.id;
    this.databaseService.testConnection(db.id).subscribe({
      next: (response) => {
        this.testingId = null;
        if (response.success) {
          this.toastService.showSuccess('Database connection successful');
        } else {
          this.toastService.showError(response.message || 'Database connection failed');
        }
        this.loadDatabases();
      },
      error: (error) => {
        console.error(error);
        this.testingId = null;
        this.toastService.showError('Connection test failed');
      },
    });
  }

  requestHealth(db: any): void {
    this.viewHealth.emit(db);
  }

  openDeleteConfirm(db: any): void {
    this.deleteTarget = db;
  }

  closeDeleteConfirm(): void {
    this.deleteTarget = null;
  }

  confirmDelete(): void {
    if (!this.deleteTarget) return;

    this.isDeleting = true;

    this.databaseService.deleteDatabase(this.deleteTarget.id).subscribe({
      next: () => {
        this.toastService.showSuccess(
          `${this.deleteTarget.displayName} removed from monitoring`,
        );
        this.isDeleting = false;
        this.deleteTarget = null;
        this.loadDatabases();
      },
      error: (error) => {
        console.error(error);
        this.isDeleting = false;
        this.toastService.showError('Failed to remove database');
      },
    });
  }

  resetForm(): void {
    this.databaseForm = {
      displayName: '',
      databaseType: 'POSTGRESQL',
      host: '',
      port: 5432,
      databaseName: '',
      username: '',
      password: '',
    };
  }
}
