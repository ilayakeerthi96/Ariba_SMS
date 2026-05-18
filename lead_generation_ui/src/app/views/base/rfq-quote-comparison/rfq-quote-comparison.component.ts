


// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ActivatedRoute, Router } from '@angular/router';
// import {
//   CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent,
//   RowComponent, ColComponent, ContainerComponent,
//   ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
// } from '@coreui/angular';
// import { DataService } from '../../../shared/service/DataService';
// import { AuthService } from '../../../shared/service/AuthService';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { StorageService } from '../../../shared/service/StorageService';
// import * as XLSX from 'xlsx';

// @Component({
//   selector: 'app-quote-comparison',
//   templateUrl: './rfq-quote-comparison.component.html',
//   styleUrls: ['./rfq-quote-comparison.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
//     CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent,
//     ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
//   ]
// })
// export class QuoteComparisonComponent implements OnInit {

//   rfqId: number = 0;
//   buyerId: number = 0;
//   rfqDetails: any = null;
//   loggedInBuyerId: number | null = null;
//   loggedInBuyer: any = null;
//   comparisonData: any = null;
//   suppliers: any[] = [];
//   items: any[] = [];
//   isLoading: boolean = false;
//   isExporting: boolean = false;
//   selectedSupplierId: number | null = null;
//   selectedQuotes: Map<number, number> = new Map();
//   evaluationDone: boolean = false;

//   // ✅ Page-level currency = buyer's location currency (used in page header only)
//   // Each supplier column uses its OWN currency via getSupplierCurrencyCode() / getSupplierCurrencySymbol()
//   currencyCode: string = 'INR';
//   currencySymbol: string = '₹';

//   // ✅ EXCHANGE RATES: live rates fetched from open.er-api.com (free, no key needed)
//   // Stored as: exchangeRates['USD'] = 83.5  (means 1 USD = 83.5 INR)
//   // Key = source currency code, Value = how much 1 unit = in buyer's currencyCode
//   exchangeRates: Map<string, number> = new Map();
//   exchangeRatesLoaded: boolean = false;
//   exchangeRatesError: boolean = false;
//   // Show converted amounts toggle (buyer can turn off if not needed)
//   showConvertedAmounts: boolean = true;

//   supplierColors: string[] = [
//     'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
//     'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
//     'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
//     'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
//     'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
//   ];

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private authService: AuthService,
//     private dataService: DataService,
//     private buyerService: BuyerService
//   ) {}

//   ngOnInit(): void {
//     this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
//     this.getLoggedInBuyerId();

//     if (!this.rfqId || this.rfqId === 0) {
//       alert('Error: Invalid RFQ ID');
//       this.router.navigate(['/rfq-dashboard']);
//       return;
//     }

//     this.loadLoggedInBuyer();
//     this.loadQuoteComparison();
//     this.checkEvaluationStatus();
//     this.loadExchangeRates();
//   }

//   // ==================== BUYER INFO ====================

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
//         if (storedId && storedId !== 'N/A') buyerId = Number(storedId);
//       }

//       if (!buyerId || isNaN(buyerId) || buyerId <= 0) {
//         alert('Warning: Please login as a buyer first');
//         this.router.navigate(['/login']);
//         return;
//       }

//       this.loggedInBuyerId = buyerId;
//       this.buyerId = buyerId;
//     } catch (error) {
//       console.error('❌ Error getting buyer ID:', error);
//       alert('Error: Failed to get buyer details');
//       this.router.navigate(['/login']);
//     }
//   }

//   private loadLoggedInBuyer(): void {
//     if (!this.loggedInBuyerId) return;

//     this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
//       next: (response: any) => {
//         const buyerDetails = response?.data || response;
//         if (!buyerDetails || !buyerDetails.id) { this.loadBuyerInfoFromLocalStorage(); return; }

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
//           id: buyerDetails.id,
//           companyName: buyerDetails.companyName || 'N/A',
//           companyType: buyerDetails.companyType || 'Others',
//           email: loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
//           contactPersonName: loggedInName || buyerDetails.contactPersonName || 'N/A',
//           contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
//           city, state
//         };
//       },
//       error: () => this.loadBuyerInfoFromLocalStorage()
//     });
//   }

//   private loadBuyerInfoFromLocalStorage(): void {
//     this.loggedInBuyer = {
//       id: this.loggedInBuyerId,
//       contactPersonName: localStorage.getItem('fullName') || 'Buyer User',
//       email: localStorage.getItem('email') || localStorage.getItem('username') || 'N/A',
//       contactPersonPhone: localStorage.getItem('phone') || 'N/A',
//       companyName: localStorage.getItem('companyName') || 'N/A',
//       companyType: localStorage.getItem('companyType') || 'Buyer',
//       city: localStorage.getItem('city') || 'N/A',
//       state: localStorage.getItem('state') || 'N/A'
//     };
//   }

//   getInitials(name: string): string {
//     if (!name || name === 'N/A') return 'BU';
//     const parts = name.split(' ').filter(p => p.length > 0);
//     if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
//     return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
//   }

//   private checkEvaluationStatus(): void {
//     this.dataService.getSupplierRankings(this.rfqId).subscribe({
//       next: (response: any) => {
//         const rankings = response?.data || response;
//         this.evaluationDone = Array.isArray(rankings) && rankings.length > 0;
//       },
//       error: () => { this.evaluationDone = false; }
//     });
//   }

//   // ==================== LOAD DATA ====================

//   loadQuoteComparison(): void {
//     this.isLoading = true;

//     this.dataService.getQuoteComparison(this.rfqId).subscribe({
//       next: (response: any) => {
//         if (response && response.success) {
//           const data = response.data;
//           if (!data) { alert('Warning: No quote data available'); this.isLoading = false; return; }

