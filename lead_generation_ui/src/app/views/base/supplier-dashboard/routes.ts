import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'supplier-dashboard',
    loadComponent: () => import('./supplier-dashboard.component').then(m => m.SupplierDashboardComponent),
    data: { title: 'supplier-dashboard' }
  }
];