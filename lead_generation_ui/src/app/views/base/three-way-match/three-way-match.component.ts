// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router, RouterModule, ActivatedRoute } from '@angular/router';
// import { DataService } from '../../../shared/service/DataService';
// import { AuthService } from '../../../shared/service/AuthService';

// @Component({
//   selector: 'app-three-way-match',
//   standalone: true,
//   imports: [CommonModule, FormsModule, RouterModule],
//   templateUrl: './three-way-match.component.html',
//   styleUrls: ['./three-way-match.component.css']
// })
// export class ThreeWayMatchComponent implements OnInit {

//   // Tabs: 'perform' | 'pending' | 'history'
//   activeTab = 'perform';

//   // ── PERFORM MATCH ─────────────────────────────────────────────────────────
//   allBuyerInvoices:  any[] = [];   // ALL non-draft invoices (for history dropdown)
//   buyerInvoices:     any[] = [];   // SUBMITTED invoices only (for perform match)
//   filteredInvoices:  any[] = [];
//   invoiceSearch      = '';
//   invoiceLoading     = false;
//   selectedInvoice:   any = null;

//   // Step 2: select GRN
//   poGRNs:            any[] = [];
//   grnLoading         = false;
//   selectedGRN:       any = null;

//   // Pre-selection from navigation state (coming from GRN list)
//   preSelectedGrnId:  number | null = null;
//   preSelectedPoId:   number | null = null;

//   // Match form
//   tolerancePercentage = 2;
//   performLoading      = false;

//   // Result
//   matchResult:       any = null;
//   showResultModal    = false;

//   // ── RESOLVE ───────────────────────────────────────────────────────────────
//   showResolveModal   = false;
//   resolveTarget:     any = null;
//   resolveForm = {
//     resolution:             '',
//     approvedPaymentAmount:  null as number | null,
//     resolutionRemarks:      ''
//   };
//   resolveLoading     = false;

//   // ── PENDING RESOLUTIONS ───────────────────────────────────────────────────
//   pendingMatches:    any[] = [];
//   pendingLoading     = false;

//   // ── HISTORY ───────────────────────────────────────────────────────────────
//   historyInvoiceId:  number | null = null;
//   historyMatches:    any[] = [];
//   historyLoading     = false;
//   historySearchInvoices: any[] = [];   // all non-draft invoices

//   // Common
//   errorMessage   = '';
//   successMessage = '';
//   companyName    = '';
//   userId         = 0;
//   buyerId        = 0;

//   // Expose Math to template
//   Math = Math;

//   resolutionOptions = [
//    // { value: 'ACCEPTED_WITH_VARIANCE',    label: 'Accept with Variance',       icon: 'bi-check-circle',           color: 'success'   },
//     { value: 'PARTIAL_PAYMENT_APPROVED',  label: 'Approve Partial Payment',    icon: 'bi-cash-coin',              color: 'info'      },
//     { value: 'REVISED_INVOICE_REQUESTED', label: 'Request Revised Invoice',    icon: 'bi-file-earmark-arrow-up',  color: 'warning'   },
//     { value: 'CREDIT_NOTE_REQUESTED',     label: 'Request Credit Note',        icon: 'bi-receipt',                color: 'primary'   },
//     { value: 'DEBIT_NOTE_RAISED',         label: 'Raise Debit Note',           icon: 'bi-file-earmark-minus',     color: 'secondary' },
//     { value: 'DISPUTED',                  label: 'Mark as Disputed',           icon: 'bi-exclamation-triangle',   color: 'danger'    },
//    // { value: 'CANCELLED',                 label: 'Cancel Match',               icon: 'bi-x-circle',               color: 'dark'      }
//   ];

//   constructor(
//     private dataService: DataService,
//     private authService: AuthService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   ngOnInit(): void {
//     // Resolve user info
//     try {
//       const buyer = JSON.parse(localStorage.getItem('buyer') || '{}');
//       this.companyName = buyer.name || '';
//     } catch {}

//     this.buyerId = Number(localStorage.getItem('buyerId') || 0);

//     const token = localStorage.getItem('token');
//     if (token) {
//       try {
//         const p = JSON.parse(atob(token.split('.')[1]));
//         this.userId = p.userId || p.id || 0;
//       } catch {}
//     }

