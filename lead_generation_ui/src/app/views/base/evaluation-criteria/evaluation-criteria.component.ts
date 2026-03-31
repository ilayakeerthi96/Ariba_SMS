

// import { Component, OnInit, ViewChild } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardComponent,
//   CardHeaderComponent, CardBodyComponent, CardFooterComponent, ButtonDirective, ModalComponent,
//   ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//   ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//   ButtonCloseDirective, FormSelectDirective
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';

// @Component({
//   selector: 'app-evaluation-criteria',
//   templateUrl: './evaluation-criteria.component.html',
//   styleUrls: ['./evaluation-criteria.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, ReactiveFormsModule, FormsModule,
//     ContainerComponent, RowComponent, ColComponent, CardComponent,
//     CardHeaderComponent, CardBodyComponent, CardFooterComponent, ButtonDirective, ModalComponent,
//     ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//     ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
//     IconDirective, ButtonCloseDirective, FormSelectDirective
//   ]
// })
// export class EvaluationCriteriaComponent implements OnInit {

//   @ViewChild('criterionModal') criterionModal!: ModalComponent;
//   @ViewChild('deleteModal') deleteModal!: ModalComponent;

//   criteria: any[] = [];
//   criterionForm!: FormGroup;
  
//   companyName: string = '';
//   fullName: string = '';
//   email: string = '';
//   role: string = 'Organization Administrator';
  
//   isLoading: boolean = false;
//   isSubmitting: boolean = false;
//   isDeleting: boolean = false;
  
//   showModal: boolean = false;
//   showDeleteModal: boolean = false;
  
//   isEditMode: boolean = false;
//   selectedCriterion: any = null;
  
//   errorMessage: string | null = null;
//   successMessage: string | null = null;

//   constructor(
//     private fb: FormBuilder,
//     private dataService: DataService,
//     private messageService: MessageService
//   ) {
//     this.initForms();
//   }

//   ngOnInit(): void {
//     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
//     this.fullName = localStorage.getItem('fullName') || 'Admin';
//     this.email = localStorage.getItem('email') || 'admin@company.com';
    
//     console.log('🎯 Evaluation Criteria Management');
//     console.log('  Company:', this.companyName);
//     console.log('  User:', this.fullName);
    
//     this.loadCriteria();
//   }

//   initForms(): void {
//     this.criterionForm = this.fb.group({
//       criterionName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
//       description: ['', [Validators.maxLength(500)]],
//       criterionType: ['MANUAL', [Validators.required]],
//       maxScore: [10, [Validators.required, Validators.min(1), Validators.max(100)]]
//     });
//   }

//   loadCriteria(): void {
//     this.isLoading = true;
    
//     console.log('📡 Loading all criteria (including inactive)...');
    
//     this.dataService.getAllCriteria().subscribe({
//       next: (response: any) => {
//         console.log('✅ Criteria Response:', response);
        
//         if (response && response.success && response.data) {
//           this.criteria = response.data;
//         } else if (Array.isArray(response)) {
//           this.criteria = response;
//         } else {
//           this.criteria = [];
//         }
        
//         // Sort by displayOrder
//         this.criteria.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        
//         console.log(`✅ Loaded ${this.criteria.length} criteria`);
//         console.log('  Active:', this.criteria.filter(c => c.isActive).length);
//         console.log('  Inactive:', this.criteria.filter(c => !c.isActive).length);
        
//         this.isLoading = false;
//       },
//       error: (error: any) => {
//         console.error('❌ Error loading criteria:', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load criteria');
//         this.isLoading = false;
//       }
//     });
//   }

//   openCreateModal(): void {
//     this.isEditMode = false;
//     this.selectedCriterion = null;
    
//     this.criterionForm.reset({
//       criterionType: 'MANUAL',
//       maxScore: 10
//     });
    
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
    
//     console.log('➕ Opening create criterion modal');
//   }

//   openEditModal(criterion: any): void {
//     this.isEditMode = true;
//     this.selectedCriterion = criterion;
    
//     this.criterionForm.patchValue({
//       criterionName: criterion.criterionName,
//       description: criterion.description,
//       criterionType: criterion.criterionType,
//       maxScore: criterion.maxScore
//     });
    
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.showModal = true;
    
