import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const sessionInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  // ✅ Public route patterns (adjust as needed)
  const publicPaths = ['/login', '/onboard', '/public', '/assets'];

  // ✅ Skip interception if the request is for a public route
  if (publicPaths.some(path => req.url.includes(path))) {
    return next(req);
  }

  // ✅ Otherwise, apply standard error handling
  return next(req).pipe(
    catchError(error => {
      if (error.status === 401 || error.status === 403) {
        // Avoid redirect loop if you're already on /login or /onboard
        // Router.url may be empty very early; fall back to window.location.hash (for hash routing)
        let currentUrl = '';
        try { currentUrl = router && router.url ? router.url : ''; } catch {}
        if (!currentUrl && typeof window !== 'undefined') {
          currentUrl = window.location.hash || window.location.pathname || window.location.href || '';
        }

        if (!publicPaths.some(path => currentUrl.includes(path))) {
          alert('Session expired! Redirecting to login.');
          localStorage.clear();
          router.navigate(['/login']);
        }
      }
      return throwError(() => error);
    })
  );
};