//     // Check for navigation state — GRN list passes grnId + poId when clicking "3-Way Match"
//     const navState = history.state as any;
//     if (navState?.grnId) {
//       this.preSelectedGrnId = Number(navState.grnId);
//       this.preSelectedPoId  = Number(navState.poId) || null;
//     }

//     // Also check query params as fallback
//     this.route.queryParams.subscribe(params => {
//       if (params['grnId'] && !this.preSelectedGrnId) {
//         this.preSelectedGrnId = Number(params['grnId']);
//         this.preSelectedPoId  = params['poId'] ? Number(params['poId']) : null;
//       }
//     });

//     if (!this.companyName) {
//       this.errorMessage = 'Company name could not be determined. Please log out and log in again.';
//       this.loadPendingMatches();
//       return;
//     }

//     this.loadInvoices();
//     this.loadPendingMatches();
//   }

//   setTab(tab: string): void {
//     this.activeTab = tab;
//     this.dismissAlert();
//   }

//   // ─────────────────────────────────────────────────────────────────────────
//   // LOAD INVOICES
//   // Perform Match → SUBMITTED invoices only (not yet matched/approved)
//   // History dropdown → all non-draft (SUBMITTED, APPROVED, REJECTED, PAID)
//   // ─────────────────────────────────────────────────────────────────────────
//   loadInvoices(): void {
//     this.invoiceLoading = true;

//     const handleResponse = (res: any) => {
//       const all: any[] = Array.isArray(res) ? res : (res.data || []);

//       // All non-draft invoices for history dropdown
//       this.allBuyerInvoices      = all.filter((inv: any) => inv.status !== 'DRAFT');
//       this.historySearchInvoices = [...this.allBuyerInvoices];

//       // For perform match: SUBMITTED invoices only
//       // (APPROVED means already matched+approved, no need to re-run)
//       this.buyerInvoices    = all.filter((inv: any) => inv.status === 'SUBMITTED');
//       this.filteredInvoices = [...this.buyerInvoices];

//       this.invoiceLoading = false;

//       // If we navigated here from GRN list with a pre-selected GRN, auto-select the matching invoice
//       if (this.preSelectedPoId) {
//         this.autoSelectInvoiceForPO(this.preSelectedPoId);
//       }
//     };

//     const handleError = () => {
//       if (this.buyerId) {
//         this.dataService.getBuyerInvoicesByBuyerId(this.buyerId).subscribe({
//           next: handleResponse,
//           error: () => { this.invoiceLoading = false; }
//         });
//       } else {
//         this.invoiceLoading = false;
//       }
//     };

//     this.dataService.getBuyerInvoices(this.companyName).subscribe({
//       next:  handleResponse,
//       error: handleError
//     });
//   }

//   // Auto-select first SUBMITTED invoice that matches the pre-selected PO
//   private autoSelectInvoiceForPO(poId: number): void {
//     const match = this.buyerInvoices.find((inv: any) => {
//       const invPoId = inv.purchaseOrderId || inv.poId || inv.purchaseOrder?.id;
//       return Number(invPoId) === poId;
//     });
//     if (match) {
//       this.selectInvoice(match);
//     }
//   }

//   filterInvoices(): void {
//     const q = this.invoiceSearch.toLowerCase();
//     this.filteredInvoices = this.buyerInvoices.filter(inv =>
//       (inv.invoiceNumber || '').toLowerCase().includes(q) ||
//       (inv.supplierName  || inv.supplier?.name || '').toLowerCase().includes(q)
//     );
//   }

//   private getPOId(inv: any): number {
//     return inv.purchaseOrderId || inv.poId || inv.purchaseOrder?.id || 0;
//   }

//   selectInvoice(inv: any): void {
//     this.selectedInvoice = inv;
//     this.selectedGRN     = null;
//     this.matchResult     = null;
//     this.errorMessage    = '';
//     this.poGRNs          = [];

//     const poId = this.getPOId(inv);
//     console.log('[3-WAY MATCH] selectInvoice —', inv.invoiceNumber, '| poId:', poId);

//     if (!poId) {
//       this.errorMessage = 'Could not determine PO reference from this invoice.';
//       return;
//     }

//     this.grnLoading = true;
//     this.dataService.getAllGRNsByPO(poId).subscribe({
//       next: (res: any) => {
//         const all: any[] = Array.isArray(res) ? res : (res.data || []);
//         this.poGRNs     = all.filter((g: any) => g.status === 'APPROVED');
//         this.grnLoading = false;