//           this.rfqDetails = {
//             rfqId: data.rfqId,
//             rfqNumber: data.rfqNumber,
//             rfqTitle: data.rfqTitle,
//             rfqDescription: data.rfqDescription,
//             dueDate: data.dueDate,
//             buyerName: data.buyerName
//           };

//           // Page-level currency (buyer's location — used in page header)
//           this.currencyCode   = data.currencyCode   || 'INR';
//           this.currencySymbol = data.currencySymbol  || '₹';

//           // ✅ Each supplier object now carries its own currencyCode + currencySymbol
//           // (set by QuoteComparisonService.buildSupplierInfoList using same-country rule)
//           this.suppliers = (data.suppliers || []).map((s: any) => ({
//             ...s,
//             currencyCode:   s.currencyCode   || 'INR',
//             currencySymbol: s.currencySymbol || '₹'
//           }));

//           this.items = this.transformItemsForDisplay(data.items || []);

//           this.comparisonData = {
//             rfqDetails: this.rfqDetails,
//             suppliers: this.suppliers,
//             items: this.items,
//             summary: data.summary
//           };

//           this.loadExistingSelections();
//         } else {
//           alert('Warning: No supplier quotes found for this RFQ');
//         }
//         this.isLoading = false;
//       },
//       error: (error: any) => {
//         console.error('❌ Error loading quote comparison:', error);
//         alert('Error: Failed to load quote comparison');
//         this.isLoading = false;
//         setTimeout(() => this.router.navigate(['/rfq-dashboard']), 2000);
//       }
//     });
//   }

//   private transformItemsForDisplay(backendItems: any[]): any[] {
//     if (!backendItems || backendItems.length === 0) return [];

//     return backendItems.map((item: any) => {
//       const quotesArray: any[] = [];

//       if (item.supplierQuotes && typeof item.supplierQuotes === 'object') {
//         Object.keys(item.supplierQuotes).forEach(supplierId => {
//           const quote = item.supplierQuotes[supplierId];
//           const supplier = this.suppliers.find(s => s.supplierId === Number(supplierId));
//           quotesArray.push({
//             quoteItemId:      quote.quoteItemId,
//             supplierId:       Number(supplierId),
//             supplierName:     supplier?.companyName    || 'Unknown',
//             supplierEmail:    supplier?.contactEmail   || 'N/A',
//             // ✅ Each quote row also carries supplier's resolved currency
//             currencyCode:     supplier?.currencyCode   || 'INR',
//             currencySymbol:   supplier?.currencySymbol || '₹',
//             unitRate:         quote.unitRate,
//             quotedQuantity:   quote.quotedQuantity,
//             totalAmount:      quote.totalAmount,
//             taxPercentage:    quote.taxPercentage,
//             taxAmount:        quote.taxAmount,
//             grandTotal:       quote.grandTotal,
//             remarks:          quote.remarks,
//             deliveryDays:     quote.deliveryDays,
//             warrantyMonths:   quote.warrantyMonths,
//             brandOffered:     quote.brandOffered,
//             makeModel:        quote.makeModel,
//             countryOfOrigin:  quote.countryOfOrigin,
//             paymentTerms:     quote.paymentTerms,
//             selected:         quote.isSelected || false
//           });
//         });
//       }

//       return {
//         itemId:                 item.rfqItemId,
//         slNo:                   item.slNo,
//         rfqItemId:              item.rfqItemId,
//         itemCode:               item.itemCode,
//         itemDescription:        item.itemDescription,
//         itemDescriptionDetailed: item.itemDescriptionDetailed,
//         specifications:         item.specifications,
//         uom:                    item.uom,
//         quantity:               item.requiredQuantity,
//         requiredQuantity:       item.requiredQuantity,
//         dynamicFields:          item.dynamicFields || {},
//         quotes:                 quotesArray,
//         lowestBid:              item.lowestBid,
//         highestBid:             item.highestBid,
//         isAwarded:              item.isAwarded,
//         awardedToSupplierId:    item.awardedToSupplierId,
//         awardedToSupplierName:  item.awardedToSupplierName
//       };
//     });
//   }

//   loadExistingSelections(): void {
//     if (!this.items || this.items.length === 0) return;
//     this.items.forEach((item: any) => {
//       if (item.quotes && Array.isArray(item.quotes)) {
//         const selectedQuote = item.quotes.find((q: any) => q.selected === true);
//         if (selectedQuote) this.selectedQuotes.set(item.itemId, selectedQuote.quoteItemId);
//       }
//     });
//   }

//   // ==================== EXCHANGE RATES ====================

//   /**
//    * ✅ Fetch live exchange rates from open.er-api.com (completely free, no API key).
//    * Base = buyer's currency (e.g. INR). Then exchangeRates['USD'] = 83.5 means
//    * 1 USD = 83.5 INR. We fetch rates based on buyer's currencyCode.
//    *
//    * Falls back gracefully — if API fails, no conversion is shown.
//    */
//   loadExchangeRates(): void {
//     // Use buyer's currency as the base (what we convert INTO)
//     const baseCurrency = this.currencyCode || 'INR';
//     const url = `https://open.er-api.com/v6/latest/${baseCurrency}`;

