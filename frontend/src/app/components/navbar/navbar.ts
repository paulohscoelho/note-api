import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  imports: [RouterLink],
  selector: 'app-navbar',
  styleUrl: './navbar.css',
  templateUrl: './navbar.html',
})
export class Navbar implements OnInit {

  private readonly authService = inject(AuthService);
  public readonly router = inject(Router);

  readonly isLoggedIn = this.authService.isLoggedIn;
  readonly userEmail = this.authService.userEmail;
  readonly userRole = this.authService.userRole;

  ngOnInit(): void {
    if (this.isLoggedIn() && !this.userEmail()) {
      this.authService.loadCurrentUser().subscribe();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
