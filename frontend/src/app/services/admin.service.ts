import { Injectable } from '@angular/core';

import {
  HttpClient,
  HttpHeaders
} from '@angular/common/http';

import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl =
    'http://localhost:8080/api/admin';

  constructor(
    private http: HttpClient
  ) {}

  getPendingUsers(): Observable<any> {

    const token =
      localStorage.getItem('token');

    const headers =
      new HttpHeaders({

        Authorization:
          `Bearer ${token}`
      });

    return this.http.get(
      `${this.apiUrl}/pending-users`,
      { headers }
    );
  }

  approveUser(
    userId: string
  ): Observable<any> {

    const token =
      localStorage.getItem('token');

    const headers =
      new HttpHeaders({

        Authorization:
          `Bearer ${token}`
      });

    return this.http.post(
      `${this.apiUrl}/approve/${userId}`,
      {},
      { headers }
    );
  }
}