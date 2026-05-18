


// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router } from '@angular/router';
// import { FormsModule } from '@angular/forms';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, FormModule,
//   TableModule, ModalModule, SpinnerComponent
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';
// import { AuthService } from '../../../shared/service/AuthService';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';

// import jsPDF from 'jspdf';
// import html2canvas from 'html2canvas';

// @Component({
//   selector: 'app-supplier-dashboard',
//   templateUrl: './supplier-dashboard.component.html',
//   styleUrls: ['./supplier-dashboard.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent, ButtonDirective,
//     IconDirective, BadgeComponent, FormModule, TableModule, ModalModule, SpinnerComponent
//   ]
// })
// export class SupplierDashboardComponent implements OnInit {

//   // ==================== USER DETAILS ====================
//   fullName: string = '';
//   email: string = '';
//   phone: string = '';
//   departmentName: string = '';
//   supplierName: string = '';
//   supplierId: number = 0;
//   companyName: string = '';
//   companyPhone: string = '';
//   city: string = '';
//   state: string = '';
//   userInitials: string = 'SU';

//   // ==================== TAB STATE ====================
//   activeTab: 'rfq' | 'po' | 'invoice' = 'rfq';

//   // ==================== STATISTICS ====================
//   statistics = { totalRFQs: 0, pendingRFQs: 0, respondedRFQs: 0, selectedRFQs: 0, rejectedRFQs: 0 };

//   // ==================== RFQ DATA ====================
//   rfqList: any[] = [];
//   filteredRFQList: any[] = [];
//   searchText: string = '';
//   statusFilter: string = 'ALL';
//   currentPage: number = 1;
//   pageSize: number = 10;
//   totalRFQs: number = 0;

//   // ==================== PO DATA ====================
//   poList: any[] = [];
//   filteredPOList: any[] = [];
//   poSearchText: string = '';
//   poStatusFilter: string = 'ALL';
//   poCurrentPage: number = 1;
//   poPageSize: number = 10;

//   // ==================== INVOICE DATA ====================
//   invoiceList: any[] = [];
//   filteredInvoiceList: any[] = [];
//   invoiceSearchText: string = '';
//   invoiceStatusFilter: string = 'ALL';
//   invoiceCurrentPage: number = 1;
//   invoicePageSize: number = 10;

//   // ✅ Currency — comes from PO's buyer location, NOT user-selectable
//   poLocationCurrencyCode: string = 'INR';
//   poLocationCurrencySymbol: string = '₹';

//   // ==================== INVOICE CREATION MODAL ====================
//   isInvoiceModalOpen: boolean = false;
//   selectedPOForInvoice: any = null;
//   isLoadingPODetails: boolean = false;
//   isCreatingInvoice: boolean = false;
//   isSubmittingInvoice: boolean = false;

//   invoiceForm = {
//     invoiceDate: this.getTodayStr(),
//     dueDate: this.getDueDateStr(30),
//     taxPercentage: 18,
//     paymentTerms: 'Net 30 days from invoice date',
//     notes: '',
//     termsAndConditions: 'Payment is due within 30 days of invoice date. Late payment will attract 2% per month interest.',
//     bankName: '',
//     accountHolderName: '',
//     accountNumber: '',
//     ifscCode: '',
//     branchName: '',
//     upiId: '',
//     overallDiscountAmount: 0,
//     poGrandTotal: 0,
//     lineItems: [] as any[]
//   };

//   // ==================== INVOICE VIEW MODAL ====================
//   isInvoiceViewModalOpen: boolean = false;
//   selectedInvoice: any = null;
//   isLoadingInvoice: boolean = false;
//   isDownloadingInvoicePDF: boolean = false;

//   // ==================== RESUBMIT MODAL ====================
//   isResubmitModalOpen: boolean = false;
//   selectedInvoiceForResubmit: any = null;
//   resubmitRemarks: string = '';
//   isResubmitting: boolean = false;
//   isEditInvoiceModalOpen: boolean = false;
//   editInvoiceMode: boolean = false;

//   editInvoiceForm: {
//     invoiceDate: string; dueDate: string; taxPercentage: number;
//     paymentTerms: string; notes: string; termsAndConditions: string;
//     bankName: string; accountHolderName: string; accountNumber: string;
//     ifscCode: string; branchName: string; upiId: string;
//     resubmitRemarks: string; overallDiscountAmount: number; lineItems: any[];
//   } = {
//     invoiceDate: '', dueDate: '', taxPercentage: 18, paymentTerms: '',
//     notes: '', termsAndConditions: '', bankName: '', accountHolderName: '',
//     accountNumber: '', ifscCode: '', branchName: '', upiId: '',
//     resubmitRemarks: '', overallDiscountAmount: 0, lineItems: []
//   };

//   // ==================== MODAL DATA (RFQ) ====================
//   selectedRFQ: any = null;
//   isViewModalOpen: boolean = false;
//   isQuoteModalOpen: boolean = false;
//   quoteForm = { quoteAmount: 0, notes: '' };

//   // ==================== LOADING STATES ====================
//   isLoading: boolean = false;
//   isLoadingStats: boolean = false;
//   isLoadingRFQs: boolean = false;
//   isLoadingPOs: boolean = false;
//   isLoadingInvoices: boolean = false;
//   isSubmittingQuote: boolean = false;
//   isDownloadingPDF: boolean = false;
//   errorMessage: string | null = null;

//   constructor(
//     private authService: AuthService,
//     private dataService: DataService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.loadSupplierUserData();
//     this.loadDashboardData();
//   }

//   // ==================== INIT ====================

//   loadSupplierUserData(): void {
//     this.fullName       = localStorage.getItem('fullName')    || 'Supplier User';
//     this.email          = localStorage.getItem('email')       || '';
//     this.phone          = localStorage.getItem('phone')       || '';
//     this.departmentName = localStorage.getItem('departmentName') || 'Supplier';
//     this.supplierName   = localStorage.getItem('supplierName') || '';
//     this.companyName    = this.supplierName;
// this.companyPhone = localStorage.getItem('companyPhone') || localStorage.getItem('phone') || '';
//     this.city           = localStorage.getItem('city')        || '';
//     this.state          = localStorage.getItem('state')       || '';
//     const supplierId  = this.authService.getSupplierId();
//     this.supplierId   = supplierId ? supplierId : Number(localStorage.getItem('supplierId') || '0');
//     this.userInitials = this.getInitials(this.fullName);
//   }

//   loadDashboardData(): void {
//     if (!this.supplierId) { this.errorMessage = 'Supplier ID not found. Please login again.'; return; }
//     this.loadStatistics();
//     this.loadRFQs();
//     this.loadPOs();
//     this.loadInvoices();
//   }

//   loadStatistics(): void {
//     this.isLoadingStats = true;
//     this.dataService.getSupplierDashboardStatistics(this.supplierId).subscribe({
//       next: (response: any) => {
//         if (response?.success && response.data) this.statistics = { ...this.statistics, ...response.data };
//         this.isLoadingStats = false;
//       },
//       error: () => { this.isLoadingStats = false; }
//     });
//   }

//   // ==================== RFQ TAB ====================

//   loadRFQs(): void {
//     this.isLoadingRFQs = true;
//     this.dataService.getSupplierRFQs(this.supplierId, this.statusFilter, this.searchText).subscribe({
//       next: (response: any) => {
//         // ✅ Normalize each RFQ to always have currencyCode/currencySymbol
//         const raw = response?.success ? (response.data || []) : (Array.isArray(response) ? response : []);
//         this.rfqList = raw.map((rfq: any) => ({
//           ...rfq,
//           currencyCode:   rfq.currencyCode   || 'INR',
//           currencySymbol: rfq.currencySymbol || '₹'
//         }));
//         this.totalRFQs = this.rfqList.length;
//         this.applyRFQFilters();
//         this.isLoadingRFQs = false;
//       },
//       error: () => { this.isLoadingRFQs = false; }
//     });
//   }

//   applyRFQFilters(): void {
//     let data = [...this.rfqList];
//     if (this.searchText.trim()) {
//       const s = this.searchText.toLowerCase();
//       data = data.filter(r => r.rfqNumber?.toLowerCase().includes(s) || r.rfqTitle?.toLowerCase().includes(s));
//     }
//     if (this.statusFilter && this.statusFilter !== 'ALL') {
//       data = data.filter(r => r.supplierStatus === this.statusFilter);
//     }
//     this.filteredRFQList = data;
//     this.totalRFQs = data.length;
//   }

//   // ==================== PO TAB ====================

//   loadPOs(): void {
//     this.isLoadingPOs = true;
//     this.dataService.getApprovedPOsForSupplier(this.supplierId).subscribe({
//       next: (response: any) => {
//         const raw = response?.success ? (response.data || []) : [];
//         // ✅ Normalize each PO to always have currencyCode/currencySymbol
//         this.poList = raw.map((po: any) => ({
//           ...po,
//           currencyCode:   po.currencyCode   || 'INR',
//           currencySymbol: po.currencySymbol || '₹'
//         }));
//         this.applyPOFilters();
//         this.isLoadingPOs = false;
//       },
//       error: () => { this.poList = []; this.filteredPOList = []; this.isLoadingPOs = false; }
//     });
//   }

//   applyPOFilters(): void {
//     let data = [...this.poList];
//     if (this.poSearchText.trim()) {
//       const s = this.poSearchText.toLowerCase();
//       data = data.filter(p =>
//         p.poNumber?.toLowerCase().includes(s) ||
//         p.rfqNumber?.toLowerCase().includes(s) ||
//         p.buyerCompanyName?.toLowerCase().includes(s)
//       );
//     }
//     if (this.poStatusFilter && this.poStatusFilter !== 'ALL') {
//       data = data.filter(p => p.invoiceStatus === this.poStatusFilter);
//     }
//     this.filteredPOList = data;
//   }

