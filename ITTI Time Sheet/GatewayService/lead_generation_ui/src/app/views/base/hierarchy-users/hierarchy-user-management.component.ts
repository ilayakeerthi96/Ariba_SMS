// // // // import { Component, OnInit, ViewChild } from '@angular/core';
// // // // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// // // // import { CommonModule } from '@angular/common';
// // // // import { DataService } from '../../../shared/service/DataService';
// // // // import {
// // // //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// // // //   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // // //   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // // //   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // // //   TooltipDirective
// // // // } from '@coreui/angular';
// // // // import { IconDirective } from '@coreui/icons-angular';

// // // // @Component({
// // // //   selector: 'app-hierarchy-user-management',
// // // //   templateUrl: './hierarchy-user-management.component.html',
// // // //   styleUrls: ['./hierarchy-user-management.component.css'],
// // // //   standalone: true,
// // // //   imports: [
// // // //     CommonModule, ReactiveFormsModule, FormsModule,
// // // //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// // // //     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // // //     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // // //     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // // //     TooltipDirective, IconDirective
// // // //   ]
// // // // })
// // // // export class HierarchyUserManagementComponent implements OnInit {

// // // //   @ViewChild('userModal') userModal!: ModalComponent;
// // // //   @ViewChild('deleteModal') deleteModal!: ModalComponent;

// // // //   hierarchyUsers: any[] = [];
// // // //   filteredUsers: any[] = [];
// // // //   hierarchyLevels: any[] = [];
// // // //   userForm!: FormGroup;
// // // //   companyName: string = '';
// // // //   selectedLevelFilter: string = '';
  
// // // //   isLoading: boolean = false;
// // // //   isSubmitting: boolean = false;
// // // //   isDeleting: boolean = false;
// // // //   showModal: boolean = false;
// // // //   showDeleteModal: boolean = false;
// // // //   isEditMode: boolean = false;
// // // //   selectedUser: any = null;
// // // //   errorMessage: string | null = null;
// // // //   successMessage: string | null = null;

// // // //   constructor(
// // // //     private fb: FormBuilder,
// // // //     private dataService: DataService
// // // //   ) {
// // // //     this.initForm();
// // // //   }

// // // //   ngOnInit(): void {
// // // //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// // // //     this.loadHierarchyLevels();
// // // //     this.loadHierarchyUsers();
// // // //   }

// // // //   // ============================================
// // // //   // INITIALIZE FORM
// // // //   // ============================================
// // // //   initForm(): void {
// // // //     this.userForm = this.fb.group({
// // // //       email: ['', [Validators.required, Validators.email]],
// // // //       fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// // // //       designation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// // // //       phone: [''],
// // // //       hierarchyLevelId: ['', Validators.required],
// // // //       reportsToIds: [[]],
// // // //       password: [''],
// // // //       companyName: [this.companyName]
// // // //     });
// // // //   }

// // // //   // ✅ NEW: Get potential managers (higher levels)
// // // // getPotentialManagers(): any[] {
// // // //   const currentLevelId = this.userForm.get('hierarchyLevelId')?.value;
// // // //   if (!currentLevelId) return [];
  
// // // //   const currentLevel = this.hierarchyLevels.find(l => l.id == currentLevelId);
// // // //   if (!currentLevel) return [];
  
// // // //   // Return users at higher levels (lower levelOrder)
// // // //   return this.hierarchyUsers.filter(u => 
// // // //     u.hierarchyLevelOrder < currentLevel.levelOrder
// // // //   );
// // // // }

// // // //   // ============================================
// // // //   // LOAD HIERARCHY LEVELS
// // // //   // ============================================
// // // //   loadHierarchyLevels(): void {
// // // //     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
// // // //       next: (response: any) => {
// // // //         if (response?.success && response?.data) {
// // // //           this.hierarchyLevels = response.data;
// // // //           console.log('✅ Loaded hierarchy levels:', this.hierarchyLevels.length);
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         console.error('❌ Error loading hierarchy levels:', error);
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // LOAD HIERARCHY USERS
// // // //   // ============================================
// // // //   loadHierarchyUsers(): void {
// // // //     this.isLoading = true;
// // // //     this.errorMessage = null;

// // // //     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// // // //       next: (response: any) => {
// // // //         console.log('✅ Hierarchy Users Response:', response);
// // // //         this.isLoading = false;

// // // //         if (response?.success && response?.data) {
// // // //           this.hierarchyUsers = response.data;
// // // //           this.filteredUsers = [...this.hierarchyUsers];
// // // //           console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
// // // //         } else {
// // // //           this.hierarchyUsers = [];
// // // //           this.filteredUsers = [];
// // // //           this.errorMessage = response?.message || 'Failed to load users';
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         this.isLoading = false;
// // // //         console.error('❌ Error loading hierarchy users:', error);
// // // //         this.errorMessage = error.error?.message || 'Failed to load users';
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // FILTER BY LEVEL
// // // //   // ============================================
// // // //   filterByLevel(): void {
// // // //     if (!this.selectedLevelFilter) {
// // // //       this.filteredUsers = [...this.hierarchyUsers];
// // // //     } else {
// // // //       this.filteredUsers = this.hierarchyUsers.filter(
// // // //         user => user.hierarchyLevelId?.toString() === this.selectedLevelFilter
// // // //       );
// // // //     }
// // // //   }

// // // //   // ============================================
// // // //   // OPEN CREATE MODAL
// // // //   // ============================================
// // // //   openCreateModal(): void {
// // // //     this.isEditMode = false;
// // // //     this.selectedUser = null;
// // // //     this.userForm.reset();
// // // //     this.userForm.patchValue({ companyName: this.companyName });
    
// // // //     // Make password required for create
// // // //     this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
// // // //     this.userForm.get('password')?.updateValueAndValidity();
    
// // // //     this.errorMessage = null;
// // // //     this.successMessage = null;
// // // //     this.showModal = true;
// // // //   }

// // // //   // ============================================
// // // //   // OPEN EDIT MODAL
// // // //   // ============================================
// // // //   openEditModal(user: any): void {
// // // //     this.isEditMode = true;
// // // //     this.selectedUser = user;
    
// // // //     this.userForm.patchValue({
// // // //       email: user.email,
// // // //       fullName: user.fullName,
// // // //       designation: user.designation,
// // // //       phone: user.phone,
// // // //       hierarchyLevelId: user.hierarchyLevelId,
// // // //       reportsToId: user.reportsToId || '',
// // // //       companyName: user.companyName
// // // //     });
    
// // // //     // Make password optional for edit
// // // //     this.userForm.get('password')?.clearValidators();
// // // //     this.userForm.get('password')?.updateValueAndValidity();
    
// // // //     this.errorMessage = null;
// // // //     this.successMessage = null;
// // // //     this.showModal = true;
// // // //   }

// // // //   // ============================================
// // // //   // SUBMIT USER (CREATE/UPDATE)
// // // //   // ============================================
// // // //   submitUser(): void {
// // // //     if (this.userForm.invalid) {
// // // //       this.userForm.markAllAsTouched();
// // // //       return;
// // // //     }

// // // //     this.isSubmitting = true;
// // // //     this.errorMessage = null;
// // // //     this.successMessage = null;

// // // //     const userData = this.userForm.value;
    
// // // //     // Remove empty password for edit
// // // //     if (this.isEditMode && !userData.password) {
// // // //       delete userData.password;
// // // //     }
    
// // // //     // Remove empty reportsToId
// // // //     if (!userData.reportsToId) {
// // // //       delete userData.reportsToId;
// // // //     }

// // // //     if (this.isEditMode) {
// // // //       // Update existing user
// // // //       this.dataService.updateHierarchyUser(this.selectedUser.id, userData).subscribe({
// // // //         next: (response: any) => {
// // // //           console.log('✅ User Updated:', response);
// // // //           this.isSubmitting = false;

