import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/auth.model';

@Component({
  imports: [FormsModule, RouterLink],
  selector: 'app-register',
  styleUrl: './register.css',
  templateUrl: './register.html',
})
export class Register {

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

    if (this.password.length < 8) {
      this.errorMessage = 'A senha deve ter no mínimo 8 caracteres.';
      return;
    }

    this.loading = true;

    const request: RegisterRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;

        if (err.status === 409) {
          this.errorMessage = 'Email já cadastrado.';
        } else if (err.status === 400) {
          this.errorMessage = 'Dados inválidos. Verifique email e senha.';
        } else {
          this.errorMessage = 'Erro ao cadastrar. Tente novamente.';
        }

        console.error('Erro no cadastro:', err);
      }
    });
  }
}
