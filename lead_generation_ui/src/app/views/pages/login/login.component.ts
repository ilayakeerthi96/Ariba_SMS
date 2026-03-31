

// import { Component, inject } from '@angular/core';
// import { DataService } from '../../../shared/service/DataService';
// import { Router } from '@angular/router';
// import { IconDirective } from '@coreui/icons-angular';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardGroupComponent,
//   TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent,
//   InputGroupTextDirective, FormControlDirective, ButtonDirective
// } from '@coreui/angular';
// import { FormControl, FormGroup, Validators } from '@angular/forms';
// import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';
// import { AlertModule } from '@coreui/angular';
// import { AuthService } from '../../../shared/service/AuthService';
// import { User } from '../../../shared/user/User';
// import { MessageService } from '../../../shared/service/message.service';
// import { forkJoin, of } from 'rxjs';
// import { catchError } from 'rxjs/operators';

// @Component({
//   selector: 'app-login',
//   templateUrl: './login.component.html',
//   styleUrls: ['./login.component.scss'],
//   standalone: true,
//   imports: [
//     CommonModule, ContainerComponent, RowComponent, ColComponent,
//     CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent,
//     InputGroupComponent, InputGroupTextDirective, IconDirective,
//     FormControlDirective, ButtonDirective, FormsModule, ReactiveFormsModule, AlertModule
//   ]
// })
// export class LoginComponent {

//   loginForm!: FormGroup;
//   errorMessage: string | null = null;
//   successMessage: string | null = null;
//   isLoading: boolean = false;
//   submitted = false;

//   dataService = inject(DataService);
//   router = inject(Router);

//   constructor(
//     private authService: AuthService,
//     private messageService: MessageService
//   ) {
//     this.initializeForm();
//   }

//   initializeForm() {
//     this.loginForm = new FormGroup({
//       email: new FormControl('', [Validators.required, Validators.email]),
//       password: new FormControl('', [Validators.required])
//     });
//   }

//   get loginF() {
//     return this.loginForm.controls;
//   }

//   unifiedLogin() {
//     this.isLoading = true;
//     this.submitted = true;
//     this.errorMessage = null;
//     this.successMessage = null;

//     if (this.loginForm.valid) {
//       const loginRequest = {
//         email: this.loginForm.get('email')?.value,
//         password: this.loginForm.get('password')?.value
//       };

//       console.log('🔵 UNIFIED LOGIN ATTEMPT for:', loginRequest.email);

//       forkJoin({
//         superAdmin: this.dataService.superAdminLogin(loginRequest).pipe(
//           catchError(err => of({ success: false, error: err, type: 'superadmin' }))
//         ),
//         orgAdmin: this.dataService.organizationAdminLogin(loginRequest).pipe(
//           catchError(err => of({ success: false, error: err, type: 'orgadmin' }))
//         ),
//         hierarchy: this.dataService.hierarchyLogin(loginRequest).pipe(
//           catchError(err => of({ success: false, error: err, type: 'hierarchy' }))
//         ),
//         buyer: this.dataService.buyerLogin(loginRequest).pipe(
//           catchError(err => of({ success: false, error: err, type: 'buyer' }))
//         ),
//         supplier: this.dataService.supplierLogin(loginRequest).pipe(
//           catchError(err => of({ success: false, error: err, type: 'supplier' }))
//         )
//       }).subscribe({
//         next: (results: any) => {
//           console.log('📊 Login Results:', results);

//           if (results.superAdmin?.success === true && results.superAdmin?.data?.token) {
//             this.handleSuperAdminLogin(results.superAdmin, loginRequest);
//           } else if (results.orgAdmin?.success === true && results.orgAdmin?.data?.token) {
//             this.handleOrgAdminLogin(results.orgAdmin, loginRequest);
//           } else if (results.hierarchy?.success === true && results.hierarchy?.data?.token) {
//             this.handleHierarchyLogin(results.hierarchy, loginRequest);
//           } else if (results.buyer?.success === true && results.buyer?.token) {
//             this.handleBuyerLogin(results.buyer, loginRequest);
//           } else if (results.supplier?.success === true && results.supplier?.token) {
//             this.handleSupplierLogin(results.supplier, loginRequest);
//           } else {
//             this.isLoading = false;
//             this.errorMessage = 'Invalid email or password. Please try again.';
//             console.error('❌ All login attempts failed');
//           }
//         },
//         error: (error: any) => {
//           this.isLoading = false;
//           console.error('❌ Login error:', error);
//           this.errorMessage = 'An error occurred. Please try again.';
//         }
//       });
//     } else {
//       this.isLoading = false;
//       this.loginForm.markAllAsTouched();
//     }
//   }

