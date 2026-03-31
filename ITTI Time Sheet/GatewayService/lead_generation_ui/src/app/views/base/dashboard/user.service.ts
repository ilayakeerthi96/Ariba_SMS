import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { User } from './buyer-b.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private API = environment.API_URL + 'leadcapture/api/users';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  createUser(departmentId: number, user: User): Observable<any> {
    return this.http.post(`${this.API}/${departmentId}`, user, { headers: this.getAuthHeaders() });
  }
}