//         if (this.poGRNs.length === 0) {
//           const draft     = all.filter(g => g.status === 'DRAFT').length;
//           const submitted = all.filter(g => g.status === 'SUBMITTED').length;
//           const qaReview  = all.filter(g => g.status === 'QA_REVIEW').length;
//           if (all.length === 0) {
//             this.errorMessage = 'No GRNs found for this PO. Please create and approve a GRN first.';
//           } else if (submitted + qaReview > 0) {
//             this.errorMessage = `${submitted + qaReview} GRN(s) are pending QA/approval. Please approve the GRN before running 3-Way Match.`;
//           } else if (draft > 0) {
//             this.errorMessage = 'GRN(s) are in DRAFT. Please submit and approve the GRN first.';
//           }
//         } else {
//           // Auto-select GRN if navigated from GRN list
//           if (this.preSelectedGrnId) {
//             const preGrn = this.poGRNs.find((g: any) => g.id === this.preSelectedGrnId);
//             if (preGrn) {
//               this.selectedGRN      = preGrn;
//               this.preSelectedGrnId = null; // consume it
//             }
//           }
//         }
//       },
//       error: () => { this.grnLoading = false; }
//     });
//   }

//   performMatch(): void {
//     if (!this.selectedInvoice || !this.selectedGRN) {
//       this.errorMessage = 'Please select both an invoice and a GRN.';
//       return;
//     }
//     this.performLoading = true;
//     this.errorMessage   = '';
//     const poId = this.getPOId(this.selectedInvoice);

//     this.dataService.performThreeWayMatch({
//       purchaseOrderId:     poId,
//       grnId:               this.selectedGRN.id,
//       invoiceId:           this.selectedInvoice.id,
//       tolerancePercentage: this.tolerancePercentage,
//       performedByUserId:   this.userId
//     }).subscribe({
//       next: (res: any) => {
//         this.matchResult    = res.data || res;
//         this.showResultModal = true;
//         this.performLoading  = false;
//         this.loadPendingMatches();
//         // Reload invoices — if auto-approved, the invoice status changes
//         this.loadInvoices();
//       },
//       error: (err: any) => {
//         this.errorMessage  = err?.error?.message || 'Failed to perform 3-way match.';
//         this.performLoading = false;
//       }
//     });
//   }

//   closeResultModal(): void {
//     this.showResultModal = false;
//   }

//   // ─────────────────────────────────────────────────────────────────────────
//   // PENDING RESOLUTIONS
//   // ─────────────────────────────────────────────────────────────────────────
//   loadPendingMatches(): void {
//     this.pendingLoading = true;
//     this.dataService.getPendingMatchResolutions().subscribe({
//       next: (res: any) => {
//         const raw: any[] = Array.isArray(res) ? res : (res.data || []);
//         // Enrich with invoice/grn/po reference numbers if backend returns IDs only
//         this.pendingMatches = raw.map(m => this.enrichMatchRecord(m));
//         this.pendingLoading = false;
//       },
//       error: () => { this.pendingLoading = false; }
//     });
//   }

//   // Enrich a match record — backend may return full objects or just IDs depending on DTO
//   private enrichMatchRecord(m: any): any {
//     return {
//       ...m,
//       invoiceNumber: m.invoiceNumber || m.invoice?.invoiceNumber || `INV-${m.invoiceId}`,
//       grnNumber:     m.grnNumber     || m.grn?.grnNumber         || `GRN-${m.grnId}`,
//       poNumber:      m.poNumber      || m.purchaseOrder?.poNumber || `PO-${m.purchaseOrderId}`,
//     };
//   }

//   openResolve(match: any): void {
//     this.resolveTarget = match;
//     this.resolveForm   = { resolution: '', approvedPaymentAmount: null, resolutionRemarks: '' };
//     this.showResolveModal = true;
//     this.errorMessage     = '';
//   }

//   closeResolveModal(): void {
//     this.showResolveModal = false;
//     this.resolveTarget    = null;
//     this.errorMessage     = '';
//   }

