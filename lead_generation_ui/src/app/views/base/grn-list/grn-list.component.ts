
// // import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormsModule } from '@angular/forms';
// // import { Router, RouterModule } from '@angular/router';
// // import { DataService } from '../../../shared/service/DataService';
// // import { AuthService } from '../../../shared/service/AuthService';
// // import { BuyerService } from '../dashboard/buyer-b.service';

// // interface UpdateLineItem {
// //   grnLineItemId:    number;
// //   poLineItemId:     number;
// //   itemDescription:  string;
// //   uom:              string;
// //   orderedQuantity:  number;
// //   previousReceived: number;
// //   remainingQty:     number;
// //   additionalQty:    number;
// //   newTotalReceived: number;
// // }

// // @Component({
// //   selector: 'app-grn-list',
// //   standalone: true,
// //   imports: [CommonModule, FormsModule, RouterModule],
// //   templateUrl: './grn-list.component.html',
// //   styleUrls: ['./grn-list.component.css']
// // })
// // export class GrnListComponent implements OnInit {

// //   // ==================== BUYER INFO ====================
// // loggedInBuyer: any = null;
// // buyerId: number | null = null;

// //   grns: any[] = [];
// //   filteredGrns: any[] = [];
// //   pagedGrns: any[] = [];

// //   isLoading      = false;
// //   actionLoading  = false;
// //   errorMessage   = '';
// //   successMessage = '';

// //   searchText   = '';
// //   statusFilter = '';

// //   currentPage      = 1;
// //   pageSize         = 10;
// //   totalPages       = 1;
// //   pageSizeOptions  = [5, 10, 25, 50];

// //   showViewModal = false;
// //   viewGrn: any  = null;
// //   viewLoading   = false;

// //   showActionModal = false;
// //   actionType: 'approve' | 'cancel' = 'approve';
// //   selectedGrn: any = null;

// //   showMatchModal       = false;
// //   matchGrn: any        = null;
// //   matchInvoices: any[] = [];
// //   selectedInvoiceId: number | null = null;
// //   matchTolerance       = 2;
// //   matchSubmitting      = false;
// //   matchResult: any     = null;
// //   matchErrorMessage    = '';
// //   matchInvoicesLoading = false;

// //   // UPDATE RECEIVED QTY MODAL
// //   showUpdateModal       = false;
// //   updateGrn: any        = null;
// //   updateLineItems: UpdateLineItem[] = [];
// //   updateSubmitting      = false;
// //   updateErrorMessage    = '';
// //   updateSuccessMessage  = '';
// //   // 0=idle 1=reverting 2=updating 3=resubmitting 4=done
// //   updateStep      = 0;
// //   updateStepLabel = '';

// //   userId    = 0;
// //   buyerName = '';

// //   constructor(
// //     private dataService: DataService,
// //     private authService: AuthService,
// //     private router: Router,
// //     private cdr: ChangeDetectorRef,
// //     private buyerService: BuyerService 
// //   ) {}


// //   private loadLoggedInBuyer(): void {
// //   this.buyerId = Number(localStorage.getItem('buyerId'));
// //   if (!this.buyerId || isNaN(this.buyerId)) return;

// //   this.buyerService.getBuyerById(this.buyerId).subscribe({
// //     next: (response: any) => {
// //       const buyerDetails = response?.data || response;
// //       if (!buyerDetails || !buyerDetails.id) return;

// //       const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
// //       const loggedInName  = localStorage.getItem('fullName');
// //       const loggedInPhone = localStorage.getItem('phone');

// //       let city = 'N/A', state = 'N/A';
// //       const userLocationId = localStorage.getItem('locationId');
// //       if (buyerDetails.locations && Array.isArray(buyerDetails.locations)) {
// //         let selectedLoc = userLocationId
// //           ? buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId))
// //           : null;
// //         if (!selectedLoc && buyerDetails.locations.length > 0) selectedLoc = buyerDetails.locations[0];
// //         if (selectedLoc) { city = selectedLoc.city || 'N/A'; state = selectedLoc.state || 'N/A'; }
// //       }

// //       this.loggedInBuyer = {
// //         companyName:        buyerDetails.companyName || 'N/A',
// //         companyType:        buyerDetails.companyType || 'IT',
// //         email:              loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
// //         contactPersonName:  loggedInName  || buyerDetails.contactPersonName  || 'N/A',
// //         contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A',
// //         city, state
// //       };
// //       this.cdr.markForCheck();
// //     },
// //     error: () => {}
// //   });
// // }

// // getInitials(name: string): string {
// //   if (!name) return 'NA';
// //   const parts = name.trim().split(' ');
// //   return parts.length >= 2
// //     ? (parts[0][0] + parts[1][0]).toUpperCase()
// //     : name.substring(0, 2).toUpperCase();
// // }

// //   ngOnInit(): void {
// //     const token = localStorage.getItem('token');
// //     if (token) {
// //       try {
// //         const p = JSON.parse(atob(token.split('.')[1]));
// //         this.userId = p.userId || p.id || p.sub || 0;
// //       } catch {}
// //     }
// //     if (!this.userId) this.userId = Number(localStorage.getItem('buyerId') || 0);

// //     this.buyerName =
// //       localStorage.getItem('buyerName') ||
// //       localStorage.getItem('fullName')  ||
// //       localStorage.getItem('email')     || 'Buyer';

// //     this.loadGRNs();
// //     this.loadLoggedInBuyer(); 
// //   }

// //   // loadGRNs(): void {
// //   //   this.isLoading    = true;
// //   //   this.errorMessage = '';
// //   //   this.dataService.getGRNsByUser(this.userId).subscribe({
// //   //     next: (res: any) => {
// //   //       const raw = Array.isArray(res) ? res : (res.data || []);
// //   //       this.grns = raw.map((g: any) => this.normalizeGrn(g));
// //   //       this.applyFilters();
// //   //       this.isLoading = false;
// //   //       this.cdr.markForCheck();
// //   //     },
// //   //     error: (err: any) => {
// //   //       this.errorMessage = err?.error?.message || 'Failed to load GRNs.';
// //   //       this.isLoading    = false;
// //   //     }
// //   //   });
// //   // }
// //   loadGRNs(): void {
// //   this.isLoading    = true;
// //   this.errorMessage = '';
// //   this.dataService.getGRNsByUser(this.userId).subscribe({
// //     next: (res: any) => {
// //       const raw = Array.isArray(res) ? res : (res.data || []);
// //       const normalized = raw.map((g: any) => this.normalizeGrn(g));
      
// //       // Group by PO and compute cross-GRN totals per PO line
// //       this.enrichWithCrossGrnData(normalized);
      
// //       this.grns = normalized;
// //       this.applyFilters();
// //       this.isLoading = false;
// //       this.cdr.markForCheck();
// //     },
// //     error: (err: any) => {
// //       this.errorMessage = err?.error?.message || 'Failed to load GRNs.';
// //       this.isLoading    = false;
// //     }
// //   });
// // }

// //   applyFilters(): void {
// //     let data = [...this.grns];
// //     if (this.searchText.trim()) {
// //       const q = this.searchText.toLowerCase();
// //       data = data.filter(g =>
// //         (g.grnNumber    || '').toLowerCase().includes(q) ||
// //         (g.poNumber     || '').toLowerCase().includes(q) ||
// //         (g.supplierName || '').toLowerCase().includes(q)
// //       );
// //     }
// //     if (this.statusFilter) data = data.filter(g => g.status === this.statusFilter);
// //     this.filteredGrns = data;
// //     this.totalPages   = Math.max(1, Math.ceil(data.length / this.pageSize));
// //     this.currentPage  = 1;
// //     this.updatePage();
// //   }

// //   updatePage(): void {
// //     const start = (this.currentPage - 1) * this.pageSize;
// //     this.pagedGrns = this.filteredGrns.slice(start, start + this.pageSize);
// //   }

// //   onSearch():                     void { this.applyFilters(); }
// //   onStatusFilter(s: string):      void { this.statusFilter = s; this.applyFilters(); }
// //   prevPage():                     void { if (this.currentPage > 1) { this.currentPage--; this.updatePage(); } }
// //   nextPage():                     void { if (this.currentPage < this.totalPages) { this.currentPage++; this.updatePage(); } }
// //   goToPage(p: number):            void { if (p >= 1 && p <= this.totalPages) { this.currentPage = p; this.updatePage(); } }
// //   onPageSizeChange(size: number): void { this.pageSize = size; this.totalPages = Math.max(1, Math.ceil(this.filteredGrns.length / size)); this.currentPage = 1; this.updatePage(); }
// //   countByStatus(s: string):       number { return this.grns.filter(g => g.status === s).length; }

// //   get pageNumbers(): number[] {
// //     const total = this.totalPages, current = this.currentPage, pages: number[] = [];
// //     if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
// //     pages.push(1);
// //     if (current > 3) pages.push(-1);
// //     const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
// //     for (let i = start; i <= end; i++) pages.push(i);
// //     if (current < total - 2) pages.push(-1);
// //     pages.push(total);
// //     return pages;
// //   }

// //   hasPartialReceipt(grn: any): boolean {
// //     const allowedStatuses = ['SUBMITTED', 'QA_REVIEW', 'APPROVED'];
// //     if (!allowedStatuses.includes(grn.status)) return false;
// //     return (grn.lineItems || []).some(
// //       (li: any) => Number(li.receivedQuantity) < Number(li.orderedQuantity)
// //     );
// //   }

// //   // NEW method — show "Partial" badge only when received < ordered
// // isPartialDelivery(grn: any): boolean {
// //   return grn.isPartialBatch === true;
// // }

// //   // ── UPDATE RECEIVED QTY MODAL ─────────────────────────────────────────────────

// //   openUpdateModal(grn: any): void {
// //     this.updateGrn            = null;
// //     this.updateLineItems      = [];
// //     this.updateErrorMessage   = '';
// //     this.updateSuccessMessage = '';
// //     this.updateStep           = 0;
// //     this.updateStepLabel      = '';
// //     this.showUpdateModal      = true;

// //     this.dataService.getGRNById(grn.id).subscribe({
// //       next: (res: any) => {
// //         const fresh = this.normalizeGrn(res.data || res);
// //         this.updateGrn = fresh;

// //         this.updateLineItems = (fresh.lineItems || [])
// //           .filter((li: any) => Number(li.orderedQuantity) > Number(li.receivedQuantity))
// //           .map((li: any) => {
// //             const prev      = Number(li.receivedQuantity);
// //             const ordered   = Number(li.orderedQuantity);
// //             const remaining = Math.max(0, ordered - prev);
// //             return {
// //               grnLineItemId:    li.id,
// //               poLineItemId:     li.poLineItemId || li.poLineItem?.id,
// //               itemDescription:  li.itemDescription || li.itemName || '—',
// //               uom:              li.uom || 'NOS',
// //               orderedQuantity:  ordered,
// //               previousReceived: prev,
// //               remainingQty:     remaining,
// //               additionalQty:    remaining,
// //               newTotalReceived: ordered
// //             };
// //           });

