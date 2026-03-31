// // // import { Component, OnInit, ViewChild } from '@angular/core';
// // // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // // import { CommonModule } from '@angular/common';
// // // import { DataService } from '../../../shared/service/DataService';
// // // import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
// // // import {
// // //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // //   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // //   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // //   TooltipDirective
// // // } from '@coreui/angular';
// // // import { IconDirective } from '@coreui/icons-angular';

// // // @Component({
// // //   selector: 'app-hierarchy-level-management',
// // //   templateUrl: './hierarchy-level-management.component.html',
// // //   styleUrls: ['./hierarchy-level-management.component.css'],
// // //   standalone: true,
// // //   imports: [
// // //     CommonModule, ReactiveFormsModule, DragDropModule,
// // //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// // //     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// // //     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// // //     TooltipDirective, IconDirective
// // //   ]
// // // })
// // // export class HierarchyLevelManagementComponent implements OnInit {

// // //   @ViewChild('levelModal') levelModal!: ModalComponent;
// // //   @ViewChild('deleteModal') deleteModal!: ModalComponent;

// // //   hierarchyLevels: any[] = [];
// // //   levelForm!: FormGroup;
// // //   companyName: string = '';
// // //   isLoading: boolean = false;
// // //   isSubmitting: boolean = false;
// // //   isDeleting: boolean = false;
// // //   showModal: boolean = false;
// // //   showDeleteModal: boolean = false;
// // //   isEditMode: boolean = false;
// // //   selectedLevel: any = null;
// // //   errorMessage: string | null = null;
// // //   successMessage: string | null = null;

// // //   constructor(
// // //     private fb: FormBuilder,
// // //     private dataService: DataService
// // //   ) {
// // //     this.initForm();
// // //   }

// // //   ngOnInit(): void {
// // //     // Get company name from localStorage
// // //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// // //     this.loadHierarchyLevels();
// // //   }

// // //   // ============================================
// // //   // INITIALIZE FORM
// // //   // ============================================
// // //   initForm(): void {
// // //     this.levelForm = this.fb.group({
// // //       levelName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// // //       levelOrder: ['', [Validators.required, Validators.min(1)]],
// // //       description: ['', [Validators.maxLength(500)]],
// // //       companyName: [this.companyName]
// // //     });
// // //   }

// // //   // ============================================
// // //   // LOAD HIERARCHY LEVELS
// // //   // ============================================
// // //   loadHierarchyLevels(): void {
// // //     this.isLoading = true;
// // //     this.errorMessage = null;

// // //     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ Hierarchy Levels Response:', response);
// // //         this.isLoading = false;

// // //         if (response?.success && response?.data) {
// // //           this.hierarchyLevels = response.data;
// // //           console.log(`✅ Loaded ${this.hierarchyLevels.length} hierarchy levels`);
// // //         } else {
// // //           this.hierarchyLevels = [];
// // //           this.errorMessage = response?.message || 'Failed to load hierarchy levels';
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         this.isLoading = false;
// // //         console.error('❌ Error loading hierarchy levels:', error);
// // //         this.errorMessage = error.error?.message || 'Failed to load hierarchy levels';
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // OPEN CREATE MODAL
// // //   // ============================================
// // //   openCreateModal(): void {
// // //     this.isEditMode = false;
// // //     this.selectedLevel = null;
// // //     this.levelForm.reset();
// // //     this.levelForm.patchValue({ companyName: this.companyName });
// // //     this.errorMessage = null;
// // //     this.successMessage = null;
// // //     this.showModal = true;
// // //   }

// // //   // ============================================
// // //   // OPEN EDIT MODAL
// // //   // ============================================
// // //   openEditModal(level: any): void {
// // //     this.isEditMode = true;
// // //     this.selectedLevel = level;
// // //     this.levelForm.patchValue({
// // //       levelName: level.levelName,
// // //       levelOrder: level.levelOrder,
// // //       description: level.description,
// // //       companyName: level.companyName
// // //     });
// // //     this.errorMessage = null;
// // //     this.successMessage = null;
// // //     this.showModal = true;
// // //   }

// // //   // ============================================
// // //   // SUBMIT LEVEL (CREATE/UPDATE)
// // //   // ============================================
// // //   submitLevel(): void {
// // //     if (this.levelForm.invalid) {
// // //       this.levelForm.markAllAsTouched();
// // //       return;
// // //     }

// // //     this.isSubmitting = true;
// // //     this.errorMessage = null;
// // //     this.successMessage = null;

// // //     const levelData = this.levelForm.value;

// // //     if (this.isEditMode) {
// // //       // Update existing level
// // //       this.dataService.updateHierarchyLevel(this.selectedLevel.id, levelData).subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ Level Updated:', response);
// // //           this.isSubmitting = false;