//   // ============================================
//   // SUPERADMIN LOGIN
//   // ============================================
//   private handleSuperAdminLogin(response: any, loginRequest: any) {
//     console.log('✅ SUPERADMIN LOGIN SUCCESS');
//     this.isLoading = false;
//     const userData = response.data;

//    localStorage.setItem('token', userData.token);
//     const now = new Date().getTime();
//     localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
//     localStorage.setItem('userId', userData.id?.toString() || '');
//     localStorage.setItem('email', userData.email || '');
//     localStorage.setItem('fullName', userData.fullName || '');
//     localStorage.setItem('phone', userData.phone || '');
//     localStorage.setItem('companyName', userData.companyName || '');
//     localStorage.setItem('role', 'SUPER_ADMIN');
//     localStorage.setItem('userType', 'SUPER_ADMIN');
//     localStorage.setItem('loginStatus', 'true');

//     // ✅ ADD THIS ONE LINE — save superadmin logo URL at login time
//     localStorage.setItem('superAdminLogoUrl', userData.logoUrl || '');


//     const currentUser: User = {
//       id: userData.id || 0,
//       username: userData.email || loginRequest.email,
//       roles: ['SUPER_ADMIN'],
//       department: []
//     };

//     const signinData = {
//       userId: userData.id,
//       email: userData.email,
//       fullName: userData.fullName,
//       companyName: userData.companyName,
//       role: 'SUPER_ADMIN',
//       userType: 'SUPER_ADMIN',
//       isActive: userData.isActive,
//       userRoleAccess: [{ "userRoleAccessId": 0, "userRoles": "SUPER_ADMIN", "pageAccess": "ALL", "createdDate": null, "accessRead": true, "accessEdit": true, "accessDelete": true }]
//     };

//     localStorage.setItem('signinData', JSON.stringify(signinData));
//     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);
//     this.router.navigate(['/superadmin-dashboard']);
//   }

//   // ============================================
//   // ORG ADMIN LOGIN
//   // ============================================
//   private handleOrgAdminLogin(response: any, loginRequest: any) {
//     console.log('✅ ORGANIZATION ADMIN LOGIN SUCCESS');
//     this.isLoading = false;
//     const userData = response.data;

//     localStorage.setItem('token', userData.token);
//     const now = new Date().getTime();
//     localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
//     localStorage.setItem('userId', userData.id?.toString() || '');
//     localStorage.setItem('email', userData.email || '');
//     localStorage.setItem('fullName', userData.fullName || '');
//     localStorage.setItem('phone', userData.phone || '');
//     localStorage.setItem('companyName', userData.companyName || '');
//     localStorage.setItem('role', 'ORGANIZATION_ADMIN');
//     localStorage.setItem('userType', 'ORGANIZATION_ADMIN');
//     localStorage.setItem('loginStatus', 'true');
//     localStorage.setItem('mustChangePassword', userData.mustChangePassword?.toString() || 'false');

//     const currentUser: User = {
//       id: userData.id || 0,
//       username: userData.email || loginRequest.email,
//       roles: ['ORGANIZATION_ADMIN'],
//       department: []
//     };

//     const signinData = {
//       userId: userData.id,
//       email: userData.email,
//       fullName: userData.fullName,
//       companyName: userData.companyName,
//       role: 'ORGANIZATION_ADMIN',
//       userType: 'ORGANIZATION_ADMIN',
//       isActive: userData.isActive,
//       mustChangePassword: userData.mustChangePassword,
//       userRoleAccess: [{ "userRoleAccessId": 0, "userRoles": "ORGANIZATION_ADMIN", "pageAccess": "ALL", "createdDate": null, "accessRead": true, "accessEdit": true, "accessDelete": true }]
//     };

//     localStorage.setItem('signinData', JSON.stringify(signinData));
//     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);

//     // if (userData.mustChangePassword === true) {
//     //   this.messageService.showMessage('warning', 'Password Change Required', 'You must change your password on first login.');
//     //   setTimeout(() => this.router.navigate(['/change-password']), 500);
//     // } else {
//     //   this.router.navigate(['/orgadmin-dashboard']);
//     // }
//     // ✅ ALWAYS go to dashboard (password change disabled)
// console.log('✅ Redirecting to OrgAdmin Dashboard');
// this.router.navigate(['/orgadmin-dashboard']);
//   }

