import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, switchMap, map } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth.model';
import { MeResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API_URL = 'http://localhost:8080';
  private readonly TOKEN_KEY = 'auth_token';

  private readonly tokenSignal = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));
  private readonly userEmailSignal = signal<string | null>(null);

  readonly isLoggedIn = computed(() => this.tokenSignal() !== null);
  readonly userEmail = this.userEmailSignal.asReadonly();

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/auth/login`, request)
      .pipe(
        tap(response => this.saveToken(response.token)),
        switchMap(loginResponse =>
          this.getMe().pipe(
            map(me => {
              this.userEmailSignal.set(me.email);
              return loginResponse;
            })
          )
        )
      );
  }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/users`, request);
  }

  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.API_URL}/users/me`);
  }

  loadCurrentUser(): Observable<MeResponse> {
    return this.getMe().pipe(
      tap(me => this.userEmailSignal.set(me.email))
    );
  }

  logout(): void {
    console.log('[AuthService] LOGOUT CHAMADO!');   // ← rastreia quem chama
    localStorage.removeItem(this.TOKEN_KEY);
    this.tokenSignal.set(null);
    this.userEmailSignal.set(null);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  private saveToken(token: string): void {
    console.log('[AuthService] saveToken chamado');
    localStorage.setItem(this.TOKEN_KEY, token);
    this.tokenSignal.set(token);
  }
}