//   submitResolution(): void {
//     if (!this.resolveForm.resolution) {
//       this.errorMessage = 'Please select a resolution action.';
//       return;
//     }
//     if (!this.resolveForm.resolutionRemarks?.trim()) {
//       this.errorMessage = 'Resolution remarks are mandatory.';
//       return;
//     }
//     if (this.needsPaymentAmount() && !this.resolveForm.approvedPaymentAmount) {
//       this.errorMessage = 'Please enter the approved payment amount.';
//       return;
//     }

//     this.resolveLoading = true;
//     this.errorMessage   = '';

//     const payload: any = {
//       resolution:        this.resolveForm.resolution,
//       resolutionRemarks: this.resolveForm.resolutionRemarks.trim(),
//       resolvedByUserId:  this.userId
//     };
//     if (this.resolveForm.approvedPaymentAmount != null) {
//       payload.approvedPaymentAmount = this.resolveForm.approvedPaymentAmount;
//     }

//     this.dataService.resolveMatch(this.resolveTarget.id, payload).subscribe({
//       next: () => {
//         this.successMessage = `Match resolved: ${this.getResolutionLabel(this.resolveForm.resolution)}`;
//         this.resolveLoading = false;
//         this.closeResolveModal();
//         this.loadPendingMatches();
//         this.loadInvoices();
//       },
//       error: (err: any) => {
//         this.errorMessage  = err?.error?.message || 'Failed to resolve match.';
//         this.resolveLoading = false;
//       }
//     });
//   }

//   // ─────────────────────────────────────────────────────────────────────────
//   // HISTORY
//   // ─────────────────────────────────────────────────────────────────────────
//   loadHistory(): void {
//     if (!this.historyInvoiceId) {
//       this.errorMessage = 'Select an invoice to view match history.';
//       return;
//     }
//     this.historyLoading = true;
//     this.historyMatches = [];
//     this.dataService.getMatchesByInvoice(this.historyInvoiceId).subscribe({
//       next: (res: any) => {
//         const raw: any[] = Array.isArray(res) ? res : (res.data || []);
//         this.historyMatches = raw.map(m => this.enrichMatchRecord(m));
//         this.historyLoading = false;
//         if (this.historyMatches.length === 0) {
//           this.errorMessage = 'No match history found for this invoice.';
//         }
//       },
//       error: () => { this.historyLoading = false; }
//     });
//   }

//   // ─────────────────────────────────────────────────────────────────────────
//   // HELPERS
//   // ─────────────────────────────────────────────────────────────────────────
//   needsPaymentAmount(): boolean {
//     return ['PARTIAL_PAYMENT_APPROVED', 'ACCEPTED_WITH_VARIANCE'].includes(this.resolveForm.resolution);
//   }

//   getResolutionLabel(value: string): string {
//     return this.resolutionOptions.find(o => o.value === value)?.label || value;
//   }

//   getOverallStatusBadgeClass(status: string): string {
//     const map: Record<string, string> = {
//       MATCHED:             'badge bg-success',
//       PARTIAL_MATCH:       'badge bg-info',
//       QUANTITY_MISMATCH:   'badge bg-warning text-dark',
//       PRICE_MISMATCH:      'badge bg-warning text-dark',
//       ITEM_MISMATCH:       'badge bg-danger',
//       EXCESS_DELIVERY:     'badge bg-secondary',
//       OVERRIDDEN_APPROVED: 'badge bg-success',
//       DISPUTED:            'badge bg-danger',
//       FAILED:              'badge bg-dark'
//     };
//     return map[status] || 'badge bg-secondary';
//   }

//   getLineStatusBadgeClass(status: string): string {
//     const map: Record<string, string> = {
//       MATCHED:           'badge bg-success',
//       PARTIALLY_MATCHED: 'badge bg-info',
//       QTY_SHORT:         'badge bg-warning text-dark',
//       PRICE_VARIANCE:    'badge bg-warning text-dark',
//       QTY_EXCESS:        'badge bg-secondary',
//       NOT_IN_PO:         'badge bg-danger',
//       NOT_RECEIVED:      'badge bg-danger',
//     };
//     return (map[status] || 'badge bg-secondary') + ' small';
//   }

