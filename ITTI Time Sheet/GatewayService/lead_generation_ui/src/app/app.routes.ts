
// // // // import { Routes } from '@angular/router';

// // // // export const routes: Routes = [
// // // //   {
// // // //     path: '',
// // // //     redirectTo: 'login',
// // // //     pathMatch: 'full'
// // // //   },

// // // //   // ============================================
// // // //   // MAIN LAYOUT (Protected Routes)
// // // //   // ============================================
// // // //   {
// // // //     path: '',
// // // //     loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
// // // //     data: {
// // // //       title: 'Home',
// // // //     },
// // // //     children: [
// // // //       {
// // // //         path: '',
// // // //         pathMatch: 'full',
// // // //         redirectTo: 'dashboard'
// // // //       },

// // // //       // ============================================
// // // //       // 🆕 SUPERADMIN ROUTES (Phase 1-4)
// // // //       // ============================================
// // // //       {
// // // //         path: 'superadmin-dashboard',
// // // //         loadComponent: () => import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
// // // //           .then(m => m.SuperAdminDashboardComponent),
// // // //         data: { title: 'SuperAdmin Dashboard', roles: ['SUPER_ADMIN'] }
// // // //       },
// // // //       {
// // // //         path: 'hierarchy-levels',
// // // //         loadComponent: () => import('./views/base/hierarchy-levels/hierarchy-level-management.component')
// // // //           .then(m => m.HierarchyLevelManagementComponent),
// // // //         data: { title: 'Hierarchy Levels', roles: ['SUPER_ADMIN'] }
// // // //       },
// // // //       {
// // // //         path: 'hierarchy-users',
// // // //         loadComponent: () => import('./views/base/hierarchy-users/hierarchy-user-management.component')
// // // //           .then(m => m.HierarchyUserManagementComponent),
// // // //         data: { title: 'Hierarchy Users', roles: ['SUPER_ADMIN'] }
// // // //       },
// // // //       {
// // // //         path: 'reporting-structure',
// // // //         loadComponent: () => import('./views/base/reporting-structure/reporting-structure.component')
// // // //           .then(m => m.ReportingStructureComponent),
// // // //         data: { title: 'Reporting Structure', roles: ['SUPER_ADMIN'] }
// // // //       },

// // // //       // ============================================
// // // //       // EXISTING ROUTES
// // // //       // ============================================
// // // //       {
// // // //         path: 'dashboard',
// // // //         loadChildren: () => import('./views/base/dashboard/routes').then((m) => m.routes),
// // // //       },
// // // //       {
// // // //         path: 'lead-create',
// // // //         loadChildren: () => import('./views/base/lead-create/routes').then((m) => m.routes),
// // // //       },
// // // //       {
// // // //         path: 'rfq-dashboard',
// // // //         loadChildren: () => import('./views/base/rfq-dashboard/routes').then((m) => m.routes),
// // // //       },
// // // //       {
// // // //         path: 'create-rfq',
// // // //         loadChildren: () => import('./views/base/create-rfq/routes').then((m) => m.routes),
// // // //       },
// // // //       {
// // // //         path: 'create-b',
// // // //         loadChildren: () => import('./views/base/create-b/routes').then((m) => m.routes),
// // // //       },
// // // //       {
// // // //         path: 'create-s',
// // // //         loadChildren: () => import('./views/base/create-s/routes').then((m) => m.routes),
// // // //       }
// // // //     ]
// // // //   },

// // // //   // ============================================
// // // //   // PUBLIC ROUTES
// // // //   // ============================================
// // // //   {
// // // //     path: 'login',
// // // //     loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
// // // //     data: {
// // // //       title: 'Login Page'
// // // //     }
// // // //   },
// // // //   {
// // // //     path: '404',
// // // //     loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
// // // //     data: {
// // // //       title: 'Page 404'
// // // //     }
// // // //   },
// // // //   {
// // // //     path: '500',
// // // //     loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
// // // //     data: {
// // // //       title: 'Page 500'
// // // //     }
// // // //   },