// // // //           if (response?.success) {
// // // //             this.successMessage = 'User updated successfully!';
// // // //             setTimeout(() => {
// // // //               this.closeModal();
// // // //               this.loadHierarchyUsers();
// // // //             }, 1000);
// // // //           } else {
// // // //             this.errorMessage = response?.message || 'Failed to update user';
// // // //           }
// // // //         },
// // // //         error: (error: any) => {
// // // //           this.isSubmitting = false;
// // // //           console.error('❌ Error updating user:', error);
// // // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update user';
// // // //         }
// // // //       });
// // // //     } else {
// // // //       // Create new user
// // // //       this.dataService.createHierarchyUser(userData).subscribe({
// // // //         next: (response: any) => {
// // // //           console.log('✅ User Created:', response);
// // // //           this.isSubmitting = false;

// // // //           if (response?.success) {
// // // //             this.successMessage = 'User created successfully!';
// // // //             setTimeout(() => {
// // // //               this.closeModal();
// // // //               this.loadHierarchyUsers();
// // // //             }, 1000);
// // // //           } else {
// // // //             this.errorMessage = response?.message || 'Failed to create user';
// // // //           }
// // // //         },
// // // //         error: (error: any) => {
// // // //           this.isSubmitting = false;
// // // //           console.error('❌ Error creating user:', error);
// // // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create user';
// // // //         }
// // // //       });
// // // //     }
// // // //   }

// // // //   // ============================================
// // // //   // TOGGLE USER STATUS
// // // //   // ============================================
// // // //   toggleUserStatus(user: any): void {
// // // //     const action = user.isActive ? 'deactivate' : 'activate';
// // // //     const confirmMsg = `Are you sure you want to ${action} "${user.fullName}"?`;

// // // //     if (!confirm(confirmMsg)) {
// // // //       return;
// // // //     }

// // // //     const apiCall = user.isActive
// // // //       ? this.dataService.deactivateHierarchyUser(user.id)
// // // //       : this.dataService.activateHierarchyUser(user.id);

// // // //     apiCall.subscribe({
// // // //       next: (response: any) => {
// // // //         console.log(`✅ User ${action}d:`, response);
// // // //         if (response?.success) {
// // // //           this.loadHierarchyUsers();
// // // //         } else {
// // // //           alert(response?.message || `Failed to ${action} user`);
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         console.error(`❌ Error ${action}ing user:`, error);
// // // //         alert(error.error?.message || `Failed to ${action} user`);
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // CONFIRM DELETE
// // // //   // ============================================
// // // //   confirmDelete(user: any): void {
// // // //     this.selectedUser = user;
// // // //     this.showDeleteModal = true;
// // // //   }

// // // //   // ============================================
// // // //   // DELETE USER
// // // //   // ============================================
// // // //   deleteUser(): void {
// // // //     if (!this.selectedUser) return;

// // // //     this.isDeleting = true;

// // // //     this.dataService.deleteHierarchyUser(this.selectedUser.id).subscribe({
// // // //       next: (response: any) => {
// // // //         console.log('✅ User Deleted:', response);
// // // //         this.isDeleting = false;

// // // //         if (response?.success) {
// // // //           this.showDeleteModal = false;
// // // //           this.loadHierarchyUsers();
// // // //         } else {
// // // //           alert(response?.message || 'Failed to delete user');
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         this.isDeleting = false;
// // // //         console.error('❌ Error deleting user:', error);
// // // //         alert(error.error?.message || 'Failed to delete user');
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // CLOSE MODAL
// // // //   // ============================================
// // // //   closeModal(): void {
// // // //     this.showModal = false;
// // // //     this.userForm.reset();
// // // //     this.errorMessage = null;
// // // //     this.successMessage = null;
// // // //   }

// // // //   onModalClose(visible: boolean): void {
// // // //     if (!visible) {
// // // //       this.closeModal();
// // // //     }
// // // //   }

// // // //   // ============================================
// // // //   // UTILITY METHODS
// // // //   // ============================================
// // // //   getLevelBadgeColor(order: number): string {
// // // //     if (order === 1) return 'danger';
// // // //     if (order === 2) return 'warning';
// // // //     if (order === 3) return 'info';
// // // //     return 'secondary';
// // // //   }
// // // // }


// // // import { Component, OnInit, ViewChild } from '@angular/core';
// // // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// // // import { CommonModule } from '@angular/common';
// // // import { DataService } from '../../../shared/service/DataService';
// // // import {
// // //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // //   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // //   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // //   TooltipDirective
// // // } from '@coreui/angular';
// // // import { IconDirective } from '@coreui/icons-angular';

// // // @Component({
// // //   selector: 'app-hierarchy-user-management',
// // //   templateUrl: './hierarchy-user-management.component.html',
// // //   styleUrls: ['./hierarchy-user-management.component.css'],
// // //   standalone: true,
// // //   imports: [
// // //     CommonModule, ReactiveFormsModule, FormsModule,
// // //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // //     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // //     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // //     TooltipDirective, IconDirective
// // //   ]
// // // })
// // // export class HierarchyUserManagementComponent implements OnInit {

// // //   @ViewChild('userModal') userModal!: ModalComponent;
// // //   @ViewChild('deleteModal') deleteModal!: ModalComponent;

// // //   hierarchyUsers: any[] = [];
// // //   filteredUsers: any[] = [];
// // //   hierarchyLevels: any[] = [];
// // //   potentialManagers: any[] = []; // ✅ Store potential managers
// // //   userForm!: FormGroup;
// // //   companyName: string = '';
// // //   selectedLevelFilter: string = '';
  
// // //   isLoading: boolean = false;
// // //   isSubmitting: boolean = false;
// // //   isDeleting: boolean = false;
// // //   showModal: boolean = false;
// // //   showDeleteModal: boolean = false;
// // //   isEditMode: boolean = false;
// // //   selectedUser: any = null;
// // //   errorMessage: string | null = null;
// // //   successMessage: string | null = null;

// // //   constructor(
// // //     private fb: FormBuilder,
// // //     private dataService: DataService
// // //   ) {
// // //     this.initForm();
// // //   }

// // //   ngOnInit(): void {
// // //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// // //     this.loadHierarchyLevels();
// // //     this.loadHierarchyUsers();
// // //   }

// // //   // ============================================
// // //   // INITIALIZE FORM - ✅ UPDATED with array for multiple managers
// // //   // ============================================
// // //   initForm(): void {
// // //     this.userForm = this.fb.group({
// // //       email: ['', [Validators.required, Validators.email]],
// // //       fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// // //       designation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// // //       phone: [''],
// // //       hierarchyLevelId: ['', Validators.required],
// // //       reportsToIds: [[]],  // ✅ CHANGED: Array for multiple managers
// // //       password: [''],
// // //       companyName: [this.companyName]
// // //     });

// // //     // ✅ Watch for hierarchy level changes to update potential managers
// // //     this.userForm.get('hierarchyLevelId')?.valueChanges.subscribe(() => {
// // //       this.updatePotentialManagers();
// // //     });
// // //   }

// // //   // ============================================
// // //   // LOAD HIERARCHY LEVELS
// // //   // ============================================
// // //   loadHierarchyLevels(): void {
// // //     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
// // //       next: (response: any) => {
// // //         if (response?.success && response?.data) {
// // //           this.hierarchyLevels = response.data;
// // //           console.log('✅ Loaded hierarchy levels:', this.hierarchyLevels.length);
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         console.error('❌ Error loading hierarchy levels:', error);
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // LOAD HIERARCHY USERS
// // //   // ============================================
// // //   // loadHierarchyUsers(): void {
// // //   //   this.isLoading = true;
// // //   //   this.errorMessage = null;

// // //   //   this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// // //   //     next: (response: any) => {
// // //   //       console.log('✅ Hierarchy Users Response:', response);
// // //   //       this.isLoading = false;

// // //   //       if (response?.success && response?.data) {
// // //   //         this.hierarchyUsers = response.data;
// // //   //         this.filteredUsers = [...this.hierarchyUsers];
// // //   //         console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
// // //   //       } else {
// // //   //         this.hierarchyUsers = [];
// // //   //         this.filteredUsers = [];
// // //   //         this.errorMessage = response?.message || 'Failed to load users';
// // //   //       }
// // //   //     },
// // //   //     error: (error: any) => {
// // //   //       this.isLoading = false;
// // //   //       console.error('❌ Error loading hierarchy users:', error);
// // //   //       this.errorMessage = error.error?.message || 'Failed to load users';
// // //   //     }
// // //   //   });
// // //   // }
// // //   loadHierarchyUsers(): void {
// // //   this.isLoading = true;
// // //   this.errorMessage = null;