// // //           if (response?.success) {
// // //             this.successMessage = 'Hierarchy level updated successfully!';
// // //             setTimeout(() => {
// // //               this.closeModal();
// // //               this.loadHierarchyLevels();
// // //             }, 1000);
// // //           } else {
// // //             this.errorMessage = response?.message || 'Failed to update level';
// // //           }
// // //         },
// // //         error: (error: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Error updating level:', error);
// // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update level';
// // //         }
// // //       });
// // //     } else {
// // //       // Create new level
// // //       this.dataService.createHierarchyLevel(levelData).subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ Level Created:', response);
// // //           this.isSubmitting = false;

// // //           if (response?.success) {
// // //             this.successMessage = 'Hierarchy level created successfully!';
// // //             setTimeout(() => {
// // //               this.closeModal();
// // //               this.loadHierarchyLevels();
// // //             }, 1000);
// // //           } else {
// // //             this.errorMessage = response?.message || 'Failed to create level';
// // //           }
// // //         },
// // //         error: (error: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Error creating level:', error);
// // //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create level';
// // //         }
// // //       });
// // //     }
// // //   }

// // //   // ============================================
// // //   // TOGGLE LEVEL STATUS (ACTIVATE/DEACTIVATE)
// // //   // ============================================
// // //   toggleLevelStatus(level: any): void {
// // //     const action = level.isActive ? 'deactivate' : 'activate';
// // //     const confirmMsg = `Are you sure you want to ${action} the level "${level.levelName}"?`;

// // //     if (!confirm(confirmMsg)) {
// // //       return;
// // //     }

// // //     const apiCall = level.isActive
// // //       ? this.dataService.deactivateHierarchyLevel(level.id)
// // //       : this.dataService.activateHierarchyLevel(level.id);

// // //     apiCall.subscribe({
// // //       next: (response: any) => {
// // //         console.log(`✅ Level ${action}d:`, response);
// // //         if (response?.success) {
// // //           this.loadHierarchyLevels();
// // //         } else {
// // //           alert(response?.message || `Failed to ${action} level`);
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         console.error(`❌ Error ${action}ing level:`, error);
// // //         alert(error.error?.message || `Failed to ${action} level`);
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // CONFIRM DELETE
// // //   // ============================================
// // //   confirmDelete(level: any): void {
// // //     this.selectedLevel = level;
// // //     this.showDeleteModal = true;
// // //   }

// // //   // ============================================
// // //   // DELETE LEVEL
// // //   // ============================================
// // //   deleteLevel(): void {
// // //     if (!this.selectedLevel) return;

// // //     this.isDeleting = true;

// // //     this.dataService.deleteHierarchyLevel(this.selectedLevel.id).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ Level Deleted:', response);
// // //         this.isDeleting = false;

// // //         if (response?.success) {
// // //           this.showDeleteModal = false;
// // //           this.loadHierarchyLevels();
// // //         } else {
// // //           alert(response?.message || 'Failed to delete level');
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         this.isDeleting = false;
// // //         console.error('❌ Error deleting level:', error);
// // //         alert(error.error?.message || 'Failed to delete level');
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // DRAG & DROP REORDER
// // //   // ============================================
// // //   onDrop(event: CdkDragDrop<any[]>): void {
// // //     moveItemInArray(this.hierarchyLevels, event.previousIndex, event.currentIndex);

// // //     // Get IDs in new order
// // //     const levelIds = this.hierarchyLevels.map(level => level.id);

// // //     // Save new order to backend
// // //     this.dataService.reorderHierarchyLevels(this.companyName, levelIds).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ Levels Reordered:', response);
// // //         if (response?.success) {
// // //           this.loadHierarchyLevels();
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         console.error('❌ Error reordering levels:', error);
// // //         alert('Failed to save new order');
// // //         this.loadHierarchyLevels(); // Reload to reset
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // CLOSE MODAL
// // //   // ============================================
// // //   closeModal(): void {
// // //     this.showModal = false;
// // //     this.levelForm.reset();
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
// // //     if (order === 1) return 'danger'; // Highest
// // //     if (order === 2) return 'warning';
// // //     if (order === 3) return 'info';
// // //     return 'secondary';
// // //   }

// // //   formatDate(dateStr: string): string {
// // //     if (!dateStr) return 'N/A';
// // //     const date = new Date(dateStr);
// // //     return date.toLocaleDateString('en-US', { 
// // //       year: 'numeric', 
// // //       month: 'short', 
// // //       day: 'numeric' 
// // //     });
// // //   }
// // // }

// // // ============================================
// // // Updated hierarchy-level-management.component.ts
// // // ✅ Level ordering with increments of 10
// // // ✅ Suggests next available level order
// // // ============================================

// // import { Component, OnInit, ViewChild } from '@angular/core';
// // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // import { CommonModule } from '@angular/common';
// // import { DataService } from '../../../shared/service/DataService';
// // import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
// // import {
// //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// //   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// //   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// //   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// //   TooltipDirective
// // } from '@coreui/angular';
// // import { IconDirective } from '@coreui/icons-angular';

