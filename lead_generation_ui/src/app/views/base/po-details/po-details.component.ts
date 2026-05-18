

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CardComponent, CardBodyComponent, CardHeaderComponent,
  RowComponent, ColComponent, ContainerComponent,
  ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';

@Component({
  selector: 'app-po-details',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent,
    ButtonDirective, BadgeComponent, TableModule, SpinnerComponent
  ],
  templateUrl: './po-details.component.html',
  styleUrls: ['./po-details.component.css']
})
export class PODetailsComponent implements OnInit {

  isDownloadingExcel = false;
isDownloadingPdf   = false;
  po: any = null;
  isLoading   = false;
  isDeleting  = false;
  isSaving    = false;
  poId        = 0;
  editForm: any = {};

  // Logo state
  logoLoadError = false;

  // Currency from PO data
  currencyCode: string = 'INR';
  currencySymbol: string = '₹';

  get spacerRows(): number[] {
    const minRows = 8;
    const extra = Math.max(0, minRows - (this.po?.lineItems?.length || 0));
    return Array(extra).fill(0);
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.poId = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.poId || isNaN(this.poId)) {
      this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
      this.router.navigate(['/po-list']);
      return;
    }
    this.loadPODetails();
  }

  downloadPOExcel(): void {
    if (!this.poId) return;
    this.isDownloadingExcel = true;
    this.dataService.getPOSummaryExcel(this.poId).subscribe({
        next: (blob: Blob) => {
            this.dataService.saveBlob(blob, `PO_Report_${this.po?.poNumber}_${this.dataService.todayStr()}.xlsx`);
            this.messageService.showMessage('success', 'Success', 'PO Excel report downloaded');
            this.isDownloadingExcel = false;
        },
        error: () => {
            this.messageService.showMessage('error', 'Error', 'Failed to download PO Excel');
            this.isDownloadingExcel = false;
        }
    });
}
 