//   // ==================== INVOICE TAB ====================

//   loadInvoices(): void {
//     this.isLoadingInvoices = true;
//     this.dataService.getSupplierInvoices(this.supplierId).subscribe({
//       next: (response: any) => {
//         // ✅ Normalize invoices to always have currencyCode
//         const raw = response?.success ? (response.data || []) : [];
//         this.invoiceList = raw.map((inv: any) => ({
//           ...inv,
//           currencyCode:   inv.currencyCode   || inv.currency || 'INR',
//           currencySymbol: inv.currencySymbol || '₹'
//         }));
//         this.applyInvoiceFilters();
//         this.isLoadingInvoices = false;
//       },
//       error: () => { this.invoiceList = []; this.filteredInvoiceList = []; this.isLoadingInvoices = false; }
//     });
//   }

//   applyInvoiceFilters(): void {
//     let data = [...this.invoiceList];
//     if (this.invoiceSearchText.trim()) {
//       const s = this.invoiceSearchText.toLowerCase();
//       data = data.filter(i =>
//         i.invoiceNumber?.toLowerCase().includes(s) ||
//         i.poNumber?.toLowerCase().includes(s) ||
//         i.rfqNumber?.toLowerCase().includes(s)
//       );
//     }
//     if (this.invoiceStatusFilter !== 'ALL') {
//       if (this.invoiceStatusFilter === 'REJECTED') {
//         data = data.filter(i => i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED');
//       } else {
//         data = data.filter(i => i.status === this.invoiceStatusFilter);
//       }
//     }
//     this.filteredInvoiceList = data;
//   }

//   // ==================== CREATE INVOICE FLOW ====================

//   openCreateInvoiceModal(po: any): void {
//     this.isLoadingPODetails = true;
//     this.selectedPOForInvoice = po;
//     this.isInvoiceModalOpen = true;

//     // ✅ Pre-set currency from PO list (already normalised above)
//     this.poLocationCurrencyCode   = po.currencyCode   || 'INR';
//     this.poLocationCurrencySymbol = po.currencySymbol || '₹';

//     this.invoiceForm = {
//       invoiceDate: this.getTodayStr(),
//       dueDate: this.getDueDateStr(30),
//       taxPercentage: 18,
//       paymentTerms: 'Net 30 days from invoice date',
//       notes: '',
//       termsAndConditions: 'Payment is due within 30 days of invoice date. Late payment will attract 2% per month interest.',
//       bankName:           localStorage.getItem('bankName')          || '',
//       accountHolderName:  localStorage.getItem('accountHolderName') || this.supplierName,
//       accountNumber:      localStorage.getItem('accountNumber')     || '',
//       ifscCode:           localStorage.getItem('ifscCode')          || '',
//       branchName:         localStorage.getItem('branchName')        || '',
//       upiId:              localStorage.getItem('upiId')             || '',
//       overallDiscountAmount: 0,
//       poGrandTotal: 0,
//       lineItems: []
//     };

//     this.dataService.getPODetailsForInvoice(this.supplierId, po.id).subscribe({
//       next: (response: any) => {
//         if (response?.success && response.data) {
//           const poDetails = response.data;
//           this.invoiceForm.overallDiscountAmount = Number(poDetails.overallDiscountAmount || 0);
//           this.invoiceForm.poGrandTotal          = Number(poDetails.grandTotal || 0);

//           // ✅ Override currency from PO details (most accurate source)
//           if (poDetails.currencyCode) {
//             this.poLocationCurrencyCode   = poDetails.currencyCode;
//             this.poLocationCurrencySymbol = poDetails.currencySymbol || this.getSymbolForCode(poDetails.currencyCode);
//           }

//           this.invoiceForm.lineItems = (poDetails.lineItems || []).map((item: any) => {
//             const poQty        = Number(item.quantity)    || 0;
//             const invoicedQty  = Number(item.invoicedQty || item.alreadyInvoicedQty || 0);
//             const remainingQty = Math.max(0, poQty - invoicedQty);
//             return {
//               id:                      item.id,
//               itemCode:                item.itemCode || '',
//               itemDescription:         item.itemDescription || item.description || '',
//               itemDescriptionDetailed: item.itemDescriptionDetailed || '',
//               hsnSacCode:              item.hsnSacCode || '',
//               uom:                     item.uom || 'PCS',
//               poQuantity:              poQty,
//               alreadyInvoicedQty:      invoicedQty,
//               remainingQty:            remainingQty,
//               qtyToInvoice:            remainingQty,
//               unitPrice:               Number(item.unitPrice || item.rate || 0),
//               discountPercentage:      Number(item.discountPercentage) || 0,
//               taxPercentage:           Number(item.taxPercentage) ?? 18,
//             };
//           });

//           this.selectedPOForInvoice = { ...po, ...poDetails };
//         }
//         this.isLoadingPODetails = false;
//       },
//       error: () => { this.invoiceForm.lineItems = []; this.isLoadingPODetails = false; }
//     });
//   }

//   // ✅ Symbol lookup helper
//   private getSymbolForCode(code: string): string {
//     const map: Record<string, string> = {
//       'INR': '₹', 'USD': '$', 'EUR': '€', 'GBP': '£',
//       'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
//       'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$', 'NZD': 'NZ$',
//       'SAR': 'ر.س', 'QAR': 'ر.ق', 'KWD': 'د.ك', 'BHD': '.د.ب',
//       'OMR': 'ر.ع.', 'MYR': 'RM', 'THB': '฿', 'IDR': 'Rp',
//       'PKR': '₨', 'BDT': '৳', 'LKR': '₨', 'NPR': '₨',
//     };
//     return map[code] || code;
//   }

//   addInvoiceLineItem(): void {
//     this.invoiceForm.lineItems.push({
//       itemCode: '', itemDescription: '', uom: 'PCS',
//       poQuantity: 0, alreadyInvoicedQty: 0, remainingQty: 0,
//       qtyToInvoice: 1, unitPrice: 0, discountPercentage: 0,
//       taxPercentage: 18, hsnSacCode: ''
//     });
//   }

//   removeInvoiceLineItem(index: number): void { this.invoiceForm.lineItems.splice(index, 1); }

//   getLineTotal(item: any): number {
//     const base      = (Number(item.qtyToInvoice) || 0) * (Number(item.unitPrice) || 0);
//     const afterDisc = base - (base * (Number(item.discountPercentage) || 0) / 100);
//     return afterDisc + (afterDisc * (Number(item.taxPercentage) || 0) / 100);
//   }

//   getInvoiceSubtotal(): number {
//     return this.invoiceForm.lineItems.reduce((sum, item) => {
//       const base = (Number(item.qtyToInvoice) || 0) * (Number(item.unitPrice) || 0);
//       return sum + base - (base * (Number(item.discountPercentage) || 0) / 100);
//     }, 0);
//   }

//   getInvoiceTaxTotal(): number {
//     return this.invoiceForm.lineItems.reduce((sum, item) => {
//       const base      = (Number(item.qtyToInvoice) || 0) * (Number(item.unitPrice) || 0);
//       const afterDisc = base - (base * (Number(item.discountPercentage) || 0) / 100);
//       return sum + (afterDisc * (Number(item.taxPercentage) || 0) / 100);
//     }, 0);
//   }

//   getInvoiceGrandTotal(): number {
//     if (!this.invoiceForm.lineItems || this.invoiceForm.lineItems.length === 0)
//       return Number(this.invoiceForm.poGrandTotal) || 0;
//     const isFullInvoice = this.invoiceForm.lineItems.every(
//       (item: any) => Number(item.qtyToInvoice) >= Number(item.remainingQty)
//     );
//     if (isFullInvoice && this.invoiceForm.poGrandTotal > 0)
//       return Number(this.invoiceForm.poGrandTotal);
//     return this.getInvoiceSubtotal() + this.getInvoiceTaxTotal()
//            - (Number(this.invoiceForm.overallDiscountAmount) || 0);
//   }

//   saveInvoiceDraft(): void {
//     if (!this.validateInvoiceForm()) return;
//     this.isCreatingInvoice = true;
//     this.dataService.createInvoice(this.supplierId, this.selectedPOForInvoice.id,
//       this.buildInvoicePayload()).subscribe({
//       next: (response: any) => {
//         if (response?.success) {
//           this.messageService.showMessage('success', 'Saved',
//             `Invoice ${response.data?.invoiceNumber} saved as DRAFT`);
//           this.isInvoiceModalOpen = false;
//           this.loadInvoices(); this.loadPOs();
//         }
//         this.isCreatingInvoice = false;
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to create invoice');
//         this.isCreatingInvoice = false;
//       }
//     });
//   }

//   createAndSubmitInvoice(): void {
//     if (!this.validateInvoiceForm(true)) return;
//     this.isSubmittingInvoice = true;
//     this.dataService.createInvoice(this.supplierId, this.selectedPOForInvoice.id,
//       this.buildInvoicePayload()).subscribe({
//       next: (createResp: any) => {
//         if (createResp?.success) {
//           this.dataService.submitInvoice(createResp.data.id, this.supplierId).subscribe({
//             next: () => {
//               this.messageService.showMessage('success', 'Invoice Submitted',
//                 `Invoice ${createResp.data.invoiceNumber} sent to buyer`);
//               this.isInvoiceModalOpen = false;
//               this.loadInvoices(); this.loadPOs();
//               this.isSubmittingInvoice = false;
//             },
//             error: () => {
//               this.messageService.showMessage('warning', 'Created but not submitted',
//                 'Invoice saved as draft. Please submit manually.');
//               this.isInvoiceModalOpen = false;
//               this.loadInvoices();
//               this.isSubmittingInvoice = false;
//             }
//           });
//         }
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to create invoice');
//         this.isSubmittingInvoice = false;
//       }
//     });
//   }

