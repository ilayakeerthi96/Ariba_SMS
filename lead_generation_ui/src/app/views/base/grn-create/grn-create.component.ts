import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';

interface Phase1LineItem {
  poLineItemId:    number;
  itemOrder:       number;
  itemName:        string;
  itemCode:        string;
  brandMakeModel:  string;
  specifications:  string;
  uom:             string;
  orderedQty:      number;   // original PO ordered qty
  alreadyReceived: number;   // sum of receivedQty across all SUBMITTED/APPROVED GRNs for this line
  remainingQty:    number;   // orderedQty - alreadyReceived   ← max allowed for this GRN
  poUnitRate:      number;
  receivedQty:     number;
  remarks:         string;
}

@Component({
  selector: 'app-grn-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './grn-create.component.html',
  styleUrls: ['./grn-create.component.css']
})
export class GrnCreateComponent implements OnInit {

  // ── PO Selection ────────────────────────────────────────────────────────────
  approvedPOs:   any[] = [];
  filteredPOs:   any[] = [];
  poSearch         = '';
  selectedPO:    any   = null;
  poLoading        = false;
  poDropdownOpen   = false;

  // ── GRN form ─────────────────────────────────────────────────────────────────
  grn = {
    deliveryDate:          '',
    deliveryChallanNumber: '',
    lrNumber:              '',
    transporterName:       '',
    vehicleNumber:         '',
    deliveryLocation:      '',
    receiverName:          '',
    receiverDesignation:   '',
    receiverRemarks:       '',
    internalNotes:         ''
  };

  lineItems:    Phase1LineItem[] = [];
  existingGRNs: any[]   = [];

  // ── PO fully-received guard ──────────────────────────────────────────────────
  // Set to true once we have loaded existingGRNs and computed that ALL lines are
  // already fully received (remainingQty === 0 on every line).
  isPOFullyReceived = false;
  // Loading flag while we fetch existing GRNs for the selected PO
  existingGRNsLoading = false;

  // ── UI state ─────────────────────────────────────────────────────────────────
  isSubmitting   = false;
  errorMessage   = '';
  successMessage = '';
  companyName    = '';
  userId         = 0;

  // ── Computed totals ──────────────────────────────────────────────────────────
  get today(): string { return new Date().toISOString().split('T')[0]; }

  get totalOrderedQty():    number { return this.lineItems.reduce((s, l) => s + (l.orderedQty    || 0), 0); }
  get totalAlreadyReceived(): number { return this.lineItems.reduce((s, l) => s + (l.alreadyReceived || 0), 0); }
  get totalRemainingQty():  number { return this.lineItems.reduce((s, l) => s + (l.remainingQty  || 0), 0); }
  get totalReceivedQty():   number { return this.lineItems.reduce((s, l) => s + (Number(l.receivedQty) || 0), 0); }

