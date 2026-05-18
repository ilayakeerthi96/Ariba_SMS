
// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { TableModule } from '@coreui/angular'; 
// import { BuyerService } from './buyer-b.service';
// import { SupplierService } from './supplier.service';
// import { Buyer } from './buyer-b.model';
// import { Supplier } from './supplier.model';
// import { MessageService } from '../../../shared/service/message.service';
// import { Router } from '@angular/router';
// import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';

// @Component({
//   selector: 'app-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule, TableModule, DownloadExcelComponent],
//   templateUrl: './dashboard.component.html',
//   styleUrls: ['./dashboard.component.css']
// })
// export class DashboardComponent implements OnInit {

//   // ✅ ADD: User Details (MISSING in original code)
//   fullName: string = '';
//   email: string = '';
//   companyName: string = '';
//   phone: string = '';
//   role: string = 'Organization Administrator';

//   // Buyer properties
//   buyers: Buyer[] = [];
//   filteredBuyers: any[] = [];
//   pagedBuyers: any[] = [];
//   buyerSearchText = '';
//   buyerPage = 1;
//   buyerPageSize = 5;

//   // Supplier properties
//   suppliers: Supplier[] = [];
//   filteredSuppliers: any[] = [];
//   pagedSuppliers: any[] = [];
//   supplierSearchText = '';
//   supplierPage = 1;
//   supplierPageSize = 5;
  
//   // Organization Admin ID
//   orgAdminId: number | null = null;
  
//   // Expose Math to template
//   Math = Math;

//   constructor(
//     private buyerService: BuyerService,
//     private supplierService: SupplierService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.loadUserDetails(); // ✅ CRITICAL FIX: Load user details FIRST
//     this.getOrgAdminId();
//     this.loadBuyers();
//     this.loadSuppliers();
//   }

//   // ==================== ✅ NEW: LOAD USER DETAILS ====================
//   /**
//    * ✅ CRITICAL FIX: Load user details from localStorage
//    * This was missing in the original dashboard component!
//    */
//   loadUserDetails(): void {
//     console.log('%c[LOADING USER DETAILS]', 'color: #0066cc; font-weight: bold;');
    
//     // Get user details from localStorage
//     this.fullName = localStorage.getItem('fullName') || 'NA';
//     this.email = localStorage.getItem('email') || '';
//     this.companyName = localStorage.getItem('companyName') || '';
//     this.phone = localStorage.getItem('phone') || '';
    
//     console.log('User Details Loaded:', {
//       fullName: this.fullName,
//       email: this.email,
//       companyName: this.companyName,
//       phone: this.phone
//     });

//     // ✅ VALIDATE: Check if user details exist
//     if (!this.email || !this.companyName) {
//       console.warn('%c[WARNING] User details incomplete in localStorage', 'color: #ff9900;');
//     }
//   }

//   // ==================== GET INITIALS (REQUIRED BY TEMPLATE) ====================
//   getInitials(name: string): string {
//     if (!name || name === 'NA') return 'NA';
//     const parts = name.trim().split(' ');
//     if (parts.length >= 2) {
//       return (parts[0][0] + parts[1][0]).toUpperCase();
//     }
//     return name.substring(0, 2).toUpperCase();
//   }

//   // ==================== GET ORG ADMIN ID ====================
//   /**
//    * Get the logged-in Organization Admin ID
//    */
//   private getOrgAdminId(): void {
//     try {
//       console.log('%c[GET ORG ADMIN ID]', 'color: #0066cc; font-weight: bold;');
      
//       // ✅ FIX: Try multiple possible keys
//       const adminId = localStorage.getItem('orgAdminId') || 
//                      localStorage.getItem('adminId') ||
//                      localStorage.getItem('userId') ||
//                      localStorage.getItem('id'); // Try 'id' as well
      
//       console.log('Found adminId in localStorage:', adminId);
//       console.log('All localStorage keys:', Object.keys(localStorage));

//       if (!adminId || adminId === 'null' || adminId === 'undefined') {
//         console.error('%c[ERROR] No Organization Admin ID found', 'color: #cc0000; font-weight: bold;');
//         console.log('Available localStorage:', {
//           orgAdminId: localStorage.getItem('orgAdminId'),
//           adminId: localStorage.getItem('adminId'),
//           userId: localStorage.getItem('userId'),
//           id: localStorage.getItem('id'),
//           email: localStorage.getItem('email')
//         });
//         this.messageService.showMessage('error', 'Error', 'Please login as Organization Admin');
//         // Don't navigate away immediately - let them see the header at least
//         // this.router.navigate(['/login']);
//         return;
//       }

//       this.orgAdminId = Number(adminId);
      
//       if (isNaN(this.orgAdminId) || this.orgAdminId <= 0) {
//         console.error('%c[ERROR] Invalid Organization Admin ID:', 'color: #cc0000;', adminId);
//         this.messageService.showMessage('error', 'Error', 'Invalid admin ID');
//         return;
//       }

//       console.log('%c[✅ ORG ADMIN ID]', 'color: #00aa00; font-weight: bold;', this.orgAdminId);
      
