
// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ActivatedRoute, Router } from '@angular/router';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, SpinnerComponent, TableModule
// } from '@coreui/angular';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';

// interface NegotiationLineItem {
//   id: number;
//   rfqItemId: number;
//   itemDescription: string;
//   itemCode?: string;
//   specifications?: string;
//   quantity: number;
//   uom: string;
//   actualQuotedPrice: number;
//   discountPercentage: number;
//   discountAmount: number;
//   finalizedPrice: number;
//   lineTotal: number;
//   taxPercentage: number;
//   taxAmount: number;
//   lineTotalWithTax: number;
//   priceExceedsQuoted: boolean;
// }

// @Component({
//   selector: 'app-po-negotiation',
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent,
//     ButtonDirective, BadgeComponent, SpinnerComponent, TableModule
//   ],
//   templateUrl: './po-negotiation.component.html',
//   styleUrls: ['./po-negotiation.component.css']
// })
// export class PONegotiationComponent implements OnInit {

//   rfqId: number = 0;
//   supplierId: number = 0;
//   selectionId: number = 0;
//   negotiationId: number = 0;

//   isLoading: boolean = false;
//   isSaving: boolean = false;
//   isCreatingPO: boolean = false;

//   negotiation: any = null;
//   lineItems: NegotiationLineItem[] = [];

//   terms: string = '';

//   subtotalQuoted: number = 0;
//   subtotalFinalized: number = 0;
//   totalDiscountAmount: number = 0;
//   totalTaxAmount: number = 0;
//   grandTotalQuoted: number = 0;
//   grandTotalFinalized: number = 0;

//   overallDiscountPercentage: number = 0;
//   overallDiscountAmount: number = 0;

//   roundingOff: number = 0;
//   roundingSign: '+' | '-' = '-';

//   netPayable: number = 0;

//   supplierName: string = '';
//   errorMessage: string = '';

//   // ✅ Currency — comes from buyer's location (set during buyer creation), NOT user-selectable
//   currencyCode: string = 'INR';
//   currencySymbol: string = '₹';

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private dataService: DataService,
//     private messageService: MessageService,
//   ) {}

//   ngOnInit(): void {
//     this.rfqId       = Number(this.route.snapshot.paramMap.get('rfqId'));
//     this.supplierId  = Number(this.route.snapshot.paramMap.get('supplierId'));
//     this.selectionId = Number(this.route.snapshot.paramMap.get('selectionId'));

//     if (!this.rfqId || !this.supplierId) {
//       this.messageService.showMessage('error', 'Error', 'Error: Missing required parameters');
//       this.router.navigate(['/rfq-dashboard']);
//       return;
//     }

//     this.initNegotiation();
//   }

//   // ============================================================
//   // INIT
//   // ============================================================

//   initNegotiation(): void {
//     this.isLoading = true;
//     this.errorMessage = '';

//     this.dataService.getPONegotiationsByRFQ(this.rfqId).subscribe({
//       next: (response: any) => {
//         let list: any[] = [];
//         if (Array.isArray(response))            list = response;
//         else if (Array.isArray(response?.data)) list = response.data;

//         const matches = list.filter((n: any) =>
//           Number(n.supplierId) === Number(this.supplierId)
//         );

//         let existing: any = null;
//         if (matches.length > 0) {
//           if (this.selectionId) {
//             existing = matches.find((n: any) => Number(n.finalSelectionId) === Number(this.selectionId));
//           }
//           if (!existing) existing = matches.find((n: any) => ['DRAFT', 'FINALIZED'].includes(n.status));
//           if (!existing) existing = matches[0];
//         }

//         if (existing?.id) {
//           this.negotiationId = existing.id;
//           this.populateFromNegotiation(existing);
//           this.isLoading = false;
//         } else {
//           this.createNewNegotiation();
//         }
//       },
//       error: () => this.createNewNegotiation()
//     });
//   }

//   private createNewNegotiation(): void {
//     this.dataService.initPONegotiation(this.rfqId, this.supplierId).subscribe({
//       next: (response: any) => {
//         const data = response?.data || response;
//         if (!data || !data.id) {
//           this.errorMessage = 'Server returned an empty response. Please check if the supplier has submitted quotes for this RFQ.';
//           this.isLoading = false;
//           return;
//         }
//         this.negotiationId = data.id;
//         this.populateFromNegotiation(data);
//         this.isLoading = false;
//       },
//       error: (error: any) => {
//         const backendMsg =
//           error?.error?.message ||
//           error?.error?.error  ||
//           (typeof error?.error === 'string' ? error.error : null) ||
//           error?.message ||
//           'Failed to initialize price negotiation';
//         this.errorMessage = backendMsg;
//         this.messageService.showMessage('error', 'Error', 'Initialization Failed: ' + backendMsg);
//         this.isLoading = false;
//       }
//     });
//   }

//   private populateFromNegotiation(data: any): void {
//     if (!data) return;

//     this.negotiation  = data;
//     this.supplierName = data.supplierName || `Supplier #${this.supplierId}`;
//     this.terms        = data.terms || data.paymentTerms || '';

//     this.overallDiscountPercentage = data.overallDiscountPercentage || 0;
//     this.overallDiscountAmount     = data.overallDiscountAmount || 0;

//     const savedRounding = data.roundingOff || 0;
//     this.roundingOff    = savedRounding;
//     this.roundingSign   = savedRounding >= 0 ? '+' : '-';

//     // ✅ Currency comes from backend (buyer location) — never user-selectable
//     if (data.currencyCode) {
//       this.currencyCode   = data.currencyCode;
//       this.currencySymbol = data.currencySymbol || this.getSymbolForCode(data.currencyCode);
//     }

