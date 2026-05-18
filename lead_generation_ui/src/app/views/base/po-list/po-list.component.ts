

// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router } from '@angular/router';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
// } from '@coreui/angular';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { BuyerService } from '../dashboard/buyer-b.service';

// @Component({
//   selector: 'app-po-list',
//   standalone: true,
//   imports: [
//     CommonModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent,
//     ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
//   ],
//   templateUrl: './po-list.component.html',
//   styleUrls: ['./po-list.component.css']
// })
// export class POListComponent implements OnInit {

//   purchaseOrders: any[] = [];
//   isLoading: boolean = false;
//   deletingId: number | null = null;
//   buyerId: number | null = null;
//   holdCount: number = 0;
//   loggedInBuyer: any = null;
//   approvalRemarksMap: Map<number, any> = new Map();
//   loadingRemarks: boolean = false;

//   // Buyer's local currency (from their location, e.g. INR for India)
//   currencyCode: string = 'INR';
//   currencySymbol: string = '₹';

//   // ✅ Exchange rates — loaded once, keyed by currency code (base = buyer's currency)
//   // e.g. if buyer is INR: { USD: 0.012, EUR: 0.011, ... } meaning 1 INR = 0.012 USD
//   // We actually store rates as: 1 USD = X INR, so we store { USD: 83.5, EUR: 89.2 }
//   // i.e. base currency is USD from the API, then we re-base to buyer's currency
//   exchangeRates: Record<string, number> = {};
//   exchangeRatesLoaded: boolean = false;

//   // ── Filter ───────────────────────────────────────────────────
//   statusFilter: string = 'ALL';
//   filteredOrders: any[] = [];

//   // ── Pagination ──────────────────────────────────────────────
//   currentPage: number = 1;
//   pageSize: number = 10;
//   pageSizeOptions: number[] = [5, 10, 25, 50];

//   constructor(
//     private router: Router,
//     private dataService: DataService,
//     private messageService: MessageService,
//     private buyerService: BuyerService
//   ) {}

//   ngOnInit(): void {
//     this.buyerId = Number(localStorage.getItem('buyerId'));
//     if (!this.buyerId || isNaN(this.buyerId)) {
//       this.messageService.showMessage('error', 'Error', 'Buyer ID not found. Please login again.');
//       this.router.navigate(['/login']);
//       return;
//     }
//     this.loadLoggedInBuyer();
//     this.loadPurchaseOrders();
//   }

//   // ── Buyer Info ───────────────────────────────────────────────

//   private loadLoggedInBuyer(): void {
//     if (!this.buyerId) return;
//     this.buyerService.getBuyerById(this.buyerId).subscribe({
//       next: (response: any) => {
//         const buyerDetails = response?.data || response;
//         if (!buyerDetails || !buyerDetails.id) return;

//         const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
//         const loggedInName  = localStorage.getItem('fullName');
//         const loggedInPhone = localStorage.getItem('phone');

//         let city = 'N/A', state = 'N/A';
//         const userLocationId = localStorage.getItem('locationId');
//         if (buyerDetails.locations && Array.isArray(buyerDetails.locations)) {
//           let selectedLoc = userLocationId
//             ? buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId))
//             : null;
//           if (!selectedLoc && buyerDetails.locations.length > 0) selectedLoc = buyerDetails.locations[0];
//           if (selectedLoc) { city = selectedLoc.city || 'N/A'; state = selectedLoc.state || 'N/A'; }
//         }

//         this.loggedInBuyer = {
//           companyName:        buyerDetails.companyName || 'N/A',
//           companyType:        buyerDetails.companyType || 'IT',
//           email:              loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
//           contactPersonName:  loggedInName  || buyerDetails.contactPersonName  || 'N/A',
//           contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
//           city, state
//         };
//       },
//       error: () => {}
//     });
//   }

//   getInitials(name: string): string {
//     if (!name) return 'NA';
//     const parts = name.trim().split(' ');
//     return parts.length >= 2
//       ? (parts[0][0] + parts[1][0]).toUpperCase()
//       : name.substring(0, 2).toUpperCase();
//   }
//   hasCrossBorderPOs(): boolean {
//   return this.purchaseOrders.some(po => this.isCrossBorderPO(po));
// }

