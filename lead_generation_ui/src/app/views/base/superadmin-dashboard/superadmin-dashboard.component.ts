
// import { Component, OnInit, ViewChild } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router } from '@angular/router';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { Buyer } from '../dashboard/buyer-b.model';
// import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardComponent,
//   CardHeaderComponent, CardBodyComponent, ButtonDirective, BadgeComponent,
//   ModalComponent, ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent,
//   ModalFooterComponent, FormControlDirective, AlertComponent, ButtonCloseDirective
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';

// @Component({
//   selector: 'app-superadmin-dashboard',
//   templateUrl: './superadmin-dashboard.component.html',
//   styleUrls: ['./superadmin-dashboard.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, ReactiveFormsModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardHeaderComponent, CardBodyComponent, ButtonDirective,
//     IconDirective, BadgeComponent, ModalComponent, ModalHeaderComponent,
//     ModalTitleDirective, ModalBodyComponent, ModalFooterComponent,
//     FormControlDirective, AlertComponent, ButtonCloseDirective, DownloadExcelComponent
//   ]
// })
// export class SuperAdminDashboardComponent implements OnInit {

//   @ViewChild('createModal') createModal!: ModalComponent;

//   // User Details
//   fullName: string = '';
//   email: string = '';
//   companyName: string = '';
//   phone: string = '';
//   role: string = 'Super Administrator';
  
//   // Organization Admin Data
//   orgAdmins: any[] = [];
//   filteredOrgAdmins: any[] = [];
//   pagedOrgAdmins: any[] = [];
//   orgAdminSearchText = '';
//   orgAdminPage = 1;
//   orgAdminPageSize = 5;
//   orgAdminForm!: FormGroup;
//   isLoadingOrgAdmins: boolean = false;
  
//   // Buyer Data
//   buyers: Buyer[] = [];
//   filteredBuyers: any[] = [];
//   pagedBuyers: any[] = [];
//   buyerSearchText = '';
//   buyerPage = 1;
//   buyerPageSize = 5;
//   isLoadingBuyers: boolean = false;
  
//   // UI State
//   isSubmitting: boolean = false;
//   showModal: boolean = false;
//   errorMessage: string | null = null;
//   successMessage: string | null = null;

//   // Statistics
//   stats = {
//     totalOrgAdmins: 0,
//     activeOrgAdmins: 0,
//     totalBuyers: 0
//   };

//   // Expose Math to template
//   Math = Math;

//   constructor(
//     private router: Router,
//     private dataService: DataService,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private fb: FormBuilder
//   ) {
//     this.initForm();
//   }

//   ngOnInit(): void {
//     this.loadUserDetails();
//     this.loadOrgAdmins();
//     this.loadAllBuyers(); // ✅ CHANGED: Load all buyers for SuperAdmin
//   }

//   initForm(): void {
//     this.orgAdminForm = this.fb.group({
//       email: ['', [Validators.required, Validators.email]],
//       fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
//       companyName: ['', [Validators.required]],
//       phone: [''],
//       password: ['', [Validators.required, Validators.minLength(6)]]
//     });
//   }

//   loadUserDetails(): void {
//     this.fullName = localStorage.getItem('fullName') || 'SuperAdmin';
//     this.email = localStorage.getItem('email') || 'admin@company.com';
//     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
//     this.phone = localStorage.getItem('phone') || 'Not provided';
//   }

//   // ==================== ORGANIZATION ADMIN METHODS ====================

//   loadOrgAdmins(): void {
//     console.log('🔄 Loading Organization Admins...');
//     this.isLoadingOrgAdmins = true;
    
//     this.dataService.superAdminGetAllOrgAdmins().subscribe({
//       next: (response: any) => {
//         console.log('✅ OrgAdmins Response:', response);
//         this.isLoadingOrgAdmins = false;

//         if (response?.success && response?.data) {
//           this.orgAdmins = Array.isArray(response.data) ? response.data : [];
          
//           // Calculate stats - ensure we're counting correctly
//           this.stats.totalOrgAdmins = this.orgAdmins.length;
//           this.stats.activeOrgAdmins = this.orgAdmins.filter((admin: any) => {
//             // Check multiple possible property names for active status
//             return admin.isActive === true || admin.active === true || admin.status === 'active';
//           }).length;
          
