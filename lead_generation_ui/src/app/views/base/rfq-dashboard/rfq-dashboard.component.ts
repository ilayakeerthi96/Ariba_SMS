

// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from '../models/rfq.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { RFQ } from '../models/rfq.model';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { StorageService } from '../../../shared/service/StorageService';
// import { RfqApprovalService } from '../models/rfq-approval.service';
// import { DataService } from '../../../shared/service/DataService';


// // ── Financial Year option shape ──────────────────────────────
// interface FYOption {
//   value: string;   // e.g. 'FY2025-26'
//   label: string;   // e.g. 'FY 2025-26 (Apr 2025 – Mar 2026)'
//   from: Date;
//   to: Date;
// }

// @Component({
//   selector: 'app-rfq-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './rfq-dashboard.component.html',
//   styleUrls: ['./rfq-dashboard.component.css']
// })
// export class RFQDashboardComponent implements OnInit {

//   rfqs: RFQ[] = [];

//   // ── Date-filtered source (stat cards use this) ───────────────
//   dateFilteredRFQs: any[] = [];

//   // ── Further filtered + paginated (table uses this) ───────────
//   filteredRFQs: any[] = [];
//   pagedRFQs: any[] = [];

//   loggedInBuyerId: number | null = null;
//   loggedInBuyer: any = null;

//   searchText = '';
//   currentPage = 1;
//   pageSize = 5;

//   statusFilter = '';
//   priorityFilter = '';
//   buyerFilter = '';

//   isLoading = false;
//   Math = Math;

//   statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED', 'HOLD', 'RETURNED_FOR_REVISION'];
//   priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
//   buyers: any[] = [];

//   activeStatusFilter: string = 'ALL';
//   pageSizeOptions: number[] = [5, 10, 25, 50];

//   // ── Date Filter State ────────────────────────────────────────
//   financialYearOptions: FYOption[] = [];
//   selectedFYOption: string = '';   // 'ALL' | 'FY2025-26' | 'CUSTOM'
//   customFromDate: string = '';     // 'YYYY-MM-DD'
//   customToDate: string = '';       // 'YYYY-MM-DD'
//   activeDateRangeLabel: string = '';

//   constructor(
//     private rfqService: RFQService,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private dataService: DataService,
//     public router: Router,
//     private approvalService: RfqApprovalService
//   ) {}

//   ngOnInit(): void {
//     this.buildFinancialYearOptions();
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.updateActiveDateRangeLabel();

//     this.getLoggedInBuyerId();
//     this.loadLoggedInBuyer();
//     this.loadRFQs();
//   }

//   // ── FINANCIAL YEAR HELPERS ────────────────────────────────────

//   /**
//    * Build a list of FY options: current FY + 3 previous FYs.
//    * Indian financial year: April 1 → March 31.
//    */
//   private buildFinancialYearOptions(): void {
//     const today = new Date();
//     // Determine current FY start year: if month < April (0-indexed: 3), FY started previous year
//     let currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;

//     this.financialYearOptions = [];

//     // Build 4 FY options (current + 3 previous)
//     for (let i = 0; i < 4; i++) {
//       const startYear = currentFYStartYear - i;
//       const endYear = startYear + 1;
//       const from = new Date(startYear, 3, 1, 0, 0, 0, 0);           // Apr 1 of startYear
//       const to   = new Date(endYear,   2, 31, 23, 59, 59, 999);     // Mar 31 of endYear

//       this.financialYearOptions.push({
//         value: `FY${startYear}-${String(endYear).slice(-2)}`,
//         label: `FY ${startYear}-${String(endYear).slice(-2)}  (Apr ${startYear} – Mar ${endYear})`,
//         from,
//         to
//       });
//     }
//   }

//   /** Returns the value string of the current financial year. */
//   getCurrentFYValue(): string {
//     return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL';
//   }

//   /** Called when the FY dropdown changes. */
//   onFYOptionChange(): void {
//     // Clear custom dates when switching away from CUSTOM
//     if (this.selectedFYOption !== 'CUSTOM') {
//       this.customFromDate = '';
//       this.customToDate = '';
//     }
//     this.updateActiveDateRangeLabel();
//     this.applyFiltersAndPagination();
//   }

//   /** Resets date filter back to the current financial year. */
//   resetDateFilter(): void {
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.customFromDate = '';
//     this.customToDate = '';
//     this.updateActiveDateRangeLabel();
//     this.applyFiltersAndPagination();
//   }

//   /** Builds the human-readable label shown as the active badge. */
//   private updateActiveDateRangeLabel(): void {
//     if (this.selectedFYOption === 'ALL') {
//       this.activeDateRangeLabel = 'All Time';
//       return;
//     }

//     if (this.selectedFYOption === 'CUSTOM') {
//       if (this.customFromDate && this.customToDate) {
//         this.activeDateRangeLabel = `${this.formatDisplayDate(this.customFromDate)} – ${this.formatDisplayDate(this.customToDate)}`;
//       } else if (this.customFromDate) {
//         this.activeDateRangeLabel = `From ${this.formatDisplayDate(this.customFromDate)}`;
//       } else if (this.customToDate) {
//         this.activeDateRangeLabel = `Up to ${this.formatDisplayDate(this.customToDate)}`;
//       } else {
//         this.activeDateRangeLabel = 'Custom Range';
//       }
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

//   /**
//    * Returns the date range [from, to] for the currently selected FY option.
//    * Returns null for 'ALL' (no date restriction).
//    */
//   private getActiveDateRange(): { from: Date; to: Date } | null {
//     if (this.selectedFYOption === 'ALL') {
//       return null;
//     }

//     if (this.selectedFYOption === 'CUSTOM') {
//       const from = this.customFromDate ? new Date(this.customFromDate + 'T00:00:00') : null;
//       const to   = this.customToDate   ? new Date(this.customToDate   + 'T23:59:59') : null;

//       if (!from && !to) return null;

//       return {
//         from: from ?? new Date(0),
//         to:   to   ?? new Date(8640000000000000)
//       };
//     }

//     const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
//     return fy ? { from: fy.from, to: fy.to } : null;
//   }

//   /**
//    * Filters an array of raw RFQ objects by the active date range
//    * based on the rfq.createdAt field.
//    */
//   private applyDateFilter(rfqs: RFQ[]): RFQ[] {
//     const range = this.getActiveDateRange();
//     if (!range) return rfqs;