//   // ── Exchange Rates ────────────────────────────────────────────
//   // Load rates with buyer's currency as base.
//   // e.g. buyer is INR → fetch base=INR → rates.USD = how many USD per 1 INR
//   // Then: to convert $4770 USD → INR: amount / rates.USD
//   // i.e. converted = amount * (1 / rates[poCurrency])
//   //
//   // Simpler: store as "1 poCurrency = X buyerCurrency"
//   // We use open.er-api.com with base=buyerCurrency → rates[poCurrency] = how many poCurrency per 1 buyerCurrency
//   // So: convertedInBuyerCurrency = poAmount / rates[poCurrency]
//   // e.g. base=INR, rates.USD=0.01199 → $4770 / 0.01199 = ₹3,97,831

//   private loadExchangeRates(): void {
//     if (!this.currencyCode || this.currencyCode === 'INR') {
//       // Use INR as base — fetch from open.er-api.com
//     }
//     const base = this.currencyCode;
//     fetch(`https://open.er-api.com/v6/latest/${base}`)
//       .then(r => r.json())
//       .then(data => {
//         if (data && data.rates) {
//           this.exchangeRates = data.rates;
//           this.exchangeRatesLoaded = true;
//           console.log(`[POList] Exchange rates loaded (base=${base}):`, data.rates);
//         }
//       })
//       .catch(err => {
//         console.warn('[POList] Exchange rate fetch failed:', err);
//         this.exchangeRatesLoaded = false;
//       });
//   }

//   // ── Currency helpers ──────────────────────────────────────────

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

//   // Format an amount in the buyer's own currency
//   formatCurrencyAmount(amount: number): string {
//     const val = Number(amount || 0);
//     const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
//     return `${this.currencySymbol} ${formatted}`;
//   }

//   // Format any amount with an explicit currency code/symbol
//   formatInCurrency(amount: number, code: string): string {
//     const sym = this.getSymbolForCode(code);
//     const val = Number(amount || 0);
//     const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     if (rtlCodes.includes(code)) return `${formatted} ${sym}`;
//     return `${sym} ${formatted}`;
//   }

//   /**
//    * ✅ Returns true if this PO's currency is different from buyer's local currency.
//    * e.g. PO is in USD but buyer is in INR → cross-border → show conversion
//    */
//   isCrossBorderPO(po: any): boolean {
//     const poCode = po.currencyCode || this.currencyCode;
//     return poCode !== this.currencyCode;
//   }

//   /**
//    * ✅ Convert po.grandTotal from PO currency (e.g. USD) → buyer's local currency (e.g. INR).
//    *
//    * Rate source: open.er-api.com with base = buyer's currency (e.g. INR)
//    * Response: { rates: { USD: 0.01199, EUR: 0.01089, ... } }
//    * Meaning: 1 INR = 0.01199 USD
//    * So: USD amount → INR = usdAmount / rates.USD
//    */
//   convertToLocalCurrency(amount: number, fromCurrencyCode: string): number {
//     if (!this.exchangeRatesLoaded || !this.exchangeRates) return 0;
//     if (fromCurrencyCode === this.currencyCode) return amount;
//     const rate = this.exchangeRates[fromCurrencyCode];
//     if (!rate || rate === 0) return 0;
//     // rate = how many fromCurrency per 1 buyerCurrency
//     // so buyerCurrency = fromCurrencyAmount / rate
//     return amount / rate;
//   }

//   /**
//    * ✅ Returns the formatted converted amount string for display.
//    * e.g. "≈ ₹ 3,97,832.50"
//    */
//   getConvertedDisplay(po: any): string {
//     if (!this.isCrossBorderPO(po)) return '';
//     if (!this.exchangeRatesLoaded) return 'Loading...';
//     const converted = this.convertToLocalCurrency(po.grandTotal, po.currencyCode || 'USD');
//     if (converted === 0) return '';
//     return `≈ ${this.formatCurrencyAmount(converted)}`;
//   }

//   /**
//    * ✅ For the Total Value stat card — convert all POs to buyer currency for summing
//    */
//   getTotalValueInLocalCurrency(): number {
//     return this.purchaseOrders.reduce((sum, po) => {
//       const code = po.currencyCode || this.currencyCode;
//       if (code === this.currencyCode) {
//         return sum + (po.grandTotal || 0);
//       }
//       const converted = this.convertToLocalCurrency(po.grandTotal || 0, code);
//       return sum + converted;
//     }, 0);
//   }

