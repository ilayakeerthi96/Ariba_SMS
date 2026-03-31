
// import { Routes } from '@angular/router';

// export const routes: Routes = [
//   {
//     path: '',
//     loadComponent: () => import('./create-roq.component').then(m => m.CreateRFQComponent),
//     data: { title: 'Create roq' }
//   },

// ];

// File: src/app/views/base/create-roq/routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./create-roq.component').then(m => m.RFQListComponent),
    data: { title: 'Create RFQ' }
  },

];