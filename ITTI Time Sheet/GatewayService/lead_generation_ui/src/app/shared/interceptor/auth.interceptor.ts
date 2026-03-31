import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { MessageService } from '../service/message.service';

/**
 * ============================================
 * HTTP INTERCEPTOR - Enterprise Security
 * ============================================
 * Automatically adds JWT token to all requests
 * Handles authentication errors globally
 * ============================================
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private messageService: MessageService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // URLs that don't need authentication
    const publicUrls = [
      '/auth/login',
      '/auth/register',
      '/superadmin/auth',
      '/organization-admin/auth/login',
      '/hierarchy-users/auth/login',
      '/buyer/login'
    ];

    // Check if request is to a public endpoint
    const isPublicUrl = publicUrls.some(url => req.url.includes(url));

    // Clone request and add auth header if not public
    let authReq = req;
    if (!isPublicUrl) {
      const token = localStorage.getItem('token');
      
      if (token) {
        authReq = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      } else {
        console.warn('⚠️ No token found for protected endpoint:', req.url);
      }
    }

    // Handle the request
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('❌ HTTP Error:', error);

        // Handle different error scenarios
        if (error.status === 401) {
          // Unauthorized - token invalid or expired
          console.error('🔐 Authentication failed - redirecting to login');
          this.handleAuthError();
        } else if (error.status === 403) {
          // Forbidden - user doesn't have permission
          this.messageService.showMessage(
            'error',
            'Access Denied',
            'You do not have permission to perform this action'
          );
        } else if (error.status === 0) {
          // Network error
          this.messageService.showMessage(
            'error',
            'Connection Error',
            'Unable to connect to server. Please check your connection.'
          );
        }

        return throwError(() => error);
      })
    );
  }

  /**
   * Handle authentication errors
   */
  private handleAuthError(): void {
    // Clear local storage
    localStorage.clear();
    
    // Show message
    this.messageService.showMessage(
      'warning',
      'Session Expired',
      'Your session has expired. Please login again.'
    );
    
    // Redirect to login
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 1500);
  }
}