//     fetch(url)
//       .then(res => res.json())
//       .then((data: any) => {
//         if (data?.result === 'success' && data?.rates) {
//           const rates = data.rates as Record<string, number>;
//           // rates are in format: how many of baseCurrency = 1 unit of other currency
//           // Actually open.er-api returns: 1 baseCurrency = X other. We need inverse.
//           // e.g. base=INR, rates.USD = 0.012 → 1 INR = 0.012 USD → 1 USD = 1/0.012 ≈ 83.3 INR
//           this.exchangeRates.clear();
//           Object.entries(rates).forEach(([code, rate]) => {
//             if (rate && rate > 0) {
//               // Store as: how many buyer-currency units = 1 foreign unit
//               this.exchangeRates.set(code, 1 / rate);
//             }
//           });
//           // Same currency = rate 1 (no conversion needed)
//           this.exchangeRates.set(baseCurrency, 1);
//           this.exchangeRatesLoaded = true;
//           this.exchangeRatesError = false;
//           console.log(`✅ Exchange rates loaded. Base: ${baseCurrency}. USD→${baseCurrency}: ${this.getExchangeRate('USD').toFixed(2)}`);
//         } else {
//           this.exchangeRatesError = true;
//           console.warn('⚠️ Exchange rate API returned no data');
//         }
//       })
//       .catch((err: any) => {
//         this.exchangeRatesError = true;
//         console.warn('⚠️ Could not load exchange rates:', err);
//       });
//   }

//   /**
//    * Get the exchange rate: how many buyer-currency units = 1 unit of foreignCode.
//    * e.g. buyer=INR, foreignCode=USD → returns ~83.5
//    * Returns 1 if same currency or rate not available.
//    */
//   getExchangeRate(foreignCode: string): number {
//     if (!foreignCode || foreignCode === this.currencyCode) return 1;
//     return this.exchangeRates.get(foreignCode) || 0;
//   }

//   /**
//    * ✅ Returns true if this supplier uses a DIFFERENT currency than the buyer.
//    * Only cross-currency suppliers need conversion display.
//    */
//   isCrossCurrencySupplier(supplierId: number): boolean {
//     const code = this.getSupplierCurrencyCode(supplierId);
//     return code !== this.currencyCode;
//   }

//   /**
//    * ✅ Convert an amount from supplier's currency to buyer's currency.
//    * Returns null if rate not available (don't show conversion).
//    */
//   convertToBuyerCurrency(amount: number, supplierId: number): number | null {
//     if (!amount || amount === 0) return 0;
//     const supplierCode = this.getSupplierCurrencyCode(supplierId);
//     if (supplierCode === this.currencyCode) return null; // Same currency, no conversion needed
//     const rate = this.getExchangeRate(supplierCode);
//     if (!rate || rate === 0) return null; // Rate not available
//     return Number((amount * rate).toFixed(2));
//   }

//   /**
//    * ✅ Format converted amount in buyer's currency with ≈ prefix (approximate).
//    */
//   formatConvertedAmount(amount: number, supplierId: number): string {
//     const converted = this.convertToBuyerCurrency(amount, supplierId);
//     if (converted === null) return '';
//     const formatted = converted.toLocaleString('en-IN', {
//       minimumFractionDigits: 2, maximumFractionDigits: 2
//     });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     const sym = this.currencySymbol;
//     const amountStr = rtlCodes.includes(this.currencyCode)
//       ? `${formatted} ${sym}`
//       : `${sym} ${formatted}`;
//     return `≈ ${amountStr}`;
//   }

//   /**
//    * ✅ Get the exchange rate label for display in the column header.
//    * e.g. "1 USD = ₹ 83.50"
//    */
//   getExchangeRateLabel(supplierId: number): string {
//     const code = this.getSupplierCurrencyCode(supplierId);
//     if (code === this.currencyCode) return '';
//     const rate = this.getExchangeRate(code);
//     if (!rate || rate === 0) return 'Rate not available';
//     const formatted = rate.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//     return `1 ${code} = ${this.currencySymbol} ${formatted}`;
//   }

//   // ==================== PER-SUPPLIER CURRENCY HELPERS ====================

//   /**
//    * ✅ Get currency code for a specific supplier.
//    * India buyer + India supplier  → INR
//    * India buyer + Dubai supplier  → USD
//    */
//   getSupplierCurrencyCode(supplierId: number): string {
//     const supplier = this.suppliers.find(s => s.supplierId === supplierId);
//     return supplier?.currencyCode || 'INR';
//   }

//   getSupplierCurrencySymbol(supplierId: number): string {
//     const supplier = this.suppliers.find(s => s.supplierId === supplierId);
//     return supplier?.currencySymbol || '₹';
//   }

//   /**
//    * ✅ Format a currency amount using the supplier's own currency.
//    */
//   formatSupplierCurrency(amount: number, supplierId: number): string {
//     const code   = this.getSupplierCurrencyCode(supplierId);
//     const symbol = this.getSupplierCurrencySymbol(supplierId);
//     const val    = Number(amount || 0);
//     const formatted = val.toLocaleString('en-IN', {
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2
//     });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
//   }

//   // ==================== NAVIGATION ====================

//   evaluateSuppliers(): void {
//     if (this.suppliers.length === 0) { alert('Warning: No suppliers have submitted quotes yet'); return; }
//     this.router.navigate(['/supplier-evaluation', this.rfqId]);
//   }

//   proceedToSupplierSelection(): void {
//     if (this.suppliers.length === 0) { alert('Warning: No suppliers have submitted quotes yet'); return; }
//     if (!this.evaluationDone) {
//       const proceed = confirm(
//         'Supplier evaluation has not been completed for this RFQ.\n\n' +
//         'It is recommended to evaluate suppliers before selecting one.\n\n' +
//         'Do you still want to proceed to supplier selection?'
//       );
//       if (!proceed) return;
//     }
//     this.router.navigate(['/supplier-selection', this.rfqId]);
//   }

//   // ==================== QUOTE HELPERS ====================

//   getQuoteForSupplier(quotes: any[], supplierId: number): any | null {
//     if (!quotes || quotes.length === 0) return null;
//     return quotes.find(q => q.supplierId === supplierId) || null;
//   }

//   // ==================== LOWEST PRICE COMPARISONS (all in buyer's currency) ====================

