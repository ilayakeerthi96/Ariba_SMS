

// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, ModalModule, SpinnerComponent
// } from '@coreui/angular';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';
// import { BuyerService } from '../dashboard/buyer-b.service';

// import jsPDF from 'jspdf';
// import html2canvas from 'html2canvas';

// @Component({
//   selector: 'app-buyer-invoices',
//   templateUrl: './buyer-invoices.component.html',
//   styleUrls: ['./buyer-invoices.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent, ButtonDirective,
//     BadgeComponent, ModalModule, SpinnerComponent
//   ]
// })
// export class BuyerInvoicesComponent implements OnInit {

//   // ==================== BUYER INFO ====================
//   loggedInBuyer: any = null;
//   buyerId: number | null = null;

//   // ==================== USER ====================
//   buyerName: string = '';
//   companyName: string = '';

//   // ==================== DATA ====================
//   invoiceList: any[] = [];
//   filteredInvoices: any[] = [];
//   pagedInvoices: any[] = [];

//   // ==================== FILTERS ====================
//   searchText: string = '';
//   statusFilter: string = 'ALL';

//   // ==================== PAGINATION ====================
//   currentPage: number = 1;
//   pageSize: number = 10;

//   // ==================== VIEW MODAL ====================
//   selectedInvoice: any = null;
//   isViewModalOpen: boolean = false;

//   // ==================== ACTION MODAL ====================
//   isActionModalOpen: boolean = false;
//   pendingAction: 'approve' | 'reject' | 'rejectClose' | 'paid' | null = null;
//   actionRemarks: string = '';
//   paymentReference: string = '';
//   isPerformingAction: boolean = false;

//   // ==================== LOADING ====================
//   isLoading: boolean = false;
//   isDownloadingPDF: boolean = false;
//   isDownloadingInvoiceExcel = false;
// isDownloadingInvoicePdf   = false;

//   // ==================== STATS ====================
//   stats = { total: 0, submitted: 0, approved: 0, rejected: 0, paid: 0 };

//   // ==================== PREREQUISITE MODAL ====================
//   isPrereqModalOpen: boolean = false;
//   prereqInvoice: any = null;
//   prereqChecks = {
//     grnCreated:  false,
//     grnApproved: false,
//     matchDone:   false,
//     matchPassed: false,
//   };
//   isCheckingPrereqs: boolean = false;

//   constructor(
//     private dataService: DataService,
//     private messageService: MessageService,
//     private authService: AuthService,
//     private router: Router,
//     private cdr: ChangeDetectorRef,
//     private buyerService: BuyerService
//   ) {}

//   downloadInvoiceExcel(invoiceId: number, invoiceNumber: string): void {
//     this.isDownloadingInvoiceExcel = true;
//     this.dataService.getInvoiceExcel(invoiceId).subscribe({
//         next: (blob: Blob) => {
//             this.dataService.saveBlob(blob, `Invoice_Report_${invoiceNumber}_${this.dataService.todayStr()}.xlsx`);
//             this.messageService.showMessage('success', 'Success', 'Invoice Excel report downloaded');
//             this.isDownloadingInvoiceExcel = false;
//         },
//         error: () => {
//             this.messageService.showMessage('error', 'Error', 'Failed to download Invoice Excel');
//             this.isDownloadingInvoiceExcel = false;
//         }
//     });
// }
 
// downloadInvoicePdf(invoiceId: number, invoiceNumber: string): void {
//     this.isDownloadingInvoicePdf = true;
//     this.dataService.getInvoicePDF(invoiceId).subscribe({
//         next: (blob: Blob) => {
//             this.dataService.saveBlob(blob, `Invoice_Report_${invoiceNumber}_${this.dataService.todayStr()}.pdf`);
//             this.messageService.showMessage('success', 'Success', 'Invoice PDF report downloaded');
//             this.isDownloadingInvoicePdf = false;
//         },
//         error: () => {
//             this.messageService.showMessage('error', 'Error', 'Failed to download Invoice PDF');
//             this.isDownloadingInvoicePdf = false;
//         }
//     });
// }