// //         if (this.updateLineItems.length === 0) {
// //           this.updateErrorMessage = 'All line items have already been fully received. Nothing to update.';
// //         }
// //       },
// //       error: (err: any) => {
// //         this.updateErrorMessage = err?.error?.message || 'Failed to load GRN details.';
// //       }
// //     });
// //   }

// //   closeUpdateModal(): void {
// //     this.showUpdateModal      = false;
// //     this.updateGrn            = null;
// //     this.updateLineItems      = [];
// //     this.updateErrorMessage   = '';
// //     this.updateSuccessMessage = '';
// //     this.updateStep           = 0;
// //     this.updateStepLabel      = '';
// //   }

// //   onAdditionalQtyChange(li: UpdateLineItem): void {
// //     if (li.additionalQty < 0)               li.additionalQty = 0;
// //     if (li.additionalQty > li.remainingQty)  li.additionalQty = li.remainingQty;
// //     li.newTotalReceived = li.previousReceived + li.additionalQty;
// //   }

// //   get updateTotalAdditional(): number {
// //     return this.updateLineItems.reduce((s, li) => s + (Number(li.additionalQty) || 0), 0);
// //   }

// //   updateTotal(field: keyof UpdateLineItem): number {
// //     return this.updateLineItems.reduce((s, li) => s + (Number((li as any)[field]) || 0), 0);
// //   }

// //   /**
// //    * 3-step silent flow:
// //    *   Step 1 — PUT /api/grn/{id}/revert-to-draft   (skipped if already DRAFT)
// //    *   Step 2 — PUT /api/grn/{id}                   (update quantities)
// //    *   Step 3 — PUT /api/grn/{id}/submit             (re-submit for QA)
// //    */
// //   submitUpdateReceivedQty(): void {
// //     this.updateErrorMessage = '';

// //     if (!this.updateGrn) { this.updateErrorMessage = 'No GRN loaded.'; return; }
// //     if (this.updateTotalAdditional === 0) {
// //       this.updateErrorMessage = 'Please enter at least one additional received quantity greater than zero.';
// //       return;
// //     }
// //     for (const li of this.updateLineItems) {
// //       if (li.additionalQty < 0) {
// //         this.updateErrorMessage = `Additional qty cannot be negative for: "${li.itemDescription}"`;
// //         return;
// //       }
// //       if (li.additionalQty > li.remainingQty) {
// //         this.updateErrorMessage =
// //           `Additional qty (${li.additionalQty}) exceeds remaining qty (${li.remainingQty}) for: "${li.itemDescription}"`;
// //         return;
// //       }
// //     }

// //     this.updateSubmitting = true;
// //     const grnId     = this.updateGrn.id;
// //     const grnNumber = this.updateGrn.grnNumber;
// //     const addedQty  = this.updateTotalAdditional;
// //     const isDraft   = this.updateGrn.status === 'DRAFT';

// //     const allLines = (this.updateGrn.lineItems || []).map((li: any) => {
// //       const upd = this.updateLineItems.find(u => u.grnLineItemId === li.id);
// //       return {
// //         id:               li.id,
// //         poLineItemId:     li.poLineItemId || li.poLineItem?.id,
// //         itemOrder:        li.itemOrder || 0,
// //         itemDescription:  li.itemDescription || '',
// //         itemCode:         li.itemCode || '',
// //         brandMakeModel:   li.brandMakeModel || '',
// //         specifications:   li.specifications || '',
// //         uom:              li.uom || 'NOS',
// //         orderedQuantity:  li.orderedQuantity,
// //         receivedQuantity: upd ? upd.newTotalReceived : li.receivedQuantity,
// //         poUnitRate:       li.poUnitRate || 0,
// //         remarks:          li.remarks || null
// //       };
// //     });
   
// //     const updatePayload = {
// //   purchaseOrderId:       this.updateGrn.purchaseOrderId || this.updateGrn.poId,
// //   receivedDate:          this.updateGrn.deliveryDate || this.updateGrn.receivedDate,
// //   deliveryChallanNumber: this.updateGrn.deliveryChallanNumber || null,
// //   lrNumber:              this.updateGrn.lrNumber              || null,
// //   transporterName:       this.updateGrn.transporterName       || null,
// //   vehicleNumber:         this.updateGrn.vehicleNumber         || null,
// //   deliveryLocation:      this.updateGrn.deliveryLocation      || null,
// //   receivedByUserId:      this.updateGrn.receivedByUserId      || this.userId,
// //   receivedByName:        this.updateGrn.receivedByName        || null,
// //   remarks:               this.updateGrn.remarks               || null,
// //   internalNotes:         this.updateGrn.internalNotes         || null,
// //   lineItems: allLines
// // };

// //     // Step 2+3 inner helper
// //     const step2and3 = () => {
// //       this.updateStep      = 2;
// //       this.updateStepLabel = 'Step 2 of 3 — Updating received quantities…';
// //       this.cdr.markForCheck();

// //       this.dataService.updateGRN(grnId, updatePayload).subscribe({
// //         next: () => {
// //           this.updateStep      = 3;
// //           this.updateStepLabel = 'Step 3 of 3 — Re-submitting GRN for QA review…';
// //           this.cdr.markForCheck();

// //           this.dataService.submitGRN(grnId).subscribe({
// //             next: () => {
// //               this.updateStep           = 4;
// //               this.updateStepLabel      = '';
// //               this.updateSuccessMessage =
// //                 `GRN ${grnNumber} updated successfully! Added ${addedQty} unit(s). ` +
// //                 `GRN has been re-submitted for QA review.`;
// //               this.updateSubmitting     = false;
// //               this.loadGRNs();
// //               this.cdr.markForCheck();
// //               setTimeout(() => this.closeUpdateModal(), 2800);
// //             },
// //             error: (err: any) => {
// //               this.updateErrorMessage =
// //                 `Quantities saved but re-submit failed: ` +
// //                 (err?.error?.message || 'Please manually submit the GRN from the list.');
// //               this.updateSubmitting = false;
// //               this.updateStep       = 0;
// //               this.loadGRNs();
// //               this.cdr.markForCheck();
// //             }
// //           });
// //         },
// //         error: (err: any) => {
// //           this.updateErrorMessage =
// //             `Failed to update quantities: ${err?.error?.message || 'Please try again.'}`;
// //           this.updateSubmitting = false;
// //           this.updateStep       = 0;
// //           this.cdr.markForCheck();
// //         }
// //       });
// //     };

// //     if (isDraft) {
// //       step2and3();
// //     } else {
// //       // Step 1 — revert to DRAFT
// //       this.updateStep      = 1;
// //       this.updateStepLabel = 'Step 1 of 3 — Reverting GRN to draft…';
// //       this.cdr.markForCheck();

// //       this.dataService.revertGRNToDraft(grnId).subscribe({
// //         next: ()           => step2and3(),
// //         error: (err: any)  => {
// //           this.updateErrorMessage =
// //             `Could not revert GRN to draft: ${err?.error?.message || 'Server error. Please try again.'}`;
// //           this.updateSubmitting = false;
// //           this.updateStep       = 0;
// //           this.cdr.markForCheck();
// //         }
// //       });
// //     }
// //   }

// //   // ── VIEW MODAL ────────────────────────────────────────────────────────────────

// //   openView(grn: any): void {
// //     this.viewLoading   = true;
// //     this.showViewModal = true;
// //     this.viewGrn       = null;
// //     this.dataService.getGRNById(grn.id).subscribe({
// //       next: (res: any) => { this.viewGrn = this.normalizeGrn(res.data || res); this.viewLoading = false; },
// //       error: ()        => { this.viewGrn = grn; this.viewLoading = false; }
// //     });
// //   }
// //   closeViewModal(): void { this.showViewModal = false; this.viewGrn = null; }

// //   openAction(grn: any, type: 'approve' | 'cancel'): void {
// //     this.selectedGrn = grn; this.actionType = type; this.showActionModal = true; this.errorMessage = '';
// //   }
// //   closeActionModal(): void { this.showActionModal = false; this.selectedGrn = null; }

// //   confirmAction(): void {
// //     if (!this.selectedGrn) return;
// //     this.actionLoading = true;
// //     const obs = this.actionType === 'approve'
// //       ? this.dataService.approveGRN(this.selectedGrn.id, this.buyerName)
// //       : this.dataService.cancelGRN(this.selectedGrn.id);
// //     obs.subscribe({
// //       next: () => {
// //         this.successMessage = `GRN ${this.actionType === 'approve' ? 'approved' : 'cancelled'} successfully.`;
// //         this.actionLoading  = false;
// //         this.closeActionModal();
// //         this.loadGRNs();
// //       },
// //       error: (err: any) => {
// //         this.errorMessage  = err?.error?.message || `Failed to ${this.actionType} GRN.`;
// //         this.actionLoading = false;
// //       }
// //     });
// //   }

// //   submitGRN(grn: any): void {
// //     this.actionLoading = true;
// //     this.dataService.submitGRN(grn.id).subscribe({
// //       next: () => { this.successMessage = 'GRN submitted for QA review.'; this.actionLoading = false; this.loadGRNs(); },
// //       error: (err: any) => { this.errorMessage = err?.error?.message || 'Failed to submit GRN.'; this.actionLoading = false; }
// //     });
// //   }

// //   openQAReview(grn: any): void { this.router.navigate(['/grn-qa', grn.id]); }

// //   // openMatchModal(grn: any): void {
// //   //   this.matchGrn = grn; this.matchInvoices = []; this.selectedInvoiceId = null;
// //   //   this.matchResult = null; this.matchErrorMessage = ''; this.matchTolerance = 2; this.showMatchModal = true;
// //   //   this.loadInvoicesForMatch(grn);
// //   // }

// //     openMatchModal(grn: any): void {
// //     // Navigate to the dedicated 3-Way Match page with GRN pre-selected.
// //     // The ThreeWayMatchComponent reads navigation state to auto-select
// //     // the GRN + its PO.
// //     this.router.navigate(['/three-way-match'], {
// //       state: {
// //         grnId: grn.id,
// //         poId:  grn.purchaseOrderId || grn.poId
// //       }
// //     });
// //   }
// // private enrichWithCrossGrnData(grns: any[]): void {
// //   const poGroups = new Map<number, any[]>();
// //   for (const grn of grns) {
// //     const poId = grn.purchaseOrderId;
// //     if (!poGroups.has(poId)) poGroups.set(poId, []);
// //     poGroups.get(poId)!.push(grn);
// //   }

// //   for (const [poId, poGrns] of poGroups) {
// //     const sorted = [...poGrns].sort(
// //       (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
// //     );

// //     const totalOrdered = sorted[0]?.totalOrderedQuantity ?? 0;

// //     // Total received across ALL GRNs for this PO (any status except CANCELLED)
// //     const totalReceivedAcrossPO = sorted
// //       .filter(g => g.status !== 'CANCELLED')
// //       .reduce((sum, g) => sum + g.totalReceivedQuantity, 0);

// //     // PO is fully received when cumulative >= ordered
// //     const poFullyReceived = totalReceivedAcrossPO >= totalOrdered;