//     } catch (error) {
//       console.error('%c[ERROR] Exception in getOrgAdminId', 'color: #cc0000;', error);
//       this.messageService.showMessage('error', 'Error', 'Failed to get admin details');
//     }
//   }

//   // ==================== BUYER METHODS ====================
  
//   /**
//    * Load buyers for the logged-in Organization Admin
//    */
//   loadBuyers() {
//     console.log('%c[LOADING BUYERS]', 'color: #ff6600; font-weight: bold;');
    
//     // Check if we have org admin ID
//     if (!this.orgAdminId) {
//       console.error('%c[ERROR] Cannot load buyers - No org admin ID', 'color: #cc0000;');
//       console.log('💡 TIP: Check your login response to see what keys are being stored');
//       this.messageService.showMessage('warning', 'Warning', 'Organization Admin ID not found. Some features may not work.');
//       return;
//     }

//     console.log('%c[LOADING] Buyers for Org Admin ID:', 'color: #0066cc; font-weight: bold;', this.orgAdminId);
    
//     this.buyerService.getBuyersByOrganizationAdmin(this.orgAdminId).subscribe({
//       next: (buyers: Buyer[]) => {
//         console.log('%c[SUCCESS] Buyers loaded:', 'color: #00aa00; font-weight: bold;', buyers);
//         console.log('Total buyers for admin:', buyers.length);
        
//         // Verify data structure
//         if (buyers && Array.isArray(buyers)) {
//           buyers.forEach((buyer, idx) => {
//             console.log(`Buyer ${idx}:`, {
//               id: buyer.id,
//               companyName: buyer.companyName,
//               organizationCompanyName: buyer.organizationCompanyName,
//               locationsCount: buyer.locations?.length || 0,
//               hasNestedData: buyer.locations?.some(l => l.departments?.length) ?? false
//             });
//           });
//         }
        
//         this.buyers = buyers;
//         this.applyBuyerSearchAndPagination();
//       },
//       error: (err: any) => {
//         console.error('%c[ERROR] Failed to load buyers:', 'color: #cc0000; font-weight: bold;', err);
        
//         // More detailed error logging
//         console.error('Error details:', {
//           status: err.status,
//           statusText: err.statusText,
//           message: err.error?.message || err.message,
//           url: err.url
//         });
        
//         this.messageService.showMessage('error', 'Error', 
//           err.error?.message || 'Failed to load buyers. Please try again.');
//       }
//     });
//   }

//   /**
//    * Transform buyer data with robust null/undefined checks
//    */
//   private transformBuyerDataForDisplay(buyers: Buyer[]): any[] {
//     console.log('%c[TRANSFORM] Transforming buyer data...', 'color: #0066cc;');
//     const displayRows: any[] = [];

//     if (!buyers || !Array.isArray(buyers) || buyers.length === 0) {
//       console.warn('❌ No buyers to transform');
//       return displayRows;
//     }

//     buyers.forEach((buyer, buyerIdx) => {
//       if (!buyer) {
//         console.warn(`⚠️ Buyer ${buyerIdx} is null/undefined`);
//         return;
//       }

//       const buyerBase = {
//         buyerId: buyer.id || 0,
//         buyerName: buyer.companyName || 'N/A',
//         buyerSector: buyer.companyType || 'N/A',
//         buyerAddress: this.formatAddress(buyer.addressLine1, buyer.city, buyer.state),
//         original: buyer
//       };

//       // Case 1: No locations
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

//       // Case 2: Has locations
//       buyer.locations.forEach((location: any, locIdx: number) => {
//         if (!location) {
//           console.warn(`⚠️ Location ${locIdx} is null for buyer ${buyer.id}`);
//           return;
//         }

//         const locationBase = {
//           ...buyerBase,
//           locationName: location.locationName || `Location ${locIdx + 1}`
//         };

//         // Case 2a: Location has no departments
//         if (!location.departments || location.departments.length === 0) {
//           displayRows.push({
//             ...locationBase,
//             departmentName: '—',
//             userName: '—',
//             userEmail: '—',
//             userPhone: '—'
//           });
//           return;
//         }

//         // Case 2b: Location has departments
//         location.departments.forEach((department: any, deptIdx: number) => {
//           if (!department) {
//             console.warn(`⚠️ Department ${deptIdx} is null in location ${location.id}`);
//             return;
//           }

//           const departmentBase = {
//             ...locationBase,
//             departmentName: department.departmentName || `Department ${deptIdx + 1}`
//           };

//           // Case 2b-i: Department has no users
//           if (!department.users || department.users.length === 0) {
//             displayRows.push({
//               ...departmentBase,
//               userName: '—',
//               userEmail: '—',
//               userPhone: '—'
//             });
//             return;
//           }

//           // Case 2b-ii: Department has users
//           department.users.forEach((user: any, userIdx: number) => {
//             if (!user) {
//               console.warn(`⚠️ User ${userIdx} is null in department ${department.id}`);
//               return;
//             }
            
