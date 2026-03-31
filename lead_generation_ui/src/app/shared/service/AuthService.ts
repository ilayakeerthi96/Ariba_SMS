import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject, interval, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../../../app/shared/user/User';
import { MessageService } from './message.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedOut = false;
  private countdownSub?: Subscription;
  private remainingTimeSubject = new BehaviorSubject<string>('00:00:00');
  remainingTime$ = this.remainingTimeSubject.asObservable();
  private currentUser: User | null = null;

  private loginStatusSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  loginStatus$ = this.loginStatusSubject.asObservable();

    // 🆕 Emits the expiry timestamp (ms) when the warning dialog should open.
  //    DefaultHeaderComponent listens to this to show the dialog.
  private sessionWarningSubject = new BehaviorSubject<number | null>(null);
  sessionWarning$ = this.sessionWarningSubject.asObservable();


  constructor(
    private router: Router,
    private ngZone: NgZone,
    private messageService: MessageService
  ) {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        this.currentUser = JSON.parse(savedUser);
      } catch (e) {
        console.error('Error parsing saved user:', e);
        this.currentUser = null;
      }
    }

    // Check session on init
    try {
      let currentUrl = '';
      try {
        if (this.router?.url && this.router.url !== '/' && this.router.url !== '') {
          currentUrl = this.router.url;
        } else if (typeof window !== 'undefined') {
          currentUrl =
            window.location.hash?.replace(/^#/, '') ||
            window.location.pathname ||
            window.location.href ||
            '';
        }
      } catch {
        currentUrl = window.location.href || '';
      }

      const publicPaths = ['/login',  '/onboard', '/public', '/assets'];

      if (this.isSessionExpired()) {
        if (!publicPaths.some(p => currentUrl.includes(p))) {
          this.logout();
        } else {
          this.loginStatusSubject.next(false);
        }
      } else if (this.isLoggedIn()) {
        this.startTimer();
      }
    } catch {
      if (this.isSessionExpired()) {
        this.loginStatusSubject.next(false);
      }
    }
  }

  logout() {
    this.loggedOut = true;
    localStorage.clear();
    this.currentUser = null;
    this.loginStatusSubject.next(false);
    this.stopTimer();
     this.sessionWarningSubject.next(null);//

    try {
      let currentUrl = this.router?.url || '';
      if (!currentUrl && typeof window !== 'undefined') {
        currentUrl = window.location.hash || window.location.pathname || window.location.href || '';
      }

      const publicPaths = ['/login', '/onboard', '/public', '/assets'];
      if (!publicPaths.some(p => currentUrl.includes(p))) {
        this.ngZone.run(() => this.router.navigate(['/login']));
      }
    } catch {
      // Fail silently
    }
  }

  isSessionExpired(): boolean {
    const expirationTime = localStorage.getItem('expirationTime');
    if (!expirationTime) return true;

    try {
      let expiry: number;
      
      if (typeof expirationTime === 'string') {
        const cleanTime = expirationTime.replace(/['"]/g, '');
        expiry = parseInt(cleanTime, 10);
      } else {
        expiry = expirationTime as any;
      }

      if (expiry < Date.now()) {
        this.logout();
        return true;
      }
      return false;
    } catch {
      console.error('Error checking session expiry');
      return true;
    }
  }

  setLoginStatus(status: boolean) {
    this.loginStatusSubject.next(status);
    localStorage.setItem('loginStatus', status.toString());

    if (status) {
      this.startTimer();
    } else {
      this.stopTimer();
    }
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('loginStatus') === 'true';
  }

    // 🆕 Called by the dialog when user clicks "Extend +1 Hour"
  extendSession(): void {
    const token = localStorage.getItem('token');
    if (!token) return;

    try {
      const decodedToken = JSON.parse(atob(token.split('.')[1]));
      const currentExpiry = decodedToken.exp * 1000;
      const newExpiry = currentExpiry + 60 * 60 * 1000; // +1 hour in ms

      // Update expirationTime in localStorage so isSessionExpired() stays happy
      localStorage.setItem('expirationTime', newExpiry.toString());

      // Close the warning dialog
      this.sessionWarningSubject.next(null);

      // Restart the timer so warningShown resets and the countdown reflects new expiry
      this.startTimer();

      this.messageService.showMessage(
        'success',
        'Session Extended',
        'Your session has been extended by 1 hour.'
      );
    } catch {
      console.error('Failed to extend session');
    }
  }

  // Session Timer
  private startTimer() {
    this.stopTimer();
    let warningShown = false;

    this.countdownSub = interval(1000).subscribe(() => {
      const token = localStorage.getItem('token');
      if (!token) {
        this.logout();
        return;
      }

   try {
        const decodedToken = JSON.parse(atob(token.split('.')[1]));
        const expiry = decodedToken.exp * 1000;

        // 🆕 Use expirationTime override if set (after extend)
        const storedExpiry = localStorage.getItem('expirationTime');
        const effectiveExpiry = storedExpiry
          ? parseInt(storedExpiry.replace(/['"]/g, ''), 10)
          : expiry;

        const diff = effectiveExpiry - Date.now();

        if (diff <= 5 * 60 * 1000 && !warningShown) {
          warningShown = true;
          // 🆕 Emit expiry timestamp → triggers dialog in header component
          this.sessionWarningSubject.next(effectiveExpiry);
          // Keep toast as fallback (optional — remove if you prefer dialog only)
          // this.messageService.showMessage('warning', 'Login Status', 'Your session will expire in 5 minutes');
        }

        if (diff <= 0) {
          this.logout();
          return;
        }

        this.remainingTimeSubject.next(this.formatDuration(diff));
      } catch {
        this.logout();
      }
    });
  }

  private stopTimer() {
    if (this.countdownSub) {
      this.countdownSub.unsubscribe();
    }
  }

  private formatDuration(ms: number): string {
    const totalSeconds = Math.floor(ms / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    return `${hours.toString().padStart(2, '0')}:${minutes
      .toString()
      .padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  setUser(user: User) {
    this.currentUser = user;

    if (this.currentUser.roles && Array.isArray(this.currentUser.roles)) {
      this.currentUser.roles = this.currentUser.roles.map((item: any) => {
        if (typeof item === 'object' && item.userRoles) {
          return item.userRoles;
        }
        return item;
      });
    }

    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    console.log('✅ User set in AuthService:', this.currentUser);
  }

  getUser(): User | null {
    return this.currentUser;
  }

  hasRole(role: string): boolean {
    if (!this.currentUser?.roles) return false;
    return this.currentUser.roles.includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some(r => this.hasRole(r));
  }

  /**
   * ✅ Check if user is SuperAdmin
   */
  isSuperAdmin(): boolean {
    return this.hasRole('SUPER_ADMIN');
  }

  /**
   * 🆕 Check if user is Organization Admin
   */
  isOrganizationAdmin(): boolean {
    return this.hasRole('ORGANIZATION_ADMIN');
  }

  /**
   * ✅ Check if user is any type of admin
   */
  isAdmin(): boolean {
    return this.hasAnyRole(['SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'ADMIN']);
  }

  /**
   * ✅ Check if user is Buyer
   */
  isBuyer(): boolean {
    return this.hasRole('ROLE_BUYER');
  }

  //supplier
  isSupplier(): boolean {
  return this.hasRole('ROLE_SUPPLIER');
}
getSupplierId(): number | null {
  const supplierId = localStorage.getItem('supplierId');
  return supplierId ? Number(supplierId) : null;
}
getSupplierName(): string | null {
  return localStorage.getItem('supplierName');
}

  /**
   * 🆕 Check if user is Hierarchy User (Staff)
   */
  isHierarchyUser(): boolean {
    return this.hasAnyRole(['CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE', 'ADMIN']);
  }

  /**
   * ✅ Get user role display name
   */
  getUserRoleDisplay(): string {
    const role = localStorage.getItem('role');
    
    if (role === 'SUPER_ADMIN') return 'SuperAdmin';
    if (role === 'ORGANIZATION_ADMIN') return 'Organization Admin';
    if (role === 'ADMIN') return 'Admin';
    if (role === 'CEO') return 'CEO';
    if (role === 'COO') return 'COO';
    if (role === 'MANAGER') return 'Manager';
    if (role === 'PROCUREMENT') return 'Procurement';
    if (role === 'FINANCE') return 'Finance';
    if (role === 'ROLE_BUYER') return 'Buyer';
    if (role === 'ROLE_SUPPLIER') return 'Supplier';
    
    return role || 'User';
  }

  /**
   * ✅ Get user company name
   */
  getCompanyName(): string | null {
    return localStorage.getItem('companyName');
  }

  /**
   * 🆕 Get dashboard route based on role
   */
  getDashboardRoute(): string {
    const role = localStorage.getItem('role');
    
    if (role === 'SUPER_ADMIN') return '/superadmin-dashboard';
    if (role === 'ORGANIZATION_ADMIN') return '/orgadmin-dashboard';
    if (role === 'ROLE_BUYER') return '/rfq-dashboard';
    if (role === 'ROLE_SUPPLIER') return '/supplier-dashboard'; 
    if (role === 'CEO') return '/ceo-dashboard';
    if (role === 'COO') return '/coo-dashboard';
    if (role === 'PROCUREMENT') return '/procurement-dashboard';
    if (role === 'FINANCE') return '/finance-dashboard';
    if (role === 'MANAGER') return '/manager-dashboard';
    
    return '/pending-approvals'; // Default
  }

  // ADD THESE METHODS TO YOUR EXISTING AuthService

/**
 * 🆕 Get user's hierarchy level ID
 */
getHierarchyLevelId(): number | null {
  const levelId = localStorage.getItem('hierarchyLevelId');
  return levelId ? Number(levelId) : null;
}

/**
 * 🆕 Get user's hierarchy level name
 */
getHierarchyLevelName(): string | null {
  return localStorage.getItem('hierarchyLevelName');
}

/**
 * 🆕 Get user's hierarchy level order
 */
getHierarchyLevelOrder(): number | null {
  const order = localStorage.getItem('hierarchyLevelOrder');
  return order ? Number(order) : null;
}

/**
 * 🆕 Check if user is a hierarchy user (has hierarchy level)
 */
isHierarchyUserWithLevel(): boolean {
  return this.getHierarchyLevelId() !== null;
}

/**
 * 🆕 Get dashboard route based on hierarchy level
 */
getDashboardRouteForHierarchyUser(): string {
  const levelOrder = this.getHierarchyLevelOrder();
  
  if (levelOrder === null) {
    return '/dashboard';
  }
  
  // CEO (order 10) gets special treatment if you want
  if (levelOrder === 10) {
    return '/ceo-dashboard';
  }
  
  // All others get generic hierarchy dashboard
  return '/hierarchy-dashboard';
}
}