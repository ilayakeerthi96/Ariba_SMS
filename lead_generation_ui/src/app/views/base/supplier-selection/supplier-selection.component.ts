
// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ActivatedRoute, Router } from '@angular/router';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, SpinnerComponent
// } from '@coreui/angular';
// import { MessageService } from '../../../shared/service/message.service';
// import { DataService } from '../../../shared/service/DataService';

// interface SupplierRanking {
//   supplierId: number;
//   supplierName: string;
//   contactEmail: string;
//   rank: number;
//   finalScore: number;
//   totalQuotedAmount: number;
//   isTopRecommendation: boolean;
//   quoteWeightage: number;
//   manualScore: number;
// }

// @Component({
//   selector: 'app-supplier-selection',
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent,
//     ButtonDirective, BadgeComponent, SpinnerComponent
//   ],
//   templateUrl: './supplier-selection.component.html',
//   styleUrls: ['./supplier-selection.component.css']
// })
// export class SupplierSelectionComponent implements OnInit {

//   rfqId: number = 0;
//   isLoading: boolean = false;
//   isSubmitting: boolean = false;

//   rankings: SupplierRanking[] = [];
//   systemRecommendedSupplierId: number | null = null;
//   existingSelection: any = null;

//   selectedSupplierId: number | null = null;
//   justification: string = '';
//   remarks: string = '';

//   // ✅ Currency — from buyer's location via backend
//   currencyCode: string = 'INR';
//   currencySymbol: string = '₹';

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private dataService: DataService,
//     private messageService: MessageService,
//   ) {}

//   ngOnInit(): void {
//     this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
//     if (!this.rfqId || isNaN(this.rfqId)) {
//       this.messageService.showMessage('error', 'Error', 'Error: Invalid RFQ ID');
//       this.router.navigate(['/rfq-dashboard']);
//       return;
//     }
//     this.loadCurrencyFromRFQ();
//     this.loadSelectionStatus();
//   }

//   // ✅ Load currency from the same quote comparison endpoint (has location currency)
//   private loadCurrencyFromRFQ(): void {
//     this.dataService.getQuoteComparison(this.rfqId).subscribe({
//       next: (res: any) => {
//         const data = res?.data;
//         if (data?.currencyCode) {
//           this.currencyCode   = data.currencyCode;
//           this.currencySymbol = data.currencySymbol || this.getSymbolForCode(data.currencyCode);
//         }
//       },
//       error: () => {}
//     });
//   }

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
//   // LOAD
//   // ============================================================

//   loadSelectionStatus(): void {
//     this.isLoading = true;
//     this.dataService.getSupplierSelectionStatus(this.rfqId).subscribe({
//       next: (response: any) => {
//         const data = response?.data || response;
//         this.systemRecommendedSupplierId = data?.systemRecommendedSupplierId || null;
//         this.rankings = (data?.rankings || []).sort((a: any, b: any) => a.rank - b.rank);
//         this.existingSelection = data?.existingSelection || null;
//         if (this.existingSelection) {
//           this.selectedSupplierId = this.existingSelection.supplierId;
//           this.justification      = this.existingSelection.justification || '';
//           this.remarks            = this.existingSelection.remarks || '';
//         }
//         this.isLoading = false;
//       },
//       error: () => this.loadRankingsFallback()
//     });
//   }

//   private loadRankingsFallback(): void {
//     this.dataService.getSupplierRankings(this.rfqId).subscribe({
//       next: (response: any) => {
//         const data = response?.data || response;
//         this.rankings = Array.isArray(data) ? data.sort((a: any, b: any) => a.rank - b.rank) : [];
//         if (this.rankings.length > 0) {
//           this.systemRecommendedSupplierId = this.rankings[0].supplierId;
//           this.rankings[0].isTopRecommendation = true;
//         }
//         this.isLoading = false;
//       },
//       error: () => {
//         this.messageService.showMessage('error', 'Error', 'Error: Could not load supplier rankings. Please run supplier evaluation first.');
//         this.isLoading = false;
//       }
//     });
//   }