//             displayRows.push({
//               ...departmentBase,
//               userName: this.formatUserName(user.firstName, user.lastName),
//               userEmail: user.email || 'N/A',
//               userPhone: user.phone || 'N/A'
//             });
//           });
//         });
//       });
//     });

//     console.log(`%c[RESULT] Transformed ${displayRows.length} rows`, 'color: #00aa00;');
//     return displayRows;
//   }

//   /**
//    * Helper: Format address safely
//    */
//   private formatAddress(line1?: string, city?: string, state?: string): string {
//     const parts = [line1, city, state].filter(p => p && p.trim().length > 0);
//     return parts.length > 0 ? parts.join(', ') : 'N/A';
//   }

//   /**
//    * Helper: Format user name safely
//    */
//   private formatUserName(firstName?: string, lastName?: string): string {
//     const parts = [firstName, lastName].filter(p => p && p.trim().length > 0);
//     return parts.length > 0 ? parts.join(' ') : 'N/A';
//   }

//   /**
//    * Apply search and pagination with proper checks
//    */
//   applyBuyerSearchAndPagination() {
//     let data = this.transformBuyerDataForDisplay(this.buyers);

//     const term = this.buyerSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(row => 
//         (row.buyerName || '').toLowerCase().includes(term) ||
//         (row.buyerSector || '').toLowerCase().includes(term) ||
//         (row.buyerAddress || '').toLowerCase().includes(term) ||
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

//   editBuyer(row: any) {
//     console.log('Editing buyer:', row.buyerId);
//     this.router.navigate([`/create-b/edit/${row.buyerId}`]);
//   }

//   deleteBuyer(row: any) {
//     const confirmMessage = `Are you sure you want to delete ${row.buyerName}?\n\nAddress: ${row.buyerAddress || 'N/A'}\n\nThis will delete all associated locations, departments, and users.`;

//     if (!confirm(confirmMessage)) {
//       return;
//     }

//     this.buyerService.deleteBuyer(row.buyerId).subscribe({
//       next: () => {
//         this.loadBuyers();
//         this.messageService.showMessage('success', 'Success', 'Buyer deleted successfully');
//       },
//       error: (err) => {
//         console.error('Error deleting buyer:', err);
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete buyer. Please try again.');
//       }
//     });
//   }

//   // ==================== SUPPLIER METHODS ====================
  
//   loadSuppliers() {
//     console.log('%c[LOADING] Suppliers data...', 'color: #ff6600; font-weight: bold;');
    
//     this.supplierService.getAllSuppliers().subscribe({
//       next: (suppliers: Supplier[]) => {
//         console.log('%c[SUCCESS] Suppliers loaded:', 'color: #00aa00; font-weight: bold;', suppliers);
//         this.suppliers = suppliers;
//         this.applySupplierSearchAndPagination();
//       },
//       error: (err) => {
//         console.error('%c[ERROR] Failed to load suppliers:', 'color: #cc0000; font-weight: bold;', err);
//         this.messageService.showMessage('error', 'Error', 'Failed to load suppliers');
//       }
//     });
//   }

//   private transformSupplierDataForDisplay(suppliers: Supplier[]): any[] {
//     console.log('%c[TRANSFORM] Transforming supplier data...', 'color: #0066cc;');
//     const displayRows: any[] = [];

//     if (!suppliers || !Array.isArray(suppliers)) {
//       return displayRows;
//     }

//     suppliers.forEach(supplier => {
//       if (!supplier) {
//         return;
//       }

//       if (!supplier.locations || supplier.locations.length === 0) {
//         displayRows.push({
//           supplierId: supplier.id,
//           supplierName: supplier.companyName || 'N/A',
//           industrySector: supplier.industrySector || 'N/A',
//           supplierAddress: `${supplier.addressLine1 || ''}, ${supplier.city || ''}, ${supplier.state || ''}`.trim(),
//           locationName: '—',
//           categoryName: '—',
//           userName: '—',
//           userEmail: '—',
//           userPhone: '—',
//           original: supplier
//         });
//         return;
//       }

//       supplier.locations.forEach((location: any) => {
//         if (!location) {
//           return;
//         }

//         if (!location.departments || location.departments.length === 0) {
//           displayRows.push({
//             supplierId: supplier.id,
//             supplierName: supplier.companyName || 'N/A',
//             industrySector: supplier.industrySector || 'N/A',
//             supplierAddress: `${supplier.addressLine1 || ''}, ${supplier.city || ''}, ${supplier.state || ''}`.trim(),
//             locationName: location.locationName || 'N/A',
//             categoryName: '—',
//             userName: '—',
//             userEmail: '—',
//             userPhone: '—',
//             original: supplier
//           });
//           return;
//         }

//         location.departments.forEach((department: any) => {
//           if (!department) {
//             return;
//           }

//           if (!department.users || department.users.length === 0) {
//             displayRows.push({
//               supplierId: supplier.id,
//               supplierName: supplier.companyName || 'N/A',
//               industrySector: supplier.industrySector || 'N/A',
//               supplierAddress: `${supplier.addressLine1 || ''}, ${supplier.city || ''}, ${supplier.state || ''}`.trim(),
//               locationName: location.locationName || 'N/A',
//               categoryName: department.departmentName || 'N/A',
//               userName: '—',
//               userEmail: '—',
//               userPhone: '—',
//               original: supplier
//             });
//             return;
//           }

