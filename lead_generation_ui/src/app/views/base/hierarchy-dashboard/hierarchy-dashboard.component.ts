
// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from '../models/rfq.service';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';
// import { Pipe, PipeTransform } from '@angular/core';

// @Pipe({ name: 'filter', standalone: true })
// export class FilterPipe implements PipeTransform {
//   transform(items: any[], field: string, value: any): any[] {
//     if (!items || !field) return items;
//     return items.filter(item => item[field] === value);
//   }
// }

// @Component({
//   selector: 'app-hierarchy-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule, FilterPipe],
//   templateUrl: './hierarchy-dashboard.component.html',
//   styleUrls: ['./hierarchy-dashboard.component.css']
// })
// export class HierarchyDashboardComponent implements OnInit {

//   // ==================== USER INFO ====================
//   userId: number = 0;
//   userName: string = '';
//   userEmail: string = '';
//   hierarchyLevelId: number | null = null;
//   hierarchyLevelName: string = '';
//   hierarchyLevelOrder: number | null = null;
//   companyName: string = '';

//   // ==================== TAB ====================
//   activeTab: 'rfq' | 'po' = 'rfq';

//   // ==================== RFQ DATA ====================
//   allRFQs: any[] = [];
//   filteredRFQs: any[] = [];
//   pagedRFQs: any[] = [];

//   // ==================== PO DATA ====================
//   allPOs: any[] = [];
//   filteredPOs: any[] = [];
//   pagedPOs: any[] = [];

//   // ==================== PAGINATION & SEARCH ====================
//   searchText = '';
//   currentPage = 1;
//   pageSize = 10;
//   poSearchText = '';
//   poCurrentPage = 1;
//   poPageSize = 10;

//   // ==================== FILTERS ====================
//   statusFilter = '';
//   approvalStatusFilter = '';
//   poStatusFilter = '';

//   // ==================== UI STATE ====================
//   isLoading = false;
//   isPOLoading = false;

//   // ==================== COUNTS ====================
//   pendingApprovalsCount = 0;
//   holdApprovalsCount: number = 0;

//   // ✅ PO approval counts — fetched from API (user-specific, not company-wide)
//   // These reflect only POs assigned to THIS user at THEIR hierarchy level
//   pendingPOCount: number = 0;
//   holdPOCount: number = 0;

//   // Total POs approved — derived from loaded data (company-wide is fine for this)
//   get approvedPOCount(): number {
//     return this.allPOs.filter(po =>
//       (po.approvalStatus || po.status) === 'APPROVED'
//     ).length;
//   }

//   get totalBadgeCount(): number {
//     return this.pendingApprovalsCount + this.holdApprovalsCount + this.pendingPOCount + this.holdPOCount;
//   }

//   // ==================== STATISTICS ====================
//   statistics = {
//     totalRFQs: 0, pendingApprovals: 0, approved: 0,
//     rejected: 0, published: 0, closed: 0, draft: 0, awaitingApproval: 0
//   };

//   // ==================== DROPDOWNS ====================
//   statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED', 'HOLD'];
//   approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
//   poStatuses = ['DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'HOLD', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'COMPLETED', 'CANCELLED'];

//   constructor(
//     private rfqService: RFQService,
//     private dataService: DataService,
//     private messageService: MessageService,
//     private authService: AuthService,
//     public router: Router,
//     private cdr: ChangeDetectorRef
//   ) {}

//   ngOnInit(): void {
//     this.initializeUser();
//     this.loadData();
//   }

//   // ==================== INITIALIZE USER ====================

//   private initializeUser(): void {
//     this.userId = Number(localStorage.getItem('userId')) || 0;
//     this.userName = localStorage.getItem('fullName') || 'User';
//     this.userEmail = localStorage.getItem('email') || localStorage.getItem('username') || '';
//     this.companyName = localStorage.getItem('companyName') || 'NA';
//     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || 'Hierarchy User';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

//     console.log('%c[HIERARCHY DASHBOARD INIT]', 'color: #00aa00; font-weight: bold;', {
//       userId: this.userId, userName: this.userName, userEmail: this.userEmail,
//       companyName: this.companyName, levelName: this.hierarchyLevelName, levelOrder: this.hierarchyLevelOrder
//     });

//     if (!this.userId) {
//       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//       this.router.navigate(['/login']);
//     }
//   }

