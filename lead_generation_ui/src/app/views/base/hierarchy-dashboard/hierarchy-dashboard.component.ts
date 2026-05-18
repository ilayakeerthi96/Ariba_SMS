

// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from '../models/rfq.service';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';
// import { Pipe, PipeTransform } from '@angular/core';

// // ── Financial Year option shape ──────────────────────────────
// interface FYOption {
//   value: string;
//   label: string;
//   from: Date;
//   to: Date;
// }

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

//   activeTab: 'rfq' | 'po' = 'rfq';

//   allRFQs: any[] = [];
//   filteredRFQs: any[] = [];
//   pagedRFQs: any[] = [];

//   allPOs: any[] = [];
//   filteredPOs: any[] = [];
//   pagedPOs: any[] = [];

//   // ── Date-filtered sources (stat cards use these) ─────────────
//   dateFilteredRFQs: any[] = [];
//   dateFilteredPOs: any[] = [];

//   searchText = '';
//   currentPage = 1;
//   pageSize = 10;
//   poSearchText = '';
//   poCurrentPage = 1;
//   poPageSize = 10;

//   statusFilter = '';
//   approvalStatusFilter = '';
//   poStatusFilter = '';

//   isLoading = false;
//   isPOLoading = false;

//   pendingApprovalsCount = 0;
//   holdApprovalsCount: number = 0;
//   pendingPOCount: number = 0;
//   holdPOCount: number = 0;

//   // Add these properties
// downloadingRfqId: number | null = null;
// downloadingRfqType: 'excel' | 'pdf' | null = null;

// // Add these methods
// // downloadRFQExcel(rfq: any): void {
// //     this.downloadingRfqId   = rfq.id;
// //     this.downloadingRfqType = 'excel';
// //     this.dataService.getRFQSummaryExcels(rfq.id, this.userId).subscribe({
// //         next: (blob: Blob) => {
// //             this.dataService.saveBlob(blob, 'RFQ_Summary_' + rfq.rfqNumber + '_' + this.ts() + '.xlsx');
// //             this.clearDownloadState();
// //             this.messageService.showMessage('success', 'Downloaded', rfq.rfqNumber + ' Excel downloaded');
// //         },
// //         error: () => {
// //             this.messageService.showMessage('error', 'Error', 'Failed to download');
// //             this.clearDownloadState();
// //         }
// //     });
// // }
// downloadRFQExcel(rfq: any): void {
//     this.downloadingRfqId   = rfq.id;
//     this.downloadingRfqType = 'excel';
//     this.dataService.getRFQSummaryExcels(rfq.id, this.userId).subscribe({
//         next: (blob: Blob) => {
//             this.dataService.saveBlob(blob, 'RFQ_Summary_' + rfq.rfqNumber + '_' + this.ts() + '.xlsx');
//             this.clearDownloadState();
//             this.messageService.showMessage('success', 'Downloaded', rfq.rfqNumber + ' downloaded');
//         },
//         error: () => {
//             this.messageService.showMessage('error', 'Error', 'Failed to download');
//             this.clearDownloadState();
//         }
//     });
// }

// isRowDownloading(rfqId: number): boolean {
//   return this.downloadingRfqId === rfqId;
// }

// private clearDownloadState(): void {
//   this.downloadingRfqId   = null;
//   this.downloadingRfqType = null;
// }

// private ts(): string {
//   return new Date().toISOString().slice(0, 10);
// }

//   get approvedPOCount(): number {
//     return this.dateFilteredPOs.filter(po => (po.approvalStatus || po.status) === 'APPROVED').length;
//   }

//   get totalBadgeCount(): number {
//     return this.pendingApprovalsCount + this.holdApprovalsCount + this.pendingPOCount + this.holdPOCount;
//   }

//   statistics = {
//     totalRFQs: 0, pendingApprovals: 0, approved: 0,
//     rejected: 0, published: 0, closed: 0, draft: 0, awaitingApproval: 0
//   };

//   statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED', 'HOLD'];
//   approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
//   poStatuses = ['DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'HOLD', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'COMPLETED', 'CANCELLED'];

//   // ── Date Filter State ────────────────────────────────────────
//   financialYearOptions: FYOption[] = [];
//   selectedFYOption: string = '';
//   customFromDate: string = '';
//   customToDate: string = '';
//   activeDateRangeLabel: string = '';

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
//     this.buildFinancialYearOptions();
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.updateActiveDateRangeLabel();
//     this.loadData();
//   }

