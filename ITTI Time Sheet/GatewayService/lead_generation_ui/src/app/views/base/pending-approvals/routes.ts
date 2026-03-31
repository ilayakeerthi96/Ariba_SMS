// import { Routes } from '@angular/router';

// export const routes: Routes = [
//   {
//     path: '',
//     loadComponent: () => import('./pending-approvals.component').then(m => m.PendingApprovalsComponent),
//     data: {
//       title: 'Pending Approvals'
//     }
//   }
// ]

import { Routes } from '@angular/router';
import { PendingApprovalsComponent } from './pending-approvals.component';

export const routes: Routes = [
  {
    path: '',
    component: PendingApprovalsComponent,
    data: {
      title: 'Pending Approvals'
    }
  }
];