//   // ============================================
//   // HIERARCHY LOGIN
//   // ============================================
//   private handleHierarchyLogin(response: any, loginRequest: any) {
//     console.log('✅ HIERARCHY LOGIN SUCCESS');
//     this.isLoading = false;

//     let userData: any = response?.data || response;
//     let hierarchyLevelData: any = null;

//     if (userData.hierarchyLevel && userData.hierarchyLevel.id) {
//       hierarchyLevelData = userData.hierarchyLevel;
//     } else if (userData.hierarchyLevelId) {
//       hierarchyLevelData = {
//         id: userData.hierarchyLevelId,
//         levelName: userData.hierarchyLevelName || 'Unknown Level',
//         levelOrder: userData.hierarchyLevelOrder || 0,
//         companyName: userData.companyName || ''
//       };
//     } else {
//       this.messageService.showMessage('error', 'Login Failed', 'Account not assigned to any hierarchy level.');
//       this.isLoading = false;
//       return;
//     }

//     localStorage.setItem('token', userData.token);
//     const now = new Date().getTime();
//     localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
//     localStorage.setItem('userId', userData.id?.toString() || '');
//     localStorage.setItem('email', userData.email || '');
//     localStorage.setItem('fullName', userData.fullName || '');
//     localStorage.setItem('phone', userData.phone || '');
//     localStorage.setItem('role', userData.role || '');
//     localStorage.setItem('userType', userData.role || '');
//     localStorage.setItem('loginStatus', 'true');
//     localStorage.setItem('hierarchyLevelId', hierarchyLevelData.id?.toString() || '');
//     localStorage.setItem('hierarchyLevelName', hierarchyLevelData.levelName || '');
//     localStorage.setItem('hierarchyLevelOrder', hierarchyLevelData.levelOrder?.toString() || '');
//     localStorage.setItem('companyName', hierarchyLevelData.companyName || '');

//     const currentUser: User = {
//       id: userData.id || 0,
//       username: userData.email || loginRequest.email,
//       roles: [userData.role || 'ADMIN'],
//       department: []
//     };

//     const signinData = {
//       userId: userData.id,
//       email: userData.email,
//       fullName: userData.fullName,
//       role: userData.role,
//       userType: userData.role,
//       isActive: userData.isActive,
//       hierarchyLevel: hierarchyLevelData,
//       userRoleAccess: [{ "userRoleAccessId": 0, "userRoles": userData.role, "pageAccess": "ALL", "createdDate": null, "accessRead": true, "accessEdit": true, "accessDelete": true }]
//     };

//     localStorage.setItem('signinData', JSON.stringify(signinData));
//     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);
//     this.router.navigate(['/hierarchy-dashboard']);
//   }

//   // ============================================
//   // ✅ FIXED: BUYER LOGIN - stores buyerId + companyName correctly
//   // ============================================
//   private handleBuyerLogin(authResponse: any, loginRequest: any) {
//     console.log('✅ BUYER LOGIN SUCCESS');
//     console.log('%c[FULL RESPONSE]', 'color: #0066cc; font-weight: bold;', authResponse);
//     this.isLoading = false;

//     if (!authResponse.token) {
//       console.error('❌ No token in response');
//       this.errorMessage = 'Login failed: Invalid response from server';
//       return;
//     }

//     const now = new Date().getTime();
//     localStorage.setItem('token', authResponse.token);
//     localStorage.setItem('expirationTime', JSON.stringify(now + (60 * 60 * 1000)));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
//     localStorage.setItem('role', 'ROLE_BUYER');
//     localStorage.setItem('userType', 'ROLE_BUYER');
//     localStorage.setItem('loginStatus', 'true');

//     const userId = authResponse.userId || authResponse.id || 0;
//     localStorage.setItem('userId', userId.toString());

//     const email = authResponse.email || loginRequest.email;
//     localStorage.setItem('email', email);

//     // Build full name
//     let fullName = '';
//     if (authResponse.fullName?.trim()) {
//       fullName = authResponse.fullName.trim();
//     } else if (authResponse.firstName || authResponse.lastName) {
//       fullName = ((authResponse.firstName || '') + ' ' + (authResponse.lastName || '')).trim();
//     }
//     if (!fullName) fullName = email.split('@')[0];
//     localStorage.setItem('fullName', fullName);