//   // ── Status Filter ────────────────────────────────────────────

//   applyStatusFilter(): void {
//     if (this.statusFilter === 'ALL') {
//       this.filteredOrders = [...this.purchaseOrders];
//     } else if (this.statusFilter === 'HOLD') {
//       this.filteredOrders = this.purchaseOrders.filter(
//         po => po.status === 'PENDING_APPROVAL' && po.internalNotes?.toLowerCase().includes('hold')
//       );
//     } else {
//       this.filteredOrders = this.purchaseOrders.filter(po => po.status === this.statusFilter);
//     }
//     this.currentPage = 1;
//   }

//   // ── Purchase Orders ──────────────────────────────────────────

//   loadPurchaseOrders(): void {
//     if (!this.buyerId) return;
//     this.isLoading = true;

//     this.dataService.getPurchaseOrdersByBuyer(this.buyerId).subscribe({
//       next: (response: any) => {
//         if (response && response.success) {
//           this.purchaseOrders = response.data || [];
//         } else {
//           this.purchaseOrders = [];
//         }

//         // Read buyer's local currency from first PO
//         if (this.purchaseOrders.length > 0) {
//           // Find a same-country PO first (its currency IS the buyer's local currency)
//           // Fallback: use first PO currency
//           const sameCurrencyPO = this.purchaseOrders.find(po =>
//             po.currencyCode && po.currencyCode !== 'USD'
//           );
//           const refPO = sameCurrencyPO || this.purchaseOrders[0];
//           if (refPO.currencyCode) {
//             this.currencyCode   = refPO.currencyCode;
//             this.currencySymbol = refPO.currencySymbol || this.getSymbolForCode(refPO.currencyCode);
//           }
//         }

//         this.holdCount = this.purchaseOrders.filter(
//           po => po.status === 'PENDING_APPROVAL' && po.internalNotes?.toLowerCase().includes('hold')
//         ).length;

//         this.currentPage = 1;
//         this.applyStatusFilter();
//         this.isLoading = false;
//         this.loadApprovalRemarks();

//         // ✅ Load exchange rates after we know the buyer's currency
//         this.loadExchangeRates();
//       },
//       error: () => {
//         this.messageService.showMessage('error', 'Error', 'Failed to load Purchase Orders');
//         this.isLoading = false;
//       }
//     });
//   }

//   private loadApprovalRemarks(): void {
//     const actionedPOs = this.purchaseOrders.filter(po =>
//       ['REJECTED', 'RETURNED_FOR_REVISION', 'PENDING_APPROVAL', 'HOLD'].includes(po.status)
//     );
//     if (actionedPOs.length === 0) return;
//     this.loadingRemarks = true;
//     let completed = 0;

//     actionedPOs.forEach(po => {
//       this.dataService.getPOApprovalHistory(po.id).subscribe({
//         next: (response: any) => {
//           const history: any[] = response?.data || response || [];
//           const latestAction = this.extractLatestAction(history, po.status);
//           if (latestAction) this.approvalRemarksMap.set(po.id, latestAction);
//           completed++;
//           if (completed === actionedPOs.length) this.loadingRemarks = false;
//         },
//         error: () => {
//           completed++;
//           if (completed === actionedPOs.length) this.loadingRemarks = false;
//         }
//       });
//     });
//   }

//   private extractLatestAction(history: any[], status: string): any | null {
//     if (!history || history.length === 0) return null;
//     const sorted = [...history].sort((a, b) =>
//       new Date(b.actionDate || 0).getTime() - new Date(a.actionDate || 0).getTime()
//     );
//     if (status === 'REJECTED') {
//       const r = sorted.find(h => h.rejectedByUserName || h.rejectRemarks || h.status === 'REJECTED');
//       if (r) return { type: 'rejected', remarks: r.rejectRemarks || r.comments, byName: r.rejectedByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.rejectDate || r.actionDate };
//     }
//     if (status === 'RETURNED_FOR_REVISION') {
//       const r = sorted.find(h => h.returnedByUserName || h.returnRemarks || h.status === 'RESUBMITTED');
//       if (r) return { type: 'revision', remarks: r.returnRemarks || r.revisionComments || r.comments, byName: r.returnedByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.returnDate || r.actionDate };
//     }
//     if (status === 'HOLD') {
//       const r = sorted.find(h => h.heldByUserName || h.holdRemarks || h.status === 'HOLD');
//       if (r) return { type: 'hold', remarks: r.holdRemarks || r.comments, byName: r.heldByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.holdDate || r.actionDate };
//     }
//     if (status === 'PENDING_APPROVAL') {
//       const r = sorted.find(h => h.status === 'PENDING');
//       if (r) return { type: 'pending', remarks: null, byName: r.approverUserName, byLevel: r.hierarchyLevelName, date: r.actionDate };
//     }
//     return null;
//   }