//     return rfqs.filter((rfq: any) => {
//       if (!rfq.createdAt) return false;
//       const created = new Date(rfq.createdAt);
//       return created >= range.from && created <= range.to;
//     });
//   }

//   // ── STAT CARD COUNTS (date-filtered) ─────────────────────────

//   countDateFilteredByStatus(status: string): number {
//     return this.dateFilteredRFQs.filter((r: any) => r.status === status).length;
//   }

//   // ── EXISTING METHODS (unchanged) ─────────────────────────────

//   private getLoggedInBuyerId(): void {
//     try {
//       const buyerData = StorageService.getBuyerDetails();
//       let buyerId: number | null = null;

//       if (buyerData?.id && buyerData.id !== 'N/A') {
//         buyerId = Number(buyerData.id);
//       } else if (buyerData?.buyerId) {
//         buyerId = Number(buyerData.buyerId);
//       } else {
//         const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
//         if (storedId && storedId !== 'N/A') {
//           buyerId = Number(storedId);
//         }
//       }

//       if (!buyerId || isNaN(buyerId) || buyerId <= 0) {
//         this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
//         this.router.navigate(['/login']);
//         return;
//       }

//       this.loggedInBuyerId = buyerId;

//     } catch (error) {
//       this.messageService.showMessage('error', 'Error', 'Failed to get buyer details');
//       this.router.navigate(['/login']);
//     }
//   }

//   private loadLoggedInBuyer(): void {
//     if (!this.loggedInBuyerId) return;

//     this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
//       next: (response: any) => {
//         const buyerDetails = response?.data || response;

//         if (!buyerDetails || !buyerDetails.id) {
//           this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
//           return;
//         }

//         const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
//         const loggedInName = localStorage.getItem('fullName');
//         const loggedInPhone = localStorage.getItem('phone');

//         let city = 'N/A';
//         let state = 'N/A';

//         const userLocationId = localStorage.getItem('locationId');
//         if (buyerDetails.locations && Array.isArray(buyerDetails.locations)) {
//           let selectedLoc = null;

//           if (userLocationId) {
//             selectedLoc = buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId));
//           }

//           if (!selectedLoc && buyerDetails.locations.length > 0) {
//             selectedLoc = buyerDetails.locations[0];
//           }

//           if (selectedLoc) {
//             city = selectedLoc.city || 'N/A';
//             state = selectedLoc.state || 'N/A';
//           }
//         }

//         this.loggedInBuyer = {
//           id: buyerDetails.id,
//           companyName: buyerDetails.companyName || 'N/A',
//           companyType: buyerDetails.companyType || 'Others',
//           email: loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
//           contactPersonName: loggedInName || buyerDetails.contactPersonName || 'N/A',
//           contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
//           city: city,
//           state: state
//         };

//         console.log('%c[✅ BUYER LOADED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold; font-size: 14px;', this.loggedInBuyer);
//       },
//       error: (error: any) => {
//         console.error('%c[ERROR]', 'color: #cc0000;', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
//       }
//     });
//   }

//   getInitials(name: string): string {
//     if (!name) return 'NA';
//     const nameParts = name.trim().split(' ');
//     if (nameParts.length >= 2) {
//       return (nameParts[0][0] + nameParts[1][0]).toUpperCase();
//     }
//     return name.substring(0, 2).toUpperCase();
//   }

//   loadRFQs(): void {
//     if (!this.loggedInBuyerId) {
//       this.messageService.showMessage('error', 'Error', 'Buyer ID not found');
//       return;
//     }

//     this.isLoading = true;

//     this.rfqService.getRFQsByBuyer(this.loggedInBuyerId).subscribe({
//       next: (response: any) => {
//         this.rfqs = response.data || [];

//         const rfqIds = this.rfqs.map((r: any) => r.id);

//         if (rfqIds.length === 0) {
//           this.applyFiltersAndPagination();
//           this.isLoading = false;
//           return;
//         }

//         const historyPromises = rfqIds.map(id =>
//           this.dataService.getApprovalHistory(id).toPromise()
//         );

//         Promise.all(historyPromises).then((histories: any[]) => {
//           histories.forEach((history, index) => {
//             const rfq = this.rfqs[index] as any;

//             if (history && history.success && Array.isArray(history.data)) {
//               rfq.approvalHistory = history.data;
//             } else if (history && Array.isArray(history)) {
//               rfq.approvalHistory = history;
//             } else {
//               rfq.approvalHistory = [];
//             }
//           });

//           this.applyFiltersAndPagination();
//           this.isLoading = false;
//         }).catch(error => {
//           console.error('History error:', error);
//           this.applyFiltersAndPagination();
//           this.isLoading = false;
//         });
//       },
//       error: (error: any) => {
//         console.error('RFQ load error:', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//         this.isLoading = false;
//       }
//     });
//   }

//   private transformRFQDataForDisplay(rfqs: RFQ[]): any[] {
//     return rfqs.map((rfq: any) => {
//       const status = rfq.status || 'DRAFT';
//       const priority = rfq.priority || 'MEDIUM';

//       let holdDetails = null;
//       if (rfq.approvalHistory && Array.isArray(rfq.approvalHistory)) {
//         const holdApprovals = rfq.approvalHistory.filter((a: any) => a.status === 'HOLD');

//         if (holdApprovals.length > 0) {
//           const mostRecent = holdApprovals[holdApprovals.length - 1];
//           holdDetails = {
//             heldByUserName: mostRecent.heldByUserName || mostRecent.approverUserName || 'Unknown',
//             holdRemarks: mostRecent.holdRemarks || mostRecent.comments || 'No remarks',
//             holdDate: mostRecent.holdDate || mostRecent.actionDate || null,
//             hierarchyLevelName: mostRecent.hierarchyLevelName || 'Unknown'
//           };
//         }
//       }

//       let rejectDetails = null;
//       if (rfq.approvalHistory && Array.isArray(rfq.approvalHistory)) {
//         const rejectApprovals = rfq.approvalHistory.filter((a: any) => a.status === 'REJECTED');

//         if (rejectApprovals.length > 0) {
//           const mostRecent = rejectApprovals[rejectApprovals.length - 1];