//   getResolutionBadgeClass(status: string): string {
//     const map: Record<string, string> = {
//       PENDING_RESOLUTION:        'badge bg-warning text-dark',
//       ACCEPTED_WITH_VARIANCE:    'badge bg-success',
//       REVISED_INVOICE_REQUESTED: 'badge bg-primary',
//       PARTIAL_PAYMENT_APPROVED:  'badge bg-info',
//       CREDIT_NOTE_REQUESTED:     'badge bg-secondary',
//       DEBIT_NOTE_RAISED:         'badge bg-secondary',
//       DISPUTED:                  'badge bg-danger',
//       CANCELLED:                 'badge bg-dark'
//     };
//     return map[status] || 'badge bg-secondary';
//   }

//   dismissAlert(): void {
//     this.errorMessage   = '';
//     this.successMessage = '';
//   }

//   goBack(): void {
//     this.router.navigate(['/grn-list']);
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';

@Component({
  selector: 'app-three-way-match',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './three-way-match.component.html',
  styleUrls: ['./three-way-match.component.css']
})
export class ThreeWayMatchComponent implements OnInit {

  activeTab = 'perform';

  // ── PERFORM MATCH ─────────────────────────────────────────────────────────
  allBuyerInvoices:  any[] = [];
  buyerInvoices:     any[] = [];
  filteredInvoices:  any[] = [];
  invoiceSearch      = '';
  invoiceLoading     = false;
  selectedInvoice:   any = null;

  poGRNs:            any[] = [];
  grnLoading         = false;
  selectedGRN:       any = null;

  preSelectedGrnId:  number | null = null;
  preSelectedPoId:   number | null = null;

  tolerancePercentage = 2;
  performLoading      = false;

  matchResult:       any = null;
  showResultModal    = false;

  // ── RESOLVE ───────────────────────────────────────────────────────────────
  showResolveModal   = false;
  resolveTarget:     any = null;
  resolveForm = {
    resolution:             '',
    approvedPaymentAmount:  null as number | null,
    resolutionRemarks:      ''
  };
  resolveLoading     = false;

  // ── PENDING RESOLUTIONS ───────────────────────────────────────────────────
  pendingMatches:    any[] = [];
  pendingLoading     = false;

  // ── HISTORY ───────────────────────────────────────────────────────────────
  historyInvoiceId:  number | null = null;
  historyMatches:    any[] = [];
  historyLoading     = false;
  historySearchInvoices: any[] = [];

  // ── Common ────────────────────────────────────────────────────────────────
  errorMessage   = '';
  successMessage = '';
  companyName    = '';
  userId         = 0;
  buyerId        = 0;

  Math = Math;

  // ✅ Active currency — set from the selected invoice's buyer location
  activeCurrencyCode:   string = 'INR';
  activeCurrencySymbol: string = '₹';

  resolutionOptions = [
    { value: 'PARTIAL_PAYMENT_APPROVED',  label: 'Approve Partial Payment',  icon: 'bi-cash-coin',             color: 'info'      },
    { value: 'REVISED_INVOICE_REQUESTED', label: 'Request Revised Invoice',  icon: 'bi-file-earmark-arrow-up', color: 'warning'   },
    { value: 'CREDIT_NOTE_REQUESTED',     label: 'Request Credit Note',      icon: 'bi-receipt',               color: 'primary'   },
    { value: 'DEBIT_NOTE_RAISED',         label: 'Raise Debit Note',         icon: 'bi-file-earmark-minus',    color: 'secondary' },
    { value: 'DISPUTED',                  label: 'Mark as Disputed',         icon: 'bi-exclamation-triangle',  color: 'danger'    },
  ];

  constructor(
    private dataService: DataService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    try {
      const buyer = JSON.parse(localStorage.getItem('buyer') || '{}');
      this.companyName = buyer.name || '';
    } catch {}

    this.buyerId = Number(localStorage.getItem('buyerId') || 0);

    const token = localStorage.getItem('token');
    if (token) {
      try {
        const p = JSON.parse(atob(token.split('.')[1]));
        this.userId = p.userId || p.id || 0;
      } catch {}
    }

    const navState = history.state as any;
    if (navState?.grnId) {
      this.preSelectedGrnId = Number(navState.grnId);
      this.preSelectedPoId  = Number(navState.poId) || null;
    }

    this.route.queryParams.subscribe(params => {
      if (params['grnId'] && !this.preSelectedGrnId) {
        this.preSelectedGrnId = Number(params['grnId']);
        this.preSelectedPoId  = params['poId'] ? Number(params['poId']) : null;
      }
    });

    if (!this.companyName) {
      this.errorMessage = 'Company name could not be determined. Please log out and log in again.';
      this.loadPendingMatches();
      return;
    }

    this.loadInvoices();
    this.loadPendingMatches();
  }