// // @Component({
// //   selector: 'app-hierarchy-level-management',
// //   templateUrl: './hierarchy-level-management.component.html',
// //   styleUrls: ['./hierarchy-level-management.component.css'],
// //   standalone: true,
// //   imports: [
// //     CommonModule, ReactiveFormsModule, DragDropModule,
// //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// //     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
// //     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
// //     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
// //     TooltipDirective, IconDirective
// //   ]
// // })
// // export class HierarchyLevelManagementComponent implements OnInit {

// //   @ViewChild('levelModal') levelModal!: ModalComponent;
// //   @ViewChild('deleteModal') deleteModal!: ModalComponent;

// //   hierarchyLevels: any[] = [];
// //   levelForm!: FormGroup;
// //   companyName: string = '';
// //   isLoading: boolean = false;
// //   isSubmitting: boolean = false;
// //   isDeleting: boolean = false;
// //   showModal: boolean = false;
// //   showDeleteModal: boolean = false;
// //   isEditMode: boolean = false;
// //   selectedLevel: any = null;
// //   errorMessage: string | null = null;
// //   successMessage: string | null = null;
// //   suggestedLevelOrder: number = 10; // ✅ NEW

// //   constructor(
// //     private fb: FormBuilder,
// //     private dataService: DataService
// //   ) {
// //     this.initForm();
// //   }

// //   ngOnInit(): void {
// //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// //       this.loadHierarchyLevels();
// //   this.loadHierarchyUsers();
// //   }

// //   // ============================================
// //   // INITIALIZE FORM
// //   // ============================================
// //   initForm(): void {
// //     this.levelForm = this.fb.group({
// //       levelName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
// //       levelOrder: [10, [Validators.required, Validators.min(1)]], // ✅ Default to 10
// //       description: ['', [Validators.maxLength(500)]],
// //       companyName: [this.companyName]
// //     });
// //   }

// //   // ============================================
// //   // LOAD HIERARCHY LEVELS
// //   // ============================================
// //   loadHierarchyLevels(): void {
// //     this.isLoading = true;
// //     this.errorMessage = null;

// //     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ Hierarchy Levels Response:', response);
// //         this.isLoading = false;

// //         if (response?.success && response?.data) {
// //           this.hierarchyLevels = response.data;
// //           console.log(`✅ Loaded ${this.hierarchyLevels.length} hierarchy levels`);
          
// //           // ✅ Calculate suggested next level order
// //           this.calculateSuggestedLevelOrder();
// //         } else {
// //           this.hierarchyLevels = [];
// //           this.errorMessage = response?.message || 'Failed to load hierarchy levels';
// //         }
// //       },
// //       error: (error: any) => {
// //         this.isLoading = false;
// //         console.error('❌ Error loading hierarchy levels:', error);
// //         this.errorMessage = error.error?.message || 'Failed to load hierarchy levels';
// //       }
// //     });
// //   }

// //   getLevelGradient(levelOrder: number): string {
// //   if (levelOrder <= 10) {
// //     return 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)'; // CEO - Red
// //   }
// //   if (levelOrder <= 20) {
// //     return 'linear-gradient(135deg, #ffc107 0%, #ff9800 100%)'; // COO - Orange
// //   }
// //   if (levelOrder <= 30) {
// //     return 'linear-gradient(135deg, #17a2b8 0%, #138496 100%)'; // Manager - Cyan
// //   }
// //   if (levelOrder <= 40) {
// //     return 'linear-gradient(135deg, #28a745 0%, #218838 100%)'; // Team Lead - Green
// //   }
// //   return 'linear-gradient(135deg, #6c757d 0%, #545b62 100%)'; // Others - Gray
// // }

// // // ============================================
// // // UTILITY METHOD: Get user count by level
// // // ============================================
// // getUserCountByLevel(levelId: number): number {
// //   // You need to have hierarchyUsers loaded in your component
// //   // Add this property at the top of your class if not present:
// //   // hierarchyUsers: any[] = [];
  
// //   // Then in ngOnInit or when loading levels, also load users:
// //   // this.loadHierarchyUsers();
  
// //   if (!this.hierarchyUsers || this.hierarchyUsers.length === 0) {
// //     return 0;
// //   }
  
// //   return this.hierarchyUsers.filter(user => user.hierarchyLevelId === levelId).length;
// // }

// // // ============================================
// // // METHOD: Load hierarchy users (add this if not present)
// // // ============================================
// // private hierarchyUsers: any[] = []; // Add this property to your class

// // loadHierarchyUsers(): void {
// //   this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// //     next: (response: any) => {
// //       if (response?.success && response?.data) {
// //         this.hierarchyUsers = response.data;
// //       }
// //     },
// //     error: (error: any) => {
// //       console.error('Error loading hierarchy users:', error);
// //     }
// //   });
// // }

