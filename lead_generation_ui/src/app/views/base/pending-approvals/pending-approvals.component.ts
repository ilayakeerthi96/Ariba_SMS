

// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { DataService } from '../../../shared/service/DataService';
// import { RFQService } from '../models/rfq.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';

// interface FYOption {
//   value: string;
//   label: string;
//   from: Date;
//   to: Date;
// }

// @Component({
//   selector: 'app-pending-approvals',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './pending-approvals.component.html',
//   styleUrls: ['./pending-approvals.component.css']
// })
// export class PendingApprovalsComponent implements OnInit {

//   // ==================== USER INFO ====================
//   userId: number = 0;
//   userName: string = '';
//   userEmail: string = '';
//   hierarchyLevelId: number | null = null;
//   hierarchyLevelName: string = '';
//   hierarchyLevelOrder: number | null = null;
//   companyName: string = '';

//   // ==================== RFQ RAW DATA ====================
//   pendingApprovals: any[] = [];
//   holdApprovals: any[] = [];

//   // ==================== PO RAW DATA ====================
//   pendingPOApprovals: any[] = [];
//   holdPOApprovals: any[] = [];

//   // ==================== SUPPLIER RAW DATA ====================
//   pendingSupplierApprovals: any[] = [];
//   holdSupplierApprovals: any[] = [];
//   pendingSupplierCount: number = 0;
//   holdSupplierCount: number = 0;

//   // ==================== DATE-FILTERED ====================
//   dateFilteredPendingApprovals: any[] = [];
//   dateFilteredHoldApprovals: any[] = [];
//   dateFilteredPOApprovals: any[] = [];
//   dateFilteredHoldPOApprovals: any[] = [];
//   dateFilteredSupplierApprovals: any[] = [];
//   dateFilteredHoldSupplierApprovals: any[] = [];

//   // ==================== DISPLAYED ====================
//   filteredApprovals: any[] = [];
//   filteredHoldApprovals: any[] = [];
//   filteredPOApprovals: any[] = [];
//   filteredHoldPOApprovals: any[] = [];
//   filteredSupplierApprovals: any[] = [];
//   filteredHoldSupplierApprovals: any[] = [];

//   activeTab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold' | 'supplier-pending' | 'supplier-hold' = 'rfq-pending';
//   isLoading = false;
//   searchText = '';

//   // ==================== APPROVAL MODAL STATE ====================
//   showApprovalModal = false;
//   selectedItem: any = null;
//   modalMode: 'rfq' | 'po' | 'supplier' = 'rfq';
//   actionType: 'approve' | 'reject' | 'return' | 'hold' | 'release' = 'approve';
//   actionComments = '';
//   isSubmitting = false;

//   isLastApprover: boolean = false;
//   requiresDates: boolean = false;
//   minDueDate: string = '';
//   minDeliveryDate: string = '';
//   rfqDueDate: string = '';
//   rfqDeliveryDate: string = '';
//   allowSupplierDownload: boolean = false;

//   // ==================== SUPPLIER DETAIL MODAL STATE ====================
//   showSupplierDetailModal = false;
//   selectedSupplierDetail: any = null;
//   selectedSupplierApprovalItem: any = null;
//   isLoadingSupplierDetail = false;
//   expandedLocations: { [key: number]: boolean } = {};
//   expandedDepts: { [key: string]: boolean } = {};

//   // ==================== DATE FILTER STATE ====================
//   financialYearOptions: FYOption[] = [];
//   selectedFYOption: string = '';
//   customFromDate: string = '';
//   customToDate: string = '';
//   activeDateRangeLabel: string = '';

//   holdApprovalsCount: number = 0;
//   pendingPOCount: number = 0;
//   holdPOCount: number = 0;

//   constructor(
//     private dataService: DataService,
//     private rfqService: RFQService,
//     private messageService: MessageService,
//     private authService: AuthService,
//     public router: Router,
//     private cdr: ChangeDetectorRef
//   ) {}

//   ngOnInit(): void {
//     this.initializeUser();
//     this.buildFinancialYearOptions();
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.updateActiveDateRangeLabel();
//     this.loadAllData();
//     this.setMinDates();
//   }

//   // =========================================================================
//   //  INITIALIZE USER
//   // =========================================================================

//   private initializeUser(): void {
//     this.userId             = Number(localStorage.getItem('userId')) || 0;
//     this.userName           = localStorage.getItem('fullName') || 'User';
//     this.userEmail          = localStorage.getItem('email') || localStorage.getItem('username') || '';
//     this.companyName        = localStorage.getItem('companyName') || 'NA';
//     this.hierarchyLevelId   = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
//     if (!this.userId) {
//       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//       this.router.navigate(['/login']);
//     }
//   }

//   private setMinDates(): void {
//     const now = new Date();
//     this.minDueDate = this.formatDateForInput(now);
//     this.minDeliveryDate = this.formatDateForInput(now);
//   }

//   private formatDateForInput(date: Date): string {
//     const y   = date.getFullYear();
//     const m   = String(date.getMonth() + 1).padStart(2, '0');
//     const d   = String(date.getDate()).padStart(2, '0');
//     const h   = String(date.getHours()).padStart(2, '0');
//     const min = String(date.getMinutes()).padStart(2, '0');
//     return `${y}-${m}-${d}T${h}:${min}`;
//   }

//   // =========================================================================
//   //  FINANCIAL YEAR HELPERS
//   // =========================================================================

//   private buildFinancialYearOptions(): void {
//     const today = new Date();
//     let currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;
//     this.financialYearOptions = [];
//     for (let i = 0; i < 4; i++) {
//       const startYear = currentFYStartYear - i;
//       const endYear   = startYear + 1;
//       const from = new Date(startYear, 3, 1, 0, 0, 0, 0);
//       const to   = new Date(endYear,   2, 31, 23, 59, 59, 999);
//       this.financialYearOptions.push({
//         value: `FY${startYear}-${String(endYear).slice(-2)}`,
//         label: `FY ${startYear}-${String(endYear).slice(-2)}  (Apr ${startYear} – Mar ${endYear})`,
//         from,
//         to
//       });
//     }
//   }

//   getCurrentFYValue(): string {
//     return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL';
//   }

//   onFYOptionChange(): void {
//     if (this.selectedFYOption !== 'CUSTOM') {
//       this.customFromDate = '';
//       this.customToDate   = '';
//     }
//     this.updateActiveDateRangeLabel();
//     this.applyDateAndSearchFilters();
//   }

//   resetDateFilter(): void {
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.customFromDate   = '';
//     this.customToDate     = '';
//     this.updateActiveDateRangeLabel();
//     this.applyDateAndSearchFilters();
//   }

//   private updateActiveDateRangeLabel(): void {
//     if (this.selectedFYOption === 'ALL') {
//       this.activeDateRangeLabel = 'All Time';
//       return;
//     }
//     if (this.selectedFYOption === 'CUSTOM') {
//       if (this.customFromDate && this.customToDate)
//         this.activeDateRangeLabel = `${this.formatDisplayDate(this.customFromDate)} – ${this.formatDisplayDate(this.customToDate)}`;
//       else if (this.customFromDate)
//         this.activeDateRangeLabel = `From ${this.formatDisplayDate(this.customFromDate)}`;
//       else if (this.customToDate)
//         this.activeDateRangeLabel = `Up to ${this.formatDisplayDate(this.customToDate)}`;
//       else
//         this.activeDateRangeLabel = 'Custom Range';
//       return;
//     }
//     const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
//     this.activeDateRangeLabel = fy ? fy.label : '';
//   }

