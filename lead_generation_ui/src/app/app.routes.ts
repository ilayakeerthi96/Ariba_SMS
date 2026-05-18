 import { Routes } from '@angular/router';

  export const routes: Routes = [
    {
      path: '',
      redirectTo: 'login',
      pathMatch: 'full'
    },
    {
      path: '',
      loadComponent: () =>
        import('./layout').then(m => m.DefaultLayoutComponent),
      data: { title: 'Home' },
      children: [
        {
          path: '',
          redirectTo: 'hierarchy-dashboard',
          pathMatch: 'full'
        },
        {
          path: 'hierarchy-dashboard',
          loadComponent: () =>
            import('./views/base/hierarchy-dashboard/hierarchy-dashboard.component')
              .then(m => m.HierarchyDashboardComponent),
          data: {
            title: 'Dashboard',
            roles: ['CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE', 'ADMIN']
          }
        },
        {
          path: 'change-password',
          loadComponent: () =>
            import('./views/base/change-password/change-password.component')
              .then(m => m.ChangePasswordComponent),
          data: { title: 'Change Password' }
        },
        {
          path: 'superadmin-dashboard',
          loadComponent: () =>
            import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
              .then(m => m.SuperAdminDashboardComponent),
          data: {
            title: 'Super Admin Dashboard',
            roles: ['SUPER_ADMIN']
          }
        },
        {
          path: 'orgadmin-dashboard',
          loadComponent: () =>
            import('./views/base/orgadmin-dashboard/orgadmin-dashboard.component')
              .then(m => m.OrgAdminDashboardComponent),
          data: {
            title: 'Organization Admin Dashboard',
            roles: ['ORGANIZATION_ADMIN']
          }
        },
        
        {
          path: 'hierarchy-levels',
          loadComponent: () =>
            import('./views/base/hierarchy-levels/hierarchy-level-management.component')
              .then(m => m.HierarchyLevelManagementComponent),
          data: {
            title: 'Hierarchy Levels',
            roles: ['ORGANIZATION_ADMIN']
          }
        },
        {
          path: 'hierarchy-users',
          loadComponent: () =>
            import('./views/base/hierarchy-users/hierarchy-user-management.component')
              .then(m => m.HierarchyUserManagementComponent),
          data: {
            title: 'Hierarchy Users',
            roles: ['ORGANIZATION_ADMIN']
          }
        },
        {
          path: 'reporting-structure',
          loadComponent: () =>
            import('./views/base/reporting-structure/reporting-structure.component')
              .then(m => m.ReportingStructureComponent),
          data: {
            title: 'Reporting Structure',
            roles: ['ORGANIZATION_ADMIN']
          }
        },
        {
          path: 'supplier-dashboard',
          loadComponent: () =>
            import('./views/base/supplier-dashboard/supplier-dashboard.component').then(m => m.SupplierDashboardComponent),
          data: {
            title: 'Supplier Dashboard',
            roles: ['ROLE_SUPPLIER']
          }
        },
        {
          path: 'dashboard',
          loadChildren: () =>
            import('./views/base/dashboard/routes').then(m => m.routes),
          data: {
            roles: ['ADMIN', 'CEO', 'COO', 'PROCUREMENT', 'FINANCE']
          }
        },
        {
          path: 'lead-create',
          loadChildren: () =>
            import('./views/base/lead-create/routes').then(m => m.routes),
          data: {
            roles: ['ADMIN', 'CEO', 'COO', 'PROCUREMENT', 'FINANCE']
          }
        },
        {
          path: 'pending-approvals',
          loadChildren: () =>
            import('./views/base/pending-approvals/routes').then(m => m.routes),
          data: {
            roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE', 'MANAGER', 'ADMIN']
          }
        },
        { path: 'ceo-dashboard', redirectTo: 'hierarchy-dashboard', pathMatch: 'full' },
        { path: 'coo-dashboard', redirectTo: 'hierarchy-dashboard', pathMatch: 'full' },
        { path: 'procurement-dashboard', redirectTo: 'hierarchy-dashboard', pathMatch: 'full' },
        { path: 'finance-dashboard', redirectTo: 'hierarchy-dashboard', pathMatch: 'full' },
        { path: 'manager-dashboard', redirectTo: 'hierarchy-dashboard', pathMatch: 'full' },
        {
          path: 'rfq-dashboard',
          loadChildren: () =>
            import('./views/base/rfq-dashboard/routes').then(m => m.routes),
          data: { roles: ['ROLE_BUYER'] }
        },
        {
          path: 'create-rfq',
          loadChildren: () =>
            import('./views/base/create-rfq/routes').then(m => m.routes),
          data: { roles: ['ROLE_BUYER'] }
        },
        {
          path: 'quote-comparison',
          loadChildren: () =>
            import('./views/base/rfq-quote-comparison/routes').then(m => m.routes),
          data: {
            roles: ['ROLE_BUYER'],
            title: 'Quote Comparison'
          }
        },
        {
          path: 'supplier-quote',
          loadChildren: () =>
            import('./views/base/supplier-quote-submission/routes').then(m => m.routes),
          data: {
            roles: ['ROLE_SUPPLIER'],
            title: 'Submit Quote'
          }
        },
        {
          path: 'supplier-evaluation',
          loadChildren: () =>
            import('./views/base/supplier-evaluation/routes').then(m => m.routes),
          data: {
            roles: ['ROLE_BUYER'],
            title: 'Evaluate Suppliers'
          }
        },

        // ✅ NEW: Supplier Final Selection (after evaluation)
      {
          path: 'supplier-selection/:rfqId',
          loadComponent: () =>
            import('./views/base/supplier-selection/supplier-selection.component')
              .then(m => m.SupplierSelectionComponent),
          data: {
            title: 'Select Supplier',
            roles: ['ROLE_BUYER']
          }
        },

        // ✅ NEW: PO Price Negotiation (after supplier selection)
          {
          path: 'po-negotiation/:rfqId/:supplierId/:selectionId',
          loadComponent: () =>
            import('./views/base/po-negotiation/po-negotiation.component')
              .then(m => m.PONegotiationComponent),
          data: {
            title: 'PO Price Negotiation',
            roles: ['ROLE_BUYER']
          }
        },

        // Purchase Order Routes
        {
          path: 'po-list',
          loadComponent: () =>
            import('./views/base/po-list/po-list.component')
              .then(m => m.POListComponent),
          data: {
            title: 'Purchase Orders',
            roles: ['ROLE_BUYER']
          }
        },
        {
          path: 'po-details/:id',
          loadComponent: () =>
            import('./views/base/po-details/po-details.component')
              .then(m => m.PODetailsComponent),
          data: {
            title: 'Purchase Order Details',
            roles: ['ROLE_BUYER']
          }
        },
            // ✅ NEW: GRN Routes (Goods Receipt Note)
      {
        path: 'grn-list',
        loadComponent: () =>
          import('./views/base/grn-list/grn-list.component')
            .then(m => m.GrnListComponent),
        data: {
          title: 'Goods Receipt Notes',
          roles: ['ROLE_BUYER']
        }
      },
      {
        path: 'grn-create',
        loadComponent: () =>
          import('./views/base/grn-create/grn-create.component')
            .then(m => m.GrnCreateComponent),
        data: {
          title: 'Create GRN',
          roles: ['ROLE_BUYER']
        }
      },
      {
        path: 'grn-view/:id',
        loadComponent: () =>
          import('./views/base/grn-list/grn-list.component')
            .then(m => m.GrnListComponent),
        data: {
          title: 'View GRN',
          roles: ['ROLE_BUYER']
        }
      },
            {
        path: 'grn-qa/:id',
        loadComponent: () =>
          import('./views/base/grn-qa/grn-qa.component')
            .then(m => m.GrnQaComponent),
        data: {
          title: 'View GRN',
          roles: ['ROLE_BUYER']
        }
      },

      // ✅ NEW: 3-Way Match Route
      {
        path: 'three-way-match',
        loadComponent: () =>
          import('./views/base/three-way-match/three-way-match.component')
            .then(m => m.ThreeWayMatchComponent),
        data: {
          title: '3-Way Match',
          roles: ['ROLE_BUYER']
        }
      },
        {
          path: 'create-b',
          loadChildren: () =>
            import('./views/base/create-b/routes').then(m => m.routes),
          data: { roles: ['ORGANIZATION_ADMIN'] }
        },
        {
          path: 'create-s',
          loadChildren: () =>
            import('./views/base/create-s/routes').then(m => m.routes),
          data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
        },
        {
          path: 'evaluation-criteria',
          loadComponent: () =>
            import('./views/base/evaluation-criteria/evaluation-criteria.component')
              .then(m => m.EvaluationCriteriaComponent),
          data: {
            title: 'Evaluation Criteria Management',
            roles: ['ORGANIZATION_ADMIN']
          }
        },
        {
          path: 'invoices',
          loadComponent: () =>
            import('./views/base/buyer-invoices/buyer-invoices.component')
              .then(m => m.BuyerInvoicesComponent),
          data: {
            title: 'Invoice Management',
            roles: ['ROLE_BUYER']
          }
        },

        // ✅ RFQ Chat — accessible by both buyers and suppliers after supplier selection
        {
          path: 'rfq-chat/:rfqId',
          loadComponent: () =>
            import('./views/base/rfq-chat/rfq-chat.component')
              .then(m => m.RfqChatComponent),
          data: {
            title: 'RFQ Chat',
            roles: ['ROLE_BUYER', 'ROLE_SUPPLIER']
          }
        },
      ]
    },

    {
      path: 'login',
      loadComponent: () =>
        import('./views/pages/login/login.component')
          .then(m => m.LoginComponent),
      data: { title: 'Login Page' }
    },
    {
      path: '404',
      loadComponent: () =>
        import('./views/pages/page404/page404.component')
          .then(m => m.Page404Component),
      data: { title: 'Page Not Found' }
    },
    {
      path: '500',
      loadComponent: () =>
        import('./views/pages/page500/page500.component')
          .then(m => m.Page500Component),
      data: { title: 'Server Error' }
    },
    { path: '**', redirectTo: '404' }
  ];