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
// loggedInBuyer: any = null;
// buyerId: number | null = null;

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
//      private buyerService: BuyerService 
//   ) {}


//   private loadLoggedInBuyer(): void {
//   this.buyerId = Number(localStorage.getItem('buyerId'));
//   if (!this.buyerId || isNaN(this.buyerId)) return;

//   this.buyerService.getBuyerById(this.buyerId).subscribe({
//     next: (response: any) => {
//       const buyerDetails = response?.data || response;
//       if (!buyerDetails || !buyerDetails.id) return;

//       const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
//       const loggedInName  = localStorage.getItem('fullName');
//       const loggedInPhone = localStorage.getItem('phone');

//       let city = 'N/A', state = 'N/A';
//       const userLocationId = localStorage.getItem('locationId');
//       if (buyerDetails.locations && Array.isArray(buyerDetails.locations)) {
//         let selectedLoc = userLocationId
//           ? buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId))
//           : null;
//         if (!selectedLoc && buyerDetails.locations.length > 0) selectedLoc = buyerDetails.locations[0];
//         if (selectedLoc) { city = selectedLoc.city || 'N/A'; state = selectedLoc.state || 'N/A'; }
//       }

//       this.loggedInBuyer = {
//         companyName:        buyerDetails.companyName || 'N/A',
//         companyType:        buyerDetails.companyType || 'IT',
//         email:              loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
//         contactPersonName:  loggedInName  || buyerDetails.contactPersonName  || 'N/A',
//         contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
//         city, state
//       };
//       this.cdr.markForCheck();
//     },
//     error: () => {}
//   });
// }

// getInitials(name: string): string {
//   if (!name) return 'NA';
//   const parts = name.trim().split(' ');
//   return parts.length >= 2
//     ? (parts[0][0] + parts[1][0]).toUpperCase()
//     : name.substring(0, 2).toUpperCase();
// }

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
//         this.invoiceList = response?.success ? (response.data || []) : [];
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
//         this.selectedInvoice = response?.success ? response.data : invoice;
//         this.isViewModalOpen = true;
//         this.isLoading = false;
//       },
//       error: () => {
//         this.selectedInvoice = invoice;
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

//   /**
//    * Called when buyer clicks the Approve button.
//    * If canApprove is already confirmed by backend → open normal action modal.
//    * Otherwise → fetch latest invoice state and show prerequisite checklist dialog.
//    */
//   handleApproveClick(invoice: any): void {
//     if (invoice.canApprove === true) {
//       this.openActionModal(invoice, 'approve');
//       return;
//     }

//     // Show prereq dialog and fetch latest status from backend
//     this.prereqInvoice      = invoice;
//     this.isCheckingPrereqs  = true;
//     this.isPrereqModalOpen  = true;

//     // Reset checks to false while loading
//     this.prereqChecks = { grnCreated: false, grnApproved: false, matchDone: false, matchPassed: false };

//     this.dataService.getInvoiceById(invoice.id).subscribe({
//       next: (response: any) => {
//         const data = response?.success ? response.data : invoice;
//         this.prereqInvoice = data;

//         const matchStatus  = data.threeWayMatchStatus;
//         const matchPassed  = data.threeWayMatchPassed === true;

//         // If a 3-way match record exists at all → GRN was created + approved
//         const matchExists  = matchStatus != null && matchStatus !== 'null';

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
//         // Fallback: all false
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
//         pos = hLeft - imgH;
//         pdf.addPage();
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
//     if (!d) return 'N/SA';
//     try { return new Date(d).toLocaleDateString('en-GB'); } catch { return 'N/A'; }
//   }

//   formatCurrency(amount: number | null): string {
//     if (amount == null) return '₹0.00';
//     return '₹' + Number(amount).toLocaleString('en-IN', {
//       minimumFractionDigits: 2, maximumFractionDigits: 2
//     });
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
  invoiceList: any[] = [];
  filteredInvoices: any[] = [];
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

  // ==================== STATS ====================
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

  constructor(
    private dataService: DataService,
    private messageService: MessageService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private buyerService: BuyerService
  ) {}

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

  ngOnInit(): void {
    this.companyName = localStorage.getItem('companyName')
                    || localStorage.getItem('buyerCompanyName')
                    || localStorage.getItem('buyerName')
                    || '';

    this.buyerName = localStorage.getItem('buyerName')
                  || localStorage.getItem('fullName')
                  || localStorage.getItem('email')
                  || 'Buyer';

    if (!this.companyName.trim()) {
      console.error('[BuyerInvoices] companyName is empty — check localStorage keys');
    }

    this.loadInvoices();
    this.loadLoggedInBuyer();
  }

  goBack(): void { this.router.navigate(['/rfq-dashboard']); }

  // ==================== LOAD ====================

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
        // ✅ Normalize currency fields on every invoice
        this.invoiceList = raw.map((inv: any) => ({
          ...inv,
          currencyCode:   inv.currencyCode   || inv.currency || 'INR',
          currencySymbol: inv.currencySymbol || this.getSymbolForCode(inv.currencyCode || inv.currency || 'INR')
        }));
        this.calculateStats();
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

  calculateStats(): void {
    this.stats = {
      total:     this.invoiceList.length,
      submitted: this.invoiceList.filter(i => i.status === 'SUBMITTED').length,
      approved:  this.invoiceList.filter(i => i.status === 'APPROVED').length,
      rejected:  this.invoiceList.filter(i =>
        i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED').length,
      paid:      this.invoiceList.filter(i => i.status === 'PAID').length
    };
  }

  applyFilters(): void {
    let data = [...this.invoiceList];

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

  // ==================== VIEW INVOICE ====================

  viewInvoice(invoice: any): void {
    this.isLoading = true;
    this.dataService.getInvoiceById(invoice.id).subscribe({
      next: (response: any) => {
        const data = response?.success ? response.data : invoice;
        // ✅ Normalize currency on the fetched invoice too
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

  // ==================== ACTION MODAL ====================

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
        action$ = this.dataService.approveInvoice(
          this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break;
      case 'reject':
        action$ = this.dataService.rejectInvoice(
          this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break;
      case 'rejectClose':
        action$ = this.dataService.rejectInvoicePermanent(
          this.selectedInvoice.id, this.buyerName, this.actionRemarks);
        break;
      case 'paid':
        action$ = this.dataService.markInvoicePaid(
          this.selectedInvoice.id, this.buyerName, this.paymentReference);
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
        this.messageService.showMessage('success', 'Success',
          msgs[this.pendingAction!] || 'Done');
        this.isActionModalOpen = false;
        this.isViewModalOpen   = false;
        this.isPerformingAction = false;
        this.loadInvoices();
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error',
          err.error?.message || 'Action failed');
        this.isPerformingAction = false;
      }
    });
  }

  // ==================== PREREQUISITE MODAL ====================

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
        // ✅ Normalize currency here too
        this.prereqInvoice = {
          ...data,
          currencyCode: data.currencyCode || data.currency || 'INR'
        };

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

  // ==================== PDF ====================

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

  // ==================== UTILITY ====================

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

  /**
   * ✅ Format currency using the buyer's location currency code.
   * Reads from the invoice's currencyCode field (set from PO → buyer location).
   * Falls back to INR if not present.
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

  /** Lookup currency symbol from code */
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