//   /**
//    * ✅ Convert a supplier's amount to buyer's currency for fair comparison.
//    * If exchange rate not loaded yet, falls back to raw value (best effort).
//    * e.g. USD supplier: $150 × 83.5 = ₹12,525 → compared against ₹20,000 from INR supplier
//    */
//   private toComparableBuyerAmount(rawAmount: number, supplierId: number): number {
//     if (!rawAmount) return 0;
//     const supplierCode = this.getSupplierCurrencyCode(supplierId);
//     if (supplierCode === this.currencyCode) return rawAmount; // Already in buyer's currency
//     if (!this.exchangeRatesLoaded) return rawAmount; // Rates not loaded yet — fallback
//     const rate = this.getExchangeRate(supplierCode);
//     return rate > 0 ? rawAmount * rate : rawAmount;
//   }

//   /**
//    * ✅ Determine lowest quote per item — compares all supplier quotes
//    * in the BUYER'S CURRENCY (INR) so cross-currency quotes are fairly compared.
//    *
//    * e.g. Supplier Malaysia quotes $150 USD = ₹12,525 INR
//    *      Supplier India   quotes ₹20,000 INR
//    * → Malaysia is lowest after conversion ✅
//    */
//   isLowestQuote(quote: any, allQuotes: any[]): boolean {
//     if (!allQuotes || allQuotes.length === 0) return false;
//     const lowestQuote = allQuotes.reduce((lowest, current) => {
//       const currentInBuyer  = this.toComparableBuyerAmount(current.grandTotal || 0, current.supplierId);
//       const lowestInBuyer   = this.toComparableBuyerAmount(lowest.grandTotal  || 0, lowest.supplierId);
//       return currentInBuyer < lowestInBuyer ? current : lowest;
//     });
//     return lowestQuote && lowestQuote.quoteItemId === quote.quoteItemId;
//   }

//   /**
//    * ✅ Determine lowest TOTAL supplier — compares grand totals converted to
//    * buyer's currency (INR) so USD total is not unfairly shown as cheaper.
//    *
//    * e.g. Malaysia total $5,310 USD = ₹4,43,385 INR
//    *      India total    ₹2,44,260 INR
//    * → India is lowest ✅ (not Malaysia whose raw number looked smaller)
//    */
//   isLowestSupplierTotal(supplierId: number): boolean {
//     if (this.suppliers.length === 0) return false;
//     const currentInBuyer = this.toComparableBuyerAmount(
//       this.getSupplierTotal(supplierId), supplierId
//     );
//     if (currentInBuyer === 0) return false;

//     let lowestInBuyer = Infinity;
//     this.suppliers.forEach((s: any) => {
//       const converted = this.toComparableBuyerAmount(this.getSupplierTotal(s.supplierId), s.supplierId);
//       if (converted > 0 && converted < lowestInBuyer) lowestInBuyer = converted;
//     });
//     return currentInBuyer === lowestInBuyer;
//   }

//   /** Get raw supplier total in their own currency (for display purposes) */
//   getSupplierTotal(supplierId: number): number {
//     let total = 0;
//     if (this.items) {
//       this.items.forEach((item: any) => {
//         const quote = this.getQuoteForSupplier(item.quotes, supplierId);
//         if (quote) total += quote.grandTotal || 0;
//       });
//     }
//     return total;
//   }

//   /** Get supplier total converted to buyer's currency (for comparison tooltip) */
//   getSupplierTotalInBuyerCurrency(supplierId: number): number {
//     return this.toComparableBuyerAmount(this.getSupplierTotal(supplierId), supplierId);
//   }

//   getAllSupplierTotals(): Map<number, number> {
//     const totals = new Map<number, number>();
//     this.suppliers.forEach((supplier: any) => {
//       totals.set(supplier.supplierId, this.getSupplierTotal(supplier.supplierId));
//     });
//     return totals;
//   }

//   getSupplierCount(): number { return this.suppliers.length; }

//   getSupplierName(supplierId: number): string {
//     const supplier = this.suppliers.find(s => s.supplierId === supplierId);
//     return supplier ? supplier.companyName : 'Unknown Supplier';
//   }

//   getSupplierColor(index: number): string {
//     return this.supplierColors[index % this.supplierColors.length];
//   }

//   objectKeys(obj: any): string[] { return obj ? Object.keys(obj) : []; }
//   hasSelectedSupplier(): boolean { return this.selectedSupplierId !== null; }

//   getSelectedSupplierName(): string {
//     if (!this.selectedSupplierId) return '';
//     const supplier = this.suppliers.find((s: any) => s.supplierId === this.selectedSupplierId);
//     return supplier ? supplier.companyName : '';
//   }

//   goBack(): void { this.router.navigate(['/rfq-dashboard']); }

//   // ✅ Page-level format (buyer's currency — used in page header)
//   formatCurrency(amount: number): string {
//     const val = Number(amount || 0);
//     const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//     const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
//     if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
//     return `${this.currencySymbol} ${formatted}`;
//   }

//   // ==================== EXCEL EXPORT ====================

//   exportToExcel(): void {
//     if (!this.comparisonData || !this.items || this.items.length === 0) {
//       alert('Warning: No data available to export'); return;
//     }
//     this.isExporting = true;
//     try {
//       const wb: XLSX.WorkBook = XLSX.utils.book_new();
//       const excelData: any[] = [];

//       const headerRow: any = {
//         'Sl No': 'Sl No', 'Item Code': 'Item Code', 'Item Description': 'Item Description',
//         'Brief Specifications': 'Brief Specifications', 'Quantity': 'Quantity', 'UOM': 'UOM'
//       };