//     this.lineItems = (data.lineItems || []).map((item: any) => {
//       const quoted    = Number(item.actualQuotedPrice) || 0;
//       const qty       = Number(item.quantity) || 0;
//       const taxPct    = item.taxPercentage != null ? Number(item.taxPercentage) : 0;
//       const discPct   = item.discountPercentage != null ? Number(item.discountPercentage) : 0;
//       const discAmt   = item.discountAmount != null
//         ? Number(item.discountAmount)
//         : +(quoted * discPct / 100).toFixed(2);
//       const final     = item.finalizedPrice != null
//         ? Number(item.finalizedPrice)
//         : +(quoted - discAmt).toFixed(2);
//       const lineTotal = +(final * qty).toFixed(2);
//       const taxAmt    = item.taxAmount != null
//         ? Number(item.taxAmount)
//         : +(lineTotal * taxPct / 100).toFixed(2);

//       return {
//         id: item.id,
//         rfqItemId: item.rfqItemId,
//         itemDescription: item.itemDescription || item.description || 'Item',
//         itemCode: item.itemCode,
//         specifications: item.specifications,
//         quantity: qty,
//         uom: item.uom || 'Nos',
//         actualQuotedPrice: quoted,
//         discountPercentage: discPct,
//         discountAmount: discAmt,
//         finalizedPrice: final,
//         lineTotal: lineTotal,
//         taxPercentage: taxPct,
//         taxAmount: taxAmt,
//         lineTotalWithTax: +(lineTotal + taxAmt).toFixed(2),
//         priceExceedsQuoted: false
//       } as NegotiationLineItem;
//     });

//     this.recalculateTotals();
//   }

//   // ✅ Helper to get symbol from code (fallback only)
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

//   // ============================================================
//   // INLINE EDITING
//   // ============================================================

//   onDiscountChange(item: NegotiationLineItem): void {
//     if (item.discountPercentage < 0)   item.discountPercentage = 0;
//     if (item.discountPercentage > 100) item.discountPercentage = 100;
//     item.discountAmount     = +(item.actualQuotedPrice * item.discountPercentage / 100).toFixed(2);
//     item.finalizedPrice     = +(item.actualQuotedPrice - item.discountAmount).toFixed(2);
//     item.priceExceedsQuoted = false;
//     this.recalculateLineItem(item);
//     this.recalculateTotals();
//   }

//   onDiscountAmountChange(item: NegotiationLineItem): void {
//     if (item.discountAmount < 0) item.discountAmount = 0;
//     if (item.discountAmount > item.actualQuotedPrice) item.discountAmount = item.actualQuotedPrice;
//     item.discountPercentage = item.actualQuotedPrice > 0
//       ? +((item.discountAmount / item.actualQuotedPrice) * 100).toFixed(2) : 0;
//     item.finalizedPrice     = +(item.actualQuotedPrice - item.discountAmount).toFixed(2);
//     item.priceExceedsQuoted = false;
//     this.recalculateLineItem(item);
//     this.recalculateTotals();
//   }

//   onFinalizedPriceChange(item: NegotiationLineItem): void {
//     if (item.finalizedPrice < 0) item.finalizedPrice = 0;
//     if (item.finalizedPrice > item.actualQuotedPrice) {
//       item.priceExceedsQuoted = true;
//       item.finalizedPrice     = item.actualQuotedPrice;
//     } else {
//       item.priceExceedsQuoted = false;
//     }
//     if (item.finalizedPrice >= item.actualQuotedPrice) {
//       item.discountPercentage = 0;
//       item.discountAmount     = 0;
//     } else {
//       item.discountAmount     = +(item.actualQuotedPrice - item.finalizedPrice).toFixed(2);
//       item.discountPercentage = item.actualQuotedPrice > 0
//         ? +((item.discountAmount / item.actualQuotedPrice) * 100).toFixed(2) : 0;
//     }
//     this.recalculateLineItem(item);
//     this.recalculateTotals();
//   }

//   onItemTaxChange(item: NegotiationLineItem): void {
//     if (item.taxPercentage < 0)   item.taxPercentage = 0;
//     if (item.taxPercentage > 100) item.taxPercentage = 100;
//     this.recalculateLineItem(item);
//     this.recalculateTotals();
//   }

//   private recalculateLineItem(item: NegotiationLineItem): void {
//     item.lineTotal        = +(item.finalizedPrice * item.quantity).toFixed(2);
//     item.taxAmount        = +(item.lineTotal * item.taxPercentage / 100).toFixed(2);
//     item.lineTotalWithTax = +(item.lineTotal + item.taxAmount).toFixed(2);
//   }

//   resetItem(item: NegotiationLineItem): void {
//     item.discountPercentage = 0;
//     item.discountAmount     = 0;
//     item.finalizedPrice     = item.actualQuotedPrice;
//     item.priceExceedsQuoted = false;
//     this.recalculateLineItem(item);
//     this.recalculateTotals();
//   }

//   resetAll(): void {
//     this.lineItems.forEach(item => this.resetItem(item));
//     this.overallDiscountPercentage = 0;
//     this.overallDiscountAmount     = 0;
//     this.roundingOff               = 0;
//     this.roundingSign              = '-';
//     this.recalculateTotals();
//     this.messageService.showMessage('info', 'Info', 'All prices have been reset to original quoted prices.');
//   }