//   // ============================================================
//   // SELECTION LOGIC
//   // ============================================================

//   selectSupplier(supplierId: number): void {
//     this.selectedSupplierId = supplierId;
//     if (this.isSystemRecommended(supplierId)) this.justification = '';
//   }

//   isSelected(supplierId: number): boolean { return this.selectedSupplierId === supplierId; }

//   isSystemRecommended(supplierId: number): boolean {
//     return supplierId === this.systemRecommendedSupplierId;
//   }

//   get isSelectingRecommended(): boolean {
//     return this.selectedSupplierId !== null && this.selectedSupplierId === this.systemRecommendedSupplierId;
//   }

//   get needsJustification(): boolean {
//     return this.selectedSupplierId !== null && this.selectedSupplierId !== this.systemRecommendedSupplierId;
//   }

//   get isFormValid(): boolean {
//     if (!this.selectedSupplierId) return false;
//     if (this.needsJustification && (!this.justification || this.justification.trim().length < 10)) return false;
//     return true;
//   }

//   getSelectedSupplier(): SupplierRanking | null {
//     return this.rankings.find(r => r.supplierId === this.selectedSupplierId) || null;
//   }

//   // ============================================================
//   // SUBMIT
//   // ============================================================

//   submitSelection(): void {
//     if (!this.isFormValid) {
//       if (!this.selectedSupplierId) {
//         this.messageService.showMessage('warning', 'Warning', 'Warning: Please select a supplier'); return;
//       }
//       if (this.needsJustification) {
//         this.messageService.showMessage('warning', 'Warning', 'Warning: Justification is required (minimum 10 characters)'); return;
//       }
//       return;
//     }

//     const userId = Number(localStorage.getItem('userId') || localStorage.getItem('buyerId'));
//     if (!userId) {
//       this.messageService.showMessage('error', 'Error', 'Error: User ID not found. Please login again.'); return;
//     }

//     const payload = {
//       rfqId: this.rfqId,
//       supplierId: this.selectedSupplierId!,
//       isSystemRecommended: this.isSelectingRecommended,
//       justification: this.needsJustification ? this.justification.trim() : undefined,
//       remarks: this.remarks.trim() || undefined,
//       selectedByUserId: userId
//     };

//     this.isSubmitting = true;
//     this.dataService.selectFinalSupplier(payload).subscribe({
//       next: (response: any) => {
//         if (response?.success || response?.data) {
//           const selectionId = response?.data?.id;
//           this.messageService.showMessage('success', 'Success',
//             `Supplier Selected!\n\n${this.getSelectedSupplier()?.supplierName} has been selected for this RFQ.`);
//           this.router.navigate(['/po-negotiation', this.rfqId, this.selectedSupplierId, selectionId || 0]);
//         } else {
//           this.messageService.showMessage('error', 'Error', 'Error: ' + (response?.message || 'Failed to save selection'));
//         }
//         this.isSubmitting = false;
//       },
//       error: (error: any) => {
//         const msg = error?.error?.message || error?.message || 'Failed to save supplier selection';
//         this.messageService.showMessage('error', 'Error', 'Error: ' + msg);
//         this.isSubmitting = false;
//       }
//     });
//   }

//   // ============================================================
//   // HELPERS
//   // ============================================================

//   getRankBadgeClass(rank: number): string {
//     if (rank === 1) return 'rank-gold';
//     if (rank === 2) return 'rank-silver';
//     if (rank === 3) return 'rank-bronze';
//     return 'rank-default';
//   }

//   getRankIcon(rank: number): string {
//     if (rank === 1) return '🥇';
//     if (rank === 2) return '🥈';
//     if (rank === 3) return '🥉';
//     return `#${rank}`;
//   }

//   getScoreColor(score: number): string {
//     if (score >= 80) return '#16a34a';
//     if (score >= 60) return '#d97706';
//     if (score >= 40) return '#ea580c';
//     return '#dc2626';
//   }

//   // ✅ formatCurrency uses buyer's location currency
//   formatCurrency(amount: number): string {
//     if (!amount) return `${this.currencySymbol} 0.00`;
//     const formatted = amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
//     return `${this.currencySymbol} ${formatted}`;
//   }