//           let remarks: string;
//           if (mostRecent.rejectRemarks?.trim()) {
//             remarks = mostRecent.rejectRemarks.trim();
//           } else if (mostRecent.comments?.trim()) {
//             remarks = mostRecent.comments.trim();
//           } else if (rfq.approvalComments?.trim()) {
//             remarks = rfq.approvalComments.trim();
//           } else {
//             remarks = 'No remarks provided';
//           }

//           let userName: string;
//           if (mostRecent.rejectedByUserName?.trim()) {
//             userName = mostRecent.rejectedByUserName.trim();
//           } else if (mostRecent.approverUserName?.trim()) {
//             userName = mostRecent.approverUserName.trim();
//           } else {
//             userName = 'Unknown';
//           }

//           rejectDetails = {
//             rejectedByUserName: userName,
//             rejectRemarks: remarks,
//             rejectDate: mostRecent.rejectDate || mostRecent.actionDate || null,
//             hierarchyLevelName: mostRecent.hierarchyLevelName || 'Unknown'
//           };
//         }
//       }

//       let returnDetails = null;
//       if (rfq.approvalHistory && Array.isArray(rfq.approvalHistory)) {
//         const returnApprovals = rfq.approvalHistory.filter((a: any) => a.status === 'RESUBMITTED');

//         if (returnApprovals.length > 0) {
//           const mostRecent = returnApprovals[returnApprovals.length - 1];

//           let remarks: string;
//           if (mostRecent.comments?.trim()) {
//             remarks = mostRecent.comments.trim();
//           } else if (rfq.approvalComments?.trim()) {
//             remarks = rfq.approvalComments.trim();
//           } else {
//             remarks = 'Revision needed';
//           }

//           let userName: string;
//           let levelName: string;

//           if (mostRecent.approverUserName?.trim()) {
//             userName = mostRecent.approverUserName.trim();
//             levelName = mostRecent.hierarchyLevelName || 'Unknown';
//           } else {
//             const previousApprovals = rfq.approvalHistory.filter((a: any, idx: number) => {
//               const currentIdx = rfq.approvalHistory.indexOf(mostRecent);
//               return idx < currentIdx && (a.status === 'PENDING' || a.status === 'APPROVED');
//             });

//             if (previousApprovals.length > 0) {
//               const lastPrevious = previousApprovals[previousApprovals.length - 1];
//               userName = lastPrevious.approverUserName || 'Unknown';
//               levelName = lastPrevious.hierarchyLevelName || 'Unknown';
//             } else {
//               userName = 'Unknown';
//               levelName = 'Unknown';
//             }
//           }

//           returnDetails = {
//             returnedByUserName: userName,
//             returnRemarks: remarks,
//             returnDate: mostRecent.actionDate || null,
//             hierarchyLevelName: levelName
//           };

//         } else if (status === 'RETURNED_FOR_REVISION') {
//           returnDetails = {
//             returnedByUserName: 'Unknown',
//             returnRemarks: rfq.approvalComments?.trim() || 'Revision needed',
//             returnDate: rfq.updatedAt || null,
//             hierarchyLevelName: 'Unknown'
//           };
//         }
//       }

//       return {
//         id: rfq.id,
//         rfqNumber: rfq.rfqNumber || 'N/A',
//         rfqTitle: rfq.rfqTitle || 'N/A',
//         status: status,
//         priority: priority,
//         buyerName: rfq.buyer?.companyName || 'N/A',
//         locationName: rfq.location?.locationName || 'N/A',
//         itemsCount: rfq.items?.length || 0,
//         suppliersCount: rfq.selectedSuppliers?.length || 0,
//         dueDate: rfq.dueDate ? new Date(rfq.dueDate).toLocaleDateString('en-GB') : 'N/A',
//         createdAt: rfq.createdAt ? new Date(rfq.createdAt).toLocaleDateString('en-GB') : 'N/A',
//         approvalStatus: rfq.approvalStatus || 'PENDING',
//         approvalComments: rfq.approvalComments || null,
//         holdDetails: holdDetails,
//         rejectDetails: rejectDetails,
//         returnDetails: returnDetails,
//         original: rfq
//       };
//     });
//   }

//   // ── MAIN FILTER METHOD ────────────────────────────────────────
//   /**
//    * Two-stage filtering:
//    *   Stage 1 → Date filter  → feeds stat cards (dateFilteredRFQs)
//    *   Stage 2 → Search / status / priority → feeds table (filteredRFQs)
//    */
//   applyFiltersAndPagination(): void {
//     this.updateActiveDateRangeLabel();

//     // Stage 1: apply date filter to raw rfqs → stat cards source
//     const dateFiltered = this.applyDateFilter(this.rfqs);
//     this.dateFilteredRFQs = this.transformRFQDataForDisplay(dateFiltered);

//     // Stage 2: apply search + status + priority on top of date-filtered data
//     let data = [...this.dateFilteredRFQs];

//     if (this.searchText.trim()) {
//       const term = this.searchText.toLowerCase();
//       data = data.filter((row: any) =>
//         (row.rfqNumber || '').toLowerCase().includes(term) ||
//         (row.rfqTitle || '').toLowerCase().includes(term) ||
//         (row.buyerName || '').toLowerCase().includes(term) ||
//         (row.locationName || '').toLowerCase().includes(term)
//       );
//     }

//     if (this.statusFilter && this.statusFilter.trim() !== '') {
//       data = data.filter((row: any) => row.status === this.statusFilter);
//     }

//     if (this.priorityFilter && this.priorityFilter.trim() !== '') {
//       data = data.filter((row: any) => row.priority === this.priorityFilter);
//     }

//     this.filteredRFQs = data;
//     this.currentPage = 1;
//     this.updatePagination();
//   }

//   updatePagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
//   }

//   viewRFQ(rfq: any): void {
//     this.router.navigate([`/create-rfq/${rfq.id}/view`]);
//   }

//   editRFQ(rfq: any): void {
//     if (rfq.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited');
//       return;
//     }
//     this.router.navigate([`/create-rfq/${rfq.id}/edit`]);
//   }

//   deleteRFQ(rfq: any): void {
//     if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
//       this.messageService.showMessage('error', 'Error', 'You can only delete your own RFQs');
//       return;
//     }

//     if (!confirm(`Delete RFQ ${rfq.rfqNumber}?`)) return;

//     this.rfqService.deleteRFQ(rfq.id).subscribe({
//       next: () => {
//         this.loadRFQs();
//         this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
//       },
//       error: () => {
//         this.messageService.showMessage('error', 'Error', 'Failed to delete RFQ');
//       }
//     });
//   }