// //     let cumulativeReceived = 0;
// //     for (const grn of sorted) {
// //       if (grn.status === 'CANCELLED') {
// //         grn.cumulativeReceived = cumulativeReceived;
// //         grn.pendingQty         = Math.max(0, totalOrdered - cumulativeReceived);
// //         grn.isPartialBatch     = false;
// //         grn.poFullyReceived    = poFullyReceived;
// //         grn.deliveryRemarks    = '';
// //         continue;
// //       }

// //       cumulativeReceived += grn.totalReceivedQuantity;
// //       const pendingAfterThisGrn = Math.max(0, totalOrdered - cumulativeReceived);

// //       grn.cumulativeReceived = cumulativeReceived;
// //       grn.pendingQty         = pendingAfterThisGrn;
// //       grn.poFullyReceived    = poFullyReceived;

// //       // Show "Partial" badge ONLY if:
// //       // 1. This specific GRN is a partial batch (pending > 0 after it), AND
// //       // 2. The PO is NOT yet fully received across all GRNs
// //       grn.isPartialBatch = pendingAfterThisGrn > 0 && !poFullyReceived;

// //       grn.deliveryRemarks = this.buildDeliveryRemarks(
// //         grn, totalOrdered, cumulativeReceived,
// //         pendingAfterThisGrn, poFullyReceived, sorted
// //       );
// //     }
// //   }
// // }

// // private buildDeliveryRemarks(
// //   grn: any,
// //   totalOrdered: number,
// //   cumulativeReceived: number,
// //   pendingQty: number,
// //   poFullyReceived: boolean,
// //   allGrnsForPo: any[]
// // ): string {
// //   const thisReceived  = grn.totalReceivedQuantity;
// //   const grnIndex      = allGrnsForPo.filter(g => g.status !== 'CANCELLED').indexOf(grn) + 1;
// //   const totalBatches  = allGrnsForPo.filter(g => g.status !== 'CANCELLED').length;
// //   const poRef         = grn.poNumber      ? `PO: ${grn.poNumber}`           : '';
// //   const invRef        = grn.invoiceNumber ? ` | Invoice: ${grn.invoiceNumber}` : '';
// //   const docRef        = poRef + invRef;

// //   if (totalBatches === 1) {
// //     if (pendingQty === 0) {
// //       // Single GRN, fully received
// //       return `${docRef} — Full delivery. All ${totalOrdered} units received.`;
// //     } else {
// //       // Single GRN, partial
// //       return `${docRef} — Partial delivery. Received ${thisReceived} of ${totalOrdered} units. ${pendingQty} unit(s) pending.`;
// //     }
// //   }

// //   // Multiple batches
// //   if (pendingQty > 0) {
// //     // This is an intermediate batch — still pending after this GRN
// //     return `${docRef} — Partial delivery (Batch ${grnIndex} of ${totalBatches}). ` +
// //            `Received ${thisReceived} units. ` +
// //            `Cumulative: ${cumulativeReceived}/${totalOrdered}. ` +
// //            `${pendingQty} unit(s) still pending.`;
// //   } else {
// //     // This GRN completes the delivery
// //     if (grnIndex === 1) {
// //       return `${docRef} — Full delivery. All ${totalOrdered} units received.`;
// //     }
// //     return `${docRef} — Final batch (Batch ${grnIndex} of ${totalBatches}). ` +
// //            `Received ${thisReceived} remaining unit(s). ` +
// //            `All ${totalOrdered} units now fully received across ${totalBatches} GRNs.`;
// //   }
// // }

// //   private loadInvoicesForMatch(grn: any): void {
// //     this.matchInvoicesLoading = true;
// //     this.dataService.getInvoicesByPO(grn.purchaseOrderId || grn.poId).subscribe({
// //       next: (res: any) => {
// //         const all = Array.isArray(res) ? res : (res.data || []);
// //         this.matchInvoices        = all.filter((inv: any) => inv.status === 'SUBMITTED');
// //         this.matchInvoicesLoading = false;
// //       },
// //       error: () => { this.matchInvoicesLoading = false; this.matchErrorMessage = 'Could not load invoices for this PO.'; }
// //     });
// //   }

// //   closeMatchModal(): void { this.showMatchModal = false; this.matchGrn = null; this.matchResult = null; this.matchErrorMessage = ''; }

// //   performMatch(): void {
// //     if (!this.selectedInvoiceId) { this.matchErrorMessage = 'Please select an invoice.'; return; }
// //     this.matchErrorMessage = ''; this.matchSubmitting = true; this.matchResult = null;
// //     const payload = {
// //       purchaseOrderId:      this.matchGrn.purchaseOrderId || this.matchGrn.poId,
// //       grnId:                this.matchGrn.id,
// //       invoiceId:            this.selectedInvoiceId,
// //       tolerancePercentage:  this.matchTolerance,
// //       performedByName:      this.buyerName
// //     };
// //     this.dataService.performThreeWayMatch(payload).subscribe({
// //       next: (res: any) => { this.matchResult = res.data || res; this.matchSubmitting = false; this.loadGRNs(); },
// //       error: (e: any)  => { this.matchErrorMessage = e?.error?.message || 'Failed to perform 3-Way Match.'; this.matchSubmitting = false; }
// //     });
// //   }

// //   navigateCreate(): void { this.router.navigate(['/grn-create']); }
// //   dismissAlert():   void { this.errorMessage = ''; this.successMessage = ''; }

// //   getMatchStatusClass(status: string): string {
// //     const map: Record<string, string> = {
// //       MATCHED: 'match-badge match-ok', OVERRIDDEN_APPROVED: 'match-badge match-ok',
// //       PARTIAL_MATCH: 'match-badge match-warn', EXCESS_DELIVERY: 'match-badge match-warn',
// //       QUANTITY_MISMATCH: 'match-badge match-fail', PRICE_MISMATCH: 'match-badge match-fail',
// //       ITEM_MISMATCH: 'match-badge match-fail', FAILED: 'match-badge match-fail', DISPUTED: 'match-badge match-fail',
// //     };
// //     return map[status] || 'match-badge match-warn';
// //   }

// //   private normalizeGrn(g: any): any {
// //     const lineItems = (g.lineItems || g.grnLineItems || []).map((li: any) => ({
// //       ...li,
// //       itemDescription:   li.itemDescription  || li.itemName || '',
// //       orderedQuantity:   Number(li.orderedQuantity   ?? li.orderedQty   ?? 0),
// //       receivedQuantity:  Number(li.receivedQuantity  ?? li.receivedQty  ?? 0),
// //       defectiveQuantity: Number(li.defectiveQuantity ?? 0),
// //       rejectedQuantity:  Number(li.rejectedQuantity  ?? li.rejectedQty  ?? 0),
// //       acceptedQuantity:  Number(li.acceptedQuantity  ?? li.acceptedQty  ?? li.receivedQuantity ?? li.receivedQty ?? 0),
// //       qaRemarks:     li.qaRemarks     || '',
// //       itemCondition: li.itemCondition || li.condition || 'GOOD',
// //     }));
// //     return {
// //       ...g,
// //       deliveryDate:    g.deliveryDate || g.receivedDate || '',
// //       lineItems,
// //       poNumber:        g.poNumber        || g.purchaseOrder?.poNumber || '',
// //       supplierName:    g.supplierName    || g.supplier?.name          || '',
// //       transporterName: g.transporterName || g.transporter             || '',
// //       receivedByName:  g.receivedByName  || g.receiverName            || '',
// //       inspectedByName: g.inspectedByName || '',
// //       approvedByName:  g.approvedByName  || '',
// //       purchaseOrderId: g.purchaseOrderId || g.poId || g.purchaseOrder?.id,
// //       totalOrderedQuantity:   lineItems.reduce((s: number, li: any) => s + li.orderedQuantity,   0),
// //       totalReceivedQuantity:  lineItems.reduce((s: number, li: any) => s + li.receivedQuantity,  0),
// //       totalAcceptedQuantity:  lineItems.reduce((s: number, li: any) => s + li.acceptedQuantity,  0),
// //       totalRejectedQuantity:  lineItems.reduce((s: number, li: any) => s + li.rejectedQuantity,  0),
// //       totalDefectiveQuantity: lineItems.reduce((s: number, li: any) => s + li.defectiveQuantity, 0),
// //     };
// //   }

// //   getStatusBadgeClass(status: string): string {
// //     const map: Record<string, string> = {
// //       DRAFT: 'status-badge status-draft', SUBMITTED: 'status-badge status-submitted',
// //       QA_REVIEW: 'status-badge status-qa', APPROVED: 'status-badge status-approved',
// //       CLOSED: 'status-badge status-closed', CANCELLED: 'status-badge status-cancelled'
// //     };
// //     return map[status] || 'status-badge status-draft';
// //   }

// //   getStatusLabel(status: string): string {
// //     const map: Record<string, string> = {
// //       DRAFT: 'Draft', SUBMITTED: 'Submitted', QA_REVIEW: 'QA Review',
// //       APPROVED: 'Approved', CLOSED: 'Closed', CANCELLED: 'Cancelled'
// //     };
// //     return map[status] || status;
// //   }

// //   formatDate(d: string): string {
// //     if (!d) return '—';
// //     try { return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }); }
// //     catch { return '—'; }
// //   }

// //   formatCurrency(v: number | null): string {
// //     if (v == null) return '₹0.00';
// //     return '₹' + Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
// //   }
// // }

// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router, RouterModule } from '@angular/router';
// import { DataService } from '../../../shared/service/DataService';
// import { AuthService } from '../../../shared/service/AuthService';
// import { BuyerService } from '../dashboard/buyer-b.service';

// // ── Financial Year option shape ──────────────────────────────
// interface FYOption {
//   value: string;
//   label: string;
//   from: Date;
//   to: Date;
// }

// interface UpdateLineItem {
//   grnLineItemId:    number;
//   poLineItemId:     number;
//   itemDescription:  string;
//   uom:              string;
//   orderedQuantity:  number;
//   previousReceived: number;
//   remainingQty:     number;
//   additionalQty:    number;
//   newTotalReceived: number;
// }

// @Component({
//   selector: 'app-grn-list',
//   standalone: true,
//   imports: [CommonModule, FormsModule, RouterModule],
//   templateUrl: './grn-list.component.html',
//   styleUrls: ['./grn-list.component.css']
// })
// export class GrnListComponent implements OnInit {

//   // ==================== BUYER INFO ====================
//   loggedInBuyer: any = null;
//   buyerId: number | null = null;

//   grns: any[] = [];
//   filteredGrns: any[] = [];
//   pagedGrns: any[] = [];

//   // ── Date-filtered source (stat cards use this) ───────────────
//   dateFilteredGrns: any[] = [];

//   isLoading      = false;
//   actionLoading  = false;
//   errorMessage   = '';
//   successMessage = '';

//   searchText   = '';
//   statusFilter = '';

//   currentPage      = 1;
//   pageSize         = 10;
//   totalPages       = 1;
//   pageSizeOptions  = [5, 10, 25, 50];