//   private loadLoggedInBuyer(): void {
//     this.buyerId = Number(localStorage.getItem('buyerId'));
//     if (!this.buyerId || isNaN(this.buyerId)) return;

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
//         this.cdr.markForCheck();
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

//   ngOnInit(): void {
//     this.companyName = localStorage.getItem('companyName')
//                     || localStorage.getItem('buyerCompanyName')
//                     || localStorage.getItem('buyerName')
//                     || '';

//     this.buyerName = localStorage.getItem('buyerName')
//                   || localStorage.getItem('fullName')
//                   || localStorage.getItem('email')
//                   || 'Buyer';

//     if (!this.companyName.trim()) {
//       console.error('[BuyerInvoices] companyName is empty — check localStorage keys');
//     }

//     this.loadInvoices();
//     this.loadLoggedInBuyer();
//   }

//   goBack(): void { this.router.navigate(['/rfq-dashboard']); }

//   // ==================== LOAD ====================

//   loadInvoices(): void {
//     if (!this.companyName.trim()) {
//       this.messageService.showMessage('error', 'Error',
//         'Company name not found. Please logout and login again.');
//       return;
//     }
//     this.isLoading = true;
//     this.dataService.getBuyerInvoices(this.companyName).subscribe({
//       next: (response: any) => {
//         const raw = response?.success ? (response.data || []) : [];
//         // ✅ Normalize currency fields on every invoice
//         this.invoiceList = raw.map((inv: any) => ({
//           ...inv,
//           currencyCode:   inv.currencyCode   || inv.currency || 'INR',
//           currencySymbol: inv.currencySymbol || this.getSymbolForCode(inv.currencyCode || inv.currency || 'INR')
//         }));
//         this.calculateStats();
//         this.applyFilters();
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: (err: any) => {
//         console.error('[BuyerInvoices] Load error:', err);
//         this.invoiceList = [];
//         this.isLoading = false;
//       }
//     });
//   }

//   calculateStats(): void {
//     this.stats = {
//       total:     this.invoiceList.length,
//       submitted: this.invoiceList.filter(i => i.status === 'SUBMITTED').length,
//       approved:  this.invoiceList.filter(i => i.status === 'APPROVED').length,
//       rejected:  this.invoiceList.filter(i =>
//         i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED').length,
//       paid:      this.invoiceList.filter(i => i.status === 'PAID').length
//     };
//   }

//   applyFilters(): void {
//     let data = [...this.invoiceList];

//     if (this.searchText.trim()) {
//       const s = this.searchText.toLowerCase();
//       data = data.filter(i =>
//         i.invoiceNumber?.toLowerCase().includes(s) ||
//         i.supplierName?.toLowerCase().includes(s)  ||
//         i.supplierCompanyName?.toLowerCase().includes(s) ||
//         i.poNumber?.toLowerCase().includes(s)      ||
//         i.rfqNumber?.toLowerCase().includes(s)
//       );
//     }

//     if (this.statusFilter === 'REJECTED') {
//       data = data.filter(i => i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED');
//     } else if (this.statusFilter !== 'ALL') {
//       data = data.filter(i => i.status === this.statusFilter);
//     }

//     this.filteredInvoices = data;
//     this.currentPage = 1;
//     this.updatePagination();
//   }

//   updatePagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedInvoices = this.filteredInvoices.slice(start, start + this.pageSize);
//     this.cdr.markForCheck();
//   }

//   get totalPages(): number { return Math.ceil(this.filteredInvoices.length / this.pageSize); }

//   onPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.updatePagination();
//     }
//   }

//   // ==================== VIEW INVOICE ====================

//   viewInvoice(invoice: any): void {
//     this.isLoading = true;
//     this.dataService.getInvoiceById(invoice.id).subscribe({
//       next: (response: any) => {
//         const data = response?.success ? response.data : invoice;
//         // ✅ Normalize currency on the fetched invoice too
//         this.selectedInvoice = {
//           ...data,
//           currencyCode:   data.currencyCode   || data.currency || 'INR',
//           currencySymbol: data.currencySymbol || this.getSymbolForCode(data.currencyCode || data.currency || 'INR')
//         };
//         this.isViewModalOpen = true;
//         this.isLoading = false;
//       },
//       error: () => {
//         this.selectedInvoice = {
//           ...invoice,
//           currencyCode:   invoice.currencyCode   || invoice.currency || 'INR',
//           currencySymbol: invoice.currencySymbol || this.getSymbolForCode(invoice.currencyCode || invoice.currency || 'INR')
//         };
//         this.isViewModalOpen = true;
//         this.isLoading = false;
//       }
//     });
//   }

//   closeViewModal(): void {
//     this.isViewModalOpen = false;
//     this.selectedInvoice = null;
//   }

//   // ==================== ACTION MODAL ====================

//   openActionModal(invoice: any, action: 'approve' | 'reject' | 'rejectClose' | 'paid'): void {
//     this.selectedInvoice  = invoice;
//     this.pendingAction    = action;
//     this.actionRemarks    = '';
//     this.paymentReference = '';
//     this.isActionModalOpen = true;
//   }

//   confirmAction(): void {
//     if (!this.selectedInvoice || !this.pendingAction) return;

//     if (['approve', 'reject', 'rejectClose'].includes(this.pendingAction)
//         && !this.actionRemarks.trim()) {
//       this.messageService.showMessage('warning', 'Required', 'Please enter remarks');
//       return;
//     }

//     this.isPerformingAction = true;
//     let action$: any;

//     switch (this.pendingAction) {
//       case 'approve':
//         action$ = this.dataService.approveInvoice(
//           this.selectedInvoice.id, this.buyerName, this.actionRemarks);
//         break;
//       case 'reject':
//         action$ = this.dataService.rejectInvoice(
//           this.selectedInvoice.id, this.buyerName, this.actionRemarks);
//         break;
//       case 'rejectClose':
//         action$ = this.dataService.rejectInvoicePermanent(
//           this.selectedInvoice.id, this.buyerName, this.actionRemarks);
//         break;
//       case 'paid':
//         action$ = this.dataService.markInvoicePaid(
//           this.selectedInvoice.id, this.buyerName, this.paymentReference);
//         break;
//     }

//     action$!.subscribe({
//       next: () => {
//         const msgs: Record<string, string> = {
//           approve:     'Invoice approved — supplier has been notified',
//           reject:      'Invoice returned to supplier for correction (1 resubmission allowed)',
//           rejectClose: 'Invoice permanently closed — supplier cannot resubmit',
//           paid:        'Invoice marked as paid — supplier notified'
//         };
//         this.messageService.showMessage('success', 'Success',
//           msgs[this.pendingAction!] || 'Done');
//         this.isActionModalOpen = false;
//         this.isViewModalOpen   = false;
//         this.isPerformingAction = false;
//         this.loadInvoices();
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error',
//           err.error?.message || 'Action failed');
//         this.isPerformingAction = false;
//       }
//     });
//   }

//   // ==================== PREREQUISITE MODAL ====================

//   handleApproveClick(invoice: any): void {
//     if (invoice.canApprove === true) {
//       this.openActionModal(invoice, 'approve');
//       return;
//     }

//     this.prereqInvoice      = invoice;
//     this.isCheckingPrereqs  = true;
//     this.isPrereqModalOpen  = true;
//     this.prereqChecks = { grnCreated: false, grnApproved: false, matchDone: false, matchPassed: false };

//     this.dataService.getInvoiceById(invoice.id).subscribe({
//       next: (response: any) => {
//         const data = response?.success ? response.data : invoice;
//         // ✅ Normalize currency here too
//         this.prereqInvoice = {
//           ...data,
//           currencyCode: data.currencyCode || data.currency || 'INR'
//         };

//         const matchStatus = data.threeWayMatchStatus;
//         const matchPassed = data.threeWayMatchPassed === true;
//         const matchExists = matchStatus != null && matchStatus !== 'null';

//         this.prereqChecks = {
//           grnCreated:  matchExists,
//           grnApproved: matchExists,
//           matchDone:   matchExists,
//           matchPassed: matchPassed,
//         };

//         this.isCheckingPrereqs = false;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.prereqChecks      = { grnCreated: false, grnApproved: false, matchDone: false, matchPassed: false };
//         this.isCheckingPrereqs = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   closePrereqModal(): void {
//     this.isPrereqModalOpen = false;
//     this.prereqInvoice     = null;
//   }

//   proceedToApprove(): void {
//     const inv = this.prereqInvoice;
//     this.closePrereqModal();
//     this.openActionModal(inv, 'approve');
//   }

//   get allPrereqsPassed(): boolean {
//     return this.prereqChecks.grnCreated
//         && this.prereqChecks.grnApproved
//         && this.prereqChecks.matchDone
//         && this.prereqChecks.matchPassed;
//   }

//   // ==================== PDF ====================

//   downloadPDF(): void {
//     if (!this.selectedInvoice) return;
//     this.isDownloadingPDF = true;
//     const el = document.getElementById('buyer-invoice-print');
//     if (!el) { this.isDownloadingPDF = false; return; }

//     html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#ffffff' }).then(canvas => {
//       const pdf      = new jsPDF('p', 'mm', 'a4');
//       const imgData  = canvas.toDataURL('image/png');
//       const pdfWidth = 210;
//       const imgH     = (canvas.height * pdfWidth) / canvas.width;
//       let hLeft = imgH, pos = 0;
//       pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgH);
//       hLeft -= 297;
//       while (hLeft > 0) {
//         pos = hLeft - imgH; pdf.addPage();
//         pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgH);
//         hLeft -= 297;
//       }
//       pdf.save(`${this.selectedInvoice.invoiceNumber}.pdf`);
//       this.isDownloadingPDF = false;
//     }).catch(() => { this.isDownloadingPDF = false; });
//   }

//   // ==================== UTILITY ====================

//   getInvoiceStatusLabel(status: string): string {
//     const m: Record<string, string> = {
//       DRAFT:           'Draft',
//       SUBMITTED:       'Pending Review',
//       APPROVED:        'Approved',
//       REJECTED:        'Returned for Correction',
//       REJECTED_CLOSED: 'Permanently Closed',
//       PAID:            'Paid'
//     };
//     return m[status] || status;
//   }

//   getActionLabel(): string {
//     const m: Record<string, string> = {
//       approve:     'Approve Invoice',
//       reject:      'Return for Correction (Supplier can resubmit once)',
//       rejectClose: 'Reject & Close Permanently',
//       paid:        'Mark as Paid'
//     };
//     return m[this.pendingAction!] || '';
//   }

//   getActionButtonColor(): string {
//     const m: Record<string, string> = {
//       approve:     'success',
//       reject:      'warning',
//       rejectClose: 'danger',
//       paid:        'info'
//     };
//     return m[this.pendingAction!] || 'primary';
//   }

//   isOverdue(invoice: any): boolean {
//     if (!invoice?.dueDate || invoice.status === 'PAID') return false;
//     return new Date(invoice.dueDate) < new Date();
//   }

//   formatDate(d: string): string {
//     if (!d) return 'N/A';
//     try { return new Date(d).toLocaleDateString('en-GB'); } catch { return 'N/A'; }
//   }

//   /**
//    * ✅ Format currency using the buyer's location currency code.
//    * Reads from the invoice's currencyCode field (set from PO → buyer location).
//    * Falls back to INR if not present.
//    */
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

//   /** Lookup currency symbol from code */
//   private getSymbolForCode(code: string): string {
//     const map: Record<string, string> = {
//       'INR': '₹', 'USD': '$', 'EUR': '€', 'GBP': '£',
//       'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
//       'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$', 'NZD': 'NZ$',
//       'SAR': 'ر.س', 'QAR': 'ر.ق', 'KWD': 'د.ك', 'BHD': '.د.ب',
//       'OMR': 'ر.ع.', 'MYR': 'RM', 'THB': '฿', 'IDR': 'Rp',
//       'PKR': '₨', 'BDT': '৳',
//     };
//     return map[code] || code;
//   }

//   refresh(): void { this.loadInvoices(); }
// }

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, ModalModule, SpinnerComponent
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import { AuthService } from '../../../shared/service/AuthService';
import { BuyerService } from '../dashboard/buyer-b.service';

import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

// ── Financial Year option shape ──────────────────────────────
interface FYOption {
  value: string;
  label: string;
  from: Date;
  to: Date;
}

@Component({
  selector: 'app-buyer-invoices',
  templateUrl: './buyer-invoices.component.html',
  styleUrls: ['./buyer-invoices.component.css'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent, ButtonDirective,
    BadgeComponent, ModalModule, SpinnerComponent
  ]
})
export class BuyerInvoicesComponent implements OnInit {

  // ==================== BUYER INFO ====================
  loggedInBuyer: any = null;
  buyerId: number | null = null;

  // ==================== USER ====================
  buyerName: string = '';
  companyName: string = '';

  // ==================== DATA ====================
  invoiceList: any[] = [];             // raw data from API
  dateFilteredInvoices: any[] = [];   // after date filter (stat cards use this)
  filteredInvoices: any[] = [];        // after date + search/status filter (table uses this)
  pagedInvoices: any[] = [];

  // ==================== FILTERS ====================
  searchText: string = '';
  statusFilter: string = 'ALL';

  // ==================== PAGINATION ====================
  currentPage: number = 1;
  pageSize: number = 10;

  // ==================== VIEW MODAL ====================
  selectedInvoice: any = null;
  isViewModalOpen: boolean = false;

  // ==================== ACTION MODAL ====================
  isActionModalOpen: boolean = false;
  pendingAction: 'approve' | 'reject' | 'rejectClose' | 'paid' | null = null;
  actionRemarks: string = '';
  paymentReference: string = '';
  isPerformingAction: boolean = false;

  // ==================== LOADING ====================
  isLoading: boolean = false;
  isDownloadingPDF: boolean = false;
  isDownloadingInvoiceExcel = false;
  isDownloadingInvoicePdf   = false;

  // ==================== STATS (date-filtered) ====================
  stats = { total: 0, submitted: 0, approved: 0, rejected: 0, paid: 0 };

  // ==================== PREREQUISITE MODAL ====================
  isPrereqModalOpen: boolean = false;
  prereqInvoice: any = null;
  prereqChecks = {
    grnCreated:  false,
    grnApproved: false,
    matchDone:   false,
    matchPassed: false,
  };
  isCheckingPrereqs: boolean = false;

  // ── Date Filter State ────────────────────────────────────────
  financialYearOptions: FYOption[] = [];
  selectedFYOption: string = '';
  customFromDate: string = '';
  customToDate: string = '';
  activeDateRangeLabel: string = '';

  constructor(
    private dataService: DataService,
    private messageService: MessageService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private buyerService: BuyerService
  ) {}

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
    this.applyFilters();
  }

  resetDateFilter(): void {
    this.selectedFYOption = this.getCurrentFYValue();
    this.customFromDate   = '';
    this.customToDate     = '';
    this.updateActiveDateRangeLabel();
    this.applyFilters();
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

  private applyDateFilter(invoices: any[]): any[] {
    const range = this.getActiveDateRange();
    if (!range) return invoices;
    return invoices.filter((inv: any) => {
      // use invoiceDate as primary, fall back to createdAt
      const dateStr = inv.invoiceDate || inv.createdAt;
      if (!dateStr) return false;
      const d = new Date(dateStr);
      return d >= range.from && d <= range.to;
    });
  }

  // =========================================================================
  //  NGON INIT
  // =========================================================================

  ngOnInit(): void {
    this.companyName = localStorage.getItem('companyName')
                    || localStorage.getItem('buyerCompanyName')
                    || localStorage.getItem('buyerName')
                    || '';
    this.buyerName = localStorage.getItem('buyerName')
                  || localStorage.getItem('fullName')
                  || localStorage.getItem('email')
                  || 'Buyer';

    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();

    this.loadInvoices();
    this.loadLoggedInBuyer();
  }

  goBack(): void { this.router.navigate(['/rfq-dashboard']); }

  // =========================================================================
  //  LOAD
  // =========================================================================

  loadInvoices(): void {
    if (!this.companyName.trim()) {
      this.messageService.showMessage('error', 'Error',
        'Company name not found. Please logout and login again.');
      return;
    }
    this.isLoading = true;
    this.dataService.getBuyerInvoices(this.companyName).subscribe({
      next: (response: any) => {
        const raw = response?.success ? (response.data || []) : [];
        this.invoiceList = raw.map((inv: any) => ({
          ...inv,
          currencyCode:   inv.currencyCode   || inv.currency || 'INR',
          currencySymbol: inv.currencySymbol || this.getSymbolForCode(inv.currencyCode || inv.currency || 'INR')
        }));
        this.applyFilters();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('[BuyerInvoices] Load error:', err);
        this.invoiceList = [];
        this.isLoading = false;
      }
    });
  }

  // =========================================================================
  //  FILTERING & PAGINATION
  // =========================================================================

  applyFilters(): void {
    this.updateActiveDateRangeLabel();

    // Step 1: date filter → stat cards use this
    this.dateFilteredInvoices = this.applyDateFilter(this.invoiceList);

    // Step 2: recalculate stats from date-filtered data
    this.calculateStats();

    // Step 3: apply search + status filter on top
    let data = [...this.dateFilteredInvoices];

    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      data = data.filter(i =>
        i.invoiceNumber?.toLowerCase().includes(s) ||
        i.supplierName?.toLowerCase().includes(s)  ||
        i.supplierCompanyName?.toLowerCase().includes(s) ||
        i.poNumber?.toLowerCase().includes(s)      ||
        i.rfqNumber?.toLowerCase().includes(s)
      );
    }

    if (this.statusFilter === 'REJECTED') {
      data = data.filter(i => i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED');
    } else if (this.statusFilter !== 'ALL') {
      data = data.filter(i => i.status === this.statusFilter);
    }

    this.filteredInvoices = data;
    this.currentPage = 1;
    this.updatePagination();
  }

  calculateStats(): void {
    // Stats are always based on date-filtered data
    this.stats = {
      total:     this.dateFilteredInvoices.length,
      submitted: this.dateFilteredInvoices.filter(i => i.status === 'SUBMITTED').length,
      approved:  this.dateFilteredInvoices.filter(i => i.status === 'APPROVED').length,
      rejected:  this.dateFilteredInvoices.filter(i =>
        i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED').length,
      paid:      this.dateFilteredInvoices.filter(i => i.status === 'PAID').length
    };
  }

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedInvoices = this.filteredInvoices.slice(start, start + this.pageSize);
    this.cdr.markForCheck();
  }

  get totalPages(): number { return Math.ceil(this.filteredInvoices.length / this.pageSize); }

  onPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  // =========================================================================
  //  BUYER INFO
  // =========================================================================

  private loadLoggedInBuyer(): void {
    this.buyerId = Number(localStorage.getItem('buyerId'));
    if (!this.buyerId || isNaN(this.buyerId)) return;

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
        this.cdr.markForCheck();
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

  // =========================================================================
  //  DOWNLOAD
  // =========================================================================

  downloadInvoiceExcel(invoiceId: number, invoiceNumber: string): void {
    this.isDownloadingInvoiceExcel = true;
    this.dataService.getInvoiceExcel(invoiceId).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, `Invoice_Report_${invoiceNumber}_${this.dataService.todayStr()}.xlsx`);
        this.messageService.showMessage('success', 'Success', 'Invoice Excel report downloaded');
        this.isDownloadingInvoiceExcel = false;
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to download Invoice Excel');
        this.isDownloadingInvoiceExcel = false;
      }
    });
  }

  downloadInvoicePdf(invoiceId: number, invoiceNumber: string): void {
    this.isDownloadingInvoicePdf = true;
    this.dataService.getInvoicePDF(invoiceId).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, `Invoice_Report_${invoiceNumber}_${this.dataService.todayStr()}.pdf`);
        this.messageService.showMessage('success', 'Success', 'Invoice PDF report downloaded');
        this.isDownloadingInvoicePdf = false;
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to download Invoice PDF');
        this.isDownloadingInvoicePdf = false;
      }
    });
  }

  // =========================================================================
  //  VIEW INVOICE
  // =========================================================================

  viewInvoice(invoice: any): void {
    this.isLoading = true;
    this.dataService.getInvoiceById(invoice.id).subscribe({
      next: (response: any) => {
        const data = response?.success ? response.data : invoice;
        this.selectedInvoice = {
          ...data,
          currencyCode:   data.currencyCode   || data.currency || 'INR',
          currencySymbol: data.currencySymbol || this.getSymbolForCode(data.currencyCode || data.currency || 'INR')
        };
        this.isViewModalOpen = true;
        this.isLoading = false;
      },
      error: () => {
        this.selectedInvoice = {
          ...invoice,
          currencyCode:   invoice.currencyCode   || invoice.currency || 'INR',
          currencySymbol: invoice.currencySymbol || this.getSymbolForCode(invoice.currencyCode || invoice.currency || 'INR')
        };
        this.isViewModalOpen = true;
        this.isLoading = false;
      }
    });
  }

  closeViewModal(): void {
    this.isViewModalOpen = false;
    this.selectedInvoice = null;
  }

  // =========================================================================
  //  ACTION MODAL
  // =========================================================================

  openActionModal(invoice: any, action: 'approve' | 'reject' | 'rejectClose' | 'paid'): void {
    this.selectedInvoice  = invoice;
    this.pendingAction    = action;
    this.actionRemarks    = '';
    this.paymentReference = '';
    this.isActionModalOpen = true;
  }

  confirmAction(): void {
    if (!this.selectedInvoice || !this.pendingAction) return;

    if (['approve', 'reject', 'rejectClose'].includes(this.pendingAction)
        && !this.actionRemarks.trim()) {
      this.messageService.showMessage('warning', 'Required', 'Please enter remarks');
      return;
    }

    this.isPerformingAction = true;
    let action$: any;

    switch (this.pendingAction) {
      case 'approve':
        action$ = this.dataService.approveInvoice(this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break; 
      case 'reject':
        action$ = this.dataService.rejectInvoice(this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break;
      case 'rejectClose':
        action$ = this.dataService.rejectInvoicePermanent(this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break;
      case 'paid':
        action$ = this.dataService.markInvoicePaid(this.selectedInvoice.id, this.buyerName, this.paymentReference);
        break;
    }

    action$!.subscribe({
      next: () => {
        const msgs: Record<string, string> = {
          approve:     'Invoice approved — supplier has been notified',
          reject:      'Invoice returned to supplier for correction (1 resubmission allowed)',
          rejectClose: 'Invoice permanently closed — supplier cannot resubmit',
          paid:        'Invoice marked as paid — supplier notified'
        };
        this.messageService.showMessage('success', 'Success', msgs[this.pendingAction!] || 'Done');
        this.isActionModalOpen = false;
        this.isViewModalOpen   = false;
        this.isPerformingAction = false;
        this.loadInvoices();
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err.error?.message || 'Action failed');
        this.isPerformingAction = false;
      }
    });
  }

  // =========================================================================
  //  PREREQUISITE MODAL
  // =========================================================================

  handleApproveClick(invoice: any): void {
    if (invoice.canApprove === true) {
      this.openActionModal(invoice, 'approve');
      return;
    }

    this.prereqInvoice      = invoice;
    this.isCheckingPrereqs  = true;
    this.isPrereqModalOpen  = true;
    this.prereqChecks = { grnCreated: false, grnApproved: false, matchDone: false, matchPassed: false };

    this.dataService.getInvoiceById(invoice.id).subscribe({
      next: (response: any) => {
        const data = response?.success ? response.data : invoice;
        this.prereqInvoice = { ...data, currencyCode: data.currencyCode || data.currency || 'INR' };

        const matchStatus = data.threeWayMatchStatus;
        const matchPassed = data.threeWayMatchPassed === true;
        const matchExists = matchStatus != null && matchStatus !== 'null';

        this.prereqChecks = {
          grnCreated:  matchExists,
          grnApproved: matchExists,
          matchDone:   matchExists,
          matchPassed: matchPassed,
        };

        this.isCheckingPrereqs = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.prereqChecks      = { grnCreated: false, grnApproved: false, matchDone: false, matchPassed: false };
        this.isCheckingPrereqs = false;
        this.cdr.markForCheck();
      }
    });
  }

  closePrereqModal(): void {
    this.isPrereqModalOpen = false;
    this.prereqInvoice     = null;
  }

  proceedToApprove(): void {
    const inv = this.prereqInvoice;
    this.closePrereqModal();
    this.openActionModal(inv, 'approve');
  }

  get allPrereqsPassed(): boolean {
    return this.prereqChecks.grnCreated
        && this.prereqChecks.grnApproved
        && this.prereqChecks.matchDone
        && this.prereqChecks.matchPassed;
  }

  // =========================================================================
  //  PDF
  // =========================================================================

  downloadPDF(): void {
    if (!this.selectedInvoice) return;
    this.isDownloadingPDF = true;
    const el = document.getElementById('buyer-invoice-print');
    if (!el) { this.isDownloadingPDF = false; return; }

    html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#ffffff' }).then(canvas => {
      const pdf      = new jsPDF('p', 'mm', 'a4');
      const imgData  = canvas.toDataURL('image/png');
      const pdfWidth = 210;
      const imgH     = (canvas.height * pdfWidth) / canvas.width;
      let hLeft = imgH, pos = 0;
      pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgH);
      hLeft -= 297;
      while (hLeft > 0) {
        pos = hLeft - imgH; pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgH);
        hLeft -= 297;
      }
      pdf.save(`${this.selectedInvoice.invoiceNumber}.pdf`);
      this.isDownloadingPDF = false;
    }).catch(() => { this.isDownloadingPDF = false; });
  }

  // =========================================================================
  //  UTILITY
  // =========================================================================

  getInvoiceStatusLabel(status: string): string {
    const m: Record<string, string> = {
      DRAFT:           'Draft',
      SUBMITTED:       'Pending Review',
      APPROVED:        'Approved',
      REJECTED:        'Returned for Correction',
      REJECTED_CLOSED: 'Permanently Closed',
      PAID:            'Paid'
    };
    return m[status] || status;
  }

  getActionLabel(): string {
    const m: Record<string, string> = {
      approve:     'Approve Invoice',
      reject:      'Return for Correction (Supplier can resubmit once)',
      rejectClose: 'Reject & Close Permanently',
      paid:        'Mark as Paid'
    };
    return m[this.pendingAction!] || '';
  }

  getActionButtonColor(): string {
    const m: Record<string, string> = {
      approve:     'success',
      reject:      'warning',
      rejectClose: 'danger',
      paid:        'info'
    };
    return m[this.pendingAction!] || 'primary';
  }

  isOverdue(invoice: any): boolean {
    if (!invoice?.dueDate || invoice.status === 'PAID') return false;
    return new Date(invoice.dueDate) < new Date();
  }

  formatDate(d: string): string {
    if (!d) return 'N/A';
    try { return new Date(d).toLocaleDateString('en-GB'); } catch { return 'N/A'; }
  }

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
      'PKR': '₨', 'BDT': '৳',
    };
    return map[code] || code;
  }

  refresh(): void { this.loadInvoices(); }
}