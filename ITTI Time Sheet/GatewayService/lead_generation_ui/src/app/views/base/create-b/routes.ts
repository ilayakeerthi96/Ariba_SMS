import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./create-b.component').then(m => m.CreateBComponent),
    data: { title: 'Create b' }
  },
    {
    path: 'edit/:id',
    loadComponent: () => import('./create-b.component').then(m => m.CreateBComponent),
    data: { title: 'Edit Buyer' }
  },
 
];