//   submitExistingInvoice(invoice: any): void {
//     this.dataService.submitInvoice(invoice.id, this.supplierId).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Submitted', 'Invoice sent to buyer');
//         this.loadInvoices(); this.loadPOs();
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to submit invoice');
//       }
//     });
//   }

//   // ==================== RESUBMIT INVOICE ====================

//   openResubmitModal(invoice: any): void {
//     this.isLoadingInvoice = true;
//     this.dataService.getInvoiceById(invoice.id).subscribe({
//       next: (response: any) => {
//         const fullInvoice = response?.success ? response.data : invoice;
//         this.selectedInvoiceForResubmit = fullInvoice;
//         this._populateEditFormFromInvoice(fullInvoice);
//         this.isResubmitModalOpen = true;
//         this.isLoadingInvoice = false;
//       },
//       error: () => {
//         this.selectedInvoiceForResubmit = invoice;
//         this._populateEditFormFromInvoice(invoice);
//         this.isResubmitModalOpen = true;
//         this.isLoadingInvoice = false;
//       }
//     });
//   }

//   private _populateEditFormFromInvoice(inv: any): void {
//     // ✅ Set currency from the existing invoice
//     if (inv.currencyCode || inv.currency) {
//       this.poLocationCurrencyCode   = inv.currencyCode || inv.currency || 'INR';
//       this.poLocationCurrencySymbol = inv.currencySymbol || this.getSymbolForCode(this.poLocationCurrencyCode);
//     }
//     this.editInvoiceForm = {
//       invoiceDate:           inv.invoiceDate ? inv.invoiceDate.split('T')[0] : this.getTodayStr(),
//       dueDate:               inv.dueDate     ? inv.dueDate.split('T')[0]     : this.getDueDateStr(30),
//       taxPercentage:         inv.taxPercentage ?? 18,
//       paymentTerms:          inv.paymentTerms || 'Net 30 days from invoice date',
//       notes:                 inv.notes || '',
//       termsAndConditions:    inv.termsAndConditions || '',
//       bankName:              inv.bankName || '',
//       accountHolderName:     inv.accountHolderName || this.supplierName,
//       accountNumber:         inv.accountNumber || '',
//       ifscCode:              inv.ifscCode || '',
//       branchName:            inv.branchName || '',
//       upiId:                 inv.upiId || '',
//       resubmitRemarks:       '',
//       overallDiscountAmount: Number(inv.overallDiscountAmount || 0),
//       lineItems: (inv.lineItems || inv.items || []).map((item: any) => ({
//         id:                 item.id,
//         itemCode:           item.itemCode || '',
//         itemDescription:    item.itemDescription || item.description || '',
//         hsnSacCode:         item.hsnSacCode || '',
//         uom:                item.uom || 'PCS',
//         quantity:           item.quantity || 1,
//         unitPrice:          item.unitPrice || item.rate || 0,
//         discountPercentage: item.discountPercentage || 0,
//         taxPercentage:      item.taxPercentage ?? 18
//       }))
//     };
//     this.resubmitRemarks = '';
//   }

//   closeResubmitModal(): void {
//     this.isResubmitModalOpen = false;
//     this.selectedInvoiceForResubmit = null;
//     this.resubmitRemarks = '';
//   }

//   addEditInvoiceLineItem(): void {
//     this.editInvoiceForm.lineItems.push({
//       itemCode: '', itemDescription: '', hsnSacCode: '', uom: 'PCS',
//       quantity: 1, unitPrice: 0, discountPercentage: 0, taxPercentage: 18
//     });
//   }

//   removeEditInvoiceLineItem(index: number): void { this.editInvoiceForm.lineItems.splice(index, 1); }

//   getEditLineTotal(item: any): number {
//     const base      = (Number(item.quantity) || 0) * (Number(item.unitPrice) || 0);
//     const afterDisc = base - (base * (Number(item.discountPercentage) || 0) / 100);
//     return afterDisc + (afterDisc * (Number(item.taxPercentage) || 0) / 100);
//   }

//   getEditInvoiceSubtotal(): number {
//     return this.editInvoiceForm.lineItems.reduce((sum, item) => {
//       const base = (Number(item.quantity) || 0) * (Number(item.unitPrice) || 0);
//       return sum + base - (base * (Number(item.discountPercentage) || 0) / 100);
//     }, 0);
//   }

//   getEditInvoiceTaxTotal(): number {
//     return this.editInvoiceForm.lineItems.reduce((sum, item) => {
//       const base      = (Number(item.quantity) || 0) * (Number(item.unitPrice) || 0);
//       const afterDisc = base - (base * (Number(item.discountPercentage) || 0) / 100);
//       return sum + (afterDisc * (Number(item.taxPercentage) || 0) / 100);
//     }, 0);
//   }

//   getEditInvoiceGrandTotal(): number {
//     return this.getEditInvoiceSubtotal() + this.getEditInvoiceTaxTotal()
//            - (Number(this.editInvoiceForm.overallDiscountAmount) || 0);
//   }

//   confirmResubmit(): void {
//     const remarks = this.editInvoiceForm.resubmitRemarks?.trim() || this.resubmitRemarks?.trim();
//     if (!remarks) {
//       this.messageService.showMessage('warning', 'Remarks Required', 'Please describe what you corrected');
//       return;
//     }
//     if (this.editInvoiceForm.lineItems.length === 0) {
//       this.messageService.showMessage('warning', 'Validation', 'Please add at least one line item');
//       return;
//     }
//     if (!this.selectedInvoiceForResubmit) return;

//     this.isResubmitting = true;

//     const updatePayload = {
//       invoiceDate:           this.editInvoiceForm.invoiceDate,
//       dueDate:               this.editInvoiceForm.dueDate,
//       taxPercentage:         this.editInvoiceForm.taxPercentage,
//       paymentTerms:          this.editInvoiceForm.paymentTerms,
//       notes:                 this.editInvoiceForm.notes,
//       termsAndConditions:    this.editInvoiceForm.termsAndConditions,
//       bankName:              this.editInvoiceForm.bankName,
//       accountHolderName:     this.editInvoiceForm.accountHolderName,
//       accountNumber:         this.editInvoiceForm.accountNumber,
//       ifscCode:              this.editInvoiceForm.ifscCode,
//       branchName:            this.editInvoiceForm.branchName,
//       upiId:                 this.editInvoiceForm.upiId,
//       overallDiscountAmount: this.editInvoiceForm.overallDiscountAmount || 0,
//       lineItems:             this.editInvoiceForm.lineItems
//     };

//     this.dataService.updateInvoice(
//       this.selectedInvoiceForResubmit.id, this.supplierId, updatePayload
//     ).subscribe({
//       next: () => {
//         this.dataService.resubmitInvoice(
//           this.selectedInvoiceForResubmit.id, this.supplierId, remarks
//         ).subscribe({
//           next: () => {
//             this.messageService.showMessage('success', 'Invoice Resubmitted',
//               `Invoice ${this.selectedInvoiceForResubmit.invoiceNumber} sent back to buyer`);
//             this.isResubmitModalOpen = false; this.isInvoiceViewModalOpen = false;
//             this.selectedInvoiceForResubmit = null; this.resubmitRemarks = '';
//             this.isResubmitting = false;
//             this.loadInvoices(); this.loadPOs();
//           },
//           error: (err: any) => {
//             this.messageService.showMessage('error', 'Resubmit Failed', err.error?.message || 'Could not resubmit.');
//             this.isResubmitting = false;
//           }
//         });
//       },
//       error: () => {
//         this.dataService.resubmitInvoice(
//           this.selectedInvoiceForResubmit.id, this.supplierId, remarks
//         ).subscribe({
//           next: () => {
//             this.messageService.showMessage('success', 'Invoice Resubmitted',
//               `Invoice ${this.selectedInvoiceForResubmit.invoiceNumber} sent back to buyer`);
//             this.isResubmitModalOpen = false; this.isInvoiceViewModalOpen = false;
//             this.selectedInvoiceForResubmit = null; this.resubmitRemarks = '';
//             this.isResubmitting = false;
//             this.loadInvoices(); this.loadPOs();
//           },
//           error: (err2: any) => {
//             this.messageService.showMessage('error', 'Resubmit Failed', err2.error?.message || 'Could not resubmit invoice.');
//             this.isResubmitting = false;
//           }
//         });
//       }
//     });
//   }

//   canResubmit(invoice: any): boolean {
//     if (!invoice) return false;
//     return invoice.status === 'REJECTED' &&
//       (invoice.canResubmit === true || invoice.resubmitCount === 0 || invoice.resubmitCount == null);
//   }

//   isPermanentlyClosed(invoice: any): boolean {
//     return invoice?.status === 'REJECTED_CLOSED';
//   }

//   // ==================== VIEW INVOICE ====================

//   viewInvoice(invoice: any): void {
//     this.isLoadingInvoice = true;
//     this.isInvoiceViewModalOpen = true;
//     this.dataService.getInvoiceById(invoice.id).subscribe({
//       next: (response: any) => {
//         this.selectedInvoice = response?.success ? response.data : invoice;
//         this.isLoadingInvoice = false;
//       },
//       error: () => { this.selectedInvoice = invoice; this.isLoadingInvoice = false; }
//     });
//   }

//   closeInvoiceViewModal(): void { this.isInvoiceViewModalOpen = false; this.selectedInvoice = null; }

//   // ==================== DOWNLOAD PDF ====================

//   downloadInvoicePDF(): void {
//     if (!this.selectedInvoice) return;
//     this.isDownloadingInvoicePDF = true;
//     const element = document.getElementById('invoice-print-content');
//     if (!element) { this.isDownloadingInvoicePDF = false; return; }