//   private recalculateTotals(): void {
//     this.subtotalQuoted      = +this.lineItems.reduce((s, i) => s + i.actualQuotedPrice * i.quantity, 0).toFixed(2);
//     this.subtotalFinalized   = +this.lineItems.reduce((s, i) => s + i.lineTotal, 0).toFixed(2);
//     this.totalDiscountAmount = +(this.subtotalQuoted - this.subtotalFinalized).toFixed(2);
//     this.totalTaxAmount      = +this.lineItems.reduce((s, i) => s + i.taxAmount, 0).toFixed(2);
//     this.grandTotalQuoted    = +(this.subtotalQuoted).toFixed(2);
//     this.grandTotalFinalized = +(this.subtotalFinalized + this.totalTaxAmount).toFixed(2);
//     this.recalculateOverallDiscount();
//   }

//   // ============================================================
//   // OVERALL DISCOUNT
//   // ============================================================

//   onOverallDiscountPctChange(): void {
//     if (this.overallDiscountPercentage < 0)   this.overallDiscountPercentage = 0;
//     if (this.overallDiscountPercentage > 100) this.overallDiscountPercentage = 100;
//     this.overallDiscountAmount =
//       +(this.subtotalFinalized * this.overallDiscountPercentage / 100).toFixed(2);
//     this.recalculateNetPayable();
//   }

//   onOverallDiscountAmtChange(): void {
//     if (this.overallDiscountAmount < 0) this.overallDiscountAmount = 0;
//     if (this.overallDiscountAmount > this.subtotalFinalized)
//       this.overallDiscountAmount = this.subtotalFinalized;
//     this.overallDiscountPercentage = this.subtotalFinalized > 0
//       ? +((this.overallDiscountAmount / this.subtotalFinalized) * 100).toFixed(2) : 0;
//     this.recalculateNetPayable();
//   }

//   private recalculateOverallDiscount(): void {
//     this.overallDiscountAmount =
//       +(this.subtotalFinalized * this.overallDiscountPercentage / 100).toFixed(2);
//     this.recalculateNetPayable();
//   }

//   // ============================================================
//   // ROUNDING OFF
//   // ============================================================

//   get roundingOffDisplay(): number { return Math.abs(this.roundingOff); }
//   set roundingOffDisplay(val: number) {
//     const abs = val > 0 ? val : 0;
//     this.roundingOff = this.roundingSign === '-' ? -abs : +abs;
//   }
//   get roundingOffAbsDisplay(): number { return Math.abs(this.roundingOff); }

//   toggleRoundingSign(): void {
//     this.roundingSign = this.roundingSign === '-' ? '+' : '-';
//     if (this.roundingOff !== 0) this.roundingOff = -this.roundingOff;
//     this.recalculateNetPayable();
//   }

//   onRoundingOffChange(): void { this.recalculateNetPayable(); }

//   clearRoundingOff(): void {
//     this.roundingOff  = 0;
//     this.roundingSign = '-';
//     this.recalculateNetPayable();
//   }

//   // ============================================================
//   // NET PAYABLE
//   // ============================================================

//   private recalculateNetPayable(): void {
//     const afterOverall = +(this.subtotalFinalized - this.overallDiscountAmount).toFixed(2);
//     this.netPayable    = +(afterOverall + this.totalTaxAmount + this.roundingOff).toFixed(2);
//   }

//   get combinedDiscountAmount(): number {
//     return +(this.totalDiscountAmount + this.overallDiscountAmount).toFixed(2);
//   }

//   get combinedDiscountPercent(): number {
//     return this.subtotalQuoted > 0
//       ? +((this.combinedDiscountAmount / this.subtotalQuoted) * 100).toFixed(2) : 0;
//   }

//   resetOverallDiscount(): void {
//     this.overallDiscountPercentage = 0;
//     this.overallDiscountAmount     = 0;
//     this.recalculateNetPayable();
//   }

//   // ============================================================
//   // FORMAT CURRENCY — uses location currency from backend
//   // ============================================================

