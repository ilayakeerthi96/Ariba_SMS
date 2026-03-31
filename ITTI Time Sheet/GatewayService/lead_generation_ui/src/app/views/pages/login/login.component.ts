

// // import { Component, inject } from '@angular/core';
// // import { DataService } from '../../../shared/service/DataService';
// // import { Router } from '@angular/router';
// // import { IconDirective } from '@coreui/icons-angular';
// // import { ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, FormControlDirective, ButtonDirective } from '@coreui/angular';
// // import { FormControl, FormGroup, Validators } from '@angular/forms';
// // import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// // import { CommonModule } from '@angular/common';
// // import { AlertModule } from '@coreui/angular';
// // import { AuthService } from '../../../shared/service/AuthService';
// // import { User } from '../../../shared/user/User';
// // import { MessageService } from '../../../shared/service/message.service';
// // import { forkJoin, of } from 'rxjs';
// // import { catchError } from 'rxjs/operators';

// // @Component({
// //   selector: 'app-login',
// //   templateUrl: './login.component.html',
// //   styleUrls: ['./login.component.scss'],
// //   standalone: true,
// //   imports: [CommonModule, ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, IconDirective, FormControlDirective, ButtonDirective, FormsModule, ReactiveFormsModule, AlertModule]
// // })
// // export class LoginComponent {

// //   // Single Form Group
// //   loginForm!: FormGroup;

// //   // UI State
// //   errorMessage: string | null = null;
// //   successMessage: string | null = null;
// //   isLoading: boolean = false;
// //   submitted = false;

// //   dataService = inject(DataService);
// //   router = inject(Router);

// //   constructor(
// //     private authService: AuthService,
// //     private messageService: MessageService
// //   ) {
// //     this.initializeForm();
// //   }

// //   // ============================================
// //   // INITIALIZE SINGLE FORM
// //   // ============================================
// //   initializeForm() {
// //     this.loginForm = new FormGroup({
// //       email: new FormControl('', [Validators.required, Validators.email]),
// //       password: new FormControl('', [Validators.required])
// //     });
// //   }

// //   // ============================================
// //   // FORM GETTER
// //   // ============================================
// //   get loginF() {
// //     return this.loginForm.controls;
// //   }

// //   // ============================================
// //   // UNIFIED LOGIN - TRIES ALL ENDPOINTS
// //   // ============================================
// //   unifiedLogin() {
// //     this.isLoading = true;
// //     this.submitted = true;
// //     this.errorMessage = null;
// //     this.successMessage = null;

// //     if (this.loginForm.valid) {
// //       const loginRequest = {
// //         email: this.loginForm.get('email')?.value,
// //         password: this.loginForm.get('password')?.value
// //       };

// //       console.log('🔵 UNIFIED LOGIN ATTEMPT for:', loginRequest.email);

// //       // Try all login endpoints in parallel
// //       forkJoin({
// //         superAdmin: this.dataService.superAdminLogin(loginRequest).pipe(
// //           catchError(err => of({ success: false, error: err, type: 'superadmin' }))
// //         ),
// //         orgAdmin: this.dataService.organizationAdminLogin(loginRequest).pipe(
// //           catchError(err => of({ success: false, error: err, type: 'orgadmin' }))
// //         ),
// //         hierarchy: this.dataService.hierarchyLogin(loginRequest).pipe(
// //           catchError(err => of({ success: false, error: err, type: 'hierarchy' }))
// //         ),
// //         buyer: this.dataService.buyerLogin(loginRequest).pipe(
// //           catchError(err => of({ success: false, error: err, type: 'buyer' }))
// //         )
// //       }).subscribe({
// //         next: (results: any) => {
// //           console.log('📊 Login Results:', results);

// //           // Check which login succeeded
// //           if (results.superAdmin?.success === true && results.superAdmin?.data?.token) {
// //             this.handleSuperAdminLogin(results.superAdmin, loginRequest);
// //           } 
// //           else if (results.orgAdmin?.success === true && results.orgAdmin?.data?.token) {
// //             this.handleOrgAdminLogin(results.orgAdmin, loginRequest);
// //           } 
// //           else if (results.hierarchy?.success === true && results.hierarchy?.data?.token) {
// //             this.handleHierarchyLogin(results.hierarchy, loginRequest);
// //           } 
// //           else if (results.buyer?.success === true && results.buyer?.token) {
// //             this.handleBuyerLogin(results.buyer, loginRequest);
// //           } 
// //           else {
// //             // All failed
// //             this.isLoading = false;
// //             this.errorMessage = 'Invalid email or password. Please try again.';
// //             console.error('❌ All login attempts failed');
// //           }
// //         },
// //         error: (error: any) => {
// //           this.isLoading = false;
// //           console.error('❌ Login error:', error);
// //           this.errorMessage = 'An error occurred. Please try again.';
// //         }
// //       });
// //     } else {
// //       this.isLoading = false;
// //       this.loginForm.markAllAsTouched();
// //     }
// //   }

// //   // ============================================
// //   // HANDLE SUPERADMIN LOGIN SUCCESS
// //   // ============================================
// //   private handleSuperAdminLogin(response: any, loginRequest: any) {
// //     console.log('✅ SUPERADMIN LOGIN SUCCESS');
// //     this.isLoading = false;

// //     const userData = response.data;