//     console.log('✏️ Opening edit modal for:', criterion.criterionName);
//   }

//   submitCriterion(): void {
//     if (this.criterionForm.invalid) {
//       this.criterionForm.markAllAsTouched();
//       return;
//     }

//     this.isSubmitting = true;
//     this.errorMessage = null;
//     this.successMessage = null;

//     const criterionData = {
//       ...this.criterionForm.value,
//       isActive: true,
//       displayOrder: this.isEditMode 
//         ? this.selectedCriterion.displayOrder 
//         : (this.criteria.length + 1)
//     };

//     console.log('📤 Submitting criterion:', criterionData);

//     if (this.isEditMode && this.selectedCriterion) {
//       // UPDATE
//       this.dataService.updateCriterion(this.selectedCriterion.id, criterionData).subscribe({
//         next: (response: any) => {
//           console.log('✅ Criterion updated:', response);
          
//           this.isSubmitting = false;
          
//           if (response && response.success) {
//             this.messageService.showMessage('success', 'Success', 'Criterion updated successfully');
//             this.closeModal();
//             this.loadCriteria();
//           } else {
//             this.errorMessage = response?.message || 'Failed to update criterion';
//           }
//         },
//         error: (error: any) => {
//           console.error('❌ Error updating criterion:', error);
//           this.isSubmitting = false;
//           this.errorMessage = error.error?.message || 'Failed to update criterion';
//         }
//       });
//     } else {
//       // CREATE
//       this.dataService.createCriterion(criterionData).subscribe({
//         next: (response: any) => {
//           console.log('✅ Criterion created:', response);
          
//           this.isSubmitting = false;
          
//           if (response && response.success) {
//             this.messageService.showMessage('success', 'Success', 'Criterion created successfully');
//             this.closeModal();
//             this.loadCriteria();
//           } else {
//             this.errorMessage = response?.message || 'Failed to create criterion';
//           }
//         },
//         error: (error: any) => {
//           console.error('❌ Error creating criterion:', error);
//           this.isSubmitting = false;
//           this.errorMessage = error.error?.message || 'Failed to create criterion';
//         }
//       });
//     }
//   }

//   confirmDelete(criterion: any): void {
//     this.selectedCriterion = criterion;
//     this.showDeleteModal = true;
    
//     console.log('🗑️ Confirm delete:', criterion.criterionName);
//   }

//   deleteCriterion(): void {
//     if (!this.selectedCriterion) return;

//     this.isDeleting = true;
    
//     console.log('🗑️ Deleting criterion:', this.selectedCriterion.id);

//     this.dataService.deleteCriterion(this.selectedCriterion.id).subscribe({
//       next: (response: any) => {
//         console.log('✅ Criterion deleted:', response);
        
//         this.isDeleting = false;
        
//         if (response && response.success) {
//           this.messageService.showMessage('success', 'Success', 'Criterion deleted successfully');
//           this.showDeleteModal = false;
//           this.loadCriteria();
//         } else {
//           this.messageService.showMessage('error', 'Error', response?.message || 'Failed to delete');
//         }
//       },
//       error: (error: any) => {
//         console.error('❌ Error deleting criterion:', error);
//         this.isDeleting = false;
//         this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete');
//       }
//     });
//   }

//   toggleStatus(criterion: any): void {
//     const action = criterion.isActive ? 'deactivate' : 'activate';
    
//     if (!confirm(`Are you sure you want to ${action} "${criterion.criterionName}"?`)) {
//       return;
//     }

//     console.log(`🔄 ${action.toUpperCase()} criterion:`, criterion.criterionName);

//     const apiCall = criterion.isActive
//       ? this.dataService.deactivateCriterion(criterion.id)
//       : this.dataService.activateCriterion(criterion.id);

//     apiCall.subscribe({
//       next: (response: any) => {
//         console.log(`✅ Criterion ${action}d:`, response);
        
//         if (response && response.success) {
//           this.messageService.showMessage('success', 'Success', `Criterion ${action}d`);
//           this.loadCriteria();
//         }
//       },
//       error: (error: any) => {
//         console.error(`❌ Error ${action}ing:`, error);
//         this.messageService.showMessage('error', 'Error', `Failed to ${action}`);
//       }
//     });
//   }

