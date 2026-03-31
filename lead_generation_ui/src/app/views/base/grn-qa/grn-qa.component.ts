import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';

export interface QALineItemForm {
  grnLineItemId:    number;
  itemDescription:  string;
  orderedQuantity:  number;
  receivedQuantity: number;
  defectiveQuantity: number;
  rejectedQuantity:  number;
  acceptedQuantity:  number;   // auto-computed: receivedQty - rejectedQty
  qaRemarks:        string;
  itemCondition:    string;
}

@Component({
  selector: 'app-grn-qa',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './grn-qa.component.html',
  styleUrls: ['./grn-qa.component.css']
})
export class GrnQaComponent implements OnInit {

  // ── GRN data ────────────────────────────────────────────────────────────────
  grn: any = null;
  grnId = 0;

  // ── Form state ──────────────────────────────────────────────────────────────
  qaLineItems:      QALineItemForm[] = [];
  qaInspectorName   = '';
  qaOverallRemarks  = '';

  // ── UI state ─────────────────────────────────────────────────────────────────
  isLoading     = false;
  isSubmitting  = false;
  errorMessage  = '';
  successMessage= '';
  pagePhase: 'loading' | 'ready' | 'done' = 'loading';

  // ── User ─────────────────────────────────────────────────────────────────────
  userId    = 0;
  buyerName = '';

  conditionOptions = ['GOOD', 'PARTIAL', 'DAMAGED', 'WRONG_ITEM', 'SHORT_DELIVERY'];