//   publishRFQ(rfq: any): void {
//     if (rfq.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be published');
//       return;
//     }

//     if (!confirm(`Submit RFQ ${rfq.rfqNumber} for approval?`)) return;

//     const createdByUserId = rfq.original.createdByUser?.id || Number(localStorage.getItem('userId'));

//     this.dataService.initiateApprovalWorkflow(rfq.id, createdByUserId).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'RFQ submitted for approval');
//         this.loadRFQs();
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to submit');
//       }
//     });
//   }

//   canResubmit(rfq: any): boolean {
//     return rfq.status === 'RETURNED_FOR_REVISION' &&
//            rfq.original?.buyer?.id === this.loggedInBuyerId;
//   }

//   resubmitRFQ(rfq: any): void {
//     if (!this.canResubmit(rfq)) return;

//     if (!confirm(`Resubmit RFQ ${rfq.rfqNumber}?`)) return;

//     const resubmitterId = Number(localStorage.getItem('userId'));

//     this.dataService.resubmitRFQDynamic(rfq.id, resubmitterId).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'RFQ resubmitted');
//         this.loadRFQs();
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to resubmit');
//       }
//     });
//   }

//   closeRFQ(rfq: any): void {
//     if (!confirm(`Close RFQ ${rfq.rfqNumber}?`)) return;

//     this.rfqService.closeRFQ(rfq.id).subscribe({
//       next: () => {
//         this.loadRFQs();
//         this.messageService.showMessage('success', 'Success', 'RFQ closed');
//       },
//       error: () => {
//         this.messageService.showMessage('error', 'Error', 'Failed to close RFQ');
//       }
//     });
//   }

//   compareQuotes(rfq: any): void {
//     console.log('📊 Navigating to quote comparison for RFQ:', rfq.id);
//     this.router.navigate(['/quote-comparison', rfq.id]);
//   }

//   canCompareQuotes(rfq: any): boolean {
//     return (rfq.status === 'PUBLISHED' || rfq.status === 'RESPONSES_RECEIVED') &&
//            rfq.suppliersCount > 0;
//   }

//   formatDateTime(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try {
//       return new Date(dateString).toLocaleString('en-GB', {
//         year: 'numeric',
//         month: 'short',
//         day: 'numeric',
//         hour: '2-digit',
//         minute: '2-digit'
//       });
//     } catch {
//       return 'Invalid Date';
//     }
//   }

//   get totalPages(): number {
//     return Math.ceil(this.filteredRFQs.length / this.pageSize);
//   }

//   onPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.updatePagination();
//     }
//   }

//   onPageSizeChange(size: number): void {
//     this.pageSize = size;
//     this.currentPage = 1;
//     this.updatePagination();
//   }

//   getStatusClass(status: string): string {
//     const map: { [key: string]: string } = {
//       'DRAFT': 'bg-secondary',
//       'AWAITING_APPROVAL': 'bg-warning',
//       'PUBLISHED': 'bg-info',
//       'RESPONSES_RECEIVED': 'bg-success',
//       'CLOSED': 'bg-danger',
//       'CANCELLED': 'bg-dark',
//       'RETURNED_FOR_REVISION': 'bg-warning',
//       'HOLD': 'bg-warning'
//     };
//     return map[status] || 'bg-light';
//   }

//   getPriorityClass(priority: string): string {
//     const map: { [key: string]: string } = {
//       'LOW': 'bg-light text-dark',
//       'MEDIUM': 'bg-info text-white',
//       'HIGH': 'bg-warning text-dark',
//       'URGENT': 'bg-danger text-white'
//     };
//     return map[priority] || 'bg-light text-dark';
//   }

//   getStatusLabel(status: string): string {
//     const map: { [key: string]: string } = {
//       'DRAFT': 'Draft',
//       'AWAITING_APPROVAL': 'Awaiting Approval',
//       'PUBLISHED': 'Published',
//       'RESPONSES_RECEIVED': 'Responses Received',
//       'CLOSED': 'Closed',
//       'CANCELLED': 'Cancelled',
//       'RETURNED_FOR_REVISION': 'Returned for Revision',
//       'HOLD': 'On HOLD'
//     };
//     return map[status] || status.replace(/_/g, ' ');
//   }

//   canEdit(rfq: any): boolean {
//     return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
//   }

//   canPublish(rfq: any): boolean {
//     return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
//   }

//   canClose(rfq: any): boolean {
//     return ['PUBLISHED', 'RESPONSES_RECEIVED'].includes(rfq.status) &&
//            rfq.original.buyer?.id === this.loggedInBuyerId;
//   }

//   resetFilters(): void {
//     this.searchText = '';
//     this.statusFilter = '';
//     this.priorityFilter = '';
//     this.currentPage = 1;
//     this.applyFiltersAndPagination();
//   }

//   applyCardFilter(status: string): void {
//     this.activeStatusFilter = status;
//     this.statusFilter = status === 'ALL' ? '' : status;
//     this.applyFiltersAndPagination();
//   }

//   // Kept for backward compatibility (used in old template if any)
//   countRFQByStatus(status: string): number {
//     return this.rfqs.filter((r: any) => r.status === status).length;
//   }

//   get pageNumbers(): number[] {
//     const total = this.totalPages;
//     const current = this.currentPage;
//     const pages: number[] = [];
//     if (total <= 7) {
//       for (let i = 1; i <= total; i++) pages.push(i);
//       return pages;
//     }
//     pages.push(1);
//     if (current > 3) pages.push(-1);
//     const start = Math.max(2, current - 1);
//     const end = Math.min(total - 1, current + 1);
//     for (let i = start; i <= end; i++) pages.push(i);
//     if (current < total - 2) pages.push(-1);
//     pages.push(total);
//     return pages;
//   }

//   get startIndex(): number {
//     if (this.filteredRFQs.length === 0) return 0;
//     return (this.currentPage - 1) * this.pageSize + 1;
//   }

//   get endIndex(): number {
//     return Math.min(this.currentPage * this.pageSize, this.filteredRFQs.length);
//   }

//   goToPage(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.updatePagination();
//     }
//   }
// }

import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RFQService } from '../models/rfq.service';
import { MessageService } from '../../../shared/service/message.service';
import { RFQ } from '../models/rfq.model';
import { BuyerService } from '../dashboard/buyer-b.service';
import { StorageService } from '../../../shared/service/StorageService';
import { RfqApprovalService } from '../models/rfq-approval.service';
import { DataService } from '../../../shared/service/DataService';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

