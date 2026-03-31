import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '0',
    pathMatch: 'full'
  },
  {
    path: ':rfqId',
    loadComponent: () => 
      import('./supplier-evaluation.component').then(m => m.SupplierEvaluationComponent),
    data: { 
      title: 'Evaluate Suppliers',
      roles: ['ROLE_BUYER']
    }
  }
];