  constructor(
    private dataService: DataService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Resolve user
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const p = JSON.parse(atob(token.split('.')[1]));
        this.userId = p.userId || p.id || p.sub || 0;
      } catch {}
    }
    if (!this.userId) this.userId = Number(localStorage.getItem('buyerId') || 0);

    this.buyerName =
      localStorage.getItem('buyerName') ||
      localStorage.getItem('fullName')  ||
      localStorage.getItem('email')     ||
      'QA Inspector';

    this.qaInspectorName = this.buyerName;

    // Get GRN id from route
    this.grnId = Number(this.route.snapshot.paramMap.get('id') || 0);
    if (!this.grnId) {
      this.errorMessage = 'Invalid GRN ID.';
      this.pagePhase = 'ready';
      return;
    }

    this.startQAFlow();
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // FLOW: startQAReview (if SUBMITTED) → load full GRN → populate form
  // ═══════════════════════════════════════════════════════════════════════════

  private startQAFlow(): void {
    this.isLoading  = true;
    this.pagePhase  = 'loading';
    this.errorMessage = '';

    // First load the GRN to check its current status
    this.dataService.getGRNById(this.grnId).subscribe({
      next: (res: any) => {
        const g = res.data || res;
        const status = g.status || g.grnStatus;

        if (status === 'SUBMITTED') {
          // Call startQAReview to transition SUBMITTED → QA_REVIEW
          this.dataService.startQAReview(this.grnId, this.buyerName).subscribe({
            next: (qaRes: any) => {
              // After transition, reload fresh data
              this.loadGRNData();
            },
            error: (err: any) => {
              // If already QA_REVIEW (race / reload), just load data
              const msg = err?.error?.message || '';
              if (msg.toLowerCase().includes('qa_review') || err?.status === 400) {
                this.loadGRNData();
              } else {
                this.errorMessage = msg || 'Failed to start QA review.';
                this.isLoading    = false;
                this.pagePhase    = 'ready';
              }
            }
          });
        } else if (status === 'QA_REVIEW') {
          // Already in QA_REVIEW — load data directly
          this.loadGRNData();
        } else {
          // Wrong status (shouldn't normally land here)
          this.errorMessage = `GRN is in "${status}" status. QA Review is only available for SUBMITTED or QA_REVIEW GRNs.`;
          this.isLoading    = false;
          this.pagePhase    = 'ready';
        }
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to load GRN.';
        this.isLoading    = false;
        this.pagePhase    = 'ready';
      }
    });
  }

  private loadGRNData(): void {
    this.dataService.getGRNById(this.grnId).subscribe({
      next: (res: any) => {
        this.grn = this.normalizeGrn(res.data || res);

        this.qaLineItems = (this.grn.lineItems || []).map((li: any) => ({
          grnLineItemId:     li.id,
          itemDescription:   li.itemDescription || '',
          orderedQuantity:   Number(li.orderedQuantity)   || 0,
          receivedQuantity:  Number(li.receivedQuantity)  || 0,
          defectiveQuantity: Number(li.defectiveQuantity) || 0,
          rejectedQuantity:  Number(li.rejectedQuantity)  || 0,
          acceptedQuantity:  Number(li.acceptedQuantity)  || Number(li.receivedQuantity) || 0,
          qaRemarks:     li.qaRemarks     || '',
          itemCondition: li.itemCondition || 'GOOD'
        }));

        if (this.grn.inspectedByName) this.qaInspectorName = this.grn.inspectedByName;
        if (this.grn.qaOverallRemarks || this.grn.internalNotes) {
          this.qaOverallRemarks = this.grn.qaOverallRemarks || this.grn.internalNotes || '';
        }

        this.isLoading = false;
        this.pagePhase = 'ready';
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to load GRN details.';
        this.isLoading    = false;
        this.pagePhase    = 'ready';
      }
    });
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // REACTIVE FIELD HELPERS
  // ═══════════════════════════════════════════════════════════════════════════

  onDefectiveChange(li: QALineItemForm): void {
    if (li.rejectedQuantity < li.defectiveQuantity) li.rejectedQuantity = li.defectiveQuantity;
    this.recalcAccepted(li);
  }

  onRejectedChange(li: QALineItemForm): void {
    if (li.rejectedQuantity > li.receivedQuantity) li.rejectedQuantity = li.receivedQuantity;
    if (li.rejectedQuantity < 0) li.rejectedQuantity = 0;
    this.recalcAccepted(li);
  }

  private recalcAccepted(li: QALineItemForm): void {
    li.acceptedQuantity = Math.max(0, li.receivedQuantity - li.rejectedQuantity);
  }

  qaGetTotal(field: keyof QALineItemForm): number {
    return this.qaLineItems.reduce((s, li) => s + (Number((li as any)[field]) || 0), 0);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // VALIDATION
  // ═══════════════════════════════════════════════════════════════════════════

  validate(): string | null {
    if (!this.qaInspectorName.trim()) return 'Inspector name is required.';
    for (const li of this.qaLineItems) {
      if (li.rejectedQuantity > li.receivedQuantity) {
        return `Rejected qty (${li.rejectedQuantity}) exceeds received qty (${li.receivedQuantity}) for: ${li.itemDescription}`;
      }
      if (li.rejectedQuantity > 0 && !li.qaRemarks?.trim()) {
        return `QA remarks are required when rejecting items. Item: ${li.itemDescription}`;
      }
    }
    return null;
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // SUBMIT ACTIONS
  // ═══════════════════════════════════════════════════════════════════════════

  /**
   * Save QA data only — GRN stays in QA_REVIEW. Supervisor approves later.
   */
  saveQAData(): void {
    const err = this.validate();
    if (err) { this.errorMessage = err; return; }
    this.errorMessage  = '';
    this.isSubmitting  = true;

    this.dataService.submitQAReview(this.grnId, this.buildPayload()).subscribe({
      next: () => {
        this.successMessage = 'QA data saved. GRN is awaiting supervisor approval.';
        this.isSubmitting   = false;
        // Reload to reflect saved state
        this.loadGRNData();
      },
      error: (e: any) => {
        this.errorMessage = e?.error?.message || 'Failed to save QA data.';
        this.isSubmitting = false;
      }
    });
  }

  /**
   * Save QA data AND approve — QA_REVIEW → APPROVED in one step.
   */
  completeQAAndApprove(): void {
    const err = this.validate();
    if (err) { this.errorMessage = err; return; }
    this.errorMessage  = '';
    this.isSubmitting  = true;

    this.dataService.submitQAReview(this.grnId, this.buildPayload()).subscribe({
      next: () => {
        this.dataService.approveGRN(this.grnId, this.buyerName).subscribe({
          next: () => {
            this.successMessage = 'QA review completed and GRN approved! Ready for 3-Way Match.';
            this.isSubmitting   = false;
            this.pagePhase      = 'done';
            setTimeout(() => this.router.navigate(['/grn-list']), 2000);
          },
          error: (e: any) => {
            this.errorMessage = e?.error?.message || 'QA saved but approval failed.';
            this.isSubmitting = false;
          }
        });
      },
      error: (e: any) => {
        this.errorMessage = e?.error?.message || 'Failed to submit QA review.';
        this.isSubmitting = false;
      }
    });
  }

  private buildPayload(): any {
    return {
      grnId:            this.grnId,
      inspectedByName:  this.qaInspectorName,
      qaOverallRemarks: this.qaOverallRemarks,
      lineItems: this.qaLineItems.map(li => ({
        grnLineItemId:     li.grnLineItemId,
        defectiveQuantity: li.defectiveQuantity,
        rejectedQuantity:  li.rejectedQuantity,
        qaRemarks:         li.qaRemarks || '',
        itemCondition:     li.itemCondition || 'GOOD'
      }))
    };
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // HELPERS
  // ═══════════════════════════════════════════════════════════════════════════

  goBack(): void { this.router.navigate(['/grn-list']); }
  dismissAlert(): void { this.errorMessage = ''; this.successMessage = ''; }

  private normalizeGrn(g: any): any {
    const lineItems = (g.lineItems || g.grnLineItems || []).map((li: any) => ({
      ...li,
      id:                li.id,
      itemDescription:   li.itemDescription  || li.itemName   || '',
      orderedQuantity:   Number(li.orderedQuantity   ?? li.orderedQty   ?? 0),
      receivedQuantity:  Number(li.receivedQuantity  ?? li.receivedQty  ?? 0),
      defectiveQuantity: Number(li.defectiveQuantity ?? 0),
      rejectedQuantity:  Number(li.rejectedQuantity  ?? li.rejectedQty  ?? 0),
      acceptedQuantity:  Number(li.acceptedQuantity  ?? li.acceptedQty  ?? li.receivedQuantity ?? li.receivedQty ?? 0),
      qaRemarks:     li.qaRemarks     || '',
      itemCondition: li.itemCondition || li.condition || 'GOOD',
    }));
    return {
      ...g,
      deliveryDate:    g.deliveryDate || g.receivedDate || '',
      lineItems,
      poNumber:        g.poNumber        || g.purchaseOrder?.poNumber || '',
      supplierName:    g.supplierName    || g.supplier?.name          || '',
      receivedByName:  g.receivedByName  || g.receiverName            || '',
      inspectedByName: g.inspectedByName || '',
    };
  }

  formatDate(d: string): string {
    if (!d) return '—';
    try { return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }); }
    catch { return '—'; }
  }
}