// // //   this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// // //     next: (response: any) => {
// // //       console.log('✅ Full Hierarchy Users Response:', response);
// // //       this.isLoading = false;

// // //       if (response?.success && response?.data) {
// // //         this.hierarchyUsers = response.data;
// // //         this.filteredUsers = [...this.hierarchyUsers];
        
// // //         // ✅ DEBUG: Check each user's reportsTo data
// // //         this.hierarchyUsers.forEach(user => {
// // //           console.log(`👤 User: ${user.fullName}`);
// // //           console.log('   reportsTo:', user.reportsTo);
// // //           console.log('   reportsToId:', user.reportsToId);
// // //           console.log('   reportsToName:', user.reportsToName);
// // //         });
        
// // //         console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
// // //       } else {
// // //         this.hierarchyUsers = [];
// // //         this.filteredUsers = [];
// // //         this.errorMessage = response?.message || 'Failed to load users';
// // //       }
// // //     },
// // //     error: (error: any) => {
// // //       this.isLoading = false;
// // //       console.error('❌ Error loading hierarchy users:', error);
// // //       this.errorMessage = error.error?.message || 'Failed to load users';
// // //     }
// // //   });
// // // }

// // //   // ============================================
// // //   // ✅ NEW: Update potential managers based on selected level
// // //   // ============================================
// // //   updatePotentialManagers(): void {
// // //     const selectedLevelId = this.userForm.get('hierarchyLevelId')?.value;
    
// // //     if (!selectedLevelId) {
// // //       this.potentialManagers = [];
// // //       return;
// // //     }

// // //     const selectedLevel = this.hierarchyLevels.find(l => l.id == selectedLevelId);
    
// // //     if (!selectedLevel) {
// // //       this.potentialManagers = [];
// // //       return;
// // //     }

// // //     // Filter users at higher levels (lower levelOrder) and exclude current user if editing
// // //     this.potentialManagers = this.hierarchyUsers.filter(user => {
// // //       // Exclude current user when editing
// // //       if (this.isEditMode && this.selectedUser && user.id === this.selectedUser.id) {
// // //         return false;
// // //       }
      
// // //       // Only show users at higher hierarchy levels
// // //       return user.hierarchyLevelOrder < selectedLevel.levelOrder;
// // //     });

// // //     console.log('✅ Updated potential managers:', this.potentialManagers.length);
// // //   }

// // //   // ============================================
// // //   // FILTER BY LEVEL
// // //   // ============================================
// // //   filterByLevel(): void {
// // //     if (!this.selectedLevelFilter) {
// // //       this.filteredUsers = [...this.hierarchyUsers];
// // //     } else {
// // //       this.filteredUsers = this.hierarchyUsers.filter(
// // //         user => user.hierarchyLevelId?.toString() === this.selectedLevelFilter
// // //       );
// // //     }
// // //   }

// // //   // ============================================
// // //   // OPEN CREATE MODAL
// // //   // ============================================
// // //   openCreateModal(): void {
// // //     this.isEditMode = false;
// // //     this.selectedUser = null;
// // //     this.userForm.reset();
// // //     this.userForm.patchValue({ 
// // //       companyName: this.companyName,
// // //       reportsToIds: []  // ✅ Initialize as empty array
// // //     });
    
// // //     // Make password required for create
// // //     this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
// // //     this.userForm.get('password')?.updateValueAndValidity();
    
// // //     this.potentialManagers = [];
// // //     this.errorMessage = null;
// // //     this.successMessage = null;
// // //     this.showModal = true;
// // //   }

// // //   // ============================================
// // //   // OPEN EDIT MODAL - ✅ UPDATED for multiple managers
// // //   // ============================================
// // //   openEditModal(user: any): void {
// // //     this.isEditMode = true;
// // //     this.selectedUser = user;
    
// // //     // ✅ Extract manager IDs from reportsTo array
// // //     let managerIds: number[] = [];
// // //     if (user.reportsTo && Array.isArray(user.reportsTo)) {
// // //       managerIds = user.reportsTo.map((m: any) => m.id);
// // //     } else if (user.reportsToId) {
// // //       // Backward compatibility: single manager
// // //       managerIds = [user.reportsToId];
// // //     }
    
// // //     this.userForm.patchValue({
// // //       email: user.email,
// // //       fullName: user.fullName,
// // //       designation: user.designation,
// // //       phone: user.phone,
// // //       hierarchyLevelId: user.hierarchyLevelId,
// // //       reportsToIds: managerIds,  // ✅ Set array of manager IDs
// // //       companyName: user.companyName
// // //     });
    
// // //     // Trigger potential managers update
// // //     this.updatePotentialManagers();
    
// // //     // Make password optional for edit
// // //     this.userForm.get('password')?.clearValidators();
// // //     this.userForm.get('password')?.updateValueAndValidity();
    
// // //     this.errorMessage = null;
// // //     this.successMessage = null;
// // //     this.showModal = true;
// // //   }

// // //   // ============================================
// // //   // SUBMIT USER (CREATE/UPDATE) - ✅ UPDATED
// // //   // ============================================
// // //   submitUser(): void {
// // //     if (this.userForm.invalid) {
// // //       this.userForm.markAllAsTouched();
// // //       return;
// // //     }

// // //     this.isSubmitting = true;
// // //     this.errorMessage = null;
// // //     this.successMessage = null;

// // //     const userData = { ...this.userForm.value };
    
// // //     // ✅ Ensure reportsToIds is always an array (even if empty)
// // //     if (!userData.reportsToIds || !Array.isArray(userData.reportsToIds)) {
// // //       userData.reportsToIds = [];
// // //     }
    
// // //     // Remove empty password for edit
// // //     if (this.isEditMode && !userData.password) {
// // //       delete userData.password;
// // //     }

// // //     console.log('📤 Submitting user data:', userData);

// // //     if (this.isEditMode) {
// // //       // Update existing user
// // //       this.dataService.updateHierarchyUser(this.selectedUser.id, userData).subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ User Updated:', response);
// // //           this.isSubmitting = false;

// // //           if (response?.success) {
// // //             this.successMessage = 'User updated successfully!';
// // //             setTimeout(() => {
// // //               this.closeModal();
// // //               this.loadHierarchyUsers();
// // //             }, 1000);
// // //           } else {
// // //             this.errorMessage = response?.message || 'Failed to update user';
// // //           }
// // //         },
// // //         error: (error: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Error updating user:', error);
// // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update user';
// // //         }
// // //       });
// // //     } else {
// // //       // Create new user
// // //       this.dataService.createHierarchyUser(userData).subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ User Created:', response);
// // //           this.isSubmitting = false;

// // //           if (response?.success) {
// // //             this.successMessage = 'User created successfully!';
// // //             setTimeout(() => {
// // //               this.closeModal();
// // //               this.loadHierarchyUsers();
// // //             }, 1000);
// // //           } else {
// // //             this.errorMessage = response?.message || 'Failed to create user';
// // //           }
// // //         },
// // //         error: (error: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Error creating user:', error);
// // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create user';
// // //         }
// // //       });
// // //     }
// // //   }

// // //   // ============================================
// // //   // TOGGLE USER STATUS
// // //   // ============================================
// // //   toggleUserStatus(user: any): void {
// // //     const action = user.isActive ? 'deactivate' : 'activate';
// // //     const confirmMsg = `Are you sure you want to ${action} "${user.fullName}"?`;

// // //     if (!confirm(confirmMsg)) {
// // //       return;
// // //     }

// // //     const apiCall = user.isActive
// // //       ? this.dataService.deactivateHierarchyUser(user.id)
// // //       : this.dataService.activateHierarchyUser(user.id);