// //     localStorage.setItem('token', userData.token);
// //     const now = new Date().getTime();
// //     const expiryTime = now + (24 * 60 * 60 * 1000);
// //     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
// //     localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
// //     localStorage.setItem('userId', userData.id?.toString() || '');
// //     localStorage.setItem('email', userData.email || '');
// //     localStorage.setItem('fullName', userData.fullName || '');
// //     localStorage.setItem('phone', userData.phone || '');
// //     localStorage.setItem('companyName', userData.companyName || '');
// //     localStorage.setItem('role', 'SUPER_ADMIN');
// //     localStorage.setItem('userType', 'SUPER_ADMIN');
// //     localStorage.setItem('loginStatus', 'true');

// //     const currentUser: User = {
// //       id: userData.id || 0,
// //       username: userData.email || loginRequest.email,
// //       roles: ['SUPER_ADMIN'],
// //       department: []
// //     };

// //     const signinData = {
// //       userId: userData.id,
// //       email: userData.email,
// //       fullName: userData.fullName,
// //       companyName: userData.companyName,
// //       role: 'SUPER_ADMIN',
// //       userType: 'SUPER_ADMIN',
// //       isActive: userData.isActive,
// //       userRoleAccess: [{
// //         "userRoleAccessId": 0,
// //         "userRoles": "SUPER_ADMIN",
// //         "pageAccess": "ALL",
// //         "createdDate": null,
// //         "accessRead": true,
// //         "accessEdit": true,
// //         "accessDelete": true
// //       }]
// //     };

// //     localStorage.setItem('signinData', JSON.stringify(signinData));
// //     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

// //     this.authService.setUser(currentUser);
// //     this.authService.setLoginStatus(true);

// //     console.log('✅ Redirecting to SuperAdmin Dashboard');
// //     this.router.navigate(['/superadmin-dashboard']);
// //   }

// //   // ============================================
// //   // HANDLE ORGANIZATION ADMIN LOGIN SUCCESS
// //   // ============================================
// //   private handleOrgAdminLogin(response: any, loginRequest: any) {
// //     console.log('✅ ORGANIZATION ADMIN LOGIN SUCCESS');
// //     this.isLoading = false;

// //     const userData = response.data;

// //     localStorage.setItem('token', userData.token);
// //     const now = new Date().getTime();
// //     const expiryTime = now + (24 * 60 * 60 * 1000);
// //     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
// //     localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
// //     localStorage.setItem('userId', userData.id?.toString() || '');
// //     localStorage.setItem('email', userData.email || '');
// //     localStorage.setItem('fullName', userData.fullName || '');
// //     localStorage.setItem('phone', userData.phone || '');
// //     localStorage.setItem('companyName', userData.companyName || '');
// //     localStorage.setItem('role', 'ORGANIZATION_ADMIN');
// //     localStorage.setItem('userType', 'ORGANIZATION_ADMIN');
// //     localStorage.setItem('loginStatus', 'true');
// //     localStorage.setItem('mustChangePassword', userData.mustChangePassword?.toString() || 'false');

// //     const currentUser: User = {
// //       id: userData.id || 0,
// //       username: userData.email || loginRequest.email,
// //       roles: ['ORGANIZATION_ADMIN'],
// //       department: []
// //     };

// //     const signinData = {
// //       userId: userData.id,
// //       email: userData.email,
// //       fullName: userData.fullName,
// //       companyName: userData.companyName,
// //       role: 'ORGANIZATION_ADMIN',
// //       userType: 'ORGANIZATION_ADMIN',
// //       isActive: userData.isActive,
// //       mustChangePassword: userData.mustChangePassword,
// //       userRoleAccess: [{
// //         "userRoleAccessId": 0,
// //         "userRoles": "ORGANIZATION_ADMIN",
// //         "pageAccess": "ALL",
// //         "createdDate": null,
// //         "accessRead": true,
// //         "accessEdit": true,
// //         "accessDelete": true
// //       }]
// //     };

// //     localStorage.setItem('signinData', JSON.stringify(signinData));
// //     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

// //     this.authService.setUser(currentUser);
// //     this.authService.setLoginStatus(true);

// //     // Check if password change is required
// //     if (userData.mustChangePassword === true) {
// //       console.log('⚠️ Password change required');
// //       this.messageService.showMessage('warning', 'Password Change Required', 
// //         'You must change your password on first login for security.');
// //       setTimeout(() => {
// //         this.router.navigate(['/change-password']);
// //       }, 500);
// //     } else {
// //       console.log('✅ Redirecting to OrgAdmin Dashboard');
// //       this.router.navigate(['/orgadmin-dashboard']);
// //     }
// //   }

// //   // ============================================
// //   // HANDLE HIERARCHY LOGIN SUCCESS
// //   // ============================================
// //   private handleHierarchyLogin(response: any, loginRequest: any) {
// //     console.log('✅ HIERARCHY LOGIN SUCCESS');
// //     this.isLoading = false;

// //     const userData = response.data;

// //     localStorage.setItem('token', userData.token);
// //     const now = new Date().getTime();
// //     const expiryTime = now + (24 * 60 * 60 * 1000);
// //     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
// //     localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
// //     localStorage.setItem('userId', userData.id?.toString() || '');
// //     localStorage.setItem('email', userData.email || '');
// //     localStorage.setItem('fullName', userData.fullName || '');
// //     localStorage.setItem('phone', userData.phone || '');
// //     localStorage.setItem('role', userData.role || '');
// //     localStorage.setItem('userType', userData.role || '');
// //     localStorage.setItem('loginStatus', 'true');

// //     const currentUser: User = {
// //       id: userData.id || 0,
// //       username: userData.email || loginRequest.email,
// //       roles: [userData.role || 'ADMIN'],
// //       department: []
// //     };