//     html2canvas(element, { scale: 2, useCORS: true, backgroundColor: '#ffffff' }).then(canvas => {
//       const pdf = new jsPDF('p', 'mm', 'a4');
//       const imgData = canvas.toDataURL('image/png');
//       const pdfWidth = 210;
//       const imgHeight = (canvas.height * pdfWidth) / canvas.width;
//       let heightLeft = imgHeight, pos = 0;
//       pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgHeight);
//       heightLeft -= 297;
//       while (heightLeft > 0) {
//         pos = heightLeft - imgHeight; pdf.addPage();
//         pdf.addImage(imgData, 'PNG', 0, pos, pdfWidth, imgHeight);
//         heightLeft -= 297;
//       }
//       pdf.save(`${this.selectedInvoice.invoiceNumber}.pdf`);
//       this.isDownloadingInvoicePDF = false;
//     }).catch(() => { this.isDownloadingInvoicePDF = false; });
//   }

//   // ==================== VALIDATION ====================

//   private validateInvoiceForm(requireBankDetails = false): boolean {
//     if (this.invoiceForm.lineItems.length === 0) {
//       this.messageService.showMessage('warning', 'Validation', 'Please add at least one line item'); return false;
//     }
//     const today = this.getTodayStr();
//     if (this.invoiceForm.invoiceDate < today) {
//       this.messageService.showMessage('warning', 'Invalid Date', 'Invoice date cannot be in the past'); return false;
//     }
//     if (this.invoiceForm.dueDate < today) {
//       this.messageService.showMessage('warning', 'Invalid Date', 'Payment due date cannot be in the past'); return false;
//     }
//     for (const item of this.invoiceForm.lineItems) {
//       if (!item.itemDescription) {
//         this.messageService.showMessage('warning', 'Validation', 'Please fill item description for all line items');
//         return false;
//       }
//       if (Number(item.qtyToInvoice) <= 0) {
//         this.messageService.showMessage('warning', 'Validation',
//           `Qty to Invoice must be greater than 0 for "${item.itemDescription}"`); return false;
//       }
//       if (item.remainingQty > 0 && Number(item.qtyToInvoice) > Number(item.remainingQty)) {
//         this.messageService.showMessage('warning', 'Quantity Exceeded',
//           `"${item.itemDescription}": Qty to Invoice exceeds remaining PO qty`); return false;
//       }
//     }
//     if (requireBankDetails &&
//         (!this.invoiceForm.bankName || !this.invoiceForm.accountNumber || !this.invoiceForm.ifscCode)) {
//       this.messageService.showMessage('warning', 'Bank Details Required',
//         'Please enter bank name, account number and IFSC to submit invoice'); return false;
//     }
//     return true;
//   }

//   private buildInvoicePayload(): any {
//     return {
//       invoiceDate: this.invoiceForm.invoiceDate, dueDate: this.invoiceForm.dueDate,
//       taxPercentage: this.invoiceForm.taxPercentage, paymentTerms: this.invoiceForm.paymentTerms,
//       notes: this.invoiceForm.notes, termsAndConditions: this.invoiceForm.termsAndConditions,
//       bankName: this.invoiceForm.bankName, accountHolderName: this.invoiceForm.accountHolderName,
//       accountNumber: this.invoiceForm.accountNumber, ifscCode: this.invoiceForm.ifscCode,
//       branchName: this.invoiceForm.branchName, upiId: this.invoiceForm.upiId,
//       overallDiscountAmount: this.invoiceForm.overallDiscountAmount || 0,
//       lineItems: this.invoiceForm.lineItems.map(item => ({
//         id: item.id, itemCode: item.itemCode, itemDescription: item.itemDescription,
//         itemDescriptionDetailed: item.itemDescriptionDetailed || '',
//         hsnSacCode: item.hsnSacCode, uom: item.uom, quantity: item.qtyToInvoice,
//         unitPrice: item.unitPrice, discountPercentage: item.discountPercentage,
//         taxPercentage: item.taxPercentage
//       }))
//     };
//   }

//   // ==================== TAB SWITCHING ====================
//   switchTab(tab: 'rfq' | 'po' | 'invoice'): void { this.activeTab = tab; }

//   // ==================== RFQ METHODS ====================

//   viewRFQDetails(rfq: any): void {
//     const rfqId = rfq.rfqId || rfq.id;
//     if (!rfqId) { this.messageService.showMessage('error', 'Error', 'RFQ ID not found'); return; }
//     this.isLoading = true;
//     this.dataService.getSupplierRFQDetails(this.supplierId, rfqId).subscribe({
//       next: (response: any) => {
//         if (response?.success && response.data) { this.selectedRFQ = response.data; this.isViewModalOpen = true; }
//         else this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
//         this.isLoading = false;
//       },
//       error: () => { this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details'); this.isLoading = false; }
//     });
//   }

//   closeViewModal(): void { this.isViewModalOpen = false; this.selectedRFQ = null; }

//   navigateToQuoteSubmission(rfq: any): void {
//     if (rfq.supplierStatus === 'RESPONDED') {
//       this.messageService.showMessage('info', 'Already Submitted', 'Contact the buyer to make changes.'); return;
//     }
//     this.router.navigate(['/supplier-quote', rfq.rfqId || rfq.id]);
//   }

//   viewSubmittedQuote(rfq: any): void {
//     this.router.navigate(['/supplier-quote', rfq.rfqId || rfq.id], { queryParams: { viewOnly: true } });
//   }

//   canSubmitQuote(rfq: any): boolean { return rfq.supplierStatus === 'PENDING' || rfq.supplierStatus === 'SENT'; }
//   hasSubmittedQuote(rfq: any): boolean { return rfq.supplierStatus === 'RESPONDED'; }
//   openQuoteModal(rfq: any): void {}
//   submitQuote(): void {}
//   closeQuoteModal(): void { this.isQuoteModalOpen = false; }
//   downloadRFQPDF(): void {}
//   downloadAttachment(att: any): void { window.open(`http://localhost:8080/leadcapture${att.downloadUrl}`, '_blank'); }

//   // ==================== PAGINATION ====================

//   get paginatedRFQs(): any[] {
//     return this.filteredRFQList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize);
//   }
//   get totalPages(): number { return Math.ceil(this.totalRFQs / this.pageSize); }
//   nextPage(): void { if (this.currentPage < this.totalPages) this.currentPage++; }
//   previousPage(): void { if (this.currentPage > 1) this.currentPage--; }

//   get paginatedPOs(): any[] {
//     return this.filteredPOList.slice((this.poCurrentPage - 1) * this.poPageSize, this.poCurrentPage * this.poPageSize);
//   }
//   get totalPOPages(): number { return Math.ceil(this.filteredPOList.length / this.poPageSize); }

//   get paginatedInvoices(): any[] {
//     return this.filteredInvoiceList.slice(
//       (this.invoiceCurrentPage - 1) * this.invoicePageSize, this.invoiceCurrentPage * this.invoicePageSize);
//   }
//   get totalInvoicePages(): number { return Math.ceil(this.filteredInvoiceList.length / this.invoicePageSize); }

//   // ==================== UTILITY ====================

//   refresh(): void {
//     this.currentPage = 1; this.poCurrentPage = 1; this.invoiceCurrentPage = 1;
//     this.loadDashboardData();
//   }

//   onSearchChange(): void { this.currentPage = 1; this.applyRFQFilters(); }

//   getInitials(name: string): string {
//     if (!name?.trim()) return 'SU';
//     const parts = name.trim().split(' ');
//     return parts.length === 1
//       ? parts[0].substring(0, 2).toUpperCase()
//       : (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
//   }

//   getStatusBadgeClass(status: string): string {
//     const m: any = { PENDING: 'warning', SENT: 'info', RESPONDED: 'success', SELECTED: 'success', REJECTED: 'danger' };
//     return m[status] || 'secondary';
//   }

//   getInvoiceStatusClass(status: string): string {
//     const m: any = { DRAFT: 'secondary', SUBMITTED: 'primary', APPROVED: 'success', PAID: 'info',
//                      REJECTED: 'warning', REJECTED_CLOSED: 'danger' };
//     return m[status] || 'secondary';
//   }

//   getInvoiceStatusLabel(status: string): string {
//     const m: any = { DRAFT: 'Draft', SUBMITTED: 'Submitted', APPROVED: 'Approved',
//                      PAID: 'Paid', REJECTED: 'Rejected', REJECTED_CLOSED: 'Closed' };
//     return m[status] || status;
//   }

//   formatDate(dateString: string): string {
//     if (!dateString) return 'N/A';
//     try { return new Date(dateString).toLocaleDateString('en-GB'); } catch { return 'N/A'; }
//   }

//   /**
//    * ✅ Format currency using the code from each PO/invoice/RFQ object.
//    * Accepts optional currencyCode for dynamic display.
//    * Used in: PO tab (po.currencyCode), Invoice tab (inv.currencyCode), RFQ tab (rfq.currencyCode)
//    */
//   formatCurrency(amount: number | null, currencyCode?: string): string {
//     const code   = currencyCode || 'INR';
//     const symbol = this.getSymbolForCode(code);
//     const val    = Number(amount ?? 0);
//     const formatted = val.toLocaleString('en-IN', {
//       minimumFractionDigits: 2, maximumFractionDigits: 2
//     });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
//   }

//   /** Convenience: format using current PO/invoice modal currency */
//   formatInvoiceCurrency(amount: number | null): string {
//     return this.formatCurrency(amount, this.poLocationCurrencyCode);
//   }

//   formatFileSize(bytes: number): string {
//     if (!bytes) return '0 B';
//     const k = 1024, sizes = ['B', 'KB', 'MB', 'GB'];
//     const i = Math.floor(Math.log(bytes) / Math.log(k));
//     return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
//   }

//   objectKeys(obj: any): string[] { return obj ? Object.keys(obj) : []; }

//   getTodayStr(): string { return new Date().toISOString().split('T')[0]; }

//   getDueDateStr(days: number): string {
//     const d = new Date();
//     d.setDate(d.getDate() + days);
//     return d.toISOString().split('T')[0];
//   }