// //   // ============================================
// //   // ✅ NEW: Calculate suggested next level order
// //   // ============================================
// //   calculateSuggestedLevelOrder(): void {
// //     if (this.hierarchyLevels.length === 0) {
// //       this.suggestedLevelOrder = 10;
// //     } else {
// //       const maxOrder = Math.max(...this.hierarchyLevels.map(l => l.levelOrder));
// //       this.suggestedLevelOrder = maxOrder + 10;
// //     }
// //   }

// //   // ============================================
// //   // OPEN CREATE MODAL
// //   // ============================================
// //   openCreateModal(): void {
// //     this.isEditMode = false;
// //     this.selectedLevel = null;
// //     this.levelForm.reset();
// //     this.levelForm.patchValue({ 
// //       companyName: this.companyName,
// //       levelOrder: this.suggestedLevelOrder // ✅ Set suggested order
// //     });
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //     this.showModal = true;
// //   }

// //   // ============================================
// //   // OPEN EDIT MODAL
// //   // ============================================
// //   openEditModal(level: any): void {
// //     this.isEditMode = true;
// //     this.selectedLevel = level;
// //     this.levelForm.patchValue({
// //       levelName: level.levelName,
// //       levelOrder: level.levelOrder,
// //       description: level.description,
// //       companyName: level.companyName
// //     });
// //     this.errorMessage = null;
// //     this.successMessage = null;
// //     this.showModal = true;
// //   }

// //   // ============================================
// //   // SUBMIT LEVEL (CREATE/UPDATE)
// //   // ============================================
// //   submitLevel(): void {
// //     if (this.levelForm.invalid) {
// //       this.levelForm.markAllAsTouched();
// //       return;
// //     }

// //     this.isSubmitting = true;
// //     this.errorMessage = null;
// //     this.successMessage = null;

// //     const levelData = this.levelForm.value;

// //     if (this.isEditMode) {
// //       // Update existing level
// //       this.dataService.updateHierarchyLevel(this.selectedLevel.id, levelData).subscribe({
// //         next: (response: any) => {
// //           console.log('✅ Level Updated:', response);
// //           this.isSubmitting = false;

// //           if (response?.success) {
// //             this.successMessage = 'Hierarchy level updated successfully!';
// //             setTimeout(() => {
// //               this.closeModal();
// //               this.loadHierarchyLevels();
// //             }, 1000);
// //           } else {
// //             this.errorMessage = response?.message || 'Failed to update level';
// //           }
// //         },
// //         error: (error: any) => {
// //           this.isSubmitting = false;
// //           console.error('❌ Error updating level:', error);
// //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update level';
// //         }
// //       });
// //     } else {
// //       // Create new level
// //       this.dataService.createHierarchyLevel(levelData).subscribe({
// //         next: (response: any) => {
// //           console.log('✅ Level Created:', response);
// //           this.isSubmitting = false;

// //           if (response?.success) {
// //             this.successMessage = 'Hierarchy level created successfully!';
// //             setTimeout(() => {
// //               this.closeModal();
// //               this.loadHierarchyLevels();
// //             }, 1000);
// //           } else {
// //             this.errorMessage = response?.message || 'Failed to create level';
// //           }
// //         },
// //         error: (error: any) => {
// //           this.isSubmitting = false;
// //           console.error('❌ Error creating level:', error);
// //           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create level';
// //         }
// //       });
// //     }
// //   }

// //   // ============================================
// //   // TOGGLE LEVEL STATUS
// //   // ============================================
// //   toggleLevelStatus(level: any): void {
// //     const action = level.isActive ? 'deactivate' : 'activate';
// //     const confirmMsg = `Are you sure you want to ${action} the level "${level.levelName}"?`;

// //     if (!confirm(confirmMsg)) {
// //       return;
// //     }

// //     const apiCall = level.isActive
// //       ? this.dataService.deactivateHierarchyLevel(level.id)
// //       : this.dataService.activateHierarchyLevel(level.id);

// //     apiCall.subscribe({
// //       next: (response: any) => {
// //         console.log(`✅ Level ${action}d:`, response);
// //         if (response?.success) {
// //           this.loadHierarchyLevels();
// //         } else {
// //           alert(response?.message || `Failed to ${action} level`);
// //         }
// //       },
// //       error: (error: any) => {
// //         console.error(`❌ Error ${action}ing level:`, error);
// //         alert(error.error?.message || `Failed to ${action} level`);
// //       }
// //     });
// //   }

// //   // ============================================
// //   // CONFIRM DELETE
// //   // ============================================
// //   confirmDelete(level: any): void {
// //     this.selectedLevel = level;
// //     this.showDeleteModal = true;
// //   }

// //   // ============================================
// //   // DELETE LEVEL (Soft Delete)
// //   // ============================================
// //   deleteLevel(): void {
// //     if (!this.selectedLevel) return;

// //     this.isDeleting = true;

// //     this.dataService.deleteHierarchyLevel(this.selectedLevel.id).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ Level Soft Deleted:', response);
// //         this.isDeleting = false;