//       this.suppliers.forEach((supplier: any) => {
//         const n  = supplier.companyName;
//         const sym = supplier.currencySymbol || '₹';
//         headerRow[`${n} - Rate`]     = `Rate (${sym})`;
//         headerRow[`${n} - Amount`]   = `Amount (${sym})`;
//         headerRow[`${n} - Currency`] = `Currency`;
//         headerRow[`${n} - Delivery`] = 'Delivery (days)';
//         headerRow[`${n} - Warranty`] = 'Warranty (months)';
//         headerRow[`${n} - Brand`]    = 'Brand/Make/Model';
//         headerRow[`${n} - Remarks`]  = 'Remarks';
//       });
//       excelData.push(headerRow);

//       this.items.forEach((item: any, index: number) => {
//         const row: any = {
//           'Sl No': index + 1, 'Item Code': item.itemCode || '-',
//           'Item Description': item.itemDescription,
//           'Brief Specifications': item.itemDescriptionDetailed || item.specifications || '-',
//           'Quantity': item.quantity, 'UOM': item.uom
//         };
//         this.suppliers.forEach((supplier: any) => {
//           const quote = this.getQuoteForSupplier(item.quotes, supplier.supplierId);
//           const n = supplier.companyName;
//           if (quote) {
//             row[`${n} - Rate`]     = quote.unitRate    || 0;
//             row[`${n} - Amount`]   = quote.grandTotal  || 0;
//             row[`${n} - Currency`] = supplier.currencyCode || 'INR';
//             row[`${n} - Delivery`] = quote.deliveryDays  || '-';
//             row[`${n} - Warranty`] = quote.warrantyMonths || '-';
//             row[`${n} - Brand`]    = [quote.brandOffered, quote.makeModel].filter(Boolean).join(' / ') || '-';
//             row[`${n} - Remarks`]  = quote.remarks || '-';
//           } else {
//             row[`${n} - Rate`] = '-'; row[`${n} - Amount`] = '-';
//             row[`${n} - Currency`] = '-';
//             row[`${n} - Delivery`] = '-'; row[`${n} - Warranty`] = '-';
//             row[`${n} - Brand`] = '-'; row[`${n} - Remarks`] = '-';
//           }
//         });
//         excelData.push(row);
//       });

//       const totalRow: any = {
//         'Sl No': '', 'Item Code': '', 'Item Description': 'TOTAL',
//         'Brief Specifications': '', 'Quantity': '', 'UOM': ''
//       };
//       this.suppliers.forEach((supplier: any) => {
//         const n = supplier.companyName;
//         totalRow[`${n} - Rate`]     = '';
//         totalRow[`${n} - Amount`]   = this.getSupplierTotal(supplier.supplierId);
//         totalRow[`${n} - Currency`] = supplier.currencyCode || 'INR';
//         totalRow[`${n} - Delivery`] = ''; totalRow[`${n} - Warranty`] = '';
//         totalRow[`${n} - Brand`]    = ''; totalRow[`${n} - Remarks`]  = '';
//       });
//       excelData.push(totalRow);

//       const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(excelData, { skipHeader: true });
//       const colWidths = [{ wch: 8 }, { wch: 12 }, { wch: 40 }, { wch: 35 }, { wch: 10 }, { wch: 8 }];
//       this.suppliers.forEach(() => {
//         colWidths.push({ wch: 12 }, { wch: 15 }, { wch: 10 }, { wch: 12 }, { wch: 12 }, { wch: 20 }, { wch: 30 });
//       });
//       ws['!cols'] = colWidths;

//       XLSX.utils.book_append_sheet(wb, ws, 'Quote Comparison');
//       const fileName = `Quote_Comparison_${this.rfqDetails?.rfqNumber || this.rfqId}_${Date.now()}.xlsx`;
//       XLSX.writeFile(wb, fileName);
//       alert(`Success: Exported to ${fileName}`);
//     } catch (error) {
//       console.error('❌ Excel export error:', error);
//       alert('Error: Failed to export to Excel');
//     } finally {
//       this.isExporting = false;
//     }
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';
import { BuyerService } from '../dashboard/buyer-b.service';
import { StorageService } from '../../../shared/service/StorageService';

@Component({
  selector: 'app-quote-comparison',
  templateUrl: './rfq-quote-comparison.component.html',
  styleUrls: ['./rfq-quote-comparison.component.css'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent,
    ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
  ]
})
export class QuoteComparisonComponent implements OnInit {

  rfqId: number = 0;
  buyerId: number = 0;
  rfqDetails: any = null;
  loggedInBuyerId: number | null = null;
  loggedInBuyer: any = null;
  comparisonData: any = null;
  suppliers: any[] = [];
  items: any[] = [];
  isLoading: boolean = false;

  // ── Download state (mirrors RFQ dashboard pattern) ──────────────────────
  isExporting:        boolean = false;   // Excel via backend
  isExportingPDF:     boolean = false;   // PDF via backend
  showDownloadMenu:   boolean = false;   // Angular dropdown panel

  selectedSupplierId: number | null = null;
  selectedQuotes: Map<number, number> = new Map();
  evaluationDone: boolean = false;

  // ── Buyer currency (page-level) ──────────────────────────────────────────
  currencyCode:   string = 'INR';
  currencySymbol: string = '₹';

  // ── Exchange rates ────────────────────────────────────────────────────────
  exchangeRates:       Map<string, number> = new Map();
  exchangeRatesLoaded: boolean = false;
  exchangeRatesError:  boolean = false;
  showConvertedAmounts: boolean = true;