//   formatCurrency(amount: number): string {
//     const sym = this.currencySymbol;
//     const val = Number(amount || 0);
//     const formatted = val.toLocaleString('en-IN', {
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2
//     });
//     // For right-to-left currencies put symbol after
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     if (rtlCodes.includes(this.currencyCode)) {
//       return `${formatted} ${sym}`;
//     }
//     return `${sym} ${formatted}`;
//   }

//   // ============================================================
//   // SAVE
//   // ============================================================

//   saveNegotiation(): void {
//     if (!this.negotiationId) return;
//     this.isSaving = true;

//     const payload = {
//       lineItems: this.lineItems.map(i => ({
//         id: i.id,
//         taxPercentage: i.taxPercentage,
//         discountPercentage: i.discountPercentage,
//         discountAmount: i.discountAmount,
//         finalizedPrice: i.finalizedPrice
//       })),
//       terms: this.terms,
//       overallDiscountAmount:     this.overallDiscountAmount,
//       overallDiscountPercentage: this.overallDiscountPercentage,
//       roundingOff:               this.roundingOff,
//       netPayable:                this.netPayable
//       // ✅ No currency in payload — currency is stored on Location, not negotiation
//     };

//     this.dataService.savePONegotiation(this.negotiationId, payload).subscribe({
//       next: (res: any) => {
//         const data = res?.data || res;
//         if (data) this.populateFromNegotiation(data);
//         this.messageService.showMessage('success', 'Success', 'Price negotiation saved successfully.');
//         this.isSaving = false;
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', 'Error saving: ' + (err?.error?.message || 'Failed to save'));
//         this.isSaving = false;
//       }
//     });
//   }

//   // ============================================================
//   // CREATE PO
//   // ============================================================

//   createPO(): void {
//     if (!this.negotiationId) return;
//     const invalidItems = this.lineItems.filter(i => i.priceExceedsQuoted);
//     if (invalidItems.length > 0) {
//       this.messageService.showMessage('error', 'Validation Error',
//         'Some items have a Final Rate exceeding the Quoted Rate. Please fix before creating a PO.');
//       return;
//     }
//     this.messageService.showMessage('info', 'PO Creation',
//       `Creating Purchase Order for ${this.supplierName}`);

//     const userId = Number(localStorage.getItem('userId') || localStorage.getItem('buyerId'));
//     this.isCreatingPO = true;

//     const savePayload = {
//       lineItems: this.lineItems.map(i => ({
//         id: i.id,
//         taxPercentage: i.taxPercentage,
//         discountPercentage: i.discountPercentage,
//         discountAmount: i.discountAmount,
//         finalizedPrice: i.finalizedPrice
//       })),
//       terms: this.terms,
//       overallDiscountAmount:     this.overallDiscountAmount,
//       overallDiscountPercentage: this.overallDiscountPercentage,
//       roundingOff:               this.roundingOff,
//       netPayable:                this.netPayable
//     };

//     this.dataService.savePONegotiation(this.negotiationId, savePayload).subscribe({
//       next: () => {
//         this.dataService.createPOFromNegotiation(this.negotiationId, userId).subscribe({
//           next: (res: any) => {
//             const po = res?.data || res;
//             if (po?.id) {
//               this.messageService.showMessage('success', 'Success',
//                 `PO Created Successfully!\n\nPO Number: ${po.poNumber}\nTotal: ${this.currencySymbol}${po.grandTotal}`);
//               this.router.navigate(['/po-details', po.id]);
//             } else {
//               this.messageService.showMessage('error', 'Error', 'Error: ' + (res?.message || 'Failed to create PO'));
//             }
//             this.isCreatingPO = false;
//           },
//           error: (err: any) => {
//             this.messageService.showMessage('error', 'Error',
//               'Error creating PO: ' + (err?.error?.message || 'Failed to create PO'));
//             this.isCreatingPO = false;
//           }
//         });
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error',
//           'Error saving before PO creation: ' + (err?.error?.message || 'Pre-save failed'));
//         this.isCreatingPO = false;
//       }
//     });
//   }

//   // ============================================================
//   // HELPERS
//   // ============================================================

//   get hasValidationErrors(): boolean { return this.lineItems.some(i => i.priceExceedsQuoted); }

//   getDiscountPercent(): number {
//     if (this.subtotalQuoted === 0) return 0;
//     return +((this.totalDiscountAmount / this.subtotalQuoted) * 100).toFixed(2);
//   }

//   trackByItemId(_: number, item: NegotiationLineItem): number { return item.id; }

//   retryInit(): void {
//     this.negotiation  = null;
//     this.errorMessage = '';
//     this.initNegotiation();
//   }

//   goBack(): void   { this.router.navigate(['/supplier-selection', this.rfqId]); }
//   goToList(): void { this.router.navigate(['/po-list']); }
// }




import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, SpinnerComponent, TableModule
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';

interface NegotiationLineItem {
  id: number;
  rfqItemId: number;
  itemDescription: string;
  itemCode?: string;
  specifications?: string;
  quantity: number;
  uom: string;
  actualQuotedPrice: number;
  discountPercentage: number;
  discountAmount: number;
  finalizedPrice: number;
  lineTotal: number;
  taxPercentage: number;
  taxAmount: number;
  lineTotalWithTax: number;
  priceExceedsQuoted: boolean;
}

@Component({
  selector: 'app-po-negotiation',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent,
    ButtonDirective, BadgeComponent, SpinnerComponent, TableModule
  ],
  templateUrl: './po-negotiation.component.html',
  styleUrls: ['./po-negotiation.component.css']
})
export class PONegotiationComponent implements OnInit {

  rfqId: number = 0;
  supplierId: number = 0;
  selectionId: number = 0;
  negotiationId: number = 0;

  isLoading: boolean = false;
  isSaving: boolean = false;
  isCreatingPO: boolean = false;

  negotiation: any = null;
  lineItems: NegotiationLineItem[] = [];

  terms: string = '';

  subtotalQuoted: number = 0;
  subtotalFinalized: number = 0;
  totalDiscountAmount: number = 0;
  totalTaxAmount: number = 0;
  grandTotalQuoted: number = 0;
  grandTotalFinalized: number = 0;

  overallDiscountPercentage: number = 0;
  overallDiscountAmount: number = 0;

  roundingOff: number = 0;
  roundingSign: '+' | '-' = '-';

  netPayable: number = 0;

  supplierName: string = '';
  errorMessage: string = '';

  // ✅ Live exchange rates for converting supplier currency → buyer's original currency
  // Only used when supplier is cross-border (e.g. buyer=India/INR, supplier=Malaysia/USD)
  exchangeRates: Map<string, number> = new Map();
  exchangeRatesLoaded: boolean = false;
  // Buyer's original/home currency (INR for India, AED for Dubai, etc.)
  // This is the currency we CONVERT INTO for cross-border display
  buyerOriginalCurrencyCode: string = 'INR';
  buyerOriginalCurrencySymbol: string = '₹';

  // ✅ Currency — resolved per the same-country rule:
  //   buyer country == supplier country  →  buyer's own currency (INR, EUR…)
  //   different countries                →  USD
  // Source priority:
  //   1. Quote comparison supplier entry (most reliable — already resolved)
  //   2. Negotiation response currencyCode (backend may still return buyer currency)
  currencyCode: string = 'INR';
  currencySymbol: string = '₹';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.rfqId       = Number(this.route.snapshot.paramMap.get('rfqId'));
    this.supplierId  = Number(this.route.snapshot.paramMap.get('supplierId'));
    this.selectionId = Number(this.route.snapshot.paramMap.get('selectionId'));