// // //     apiCall.subscribe({
// // //       next: (response: any) => {
// // //         console.log(`✅ User ${action}d:`, response);
// // //         if (response?.success) {
// // //           this.loadHierarchyUsers();
// // //         } else {
// // //           alert(response?.message || `Failed to ${action} user`);
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         console.error(`❌ Error ${action}ing user:`, error);
// // //         alert(error.error?.message || `Failed to ${action} user`);
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // CONFIRM DELETE
// // //   // ============================================
// // //   confirmDelete(user: any): void {
// // //     this.selectedUser = user;
// // //     this.showDeleteModal = true;
// // //   }

// // //   // ============================================
// // //   // DELETE USER
// // //   // ============================================
// // //   deleteUser(): void {
// // //     if (!this.selectedUser) return;

// // //     this.isDeleting = true;

// // //     this.dataService.deleteHierarchyUser(this.selectedUser.id).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ User Deleted:', response);
// // //         this.isDeleting = false;

// // //         if (response?.success) {
// // //           this.showDeleteModal = false;
// // //           this.loadHierarchyUsers();
// // //         } else {
// // //           alert(response?.message || 'Failed to delete user');
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         this.isDeleting = false;
// // //         console.error('❌ Error deleting user:', error);
// // //         alert(error.error?.message || 'Failed to delete user');
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // CLOSE MODAL
// // //   // ============================================
// // //   closeModal(): void {
// // //     this.showModal = false;
// // //     this.userForm.reset();
// // //     this.potentialManagers = [];
// // //     this.errorMessage = null;
// // //     this.successMessage = null;
// // //   }

// // //   onModalClose(visible: boolean): void {
// // //     if (!visible) {
// // //       this.closeModal();
// // //     }
// // //   }

// // //   // ============================================
// // //   // UTILITY METHODS
// // //   // ============================================
// // //   getLevelBadgeColor(order: number): string {
// // //     if (order <= 10) return 'danger';
// // //     if (order <= 20) return 'warning';
// // //     if (order <= 30) return 'info';
// // //     return 'secondary';
// // //   }

// // //   // ✅ NEW: Format managers display
// // //   formatManagers(managers: any[]): string {
// // //     if (!managers || managers.length === 0) {
// // //       return 'None';
// // //     }
// // //     return managers.map(m => m.fullName).join(', ');
// // //   }

// // //   // Add these methods to your existing HierarchyUserManagementComponent

// // // // ============================================
// // // // ✅ NEW: Check if manager is selected in form
// // // // ============================================
// // // isManagerSelectedInForm(managerId: number): boolean {
// // //   const currentSelection = this.userForm.get('reportsToIds')?.value || [];
// // //   return currentSelection.includes(managerId);
// // // }

// // // // ============================================
// // // // ✅ NEW: Toggle manager selection in form (chip style)
// // // // ============================================
// // // toggleManagerInForm(managerId: number): void {
// // //   const currentSelection: number[] = this.userForm.get('reportsToIds')?.value || [];
  
// // //   const index = currentSelection.indexOf(managerId);
// // //   if (index > -1) {
// // //     // Remove manager
// // //     currentSelection.splice(index, 1);
// // //   } else {
// // //     // Add manager
// // //     currentSelection.push(managerId);
// // //   }
  
// // //   // Update form control
// // //   this.userForm.patchValue({
// // //     reportsToIds: [...currentSelection]
// // //   });
  
// // //   console.log('✅ Updated managers selection:', currentSelection);
// // // }

// // // }


// // import { Component, OnInit, ViewChild } from '@angular/core';
// // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// // import { CommonModule } from '@angular/common';
// // import { DataService } from '../../../shared/service/DataService';
// // import {
// //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// //   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// //   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// //   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// //   TooltipDirective
// // } from '@coreui/angular';
// // import { IconDirective } from '@coreui/icons-angular';

// // @Component({
// //   selector: 'app-hierarchy-user-management',
// //   templateUrl: './hierarchy-user-management.component.html',
// //   styleUrls: ['./hierarchy-user-management.component.css'],
// //   standalone: true,
// //   imports: [
// //     CommonModule, ReactiveFormsModule, FormsModule,
// //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// //     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// //     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// //     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// //     TooltipDirective, IconDirective
// //   ]
// // })
// // export class HierarchyUserManagementComponent implements OnInit {

// //   @ViewChild('userModal') userModal!: ModalComponent;
// //   @ViewChild('deleteModal') deleteModal!: ModalComponent;

// //   hierarchyUsers: any[] = [];
// //   filteredUsers: any[] = [];
// //   hierarchyLevels: any[] = [];
// //   potentialManagers: any[] = [];
// //   userForm!: FormGroup;
// //   companyName: string = '';
// //   selectedLevelFilter: string = '';
  
// //   isLoading: boolean = false;
// //   isSubmitting: boolean = false;
// //   isDeleting: boolean = false;
// //   showModal: boolean = false;
// //   showDeleteModal: boolean = false;
// //   isEditMode: boolean = false;
// //   selectedUser: any = null;
// //   errorMessage: string | null = null;
// //   successMessage: string | null = null;

// //   constructor(
// //     private fb: FormBuilder,
// //     private dataService: DataService
// //   ) {
// //     this.initForm();
// //   }

// //   ngOnInit(): void {
// //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// //     this.loadHierarchyLevels();
// //     this.loadHierarchyUsers();
// //   }

// //   // ============================================
// //   // INITIALIZE FORM
// //   // ============================================
// //   initForm(): void {
// //     this.userForm = this.fb.group({
// //       email: ['', [Validators.required, Validators.email]],
// //       fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// //       designation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// //       phone: [''],
// //       hierarchyLevelId: ['', Validators.required],
// //       reportsToIds: [[]],  // Array for multiple managers
// //       password: [''],
// //       companyName: [this.companyName]
// //     });

// //     // Watch for hierarchy level changes to update potential managers
// //     this.userForm.get('hierarchyLevelId')?.valueChanges.subscribe(() => {
// //       this.updatePotentialManagers();
// //     });
// //   }

// //   // ============================================
// //   // LOAD HIERARCHY LEVELS
// //   // ============================================
// //   loadHierarchyLevels(): void {
// //     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
// //       next: (response: any) => {
// //         if (response?.success && response?.data) {
// //           this.hierarchyLevels = response.data;
// //           console.log('✅ Loaded hierarchy levels:', this.hierarchyLevels.length);
// //         }
// //       },
// //       error: (error: any) => {
// //         console.error('❌ Error loading hierarchy levels:', error);
// //       }
// //     });
// //   }

// //   // ============================================
// //   // LOAD HIERARCHY USERS
// //   // ============================================
// //   loadHierarchyUsers(): void {
// //     this.isLoading = true;
// //     this.errorMessage = null;

// //     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ Full Hierarchy Users Response:', response);
// //         this.isLoading = false;

// //         if (response?.success && response?.data) {
// //           this.hierarchyUsers = response.data;
// //           this.filteredUsers = [...this.hierarchyUsers];
          
// //           // DEBUG: Check each user's reportsTo data
// //           this.hierarchyUsers.forEach(user => {
// //             console.log(`👤 User: ${user.fullName}`);
// //             console.log('   reportsTo:', user.reportsTo);
// //           });
          
// //           console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
// //         } else {
// //           this.hierarchyUsers = [];
// //           this.filteredUsers = [];
// //           this.errorMessage = response?.message || 'Failed to load users';
// //         }
// //       },
// //       error: (error: any) => {
// //         this.isLoading = false;
// //         console.error('❌ Error loading hierarchy users:', error);
// //         this.errorMessage = error.error?.message || 'Failed to load users';
// //       }
// //     });
// //   }

// //   // ============================================
// //   // ✅ FIXED: Update potential managers
// //   // ============================================
// //   updatePotentialManagers(): void {
// //     const selectedLevelId = this.userForm.get('hierarchyLevelId')?.value;
    
// //     if (!selectedLevelId) {
// //       this.potentialManagers = [];
// //       return;
// //     }

// //     // ✅ Convert to number for comparison
// //     const levelIdNumber = Number(selectedLevelId);
// //     const selectedLevel = this.hierarchyLevels.find(l => l.id === levelIdNumber);
    
