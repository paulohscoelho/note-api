import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { AuthService } from './services/auth.service';

@Component({
  imports: [RouterOutlet, Navbar],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {

  private readonly authService = inject(AuthService);

  title = 'frontend';

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.authService.loadCurrentUser().subscribe({
        error: (err) => {
          console.error('Erro ao carregar user:', err);
          this.authService.logout();
        }
      });
    }
  }
}
