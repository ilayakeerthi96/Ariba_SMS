import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./rfq-dashboard.component').then(m => m.RFQDashboardComponent),
    data: { title: 'RFQ Dashboard' }
  }
];