interface FYOption {
  value: string;
  label: string;
  from: Date;
  to: Date;
}

@Component({
  selector: 'app-rfq-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rfq-dashboard.component.html',
  styleUrls: ['./rfq-dashboard.component.css']
})
export class RFQDashboardComponent implements OnInit {

  rfqs: RFQ[] = [];
  dateFilteredRFQs: any[] = [];
  filteredRFQs: any[]     = [];
  pagedRFQs: any[]        = [];

  loggedInBuyerId: number | null = null;
  loggedInBuyer: any = null;

  searchText     = '';
  currentPage    = 1;
  pageSize       = 5;
  statusFilter   = '';
  priorityFilter = '';

  isLoading = false;
  Math = Math;

  // ── Report downloads ──────────────────────────────────────────
  isDownloadingListExcel = false;
  downloadingRfqId: number | null = null;
  downloadingRfqType: 'excel' | 'pdf' | null = null;
  showListDownloadMenu   = false;
  openDropdownRfqId: number | null = null;

  statuses   = ['DRAFT','AWAITING_APPROVAL','PUBLISHED','RESPONSES_RECEIVED','CLOSED','CANCELLED','HOLD','RETURNED_FOR_REVISION'];
  priorities = ['LOW','MEDIUM','HIGH','URGENT'];

  activeStatusFilter = 'ALL';
  pageSizeOptions    = [5, 10, 25, 50];

  // ── Date filter ───────────────────────────────────────────────
  financialYearOptions: FYOption[] = [];
  selectedFYOption   = '';
  customFromDate     = '';
  customToDate       = '';
  activeDateRangeLabel = '';

  // =========================================================================
  //  FULL PROCESS MODAL STATE
  // =========================================================================
  showProcessModal   = false;
  processLoading     = false;
  selectedRfqProcess: any = null;
  processData: {
    rfq:      any;
    po:       any | null;
    grn:      any | null;
    invoice:  any | null;
    match:    any | null;
  } = { rfq: null, po: null, grn: null, invoice: null, match: null };

  // Download buttons inside modal
  downloadingProcessReport = false;

  constructor(
    private rfqService:      RFQService,
    private buyerService:    BuyerService,
    private messageService:  MessageService,
    private dataService:     DataService,
    public  router:          Router,
    private approvalService: RfqApprovalService,
  ) {}

  @HostListener('document:click')
  onDocumentClick(): void { this.closeAllDropdowns(); }

  ngOnInit(): void {
    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();
    this.getLoggedInBuyerId();
    this.loadLoggedInBuyer();
    this.loadRFQs();
  }

  // =========================================================================
  //  FULL PROCESS MODAL — view RFQ → PO → GRN → Invoice → 3-Way Match
  // =========================================================================

  openProcessModal(rfq: any): void {
    this.selectedRfqProcess = rfq;
    this.showProcessModal   = true;
    this.processLoading     = true;
    this.processData        = { rfq: rfq.original || rfq, po: null, grn: null, invoice: null, match: null };

    const rfqId = rfq.id;

    // Step 1: Load POs for this RFQ
    this.dataService.getPOsByRFQ(rfqId).pipe(catchError(() => of({ data: [] }))).subscribe((poRes: any) => {
      const pos = poRes?.data || poRes || [];
      const po  = Array.isArray(pos) ? (pos[0] || null) : null;
      this.processData.po = po;

      if (!po) { this.processLoading = false; return; }

      const poId = po.id;

      // Step 2: Load invoices + GRNs + match in parallel
      forkJoin({
        invoices: this.dataService.getInvoicesByPO(poId).pipe(catchError(() => of({ data: [] }))),
        grns:     this.dataService.getAllGRNsByPO(poId).pipe(catchError(() => of({ data: [] }))),
      }).subscribe(({ invoices, grns }: any) => {

        const invList  = invoices?.data || invoices || [];
        const grnList  = grns?.data || grns || [];
        const invoice  = Array.isArray(invList) ? (invList[0] || null) : null;
        const grn      = Array.isArray(grnList) ? (grnList[0] || null) : null;

        this.processData.invoice = invoice;
        this.processData.grn     = grn;

        // Step 3: Load 3-Way Match if invoice exists
        if (invoice?.id) {
          this.dataService.getMatchesByInvoice(invoice.id).pipe(catchError(() => of({ data: [] }))).subscribe((matchRes: any) => {
            const matches = matchRes?.data || matchRes || [];
            this.processData.match = Array.isArray(matches) && matches.length > 0 ? matches[0] : null;
            this.processLoading    = false;
          });
        } else {
          this.processLoading = false;
        }
      });
    });
  }

  closeProcessModal(): void {
    this.showProcessModal   = false;
    this.selectedRfqProcess = null;
    this.processData        = { rfq: null, po: null, grn: null, invoice: null, match: null };
  }

  // Download from process modal
  downloadProcessExcel(type: 'rfq' | 'po' | 'grn' | 'match' | 'invoice', id: number): void {
    this.downloadingProcessReport = true;
    let obs$: any;
    let filename: string;
    const d = this.ts();

    switch (type) {
      case 'rfq':     obs$ = this.dataService.getRFQSummaryExcel(id);      filename = 'RFQ_Summary_' + id + '_' + d + '.xlsx';           break;
      case 'po':      obs$ = this.dataService.getPOSummaryExcel(id);       filename = 'PO_Summary_' + id + '_' + d + '.xlsx';            break;
      case 'grn':     obs$ = this.dataService.getGRNReportExcel(id);       filename = 'GRN_Report_' + id + '_' + d + '.xlsx';            break;
      case 'invoice': obs$ = this.dataService.getInvoiceExcel(id);         filename = 'Invoice_' + id + '_' + d + '.xlsx';               break;
      case 'match':   obs$ = this.dataService.getThreeWayMatchExcel(id);   filename = 'ThreeWayMatch_' + id + '_' + d + '.xlsx';         break;
      default: this.downloadingProcessReport = false; return;
    }

    obs$.subscribe({
      next: (blob: Blob) => { this.dataService.saveBlob(blob, filename); this.downloadingProcessReport = false; },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to download report'); this.downloadingProcessReport = false; }
    });
  }

