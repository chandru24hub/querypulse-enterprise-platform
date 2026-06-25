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
    userId: string,
    role: string = 'USER',
    message: string = ''
  ): Observable<any> {

    const token =
      localStorage.getItem('token');

    const headers =
      new HttpHeaders({

        Authorization:
          `Bearer ${token}`
      });

    const body = {
      role: role,
      message: message
    };

    return this.http.post(
      `${this.apiUrl}/approve-user/${userId}`,
      body,
      { headers }
    );
  }

  rejectUser(
    userId: string,
    reason: string = ''
  ): Observable<any> {

    const token =
      localStorage.getItem('token');

    const headers =
      new HttpHeaders({

        Authorization:
          `Bearer ${token}`
      });

    const body = {
      message: reason
    };

    return this.http.post(
      `${this.apiUrl}/reject-user/${userId}`,
      body,
      { headers }
    );
  }
}