//   showViewModal = false;
//   viewGrn: any  = null;
//   viewLoading   = false;

//   showActionModal = false;
//   actionType: 'approve' | 'cancel' = 'approve';
//   selectedGrn: any = null;

//   showMatchModal       = false;
//   matchGrn: any        = null;
//   matchInvoices: any[] = [];
//   selectedInvoiceId: number | null = null;
//   matchTolerance       = 2;
//   matchSubmitting      = false;
//   matchResult: any     = null;
//   matchErrorMessage    = '';
//   matchInvoicesLoading = false;

//   // UPDATE RECEIVED QTY MODAL
//   showUpdateModal       = false;
//   updateGrn: any        = null;
//   updateLineItems: UpdateLineItem[] = [];
//   updateSubmitting      = false;
//   updateErrorMessage    = '';
//   updateSuccessMessage  = '';
//   updateStep      = 0;
//   updateStepLabel = '';

//   userId    = 0;
//   buyerName = '';

//   // ── Date Filter State ────────────────────────────────────────
//   financialYearOptions: FYOption[] = [];
//   selectedFYOption: string = '';
//   customFromDate: string = '';
//   customToDate: string = '';
//   activeDateRangeLabel: string = '';

//   constructor(
//     private dataService: DataService,
//     private authService: AuthService,
//     private router: Router,
//     private cdr: ChangeDetectorRef,
//     private buyerService: BuyerService
//   ) {}

//   // =========================================================================
//   //  BUYER LOADING
//   // =========================================================================

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

//   // =========================================================================
//   //  LIFECYCLE
//   // =========================================================================

//   ngOnInit(): void {
//     const token = localStorage.getItem('token');
//     if (token) {
//       try {
//         const p = JSON.parse(atob(token.split('.')[1]));
//         this.userId = p.userId || p.id || p.sub || 0;
//       } catch {}
//     }
//     if (!this.userId) this.userId = Number(localStorage.getItem('buyerId') || 0);

//     this.buyerName =
//       localStorage.getItem('buyerName') ||
//       localStorage.getItem('fullName')  ||
//       localStorage.getItem('email')     || 'Buyer';

//     this.buildFinancialYearOptions();
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.updateActiveDateRangeLabel();
//     this.loadGRNs();
//     this.loadLoggedInBuyer();
//   }

//   // =========================================================================
//   //  FINANCIAL YEAR HELPERS
//   // =========================================================================

//   private buildFinancialYearOptions(): void {
//     const today = new Date();
//     let currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;
//     this.financialYearOptions = [];
//     for (let i = 0; i < 4; i++) {
//       const startYear = currentFYStartYear - i;
//       const endYear   = startYear + 1;
//       const from = new Date(startYear, 3, 1, 0, 0, 0, 0);
//       const to   = new Date(endYear,   2, 31, 23, 59, 59, 999);
//       this.financialYearOptions.push({
//         value: `FY${startYear}-${String(endYear).slice(-2)}`,
//         label: `FY ${startYear}-${String(endYear).slice(-2)}  (Apr ${startYear} – Mar ${endYear})`,
//         from,
//         to
//       });
//     }
//   }

//   getCurrentFYValue(): string {
//     return this.financialYearOptions.length > 0 ? this.financialYearOptions[0].value : 'ALL';
//   }

//   onFYOptionChange(): void {
//     if (this.selectedFYOption !== 'CUSTOM') {
//       this.customFromDate = '';
//       this.customToDate   = '';
//     }
//     this.updateActiveDateRangeLabel();
//     this.applyFilters();
//   }

//   resetDateFilter(): void {
//     this.selectedFYOption = this.getCurrentFYValue();
//     this.customFromDate   = '';
//     this.customToDate     = '';
//     this.updateActiveDateRangeLabel();
//     this.applyFilters();
//   }

//   private updateActiveDateRangeLabel(): void {
//     if (this.selectedFYOption === 'ALL') { this.activeDateRangeLabel = 'All Time'; return; }
//     if (this.selectedFYOption === 'CUSTOM') {
//       if (this.customFromDate && this.customToDate)
//         this.activeDateRangeLabel = `${this.formatDisplayDate(this.customFromDate)} – ${this.formatDisplayDate(this.customToDate)}`;
//       else if (this.customFromDate)
//         this.activeDateRangeLabel = `From ${this.formatDisplayDate(this.customFromDate)}`;
//       else if (this.customToDate)
//         this.activeDateRangeLabel = `Up to ${this.formatDisplayDate(this.customToDate)}`;
//       else
//         this.activeDateRangeLabel = 'Custom Range';
//       return;
//     }
//     const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
//     this.activeDateRangeLabel = fy ? fy.label : '';
//   }

//   private formatDisplayDate(dateStr: string): string {
//     if (!dateStr) return '';
//     const d = new Date(dateStr + 'T00:00:00');
//     return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
//   }

//   private getActiveDateRange(): { from: Date; to: Date } | null {
//     if (this.selectedFYOption === 'ALL') return null;
//     if (this.selectedFYOption === 'CUSTOM') {
//       const from = this.customFromDate ? new Date(this.customFromDate + 'T00:00:00') : null;
//       const to   = this.customToDate   ? new Date(this.customToDate   + 'T23:59:59') : null;
//       if (!from && !to) return null;
//       return { from: from ?? new Date(0), to: to ?? new Date(8640000000000000) };
//     }
//     const fy = this.financialYearOptions.find(f => f.value === this.selectedFYOption);
//     return fy ? { from: fy.from, to: fy.to } : null;
//   }

//   private applyDateFilter(grns: any[]): any[] {
//     const range = this.getActiveDateRange();
//     if (!range) return grns;
//     return grns.filter((grn: any) => {
//       const dateStr = grn.createdAt || grn.deliveryDate;
//       if (!dateStr) return false;
//       const created = new Date(dateStr);
//       return created >= range.from && created <= range.to;
//     });
//   }

//   countByStatusDateFiltered(s: string): number {
//     return this.dateFilteredGrns.filter(g => g.status === s).length;
//   }

//   // =========================================================================
//   //  GRN LOADING
//   // =========================================================================

