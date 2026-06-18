import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DatabaseService {

  private apiUrl =
    'http://localhost:8080/api/databases';

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
}