//   navigateTo(route: string): void { this.router.navigate([route]); }

//   isOverdue(invoice: any): boolean {
//     if (!invoice?.dueDate || invoice.status === 'PAID') return false;
//     return new Date(invoice.dueDate) < new Date();
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, FormModule,
  TableModule, ModalModule, SpinnerComponent
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { AuthService } from '../../../shared/service/AuthService';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-supplier-dashboard',
  templateUrl: './supplier-dashboard.component.html',
  styleUrls: ['./supplier-dashboard.component.css'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent, ButtonDirective,
    IconDirective, BadgeComponent, FormModule, TableModule, ModalModule, SpinnerComponent
  ]
})
export class SupplierDashboardComponent implements OnInit {

  // ── User profile ──────────────────────────────────────────────────────────
  fullName      : string = '';
  email         : string = '';
  phone         : string = '';
  departmentName: string = '';
  supplierName  : string = '';
  supplierId    : number = 0;
  companyName   : string = '';
  companyPhone  : string = '';
  city          : string = '';
  state         : string = '';
  userInitials  : string = 'SU';

  // ── Tab state ─────────────────────────────────────────────────────────────
  activeTab: 'rfq' | 'po' | 'invoice' = 'rfq';

  // ── Statistics ────────────────────────────────────────────────────────────
  // expiredRFQs is new — shows how many RFQs the supplier missed
  statistics = {
    totalRFQs    : 0,
    pendingRFQs  : 0,
    respondedRFQs: 0,
    selectedRFQs : 0,
    rejectedRFQs : 0,
    expiredRFQs  : 0
  };

  // ── RFQ list ──────────────────────────────────────────────────────────────
  rfqList        : any[] = [];
  filteredRFQList: any[] = [];
  searchText     : string = '';
  // EXPIRED is a virtual status filter — mapped to the isExpired flag
  statusFilter   : string = 'ALL';
  currentPage    : number = 1;
  pageSize       : number = 10;
  totalRFQs      : number = 0;

  // ── PO list ───────────────────────────────────────────────────────────────
  poList        : any[] = [];
  filteredPOList: any[] = [];
  poSearchText  : string = '';
  poStatusFilter: string = 'ALL';
  poCurrentPage : number = 1;
  poPageSize    : number = 10;

  // ── Invoice list ──────────────────────────────────────────────────────────
  invoiceList         : any[] = [];
  filteredInvoiceList : any[] = [];
  invoiceSearchText   : string = '';
  invoiceStatusFilter : string = 'ALL';
  invoiceCurrentPage  : number = 1;
  invoicePageSize     : number = 10;

  // ── Currency (comes from PO/RFQ location, not user-selectable) ───────────
  poLocationCurrencyCode  : string = 'INR';
  poLocationCurrencySymbol: string = '₹';

  // ── Invoice creation modal ────────────────────────────────────────────────
  isInvoiceModalOpen   : boolean = false;
  selectedPOForInvoice : any    = null;
  isLoadingPODetails   : boolean = false;
  isCreatingInvoice    : boolean = false;
  isSubmittingInvoice  : boolean = false;

  invoiceForm = {
    invoiceDate          : this.getTodayStr(),
    dueDate              : this.getDueDateStr(30),
    taxPercentage        : 18,
    paymentTerms         : 'Net 30 days from invoice date',
    notes                : '',
    termsAndConditions   : 'Payment is due within 30 days of invoice date. Late payment will attract 2% per month interest.',
    bankName             : '',
    accountHolderName    : '',
    accountNumber        : '',
    ifscCode             : '',
    branchName           : '',
    upiId                : '',
    overallDiscountAmount: 0,
    poGrandTotal         : 0,
    lineItems            : [] as any[]
  };

  // ── Invoice view modal ────────────────────────────────────────────────────
  isInvoiceViewModalOpen: boolean = false;
  selectedInvoice       : any    = null;
  isLoadingInvoice      : boolean = false;
  isDownloadingInvoicePDF: boolean = false;

  // ── Resubmit modal ────────────────────────────────────────────────────────
  isResubmitModalOpen       : boolean = false;
  selectedInvoiceForResubmit: any    = null;
  resubmitRemarks           : string = '';
  isResubmitting            : boolean = false;
  isEditInvoiceModalOpen    : boolean = false;
  editInvoiceMode           : boolean = false;

  editInvoiceForm: {
    invoiceDate: string; dueDate: string; taxPercentage: number;
    paymentTerms: string; notes: string; termsAndConditions: string;
    bankName: string; accountHolderName: string; accountNumber: string;
    ifscCode: string; branchName: string; upiId: string;
    resubmitRemarks: string; overallDiscountAmount: number; lineItems: any[];
  } = {
    invoiceDate: '', dueDate: '', taxPercentage: 18, paymentTerms: '',
    notes: '', termsAndConditions: '', bankName: '', accountHolderName: '',
    accountNumber: '', ifscCode: '', branchName: '', upiId: '',
    resubmitRemarks: '', overallDiscountAmount: 0, lineItems: []
  };

  // ── RFQ view modal ────────────────────────────────────────────────────────
  selectedRFQ      : any    = null;
  isViewModalOpen  : boolean = false;
  isQuoteModalOpen : boolean = false;
  quoteForm = { quoteAmount: 0, notes: '' };

  // ── Loading / error ───────────────────────────────────────────────────────
  isLoading         : boolean = false;
  isLoadingStats    : boolean = false;
  isLoadingRFQs     : boolean = false;
  isLoadingPOs      : boolean = false;
  isLoadingInvoices : boolean = false;
  isSubmittingQuote : boolean = false;
  isDownloadingPDF  : boolean = false;
  errorMessage      : string | null = null;

  constructor(
    private authService   : AuthService,
    private dataService   : DataService,
    private messageService: MessageService,
    private router        : Router
  ) {}

  ngOnInit(): void {
    this.loadSupplierUserData();
    this.loadDashboardData();
  }

  // ── Init ──────────────────────────────────────────────────────────────────

  loadSupplierUserData(): void {
    this.fullName       = localStorage.getItem('fullName')       || 'Supplier User';
    this.email          = localStorage.getItem('email')          || '';
    this.phone          = localStorage.getItem('phone')          || '';
    this.departmentName = localStorage.getItem('departmentName') || 'Supplier';
    this.supplierName   = localStorage.getItem('supplierName')   || '';
    this.companyName    = this.supplierName;
    this.companyPhone   = localStorage.getItem('companyPhone')   || localStorage.getItem('phone') || '';
    this.city           = localStorage.getItem('city')           || '';
    this.state          = localStorage.getItem('state')          || '';
    const sid           = this.authService.getSupplierId();
    this.supplierId     = sid ? sid : Number(localStorage.getItem('supplierId') || '0');
    this.userInitials   = this.getInitials(this.fullName);
  }

  loadDashboardData(): void {
    if (!this.supplierId) {
      this.errorMessage = 'Supplier ID not found. Please login again.';
      return;
    }
    this.loadStatistics();
    this.loadRFQs();
    this.loadPOs();
    this.loadInvoices();
  }

  loadStatistics(): void {
    this.isLoadingStats = true;
    this.dataService.getSupplierDashboardStatistics(this.supplierId).subscribe({
      next: (r: any) => {
        if (r?.success && r.data) this.statistics = { ...this.statistics, ...r.data };
        this.isLoadingStats = false;
      },
      error: () => { this.isLoadingStats = false; }
    });
  }

  // ── RFQ tab ───────────────────────────────────────────────────────────────

  loadRFQs(): void {
    this.isLoadingRFQs = true;
    this.dataService.getSupplierRFQs(this.supplierId, this.statusFilter, this.searchText).subscribe({
      next: (r: any) => {
        const raw = r?.success ? (r.data || []) : (Array.isArray(r) ? r : []);

        // Normalise each RFQ. If the backend already sent isExpired we use it;
        // otherwise we fall back to a client-side check so nothing breaks if
        // an older cached response arrives without the flag.
        this.rfqList = raw.map((rfq: any) => ({
          ...rfq,
          currencyCode   : rfq.currencyCode    || 'INR',
          currencySymbol : rfq.currencySymbol  || '₹',
          isExpired      : rfq.isExpired !== undefined
                             ? rfq.isExpired
                             : this.clientSideExpiredCheck(rfq),
          daysUntilDue   : rfq.daysUntilDue !== undefined
                             ? rfq.daysUntilDue
                             : this.computeDaysUntilDue(rfq.dueDate)
        }));

        this.totalRFQs = this.rfqList.length;
        this.applyRFQFilters();
        this.isLoadingRFQs = false;
      },
      error: () => { this.isLoadingRFQs = false; }
    });
  }

  /**
   * Client-side expiry check — fallback only.
   * The backend value is always preferred when present.
   */
  clientSideExpiredCheck(rfq: any): boolean {
    if (!rfq.dueDate) return false;
    const alreadyActed = rfq.supplierStatus === 'RESPONDED'
                      || rfq.supplierStatus === 'SELECTED'
                      || rfq.supplierStatus === 'REJECTED';
    return new Date() > new Date(rfq.dueDate) && !alreadyActed;
  }

  computeDaysUntilDue(dueDateStr: string): number | null {
    if (!dueDateStr) return null;
    const diff = Math.round(
      (new Date(dueDateStr).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)
    );
    return diff;
  }

  applyRFQFilters(): void {
    let data = [...this.rfqList];

    // Text search
    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      data = data.filter(r =>
        r.rfqNumber?.toLowerCase().includes(s) ||
        r.rfqTitle?.toLowerCase().includes(s)
      );
    }

    // Status filter — EXPIRED is virtual, all others exclude expired rows
    if (this.statusFilter && this.statusFilter !== 'ALL') {
      if (this.statusFilter === 'EXPIRED') {
        data = data.filter(r => r.isExpired && !this.hasSubmittedQuote(r));
      } else {
        data = data.filter(r =>
          r.supplierStatus === this.statusFilter &&
          !(r.isExpired && !this.hasSubmittedQuote(r))
        );
      }
    }

