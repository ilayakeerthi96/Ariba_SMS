
// import { NgTemplateOutlet, CommonModule } from '@angular/common';
// import { Component, computed, inject, input, OnInit, OnDestroy } from '@angular/core';
// import { MatDialog, MatDialogModule } from '@angular/material/dialog';
// import { RouterLink } from '@angular/router';
// import { Subscription } from 'rxjs';

// import {
//   AvatarComponent,
//   BreadcrumbRouterComponent,
//   ColorModeService,
//   ContainerComponent,
//   DropdownComponent,
//   DropdownItemDirective,
//   DropdownMenuDirective,
//   DropdownToggleDirective,
//   HeaderComponent,
//   HeaderNavComponent,
//   HeaderTogglerDirective,
//   SidebarToggleDirective
// } from '@coreui/angular';

// import { IconDirective } from '@coreui/icons-angular';
// import { SessionTimerComponent } from '../../../views/session-timer/session-timer-component';
// import { SessionExpiryDialogComponent } from '../../../shared/session-expiry-dialog/session-expiry-dialog.component';
// import { DataService } from '../../../shared/service/DataService';
// import { AuthService } from '../../../shared/service/AuthService';

// @Component({
//   selector: 'app-default-header',
//   templateUrl: './default-header.component.html',
//   styleUrls: ['./default-header.component.css'],
//   imports: [
//     CommonModule,
//     ContainerComponent,
//     HeaderTogglerDirective,
//     SidebarToggleDirective,
//     IconDirective,
//     HeaderNavComponent,
//     RouterLink,
//     NgTemplateOutlet,
//     DropdownComponent,
//     DropdownToggleDirective,
//     AvatarComponent,
//     DropdownMenuDirective,
//     DropdownItemDirective,
//     MatDialogModule,
//     SessionTimerComponent,
//     BreadcrumbRouterComponent,
//     SessionExpiryDialogComponent
//   ]
// })
// export class DefaultHeaderComponent extends HeaderComponent implements OnInit, OnDestroy {

//   readonly #colorModeService = inject(ColorModeService);
//   readonly colorMode = this.#colorModeService.colorMode;

//   userName: string = 'Guest';
//   designation: string = 'N/A';
//   designationDisplay: string = 'N/A';

//   companyName: string = '';
//   companyLogo: string | null = null;
//   departmentName: string = '';
//   buyerId: number | null = null;
//   supplierId: number | null = null;

//   showSessionDialog = false;
//   sessionExpiryMs = 0;

//   private warningSub?: Subscription;
//   sidebarId = input('sidebar1');

//   constructor(
//     private dialog: MatDialog,
//     private dataService: DataService,
//     private authService: AuthService
//   ) {
//     super();
//   }

//   readonly colorModes = [
//     { name: 'light', text: 'Light', icon: 'cilSun' },
//     { name: 'dark', text: 'Dark', icon: 'cilMoon' },
//     { name: 'auto', text: 'Auto', icon: 'cilContrast' }
//   ];

//   readonly icons = computed(() => {
//     const currentMode = this.colorMode();
//     return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? 'cilSun';
//   });

//   ngOnInit(): void {
//     this.loadUserInfo();
//     this.loadCompanyLogo();   // ✅ called AFTER loadUserInfo so IDs are set

//     this.warningSub = this.authService.sessionWarning$.subscribe(expiryMs => {
//       if (expiryMs !== null) {
//         this.sessionExpiryMs = expiryMs;
//         this.showSessionDialog = true;
//       } else {
//         this.showSessionDialog = false;
//       }
//     });
//   }

//   ngOnDestroy(): void {
//     this.warningSub?.unsubscribe();
//   }

//   // ============================================
//   // LOAD USER INFO FROM localStorage
//   // ============================================
//   private loadUserInfo(): void {
//     try {
//       const signinRaw = localStorage.getItem('signinData');
//       const signinData = signinRaw ? JSON.parse(signinRaw) : null;

//       // Full name
//       this.userName =
//         signinData?.fullName ||
//         localStorage.getItem('fullName') ||
//         'Guest';

//       // Role
//       const role =
//         signinData?.role ||
//         signinData?.userType ||
//         localStorage.getItem('role') ||
//         '';
//       this.designation = role;
//       this.designationDisplay = this.getRoleDisplayName(role);

