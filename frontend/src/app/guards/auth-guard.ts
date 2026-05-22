import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route) => {

  const router = inject(Router);

  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token) {
    router.navigate(['']);
    return false;
  }

  const path = route.routeConfig?.path;

  if (path === 'admin-dashboard' && role !== 'ADMIN') {
    router.navigate(['']);
    return false;
  }

  return true;
};