//   loadGRNs(): void {
//     this.isLoading    = true;
//     this.errorMessage = '';
//     this.dataService.getGRNsByUser(this.userId).subscribe({
//       next: (res: any) => {
//         const raw = Array.isArray(res) ? res : (res.data || []);
//         const normalized = raw.map((g: any) => this.normalizeGrn(g));
//         this.enrichWithCrossGrnData(normalized);
//         this.grns = normalized;
//         this.applyFilters();
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: (err: any) => {
//         this.errorMessage = err?.error?.message || 'Failed to load GRNs.';
//         this.isLoading    = false;
//       }
//     });
//   }

//   // =========================================================================
//   //  FILTERING & PAGINATION
//   // =========================================================================

//   applyFilters(): void {
//     this.updateActiveDateRangeLabel();
//     const dateFiltered = this.applyDateFilter(this.grns);
//     this.dateFilteredGrns = dateFiltered;

//     let data = [...dateFiltered];
//     if (this.searchText.trim()) {
//       const q = this.searchText.toLowerCase();
//       data = data.filter(g =>
//         (g.grnNumber    || '').toLowerCase().includes(q) ||
//         (g.poNumber     || '').toLowerCase().includes(q) ||
//         (g.supplierName || '').toLowerCase().includes(q)
//       );
//     }
//     if (this.statusFilter) data = data.filter(g => g.status === this.statusFilter);
//     this.filteredGrns = data;
//     this.totalPages   = Math.max(1, Math.ceil(data.length / this.pageSize));
//     this.currentPage  = 1;
//     this.updatePage();
//   }

//   updatePage(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedGrns = this.filteredGrns.slice(start, start + this.pageSize);
//   }

//   onSearch():                     void { this.applyFilters(); }
//   onStatusFilter(s: string):      void { this.statusFilter = s; this.applyFilters(); }
//   prevPage():                     void { if (this.currentPage > 1) { this.currentPage--; this.updatePage(); } }
//   nextPage():                     void { if (this.currentPage < this.totalPages) { this.currentPage++; this.updatePage(); } }
//   goToPage(p: number):            void { if (p >= 1 && p <= this.totalPages) { this.currentPage = p; this.updatePage(); } }
//   onPageSizeChange(size: number): void { this.pageSize = size; this.totalPages = Math.max(1, Math.ceil(this.filteredGrns.length / size)); this.currentPage = 1; this.updatePage(); }
//   countByStatus(s: string):       number { return this.grns.filter(g => g.status === s).length; }

//   get pageNumbers(): number[] {
//     const total = this.totalPages, current = this.currentPage, pages: number[] = [];
//     if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
//     pages.push(1);
//     if (current > 3) pages.push(-1);
//     const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
//     for (let i = start; i <= end; i++) pages.push(i);
//     if (current < total - 2) pages.push(-1);
//     pages.push(total);
//     return pages;
//   }

//   // =========================================================================
//   //  CROSS-GRN ENRICHMENT
//   // =========================================================================

//   private enrichWithCrossGrnData(grns: any[]): void {
//     const poGroups = new Map<number, any[]>();
//     for (const grn of grns) {
//       const poId = grn.purchaseOrderId;
//       if (!poGroups.has(poId)) poGroups.set(poId, []);
//       poGroups.get(poId)!.push(grn);
//     }

//     for (const [poId, poGrns] of poGroups) {
//       const sorted = [...poGrns].sort(
//         (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
//       );

//       const totalOrdered = sorted[0]?.totalOrderedQuantity ?? 0;

//       const totalReceivedAcrossPO = sorted
//         .filter(g => g.status !== 'CANCELLED')
//         .reduce((sum, g) => sum + g.totalReceivedQuantity, 0);

//       const poFullyReceived = totalReceivedAcrossPO >= totalOrdered;

//       let cumulativeReceived = 0;
//       for (const grn of sorted) {
//         if (grn.status === 'CANCELLED') {
//           grn.cumulativeReceived = cumulativeReceived;
//           grn.pendingQty         = Math.max(0, totalOrdered - cumulativeReceived);
//           grn.isPartialBatch     = false;
//           grn.poFullyReceived    = poFullyReceived;
//           grn.deliveryRemarks    = '';
//           continue;
//         }

//         cumulativeReceived += grn.totalReceivedQuantity;
//         const pendingAfterThisGrn = Math.max(0, totalOrdered - cumulativeReceived);

//         grn.cumulativeReceived = cumulativeReceived;
//         grn.pendingQty         = pendingAfterThisGrn;
//         grn.poFullyReceived    = poFullyReceived;
//         grn.isPartialBatch     = pendingAfterThisGrn > 0 && !poFullyReceived;

//         grn.deliveryRemarks = this.buildDeliveryRemarks(
//           grn, totalOrdered, cumulativeReceived,
//           pendingAfterThisGrn, poFullyReceived, sorted
//         );
//       }
//     }
//   }

//   private buildDeliveryRemarks(
//     grn: any,
//     totalOrdered: number,
//     cumulativeReceived: number,
//     pendingQty: number,
//     poFullyReceived: boolean,
//     allGrnsForPo: any[]
//   ): string {
//     const thisReceived  = grn.totalReceivedQuantity;
//     const grnIndex      = allGrnsForPo.filter(g => g.status !== 'CANCELLED').indexOf(grn) + 1;
//     const totalBatches  = allGrnsForPo.filter(g => g.status !== 'CANCELLED').length;
//     const poRef         = grn.poNumber      ? `PO: ${grn.poNumber}`              : '';
//     const invRef        = grn.invoiceNumber ? ` | Invoice: ${grn.invoiceNumber}` : '';
//     const docRef        = poRef + invRef;

//     if (totalBatches === 1) {
//       return pendingQty === 0
//         ? `${docRef} — Full delivery. All ${totalOrdered} units received.`
//         : `${docRef} — Partial delivery. Received ${thisReceived} of ${totalOrdered} units. ${pendingQty} unit(s) pending.`;
//     }

//     if (pendingQty > 0) {
//       return `${docRef} — Partial delivery (Batch ${grnIndex} of ${totalBatches}). ` +
//              `Received ${thisReceived} units. ` +
//              `Cumulative: ${cumulativeReceived}/${totalOrdered}. ` +
//              `${pendingQty} unit(s) still pending.`;
//     } else {
//       if (grnIndex === 1) {
//         return `${docRef} — Full delivery. All ${totalOrdered} units received.`;
//       }
//       return `${docRef} — Final batch (Batch ${grnIndex} of ${totalBatches}). ` +
//              `Received ${thisReceived} remaining unit(s). ` +
//              `All ${totalOrdered} units now fully received across ${totalBatches} GRNs.`;
//     }
//   }

//   // =========================================================================
//   //  PARTIAL RECEIPT HELPERS
//   // =========================================================================

//   hasPartialReceipt(grn: any): boolean {
//     const allowedStatuses = ['SUBMITTED', 'QA_REVIEW', 'APPROVED'];
//     if (!allowedStatuses.includes(grn.status)) return false;
//     return (grn.lineItems || []).some(
//       (li: any) => Number(li.receivedQuantity) < Number(li.orderedQuantity)
//     );
//   }

//   isPartialDelivery(grn: any): boolean {
//     return grn.isPartialBatch === true;
//   }

//   // =========================================================================
//   //  UPDATE RECEIVED QTY MODAL
//   // =========================================================================

//   openUpdateModal(grn: any): void {
//     this.updateGrn            = null;
//     this.updateLineItems      = [];
//     this.updateErrorMessage   = '';
//     this.updateSuccessMessage = '';
//     this.updateStep           = 0;
//     this.updateStepLabel      = '';
//     this.showUpdateModal      = true;

//     this.dataService.getGRNById(grn.id).subscribe({
//       next: (res: any) => {
//         const fresh = this.normalizeGrn(res.data || res);
//         this.updateGrn = fresh;

//         this.updateLineItems = (fresh.lineItems || [])
//           .filter((li: any) => Number(li.orderedQuantity) > Number(li.receivedQuantity))
//           .map((li: any) => {
//             const prev      = Number(li.receivedQuantity);
//             const ordered   = Number(li.orderedQuantity);
//             const remaining = Math.max(0, ordered - prev);
//             return {
//               grnLineItemId:    li.id,
//               poLineItemId:     li.poLineItemId || li.poLineItem?.id,
//               itemDescription:  li.itemDescription || li.itemName || '—',
//               uom:              li.uom || 'NOS',
//               orderedQuantity:  ordered,
//               previousReceived: prev,
//               remainingQty:     remaining,
//               additionalQty:    remaining,
//               newTotalReceived: ordered
//             };
//           });

//         if (this.updateLineItems.length === 0) {
//           this.updateErrorMessage = 'All line items have already been fully received. Nothing to update.';
//         }
//       },
//       error: (err: any) => {
//         this.updateErrorMessage = err?.error?.message || 'Failed to load GRN details.';
//       }
//     });
//   }

//   closeUpdateModal(): void {
//     this.showUpdateModal      = false;
//     this.updateGrn            = null;
//     this.updateLineItems      = [];
//     this.updateErrorMessage   = '';
//     this.updateSuccessMessage = '';
//     this.updateStep           = 0;
//     this.updateStepLabel      = '';
//   }

//   onAdditionalQtyChange(li: UpdateLineItem): void {
//     if (li.additionalQty < 0)              li.additionalQty = 0;
//     if (li.additionalQty > li.remainingQty) li.additionalQty = li.remainingQty;
//     li.newTotalReceived = li.previousReceived + li.additionalQty;
//   }

//   get updateTotalAdditional(): number {
//     return this.updateLineItems.reduce((s, li) => s + (Number(li.additionalQty) || 0), 0);
//   }

//   updateTotal(field: keyof UpdateLineItem): number {
//     return this.updateLineItems.reduce((s, li) => s + (Number((li as any)[field]) || 0), 0);
//   }

//   submitUpdateReceivedQty(): void {
//     this.updateErrorMessage = '';

//     if (!this.updateGrn) { this.updateErrorMessage = 'No GRN loaded.'; return; }
//     if (this.updateTotalAdditional === 0) {
//       this.updateErrorMessage = 'Please enter at least one additional received quantity greater than zero.';
//       return;
//     }
//     for (const li of this.updateLineItems) {
//       if (li.additionalQty < 0) {
//         this.updateErrorMessage = `Additional qty cannot be negative for: "${li.itemDescription}"`;
//         return;
//       }
//       if (li.additionalQty > li.remainingQty) {
//         this.updateErrorMessage =
//           `Additional qty (${li.additionalQty}) exceeds remaining qty (${li.remainingQty}) for: "${li.itemDescription}"`;
//         return;
//       }
//     }

//     this.updateSubmitting = true;
//     const grnId     = this.updateGrn.id;
//     const grnNumber = this.updateGrn.grnNumber;
//     const addedQty  = this.updateTotalAdditional;
//     const isDraft   = this.updateGrn.status === 'DRAFT';

//     const allLines = (this.updateGrn.lineItems || []).map((li: any) => {
//       const upd = this.updateLineItems.find(u => u.grnLineItemId === li.id);
//       return {
//         id:               li.id,
//         poLineItemId:     li.poLineItemId || li.poLineItem?.id,
//         itemOrder:        li.itemOrder || 0,
//         itemDescription:  li.itemDescription || '',
//         itemCode:         li.itemCode || '',
//         brandMakeModel:   li.brandMakeModel || '',
//         specifications:   li.specifications || '',
//         uom:              li.uom || 'NOS',
//         orderedQuantity:  li.orderedQuantity,
//         receivedQuantity: upd ? upd.newTotalReceived : li.receivedQuantity,
//         poUnitRate:       li.poUnitRate || 0,
//         remarks:          li.remarks || null
//       };
//     });

//     const updatePayload = {
//       purchaseOrderId:       this.updateGrn.purchaseOrderId || this.updateGrn.poId,
//       receivedDate:          this.updateGrn.deliveryDate || this.updateGrn.receivedDate,
//       deliveryChallanNumber: this.updateGrn.deliveryChallanNumber || null,
//       lrNumber:              this.updateGrn.lrNumber              || null,
//       transporterName:       this.updateGrn.transporterName       || null,
//       vehicleNumber:         this.updateGrn.vehicleNumber         || null,
//       deliveryLocation:      this.updateGrn.deliveryLocation      || null,
//       receivedByUserId:      this.updateGrn.receivedByUserId      || this.userId,
//       receivedByName:        this.updateGrn.receivedByName        || null,
//       remarks:               this.updateGrn.remarks               || null,
//       internalNotes:         this.updateGrn.internalNotes         || null,
//       lineItems: allLines
//     };

//     const step2and3 = () => {
//       this.updateStep      = 2;
//       this.updateStepLabel = 'Step 2 of 3 — Updating received quantities…';
//       this.cdr.markForCheck();

//       this.dataService.updateGRN(grnId, updatePayload).subscribe({
//         next: () => {
//           this.updateStep      = 3;
//           this.updateStepLabel = 'Step 3 of 3 — Re-submitting GRN for QA review…';
//           this.cdr.markForCheck();

//           this.dataService.submitGRN(grnId).subscribe({
//             next: () => {
//               this.updateStep           = 4;
//               this.updateStepLabel      = '';
//               this.updateSuccessMessage =
//                 `GRN ${grnNumber} updated successfully! Added ${addedQty} unit(s). ` +
//                 `GRN has been re-submitted for QA review.`;
//               this.updateSubmitting     = false;
//               this.loadGRNs();
//               this.cdr.markForCheck();
//               setTimeout(() => this.closeUpdateModal(), 2800);
//             },
//             error: (err: any) => {
//               this.updateErrorMessage =
//                 `Quantities saved but re-submit failed: ` +
//                 (err?.error?.message || 'Please manually submit the GRN from the list.');
//               this.updateSubmitting = false;
//               this.updateStep       = 0;
//               this.loadGRNs();
//               this.cdr.markForCheck();
//             }
//           });
//         },
//         error: (err: any) => {
//           this.updateErrorMessage =
//             `Failed to update quantities: ${err?.error?.message || 'Please try again.'}`;
//           this.updateSubmitting = false;
//           this.updateStep       = 0;
//           this.cdr.markForCheck();
//         }
//       });
//     };

//     if (isDraft) {
//       step2and3();
//     } else {
//       this.updateStep      = 1;
//       this.updateStepLabel = 'Step 1 of 3 — Reverting GRN to draft…';
//       this.cdr.markForCheck();

//       this.dataService.revertGRNToDraft(grnId).subscribe({
//         next: ()           => step2and3(),
//         error: (err: any)  => {
//           this.updateErrorMessage =
//             `Could not revert GRN to draft: ${err?.error?.message || 'Server error. Please try again.'}`;
//           this.updateSubmitting = false;
//           this.updateStep       = 0;
//           this.cdr.markForCheck();
//         }
//       });
//     }
//   }

//   // =========================================================================
//   //  VIEW MODAL
//   // =========================================================================

//   openView(grn: any): void {
//     this.viewLoading   = true;
//     this.showViewModal = true;
//     this.viewGrn       = null;
//     this.dataService.getGRNById(grn.id).subscribe({
//       next: (res: any) => { this.viewGrn = this.normalizeGrn(res.data || res); this.viewLoading = false; },
//       error: ()        => { this.viewGrn = grn; this.viewLoading = false; }
//     });
//   }
//   closeViewModal(): void { this.showViewModal = false; this.viewGrn = null; }

//   openAction(grn: any, type: 'approve' | 'cancel'): void {
//     this.selectedGrn = grn; this.actionType = type; this.showActionModal = true; this.errorMessage = '';
//   }
//   closeActionModal(): void { this.showActionModal = false; this.selectedGrn = null; }

//   confirmAction(): void {
//     if (!this.selectedGrn) return;
//     this.actionLoading = true;
//     const obs = this.actionType === 'approve'
//       ? this.dataService.approveGRN(this.selectedGrn.id, this.buyerName)
//       : this.dataService.cancelGRN(this.selectedGrn.id);
//     obs.subscribe({
//       next: () => {
//         this.successMessage = `GRN ${this.actionType === 'approve' ? 'approved' : 'cancelled'} successfully.`;
//         this.actionLoading  = false;
//         this.closeActionModal();
//         this.loadGRNs();
//       },
//       error: (err: any) => {
//         this.errorMessage  = err?.error?.message || `Failed to ${this.actionType} GRN.`;
//         this.actionLoading = false;
//       }
//     });
//   }

//   submitGRN(grn: any): void {
//     this.actionLoading = true;
//     this.dataService.submitGRN(grn.id).subscribe({
//       next: () => { this.successMessage = 'GRN submitted for QA review.'; this.actionLoading = false; this.loadGRNs(); },
//       error: (err: any) => { this.errorMessage = err?.error?.message || 'Failed to submit GRN.'; this.actionLoading = false; }
//     });
//   }

//   openQAReview(grn: any): void { this.router.navigate(['/grn-qa', grn.id]); }

//   openMatchModal(grn: any): void {
//     this.router.navigate(['/three-way-match'], {
//       state: {
//         grnId: grn.id,
//         poId:  grn.purchaseOrderId || grn.poId
//       }
//     });
//   }

//   private loadInvoicesForMatch(grn: any): void {
//     this.matchInvoicesLoading = true;
//     this.dataService.getInvoicesByPO(grn.purchaseOrderId || grn.poId).subscribe({
//       next: (res: any) => {
//         const all = Array.isArray(res) ? res : (res.data || []);
//         this.matchInvoices        = all.filter((inv: any) => inv.status === 'SUBMITTED');
//         this.matchInvoicesLoading = false;
//       },
//       error: () => { this.matchInvoicesLoading = false; this.matchErrorMessage = 'Could not load invoices for this PO.'; }
//     });
//   }

//   closeMatchModal(): void { this.showMatchModal = false; this.matchGrn = null; this.matchResult = null; this.matchErrorMessage = ''; }

//   performMatch(): void {
//     if (!this.selectedInvoiceId) { this.matchErrorMessage = 'Please select an invoice.'; return; }
//     this.matchErrorMessage = ''; this.matchSubmitting = true; this.matchResult = null;
//     const payload = {
//       purchaseOrderId:      this.matchGrn.purchaseOrderId || this.matchGrn.poId,
//       grnId:                this.matchGrn.id,
//       invoiceId:            this.selectedInvoiceId,
//       tolerancePercentage:  this.matchTolerance,
//       performedByName:      this.buyerName
//     };
//     this.dataService.performThreeWayMatch(payload).subscribe({
//       next: (res: any) => { this.matchResult = res.data || res; this.matchSubmitting = false; this.loadGRNs(); },
//       error: (e: any)  => { this.matchErrorMessage = e?.error?.message || 'Failed to perform 3-Way Match.'; this.matchSubmitting = false; }
//     });
//   }

//   navigateCreate(): void { this.router.navigate(['/grn-create']); }
//   dismissAlert():   void { this.errorMessage = ''; this.successMessage = ''; }

//   // =========================================================================
//   //  UTILITIES
//   // =========================================================================

//   getMatchStatusClass(status: string): string {
//     const map: Record<string, string> = {
//       MATCHED: 'match-badge match-ok', OVERRIDDEN_APPROVED: 'match-badge match-ok',
//       PARTIAL_MATCH: 'match-badge match-warn', EXCESS_DELIVERY: 'match-badge match-warn',
//       QUANTITY_MISMATCH: 'match-badge match-fail', PRICE_MISMATCH: 'match-badge match-fail',
//       ITEM_MISMATCH: 'match-badge match-fail', FAILED: 'match-badge match-fail', DISPUTED: 'match-badge match-fail',
//     };
//     return map[status] || 'match-badge match-warn';
//   }

//   private normalizeGrn(g: any): any {
//     const lineItems = (g.lineItems || g.grnLineItems || []).map((li: any) => ({
//       ...li,
//       itemDescription:   li.itemDescription  || li.itemName || '',
//       orderedQuantity:   Number(li.orderedQuantity   ?? li.orderedQty   ?? 0),
//       receivedQuantity:  Number(li.receivedQuantity  ?? li.receivedQty  ?? 0),
//       defectiveQuantity: Number(li.defectiveQuantity ?? 0),
//       rejectedQuantity:  Number(li.rejectedQuantity  ?? li.rejectedQty  ?? 0),
//       acceptedQuantity:  Number(li.acceptedQuantity  ?? li.acceptedQty  ?? li.receivedQuantity ?? li.receivedQty ?? 0),
//       qaRemarks:     li.qaRemarks     || '',
//       itemCondition: li.itemCondition || li.condition || 'GOOD',
//     }));
//     return {
//       ...g,
//       deliveryDate:    g.deliveryDate || g.receivedDate || '',
//       lineItems,
//       poNumber:        g.poNumber        || g.purchaseOrder?.poNumber || '',
//       supplierName:    g.supplierName    || g.supplier?.name          || '',
//       transporterName: g.transporterName || g.transporter             || '',
//       receivedByName:  g.receivedByName  || g.receiverName            || '',
//       inspectedByName: g.inspectedByName || '',
//       approvedByName:  g.approvedByName  || '',
//       purchaseOrderId: g.purchaseOrderId || g.poId || g.purchaseOrder?.id,
//       totalOrderedQuantity:   lineItems.reduce((s: number, li: any) => s + li.orderedQuantity,   0),
//       totalReceivedQuantity:  lineItems.reduce((s: number, li: any) => s + li.receivedQuantity,  0),
//       totalAcceptedQuantity:  lineItems.reduce((s: number, li: any) => s + li.acceptedQuantity,  0),
//       totalRejectedQuantity:  lineItems.reduce((s: number, li: any) => s + li.rejectedQuantity,  0),
//       totalDefectiveQuantity: lineItems.reduce((s: number, li: any) => s + li.defectiveQuantity, 0),
//     };
//   }

//   getStatusBadgeClass(status: string): string {
//     const map: Record<string, string> = {
//       DRAFT: 'status-badge status-draft', SUBMITTED: 'status-badge status-submitted',
//       QA_REVIEW: 'status-badge status-qa', APPROVED: 'status-badge status-approved',
//       CLOSED: 'status-badge status-closed', CANCELLED: 'status-badge status-cancelled'
//     };
//     return map[status] || 'status-badge status-draft';
//   }

//   getStatusLabel(status: string): string {
//     const map: Record<string, string> = {
//       DRAFT: 'Draft', SUBMITTED: 'Submitted', QA_REVIEW: 'QA Review',
//       APPROVED: 'Approved', CLOSED: 'Closed', CANCELLED: 'Cancelled'
//     };
//     return map[status] || status;
//   }

//   formatDate(d: string): string {
//     if (!d) return '—';
//     try { return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }); }
//     catch { return '—'; }
//   }

//   formatCurrency(v: number | null): string {
//     if (v == null) return '₹0.00';
//     return '₹' + Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//   }
// }


import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { AuthService } from '../../../shared/service/AuthService';
import { BuyerService } from '../dashboard/buyer-b.service';

// ── Financial Year option shape ──────────────────────────────
interface FYOption {
  value: string;
  label: string;
  from: Date;
  to: Date;
}

interface UpdateLineItem {
  grnLineItemId:    number;
  poLineItemId:     number;
  itemDescription:  string;
  uom:              string;
  orderedQuantity:  number;
  previousReceived: number;
  remainingQty:     number;
  additionalQty:    number;
  newTotalReceived: number;
}

@Component({
  selector: 'app-grn-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './grn-list.component.html',
  styleUrls: ['./grn-list.component.css']
})
export class GrnListComponent implements OnInit {

  // ==================== BUYER INFO ====================
  loggedInBuyer: any = null;
  buyerId: number | null = null;

  grns: any[] = [];
  filteredGrns: any[] = [];
  pagedGrns: any[] = [];

  // ── Date-filtered source (stat cards use this) ───────────────
  dateFilteredGrns: any[] = [];

  isLoading      = false;
  actionLoading  = false;
  errorMessage   = '';
  successMessage = '';

  searchText   = '';
  statusFilter = '';

  currentPage      = 1;
  pageSize         = 10;
  totalPages       = 1;
  pageSizeOptions  = [5, 10, 25, 50];

  showViewModal = false;
  viewGrn: any  = null;
  viewLoading   = false;

  showActionModal = false;
  actionType: 'approve' | 'cancel' = 'approve';
  selectedGrn: any = null;

  showMatchModal       = false;
  matchGrn: any        = null;
  matchInvoices: any[] = [];
  selectedInvoiceId: number | null = null;
  matchTolerance       = 2;
  matchSubmitting      = false;
  matchResult: any     = null;
  matchErrorMessage    = '';
  matchInvoicesLoading = false;

  // UPDATE RECEIVED QTY MODAL
  showUpdateModal       = false;
  updateGrn: any        = null;
  updateLineItems: UpdateLineItem[] = [];
  updateSubmitting      = false;
  updateErrorMessage    = '';
  updateSuccessMessage  = '';
  updateStep      = 0;
  updateStepLabel = '';

  userId    = 0;
  buyerName = '';

  // ── Date Filter State ────────────────────────────────────────
  financialYearOptions: FYOption[] = [];
  selectedFYOption: string = '';
  customFromDate: string = '';
  customToDate: string = '';
  activeDateRangeLabel: string = '';

  // ── GRN Excel Download State ─────────────────────────────────
  /** Tracks which GRN is currently being downloaded (null = none) */
  downloadingGrnId: number | null = null;

  constructor(
    private dataService: DataService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private buyerService: BuyerService
  ) {}

  // =========================================================================
  //  BUYER LOADING
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
  //  LIFECYCLE
  // =========================================================================

  ngOnInit(): void {
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
      localStorage.getItem('email')     || 'Buyer';

    this.buildFinancialYearOptions();
    this.selectedFYOption = this.getCurrentFYValue();
    this.updateActiveDateRangeLabel();
    this.loadGRNs();
    this.loadLoggedInBuyer();
  }

  // =========================================================================
  //  FINANCIAL YEAR HELPERS
  // =========================================================================

  private buildFinancialYearOptions(): void {
    const today = new Date();
    let currentFYStartYear = today.getMonth() >= 3 ? today.getFullYear() : today.getFullYear() - 1;
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

  private applyDateFilter(grns: any[]): any[] {
    const range = this.getActiveDateRange();
    if (!range) return grns;
    return grns.filter((grn: any) => {
      const dateStr = grn.createdAt || grn.deliveryDate;
      if (!dateStr) return false;
      const created = new Date(dateStr);
      return created >= range.from && created <= range.to;
    });
  }

  countByStatusDateFiltered(s: string): number {
    return this.dateFilteredGrns.filter(g => g.status === s).length;
  }

  // =========================================================================
  //  GRN LOADING
  // =========================================================================

  loadGRNs(): void {
    this.isLoading    = true;
    this.errorMessage = '';
    this.dataService.getGRNsByUser(this.userId).subscribe({
      next: (res: any) => {
        const raw = Array.isArray(res) ? res : (res.data || []);
        const normalized = raw.map((g: any) => this.normalizeGrn(g));
        this.enrichWithCrossGrnData(normalized);
        this.grns = normalized;
        this.applyFilters();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to load GRNs.';
        this.isLoading    = false;
      }
    });
  }

  // =========================================================================
  //  FILTERING & PAGINATION
  // =========================================================================

  applyFilters(): void {
    this.updateActiveDateRangeLabel();
    const dateFiltered = this.applyDateFilter(this.grns);
    this.dateFilteredGrns = dateFiltered;

    let data = [...dateFiltered];
    if (this.searchText.trim()) {
      const q = this.searchText.toLowerCase();
      data = data.filter(g =>
        (g.grnNumber    || '').toLowerCase().includes(q) ||
        (g.poNumber     || '').toLowerCase().includes(q) ||
        (g.supplierName || '').toLowerCase().includes(q)
      );
    }
    if (this.statusFilter) data = data.filter(g => g.status === this.statusFilter);
    this.filteredGrns = data;
    this.totalPages   = Math.max(1, Math.ceil(data.length / this.pageSize));
    this.currentPage  = 1;
    this.updatePage();
  }

  updatePage(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedGrns = this.filteredGrns.slice(start, start + this.pageSize);
  }

  onSearch():                     void { this.applyFilters(); }
  onStatusFilter(s: string):      void { this.statusFilter = s; this.applyFilters(); }
  prevPage():                     void { if (this.currentPage > 1) { this.currentPage--; this.updatePage(); } }
  nextPage():                     void { if (this.currentPage < this.totalPages) { this.currentPage++; this.updatePage(); } }
  goToPage(p: number):            void { if (p >= 1 && p <= this.totalPages) { this.currentPage = p; this.updatePage(); } }
  onPageSizeChange(size: number): void { this.pageSize = size; this.totalPages = Math.max(1, Math.ceil(this.filteredGrns.length / size)); this.currentPage = 1; this.updatePage(); }
  countByStatus(s: string):       number { return this.grns.filter(g => g.status === s).length; }

  get pageNumbers(): number[] {
    const total = this.totalPages, current = this.currentPage, pages: number[] = [];
    if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); return pages; }
    pages.push(1);
    if (current > 3) pages.push(-1);
    const start = Math.max(2, current - 1), end = Math.min(total - 1, current + 1);
    for (let i = start; i <= end; i++) pages.push(i);
    if (current < total - 2) pages.push(-1);
    pages.push(total);
    return pages;
  }

  // =========================================================================
  //  ✅ GRN EXCEL DOWNLOAD
  // =========================================================================

  /**
   * Downloads the GRN as a professional Excel report.
   * Uses the existing GET /api/reports/grn/{grnId}/excel endpoint.
   * Works from both the table row button and the View modal button.
   */
  downloadGRNExcel(grn: any): void {
    if (this.downloadingGrnId === grn.id) return; // prevent double-click

    this.downloadingGrnId = grn.id;
    this.cdr.markForCheck();

    this.dataService.getGRNReportExcel(grn.id).subscribe({
      next: (blob: Blob) => {
        const grnNumber = grn.grnNumber || `GRN_${grn.id}`;
        const today     = new Date().toISOString().slice(0, 10).replace(/-/g, '');
        const filename  = `${grnNumber}_Report_${today}.xlsx`;
        this.dataService.saveBlob(blob, filename);
        this.downloadingGrnId = null;
        this.successMessage   = `GRN report downloaded: ${filename}`;
        this.cdr.markForCheck();
        // auto-dismiss success after 3 seconds
        setTimeout(() => { this.successMessage = ''; this.cdr.markForCheck(); }, 3000);
      },
      error: (err: any) => {
        this.downloadingGrnId = null;
        this.errorMessage     = err?.error?.message || `Failed to download GRN report for ${grn.grnNumber || grn.id}.`;
        this.cdr.markForCheck();
      }
    });
  }

  // =========================================================================
  //  CROSS-GRN ENRICHMENT
  // =========================================================================

  private enrichWithCrossGrnData(grns: any[]): void {
    const poGroups = new Map<number, any[]>();
    for (const grn of grns) {
      const poId = grn.purchaseOrderId;
      if (!poGroups.has(poId)) poGroups.set(poId, []);
      poGroups.get(poId)!.push(grn);
    }

    for (const [poId, poGrns] of poGroups) {
      const sorted = [...poGrns].sort(
        (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
      );

      const totalOrdered = sorted[0]?.totalOrderedQuantity ?? 0;

      const totalReceivedAcrossPO = sorted
        .filter(g => g.status !== 'CANCELLED')
        .reduce((sum, g) => sum + g.totalReceivedQuantity, 0);

      const poFullyReceived = totalReceivedAcrossPO >= totalOrdered;

      let cumulativeReceived = 0;
      for (const grn of sorted) {
        if (grn.status === 'CANCELLED') {
          grn.cumulativeReceived = cumulativeReceived;
          grn.pendingQty         = Math.max(0, totalOrdered - cumulativeReceived);
          grn.isPartialBatch     = false;
          grn.poFullyReceived    = poFullyReceived;
          grn.deliveryRemarks    = '';
          continue;
        }

        cumulativeReceived += grn.totalReceivedQuantity;
        const pendingAfterThisGrn = Math.max(0, totalOrdered - cumulativeReceived);

        grn.cumulativeReceived = cumulativeReceived;
        grn.pendingQty         = pendingAfterThisGrn;
        grn.poFullyReceived    = poFullyReceived;
        grn.isPartialBatch     = pendingAfterThisGrn > 0 && !poFullyReceived;

        grn.deliveryRemarks = this.buildDeliveryRemarks(
          grn, totalOrdered, cumulativeReceived,
          pendingAfterThisGrn, poFullyReceived, sorted
        );
      }
    }
  }

  private buildDeliveryRemarks(
    grn: any,
    totalOrdered: number,
    cumulativeReceived: number,
    pendingQty: number,
    poFullyReceived: boolean,
    allGrnsForPo: any[]
  ): string {
    const thisReceived  = grn.totalReceivedQuantity;
    const grnIndex      = allGrnsForPo.filter(g => g.status !== 'CANCELLED').indexOf(grn) + 1;
    const totalBatches  = allGrnsForPo.filter(g => g.status !== 'CANCELLED').length;
    const poRef         = grn.poNumber      ? `PO: ${grn.poNumber}`              : '';
    const invRef        = grn.invoiceNumber ? ` | Invoice: ${grn.invoiceNumber}` : '';
    const docRef        = poRef + invRef;

    if (totalBatches === 1) {
      return pendingQty === 0
        ? `${docRef} — Full delivery. All ${totalOrdered} units received.`
        : `${docRef} — Partial delivery. Received ${thisReceived} of ${totalOrdered} units. ${pendingQty} unit(s) pending.`;
    }

    if (pendingQty > 0) {
      return `${docRef} — Partial delivery (Batch ${grnIndex} of ${totalBatches}). ` +
             `Received ${thisReceived} units. ` +
             `Cumulative: ${cumulativeReceived}/${totalOrdered}. ` +
             `${pendingQty} unit(s) still pending.`;
    } else {
      if (grnIndex === 1) {
        return `${docRef} — Full delivery. All ${totalOrdered} units received.`;
      }
      return `${docRef} — Final batch (Batch ${grnIndex} of ${totalBatches}). ` +
             `Received ${thisReceived} remaining unit(s). ` +
             `All ${totalOrdered} units now fully received across ${totalBatches} GRNs.`;
    }
  }

  // =========================================================================
  //  PARTIAL RECEIPT HELPERS
  // =========================================================================

  hasPartialReceipt(grn: any): boolean {
    const allowedStatuses = ['SUBMITTED', 'QA_REVIEW', 'APPROVED'];
    if (!allowedStatuses.includes(grn.status)) return false;
    return (grn.lineItems || []).some(
      (li: any) => Number(li.receivedQuantity) < Number(li.orderedQuantity)
    );
  }

  isPartialDelivery(grn: any): boolean {
    return grn.isPartialBatch === true;
  }

  // =========================================================================
  //  UPDATE RECEIVED QTY MODAL
  // =========================================================================

  openUpdateModal(grn: any): void {
    this.updateGrn            = null;
    this.updateLineItems      = [];
    this.updateErrorMessage   = '';
    this.updateSuccessMessage = '';
    this.updateStep           = 0;
    this.updateStepLabel      = '';
    this.showUpdateModal      = true;

    this.dataService.getGRNById(grn.id).subscribe({
      next: (res: any) => {
        const fresh = this.normalizeGrn(res.data || res);
        this.updateGrn = fresh;

        this.updateLineItems = (fresh.lineItems || [])
          .filter((li: any) => Number(li.orderedQuantity) > Number(li.receivedQuantity))
          .map((li: any) => {
            const prev      = Number(li.receivedQuantity);
            const ordered   = Number(li.orderedQuantity);
            const remaining = Math.max(0, ordered - prev);
            return {
              grnLineItemId:    li.id,
              poLineItemId:     li.poLineItemId || li.poLineItem?.id,
              itemDescription:  li.itemDescription || li.itemName || '—',
              uom:              li.uom || 'NOS',
              orderedQuantity:  ordered,
              previousReceived: prev,
              remainingQty:     remaining,
              additionalQty:    remaining,
              newTotalReceived: ordered
            };
          });

        if (this.updateLineItems.length === 0) {
          this.updateErrorMessage = 'All line items have already been fully received. Nothing to update.';
        }
      },
      error: (err: any) => {
        this.updateErrorMessage = err?.error?.message || 'Failed to load GRN details.';
      }
    });
  }

  closeUpdateModal(): void {
    this.showUpdateModal      = false;
    this.updateGrn            = null;
    this.updateLineItems      = [];
    this.updateErrorMessage   = '';
    this.updateSuccessMessage = '';
    this.updateStep           = 0;
    this.updateStepLabel      = '';
  }

  onAdditionalQtyChange(li: UpdateLineItem): void {
    if (li.additionalQty < 0)               li.additionalQty = 0;
    if (li.additionalQty > li.remainingQty)  li.additionalQty = li.remainingQty;
    li.newTotalReceived = li.previousReceived + li.additionalQty;
  }

  get updateTotalAdditional(): number {
    return this.updateLineItems.reduce((s, li) => s + (Number(li.additionalQty) || 0), 0);
  }

  updateTotal(field: keyof UpdateLineItem): number {
    return this.updateLineItems.reduce((s, li) => s + (Number((li as any)[field]) || 0), 0);
  }

  submitUpdateReceivedQty(): void {
    this.updateErrorMessage = '';

    if (!this.updateGrn) { this.updateErrorMessage = 'No GRN loaded.'; return; }
    if (this.updateTotalAdditional === 0) {
      this.updateErrorMessage = 'Please enter at least one additional received quantity greater than zero.';
      return;
    }
    for (const li of this.updateLineItems) {
      if (li.additionalQty < 0) {
        this.updateErrorMessage = `Additional qty cannot be negative for: "${li.itemDescription}"`;
        return;
      }
      if (li.additionalQty > li.remainingQty) {
        this.updateErrorMessage =
          `Additional qty (${li.additionalQty}) exceeds remaining qty (${li.remainingQty}) for: "${li.itemDescription}"`;
        return;
      }
    }

    this.updateSubmitting = true;
    const grnId     = this.updateGrn.id;
    const grnNumber = this.updateGrn.grnNumber;
    const addedQty  = this.updateTotalAdditional;
    const isDraft   = this.updateGrn.status === 'DRAFT';

    const allLines = (this.updateGrn.lineItems || []).map((li: any) => {
      const upd = this.updateLineItems.find(u => u.grnLineItemId === li.id);
      return {
        id:               li.id,
        poLineItemId:     li.poLineItemId || li.poLineItem?.id,
        itemOrder:        li.itemOrder || 0,
        itemDescription:  li.itemDescription || '',
        itemCode:         li.itemCode || '',
        brandMakeModel:   li.brandMakeModel || '',
        specifications:   li.specifications || '',
        uom:              li.uom || 'NOS',
        orderedQuantity:  li.orderedQuantity,
        receivedQuantity: upd ? upd.newTotalReceived : li.receivedQuantity,
        poUnitRate:       li.poUnitRate || 0,
        remarks:          li.remarks || null
      };
    });

    const updatePayload = {
      purchaseOrderId:       this.updateGrn.purchaseOrderId || this.updateGrn.poId,
      receivedDate:          this.updateGrn.deliveryDate || this.updateGrn.receivedDate,
      deliveryChallanNumber: this.updateGrn.deliveryChallanNumber || null,
      lrNumber:              this.updateGrn.lrNumber              || null,
      transporterName:       this.updateGrn.transporterName       || null,
      vehicleNumber:         this.updateGrn.vehicleNumber         || null,
      deliveryLocation:      this.updateGrn.deliveryLocation      || null,
      receivedByUserId:      this.updateGrn.receivedByUserId      || this.userId,
      receivedByName:        this.updateGrn.receivedByName        || null,
      remarks:               this.updateGrn.remarks               || null,
      internalNotes:         this.updateGrn.internalNotes         || null,
      lineItems: allLines
    };

    const step2and3 = () => {
      this.updateStep      = 2;
      this.updateStepLabel = 'Step 2 of 3 — Updating received quantities…';
      this.cdr.markForCheck();

      this.dataService.updateGRN(grnId, updatePayload).subscribe({
        next: () => {
          this.updateStep      = 3;
          this.updateStepLabel = 'Step 3 of 3 — Re-submitting GRN for QA review…';
          this.cdr.markForCheck();

          this.dataService.submitGRN(grnId).subscribe({
            next: () => {
              this.updateStep           = 4;
              this.updateStepLabel      = '';
              this.updateSuccessMessage =
                `GRN ${grnNumber} updated successfully! Added ${addedQty} unit(s). ` +
                `GRN has been re-submitted for QA review.`;
              this.updateSubmitting     = false;
              this.loadGRNs();
              this.cdr.markForCheck();
              setTimeout(() => this.closeUpdateModal(), 2800);
            },
            error: (err: any) => {
              this.updateErrorMessage =
                `Quantities saved but re-submit failed: ` +
                (err?.error?.message || 'Please manually submit the GRN from the list.');
              this.updateSubmitting = false;
              this.updateStep       = 0;
              this.loadGRNs();
              this.cdr.markForCheck();
            }
          });
        },
        error: (err: any) => {
          this.updateErrorMessage =
            `Failed to update quantities: ${err?.error?.message || 'Please try again.'}`;
          this.updateSubmitting = false;
          this.updateStep       = 0;
          this.cdr.markForCheck();
        }
      });
    };

    if (isDraft) {
      step2and3();
    } else {
      this.updateStep      = 1;
      this.updateStepLabel = 'Step 1 of 3 — Reverting GRN to draft…';
      this.cdr.markForCheck();

      this.dataService.revertGRNToDraft(grnId).subscribe({
        next: ()           => step2and3(),
        error: (err: any)  => {
          this.updateErrorMessage =
            `Could not revert GRN to draft: ${err?.error?.message || 'Server error. Please try again.'}`;
          this.updateSubmitting = false;
          this.updateStep       = 0;
          this.cdr.markForCheck();
        }
      });
    }
  }

  // =========================================================================
  //  VIEW MODAL
  // =========================================================================

  openView(grn: any): void {
    this.viewLoading   = true;
    this.showViewModal = true;
    this.viewGrn       = null;
    this.dataService.getGRNById(grn.id).subscribe({
      next: (res: any) => { this.viewGrn = this.normalizeGrn(res.data || res); this.viewLoading = false; },
      error: ()        => { this.viewGrn = grn; this.viewLoading = false; }
    });
  }
  closeViewModal(): void { this.showViewModal = false; this.viewGrn = null; }

  openAction(grn: any, type: 'approve' | 'cancel'): void {
    this.selectedGrn = grn; this.actionType = type; this.showActionModal = true; this.errorMessage = '';
  }
  closeActionModal(): void { this.showActionModal = false; this.selectedGrn = null; }

  confirmAction(): void {
    if (!this.selectedGrn) return;
    this.actionLoading = true;
    const obs = this.actionType === 'approve'
      ? this.dataService.approveGRN(this.selectedGrn.id, this.buyerName)
      : this.dataService.cancelGRN(this.selectedGrn.id);
    obs.subscribe({
      next: () => {
        this.successMessage = `GRN ${this.actionType === 'approve' ? 'approved' : 'cancelled'} successfully.`;
        this.actionLoading  = false;
        this.closeActionModal();
        this.loadGRNs();
      },
      error: (err: any) => {
        this.errorMessage  = err?.error?.message || `Failed to ${this.actionType} GRN.`;
        this.actionLoading = false;
      }
    });
  }

  submitGRN(grn: any): void {
    this.actionLoading = true;
    this.dataService.submitGRN(grn.id).subscribe({
      next: () => { this.successMessage = 'GRN submitted for QA review.'; this.actionLoading = false; this.loadGRNs(); },
      error: (err: any) => { this.errorMessage = err?.error?.message || 'Failed to submit GRN.'; this.actionLoading = false; }
    });
  }

  openQAReview(grn: any): void { this.router.navigate(['/grn-qa', grn.id]); }

  openMatchModal(grn: any): void {
    this.router.navigate(['/three-way-match'], {
      state: {
        grnId: grn.id,
        poId:  grn.purchaseOrderId || grn.poId
      }
    });
  }

  private loadInvoicesForMatch(grn: any): void {
    this.matchInvoicesLoading = true;
    this.dataService.getInvoicesByPO(grn.purchaseOrderId || grn.poId).subscribe({
      next: (res: any) => {
        const all = Array.isArray(res) ? res : (res.data || []);
        this.matchInvoices        = all.filter((inv: any) => inv.status === 'SUBMITTED');
        this.matchInvoicesLoading = false;
      },
      error: () => { this.matchInvoicesLoading = false; this.matchErrorMessage = 'Could not load invoices for this PO.'; }
    });
  }

  closeMatchModal(): void { this.showMatchModal = false; this.matchGrn = null; this.matchResult = null; this.matchErrorMessage = ''; }

  performMatch(): void {
    if (!this.selectedInvoiceId) { this.matchErrorMessage = 'Please select an invoice.'; return; }
    this.matchErrorMessage = ''; this.matchSubmitting = true; this.matchResult = null;
    const payload = {
      purchaseOrderId:      this.matchGrn.purchaseOrderId || this.matchGrn.poId,
      grnId:                this.matchGrn.id,
      invoiceId:            this.selectedInvoiceId,
      tolerancePercentage:  this.matchTolerance,
      performedByName:      this.buyerName
    };
    this.dataService.performThreeWayMatch(payload).subscribe({
      next: (res: any) => { this.matchResult = res.data || res; this.matchSubmitting = false; this.loadGRNs(); },
      error: (e: any)  => { this.matchErrorMessage = e?.error?.message || 'Failed to perform 3-Way Match.'; this.matchSubmitting = false; }
    });
  }

  navigateCreate(): void { this.router.navigate(['/grn-create']); }
  dismissAlert():   void { this.errorMessage = ''; this.successMessage = ''; }

  // =========================================================================
  //  UTILITIES
  // =========================================================================

  getMatchStatusClass(status: string): string {
    const map: Record<string, string> = {
      MATCHED: 'match-badge match-ok', OVERRIDDEN_APPROVED: 'match-badge match-ok',
      PARTIAL_MATCH: 'match-badge match-warn', EXCESS_DELIVERY: 'match-badge match-warn',
      QUANTITY_MISMATCH: 'match-badge match-fail', PRICE_MISMATCH: 'match-badge match-fail',
      ITEM_MISMATCH: 'match-badge match-fail', FAILED: 'match-badge match-fail', DISPUTED: 'match-badge match-fail',
    };
    return map[status] || 'match-badge match-warn';
  }

  private normalizeGrn(g: any): any {
    const lineItems = (g.lineItems || g.grnLineItems || []).map((li: any) => ({
      ...li,
      itemDescription:   li.itemDescription  || li.itemName || '',
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
      transporterName: g.transporterName || g.transporter             || '',
      receivedByName:  g.receivedByName  || g.receiverName            || '',
      inspectedByName: g.inspectedByName || '',
      approvedByName:  g.approvedByName  || '',
      purchaseOrderId: g.purchaseOrderId || g.poId || g.purchaseOrder?.id,
      totalOrderedQuantity:   lineItems.reduce((s: number, li: any) => s + li.orderedQuantity,   0),
      totalReceivedQuantity:  lineItems.reduce((s: number, li: any) => s + li.receivedQuantity,  0),
      totalAcceptedQuantity:  lineItems.reduce((s: number, li: any) => s + li.acceptedQuantity,  0),
      totalRejectedQuantity:  lineItems.reduce((s: number, li: any) => s + li.rejectedQuantity,  0),
      totalDefectiveQuantity: lineItems.reduce((s: number, li: any) => s + li.defectiveQuantity, 0),
    };
  }

  getStatusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      DRAFT: 'status-badge status-draft', SUBMITTED: 'status-badge status-submitted',
      QA_REVIEW: 'status-badge status-qa', APPROVED: 'status-badge status-approved',
      CLOSED: 'status-badge status-closed', CANCELLED: 'status-badge status-cancelled'
    };
    return map[status] || 'status-badge status-draft';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      DRAFT: 'Draft', SUBMITTED: 'Submitted', QA_REVIEW: 'QA Review',
      APPROVED: 'Approved', CLOSED: 'Closed', CANCELLED: 'Cancelled'
    };
    return map[status] || status;
  }

  formatDate(d: string): string {
    if (!d) return '—';
    try { return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }); }
    catch { return '—'; }
  }

  formatCurrency(v: number | null): string {
    if (v == null) return '₹0.00';
    return '₹' + Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
}