// // // //   // ============================================
// // // //   // FALLBACK
// // // //   // ============================================
// // // //   { path: '**', redirectTo: 'login' }
// // // // ];


// // // import { Routes } from '@angular/router';

// // // export const routes: Routes = [
// // //   {
// // //     path: '',
// // //     redirectTo: 'login',
// // //     pathMatch: 'full'
// // //   },

// // //   // ============================================
// // //   // MAIN LAYOUT (Protected Routes)
// // //   // ============================================
// // //   {
// // //     path: '',
// // //     loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
// // //     data: {
// // //       title: 'Home',
// // //     },
// // //     children: [
// // //       {
// // //         path: '',
// // //         pathMatch: 'full',
// // //         redirectTo: 'dashboard'
// // //       },

// // //       // ============================================
// // //       // 🆕 SUPERADMIN ROUTES (Only Creates Org Admins)
// // //       // ============================================
// // //       {
// // //         path: 'superadmin-dashboard',
// // //         loadComponent: () => import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
// // //           .then(m => m.SuperAdminDashboardComponent),
// // //         data: { title: 'SuperAdmin Dashboard', roles: ['SUPER_ADMIN'] }
// // //       },

// // //       // ============================================
// // //       // 🆕 ORGANIZATION ADMIN ROUTES (Full Management)
// // //       // ============================================
// // //       {
// // //         path: 'orgadmin-dashboard',
// // //         loadComponent: () => import('./views/base/orgadmin-dashboard/orgadmin-dashboard.component')
// // //           .then(m => m.OrgAdminDashboardComponent),
// // //         data: { title: 'Organization Admin Dashboard', roles: ['ORGANIZATION_ADMIN'] }
// // //       },
// // //       {
// // //         path: 'hierarchy-levels',
// // //         loadComponent: () => import('./views/base/hierarchy-levels/hierarchy-level-management.component')
// // //           .then(m => m.HierarchyLevelManagementComponent),
// // //         data: { title: 'Hierarchy Levels', roles: ['ORGANIZATION_ADMIN'] }
// // //       },
// // //       {
// // //         path: 'hierarchy-users',
// // //         loadComponent: () => import('./views/base/hierarchy-users/hierarchy-user-management.component')
// // //           .then(m => m.HierarchyUserManagementComponent),
// // //         data: { title: 'Hierarchy Users', roles: ['ORGANIZATION_ADMIN'] }
// // //       },
// // //       {
// // //         path: 'reporting-structure',
// // //         loadComponent: () => import('./views/base/reporting-structure/reporting-structure.component')
// // //           .then(m => m.ReportingStructureComponent),
// // //         data: { title: 'Reporting Structure', roles: ['ORGANIZATION_ADMIN'] }
// // //       },

// // //       // ============================================
// // //       // HIERARCHY USER ROUTES (Staff)
// // //       // ============================================
// // //       {
// // //         path: 'dashboard',
// // //         loadChildren: () => import('./views/base/dashboard/routes').then((m) => m.routes),
// // //         data: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE'] }
// // //       },
// // //       {
// // //         path: 'lead-create',
// // //         loadChildren: () => import('./views/base/lead-create/routes').then((m) => m.routes),
// // //         data: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE'] }
// // //       },

// // //       // ============================================
// // //       // BUYER ROUTES
// // //       // ============================================
// // //       {
// // //         path: 'rfq-dashboard',
// // //         loadChildren: () => import('./views/base/rfq-dashboard/routes').then((m) => m.routes),
// // //         data: { roles: ['ROLE_BUYER'] }
// // //       },
// // //       {
// // //         path: 'create-rfq',
// // //         loadChildren: () => import('./views/base/create-rfq/routes').then((m) => m.routes),
// // //         data: { roles: ['ROLE_BUYER'] }
// // //       },

// // //       // ============================================
// // //       // ADMIN ROUTES
// // //       // ============================================
// // //       {
// // //         path: 'create-b',
// // //         loadChildren: () => import('./views/base/create-b/routes').then((m) => m.routes),
// // //         data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
// // //       },
// // //       {
// // //         path: 'create-s',
// // //         loadChildren: () => import('./views/base/create-s/routes').then((m) => m.routes),
// // //         data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
// // //       }
// // //     ]
// // //   },

