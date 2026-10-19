import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AdminUserResponse, Role } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private readonly API_URL = environment.apiUrl;
  private readonly http = inject(HttpClient);

  listAllUsers(): Observable<AdminUserResponse[]> {
    return this.http.get<AdminUserResponse[]>(`${this.API_URL}/admin/users`);
  }

  updateUserRole(uuid: string, role: Role): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/admin/users/${uuid}/role`, { role });
  }

  deleteUser(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/users/${uuid}`);
  }
}