//   private formatDisplayDate(dateStr: string): string {
//     if (!dateStr) return '';
//     return new Date(dateStr + 'T00:00:00').toLocaleDateString('en-GB', {
//       day: '2-digit', month: 'short', year: 'numeric'
//     });
//   }

//   private getActiveDateRange(): { from: Date; to: Date } | null {
//     if (this.selectedFYOption === 'ALL') return null;
//     if (this.selectedFYOption === 'CUSTOM') {
//       const from = this.customFromDate ? new Date(this.customFromDate + 'T00:00:00') : null;
//       const to   = this.customToDate   ? new Date(this.customToDate   + 'T23:59:59') : null;
//       if (!from && !to) return null;
//       return { from: from ?? new Date(0), to: to ?? new Date(8640000000000000) };
//     }
//     const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
//     return fy ? { from: fy.from, to: fy.to } : null;
//   }

//   private filterByDate(items: any[], dateField: string = 'createdAt'): any[] {
//     const range = this.getActiveDateRange();
//     if (!range) return items;
//     return items.filter((item: any) => {
//       const dateStr = item[dateField] || item.createdAt || item.actionDate;
//       if (!dateStr) return false;
//       const d = new Date(dateStr);
//       return d >= range.from && d <= range.to;
//     });
//   }

//   // =========================================================================
//   //  COMBINED DATE + SEARCH FILTER
//   // =========================================================================

//   applyDateAndSearchFilters(): void {
//     this.updateActiveDateRangeLabel();
//     const term = this.searchText.toLowerCase().trim();

//     const filterArr = (arr: any[], fields: string[]) =>
//       term
//         ? arr.filter(item => fields.some(f => (item[f] || '').toLowerCase().includes(term)))
//         : [...arr];

//     this.dateFilteredPendingApprovals      = this.filterByDate(this.pendingApprovals, 'createdAt');
//     this.dateFilteredHoldApprovals         = this.filterByDate(this.holdApprovals, 'holdDate');
//     this.dateFilteredPOApprovals           = this.filterByDate(this.pendingPOApprovals, 'createdAt');
//     this.dateFilteredHoldPOApprovals       = this.filterByDate(this.holdPOApprovals, 'holdDate');
//     this.dateFilteredSupplierApprovals     = this.filterByDate(this.pendingSupplierApprovals, 'createdAt');
//     this.dateFilteredHoldSupplierApprovals = this.filterByDate(this.holdSupplierApprovals, 'holdDate');

//     this.filteredApprovals             = filterArr(this.dateFilteredPendingApprovals,      ['rfqNumber', 'rfqTitle', 'buyerName']);
//     this.filteredHoldApprovals         = filterArr(this.dateFilteredHoldApprovals,         ['rfqNumber', 'rfqTitle', 'buyerName']);
//     this.filteredPOApprovals           = filterArr(this.dateFilteredPOApprovals,           ['poNumber', 'supplierName', 'buyerName']);
//     this.filteredHoldPOApprovals       = filterArr(this.dateFilteredHoldPOApprovals,       ['poNumber', 'supplierName', 'buyerName']);
//     this.filteredSupplierApprovals     = filterArr(this.dateFilteredSupplierApprovals,     ['supplierCompanyName', 'hierarchyLevelName']);
//     this.filteredHoldSupplierApprovals = filterArr(this.dateFilteredHoldSupplierApprovals, ['supplierCompanyName', 'hierarchyLevelName']);

//     this.cdr.markForCheck();
//   }

//   onSearch(): void {
//     this.applyDateAndSearchFilters();
//   }

//   // =========================================================================
//   //  DATA LOADING
//   // =========================================================================

//   private loadAllData(): void {
//     this.loadPendingApprovals();
//     this.loadHoldApprovals();
//     this.loadPendingPOApprovals();
//     this.loadHoldPOApprovals();
//     this.loadPendingSupplierApprovals();
//     this.loadHoldSupplierApprovals();
//   }