  getMatchStatusClass(status: string): string {
    const m: any = { MATCHED: 'success', PARTIAL_MATCH: 'info', QUANTITY_MISMATCH: 'warning',
                     PRICE_MISMATCH: 'warning', ITEM_MISMATCH: 'danger', EXCESS_DELIVERY: 'secondary',
                     OVERRIDDEN_APPROVED: 'success', DISPUTED: 'danger', FAILED: 'dark' };
    return m[status] || 'secondary';
  }

  getGRNStatusClass(status: string): string {
    const m: any = { DRAFT: 'secondary', SUBMITTED: 'primary', QA_REVIEW: 'warning',
                     APPROVED: 'success', CLOSED: 'info', CANCELLED: 'danger' };
    return m[status] || 'secondary';
  }

  formatCurrency(amount: any, code: string = 'INR'): string {
    const val = Number(amount ?? 0);
    const sym: any = { INR: '₹', USD: '$', EUR: '€', GBP: '£', AED: 'د.إ' };
    return (sym[code] || code) + ' ' + val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  // =========================================================================
  //  REPORT DOWNLOAD METHODS
  // =========================================================================

  downloadRFQListExcel(): void {
    if (!this.loggedInBuyerId) { this.messageService.showMessage('warning', 'Warning', 'Buyer ID not found'); return; }
    this.isDownloadingListExcel = true;
    const status = this.statusFilter || 'ALL';
    this.dataService.getRFQListExcel(this.loggedInBuyerId, status).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, 'RFQ_List_' + (status === 'ALL' ? 'All' : status) + '_' + this.ts() + '.xlsx');
        this.isDownloadingListExcel = false;
        this.messageService.showMessage('success', 'Downloaded', 'RFQ list report downloaded');
      },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to download'); this.isDownloadingListExcel = false; }
    });
  }

  downloadRFQExcel(rfq: any): void {
    this.downloadingRfqId   = rfq.id;
    this.downloadingRfqType = 'excel';
    this.dataService.getRFQSummaryExcel(rfq.id).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, 'RFQ_Summary_' + rfq.rfqNumber + '_' + this.ts() + '.xlsx');
        this.clearDownloadState();
        this.messageService.showMessage('success', 'Downloaded', rfq.rfqNumber + ' Excel downloaded');
      },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed'); this.clearDownloadState(); }
    });
  }

  downloadRFQPDF(rfq: any): void {
    this.downloadingRfqId   = rfq.id;
    this.downloadingRfqType = 'pdf';
    this.dataService.getRFQSummaryPDF(rfq.id).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, 'RFQ_Summary_' + rfq.rfqNumber + '_' + this.ts() + '.pdf');
        this.clearDownloadState();
        this.messageService.showMessage('success', 'Downloaded', rfq.rfqNumber + ' PDF downloaded');
      },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed'); this.clearDownloadState(); }
    });
  }

  isRowDownloading(rfqId: number): boolean { return this.downloadingRfqId === rfqId; }
  private clearDownloadState(): void { this.downloadingRfqId = null; this.downloadingRfqType = null; }

  toggleDownloadDropdown(rfqId: number, event: Event): void {
    event.stopPropagation();
    this.showListDownloadMenu = false;
    this.openDropdownRfqId = this.openDropdownRfqId === rfqId ? null : rfqId;
  }

  closeAllDropdowns(): void { this.openDropdownRfqId = null; this.showListDownloadMenu = false; }

  private ts(): string { return new Date().toISOString().slice(0, 10); }

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
        value: 'FY' + startYear + '-' + String(endYear).slice(-2),
        label: 'FY ' + startYear + '-' + String(endYear).slice(-2) + '  (Apr ' + startYear + ' – Mar ' + endYear + ')',
        from, to
      });
    }
  }

  getCurrentFYValue(): string { return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL'; }

  onFYOptionChange(): void {
    if (this.selectedFYOption !== 'CUSTOM') { this.customFromDate = ''; this.customToDate = ''; }
    this.updateActiveDateRangeLabel();
    this.applyFiltersAndPagination();
  }

  resetDateFilter(): void {
    this.selectedFYOption = this.getCurrentFYValue();
    this.customFromDate = ''; this.customToDate = '';
    this.updateActiveDateRangeLabel();
    this.applyFiltersAndPagination();
  }

  private updateActiveDateRangeLabel(): void {
    if (this.selectedFYOption === 'ALL') { this.activeDateRangeLabel = 'All Time'; return; }
    if (this.selectedFYOption === 'CUSTOM') {
      if (this.customFromDate && this.customToDate)
        this.activeDateRangeLabel = this.formatDisplayDate(this.customFromDate) + ' – ' + this.formatDisplayDate(this.customToDate);
      else if (this.customFromDate) this.activeDateRangeLabel = 'From ' + this.formatDisplayDate(this.customFromDate);
      else if (this.customToDate)   this.activeDateRangeLabel = 'Up to ' + this.formatDisplayDate(this.customToDate);
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

  private applyDateFilter(rfqs: RFQ[]): RFQ[] {
    const range = this.getActiveDateRange();
    if (!range) return rfqs;
    return rfqs.filter((rfq: any) => {
      if (!rfq.createdAt) return false;
      const created = new Date(rfq.createdAt);
      return created >= range.from && created <= range.to;
    });
  }

  countDateFilteredByStatus(status: string): number {
    return this.dateFilteredRFQs.filter((r: any) => r.status === status).length;
  }

  // =========================================================================
  //  BUYER LOADING
  // =========================================================================

  private getLoggedInBuyerId(): void {
    try {
      const buyerData = StorageService.getBuyerDetails();
      let buyerId: number | null = null;
      if (buyerData?.id && buyerData.id !== 'N/A') buyerId = Number(buyerData.id);
      else if (buyerData?.buyerId) buyerId = Number(buyerData.buyerId);
      else {
        const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
        if (storedId && storedId !== 'N/A') buyerId = Number(storedId);
      }
      if (!buyerId || isNaN(buyerId) || buyerId <= 0) {
        this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
        this.router.navigate(['/login']); return;
      }
      this.loggedInBuyerId = buyerId;
    } catch { this.messageService.showMessage('error', 'Error', 'Failed to get buyer details'); this.router.navigate(['/login']); }
  }

  private loadLoggedInBuyer(): void {
    if (!this.loggedInBuyerId) return;
    this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
      next: (response: any) => {
        const b = response?.data || response;
        if (!b || !b.id) return;
        const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
        const loggedInName  = localStorage.getItem('fullName');
        const loggedInPhone = localStorage.getItem('phone');
        let city = 'N/A', state = 'N/A';
        const userLocationId = localStorage.getItem('locationId');
        if (b.locations && Array.isArray(b.locations)) {
          let loc = userLocationId ? b.locations.find((l: any) => l.id === Number(userLocationId)) : null;
          if (!loc && b.locations.length > 0) loc = b.locations[0];
          if (loc) { city = loc.city || 'N/A'; state = loc.state || 'N/A'; }
        }
        this.loggedInBuyer = {
          id: b.id, companyName: b.companyName || 'N/A', companyType: b.companyType || 'Others',
          email: loggedInEmail || b.contactPersonEmail || 'N/A',
          contactPersonName: loggedInName || b.contactPersonName || 'N/A',
          contactPersonPhone: loggedInPhone || b.contactPersonPhone || 'N/A',
          city, state
        };
      },
      error: () => {}
    });
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const p = name.trim().split(' ');
    return p.length >= 2 ? (p[0][0] + p[1][0]).toUpperCase() : name.substring(0, 2).toUpperCase();
  }

  // =========================================================================
  //  RFQ LOADING
  // =========================================================================

  loadRFQs(): void {
    if (!this.loggedInBuyerId) { this.messageService.showMessage('error', 'Error', 'Buyer ID not found'); return; }
    this.isLoading = true;
    this.rfqService.getRFQsByBuyer(this.loggedInBuyerId).subscribe({
      next: (response: any) => {
        this.rfqs = response.data || [];
        const rfqIds = this.rfqs.map((r: any) => r.id);
        if (rfqIds.length === 0) { this.applyFiltersAndPagination(); this.isLoading = false; return; }
        const historyPromises = rfqIds.map(id => this.dataService.getApprovalHistory(id).toPromise());
        Promise.all(historyPromises).then((histories: any[]) => {
          histories.forEach((history, index) => {
            const rfq = this.rfqs[index] as any;
            if (history?.success && Array.isArray(history.data)) rfq.approvalHistory = history.data;
            else if (Array.isArray(history)) rfq.approvalHistory = history;
            else rfq.approvalHistory = [];
          });
          this.applyFiltersAndPagination();
          this.isLoading = false;
        }).catch(() => { this.applyFiltersAndPagination(); this.isLoading = false; });
      },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to load RFQs'); this.isLoading = false; }
    });
  }

  private transformRFQDataForDisplay(rfqs: RFQ[]): any[] {
    return rfqs.map((rfq: any) => {
      const status = rfq.status || 'DRAFT', priority = rfq.priority || 'MEDIUM';
      let holdDetails = null, rejectDetails = null, returnDetails = null;

      if (Array.isArray(rfq.approvalHistory)) {
        const holds = rfq.approvalHistory.filter((a: any) => a.status === 'HOLD');
        if (holds.length > 0) {
          const h = holds[holds.length - 1];
          holdDetails = { heldByUserName: h.heldByUserName || h.approverUserName || 'Unknown', holdRemarks: h.holdRemarks || h.comments || 'No remarks', holdDate: h.holdDate || h.actionDate || null, hierarchyLevelName: h.hierarchyLevelName || 'Unknown' };
        }
        const rejects = rfq.approvalHistory.filter((a: any) => a.status === 'REJECTED');
        if (rejects.length > 0) {
          const r = rejects[rejects.length - 1];
          rejectDetails = { rejectedByUserName: (r.rejectedByUserName || r.approverUserName || 'Unknown').trim(), rejectRemarks: (r.rejectRemarks || r.comments || rfq.approvalComments || 'No remarks').trim(), rejectDate: r.rejectDate || r.actionDate || null, hierarchyLevelName: r.hierarchyLevelName || 'Unknown' };
        }
        const returns = rfq.approvalHistory.filter((a: any) => a.status === 'RESUBMITTED');
        if (returns.length > 0) {
          const r = returns[returns.length - 1];
          returnDetails = { returnedByUserName: r.approverUserName?.trim() || 'Unknown', returnRemarks: r.comments?.trim() || rfq.approvalComments?.trim() || 'Revision needed', returnDate: r.actionDate || null, hierarchyLevelName: r.hierarchyLevelName || 'Unknown' };
        } else if (status === 'RETURNED_FOR_REVISION') {
          returnDetails = { returnedByUserName: 'Unknown', returnRemarks: rfq.approvalComments?.trim() || 'Revision needed', returnDate: rfq.updatedAt || null, hierarchyLevelName: 'Unknown' };
        }
      }

      return {
        id: rfq.id, rfqNumber: rfq.rfqNumber || 'N/A', rfqTitle: rfq.rfqTitle || 'N/A',
        status, priority, buyerName: rfq.buyer?.companyName || 'N/A',
        locationName: rfq.location?.locationName || 'N/A',
        itemsCount: rfq.items?.length || 0, suppliersCount: rfq.selectedSuppliers?.length || 0,
        dueDate:   rfq.dueDate   ? new Date(rfq.dueDate).toLocaleDateString('en-GB')   : 'N/A',
        createdAt: rfq.createdAt ? new Date(rfq.createdAt).toLocaleDateString('en-GB') : 'N/A',
        approvalStatus: rfq.approvalStatus || 'PENDING', approvalComments: rfq.approvalComments || null,
        holdDetails, rejectDetails, returnDetails, original: rfq
      };
    });
  }

  // =========================================================================
  //  FILTERING & PAGINATION
  // =========================================================================

  applyFiltersAndPagination(): void {
    this.updateActiveDateRangeLabel();
    const dateFiltered = this.applyDateFilter(this.rfqs);
    this.dateFilteredRFQs = this.transformRFQDataForDisplay(dateFiltered);

    let data = [...this.dateFilteredRFQs];
    if (this.searchText.trim()) {
      const term = this.searchText.toLowerCase();
      data = data.filter((row: any) =>
        (row.rfqNumber || '').toLowerCase().includes(term) ||
        (row.rfqTitle  || '').toLowerCase().includes(term) ||
        (row.buyerName || '').toLowerCase().includes(term) ||
        (row.locationName || '').toLowerCase().includes(term)
      );
    }
    if (this.statusFilter?.trim())   data = data.filter((r: any) => r.status   === this.statusFilter);
    if (this.priorityFilter?.trim()) data = data.filter((r: any) => r.priority === this.priorityFilter);

    this.filteredRFQs = data;
    this.currentPage  = 1;
    this.updatePagination();
  }

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
  }

  // =========================================================================
  //  RFQ ACTIONS
  // =========================================================================

  viewRFQ(rfq: any): void    { this.router.navigate(['/create-rfq/' + rfq.id + '/view']); }
  editRFQ(rfq: any): void    {
    if (rfq.status !== 'DRAFT') { this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited'); return; }
    this.router.navigate(['/create-rfq/' + rfq.id + '/edit']);
  }
  deleteRFQ(rfq: any): void  {
    if (rfq.original.buyer?.id !== this.loggedInBuyerId) { this.messageService.showMessage('error', 'Error', 'You can only delete your own RFQs'); return; }
    if (!confirm('Delete RFQ ' + rfq.rfqNumber + '?')) return;
    this.rfqService.deleteRFQ(rfq.id).subscribe({
      next: () => { this.loadRFQs(); this.messageService.showMessage('success', 'Success', 'RFQ deleted'); },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to delete RFQ'); }
    });
  }
  publishRFQ(rfq: any): void {
    if (rfq.status !== 'DRAFT') { this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be published'); return; }
    if (!confirm('Submit RFQ ' + rfq.rfqNumber + ' for approval?')) return;
    const uid = rfq.original.createdByUser?.id || Number(localStorage.getItem('userId'));
    this.dataService.initiateApprovalWorkflow(rfq.id, uid).subscribe({
      next: () => { this.messageService.showMessage('success', 'Success', 'RFQ submitted for approval'); this.loadRFQs(); },
      error: (err: any) => { this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to submit'); }
    });
  }
  canResubmit(rfq: any): boolean { return rfq.status === 'RETURNED_FOR_REVISION' && rfq.original?.buyer?.id === this.loggedInBuyerId; }
  resubmitRFQ(rfq: any): void {
    if (!this.canResubmit(rfq)) return;
    if (!confirm('Resubmit RFQ ' + rfq.rfqNumber + '?')) return;
    this.dataService.resubmitRFQDynamic(rfq.id, Number(localStorage.getItem('userId'))).subscribe({
      next: () => { this.messageService.showMessage('success', 'Success', 'RFQ resubmitted'); this.loadRFQs(); },
      error: (err: any) => { this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to resubmit'); }
    });
  }
  closeRFQ(rfq: any): void {
    if (!confirm('Close RFQ ' + rfq.rfqNumber + '?')) return;
    this.rfqService.closeRFQ(rfq.id).subscribe({
      next: () => { this.loadRFQs(); this.messageService.showMessage('success', 'Success', 'RFQ closed'); },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to close RFQ'); }
    });
  }
  compareQuotes(rfq: any): void { this.router.navigate(['/quote-comparison', rfq.id]); }
  canCompareQuotes(rfq: any): boolean { return (rfq.status === 'PUBLISHED' || rfq.status === 'RESPONSES_RECEIVED') && rfq.suppliersCount > 0; }

  // =========================================================================
  //  HELPERS
  // =========================================================================

  formatDateTime(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try { return new Date(dateString).toLocaleString('en-GB', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }); }
    catch { return 'Invalid Date'; }
  }

  get totalPages(): number { return Math.ceil(this.filteredRFQs.length / this.pageSize); }
  onPageChange(page: number): void { if (page >= 1 && page <= this.totalPages) { this.currentPage = page; this.updatePagination(); } }
  onPageSizeChange(size: number): void { this.pageSize = size; this.currentPage = 1; this.updatePagination(); }

  getStatusClass(status: string): string {
    const m: any = { DRAFT: 'bg-secondary', AWAITING_APPROVAL: 'bg-warning', PUBLISHED: 'bg-info',
                     RESPONSES_RECEIVED: 'bg-success', CLOSED: 'bg-danger', CANCELLED: 'bg-dark',
                     RETURNED_FOR_REVISION: 'bg-warning', HOLD: 'bg-warning' };
    return m[status] || 'bg-light';
  }
  getPriorityClass(priority: string): string {
    const m: any = { LOW: 'bg-light text-dark', MEDIUM: 'bg-info text-white', HIGH: 'bg-warning text-dark', URGENT: 'bg-danger text-white' };
    return m[priority] || 'bg-light text-dark';
  }
  getStatusLabel(status: string): string {
    const m: any = { DRAFT: 'Draft', AWAITING_APPROVAL: 'Awaiting Approval', PUBLISHED: 'Published',
                     RESPONSES_RECEIVED: 'Responses Received', CLOSED: 'Closed', CANCELLED: 'Cancelled',
                     RETURNED_FOR_REVISION: 'Returned for Revision', HOLD: 'On HOLD' };
    return m[status] || status.replace(/_/g, ' ');
  }
  canEdit(rfq: any): boolean    { return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId; }
  canPublish(rfq: any): boolean { return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId; }
  canClose(rfq: any): boolean   { return ['PUBLISHED','RESPONSES_RECEIVED'].includes(rfq.status) && rfq.original.buyer?.id === this.loggedInBuyerId; }
  resetFilters(): void          { this.searchText = ''; this.statusFilter = ''; this.priorityFilter = ''; this.currentPage = 1; this.applyFiltersAndPagination(); }
  applyCardFilter(status: string): void { this.activeStatusFilter = status; this.statusFilter = status === 'ALL' ? '' : status; this.applyFiltersAndPagination(); }
  countRFQByStatus(status: string): number { return this.rfqs.filter((r: any) => r.status === status).length; }

  get pageNumbers(): number[] {
    const total = this.totalPages, current = this.currentPage, pages: number[] = [];
    if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
    pages.push(1);
    if (current > 3) pages.push(-1);
    const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
    for (let i = start; i <= end; i++) pages.push(i);
    if (current < total - 2) pages.push(-1);
    pages.push(total);
    return pages;
  }

  get startIndex(): number { return this.filteredRFQs.length === 0 ? 0 : (this.currentPage - 1) * this.pageSize + 1; }
  get endIndex(): number   { return Math.min(this.currentPage * this.pageSize, this.filteredRFQs.length); }
  goToPage(page: number): void { if (page >= 1 && page <= this.totalPages) { this.currentPage = page; this.updatePagination(); } }
}