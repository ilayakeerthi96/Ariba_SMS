import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./create-s.component').then(m => m.CreateSComponent),
    data: { title: 'Create s' }
  },
    {
    path: 'edit/:id',
    loadComponent: () => import('./create-s.component').then(m => m.CreateSComponent),
    data: { title: 'Edit Supplier' }
  },
 
];
