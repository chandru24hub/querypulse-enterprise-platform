import { Component, OnInit } from '@angular/core';

import { CommonModule }
from '@angular/common';

import { FormsModule }
from '@angular/forms';

import { HttpClient }
from '@angular/common/http';

import { Sidebar }
from '../../components/sidebar/sidebar';

@Component({
  selector: 'app-database-management',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    Sidebar
  ],

  templateUrl:
    './database-management.html',

  styleUrls: [
    './database-management.css'
  ]
})

export class DatabaseManagement
implements OnInit {

  databases: any[] = [];

  databaseForm = {

    displayName: '',

    databaseType: 'POSTGRESQL',

    host: '',

    port: 5432,

    databaseName: '',

    username: '',

    password: ''
  };

  constructor(
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    this.loadDatabases();
  }

  createDatabase(): void {

  if (
    !this.databaseForm.displayName ||
    !this.databaseForm.host ||
    !this.databaseForm.databaseName ||
    !this.databaseForm.username ||
    !this.databaseForm.password
  ) {

    alert(
      'Please fill all required fields'
    );

    return;
  }

  this.http.post(

      'http://localhost:8080/api/databases',

      this.databaseForm

    ).subscribe({

      next: () => {

        alert(
          'Database Registered Successfully'
        );

        this.resetForm();

        this.loadDatabases();
      },

      error: (error) => {

        console.error(error);

        alert(
          'Failed to Register Database'
        );
      }
    });
  }

  loadDatabases(): void {

    this.http.get<any[]>(

      'http://localhost:8080/api/databases'

    ).subscribe({

      next: (response) => {

        this.databases = response;
      },

      error: (error) => {

        console.error(error);
      }
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

      password: ''
    };
  }
}