// // //   // ============================================
// // //   // PUBLIC ROUTES
// // //   // ============================================
// // //   {
// // //     path: 'login',
// // //     loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
// // //     data: {
// // //       title: 'Login Page'
// // //     }
// // //   },
// // //   {
// // //     path: '404',
// // //     loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
// // //     data: {
// // //       title: 'Page 404'
// // //     }
// // //   },
// // //   {
// // //     path: '500',
// // //     loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
// // //     data: {
// // //       title: 'Page 500'
// // //     }
// // //   },

// // //   // ============================================
// // //   // FALLBACK
// // //   // ============================================
// // //   { path: '**', redirectTo: 'login' }
// // // ];

// // import { Routes } from '@angular/router';

// // export const routes: Routes = [
// //   {
// //     path: '',
// //     redirectTo: 'login',
// //     pathMatch: 'full'
// //   },

// //   // ============================================
// //   // MAIN LAYOUT (Protected Routes)
// //   // ============================================
// //   {
// //     path: '',
// //     loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
// //     data: {
// //       title: 'Home',
// //     },
// //     children: [
// //       {
// //         path: '',
// //         pathMatch: 'full',
// //         redirectTo: 'dashboard'
// //       },

// //       // ============================================
// //       // 🆕 SUPERADMIN ROUTES (Creates Org Admins + Manages Buyers)
// //       // ============================================
// //       {
// //         path: 'superadmin-dashboard',
// //         loadComponent: () => import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
// //           .then(m => m.SuperAdminDashboardComponent),
// //         data: { title: 'SuperAdmin Dashboard', roles: ['SUPER_ADMIN'] }
// //       },

// //       // ============================================
// //       // 🆕 ORGANIZATION ADMIN ROUTES (Full Management)
// //       // ============================================
// //       {
// //         path: 'orgadmin-dashboard',
// //         loadComponent: () => import('./views/base/orgadmin-dashboard/orgadmin-dashboard.component')
// //           .then(m => m.OrgAdminDashboardComponent),
// //         data: { title: 'Organization Admin Dashboard', roles: ['ORGANIZATION_ADMIN'] }
// //       },
// //       {
// //         path: 'hierarchy-levels',
// //         loadComponent: () => import('./views/base/hierarchy-levels/hierarchy-level-management.component')
// //           .then(m => m.HierarchyLevelManagementComponent),
// //         data: { title: 'Hierarchy Levels', roles: ['ORGANIZATION_ADMIN'] }
// //       },
// //       {
// //         path: 'hierarchy-users',
// //         loadComponent: () => import('./views/base/hierarchy-users/hierarchy-user-management.component')
// //           .then(m => m.HierarchyUserManagementComponent),
// //         data: { title: 'Hierarchy Users', roles: ['ORGANIZATION_ADMIN'] }
// //       },
// //       {
// //         path: 'reporting-structure',
// //         loadComponent: () => import('./views/base/reporting-structure/reporting-structure.component')
// //           .then(m => m.ReportingStructureComponent),
// //         data: { title: 'Reporting Structure', roles: ['ORGANIZATION_ADMIN'] }
// //       },

// //       // ============================================
// //       // HIERARCHY USER ROUTES (Staff)
// //       // ============================================
// //       {
// //         path: 'dashboard',
// //         loadChildren: () => import('./views/base/dashboard/routes').then((m) => m.routes),
// //         data: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE'] }
// //       },
// //       {
// //         path: 'lead-create',
// //         loadChildren: () => import('./views/base/lead-create/routes').then((m) => m.routes),
// //         data: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE'] }
// //       },