//           department.users.forEach((user: any) => {
//             if (!user) {
//               return;
//             }

//             displayRows.push({
//               supplierId: supplier.id,
//               supplierName: supplier.companyName || 'N/A',
//               industrySector: supplier.industrySector || 'N/A',
//               supplierAddress: `${supplier.addressLine1 || ''}, ${supplier.city || ''}, ${supplier.state || ''}`.trim(),
//               locationName: location.locationName || 'N/A',
//               categoryName: department.departmentName || 'N/A',
//               userName: `${user.firstName || ''} ${user.lastName || ''}`.trim(),
//               userEmail: user.email || 'N/A',
//               userPhone: user.phone || 'N/A',
//               original: supplier
//             });
//           });
//         });
//       });
//     });

//     console.log('%c[RESULT] Transformed rows:', 'color: #00aa00;', displayRows.length);
//     return displayRows;
//   }

//   applySupplierSearchAndPagination() {
//     let data = this.transformSupplierDataForDisplay(this.suppliers);

//     const term = this.supplierSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(row =>
//         (row.supplierName || '').toLowerCase().includes(term) ||
//         (row.industrySector || '').toLowerCase().includes(term) ||
//         (row.supplierAddress || '').toLowerCase().includes(term) ||
//         (row.locationName || '').toLowerCase().includes(term) ||
//         (row.categoryName || '').toLowerCase().includes(term) ||
//         (row.userName || '').toLowerCase().includes(term) ||
//         (row.userEmail || '').toLowerCase().includes(term)
//       );
//     }

//     this.filteredSuppliers = data;
//     const start = (this.supplierPage - 1) * this.supplierPageSize;
//     this.pagedSuppliers = data.slice(start, start + this.supplierPageSize);
//   }

//   editSupplier(row: any) {
//     this.router.navigate([`/create-s/edit/${row.supplierId}`]);
//   }

//   deleteSupplier(row: any) {
//     const confirmMessage = `Are you sure you want to delete ${row.supplierName}?\n\nAddress: ${row.supplierAddress || 'N/A'}\n\nThis will delete all associated locations, categories, and contacts.`;

//     if (!confirm(confirmMessage)) {
//       return;
//     }

//     this.supplierService.deleteSupplier(row.supplierId).subscribe({
//       next: () => {
//         this.loadSuppliers();
//         this.messageService.showMessage('success', 'Success', 'Supplier deleted successfully');
//       },
//       error: (err) => {
//         console.error('Error deleting supplier:', err);
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete supplier. Please try again.');
//       }
//     });
//   }

//   // ==================== PAGINATION ====================

//   get buyerTotalPages(): number {
//     return Math.ceil(this.filteredBuyers.length / this.buyerPageSize);
//   }

//   get supplierTotalPages(): number {
//     return Math.ceil(this.filteredSuppliers.length / this.supplierPageSize);
//   }

//   onBuyerPageChange(page: number): void {
//     if (page >= 1 && page <= this.buyerTotalPages) {
//       this.buyerPage = page;
//       this.applyBuyerSearchAndPagination();
//     }
//   }

//   onSupplierPageChange(page: number): void {
//     if (page >= 1 && page <= this.supplierTotalPages) {
//       this.supplierPage = page;
//       this.applySupplierSearchAndPagination();
//     }
//   }

//   onBuyerPageSizeChange(size: number): void {
//     this.buyerPageSize = size;
//     this.buyerPage = 1;
//     this.applyBuyerSearchAndPagination();
//   }

//   onSupplierPageSizeChange(size: number): void {
//     this.supplierPageSize = size;
//     this.supplierPage = 1;
//     this.applySupplierSearchAndPagination();
//   }

//   // ==================== EXCEL EXPORT METHODS ====================

//   prepareBuyerExcelData(): any[] {
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

//   prepareSupplierExcelData(): any[] {
//     const excelData: any[] = [];

//     this.suppliers.forEach(supplier => {
//       if (!supplier.locations || supplier.locations.length === 0) {
//         excelData.push(this.createSupplierExcelRow(supplier, null, null, null));
//       } else {
//         supplier.locations.forEach((location: any) => {
//           if (!location.departments || location.departments.length === 0) {
//             excelData.push(this.createSupplierExcelRow(supplier, location, null, null));
//           } else {
//             location.departments.forEach((department: any) => {
//               if (!department.users || department.users.length === 0) {
//                 excelData.push(this.createSupplierExcelRow(supplier, location, department, null));
//               } else {
//                 department.users.forEach((user: any) => {
//                   excelData.push(this.createSupplierExcelRow(supplier, location, department, user));
//                 });
//               }
//             });
//           }
//         });
//       }
//     });

//     return excelData;
//   }

