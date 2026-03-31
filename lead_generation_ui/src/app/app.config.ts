// import { ApplicationConfig, importProvidersFrom } from '@angular/core';
// import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
// import {
//   provideRouter,
//   withEnabledBlockingInitialNavigation,
//   withHashLocation,
//   withInMemoryScrolling,
//   withRouterConfig,
//   withViewTransitions
// } from '@angular/router';

// import { DropdownModule, SidebarModule } from '@coreui/angular';
// import { IconSetService } from '@coreui/icons-angular';
// import { routes } from './app.routes';
// import { provideHttpClient } from '@angular/common/http';

// export const appConfig: ApplicationConfig = {
//   providers: [
//     provideRouter(routes,
//       withRouterConfig({
//         onSameUrlNavigation: 'reload'
//       }),
//       withInMemoryScrolling({
//         scrollPositionRestoration: 'top',
//         anchorScrolling: 'enabled'
//       }),
//       withEnabledBlockingInitialNavigation(),
//       withViewTransitions(),
//       withHashLocation()
//     ),
//     importProvidersFrom(SidebarModule, DropdownModule),
//     IconSetService,
//     provideAnimationsAsync(),
//     provideHttpClient()
//   ]
// };


import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withHashLocation,
  withInMemoryScrolling,
  withRouterConfig,
  withViewTransitions
} from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';

import { DropdownModule, SidebarModule } from '@coreui/angular';
import { IconSetService } from '@coreui/icons-angular';
import { routes } from './app.routes';
import { AuthInterceptor } from '../app/shared/interceptor/auth.interceptor';

/**
 * ============================================
 * APPLICATION CONFIGURATION
 * ============================================
 * Enterprise-grade configuration with
 * HTTP interceptor for authentication
 * ============================================
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes,
      withRouterConfig({
        onSameUrlNavigation: 'reload'
      }),
      withInMemoryScrolling({
        scrollPositionRestoration: 'top',
        anchorScrolling: 'enabled'
      }),
      withEnabledBlockingInitialNavigation(),
      withViewTransitions(),
      withHashLocation()
    ),
    importProvidersFrom(SidebarModule, DropdownModule),
    IconSetService,
    provideAnimationsAsync(),
    provideHttpClient(),
    
    // ✅ Register HTTP Interceptor for automatic JWT inclusion
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
};