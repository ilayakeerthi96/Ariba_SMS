import { Routes } from '@angular/router';

/**
 * ✅ SUPPLIER QUOTE SUBMISSION ROUTES
 * Supplier-side routes for submitting item-level quotes
 */
export const routes: Routes = [
  {
    path: '',
    loadComponent: () => 
      import('./supplier-quote-submission.component').then(m => m.SupplierQuoteSubmissionComponent),
    data: { 
      title: 'Submit Quote',
      roles: ['ROLE_SUPPLIER']
    }
  },
  {
    path: ':rfqId',
    loadComponent: () => 
      import('./supplier-quote-submission.component').then(m => m.SupplierQuoteSubmissionComponent),
    data: { 
      title: 'Submit Quote',
      roles: ['ROLE_SUPPLIER']
    }
  }
];