// //       // ============================================
// //       // BUYER ROUTES
// //       // ============================================
// //       {
// //         path: 'rfq-dashboard',
// //         loadChildren: () => import('./views/base/rfq-dashboard/routes').then((m) => m.routes),
// //         data: { roles: ['ROLE_BUYER'] }
// //       },
// //       {
// //         path: 'create-rfq',
// //         loadChildren: () => import('./views/base/create-rfq/routes').then((m) => m.routes),
// //         data: { roles: ['ROLE_BUYER'] }
// //       },

// //       // ============================================
// //       // ADMIN ROUTES (SuperAdmin + Org Admin + Hierarchy Admin)
// //       // ============================================
// //       {
// //         path: 'create-b',
// //         loadChildren: () => import('./views/base/create-b/routes').then((m) => m.routes),
// //         data: { roles: ['SUPER_ADMIN', 'ADMIN', 'ORGANIZATION_ADMIN'] }
// //       },
// //       {
// //         path: 'create-s',
// //         loadChildren: () => import('./views/base/create-s/routes').then((m) => m.routes),
// //         data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
// //       }
// //     ]
// //   },
  

// //   // ============================================
// //   // PUBLIC ROUTES
// //   // ============================================
// //   {
// //     path: 'login',
// //     loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
// //     data: {
// //       title: 'Login Page'
// //     }
// //   },
// //   {
// //     path: '404',
// //     loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
// //     data: {
// //       title: 'Page 404'
// //     }
// //   },
// //   {
// //     path: '500',
// //     loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
// //     data: {
// //       title: 'Page 500'
// //     }
// //   },

// //   // ============================================
// //   // FALLBACK
// //   // ============================================
// //   { path: '**', redirectTo: 'login' }
// // ];

// import { Routes } from '@angular/router';

// export const routes: Routes = [
//   {
//     path: '',
//     redirectTo: 'login',
//     pathMatch: 'full'
//   },

//   // ============================================
//   // MAIN LAYOUT (Protected Routes)
//   // ============================================
//   {
//     path: '',
//     loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
//     data: {
//       title: 'Home',
//     },
//     children: [
//       {
//         path: '',
//         pathMatch: 'full',
//         redirectTo: 'dashboard'
//       },

//       // ============================================
//       // 🆕 CHANGE PASSWORD ROUTE (All Admins)
//       // ============================================
//       {
//         path: 'change-password',
//         loadComponent: () => import('./views/base/change-password/change-password.component')
//           .then(m => m.ChangePasswordComponent),
//         data: { title: 'Change Password' }
//       },

//       // ============================================
//       // 🆕 SUPERADMIN ROUTES
//       // ============================================
//       {
//         path: 'superadmin-dashboard',
//         loadComponent: () => import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
//           .then(m => m.SuperAdminDashboardComponent),
//         data: { title: 'Super Admin Dashboard', roles: ['SUPER_ADMIN'] }
//       },

//       // ============================================
//       // 🆕 ORGANIZATION ADMIN ROUTES
//       // ============================================
//       {
//         path: 'orgadmin-dashboard',
//         loadComponent: () => import('./views/base/orgadmin-dashboard/orgadmin-dashboard.component')
//           .then(m => m.OrgAdminDashboardComponent),
//         data: { title: 'Organization Admin Dashboard', roles: ['ORGANIZATION_ADMIN'] }
//       },
//       {
//         path: 'hierarchy-levels',
//         loadComponent: () => import('./views/base/hierarchy-levels/hierarchy-level-management.component')
//           .then(m => m.HierarchyLevelManagementComponent),
//         data: { title: 'Hierarchy Levels', roles: ['ORGANIZATION_ADMIN'] }
//       },
//       {
//         path: 'hierarchy-users',
//         loadComponent: () => import('./views/base/hierarchy-users/hierarchy-user-management.component')
//           .then(m => m.HierarchyUserManagementComponent),
//         data: { title: 'Hierarchy Users', roles: ['ORGANIZATION_ADMIN'] }
//       },
//       {
//         path: 'reporting-structure',
//         loadComponent: () => import('./views/base/reporting-structure/reporting-structure.component')
//           .then(m => m.ReportingStructureComponent),
//         data: { title: 'Reporting Structure', roles: ['ORGANIZATION_ADMIN'] }
//       },