//   loadPendingApprovals(): void {
//     if (!this.userId) return;
//     this.isLoading = true;
//     this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.pendingApprovals = data;
//         this.isLoading = false;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingApprovals = [];
//         this.isLoading = false;
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   loadHoldApprovals(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.holdApprovals      = data;
//         this.holdApprovalsCount = data.length;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.holdApprovals = [];
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   loadPendingPOApprovals(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingPOApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.pendingPOApprovals = data.map((item: any) => ({
//           ...item,
//           poId:           item.purchaseOrderId ?? item.poId ?? item.id,
//           currencyCode:   item.currencyCode   || 'INR',
//           currencySymbol: item.currencySymbol || '₹'
//         }));
//         this.pendingPOCount = this.pendingPOApprovals.length;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingPOApprovals = [];
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   loadHoldPOApprovals(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldPOApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.holdPOApprovals = data.map((item: any) => ({
//           ...item,
//           poId:           item.purchaseOrderId ?? item.poId ?? item.id,
//           currencyCode:   item.currencyCode   || 'INR',
//           currencySymbol: item.currencySymbol || '₹'
//         }));
//         this.holdPOCount = this.holdPOApprovals.length;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.holdPOApprovals = [];
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   loadPendingSupplierApprovals(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingSupplierApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.pendingSupplierApprovals = data;
//         this.pendingSupplierCount     = data.length;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingSupplierApprovals = [];
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   loadHoldSupplierApprovals(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldSupplierApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.holdSupplierApprovals = data;
//         this.holdSupplierCount     = data.length;
//         this.applyDateAndSearchFilters();
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.holdSupplierApprovals = [];
//         this.applyDateAndSearchFilters();
//       }
//     });
//   }

//   // =========================================================================
//   //  SUPPLIER DETAIL MODAL
//   // =========================================================================

//   /**
//    * Opens the supplier detail modal and fetches full supplier data.
//    * Called by the eye (👁) button on the Supplier Pending and Supplier Hold tabs.
//    */
//   viewSupplierDetails(approval: any): void {
//     this.selectedSupplierApprovalItem = approval;
//     this.showSupplierDetailModal      = true;
//     this.selectedSupplierDetail       = null;
//     this.expandedLocations            = {};
//     this.expandedDepts                = {};
//     this.isLoadingSupplierDetail      = true;
//     this.cdr.markForCheck();

//     const supplierId = approval.supplierId;

//     if (!supplierId) {
//       // Fallback: only show what the approval record carries
//       this.selectedSupplierDetail = {
//         id:             approval.supplierId,
//         companyName:    approval.supplierCompanyName,
//         approvalStatus: 'PENDING',
//         locations:      []
//       };
//       this.isLoadingSupplierDetail = false;
//       this.cdr.markForCheck();
//       return;
//     }

//     this.dataService.getSupplierById(supplierId).subscribe({
//       next: (response: any) => {
//         // Handle { data: supplier } wrapper OR direct supplier object
//         if (response && response.data) {
//           this.selectedSupplierDetail = response.data;
//         } else if (response && response.companyName) {
//           this.selectedSupplierDetail = response;
//         } else {
//           this.selectedSupplierDetail = {
//             id:             supplierId,
//             companyName:    approval.supplierCompanyName,
//             approvalStatus: approval.status || 'PENDING',
//             locations:      []
//           };
//         }

//         // Auto-expand first location and its first department for immediate UX
//         if (this.selectedSupplierDetail?.locations?.length > 0) {
//           this.expandedLocations[0] = true;
//           if (this.selectedSupplierDetail.locations[0]?.departments?.length > 0) {
//             this.expandedDepts['0-0'] = true;
//           }
//         }

//         this.isLoadingSupplierDetail = false;
//         this.cdr.markForCheck();

//         // Load logo separately (non-blocking)
//         this.loadSupplierLogo(supplierId);
//       },
//       error: (err: any) => {
//         console.error('Failed to load supplier detail:', err);
//         // Graceful fallback
//         this.selectedSupplierDetail = {
//           id:             supplierId,
//           companyName:    approval.supplierCompanyName,
//           approvalStatus: approval.status || 'PENDING',
//           industrySector: approval.industrySector || '',
//           locations:      []
//         };
//         this.isLoadingSupplierDetail = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   /** Load the supplier logo separately and patch it into selectedSupplierDetail */
//   private loadSupplierLogo(supplierId: number): void {
//     this.dataService.getSupplierLogoBase64(supplierId).subscribe({
//       next: (logoData: any) => {
//         if (logoData && this.selectedSupplierDetail) {
//           this.selectedSupplierDetail = { ...this.selectedSupplierDetail, logoBase64: logoData };
//           this.cdr.markForCheck();
//         }
//       },
//       error: () => { /* logo is optional — silent fail */ }
//     });
//   }

//   /** Close the supplier detail modal and clean up state */
//   closeSupplierDetailModal(): void {
//     this.showSupplierDetailModal      = false;
//     this.selectedSupplierDetail       = null;
//     this.selectedSupplierApprovalItem = null;
//     this.expandedLocations            = {};
//     this.expandedDepts                = {};
//     this.cdr.markForCheck();
//   }

//   /** Toggle a location accordion panel open/closed */
//   toggleLocationExpand(index: number): void {
//     this.expandedLocations[index] = !this.expandedLocations[index];
//     this.cdr.markForCheck();
//   }

//   /** Toggle a department accordion panel open/closed */
//   toggleDeptExpand(locIdx: number, deptIdx: number): void {
//     const key = `${locIdx}-${deptIdx}`;
//     this.expandedDepts[key] = !this.expandedDepts[key];
//     this.cdr.markForCheck();
//   }

//   // =========================================================================
//   //  SUPPLIER DETAIL — DISPLAY HELPERS
//   // =========================================================================

//   /** Get two-letter initials from a full name */
//   getInitials(name: string | undefined | null): string {
//     if (!name || !name.trim()) return '??';
//     const parts = name.trim().split(/\s+/);
//     if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
//     return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
//   }

//   /** Count total departments across all locations of a supplier */
//   getTotalDepartments(supplier: any): number {
//     if (!supplier?.locations) return 0;
//     return supplier.locations.reduce(
//       (sum: number, loc: any) => sum + (loc.departments?.length || 0), 0
//     );
//   }

//   /** Count total users across all locations and departments of a supplier */
//   getTotalUsers(supplier: any): number {
//     if (!supplier?.locations) return 0;
//     return supplier.locations.reduce(
//       (sum: number, loc: any) =>
//         sum + (loc.departments || []).reduce(
//           (s: number, dept: any) => s + (dept.users?.length || 0), 0
//         ),
//       0
//     );
//   }

//   /** Count users within a single location (across all its departments) */
//   getUserCountForLocation(loc: any): number {
//     if (!loc?.departments) return 0;
//     return loc.departments.reduce(
//       (sum: number, dept: any) => sum + (dept.users?.length || 0), 0
//     );
//   }

//   // =========================================================================
//   //  APPROVAL MODAL
//   // =========================================================================

//   openApprovalModal(
//     item: any,
//     action: 'approve' | 'reject' | 'return' | 'hold' | 'release',
//     mode: 'rfq' | 'po' | 'supplier'
//   ): void {
//     this.selectedItem      = item;
//     this.actionType        = action;
//     this.modalMode         = mode;
//     this.actionComments    = '';
//     this.rfqDueDate        = '';
//     this.rfqDeliveryDate   = '';
//     this.isLastApprover    = false;
//     this.requiresDates     = false;
//     this.allowSupplierDownload =
//       item.allowSupplierDownload !== undefined ? item.allowSupplierDownload : true;

//     if (mode === 'rfq' && action === 'approve') {
//       this.checkIfLastApprover(item.rfqId);
//     }

//     this.showApprovalModal = true;
//     this.cdr.markForCheck();
//   }

//   private checkIfLastApprover(rfqId: number): void {
//     this.dataService.isLastApprover(rfqId, this.userId).subscribe({
//       next: (response: any) => {
//         if (response.success) {
//           this.isLastApprover = response.isLastApprover || false;
//           this.requiresDates  = response.requiresDates  || false;
//           this.cdr.markForCheck();
//         }
//       },
//       error: () => { this.isLastApprover = false; }
//     });
//   }

//   onDueDateChange(): void {
//     if (this.rfqDueDate) {
//       this.minDeliveryDate = this.rfqDueDate;
//       if (this.rfqDeliveryDate && this.rfqDeliveryDate < this.rfqDueDate) {
//         this.rfqDeliveryDate = '';
//       }
//       this.cdr.markForCheck();
//     }
//   }

//   closeApprovalModal(): void {
//     this.showApprovalModal = false;
//     this.selectedItem      = null;
//     this.actionComments    = '';
//     this.isSubmitting      = false;
//     this.cdr.markForCheck();
//   }

//   submitApprovalAction(): void {
//     if (!this.selectedItem || !this.userId) {
//       this.messageService.showMessage('error', 'Error', 'Invalid approval data');
//       return;
//     }

//     const requiresComments = ['reject', 'return', 'hold'];
//     if (requiresComments.includes(this.actionType) && !this.actionComments.trim()) {
//       const msgs: any = {
//         reject: 'Please provide rejection reason',
//         return: 'Please provide revision comments',
//         hold:   'Please provide hold remarks'
//       };
//       this.messageService.showMessage('warning', 'Warning', msgs[this.actionType]);
//       return;
//     }

//     if (
//       this.modalMode === 'rfq' &&
//       this.actionType === 'approve' &&
//       this.isLastApprover
//     ) {
//       if (!this.rfqDueDate || !this.rfqDeliveryDate) {
//         this.messageService.showMessage(
//           'error', 'Validation Error',
//           'As final approver, you must set both RFQ dates'
//         );
//         return;
//       }
//     }

//     this.isSubmitting = true;
//     const comments    = this.actionComments.trim() || 'No comments provided';
//     let apiCall: any;

//     // ── RFQ actions ─────────────────────────────────────────────
//     if (this.modalMode === 'rfq') {
//       const rfqId = this.selectedItem.rfqId;
//       if (!rfqId) {
//         this.messageService.showMessage('error', 'Error', 'Invalid RFQ ID');
//         this.isSubmitting = false;
//         return;
//       }
//       if (this.actionType === 'approve')
//         apiCall = this.dataService.approveRFQWithDates(
//           rfqId, this.userId, comments,
//           this.rfqDueDate || undefined,
//           this.rfqDeliveryDate || undefined,
//           this.allowSupplierDownload
//         );
//       else if (this.actionType === 'reject')
//         apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
//       else if (this.actionType === 'return')
//         apiCall = this.dataService.returnRFQForRevision(rfqId, this.userId, comments);
//       else if (this.actionType === 'hold')
//         apiCall = this.dataService.holdRFQ(rfqId, this.userId, comments);
//       else if (this.actionType === 'release')
//         apiCall = this.dataService.releaseHold(rfqId, this.userId, comments);

//     // ── PO actions ───────────────────────────────────────────────
//     } else if (this.modalMode === 'po') {
//       const poId = this.selectedItem.poId ?? this.selectedItem.purchaseOrderId ?? this.selectedItem.id;
//       if (!poId) {
//         this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
//         this.isSubmitting = false;
//         return;
//       }
//       if (this.actionType === 'approve')
//         apiCall = this.dataService.approvePO(poId, this.userId, comments);
//       else if (this.actionType === 'reject')
//         apiCall = this.dataService.rejectPO(poId, this.userId, comments);
//       else if (this.actionType === 'return')
//         apiCall = this.dataService.returnPOForRevision(poId, this.userId, comments);
//       else if (this.actionType === 'hold')
//         apiCall = this.dataService.holdPO(poId, this.userId, comments);
//       else if (this.actionType === 'release')
//         apiCall = this.dataService.releasePOHold(poId, this.userId, comments);

//     // ── Supplier actions ─────────────────────────────────────────
//     } else if (this.modalMode === 'supplier') {
//       const supplierId = this.selectedItem.supplierId;
//       if (!supplierId) {
//         this.messageService.showMessage('error', 'Error', 'Invalid Supplier ID');
//         this.isSubmitting = false;
//         return;
//       }
//       if (this.actionType === 'approve')
//         apiCall = this.dataService.approveSupplier(supplierId, this.userId, comments);
//       else if (this.actionType === 'reject')
//         apiCall = this.dataService.rejectSupplier(supplierId, this.userId, comments);
//       else if (this.actionType === 'hold')
//         apiCall = this.dataService.holdSupplier(supplierId, this.userId, comments);
//       else if (this.actionType === 'release')
//         apiCall = this.dataService.releaseSupplierHold(supplierId, this.userId, comments);
//     }

//     if (!apiCall) {
//       this.messageService.showMessage('error', 'Error', 'Invalid action');
//       this.isSubmitting = false;
//       return;
//     }

//     apiCall.subscribe({
//       next: () => {
//         const labels: any = {
//           approve: 'approved',
//           reject:  'permanently rejected',
//           return:  'returned for revision',
//           hold:    'put on HOLD',
//           release: 'released from HOLD'
//         };
//         const ref =
//           this.modalMode === 'rfq'      ? this.selectedItem.rfqNumber
//           : this.modalMode === 'po'     ? this.selectedItem.poNumber
//           : this.selectedItem.supplierCompanyName;

//         this.messageService.showMessage(
//           'success', 'Success',
//           `${ref} ${labels[this.actionType]} successfully`
//         );
//         this.closeApprovalModal();
//         this.loadAllData();
//       },
//       error: (error: any) => {
//         this.messageService.showMessage(
//           'error', 'Error',
//           error.error?.message || `Failed to ${this.actionType}`
//         );
//         this.isSubmitting = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // =========================================================================
//   //  NAVIGATION & TAB
//   // =========================================================================

//   viewRFQDetailsInNewTab(approval: any): void {
//     this.router.navigate([`/create-rfq/${approval.rfqId}/view`]);
//   }

//   viewPODetails(approval: any): void {
//     const poId = approval?.poId ?? approval?.purchaseOrderId ?? approval?.id;
//     if (!poId) {
//       this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
//       return;
//     }
//     this.router.navigate([`/po-details/${poId}`]);
//   }

//   switchTab(
//     tab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold' | 'supplier-pending' | 'supplier-hold'
//   ): void {
//     this.activeTab  = tab;
//     this.searchText = '';
//     this.applyDateAndSearchFilters();
//     this.cdr.markForCheck();
//   }

//   refresh(): void {
//     this.searchText = '';
//     this.loadAllData();
//   }

//   get totalBadgeCount(): number {
//     return (
//       this.filteredApprovals.length +
//       this.pendingPOCount +
//       this.pendingSupplierCount
//     );
//   }

//   // =========================================================================
//   //  MODAL DISPLAY HELPERS
//   // =========================================================================

//   getActionButtonText(): string {
//     const labels: any = {
//       approve: 'Approve',
//       reject:  'Reject Permanently',
//       return:  'Return for Revision',
//       hold:    'Put on HOLD',
//       release: 'Release HOLD'
//     };
//     return labels[this.actionType] || 'Submit';
//   }

//   getActionButtonClass(): string {
//     if (this.actionType === 'approve' || this.actionType === 'release') return 'btn-success';
//     if (this.actionType === 'reject') return 'btn-danger';
//     return 'btn-warning';
//   }

//   getModalTitle(): string {
//     const type =
//       this.modalMode === 'rfq'     ? 'RFQ'
//       : this.modalMode === 'po'   ? 'PO'
//       : 'Supplier';

//     if (this.actionType === 'approve') return `Approve ${type}`;
//     if (this.actionType === 'reject')  return `Reject ${type} (Permanent)`;
//     if (this.actionType === 'return')  return `Return ${type} for Revision`;
//     if (this.actionType === 'hold')    return `Put ${type} on HOLD`;
//     if (this.actionType === 'release') return `Release ${type} HOLD`;
//     return 'Action';
//   }

//   getModalHeaderClass(): string {
//     if (this.actionType === 'approve' || this.actionType === 'release') return 'bg-success';
//     if (this.actionType === 'reject') return 'bg-danger';
//     return 'bg-warning';
//   }

//   // =========================================================================
//   //  UTILITIES
//   // =========================================================================

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try {
//       return new Date(dateString).toLocaleString('en-GB');
//     } catch {
//       return 'Invalid Date';
//     }
//   }

//   formatCurrency(value: number, currencyCode?: string): string {
//     const code   = currencyCode || 'INR';
//     const symbol = this.getSymbolForCode(code);
//     if (!value) return `${symbol} 0.00`;
//     const formatted = Number(value).toLocaleString('en-IN', {
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2
//     });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
//   }

//   private getSymbolForCode(code: string): string {
//     const map: Record<string, string> = {
//       'INR': '₹', 'USD': '$',  'EUR': '€', 'GBP': '£',
//       'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
//       'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$'
//     };
//     return map[code] || code;
//   }

//   getStatusBadgeClass(status: string): string {
//     const m: any = {
//       'PENDING':               'bg-warning text-dark',
//       'APPROVED':              'bg-success',
//       'REJECTED':              'bg-danger',
//       'DRAFT':                 'bg-secondary',
//       'AWAITING_APPROVAL':     'bg-warning',
//       'RETURNED_FOR_REVISION': 'bg-warning text-dark',
//       'HOLD':                  'bg-warning text-dark',
//       'PENDING_APPROVAL':      'bg-warning text-dark'
//     };
//     return m[status] || 'bg-light text-dark';
//   }
// }


import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { RFQService } from '../models/rfq.service';
import { MessageService } from '../../../shared/service/message.service';
import { AuthService } from '../../../shared/service/AuthService';

interface FYOption {
  value: string;
  label: string;
  from: Date;
  to: Date;
}

@Component({
  selector: 'app-pending-approvals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pending-approvals.component.html',
  styleUrls: ['./pending-approvals.component.css']
})
export class PendingApprovalsComponent implements OnInit {

  // ==================== USER INFO ====================
  userId: number = 0;
  userName: string = '';
  userEmail: string = '';
  hierarchyLevelId: number | null = null;
  hierarchyLevelName: string = '';
  hierarchyLevelOrder: number | null = null;
  companyName: string = '';

  // ==================== RFQ RAW DATA ====================
  pendingApprovals: any[] = [];
  holdApprovals: any[] = [];

  // ==================== PO RAW DATA ====================
  pendingPOApprovals: any[] = [];
  holdPOApprovals: any[] = [];

  // ==================== SUPPLIER RAW DATA ====================
  pendingSupplierApprovals: any[] = [];
  holdSupplierApprovals: any[] = [];
  needMoreInfoSupplierApprovals: any[] = [];
  pendingSupplierCount: number = 0;
  holdSupplierCount: number = 0;
  needMoreInfoSupplierCount: number = 0;

  // ==================== DATE-FILTERED ====================
  dateFilteredPendingApprovals: any[] = [];
  dateFilteredHoldApprovals: any[] = [];
  dateFilteredPOApprovals: any[] = [];
  dateFilteredHoldPOApprovals: any[] = [];
  dateFilteredSupplierApprovals: any[] = [];
  dateFilteredHoldSupplierApprovals: any[] = [];
  dateFilteredNeedMoreInfoApprovals: any[] = [];

  // ==================== DISPLAYED ====================
  filteredApprovals: any[] = [];
  filteredHoldApprovals: any[] = [];
  filteredPOApprovals: any[] = [];
  filteredHoldPOApprovals: any[] = [];
  filteredSupplierApprovals: any[] = [];
  filteredHoldSupplierApprovals: any[] = [];
  filteredNeedMoreInfoApprovals: any[] = [];

  activeTab:
    | 'rfq-pending'
    | 'rfq-hold'
    | 'po-pending'
    | 'po-hold'
    | 'supplier-pending'
    | 'supplier-hold'
    | 'supplier-info' = 'rfq-pending';

  isLoading = false;
  searchText = '';

  // ==================== APPROVAL MODAL STATE ====================
  showApprovalModal = false;
  selectedItem: any = null;
  modalMode: 'rfq' | 'po' | 'supplier' = 'rfq';
  actionType: 'approve' | 'reject' | 'return' | 'hold' | 'release' | 'need_more_info' = 'approve';
  actionComments = '';
  isSubmitting = false;

  isLastApprover: boolean = false;
  requiresDates: boolean = false;
  minDueDate: string = '';
  minDeliveryDate: string = '';
  rfqDueDate: string = '';
  rfqDeliveryDate: string = '';
  allowSupplierDownload: boolean = false;

  // ==================== SUPPLIER DETAIL MODAL STATE ====================
  showSupplierDetailModal = false;
  selectedSupplierDetail: any = null;
  selectedSupplierApprovalItem: any = null;
  isLoadingSupplierDetail = false;
  expandedLocations: { [key: number]: boolean } = {};
  expandedDepts: { [key: string]: boolean } = {};

  // ==================== DATE FILTER STATE ====================
  financialYearOptions: FYOption[] = [];
  selectedFYOption: string = '';
  customFromDate: string = '';
  customToDate: string = '';
  activeDateRangeLabel: string = '';

  holdApprovalsCount: number = 0;
  pendingPOCount: number = 0;
  holdPOCount: number = 0;

  constructor(
    private dataService: DataService,
    private rfqService: RFQService,
    private messageService: MessageService,
    private authService: AuthService,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeUser();
    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();
    this.loadAllData();
    this.setMinDates();
  }

  // =========================================================================
  //  INITIALIZE USER
  // =========================================================================

  private initializeUser(): void {
    this.userId             = Number(localStorage.getItem('userId')) || 0;
    this.userName           = localStorage.getItem('fullName') || 'User';
    this.userEmail          = localStorage.getItem('email') || localStorage.getItem('username') || '';
    this.companyName        = localStorage.getItem('companyName') || 'NA';
    this.hierarchyLevelId   = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
    if (!this.userId) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
    }
  }

  private setMinDates(): void {
    const now = new Date();
    this.minDueDate = this.formatDateForInput(now);
    this.minDeliveryDate = this.formatDateForInput(now);
  }

  private formatDateForInput(date: Date): string {
    const y   = date.getFullYear();
    const m   = String(date.getMonth() + 1).padStart(2, '0');
    const d   = String(date.getDate()).padStart(2, '0');
    const h   = String(date.getHours()).padStart(2, '0');
    const min = String(date.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${d}T${h}:${min}`;
  }

  // =========================================================================
  //  FINANCIAL YEAR HELPERS
  // =========================================================================

  private buildFinancialYearOptions(): void {
    const today = new Date();
    let currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;
    this.financialYearOptions = [];
    for (let i = 0; i < 4; i++) {
      const startYear = currentFYStartYear - i;
      const endYear   = startYear + 1;
      const from = new Date(startYear, 3, 1, 0, 0, 0, 0);
      const to   = new Date(endYear,   2, 31, 23, 59, 59, 999);
      this.financialYearOptions.push({
        value: `FY${startYear}-${String(endYear).slice(-2)}`,
        label: `FY ${startYear}-${String(endYear).slice(-2)}  (Apr ${startYear} – Mar ${endYear})`,
        from,
        to
      });
    }
  }

  getCurrentFYValue(): string {
    return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL';
  }

  onFYOptionChange(): void {
    if (this.selectedFYOption !== 'CUSTOM') {
      this.customFromDate = '';
      this.customToDate   = '';
    }
    this.updateActiveDateRangeLabel();
    this.applyDateAndSearchFilters();
  }

  resetDateFilter(): void {
    this.selectedFYOption = this.getCurrentFYValue();
    this.customFromDate   = '';
    this.customToDate     = '';
    this.updateActiveDateRangeLabel();
    this.applyDateAndSearchFilters();
  }

  private updateActiveDateRangeLabel(): void {
    if (this.selectedFYOption === 'ALL') {
      this.activeDateRangeLabel = 'All Time';
      return;
    }
    if (this.selectedFYOption === 'CUSTOM') {
      if (this.customFromDate && this.customToDate)
        this.activeDateRangeLabel = `${this.formatDisplayDate(this.customFromDate)} – ${this.formatDisplayDate(this.customToDate)}`;
      else if (this.customFromDate)
        this.activeDateRangeLabel = `From ${this.formatDisplayDate(this.customFromDate)}`;
      else if (this.customToDate)
        this.activeDateRangeLabel = `Up to ${this.formatDisplayDate(this.customToDate)}`;
      else
        this.activeDateRangeLabel = 'Custom Range';
      return;
    }
    const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
    this.activeDateRangeLabel = fy ? fy.label : '';
  }

  private formatDisplayDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr + 'T00:00:00').toLocaleDateString('en-GB', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }

  private getActiveDateRange(): { from: Date; to: Date } | null {
    if (this.selectedFYOption === 'ALL') return null;
    if (this.selectedFYOption === 'CUSTOM') {
      const from = this.customFromDate ? new Date(this.customFromDate + 'T00:00:00') : null;
      const to   = this.customToDate   ? new Date(this.customToDate   + 'T23:59:59') : null;
      if (!from && !to) return null;
      return { from: from ?? new Date(0), to: to ?? new Date(8640000000000000) };
    }
    const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
    return fy ? { from: fy.from, to: fy.to } : null;
  }

  private filterByDate(items: any[], dateField: string = 'createdAt'): any[] {
    const range = this.getActiveDateRange();
    if (!range) return items;
    return items.filter((item: any) => {
      const dateStr = item[dateField] || item.createdAt || item.actionDate;
      if (!dateStr) return false;
      const d = new Date(dateStr);
      return d >= range.from && d <= range.to;
    });
  }

  // =========================================================================
  //  COMBINED DATE + SEARCH FILTER
  // =========================================================================

  applyDateAndSearchFilters(): void {
    this.updateActiveDateRangeLabel();
    const term = this.searchText.toLowerCase().trim();

    const filterArr = (arr: any[], fields: string[]) =>
      term
        ? arr.filter(item => fields.some(f => (item[f] || '').toLowerCase().includes(term)))
        : [...arr];

    this.dateFilteredPendingApprovals       = this.filterByDate(this.pendingApprovals, 'createdAt');
    this.dateFilteredHoldApprovals          = this.filterByDate(this.holdApprovals, 'holdDate');
    this.dateFilteredPOApprovals            = this.filterByDate(this.pendingPOApprovals, 'createdAt');
    this.dateFilteredHoldPOApprovals        = this.filterByDate(this.holdPOApprovals, 'holdDate');
    this.dateFilteredSupplierApprovals      = this.filterByDate(this.pendingSupplierApprovals, 'createdAt');
    this.dateFilteredHoldSupplierApprovals  = this.filterByDate(this.holdSupplierApprovals, 'holdDate');
    this.dateFilteredNeedMoreInfoApprovals  = this.filterByDate(this.needMoreInfoSupplierApprovals, 'infoRequestDate');

    this.filteredApprovals             = filterArr(this.dateFilteredPendingApprovals,      ['rfqNumber', 'rfqTitle', 'buyerName']);
    this.filteredHoldApprovals         = filterArr(this.dateFilteredHoldApprovals,         ['rfqNumber', 'rfqTitle', 'buyerName']);
    this.filteredPOApprovals           = filterArr(this.dateFilteredPOApprovals,           ['poNumber', 'supplierName', 'buyerName']);
    this.filteredHoldPOApprovals       = filterArr(this.dateFilteredHoldPOApprovals,       ['poNumber', 'supplierName', 'buyerName']);
    this.filteredSupplierApprovals     = filterArr(this.dateFilteredSupplierApprovals,     ['supplierCompanyName', 'hierarchyLevelName']);
    this.filteredHoldSupplierApprovals = filterArr(this.dateFilteredHoldSupplierApprovals, ['supplierCompanyName', 'hierarchyLevelName']);
    this.filteredNeedMoreInfoApprovals = filterArr(this.dateFilteredNeedMoreInfoApprovals,  ['supplierCompanyName', 'infoRequest']);

    this.cdr.markForCheck();
  }

  onSearch(): void {
    this.applyDateAndSearchFilters();
  }

  // =========================================================================
  //  DATA LOADING
  // =========================================================================

  private loadAllData(): void {
    this.loadPendingApprovals();
    this.loadHoldApprovals();
    this.loadPendingPOApprovals();
    this.loadHoldPOApprovals();
    this.loadPendingSupplierApprovals();
    this.loadHoldSupplierApprovals();
  }

  loadPendingApprovals(): void {
    if (!this.userId) return;
    this.isLoading = true;
    this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.pendingApprovals = data;
        this.isLoading = false;
        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => {
        this.pendingApprovals = [];
        this.isLoading = false;
        this.applyDateAndSearchFilters();
      }
    });
  }

  loadHoldApprovals(): void {
    if (!this.userId) return;
    this.dataService.getHoldApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.holdApprovals      = data;
        this.holdApprovalsCount = data.length;
        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => { this.holdApprovals = []; this.applyDateAndSearchFilters(); }
    });
  }

  loadPendingPOApprovals(): void {
    if (!this.userId) return;
    this.dataService.getPendingPOApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.pendingPOApprovals = data.map((item: any) => ({
          ...item,
          poId:           item.purchaseOrderId ?? item.poId ?? item.id,
          currencyCode:   item.currencyCode   || 'INR',
          currencySymbol: item.currencySymbol || '₹'
        }));
        this.pendingPOCount = this.pendingPOApprovals.length;
        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => { this.pendingPOApprovals = []; this.applyDateAndSearchFilters(); }
    });
  }

  loadHoldPOApprovals(): void {
    if (!this.userId) return;
    this.dataService.getHoldPOApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.holdPOApprovals = data.map((item: any) => ({
          ...item,
          poId:           item.purchaseOrderId ?? item.poId ?? item.id,
          currencyCode:   item.currencyCode   || 'INR',
          currencySymbol: item.currencySymbol || '₹'
        }));
        this.holdPOCount = this.holdPOApprovals.length;
        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => { this.holdPOApprovals = []; this.applyDateAndSearchFilters(); }
    });
  }

  loadPendingSupplierApprovals(): void {
    if (!this.userId) return;
    this.dataService.getPendingSupplierApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;

        // Separate NEED_MORE_INFO from regular PENDING
        this.needMoreInfoSupplierApprovals = data.filter((d: any) => d.status === 'NEED_MORE_INFO');
        this.pendingSupplierApprovals      = data.filter((d: any) => d.status !== 'NEED_MORE_INFO');
        this.pendingSupplierCount          = this.pendingSupplierApprovals.length;
        this.needMoreInfoSupplierCount     = this.needMoreInfoSupplierApprovals.length;

        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => {
        this.pendingSupplierApprovals  = [];
        this.needMoreInfoSupplierApprovals = [];
        this.applyDateAndSearchFilters();
      }
    });
  }

  loadHoldSupplierApprovals(): void {
    if (!this.userId) return;
    this.dataService.getHoldSupplierApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.holdSupplierApprovals = data;
        this.holdSupplierCount     = data.length;
        this.applyDateAndSearchFilters();
        this.cdr.markForCheck();
      },
      error: () => { this.holdSupplierApprovals = []; this.applyDateAndSearchFilters(); }
    });
  }

  // =========================================================================
  //  SUPPLIER DETAIL MODAL
  // =========================================================================

  viewSupplierDetails(approval: any): void {
    this.selectedSupplierApprovalItem = approval;
    this.showSupplierDetailModal      = true;
    this.selectedSupplierDetail       = null;
    this.expandedLocations            = {};
    this.expandedDepts                = {};
    this.isLoadingSupplierDetail      = true;
    this.cdr.markForCheck();

    const supplierId = approval.supplierId;

    if (!supplierId) {
      this.selectedSupplierDetail = {
        id: approval.supplierId,
        companyName: approval.supplierCompanyName,
        approvalStatus: 'PENDING',
        locations: []
      };
      this.isLoadingSupplierDetail = false;
      this.cdr.markForCheck();
      return;
    }

    this.dataService.getSupplierById(supplierId).subscribe({
      next: (response: any) => {
        if (response && response.data)          this.selectedSupplierDetail = response.data;
        else if (response && response.companyName) this.selectedSupplierDetail = response;
        else
          this.selectedSupplierDetail = {
            id: supplierId, companyName: approval.supplierCompanyName,
            approvalStatus: approval.status || 'PENDING', locations: []
          };

        if (this.selectedSupplierDetail?.locations?.length > 0) {
          this.expandedLocations[0] = true;
          if (this.selectedSupplierDetail.locations[0]?.departments?.length > 0)
            this.expandedDepts['0-0'] = true;
        }

        this.isLoadingSupplierDetail = false;
        this.cdr.markForCheck();
        this.loadSupplierLogo(supplierId);
      },
      error: () => {
        this.selectedSupplierDetail = {
          id: supplierId, companyName: approval.supplierCompanyName,
          approvalStatus: approval.status || 'PENDING',
          industrySector: approval.industrySector || '', locations: []
        };
        this.isLoadingSupplierDetail = false;
        this.cdr.markForCheck();
      }
    });
  }

  private loadSupplierLogo(supplierId: number): void {
    this.dataService.getSupplierLogoBase64(supplierId).subscribe({
      next: (logoData: any) => {
        if (logoData && this.selectedSupplierDetail) {
          this.selectedSupplierDetail = { ...this.selectedSupplierDetail, logoBase64: logoData };
          this.cdr.markForCheck();
        }
      },
      error: () => {}
    });
  }

  closeSupplierDetailModal(): void {
    this.showSupplierDetailModal      = false;
    this.selectedSupplierDetail       = null;
    this.selectedSupplierApprovalItem = null;
    this.expandedLocations            = {};
    this.expandedDepts                = {};
    this.cdr.markForCheck();
  }

  toggleLocationExpand(index: number): void {
    this.expandedLocations[index] = !this.expandedLocations[index];
    this.cdr.markForCheck();
  }

  toggleDeptExpand(locIdx: number, deptIdx: number): void {
    const key = `${locIdx}-${deptIdx}`;
    this.expandedDepts[key] = !this.expandedDepts[key];
    this.cdr.markForCheck();
  }

  // =========================================================================
  //  SUPPLIER DETAIL — DISPLAY HELPERS
  // =========================================================================

  getInitials(name: string | undefined | null): string {
    if (!name || !name.trim()) return '??';
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  getTotalDepartments(supplier: any): number {
    if (!supplier?.locations) return 0;
    return supplier.locations.reduce(
      (sum: number, loc: any) => sum + (loc.departments?.length || 0), 0);
  }

  getTotalUsers(supplier: any): number {
    if (!supplier?.locations) return 0;
    return supplier.locations.reduce(
      (sum: number, loc: any) =>
        sum + (loc.departments || []).reduce(
          (s: number, dept: any) => s + (dept.users?.length || 0), 0), 0);
  }

  getUserCountForLocation(loc: any): number {
    if (!loc?.departments) return 0;
    return loc.departments.reduce(
      (sum: number, dept: any) => sum + (dept.users?.length || 0), 0);
  }

  // =========================================================================
  //  APPROVAL MODAL
  // =========================================================================

  openApprovalModal(
    item: any,
    action: 'approve' | 'reject' | 'return' | 'hold' | 'release' | 'need_more_info',
    mode: 'rfq' | 'po' | 'supplier'
  ): void {
    this.selectedItem      = item;
    this.actionType        = action;
    this.modalMode         = mode;
    this.actionComments    = '';
    this.rfqDueDate        = '';
    this.rfqDeliveryDate   = '';
    this.isLastApprover    = false;
    this.requiresDates     = false;
    this.allowSupplierDownload =
      item.allowSupplierDownload !== undefined ? item.allowSupplierDownload : true;

    if (mode === 'rfq' && action === 'approve') {
      this.checkIfLastApprover(item.rfqId);
    }

    this.showApprovalModal = true;
    this.cdr.markForCheck();
  }

  private checkIfLastApprover(rfqId: number): void {
    this.dataService.isLastApprover(rfqId, this.userId).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.isLastApprover = response.isLastApprover || false;
          this.requiresDates  = response.requiresDates  || false;
          this.cdr.markForCheck();
        }
      },
      error: () => { this.isLastApprover = false; }
    });
  }

  onDueDateChange(): void {
    if (this.rfqDueDate) {
      this.minDeliveryDate = this.rfqDueDate;
      if (this.rfqDeliveryDate && this.rfqDeliveryDate < this.rfqDueDate)
        this.rfqDeliveryDate = '';
      this.cdr.markForCheck();
    }
  }

  closeApprovalModal(): void {
    this.showApprovalModal = false;
    this.selectedItem      = null;
    this.actionComments    = '';
    this.isSubmitting      = false;
    this.cdr.markForCheck();
  }

  isCommentsRequired(): boolean {
    return ['reject', 'return', 'hold', 'need_more_info'].includes(this.actionType);
  }

  submitApprovalAction(): void {
    if (!this.selectedItem || !this.userId) {
      this.messageService.showMessage('error', 'Error', 'Invalid approval data');
      return;
    }

    if (this.isCommentsRequired() && !this.actionComments.trim()) {
      const msgs: any = {
        reject:         'Please provide rejection reason',
        return:         'Please provide revision comments',
        hold:           'Please provide hold remarks',
        need_more_info: 'Please describe what information you need'
      };
      this.messageService.showMessage('warning', 'Warning', msgs[this.actionType]);
      return;
    }

    if (this.modalMode === 'rfq' && this.actionType === 'approve' && this.isLastApprover) {
      if (!this.rfqDueDate || !this.rfqDeliveryDate) {
        this.messageService.showMessage('error', 'Validation Error',
          'As final approver, you must set both RFQ dates');
        return;
      }
    }

    this.isSubmitting = true;
    const comments    = this.actionComments.trim() || 'No comments provided';
    let apiCall: any;

    // ── RFQ ─────────────────────────────────────────────────────
    if (this.modalMode === 'rfq') {
      const rfqId = this.selectedItem.rfqId;
      if (!rfqId) {
        this.messageService.showMessage('error', 'Error', 'Invalid RFQ ID');
        this.isSubmitting = false; return;
      }
      if (this.actionType === 'approve')
        apiCall = this.dataService.approveRFQWithDates(rfqId, this.userId, comments,
          this.rfqDueDate || undefined, this.rfqDeliveryDate || undefined, this.allowSupplierDownload);
      else if (this.actionType === 'reject')
        apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
      else if (this.actionType === 'return')
        apiCall = this.dataService.returnRFQForRevision(rfqId, this.userId, comments);
      else if (this.actionType === 'hold')
        apiCall = this.dataService.holdRFQ(rfqId, this.userId, comments);
      else if (this.actionType === 'release')
        apiCall = this.dataService.releaseHold(rfqId, this.userId, comments);

    // ── PO ──────────────────────────────────────────────────────
    } else if (this.modalMode === 'po') {
      const poId = this.selectedItem.poId ?? this.selectedItem.purchaseOrderId ?? this.selectedItem.id;
      if (!poId) {
        this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
        this.isSubmitting = false; return;
      }
      if (this.actionType === 'approve')
        apiCall = this.dataService.approvePO(poId, this.userId, comments);
      else if (this.actionType === 'reject')
        apiCall = this.dataService.rejectPO(poId, this.userId, comments);
      else if (this.actionType === 'return')
        apiCall = this.dataService.returnPOForRevision(poId, this.userId, comments);
      else if (this.actionType === 'hold')
        apiCall = this.dataService.holdPO(poId, this.userId, comments);
      else if (this.actionType === 'release')
        apiCall = this.dataService.releasePOHold(poId, this.userId, comments);

    // ── SUPPLIER ─────────────────────────────────────────────────
    } else if (this.modalMode === 'supplier') {
      const supplierId = this.selectedItem.supplierId;
      if (!supplierId) {
        this.messageService.showMessage('error', 'Error', 'Invalid Supplier ID');
        this.isSubmitting = false; return;
      }
      if (this.actionType === 'approve')
        apiCall = this.dataService.approveSupplier(supplierId, this.userId, comments);
      else if (this.actionType === 'reject')
        apiCall = this.dataService.rejectSupplier(supplierId, this.userId, comments);
      else if (this.actionType === 'hold')
        apiCall = this.dataService.holdSupplier(supplierId, this.userId, comments);
      else if (this.actionType === 'release')
        apiCall = this.dataService.releaseSupplierHold(supplierId, this.userId, comments);
      else if (this.actionType === 'need_more_info')
        apiCall = this.dataService.requestMoreInfoFromSupplier(supplierId, this.userId, comments);
    }

    if (!apiCall) {
      this.messageService.showMessage('error', 'Error', 'Invalid action');
      this.isSubmitting = false;
      return;
    }

    apiCall.subscribe({
      next: () => {
        const labels: any = {
          approve:        'approved',
          reject:         'permanently rejected',
          return:         'returned for revision',
          hold:           'put on HOLD',
          release:        'released from HOLD',
          need_more_info: '— information request sent to supplier'
        };
        const ref =
          this.modalMode === 'rfq'     ? this.selectedItem.rfqNumber
          : this.modalMode === 'po'   ? this.selectedItem.poNumber
          : this.selectedItem.supplierCompanyName;

        this.messageService.showMessage(
          'success', 'Success', `${ref} ${labels[this.actionType]} successfully`);
        this.closeApprovalModal();
        this.loadAllData();
      },
      error: (error: any) => {
        this.messageService.showMessage(
          'error', 'Error', error.error?.message || `Failed to ${this.actionType}`);
        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }

  // =========================================================================
  //  NAVIGATION & TAB
  // =========================================================================

  viewRFQDetailsInNewTab(approval: any): void {
    this.router.navigate([`/create-rfq/${approval.rfqId}/view`]);
  }

  viewPODetails(approval: any): void {
    const poId = approval?.poId ?? approval?.purchaseOrderId ?? approval?.id;
    if (!poId) {
      this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
      return;
    }
    this.router.navigate([`/po-details/${poId}`]);
  }

  switchTab(
    tab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold'
       | 'supplier-pending' | 'supplier-hold' | 'supplier-info'
  ): void {
    this.activeTab  = tab;
    this.searchText = '';
    this.applyDateAndSearchFilters();
    this.cdr.markForCheck();
  }

  refresh(): void {
    this.searchText = '';
    this.loadAllData();
  }

  get totalBadgeCount(): number {
    return this.filteredApprovals.length + this.pendingPOCount + this.pendingSupplierCount;
  }

  // =========================================================================
  //  MODAL DISPLAY HELPERS
  // =========================================================================

  getActionButtonText(): string {
    const labels: any = {
      approve:        'Approve',
      reject:         'Reject Permanently',
      return:         'Return for Revision',
      hold:           'Put on HOLD',
      release:        'Release HOLD',
      need_more_info: 'Send Info Request'
    };
    return labels[this.actionType] || 'Submit';
  }

  getActionButtonClass(): string {
    if (this.actionType === 'approve' || this.actionType === 'release') return 'btn-success';
    if (this.actionType === 'reject')         return 'btn-danger';
    if (this.actionType === 'need_more_info') return 'btn-info text-white';
    return 'btn-warning';
  }

  getModalTitle(): string {
    const type =
      this.modalMode === 'rfq'    ? 'RFQ'
      : this.modalMode === 'po'  ? 'PO'
      : 'Supplier';
    if (this.actionType === 'approve')        return `Approve ${type}`;
    if (this.actionType === 'reject')         return `Reject ${type} (Permanent)`;
    if (this.actionType === 'return')         return `Return ${type} for Revision`;
    if (this.actionType === 'hold')           return `Put ${type} on HOLD`;
    if (this.actionType === 'release')        return `Release ${type} HOLD`;
    if (this.actionType === 'need_more_info') return `Request More Info from Supplier`;
    return 'Action';
  }

  getModalHeaderClass(): string {
    if (this.actionType === 'approve' || this.actionType === 'release') return 'bg-success';
    if (this.actionType === 'reject')         return 'bg-danger';
    if (this.actionType === 'need_more_info') return 'bg-info';
    return 'bg-warning';
  }

  getModalIcon(): string {
    const icons: any = {
      approve:        'bi-check-circle',
      reject:         'bi-x-circle',
      return:         'bi-arrow-return-left',
      hold:           'bi-pause-circle',
      release:        'bi-play-circle',
      need_more_info: 'bi-question-circle'
    };
    return icons[this.actionType] || 'bi-check';
  }

  getCommentsPlaceholder(): string {
    const map: any = {
      approve:        'Optional: Add approval comments...',
      reject:         'Required: Reason for permanent rejection...',
      return:         'Required: What needs to be revised...',
      hold:           'Required: Why are you putting this on HOLD?',
      release:        'Optional: Comments about releasing the hold...',
      need_more_info: 'Required: Describe exactly what information you need from the supplier...'
    };
    return map[this.actionType] || 'Enter comments...';
  }

  getCommentsLabel(): string {
    const map: any = {
      hold:           'Hold Remarks',
      release:        'Release Comments (Optional)',
      need_more_info: 'Information Required from Supplier',
      reject:         'Rejection Reason',
      return:         'Revision Comments'
    };
    return map[this.actionType] || 'Comments';
  }

  // =========================================================================
  //  UTILITIES
  // =========================================================================

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try { return new Date(dateString).toLocaleString('en-GB'); }
    catch { return 'Invalid Date'; }
  }

  formatCurrency(value: number, currencyCode?: string): string {
    const code   = currencyCode || 'INR';
    const symbol = this.getSymbolForCode(code);
    if (!value) return `${symbol} 0.00`;
    const formatted = Number(value).toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
  }

  private getSymbolForCode(code: string): string {
    const map: Record<string, string> = {
      'INR': '₹', 'USD': '$',  'EUR': '€',  'GBP': '£',
      'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
      'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$'
    };
    return map[code] || code;
  }

  getStatusBadgeClass(status: string): string {
    const m: any = {
      'PENDING':               'bg-warning text-dark',
      'APPROVED':              'bg-success',
      'REJECTED':              'bg-danger',
      'DRAFT':                 'bg-secondary',
      'AWAITING_APPROVAL':     'bg-warning',
      'RETURNED_FOR_REVISION': 'bg-warning text-dark',
      'HOLD':                  'bg-warning text-dark',
      'PENDING_APPROVAL':      'bg-warning text-dark',
      'NEED_MORE_INFO':        'bg-info text-white'
    };
    return m[status] || 'bg-light text-dark';
  }

  // Expose activeTab-aware flag for the supplier detail footer buttons
  isSupplierPendingTab(): boolean  { return this.activeTab === 'supplier-pending'; }
  isSupplierHoldTab(): boolean     { return this.activeTab === 'supplier-hold'; }
  isSupplierInfoTab(): boolean     { return this.activeTab === 'supplier-info'; }
}