// //     const signinData = {
// //       userId: userData.id,
// //       email: userData.email,
// //       fullName: userData.fullName,
// //       role: userData.role,
// //       userType: userData.role,
// //       isActive: userData.isActive,
// //       userRoleAccess: [{
// //         "userRoleAccessId": 0,
// //         "userRoles": userData.role,
// //         "pageAccess": "ALL",
// //         "createdDate": null,
// //         "accessRead": true,
// //         "accessEdit": true,
// //         "accessDelete": true
// //       }]
// //     };

// //     localStorage.setItem('signinData', JSON.stringify(signinData));
// //     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

// //     this.authService.setUser(currentUser);
// //     this.authService.setLoginStatus(true);

// //     console.log('✅ Redirecting to Dashboard');
// //     this.router.navigate(['/pending-approvals']);
// //   }

// //   // ============================================
// //   // HANDLE BUYER LOGIN SUCCESS
// //   // ============================================
// //   private handleBuyerLogin(authResponse: any, loginRequest: any) {
// //     console.log('✅ BUYER LOGIN SUCCESS');
// //     this.isLoading = false;

// //     const now = new Date().getTime();
// //     const oneHourMs = 60 * 60 * 1000;
// //     const expiryTime = now + oneHourMs;

// //     localStorage.setItem('token', authResponse.token || '');
// //     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
// //     localStorage.setItem('loginTimestamp', JSON.stringify(now));
// //     localStorage.setItem('role', 'ROLE_BUYER');
// //     localStorage.setItem('loginStatus', 'true');
// //     localStorage.setItem('userId', authResponse.userId?.toString() || '');
// //     localStorage.setItem('email', authResponse.email || loginRequest.email);
// //     localStorage.setItem('fullName', authResponse.fullName || '');

// //     if (authResponse.department) {
// //       localStorage.setItem('departmentId', authResponse.department.id?.toString() || '');
// //       localStorage.setItem('departmentName', authResponse.department.name || '');
// //       localStorage.setItem('department', JSON.stringify(authResponse.department));
// //     }

// //     if (authResponse.location) {
// //       localStorage.setItem('locationId', authResponse.location.id?.toString() || '');
// //       localStorage.setItem('city', authResponse.location.city || '');
// //       localStorage.setItem('state', authResponse.location.state || '');
// //       localStorage.setItem('postalCode', authResponse.location.postalCode || '');
// //       localStorage.setItem('country', authResponse.location.country || '');
// //       localStorage.setItem('location', JSON.stringify(authResponse.location));
// //     }

// //     if (authResponse.buyer) {
// //       localStorage.setItem('buyerId', authResponse.buyer.id?.toString() || '');
// //       localStorage.setItem('buyerName', authResponse.buyer.name || '');
// //       localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
// //       localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));
// //     }

// //     const currentUser: User = {
// //       id: authResponse.userId || 0,
// //       username: authResponse.email || loginRequest.email,
// //       roles: ['ROLE_BUYER'],
// //       department: [authResponse.department?.id || 0]
// //     };

// //     this.authService.setUser(currentUser);
// //     this.authService.setLoginStatus(true);
// //     localStorage.setItem('signinData', JSON.stringify(authResponse));

// //     console.log('✅ Redirecting to RFQ Dashboard');
// //     this.router.navigate(['/rfq-dashboard']);
// //   }

// //   // ============================================
// //   // UTILITY METHODS
// //   // ============================================
// //   clearError() {
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //   }
// // }


// import { Component, inject } from '@angular/core';
// import { DataService } from '../../../shared/service/DataService';
// import { Router } from '@angular/router';
// import { IconDirective } from '@coreui/icons-angular';
// import { ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, FormControlDirective, ButtonDirective } from '@coreui/angular';
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
//   imports: [CommonModule, ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, IconDirective, FormControlDirective, ButtonDirective, FormsModule, ReactiveFormsModule, AlertModule]
// })
// export class LoginComponent {

//   // Single Form Group
//   loginForm!: FormGroup;

//   // UI State
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

//   // ============================================
//   // INITIALIZE SINGLE FORM
//   // ============================================
//   initializeForm() {
//     this.loginForm = new FormGroup({
//       email: new FormControl('', [Validators.required, Validators.email]),
//       password: new FormControl('', [Validators.required])
//     });
//   }

//   // ============================================
//   // FORM GETTER
//   // ============================================
//   get loginF() {
//     return this.loginForm.controls;
//   }

//   // ============================================
//   // UNIFIED LOGIN - TRIES ALL ENDPOINTS
//   // ============================================
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

//       // Try all login endpoints in parallel
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
//         )
//       }).subscribe({
//         next: (results: any) => {
//           console.log('📊 Login Results:', results);

//           // Check which login succeeded
//           if (results.superAdmin?.success === true && results.superAdmin?.data?.token) {
//             this.handleSuperAdminLogin(results.superAdmin, loginRequest);
//           } 
//           else if (results.orgAdmin?.success === true && results.orgAdmin?.data?.token) {
//             this.handleOrgAdminLogin(results.orgAdmin, loginRequest);
//           } 
//           else if (results.hierarchy?.success === true && results.hierarchy?.data?.token) {
//             this.handleHierarchyLogin(results.hierarchy, loginRequest);
//           } 
//           else if (results.buyer?.success === true && results.buyer?.token) {
//             this.handleBuyerLogin(results.buyer, loginRequest);
//           } 
//           else {
//             // All failed
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
//   // HANDLE SUPERADMIN LOGIN SUCCESS
//   // ============================================
//   private handleSuperAdminLogin(response: any, loginRequest: any) {
//     console.log('✅ SUPERADMIN LOGIN SUCCESS');
//     this.isLoading = false;

//     const userData = response.data;