//       // ============================================
//       // HIERARCHY USER ROUTES
//       // ============================================
//       {
//         path: 'dashboard',
//         loadChildren: () => import('./views/base/dashboard/routes').then((m) => m.routes),
//         data: { roles: ['ADMIN', 'CEO', 'COO', 'PROCUREMENT', 'FINANCE'] }
//       },
//       {
//         path: 'lead-create',
//         loadChildren: () => import('./views/base/lead-create/routes').then((m) => m.routes),
//         data: { roles: ['ADMIN', 'CEO', 'COO',  'PROCUREMENT', 'FINANCE'] }
//       },
//       {
//   path: 'pending-approvals',
//   loadChildren: () => import('./views/base/pending-approvals/routes').then((m) => m.routes),
//   data: { roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE'] }
// },


//       // ============================================
//       // BUYER ROUTES
//       // ============================================
//       {
//         path: 'rfq-dashboard',
//         loadChildren: () => import('./views/base/rfq-dashboard/routes').then((m) => m.routes),
//         data: { roles: ['ROLE_BUYER'] }
//       },
//       {
//         path: 'create-rfq',
//         loadChildren: () => import('./views/base/create-rfq/routes').then((m) => m.routes),
//         data: { roles: ['ROLE_BUYER'] }
//       },

//       // ============================================
//       // ADMIN ROUTES
//       // ============================================
//       {
//         path: 'create-b',
//         loadChildren: () => import('./views/base/create-b/routes').then((m) => m.routes),
//         data: { roles: [ 'ADMIN', 'ORGANIZATION_ADMIN'] }
//       },
//       {
//         path: 'create-s',
//         loadChildren: () => import('./views/base/create-s/routes').then((m) => m.routes),
//         data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
//       }
//     ]
//   },

//   // ============================================
//   // PUBLIC ROUTES
//   // ============================================
//   {
//     path: 'login',
//     loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
//     data: {
//       title: 'Login Page'
//     }
//   },
//   {
//     path: '404',
//     loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
//     data: {
//       title: 'Page 404'
//     }
//   },
//   {
//     path: '500',
//     loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
//     data: {
//       title: 'Page 500'
//     }
//   },

//   // ============================================
//   // FALLBACK
//   // ============================================
//   { path: '**', redirectTo: 'login' }
// ];