// //     if (!selectedLevel) {
// //       this.potentialManagers = [];
// //       return;
// //     }

// //     // Filter users at higher levels (lower levelOrder)
// //     this.potentialManagers = this.hierarchyUsers.filter(user => {
// //       // Exclude current user when editing
// //       if (this.isEditMode && this.selectedUser && user.id === this.selectedUser.id) {
// //         return false;
// //       }
      
// //       // Only show users at higher hierarchy levels
// //       return user.hierarchyLevelOrder < selectedLevel.levelOrder;
// //     });

// //     console.log('✅ Updated potential managers:', this.potentialManagers.length);
// //   }

// //   // ============================================
// //   // FILTER BY LEVEL
// //   // ============================================
// //   filterByLevel(): void {
// //     if (!this.selectedLevelFilter) {
// //       this.filteredUsers = [...this.hierarchyUsers];
// //     } else {
// //       this.filteredUsers = this.hierarchyUsers.filter(
// //         user => user.hierarchyLevelId?.toString() === this.selectedLevelFilter
// //       );
// //     }
// //   }

// //   // ============================================
// //   // OPEN CREATE MODAL
// //   // ============================================
// //   openCreateModal(): void {
// //     this.isEditMode = false;
// //     this.selectedUser = null;
// //     this.userForm.reset();
// //     this.userForm.patchValue({ 
// //       companyName: this.companyName,
// //       reportsToIds: []
// //     });
    
// //     // Make password required for create
// //     this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
// //     this.userForm.get('password')?.updateValueAndValidity();
    
// //     this.potentialManagers = [];
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //     this.showModal = true;
// //   }

// //   // ============================================
// //   // OPEN EDIT MODAL
// //   // ============================================
// //   openEditModal(user: any): void {
// //     this.isEditMode = true;
// //     this.selectedUser = user;
    
// //     // Extract manager IDs from reportsTo array
// //     let managerIds: number[] = [];
// //     if (user.reportsTo && Array.isArray(user.reportsTo)) {
// //       managerIds = user.reportsTo.map((m: any) => m.id);
// //     } else if (user.reportsToId) {
// //       managerIds = [user.reportsToId];
// //     }
    
// //     this.userForm.patchValue({
// //       email: user.email,
// //       fullName: user.fullName,
// //       designation: user.designation,
// //       phone: user.phone,
// //       hierarchyLevelId: user.hierarchyLevelId,
// //       reportsToIds: managerIds,
// //       companyName: user.companyName
// //     });
    
// //     // Trigger potential managers update
// //     this.updatePotentialManagers();
    
// //     // Make password optional for edit
// //     this.userForm.get('password')?.clearValidators();
// //     this.userForm.get('password')?.updateValueAndValidity();
    
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //     this.showModal = true;
// //   }

// //   // ============================================
// //   // ✅ FIXED: SUBMIT USER (CREATE/UPDATE)
// //   // ============================================
// //   submitUser(): void {
// //     if (this.userForm.invalid) {
// //       this.userForm.markAllAsTouched();
// //       return;
// //     }

// //     this.isSubmitting = true;
// //     this.errorMessage = null;
// //     this.successMessage = null;

// //     const userData = { ...this.userForm.value };
    
// //     // ✅ CRITICAL FIX: Convert hierarchyLevelId to number
// //     if (userData.hierarchyLevelId) {
// //       userData.hierarchyLevelId = Number(userData.hierarchyLevelId);
// //     }
    
// //     // ✅ CRITICAL FIX: Convert all manager IDs to numbers
// //     if (userData.reportsToIds && Array.isArray(userData.reportsToIds)) {
// //       userData.reportsToIds = userData.reportsToIds.map((id: any) => Number(id));
// //     } else {
// //       userData.reportsToIds = [];
// //     }
    
// //     // Remove empty password for edit
// //     if (this.isEditMode && !userData.password) {
// //       delete userData.password;
// //     }

// //     console.log('📤 Submitting user data:', userData);
// //     console.log('   hierarchyLevelId type:', typeof userData.hierarchyLevelId);
// //     console.log('   reportsToIds types:', userData.reportsToIds.map((id: any) => typeof id));

// //     if (this.isEditMode) {
// //       // Update existing user
// //       this.dataService.updateHierarchyUser(this.selectedUser.id, userData).subscribe({
// //         next: (response: any) => {
// //           console.log('✅ User Updated:', response);
// //           this.isSubmitting = false;

// //           if (response?.success) {
// //             this.successMessage = 'User updated successfully!';
// //             setTimeout(() => {
// //               this.closeModal();
// //               this.loadHierarchyUsers();
// //             }, 1000);
// //           } else {
// //             this.errorMessage = response?.message || 'Failed to update user';
// //           }
// //         },
// //         error: (error: any) => {
// //           this.isSubmitting = false;
// //           console.error('❌ Error updating user:', error);
// //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update user';
// //         }
// //       });
// //     } else {
// //       // Create new user
// //       this.dataService.createHierarchyUser(userData).subscribe({
// //         next: (response: any) => {
// //           console.log('✅ User Created:', response);
// //           this.isSubmitting = false;

// //           if (response?.success) {
// //             this.successMessage = 'User created successfully!';
// //             setTimeout(() => {
// //               this.closeModal();
// //               this.loadHierarchyUsers();
// //             }, 1000);
// //           } else {
// //             this.errorMessage = response?.message || 'Failed to create user';
// //           }
// //         },
// //         error: (error: any) => {
// //           this.isSubmitting = false;
// //           console.error('❌ Error creating user:', error);
// //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create user';
// //         }
// //       });
// //     }
// //   }

// //   // ============================================
// //   // TOGGLE USER STATUS
// //   // ============================================
// //   toggleUserStatus(user: any): void {
// //     const action = user.isActive ? 'deactivate' : 'activate';
// //     const confirmMsg = `Are you sure you want to ${action} "${user.fullName}"?`;

// //     if (!confirm(confirmMsg)) {
// //       return;
// //     }

// //     const apiCall = user.isActive
// //       ? this.dataService.deactivateHierarchyUser(user.id)
// //       : this.dataService.activateHierarchyUser(user.id);

// //     apiCall.subscribe({
// //       next: (response: any) => {
// //         console.log(`✅ User ${action}d:`, response);
// //         if (response?.success) {
// //           this.loadHierarchyUsers();
// //         } else {
// //           alert(response?.message || `Failed to ${action} user`);
// //         }
// //       },
// //       error: (error: any) => {
// //         console.error(`❌ Error ${action}ing user:`, error);
// //         alert(error.error?.message || `Failed to ${action} user`);
// //       }
// //     });
// //   }

// //   // ============================================
// //   // CONFIRM DELETE
// //   // ============================================
// //   confirmDelete(user: any): void {
// //     this.selectedUser = user;
// //     this.showDeleteModal = true;
// //   }

// //   // ============================================
// //   // DELETE USER
// //   // ============================================
// //   deleteUser(): void {
// //     if (!this.selectedUser) return;

// //     this.isDeleting = true;

// //     this.dataService.deleteHierarchyUser(this.selectedUser.id).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ User Deleted:', response);
// //         this.isDeleting = false;

// //         if (response?.success) {
// //           this.showDeleteModal = false;
// //           this.loadHierarchyUsers();
// //         } else {
// //           alert(response?.message || 'Failed to delete user');
// //         }
// //       },
// //       error: (error: any) => {
// //         this.isDeleting = false;
// //         console.error('❌ Error deleting user:', error);
// //         alert(error.error?.message || 'Failed to delete user');
// //       }
// //     });
// //   }

// //   // ============================================
// //   // CLOSE MODAL
// //   // ============================================
// //   closeModal(): void {
// //     this.showModal = false;
// //     this.userForm.reset();
// //     this.potentialManagers = [];
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //   }

// //   onModalClose(visible: boolean): void {
// //     if (!visible) {
// //       this.closeModal();
// //     }
// //   }