//   // ==================== LOAD DATA ====================

//   private loadData(): void {
//     this.isLoading = true;
//     console.log('%c[LOADING DASHBOARD DATA]', 'color: #0066cc; font-weight: bold;');

//     this.dataService.getRFQDashboardList(this.userId, {}).subscribe({
//       next: (response: any) => {
//         console.log('%c[✅ RFQs LOADED]', 'color: #00aa00;', response);
//         let rawRFQs = response.data || [];

//         this.allRFQs = rawRFQs.map((rfq: any) => ({
//           ...rfq,
//           suppliersCount: rfq.suppliersCount || 0,
//           itemsCount: rfq.itemsCount || 0,
//           buyerName: rfq.buyerName || 'N/A'
//         }));

//         if (this.companyName && this.companyName !== 'Unknown Company' && this.companyName !== 'NA') {
//           const beforeCount = this.allRFQs.length;
//           this.allRFQs = this.allRFQs.filter((rfq: any) =>
//             rfq.buyerName === this.companyName ||
//             rfq.buyer?.organizationCompanyName === this.companyName ||
//             rfq.buyer?.companyName === this.companyName
//           );
//           console.log(`%c[FILTERED BY COMPANY]`, 'color: #9c27b0;', `${beforeCount} → ${this.allRFQs.length} RFQs`);
//         }