//       // Company name
//       this.companyName =
//         signinData?.buyer?.name ||
//         signinData?.supplier?.name ||      // ✅ also check supplier.name
//         localStorage.getItem('companyName') ||
//         signinData?.companyName ||
//         '';

//       // Department
//       this.departmentName = this.getDepartmentName(signinData);

//       // ✅ buyerId
//       const buyerIdRaw = localStorage.getItem('buyerId');
//       this.buyerId = buyerIdRaw ? Number(buyerIdRaw) : null;
//       if (!this.buyerId && signinData?.buyer?.id) {
//         this.buyerId = Number(signinData.buyer.id);
//       }

//       // ✅ supplierId
//       const supplierIdRaw = localStorage.getItem('supplierId');
//       this.supplierId = supplierIdRaw ? Number(supplierIdRaw) : null;
//       if (!this.supplierId && signinData?.supplier?.id) {
//         this.supplierId = Number(signinData.supplier.id);
//       }

//       console.log('🔍 [HEADER] userName:', this.userName);
//       console.log('🔍 [HEADER] companyName:', this.companyName);
//       console.log('🔍 [HEADER] buyerId:', this.buyerId);
//       console.log('🔍 [HEADER] supplierId:', this.supplierId);
//       console.log('🔍 [HEADER] role:', role);

//     } catch (error) {
//       console.error('Error loading user info:', error);
//       this.setDefaultValues();
//     }
//   }

//   // ============================================
//   // ✅ FIXED: LOAD COMPANY LOGO — handles BOTH buyer and supplier
//   // ============================================
//   private loadCompanyLogo(): void {
//     // ✅ Try buyer logo first
//     if (this.buyerId) {
//       console.log('🖼️ [HEADER] Loading buyer logo for buyerId:', this.buyerId);
//       this.dataService.getBuyerLogoBase64(this.buyerId).subscribe({
//         next: (logoData: string | null) => {
//           if (logoData && logoData.trim() && logoData.trim() !== 'null') {
//             this.companyLogo = logoData;
//             console.log('✅ [HEADER] Buyer logo loaded successfully');
//           } else {
//             this.companyLogo = null;
//             console.log('⚠️ [HEADER] No buyer logo — showing text:', this.companyName);
//           }
//         },
//         error: (err: any) => {
//           this.companyLogo = null;
//           console.warn('⚠️ [HEADER] Buyer logo fetch error:', err?.status);
//         }
//       });
//       return;
//     }

//     // ✅ Try supplier logo if no buyerId
//     if (this.supplierId) {
//       console.log('🖼️ [HEADER] Loading supplier logo for supplierId:', this.supplierId);
//       this.dataService.getSupplierLogoBase64(this.supplierId).subscribe({
//         next: (logoData: string | null) => {
//           if (logoData && logoData.trim() && logoData.trim() !== 'null') {
//             this.companyLogo = logoData;
//             console.log('✅ [HEADER] Supplier logo loaded successfully');
//           } else {
//             this.companyLogo = null;
//             console.log('⚠️ [HEADER] No supplier logo — showing text:', this.companyName);
//           }
//         },
//         error: (err: any) => {
//           this.companyLogo = null;
//           console.warn('⚠️ [HEADER] Supplier logo fetch error:', err?.status);
//         }
//       });
//       return;
//     }

//     // No ID at all — just show text badge
//     console.log('⚠️ [HEADER] No buyerId or supplierId — showing companyName text badge');
//   }

//   // ============================================
//   // HELPERS
//   // ============================================
//   private getDepartmentName(signinData: any): string {
//     if (!signinData) return localStorage.getItem('departmentName') || '';
//     if (signinData.department) {
//       if (typeof signinData.department === 'string') return signinData.department;
//       if (signinData.department.departmentName) return signinData.department.departmentName;
//       if (signinData.department.name) return signinData.department.name;
//     }
//     return localStorage.getItem('departmentName') || '';
//   }

//   private getRoleDisplayName(role: string): string {
//     const roleMap: { [key: string]: string } = {
//       SUPER_ADMIN: 'Super Admin',
//       ORGANIZATION_ADMIN: 'Organization Admin',
//       CEO: 'CEO',
//       COO: 'COO',
//       MANAGER: 'Manager',
//       PROCUREMENT: 'Procurement Manager',
//       FINANCE: 'Finance Manager',
//       ADMIN: 'Administrator',
//       ROLE_BUYER: 'RFQ Creator',
//       ROLE_SUPPLIER: 'Supplier'
//     };
//     return roleMap[role] || role || 'User';
//   }