// //   // ============================================
// //   // UTILITY METHODS
// //   // ============================================
// //   getLevelBadgeColor(order: number): string {
// //     if (order <= 10) return 'danger';
// //     if (order <= 20) return 'warning';
// //     if (order <= 30) return 'info';
// //     return 'secondary';
// //   }

// //   formatManagers(managers: any[]): string {
// //     if (!managers || managers.length === 0) {
// //       return 'None';
// //     }
// //     return managers.map(m => m.fullName).join(', ');
// //   }

// //   // ============================================
// //   // ✅ Check if manager is selected in form
// //   // ============================================
// //   isManagerSelectedInForm(managerId: number): boolean {
// //     const currentSelection = this.userForm.get('reportsToIds')?.value || [];
// //     return currentSelection.includes(managerId);
// //   }

// //   // ============================================
// //   // ✅ FIXED: Toggle manager selection (convert to number)
// //   // ============================================
// //   toggleManagerInForm(managerId: number): void {
// //     const currentSelection: number[] = this.userForm.get('reportsToIds')?.value || [];
    
// //     // ✅ Convert to number for comparison
// //     const managerIdNumber = Number(managerId);
// //     const index = currentSelection.indexOf(managerIdNumber);
    
// //     if (index > -1) {
// //       // Remove manager
// //       currentSelection.splice(index, 1);
// //     } else {
// //       // Add manager (as number)
// //       currentSelection.push(managerIdNumber);
// //     }
    
// //     // Update form control
// //     this.userForm.patchValue({
// //       reportsToIds: [...currentSelection]
// //     });
    
// //     console.log('✅ Updated managers selection:', currentSelection);
// //     console.log('   Types:', currentSelection.map(id => typeof id));
// //   }
// // }

// import { Component, OnInit, ViewChild } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';
// import { DataService } from '../../../shared/service/DataService';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardComponent,
//   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
//   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//   TooltipDirective
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';

// @Component({
//   selector: 'app-hierarchy-user-management',
//   templateUrl: './hierarchy-user-management.component.html',
//   styleUrls: ['./hierarchy-user-management.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, ReactiveFormsModule, FormsModule,
//     ContainerComponent, RowComponent, ColComponent, CardComponent,
//     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
//     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//     TooltipDirective, IconDirective
//   ]
// })
// export class HierarchyUserManagementComponent implements OnInit {

//   @ViewChild('userModal') userModal!: ModalComponent;
//   @ViewChild('deleteModal') deleteModal!: ModalComponent;

//   hierarchyUsers: any[] = [];
//   filteredUsers: any[] = [];
//   hierarchyLevels: any[] = [];
//   potentialManagers: any[] = [];
//   userForm!: FormGroup;
//   companyName: string = '';
//   fullName: string = '';
//   email: string = '';
//   role: string = 'Super Administrator';
//   selectedLevelFilter: string = '';
  
//   isLoading: boolean = false;
//   isSubmitting: boolean = false;
//   isDeleting: boolean = false;
//   showModal: boolean = false;
//   showDeleteModal: boolean = false;
//   isEditMode: boolean = false;
//   selectedUser: any = null;
//   errorMessage: string | null = null;
//   successMessage: string | null = null;

//   constructor(
//     private fb: FormBuilder,
//     private dataService: DataService
//   ) {
//     this.initForm();
//   }

//   ngOnInit(): void {
//     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
//     this.fullName = localStorage.getItem('fullName') || 'SuperAdmin';
//     this.email = localStorage.getItem('email') || 'admin@company.com';
//     this.loadHierarchyLevels();
//     this.loadHierarchyUsers();
//   }

//   initForm(): void {
//     this.userForm = this.fb.group({
//       email: ['', [Validators.required, Validators.email]],
//       fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
//       designation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
//       phone: [''],
//       hierarchyLevelId: ['', Validators.required],
//       reportsToIds: [[]],
//       password: [''],
//       companyName: [this.companyName]
//     });

//     this.userForm.get('hierarchyLevelId')?.valueChanges.subscribe(() => {
//       this.updatePotentialManagers();
//     });
//   }

//   loadHierarchyLevels(): void {
//     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         if (response?.success && response?.data) {
//           this.hierarchyLevels = response.data;
//           console.log('✅ Loaded hierarchy levels:', this.hierarchyLevels.length);
//         }
//       },
//       error: (error: any) => {
//         console.error('❌ Error loading hierarchy levels:', error);
//       }
//     });
//   }

//   loadHierarchyUsers(): void {
//     this.isLoading = true;
//     this.errorMessage = null;

//     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         console.log('✅ Full Hierarchy Users Response:', response);
//         this.isLoading = false;

//         if (response?.success && response?.data) {
//           this.hierarchyUsers = response.data;
//           this.filteredUsers = [...this.hierarchyUsers];
          
//           this.hierarchyUsers.forEach(user => {
//             console.log(`👤 User: ${user.fullName}`);
//             console.log('   reportsTo:', user.reportsTo);
//           });
          
//           console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
//         } else {
//           this.hierarchyUsers = [];
//           this.filteredUsers = [];
//           this.errorMessage = response?.message || 'Failed to load users';
//         }
//       },
//       error: (error: any) => {
//         this.isLoading = false;
//         console.error('❌ Error loading hierarchy users:', error);
//         this.errorMessage = error.error?.message || 'Failed to load users';
//       }
//     });
//   }

//   updatePotentialManagers(): void {
//     const selectedLevelId = this.userForm.get('hierarchyLevelId')?.value;
    
//     if (!selectedLevelId) {
//       this.potentialManagers = [];
//       return;
//     }

//     const levelIdNumber = Number(selectedLevelId);
//     const selectedLevel = this.hierarchyLevels.find(l => l.id === levelIdNumber);
    
//     if (!selectedLevel) {
//       this.potentialManagers = [];
//       return;
//     }

//     this.potentialManagers = this.hierarchyUsers.filter(user => {
//       if (this.isEditMode && this.selectedUser && user.id === this.selectedUser.id) {
//         return false;
//       }
//       return user.hierarchyLevelOrder < selectedLevel.levelOrder;
//     });

//     console.log('✅ Updated potential managers:', this.potentialManagers.length);
//   }

//   filterByLevel(): void {
//     if (!this.selectedLevelFilter) {
//       this.filteredUsers = [...this.hierarchyUsers];
//     } else {
//       this.filteredUsers = this.hierarchyUsers.filter(
//         user => user.hierarchyLevelId?.toString() === this.selectedLevelFilter
//       );
//     }
//   }

//   openCreateModal(): void {
//     this.isEditMode = false;
//     this.selectedUser = null;
//     this.userForm.reset();
//     this.userForm.patchValue({ 
//       companyName: this.companyName,
//       reportsToIds: []
//     });
    
//     this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
//     this.userForm.get('password')?.updateValueAndValidity();
    
//     this.potentialManagers = [];
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
//   }

//   openEditModal(user: any): void {
//     this.isEditMode = true;
//     this.selectedUser = user;
    
//     let managerIds: number[] = [];
//     if (user.reportsTo && Array.isArray(user.reportsTo)) {
//       managerIds = user.reportsTo.map((m: any) => m.id);
//     } else if (user.reportsToId) {
//       managerIds = [user.reportsToId];
//     }
    
//     this.userForm.patchValue({
//       email: user.email,
//       fullName: user.fullName,
//       designation: user.designation,
//       phone: user.phone,
//       hierarchyLevelId: user.hierarchyLevelId,
//       reportsToIds: managerIds,
//       companyName: user.companyName
//     });
    
//     this.updatePotentialManagers();
    
//     this.userForm.get('password')?.clearValidators();
//     this.userForm.get('password')?.updateValueAndValidity();
    
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
//   }

//   submitUser(): void {
//     if (this.userForm.invalid) {
//       this.userForm.markAllAsTouched();
//       return;
//     }

//     this.isSubmitting = true;
//     this.errorMessage = null;
//     this.successMessage = null;

//     const userData = { ...this.userForm.value };
    
//     if (userData.hierarchyLevelId) {
//       userData.hierarchyLevelId = Number(userData.hierarchyLevelId);
//     }
    