//     localStorage.setItem('token', userData.token);
//     const now = new Date().getTime();
//     const expiryTime = now + (24 * 60 * 60 * 1000);
//     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
//     localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
//     localStorage.setItem('userId', userData.id?.toString() || '');
//     localStorage.setItem('email', userData.email || '');
//     localStorage.setItem('fullName', userData.fullName || '');
//     localStorage.setItem('phone', userData.phone || '');
//     localStorage.setItem('companyName', userData.companyName || '');
//     localStorage.setItem('role', 'SUPER_ADMIN');
//     localStorage.setItem('userType', 'SUPER_ADMIN');
//     localStorage.setItem('loginStatus', 'true');

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
//       userRoleAccess: [{
//         "userRoleAccessId": 0,
//         "userRoles": "SUPER_ADMIN",
//         "pageAccess": "ALL",
//         "createdDate": null,
//         "accessRead": true,
//         "accessEdit": true,
//         "accessDelete": true
//       }]
//     };

//     localStorage.setItem('signinData', JSON.stringify(signinData));
//     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);

//     console.log('✅ Redirecting to SuperAdmin Dashboard');
//     this.router.navigate(['/superadmin-dashboard']);
//   }

//   // ============================================
//   // HANDLE ORGANIZATION ADMIN LOGIN SUCCESS
//   // ============================================
//   private handleOrgAdminLogin(response: any, loginRequest: any) {
//     console.log('✅ ORGANIZATION ADMIN LOGIN SUCCESS');
//     this.isLoading = false;

//     const userData = response.data;

//     localStorage.setItem('token', userData.token);
//     const now = new Date().getTime();
//     const expiryTime = now + (24 * 60 * 60 * 1000);
//     localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
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
//       userRoleAccess: [{
//         "userRoleAccessId": 0,
//         "userRoles": "ORGANIZATION_ADMIN",
//         "pageAccess": "ALL",
//         "createdDate": null,
//         "accessRead": true,
//         "accessEdit": true,
//         "accessDelete": true
//       }]
//     };

//     localStorage.setItem('signinData', JSON.stringify(signinData));
//     localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

//     this.authService.setUser(currentUser);
//     this.authService.setLoginStatus(true);

//     // Check if password change is required
//     if (userData.mustChangePassword === true) {
//       console.log('⚠️ Password change required');
//       this.messageService.showMessage('warning', 'Password Change Required', 
//         'You must change your password on first login for security.');
//       setTimeout(() => {
//         this.router.navigate(['/change-password']);
//       }, 500);
//     } else {
//       console.log('✅ Redirecting to OrgAdmin Dashboard');
//       this.router.navigate(['/orgadmin-dashboard']);
//     }
//   }

//   // ============================================
//   // ✅ FIXED: HANDLE HIERARCHY LOGIN SUCCESS
//   // ============================================
//  // UPDATE handleHierarchyLogin() method - ADD THIS VALIDATION

// // ==================== COMPLETE FIXED login.component.ts ====================
// // Replace the ENTIRE handleHierarchyLogin method with this:

// private handleHierarchyLogin(response: any, loginRequest: any) {
//   console.log('✅ HIERARCHY LOGIN SUCCESS');
//   console.log('%c[FULL LOGIN RESPONSE]', 'color: #0066cc; font-weight: bold;', response);
//   console.log('%c[RESPONSE TYPE]', 'color: #0066cc;', typeof response);
//   console.log('%c[RESPONSE KEYS]', 'color: #0066cc;', Object.keys(response || {}));
  
//   this.isLoading = false;

//   // ✅ CRITICAL FIX: Extract user data from response
//   let userData: any;
  
//   if (response?.data) {
//     userData = response.data;
//     console.log('%c[EXTRACTED FROM response.data]', 'color: #0066cc;', userData);
//   } else {
//     userData = response;
//     console.log('%c[USING DIRECT response]', 'color: #0066cc;', userData);
//   }

//   console.log('%c[USER DATA KEYS]', 'color: #0066cc;', Object.keys(userData || {}));
//   console.log('%c[CHECKING hierarchyLevel]', 'color: #0066cc;', userData.hierarchyLevel);

//   // ✅ CRITICAL: If hierarchyLevel is missing, check if user has hierarchyLevelId
//   let hierarchyLevelData: any = null;

//   if (userData.hierarchyLevel && userData.hierarchyLevel.id) {
//     // Best case: Full hierarchy level object exists
//     hierarchyLevelData = userData.hierarchyLevel;
//     console.log('%c[✓ FOUND FULL hierarchyLevel]', 'color: #00aa00;', hierarchyLevelData);
//   } 
//   else if (userData.hierarchyLevelId) {
//     // Fallback: Only ID exists, need to fetch level details
//     console.warn('%c[⚠️ ONLY hierarchyLevelId EXISTS]', 'color: #ff9800;', userData.hierarchyLevelId);
    
//     // Create minimal hierarchy level object
//     hierarchyLevelData = {
//       id: userData.hierarchyLevelId,
//       levelName: userData.hierarchyLevelName || 'Unknown Level',
//       levelOrder: userData.hierarchyLevelOrder || 0,
//       companyName: userData.companyName || ''
//     };
    
//     console.log('%c[CREATED MINIMAL hierarchyLevel]', 'color: #ff9800;', hierarchyLevelData);
//   }
//   else {
//     // No hierarchy level data at all
//     console.error('%c[❌ ERROR] No hierarchy level data found in response', 'color: #cc0000; font-weight: bold;');
//     console.error('%c[USER DATA]', 'color: #cc0000;', userData);
    