//   private initializeUser(): void {
//     this.userId            = Number(localStorage.getItem('userId')) || 0;
//     this.userName          = localStorage.getItem('fullName') || 'User';
//     this.userEmail         = localStorage.getItem('email') || localStorage.getItem('username') || '';
//     this.companyName       = localStorage.getItem('companyName') || 'NA';
//     this.hierarchyLevelId  = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || 'Hierarchy User';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
//     if (!this.userId) {
//       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//       this.router.navigate(['/login']);
//     }
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
//     this.applyRFQFilters();
//     this.applyPOFilters();
//   }

//   resetDateFilter(): void {
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.customFromDate   = '';
//     this.customToDate     = '';
//     this.updateActiveDateRangeLabel();
//     this.applyRFQFilters();
//     this.applyPOFilters();
//   }

//   private updateActiveDateRangeLabel(): void {
//     if (this.selectedFYOption === 'ALL') { this.activeDateRangeLabel = 'All Time'; return; }
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
//     const d = new Date(dateStr + 'T00:00:00');
//     return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
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

//   private applyDateFilterToArray(items: any[], dateField: string = 'createdAt'): any[] {
//     const range = this.getActiveDateRange();
//     if (!range) return items;
//     return items.filter((item: any) => {
//       const dateStr = item[dateField] || item.createdAt;
//       if (!dateStr) return false;
//       const d = new Date(dateStr);
//       return d >= range.from && d <= range.to;
//     });
//   }

//   // =========================================================================
//   //  DATA LOADING
//   // =========================================================================

//   private loadData(): void {
//     this.isLoading = true;
//     this.dataService.getRFQDashboardList(this.userId, {}).subscribe({
//       next: (response: any) => {
//         let rawRFQs = response.data || [];
//         this.allRFQs = rawRFQs.map((rfq: any) => ({
//           ...rfq,
//           suppliersCount: rfq.suppliersCount || 0,
//           itemsCount: rfq.itemsCount || 0,
//           buyerName: rfq.buyerName || 'N/A'
//         }));

//         if (this.companyName && this.companyName !== 'Unknown Company' && this.companyName !== 'NA') {
//           this.allRFQs = this.allRFQs.filter((rfq: any) =>
//             rfq.buyerName === this.companyName ||
//             rfq.buyer?.organizationCompanyName === this.companyName ||
//             rfq.buyer?.companyName === this.companyName
//           );
//         }

//         this.calculateStatistics();
//         this.applyRFQFilters();
//         this.loadPendingApprovalCount();
//         this.loadHoldApprovalCount();
//         this.loadPendingPOApprovalCount();
//         this.loadHoldPOApprovalCount();
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.isLoading = false; this.cdr.markForCheck(); }
//     });
//     this.loadPOs();
//   }

//   private loadPOs(): void {
//     if (!this.userId) return;
//     this.isPOLoading = true;
//     this.dataService.getPurchaseOrdersByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         let rawPOs: any[] = [];
//         if (Array.isArray(response)) rawPOs = response;
//         else if (response && Array.isArray(response.data)) rawPOs = response.data;
//         else if (response && Array.isArray(response.content)) rawPOs = response.content;

//         this.allPOs = rawPOs.map((po: any) => ({
//           ...po,
//           supplierName:   po.supplierName   || po.supplier?.companyName || 'N/A',
//           rfqNumber:      po.rfqNumber      || po.rfq?.rfqNumber || 'N/A',
//           grandTotal:     po.grandTotal     || po.totalAmount    || 0,
//           itemsCount:     po.itemsCount     || po.items?.length  || 0,
//           poNumber:       po.poNumber       || po.id             || 'N/A',
//           createdDate:    po.createdAt      || po.createdDate    || po.poDate || null,
//           approvalStatus: po.approvalStatus || po.status         || 'DRAFT',
//           currencyCode:   po.currencyCode   || 'INR',
//           currencySymbol: po.currencySymbol || '₹'
//         }));

//         this.applyPOFilters();
//         this.isPOLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.allPOs = []; this.filteredPOs = []; this.pagedPOs = [];
//         this.isPOLoading = false; this.cdr.markForCheck();
//       }
//     });
//   }

//   private loadPendingApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         this.pendingApprovalsCount = response.pendingCount ?? response.count ?? response.data ?? 0;
//         this.statistics.pendingApprovals = this.pendingApprovalsCount;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.pendingApprovalsCount = 0; }
//     });
//   }

