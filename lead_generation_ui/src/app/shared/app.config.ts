import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { sessionInterceptor } from './interceptor/SessionInterceptor';
import { MatDatepickerModule } from '@angular/material/datepicker';


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom([BrowserAnimationsModule]),
    provideZoneChangeDetection({ eventCoalescing: true }),   
    provideHttpClient(withInterceptors([sessionInterceptor])),
    importProvidersFrom(MatDatepickerModule)  
  ]
};