//           console.log(`✅ Loaded ${this.orgAdmins.length} Organization Admins`);
//           console.log(`✅ Active Admins: ${this.stats.activeOrgAdmins}`);
//           console.log('📊 OrgAdmins Data:', this.orgAdmins);
          
//           // Apply pagination
//           this.applyOrgAdminSearchAndPagination();
//         } else {
//           console.warn('⚠️ No data in response or unsuccessful');
//           this.orgAdmins = [];
//           this.stats.totalOrgAdmins = 0;
//           this.stats.activeOrgAdmins = 0;
//         }
//       },
//       error: (error: any) => {
//         this.isLoadingOrgAdmins = false;
//         console.error('❌ Error loading OrgAdmins:', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load organization admins');
//         this.orgAdmins = [];
//         this.stats.totalOrgAdmins = 0;
//         this.stats.activeOrgAdmins = 0;
//       }
//     });
//   }

//   applyOrgAdminSearchAndPagination(): void {
//     let data = [...this.orgAdmins];

//     const term = this.orgAdminSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(admin =>
//         (admin.fullName || '').toLowerCase().includes(term) ||
//         (admin.email || '').toLowerCase().includes(term) ||
//         (admin.companyName || '').toLowerCase().includes(term) ||
//         (admin.phone || '').toLowerCase().includes(term)
//       );
//     }

//     this.filteredOrgAdmins = data;
//     const start = (this.orgAdminPage - 1) * this.orgAdminPageSize;
//     this.pagedOrgAdmins = data.slice(start, start + this.orgAdminPageSize);
//   }

//   get orgAdminTotalPages(): number {
//     return Math.ceil(this.filteredOrgAdmins.length / this.orgAdminPageSize);
//   }

//   onOrgAdminPageChange(page: number): void {
//     if (page >= 1 && page <= this.orgAdminTotalPages) {
//       this.orgAdminPage = page;
//       this.applyOrgAdminSearchAndPagination();
//     }
//   }

//   onOrgAdminPageSizeChange(size: number): void {
//     this.orgAdminPageSize = size;
//     this.orgAdminPage = 1;
//     this.applyOrgAdminSearchAndPagination();
//   }

//   openCreateOrgAdminModal(): void {
//     console.log('🔵 Opening CREATE ORG ADMIN modal');
//     this.orgAdminForm.reset();
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.isSubmitting = false;
//     this.showModal = true;
//   }

//   closeModal(): void {
//     console.log('🔴 Closing modal');
//     this.showModal = false;
//     this.orgAdminForm.reset();
//     this.errorMessage = null;
//     this.successMessage = null;
//     this.isSubmitting = false;
//   }
  

//   submitOrgAdmin(): void {
//     console.log('📤 Submit Org Admin Called');

//     if (this.orgAdminForm.invalid) {
//       this.orgAdminForm.markAllAsTouched();
//       console.log('❌ Form is invalid');
//       this.errorMessage = 'Please fill all required fields correctly';
//       return;
//     }

//     this.isSubmitting = true;
//     this.errorMessage = null;
//     this.successMessage = null;

//     const orgAdminData = this.orgAdminForm.value;

//     console.log('📤 Creating OrganizationAdmin:', orgAdminData);

//     this.dataService.superAdminCreateOrgAdmin(orgAdminData).subscribe({
//       next: (response: any) => {
//         console.log('✅ OrgAdmin Created Response:', response);
        
//         if (response?.success) {
//           this.successMessage = 'Organization Admin created successfully!';
//           this.messageService.showMessage('success', 'Success', 'Organization Admin created successfully!');
          
//           // Wait a moment for the success message to show, then close and reload
//           setTimeout(() => {
//             this.isSubmitting = false;
//             this.closeModal();
//             this.loadOrgAdmins(); // Reload the list
//           }, 1000);
//         } else {
//           this.isSubmitting = false;
//           this.errorMessage = response?.message || 'Failed to create admin';
//           console.error('❌ Creation failed:', response);
//         }
//       },
//       error: (error: any) => {
//         this.isSubmitting = false;
//         console.error('❌ Error creating OrgAdmin:', error);
//         const errorMsg = error.error?.error || error.error?.message || 'Failed to create admin';
//         this.errorMessage = errorMsg;
//         this.messageService.showMessage('error', 'Error', errorMsg);
//       }
//     });
//   }

//   // ==================== BUYER METHODS ====================