//   private loadHoldApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         this.holdApprovalsCount = response.holdCount ?? response.count ?? response.data ?? 0;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.holdApprovalsCount = 0; }
//     });
//   }

//   private loadPendingPOApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getPendingPOApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         this.pendingPOCount = response.pendingCount ?? response.count ?? response.data ?? 0;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.pendingPOCount = 0; }
//     });
//   }

//   private loadHoldPOApprovalCount(): void {
//     if (!this.userId) return;
//     this.dataService.getHoldPOApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         this.holdPOCount = response.holdCount ?? response.count ?? response.data ?? 0;
//         this.cdr.markForCheck();
//       },
//       error: () => { this.holdPOCount = 0; }
//     });
//   }

//   private calculateStatistics(): void {
//     this.statistics = {
//       totalRFQs:        this.allRFQs.length,
//       pendingApprovals: this.pendingApprovalsCount,
//       approved:         this.allRFQs.filter(r => r.approvalStatus === 'APPROVED').length,
//       rejected:         this.allRFQs.filter(r => r.approvalStatus === 'REJECTED').length,
//       published:        this.allRFQs.filter(r => r.status === 'PUBLISHED').length,
//       closed:           this.allRFQs.filter(r => r.status === 'CLOSED').length,
//       draft:            this.allRFQs.filter(r => r.status === 'DRAFT').length,
//       awaitingApproval: this.allRFQs.filter(r => r.status === 'AWAITING_APPROVAL').length
//     };
//   }

//   // =========================================================================
//   //  FILTERING & PAGINATION
//   // =========================================================================

//   switchTab(tab: 'rfq' | 'po'): void {
//     this.activeTab = tab;
//     this.searchText = '';
//     this.poSearchText = '';
//     this.cdr.markForCheck();
//   }

//   applyRFQFilters(): void {
//     this.updateActiveDateRangeLabel();
//     const dateFiltered = this.applyDateFilterToArray(this.allRFQs, 'createdAt');
//     this.dateFilteredRFQs = dateFiltered;

//     let data = [...dateFiltered];
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

//   applyFiltersAndPagination(): void { this.applyRFQFilters(); }

//   private updateRFQPagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
//     this.cdr.markForCheck();
//   }

//   get totalPages(): number { return Math.ceil(this.filteredRFQs.length / this.pageSize); }

//   onPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPages) { this.currentPage = page; this.updateRFQPagination(); }
//   }

//   applyPOFilters(): void {
//     this.updateActiveDateRangeLabel();
//     const dateFiltered = this.applyDateFilterToArray(this.allPOs, 'createdDate');
//     this.dateFilteredPOs = dateFiltered;

//     let data = [...dateFiltered];
//     if (this.poSearchText.trim()) {
//       const term = this.poSearchText.toLowerCase();
//       data = data.filter((po: any) =>
//         (po.poNumber || '').toString().toLowerCase().includes(term) ||
//         (po.rfqNumber || '').toLowerCase().includes(term) ||
//         (po.supplierName || '').toLowerCase().includes(term)
//       );
//     }
//     if (this.poStatusFilter) {
//       data = data.filter((po: any) => (po.approvalStatus || po.status) === this.poStatusFilter);
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

//   get totalPOPages(): number { return Math.ceil(this.filteredPOs.length / this.poPageSize); }

//   onPOPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPOPages) { this.poCurrentPage = page; this.updatePOPagination(); }
//   }

//   // =========================================================================
//   //  NAVIGATION & ACTIONS
//   // =========================================================================

//   viewRFQDetails(rfq: any): void { this.router.navigate([`/create-rfq/${rfq.id}/view`]); }
//   viewPODetails(po: any): void { this.router.navigate([`/po-details/${po.id}`]); }
//   navigateToPendingApprovals(): void { this.router.navigate(['/pending-approvals']); }

//   deleteRFQ(rfq: any): void {
//     if (!confirm(`Delete RFQ ${rfq.rfqNumber}?\n\nThis cannot be undone.`)) return;
//     this.rfqService.deleteRFQ(rfq.id).subscribe({
//       next: () => { this.messageService.showMessage('success', 'Success', `RFQ deleted`); this.loadData(); },
//       error: (error: any) => this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete')
//     });
//   }

//   refresh(): void { this.loadData(); }