//         this.calculateStatistics();
//         this.applyRFQFilters();
//         this.loadPendingApprovalCount();
//         this.loadHoldApprovalCount();
//         // ✅ Load PO approval counts for THIS user (not company-wide)
//         this.loadPendingPOApprovalCount();
//         this.loadHoldPOApprovalCount();
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: (error: any) => {
//         console.error('%c[ERROR LOADING RFQs]', 'color: #cc0000;', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       }
//     });

//     this.loadPOs();
//   }

//   // ==================== LOAD POs ====================

//   private loadPOs(): void {
//     if (!this.userId) return;
//     this.isPOLoading = true;

//     console.log("%c[LOADING POs]", "color: #e65100; font-weight: bold;", this.companyName);

//     this.dataService.getPurchaseOrdersByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         console.log('%c[✅ POs LOADED RAW]', 'color: #00aa00;', response);

//         // Handle different response shapes
//         let rawPOs: any[] = [];
//         if (Array.isArray(response)) {
//           rawPOs = response;
//         } else if (response && Array.isArray(response.data)) {
//           rawPOs = response.data;
//         } else if (response && Array.isArray(response.content)) {
//           rawPOs = response.content;
//         } else {
//           rawPOs = [];
//         }

//         this.allPOs = rawPOs.map((po: any) => ({
//           ...po,
//           supplierName: po.supplierName || po.supplier?.companyName || po.supplier?.name || 'N/A',
//           rfqNumber: po.rfqNumber || po.rfq?.rfqNumber || 'N/A',
//           grandTotal: po.grandTotal || po.totalAmount || 0,
//           itemsCount: po.itemsCount || po.items?.length || 0,
//           poNumber: po.poNumber || po.id || 'N/A',
//           createdDate: po.createdAt || po.createdDate || po.poDate || null,
//           approvalStatus: po.approvalStatus || po.status || 'DRAFT'
//         }));

//         console.log('%c[✅ POs PROCESSED]', 'color: #00aa00;', `${this.allPOs.length} POs`, {
//           pending: this.pendingPOCount,
//           hold: this.holdPOCount,
//           approved: this.approvedPOCount
//         });

//         this.applyPOFilters();
//         this.isPOLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: (error: any) => {
//         console.error('%c[ERROR LOADING POs]', 'color: #cc0000;', error);
//         this.allPOs = [];
//         this.filteredPOs = [];
//         this.pagedPOs = [];
//         this.isPOLoading = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== RFQ APPROVAL COUNTS ====================

//   private loadPendingApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         console.log('%c[PENDING RFQ COUNT]', 'color: #0066cc;', response);
//         if (response.success && typeof response.pendingCount === 'number') {
//           this.pendingApprovalsCount = response.pendingCount;
//         } else if (typeof response.pendingCount === 'number') {
//           this.pendingApprovalsCount = response.pendingCount;
//         } else if (typeof response.count === 'number') {
//           this.pendingApprovalsCount = response.count;
//         } else if (typeof response.data === 'number') {
//           this.pendingApprovalsCount = response.data;
//         } else {
//           this.pendingApprovalsCount = 0;
//         }
//         this.statistics.pendingApprovals = this.pendingApprovalsCount;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.pendingApprovalsCount = 0; this.cdr.markForCheck(); }
//     });
//   }

//   private loadHoldApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         console.log('%c[HOLD RFQ COUNT]', 'color: #ff9800;', response);
//         if (response.success && typeof response.holdCount === 'number') {
//           this.holdApprovalsCount = response.holdCount;
//         } else if (typeof response.holdCount === 'number') {
//           this.holdApprovalsCount = response.holdCount;
//         } else if (typeof response.count === 'number') {
//           this.holdApprovalsCount = response.count;
//         } else if (typeof response.data === 'number') {
//           this.holdApprovalsCount = response.data;
//         } else {
//           this.holdApprovalsCount = 0;
//         }
//         this.cdr.markForCheck();
//       },
//       error: () => { this.holdApprovalsCount = 0; this.cdr.markForCheck(); }
//     });
//   }

//   // ✅ PO pending count — calls /api/po-approval/pending/user/{userId}/count
//   // Returns only POs where THIS user is the current assigned approver
//   private loadPendingPOApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingPOApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         console.log('%c[PENDING PO COUNT - USER SPECIFIC]', 'color: #e53935;', response);
//         if (typeof response.pendingCount === 'number') {
//           this.pendingPOCount = response.pendingCount;
//         } else if (typeof response.data === 'number') {
//           this.pendingPOCount = response.data;
//         } else if (typeof response.count === 'number') {
//           this.pendingPOCount = response.count;
//         } else {
//           this.pendingPOCount = 0;
//         }
//         console.log('%c[✅ PENDING PO COUNT]', 'color: #00aa00;', this.pendingPOCount);
//         this.cdr.markForCheck();
//       },
//       error: () => { this.pendingPOCount = 0; this.cdr.markForCheck(); }
//     });
//   }

//   // ✅ PO hold count — calls /api/po-approval/hold/user/{userId}/count
//   // Returns only POs on hold visible to THIS user at their hierarchy level
//   private loadHoldPOApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldPOApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         console.log('%c[HOLD PO COUNT - USER SPECIFIC]', 'color: #ff6f00;', response);
//         if (typeof response.holdCount === 'number') {
//           this.holdPOCount = response.holdCount;
//         } else if (typeof response.data === 'number') {
//           this.holdPOCount = response.data;
//         } else if (typeof response.count === 'number') {
//           this.holdPOCount = response.count;
//         } else {
//           this.holdPOCount = 0;
//         }
//         console.log('%c[✅ HOLD PO COUNT]', 'color: #00aa00;', this.holdPOCount);
//         this.cdr.markForCheck();
//       },
//       error: () => { this.holdPOCount = 0; this.cdr.markForCheck(); }
//     });
//   }

//   // ==================== STATISTICS ====================

//   private calculateStatistics(): void {
//     this.statistics = {
//       totalRFQs: this.allRFQs.length,
//       pendingApprovals: this.pendingApprovalsCount,
//       approved: this.allRFQs.filter(rfq => rfq.approvalStatus === 'APPROVED').length,
//       rejected: this.allRFQs.filter(rfq => rfq.approvalStatus === 'REJECTED').length,
//       published: this.allRFQs.filter(rfq => rfq.status === 'PUBLISHED').length,
//       closed: this.allRFQs.filter(rfq => rfq.status === 'CLOSED').length,
//       draft: this.allRFQs.filter(rfq => rfq.status === 'DRAFT').length,
//       awaitingApproval: this.allRFQs.filter(rfq => rfq.status === 'AWAITING_APPROVAL').length
//     };
//     console.log('%c[STATISTICS]', 'color: #9c27b0;', this.statistics);
//   }

//   // ==================== TAB SWITCHING ====================

//   switchTab(tab: 'rfq' | 'po'): void {
//     this.activeTab = tab;
//     this.searchText = '';
//     this.poSearchText = '';
//     this.cdr.markForCheck();
//   }

//   // ==================== RFQ FILTERS & PAGINATION ====================

//   applyRFQFilters(): void {
//     let data = [...this.allRFQs];
//     if (this.searchText.trim()) {
//       const term = this.searchText.toLowerCase();
//       data = data.filter((rfq: any) =>
//         (rfq.rfqNumber || '').toLowerCase().includes(term) ||
//         (rfq.rfqTitle || '').toLowerCase().includes(term) ||
//         (rfq.buyer?.companyName || '').toLowerCase().includes(term)
//       );
//     }
//     if (this.statusFilter) data = data.filter((rfq: any) => rfq.status === this.statusFilter);
//     if (this.approvalStatusFilter) data = data.filter((rfq: any) => rfq.approvalStatus === this.approvalStatusFilter);
//     this.filteredRFQs = data;
//     this.currentPage = 1;
//     this.updateRFQPagination();
//   }

//   // Keep old method name for compatibility
//   applyFiltersAndPagination(): void {
//     this.applyRFQFilters();
//   }

//   private updateRFQPagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
//     this.cdr.markForCheck();
//   }

//   get totalPages(): number {
//     return Math.ceil(this.filteredRFQs.length / this.pageSize);
//   }

//   onPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.updateRFQPagination();
//     }
//   }

//   // ==================== PO FILTERS & PAGINATION ====================

//   applyPOFilters(): void {
//     let data = [...this.allPOs];
//     if (this.poSearchText.trim()) {
//       const term = this.poSearchText.toLowerCase();
//       data = data.filter((po: any) =>
//         (po.poNumber || '').toString().toLowerCase().includes(term) ||
//         (po.rfqNumber || '').toLowerCase().includes(term) ||
//         (po.supplierName || '').toLowerCase().includes(term)
//       );
//     }
//     if (this.poStatusFilter) {
//       data = data.filter((po: any) =>
//         (po.approvalStatus || po.status) === this.poStatusFilter
//       );
//     }
//     this.filteredPOs = data;
//     this.poCurrentPage = 1;
//     this.updatePOPagination();
//   }

//   private updatePOPagination(): void {
//     const start = (this.poCurrentPage - 1) * this.poPageSize;
//     this.pagedPOs = this.filteredPOs.slice(start, start + this.poPageSize);
//     this.cdr.markForCheck();
//   }

//   get totalPOPages(): number {
//     return Math.ceil(this.filteredPOs.length / this.poPageSize);
//   }

//   onPOPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPOPages) {
//       this.poCurrentPage = page;
//       this.updatePOPagination();
//     }
//   }

//   // ==================== NAVIGATION ====================

//   viewRFQDetails(rfq: any): void {
//     console.log('%c[VIEW RFQ]', 'color: #0066cc;', rfq.id);
//     this.router.navigate([`/create-rfq/${rfq.id}/view`]);
//   }

//   viewPODetails(po: any): void {
//     console.log('%c[VIEW PO]', 'color: #e65100;', po.id);
//     this.router.navigate([`/po-details/${po.id}`]);
//   }

//   navigateToPendingApprovals(): void {
//     this.router.navigate(['/pending-approvals']);
//   }

//   deleteRFQ(rfq: any): void {
//     const confirmed = confirm(
//       `Are you sure you want to delete RFQ ${rfq.rfqNumber}?\n\nTitle: ${rfq.rfqTitle}\n\nThis action cannot be undone.`
//     );
//     if (!confirmed) return;

//     this.rfqService.deleteRFQ(rfq.id).subscribe({
//       next: (response: any) => {
//         this.messageService.showMessage('success', 'Success', `RFQ ${rfq.rfqNumber} deleted successfully`);
//         this.loadData();
//       },
//       error: (error: any) => {
//         const errorMsg = error.error?.message || error.message || 'Failed to delete RFQ';
//         this.messageService.showMessage('error', 'Error', errorMsg);
//       }
//     });
//   }

//   refresh(): void {
//     this.loadData();
//   }

//   resetFilters(): void {
//     this.searchText = '';
//     this.statusFilter = '';
//     this.approvalStatusFilter = '';
//     this.poSearchText = '';
//     this.poStatusFilter = '';
//     this.applyRFQFilters();
//     this.applyPOFilters();
//   }

//   // ==================== UTILITY METHODS ====================

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try { return new Date(dateString).toLocaleDateString('en-GB'); } catch { return 'Invalid Date'; }
//   }

//   formatCurrency(amount: number | null): string {
//     if (amount == null || isNaN(amount)) return '₹0.00';
//     return '₹' + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//   }

//   getStatusClass(status: string): string {
//     const statusMap: { [key: string]: string } = {
//       'DRAFT': 'bg-secondary', 'AWAITING_APPROVAL': 'bg-warning text-dark',
//       'PUBLISHED': 'bg-info text-dark', 'RESPONSES_RECEIVED': 'bg-success',
//       'CLOSED': 'bg-danger', 'CANCELLED': 'bg-dark', 'HOLD': 'bg-warning text-dark'
//     };
//     return statusMap[status] || 'bg-light text-dark';
//   }

//   getApprovalStatusClass(status: string): string {
//     const statusMap: { [key: string]: string } = {
//       'PENDING': 'bg-warning text-dark', 'APPROVED': 'bg-success',
//       'REJECTED': 'bg-danger', 'PENDING_APPROVAL': 'bg-warning text-dark',
//       'HOLD': 'bg-orange text-dark', 'DRAFT': 'bg-secondary'
//     };
//     return statusMap[status] || 'bg-secondary';
//   }

//   getPOStatusClass(status: string): string {
//     const map: { [key: string]: string } = {
//       'DRAFT': 'badge-po-draft',
//       'PENDING_APPROVAL': 'badge-po-pending',
//       'APPROVED': 'badge-po-approved',
//       'REJECTED': 'badge-po-rejected',
//       'HOLD': 'badge-po-hold',
//       'SENT_TO_SUPPLIER': 'badge-po-sent',
//       'ACKNOWLEDGED': 'badge-po-ack',
//       'COMPLETED': 'badge-po-done',
//       'CANCELLED': 'badge-po-cancelled'
//     };
//     return map[status] || 'badge-po-draft';
//   }

//   getPOStatusLabel(status: string): string {
//     const map: { [key: string]: string } = {
//       'DRAFT': 'Draft', 'PENDING_APPROVAL': 'Pending Approval',
//       'APPROVED': 'Approved', 'REJECTED': 'Rejected',
//       'HOLD': 'On Hold', 'SENT_TO_SUPPLIER': 'Sent to Supplier',
//       'ACKNOWLEDGED': 'Acknowledged', 'COMPLETED': 'Completed', 'CANCELLED': 'Cancelled'
//     };
//     return map[status] || (status || 'Unknown').replace(/_/g, ' ');
//   }

//   getPriorityClass(priority: string): string {
//     const priorityMap: { [key: string]: string } = {
//       'LOW': 'bg-light text-dark', 'MEDIUM': 'bg-info text-white',
//       'HIGH': 'bg-warning text-dark', 'URGENT': 'bg-danger text-white'
//     };
//     return priorityMap[priority] || 'bg-secondary';
//   }

//   getStatusLabel(status: string): string {
//     const labelMap: { [key: string]: string } = {
//       'DRAFT': 'Draft', 'AWAITING_APPROVAL': 'Awaiting Approval', 'PUBLISHED': 'Published',
//       'RESPONSES_RECEIVED': 'Responses Received', 'CLOSED': 'Closed',
//       'CANCELLED': 'Cancelled', 'HOLD': 'On HOLD'
//     };
//     return labelMap[status] || status.replace(/_/g, ' ');
//   }
// }

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RFQService } from '../models/rfq.service';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import { AuthService } from '../../../shared/service/AuthService';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'filter', standalone: true })
export class FilterPipe implements PipeTransform {
  transform(items: any[], field: string, value: any): any[] {
    if (!items || !field) return items;
    return items.filter(item => item[field] === value);
  }
}

