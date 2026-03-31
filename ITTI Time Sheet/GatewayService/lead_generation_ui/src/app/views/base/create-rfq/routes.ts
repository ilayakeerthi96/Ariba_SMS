import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./create-rfq.component').then(m => m.CreateRFQComponent),
    data: { title: 'Create RFQ' }
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./create-rfq.component').then(m => m.CreateRFQComponent),
    data: { title: 'Edit RFQ' }
  },
  {
    path: ':id/view',
    loadComponent: () => import('./create-rfq.component').then(m => m.CreateRFQComponent),
    data: { title: 'View RFQ' }
  }
];