//   closeModal(): void {
//     this.showModal = false;
//     this.criterionForm.reset();
//     this.errorMessage = null;
//     this.successMessage = null;
//   }

//   onModalClose(visible: boolean): void {
//     if (!visible) {
//       this.closeModal();
//     }
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
import { MessageService } from '../../../shared/service/message.service';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, CardFooterComponent, ButtonDirective, ModalComponent,
  ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
  ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
  ButtonCloseDirective, FormSelectDirective
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

/**
 * ✅ SIMPLIFIED: Admin Evaluation Criteria Component
 * Only Name + Description (Max Score = 5, Type = MANUAL are hardcoded)
 */
@Component({
  selector: 'app-evaluation-criteria',
  templateUrl: './evaluation-criteria.component.html',
  styleUrls: ['./evaluation-criteria.component.css'],
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule,
    ContainerComponent, RowComponent, ColComponent, CardComponent,
    CardHeaderComponent, CardBodyComponent, CardFooterComponent, ButtonDirective, ModalComponent,
    ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
    ModalFooterComponent, FormControlDirective, BadgeComponent, AlertComponent,
    IconDirective, ButtonCloseDirective, FormSelectDirective
  ]
})
export class EvaluationCriteriaComponent implements OnInit {

  @ViewChild('criterionModal') criterionModal!: ModalComponent;
  @ViewChild('deleteModal') deleteModal!: ModalComponent;

  criteria: any[] = [];
  criterionForm!: FormGroup;
  
  companyName: string = '';
  fullName: string = '';
  email: string = '';
  role: string = 'Organization Administrator';
  
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isDeleting: boolean = false;
  
  showModal: boolean = false;
  showDeleteModal: boolean = false;
  
  isEditMode: boolean = false;
  selectedCriterion: any = null;
  
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dataService: DataService,
    private messageService: MessageService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
    this.fullName = localStorage.getItem('fullName') || 'Admin';
    this.email = localStorage.getItem('email') || 'admin@company.com';
    
    console.log('🎯 Evaluation Criteria Management (SIMPLIFIED)');
    console.log('  Company:', this.companyName);
    console.log('  User:', this.fullName);
    console.log('  ✅ Max Score: 5 (hardcoded)');
    console.log('  ✅ Type: MANUAL (hardcoded)');
    
