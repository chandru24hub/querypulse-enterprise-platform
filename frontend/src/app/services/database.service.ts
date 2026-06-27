import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DatabaseService {

  private apiUrl =
    `${environment.apiUrl}/databases`;

  constructor(
    private http: HttpClient
  ) {}

  getAllDatabases(): Observable<any[]> {

    return this.http.get<any[]>(
      this.apiUrl
    );
  }

  createDatabase(
    database: any
  ): Observable<any> {

    return this.http.post(
      this.apiUrl,
      database
    );
  }

  deleteDatabase(
    databaseId: string
  ): Observable<any> {

    return this.http.delete(
      `${this.apiUrl}/${databaseId}`
    );
  }

  testConnection(
    databaseId: string
  ): Observable<any> {

    return this.http.post(

      `${this.apiUrl}/${databaseId}/test-connection`,

      {}

    );
  }

  getHealth(
    databaseId: string
  ): Observable<any> {

    return this.http.get(

      `${this.apiUrl}/${databaseId}/health`

    );
  }

  getHistory(
    databaseId: string
  ): Observable<any[]> {

    return this.http.get<any[]>(

      `${this.apiUrl}/${databaseId}/history`

    );
  }

  getQueryAnalysis(
  databaseId: string
): Observable<any> {

  return this.http.get(

    `${this.apiUrl}/${databaseId}/query-analysis`

  );
}

getSlowQueries(
  databaseId: string
): Observable<any[]> {

  return this.http.get<any[]>(

    `${this.apiUrl}/${databaseId}/slow-queries`

  );
}

getAlerts(
  databaseId: string
): Observable<any[]> {

  return this.http.get<any[]>(

    `${this.apiUrl}/${databaseId}/alerts`

  );

}

getAllAlerts(): Observable<any[]> {

  return this.http.get<any[]>(

    `${this.apiUrl}/all/alerts`

  );
}

exportPdf(databaseId: string): Observable<Blob> {

  return this.http.get(

    `${this.apiUrl}/${databaseId}/export/pdf`,

    { responseType: 'blob' }

  );
}

exportExcel(databaseId: string): Observable<Blob> {

  return this.http.get(

    `${this.apiUrl}/${databaseId}/export/excel`,

    { responseType: 'blob' }

  );
}

optimizeQuery(sqlQuery: string): Observable<any> {

  return this.http.post(

    `${this.apiUrl}/query/optimize`,

    { query: sqlQuery }

  );
}
}