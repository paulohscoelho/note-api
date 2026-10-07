import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth.model';
import { MeResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API_URL = 'http://localhost:8080';
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/auth/login`, request)
      .pipe(
        tap(response => this.saveToken(response.token))
      );
  }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/users`, request);
  }

  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.API_URL}/users/me`);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }
}