//   private setDefaultValues(): void {
//     this.userName = 'Guest';
//     this.designation = 'N/A';
//     this.designationDisplay = 'N/A';
//     this.companyName = '';
//     this.departmentName = '';
//     this.companyLogo = null;
//     this.buyerId = null;
//     this.supplierId = null;
//   }

//   onExtendSession(): void {
//     this.showSessionDialog = false;
//     this.authService.extendSession();
//   }

//   logout(): void {
//     this.showSessionDialog = false;
//     this.authService.logout();
//   }

//   public newMessages: any[] = [];
//   public newNotifications: any[] = [];
//   public newStatus: any[] = [];
//   public newTasks: any[] = [];
// }


import { NgTemplateOutlet, CommonModule } from '@angular/common';
import { Component, computed, inject, input, OnInit, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';

import {
  AvatarComponent,
  BreadcrumbRouterComponent,
  ColorModeService,
  ContainerComponent,
  DropdownComponent,
  DropdownItemDirective,
  DropdownMenuDirective,
  DropdownToggleDirective,
  HeaderComponent,
  HeaderNavComponent,
  HeaderTogglerDirective,
  SidebarToggleDirective
} from '@coreui/angular';

import { IconDirective } from '@coreui/icons-angular';
import { SessionTimerComponent } from '../../../views/session-timer/session-timer-component';
import { SessionExpiryDialogComponent } from '../../../shared/session-expiry-dialog/session-expiry-dialog.component';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';
import { ThemeSwitcherComponent } from '../../../shared/components/theme-switcher/theme-switcher.component';

@Component({
  selector: 'app-default-header',
  templateUrl: './default-header.component.html',
  styleUrls: ['./default-header.component.css'],
  imports: [
    CommonModule,
    ContainerComponent,
    HeaderTogglerDirective,
    SidebarToggleDirective,
    IconDirective,
    HeaderNavComponent,
    RouterLink,
    NgTemplateOutlet,
    DropdownComponent,
    DropdownToggleDirective,
    AvatarComponent,
    DropdownMenuDirective,
    DropdownItemDirective,
    MatDialogModule,
    SessionTimerComponent,
    BreadcrumbRouterComponent,
    SessionExpiryDialogComponent,
    ThemeSwitcherComponent   
  ]
})
export class DefaultHeaderComponent extends HeaderComponent implements OnInit, OnDestroy {

  readonly #colorModeService = inject(ColorModeService);
  readonly colorMode = this.#colorModeService.colorMode;

  userName: string = 'Guest';
  designation: string = 'N/A';
  designationDisplay: string = 'N/A';

  companyName: string = '';
  companyLogo: string | null = null;
  departmentName: string = '';
  buyerId: number | null = null;
  supplierId: number | null = null;

  showSessionDialog = false;
  sessionExpiryMs = 0;

  private warningSub?: Subscription;
  sidebarId = input('sidebar1');

  constructor(
    private dialog: MatDialog,
    private dataService: DataService,
    private authService: AuthService
  ) {
    super();
  }

  readonly colorModes = [
    { name: 'light', text: 'Light', icon: 'cilSun' },
    { name: 'dark', text: 'Dark', icon: 'cilMoon' },
    { name: 'auto', text: 'Auto', icon: 'cilContrast' }
  ];

  readonly icons = computed(() => {
    const currentMode = this.colorMode();
    return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? 'cilSun';
  });

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadCompanyLogo();   // ✅ called AFTER loadUserInfo so IDs are set

    this.warningSub = this.authService.sessionWarning$.subscribe(expiryMs => {
      if (expiryMs !== null) {
        this.sessionExpiryMs = expiryMs;
        this.showSessionDialog = true;
      } else {
        this.showSessionDialog = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.warningSub?.unsubscribe();
  }

  // ============================================
  // LOAD USER INFO FROM localStorage
  // ============================================
  private loadUserInfo(): void {
    try {
      const signinRaw = localStorage.getItem('signinData');
      const signinData = signinRaw ? JSON.parse(signinRaw) : null;

      // Full name
      this.userName =
        signinData?.fullName ||
        localStorage.getItem('fullName') ||
        'Guest';

      // Role
      const role =
        signinData?.role ||
        signinData?.userType ||
        localStorage.getItem('role') ||
        '';
      this.designation = role;
      this.designationDisplay = this.getRoleDisplayName(role);

      // Company name
      this.companyName =
        signinData?.buyer?.name ||
        signinData?.supplier?.name ||      // ✅ also check supplier.name
        localStorage.getItem('companyName') ||
        signinData?.companyName ||
        '';

      // Department
      this.departmentName = this.getDepartmentName(signinData);

      // ✅ buyerId
      const buyerIdRaw = localStorage.getItem('buyerId');
      this.buyerId = buyerIdRaw ? Number(buyerIdRaw) : null;
      if (!this.buyerId && signinData?.buyer?.id) {
        this.buyerId = Number(signinData.buyer.id);
      }

      // ✅ supplierId
      const supplierIdRaw = localStorage.getItem('supplierId');
      this.supplierId = supplierIdRaw ? Number(supplierIdRaw) : null;
      if (!this.supplierId && signinData?.supplier?.id) {
        this.supplierId = Number(signinData.supplier.id);
      }

      console.log('🔍 [HEADER] userName:', this.userName);
      console.log('🔍 [HEADER] companyName:', this.companyName);
      console.log('🔍 [HEADER] buyerId:', this.buyerId);
      console.log('🔍 [HEADER] supplierId:', this.supplierId);
      console.log('🔍 [HEADER] role:', role);

    } catch (error) {
      console.error('Error loading user info:', error);
      this.setDefaultValues();
    }
  }

  // ============================================
  // ✅ FIXED: LOAD COMPANY LOGO — handles BOTH buyer and supplier
  // ============================================
  // private loadCompanyLogo(): void {

    
  //   // ✅ Try buyer logo first
  //   if (this.buyerId) {
  //     console.log('🖼️ [HEADER] Loading buyer logo for buyerId:', this.buyerId);
  //     this.dataService.getBuyerLogoBase64(this.buyerId).subscribe({
  //       next: (logoData: string | null) => {
  //         if (logoData && logoData.trim() && logoData.trim() !== 'null') {
  //           this.companyLogo = logoData;
  //           console.log('✅ [HEADER] Buyer logo loaded successfully');
  //         } else {
  //           this.companyLogo = null;
  //           console.log('⚠️ [HEADER] No buyer logo — showing text:', this.companyName);
  //         }
  //       },
  //       error: (err: any) => {
  //         this.companyLogo = null;
  //         console.warn('⚠️ [HEADER] Buyer logo fetch error:', err?.status);
  //       }
  //     });
  //     return;
  //   }

  //   // ✅ Try supplier logo if no buyerId
  //   if (this.supplierId) {
  //     console.log('🖼️ [HEADER] Loading supplier logo for supplierId:', this.supplierId);
  //     this.dataService.getSupplierLogoBase64(this.supplierId).subscribe({
  //       next: (logoData: string | null) => {
  //         if (logoData && logoData.trim() && logoData.trim() !== 'null') {
  //           this.companyLogo = logoData;
  //           console.log('✅ [HEADER] Supplier logo loaded successfully');
  //         } else {
  //           this.companyLogo = null;
  //           console.log('⚠️ [HEADER] No supplier logo — showing text:', this.companyName);
  //         }
  //       },
  //       error: (err: any) => {
  //         this.companyLogo = null;
  //         console.warn('⚠️ [HEADER] Supplier logo fetch error:', err?.status);
  //       }
  //     });
  //     return;
  //   }

  //   // No ID at all — just show text badge
  //   console.log('⚠️ [HEADER] No buyerId or supplierId — showing companyName text badge');
  // }

private loadCompanyLogo(): void {
     const role   = localStorage.getItem('role') || '';
  const userId = localStorage.getItem('userId');

    // ✅ FAST PATH: use cached logo from login response (avoids extra API call)
    const cachedLogo = localStorage.getItem('logoBase64');
    if (cachedLogo && cachedLogo.trim() && cachedLogo !== 'null') {
      this.companyLogo = cachedLogo;
      console.log('✅ [HEADER] Logo loaded from cache for role:', role);
      return;
    }

    // ✅ SuperAdmin: fetch from API if not cached
    if (role === 'SUPER_ADMIN') {
      const userId = localStorage.getItem('userId');
      if (!userId) return;
      this.dataService.getSuperAdminLogoBase64(Number(userId)).subscribe({
        next: (logoData: string | null) => {
          if (logoData && logoData.trim() && logoData !== 'null') {
            this.companyLogo = logoData;
            localStorage.setItem('logoBase64', logoData); // cache it
            console.log('✅ [HEADER] SuperAdmin logo loaded from API');
          } else {
            this.companyLogo = null;
            console.log('⚠️ [HEADER] No SuperAdmin logo — showing text:', this.companyName);
          }
        },
        error: () => { this.companyLogo = null; }
      });
      return;
    }

    // ✅ OrgAdmin: fetch from API if not cached
    if (role === 'ORGANIZATION_ADMIN' && userId) {
    console.log('🖼️ [HEADER] Fetching OrgAdmin logo for userId:', userId);
    this.dataService.getOrgAdminLogoBase64(Number(userId)).subscribe({
      next: (logoData: string | null) => {
        if (logoData && logoData.trim() && logoData !== 'null') {
          this.companyLogo = logoData;
          localStorage.setItem('logoBase64', logoData);
          console.log('✅ [HEADER] OrgAdmin logo loaded from API');
        } else {
          this.companyLogo = null;
          console.log('⚠️ [HEADER] No OrgAdmin logo — showing text:', this.companyName);
        }
      },
      error: (err: any) => {
        this.companyLogo = null;
        console.warn('⚠️ [HEADER] OrgAdmin logo fetch error:', err?.status);
      }
    });
    return;
  }

    // ✅ Buyer logo
    if (this.buyerId) {
      this.dataService.getBuyerLogoBase64(this.buyerId).subscribe({
        next: (logoData: string | null) => {
          this.companyLogo = (logoData && logoData.trim() && logoData !== 'null')
              ? logoData : null;
          console.log(this.companyLogo
              ? '✅ [HEADER] Buyer logo loaded'
              : '⚠️ [HEADER] No buyer logo — showing text: ' + this.companyName);
        },
        error: () => { this.companyLogo = null; }
      });
      return;
    }

    // ✅ Supplier logo
    if (this.supplierId) {
      this.dataService.getSupplierLogoBase64(this.supplierId).subscribe({
        next: (logoData: string | null) => {
          this.companyLogo = (logoData && logoData.trim() && logoData !== 'null')
              ? logoData : null;
          console.log(this.companyLogo
              ? '✅ [HEADER] Supplier logo loaded'
              : '⚠️ [HEADER] No supplier logo — showing text: ' + this.companyName);
        },
        error: () => { this.companyLogo = null; }
      });
      return;
    }

    console.log('⚠️ [HEADER] No ID found — showing companyName text badge');
  }



  // ============================================
  // HELPERS
  // ============================================
  private getDepartmentName(signinData: any): string {
    if (!signinData) return localStorage.getItem('departmentName') || '';
    if (signinData.department) {
      if (typeof signinData.department === 'string') return signinData.department;
      if (signinData.department.departmentName) return signinData.department.departmentName;
      if (signinData.department.name) return signinData.department.name;
    }
    return localStorage.getItem('departmentName') || '';
  }

  private getRoleDisplayName(role: string): string {
    const roleMap: { [key: string]: string } = {
      SUPER_ADMIN: 'Super Admin',
      ORGANIZATION_ADMIN: 'Organization Admin',
      CEO: 'CEO',
      COO: 'COO',
      MANAGER: 'Manager',
      PROCUREMENT: 'Procurement Manager',
      FINANCE: 'Finance Manager',
      ADMIN: 'Administrator',
      ROLE_BUYER: 'RFQ Creator',
      ROLE_SUPPLIER: 'Supplier'
    };
    return roleMap[role] || role || 'User';
  }

  private setDefaultValues(): void {
    this.userName = 'Guest';
    this.designation = 'N/A';
    this.designationDisplay = 'N/A';
    this.companyName = '';
    this.departmentName = '';
    this.companyLogo = null;
    this.buyerId = null;
    this.supplierId = null;
  }

  onExtendSession(): void {
    this.showSessionDialog = false;
    this.authService.extendSession();
  }

 // Add this property alongside showSessionDialog
showLogoutConfirm = false;

// Rename the old logout() trigger to this:
logout(): void {
  this.showLogoutConfirm = true;
}

// The actual logout logic moves here:
performLogout(): void {
  this.showLogoutConfirm = false;
  this.showSessionDialog = false;
  this.authService.logout();
}

  public newMessages: any[] = [];
  public newNotifications: any[] = [];
  public newStatus: any[] = [];
  public newTasks: any[] = [];
}