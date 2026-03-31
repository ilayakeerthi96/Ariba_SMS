

// import { INavData } from '@coreui/angular';

// export const navItems: INavData[] = [
//   // ============================================
//   // 🆕 SUPERADMIN MENU (Creates Org Admins + Manages Buyers)
//   // ============================================
//   {
//     name: 'Super Admin Dashboard',
//     url: '/superadmin-dashboard',
//     iconComponent: { name: 'cil-shield-alt' },
//     attributes: { roles: ['SUPER_ADMIN'] }
//   },

//   // ============================================
//   // 🆕 ORGANIZATION ADMIN MENU (Full Management Power)
//   // ============================================
//   {
//     name: 'Admin Dashboard',
//     url: '/orgadmin-dashboard',
//     iconComponent: { name: 'cil-speedometer' },
//     attributes: { roles: ['ORGANIZATION_ADMIN'] }
//   },
//   {
//     name: 'Hierarchy Management',
//     url: '/hierarchy-management',
//     iconComponent: { name: 'cil-sitemap' },
//     attributes: { roles: ['ORGANIZATION_ADMIN'] },
//     children: [
//       {
//         name: 'Hierarchy Levels',
//         url: '/hierarchy-levels',
//         iconComponent: { name: 'cil-layers' }
//       },
//       {
//         name: 'Hierarchy Users',
//         url: '/hierarchy-users',
//         iconComponent: { name: 'cil-people' }
//       },
//       {
//         name: 'Reporting Structure',
//         url: '/reporting-structure',
//         iconComponent: { name: 'cil-sitemap' }
//       }
//     ]
//   },
//   {
//     name: 'User Management',
//     url: '/supplier-management',
//     iconComponent: { name: 'cil-sitemap' },
//     attributes: { roles: ['ORGANIZATION_ADMIN'] },
//     children: [
//       {
//         name: 'Dashboard',
//         url: '/dashboard',
//         iconComponent: { name: 'cil-layers' }
//       },
//       {
//        name: 'Create Supplier',
//     url: '/create-s',
//     iconComponent: { name: 'cil-building' },
//       },
//        {
//     name: 'Create Buyer',
//     url: '/create-b',
//     iconComponent: { name: 'cil-people' },
   
//   },
     
//     ]
//   },


//   // ============================================
//   // HIERARCHY USER DASHBOARDS (Staff)
//   // ============================================
//   // {
//   //   name: 'Dashboard',
//   //   url: '/dashboard',
//   //   iconComponent: { name: 'cil-speedometer' },
//   //   attributes: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE','ORGANIZATION_ADMIN'] }
//   // },

//   // ============================================
//   // BUYER MENU ITEMS
//   // ============================================
//   {
//     name: 'RFQ Dashboard',
//     url: '/rfq-dashboard',
//     iconComponent: { name: 'cil-layers' },
//     attributes: { roles: ['ROLE_BUYER'] }
//   },
//   {
//     name: 'Create RFQ',
//     url: '/create-rfq',
//     iconComponent: { name: 'cil-file' },
//     attributes: { roles: ['ROLE_BUYER'] }
//   },

//   // ============================================
//   // ADMIN MENU ITEMS (SuperAdmin, Org Admin, and Hierarchy Admin)
//   // ============================================
 
//   // {
//   //   name: 'Supplier Management',
//   //   url: '/create-s',
//   //   iconComponent: { name: 'cil-building' },
//   //   attributes: { roles: ['ADMIN', 'ORGANIZATION_ADMIN'] }
//   // },

//   // ============================================
//   // ROLE-SPECIFIC DASHBOARDS
//   // ============================================
//   {
//     name: 'CEO Dashboard',
//     url: '/ceo-dashboard',
//     iconComponent: { name: 'cil-chart-pie' },
//     attributes: { roles: ['CEO'] }
//   },
//   {
//     name: 'COO Dashboard',
//     url: '/coo-dashboard',
//     iconComponent: { name: 'cil-chart-line' },
//     attributes: { roles: ['COO'] }
//   },
//   {
//     name: 'Procurement Dashboard',
//     url: '/procurement-dashboard',
//     iconComponent: { name: 'cil-basket' },
//     attributes: { roles: ['PROCUREMENT'] }
//   },
//   {
//     name: 'Finance Dashboard',
//     url: '/finance-dashboard',
//     iconComponent: { name: 'cil-dollar' },
//     attributes: { roles: ['FINANCE'] }
//   },