// //         if (response?.success) {
// //           this.showDeleteModal = false;
// //           this.loadHierarchyLevels();
// //         } else {
// //           alert(response?.message || 'Failed to delete level');
// //         }
// //       },
// //       error: (error: any) => {
// //         this.isDeleting = false;
// //         console.error('❌ Error deleting level:', error);
// //         alert(error.error?.message || 'Failed to delete level');
// //       }
// //     });
// //   }

// //   // ============================================
// //   // DRAG & DROP REORDER
// //   // ============================================
// //   onDrop(event: CdkDragDrop<any[]>): void {
// //     moveItemInArray(this.hierarchyLevels, event.previousIndex, event.currentIndex);

// //     // Get IDs in new order
// //     const levelIds = this.hierarchyLevels.map(level => level.id);

// //     // Save new order to backend
// //     this.dataService.reorderHierarchyLevels(this.companyName, levelIds).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ Levels Reordered:', response);
// //         if (response?.success) {
// //           this.loadHierarchyLevels();
// //         }
// //       },
// //       error: (error: any) => {
// //         console.error('❌ Error reordering levels:', error);
// //         alert('Failed to save new order');
// //         this.loadHierarchyLevels();
// //       }
// //     });
// //   }

// //   // ============================================
// //   // CLOSE MODAL
// //   // ============================================
// //   closeModal(): void {
// //     this.showModal = false;
// //     this.levelForm.reset();
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
// //     if (order <= 10) return 'danger'; // CEO level
// //     if (order <= 20) return 'warning'; // COO level
// //     if (order <= 30) return 'info';    // Manager level
// //     return 'secondary';
// //   }

// //   formatDate(dateStr: string): string {
// //     if (!dateStr) return 'N/A';
// //     const date = new Date(dateStr);
// //     return date.toLocaleDateString('en-US', { 
// //       year: 'numeric', 
// //       month: 'short', 
// //       day: 'numeric' 
// //     });
// //   }
// // }

// import { Component, OnInit, ViewChild } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';
// import { DataService } from '../../../shared/service/DataService';
// import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardComponent,
//   CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
//   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//   TooltipDirective
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';

// @Component({
//   selector: 'app-hierarchy-level-management',
//   templateUrl: './hierarchy-level-management.component.html',
//   styleUrls: ['./hierarchy-level-management.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, ReactiveFormsModule, DragDropModule,
//     ContainerComponent, RowComponent, ColComponent, CardComponent,
//     CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
//     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//     TooltipDirective, IconDirective
//   ]
// })
// export class HierarchyLevelManagementComponent implements OnInit {

//   @ViewChild('levelModal') levelModal!: ModalComponent;
//   @ViewChild('deleteModal') deleteModal!: ModalComponent;

//   hierarchyLevels: any[] = [];
//   hierarchyUsers: any[] = [];
//   levelForm!: FormGroup;
//   companyName: string = '';
//   fullName: string = '';
//   email: string = '';
//   role: string = 'Super Administrator';
//   isLoading: boolean = false;
//   isSubmitting: boolean = false;
//   isDeleting: boolean = false;
//   showModal: boolean = false;
//   showDeleteModal: boolean = false;
//   isEditMode: boolean = false;
//   selectedLevel: any = null;
//   errorMessage: string | null = null;
//   successMessage: string | null = null;
//   suggestedLevelOrder: number = 10;

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
//     this.levelForm = this.fb.group({
//       levelName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
//       levelOrder: [10, [Validators.required, Validators.min(1)]],
//       description: ['', [Validators.maxLength(500)]],
//       companyName: [this.companyName]
//     });
//   }

//   loadHierarchyLevels(): void {
//     this.isLoading = true;
//     this.errorMessage = null;

//     this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         console.log('✅ Hierarchy Levels Response:', response);
//         this.isLoading = false;

//         if (response?.success && response?.data) {
//           this.hierarchyLevels = response.data;
//           console.log(`✅ Loaded ${this.hierarchyLevels.length} hierarchy levels`);
//           this.calculateSuggestedLevelOrder();
//         } else {
//           this.hierarchyLevels = [];
//           this.errorMessage = response?.message || 'Failed to load hierarchy levels';
//         }
//       },
//       error: (error: any) => {
//         this.isLoading = false;
//         console.error('❌ Error loading hierarchy levels:', error);
//         this.errorMessage = error.error?.message || 'Failed to load hierarchy levels';
//       }
//     });
//   }

//   loadHierarchyUsers(): void {
//     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         if (response?.success && response?.data) {
//           this.hierarchyUsers = response.data;
//         }
//       },
//       error: (error: any) => {
//         console.error('Error loading hierarchy users:', error);
//       }
//     });
//   }

//   getLevelGradient(levelOrder: number): string {
//     if (levelOrder <= 10) return 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)';
//     if (levelOrder <= 20) return 'linear-gradient(135deg, #ffc107 0%, #ff9800 100%)';
//     if (levelOrder <= 30) return 'linear-gradient(135deg, #17a2b8 0%, #138496 100%)';
//     if (levelOrder <= 40) return 'linear-gradient(135deg, #28a745 0%, #218838 100%)';
//     return 'linear-gradient(135deg, #6c757d 0%, #545b62 100%)';
//   }

