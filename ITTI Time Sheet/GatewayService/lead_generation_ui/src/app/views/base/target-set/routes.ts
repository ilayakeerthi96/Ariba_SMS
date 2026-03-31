import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./target-set.component').then(m => m.TargetSetComponent),
    data: { title: 'Manage Targets' }

    
  }
];