  setTab(tab: string): void {
    this.activeTab = tab;
    this.dismissAlert();
  }

  // ─────────────────────────────────────────────────────────────────────────
  // LOAD INVOICES
  // ─────────────────────────────────────────────────────────────────────────
  loadInvoices(): void {
    this.invoiceLoading = true;

    const handleResponse = (res: any) => {
      const all: any[] = Array.isArray(res) ? res : (res.data || []);

      // ✅ Normalize currency on every invoice
      const normalized = all.map((inv: any) => ({
        ...inv,
        currencyCode:   inv.currencyCode   || inv.currency || 'INR',
        currencySymbol: inv.currencySymbol || this.getSymbolForCode(inv.currencyCode || inv.currency || 'INR')
      }));

      this.allBuyerInvoices      = normalized.filter((inv: any) => inv.status !== 'DRAFT');
      this.historySearchInvoices = [...this.allBuyerInvoices];
      this.buyerInvoices         = normalized.filter((inv: any) => inv.status === 'SUBMITTED');
      this.filteredInvoices      = [...this.buyerInvoices];
      this.invoiceLoading        = false;

      if (this.preSelectedPoId) {
        this.autoSelectInvoiceForPO(this.preSelectedPoId);
      }
    };

    const handleError = () => {
      if (this.buyerId) {
        this.dataService.getBuyerInvoicesByBuyerId(this.buyerId).subscribe({
          next: handleResponse,
          error: () => { this.invoiceLoading = false; }
        });
      } else {
        this.invoiceLoading = false;
      }
    };

    this.dataService.getBuyerInvoices(this.companyName).subscribe({
      next:  handleResponse,
      error: handleError
    });
  }

  private autoSelectInvoiceForPO(poId: number): void {
    const match = this.buyerInvoices.find((inv: any) => {
      const invPoId = inv.purchaseOrderId || inv.poId || inv.purchaseOrder?.id;
      return Number(invPoId) === poId;
    });
    if (match) this.selectInvoice(match);
  }

  filterInvoices(): void {
    const q = this.invoiceSearch.toLowerCase();
    this.filteredInvoices = this.buyerInvoices.filter(inv =>
      (inv.invoiceNumber || '').toLowerCase().includes(q) ||
      (inv.supplierName  || inv.supplier?.name || '').toLowerCase().includes(q)
    );
  }

  private getPOId(inv: any): number {
    return inv.purchaseOrderId || inv.poId || inv.purchaseOrder?.id || 0;
  }

  selectInvoice(inv: any): void {
    this.selectedInvoice = inv;
    this.selectedGRN     = null;
    this.matchResult     = null;
    this.errorMessage    = '';
    this.poGRNs          = [];

    // ✅ Set active currency from the selected invoice
    this.activeCurrencyCode   = inv.currencyCode   || inv.currency || 'INR';
    this.activeCurrencySymbol = inv.currencySymbol || this.getSymbolForCode(this.activeCurrencyCode);

    const poId = this.getPOId(inv);
    if (!poId) {
      this.errorMessage = 'Could not determine PO reference from this invoice.';
      return;
    }

    this.grnLoading = true;
    this.dataService.getAllGRNsByPO(poId).subscribe({
      next: (res: any) => {
        const all: any[] = Array.isArray(res) ? res : (res.data || []);
        this.poGRNs     = all.filter((g: any) => g.status === 'APPROVED');
        this.grnLoading = false;

        if (this.poGRNs.length === 0) {
          const draft     = all.filter(g => g.status === 'DRAFT').length;
          const submitted = all.filter(g => g.status === 'SUBMITTED').length;
          const qaReview  = all.filter(g => g.status === 'QA_REVIEW').length;
          if (all.length === 0) {
            this.errorMessage = 'No GRNs found for this PO. Please create and approve a GRN first.';
          } else if (submitted + qaReview > 0) {
            this.errorMessage = `${submitted + qaReview} GRN(s) pending QA/approval. Please approve the GRN before running 3-Way Match.`;
          } else if (draft > 0) {
            this.errorMessage = 'GRN(s) are in DRAFT. Please submit and approve the GRN first.';
          }
        } else {
          if (this.preSelectedGrnId) {
            const preGrn = this.poGRNs.find((g: any) => g.id === this.preSelectedGrnId);
            if (preGrn) {
              this.selectedGRN      = preGrn;
              this.preSelectedGrnId = null;
            }
          }
        }
      },
      error: () => { this.grnLoading = false; }
    });
  }