//   goBack(): void { this.router.navigate(['/quote-comparison', this.rfqId]); }
//   goToEvaluation(): void { this.router.navigate(['/supplier-evaluation', this.rfqId]); }
// }



import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, SpinnerComponent
} from '@coreui/angular';
import { MessageService } from '../../../shared/service/message.service';
import { DataService } from '../../../shared/service/DataService';

interface SupplierRanking {
  supplierId: number;
  supplierName: string;
  contactEmail: string;
  rank: number;
  finalScore: number;
  totalQuotedAmount: number;
  isTopRecommendation: boolean;
  quoteWeightage: number;
  manualScore: number;
}

@Component({
  selector: 'app-supplier-selection',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent,
    ButtonDirective, BadgeComponent, SpinnerComponent
  ],
  templateUrl: './supplier-selection.component.html',
  styleUrls: ['./supplier-selection.component.css']
})
export class SupplierSelectionComponent implements OnInit {

  rfqId: number = 0;
  isLoading: boolean = false;
  isSubmitting: boolean = false;

  rankings: SupplierRanking[] = [];
  systemRecommendedSupplierId: number | null = null;
  existingSelection: any = null;

  selectedSupplierId: number | null = null;
  justification: string = '';
  remarks: string = '';

  // ✅ Page-level currency (buyer's location — used in header badge only)
  currencyCode: string = 'INR';
  currencySymbol: string = '₹';

  // ✅ Per-supplier currency map — loaded from quote comparison
  private supplierCurrencyMap: Map<number, { code: string; symbol: string }> = new Map();

