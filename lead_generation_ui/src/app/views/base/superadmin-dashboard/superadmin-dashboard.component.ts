
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  FormBuilder, FormGroup, Validators,
  ReactiveFormsModule, FormsModule
} from '@angular/forms';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import { BuyerService } from '../dashboard/buyer-b.service';
import { Buyer } from '../dashboard/buyer-b.model';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, ButtonDirective, BadgeComponent,
  ModalComponent, ModalHeaderComponent, ModalTitleDirective,
  ModalBodyComponent, ModalFooterComponent, FormControlDirective,
  AlertComponent, ButtonCloseDirective
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-superadmin-dashboard',
  templateUrl: './superadmin-dashboard.component.html',
  styleUrls: ['./superadmin-dashboard.component.css'],
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule,
    ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardHeaderComponent, CardBodyComponent, ButtonDirective,
    IconDirective, BadgeComponent,
    ModalComponent, ModalHeaderComponent, ModalTitleDirective,
    ModalBodyComponent, ModalFooterComponent,
    FormControlDirective, AlertComponent, ButtonCloseDirective
  ]
})
export class SuperAdminDashboardComponent implements OnInit {

  @ViewChild('createModal') createModal!: ModalComponent;
  @ViewChild('editModal') editModal!: ModalComponent;

  // User Details
  fullName: string = '';
  email: string = '';
  companyName: string = '';
  phone: string = '';
  role: string = 'Super Administrator';

  // Org Admin Data
  orgAdmins: any[] = [];
  isLoadingOrgAdmins = false;

  // ── CREATE MODAL ──────────────────────────────────────────────
  showCreateModal = false;
  orgAdminForm!: FormGroup;
  isSubmitting = false;
  createLogoFile: File | null = null;
  createLogoPreview: string | null = null;
  createErrorMessage: string | null = null;
  createSuccessMessage: string | null = null;

  // ── EDIT MODAL ────────────────────────────────────────────────
  showEditModal = false;
  editOrgAdminForm!: FormGroup;
  isEditSubmitting = false;
  editLogoFile: File | null = null;
  editLogoPreview: string | null = null;
  editErrorMessage: string | null = null;
  editSuccessMessage: string | null = null;
  selectedAdminId: number | null = null;

  // Statistics
  stats = {
    totalOrgAdmins: 0,
    activeOrgAdmins: 0,
    totalBuyers: 0
  };

  Math = Math;

  constructor(
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService,
    private fb: FormBuilder
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadUserDetails();
    this.loadOrgAdmins();
  }

  // ============================================================
  // INIT FORMS
  // ============================================================
  private initForms(): void {
    // Create form
    this.orgAdminForm = this.fb.group({
      email:       ['', [Validators.required, Validators.email]],
      fullName:    ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      companyName: ['', [Validators.required]],
      phone:       [''],
      password:    ['', [Validators.required, Validators.minLength(6)]]
    });

    // Edit form
    this.editOrgAdminForm = this.fb.group({
      fullName:         [''],
      phone:            [''],
      companyName:      [''],
      organizationName: [''],
      password:         ['']
    });
  }

  // ============================================================
  // LOAD USER DETAILS
  // ============================================================
  loadUserDetails(): void {
    this.fullName    = localStorage.getItem('fullName') || 'SuperAdmin';
    this.email       = localStorage.getItem('email') || '';
    this.companyName = localStorage.getItem('companyName') || '';
    this.phone       = localStorage.getItem('phone') || '';
  }

  // ============================================================
  // LOAD ORG ADMINS
  // ============================================================
  loadOrgAdmins(): void {
    console.log('🔄 Loading Organization Admins...');
    this.isLoadingOrgAdmins = true;

    this.dataService.superAdminGetAllOrgAdmins().subscribe({
      next: (response: any) => {
        this.isLoadingOrgAdmins = false;
        if (response?.success && response?.data) {
          this.orgAdmins = Array.isArray(response.data) ? response.data : [];
          this.stats.totalOrgAdmins  = this.orgAdmins.length;
          this.stats.activeOrgAdmins = this.orgAdmins.filter(
            (a: any) => a.isActive === true
          ).length;
          console.log(`✅ Loaded ${this.orgAdmins.length} admins`);
        } else {
          this.orgAdmins = [];
          this.stats.totalOrgAdmins = 0;
          this.stats.activeOrgAdmins = 0;
        }
      },
      error: (error: any) => {
        this.isLoadingOrgAdmins = false;
        console.error('❌ Error loading OrgAdmins:', error);
        this.messageService.showMessage('error', 'Error',
            'Failed to load organization admins');
        this.orgAdmins = [];
      }
    });
  }

