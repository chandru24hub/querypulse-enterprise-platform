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
  DatabaseFleet
} from '../../components/database-fleet/database-fleet';

import {
  DatabaseService
} from '../../services/database.service';

import {
  ToastComponent
} from '../../shared/toast/toast';

import {
  ToastService
} from '../../shared/toast/toast.service';

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
    Sidebar,
    ToastComponent,
    DatabaseFleet
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

  slowQueries: any[] = [];

  alerts: any[] = [];

  connectionChart: any;

  sizeChart: any;

statusChart: any;

  selectedDatabaseName = '';

  constructor(

    private databaseService:
      DatabaseService,

    private toastService:
      ToastService

  ) {

    Chart.register(
      ...registerables
    );
  }

  ngOnInit(): void {}

  onDatabasesChanged(databases: any[]): void {
    this.databases = databases;
  }

  viewHealth(
    db: any
  ): void {

    const databaseId = db.id;

    this.selectedDatabaseName = db.displayName;

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

this.loadSlowQueries(
  databaseId
);

this.loadAlerts(
    databaseId
);
        },

        error: (error) => {

          console.error(
            error
          );

          this.toastService.showError(
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

  setTimeout(() => {

      //-----------------------------
      // Connection Chart
      //-----------------------------

      const connectionCanvas =
          document.getElementById(
              'connectionChart'
          ) as HTMLCanvasElement;

      if (connectionCanvas) {

          if (this.connectionChart) {

              this.connectionChart.destroy();

          }

          this.connectionChart =
              new Chart(
                  connectionCanvas,
                  {

                      type: 'line',

                      data: {

                          labels,

                          datasets: [

                              {

                                  label: 'Active Connections',
                                  data: activeConnections,
                                  tension: 0.35,
                                  borderColor: '#4f46e5',
                                  backgroundColor: 'rgba(79,70,229,0.12)',
                                  borderWidth: 2,
                                  fill: true,
                                  pointRadius: 2,
                                  pointHoverRadius: 5,
                                  pointBackgroundColor: '#4f46e5'

                              }

                          ]

                      },

                      options: this.chartOptions()

                  }

              );

      }

      //-----------------------------
      // Size Chart
      //-----------------------------

      const sizeCanvas =
          document.getElementById(
              'sizeChart'
          ) as HTMLCanvasElement;

      if (sizeCanvas) {

          if (this.sizeChart) {

              this.sizeChart.destroy();

          }

          this.sizeChart =
              new Chart(
                  sizeCanvas,
                  {

                      type: 'line',

                      data: {

                          labels,

                          datasets: [

                              {

                                  label: 'Database Size (kB)',
                                  data: databaseSizes,
                                  tension: 0.35,
                                  borderColor: '#06b6d4',
                                  backgroundColor: 'rgba(6,182,212,0.12)',
                                  borderWidth: 2,
                                  fill: true,
                                  pointRadius: 2,
                                  pointHoverRadius: 5,
                                  pointBackgroundColor: '#06b6d4'

                              }

                          ]

                      },

                      options: this.chartOptions()

                  }

              );

      }

      //-----------------------------
      // Status Chart
      //-----------------------------

      const statusCanvas =
          document.getElementById(
              'statusChart'
          ) as HTMLCanvasElement;

      if (statusCanvas) {

          if (this.statusChart) {

              this.statusChart.destroy();

          }

          this.statusChart =
              new Chart(
                  statusCanvas,
                  {

                      type: 'line',

                      data: {

                          labels,

                          datasets: [

                              {

                                  label: 'Availability',
                                  data: statuses,
                                  tension: 0.35,
                                  borderColor: '#10b981',
                                  backgroundColor: 'rgba(16,185,129,0.12)',
                                  borderWidth: 2,
                                  fill: true,
                                  stepped: true,
                                  pointRadius: 2,
                                  pointHoverRadius: 5,
                                  pointBackgroundColor: '#10b981'

                              }

                          ]

                      },

                      options: this.chartOptions()

                  }

              );

      }

  }, 100);

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

loadSlowQueries(
  databaseId: string
): void {

  this.databaseService
      .getSlowQueries(
          databaseId
      )
      .subscribe({

          next: (response) => {

              this.slowQueries =
                  response;

          },

          error: (error) => {

              console.error(
                  error
              );

          }

      });

}

loadAlerts(
  databaseId: string
): void {

  console.log(
    "Database ID sent:",
    databaseId
  );

  this.databaseService
      .getAlerts(databaseId)
      .subscribe({

        next: (response) => {

          console.log(
            "Alerts API response:",
            response
          );

          this.alerts = response;

          console.log(
            "alerts length:",
            this.alerts.length
          );

        },

        error: (error) => {

          console.error(
            error
          );

        }

      });

}

  chartOptions(): any {
    return {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: {
          display: true,
          labels: {
            color: '#5b6675',
            font: { family: 'Inter, sans-serif', size: 12, weight: '600' },
            usePointStyle: true,
            pointStyle: 'circle',
            boxWidth: 8,
            padding: 16,
          },
        },
        tooltip: {
          backgroundColor: '#0f172a',
          titleColor: '#fff',
          bodyColor: '#cbd5e1',
          padding: 12,
          cornerRadius: 8,
          displayColors: false,
          titleFont: { family: 'Inter, sans-serif' },
          bodyFont: { family: 'Inter, sans-serif' },
        },
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11 }, maxRotation: 0, autoSkip: true, maxTicksLimit: 8 },
        },
        y: {
          grid: { color: '#eef1f6' },
          border: { display: false },
          ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11 } },
        },
      },
    };
  }

}