//   getApprovalRemark(poId: number): any | null { return this.approvalRemarksMap.get(poId) || null; }
//   hasRemark(po: any): boolean { return this.approvalRemarksMap.has(po.id); }

//   // ── Pagination ───────────────────────────────────────────────

//   get totalPages(): number { return Math.max(1, Math.ceil(this.filteredOrders.length / this.pageSize)); }

//   get paginatedOrders(): any[] {
//     const start = (this.currentPage - 1) * this.pageSize;
//     return this.filteredOrders.slice(start, start + this.pageSize);
//   }

//   get pageNumbers(): number[] {
//     const total = this.totalPages, current = this.currentPage;
//     const pages: number[] = [];
//     if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
//     pages.push(1);
//     if (current > 3) pages.push(-1);
//     const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
//     for (let i = start; i <= end; i++) pages.push(i);
//     if (current < total - 2) pages.push(-1);
//     pages.push(total);
//     return pages;
//   }

//   get startIndex(): number {
//     if (this.filteredOrders.length === 0) return 0;
//     return (this.currentPage - 1) * this.pageSize + 1;
//   }
//   get endIndex(): number { return Math.min(this.currentPage * this.pageSize, this.filteredOrders.length); }

//   goToPage(page: number): void {
//     if (page < 1 || page > this.totalPages) return;
//     this.currentPage = page;
//   }
//   onPageSizeChange(size: number): void { this.pageSize = size; this.currentPage = 1; }

//   // ── Actions ──────────────────────────────────────────────────

//   viewPO(poId: number): void { this.router.navigate(['/po-details', poId]); }

//   deletePO(po: any): void {
//     if (po.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT Purchase Orders can be deleted'); return;
//     }
//     if (!confirm(`Delete Purchase Order ${po.poNumber}?\n\nThis action cannot be undone.`)) return;
//     this.deletingId = po.id;
//     this.dataService.deletePurchaseOrder(po.id).subscribe({
//       next: (response: any) => {
//         if (response && response.success) {
//           this.messageService.showMessage('success', 'Success', 'Purchase Order deleted successfully');
//           this.purchaseOrders = this.purchaseOrders.filter(p => p.id !== po.id);
//           this.approvalRemarksMap.delete(po.id);
//           this.holdCount = this.purchaseOrders.filter(
//             p => p.status === 'PENDING_APPROVAL' && p.internalNotes?.toLowerCase().includes('hold')
//           ).length;
//           this.applyStatusFilter();
//           if (this.paginatedOrders.length === 0 && this.currentPage > 1) this.currentPage--;
//         }
//         this.deletingId = null;
//       },
//       error: (error: any) => {
//         this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete');
//         this.deletingId = null;
//       }
//     });
//   }

//   submitForApproval(po: any): void {
//     if (po.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT POs can be submitted for approval'); return;
//     }
//     const userId = Number(localStorage.getItem('userId'));
//     this.dataService.initiatePOApproval(po.id, userId).subscribe({
//       next: (response: any) => {
//         if (response && response.success) {
//           this.messageService.showMessage('success', 'Success', `PO ${po.poNumber} submitted for approval`);
//           this.loadPurchaseOrders();
//         } else {
//           this.messageService.showMessage('error', 'Error', response?.message || 'Failed to submit');
//         }
//       },
//       error: (error: any) => {
//         this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to submit for approval');
//       }
//     });
//   }

//   goToQuoteComparison(): void { this.router.navigate(['/rfq-dashboard']); }
//   goBack(): void { this.router.navigate(['/rfq-dashboard']); }

//   // ── Helpers ──────────────────────────────────────────────────

//   countByStatus(status: string): number { return this.purchaseOrders.filter(po => po.status === status).length; }