@Component({
  selector: 'app-hierarchy-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterPipe],
  templateUrl: './hierarchy-dashboard.component.html',
  styleUrls: ['./hierarchy-dashboard.component.css']
})
export class HierarchyDashboardComponent implements OnInit {

  // ==================== USER INFO ====================
  userId: number = 0;
  userName: string = '';
  userEmail: string = '';
  hierarchyLevelId: number | null = null;
  hierarchyLevelName: string = '';
  hierarchyLevelOrder: number | null = null;
  companyName: string = '';

  activeTab: 'rfq' | 'po' = 'rfq';

  allRFQs: any[] = [];
  filteredRFQs: any[] = [];
  pagedRFQs: any[] = [];

  allPOs: any[] = [];
  filteredPOs: any[] = [];
  pagedPOs: any[] = [];

  searchText = '';
  currentPage = 1;
  pageSize = 10;
  poSearchText = '';
  poCurrentPage = 1;
  poPageSize = 10;

  statusFilter = '';
  approvalStatusFilter = '';
  poStatusFilter = '';

  isLoading = false;
  isPOLoading = false;

  pendingApprovalsCount = 0;
  holdApprovalsCount: number = 0;
  pendingPOCount: number = 0;
  holdPOCount: number = 0;

  get approvedPOCount(): number {
    return this.allPOs.filter(po => (po.approvalStatus || po.status) === 'APPROVED').length;
  }

