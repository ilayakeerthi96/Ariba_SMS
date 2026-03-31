import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./lead-create.component').then(m => m.LeadCreateComponent),
    data: { title: 'Manage Lead' }
  }
];