//   /**
//    * ✅ NEW METHOD: Load ALL buyers across all organizations (SuperAdmin view)
//    * This is different from Organization Admin who only sees their own buyers
//    */
//   loadAllBuyers(): void {
//     console.log('%c[LOADING ALL BUYERS - SUPERADMIN]', 'color: #ff6600; font-weight: bold;');
//     this.isLoadingBuyers = true;
    
//     // ✅ OPTION 1: If you have a dedicated SuperAdmin endpoint
//     // this.buyerService.getAllBuyersForSuperAdmin().subscribe({
    
//     // ✅ OPTION 2: Aggregate buyers from all organization admins
//     // For now, let's aggregate from all org admins
//     this.loadBuyersFromAllOrganizations();
//   }

//   /**
//    * ✅ Load buyers from all organization admins
//    */
//   private loadBuyersFromAllOrganizations(): void {
//     if (!this.orgAdmins || this.orgAdmins.length === 0) {
//       console.log('%c[INFO] No organization admins yet, cannot load buyers', 'color: #0066cc;');
//       this.isLoadingBuyers = false;
//       this.buyers = [];
//       this.stats.totalBuyers = 0;
//       this.applyBuyerSearchAndPagination();
//       return;
//     }

//     console.log('%c[LOADING BUYERS] From all organizations...', 'color: #0066cc;');
    
//     const allBuyers: Buyer[] = [];
//     let completedRequests = 0;
//     const totalRequests = this.orgAdmins.length;

//     this.orgAdmins.forEach((admin: any) => {
//       this.buyerService.getBuyersByOrganizationAdmin(admin.id).subscribe({
//         next: (buyers: Buyer[]) => {
//           console.log(`✅ Loaded ${buyers.length} buyers for admin ${admin.id} (${admin.companyName})`);
//           allBuyers.push(...buyers);
//           completedRequests++;

//           if (completedRequests === totalRequests) {
//             this.handleAllBuyersLoaded(allBuyers);
//           }
//         },
//         error: (err: any) => {
//           console.error(`❌ Failed to load buyers for admin ${admin.id}:`, err);
//           completedRequests++;

//           if (completedRequests === totalRequests) {
//             this.handleAllBuyersLoaded(allBuyers);
//           }
//         }
//       });
//     });
//   }

//   /**
//    * ✅ Handle completion of loading all buyers
//    */
//   private handleAllBuyersLoaded(allBuyers: Buyer[]): void {
//     console.log('%c[SUCCESS] All buyers loaded:', 'color: #00aa00; font-weight: bold;', allBuyers.length);
//     this.isLoadingBuyers = false;
//     this.buyers = allBuyers;
//     this.stats.totalBuyers = this.buyers.length;
//     this.applyBuyerSearchAndPagination();
//   }

//   private transformBuyerDataForDisplay(buyers: Buyer[]): any[] {
//     console.log('%c[TRANSFORM] Transforming buyer data...', 'color: #0066cc;');
//     const displayRows: any[] = [];

//     if (!buyers || !Array.isArray(buyers)) {
//       console.warn('Invalid buyers data:', buyers);
//       return displayRows;
//     }

//     buyers.forEach(buyer => {
//       if (!buyer) {
//         return;
//       }

//       const buyerBase = {
//         buyerId: buyer.id,
//         buyerName: buyer.companyName || 'N/A',
//         buyerSector: buyer.companyType || 'N/A',
//         buyerAddress: this.formatAddress(buyer.addressLine1, buyer.city, buyer.state),
//         organizationCompany: buyer.organizationCompanyName || 'N/A', // ✅ Added organization info
//         original: buyer
//       };

//       // No locations
//       if (!buyer.locations || buyer.locations.length === 0) {
//         displayRows.push({
//           ...buyerBase,
//           locationName: '—',
//           departmentName: '—',
//           userName: '—',
//           userEmail: '—',
//           userPhone: '—'
//         });
//         return;
//       }

//       // Has locations
//       buyer.locations.forEach((location: any) => {
//         if (!location) {
//           return;
//         }

//         // Location has no departments
//         if (!location.departments || location.departments.length === 0) {
//           displayRows.push({
//             ...buyerBase,
//             locationName: location.locationName || 'N/A',
//             departmentName: '—',
//             userName: '—',
//             userEmail: '—',
//             userPhone: '—'
//           });
//           return;
//         }

//         // Location has departments
//         location.departments.forEach((department: any) => {
//           if (!department) {
//             return;
//           }