  // ============================================================
  // CREATE MODAL — open / close / logo / submit
  // ============================================================
  openCreateOrgAdminModal(): void {
    this.orgAdminForm.reset();
    this.createLogoFile    = null;
    this.createLogoPreview = null;
    this.createErrorMessage   = null;
    this.createSuccessMessage = null;
    this.isSubmitting = false;
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.orgAdminForm.reset();
    this.createLogoFile    = null;
    this.createLogoPreview = null;
    this.createErrorMessage   = null;
    this.createSuccessMessage = null;
    this.isSubmitting = false;
  }

  onCreateModalClose(visible: boolean): void {
    if (!visible) this.closeCreateModal();
  }

  onCreateLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.createLogoFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e: any) => { this.createLogoPreview = e.target.result; };
      reader.readAsDataURL(this.createLogoFile);
    }
  }

  submitOrgAdmin(): void {
    if (this.orgAdminForm.invalid) {
      this.orgAdminForm.markAllAsTouched();
      this.createErrorMessage = 'Please fill all required fields correctly';
      return;
    }

    this.isSubmitting = true;
    this.createErrorMessage   = null;
    this.createSuccessMessage = null;

    const orgAdminData = this.orgAdminForm.value;

    // Step 1: Create the admin
    this.dataService.superAdminCreateOrgAdmin(orgAdminData).subscribe({
      next: (response: any) => {
        if (response?.success) {
          const createdAdminId = response.data?.id;

          // Step 2: Upload logo if selected
          if (this.createLogoFile && createdAdminId) {
            this.dataService.uploadOrgAdminLogo(createdAdminId, this.createLogoFile)
              .subscribe({
                next: (logoRes: any) => {
                  console.log('✅ Logo uploaded for new admin:', createdAdminId);
                  this.createSuccessMessage =
                    'Organization Admin created with logo successfully!';
                  this.messageService.showMessage('success', 'Success',
                      'Organization Admin created successfully!');
                  setTimeout(() => {
                    this.isSubmitting = false;
                    this.closeCreateModal();
                    this.loadOrgAdmins();
                  }, 1000);
                },
                error: (logoErr: any) => {
                  console.warn('⚠️ Admin created but logo upload failed:', logoErr);
                  this.createSuccessMessage =
                    'Admin created! Logo upload failed — you can upload it later via Edit.';
                  setTimeout(() => {
                    this.isSubmitting = false;
                    this.closeCreateModal();
                    this.loadOrgAdmins();
                  }, 1500);
                }
              });
          } else {
            // No logo — just close
            this.createSuccessMessage = 'Organization Admin created successfully!';
            this.messageService.showMessage('success', 'Success',
                'Organization Admin created successfully!');
            setTimeout(() => {
              this.isSubmitting = false;
              this.closeCreateModal();
              this.loadOrgAdmins();
            }, 1000);
          }
        } else {
          this.isSubmitting = false;
          this.createErrorMessage = response?.message || 'Failed to create admin';
        }
      },
      error: (error: any) => {
        this.isSubmitting = false;
        const msg = error.error?.error || error.error?.message || 'Failed to create admin';
        this.createErrorMessage = msg;
        this.messageService.showMessage('error', 'Error', msg);
      }
    });
  }

  // ============================================================
  // EDIT MODAL — open / close / logo / submit
  // ============================================================
  openEditOrgAdminModal(admin: any): void {
    this.selectedAdminId = admin.id;
    this.editLogoFile    = null;
    this.editErrorMessage   = null;
    this.editSuccessMessage = null;
    this.isEditSubmitting   = false;

    // Show existing logo in preview
    this.editLogoPreview = admin.logoBase64 || null;

    // Patch form with existing values
    this.editOrgAdminForm.patchValue({
      fullName:         admin.fullName         || '',
      phone:            admin.phone            || '',
      companyName:      admin.companyName      || '',
      organizationName: admin.organizationName || '',
      password:         ''
    });

    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editOrgAdminForm.reset();
    this.editLogoFile    = null;
    this.editLogoPreview = null;
    this.editErrorMessage   = null;
    this.editSuccessMessage = null;
    this.isEditSubmitting   = false;
    this.selectedAdminId    = null;
  }

  onEditModalClose(visible: boolean): void {
    if (!visible) this.closeEditModal();
  }

  onEditLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.editLogoFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e: any) => { this.editLogoPreview = e.target.result; };
      reader.readAsDataURL(this.editLogoFile);
    }
  }

  submitEditOrgAdmin(): void {
    if (!this.selectedAdminId) return;

    this.isEditSubmitting = true;
    this.editErrorMessage   = null;
    this.editSuccessMessage = null;

    const v = this.editOrgAdminForm.value;

    // Build payload — only non-blank fields
    const payload: any = {};
    if (v.fullName?.trim())         payload.fullName         = v.fullName.trim();
    if (v.phone?.trim())            payload.phone            = v.phone.trim();
    if (v.companyName?.trim())      payload.companyName      = v.companyName.trim();
    if (v.organizationName?.trim()) payload.organizationName = v.organizationName.trim();
    if (v.password?.trim())         payload.password         = v.password.trim();

    const adminId = this.selectedAdminId;

    // Step 1: Update profile fields
    this.dataService.updateOrganizationAdmin(adminId, payload).subscribe({
      next: (profileRes: any) => {
        if (profileRes?.success) {

          // Step 2: Upload logo if a new one was selected
          if (this.editLogoFile) {
            this.dataService.uploadOrgAdminLogo(adminId, this.editLogoFile).subscribe({
              next: (logoRes: any) => {
                console.log('✅ Logo updated for admin:', adminId);
                this.editSuccessMessage = 'Admin updated with new logo successfully!';
                this.messageService.showMessage('success', 'Success',
                    'Organization Admin updated!');
                setTimeout(() => {
                  this.isEditSubmitting = false;
                  this.closeEditModal();
                  this.loadOrgAdmins();
                }, 1000);
              },
              error: (logoErr: any) => {
                console.warn('⚠️ Profile updated but logo upload failed');
                this.editSuccessMessage =
                  'Profile updated! Logo upload failed — please try again.';
                setTimeout(() => {
                  this.isEditSubmitting = false;
                  this.closeEditModal();
                  this.loadOrgAdmins();
                }, 1500);
              }
            });
          } else {
            // No logo change
            this.editSuccessMessage = 'Admin updated successfully!';
            this.messageService.showMessage('success', 'Success',
                'Organization Admin updated!');
            setTimeout(() => {
              this.isEditSubmitting = false;
              this.closeEditModal();
              this.loadOrgAdmins();
            }, 1000);
          }
        } else {
          this.isEditSubmitting = false;
          this.editErrorMessage = profileRes?.message || 'Update failed';
        }
      },
      error: (error: any) => {
        this.isEditSubmitting = false;
        const msg = error.error?.error || error.error?.message || 'Update failed';
        this.editErrorMessage = msg;
        this.messageService.showMessage('error', 'Error', msg);
      }
    });
  }

  // ============================================================
  // UTILITY
  // ============================================================
  getInitials(name: string): string {
    if (!name) return 'NA';
    const parts = name.split(' ');
    return parts.length >= 2
      ? (parts[0][0] + parts[1][0]).toUpperCase()
      : name.substring(0, 2).toUpperCase();
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return 'N/A';
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return 'N/A';
      return date.toLocaleDateString('en-US', {
        year: 'numeric', month: 'short', day: 'numeric'
      });
    } catch (e) {
      return 'N/A';
    }
  }
}