  supplierColors: string[] = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private dataService: DataService,
    private buyerService: BuyerService
  ) {}

  ngOnInit(): void {
    this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
    this.getLoggedInBuyerId();

    if (!this.rfqId || this.rfqId === 0) {
      alert('Error: Invalid RFQ ID');
      this.router.navigate(['/rfq-dashboard']);
      return;
    }

    this.loadLoggedInBuyer();
    this.loadQuoteComparison();
    this.checkEvaluationStatus();
    this.loadExchangeRates();
  }

  // =========================================================================
  //  BUYER INFO
  // =========================================================================

  private getLoggedInBuyerId(): void {
    try {
      const buyerData = StorageService.getBuyerDetails();
      let buyerId: number | null = null;

      if (buyerData?.id && buyerData.id !== 'N/A') {
        buyerId = Number(buyerData.id);
      } else if (buyerData?.buyerId) {
        buyerId = Number(buyerData.buyerId);
      } else {
        const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
        if (storedId && storedId !== 'N/A') buyerId = Number(storedId);
      }

      if (!buyerId || isNaN(buyerId) || buyerId <= 0) {
        alert('Warning: Please login as a buyer first');
        this.router.navigate(['/login']);
        return;
      }

      this.loggedInBuyerId = buyerId;
      this.buyerId = buyerId;
    } catch (error) {
      console.error('❌ Error getting buyer ID:', error);
      alert('Error: Failed to get buyer details');
      this.router.navigate(['/login']);
    }
  }

  private loadLoggedInBuyer(): void {
    if (!this.loggedInBuyerId) return;

    this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
      next: (response: any) => {
        const buyerDetails = response?.data || response;
        if (!buyerDetails || !buyerDetails.id) { this.loadBuyerInfoFromLocalStorage(); return; }

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
          id: buyerDetails.id,
          companyName: buyerDetails.companyName || 'N/A',
          companyType: buyerDetails.companyType || 'Others',
          email: loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
          contactPersonName: loggedInName || buyerDetails.contactPersonName || 'N/A',
          contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
          city, state
        };
      },
      error: () => this.loadBuyerInfoFromLocalStorage()
    });
  }

  private loadBuyerInfoFromLocalStorage(): void {
    this.loggedInBuyer = {
      id: this.loggedInBuyerId,
      contactPersonName: localStorage.getItem('fullName') || 'Buyer User',
      email: localStorage.getItem('email') || localStorage.getItem('username') || 'N/A',
      contactPersonPhone: localStorage.getItem('phone') || 'N/A',
      companyName: localStorage.getItem('companyName') || 'N/A',
      companyType: localStorage.getItem('companyType') || 'Buyer',
      city:  localStorage.getItem('city')  || 'N/A',
      state: localStorage.getItem('state') || 'N/A'
    };
  }

  getInitials(name: string): string {
    if (!name || name === 'N/A') return 'BU';
    const parts = name.split(' ').filter(p => p.length > 0);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  private checkEvaluationStatus(): void {
    this.dataService.getSupplierRankings(this.rfqId).subscribe({
      next: (response: any) => {
        const rankings = response?.data || response;
        this.evaluationDone = Array.isArray(rankings) && rankings.length > 0;
      },
      error: () => { this.evaluationDone = false; }
    });
  }

  // =========================================================================
  //  LOAD DATA
  // =========================================================================

  loadQuoteComparison(): void {
    this.isLoading = true;

    this.dataService.getQuoteComparison(this.rfqId).subscribe({
      next: (response: any) => {
        if (response && response.success) {
          const data = response.data;
          if (!data) { alert('Warning: No quote data available'); this.isLoading = false; return; }

          this.rfqDetails = {
            rfqId:          data.rfqId,
            rfqNumber:      data.rfqNumber,
            rfqTitle:       data.rfqTitle,
            rfqDescription: data.rfqDescription,
            dueDate:        data.dueDate,
            buyerName:      data.buyerName
          };

          this.currencyCode   = data.currencyCode   || 'INR';
          this.currencySymbol = data.currencySymbol  || '₹';

          this.suppliers = (data.suppliers || []).map((s: any) => ({
            ...s,
            currencyCode:   s.currencyCode   || 'INR',
            currencySymbol: s.currencySymbol || '₹'
          }));

          this.items = this.transformItemsForDisplay(data.items || []);

          this.comparisonData = {
            rfqDetails: this.rfqDetails,
            suppliers:  this.suppliers,
            items:      this.items,
            summary:    data.summary
          };

          this.loadExistingSelections();
        } else {
          alert('Warning: No supplier quotes found for this RFQ');
        }
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading quote comparison:', error);
        alert('Error: Failed to load quote comparison');
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/rfq-dashboard']), 2000);
      }
    });
  }

  private transformItemsForDisplay(backendItems: any[]): any[] {
    if (!backendItems || backendItems.length === 0) return [];

    return backendItems.map((item: any) => {
      const quotesArray: any[] = [];

      if (item.supplierQuotes && typeof item.supplierQuotes === 'object') {
        Object.keys(item.supplierQuotes).forEach(supplierId => {
          const quote    = item.supplierQuotes[supplierId];
          const supplier = this.suppliers.find(s => s.supplierId === Number(supplierId));
          quotesArray.push({
            quoteItemId:     quote.quoteItemId,
            supplierId:      Number(supplierId),
            supplierName:    supplier?.companyName    || 'Unknown',
            supplierEmail:   supplier?.contactEmail   || 'N/A',
            currencyCode:    supplier?.currencyCode   || 'INR',
            currencySymbol:  supplier?.currencySymbol || '₹',
            unitRate:        quote.unitRate,
            quotedQuantity:  quote.quotedQuantity,
            totalAmount:     quote.totalAmount,
            taxPercentage:   quote.taxPercentage,
            taxAmount:       quote.taxAmount,
            grandTotal:      quote.grandTotal,
            remarks:         quote.remarks,
            deliveryDays:    quote.deliveryDays,
            warrantyMonths:  quote.warrantyMonths,
            brandOffered:    quote.brandOffered,
            makeModel:       quote.makeModel,
            countryOfOrigin: quote.countryOfOrigin,
            paymentTerms:    quote.paymentTerms,
            selected:        quote.isSelected || false
          });
        });
      }

      return {
        itemId:                  item.rfqItemId,
        slNo:                    item.slNo,
        rfqItemId:               item.rfqItemId,
        itemCode:                item.itemCode,
        itemDescription:         item.itemDescription,
        itemDescriptionDetailed: item.itemDescriptionDetailed,
        specifications:          item.specifications,
        uom:                     item.uom,
        quantity:                item.requiredQuantity,
        requiredQuantity:        item.requiredQuantity,
        dynamicFields:           item.dynamicFields || {},
        quotes:                  quotesArray,
        lowestBid:               item.lowestBid,
        highestBid:              item.highestBid,
        isAwarded:               item.isAwarded,
        awardedToSupplierId:     item.awardedToSupplierId,
        awardedToSupplierName:   item.awardedToSupplierName
      };
    });
  }

  loadExistingSelections(): void {
    if (!this.items || this.items.length === 0) return;
    this.items.forEach((item: any) => {
      if (item.quotes && Array.isArray(item.quotes)) {
        const selectedQuote = item.quotes.find((q: any) => q.selected === true);
        if (selectedQuote) this.selectedQuotes.set(item.itemId, selectedQuote.quoteItemId);
      }
    });
  }

  // =========================================================================
  //  EXCHANGE RATES
  // =========================================================================

  loadExchangeRates(): void {
    const baseCurrency = this.currencyCode || 'INR';
    const url = `https://open.er-api.com/v6/latest/${baseCurrency}`;

    fetch(url)
      .then(res => res.json())
      .then((data: any) => {
        if (data?.result === 'success' && data?.rates) {
          const rates = data.rates as Record<string, number>;
          this.exchangeRates.clear();
          Object.entries(rates).forEach(([code, rate]) => {
            if (rate && rate > 0) this.exchangeRates.set(code, 1 / rate);
          });
          this.exchangeRates.set(baseCurrency, 1);
          this.exchangeRatesLoaded = true;
          this.exchangeRatesError  = false;
        } else {
          this.exchangeRatesError = true;
        }
      })
      .catch((err: any) => {
        this.exchangeRatesError = true;
        console.warn('⚠️ Could not load exchange rates:', err);
      });
  }

  getExchangeRate(foreignCode: string): number {
    if (!foreignCode || foreignCode === this.currencyCode) return 1;
    return this.exchangeRates.get(foreignCode) || 0;
  }

  isCrossCurrencySupplier(supplierId: number): boolean {
    return this.getSupplierCurrencyCode(supplierId) !== this.currencyCode;
  }

  convertToBuyerCurrency(amount: number, supplierId: number): number | null {
    if (!amount || amount === 0) return 0;
    const supplierCode = this.getSupplierCurrencyCode(supplierId);
    if (supplierCode === this.currencyCode) return null;
    const rate = this.getExchangeRate(supplierCode);
    if (!rate || rate === 0) return null;
    return Number((amount * rate).toFixed(2));
  }

  formatConvertedAmount(amount: number, supplierId: number): string {
    const converted = this.convertToBuyerCurrency(amount, supplierId);
    if (converted === null) return '';
    const formatted = converted.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    const sym = this.currencySymbol;
    const amountStr = rtlCodes.includes(this.currencyCode) ? `${formatted} ${sym}` : `${sym} ${formatted}`;
    return `≈ ${amountStr}`;
  }

  getExchangeRateLabel(supplierId: number): string {
    const code = this.getSupplierCurrencyCode(supplierId);
    if (code === this.currencyCode) return '';
    const rate = this.getExchangeRate(code);
    if (!rate || rate === 0) return 'Rate not available';
    const formatted = rate.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    return `1 ${code} = ${this.currencySymbol} ${formatted}`;
  }

  // =========================================================================
  //  PER-SUPPLIER CURRENCY HELPERS
  // =========================================================================

  getSupplierCurrencyCode(supplierId: number): string {
    const s = this.suppliers.find(s => s.supplierId === supplierId);
    return s?.currencyCode || 'INR';
  }

  getSupplierCurrencySymbol(supplierId: number): string {
    const s = this.suppliers.find(s => s.supplierId === supplierId);
    return s?.currencySymbol || '₹';
  }

  formatSupplierCurrency(amount: number, supplierId: number): string {
    const code    = this.getSupplierCurrencyCode(supplierId);
    const symbol  = this.getSupplierCurrencySymbol(supplierId);
    const val     = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes  = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
  }

  // =========================================================================
  //  NAVIGATION
  // =========================================================================

  evaluateSuppliers(): void {
    if (this.suppliers.length === 0) { alert('Warning: No suppliers have submitted quotes yet'); return; }
    this.router.navigate(['/supplier-evaluation', this.rfqId]);
  }

  proceedToSupplierSelection(): void {
    if (this.suppliers.length === 0) { alert('Warning: No suppliers have submitted quotes yet'); return; }
    if (!this.evaluationDone) {
      const proceed = confirm(
        'Supplier evaluation has not been completed for this RFQ.\n\n' +
        'It is recommended to evaluate suppliers before selecting one.\n\n' +
        'Do you still want to proceed to supplier selection?'
      );
      if (!proceed) return;
    }
    this.router.navigate(['/supplier-selection', this.rfqId]);
  }

  goBack(): void { this.router.navigate(['/rfq-dashboard']); }

  // =========================================================================
  //  QUOTE HELPERS
  // =========================================================================

  getQuoteForSupplier(quotes: any[], supplierId: number): any | null {
    if (!quotes || quotes.length === 0) return null;
    return quotes.find(q => q.supplierId === supplierId) || null;
  }

  private toComparableBuyerAmount(rawAmount: number, supplierId: number): number {
    if (!rawAmount) return 0;
    const supplierCode = this.getSupplierCurrencyCode(supplierId);
    if (supplierCode === this.currencyCode) return rawAmount;
    if (!this.exchangeRatesLoaded) return rawAmount;
    const rate = this.getExchangeRate(supplierCode);
    return rate > 0 ? rawAmount * rate : rawAmount;
  }

  isLowestQuote(quote: any, allQuotes: any[]): boolean {
    if (!allQuotes || allQuotes.length === 0) return false;
    const lowestQuote = allQuotes.reduce((lowest, current) => {
      const currentInBuyer = this.toComparableBuyerAmount(current.grandTotal || 0, current.supplierId);
      const lowestInBuyer  = this.toComparableBuyerAmount(lowest.grandTotal  || 0, lowest.supplierId);
      return currentInBuyer < lowestInBuyer ? current : lowest;
    });
    return lowestQuote && lowestQuote.quoteItemId === quote.quoteItemId;
  }

  isLowestSupplierTotal(supplierId: number): boolean {
    if (this.suppliers.length === 0) return false;
    const currentInBuyer = this.toComparableBuyerAmount(this.getSupplierTotal(supplierId), supplierId);
    if (currentInBuyer === 0) return false;

    let lowestInBuyer = Infinity;
    this.suppliers.forEach((s: any) => {
      const converted = this.toComparableBuyerAmount(this.getSupplierTotal(s.supplierId), s.supplierId);
      if (converted > 0 && converted < lowestInBuyer) lowestInBuyer = converted;
    });
    return currentInBuyer === lowestInBuyer;
  }

  getSupplierTotal(supplierId: number): number {
    let total = 0;
    if (this.items) {
      this.items.forEach((item: any) => {
        const quote = this.getQuoteForSupplier(item.quotes, supplierId);
        if (quote) total += quote.grandTotal || 0;
      });
    }
    return total;
  }

  getSupplierTotalInBuyerCurrency(supplierId: number): number {
    return this.toComparableBuyerAmount(this.getSupplierTotal(supplierId), supplierId);
  }

  getAllSupplierTotals(): Map<number, number> {
    const totals = new Map<number, number>();
    this.suppliers.forEach((s: any) => totals.set(s.supplierId, this.getSupplierTotal(s.supplierId)));
    return totals;
  }

  getSupplierCount(): number { return this.suppliers.length; }

  getSupplierName(supplierId: number): string {
    const s = this.suppliers.find(s => s.supplierId === supplierId);
    return s ? s.companyName : 'Unknown Supplier';
  }

  getSupplierColor(index: number): string {
    return this.supplierColors[index % this.supplierColors.length];
  }

  objectKeys(obj: any): string[] { return obj ? Object.keys(obj) : []; }
  hasSelectedSupplier():    boolean { return this.selectedSupplierId !== null; }

  getSelectedSupplierName(): string {
    if (!this.selectedSupplierId) return '';
    const s = this.suppliers.find((s: any) => s.supplierId === this.selectedSupplierId);
    return s ? s.companyName : '';
  }

  formatCurrency(amount: number): string {
    const val       = Number(amount || 0);
    const formatted = val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const rtlCodes  = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    if (rtlCodes.includes(this.currencyCode)) return `${formatted} ${this.currencySymbol}`;
    return `${this.currencySymbol} ${formatted}`;
  }

  // =========================================================================
  //  ✅ REPORT DOWNLOADS  — same pattern as RFQ dashboard
  //     All calls go through DataService (has Authorization header → no 401)
  // =========================================================================

  /** Toggle the download dropdown panel */
  toggleDownloadMenu(event: Event): void {
    event.stopPropagation();
    this.showDownloadMenu = !this.showDownloadMenu;
  }

  /** Close dropdown when clicking anywhere outside */
  closeDownloadMenu(): void {
    this.showDownloadMenu = false;
  }

  /**
   * Download Quote Comparison as Excel — calls backend RFQReportService.
   * Produces the same styled multi-sheet workbook as the RFQ report.
   */
  exportToExcel(): void {
    if (!this.comparisonData) { alert('Warning: No data available to export'); return; }

    this.isExporting     = true;
    this.showDownloadMenu = false;

    const rfqNum = this.rfqDetails?.rfqNumber || this.rfqId;
    const today  = new Date().toISOString().slice(0, 10);

    this.dataService.getQuoteComparisonExcel(this.rfqId).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, `Quote_Comparison_${rfqNum}_${today}.xlsx`);
        this.isExporting = false;
      },
      error: (err: any) => {
        console.error('❌ Excel export failed:', err);
        alert('Error: Failed to download Excel report. Please try again.');
        this.isExporting = false;
      }
    });
  }

  /**
   * Download Quote Comparison as PDF — calls backend RFQReportService.
   * Produces a branded printable PDF.
   */
  exportToPDF(): void {
    if (!this.comparisonData) { alert('Warning: No data available to export'); return; }

    this.isExportingPDF  = true;
    this.showDownloadMenu = false;

    const rfqNum = this.rfqDetails?.rfqNumber || this.rfqId;
    const today  = new Date().toISOString().slice(0, 10);

    this.dataService.getQuoteComparisonPDF(this.rfqId).subscribe({
      next: (blob: Blob) => {
        this.dataService.saveBlob(blob, `Quote_Comparison_${rfqNum}_${today}.pdf`);
        this.isExportingPDF = false;
      },
      error: (err: any) => {
        console.error('❌ PDF export failed:', err);
        alert('Error: Failed to download PDF report. Please try again.');
        this.isExportingPDF = false;
      }
    });
  }
}