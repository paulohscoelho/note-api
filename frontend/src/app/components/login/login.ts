import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/auth.model';

@Component({
  imports: [FormsModule, RouterLink],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email: string = '';
  password: string = '';
  errorMessage: string = '';
  loading: boolean = false;

  onSubmit(): void {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Preencha email e senha.';
      return;
    }

    this.loading = true;

    const request: LoginRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.login(request).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/notes']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = 'Email ou senha inválidos.';
        console.error('Erro no login:', err);
      }
    });
  }
}