//   getUserCountByLevel(levelId: number): number {
//     if (!this.hierarchyUsers || this.hierarchyUsers.length === 0) return 0;
//     return this.hierarchyUsers.filter(user => user.hierarchyLevelId === levelId).length;
//   }

//   calculateSuggestedLevelOrder(): void {
//     if (this.hierarchyLevels.length === 0) {
//       this.suggestedLevelOrder = 10;
//     } else {
//       const maxOrder = Math.max(...this.hierarchyLevels.map(l => l.levelOrder));
//       this.suggestedLevelOrder = maxOrder + 10;
//     }
//   }

//   openCreateModal(): void {
//     this.isEditMode = false;
//     this.selectedLevel = null;
//     this.levelForm.reset();
//     this.levelForm.patchValue({ 
//       companyName: this.companyName,
//       levelOrder: this.suggestedLevelOrder
//     });
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
//   }

//   openEditModal(level: any): void {
//     this.isEditMode = true;
//     this.selectedLevel = level;
//     this.levelForm.patchValue({
//       levelName: level.levelName,
//       levelOrder: level.levelOrder,
//       description: level.description,
//       companyName: level.companyName
//     });
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
//   }

//   submitLevel(): void {
//     if (this.levelForm.invalid) {
//       this.levelForm.markAllAsTouched();
//       return;
//     }

//     this.isSubmitting = true;
//     this.errorMessage = null;
//     this.successMessage = null;

//     const levelData = this.levelForm.value;

//     if (this.isEditMode) {
//       this.dataService.updateHierarchyLevel(this.selectedLevel.id, levelData).subscribe({
//         next: (response: any) => {
//           console.log('✅ Level Updated:', response);
//           this.isSubmitting = false;

//           if (response?.success) {
//             this.successMessage = 'Hierarchy level updated successfully!';
//             setTimeout(() => {
//               this.closeModal();
//               this.loadHierarchyLevels();
//             }, 1000);
//           } else {
//             this.errorMessage = response?.message || 'Failed to update level';
//           }
//         },
//         error: (error: any) => {
//           this.isSubmitting = false;
//           console.error('❌ Error updating level:', error);
//           this.errorMessage = error.error?.error || error.error?.message || 'Failed to update level';
//         }
//       });
//     } else {
//       this.dataService.createHierarchyLevel(levelData).subscribe({
//         next: (response: any) => {
//           console.log('✅ Level Created:', response);
//           this.isSubmitting = false;

//           if (response?.success) {
//             this.successMessage = 'Hierarchy level created successfully!';
//             setTimeout(() => {
//               this.closeModal();
//               this.loadHierarchyLevels();
//             }, 1000);
//           } else {
//             this.errorMessage = response?.message || 'Failed to create level';
//           }
//         },
//         error: (error: any) => {
//           this.isSubmitting = false;
//           console.error('❌ Error creating level:', error);
//           this.errorMessage = error.error?.error || error.error?.message || 'Failed to create level';
//         }
//       });
//     }
//   }

//   toggleLevelStatus(level: any): void {
//     const action = level.isActive ? 'deactivate' : 'activate';
//     const confirmMsg = `Are you sure you want to ${action} the level "${level.levelName}"?`;

//     if (!confirm(confirmMsg)) return;

//     const apiCall = level.isActive
//       ? this.dataService.deactivateHierarchyLevel(level.id)
//       : this.dataService.activateHierarchyLevel(level.id);

//     apiCall.subscribe({
//       next: (response: any) => {
//         console.log(`✅ Level ${action}d:`, response);
//         if (response?.success) {
//           this.loadHierarchyLevels();
//         } else {
//           alert(response?.message || `Failed to ${action} level`);
//         }
//       },
//       error: (error: any) => {
//         console.error(`❌ Error ${action}ing level:`, error);
//         alert(error.error?.message || `Failed to ${action} level`);
//       }
//     });
//   }

//   confirmDelete(level: any): void {
//     this.selectedLevel = level;
//     this.showDeleteModal = true;
//   }

//   deleteLevel(): void {
//     if (!this.selectedLevel) return;

//     this.isDeleting = true;

//     this.dataService.deleteHierarchyLevel(this.selectedLevel.id).subscribe({
//       next: (response: any) => {
//         console.log('✅ Level Soft Deleted:', response);
//         this.isDeleting = false;

//         if (response?.success) {
//           this.showDeleteModal = false;
//           this.loadHierarchyLevels();
//         } else {
//           alert(response?.message || 'Failed to delete level');
//         }
//       },
//       error: (error: any) => {
//         this.isDeleting = false;
//         console.error('❌ Error deleting level:', error);
//         alert(error.error?.message || 'Failed to delete level');
//       }
//     });
//   }