//     this.messageService.showMessage('error', 'Login Failed', 
//       'Your account is not assigned to any hierarchy level. Please contact your administrator to assign you to a hierarchy level first.');
//     this.isLoading = false;
//     return;
//   }

//   // Store basic user data
//   localStorage.setItem('token', userData.token);
//   const now = new Date().getTime();
//   const expiryTime = now + (24 * 60 * 60 * 1000);
//   localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
//   localStorage.setItem('loginTimestamp', JSON.stringify(now));
  
//   localStorage.setItem('userId', userData.id?.toString() || '');
//   localStorage.setItem('email', userData.email || '');
//   localStorage.setItem('fullName', userData.fullName || '');
//   localStorage.setItem('phone', userData.phone || '');
//   localStorage.setItem('role', userData.role || '');
//   localStorage.setItem('userType', userData.role || '');
//   localStorage.setItem('loginStatus', 'true');
  
//   // ✅ CRITICAL FIX: Store hierarchy level data
//   localStorage.setItem('hierarchyLevelId', hierarchyLevelData.id?.toString() || '');
//   localStorage.setItem('hierarchyLevelName', hierarchyLevelData.levelName || '');
//   localStorage.setItem('hierarchyLevelOrder', hierarchyLevelData.levelOrder?.toString() || '');
//   localStorage.setItem('companyName', hierarchyLevelData.companyName || '');
  
//   console.log('%c[✅ ALL DATA STORED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold;', {
//     userId: userData.id,
//     levelId: hierarchyLevelData.id,
//     levelName: hierarchyLevelData.levelName,
//     levelOrder: hierarchyLevelData.levelOrder,
//     companyName: hierarchyLevelData.companyName
//   });

//   const currentUser: User = {
//     id: userData.id || 0,
//     username: userData.email || loginRequest.email,
//     roles: [userData.role || 'ADMIN'],
//     department: []
//   };

//   const signinData = {
//     userId: userData.id,
//     email: userData.email,
//     fullName: userData.fullName,
//     role: userData.role,
//     userType: userData.role,
//     isActive: userData.isActive,
//     hierarchyLevel: hierarchyLevelData,
//     userRoleAccess: [{
//       "userRoleAccessId": 0,
//       "userRoles": userData.role,
//       "pageAccess": "ALL",
//       "createdDate": null,
//       "accessRead": true,
//       "accessEdit": true,
//       "accessDelete": true
//     }]
//   };

//   localStorage.setItem('signinData', JSON.stringify(signinData));
//   localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

//   this.authService.setUser(currentUser);
//   this.authService.setLoginStatus(true);

//   console.log('✅ Redirecting to Hierarchy Dashboard');
//   this.router.navigate(['/hierarchy-dashboard']);
// }

//   // ============================================
//   // HANDLE BUYER LOGIN SUCCESS
//   // ============================================
//   // private handleBuyerLogin(authResponse: any, loginRequest: any) {
//   //   console.log('✅ BUYER LOGIN SUCCESS');
//   //   this.isLoading = false;

//   //   const now = new Date().getTime();
//   //   const oneHourMs = 60 * 60 * 1000;
//   //   const expiryTime = now + oneHourMs;

//   //   localStorage.setItem('token', authResponse.token || '');
//   //   localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
//   //   localStorage.setItem('loginTimestamp', JSON.stringify(now));
//   //   localStorage.setItem('role', 'ROLE_BUYER');
//   //   localStorage.setItem('loginStatus', 'true');
//   //   localStorage.setItem('userId', authResponse.userId?.toString() || '');
//   //   localStorage.setItem('email', authResponse.email || loginRequest.email);
//   //   localStorage.setItem('fullName', authResponse.fullName || '');

//   //   if (authResponse.department) {
//   //     localStorage.setItem('departmentId', authResponse.department.id?.toString() || '');
//   //     localStorage.setItem('departmentName', authResponse.department.name || '');
//   //     localStorage.setItem('department', JSON.stringify(authResponse.department));
//   //   }

//   //   if (authResponse.location) {
//   //     localStorage.setItem('locationId', authResponse.location.id?.toString() || '');
//   //     localStorage.setItem('city', authResponse.location.city || '');
//   //     localStorage.setItem('state', authResponse.location.state || '');
//   //     localStorage.setItem('postalCode', authResponse.location.postalCode || '');
//   //     localStorage.setItem('country', authResponse.location.country || '');
//   //     localStorage.setItem('location', JSON.stringify(authResponse.location));
//   //   }

//   //   if (authResponse.buyer) {
//   //     localStorage.setItem('buyerId', authResponse.buyer.id?.toString() || '');
//   //     localStorage.setItem('buyerName', authResponse.buyer.name || '');
//   //     localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
//   //     localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));
//   //   }

//   //   const currentUser: User = {
//   //     id: authResponse.userId || 0,
//   //     username: authResponse.email || loginRequest.email,
//   //     roles: ['ROLE_BUYER'],
//   //     department: [authResponse.department?.id || 0]
//   //   };

//   //   this.authService.setUser(currentUser);
//   //   this.authService.setLoginStatus(true);
//   //   localStorage.setItem('signinData', JSON.stringify(authResponse));

//   //   console.log('✅ Redirecting to RFQ Dashboard');
//   //   this.router.navigate(['/rfq-dashboard']);
//   // }
//   // ============================================
// // HANDLE BUYER LOGIN SUCCESS - COMPLETELY FIXED
// // ============================================
// private handleBuyerLogin(authResponse: any, loginRequest: any) {
//   console.log('✅ BUYER LOGIN SUCCESS');
//   console.log('%c[FULL RESPONSE]', 'color: #0066cc; font-weight: bold;', authResponse);
  
//   this.isLoading = false;

