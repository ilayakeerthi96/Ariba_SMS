import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { delay, filter, map, tap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

import { ColorModeService } from '@coreui/angular';
import { AuthService } from '../app/shared/service/AuthService';

@Component({
  selector: 'app-root',
  template: '<router-outlet />',
  imports: [RouterOutlet, CommonModule]
})
export class AppComponent implements OnInit {
  title = 'Supplier Management System';

  readonly #destroyRef: DestroyRef = inject(DestroyRef);
  readonly #activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  readonly #router = inject(Router);
  readonly #titleService = inject(Title);

  readonly #colorModeService = inject(ColorModeService);
  readonly #authService = inject(AuthService)

  constructor(private router: Router) {
    this.#titleService.setTitle(this.title);
    this.#colorModeService.localStorageItemName.set('coreui-free-angular-admin-template-theme-default');
    this.#colorModeService.eventName.set('ColorSchemeChange');

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        localStorage.setItem('lastRoute', event.urlAfterRedirects);
      }
    });
  }
  loginStatus: any

  ngOnInit(): void {

    const lastRoute = localStorage.getItem('lastRoute');
    const token = localStorage.getItem('token');
    const status = localStorage.getItem('loginStatus');

    if (token) {
      // If the browser URL (including hash) points to a public path like /onboard, don't force a redirect.
      let currentUrl = '';
      try { currentUrl = this.router && this.router.url ? this.router.url : ''; } catch {}
      if (!currentUrl && typeof window !== 'undefined') {
        currentUrl = window.location.hash || window.location.pathname || window.location.href || '';
      }

      const publicPaths = ['/login', '/onboard', '/public', '/assets'];
      if (currentUrl && publicPaths.some(p => currentUrl.includes(p))) {
        // Already on a public path (e.g. #/onboard) — don't navigate away.
      } else if (lastRoute && lastRoute !== '/login') {
        this.router.navigateByUrl(lastRoute); // restore last route
      } else {
        this.router.navigate(['dashboard']);  // fallback
      }
    }

    if (status !== null) {
      this.loginStatus = (status === 'true');
      this.#authService.setLoginStatus(this.loginStatus);
    } else {
      this.loginStatus = false;
      localStorage.setItem('loginStatus', 'false');
    }

    this.#authService.loginStatus$
      .pipe(takeUntilDestroyed(this.#destroyRef))
      .subscribe(status => {
        this.loginStatus = status;
        localStorage.setItem('loginStatus', String(status));
      });
  }
}