//           // Department has no users
//           if (!department.users || department.users.length === 0) {
//             displayRows.push({
//               ...buyerBase,
//               locationName: location.locationName || 'N/A',
//               departmentName: department.departmentName || 'N/A',
//               userName: '—',
//               userEmail: '—',
//               userPhone: '—'
//             });
//             return;
//           }

//           // Department has users
//           department.users.forEach((user: any) => {
//             if (!user) {
//               return;
//             }
            
//             displayRows.push({
//               ...buyerBase,
//               locationName: location.locationName || 'N/A',
//               departmentName: department.departmentName || 'N/A',
//               userName: `${user.firstName || ''} ${user.lastName || ''}`.trim(),
//               userEmail: user.email || 'N/A',
//               userPhone: user.phone || 'N/A'
//             });
//           });
//         });
//       });
//     });

//     console.log('%c[RESULT] Transformed rows:', 'color: #00aa00;', displayRows.length);
//     return displayRows;
//   }

//   /**
//    * ✅ Helper: Format address safely
//    */
//   private formatAddress(line1?: string, city?: string, state?: string): string {
//     const parts = [line1, city, state].filter(p => p && p.trim().length > 0);
//     return parts.length > 0 ? parts.join(', ') : 'N/A';
//   }

//   applyBuyerSearchAndPagination(): void {
//     let data = this.transformBuyerDataForDisplay(this.buyers);

//     const term = this.buyerSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(row => 
//         (row.buyerName || '').toLowerCase().includes(term) ||
//         (row.buyerSector || '').toLowerCase().includes(term) ||
//         (row.buyerAddress || '').toLowerCase().includes(term) ||
//         (row.organizationCompany || '').toLowerCase().includes(term) ||
//         (row.locationName || '').toLowerCase().includes(term) ||
//         (row.departmentName || '').toLowerCase().includes(term) ||
//         (row.userName || '').toLowerCase().includes(term) ||
//         (row.userEmail || '').toLowerCase().includes(term)
//       );
//     }

//     this.filteredBuyers = data;
//     const start = (this.buyerPage - 1) * this.buyerPageSize;
//     this.pagedBuyers = data.slice(start, start + this.buyerPageSize);
    
//     console.log(`%c[PAGINATION] Page ${this.buyerPage}: ${this.pagedBuyers.length} items of ${data.length}`, 'color: #0066cc;');
//   }

//   navigateToCreateBuyer(): void {
//     this.router.navigate(['/create-b']);
//   }

//   editBuyer(row: any): void {
//     console.log('Editing buyer:', row.buyerId);
//     this.router.navigate([`/create-b/edit/${row.buyerId}`]);
//   }

//   deleteBuyer(row: any): void {
//     const confirmMessage = `Are you sure you want to delete ${row.buyerName}?\n\nOrganization: ${row.organizationCompany}\nAddress: ${row.buyerAddress || 'N/A'}\n\nThis will delete all associated locations, departments, and users.`;

//     if (!confirm(confirmMessage)) {
//       return;
//     }

//     this.buyerService.deleteBuyer(row.buyerId).subscribe({
//       next: () => {
//         this.loadAllBuyers(); // ✅ Reload all buyers
//         this.messageService.showMessage('success', 'Success', 'Buyer deleted successfully');
//       },
//       error: (err) => {
//         console.error('Error deleting buyer:', err);
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete buyer. Please try again.');
//       }
//     });
//   }

//   // ==================== BUYER PAGINATION ====================

//   get buyerTotalPages(): number {
//     return Math.ceil(this.filteredBuyers.length / this.buyerPageSize);
//   }

//   onBuyerPageChange(page: number): void {
//     if (page >= 1 && page <= this.buyerTotalPages) {
//       this.buyerPage = page;
//       this.applyBuyerSearchAndPagination();
//     }
//   }

//   onBuyerPageSizeChange(size: number): void {
//     this.buyerPageSize = size;
//     this.buyerPage = 1;
//     this.applyBuyerSearchAndPagination();
//   }

//   // ==================== EXCEL EXPORT ====================

//   get buyerExcelData(): any[] {
//     return this.prepareBuyerExcelData();
//   }

//   get buyerExcelFileName(): string {
//     const date = new Date().toISOString().split('T')[0];
//     return `All_Buyers_Complete_Details_${date}.xlsx`;
//   }

//   private prepareBuyerExcelData(): any[] {
//     const excelData: any[] = [];