  performMatch(): void {
    if (!this.selectedInvoice || !this.selectedGRN) {
      this.errorMessage = 'Please select both an invoice and a GRN.';
      return;
    }
    this.performLoading = true;
    this.errorMessage   = '';
    const poId = this.getPOId(this.selectedInvoice);

    this.dataService.performThreeWayMatch({
      purchaseOrderId:     poId,
      grnId:               this.selectedGRN.id,
      invoiceId:           this.selectedInvoice.id,
      tolerancePercentage: this.tolerancePercentage,
      performedByUserId:   this.userId
    }).subscribe({
      next: (res: any) => {
        this.matchResult     = res.data || res;
        this.showResultModal = true;
        this.performLoading  = false;
        this.loadPendingMatches();
        this.loadInvoices();
      },
      error: (err: any) => {
        this.errorMessage  = err?.error?.message || 'Failed to perform 3-way match.';
        this.performLoading = false;
      }
    });
  }

  closeResultModal(): void { this.showResultModal = false; }

  // ─────────────────────────────────────────────────────────────────────────
  // PENDING RESOLUTIONS
  // ─────────────────────────────────────────────────────────────────────────
  loadPendingMatches(): void {
    this.pendingLoading = true;
    this.dataService.getPendingMatchResolutions().subscribe({
      next: (res: any) => {
        const raw: any[] = Array.isArray(res) ? res : (res.data || []);
        this.pendingMatches = raw.map(m => this.enrichMatchRecord(m));
        this.pendingLoading = false;
      },
      error: () => { this.pendingLoading = false; }
    });
  }

  private enrichMatchRecord(m: any): any {
    return {
      ...m,
      invoiceNumber: m.invoiceNumber || m.invoice?.invoiceNumber || `INV-${m.invoiceId}`,
      grnNumber:     m.grnNumber     || m.grn?.grnNumber         || `GRN-${m.grnId}`,
      poNumber:      m.poNumber      || m.purchaseOrder?.poNumber || `PO-${m.purchaseOrderId}`,
      // ✅ Preserve currency from match record
      currencyCode:   m.currencyCode   || 'INR',
      currencySymbol: m.currencySymbol || this.getSymbolForCode(m.currencyCode || 'INR')
    };
  }

  openResolve(match: any): void {
    this.resolveTarget = match;
    this.resolveForm   = { resolution: '', approvedPaymentAmount: null, resolutionRemarks: '' };
    this.showResolveModal = true;
    this.errorMessage     = '';
    // ✅ Set currency from the match record being resolved
    this.activeCurrencyCode   = match.currencyCode   || 'INR';
    this.activeCurrencySymbol = match.currencySymbol || this.getSymbolForCode(this.activeCurrencyCode);
  }

  closeResolveModal(): void {
    this.showResolveModal = false;
    this.resolveTarget    = null;
    this.errorMessage     = '';
  }

  submitResolution(): void {
    if (!this.resolveForm.resolution) { this.errorMessage = 'Please select a resolution action.'; return; }
    if (!this.resolveForm.resolutionRemarks?.trim()) { this.errorMessage = 'Resolution remarks are mandatory.'; return; }
    if (this.needsPaymentAmount() && !this.resolveForm.approvedPaymentAmount) {
      this.errorMessage = 'Please enter the approved payment amount.'; return;
    }

    this.resolveLoading = true;
    this.errorMessage   = '';

    const payload: any = {
      resolution:        this.resolveForm.resolution,
      resolutionRemarks: this.resolveForm.resolutionRemarks.trim(),
      resolvedByUserId:  this.userId
    };
    if (this.resolveForm.approvedPaymentAmount != null) {
      payload.approvedPaymentAmount = this.resolveForm.approvedPaymentAmount;
    }

    this.dataService.resolveMatch(this.resolveTarget.id, payload).subscribe({
      next: () => {
        this.successMessage = `Match resolved: ${this.getResolutionLabel(this.resolveForm.resolution)}`;
        this.resolveLoading = false;
        this.closeResolveModal();
        this.loadPendingMatches();
        this.loadInvoices();
      },
      error: (err: any) => {
        this.errorMessage  = err?.error?.message || 'Failed to resolve match.';
        this.resolveLoading = false;
      }
    });
  }

