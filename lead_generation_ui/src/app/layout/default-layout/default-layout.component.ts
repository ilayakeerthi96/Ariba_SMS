// import { Component, inject } from '@angular/core';
// import { RouterLink, RouterOutlet } from '@angular/router';
// import { NgScrollbar } from 'ngx-scrollbar';
// import { AuthService } from '../../shared/service/AuthService';

// import {
//   ContainerComponent,
//   INavData,
//   ShadowOnScrollDirective,
//   SidebarBrandComponent,
//   SidebarComponent,
//   SidebarFooterComponent,
//   SidebarHeaderComponent,
//   SidebarNavComponent,
//   SidebarToggleDirective,
//   SidebarTogglerDirective
// } from '@coreui/angular';

// import { DefaultFooterComponent, DefaultHeaderComponent } from './';
// import { navItems } from './_nav';

// @Component({
//   selector: 'app-dashboard',
//   templateUrl: './default-layout.component.html',
//   styleUrls: ['./default-layout.component.scss'],
//   imports: [
//     SidebarComponent,
//     SidebarHeaderComponent,
//     SidebarBrandComponent,
//     SidebarNavComponent,
//     ContainerComponent,
//     DefaultFooterComponent,
//     DefaultHeaderComponent,
//     NgScrollbar,
//     RouterOutlet,
//     RouterLink,
//     ShadowOnScrollDirective
//   ]
// })
// export class DefaultLayoutComponent {
//   private authService = inject(AuthService);

//   public navItems: INavData[] = [];

//   constructor() {
//     // Filter navItems once user roles are known
//     this.navItems = this.filterNavItems(navItems);
//   }

// private filterNavItems(items: INavData[]): INavData[] {
//   return items
//     .map(item => {
//       const roles = item.attributes?.['roles'] as string[] | undefined;
//       const allowed = !roles || this.authService.hasAnyRole(roles);

//       if (!allowed) return null;

//       let newItem: INavData = { ...item };

//       if (item.children) {
//         newItem.children = this.filterNavItems(item.children);
//       }

//       return newItem;
//     })
//     .filter((i): i is INavData => i !== null);
// }
// }


import { Component, OnInit, computed, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet, RouterLink } from '@angular/router';

import {
  SidebarComponent,
  SidebarHeaderComponent,
  SidebarBrandComponent,
  SidebarNavComponent,
  SidebarToggleDirective,
  ContainerComponent,
  ShadowOnScrollDirective,
  INavData
} from '@coreui/angular';

import { NgScrollbar } from 'ngx-scrollbar';

import { DefaultHeaderComponent } from './';
import { DefaultFooterComponent } from './';

import { navItems } from './_nav';
import { AuthService } from '../../shared/service/AuthService';

@Component({
  selector: 'app-default-layout',
  templateUrl: './default-layout.component.html',
  styleUrls: ['./default-layout.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    RouterOutlet,
    RouterLink,

    SidebarComponent,
    SidebarHeaderComponent,
    SidebarBrandComponent,
    SidebarNavComponent,
   // SidebarToggleDirective,
    ContainerComponent,
    ShadowOnScrollDirective,

    DefaultHeaderComponent,
    DefaultFooterComponent,
    NgScrollbar
  ]
})
export class DefaultLayoutComponent implements OnInit {

  private authService = inject(AuthService);

  // ✅ Store user roles (supports multi-role users)
  userRoles = signal<string[]>([]);

  // ✅ Filter menu dynamically based on roles
  navItemsFiltered = computed<INavData[]>(() => {
    const roles = this.userRoles();

    if (!roles || roles.length === 0) {
      console.warn('[MENU] No roles found for user');
      return [];
    }

    return this.filterNavItems(navItems, roles);
  });

ngOnInit(): void {
  const user = this.authService.getUser();

  console.log(
  '✅ FULL BUYER DETAILS:\n',
  JSON.stringify(user, null, 2)
);
  console.log('RAW USER FROM AUTH:', this.authService.getUser());

  let roles: string[] = [];

  if (user?.roles?.length) {
    // ✅ CLEAN null / undefined roles
    roles = user.roles.filter((r: any) => typeof r === 'string' && r.trim().length > 0);
  }

  // fallback (optional)
  if (!roles.length) {
    const roleFromStorage = localStorage.getItem('role');
    if (roleFromStorage) {
      roles = [roleFromStorage];
    }
  }

  console.log('✅ CLEANED USER ROLES:', roles);
  this.userRoles.set(roles);
}


  /**
   * ✅ Recursive role-based menu filtering
   */
private filterNavItems(items: INavData[], roles: string[]): INavData[] {
  return items
    .map(item => {
      const allowedRoles =
        item.attributes?.['roles'] as string[] | undefined;

      const allowed =
        !allowedRoles || allowedRoles.some(r => roles.includes(r));

      if (!allowed) return null;

      const newItem: INavData = { ...item };

      if (item.children) {
        newItem.children = this.filterNavItems(item.children, roles);

        // Hide parent if no visible children and no direct URL
        if (!newItem.children.length && !newItem.url) {
          return null;
        }
      }

      return newItem;
    })
    .filter((i): i is INavData => i !== null);
}

}
