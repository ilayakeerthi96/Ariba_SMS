import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  // ============================================
  // SUPERADMIN MENU
  // ============================================
  {
    name: 'Dashboard',
    url: '/superadmin-dashboard',
    iconComponent: { name: 'cil-shield-alt' },
    attributes: { roles: ['SUPER_ADMIN'] }
  },

  // ============================================
  // ORGANIZATION ADMIN MENU
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
           attributes: { roles: ['ORGANIZATION_ADMIN',] }
      },
      {
        name: 'Evaluation Criteria',
        url: '/evaluation-criteria',
        iconComponent: { name: 'cil-star' },
        attributes: { roles: ['ORGANIZATION_ADMIN'] }
      }
    ]
  },

  // ============================================
  // DYNAMIC HIERARCHY USER DASHBOARD
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
    {
    name: 'Supplier Management',
    url: '/create-s',
    iconComponent: { name: 'cil-building' },
    attributes: { roles: ['ROLE_BUYER'] }
  },
  // {
  //   name: 'Quote Comparison',
  //   url: '/quote-comparison',
  //   iconComponent: { name: 'cil-chart-line' },
  //   attributes: { roles: ['ROLE_BUYER'] }
  // },
  // ✅ NEW: Purchase Orders nav item
  {
    name: 'Purchase Orders',
    url: '/po-list',
    iconComponent: { name: 'cil-description' },
    attributes: { roles: ['ROLE_BUYER'] }
  },
  {
  name: 'Invoice Management',
  url: '/invoices',
  iconComponent: { name: 'cil-description' },
  attributes: { roles: ['ROLE_BUYER'] }
},
  {
  name: 'GRN',
  url: '/grn-list',
  iconComponent: { name: 'cil-description' },
  attributes: { roles: ['ROLE_BUYER','ROLE_BUYER'] }
},
  {
  name: '3-Way Match',
  url: '/three-way-match',
  iconComponent: { name: 'cil-description' },
  attributes: { roles: ['ROLE_BUYER','ROLE_BUYER'] }
},

  // ============================================
  // SUPPLIER MENU
  // ============================================
  {
    name: 'Supplier Dashboard',
    url: '/supplier-dashboard',
    iconComponent: { name: 'cil-speedometer' },
    attributes: { roles: ['ROLE_SUPPLIER'] }
  },

  // ============================================
  // APPROVAL WORKFLOWS
  // ============================================
  {
    name: 'Pending Approvals',
    url: '/pending-approvals',
    iconComponent: { name: 'cil-task' },
    attributes: { roles: ['CEO', 'COO', 'PROCUREMENT', 'FINANCE', 'MANAGER', 'ADMIN'] },
  }
];