downloadPOPdf(): void {
    if (!this.poId) return;
    this.isDownloadingPdf = true;
    this.dataService.getPOSummaryPDF(this.poId).subscribe({
        next: (blob: Blob) => {
            this.dataService.saveBlob(blob, `PO_Report_${this.po?.poNumber}_${this.dataService.todayStr()}.pdf`);
            this.messageService.showMessage('success', 'Success', 'PO PDF report downloaded');
            this.isDownloadingPdf = false;
        },
        error: () => {
            this.messageService.showMessage('error', 'Error', 'Failed to download PO PDF');
            this.isDownloadingPdf = false;
        }
    });
}

  // ── Load ─────────────────────────────────────────────────────────────────

  loadPODetails(): void {
    this.isLoading = true;
    this.logoLoadError = false;
    this.dataService.getPurchaseOrderById(this.poId).subscribe({
      next: (response: any) => {
        if (response?.success) {
          this.po = response.data;
          this.currencyCode   = this.po.currencyCode   || 'INR';
          this.currencySymbol = this.po.currencySymbol || this.getSymbolForCode(this.currencyCode);
        } else {
          this.messageService.showMessage('error', 'Error', 'Purchase Order not found');
          this.router.navigate(['/po-list']);
        }
        this.isLoading = false;
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to load Purchase Order details');
        this.isLoading = false;
        this.router.navigate(['/po-list']);
      }
    });
  }

  // ── Logo helpers ─────────────────────────────────────────────────────────

  /**
   * Returns the logo src to bind to <img [src]>.
   * Works with both a URL string and a base64 data-URI.
   * Returns null if no logo is available or if it previously failed to load.
   */
  getBuyerLogoSrc(): string | null {
    if (this.logoLoadError) return null;
    const url = this.po?.buyerLogoUrl;
    if (url && url.trim().length > 0) return url;
    return null;
  }

  /**
   * Called when the logo <img> fires an error event.
   * Hides the broken image and shows the initials fallback instead.
   */
  onLogoError(event: Event): void {
    this.logoLoadError = true;
    (event.target as HTMLImageElement).style.display = 'none';
  }

  /**
   * Returns 1–2 letter initials from the buyer company name.
   * e.g. "ITTI PVT LTD" → "IT"
   */
  getBuyerInitials(): string {
    const name = this.po?.buyerName || '';
    if (!name) return '?';
    const words = name.trim().split(/\s+/).filter((w: string) => w.length > 0);
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0][0] + words[1][0]).toUpperCase();
  }

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

  // ── Edit Modal ───────────────────────────────────────────────────────────

  openEditModal(): void {
    if (this.po?.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT Purchase Orders can be edited');
      return;
    }
    this.editForm = {
      paymentTerms:      this.po.paymentTerms      || '',
      deliveryTerms:     this.po.deliveryTerms      || '',
      otherTerms:        this.po.otherTerms          || '',
      dispatchedThrough: this.po.dispatchedThrough  || '',
      modeOfPayment:     this.po.modeOfPayment      || '',
      destination:       this.po.destination        || '',
      buyerRemarks:      this.po.buyerRemarks        || '',
      internalNotes:     this.po.internalNotes       || ''
    };
    const modal = document.getElementById('editModal');
    if (modal) {
      modal.style.display = 'block';
      modal.classList.add('show');
      document.body.classList.add('modal-open');
      const backdrop = document.createElement('div');
      backdrop.className = 'modal-backdrop fade show';
      backdrop.id = 'editModalBackdrop';
      document.body.appendChild(backdrop);
    }
  }

  closeEditModal(): void {
    const modal = document.getElementById('editModal');
    if (modal) { modal.style.display = 'none'; modal.classList.remove('show'); }
    document.body.classList.remove('modal-open');
    document.getElementById('editModalBackdrop')?.remove();
  }

  saveEdit(): void {
    if (!this.editForm) return;
    this.isSaving = true;
    this.dataService.updatePurchaseOrder(this.poId, this.editForm).subscribe({
      next: (response: any) => {
        if (response?.success) {
          this.po = response.data;
          this.currencyCode   = this.po.currencyCode   || this.currencyCode;
          this.currencySymbol = this.po.currencySymbol || this.currencySymbol;
          this.messageService.showMessage('success', 'Success', 'Purchase Order updated successfully');
          this.closeEditModal();
        } else {
          this.messageService.showMessage('error', 'Error', 'Failed to update Purchase Order');
        }
        this.isSaving = false;
      },
      error: (error: any) => {
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to update Purchase Order');
        this.isSaving = false;
      }
    });
  }

  // ── Delete ───────────────────────────────────────────────────────────────

  deletePO(): void {
    if (this.po?.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT Purchase Orders can be deleted'); return;
    }
    if (!confirm(`Delete Purchase Order ${this.po.poNumber}?\n\nThis action cannot be undone.`)) return;
    this.isDeleting = true;
    this.dataService.deletePurchaseOrder(this.poId).subscribe({
      next: (response: any) => {
        if (response?.success) {
          this.messageService.showMessage('success', 'Success', 'Purchase Order deleted successfully');
          this.router.navigate(['/po-list']);
        }
        this.isDeleting = false;
      },
      error: (error: any) => {
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to delete Purchase Order');
        this.isDeleting = false;
      }
    });
  }

  // ── Print ────────────────────────────────────────────────────────────────

  printPO(): void { window.print(); }

  // ── Tax / Discount helpers ───────────────────────────────────────────────

  hasTax(): boolean {
    return (this.po?.lineItems || []).some((item: any) =>
      item.taxPercentage != null && Number(item.taxPercentage) > 0
    );
  }

  getTaxGroups(): { rate: number; base: number; amount: number }[] {
    const groups: { [rate: string]: { base: number; amount: number } } = {};
    for (const item of (this.po?.lineItems || [])) {
      const rate = Number(item.taxPercentage || 0);
      if (rate === 0) continue;
      const key = rate.toFixed(2);
      if (!groups[key]) groups[key] = { base: 0, amount: 0 };
      groups[key].base   += Number(item.lineTotal   || 0);
      groups[key].amount += Number(item.taxAmount   || 0);
    }
    return Object.entries(groups)
      .map(([rate, val]) => ({ rate: Number(rate), base: val.base, amount: val.amount }))
      .sort((a, b) => a.rate - b.rate);
  }

  hasDiscount(): boolean {
    return (this.po?.lineItems || []).some((item: any) =>
      item.discountPercentage != null && Number(item.discountPercentage) > 0
    );
  }

  getOverallDiscount(): number {
    if (!this.po) return 0;
    const subtotal   = Number(this.po.subtotal   || 0);
    const taxAmount  = Number(this.po.taxAmount  || 0);
    const grandTotal = Number(this.po.grandTotal || 0);
    const discount   = (subtotal + taxAmount) - grandTotal;
    return discount > 0.005 ? discount : 0;
  }

  // ── Status / Date / Nav helpers ──────────────────────────────────────────

  getStatusBadgeClass(status: string): string {
    const map: any = {
      'DRAFT': 'bg-secondary', 'PENDING_APPROVAL': 'bg-warning text-dark',
      'APPROVED': 'bg-success', 'SENT_TO_SUPPLIER': 'bg-info',
      'ACKNOWLEDGED': 'bg-primary', 'IN_PROGRESS': 'bg-primary',
      'DELIVERED': 'bg-success', 'COMPLETED': 'bg-dark',
      'CANCELLED': 'bg-danger', 'REJECTED': 'bg-danger'
    };
    return map[status] || 'bg-secondary';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }

  formatDateShort(dateString: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  }

  goBack(): void { this.router.navigate(['/po-list']); }
}