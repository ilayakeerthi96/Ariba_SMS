// // import { Routes } from '@angular/router';

// // /**
// //  * ✅ QUOTE COMPARISON ROUTES
// //  * Buyer-side routes for comparing supplier quotes
// //  */
// // export const routes: Routes = [
// //   {
// //     path: '',
// //     loadComponent: () => 
// //       import('./quote-comparison.component').then(m => m.QuoteComparisonComponent),
// //     data: { 
// //       title: 'Quote Comparison',
// //       roles: ['ROLE_BUYER']
// //     }
// //   },
// //   {
// //     path: ':rfqId',
// //     loadComponent: () => 
// //       import('./quote-comparison.component').then(m => m.QuoteComparisonComponent),
// //     data: { 
// //       title: 'Quote Comparison',
// //       roles: ['ROLE_BUYER']
// //     }
// //   }
// // ];

// import { Routes } from '@angular/router';

// export const routes: Routes = [
//   {
//     path: '',
//     loadComponent: () => 
//       import('./quote-comparison.component').then(m => m.QuoteComparisonComponent),
//     data: { 
//       title: 'Quote Comparison',
//       roles: ['ROLE_BUYER']
//     }
//   },
//   {
//     path: ':rfqId',
//     loadComponent: () => 
//       import('./quote-comparison.component').then(m => m.QuoteComparisonComponent),
//     data: { 
//       title: 'Quote Comparison',
//       roles: ['ROLE_BUYER']
//     }
//   }
// ];

import { Routes } from '@angular/router';

/**
 * ✅ QUOTE COMPARISON ROUTES
 * Buyer-side routes for comparing supplier quotes
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: '0',
    pathMatch: 'full'
  },
  {
    path: ':rfqId',
    loadComponent: () => 
      import('./rfq-quote-comparison.component').then(m => m.QuoteComparisonComponent),
    data: { 
      title: 'Compare Quotes',
      roles: ['ROLE_BUYER']
    }
  }
];