import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  // ============================================
  // MAIN LAYOUT (Protected Routes)
  // ============================================
  {
    path: '',
    loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
    data: {
      title: 'Home',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'hierarchy-dashboard' // Default to hierarchy dashboard
      },

      // ============================================
      // 🆕 DYNAMIC HIERARCHY DASHBOARD
      // ============================================
      {
        path: 'hierarchy-dashboard',
        loadComponent: () => import('./views/base/hierarchy-dashboard/hierarchy-dashboard.component')
          .then(m => m.HierarchyDashboardComponent),
        data: { 
          title: 'Dashboard',
          roles: ['CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE', 'ADMIN'] 
        }
      },

      // ============================================
      // 🆕 CHANGE PASSWORD ROUTE (All Admins)
      // ============================================
      {
        path: 'change-password',
        loadComponent: () => import('./views/base/change-password/change-password.component')
          .then(m => m.ChangePasswordComponent),
        data: { title: 'Change Password' }
      },

      // ============================================
      // 🆕 SUPERADMIN ROUTES
      // ============================================
      {
        path: 'superadmin-dashboard',
        loadComponent: () => import('./views/base/superadmin-dashboard/superadmin-dashboard.component')
          .then(m => m.SuperAdminDashboardComponent),
        data: { title: 'Super Admin Dashboard', roles: ['SUPER_ADMIN'] }
      },

      // ============================================
      // 🆕 ORGANIZATION ADMIN ROUTES
      // ============================================
      {
        path: 'orgadmin-dashboard',
        loadComponent: () => import('./views/base/orgadmin-dashboard/orgadmin-dashboard.component')
          .then(m => m.OrgAdminDashboardComponent),
        data: { title: 'Organization Admin Dashboard', roles: ['ORGANIZATION_ADMIN'] }
      },
      {
        path: 'hierarchy-levels',
        loadComponent: () => import('./views/base/hierarchy-levels/hierarchy-level-management.component')
          .then(m => m.HierarchyLevelManagementComponent),
        data: { title: 'Hierarchy Levels', roles: ['ORGANIZATION_ADMIN'] }
      },
      {
        path: 'hierarchy-users',
        loadComponent: () => import('./views/base/hierarchy-users/hierarchy-user-management.component')
          .then(m => m.HierarchyUserManagementComponent),
        data: { title: 'Hierarchy Users', roles: ['ORGANIZATION_ADMIN'] }
      },
      {
        path: 'reporting-structure',
        loadComponent: () => import('./views/base/reporting-structure/reporting-structure.component')
          .then(m => m.ReportingStructureComponent),
        data: { title: 'Reporting Structure', roles: ['ORGANIZATION_ADMIN'] }
      },

      // ============================================
      // HIERARCHY USER ROUTES
      // ============================================
      {
        path: 'dashboard',
        loadChildren: () => import('./views/base/dashboard/routes').then((m) => m.routes),
        data: { roles: ['ADMIN', 'CEO', 'COO', 'PROCUREMENT', 'FINANCE'] }
      },
      {
        path: 'lead-create',
        loadChildren: () => import('./views/base/lead-create/routes').then((m) => m.routes),
        data: { roles: ['ADMIN', 'CEO', 'COO',  'PROCUREMENT', 'FINANCE'] }
      },
      {
        path: 'pending-approvals',
        loadChildren: () => import('./views/base/pending-approvals/routes').then((m) => m.routes),
        data: { roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE', 'MANAGER', 'ADMIN'] }
      },

      // ============================================
      // 🆕 LEGACY ROLE-SPECIFIC DASHBOARDS (Optional - redirect to hierarchy-dashboard)
      // ============================================
      {
        path: 'ceo-dashboard',
        redirectTo: 'hierarchy-dashboard',
        pathMatch: 'full'
      },
      {
        path: 'coo-dashboard',
        redirectTo: 'hierarchy-dashboard',
        pathMatch: 'full'
      },
      {
        path: 'procurement-dashboard',
        redirectTo: 'hierarchy-dashboard',
        pathMatch: 'full'
      },
      {
        path: 'finance-dashboard',
        redirectTo: 'hierarchy-dashboard',
        pathMatch: 'full'
      },
      {
        path: 'manager-dashboard',
        redirectTo: 'hierarchy-dashboard',
        pathMatch: 'full'
      },

      // ============================================
      // BUYER ROUTES
      // ============================================
      {
        path: 'rfq-dashboard',
        loadChildren: () => import('./views/base/rfq-dashboard/routes').then((m) => m.routes),
        data: { roles: ['ROLE_BUYER'] }
      },
      {
        path: 'create-rfq',
        loadChildren: () => import('./views/base/create-rfq/routes').then((m) => m.routes),
        data: { roles: ['ROLE_BUYER'] }
      },

      // ============================================
      // ADMIN ROUTES
      // ============================================
      {
        path: 'create-b',
        loadChildren: () => import('./views/base/create-b/routes').then((m) => m.routes),
        data: { roles: ['ORGANIZATION_ADMIN'] }
      },
      {
        path: 'create-s',
        loadChildren: () => import('./views/base/create-s/routes').then((m) => m.routes),
        data: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
      }
    ]
  },

  // ============================================
  // PUBLIC ROUTES
  // ============================================
  {
    path: 'login',
    loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
    data: {
      title: 'Login Page'
    }
  },
  {
    path: '404',
    loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
    data: {
      title: 'Page 404'
    }
  },
  {
    path: '500',
    loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
    data: {
      title: 'Page 500'
    }
  },

  // ============================================
  // FALLBACK
  // ============================================
  { path: '**', redirectTo: 'login' }
];