  // ✅ Live exchange rates — to convert USD → buyer's INR on supplier selection page
  exchangeRates: Map<string, number> = new Map();
  exchangeRatesLoaded: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
    if (!this.rfqId || isNaN(this.rfqId)) {
      this.messageService.showMessage('error', 'Error', 'Error: Invalid RFQ ID');
      this.router.navigate(['/rfq-dashboard']);
      return;
    }
    // ✅ Load currency map first, then load rankings
    this.loadCurrencyMapFromQuoteComparison();
    this.loadSelectionStatus();
    this.loadExchangeRates();
  }

  // ============================================================
  // ✅ CURRENCY MAP — from quote comparison (per-supplier resolved)
  // ============================================================

  private loadCurrencyMapFromQuoteComparison(): void {
    this.dataService.getQuoteComparison(this.rfqId).subscribe({
      next: (res: any) => {
        const data = res?.data;
        if (!data) return;

        // Page-level currency (buyer's location)
        if (data.currencyCode) {
          this.currencyCode   = data.currencyCode;
          this.currencySymbol = data.currencySymbol || this.getSymbolForCode(data.currencyCode);
        }

        // ✅ Build per-supplier map from supplier list
        // Each supplier already has currencyCode/currencySymbol resolved by backend
        if (data.suppliers && Array.isArray(data.suppliers)) {
          this.supplierCurrencyMap.clear();
          data.suppliers.forEach((s: any) => {
            this.supplierCurrencyMap.set(Number(s.supplierId), {
              code:   s.currencyCode   || 'INR',
              symbol: s.currencySymbol || '₹'
            });
          });
        }
      },
      error: () => {}
    });
  }

  // ============================================================
  // ✅ CURRENCY HELPERS
  // ============================================================

  getSupplierCurrencyCode(supplierId: number): string {
    return this.supplierCurrencyMap.get(Number(supplierId))?.code || this.currencyCode;
  }

  getSupplierCurrencySymbol(supplierId: number): string {
    return this.supplierCurrencyMap.get(Number(supplierId))?.symbol || this.currencySymbol;
  }

  /**
   * ✅ Format amount in the supplier's own resolved currency.
   *   India buyer + India supplier   → ₹ 4,130.00
   *   India buyer + Malaysia supplier → $ 4,130.00
   */
  formatSupplierCurrency(amount: number | null, supplierId: number): string {
    const code   = this.getSupplierCurrencyCode(supplierId);
    const symbol = this.getSupplierCurrencySymbol(supplierId);
    const val    = Number(amount ?? 0);
    const formatted = val.toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
  }

  /** Page-level currency formatting (buyer's location — used in header only) */
  formatCurrency(amount: number): string {
    if (!amount) return `${this.currencySymbol} 0.00`;
    const formatted = amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
    return `${this.currencySymbol} ${formatted}`;
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
  // ✅ EXCHANGE RATES — fetch live rates, convert supplier USD → buyer INR
  // ============================================================

  loadExchangeRates(): void {
    const base = this.currencyCode || 'INR';
    fetch(`https://open.er-api.com/v6/latest/${base}`)
      .then(r => r.json())
      .then((data: any) => {
        if (data?.result === 'success' && data?.rates) {
          this.exchangeRates.clear();
          Object.entries(data.rates as Record<string, number>).forEach(([code, rate]) => {
            if (rate && rate > 0) this.exchangeRates.set(code, 1 / rate);
          });
          this.exchangeRates.set(base, 1);
          this.exchangeRatesLoaded = true;
        }
      })
      .catch(() => { this.exchangeRatesLoaded = false; });
  }

  /**
   * ✅ Convert a supplier's amount to buyer's local currency.
   * e.g. $5,310 USD × 83.5 = ₹4,43,385 INR
   * Returns null if rate not available or same currency.
   */
  getConvertedToBuyerCurrency(amount: number | null, supplierId: number): number | null {
    if (!amount) return null;
    const supplierCode = this.getSupplierCurrencyCode(supplierId);
    if (supplierCode === this.currencyCode) return null; // Same currency, no conversion
    if (!this.exchangeRatesLoaded) return null;
    const rate = this.exchangeRates.get(supplierCode) || 0;
    return rate > 0 ? Number((amount * rate).toFixed(2)) : null;
  }

  /**
   * ✅ Format converted amount in buyer's currency with ≈ prefix.
   * e.g. "≈ ₹ 4,43,385.00"
   */
  formatConvertedToBuyer(amount: number | null, supplierId: number): string {
    const converted = this.getConvertedToBuyerCurrency(amount, supplierId);
    if (converted === null) return '';
    const formatted = converted.toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    return `≈ ${this.currencySymbol} ${formatted}`;
  }

  /** ✅ True if this supplier uses a different currency than the buyer */
  isCrossCurrencySupplier(supplierId: number): boolean {
    return this.getSupplierCurrencyCode(supplierId) !== this.currencyCode;
  }

  /** ✅ Exchange rate label e.g. "1 USD = ₹ 83.50" */
  getExchangeRateLabel(supplierId: number): string {
    const code = this.getSupplierCurrencyCode(supplierId);
    if (code === this.currencyCode) return '';
    const rate = this.exchangeRates.get(code) || 0;
    if (!rate) return '';
    return `1 ${code} = ${this.currencySymbol} ${rate.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  }

  // ============================================================
  // LOAD
  // ============================================================

  loadSelectionStatus(): void {
    this.isLoading = true;
    this.dataService.getSupplierSelectionStatus(this.rfqId).subscribe({
      next: (response: any) => {
        const data = response?.data || response;
        this.systemRecommendedSupplierId = data?.systemRecommendedSupplierId || null;
        this.rankings = (data?.rankings || []).sort((a: any, b: any) => a.rank - b.rank);
        this.existingSelection = data?.existingSelection || null;
        if (this.existingSelection) {
          this.selectedSupplierId = this.existingSelection.supplierId;
          this.justification      = this.existingSelection.justification || '';
          this.remarks            = this.existingSelection.remarks || '';
        }
        this.isLoading = false;
      },
      error: () => this.loadRankingsFallback()
    });
  }

  private loadRankingsFallback(): void {
    this.dataService.getSupplierRankings(this.rfqId).subscribe({
      next: (response: any) => {
        const data = response?.data || response;
        this.rankings = Array.isArray(data) ? data.sort((a: any, b: any) => a.rank - b.rank) : [];
        if (this.rankings.length > 0) {
          this.systemRecommendedSupplierId = this.rankings[0].supplierId;
          this.rankings[0].isTopRecommendation = true;
        }
        this.isLoading = false;
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Error: Could not load supplier rankings. Please run supplier evaluation first.');
        this.isLoading = false;
      }
    });
  }

  // ============================================================
  // SELECTION LOGIC
  // ============================================================

  selectSupplier(supplierId: number): void {
    this.selectedSupplierId = supplierId;
    if (this.isSystemRecommended(supplierId)) this.justification = '';
  }

  isSelected(supplierId: number): boolean { return this.selectedSupplierId === supplierId; }

  isSystemRecommended(supplierId: number): boolean {
    return supplierId === this.systemRecommendedSupplierId;
  }

  get isSelectingRecommended(): boolean {
    return this.selectedSupplierId !== null && this.selectedSupplierId === this.systemRecommendedSupplierId;
  }

  get needsJustification(): boolean {
    return this.selectedSupplierId !== null && this.selectedSupplierId !== this.systemRecommendedSupplierId;
  }

  get isFormValid(): boolean {
    if (!this.selectedSupplierId) return false;
    if (this.needsJustification && (!this.justification || this.justification.trim().length < 10)) return false;
    return true;
  }

  getSelectedSupplier(): SupplierRanking | null {
    return this.rankings.find(r => r.supplierId === this.selectedSupplierId) || null;
  }

  // ============================================================
  // SUBMIT
  // ============================================================

  submitSelection(): void {
    if (!this.isFormValid) {
      if (!this.selectedSupplierId) {
        this.messageService.showMessage('warning', 'Warning', 'Warning: Please select a supplier'); return;
      }
      if (this.needsJustification) {
        this.messageService.showMessage('warning', 'Warning', 'Warning: Justification is required (minimum 10 characters)'); return;
      }
      return;
    }

    const userId = Number(localStorage.getItem('userId') || localStorage.getItem('buyerId'));
    if (!userId) {
      this.messageService.showMessage('error', 'Error', 'Error: User ID not found. Please login again.'); return;
    }

    const payload = {
      rfqId:               this.rfqId,
      supplierId:          this.selectedSupplierId!,
      isSystemRecommended: this.isSelectingRecommended,
      justification:       this.needsJustification ? this.justification.trim() : undefined,
      remarks:             this.remarks.trim() || undefined,
      selectedByUserId:    userId
    };

    this.isSubmitting = true;
    this.dataService.selectFinalSupplier(payload).subscribe({
      next: (response: any) => {
        if (response?.success || response?.data) {
          const selectionId = response?.data?.id;
          this.messageService.showMessage('success', 'Success',
            `Supplier Selected!\n\n${this.getSelectedSupplier()?.supplierName} has been selected for this RFQ.`);
          this.router.navigate(['/po-negotiation', this.rfqId, this.selectedSupplierId, selectionId || 0]);
        } else {
          this.messageService.showMessage('error', 'Error', 'Error: ' + (response?.message || 'Failed to save selection'));
        }
        this.isSubmitting = false;
      },
      error: (error: any) => {
        const msg = error?.error?.message || error?.message || 'Failed to save supplier selection';
        this.messageService.showMessage('error', 'Error', 'Error: ' + msg);
        this.isSubmitting = false;
      }
    });
  }

  // ============================================================
  // HELPERS
  // ============================================================

  getRankBadgeClass(rank: number): string {
    if (rank === 1) return 'rank-gold';
    if (rank === 2) return 'rank-silver';
    if (rank === 3) return 'rank-bronze';
    return 'rank-default';
  }

  getRankIcon(rank: number): string {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return `#${rank}`;
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#16a34a';
    if (score >= 60) return '#d97706';
    if (score >= 40) return '#ea580c';
    return '#dc2626';
  }

  goBack(): void { this.router.navigate(['/quote-comparison', this.rfqId]); }
  goToEvaluation(): void { this.router.navigate(['/supplier-evaluation', this.rfqId]); }
}