//   // ============================================
//   // APPROVAL WORKFLOWS (For Staff)
//   // ============================================
//   {
//     name: 'Pending Approvals',
//     url: '/pending-approvals',
//     iconComponent: { name: 'cil-task' },
//     attributes: { roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE'] },

//   }
// ];

import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  // ============================================
  // 🆕 SUPERADMIN MENU (Creates Org Admins + Manages Buyers)
  // ============================================
  {
    name: 'Super Admin Dashboard',
    url: '/superadmin-dashboard',
    iconComponent: { name: 'cil-shield-alt' },
    attributes: { roles: ['SUPER_ADMIN'] }
  },

  // ============================================
  // 🆕 ORGANIZATION ADMIN MENU (Full Management Power)
  // ============================================
  {
    name: 'Org Admin Dashboard',
    url: '/orgadmin-dashboard',
    iconComponent: { name: 'cil-speedometer' },
    attributes: { roles: ['ORGANIZATION_ADMIN'] }
  },
  {
    name: 'Hierarchy Management',
    url: '/hierarchy-management',
    iconComponent: { name: 'cil-sitemap' },
    attributes: { roles: ['ORGANIZATION_ADMIN'] },
    children: [
      {
        name: 'Hierarchy Levels',
        url: '/hierarchy-levels',
        iconComponent: { name: 'cil-layers' }
      },
      {
        name: 'Hierarchy Users',
        url: '/hierarchy-users',
        iconComponent: { name: 'cil-people' }
      },
      {
        name: 'Reporting Structure',
        url: '/reporting-structure',
        iconComponent: { name: 'cil-sitemap' }
      }
    ]
  },
  {
    name: 'Management',
    url: '/supplier-management',
    iconComponent: { name: 'cil-sitemap' },
    attributes: { roles: ['ORGANIZATION_ADMIN'] },
    children: [
      {
        name: 'Dashboard',
        url: '/dashboard',
        iconComponent: { name: 'cil-layers' }
      },
          {
        name: 'Buyer Management',
        url: '/create-b',
        iconComponent: { name: 'cil-people' },
        attributes: { roles: ['ORGANIZATION_ADMIN'] }
      },
      {
       name: 'Supplier Management',
        url: '/create-s',
        iconComponent: { name: 'cil-building' },
      },
   
    ]
  },

  // ============================================
  // 🆕 DYNAMIC HIERARCHY USER DASHBOARD (Works for ALL levels)
  // ============================================
  {
    name: 'Dashboard',
    url: '/hierarchy-dashboard',
    iconComponent: { name: 'cil-speedometer' },
    attributes: { roles: ['ADMIN', 'CEO', 'COO', 'MANAGER', 'PROCUREMENT', 'FINANCE'] }
  },

  // ============================================
  // BUYER MENU ITEMS
  // ============================================
  {
    name: 'RFQ Dashboard',
    url: '/rfq-dashboard',
    iconComponent: { name: 'cil-layers' },
    attributes: { roles: ['ROLE_BUYER'] }
  },
  {
    name: 'Create RFQ',
    url: '/create-rfq',
    iconComponent: { name: 'cil-file' },
    attributes: { roles: ['ROLE_BUYER'] }
  },

  // ============================================
  // APPROVAL WORKFLOWS (For ALL Hierarchy Users)
  // ============================================
  {
    name: 'Pending Approvals',
    url: '/pending-approvals',
    iconComponent: { name: 'cil-task' },
    attributes: { roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE', 'MANAGER', 'ADMIN'] },
  }
];