//   onDrop(event: CdkDragDrop<any[]>): void {
//     moveItemInArray(this.hierarchyLevels, event.previousIndex, event.currentIndex);
//     const levelIds = this.hierarchyLevels.map(level => level.id);

//     this.dataService.reorderHierarchyLevels(this.companyName, levelIds).subscribe({
//       next: (response: any) => {
//         console.log('✅ Levels Reordered:', response);
//         if (response?.success) {
//           this.loadHierarchyLevels();
//         }
//       },
//       error: (error: any) => {
//         console.error('❌ Error reordering levels:', error);
//         alert('Failed to save new order');
//         this.loadHierarchyLevels();
//       }
//     });
//   }

//   closeModal(): void {
//     this.showModal = false;
//     this.levelForm.reset();
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

//   formatDate(dateStr: string): string {
//     if (!dateStr) return 'N/A';
//     const date = new Date(dateStr);
//     return date.toLocaleDateString('en-US', { 
//       year: 'numeric', 
//       month: 'short', 
//       day: 'numeric' 
//     });
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
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DataService } from '../../../shared/service/DataService';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
  ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
  ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
  TooltipDirective, ButtonCloseDirective
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-hierarchy-level-management',
  templateUrl: './hierarchy-level-management.component.html',
  styleUrls: ['./hierarchy-level-management.component.css'],
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, DragDropModule,
    ContainerComponent, RowComponent, ColComponent, CardComponent,
    CardHeaderComponent, CardBodyComponent, ButtonDirective, ModalComponent,
    ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
    ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
    TooltipDirective, IconDirective, ButtonCloseDirective
  ]
})
export class HierarchyLevelManagementComponent implements OnInit {

  @ViewChild('levelModal') levelModal!: ModalComponent;
  @ViewChild('deleteModal') deleteModal!: ModalComponent;