    this.loadCriteria();
  }

  /**
   * ✅ SIMPLIFIED: Only Name + Description
   */
  initForms(): void {
    this.criterionForm = this.fb.group({
      criterionName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]]
      // ❌ REMOVED: criterionType, maxScore
    });
  }

  loadCriteria(): void {
    this.isLoading = true;
    
    console.log('📡 Loading all criteria...');
    
    this.dataService.getAllCriteria().subscribe({
      next: (response: any) => {
        console.log('✅ Criteria Response:', response);
        
        if (response && response.success && response.data) {
          this.criteria = response.data;
        } else if (Array.isArray(response)) {
          this.criteria = response;
        } else {
          this.criteria = [];
        }
        
        // Sort by displayOrder
        this.criteria.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        
        console.log(`✅ Loaded ${this.criteria.length} criteria`);
        console.log('  Active:', this.criteria.filter(c => c.isActive).length);
        console.log('  Inactive:', this.criteria.filter(c => !c.isActive).length);
        
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading criteria:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load criteria');
        this.isLoading = false;
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedCriterion = null;
    
    this.criterionForm.reset();
    
    this.errorMessage = null;
    this.successMessage = null;
    this.showModal = true;
    
    console.log('➕ Opening create criterion modal');
    console.log('  ℹ️ Max Score will be set to 5');
    console.log('  ℹ️ Type will be set to MANUAL');
  }

  openEditModal(criterion: any): void {
    this.isEditMode = true;
    this.selectedCriterion = criterion;
    
    // ✅ SIMPLIFIED: Only patch Name + Description
    this.criterionForm.patchValue({
      criterionName: criterion.criterionName,
      description: criterion.description
      // ❌ REMOVED: criterionType, maxScore
    });
    
    this.errorMessage = null;
    this.successMessage = null;
    this.showModal = true;
    
    console.log('✏️ Opening edit modal for:', criterion.criterionName);
    console.log('  ℹ️ Max Score remains 5');
    console.log('  ℹ️ Type remains MANUAL');
  }

  /**
   * ✅ SIMPLIFIED: Hardcode Max Score = 5 and Type = MANUAL
   */
  submitCriterion(): void {
    if (this.criterionForm.invalid) {
      this.criterionForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    // ✅ HARDCODED VALUES
    const criterionData = {
      ...this.criterionForm.value,
      criterionType: 'MANUAL',  // ✅ Always MANUAL
      maxScore: 5,              // ✅ Always 5
      isActive: true,
      displayOrder: this.isEditMode 
        ? this.selectedCriterion.displayOrder 
        : (this.criteria.length + 1)
    };

    console.log('📤 Submitting criterion:', criterionData);
    console.log('  ✅ Max Score: 5 (hardcoded)');
    console.log('  ✅ Type: MANUAL (hardcoded)');

    if (this.isEditMode && this.selectedCriterion) {
      // UPDATE
      this.dataService.updateCriterion(this.selectedCriterion.id, criterionData).subscribe({
        next: (response: any) => {
          console.log('✅ Criterion updated:', response);
          
          this.isSubmitting = false;
          
          if (response && response.success) {
            this.messageService.showMessage('success', 'Success', 'Criterion updated successfully');
            this.closeModal();
            this.loadCriteria();
          } else {
            this.errorMessage = response?.message || 'Failed to update criterion';
          }
        },
        error: (error: any) => {
          console.error('❌ Error updating criterion:', error);
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Failed to update criterion';
        }
      });
    } else {
      // CREATE
      this.dataService.createCriterion(criterionData).subscribe({
        next: (response: any) => {
          console.log('✅ Criterion created:', response);
          
          this.isSubmitting = false;
          
          if (response && response.success) {
            this.messageService.showMessage('success', 'Success', 'Criterion created successfully');
            this.closeModal();
            this.loadCriteria();
          } else {
            this.errorMessage = response?.message || 'Failed to create criterion';
          }
        },
        error: (error: any) => {
          console.error('❌ Error creating criterion:', error);
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Failed to create criterion';
        }
      });
    }
  }

  confirmDelete(criterion: any): void {
    this.selectedCriterion = criterion;
    this.showDeleteModal = true;
    
    console.log('🗑️ Confirm delete:', criterion.criterionName);
  }

  deleteCriterion(): void {
    if (!this.selectedCriterion) return;

    this.isDeleting = true;
    
    console.log('🗑️ Deleting criterion:', this.selectedCriterion.id);

    this.dataService.deleteCriterion(this.selectedCriterion.id).subscribe({
      next: (response: any) => {
        console.log('✅ Criterion deleted:', response);
        
        this.isDeleting = false;
        
        if (response && response.success) {
          this.messageService.showMessage('success', 'Success', 'Criterion deleted successfully');
          this.showDeleteModal = false;
          this.loadCriteria();
        } else {
          this.messageService.showMessage('error', 'Error', response?.message || 'Failed to delete');
        }
      },
      error: (error: any) => {
        console.error('❌ Error deleting criterion:', error);
        this.isDeleting = false;
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete');
      }
    });
  }

  toggleStatus(criterion: any): void {
    const action = criterion.isActive ? 'deactivate' : 'activate';
    
    if (!confirm(`Are you sure you want to ${action} "${criterion.criterionName}"?`)) {
      return;
    }

    console.log(`🔄 ${action.toUpperCase()} criterion:`, criterion.criterionName);

    const apiCall = criterion.isActive
      ? this.dataService.deactivateCriterion(criterion.id)
      : this.dataService.activateCriterion(criterion.id);

    apiCall.subscribe({
      next: (response: any) => {
        console.log(`✅ Criterion ${action}d:`, response);
        
        if (response && response.success) {
          this.messageService.showMessage('success', 'Success', `Criterion ${action}d`);
          this.loadCriteria();
        }
      },
      error: (error: any) => {
        console.error(`❌ Error ${action}ing:`, error);
        this.messageService.showMessage('error', 'Error', `Failed to ${action}`);
      }
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.criterionForm.reset();
    this.errorMessage = null;
    this.successMessage = null;
  }

  onModalClose(visible: boolean): void {
    if (!visible) {
      this.closeModal();
    }
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