//     // ✅ FIX 1: Store department
//     if (authResponse.department) {
//       localStorage.setItem('departmentId', authResponse.department.id?.toString() || '');
//       localStorage.setItem('departmentName', authResponse.department.name || '');
//       localStorage.setItem('department', JSON.stringify(authResponse.department));
//     }

//     // ✅ FIX 2: Store location
//     if (authResponse.location) {
//       localStorage.setItem('locationId', authResponse.location.id?.toString() || '');
//       localStorage.setItem('city', authResponse.location.city || '');
//       localStorage.setItem('state', authResponse.location.state || '');
//       localStorage.setItem('postalCode', authResponse.location.postalCode || '');
//       localStorage.setItem('country', authResponse.location.country || '');
//       localStorage.setItem('location', JSON.stringify(authResponse.location));
//     }

//     // ✅ FIX 3: Store buyer — buyerId AND companyName (buyer.name is the company name)
//     if (authResponse.buyer) {
//       const buyerId = authResponse.buyer.id?.toString() || '';
//       const companyName = authResponse.buyer.name || '';   // ← LoginResponse.BuyerDetails uses "name"

//       localStorage.setItem('buyerId', buyerId);
//       localStorage.setItem('buyerName', companyName);
//       localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
//       localStorage.setItem('companyName', companyName);   // ✅ CRITICAL: used by header for text badge
//       localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));

//       console.log('✅ Stored buyerId:', buyerId);
//       console.log('✅ Stored companyName:', companyName);
//     } else {
//       // ✅ FIX 4: buyer is null in response — clear these so header shows gracefully
//       console.warn('⚠️ No buyer in login response — logo cannot be loaded');
//       localStorage.removeItem('buyerId');
//       localStorage.setItem('companyName', '');
//     }

//     const currentUser: User = {
//       id: userId,
//       username: email,
//       roles: ['ROLE_BUYER'],
//       department: authResponse.department?.id ? [authResponse.department.id] : []
//     };

//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);

//     localStorage.setItem('signinData', JSON.stringify(authResponse));
//     localStorage.setItem('userRole', JSON.stringify([{
//       "userRoleAccessId": 0, "userRoles": "ROLE_BUYER", "pageAccess": "ALL",
//       "createdDate": null, "accessRead": true, "accessEdit": true, "accessDelete": true
//     }]));

//     console.log('✅ BUYER LOGIN COMPLETE — Redirecting to RFQ Dashboard');
//     this.router.navigate(['/rfq-dashboard']);
//   }

//   // ============================================
//   // SUPPLIER LOGIN
//   // ============================================
//   private handleSupplierLogin(response: any, loginRequest: any) {
//     console.log('✅ SUPPLIER LOGIN SUCCESS');
//     this.isLoading = false;

//     if (!response.token) {
//       this.errorMessage = 'Login failed: Invalid response from server';
//       return;
//     }

//     const now = new Date().getTime();
//     localStorage.setItem('token', response.token);
//     localStorage.setItem('expirationTime', JSON.stringify(now + (60 * 60 * 1000)));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
//     localStorage.setItem('role', 'ROLE_SUPPLIER');
//     localStorage.setItem('userType', 'ROLE_SUPPLIER');
//     localStorage.setItem('loginStatus', 'true');

//     const userId = response.userId || response.id || 0;
//     localStorage.setItem('userId', userId.toString());

//     const email = response.email || loginRequest.email;
//     localStorage.setItem('email', email);

//     let fullName = response.fullName?.trim() || email.split('@')[0];
//     localStorage.setItem('fullName', fullName);

//    localStorage.setItem('phone', response.phone || response.supplier?.phone || '');

//     if (response.department) {
//       localStorage.setItem('departmentId', response.department.id?.toString() || '');
//       localStorage.setItem('departmentName', response.department.name || '');
//       localStorage.setItem('department', JSON.stringify(response.department));
//     }

//     if (response.location) {
//       localStorage.setItem('locationId', response.location.id?.toString() || '');
//       localStorage.setItem('locationName', response.location.locationName || '');
//       localStorage.setItem('city', response.location.city || '');
//       localStorage.setItem('state', response.location.state || '');
//       localStorage.setItem('postalCode', response.location.postalCode || '');
//       localStorage.setItem('country', response.location.country || '');
//       localStorage.setItem('location', JSON.stringify(response.location));
//     }

