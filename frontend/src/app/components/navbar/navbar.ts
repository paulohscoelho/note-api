import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  imports: [RouterLink],
  selector: 'app-navbar',
  styleUrl: './navbar.css',
  templateUrl: './navbar.html',
})
export class Navbar {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserEmail(): string {
    return 'usuário';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