  // ─────────────────────────────────────────────────────────────────────────
  // HISTORY
  // ─────────────────────────────────────────────────────────────────────────
  loadHistory(): void {
    if (!this.historyInvoiceId) { this.errorMessage = 'Select an invoice to view match history.'; return; }
    this.historyLoading = true;
    this.historyMatches = [];

    // ✅ Set currency from the selected history invoice
    const selectedInv = this.historySearchInvoices.find(i => i.id === this.historyInvoiceId);
    if (selectedInv) {
      this.activeCurrencyCode   = selectedInv.currencyCode   || 'INR';
      this.activeCurrencySymbol = selectedInv.currencySymbol || this.getSymbolForCode(this.activeCurrencyCode);
    }

    this.dataService.getMatchesByInvoice(this.historyInvoiceId).subscribe({
      next: (res: any) => {
        const raw: any[] = Array.isArray(res) ? res : (res.data || []);
        this.historyMatches = raw.map(m => this.enrichMatchRecord(m));
        this.historyLoading = false;
        if (this.historyMatches.length === 0) {
          this.errorMessage = 'No match history found for this invoice.';
        }
      },
      error: () => { this.historyLoading = false; }
    });
  }

  // ─────────────────────────────────────────────────────────────────────────
  // HELPERS
  // ─────────────────────────────────────────────────────────────────────────
  needsPaymentAmount(): boolean {
    return ['PARTIAL_PAYMENT_APPROVED', 'ACCEPTED_WITH_VARIANCE'].includes(this.resolveForm.resolution);
  }

  getResolutionLabel(value: string): string {
    return this.resolutionOptions.find(o => o.value === value)?.label || value;
  }

  /**
   * ✅ Format currency using the active buyer location currency.
   * Used everywhere amounts are displayed in this component.
   */
  formatCurrency(amount: number | null | undefined, currencyCode?: string): string {
    const code   = currencyCode || this.activeCurrencyCode || 'INR';
    const symbol = this.getSymbolForCode(code);
    const val    = Number(amount ?? 0);
    const formatted = val.toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol}${formatted}`;
  }

  /** Symbol lookup helper */
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

  getOverallStatusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      MATCHED:             'badge bg-success',
      PARTIAL_MATCH:       'badge bg-info',
      QUANTITY_MISMATCH:   'badge bg-warning text-dark',
      PRICE_MISMATCH:      'badge bg-warning text-dark',
      ITEM_MISMATCH:       'badge bg-danger',
      EXCESS_DELIVERY:     'badge bg-secondary',
      OVERRIDDEN_APPROVED: 'badge bg-success',
      DISPUTED:            'badge bg-danger',
      FAILED:              'badge bg-dark'
    };
    return map[status] || 'badge bg-secondary';
  }

  getLineStatusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      MATCHED:           'badge bg-success',
      PARTIALLY_MATCHED: 'badge bg-info',
      QTY_SHORT:         'badge bg-warning text-dark',
      PRICE_VARIANCE:    'badge bg-warning text-dark',
      QTY_EXCESS:        'badge bg-secondary',
      NOT_IN_PO:         'badge bg-danger',
      NOT_RECEIVED:      'badge bg-danger',
    };
    return (map[status] || 'badge bg-secondary') + ' small';
  }

  getResolutionBadgeClass(status: string): string {
    const map: Record<string, string> = {
      PENDING_RESOLUTION:        'badge bg-warning text-dark',
      ACCEPTED_WITH_VARIANCE:    'badge bg-success',
      REVISED_INVOICE_REQUESTED: 'badge bg-primary',
      PARTIAL_PAYMENT_APPROVED:  'badge bg-info',
      CREDIT_NOTE_REQUESTED:     'badge bg-secondary',
      DEBIT_NOTE_RAISED:         'badge bg-secondary',
      DISPUTED:                  'badge bg-danger',
      CANCELLED:                 'badge bg-dark'
    };
    return map[status] || 'badge bg-secondary';
  }

  dismissAlert(): void { this.errorMessage = ''; this.successMessage = ''; }

  goBack(): void { this.router.navigate(['/grn-list']); }
}