//   // ✅ CRITICAL FIX: Check if token exists
//   if (!authResponse.token) {
//     console.error('❌ No token in response');
//     this.errorMessage = 'Login failed: Invalid response from server';
//     return;
//   }

//   // ✅ Store token and basic timing
//   const now = new Date().getTime();
//   const oneHourMs = 60 * 60 * 1000;
//   const expiryTime = now + oneHourMs;

//   localStorage.setItem('token', authResponse.token);
//   localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
//   localStorage.setItem('loginTimestamp', JSON.stringify(now));
//   localStorage.setItem('role', 'ROLE_BUYER');
//   localStorage.setItem('userType', 'ROLE_BUYER');
//   localStorage.setItem('loginStatus', 'true');

//   // ✅ Store user ID (CRITICAL)
//   const userId = authResponse.userId || authResponse.id || 0;
//   localStorage.setItem('userId', userId.toString());
//   console.log('✅ Stored userId:', userId);

//   // ✅ Store email
//   const email = authResponse.email || loginRequest.email;
//   localStorage.setItem('email', email);
//   console.log('✅ Stored email:', email);

//   // ✅ CRITICAL FIX: Build full name safely
//   let fullName = '';
  
//   if (authResponse.fullName && authResponse.fullName.trim()) {
//     fullName = authResponse.fullName.trim();
//   } else if (authResponse.firstName || authResponse.lastName) {
//     const first = (authResponse.firstName || '').trim();
//     const last = (authResponse.lastName || '').trim();
//     fullName = (first + ' ' + last).trim();
//   }
  
//   // Fallback to email prefix if no name
//   if (!fullName) {
//     fullName = email.split('@')[0];
//   }
  
//   localStorage.setItem('fullName', fullName);
//   console.log('✅ Stored fullName:', fullName);

//   // ✅ Store department data (NULL safe)
//   if (authResponse.department) {
//     localStorage.setItem('departmentId', authResponse.department.id?.toString() || '');
//     localStorage.setItem('departmentName', authResponse.department.name || '');
//     localStorage.setItem('department', JSON.stringify(authResponse.department));
//     console.log('✅ Stored department:', authResponse.department.name);
//   } else {
//     console.warn('⚠️ No department data in response');
//   }

//   // ✅ Store location data (NULL safe)
//   if (authResponse.location) {
//     localStorage.setItem('locationId', authResponse.location.id?.toString() || '');
//     localStorage.setItem('city', authResponse.location.city || '');
//     localStorage.setItem('state', authResponse.location.state || '');
//     localStorage.setItem('postalCode', authResponse.location.postalCode || '');
//     localStorage.setItem('country', authResponse.location.country || '');
//     localStorage.setItem('location', JSON.stringify(authResponse.location));
//     console.log('✅ Stored location:', authResponse.location.city);
//   } else {
//     console.warn('⚠️ No location data in response');
//   }

//   // ✅ Store buyer data (NULL safe)
//   if (authResponse.buyer) {
//     localStorage.setItem('buyerId', authResponse.buyer.id?.toString() || '');
//     localStorage.setItem('buyerName', authResponse.buyer.name || '');
//     localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
//     localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));
//     console.log('✅ Stored buyer:', authResponse.buyer.name);
//   } else {
//     console.warn('⚠️ No buyer data in response');
//   }

//   // ✅ Create User object for AuthService
//   const currentUser: User = {
//     id: userId,
//     username: email,
//     roles: ['ROLE_BUYER'],
//     department: authResponse.department?.id ? [authResponse.department.id] : []
//   };

//   // ✅ Set user in AuthService
//   this.authService.setUser(currentUser);
//   this.authService.setLoginStatus(true);

//   // ✅ Store complete signin data
//   localStorage.setItem('signinData', JSON.stringify(authResponse));

//   // ✅ Create user role access
//   const userRoleAccess = [{
//     "userRoleAccessId": 0,
//     "userRoles": "ROLE_BUYER",
//     "pageAccess": "ALL",
//     "createdDate": null,
//     "accessRead": true,
//     "accessEdit": true,
//     "accessDelete": true
//   }];
//   localStorage.setItem('userRole', JSON.stringify(userRoleAccess));

//   console.log('========================================');
//   console.log('✅ BUYER LOGIN COMPLETE');
//   console.log('========================================');
//   console.log('User ID:', userId);
//   console.log('Email:', email);
//   console.log('Full Name:', fullName);
//   console.log('Role: ROLE_BUYER');
//   console.log('========================================');

//   // ✅ Navigate to RFQ Dashboard
//   this.router.navigate(['/rfq-dashboard']);
// }

//   // ============================================
//   // UTILITY METHODS
//   // ============================================
//   clearError() {
//     this.errorMessage = null;
//     this.successMessage = null;
//   }
// }

import { Component, inject } from '@angular/core';
import { DataService } from '../../../shared/service/DataService';
import { Router } from '@angular/router';
import { IconDirective } from '@coreui/icons-angular';
import { ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, FormControlDirective, ButtonDirective } from '@coreui/angular';
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
  imports: [CommonModule, ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, InputGroupComponent, InputGroupTextDirective, IconDirective, FormControlDirective, ButtonDirective, FormsModule, ReactiveFormsModule, AlertModule]
})
export class LoginComponent {

  // Single Form Group
  loginForm!: FormGroup;

  // UI State
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

