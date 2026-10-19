import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError, of } from 'rxjs';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (authService.userRole() === 'ADMIN') {
    return true;
  }

  if (authService.userRole() === 'USER') {
    router.navigate(['/notes']);
    return false;
  }

  return authService.getMe().pipe(
    map(me => {
      if (me.role === 'ADMIN') {
        return true;
      }
      router.navigate(['/notes']);
      return false;
    }),
    catchError(() => {
      authService.logout();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