//     if (userData.reportsToIds && Array.isArray(userData.reportsToIds)) {
//       userData.reportsToIds = userData.reportsToIds.map((id: any) => Number(id));
//     } else {
//       userData.reportsToIds = [];
//     }
    
//     if (this.isEditMode && !userData.password) {
//       delete userData.password;
//     }

//     console.log('📤 Submitting user data:', userData);

//     if (this.isEditMode) {
//       this.dataService.updateHierarchyUser(this.selectedUser.id, userData).subscribe({
//         next: (response: any) => {
//           console.log('✅ User Updated:', response);
//           this.isSubmitting = false;

//           if (response?.success) {
//             this.successMessage = 'User updated successfully!';
//             setTimeout(() => {
//               this.closeModal();
//               this.loadHierarchyUsers();
//             }, 1000);
//           } else {
//             this.errorMessage = response?.message || 'Failed to update user';
//           }
//         },
//         error: (error: any) => {
//           this.isSubmitting = false;
//           console.error('❌ Error updating user:', error);
//           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update user';
//         }
//       });
//     } else {
//       this.dataService.createHierarchyUser(userData).subscribe({
//         next: (response: any) => {
//           console.log('✅ User Created:', response);
//           this.isSubmitting = false;

//           if (response?.success) {
//             this.successMessage = 'User created successfully!';
//             setTimeout(() => {
//               this.closeModal();
//               this.loadHierarchyUsers();
//             }, 1000);
//           } else {
//             this.errorMessage = response?.message || 'Failed to create user';
//           }
//         },
//         error: (error: any) => {
//           this.isSubmitting = false;
//           console.error('❌ Error creating user:', error);
//           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create user';
//         }
//       });
//     }
//   }

//   toggleUserStatus(user: any): void {
//     const action = user.isActive ? 'deactivate' : 'activate';
//     const confirmMsg = `Are you sure you want to ${action} "${user.fullName}"?`;

//     if (!confirm(confirmMsg)) return;

//     const apiCall = user.isActive
//       ? this.dataService.deactivateHierarchyUser(user.id)
//       : this.dataService.activateHierarchyUser(user.id);

//     apiCall.subscribe({
//       next: (response: any) => {
//         console.log(`✅ User ${action}d:`, response);
//         if (response?.success) {
//           this.loadHierarchyUsers();
//         } else {
//           alert(response?.message || `Failed to ${action} user`);
//         }
//       },
//       error: (error: any) => {
//         console.error(`❌ Error ${action}ing user:`, error);
//         alert(error.error?.message || `Failed to ${action} user`);
//       }
//     });
//   }

//   confirmDelete(user: any): void {
//     this.selectedUser = user;
//     this.showDeleteModal = true;
//   }

//   deleteUser(): void {
//     if (!this.selectedUser) return;

//     this.isDeleting = true;

//     this.dataService.deleteHierarchyUser(this.selectedUser.id).subscribe({
//       next: (response: any) => {
//         console.log('✅ User Deleted:', response);
//         this.isDeleting = false;

//         if (response?.success) {
//           this.showDeleteModal = false;
//           this.loadHierarchyUsers();
//         } else {
//           alert(response?.message || 'Failed to delete user');
//         }
//       },
//       error: (error: any) => {
//         this.isDeleting = false;
//         console.error('❌ Error deleting user:', error);
//         alert(error.error?.message || 'Failed to delete user');
//       }
//     });
//   }

//   closeModal(): void {
//     this.showModal = false;
//     this.userForm.reset();
//     this.potentialManagers = [];
//     this.errorMessage = null;
//     this.successMessage = null;
//   }

//   onModalClose(visible: boolean): void {
//     if (!visible) {
//       this.closeModal();
//     }
//   }

//   getLevelBadgeColor(order: number): string {
//     if (order <= 10) return 'danger';
//     if (order <= 20) return 'warning';
//     if (order <= 30) return 'info';
//     return 'secondary';
//   }

//   formatManagers(managers: any[]): string {
//     if (!managers || managers.length === 0) return 'None';
//     return managers.map(m => m.fullName).join(', ');
//   }

//   isManagerSelectedInForm(managerId: number): boolean {
//     const currentSelection = this.userForm.get('reportsToIds')?.value || [];
//     return currentSelection.includes(managerId);
//   }

//   toggleManagerInForm(managerId: number): void {
//     const currentSelection: number[] = this.userForm.get('reportsToIds')?.value || [];
    
//     const managerIdNumber = Number(managerId);
//     const index = currentSelection.indexOf(managerIdNumber);
    
//     if (index > -1) {
//       currentSelection.splice(index, 1);
//     } else {
//       currentSelection.push(managerIdNumber);
//     }
    
//     this.userForm.patchValue({
//       reportsToIds: [...currentSelection]
//     });
    
//     console.log('✅ Updated managers selection:', currentSelection);
//   }

//   getInitials(name: string): string {
//     if (!name) return 'NA';
//     const parts = name.split(' ');
//     if (parts.length >= 2) {
//       return (parts[0][0] + parts[1][0]).toUpperCase();
//     }
//     return name.substring(0, 2).toUpperCase();
//   }
// }

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DataService } from '../../../shared/service/DataService';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
  ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
  ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
  TooltipDirective, ButtonCloseDirective
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-hierarchy-user-management',
  templateUrl: './hierarchy-user-management.component.html',
  styleUrls: ['./hierarchy-user-management.component.css'],
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule,
    ContainerComponent, RowComponent, ColComponent, CardComponent,
    CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
    ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
    ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
    TooltipDirective, IconDirective, ButtonCloseDirective
  ]
})
export class HierarchyUserManagementComponent implements OnInit {

  @ViewChild('userModal') userModal!: ModalComponent;
  @ViewChild('deleteModal') deleteModal!: ModalComponent;