//   getTotalValue(): number { return this.purchaseOrders.reduce((sum, po) => sum + (po.grandTotal || 0), 0); }

//   getStatusBadgeClass(status: string): string {
//     const c: any = {
//       'DRAFT': 'bg-secondary', 'PENDING_APPROVAL': 'bg-warning text-dark',
//       'APPROVED': 'bg-success', 'REJECTED': 'bg-danger',
//       'RETURNED_FOR_REVISION': 'bg-warning text-dark', 'HOLD': 'bg-warning text-dark',
//       'SENT_TO_SUPPLIER': 'bg-info', 'COMPLETED': 'bg-dark', 'CANCELLED': 'bg-danger'
//     };
//     return c[status] || 'bg-secondary';
//   }

//   formatDate(dateString: string): string {
//     if (!dateString) return 'N/A';
//     return new Date(dateString).toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
//   }

//   formatDateTime(dateString: string): string {
//     if (!dateString) return 'N/A';
//     return new Date(dateString).toLocaleString('en-IN', {
//       year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
//     });
//   }
// }


import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import { BuyerService } from '../dashboard/buyer-b.service';

// ── Financial Year option shape ──────────────────────────────
interface FYOption {
  value: string;   // e.g. 'FY2025-26'
  label: string;   // e.g. 'FY 2025-26 (Apr 2025 – Mar 2026)'
  from: Date;
  to: Date;
}

@Component({
  selector: 'app-po-list',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent,
    ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
  ],
  templateUrl: './po-list.component.html',
  styleUrls: ['./po-list.component.css']
})
export class POListComponent implements OnInit {

  purchaseOrders: any[] = [];           // raw data from API
  dateFilteredOrders: any[] = [];       // after date filter (stat cards use this)
  filteredOrders: any[] = [];           // after status/search filter (table uses this)
  isLoading: boolean = false;
  deletingId: number | null = null;
  buyerId: number | null = null;
  holdCount: number = 0;
  loggedInBuyer: any = null;
  approvalRemarksMap: Map<number, any> = new Map();
  loadingRemarks: boolean = false;

  // Buyer's local currency
  currencyCode: string = 'INR';
  currencySymbol: string = '₹';

  exchangeRates: Record<string, number> = {};
  exchangeRatesLoaded: boolean = false;

  // ── Filter ───────────────────────────────────────────────────
  statusFilter: string = 'ALL';

  // ── Pagination ──────────────────────────────────────────────
  currentPage: number = 1;
  pageSize: number = 10;
  pageSizeOptions: number[] = [5, 10, 25, 50];

  // ── Date Filter State ────────────────────────────────────────
  financialYearOptions: FYOption[] = [];
  selectedFYOption: string = '';
  customFromDate: string = '';
  customToDate: string = '';
  activeDateRangeLabel: string = '';

  constructor(
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService,
    private buyerService: BuyerService
  ) {}

  ngOnInit(): void {
    this.buyerId = Number(localStorage.getItem('buyerId'));
    if (!this.buyerId || isNaN(this.buyerId)) {
      this.messageService.showMessage('error', 'Error', 'Buyer ID not found. Please login again.');
      this.router.navigate(['/login']);
      return;
    }
    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();
    this.loadLoggedInBuyer();
    this.loadPurchaseOrders();
  }

  // =========================================================================
  //  FINANCIAL YEAR HELPERS
  // =========================================================================

  private buildFinancialYearOptions(): void {
    const today = new Date();
    const currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;
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
    this.applyFiltersAndPagination();
  }

  resetDateFilter(): void {
    this.selectedFYOption = this.getCurrentFYValue();
    this.customFromDate   = '';
    this.customToDate     = '';
    this.updateActiveDateRangeLabel();
    this.applyFiltersAndPagination();
  }