  get totalBadgeCount(): number {
    return this.pendingApprovalsCount + this.holdApprovalsCount + this.pendingPOCount + this.holdPOCount;
  }

  statistics = {
    totalRFQs: 0, pendingApprovals: 0, approved: 0,
    rejected: 0, published: 0, closed: 0, draft: 0, awaitingApproval: 0
  };

  statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED', 'HOLD'];
  approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
  poStatuses = ['DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'HOLD', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'COMPLETED', 'CANCELLED'];

  constructor(
    private rfqService: RFQService,
    private dataService: DataService,
    private messageService: MessageService,
    private authService: AuthService,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeUser();
    this.loadData();
  }

  private initializeUser(): void {
    this.userId            = Number(localStorage.getItem('userId')) || 0;
    this.userName          = localStorage.getItem('fullName') || 'User';
    this.userEmail         = localStorage.getItem('email') || localStorage.getItem('username') || '';
    this.companyName       = localStorage.getItem('companyName') || 'NA';
    this.hierarchyLevelId  = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || 'Hierarchy User';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
    if (!this.userId) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
    }
  }

  private loadData(): void {
    this.isLoading = true;
    this.dataService.getRFQDashboardList(this.userId, {}).subscribe({
      next: (response: any) => {
        let rawRFQs = response.data || [];
        this.allRFQs = rawRFQs.map((rfq: any) => ({
          ...rfq,
          suppliersCount: rfq.suppliersCount || 0,
          itemsCount: rfq.itemsCount || 0,
          buyerName: rfq.buyerName || 'N/A'
        }));

        if (this.companyName && this.companyName !== 'Unknown Company' && this.companyName !== 'NA') {
          this.allRFQs = this.allRFQs.filter((rfq: any) =>
            rfq.buyerName === this.companyName ||
            rfq.buyer?.organizationCompanyName === this.companyName ||
            rfq.buyer?.companyName === this.companyName
          );
        }

        this.calculateStatistics();
        this.applyRFQFilters();
        this.loadPendingApprovalCount();
        this.loadHoldApprovalCount();
        this.loadPendingPOApprovalCount();
        this.loadHoldPOApprovalCount();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => { this.isLoading = false; this.cdr.markForCheck(); }
    });
    this.loadPOs();
  }

  private loadPOs(): void {
    if (!this.userId) return;
    this.isPOLoading = true;
    this.dataService.getPurchaseOrdersByCompany(this.companyName).subscribe({
      next: (response: any) => {
        let rawPOs: any[] = [];
        if (Array.isArray(response)) rawPOs = response;
        else if (response && Array.isArray(response.data)) rawPOs = response.data;
        else if (response && Array.isArray(response.content)) rawPOs = response.content;

        this.allPOs = rawPOs.map((po: any) => ({
          ...po,
          supplierName:   po.supplierName   || po.supplier?.companyName || 'N/A',
          rfqNumber:      po.rfqNumber      || po.rfq?.rfqNumber || 'N/A',
          grandTotal:     po.grandTotal     || po.totalAmount    || 0,
          itemsCount:     po.itemsCount     || po.items?.length  || 0,
          poNumber:       po.poNumber       || po.id             || 'N/A',
          createdDate:    po.createdAt      || po.createdDate    || po.poDate || null,
          approvalStatus: po.approvalStatus || po.status         || 'DRAFT',
          // ✅ Carry through currency fields
          currencyCode:   po.currencyCode   || 'INR',
          currencySymbol: po.currencySymbol || '₹'
        }));

        this.applyPOFilters();
        this.isPOLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.allPOs = []; this.filteredPOs = []; this.pagedPOs = [];
        this.isPOLoading = false; this.cdr.markForCheck();
      }
    });
  }

  private loadPendingApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getPendingApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.pendingApprovalsCount =
          response.pendingCount ?? response.count ?? response.data ?? 0;
        this.statistics.pendingApprovals = this.pendingApprovalsCount;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingApprovalsCount = 0; }
    });
  }

  private loadHoldApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getHoldApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.holdApprovalsCount =
          response.holdCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.holdApprovalsCount = 0; }
    });
  }

  private loadPendingPOApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getPendingPOApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.pendingPOCount =
          response.pendingCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingPOCount = 0; }
    });
  }

  private loadHoldPOApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getHoldPOApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.holdPOCount =
          response.holdCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.holdPOCount = 0; }
    });
  }

  private calculateStatistics(): void {
    this.statistics = {
      totalRFQs:      this.allRFQs.length,
      pendingApprovals: this.pendingApprovalsCount,
      approved:       this.allRFQs.filter(r => r.approvalStatus === 'APPROVED').length,
      rejected:       this.allRFQs.filter(r => r.approvalStatus === 'REJECTED').length,
      published:      this.allRFQs.filter(r => r.status === 'PUBLISHED').length,
      closed:         this.allRFQs.filter(r => r.status === 'CLOSED').length,
      draft:          this.allRFQs.filter(r => r.status === 'DRAFT').length,
      awaitingApproval: this.allRFQs.filter(r => r.status === 'AWAITING_APPROVAL').length
    };
  }

  switchTab(tab: 'rfq' | 'po'): void {
    this.activeTab = tab;
    this.searchText = '';
    this.poSearchText = '';
    this.cdr.markForCheck();
  }

  applyRFQFilters(): void {
    let data = [...this.allRFQs];
    if (this.searchText.trim()) {
      const term = this.searchText.toLowerCase();
      data = data.filter((rfq: any) =>
        (rfq.rfqNumber || '').toLowerCase().includes(term) ||
        (rfq.rfqTitle || '').toLowerCase().includes(term) ||
        (rfq.buyer?.companyName || '').toLowerCase().includes(term)
      );
    }
    if (this.statusFilter) data = data.filter((rfq: any) => rfq.status === this.statusFilter);
    if (this.approvalStatusFilter) data = data.filter((rfq: any) => rfq.approvalStatus === this.approvalStatusFilter);
    this.filteredRFQs = data;
    this.currentPage = 1;
    this.updateRFQPagination();
  }

  applyFiltersAndPagination(): void { this.applyRFQFilters(); }

  private updateRFQPagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
    this.cdr.markForCheck();
  }

  get totalPages(): number { return Math.ceil(this.filteredRFQs.length / this.pageSize); }

  onPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPages) { this.currentPage = page; this.updateRFQPagination(); }
  }

  applyPOFilters(): void {
    let data = [...this.allPOs];
    if (this.poSearchText.trim()) {
      const term = this.poSearchText.toLowerCase();
      data = data.filter((po: any) =>
        (po.poNumber || '').toString().toLowerCase().includes(term) ||
        (po.rfqNumber || '').toLowerCase().includes(term) ||
        (po.supplierName || '').toLowerCase().includes(term)
      );
    }
    if (this.poStatusFilter) {
      data = data.filter((po: any) => (po.approvalStatus || po.status) === this.poStatusFilter);
    }
    this.filteredPOs = data;
    this.poCurrentPage = 1;
    this.updatePOPagination();
  }

  private updatePOPagination(): void {
    const start = (this.poCurrentPage - 1) * this.poPageSize;
    this.pagedPOs = this.filteredPOs.slice(start, start + this.poPageSize);
    this.cdr.markForCheck();
  }

  get totalPOPages(): number { return Math.ceil(this.filteredPOs.length / this.poPageSize); }

  onPOPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPOPages) { this.poCurrentPage = page; this.updatePOPagination(); }
  }

  viewRFQDetails(rfq: any): void { this.router.navigate([`/create-rfq/${rfq.id}/view`]); }
  viewPODetails(po: any): void { this.router.navigate([`/po-details/${po.id}`]); }
  navigateToPendingApprovals(): void { this.router.navigate(['/pending-approvals']); }

  deleteRFQ(rfq: any): void {
    if (!confirm(`Delete RFQ ${rfq.rfqNumber}?\n\nThis cannot be undone.`)) return;
    this.rfqService.deleteRFQ(rfq.id).subscribe({
      next: () => { this.messageService.showMessage('success', 'Success', `RFQ deleted`); this.loadData(); },
      error: (error: any) => this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete')
    });
  }

  refresh(): void { this.loadData(); }
  resetFilters(): void {
    this.searchText = ''; this.statusFilter = ''; this.approvalStatusFilter = '';
    this.poSearchText = ''; this.poStatusFilter = '';
    this.applyRFQFilters(); this.applyPOFilters();
  }

  // ==================== UTILITY ====================

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try { return new Date(dateString).toLocaleDateString('en-GB'); } catch { return 'Invalid Date'; }
  }

  /**
   * ✅ Format currency using the code from the PO object.
   * Accepts optional currencyCode for dynamic display.
   */
  formatCurrency(amount: number | null, currencyCode?: string): string {
    const code   = currencyCode || 'INR';
    const symbol = this.getSymbolForCode(code);
    if (amount == null || isNaN(Number(amount))) return `${symbol} 0.00`;
    const formatted = Number(amount).toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
  }

  private getSymbolForCode(code: string): string {
    const map: Record<string, string> = {
      'INR': '₹', 'USD': '$', 'EUR': '€', 'GBP': '£',
      'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
      'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$', 'NZD': 'NZ$',
      'SAR': 'ر.س', 'QAR': 'ر.ق', 'KWD': 'د.ك', 'BHD': '.د.ب',
      'OMR': 'ر.ع.', 'MYR': 'RM', 'THB': '฿', 'IDR': 'Rp',
    };
    return map[code] || code;
  }

  getStatusClass(status: string): string {
    const m: any = {
      'DRAFT': 'bg-secondary', 'AWAITING_APPROVAL': 'bg-warning text-dark',
      'PUBLISHED': 'bg-info text-dark', 'RESPONSES_RECEIVED': 'bg-success',
      'CLOSED': 'bg-danger', 'CANCELLED': 'bg-dark', 'HOLD': 'bg-warning text-dark'
    };
    return m[status] || 'bg-light text-dark';
  }

  getApprovalStatusClass(status: string): string {
    const m: any = {
      'PENDING': 'bg-warning text-dark', 'APPROVED': 'bg-success',
      'REJECTED': 'bg-danger', 'PENDING_APPROVAL': 'bg-warning text-dark',
      'HOLD': 'bg-warning text-dark', 'DRAFT': 'bg-secondary'
    };
    return m[status] || 'bg-secondary';
  }

  getPOStatusClass(status: string): string {
    const m: any = {
      'DRAFT': 'badge-po-draft', 'PENDING_APPROVAL': 'badge-po-pending',
      'APPROVED': 'badge-po-approved', 'REJECTED': 'badge-po-rejected',
      'HOLD': 'badge-po-hold', 'SENT_TO_SUPPLIER': 'badge-po-sent',
      'ACKNOWLEDGED': 'badge-po-ack', 'COMPLETED': 'badge-po-done',
      'CANCELLED': 'badge-po-cancelled'
    };
    return m[status] || 'badge-po-draft';
  }

  getPOStatusLabel(status: string): string {
    const m: any = {
      'DRAFT': 'Draft', 'PENDING_APPROVAL': 'Pending Approval',
      'APPROVED': 'Approved', 'REJECTED': 'Rejected', 'HOLD': 'On Hold',
      'SENT_TO_SUPPLIER': 'Sent to Supplier', 'ACKNOWLEDGED': 'Acknowledged',
      'COMPLETED': 'Completed', 'CANCELLED': 'Cancelled'
    };
    return m[status] || (status || 'Unknown').replace(/_/g, ' ');
  }

  getStatusLabel(status: string): string {
    const m: any = {
      'DRAFT': 'Draft', 'AWAITING_APPROVAL': 'Awaiting Approval', 'PUBLISHED': 'Published',
      'RESPONSES_RECEIVED': 'Responses Received', 'CLOSED': 'Closed',
      'CANCELLED': 'Cancelled', 'HOLD': 'On HOLD'
    };
    return m[status] || status.replace(/_/g, ' ');
  }
}