  hierarchyUsers: any[] = [];
  filteredUsers: any[] = [];
  hierarchyLevels: any[] = [];
  potentialManagers: any[] = [];
  userForm!: FormGroup;
  companyName: string = '';
  fullName: string = '';
  email: string = '';
  role: string = 'Super Administrator';
  selectedLevelFilter: string = '';
  
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isDeleting: boolean = false;
  showModal: boolean = false;
  showDeleteModal: boolean = false;
  isEditMode: boolean = false;
  selectedUser: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dataService: DataService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
    this.fullName = localStorage.getItem('fullName') || 'SuperAdmin';
    this.email = localStorage.getItem('email') || 'admin@company.com';
    this.loadHierarchyLevels();
    this.loadHierarchyUsers();
  }

  initForm(): void {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      designation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      phone: [''],
      hierarchyLevelId: ['', Validators.required],
      reportsToIds: [[]],
      password: [''],
      companyName: ['']
    });

    this.userForm.get('hierarchyLevelId')?.valueChanges.subscribe(() => {
      this.updatePotentialManagers();
    });
  }

  loadHierarchyLevels(): void {
    this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
      next: (response: any) => {
        if (response?.success && response?.data) {
          this.hierarchyLevels = response.data;
          console.log('✅ Loaded hierarchy levels:', this.hierarchyLevels.length);
        }
      },
      error: (error: any) => {
        console.error('❌ Error loading hierarchy levels:', error);
      }
    });
  }

  loadHierarchyUsers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
      next: (response: any) => {
        console.log('✅ Full Hierarchy Users Response:', response);
        this.isLoading = false;

        if (response?.success && response?.data) {
          this.hierarchyUsers = response.data;
          this.filteredUsers = [...this.hierarchyUsers];
          
          this.hierarchyUsers.forEach(user => {
            console.log(`👤 User: ${user.fullName}`);
            console.log('   reportsTo:', user.reportsTo);
          });
          
          console.log(`✅ Loaded ${this.hierarchyUsers.length} hierarchy users`);
        } else {
          this.hierarchyUsers = [];
          this.filteredUsers = [];
          this.errorMessage = response?.message || 'Failed to load users';
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('❌ Error loading hierarchy users:', error);
        this.errorMessage = error.error?.message || 'Failed to load users';
      }
    });
  }

  updatePotentialManagers(): void {
    const selectedLevelId = this.userForm.get('hierarchyLevelId')?.value;
    
    if (!selectedLevelId) {
      this.potentialManagers = [];
      return;
    }

    const levelIdNumber = Number(selectedLevelId);
    const selectedLevel = this.hierarchyLevels.find(l => l.id === levelIdNumber);
    
    if (!selectedLevel) {
      this.potentialManagers = [];
      return;
    }

    this.potentialManagers = this.hierarchyUsers.filter(user => {
      if (this.isEditMode && this.selectedUser && user.id === this.selectedUser.id) {
        return false;
      }
      return user.hierarchyLevelOrder < selectedLevel.levelOrder;
    });

    console.log('✅ Updated potential managers:', this.potentialManagers.length);
  }

  filterByLevel(): void {
    if (!this.selectedLevelFilter) {
      this.filteredUsers = [...this.hierarchyUsers];
    } else {
      this.filteredUsers = this.hierarchyUsers.filter(
        user => user.hierarchyLevelId?.toString() === this.selectedLevelFilter
      );
    }
  }

  openCreateModal(): void {
    console.log('🔵 Opening CREATE USER modal...');
    
    this.isEditMode = false;
    this.selectedUser = null;
    
    this.userForm.reset();
    
    this.userForm.patchValue({ 
      companyName: this.companyName,
      reportsToIds: [],
      email: '',
      fullName: '',
      designation: '',
      phone: '',
      hierarchyLevelId: '',
      password: ''
    });
    
    this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('password')?.updateValueAndValidity();
    
    this.potentialManagers = [];
    
    this.errorMessage = null;
    this.successMessage = null;
    
    this.showModal = true;
    
    console.log('✅ User Modal opened:', {
      showModal: this.showModal,
      isEditMode: this.isEditMode,
      formValue: this.userForm.value
    });
  }

  openEditModal(user: any): void {
    console.log('🟡 Opening EDIT USER modal for:', user);
    
    this.isEditMode = true;
    this.selectedUser = user;
    
    let managerIds: number[] = [];
    if (user.reportsTo && Array.isArray(user.reportsTo)) {
      managerIds = user.reportsTo.map((m: any) => m.id);
    } else if (user.reportsToId) {
      managerIds = [user.reportsToId];
    }
    
    this.userForm.patchValue({
      email: user.email,
      fullName: user.fullName,
      designation: user.designation,
      phone: user.phone,
      hierarchyLevelId: user.hierarchyLevelId,
      reportsToIds: managerIds,
      companyName: user.companyName
    });
    
    this.updatePotentialManagers();
    
    this.userForm.get('password')?.clearValidators();
    this.userForm.get('password')?.updateValueAndValidity();
    
    this.errorMessage = null;
    this.successMessage = null;
    this.showModal = true;
  }

  submitUser(): void {
    console.log('📤 Submit User Called');
    console.log('Form Valid:', this.userForm.valid);
    console.log('Form Value:', this.userForm.value);
    console.log('Is Edit Mode:', this.isEditMode);

    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      console.log('❌ Form is invalid');
      console.log('Form Errors:', this.getFormValidationErrors());
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const userData = { ...this.userForm.value };
    
    if (userData.hierarchyLevelId) {
      userData.hierarchyLevelId = Number(userData.hierarchyLevelId);
    }
    
    if (userData.reportsToIds && Array.isArray(userData.reportsToIds)) {
      userData.reportsToIds = userData.reportsToIds.map((id: any) => Number(id));
    } else {
      userData.reportsToIds = [];
    }
    
    if (this.isEditMode && !userData.password) {
      delete userData.password;
    }

    console.log('📤 Submitting user data:', userData);

    if (this.isEditMode) {
      console.log('🔄 Updating user:', this.selectedUser.id);
      this.dataService.updateHierarchyUser(this.selectedUser.id, userData).subscribe({
        next: (response: any) => {
          console.log('✅ User Updated:', response);
          this.isSubmitting = false;

          if (response?.success) {
            this.successMessage = 'User updated successfully!';
            setTimeout(() => {
              this.closeModal();
              this.loadHierarchyUsers();
            }, 1000);
          } else {
            this.errorMessage = response?.message || 'Failed to update user';
          }
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('❌ Error updating user:', error);
          this.errorMessage = error.error?.error || error.error?.message || 'Failed to update user';
        }
      });
    } else {
      console.log('➕ Creating new user');
      this.dataService.createHierarchyUser(userData).subscribe({
        next: (response: any) => {
          console.log('✅ User Created:', response);
          this.isSubmitting = false;

          if (response?.success) {
            this.successMessage = 'User created successfully!';
            setTimeout(() => {
              this.closeModal();
              this.loadHierarchyUsers();
            }, 1000);
          } else {
            this.errorMessage = response?.message || 'Failed to create user';
          }
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('❌ Error creating user:', error);
          this.errorMessage = error.error?.error || error.error?.message || 'Failed to create user';
        }
      });
    }
  }

  getFormValidationErrors(): any {
    const errors: any = {};
    Object.keys(this.userForm.controls).forEach(key => {
      const controlErrors = this.userForm.get(key)?.errors;
      if (controlErrors != null) {
        errors[key] = controlErrors;
      }
    });
    return errors;
  }

  toggleUserStatus(user: any): void {
    const action = user.isActive ? 'deactivate' : 'activate';
    const confirmMsg = `Are you sure you want to ${action} "${user.fullName}"?`;

    if (!confirm(confirmMsg)) return;

    const apiCall = user.isActive
      ? this.dataService.deactivateHierarchyUser(user.id)
      : this.dataService.activateHierarchyUser(user.id);

    apiCall.subscribe({
      next: (response: any) => {
        console.log(`✅ User ${action}d:`, response);
        if (response?.success) {
          this.loadHierarchyUsers();
        } else {
          alert(response?.message || `Failed to ${action} user`);
        }
      },
      error: (error: any) => {
        console.error(`❌ Error ${action}ing user:`, error);
        alert(error.error?.message || `Failed to ${action} user`);
      }
    });
  }

  confirmDelete(user: any): void {
    this.selectedUser = user;
    this.showDeleteModal = true;
  }

  deleteUser(): void {
    if (!this.selectedUser) return;

    this.isDeleting = true;

    this.dataService.deleteHierarchyUser(this.selectedUser.id).subscribe({
      next: (response: any) => {
        console.log('✅ User Deleted:', response);
        this.isDeleting = false;

        if (response?.success) {
          this.showDeleteModal = false;
          this.loadHierarchyUsers();
        } else {
          alert(response?.message || 'Failed to delete user');
        }
      },
      error: (error: any) => {
        this.isDeleting = false;
        console.error('❌ Error deleting user:', error);
        alert(error.error?.message || 'Failed to delete user');
      }
    });
  }

  closeModal(): void {
    console.log('🔴 Closing user modal');
    this.showModal = false;
    this.userForm.reset();
    this.potentialManagers = [];
    this.errorMessage = null;
    this.successMessage = null;
    this.isEditMode = false;
    this.selectedUser = null;
  }

  onModalClose(visible: boolean): void {
    if (!visible) {
      this.closeModal();
    }
  }

  getLevelBadgeColor(order: number): string {
    if (order <= 10) return 'danger';
    if (order <= 20) return 'warning';
    if (order <= 30) return 'info';
    return 'secondary';
  }

  formatManagers(managers: any[]): string {
    if (!managers || managers.length === 0) return 'None';
    return managers.map(m => m.fullName).join(', ');
  }

  isManagerSelectedInForm(managerId: number): boolean {
    const currentSelection = this.userForm.get('reportsToIds')?.value || [];
    return currentSelection.includes(managerId);
  }

  toggleManagerInForm(managerId: number): void {
    const currentSelection: number[] = this.userForm.get('reportsToIds')?.value || [];
    
    const managerIdNumber = Number(managerId);
    const index = currentSelection.indexOf(managerIdNumber);
    
    if (index > -1) {
      currentSelection.splice(index, 1);
    } else {
      currentSelection.push(managerIdNumber);
    }
    
    this.userForm.patchValue({
      reportsToIds: [...currentSelection]
    });
    
    console.log('✅ Updated managers selection:', currentSelection);
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}