//   resetFilters(): void {
//     this.searchText = ''; this.statusFilter = ''; this.approvalStatusFilter = '';
//     this.poSearchText = ''; this.poStatusFilter = '';
//     this.applyRFQFilters(); this.applyPOFilters();
//   }

//   // =========================================================================
//   //  UTILITIES
//   // =========================================================================

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try { return new Date(dateString).toLocaleDateString('en-GB'); } catch { return 'Invalid Date'; }
//   }

//   formatCurrency(amount: number | null, currencyCode?: string): string {
//     const code   = currencyCode || 'INR';
//     const symbol = this.getSymbolForCode(code);
//     if (amount == null || isNaN(Number(amount))) return `${symbol} 0.00`;
//     const formatted = Number(amount).toLocaleString('en-IN', {
//       minimumFractionDigits: 2, maximumFractionDigits: 2
//     });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
//   }

//   private getSymbolForCode(code: string): string {
//     const map: Record<string, string> = {
//       'INR': '₹', 'USD': '$', 'EUR': '€', 'GBP': '£',
//       'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
//       'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$', 'NZD': 'NZ$',
//       'SAR': 'ر.س', 'QAR': 'ر.ق', 'KWD': 'د.ك', 'BHD': '.د.ب',
//       'OMR': 'ر.ع.', 'MYR': 'RM', 'THB': '฿', 'IDR': 'Rp',
//     };
//     return map[code] || code;
//   }

//   getStatusClass(status: string): string {
//     const m: any = {
//       'DRAFT': 'bg-secondary', 'AWAITING_APPROVAL': 'bg-warning text-dark',
//       'PUBLISHED': 'bg-info text-dark', 'RESPONSES_RECEIVED': 'bg-success',
//       'CLOSED': 'bg-danger', 'CANCELLED': 'bg-dark', 'HOLD': 'bg-warning text-dark'
//     };
//     return m[status] || 'bg-light text-dark';
//   }

//   getApprovalStatusClass(status: string): string {
//     const m: any = {
//       'PENDING': 'bg-warning text-dark', 'APPROVED': 'bg-success',
//       'REJECTED': 'bg-danger', 'PENDING_APPROVAL': 'bg-warning text-dark',
//       'HOLD': 'bg-warning text-dark', 'DRAFT': 'bg-secondary'
//     };
//     return m[status] || 'bg-secondary';
//   }

//   getPOStatusClass(status: string): string {
//     const m: any = {
//       'DRAFT': 'badge-po-draft', 'PENDING_APPROVAL': 'badge-po-pending',
//       'APPROVED': 'badge-po-approved', 'REJECTED': 'badge-po-rejected',
//       'HOLD': 'badge-po-hold', 'SENT_TO_SUPPLIER': 'badge-po-sent',
//       'ACKNOWLEDGED': 'badge-po-ack', 'COMPLETED': 'badge-po-done',
//       'CANCELLED': 'badge-po-cancelled'
//     };
//     return m[status] || 'badge-po-draft';
//   }

//   getPOStatusLabel(status: string): string {
//     const m: any = {
//       'DRAFT': 'Draft', 'PENDING_APPROVAL': 'Pending Approval',
//       'APPROVED': 'Approved', 'REJECTED': 'Rejected', 'HOLD': 'On Hold',
//       'SENT_TO_SUPPLIER': 'Sent to Supplier', 'ACKNOWLEDGED': 'Acknowledged',
//       'COMPLETED': 'Completed', 'CANCELLED': 'Cancelled'
//     };
//     return m[status] || (status || 'Unknown').replace(/_/g, ' ');
//   }

//   getStatusLabel(status: string): string {
//     const m: any = {
//       'DRAFT': 'Draft', 'AWAITING_APPROVAL': 'Awaiting Approval', 'PUBLISHED': 'Published',
//       'RESPONSES_RECEIVED': 'Responses Received', 'CLOSED': 'Closed',
//       'CANCELLED': 'Cancelled', 'HOLD': 'On HOLD'
//     };
//     return m[status] || status.replace(/_/g, ' ');
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