//   private createSupplierExcelRow(supplier: Supplier, location: any, department: any, user: any): any {
//     return {
//       'Supplier ID': supplier.id || 'N/A',
//       'Company Name': supplier.companyName || 'N/A',
//       'Industry Sector': supplier.industrySector || 'N/A',
//       'Contact Person': supplier.contactPersonName || 'N/A',
//       'Designation': supplier.contactPersonDesignation || 'N/A',
//       'Contact Email': supplier.contactPersonEmail || 'N/A',
//       'Contact Phone': supplier.contactPersonPhone || 'N/A',
//       'Address Line 1': supplier.addressLine1 || 'N/A',
//       'Address Line 2': supplier.addressLine2 || 'N/A',
//       'City': supplier.city || 'N/A',
//       'State': supplier.state || 'N/A',
//       'Postal Code': supplier.postalCode || 'N/A',
//       'Country': supplier.country || 'N/A',
//       'GST Number': supplier.gstNumber || 'N/A',
//       'PAN Number': supplier.panNumber || 'N/A',
//       'Website': supplier.website || 'N/A',
//       'Location Name': location?.locationName || 'N/A',
//       'Location Type': location?.locationType || 'N/A',
//       'Location Contact Name': location?.locationContactName || 'N/A',
//       'Location Contact Email': location?.locationContactEmail || 'N/A',
//       'Location Contact Phone': location?.locationContactPhone || 'N/A',
//       'Location Address': location ? `${location.addressLine1}, ${location.city}, ${location.state}` : 'N/A',
//       'Location City': location?.city || 'N/A',
//       'Location State': location?.state || 'N/A',
//       'Landline Number': location?.landlineNumber || 'N/A',
//       'Category Name': department?.departmentName || 'N/A',
//       'Category Description': department?.departmentDescription || 'N/A',
//       'Contact ID': user?.employeeId || 'N/A',
//       'First Name': user?.firstName || 'N/A',
//       'Last Name': user?.lastName || 'N/A',
//       'Email': user?.email || 'N/A',
//       'Phone': user?.phone || 'N/A',
//       'Contact Designation': user?.designation || 'N/A',
//       'Gender': user?.gender || 'N/A',
//       'Date of Birth': user?.dateOfBirth || 'N/A',
//       'Contact Address': user?.addressLine1 || 'N/A',
//       'Contact City': user?.city || 'N/A',
//       'Contact State': user?.state || 'N/A',
//       'Contact Postal Code': user?.postalCode || 'N/A'
//     };
//   }

//   get buyerExcelData(): any[] {
//     return this.prepareBuyerExcelData();
//   }

//   get supplierExcelData(): any[] {
//     return this.prepareSupplierExcelData();
//   }

//   get buyerExcelFileName(): string {
//     const date = new Date().toISOString().split('T')[0];
//     return `Buyers_Complete_Details_${date}.xlsx`;
//   }

//   get supplierExcelFileName(): string {
//     const date = new Date().toISOString().split('T')[0];
//     return `Suppliers_Complete_Details_${date}.xlsx`;
//   }
// }


