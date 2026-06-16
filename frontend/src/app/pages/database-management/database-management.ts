import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Sidebar
} from '../../components/sidebar/sidebar';

import {
  DatabaseService
} from '../../services/database.service';

import {
  Chart,
  registerables
} from 'chart.js';

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

  selectedHealth: any = null;

  historyData: any[] = [];

  selectedQueryAnalysis: any = null;

  connectionChart: any;

  sizeChart: any;

statusChart: any;

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

    private databaseService:
      DatabaseService

  ) {

    Chart.register(
      ...registerables
    );
  }

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

    this.databaseService
      .createDatabase(
        this.databaseForm
      )
      .subscribe({

        next: () => {

          alert(
            'Database Registered Successfully'
          );

          this.resetForm();

          this.loadDatabases();
        },

        error: (error) => {

          console.error(
            error
          );

          alert(
            'Failed to Register Database'
          );
        }

      });
  }

  loadDatabases(): void {

    this.databaseService
      .getAllDatabases()
      .subscribe({

        next: (response) => {

          this.databases =
            response;
        },

        error: (error) => {

          console.error(
            error
          );
        }

      });
  }

  testConnection(
    databaseId: string
  ): void {

    this.databaseService
      .testConnection(
        databaseId
      )
      .subscribe({

        next: (response) => {

          if (
            response.success
          ) {

            alert(
              '🟢 Database Connection Successful'
            );

          } else {

            alert(
              '🔴 '
              +
              response.message
            );
          }

          this.loadDatabases();
        },

        error: (error) => {

          console.error(
            error
          );

          alert(
            'Connection Test Failed'
          );
        }

      });
  }

  viewHealth(
    databaseId: string
  ): void {

    this.databaseService
      .getHealth(
        databaseId
      )
      .subscribe({

        next: (response) => {

          this.selectedHealth =
            response;

          this.loadHistory(
            databaseId
          );
          this.loadQueryAnalysis(
         databaseId
        );
        },

        error: (error) => {

          console.error(
            error
          );

          alert(
            'Failed to load health information'
          );
        }

      });
  }

  loadHistory(
    databaseId: string
  ): void {

    this.databaseService
      .getHistory(
        databaseId
      )
      .subscribe({

        next: (response) => {

          this.historyData =
            response;

          this.renderChart();
        },

        error: (error) => {

          console.error(
            error
          );
        }

      });
  }

renderChart(): void {

  const labels =
      this.historyData.map(
          item => item.recordedAt
      );

  const activeConnections =
      this.historyData.map(
          item => item.activeConnections
      );

  const databaseSizes =
      this.historyData.map(
          item =>
              Number(
                  item.databaseSize
                      .replace(' kB', '')
              )
      );

  const statuses =
      this.historyData.map(
          item =>
              item.connectionStatus === 'CONNECTED'
                  ? 1
                  : 0
      );

  //--------------------------------

  if (this.connectionChart) {

      this.connectionChart.destroy();

  }

  this.connectionChart =
      new Chart(
          'connectionChart',
          {
              type: 'line',

              data: {

                  labels,

                  datasets: [

                      {

                          label:
                              'Active Connections',

                          data:
                              activeConnections,

                          tension: 0.3

                      }

                  ]

              }

          }

      );

  //--------------------------------

  if (this.sizeChart) {

      this.sizeChart.destroy();

  }

  this.sizeChart =
      new Chart(
          'sizeChart',
          {

              type: 'line',

              data: {

                  labels,

                  datasets: [

                      {

                          label:
                              'Database Size (kB)',

                          data:
                              databaseSizes,

                          tension: 0.3

                      }

                  ]

              }

          }

      );

  //--------------------------------

  if (this.statusChart) {

      this.statusChart.destroy();

  }

  this.statusChart =
      new Chart(
          'statusChart',
          {

              type: 'line',

              data: {

                  labels,

                  datasets: [

                      {

                          label:
                              'Availability',

                          data:
                              statuses,

                          tension: 0.3

                      }

                  ]

              }

          }

      );

}

loadQueryAnalysis(
  databaseId: string
): void {

  this.databaseService
      .getQueryAnalysis(
          databaseId
      )
      .subscribe({

          next: (response) => {

              this.selectedQueryAnalysis =
                  response;

          },

          error: (error) => {

              console.error(
                  error
              );

          }

      });

}

  resetForm(): void {

    this.databaseForm = {

      displayName: '',

      databaseType:
        'POSTGRESQL',

      host: '',

      port: 5432,

      databaseName: '',

      username: '',

      password: ''
    };
  }

}