interface FYOption {
  value: string;
  label: string;
  from: Date;
  to: Date;
}

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

  // ── User info ─────────────────────────────────────────────────
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

  dateFilteredRFQs: any[] = [];
  dateFilteredPOs: any[] = [];

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

  // ── RFQ / PO counts ──────────────────────────────────────────
  pendingApprovalsCount = 0;
  holdApprovalsCount: number = 0;
  pendingPOCount: number = 0;
  holdPOCount: number = 0;

  // ── ✅ Supplier counts ────────────────────────────────────────
  pendingSupplierCount: number = 0;
  holdSupplierCount: number = 0;

  downloadingRfqId: number | null = null;
  downloadingRfqType: 'excel' | 'pdf' | null = null;

  downloadRFQExcel(rfq: any): void {
    this.downloadingRfqId   = rfq.id;
    this.downloadingRfqType = 'excel';
    this.dataService.getRFQSummaryExcels(rfq.id, this.userId).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, 'RFQ_Summary_' + rfq.rfqNumber + '_' + this.ts() + '.xlsx');
        this.clearDownloadState();
        this.messageService.showMessage('success', 'Downloaded', rfq.rfqNumber + ' downloaded');
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to download');
        this.clearDownloadState();
      }
    });
  }

  isRowDownloading(rfqId: number): boolean { return this.downloadingRfqId === rfqId; }
  private clearDownloadState(): void { this.downloadingRfqId = null; this.downloadingRfqType = null; }
  private ts(): string { return new Date().toISOString().slice(0, 10); }

  get approvedPOCount(): number {
    return this.dateFilteredPOs.filter(po => (po.approvalStatus || po.status) === 'APPROVED').length;
  }

  // ✅ Total badge = RFQ pending + RFQ hold + PO pending + PO hold + Supplier pending + Supplier hold
  get totalBadgeCount(): number {
    return this.pendingApprovalsCount + this.holdApprovalsCount +
           this.pendingPOCount + this.holdPOCount +
           this.pendingSupplierCount + this.holdSupplierCount;
  }

  statistics = {
    totalRFQs: 0, pendingApprovals: 0, approved: 0,
    rejected: 0, published: 0, closed: 0, draft: 0, awaitingApproval: 0
  };

  statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED', 'HOLD'];
  approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
  poStatuses = ['DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'HOLD', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'COMPLETED', 'CANCELLED'];

  financialYearOptions: FYOption[] = [];
  selectedFYOption: string = '';
  customFromDate: string = '';
  customToDate: string = '';
  activeDateRangeLabel: string = '';

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
    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();
    this.loadData();
  }

  private initializeUser(): void {
    this.userId             = Number(localStorage.getItem('userId')) || 0;
    this.userName           = localStorage.getItem('fullName') || 'User';
    this.userEmail          = localStorage.getItem('email') || localStorage.getItem('username') || '';
    this.companyName        = localStorage.getItem('companyName') || 'NA';
    this.hierarchyLevelId   = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || 'Hierarchy User';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
    if (!this.userId) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
    }
  }

  // ── Financial year helpers ────────────────────────────────────

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
        from, to
      });
    }
  }

  getCurrentFYValue(): string {
    return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL';
  }

  onFYOptionChange(): void {
    if (this.selectedFYOption !== 'CUSTOM') { this.customFromDate = ''; this.customToDate = ''; }
    this.updateActiveDateRangeLabel();
    this.applyRFQFilters();
    this.applyPOFilters();
  }

  resetDateFilter(): void {
    this.selectedFYOption = this.getCurrentFYValue();
    this.customFromDate = '';
    this.customToDate = '';
    this.updateActiveDateRangeLabel();
    this.applyRFQFilters();
    this.applyPOFilters();
  }

  private updateActiveDateRangeLabel(): void {
    if (this.selectedFYOption === 'ALL') { this.activeDateRangeLabel = 'All Time'; return; }
    if (this.selectedFYOption === 'CUSTOM') {
      if (this.customFromDate && this.customToDate)
        this.activeDateRangeLabel = `${this.formatDisplayDate(this.customFromDate)} – ${this.formatDisplayDate(this.customToDate)}`;
      else if (this.customFromDate) this.activeDateRangeLabel = `From ${this.formatDisplayDate(this.customFromDate)}`;
      else if (this.customToDate)   this.activeDateRangeLabel = `Up to ${this.formatDisplayDate(this.customToDate)}`;
      else this.activeDateRangeLabel = 'Custom Range';
      return;
    }
    const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
    this.activeDateRangeLabel = fy ? fy.label : '';
  }

  private formatDisplayDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr + 'T00:00:00').toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
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

  private applyDateFilterToArray(items: any[], dateField: string = 'createdAt'): any[] {
    const range = this.getActiveDateRange();
    if (!range) return items;
    return items.filter((item: any) => {
      const dateStr = item[dateField] || item.createdAt;
      if (!dateStr) return false;
      const d = new Date(dateStr);
      return d >= range.from && d <= range.to;
    });
  }

  // ── Data loading ──────────────────────────────────────────────

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
        // ✅ Load supplier counts
        this.loadPendingSupplierCount();
        this.loadHoldSupplierCount();
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
        this.pendingApprovalsCount = response.pendingCount ?? response.count ?? response.data ?? 0;
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
        this.holdApprovalsCount = response.holdCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.holdApprovalsCount = 0; }
    });
  }

  private loadPendingPOApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getPendingPOApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.pendingPOCount = response.pendingCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingPOCount = 0; }
    });
  }

  private loadHoldPOApprovalCount(): void {
    if (!this.userId) return;
    this.dataService.getHoldPOApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.holdPOCount = response.holdCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.holdPOCount = 0; }
    });
  }

  // ✅ NEW: Load supplier pending/hold counts
  private loadPendingSupplierCount(): void {
    if (!this.userId) return;
    this.dataService.getPendingSupplierApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.pendingSupplierCount = response.pendingCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingSupplierCount = 0; }
    });
  }

  private loadHoldSupplierCount(): void {
    if (!this.userId) return;
    this.dataService.getHoldSupplierApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        this.holdSupplierCount = response.holdCount ?? response.count ?? response.data ?? 0;
        this.cdr.markForCheck();
      },
      error: () => { this.holdSupplierCount = 0; }
    });
  }

  private calculateStatistics(): void {
    this.statistics = {
      totalRFQs:        this.allRFQs.length,
      pendingApprovals: this.pendingApprovalsCount,
      approved:         this.allRFQs.filter(r => r.approvalStatus === 'APPROVED').length,
      rejected:         this.allRFQs.filter(r => r.approvalStatus === 'REJECTED').length,
      published:        this.allRFQs.filter(r => r.status === 'PUBLISHED').length,
      closed:           this.allRFQs.filter(r => r.status === 'CLOSED').length,
      draft:            this.allRFQs.filter(r => r.status === 'DRAFT').length,
      awaitingApproval: this.allRFQs.filter(r => r.status === 'AWAITING_APPROVAL').length
    };
  }

  // ── Filtering & pagination ────────────────────────────────────

  switchTab(tab: 'rfq' | 'po'): void {
    this.activeTab = tab;
    this.searchText = '';
    this.poSearchText = '';
    this.cdr.markForCheck();
  }

  applyRFQFilters(): void {
    this.updateActiveDateRangeLabel();
    const dateFiltered = this.applyDateFilterToArray(this.allRFQs, 'createdAt');
    this.dateFilteredRFQs = dateFiltered;

    let data = [...dateFiltered];
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
    this.updateActiveDateRangeLabel();
    const dateFiltered = this.applyDateFilterToArray(this.allPOs, 'createdDate');
    this.dateFilteredPOs = dateFiltered;

    let data = [...dateFiltered];
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

  // ── Navigation ────────────────────────────────────────────────

  viewRFQDetails(rfq: any): void { this.router.navigate([`/create-rfq/${rfq.id}/view`]); }
  viewPODetails(po: any): void { this.router.navigate([`/po-details/${po.id}`]); }
  navigateToPendingApprovals(): void { this.router.navigate(['/pending-approvals']); }

  deleteRFQ(rfq: any): void {
    if (!confirm(`Delete RFQ ${rfq.rfqNumber}?\n\nThis cannot be undone.`)) return;
    this.rfqService.deleteRFQ(rfq.id).subscribe({
      next: () => { this.messageService.showMessage('success', 'Success', 'RFQ deleted'); this.loadData(); },
      error: (error: any) => this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete')
    });
  }

  refresh(): void { this.loadData(); }

  resetFilters(): void {
    this.searchText = ''; this.statusFilter = ''; this.approvalStatusFilter = '';
    this.poSearchText = ''; this.poStatusFilter = '';
    this.applyRFQFilters(); this.applyPOFilters();
  }

  // ── Utilities ─────────────────────────────────────────────────

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try { return new Date(dateString).toLocaleDateString('en-GB'); } catch { return 'Invalid Date'; }
  }

  formatCurrency(amount: number | null, currencyCode?: string): string {
    const code   = currencyCode || 'INR';
    const symbol = this.getSymbolForCode(code);
    if (amount == null || isNaN(Number(amount))) return `${symbol} 0.00`;
    const formatted = Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
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