import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from '@coreui/angular';
import { BuyerService } from './buyer-b.service';
import { SupplierService } from './supplier.service';
import { Buyer } from './buyer-b.model';
import { Supplier } from './supplier.model';
import { MessageService } from '../../../shared/service/message.service';
import { Router } from '@angular/router';
import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, DownloadExcelComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  fullName: string = '';
  email: string = '';
  companyName: string = '';
  phone: string = '';
  role: string = 'Organization Administrator';

  // Buyer properties
  buyers: Buyer[] = [];
  filteredBuyers: any[] = [];
  pagedBuyers: any[] = [];
  buyerSearchText = '';
  buyerPage = 1;
  buyerPageSize = 5;

  // ✅ Supplier properties — allSuppliers holds ALL (for admin view),
  //    suppliers holds only APPROVED (for the table display).
  allSuppliers: Supplier[] = [];   // all statuses — fetched from admin endpoint
  suppliers: Supplier[] = [];      // APPROVED only — shown in table
  filteredSuppliers: any[] = [];
  pagedSuppliers: any[] = [];
  supplierSearchText = '';
  supplierPage = 1;
  supplierPageSize = 5;

  orgAdminId: number | null = null;
  Math = Math;

  constructor(
    private buyerService: BuyerService,
    private supplierService: SupplierService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserDetails();
    this.getOrgAdminId();
    this.loadBuyers();
    this.loadSuppliers();
  }

  // ── User details ──────────────────────────────────────────────

  loadUserDetails(): void {
    this.fullName    = localStorage.getItem('fullName') || 'NA';
    this.email       = localStorage.getItem('email') || '';
    this.companyName = localStorage.getItem('companyName') || '';
    this.phone       = localStorage.getItem('phone') || '';
  }

  getInitials(name: string): string {
    if (!name || name === 'NA') return 'NA';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
    return name.substring(0, 2).toUpperCase();
  }

  private getOrgAdminId(): void {
    const adminId = localStorage.getItem('orgAdminId') ||
                    localStorage.getItem('adminId') ||
                    localStorage.getItem('userId') ||
                    localStorage.getItem('id');

    if (!adminId || adminId === 'null' || adminId === 'undefined') {
      this.messageService.showMessage('error', 'Error', 'Please login as Organization Admin');
      return;
    }
    this.orgAdminId = Number(adminId);
  }

  // ── Buyers ───────────────────────────────────────────────────

  loadBuyers() {
    if (!this.orgAdminId) {
      this.messageService.showMessage('warning', 'Warning', 'Organization Admin ID not found.');
      return;
    }
    this.buyerService.getBuyersByOrganizationAdmin(this.orgAdminId).subscribe({
      next: (buyers: Buyer[]) => {
        this.buyers = buyers;
        this.applyBuyerSearchAndPagination();
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to load buyers.');
      }
    });
  }

  private transformBuyerDataForDisplay(buyers: Buyer[]): any[] {
    const displayRows: any[] = [];
    if (!buyers || !Array.isArray(buyers) || buyers.length === 0) return displayRows;

    buyers.forEach(buyer => {
      if (!buyer) return;
      const buyerBase = {
        buyerId: buyer.id || 0,
        buyerName: buyer.companyName || 'N/A',
        buyerSector: buyer.companyType || 'N/A',
        buyerAddress: this.formatAddress(buyer.addressLine1, buyer.city, buyer.state),
        original: buyer
      };

      if (!buyer.locations || buyer.locations.length === 0) {
        displayRows.push({ ...buyerBase, locationName: '—', departmentName: '—', userName: '—', userEmail: '—', userPhone: '—' });
        return;
      }

      buyer.locations.forEach((location: any) => {
        if (!location) return;
        const locationBase = { ...buyerBase, locationName: location.locationName || 'N/A' };

        if (!location.departments || location.departments.length === 0) {
          displayRows.push({ ...locationBase, departmentName: '—', userName: '—', userEmail: '—', userPhone: '—' });
          return;
        }

        location.departments.forEach((department: any) => {
          if (!department) return;
          const departmentBase = { ...locationBase, departmentName: department.departmentName || 'N/A' };

          if (!department.users || department.users.length === 0) {
            displayRows.push({ ...departmentBase, userName: '—', userEmail: '—', userPhone: '—' });
            return;
          }

          department.users.forEach((user: any) => {
            if (!user) return;
            displayRows.push({
              ...departmentBase,
              userName: this.formatUserName(user.firstName, user.lastName),
              userEmail: user.email || 'N/A',
              userPhone: user.phone || 'N/A'
            });
          });
        });
      });
    });

    return displayRows;
  }

  private formatAddress(line1?: string, city?: string, state?: string): string {
    const parts = [line1, city, state].filter(p => p && p.trim().length > 0);
    return parts.length > 0 ? parts.join(', ') : 'N/A';
  }

  private formatUserName(firstName?: string, lastName?: string): string {
    const parts = [firstName, lastName].filter(p => p && p.trim().length > 0);
    return parts.length > 0 ? parts.join(' ') : 'N/A';
  }

  applyBuyerSearchAndPagination() {
    let data = this.transformBuyerDataForDisplay(this.buyers);
    const term = this.buyerSearchText.toLowerCase().trim();
    if (term) {
      data = data.filter(row =>
        (row.buyerName || '').toLowerCase().includes(term) ||
        (row.buyerSector || '').toLowerCase().includes(term) ||
        (row.buyerAddress || '').toLowerCase().includes(term) ||
        (row.locationName || '').toLowerCase().includes(term) ||
        (row.departmentName || '').toLowerCase().includes(term) ||
        (row.userName || '').toLowerCase().includes(term) ||
        (row.userEmail || '').toLowerCase().includes(term)
      );
    }
    this.filteredBuyers = data;
    const start = (this.buyerPage - 1) * this.buyerPageSize;
    this.pagedBuyers = data.slice(start, start + this.buyerPageSize);
  }

  editBuyer(row: any) { this.router.navigate([`/create-b/edit/${row.buyerId}`]); }

  deleteBuyer(row: any) {
    if (!confirm(`Are you sure you want to delete ${row.buyerName}?\n\nThis will delete all associated locations, departments, and users.`)) return;
    this.buyerService.deleteBuyer(row.buyerId).subscribe({
      next: () => { this.loadBuyers(); this.messageService.showMessage('success', 'Success', 'Buyer deleted successfully'); },
      error: (err: any) => this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete buyer.')
    });
  }

  // ── Suppliers ─────────────────────────────────────────────────

  loadSuppliers() {
    // ✅ getAllSuppliers() on the backend now returns only APPROVED suppliers.
    // The dashboard table therefore only shows approved ones automatically.
    this.supplierService.getAllSuppliers().subscribe({
      next: (suppliers: Supplier[]) => {
        this.suppliers = suppliers;
        this.applySupplierSearchAndPagination();
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', 'Failed to load suppliers');
      }
    });
  }

  private transformSupplierDataForDisplay(suppliers: Supplier[]): any[] {
    const displayRows: any[] = [];
    if (!suppliers || !Array.isArray(suppliers)) return displayRows;

    suppliers.forEach(supplier => {
      if (!supplier) return;

      const supplierBase = {
        supplierId: supplier.id,
        supplierName: supplier.companyName || 'N/A',
        industrySector: supplier.industrySector || 'N/A',
        supplierAddress: `${supplier.addressLine1 || ''}, ${supplier.city || ''}, ${supplier.state || ''}`.trim(),
        // ✅ Include approval status for display
        approvalStatus: (supplier as any).approvalStatus || 'APPROVED',
        original: supplier
      };

      if (!supplier.locations || supplier.locations.length === 0) {
        displayRows.push({ ...supplierBase, locationName: '—', categoryName: '—', userName: '—', userEmail: '—', userPhone: '—' });
        return;
      }

      supplier.locations.forEach((location: any) => {
        if (!location) return;

        if (!location.departments || location.departments.length === 0) {
          displayRows.push({ ...supplierBase, locationName: location.locationName || 'N/A', categoryName: '—', userName: '—', userEmail: '—', userPhone: '—' });
          return;
        }

        location.departments.forEach((department: any) => {
          if (!department) return;

          if (!department.users || department.users.length === 0) {
            displayRows.push({ ...supplierBase, locationName: location.locationName || 'N/A', categoryName: department.departmentName || 'N/A', userName: '—', userEmail: '—', userPhone: '—' });
            return;
          }

          department.users.forEach((user: any) => {
            if (!user) return;
            displayRows.push({
              ...supplierBase,
              locationName: location.locationName || 'N/A',
              categoryName: department.departmentName || 'N/A',
              userName: `${user.firstName || ''} ${user.lastName || ''}`.trim(),
              userEmail: user.email || 'N/A',
              userPhone: user.phone || 'N/A'
            });
          });
        });
      });
    });

    return displayRows;
  }

  applySupplierSearchAndPagination() {
    let data = this.transformSupplierDataForDisplay(this.suppliers);
    const term = this.supplierSearchText.toLowerCase().trim();
    if (term) {
      data = data.filter(row =>
        (row.supplierName || '').toLowerCase().includes(term) ||
        (row.industrySector || '').toLowerCase().includes(term) ||
        (row.supplierAddress || '').toLowerCase().includes(term) ||
        (row.locationName || '').toLowerCase().includes(term) ||
        (row.categoryName || '').toLowerCase().includes(term) ||
        (row.userName || '').toLowerCase().includes(term) ||
        (row.userEmail || '').toLowerCase().includes(term)
      );
    }
    this.filteredSuppliers = data;
    const start = (this.supplierPage - 1) * this.supplierPageSize;
    this.pagedSuppliers = data.slice(start, start + this.supplierPageSize);
  }

  editSupplier(row: any) { this.router.navigate([`/create-s/edit/${row.supplierId}`]); }

  deleteSupplier(row: any) {
    if (!confirm(`Are you sure you want to delete ${row.supplierName}?\n\nThis will delete all associated locations, categories, and contacts.`)) return;
    this.supplierService.deleteSupplier(row.supplierId).subscribe({
      next: () => { this.loadSuppliers(); this.messageService.showMessage('success', 'Success', 'Supplier deleted successfully'); },
      error: (err: any) => this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete supplier.')
    });
  }

  // ── Pagination helpers ────────────────────────────────────────

  get buyerTotalPages(): number { return Math.ceil(this.filteredBuyers.length / this.buyerPageSize); }
  get supplierTotalPages(): number { return Math.ceil(this.filteredSuppliers.length / this.supplierPageSize); }

  onBuyerPageChange(page: number): void {
    if (page >= 1 && page <= this.buyerTotalPages) { this.buyerPage = page; this.applyBuyerSearchAndPagination(); }
  }
  onSupplierPageChange(page: number): void {
    if (page >= 1 && page <= this.supplierTotalPages) { this.supplierPage = page; this.applySupplierSearchAndPagination(); }
  }
  onBuyerPageSizeChange(size: number): void { this.buyerPageSize = size; this.buyerPage = 1; this.applyBuyerSearchAndPagination(); }
  onSupplierPageSizeChange(size: number): void { this.supplierPageSize = size; this.supplierPage = 1; this.applySupplierSearchAndPagination(); }

  // ── Excel export ──────────────────────────────────────────────

  prepareBuyerExcelData(): any[] {
    const excelData: any[] = [];
    this.buyers.forEach(buyer => {
      if (!buyer.locations || buyer.locations.length === 0) {
        excelData.push(this.createBuyerExcelRow(buyer, null, null, null));
      } else {
        buyer.locations.forEach((location: any) => {
          if (!location.departments || location.departments.length === 0) {
            excelData.push(this.createBuyerExcelRow(buyer, location, null, null));
          } else {
            location.departments.forEach((department: any) => {
              if (!department.users || department.users.length === 0) {
                excelData.push(this.createBuyerExcelRow(buyer, location, department, null));
              } else {
                department.users.forEach((user: any) => excelData.push(this.createBuyerExcelRow(buyer, location, department, user)));
              }
            });
          }
        });
      }
    });
    return excelData;
  }

  private createBuyerExcelRow(buyer: Buyer, location: any, department: any, user: any): any {
    return {
      'Buyer ID': buyer.id || 'N/A',
      'Company Name': buyer.companyName || 'N/A',
      'Company Type': buyer.companyType || 'N/A',
      'Contact Person': buyer.contactPersonName || 'N/A',
      'Designation': buyer.contactPersonDesignation || 'N/A',
      'Contact Email': buyer.contactPersonEmail || 'N/A',
      'Contact Phone': buyer.contactPersonPhone || 'N/A',
      'Address Line 1': buyer.addressLine1 || 'N/A',
      'City': buyer.city || 'N/A',
      'State': buyer.state || 'N/A',
      'Postal Code': buyer.postalCode || 'N/A',
      'Country': buyer.country || 'N/A',
      'GST Number': buyer.gstNumber || 'N/A',
      'PAN Number': buyer.panNumber || 'N/A',
      'Website': buyer.website || 'N/A',
      'Location Name': location?.locationName || 'N/A',
      'Location Type': location?.locationType || 'N/A',
      'Location Contact Name': location?.locationContactName || 'N/A',
      'Location Contact Email': location?.locationContactEmail || 'N/A',
      'Location Contact Phone': location?.locationContactPhone || 'N/A',
      'Department Name': department?.departmentName || 'N/A',
      'Department Description': department?.departmentDescription || 'N/A',
      'Employee ID': user?.employeeId || 'N/A',
      'First Name': user?.firstName || 'N/A',
      'Last Name': user?.lastName || 'N/A',
      'User Email': user?.email || 'N/A',
      'User Phone': user?.phone || 'N/A',
      'User Designation': user?.designation || 'N/A',
      'Gender': user?.gender || 'N/A',
      'Date of Birth': user?.dateOfBirth || 'N/A'
    };
  }

  prepareSupplierExcelData(): any[] {
    const excelData: any[] = [];
    this.suppliers.forEach(supplier => {
      if (!supplier.locations || supplier.locations.length === 0) {
        excelData.push(this.createSupplierExcelRow(supplier, null, null, null));
      } else {
        supplier.locations.forEach((location: any) => {
          if (!location.departments || location.departments.length === 0) {
            excelData.push(this.createSupplierExcelRow(supplier, location, null, null));
          } else {
            location.departments.forEach((department: any) => {
              if (!department.users || department.users.length === 0) {
                excelData.push(this.createSupplierExcelRow(supplier, location, department, null));
              } else {
                department.users.forEach((user: any) => excelData.push(this.createSupplierExcelRow(supplier, location, department, user)));
              }
            });
          }
        });
      }
    });
    return excelData;
  }

  private createSupplierExcelRow(supplier: Supplier, location: any, department: any, user: any): any {
    return {
      'Supplier ID': supplier.id || 'N/A',
      'Company Name': supplier.companyName || 'N/A',
      'Approval Status': (supplier as any).approvalStatus || 'N/A',
      'Industry Sector': supplier.industrySector || 'N/A',
      'Contact Person': supplier.contactPersonName || 'N/A',
      'Designation': supplier.contactPersonDesignation || 'N/A',
      'Contact Email': supplier.contactPersonEmail || 'N/A',
      'Contact Phone': supplier.contactPersonPhone || 'N/A',
      'Address Line 1': supplier.addressLine1 || 'N/A',
      'City': supplier.city || 'N/A',
      'State': supplier.state || 'N/A',
      'Postal Code': supplier.postalCode || 'N/A',
      'Country': supplier.country || 'N/A',
      'GST Number': supplier.gstNumber || 'N/A',
      'PAN Number': supplier.panNumber || 'N/A',
      'Website': supplier.website || 'N/A',
      'Location Name': location?.locationName || 'N/A',
      'Location Type': location?.locationType || 'N/A',
      'Location Contact Name': location?.locationContactName || 'N/A',
      'Location Contact Email': location?.locationContactEmail || 'N/A',
      'Location Contact Phone': location?.locationContactPhone || 'N/A',
      'Category Name': department?.departmentName || 'N/A',
      'Category Description': department?.departmentDescription || 'N/A',
      'Contact ID': user?.employeeId || 'N/A',
      'First Name': user?.firstName || 'N/A',
      'Last Name': user?.lastName || 'N/A',
      'Email': user?.email || 'N/A',
      'Phone': user?.phone || 'N/A',
      'Contact Designation': user?.designation || 'N/A',
      'Gender': user?.gender || 'N/A',
      'Date of Birth': user?.dateOfBirth || 'N/A'
    };
  }

  get buyerExcelData(): any[] { return this.prepareBuyerExcelData(); }
  get supplierExcelData(): any[] { return this.prepareSupplierExcelData(); }
  get buyerExcelFileName(): string { return `Buyers_Complete_Details_${new Date().toISOString().split('T')[0]}.xlsx`; }
  get supplierExcelFileName(): string { return `Suppliers_Complete_Details_${new Date().toISOString().split('T')[0]}.xlsx`; }
}