  private updateActiveDateRangeLabel(): void {
    if (this.selectedFYOption === 'ALL') { this.activeDateRangeLabel = 'All Time'; return; }
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
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
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

  private applyDateFilter(orders: any[]): any[] {
    const range = this.getActiveDateRange();
    if (!range) return orders;
    return orders.filter((po: any) => {
      const dateStr = po.createdAt || po.poDate;
      if (!dateStr) return false;
      const created = new Date(dateStr);
      return created >= range.from && created <= range.to;
    });
  }

  // ── Stat card counts (based on date-filtered data) ───────────
  countByStatus(status: string): number {
    return this.dateFilteredOrders.filter((po: any) => po.status === status).length;
  }

  get dateFilteredHoldCount(): number {
    return this.dateFilteredOrders.filter(
      po => po.status === 'PENDING_APPROVAL' && po.internalNotes?.toLowerCase().includes('hold')
    ).length;
  }

  getTotalValueInLocalCurrency(): number {
    return this.dateFilteredOrders.reduce((sum, po) => {
      const code = po.currencyCode || this.currencyCode;
      if (code === this.currencyCode) return sum + (po.grandTotal || 0);
      const converted = this.convertToLocalCurrency(po.grandTotal || 0, code);
      return sum + converted;
    }, 0);
  }

  // =========================================================================
  //  FILTERING & PAGINATION
  // =========================================================================

  applyFiltersAndPagination(): void {
    this.updateActiveDateRangeLabel();

    // Step 1: date filter → used by stat cards
    this.dateFilteredOrders = this.applyDateFilter(this.purchaseOrders);

    // Step 2: status filter → used by table
    let data = [...this.dateFilteredOrders];
    if (this.statusFilter === 'HOLD') {
      data = data.filter(po => po.status === 'PENDING_APPROVAL' && po.internalNotes?.toLowerCase().includes('hold'));
    } else if (this.statusFilter !== 'ALL') {
      data = data.filter(po => po.status === this.statusFilter);
    }

    this.filteredOrders = data;
    this.currentPage = 1;
  }

  applyStatusFilter(): void {
    this.applyFiltersAndPagination();
  }

  // =========================================================================
  //  BUYER INFO
  // =========================================================================

  private loadLoggedInBuyer(): void {
    if (!this.buyerId) return;
    this.buyerService.getBuyerById(this.buyerId).subscribe({
      next: (response: any) => {
        const buyerDetails = response?.data || response;
        if (!buyerDetails || !buyerDetails.id) return;

        const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
        const loggedInName  = localStorage.getItem('fullName');
        const loggedInPhone = localStorage.getItem('phone');

        let city = 'N/A', state = 'N/A';
        const userLocationId = localStorage.getItem('locationId');
        if (buyerDetails.locations && Array.isArray(buyerDetails.locations)) {
          let selectedLoc = userLocationId
            ? buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId))
            : null;
          if (!selectedLoc && buyerDetails.locations.length > 0) selectedLoc = buyerDetails.locations[0];
          if (selectedLoc) { city = selectedLoc.city || 'N/A'; state = selectedLoc.state || 'N/A'; }
        }

        this.loggedInBuyer = {
          companyName:        buyerDetails.companyName || 'N/A',
          companyType:        buyerDetails.companyType || 'IT',
          email:              loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
          contactPersonName:  loggedInName  || buyerDetails.contactPersonName  || 'N/A',
          contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
          city, state
        };
      },
      error: () => {}
    });
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const parts = name.trim().split(' ');
    return parts.length >= 2
      ? (parts[0][0] + parts[1][0]).toUpperCase()
      : name.substring(0, 2).toUpperCase();
  }

  hasCrossBorderPOs(): boolean {
    return this.purchaseOrders.some(po => this.isCrossBorderPO(po));
  }

  // =========================================================================
  //  EXCHANGE RATES
  // =========================================================================

  private loadExchangeRates(): void {
    const base = this.currencyCode;
    fetch(`https://open.er-api.com/v6/latest/${base}`)
      .then(r => r.json())
      .then(data => {
        if (data && data.rates) {
          this.exchangeRates = data.rates;
          this.exchangeRatesLoaded = true;
        }
      })
      .catch(err => {
        console.warn('[POList] Exchange rate fetch failed:', err);
        this.exchangeRatesLoaded = false;
      });
  }

  // =========================================================================
  //  CURRENCY HELPERS
  // =========================================================================

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

  formatCurrencyAmount(amount: number): string {
    const val = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
    return `${this.currencySymbol} ${formatted}`;
  }

  formatInCurrency(amount: number, code: string): string {
    const sym = this.getSymbolForCode(code);
    const val = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    if (rtlCodes.includes(code)) return `${formatted} ${sym}`;
    return `${sym} ${formatted}`;
  }

  isCrossBorderPO(po: any): boolean {
    const poCode = po.currencyCode || this.currencyCode;
    return poCode !== this.currencyCode;
  }

  convertToLocalCurrency(amount: number, fromCurrencyCode: string): number {
    if (!this.exchangeRatesLoaded || !this.exchangeRates) return 0;
    if (fromCurrencyCode === this.currencyCode) return amount;
    const rate = this.exchangeRates[fromCurrencyCode];
    if (!rate || rate === 0) return 0;
    return amount / rate;
  }

  getConvertedDisplay(po: any): string {
    if (!this.isCrossBorderPO(po)) return '';
    if (!this.exchangeRatesLoaded) return 'Loading...';
    const converted = this.convertToLocalCurrency(po.grandTotal, po.currencyCode || 'USD');
    if (converted === 0) return '';
    return `≈ ${this.formatCurrencyAmount(converted)}`;
  }

  // =========================================================================
  //  PURCHASE ORDERS
  // =========================================================================

  loadPurchaseOrders(): void {
    if (!this.buyerId) return;
    this.isLoading = true;

    this.dataService.getPurchaseOrdersByBuyer(this.buyerId).subscribe({
      next: (response: any) => {
        if (response && response.success) {
          this.purchaseOrders = response.data || [];
        } else {
          this.purchaseOrders = [];
        }

        // Detect buyer's local currency from POs
        if (this.purchaseOrders.length > 0) {
          const sameCurrencyPO = this.purchaseOrders.find(po =>
            po.currencyCode && po.currencyCode !== 'USD'
          );
          const refPO = sameCurrencyPO || this.purchaseOrders[0];
          if (refPO.currencyCode) {
            this.currencyCode   = refPO.currencyCode;
            this.currencySymbol = refPO.currencySymbol || this.getSymbolForCode(refPO.currencyCode);
          }
        }

        this.holdCount = this.purchaseOrders.filter(
          po => po.status === 'PENDING_APPROVAL' && po.internalNotes?.toLowerCase().includes('hold')
        ).length;

        this.applyFiltersAndPagination();
        this.isLoading = false;
        this.loadApprovalRemarks();
        this.loadExchangeRates();
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to load Purchase Orders');
        this.isLoading = false;
      }
    });
  }

  private loadApprovalRemarks(): void {
    const actionedPOs = this.purchaseOrders.filter(po =>
      ['REJECTED', 'RETURNED_FOR_REVISION', 'PENDING_APPROVAL', 'HOLD'].includes(po.status)
    );
    if (actionedPOs.length === 0) return;
    this.loadingRemarks = true;
    let completed = 0;

    actionedPOs.forEach(po => {
      this.dataService.getPOApprovalHistory(po.id).subscribe({
        next: (response: any) => {
          const history: any[] = response?.data || response || [];
          const latestAction = this.extractLatestAction(history, po.status);
          if (latestAction) this.approvalRemarksMap.set(po.id, latestAction);
          completed++;
          if (completed === actionedPOs.length) this.loadingRemarks = false;
        },
        error: () => {
          completed++;
          if (completed === actionedPOs.length) this.loadingRemarks = false;
        }
      });
    });
  }

  private extractLatestAction(history: any[], status: string): any | null {
    if (!history || history.length === 0) return null;
    const sorted = [...history].sort((a, b) =>
      new Date(b.actionDate || 0).getTime() - new Date(a.actionDate || 0).getTime()
    );
    if (status === 'REJECTED') {
      const r = sorted.find(h => h.rejectedByUserName || h.rejectRemarks || h.status === 'REJECTED');
      if (r) return { type: 'rejected', remarks: r.rejectRemarks || r.comments, byName: r.rejectedByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.rejectDate || r.actionDate };
    }
    if (status === 'RETURNED_FOR_REVISION') {
      const r = sorted.find(h => h.returnedByUserName || h.returnRemarks || h.status === 'RESUBMITTED');
      if (r) return { type: 'revision', remarks: r.returnRemarks || r.revisionComments || r.comments, byName: r.returnedByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.returnDate || r.actionDate };
    }
    if (status === 'HOLD') {
      const r = sorted.find(h => h.heldByUserName || h.holdRemarks || h.status === 'HOLD');
      if (r) return { type: 'hold', remarks: r.holdRemarks || r.comments, byName: r.heldByUserName || r.approverUserName, byLevel: r.hierarchyLevelName, date: r.holdDate || r.actionDate };
    }
    if (status === 'PENDING_APPROVAL') {
      const r = sorted.find(h => h.status === 'PENDING');
      if (r) return { type: 'pending', remarks: null, byName: r.approverUserName, byLevel: r.hierarchyLevelName, date: r.actionDate };
    }
    return null;
  }

  getApprovalRemark(poId: number): any | null { return this.approvalRemarksMap.get(poId) || null; }
  hasRemark(po: any): boolean { return this.approvalRemarksMap.has(po.id); }

  // =========================================================================
  //  PAGINATION
  // =========================================================================

  get totalPages(): number { return Math.max(1, Math.ceil(this.filteredOrders.length / this.pageSize)); }

  get paginatedOrders(): any[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredOrders.slice(start, start + this.pageSize);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages, current = this.currentPage;
    const pages: number[] = [];
    if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
    pages.push(1);
    if (current > 3) pages.push(-1);
    const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
    for (let i = start; i <= end; i++) pages.push(i);
    if (current < total - 2) pages.push(-1);
    pages.push(total);
    return pages;
  }

  get startIndex(): number {
    if (this.filteredOrders.length === 0) return 0;
    return (this.currentPage - 1) * this.pageSize + 1;
  }
  get endIndex(): number { return Math.min(this.currentPage * this.pageSize, this.filteredOrders.length); }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
  }
  onPageSizeChange(size: number): void { this.pageSize = size; this.currentPage = 1; }

  // =========================================================================
  //  ACTIONS
  // =========================================================================

  viewPO(poId: number): void { this.router.navigate(['/po-details', poId]); }

  deletePO(po: any): void {
    if (po.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT Purchase Orders can be deleted'); return;
    }
    if (!confirm(`Delete Purchase Order ${po.poNumber}?\n\nThis action cannot be undone.`)) return;
    this.deletingId = po.id;
    this.dataService.deletePurchaseOrder(po.id).subscribe({
      next: (response: any) => {
        if (response && response.success) {
          this.messageService.showMessage('success', 'Success', 'Purchase Order deleted successfully');
          this.purchaseOrders = this.purchaseOrders.filter(p => p.id !== po.id);
          this.approvalRemarksMap.delete(po.id);
          this.holdCount = this.purchaseOrders.filter(
            p => p.status === 'PENDING_APPROVAL' && p.internalNotes?.toLowerCase().includes('hold')
          ).length;
          this.applyFiltersAndPagination();
          if (this.paginatedOrders.length === 0 && this.currentPage > 1) this.currentPage--;
        }
        this.deletingId = null;
      },
      error: (error: any) => {
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete');
        this.deletingId = null;
      }
    });
  }

  submitForApproval(po: any): void {
    if (po.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT POs can be submitted for approval'); return;
    }
    const userId = Number(localStorage.getItem('userId'));
    this.dataService.initiatePOApproval(po.id, userId).subscribe({
      next: (response: any) => {
        if (response && response.success) {
          this.messageService.showMessage('success', 'Success', `PO ${po.poNumber} submitted for approval`);
          this.loadPurchaseOrders();
        } else {
          this.messageService.showMessage('error', 'Error', response?.message || 'Failed to submit');
        }
      },
      error: (error: any) => {
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to submit for approval');
      }
    });
  }

  goToQuoteComparison(): void { this.router.navigate(['/rfq-dashboard']); }
  goBack(): void { this.router.navigate(['/rfq-dashboard']); }

  // =========================================================================
  //  HELPERS
  // =========================================================================

  getTotalValue(): number { return this.purchaseOrders.reduce((sum, po) => sum + (po.grandTotal || 0), 0); }

  getStatusBadgeClass(status: string): string {
    const c: any = {
      'DRAFT': 'bg-secondary', 'PENDING_APPROVAL': 'bg-warning text-dark',
      'APPROVED': 'bg-success', 'REJECTED': 'bg-danger',
      'RETURNED_FOR_REVISION': 'bg-warning text-dark', 'HOLD': 'bg-warning text-dark',
      'SENT_TO_SUPPLIER': 'bg-info', 'COMPLETED': 'bg-dark', 'CANCELLED': 'bg-danger'
    };
    return c[status] || 'bg-secondary';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  formatDateTime(dateString: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }
}