//     if (response.supplier) {
//       localStorage.setItem('supplierId', response.supplier.id?.toString() || '');
//       localStorage.setItem('supplierName', response.supplier.name || '');
//       localStorage.setItem('supplierEmail', response.supplier.email || '');
//       localStorage.setItem('supplierPhone', response.supplier.phone || '');
//       localStorage.setItem('companyName', response.supplier.name || '');
//       localStorage.setItem('supplier', JSON.stringify(response.supplier));
//     }

//     const currentUser: User = {
//       id: userId,
//       username: email,
//       roles: ['ROLE_SUPPLIER'],
//       department: response.department?.id ? [response.department.id] : []
//     };

//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);

//     localStorage.setItem('signinData', JSON.stringify(response));
//     localStorage.setItem('userRole', JSON.stringify([{
//       "userRoleAccessId": 0, "userRoles": "ROLE_SUPPLIER", "pageAccess": "ALL",
//       "createdDate": null, "accessRead": true, "accessEdit": true, "accessDelete": true
//     }]));

//     this.router.navigate(['/supplier-dashboard']);
//   }

  

//   clearError() {
//     this.errorMessage = null;
//     this.successMessage = null;
//   }
// }


import { Component, inject } from '@angular/core';
import { DataService } from '../../../shared/service/DataService';
import { Router } from '@angular/router';
import { IconDirective } from '@coreui/icons-angular';
import {
  ContainerComponent, RowComponent, ColComponent, CardGroupComponent,
  TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent,
  InputGroupTextDirective, FormControlDirective, ButtonDirective
} from '@coreui/angular';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AlertModule } from '@coreui/angular';
import { AuthService } from '../../../shared/service/AuthService';
import { User } from '../../../shared/user/User';
import { MessageService } from '../../../shared/service/message.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule, ContainerComponent, RowComponent, ColComponent,
    CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent,
    InputGroupComponent, InputGroupTextDirective, IconDirective,
    FormControlDirective, ButtonDirective, FormsModule, ReactiveFormsModule, AlertModule
  ]
})
export class LoginComponent {

  loginForm!: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isLoading: boolean = false;
  submitted = false;

  dataService = inject(DataService);
  router = inject(Router);

  constructor(
    private authService: AuthService,
    private messageService: MessageService
  ) {
    this.initializeForm();
  }

  initializeForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required])
    });
  }

  get loginF() {
    return this.loginForm.controls;
  }

  unifiedLogin() {
    this.isLoading = true;
    this.submitted = true;
    this.errorMessage = null;
    this.successMessage = null;

    if (this.loginForm.valid) {
      const loginRequest = {
        email: this.loginForm.get('email')?.value,
        password: this.loginForm.get('password')?.value
      };

      console.log('🔵 UNIFIED LOGIN ATTEMPT for:', loginRequest.email);

      forkJoin({
        superAdmin: this.dataService.superAdminLogin(loginRequest).pipe(
          catchError(err => of({ success: false, error: err, type: 'superadmin' }))
        ),
        orgAdmin: this.dataService.organizationAdminLogin(loginRequest).pipe(
          catchError(err => of({ success: false, error: err, type: 'orgadmin' }))
        ),
        hierarchy: this.dataService.hierarchyLogin(loginRequest).pipe(
          catchError(err => of({ success: false, error: err, type: 'hierarchy' }))
        ),
        buyer: this.dataService.buyerLogin(loginRequest).pipe(
          catchError(err => of({ success: false, error: err, type: 'buyer' }))
        ),
        supplier: this.dataService.supplierLogin(loginRequest).pipe(
          catchError(err => of({ success: false, error: err, type: 'supplier' }))
        )
      }).subscribe({
        next: (results: any) => {
          console.log('📊 Login Results:', results);

          if (results.superAdmin?.success === true && results.superAdmin?.data?.token) {
            this.handleSuperAdminLogin(results.superAdmin, loginRequest);
          } else if (results.orgAdmin?.success === true && results.orgAdmin?.data?.token) {
            this.handleOrgAdminLogin(results.orgAdmin, loginRequest);
          } else if (results.hierarchy?.success === true && results.hierarchy?.data?.token) {
            this.handleHierarchyLogin(results.hierarchy, loginRequest);
          } else if (results.buyer?.success === true && results.buyer?.token) {
            this.handleBuyerLogin(results.buyer, loginRequest);
          } else if (results.supplier?.success === true && results.supplier?.token) {
            this.handleSupplierLogin(results.supplier, loginRequest);
          } else {
            this.isLoading = false;
            this.errorMessage = 'Invalid email or password. Please try again.';
            console.error('❌ All login attempts failed');
          }
        },
        error: (error: any) => {
          this.isLoading = false;
          console.error('❌ Login error:', error);
          this.errorMessage = 'An error occurred. Please try again.';
        }
      });
    } else {
      this.isLoading = false;
      this.loginForm.markAllAsTouched();
    }
  }

  // ============================================
  // SUPERADMIN LOGIN
  // ============================================
  private handleSuperAdminLogin(response: any, loginRequest: any) {
    console.log('✅ SUPERADMIN LOGIN SUCCESS');
    this.isLoading = false;
    const userData = response.data;

    const now = new Date().getTime();
    localStorage.setItem('token', userData.token);
    localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('userId', userData.id?.toString() || '');
    localStorage.setItem('email', userData.email || '');
    localStorage.setItem('fullName', userData.fullName || '');
    localStorage.setItem('phone', userData.phone || '');
    localStorage.setItem('companyName', userData.companyName || '');
    localStorage.setItem('role', 'SUPER_ADMIN');
    localStorage.setItem('userType', 'SUPER_ADMIN');
    localStorage.setItem('loginStatus', 'true');
    localStorage.setItem('superAdminLogoUrl', userData.logoUrl || '');

    // ✅ Cache logoBase64 so header doesn't need extra API call
    if (userData.logoBase64 && userData.logoBase64 !== 'null') {
      localStorage.setItem('logoBase64', userData.logoBase64);
    } else {
      localStorage.removeItem('logoBase64');
    }

    const currentUser: User = {
      id: userData.id || 0,
      username: userData.email || loginRequest.email,
      roles: ['SUPER_ADMIN'],
      department: []
    };

    const signinData = {
      userId: userData.id,
      email: userData.email,
      fullName: userData.fullName,
      companyName: userData.companyName,
      role: 'SUPER_ADMIN',
      userType: 'SUPER_ADMIN',
      isActive: userData.isActive,
      userRoleAccess: [{
        userRoleAccessId: 0, userRoles: 'SUPER_ADMIN', pageAccess: 'ALL',
        createdDate: null, accessRead: true, accessEdit: true, accessDelete: true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);
    this.router.navigate(['/superadmin-dashboard']);
  }

  // ============================================
  // ORG ADMIN LOGIN
  // ============================================
  private handleOrgAdminLogin(response: any, loginRequest: any) {
    console.log('✅ ORGANIZATION ADMIN LOGIN SUCCESS');
    this.isLoading = false;
    const userData = response.data;

    const now = new Date().getTime();
    localStorage.setItem('token', userData.token);
    localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('userId', userData.id?.toString() || '');
    localStorage.setItem('email', userData.email || '');
    localStorage.setItem('fullName', userData.fullName || '');
    localStorage.setItem('phone', userData.phone || '');
    localStorage.setItem('companyName', userData.companyName || '');
    localStorage.setItem('role', 'ORGANIZATION_ADMIN');
    localStorage.setItem('userType', 'ORGANIZATION_ADMIN');
    localStorage.setItem('loginStatus', 'true');
    localStorage.setItem('mustChangePassword', userData.mustChangePassword?.toString() || 'false');

    // ✅ Cache logoBase64 so header doesn't need extra API call
    if (userData.logoBase64 && userData.logoBase64 !== 'null') {
      localStorage.setItem('logoBase64', userData.logoBase64);
    } else {
      localStorage.removeItem('logoBase64');
    }

    const currentUser: User = {
      id: userData.id || 0,
      username: userData.email || loginRequest.email,
      roles: ['ORGANIZATION_ADMIN'],
      department: []
    };

    const signinData = {
      userId: userData.id,
      email: userData.email,
      fullName: userData.fullName,
      companyName: userData.companyName,
      role: 'ORGANIZATION_ADMIN',
      userType: 'ORGANIZATION_ADMIN',
      isActive: userData.isActive,
      mustChangePassword: userData.mustChangePassword,
      userRoleAccess: [{
        userRoleAccessId: 0, userRoles: 'ORGANIZATION_ADMIN', pageAccess: 'ALL',
        createdDate: null, accessRead: true, accessEdit: true, accessDelete: true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);
    console.log('✅ Redirecting to OrgAdmin Dashboard');
    this.router.navigate(['/orgadmin-dashboard']);
  }

  // ============================================
  // HIERARCHY LOGIN — unchanged
  // ============================================
  private handleHierarchyLogin(response: any, loginRequest: any) {
    console.log('✅ HIERARCHY LOGIN SUCCESS');
    this.isLoading = false;

    let userData: any = response?.data || response;
    let hierarchyLevelData: any = null;

    if (userData.hierarchyLevel && userData.hierarchyLevel.id) {
      hierarchyLevelData = userData.hierarchyLevel;
    } else if (userData.hierarchyLevelId) {
      hierarchyLevelData = {
        id: userData.hierarchyLevelId,
        levelName: userData.hierarchyLevelName || 'Unknown Level',
        levelOrder: userData.hierarchyLevelOrder || 0,
        companyName: userData.companyName || ''
      };
    } else {
      this.messageService.showMessage('error', 'Login Failed',
          'Account not assigned to any hierarchy level.');
      this.isLoading = false;
      return;
    }

    const now = new Date().getTime();
    localStorage.setItem('token', userData.token);
    localStorage.setItem('expirationTime', JSON.stringify(now + (24 * 60 * 60 * 1000)));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('userId', userData.id?.toString() || '');
    localStorage.setItem('email', userData.email || '');
    localStorage.setItem('fullName', userData.fullName || '');
    localStorage.setItem('phone', userData.phone || '');
    localStorage.setItem('role', userData.role || '');
    localStorage.setItem('userType', userData.role || '');
    localStorage.setItem('loginStatus', 'true');
    localStorage.setItem('hierarchyLevelId', hierarchyLevelData.id?.toString() || '');
    localStorage.setItem('hierarchyLevelName', hierarchyLevelData.levelName || '');
    localStorage.setItem('hierarchyLevelOrder', hierarchyLevelData.levelOrder?.toString() || '');
    localStorage.setItem('companyName', hierarchyLevelData.companyName || '');
    localStorage.removeItem('logoBase64');

    const currentUser: User = {
      id: userData.id || 0,
      username: userData.email || loginRequest.email,
      roles: [userData.role || 'ADMIN'],
      department: []
    };

    const signinData = {
      userId: userData.id,
      email: userData.email,
      fullName: userData.fullName,
      role: userData.role,
      userType: userData.role,
      isActive: userData.isActive,
      hierarchyLevel: hierarchyLevelData,
      userRoleAccess: [{
        userRoleAccessId: 0, userRoles: userData.role, pageAccess: 'ALL',
        createdDate: null, accessRead: true, accessEdit: true, accessDelete: true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));
    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);
    this.router.navigate(['/hierarchy-dashboard']);
  }

  // ============================================
  // BUYER LOGIN — unchanged
  // ============================================
  private handleBuyerLogin(authResponse: any, loginRequest: any) {
    console.log('✅ BUYER LOGIN SUCCESS');
    this.isLoading = false;

    if (!authResponse.token) {
      this.errorMessage = 'Login failed: Invalid response from server';
      return;
    }

    const now = new Date().getTime();
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('expirationTime', JSON.stringify(now + (60 * 60 * 1000)));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('role', 'ROLE_BUYER');
    localStorage.setItem('userType', 'ROLE_BUYER');
    localStorage.setItem('loginStatus', 'true');
    localStorage.removeItem('logoBase64');

    const userId = authResponse.userId || authResponse.id || 0;
    localStorage.setItem('userId', userId.toString());

    const email = authResponse.email || loginRequest.email;
    localStorage.setItem('email', email);

    let fullName = '';
    if (authResponse.fullName?.trim()) {
      fullName = authResponse.fullName.trim();
    } else if (authResponse.firstName || authResponse.lastName) {
      fullName = ((authResponse.firstName || '') + ' ' + (authResponse.lastName || '')).trim();
    }
    if (!fullName) fullName = email.split('@')[0];
    localStorage.setItem('fullName', fullName);

    if (authResponse.department) {
      localStorage.setItem('departmentId', authResponse.department.id?.toString() || '');
      localStorage.setItem('departmentName', authResponse.department.name || '');
      localStorage.setItem('department', JSON.stringify(authResponse.department));
    }

    if (authResponse.location) {
      localStorage.setItem('locationId', authResponse.location.id?.toString() || '');
      localStorage.setItem('city', authResponse.location.city || '');
      localStorage.setItem('state', authResponse.location.state || '');
      localStorage.setItem('postalCode', authResponse.location.postalCode || '');
      localStorage.setItem('country', authResponse.location.country || '');
      localStorage.setItem('location', JSON.stringify(authResponse.location));
    }

    if (authResponse.buyer) {
      const buyerId = authResponse.buyer.id?.toString() || '';
      const companyName = authResponse.buyer.name || '';
      localStorage.setItem('buyerId', buyerId);
      localStorage.setItem('buyerName', companyName);
      localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
      localStorage.setItem('companyName', companyName);
      localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));
      console.log('✅ Stored buyerId:', buyerId, '| companyName:', companyName);
    } else {
      console.warn('⚠️ No buyer in login response');
      localStorage.removeItem('buyerId');
      localStorage.setItem('companyName', '');
    }

    const currentUser: User = {
      id: userId,
      username: email,
      roles: ['ROLE_BUYER'],
      department: authResponse.department?.id ? [authResponse.department.id] : []
    };

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);
    localStorage.setItem('signinData', JSON.stringify(authResponse));
    localStorage.setItem('userRole', JSON.stringify([{
      userRoleAccessId: 0, userRoles: 'ROLE_BUYER', pageAccess: 'ALL',
      createdDate: null, accessRead: true, accessEdit: true, accessDelete: true
    }]));

    console.log('✅ BUYER LOGIN COMPLETE — Redirecting to RFQ Dashboard');
    this.router.navigate(['/rfq-dashboard']);
  }

  // ============================================
  // SUPPLIER LOGIN — unchanged
  // ============================================
  private handleSupplierLogin(response: any, loginRequest: any) {
    console.log('✅ SUPPLIER LOGIN SUCCESS');
    this.isLoading = false;

    if (!response.token) {
      this.errorMessage = 'Login failed: Invalid response from server';
      return;
    }

    const now = new Date().getTime();
    localStorage.setItem('token', response.token);
    localStorage.setItem('expirationTime', JSON.stringify(now + (60 * 60 * 1000)));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('role', 'ROLE_SUPPLIER');
    localStorage.setItem('userType', 'ROLE_SUPPLIER');
    localStorage.setItem('loginStatus', 'true');
    localStorage.removeItem('logoBase64');

    const userId = response.userId || response.id || 0;
    localStorage.setItem('userId', userId.toString());

    const email = response.email || loginRequest.email;
    localStorage.setItem('email', email);

    let fullName = response.fullName?.trim() || email.split('@')[0];
    localStorage.setItem('fullName', fullName);
    localStorage.setItem('phone', response.phone || response.supplier?.phone || '');

    if (response.department) {
      localStorage.setItem('departmentId', response.department.id?.toString() || '');
      localStorage.setItem('departmentName', response.department.name || '');
      localStorage.setItem('department', JSON.stringify(response.department));
    }

    if (response.location) {
      localStorage.setItem('locationId', response.location.id?.toString() || '');
      localStorage.setItem('locationName', response.location.locationName || '');
      localStorage.setItem('city', response.location.city || '');
      localStorage.setItem('state', response.location.state || '');
      localStorage.setItem('postalCode', response.location.postalCode || '');
      localStorage.setItem('country', response.location.country || '');
      localStorage.setItem('location', JSON.stringify(response.location));
    }

    if (response.supplier) {
      localStorage.setItem('supplierId', response.supplier.id?.toString() || '');
      localStorage.setItem('supplierName', response.supplier.name || '');
      localStorage.setItem('supplierEmail', response.supplier.email || '');
      localStorage.setItem('supplierPhone', response.supplier.phone || '');
      localStorage.setItem('companyName', response.supplier.name || '');
      localStorage.setItem('supplier', JSON.stringify(response.supplier));
    }

    const currentUser: User = {
      id: userId,
      username: email,
      roles: ['ROLE_SUPPLIER'],
      department: response.department?.id ? [response.department.id] : []
    };

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);
    localStorage.setItem('signinData', JSON.stringify(response));
    localStorage.setItem('userRole', JSON.stringify([{
      userRoleAccessId: 0, userRoles: 'ROLE_SUPPLIER', pageAccess: 'ALL',
      createdDate: null, accessRead: true, accessEdit: true, accessDelete: true
    }]));

    this.router.navigate(['/supplier-dashboard']);
  }

  clearError() {
    this.errorMessage = null;
    this.successMessage = null;
  }
}