  hierarchyLevels: any[] = [];
  hierarchyUsers: any[] = [];
  levelForm!: FormGroup;
  companyName: string = '';
  fullName: string = '';
  email: string = '';
  role: string = 'Super Administrator';
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isDeleting: boolean = false;
  showModal: boolean = false;
  showDeleteModal: boolean = false;
  isEditMode: boolean = false;
  selectedLevel: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  suggestedLevelOrder: number = 10;

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
    this.levelForm = this.fb.group({
      levelName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      levelOrder: [10, [Validators.required, Validators.min(1)]],
      description: ['', [Validators.maxLength(500)]],
      companyName: ['']
    });
  }

  loadHierarchyLevels(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.dataService.getHierarchyLevelsByCompany(this.companyName).subscribe({
      next: (response: any) => {
        console.log('✅ Hierarchy Levels Response:', response);
        this.isLoading = false;

        if (response?.success && response?.data) {
          this.hierarchyLevels = response.data;
          console.log(`✅ Loaded ${this.hierarchyLevels.length} hierarchy levels`);
          this.calculateSuggestedLevelOrder();
        } else {
          this.hierarchyLevels = [];
          this.errorMessage = response?.message || 'Failed to load hierarchy levels';
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('❌ Error loading hierarchy levels:', error);
        this.errorMessage = error.error?.message || 'Failed to load hierarchy levels';
      }
    });
  }

  loadHierarchyUsers(): void {
    this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
      next: (response: any) => {
        if (response?.success && response?.data) {
          this.hierarchyUsers = response.data;
        }
      },
      error: (error: any) => {
        console.error('Error loading hierarchy users:', error);
      }
    });
  }

  getLevelGradient(levelOrder: number): string {
    if (levelOrder <= 10) return 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)';
    if (levelOrder <= 20) return 'linear-gradient(135deg, #ffc107 0%, #ff9800 100%)';
    if (levelOrder <= 30) return 'linear-gradient(135deg, #17a2b8 0%, #138496 100%)';
    if (levelOrder <= 40) return 'linear-gradient(135deg, #28a745 0%, #218838 100%)';
    return 'linear-gradient(135deg, #6c757d 0%, #545b62 100%)';
  }

  getUserCountByLevel(levelId: number): number {
    if (!this.hierarchyUsers || this.hierarchyUsers.length === 0) return 0;
    return this.hierarchyUsers.filter(user => user.hierarchyLevelId === levelId).length;
  }

  calculateSuggestedLevelOrder(): void {
    if (this.hierarchyLevels.length === 0) {
      this.suggestedLevelOrder = 10;
    } else {
      const maxOrder = Math.max(...this.hierarchyLevels.map(l => l.levelOrder));
      this.suggestedLevelOrder = maxOrder + 10;
    }
  }

  openCreateModal(): void {
    console.log('🔵 Opening CREATE LEVEL modal...');
    
    this.isEditMode = false;
    this.selectedLevel = null;
    
    this.levelForm.reset();
    
    this.levelForm.patchValue({ 
      companyName: this.companyName,
      levelOrder: this.suggestedLevelOrder,
      levelName: '',
      description: ''
    });
    
    this.errorMessage = null;
    this.successMessage = null;
    
    this.showModal = true;
    
    console.log('✅ Level Modal opened:', {
      showModal: this.showModal,
      isEditMode: this.isEditMode,
      formValue: this.levelForm.value
    });
  }

  openEditModal(level: any): void {
    console.log('🟡 Opening EDIT LEVEL modal for:', level);
    
    this.isEditMode = true;
    this.selectedLevel = level;
    
    this.levelForm.patchValue({
      levelName: level.levelName,
      levelOrder: level.levelOrder,
      description: level.description,
      companyName: level.companyName
    });
    
    this.errorMessage = null;
    this.successMessage = null;
    this.showModal = true;
  }

  submitLevel(): void {
    console.log('📤 Submit Level Called');
    console.log('Form Valid:', this.levelForm.valid);
    console.log('Form Value:', this.levelForm.value);
    console.log('Is Edit Mode:', this.isEditMode);

    if (this.levelForm.invalid) {
      this.levelForm.markAllAsTouched();
      console.log('❌ Form is invalid');
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const levelData = this.levelForm.value;

    if (this.isEditMode) {
      console.log('🔄 Updating level:', this.selectedLevel.id);
      this.dataService.updateHierarchyLevel(this.selectedLevel.id, levelData).subscribe({
        next: (response: any) => {
          console.log('✅ Level Updated:', response);
          this.isSubmitting = false;

          if (response?.success) {
            this.successMessage = 'Hierarchy level updated successfully!';
            setTimeout(() => {
              this.closeModal();
              this.loadHierarchyLevels();
            }, 1000);
          } else {
            this.errorMessage = response?.message || 'Failed to update level';
          }
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('❌ Error updating level:', error);
          this.errorMessage = error.error?.error || error.error?.message || 'Failed to update level';
        }
      });
    } else {
      console.log('➕ Creating new level');
      this.dataService.createHierarchyLevel(levelData).subscribe({
        next: (response: any) => {
          console.log('✅ Level Created:', response);
          this.isSubmitting = false;

          if (response?.success) {
            this.successMessage = 'Hierarchy level created successfully!';
            setTimeout(() => {
              this.closeModal();
              this.loadHierarchyLevels();
            }, 1000);
          } else {
            this.errorMessage = response?.message || 'Failed to create level';
          }
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('❌ Error creating level:', error);
          this.errorMessage = error.error?.error || error.error?.message || 'Failed to create level';
        }
      });
    }
  }

  toggleLevelStatus(level: any): void {
    const action = level.isActive ? 'deactivate' : 'activate';
    const confirmMsg = `Are you sure you want to ${action} the level "${level.levelName}"?`;

    if (!confirm(confirmMsg)) return;

    const apiCall = level.isActive
      ? this.dataService.deactivateHierarchyLevel(level.id)
      : this.dataService.activateHierarchyLevel(level.id);

    apiCall.subscribe({
      next: (response: any) => {
        console.log(`✅ Level ${action}d:`, response);
        if (response?.success) {
          this.loadHierarchyLevels();
        } else {
          alert(response?.message || `Failed to ${action} level`);
        }
      },
      error: (error: any) => {
        console.error(`❌ Error ${action}ing level:`, error);
        alert(error.error?.message || `Failed to ${action} level`);
      }
    });
  }

  confirmDelete(level: any): void {
    this.selectedLevel = level;
    this.showDeleteModal = true;
  }

  deleteLevel(): void {
    if (!this.selectedLevel) return;

    this.isDeleting = true;

    this.dataService.deleteHierarchyLevel(this.selectedLevel.id).subscribe({
      next: (response: any) => {
        console.log('✅ Level Soft Deleted:', response);
        this.isDeleting = false;

        if (response?.success) {
          this.showDeleteModal = false;
          this.loadHierarchyLevels();
        } else {
          alert(response?.message || 'Failed to delete level');
        }
      },
      error: (error: any) => {
        this.isDeleting = false;
        console.error('❌ Error deleting level:', error);
        alert(error.error?.message || 'Failed to delete level');
      }
    });
  }

  onDrop(event: CdkDragDrop<any[]>): void {
    moveItemInArray(this.hierarchyLevels, event.previousIndex, event.currentIndex);
    const levelIds = this.hierarchyLevels.map(level => level.id);

    this.dataService.reorderHierarchyLevels(this.companyName, levelIds).subscribe({
      next: (response: any) => {
        console.log('✅ Levels Reordered:', response);
        if (response?.success) {
          this.loadHierarchyLevels();
        }
      },
      error: (error: any) => {
        console.error('❌ Error reordering levels:', error);
        alert('Failed to save new order');
        this.loadHierarchyLevels();
      }
    });
  }

  closeModal(): void {
    console.log('🔴 Closing level modal');
    this.showModal = false;
    this.levelForm.reset();
    this.errorMessage = null;
    this.successMessage = null;
    this.isEditMode = false;
    this.selectedLevel = null;
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

  formatDate(dateStr: string): string {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
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