    if (!this.rfqId || !this.supplierId) {
      this.messageService.showMessage('error', 'Error', 'Error: Missing required parameters');
      this.router.navigate(['/rfq-dashboard']);
      return;
    }

    // ✅ Load the correct per-supplier currency FIRST, then load negotiation
    this.loadSupplierCurrencyThenInit();
    this.loadBuyerOriginalCurrency();
  }

  // ============================================================
  // ✅ CURRENCY RESOLUTION
  // Load from quote comparison — this already has the resolved currency
  // (QuoteComparisonService.buildSupplierInfoList() applied same-country rule)
  // ============================================================

  private loadSupplierCurrencyThenInit(): void {
    this.dataService.getQuoteComparison(this.rfqId).subscribe({
      next: (res: any) => {
        const data = res?.data;
        if (data?.suppliers && Array.isArray(data.suppliers)) {
          // Find this specific supplier in the comparison response
          const supplierEntry = data.suppliers.find(
            (s: any) => Number(s.supplierId) === Number(this.supplierId)
          );
          if (supplierEntry?.currencyCode) {
            this.currencyCode   = supplierEntry.currencyCode;
            this.currencySymbol = supplierEntry.currencySymbol || this.getSymbolForCode(supplierEntry.currencyCode);
            console.log(`[PONegotiation] Currency resolved from quote comparison: ${this.currencyCode} for supplier ${this.supplierId}`);
          }
        }
        this.initNegotiation();
      },
      error: () => {
        // If quote comparison fails, proceed with default (negotiation may override)
        this.initNegotiation();
      }
    });
  }

  // ============================================================
  // INIT
  // ============================================================

  initNegotiation(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.dataService.getPONegotiationsByRFQ(this.rfqId).subscribe({
      next: (response: any) => {
        let list: any[] = [];
        if (Array.isArray(response))            list = response;
        else if (Array.isArray(response?.data)) list = response.data;

        const matches = list.filter((n: any) =>
          Number(n.supplierId) === Number(this.supplierId)
        );

        let existing: any = null;
        if (matches.length > 0) {
          if (this.selectionId) {
            existing = matches.find((n: any) => Number(n.finalSelectionId) === Number(this.selectionId));
          }
          if (!existing) existing = matches.find((n: any) => ['DRAFT', 'FINALIZED'].includes(n.status));
          if (!existing) existing = matches[0];
        }

        if (existing?.id) {
          this.negotiationId = existing.id;
          this.populateFromNegotiation(existing);
          this.isLoading = false;
        } else {
          this.createNewNegotiation();
        }
      },
      error: () => this.createNewNegotiation()
    });
  }

  private createNewNegotiation(): void {
    this.dataService.initPONegotiation(this.rfqId, this.supplierId).subscribe({
      next: (response: any) => {
        const data = response?.data || response;
        if (!data || !data.id) {
          this.errorMessage = 'Server returned an empty response. Please check if the supplier has submitted quotes for this RFQ.';
          this.isLoading = false;
          return;
        }
        this.negotiationId = data.id;
        this.populateFromNegotiation(data);
        this.isLoading = false;
      },
      error: (error: any) => {
        const backendMsg =
          error?.error?.message ||
          error?.error?.error  ||
          (typeof error?.error === 'string' ? error.error : null) ||
          error?.message ||
          'Failed to initialize price negotiation';
        this.errorMessage = backendMsg;
        this.messageService.showMessage('error', 'Error', 'Initialization Failed: ' + backendMsg);
        this.isLoading = false;
      }
    });
  }

  private populateFromNegotiation(data: any): void {
    if (!data) return;

    this.negotiation  = data;
    this.supplierName = data.supplierName || `Supplier #${this.supplierId}`;
    this.terms        = data.terms || data.paymentTerms || '';

    this.overallDiscountPercentage = data.overallDiscountPercentage || 0;
    this.overallDiscountAmount     = data.overallDiscountAmount || 0;

    const savedRounding = data.roundingOff || 0;
    this.roundingOff    = savedRounding;
    this.roundingSign   = savedRounding >= 0 ? '+' : '-';

    // ✅ Only use negotiation's currencyCode as fallback if we don't already have
    // a resolved currency from quote comparison (which is more reliable for cross-border).
    // We keep whatever was resolved in loadSupplierCurrencyThenInit() unless it's still default.
    if (data.currencyCode && this.currencyCode === 'INR' && data.currencyCode !== 'INR') {
      // Backend explicitly returned a non-INR currency — use it
      this.currencyCode   = data.currencyCode;
      this.currencySymbol = data.currencySymbol || this.getSymbolForCode(data.currencyCode);
    } else if (data.currencyCode && this.currencyCode === 'INR') {
      // Both are INR — keep as is (may legitimately be INR for India supplier)
      // currencyCode already set from quote comparison or default
    }
    // If quote comparison already resolved to USD (cross-border), keep that — don't override with backend INR

    this.lineItems = (data.lineItems || []).map((item: any) => {
      const quoted    = Number(item.actualQuotedPrice) || 0;
      const qty       = Number(item.quantity) || 0;
      const taxPct    = item.taxPercentage != null ? Number(item.taxPercentage) : 0;
      const discPct   = item.discountPercentage != null ? Number(item.discountPercentage) : 0;
      const discAmt   = item.discountAmount != null
        ? Number(item.discountAmount)
        : +(quoted * discPct / 100).toFixed(2);
      const final     = item.finalizedPrice != null
        ? Number(item.finalizedPrice)
        : +(quoted - discAmt).toFixed(2);
      const lineTotal = +(final * qty).toFixed(2);
      const taxAmt    = item.taxAmount != null
        ? Number(item.taxAmount)
        : +(lineTotal * taxPct / 100).toFixed(2);

      return {
        id: item.id,
        rfqItemId: item.rfqItemId,
        itemDescription: item.itemDescription || item.description || 'Item',
        itemCode: item.itemCode,
        specifications: item.specifications,
        quantity: qty,
        uom: item.uom || 'Nos',
        actualQuotedPrice: quoted,
        discountPercentage: discPct,
        discountAmount: discAmt,
        finalizedPrice: final,
        lineTotal: lineTotal,
        taxPercentage: taxPct,
        taxAmount: taxAmt,
        lineTotalWithTax: +(lineTotal + taxAmt).toFixed(2),
        priceExceedsQuoted: false
      } as NegotiationLineItem;
    });

    this.recalculateTotals();
  }

  // ============================================================
  // ✅ BUYER ORIGINAL CURRENCY + EXCHANGE RATES
  // ============================================================

  /**
   * Load buyer's home/original currency from quote comparison page-level data.
   * e.g. India buyer → INR, Dubai buyer → AED
   * This is what we convert USD amounts INTO for cross-border display.
   */
  private loadBuyerOriginalCurrency(): void {
    this.dataService.getQuoteComparison(this.rfqId).subscribe({
      next: (res: any) => {
        const data = res?.data;
        if (data?.currencyCode) {
          this.buyerOriginalCurrencyCode   = data.currencyCode;
          this.buyerOriginalCurrencySymbol = data.currencySymbol || this.getSymbolForCode(data.currencyCode);
          this.loadExchangeRates(data.currencyCode);
        }
      },
      error: () => {}
    });
  }

  /**
   * Fetch live exchange rates with buyer's currency as base.
   * e.g. base=INR → rates.USD=0.012 → 1 USD = 1/0.012 ≈ 83.3 INR
   */
  loadExchangeRates(baseCurrency: string = 'INR'): void {
    fetch(`https://open.er-api.com/v6/latest/${baseCurrency}`)
      .then(r => r.json())
      .then((data: any) => {
        if (data?.result === 'success' && data?.rates) {
          this.exchangeRates.clear();
          Object.entries(data.rates as Record<string, number>).forEach(([code, rate]) => {
            if (rate && rate > 0) this.exchangeRates.set(code, 1 / rate);
          });
          this.exchangeRates.set(baseCurrency, 1);
          this.exchangeRatesLoaded = true;
        }
      })
      .catch(() => {});
  }

  /**
   * ✅ True if this negotiation is cross-border (supplier uses different currency than buyer).
   * e.g. India buyer (INR) + Malaysia supplier (USD) → true
   */
  get isCrossBorder(): boolean {
    return this.currencyCode !== this.buyerOriginalCurrencyCode;
  }

  /**
   * ✅ Convert a USD (supplier) amount to buyer's original currency (INR).
   * Returns null if not cross-border or rate not loaded.
   */
  convertToBuyerCurrency(amount: number): number | null {
    if (!this.isCrossBorder || !this.exchangeRatesLoaded || !amount) return null;
    const rate = this.exchangeRates.get(this.currencyCode) || 0;
    return rate > 0 ? Number((amount * rate).toFixed(2)) : null;
  }

  /**
   * ✅ Format an amount in the buyer's original currency.
   * e.g. "₹ 20,875.00"
   */
  formatBuyerCurrency(amount: number | null): string {
    if (amount === null || amount === undefined) return '—';
    const val = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    return `${this.buyerOriginalCurrencySymbol} ${formatted}`;
  }

  /**
   * ✅ Exchange rate label for column header.
   * e.g. "1 USD = ₹ 83.50"
   */
  get exchangeRateLabel(): string {
    if (!this.isCrossBorder || !this.exchangeRatesLoaded) return '';
    const rate = this.exchangeRates.get(this.currencyCode) || 0;
    if (!rate) return '';
    return `1 ${this.currencyCode} = ${this.buyerOriginalCurrencySymbol} ${rate.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  }

  private getSymbolForCode(code: string): string {
    const map: Record<string, string> = {
      'INR': '₹', 'USD': '$', 'EUR': '€', 'GBP': '£',
      'AED': 'د.إ', 'SGD': 'S$', 'JPY': '¥', 'CNY': '¥',
      'CHF': 'Fr', 'CAD': 'C$', 'AUD': 'A$', 'NZD': 'NZ$',
      'SAR': 'ر.س', 'QAR': 'ر.ق', 'KWD': 'د.ك', 'BHD': '.د.ب',
      'OMR': 'ر.ع.', 'MYR': 'RM', 'THB': '฿', 'IDR': 'Rp',
      'PKR': '₨', 'BDT': '৳', 'LKR': '₨', 'NPR': '₨',
    };
    return map[code] || code;
  }

  // ============================================================
  // INLINE EDITING
  // ============================================================

  onDiscountChange(item: NegotiationLineItem): void {
    if (item.discountPercentage < 0)   item.discountPercentage = 0;
    if (item.discountPercentage > 100) item.discountPercentage = 100;
    item.discountAmount     = +(item.actualQuotedPrice * item.discountPercentage / 100).toFixed(2);
    item.finalizedPrice     = +(item.actualQuotedPrice - item.discountAmount).toFixed(2);
    item.priceExceedsQuoted = false;
    this.recalculateLineItem(item);
    this.recalculateTotals();
  }

  onDiscountAmountChange(item: NegotiationLineItem): void {
    if (item.discountAmount < 0) item.discountAmount = 0;
    if (item.discountAmount > item.actualQuotedPrice) item.discountAmount = item.actualQuotedPrice;
    item.discountPercentage = item.actualQuotedPrice > 0
      ? +((item.discountAmount / item.actualQuotedPrice) * 100).toFixed(2) : 0;
    item.finalizedPrice     = +(item.actualQuotedPrice - item.discountAmount).toFixed(2);
    item.priceExceedsQuoted = false;
    this.recalculateLineItem(item);
    this.recalculateTotals();
  }

  onFinalizedPriceChange(item: NegotiationLineItem): void {
    if (item.finalizedPrice < 0) item.finalizedPrice = 0;
    if (item.finalizedPrice > item.actualQuotedPrice) {
      item.priceExceedsQuoted = true;
      item.finalizedPrice     = item.actualQuotedPrice;
    } else {
      item.priceExceedsQuoted = false;
    }
    if (item.finalizedPrice >= item.actualQuotedPrice) {
      item.discountPercentage = 0;
      item.discountAmount     = 0;
    } else {
      item.discountAmount     = +(item.actualQuotedPrice - item.finalizedPrice).toFixed(2);
      item.discountPercentage = item.actualQuotedPrice > 0
        ? +((item.discountAmount / item.actualQuotedPrice) * 100).toFixed(2) : 0;
    }
    this.recalculateLineItem(item);
    this.recalculateTotals();
  }

  onItemTaxChange(item: NegotiationLineItem): void {
    if (item.taxPercentage < 0)   item.taxPercentage = 0;
    if (item.taxPercentage > 100) item.taxPercentage = 100;
    this.recalculateLineItem(item);
    this.recalculateTotals();
  }

  private recalculateLineItem(item: NegotiationLineItem): void {
    item.lineTotal        = +(item.finalizedPrice * item.quantity).toFixed(2);
    item.taxAmount        = +(item.lineTotal * item.taxPercentage / 100).toFixed(2);
    item.lineTotalWithTax = +(item.lineTotal + item.taxAmount).toFixed(2);
  }

  resetItem(item: NegotiationLineItem): void {
    item.discountPercentage = 0;
    item.discountAmount     = 0;
    item.finalizedPrice     = item.actualQuotedPrice;
    item.priceExceedsQuoted = false;
    this.recalculateLineItem(item);
    this.recalculateTotals();
  }

  resetAll(): void {
    this.lineItems.forEach(item => this.resetItem(item));
    this.overallDiscountPercentage = 0;
    this.overallDiscountAmount     = 0;
    this.roundingOff               = 0;
    this.roundingSign              = '-';
    this.recalculateTotals();
    this.messageService.showMessage('info', 'Info', 'All prices have been reset to original quoted prices.');
  }

  private recalculateTotals(): void {
    this.subtotalQuoted      = +this.lineItems.reduce((s, i) => s + i.actualQuotedPrice * i.quantity, 0).toFixed(2);
    this.subtotalFinalized   = +this.lineItems.reduce((s, i) => s + i.lineTotal, 0).toFixed(2);
    this.totalDiscountAmount = +(this.subtotalQuoted - this.subtotalFinalized).toFixed(2);
    this.totalTaxAmount      = +this.lineItems.reduce((s, i) => s + i.taxAmount, 0).toFixed(2);
    this.grandTotalQuoted    = +(this.subtotalQuoted).toFixed(2);
    this.grandTotalFinalized = +(this.subtotalFinalized + this.totalTaxAmount).toFixed(2);
    this.recalculateOverallDiscount();
  }

  // ============================================================
  // OVERALL DISCOUNT
  // ============================================================

  onOverallDiscountPctChange(): void {
    if (this.overallDiscountPercentage < 0)   this.overallDiscountPercentage = 0;
    if (this.overallDiscountPercentage > 100) this.overallDiscountPercentage = 100;
    this.overallDiscountAmount =
      +(this.subtotalFinalized * this.overallDiscountPercentage / 100).toFixed(2);
    this.recalculateNetPayable();
  }

  onOverallDiscountAmtChange(): void {
    if (this.overallDiscountAmount < 0) this.overallDiscountAmount = 0;
    if (this.overallDiscountAmount > this.subtotalFinalized)
      this.overallDiscountAmount = this.subtotalFinalized;
    this.overallDiscountPercentage = this.subtotalFinalized > 0
      ? +((this.overallDiscountAmount / this.subtotalFinalized) * 100).toFixed(2) : 0;
    this.recalculateNetPayable();
  }

  private recalculateOverallDiscount(): void {
    this.overallDiscountAmount =
      +(this.subtotalFinalized * this.overallDiscountPercentage / 100).toFixed(2);
    this.recalculateNetPayable();
  }

  // ============================================================
  // ROUNDING OFF
  // ============================================================

  get roundingOffDisplay(): number { return Math.abs(this.roundingOff); }
  set roundingOffDisplay(val: number) {
    const abs = val > 0 ? val : 0;
    this.roundingOff = this.roundingSign === '-' ? -abs : +abs;
  }
  get roundingOffAbsDisplay(): number { return Math.abs(this.roundingOff); }

  toggleRoundingSign(): void {
    this.roundingSign = this.roundingSign === '-' ? '+' : '-';
    if (this.roundingOff !== 0) this.roundingOff = -this.roundingOff;
    this.recalculateNetPayable();
  }

  onRoundingOffChange(): void { this.recalculateNetPayable(); }

  clearRoundingOff(): void {
    this.roundingOff  = 0;
    this.roundingSign = '-';
    this.recalculateNetPayable();
  }

  // ============================================================
  // NET PAYABLE
  // ============================================================

  private recalculateNetPayable(): void {
    const afterOverall = +(this.subtotalFinalized - this.overallDiscountAmount).toFixed(2);
    this.netPayable    = +(afterOverall + this.totalTaxAmount + this.roundingOff).toFixed(2);
  }

  get combinedDiscountAmount(): number {
    return +(this.totalDiscountAmount + this.overallDiscountAmount).toFixed(2);
  }

  get combinedDiscountPercent(): number {
    return this.subtotalQuoted > 0
      ? +((this.combinedDiscountAmount / this.subtotalQuoted) * 100).toFixed(2) : 0;
  }

  resetOverallDiscount(): void {
    this.overallDiscountPercentage = 0;
    this.overallDiscountAmount     = 0;
    this.recalculateNetPayable();
  }

  // ============================================================
  // ✅ FORMAT CURRENCY — uses per-supplier resolved currency
  // For Malaysia supplier: $ (USD), for India supplier: ₹ (INR)
  // ============================================================

  formatCurrency(amount: number): string {
    const sym = this.currencySymbol;
    const val = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${sym}`;
    return `${sym} ${formatted}`;
  }

  // ============================================================
  // SAVE
  // ============================================================

  saveNegotiation(): void {
    if (!this.negotiationId) return;
    this.isSaving = true;

    const payload = {
      lineItems: this.lineItems.map(i => ({
        id: i.id,
        taxPercentage:      i.taxPercentage,
        discountPercentage: i.discountPercentage,
        discountAmount:     i.discountAmount,
        finalizedPrice:     i.finalizedPrice
      })),
      terms:                     this.terms,
      overallDiscountAmount:     this.overallDiscountAmount,
      overallDiscountPercentage: this.overallDiscountPercentage,
      roundingOff:               this.roundingOff,
      netPayable:                this.netPayable
    };

    this.dataService.savePONegotiation(this.negotiationId, payload).subscribe({
      next: (res: any) => {
        const data = res?.data || res;
        if (data) this.populateFromNegotiation(data);
        this.messageService.showMessage('success', 'Success', 'Price negotiation saved successfully.');
        this.isSaving = false;
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', 'Error saving: ' + (err?.error?.message || 'Failed to save'));
        this.isSaving = false;
      }
    });
  }

  // ============================================================
  // CREATE PO
  // ============================================================

  createPO(): void {
    if (!this.negotiationId) return;
    const invalidItems = this.lineItems.filter(i => i.priceExceedsQuoted);
    if (invalidItems.length > 0) {
      this.messageService.showMessage('error', 'Validation Error',
        'Some items have a Final Rate exceeding the Quoted Rate. Please fix before creating a PO.');
      return;
    }
    this.messageService.showMessage('info', 'PO Creation',
      `Creating Purchase Order for ${this.supplierName}`);

    const userId = Number(localStorage.getItem('userId') || localStorage.getItem('buyerId'));
    this.isCreatingPO = true;

    const savePayload = {
      lineItems: this.lineItems.map(i => ({
        id: i.id,
        taxPercentage:      i.taxPercentage,
        discountPercentage: i.discountPercentage,
        discountAmount:     i.discountAmount,
        finalizedPrice:     i.finalizedPrice
      })),
      terms:                     this.terms,
      overallDiscountAmount:     this.overallDiscountAmount,
      overallDiscountPercentage: this.overallDiscountPercentage,
      roundingOff:               this.roundingOff,
      netPayable:                this.netPayable
    };

    this.dataService.savePONegotiation(this.negotiationId, savePayload).subscribe({
      next: () => {
        this.dataService.createPOFromNegotiation(this.negotiationId, userId).subscribe({
          next: (res: any) => {
            const po = res?.data || res;
            if (po?.id) {
              this.messageService.showMessage('success', 'Success',
                `PO Created Successfully!\n\nPO Number: ${po.poNumber}\nTotal: ${this.currencySymbol}${po.grandTotal}`);
              this.router.navigate(['/po-details', po.id]);
            } else {
              this.messageService.showMessage('error', 'Error', 'Error: ' + (res?.message || 'Failed to create PO'));
            }
            this.isCreatingPO = false;
          },
          error: (err: any) => {
            this.messageService.showMessage('error', 'Error',
              'Error creating PO: ' + (err?.error?.message || 'Failed to create PO'));
            this.isCreatingPO = false;
          }
        });
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error',
          'Error saving before PO creation: ' + (err?.error?.message || 'Pre-save failed'));
        this.isCreatingPO = false;
      }
    });
  }

  // ============================================================
  // HELPERS
  // ============================================================

  get hasValidationErrors(): boolean { return this.lineItems.some(i => i.priceExceedsQuoted); }

  getDiscountPercent(): number {
    if (this.subtotalQuoted === 0) return 0;
    return +((this.totalDiscountAmount / this.subtotalQuoted) * 100).toFixed(2);
  }

  trackByItemId(_: number, item: NegotiationLineItem): number { return item.id; }

  retryInit(): void {
    this.negotiation  = null;
    this.errorMessage = '';
    this.initNegotiation();
  }

  goBack(): void   { this.router.navigate(['/supplier-selection', this.rfqId]); }
  goToList(): void { this.router.navigate(['/po-list']); }
}