  constructor(
    private dataService: DataService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const p = JSON.parse(atob(token.split('.')[1]));
        this.userId = p.userId || p.id || p.sub || 0;
      } catch {}
    }
    if (!this.userId) this.userId = Number(localStorage.getItem('buyerId') || 0);

    this.grn.receiverName =
      localStorage.getItem('buyerName') ||
      localStorage.getItem('fullName')  ||
      localStorage.getItem('email')     || '';

    try {
      const buyer = JSON.parse(localStorage.getItem('buyer') || '{}');
      this.companyName = buyer.name || '';
    } catch {}
    if (!this.companyName) this.companyName = localStorage.getItem('companyName') || '';

    this.loadApprovedPOs();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.po-search-wrap')) this.poDropdownOpen = false;
  }

  // ── PO Loading & Selection ────────────────────────────────────────────────

  loadApprovedPOs(): void {
    if (!this.companyName) {
      this.errorMessage = 'Company name could not be determined. Please log out and log in again.';
      return;
    }
    this.poLoading = true;
    this.dataService.getPurchaseOrdersByCompany(this.companyName).subscribe({
      next: (res: any) => {
        const all = Array.isArray(res) ? res : (res.data || []);
        this.approvedPOs = all.filter((p: any) =>
          ['APPROVED', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'IN_PROGRESS', 'ACTIVE'].includes(p.status)
        );
        this.filteredPOs = [...this.approvedPOs];
        this.poLoading   = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load purchase orders. Please refresh and try again.';
        this.poLoading    = false;
      }
    });
  }

  filterPOs(): void {
    const q = this.poSearch.toLowerCase().trim();
    this.filteredPOs = !q
      ? [...this.approvedPOs]
      : this.approvedPOs.filter(p =>
          (p.poNumber || '').toLowerCase().includes(q) ||
          (p.supplierName || p.supplier?.companyName || '').toLowerCase().includes(q)
        );
    this.poDropdownOpen = true;
  }

  getSupplierName(po: any): string {
    return po?.supplierName || po?.supplier?.companyName || po?.supplier?.name || '—';
  }

  selectPO(po: any): void {
    this.selectedPO       = po;
    this.poSearch         = po.poNumber;
    this.poDropdownOpen   = false;
    this.errorMessage     = '';
    this.isPOFullyReceived = false;
    this.lineItems        = [];

    // Immediately show the PO's line items with 0 context, then refine once
    // we know existing GRN quantities.
    const rawItems: Phase1LineItem[] = (po.lineItems || po.items || []).map((item: any, idx: number) => ({
      poLineItemId:    item.id,
      itemOrder:       item.slNo || item.itemOrder || idx + 1,
      itemName:        item.itemDescription || item.itemName || item.name || '',
      itemCode:        item.itemCode || item.code || '',
      brandMakeModel:  item.brandMakeModel || item.brand || '',
      specifications:  item.specifications || item.specs || '',
      uom:             item.uom || item.unit || 'NOS',
      orderedQty:      Number(item.quantity || item.orderedQuantity || 0),
      alreadyReceived: 0,
      remainingQty:    Number(item.quantity || item.orderedQuantity || 0),
      poUnitRate:      Number(item.unitRate  || item.unitPrice || item.rate || 0),
      receivedQty:     0,
      remarks:         ''
    }));

    this.lineItems          = rawItems;
    this.existingGRNsLoading = true;

    // ── Fetch ALL existing GRNs for this PO and compute remaining quantities ──
    this.dataService.getAllGRNsByPO(po.id).subscribe({
      next: (res: any) => {
        this.existingGRNs     = Array.isArray(res) ? res : (res.data || []);
        this.existingGRNsLoading = false;

        // Build a map: poLineItemId → total already received
        // Count quantities from GRNs that are SUBMITTED, QA_REVIEW, or APPROVED
        // (i.e. anything that is "active" and not DRAFT or CANCELLED)
        const receivedMap = new Map<number, number>();

        for (const grn of this.existingGRNs) {
          const isActive = ['SUBMITTED', 'QA_REVIEW', 'APPROVED', 'CLOSED'].includes(grn.status);
          if (!isActive) continue; // skip DRAFT and CANCELLED

          const lineItems = grn.lineItems || grn.grnLineItems || [];
          for (const li of lineItems) {
            const key = li.poLineItemId || li.poLineItem?.id;
            if (key == null) continue;
            const qty = Number(li.receivedQuantity ?? li.receivedQty ?? 0);
            receivedMap.set(key, (receivedMap.get(key) || 0) + qty);
          }
        }

        // Apply remaining quantities to line items
        this.lineItems = rawItems.map(li => {
          const already  = receivedMap.get(li.poLineItemId) || 0;
          const remaining = Math.max(0, li.orderedQty - already);
          return {
            ...li,
            alreadyReceived: already,
            remainingQty:    remaining,
            receivedQty:     remaining   // pre-fill with remaining qty (user can reduce)
          };
        });

        // Check if ALL lines are fully received
        this.isPOFullyReceived = this.lineItems.every(li => li.remainingQty === 0);

        if (this.isPOFullyReceived) {
          this.errorMessage = `All quantities for PO ${po.poNumber} have already been fully received across existing GRNs. No new GRN is required.`;
          this.scrollToTop();
        }
      },
      error: () => {
        // If we can't load existing GRNs, fall back to full ordered qty
        this.existingGRNs        = [];
        this.existingGRNsLoading = false;
        this.lineItems = rawItems.map(li => ({
          ...li,
          alreadyReceived: 0,
          remainingQty:    li.orderedQty,
          receivedQty:     li.orderedQty
        }));
      }
    });
  }

  // ── Submit ──────────────────────────────────────────────────────────────────

  submitGRN(saveAsDraft: boolean): void {
    this.errorMessage   = '';
    this.successMessage = '';

    if (!this.selectedPO) {
      this.errorMessage = 'Please select a Purchase Order before submitting.';
      this.scrollToTop(); return;
    }

    // Hard block: all quantities already received
    if (this.isPOFullyReceived) {
      this.errorMessage = `PO ${this.selectedPO.poNumber} has already been fully received. A new GRN cannot be created.`;
      this.scrollToTop(); return;
    }

    if (!this.grn.deliveryDate) {
      this.errorMessage = 'Received date is required. Please select the date goods were received.';
      this.scrollToTop(); return;
    }

    const selectedDate = new Date(this.grn.deliveryDate);
    const todayDate    = new Date();
    todayDate.setHours(0, 0, 0, 0);
    if (selectedDate < todayDate) {
      this.errorMessage = 'Received date cannot be a past date. Please select today or a future date.';
      this.scrollToTop(); return;
    }

    if (!this.grn.receiverName?.trim()) {
      this.errorMessage = 'Receiver name is required.';
      this.scrollToTop(); return;
    }

    if (this.lineItems.length === 0) {
      this.errorMessage = 'No line items found for the selected PO.';
      this.scrollToTop(); return;
    }

    const allZero = this.lineItems.every(l => (Number(l.receivedQty) || 0) === 0);
    if (allZero && !saveAsDraft) {
      this.errorMessage = 'At least one line item must have a received quantity greater than zero.';
      this.scrollToTop(); return;
    }

    for (const li of this.lineItems) {
      const qty = Number(li.receivedQty) || 0;

      if (qty < 0) {
        this.errorMessage = `Received quantity cannot be negative for: "${li.itemName}".`;
        this.scrollToTop(); return;
      }

      // ── KEY GUARD: cannot exceed remaining (not just ordered) ──
      if (qty > li.remainingQty) {
        this.errorMessage =
          `Received quantity (${qty}) exceeds remaining quantity (${li.remainingQty}) for: "${li.itemName}". ` +
          `${li.alreadyReceived} unit(s) have already been received in a previous GRN.`;
        this.scrollToTop(); return;
      }
    }

    this.isSubmitting = true;

    const payload: any = {
      purchaseOrderId:       this.selectedPO.id,
      receivedDate:          this.grn.deliveryDate
                               ? new Date(this.grn.deliveryDate).toISOString()
                               : new Date().toISOString(),
      deliveryChallanNumber: this.grn.deliveryChallanNumber || null,
      lrNumber:              this.grn.lrNumber              || null,
      transporterName:       this.grn.transporterName       || null,
      vehicleNumber:         this.grn.vehicleNumber         || null,
      deliveryLocation:      this.grn.deliveryLocation      || null,
      receivedByUserId:      this.userId,
      receivedByName:        this.grn.receiverName,
      remarks:               this.grn.receiverRemarks       || null,
      internalNotes:         this.grn.internalNotes         || null,
      lineItems: this.lineItems.map((l, idx) => ({
        poLineItemId:     l.poLineItemId,
        itemOrder:        l.itemOrder || idx + 1,
        itemDescription:  l.itemName       || 'Item',
        itemCode:         l.itemCode       || '',
        brandMakeModel:   l.brandMakeModel || '',
        specifications:   l.specifications || '',
        uom:              l.uom            || 'NOS',
        orderedQuantity:  l.orderedQty,
        receivedQuantity: l.receivedQty,
        poUnitRate:       l.poUnitRate     || 0,
        remarks:          l.remarks        || null
      }))
    };

    this.dataService.createGRN(payload).subscribe({
      next: (res: any) => {
        const created = res.data || res;
        if (!saveAsDraft && created?.id) {
          this.dataService.submitGRN(created.id).subscribe({
            next: () => {
              this.successMessage = 'GRN created and submitted successfully! Redirecting to GRN list…';
              this.isSubmitting   = false;
              this.scrollToTop();
              setTimeout(() => this.router.navigate(['/grn-list']), 1800);
            },
            error: (err: any) => {
              this.errorMessage = 'GRN was created but could not be submitted: ' +
                (err?.error?.message || 'Unknown error. Please try submitting from the GRN list.');
              this.isSubmitting = false;
              this.scrollToTop();
            }
          });
        } else {
          this.successMessage = 'GRN saved as draft successfully! Redirecting to GRN list…';
          this.isSubmitting   = false;
          this.scrollToTop();
          setTimeout(() => this.router.navigate(['/grn-list']), 1800);
        }
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to create GRN. Please check your inputs and try again.';
        this.isSubmitting = false;
        this.scrollToTop();
      }
    });
  }

  cancel(): void { this.router.navigate(['/grn-list']); }

  private scrollToTop(): void { window.scrollTo({ top: 0, behavior: 'smooth' }); }
}