    this.filteredRFQList = data;
    this.totalRFQs       = data.length;
  }

  // ── PO tab ────────────────────────────────────────────────────────────────

  loadPOs(): void {
    this.isLoadingPOs = true;
    this.dataService.getApprovedPOsForSupplier(this.supplierId).subscribe({
      next: (r: any) => {
        const raw = r?.success ? (r.data || []) : [];
        this.poList = raw.map((po: any) => ({
          ...po,
          currencyCode  : po.currencyCode   || 'INR',
          currencySymbol: po.currencySymbol || '₹'
        }));
        this.applyPOFilters();
        this.isLoadingPOs = false;
      },
      error: () => { this.poList = []; this.filteredPOList = []; this.isLoadingPOs = false; }
    });
  }

  applyPOFilters(): void {
    let data = [...this.poList];
    if (this.poSearchText.trim()) {
      const s = this.poSearchText.toLowerCase();
      data = data.filter(p =>
        p.poNumber?.toLowerCase().includes(s) ||
        p.rfqNumber?.toLowerCase().includes(s) ||
        p.buyerCompanyName?.toLowerCase().includes(s)
      );
    }
    if (this.poStatusFilter !== 'ALL') {
      data = data.filter(p => p.invoiceStatus === this.poStatusFilter);
    }
    this.filteredPOList = data;
  }

  // ── Invoice tab ───────────────────────────────────────────────────────────

  loadInvoices(): void {
    this.isLoadingInvoices = true;
    this.dataService.getSupplierInvoices(this.supplierId).subscribe({
      next: (r: any) => {
        const raw = r?.success ? (r.data || []) : [];
        this.invoiceList = raw.map((inv: any) => ({
          ...inv,
          currencyCode  : inv.currencyCode   || inv.currency || 'INR',
          currencySymbol: inv.currencySymbol || '₹'
        }));
        this.applyInvoiceFilters();
        this.isLoadingInvoices = false;
      },
      error: () => { this.invoiceList = []; this.filteredInvoiceList = []; this.isLoadingInvoices = false; }
    });
  }

  applyInvoiceFilters(): void {
    let data = [...this.invoiceList];
    if (this.invoiceSearchText.trim()) {
      const s = this.invoiceSearchText.toLowerCase();
      data = data.filter(i =>
        i.invoiceNumber?.toLowerCase().includes(s) ||
        i.poNumber?.toLowerCase().includes(s) ||
        i.rfqNumber?.toLowerCase().includes(s)
      );
    }
    if (this.invoiceStatusFilter !== 'ALL') {
      if (this.invoiceStatusFilter === 'REJECTED') {
        data = data.filter(i => i.status === 'REJECTED' || i.status === 'REJECTED_CLOSED');
      } else {
        data = data.filter(i => i.status === this.invoiceStatusFilter);
      }
    }
    this.filteredInvoiceList = data;
  }

  // ── Invoice creation ──────────────────────────────────────────────────────

  openCreateInvoiceModal(po: any): void {
    this.isLoadingPODetails   = true;
    this.selectedPOForInvoice = po;
    this.isInvoiceModalOpen   = true;

    this.poLocationCurrencyCode   = po.currencyCode   || 'INR';
    this.poLocationCurrencySymbol = po.currencySymbol || '₹';

    this.invoiceForm = {
      invoiceDate          : this.getTodayStr(),
      dueDate              : this.getDueDateStr(30),
      taxPercentage        : 18,
      paymentTerms         : 'Net 30 days from invoice date',
      notes                : '',
      termsAndConditions   : 'Payment is due within 30 days of invoice date. Late payment will attract 2% per month interest.',
      bankName             : localStorage.getItem('bankName')          || '',
      accountHolderName    : localStorage.getItem('accountHolderName') || this.supplierName,
      accountNumber        : localStorage.getItem('accountNumber')     || '',
      ifscCode             : localStorage.getItem('ifscCode')          || '',
      branchName           : localStorage.getItem('branchName')        || '',
      upiId                : localStorage.getItem('upiId')             || '',
      overallDiscountAmount: 0,
      poGrandTotal         : 0,
      lineItems            : []
    };

    this.dataService.getPODetailsForInvoice(this.supplierId, po.id).subscribe({
      next: (r: any) => {
        if (r?.success && r.data) {
          const d = r.data;
          this.invoiceForm.overallDiscountAmount = Number(d.overallDiscountAmount || 0);
          this.invoiceForm.poGrandTotal          = Number(d.grandTotal || 0);

          if (d.currencyCode) {
            this.poLocationCurrencyCode   = d.currencyCode;
            this.poLocationCurrencySymbol = d.currencySymbol || this.getSymbolForCode(d.currencyCode);
          }

          this.invoiceForm.lineItems = (d.lineItems || []).map((item: any) => {
            const poQty        = Number(item.quantity)    || 0;
            const invoicedQty  = Number(item.invoicedQty || item.alreadyInvoicedQty || 0);
            const remainingQty = Math.max(0, poQty - invoicedQty);
            return {
              id                     : item.id,
              itemCode               : item.itemCode || '',
              itemDescription        : item.itemDescription || item.description || '',
              itemDescriptionDetailed: item.itemDescriptionDetailed || '',
              hsnSacCode             : item.hsnSacCode || '',
              uom                    : item.uom || 'PCS',
              poQuantity             : poQty,
              alreadyInvoicedQty     : invoicedQty,
              remainingQty           : remainingQty,
              qtyToInvoice           : remainingQty,
              unitPrice              : Number(item.unitPrice || item.rate || 0),
              discountPercentage     : Number(item.discountPercentage) || 0,
              taxPercentage          : Number(item.taxPercentage) ?? 18
            };
          });

          this.selectedPOForInvoice = { ...po, ...d };
        }
        this.isLoadingPODetails = false;
      },
      error: () => { this.invoiceForm.lineItems = []; this.isLoadingPODetails = false; }
    });
  }

  private getSymbolForCode(code: string): string {
    const m: Record<string, string> = {
      INR: '₹', USD: '$', EUR: '€', GBP: '£', AED: 'د.إ', SGD: 'S$',
      JPY: '¥', CNY: '¥', CHF: 'Fr', CAD: 'C$', AUD: 'A$', NZD: 'NZ$',
      SAR: 'ر.س', QAR: 'ر.ق', KWD: 'د.ك', BHD: '.د.ب', OMR: 'ر.ع.',
      MYR: 'RM', THB: '฿', IDR: 'Rp', PKR: '₨', BDT: '৳', LKR: '₨', NPR: '₨'
    };
    return m[code] || code;
  }

  addInvoiceLineItem(): void {
    this.invoiceForm.lineItems.push({
      itemCode: '', itemDescription: '', uom: 'PCS',
      poQuantity: 0, alreadyInvoicedQty: 0, remainingQty: 0,
      qtyToInvoice: 1, unitPrice: 0, discountPercentage: 0,
      taxPercentage: 18, hsnSacCode: ''
    });
  }

  removeInvoiceLineItem(i: number): void { this.invoiceForm.lineItems.splice(i, 1); }

  getLineTotal(item: any): number {
    const base  = (Number(item.qtyToInvoice) || 0) * (Number(item.unitPrice) || 0);
    const after = base - (base * (Number(item.discountPercentage) || 0) / 100);
    return after + (after * (Number(item.taxPercentage) || 0) / 100);
  }

  getInvoiceSubtotal(): number {
    return this.invoiceForm.lineItems.reduce((s, it) => {
      const base = (Number(it.qtyToInvoice) || 0) * (Number(it.unitPrice) || 0);
      return s + base - (base * (Number(it.discountPercentage) || 0) / 100);
    }, 0);
  }

  getInvoiceTaxTotal(): number {
    return this.invoiceForm.lineItems.reduce((s, it) => {
      const base  = (Number(it.qtyToInvoice) || 0) * (Number(it.unitPrice) || 0);
      const after = base - (base * (Number(it.discountPercentage) || 0) / 100);
      return s + (after * (Number(it.taxPercentage) || 0) / 100);
    }, 0);
  }

  getInvoiceGrandTotal(): number {
    if (!this.invoiceForm.lineItems || !this.invoiceForm.lineItems.length)
      return Number(this.invoiceForm.poGrandTotal) || 0;
    const full = this.invoiceForm.lineItems.every(
      (it: any) => Number(it.qtyToInvoice) >= Number(it.remainingQty)
    );
    if (full && this.invoiceForm.poGrandTotal > 0)
      return Number(this.invoiceForm.poGrandTotal);
    return this.getInvoiceSubtotal() + this.getInvoiceTaxTotal()
           - (Number(this.invoiceForm.overallDiscountAmount) || 0);
  }

  saveInvoiceDraft(): void {
    if (!this.validateInvoiceForm()) return;
    this.isCreatingInvoice = true;
    this.dataService.createInvoice(this.supplierId, this.selectedPOForInvoice.id,
        this.buildInvoicePayload()).subscribe({
      next: (r: any) => {
        if (r?.success) {
          this.messageService.showMessage('success', 'Saved', `Invoice ${r.data?.invoiceNumber} saved as DRAFT`);
          this.isInvoiceModalOpen = false;
          this.loadInvoices(); this.loadPOs();
        }
        this.isCreatingInvoice = false;
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to create invoice');
        this.isCreatingInvoice = false;
      }
    });
  }

  createAndSubmitInvoice(): void {
    if (!this.validateInvoiceForm(true)) return;
    this.isSubmittingInvoice = true;
    this.dataService.createInvoice(this.supplierId, this.selectedPOForInvoice.id,
        this.buildInvoicePayload()).subscribe({
      next: (cr: any) => {
        if (cr?.success) {
          this.dataService.submitInvoice(cr.data.id, this.supplierId).subscribe({
            next: () => {
              this.messageService.showMessage('success', 'Invoice Submitted', `Invoice ${cr.data.invoiceNumber} sent to buyer`);
              this.isInvoiceModalOpen = false; this.loadInvoices(); this.loadPOs();
              this.isSubmittingInvoice = false;
            },
            error: () => {
              this.messageService.showMessage('warning', 'Created but not submitted', 'Invoice saved as draft. Please submit manually.');
              this.isInvoiceModalOpen = false; this.loadInvoices();
              this.isSubmittingInvoice = false;
            }
          });
        }
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to create invoice');
        this.isSubmittingInvoice = false;
      }
    });
  }

  submitExistingInvoice(invoice: any): void {
    this.dataService.submitInvoice(invoice.id, this.supplierId).subscribe({
      next: () => { this.messageService.showMessage('success', 'Submitted', 'Invoice sent to buyer'); this.loadInvoices(); this.loadPOs(); },
      error: (err: any) => { this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed'); }
    });
  }

  // ── Resubmit modal ────────────────────────────────────────────────────────

  openResubmitModal(invoice: any): void {
    this.isLoadingInvoice = true;
    this.dataService.getInvoiceById(invoice.id).subscribe({
      next: (r: any) => {
        const full = r?.success ? r.data : invoice;
        this.selectedInvoiceForResubmit = full;
        this._populateEditForm(full);
        this.isResubmitModalOpen = true;
        this.isLoadingInvoice    = false;
      },
      error: () => {
        this.selectedInvoiceForResubmit = invoice;
        this._populateEditForm(invoice);
        this.isResubmitModalOpen = true;
        this.isLoadingInvoice    = false;
      }
    });
  }

  private _populateEditForm(inv: any): void {
    if (inv.currencyCode || inv.currency) {
      this.poLocationCurrencyCode   = inv.currencyCode || inv.currency || 'INR';
      this.poLocationCurrencySymbol = inv.currencySymbol || this.getSymbolForCode(this.poLocationCurrencyCode);
    }
    this.editInvoiceForm = {
      invoiceDate          : inv.invoiceDate ? inv.invoiceDate.split('T')[0] : this.getTodayStr(),
      dueDate              : inv.dueDate     ? inv.dueDate.split('T')[0]     : this.getDueDateStr(30),
      taxPercentage        : inv.taxPercentage ?? 18,
      paymentTerms         : inv.paymentTerms || 'Net 30 days from invoice date',
      notes                : inv.notes || '',
      termsAndConditions   : inv.termsAndConditions || '',
      bankName             : inv.bankName || '',
      accountHolderName    : inv.accountHolderName || this.supplierName,
      accountNumber        : inv.accountNumber || '',
      ifscCode             : inv.ifscCode || '',
      branchName           : inv.branchName || '',
      upiId                : inv.upiId || '',
      resubmitRemarks      : '',
      overallDiscountAmount: Number(inv.overallDiscountAmount || 0),
      lineItems: (inv.lineItems || inv.items || []).map((it: any) => ({
        id: it.id, itemCode: it.itemCode || '', itemDescription: it.itemDescription || '',
        hsnSacCode: it.hsnSacCode || '', uom: it.uom || 'PCS',
        quantity: it.quantity || 1, unitPrice: it.unitPrice || 0,
        discountPercentage: it.discountPercentage || 0, taxPercentage: it.taxPercentage ?? 18
      }))
    };
    this.resubmitRemarks = '';
  }

  closeResubmitModal(): void {
    this.isResubmitModalOpen = false;
    this.selectedInvoiceForResubmit = null;
    this.resubmitRemarks = '';
  }

  addEditInvoiceLineItem(): void {
    this.editInvoiceForm.lineItems.push({
      itemCode: '', itemDescription: '', hsnSacCode: '', uom: 'PCS',
      quantity: 1, unitPrice: 0, discountPercentage: 0, taxPercentage: 18
    });
  }

  removeEditInvoiceLineItem(i: number): void { this.editInvoiceForm.lineItems.splice(i, 1); }

  getEditLineTotal(it: any): number {
    const base  = (Number(it.quantity) || 0) * (Number(it.unitPrice) || 0);
    const after = base - (base * (Number(it.discountPercentage) || 0) / 100);
    return after + (after * (Number(it.taxPercentage) || 0) / 100);
  }

  getEditInvoiceSubtotal(): number {
    return this.editInvoiceForm.lineItems.reduce((s, it) => {
      const base = (Number(it.quantity) || 0) * (Number(it.unitPrice) || 0);
      return s + base - (base * (Number(it.discountPercentage) || 0) / 100);
    }, 0);
  }

  getEditInvoiceTaxTotal(): number {
    return this.editInvoiceForm.lineItems.reduce((s, it) => {
      const base  = (Number(it.quantity) || 0) * (Number(it.unitPrice) || 0);
      const after = base - (base * (Number(it.discountPercentage) || 0) / 100);
      return s + (after * (Number(it.taxPercentage) || 0) / 100);
    }, 0);
  }

  getEditInvoiceGrandTotal(): number {
    return this.getEditInvoiceSubtotal() + this.getEditInvoiceTaxTotal()
           - (Number(this.editInvoiceForm.overallDiscountAmount) || 0);
  }

  confirmResubmit(): void {
    const remarks = (this.editInvoiceForm.resubmitRemarks || this.resubmitRemarks || '').trim();
    if (!remarks) { this.messageService.showMessage('warning', 'Remarks Required', 'Please describe what you corrected'); return; }
    if (!this.editInvoiceForm.lineItems.length) { this.messageService.showMessage('warning', 'Validation', 'At least one line item required'); return; }
    if (!this.selectedInvoiceForResubmit) return;

    this.isResubmitting = true;
    const payload = {
      invoiceDate: this.editInvoiceForm.invoiceDate, dueDate: this.editInvoiceForm.dueDate,
      taxPercentage: this.editInvoiceForm.taxPercentage, paymentTerms: this.editInvoiceForm.paymentTerms,
      notes: this.editInvoiceForm.notes, termsAndConditions: this.editInvoiceForm.termsAndConditions,
      bankName: this.editInvoiceForm.bankName, accountHolderName: this.editInvoiceForm.accountHolderName,
      accountNumber: this.editInvoiceForm.accountNumber, ifscCode: this.editInvoiceForm.ifscCode,
      branchName: this.editInvoiceForm.branchName, upiId: this.editInvoiceForm.upiId,
      overallDiscountAmount: this.editInvoiceForm.overallDiscountAmount || 0,
      lineItems: this.editInvoiceForm.lineItems
    };

    const doResubmit = () => {
      this.dataService.resubmitInvoice(this.selectedInvoiceForResubmit.id, this.supplierId, remarks).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Resubmitted', `Invoice ${this.selectedInvoiceForResubmit.invoiceNumber} sent back to buyer`);
          this.isResubmitModalOpen = false; this.isInvoiceViewModalOpen = false;
          this.selectedInvoiceForResubmit = null; this.resubmitRemarks = '';
          this.isResubmitting = false;
          this.loadInvoices(); this.loadPOs();
        },
        error: (e: any) => { this.messageService.showMessage('error', 'Failed', e.error?.message || 'Could not resubmit'); this.isResubmitting = false; }
      });
    };

    this.dataService.updateInvoice(this.selectedInvoiceForResubmit.id, this.supplierId, payload).subscribe({
      next: doResubmit,
      error: doResubmit
    });
  }

  canResubmit(inv: any): boolean {
    if (!inv) return false;
    return inv.status === 'REJECTED' &&
      (inv.canResubmit === true || inv.resubmitCount === 0 || inv.resubmitCount == null);
  }

  isPermanentlyClosed(inv: any): boolean { return inv?.status === 'REJECTED_CLOSED'; }

  // ── View invoice ──────────────────────────────────────────────────────────

  viewInvoice(invoice: any): void {
    this.isLoadingInvoice = true; this.isInvoiceViewModalOpen = true;
    this.dataService.getInvoiceById(invoice.id).subscribe({
      next: (r: any) => { this.selectedInvoice = r?.success ? r.data : invoice; this.isLoadingInvoice = false; },
      error: () => { this.selectedInvoice = invoice; this.isLoadingInvoice = false; }
    });
  }

  closeInvoiceViewModal(): void { this.isInvoiceViewModalOpen = false; this.selectedInvoice = null; }

  downloadInvoicePDF(): void {
    if (!this.selectedInvoice) return;
    this.isDownloadingInvoicePDF = true;
    const el = document.getElementById('invoice-print-content');
    if (!el) { this.isDownloadingInvoicePDF = false; return; }
    html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#ffffff' }).then(canvas => {
      const pdf  = new jsPDF('p', 'mm', 'a4');
      const data = canvas.toDataURL('image/png');
      const pw   = 210;
      const ih   = (canvas.height * pw) / canvas.width;
      let hl = ih, pos = 0;
      pdf.addImage(data, 'PNG', 0, pos, pw, ih);
      hl -= 297;
      while (hl > 0) { pos = hl - ih; pdf.addPage(); pdf.addImage(data, 'PNG', 0, pos, pw, ih); hl -= 297; }
      pdf.save(`${this.selectedInvoice.invoiceNumber}.pdf`);
      this.isDownloadingInvoicePDF = false;
    }).catch(() => { this.isDownloadingInvoicePDF = false; });
  }

  // ── Form helpers ──────────────────────────────────────────────────────────

  private validateInvoiceForm(requireBank = false): boolean {
    if (!this.invoiceForm.lineItems.length) {
      this.messageService.showMessage('warning', 'Validation', 'Add at least one line item'); return false;
    }
    const today = this.getTodayStr();
    if (this.invoiceForm.invoiceDate < today) {
      this.messageService.showMessage('warning', 'Invalid Date', 'Invoice date cannot be in the past'); return false;
    }
    if (this.invoiceForm.dueDate < today) {
      this.messageService.showMessage('warning', 'Invalid Date', 'Payment due date cannot be in the past'); return false;
    }
    for (const it of this.invoiceForm.lineItems) {
      if (!it.itemDescription) {
        this.messageService.showMessage('warning', 'Validation', 'Fill description for all items'); return false;
      }
      if (Number(it.qtyToInvoice) <= 0) {
        this.messageService.showMessage('warning', 'Validation', `Qty must be > 0 for "${it.itemDescription}"`); return false;
      }
      if (it.remainingQty > 0 && Number(it.qtyToInvoice) > Number(it.remainingQty)) {
        this.messageService.showMessage('warning', 'Qty Exceeded', `"${it.itemDescription}" exceeds remaining PO qty`); return false;
      }
    }
    if (requireBank && (!this.invoiceForm.bankName || !this.invoiceForm.accountNumber || !this.invoiceForm.ifscCode)) {
      this.messageService.showMessage('warning', 'Bank Details Required', 'Enter bank name, account number and IFSC'); return false;
    }
    return true;
  }

  private buildInvoicePayload(): any {
    return {
      invoiceDate: this.invoiceForm.invoiceDate, dueDate: this.invoiceForm.dueDate,
      taxPercentage: this.invoiceForm.taxPercentage, paymentTerms: this.invoiceForm.paymentTerms,
      notes: this.invoiceForm.notes, termsAndConditions: this.invoiceForm.termsAndConditions,
      bankName: this.invoiceForm.bankName, accountHolderName: this.invoiceForm.accountHolderName,
      accountNumber: this.invoiceForm.accountNumber, ifscCode: this.invoiceForm.ifscCode,
      branchName: this.invoiceForm.branchName, upiId: this.invoiceForm.upiId,
      overallDiscountAmount: this.invoiceForm.overallDiscountAmount || 0,
      lineItems: this.invoiceForm.lineItems.map(it => ({
        id: it.id, itemCode: it.itemCode, itemDescription: it.itemDescription,
        itemDescriptionDetailed: it.itemDescriptionDetailed || '',
        hsnSacCode: it.hsnSacCode, uom: it.uom, quantity: it.qtyToInvoice,
        unitPrice: it.unitPrice, discountPercentage: it.discountPercentage,
        taxPercentage: it.taxPercentage
      }))
    };
  }

  // ── Tab ───────────────────────────────────────────────────────────────────

  switchTab(tab: 'rfq' | 'po' | 'invoice'): void { this.activeTab = tab; }

  // ── RFQ methods ───────────────────────────────────────────────────────────

  viewRFQDetails(rfq: any): void {
    const id = rfq.rfqId || rfq.id;
    if (!id) { this.messageService.showMessage('error', 'Error', 'RFQ ID not found'); return; }
    this.isLoading = true;
    this.dataService.getSupplierRFQDetails(this.supplierId, id).subscribe({
      next: (r: any) => {
        if (r?.success && r.data) { this.selectedRFQ = r.data; this.isViewModalOpen = true; }
        else this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
        this.isLoading = false;
      },
      error: () => { this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details'); this.isLoading = false; }
    });
  }

  closeViewModal(): void { this.isViewModalOpen = false; this.selectedRFQ = null; }

  /**
   * Navigate to quote submission.
   * The frontend guard prevents routing to the quote page if expired.
   * The backend guard (submitItemQuote) is the authoritative second layer.
   */
  navigateToQuoteSubmission(rfq: any): void {
    // Use backend flag first, fall back to client-side check
    const expired = rfq.isExpired !== undefined
      ? rfq.isExpired
      : this.clientSideExpiredCheck(rfq);

    if (expired) {
      this.messageService.showMessage(
        'warning',
        'Submission Closed',
        `The deadline for RFQ ${rfq.rfqNumber || ''} has passed. ` +
        `Quotes were accepted until ${this.formatDate(rfq.dueDate)}. ` +
        `Please contact the buyer if you have questions.`
      );
      return;
    }

    if (rfq.supplierStatus === 'RESPONDED') {
      this.messageService.showMessage('info', 'Already Submitted', 'You have already submitted a quote for this RFQ.');
      return;
    }

    this.router.navigate(['/supplier-quote', rfq.rfqId || rfq.id]);
  }

  viewSubmittedQuote(rfq: any): void {
    this.router.navigate(['/supplier-quote', rfq.rfqId || rfq.id], { queryParams: { viewOnly: true } });
  }

  /**
   * Whether the Submit Quote button should be visible.
   * Uses the backend-provided canSubmitQuote flag when available.
   */
  canSubmitQuote(rfq: any): boolean {
    if (!rfq) return false;
    // Backend flag is authoritative
    if (rfq.canSubmitQuote !== undefined) return rfq.canSubmitQuote;
    // Client-side fallback
    const expired = rfq.isExpired !== undefined ? rfq.isExpired : this.clientSideExpiredCheck(rfq);
    return !expired && (rfq.supplierStatus === 'PENDING' || rfq.supplierStatus === 'SENT');
  }

  hasSubmittedQuote(rfq: any): boolean {
    return rfq?.supplierStatus === 'RESPONDED' || rfq?.supplierStatus === 'SELECTED';
  }

  /**
   * Human-readable countdown / overdue label shown under the due date.
   * Examples: "3 days left", "Due today", "Overdue by 2 days"
   */
  getDueDateLabel(rfq: any): string {
    const d = rfq?.daysUntilDue;
    if (d === null || d === undefined) return '';
    if (d > 1)   return `${d} days left`;
    if (d === 1) return '1 day left';
    if (d === 0) return 'Due today';
    if (d === -1) return 'Overdue by 1 day';
    return `Overdue by ${Math.abs(d)} days`;
  }

  openQuoteModal(_rfq: any): void {}
  submitQuote(): void {}
  closeQuoteModal(): void { this.isQuoteModalOpen = false; }
  downloadRFQPDF(): void {}
  downloadAttachment(att: any): void { window.open(`http://localhost:8080/leadcapture${att.downloadUrl}`, '_blank'); }

  // ── Pagination ────────────────────────────────────────────────────────────

  get paginatedRFQs():     any[] { return this.filteredRFQList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize); }
  get totalPages():        number { return Math.ceil(this.totalRFQs / this.pageSize); }
  nextPage():    void { if (this.currentPage < this.totalPages) this.currentPage++; }
  previousPage():void { if (this.currentPage > 1)              this.currentPage--; }

  get paginatedPOs():      any[] { return this.filteredPOList.slice((this.poCurrentPage - 1) * this.poPageSize, this.poCurrentPage * this.poPageSize); }
  get totalPOPages():      number { return Math.ceil(this.filteredPOList.length / this.poPageSize); }

  get paginatedInvoices(): any[] { return this.filteredInvoiceList.slice((this.invoiceCurrentPage - 1) * this.invoicePageSize, this.invoiceCurrentPage * this.invoicePageSize); }
  get totalInvoicePages(): number { return Math.ceil(this.filteredInvoiceList.length / this.invoicePageSize); }

  // ── Utility ───────────────────────────────────────────────────────────────

  refresh(): void { this.currentPage = 1; this.poCurrentPage = 1; this.invoiceCurrentPage = 1; this.loadDashboardData(); }
  onSearchChange(): void { this.currentPage = 1; this.applyRFQFilters(); }

  getInitials(name: string): string {
    if (!name?.trim()) return 'SU';
    const p = name.trim().split(' ');
    return p.length === 1 ? p[0].substring(0, 2).toUpperCase()
                          : (p[0][0] + p[p.length - 1][0]).toUpperCase();
  }

  getStatusBadgeClass(status: string): string {
    const m: any = { PENDING: 'warning', SENT: 'info', RESPONDED: 'success', SELECTED: 'success', REJECTED: 'danger' };
    return m[status] || 'secondary';
  }

  getInvoiceStatusClass(status: string): string {
    const m: any = { DRAFT: 'secondary', SUBMITTED: 'primary', APPROVED: 'success', PAID: 'info', REJECTED: 'warning', REJECTED_CLOSED: 'danger' };
    return m[status] || 'secondary';
  }

  getInvoiceStatusLabel(status: string): string {
    const m: any = { DRAFT: 'Draft', SUBMITTED: 'Submitted', APPROVED: 'Approved', PAID: 'Paid', REJECTED: 'Rejected', REJECTED_CLOSED: 'Closed' };
    return m[status] || status;
  }

  formatDate(d: string): string {
    if (!d) return 'N/A';
    try { return new Date(d).toLocaleDateString('en-GB'); } catch { return 'N/A'; }
  }

  formatCurrency(amount: number | null, currencyCode?: string): string {
    const code      = currencyCode || 'INR';
    const symbol    = this.getSymbolForCode(code);
    const val       = Number(amount ?? 0);
    const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtl       = ['AED','SAR','QAR','KWD','BHD','OMR','IRR','IQD','JOD','LBP'];
    return rtl.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
  }

  formatInvoiceCurrency(amount: number | null): string { return this.formatCurrency(amount, this.poLocationCurrencyCode); }

  formatFileSize(bytes: number): string {
    if (!bytes) return '0 B';
    const k = 1024, sizes = ['B','KB','MB','GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }

  objectKeys(obj: any): string[] { return obj ? Object.keys(obj) : []; }
  getTodayStr():        string { return new Date().toISOString().split('T')[0]; }
  getDueDateStr(days: number): string {
    const d = new Date(); d.setDate(d.getDate() + days); return d.toISOString().split('T')[0];
  }
  navigateTo(route: string): void { this.router.navigate([route]); }
  isOverdue(inv: any): boolean { return !(!inv?.dueDate || inv.status === 'PAID') && new Date(inv.dueDate) < new Date(); }
}