  // ============================================
  // INITIALIZE SINGLE FORM
  // ============================================
  initializeForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required])
    });
  }

  // ============================================
  // FORM GETTER
  // ============================================
  get loginF() {
    return this.loginForm.controls;
  }

  // ============================================
  // UNIFIED LOGIN - TRIES ALL ENDPOINTS
  // ============================================
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

      // Try all login endpoints in parallel
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
        )
      }).subscribe({
        next: (results: any) => {
          console.log('📊 Login Results:', results);

          // Check which login succeeded
          if (results.superAdmin?.success === true && results.superAdmin?.data?.token) {
            this.handleSuperAdminLogin(results.superAdmin, loginRequest);
          } 
          else if (results.orgAdmin?.success === true && results.orgAdmin?.data?.token) {
            this.handleOrgAdminLogin(results.orgAdmin, loginRequest);
          } 
          else if (results.hierarchy?.success === true && results.hierarchy?.data?.token) {
            this.handleHierarchyLogin(results.hierarchy, loginRequest);
          } 
          else if (results.buyer?.success === true && results.buyer?.token) {
            this.handleBuyerLogin(results.buyer, loginRequest);
          } 
          else {
            // All failed
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
  // HANDLE SUPERADMIN LOGIN SUCCESS
  // ============================================
  private handleSuperAdminLogin(response: any, loginRequest: any) {
    console.log('✅ SUPERADMIN LOGIN SUCCESS');
    this.isLoading = false;

    const userData = response.data;

    localStorage.setItem('token', userData.token);
    const now = new Date().getTime();
    const expiryTime = now + (24 * 60 * 60 * 1000);
    localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
    localStorage.setItem('userId', userData.id?.toString() || '');
    localStorage.setItem('email', userData.email || '');
    localStorage.setItem('fullName', userData.fullName || '');
    localStorage.setItem('phone', userData.phone || '');
    localStorage.setItem('companyName', userData.companyName || '');
    localStorage.setItem('role', 'SUPER_ADMIN');
    localStorage.setItem('userType', 'SUPER_ADMIN');
    localStorage.setItem('loginStatus', 'true');

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
        "userRoleAccessId": 0,
        "userRoles": "SUPER_ADMIN",
        "pageAccess": "ALL",
        "createdDate": null,
        "accessRead": true,
        "accessEdit": true,
        "accessDelete": true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);

    console.log('✅ Redirecting to SuperAdmin Dashboard');
    this.router.navigate(['/superadmin-dashboard']);
  }

  // ============================================
  // HANDLE ORGANIZATION ADMIN LOGIN SUCCESS
  // ============================================
  private handleOrgAdminLogin(response: any, loginRequest: any) {
    console.log('✅ ORGANIZATION ADMIN LOGIN SUCCESS');
    this.isLoading = false;

    const userData = response.data;

    localStorage.setItem('token', userData.token);
    const now = new Date().getTime();
    const expiryTime = now + (24 * 60 * 60 * 1000);
    localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
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
        "userRoleAccessId": 0,
        "userRoles": "ORGANIZATION_ADMIN",
        "pageAccess": "ALL",
        "createdDate": null,
        "accessRead": true,
        "accessEdit": true,
        "accessDelete": true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);

    // Check if password change is required
    if (userData.mustChangePassword === true) {
      console.log('⚠️ Password change required');
      this.messageService.showMessage('warning', 'Password Change Required', 
        'You must change your password on first login for security.');
      setTimeout(() => {
        this.router.navigate(['/change-password']);
      }, 500);
    } else {
      console.log('✅ Redirecting to OrgAdmin Dashboard');
      this.router.navigate(['/orgadmin-dashboard']);
    }
  }

  // ============================================
  // ✅ COMPLETELY FIXED: HANDLE HIERARCHY LOGIN SUCCESS
  // ============================================
  private handleHierarchyLogin(response: any, loginRequest: any) {
    console.log('✅ HIERARCHY LOGIN SUCCESS');
    console.log('%c[FULL LOGIN RESPONSE]', 'color: #0066cc; font-weight: bold;', response);
    console.log('%c[RESPONSE TYPE]', 'color: #0066cc;', typeof response);
    console.log('%c[RESPONSE KEYS]', 'color: #0066cc;', Object.keys(response || {}));
    
    this.isLoading = false;

    // ✅ CRITICAL FIX: Extract user data from response
    let userData: any;
    
    if (response?.data) {
      userData = response.data;
      console.log('%c[EXTRACTED FROM response.data]', 'color: #0066cc;', userData);
    } else {
      userData = response;
      console.log('%c[USING DIRECT response]', 'color: #0066cc;', userData);
    }

    console.log('%c[USER DATA KEYS]', 'color: #0066cc;', Object.keys(userData || {}));
    console.log('%c[CHECKING hierarchyLevel]', 'color: #0066cc;', userData.hierarchyLevel);

    // ✅ CRITICAL: Extract hierarchy level data properly
    let hierarchyLevelData: any = null;

    if (userData.hierarchyLevel && userData.hierarchyLevel.id) {
      // Best case: Full hierarchy level object exists
      hierarchyLevelData = userData.hierarchyLevel;
      console.log('%c[✓ FOUND FULL hierarchyLevel]', 'color: #00aa00;', hierarchyLevelData);
    } 
    else if (userData.hierarchyLevelId) {
      // Fallback: Only ID exists, create minimal object
      console.warn('%c[⚠️ ONLY hierarchyLevelId EXISTS]', 'color: #ff9800;', userData.hierarchyLevelId);
      
      hierarchyLevelData = {
        id: userData.hierarchyLevelId,
        levelName: userData.hierarchyLevelName || 'Unknown Level',
        levelOrder: userData.hierarchyLevelOrder || 0,
        companyName: userData.companyName || ''
      };
      
      console.log('%c[CREATED MINIMAL hierarchyLevel]', 'color: #ff9800;', hierarchyLevelData);
    }
    else {
      // No hierarchy level data at all - this is an error
      console.error('%c[❌ ERROR] No hierarchy level data found in response', 'color: #cc0000; font-weight: bold;');
      console.error('%c[USER DATA]', 'color: #cc0000;', userData);
      
      this.messageService.showMessage('error', 'Login Failed', 
        'Your account is not assigned to any hierarchy level. Please contact your administrator.');
      this.isLoading = false;
      return;
    }

    // ✅ Store basic user data
    localStorage.setItem('token', userData.token);
    const now = new Date().getTime();
    const expiryTime = now + (24 * 60 * 60 * 1000);
    localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    
    localStorage.setItem('userId', userData.id?.toString() || '');
    localStorage.setItem('email', userData.email || '');
    localStorage.setItem('fullName', userData.fullName || '');
    localStorage.setItem('phone', userData.phone || '');
    localStorage.setItem('role', userData.role || '');
    localStorage.setItem('userType', userData.role || '');
    localStorage.setItem('loginStatus', 'true');
    
    // ✅ CRITICAL: Store hierarchy level data (FIXED)
    localStorage.setItem('hierarchyLevelId', hierarchyLevelData.id?.toString() || '');
    localStorage.setItem('hierarchyLevelName', hierarchyLevelData.levelName || '');
    localStorage.setItem('hierarchyLevelOrder', hierarchyLevelData.levelOrder?.toString() || '');
    localStorage.setItem('companyName', hierarchyLevelData.companyName || '');
    
    console.log('%c[✅ ALL DATA STORED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold;', {
      userId: userData.id,
      levelId: hierarchyLevelData.id,
      levelName: hierarchyLevelData.levelName,
      levelOrder: hierarchyLevelData.levelOrder,
      companyName: hierarchyLevelData.companyName
    });

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
        "userRoleAccessId": 0,
        "userRoles": userData.role,
        "pageAccess": "ALL",
        "createdDate": null,
        "accessRead": true,
        "accessEdit": true,
        "accessDelete": true
      }]
    };

    localStorage.setItem('signinData', JSON.stringify(signinData));
    localStorage.setItem('userRole', JSON.stringify(signinData.userRoleAccess));

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);

    console.log('✅ Redirecting to Hierarchy Dashboard');
    this.router.navigate(['/hierarchy-dashboard']);
  }

  // ============================================
  // ✅ COMPLETELY FIXED: HANDLE BUYER LOGIN SUCCESS
  // ============================================
  private handleBuyerLogin(authResponse: any, loginRequest: any) {
    console.log('✅ BUYER LOGIN SUCCESS');
    console.log('%c[FULL RESPONSE]', 'color: #0066cc; font-weight: bold;', authResponse);
    
    this.isLoading = false;

    // ✅ Check if token exists
    if (!authResponse.token) {
      console.error('❌ No token in response');
      this.errorMessage = 'Login failed: Invalid response from server';
      return;
    }

    // ✅ Store token and timing
    const now = new Date().getTime();
    const oneHourMs = 60 * 60 * 1000;
    const expiryTime = now + oneHourMs;

    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('expirationTime', JSON.stringify(expiryTime));
    localStorage.setItem('loginTimestamp', JSON.stringify(now));
    localStorage.setItem('role', 'ROLE_BUYER');
    localStorage.setItem('userType', 'ROLE_BUYER');
    localStorage.setItem('loginStatus', 'true');

    // ✅ Store user ID (CRITICAL)
    const userId = authResponse.userId || authResponse.id || 0;
    localStorage.setItem('userId', userId.toString());
    console.log('✅ Stored userId:', userId);

    // ✅ Store email
    const email = authResponse.email || loginRequest.email;
    localStorage.setItem('email', email);

    // ✅ Build full name safely
    let fullName = '';
    if (authResponse.fullName && authResponse.fullName.trim()) {
      fullName = authResponse.fullName.trim();
    } else if (authResponse.firstName || authResponse.lastName) {
      const first = (authResponse.firstName || '').trim();
      const last = (authResponse.lastName || '').trim();
      fullName = (first + ' ' + last).trim();
    }
    if (!fullName) {
      fullName = email.split('@')[0];
    }
    localStorage.setItem('fullName', fullName);

    // ✅ Store optional data (NULL safe)
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
      localStorage.setItem('buyerId', authResponse.buyer.id?.toString() || '');
      localStorage.setItem('buyerName', authResponse.buyer.name || '');
      localStorage.setItem('buyerEmail', authResponse.buyer.email || '');
      localStorage.setItem('buyer', JSON.stringify(authResponse.buyer));
    }

    // ✅ Create User object
    const currentUser: User = {
      id: userId,
      username: email,
      roles: ['ROLE_BUYER'],
      department: authResponse.department?.id ? [authResponse.department.id] : []
    };

    this.authService.setUser(currentUser);
    this.authService.setLoginStatus(true);

    localStorage.setItem('signinData', JSON.stringify(authResponse));

    const userRoleAccess = [{
      "userRoleAccessId": 0,
      "userRoles": "ROLE_BUYER",
      "pageAccess": "ALL",
      "createdDate": null,
      "accessRead": true,
      "accessEdit": true,
      "accessDelete": true
    }];
    localStorage.setItem('userRole', JSON.stringify(userRoleAccess));

    console.log('✅ BUYER LOGIN COMPLETE - Redirecting to RFQ Dashboard');
    this.router.navigate(['/rfq-dashboard']);
  }

  // ============================================
  // UTILITY METHODS
  // ============================================
  clearError() {
    this.errorMessage = null;
    this.successMessage = null;
  }
}