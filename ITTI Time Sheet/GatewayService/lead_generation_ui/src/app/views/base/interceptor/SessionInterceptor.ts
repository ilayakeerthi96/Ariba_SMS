import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const sessionInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router); // Inject Router in a function

  return next(req).pipe(
    catchError(error => {
      if (error.status === 401 || error.status === 403) {
        let currentUrl = '';
        try { currentUrl = router && (router as any).url ? (router as any).url : ''; } catch {}
        if (!currentUrl && typeof window !== 'undefined') {
          currentUrl = window.location.hash || window.location.pathname || window.location.href || '';
        }
        const publicPaths = ['/login', '/onboard', '/public', '/assets'];
        if (!publicPaths.some(p => currentUrl.includes(p))) {
          alert('Session expired! Redirecting to login.');
          localStorage.clear();
          window.location.href = '/login';
        }
      }
      return throwError(() => error);
    })
  );
};