//     this.buyers.forEach(buyer => {
//       if (!buyer.locations || buyer.locations.length === 0) {
//         excelData.push(this.createBuyerExcelRow(buyer, null, null, null));
//       } else {
//         buyer.locations.forEach((location: any) => {
//           if (!location.departments || location.departments.length === 0) {
//             excelData.push(this.createBuyerExcelRow(buyer, location, null, null));
//           } else {
//             location.departments.forEach((department: any) => {
//               if (!department.users || department.users.length === 0) {
//                 excelData.push(this.createBuyerExcelRow(buyer, location, department, null));
//               } else {
//                 department.users.forEach((user: any) => {
//                   excelData.push(this.createBuyerExcelRow(buyer, location, department, user));
//                 });
//               }
//             });
//           }
//         });
//       }
//     });

//     return excelData;
//   }

//   private createBuyerExcelRow(buyer: Buyer, location: any, department: any, user: any): any {
//     return {
//       'Buyer ID': buyer.id || 'N/A',
//       'Company Name': buyer.companyName || 'N/A',
//       'Organization': buyer.organizationCompanyName || 'N/A', // ✅ Added
//       'Company Type': buyer.companyType || 'N/A',
//       'Contact Person': buyer.contactPersonName || 'N/A',
//       'Designation': buyer.contactPersonDesignation || 'N/A',
//       'Contact Email': buyer.contactPersonEmail || 'N/A',
//       'Contact Phone': buyer.contactPersonPhone || 'N/A',
//       'Address Line 1': buyer.addressLine1 || 'N/A',
//       'Address Line 2': buyer.addressLine2 || 'N/A',
//       'City': buyer.city || 'N/A',
//       'State': buyer.state || 'N/A',
//       'Postal Code': buyer.postalCode || 'N/A',
//       'Country': buyer.country || 'N/A',
//       'GST Number': buyer.gstNumber || 'N/A',
//       'PAN Number': buyer.panNumber || 'N/A',
//       'CIN Number': buyer.cinNumber || 'N/A',
//       'Website': buyer.website || 'N/A',
//       'Location Name': location?.locationName || 'N/A',
//       'Location Type': location?.locationType || 'N/A',
//       'Location Contact Name': location?.locationContactName || 'N/A',
//       'Location Contact Email': location?.locationContactEmail || 'N/A',
//       'Location Contact Phone': location?.locationContactPhone || 'N/A',
//       'Location Address': location ? `${location.addressLine1}, ${location.city}, ${location.state}` : 'N/A',
//       'Location City': location?.city || 'N/A',
//       'Location State': location?.state || 'N/A',
//       'Location Postal Code': location?.postalCode || 'N/A',
//       'Landline Number': location?.landlineNumber || 'N/A',
//       'Department Name': department?.departmentName || 'N/A',
//       'Department Description': department?.departmentDescription || 'N/A',
//       'Employee ID': user?.employeeId || 'N/A',
//       'First Name': user?.firstName || 'N/A',
//       'Last Name': user?.lastName || 'N/A',
//       'User Email': user?.email || 'N/A',
//       'User Phone': user?.phone || 'N/A',
//       'User Designation': user?.designation || 'N/A',
//       'Gender': user?.gender || 'N/A',
//       'Date of Birth': user?.dateOfBirth || 'N/A',
//       'User Address': user?.addressLine1 || 'N/A',
//       'User City': user?.city || 'N/A',
//       'User State': user?.state || 'N/A',
//       'User Postal Code': user?.postalCode || 'N/A'
//     };
//   }

//   // ==================== UTILITY METHODS ====================

//   getInitials(name: string): string {
//     if (!name) return 'NA';
//     const parts = name.split(' ');
//     if (parts.length >= 2) {
//       return (parts[0][0] + parts[1][0]).toUpperCase();
//     }
//     return name.substring(0, 2).toUpperCase();
//   }

//   formatDate(dateStr: string): string {
//     if (!dateStr) return 'N/A';
//     try {
//       const date = new Date(dateStr);
//       if (isNaN(date.getTime())) return 'N/A';
//       return date.toLocaleDateString('en-US', { 
//         year: 'numeric', 
//         month: 'short', 
//         day: 'numeric' 
//       });
//     } catch (e) {
//       return 'N/A';
//     }
//   }

//   onModalClose(visible: boolean): void {
//     if (!visible) {
//       this.closeModal();
//     }
//   }
// }

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