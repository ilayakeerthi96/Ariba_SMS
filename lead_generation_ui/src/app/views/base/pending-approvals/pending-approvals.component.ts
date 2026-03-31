
// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { DataService } from '../../../shared/service/DataService';
// import { RFQService } from '../models/rfq.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';

// @Component({
//   selector: 'app-pending-approvals',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './pending-approvals.component.html',
//   styleUrls: ['./pending-approvals.component.css']
// })
// export class PendingApprovalsComponent implements OnInit {

//   // ==================== USER INFO ====================
//   userId: number = 0;
//   userName: string = '';
//   userEmail: string = '';
//   hierarchyLevelId: number | null = null;
//   hierarchyLevelName: string = '';
//   hierarchyLevelOrder: number | null = null;
//   companyName: string = '';

//   // ==================== RFQ DATA ====================
//   pendingApprovals: any[] = [];
//   filteredApprovals: any[] = [];
//   holdApprovals: any[] = [];
//   filteredHoldApprovals: any[] = [];
//   holdApprovalsCount: number = 0;

//   // ==================== PO DATA ====================
//   pendingPOApprovals: any[] = [];
//   filteredPOApprovals: any[] = [];
//   holdPOApprovals: any[] = [];
//   filteredHoldPOApprovals: any[] = [];
//   pendingPOCount: number = 0;
//   holdPOCount: number = 0;

//   // ==================== TABS ====================
//   activeTab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold' = 'rfq-pending';

//   // ==================== UI STATE ====================
//   isLoading = false;
//   searchText = '';

//   // ==================== MODAL STATE ====================
//   showApprovalModal = false;
//   selectedItem: any = null;
//   modalMode: 'rfq' | 'po' = 'rfq';
//   actionType: 'approve' | 'reject' | 'return' | 'hold' | 'release' = 'approve';
//   actionComments = '';
//   isSubmitting = false;

//   // ==================== RFQ DETAIL MODAL ====================
//   showRFQDetails = false;
//   rfqDetails: any = null;
//   approvalHistory: any[] = [];

//   // ==================== LAST APPROVER (RFQ) ====================
//   isLastApprover: boolean = false;
//   requiresDates: boolean = false;
//   minDueDate: string = '';
//   minDeliveryDate: string = '';
//   rfqDueDate: string = '';
//   rfqDeliveryDate: string = '';
//   allowSupplierDownload: boolean = false;

//   constructor(
//     private dataService: DataService,
//     private rfqService: RFQService,
//     private messageService: MessageService,
//     private authService: AuthService,
//     public router: Router,
//     private cdr: ChangeDetectorRef
//   ) {}

//   ngOnInit(): void {
//     this.initializeUser();
//     this.loadAllData();
//     this.setMinDates();
//   }

//   // ==================== INIT ====================

//   private initializeUser(): void {
//     this.userId = Number(localStorage.getItem('userId')) || 0;
//     this.userName = localStorage.getItem('fullName') || 'User';
//     this.userEmail = localStorage.getItem('email') || localStorage.getItem('username') || '';
//     this.companyName = localStorage.getItem('companyName') || 'NA';
//     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

//     if (!this.userId) {
//       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//       this.router.navigate(['/login']);
//     }
//   }

//   private setMinDates(): void {
//     const now = new Date();
//     this.minDueDate = this.formatDateForInput(now);
//     this.minDeliveryDate = this.formatDateForInput(now);
//   }

//   private formatDateForInput(date: Date): string {
//     const y = date.getFullYear();
//     const m = String(date.getMonth() + 1).padStart(2, '0');
//     const d = String(date.getDate()).padStart(2, '0');
//     const h = String(date.getHours()).padStart(2, '0');
//     const min = String(date.getMinutes()).padStart(2, '0');
//     return `${y}-${m}-${d}T${h}:${min}`;
//   }

//   private loadAllData(): void {
//     this.loadPendingApprovals();
//     this.loadHoldApprovals();
//     this.loadPendingPOApprovals();
//     this.loadHoldPOApprovals();
//   }

//   // ==================== HELPER: Resolve PO ID ====================
//   // POApprovalResponse DTO uses "purchaseOrderId", not "poId"
//   // This helper safely resolves the correct PO ID regardless of field name
//   private resolvePOId(item: any): number | undefined {
//     const id = item?.purchaseOrderId ?? item?.poId ?? item?.id;
//     return id ? Number(id) : undefined;
//   }

//   // ==================== RFQ: LOAD PENDING ====================

//   loadPendingApprovals(): void {
//     if (!this.userId) return;
//     this.isLoading = true;

//     this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) {
//           data = response.data;
//         } else if (Array.isArray(response)) {
//           data = response;
//         }
//         this.pendingApprovals = data;
//         this.filteredApprovals = [...data];
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingApprovals = [];
//         this.filteredApprovals = [];
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== RFQ: LOAD HOLD ====================

//   loadHoldApprovals(): void {
//     if (!this.userId) return;

//     this.dataService.getHoldApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;
//         this.holdApprovals = data;
//         this.filteredHoldApprovals = [...data];
//         this.holdApprovalsCount = data.length;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.holdApprovals = [];
//         this.filteredHoldApprovals = [];
//         this.holdApprovalsCount = 0;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== PO: LOAD PENDING ====================

//   loadPendingPOApprovals(): void {
//     if (!this.userId) return;

//     this.dataService.getPendingPOApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;

//         // ✅ FIX: Normalize each item so poId is always reliably set
//         this.pendingPOApprovals = data.map(item => ({
//           ...item,
//           poId: item.purchaseOrderId ?? item.poId ?? item.id
//         }));
//         this.filteredPOApprovals = [...this.pendingPOApprovals];
//         this.pendingPOCount = this.pendingPOApprovals.length;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingPOApprovals = [];
//         this.filteredPOApprovals = [];
//         this.pendingPOCount = 0;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== PO: LOAD HOLD ====================

//   loadHoldPOApprovals(): void {
//     if (!this.userId) return;

//     this.dataService.getHoldPOApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         let data: any[] = [];
//         if (response?.success === true && Array.isArray(response.data)) data = response.data;
//         else if (Array.isArray(response)) data = response;

//         // ✅ FIX: Normalize each item so poId is always reliably set
//         this.holdPOApprovals = data.map(item => ({
//           ...item,
//           poId: item.purchaseOrderId ?? item.poId ?? item.id
//         }));
//         this.filteredHoldPOApprovals = [...this.holdPOApprovals];
//         this.holdPOCount = this.holdPOApprovals.length;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.holdPOApprovals = [];
//         this.filteredHoldPOApprovals = [];
//         this.holdPOCount = 0;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== SEARCH ====================

//   onSearch(): void {
//     const term = this.searchText.toLowerCase().trim();
//     const filter = (arr: any[], fields: string[]) =>
//       term ? arr.filter(item => fields.some(f => (item[f] || '').toLowerCase().includes(term))) : [...arr];

//     this.filteredApprovals = filter(this.pendingApprovals, ['rfqNumber', 'rfqTitle', 'buyerName']);
//     this.filteredHoldApprovals = filter(this.holdApprovals, ['rfqNumber', 'rfqTitle', 'buyerName']);
//     this.filteredPOApprovals = filter(this.pendingPOApprovals, ['poNumber', 'supplierName', 'buyerName']);
//     this.filteredHoldPOApprovals = filter(this.holdPOApprovals, ['poNumber', 'supplierName', 'buyerName']);
//     this.cdr.markForCheck();
//   }

//   // ==================== OPEN MODAL ====================

//   openApprovalModal(item: any, action: 'approve' | 'reject' | 'return' | 'hold' | 'release', mode: 'rfq' | 'po'): void {
//     this.selectedItem = item;
//     this.actionType = action;
//     this.modalMode = mode;
//     this.actionComments = '';
//     this.rfqDueDate = '';
//     this.rfqDeliveryDate = '';
//     this.isLastApprover = false;
//     this.requiresDates = false;
//     this.allowSupplierDownload = item.allowSupplierDownload !== undefined ? item.allowSupplierDownload : true;

//     if (mode === 'rfq' && action === 'approve') {
//       this.checkIfLastApprover(item.rfqId);
//     }

//     this.showApprovalModal = true;
//     this.cdr.markForCheck();
//   }

//   private checkIfLastApprover(rfqId: number): void {
//     this.dataService.isLastApprover(rfqId, this.userId).subscribe({
//       next: (response: any) => {
//         if (response.success) {
//           this.isLastApprover = response.isLastApprover || false;
//           this.requiresDates = response.requiresDates || false;
//           this.cdr.markForCheck();
//         }
//       },
//       error: () => {
//         this.isLastApprover = false;
//         this.requiresDates = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   onDueDateChange(): void {
//     if (this.rfqDueDate) {
//       this.minDeliveryDate = this.rfqDueDate;
//       if (this.rfqDeliveryDate && this.rfqDeliveryDate < this.rfqDueDate) {
//         this.rfqDeliveryDate = '';
//       }
//       this.cdr.markForCheck();
//     }
//   }

//   closeApprovalModal(): void {
//     this.showApprovalModal = false;
//     this.selectedItem = null;
//     this.actionComments = '';
//     this.allowSupplierDownload = false;
//     this.isSubmitting = false;
//     this.cdr.markForCheck();
//   }

//   // ==================== SUBMIT ACTION ====================

//   submitApprovalAction(): void {
//     if (!this.selectedItem || !this.userId) {
//       this.messageService.showMessage('error', 'Error', 'Invalid approval data');
//       return;
//     }

//     const requiresComments = ['reject', 'return', 'hold'];
//     if (requiresComments.includes(this.actionType) && !this.actionComments.trim()) {
//       const msgs: any = {
//         reject: 'Please provide rejection reason',
//         return: 'Please provide revision comments',
//         hold: 'Please provide hold remarks'
//       };
//       this.messageService.showMessage('warning', 'Warning', msgs[this.actionType]);
//       return;
//     }

//     if (this.modalMode === 'rfq' && this.actionType === 'approve' && this.isLastApprover) {
//       if (!this.rfqDueDate || !this.rfqDeliveryDate) {
//         this.messageService.showMessage('error', 'Validation Error', 'As final approver, you must set both RFQ dates');
//         return;
//       }
//       if (this.rfqDeliveryDate < this.rfqDueDate) {
//         this.messageService.showMessage('error', 'Validation Error', 'Delivery Date cannot be before Due Date');
//         return;
//       }
//     }

//     this.isSubmitting = true;
//     const comments = this.actionComments.trim() || 'No comments provided';
//     let apiCall: any;

//     if (this.modalMode === 'rfq') {
//       // ==================== RFQ ACTIONS ====================
//       const rfqId = this.selectedItem.rfqId;

//       if (!rfqId) {
//         this.messageService.showMessage('error', 'Error', 'Invalid RFQ ID');
//         this.isSubmitting = false;
//         return;
//       }

//       if (this.actionType === 'approve') {
//         apiCall = this.dataService.approveRFQWithDates(
//           rfqId, this.userId, comments,
//           this.rfqDueDate || undefined,
//           this.rfqDeliveryDate || undefined,
//           this.allowSupplierDownload
//         );
//       } else if (this.actionType === 'reject') {
//         apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
//       } else if (this.actionType === 'return') {
//         apiCall = this.dataService.returnRFQForRevision(rfqId, this.userId, comments);
//       } else if (this.actionType === 'hold') {
//         apiCall = this.dataService.holdRFQ(rfqId, this.userId, comments);
//       } else if (this.actionType === 'release') {
//         apiCall = this.dataService.releaseHold(rfqId, this.userId, comments);
//       }

//     } else {
//       // ==================== PO ACTIONS ====================
//       // ✅ FIX: Use the normalized poId set during data load
//       // POApprovalResponse DTO field is "purchaseOrderId", not "poId"
//       const poId = this.selectedItem.poId
//         ?? this.selectedItem.purchaseOrderId
//         ?? this.selectedItem.id;

//       if (!poId) {
//         this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID. Cannot perform this action.');
//         this.isSubmitting = false;
//         return;
//       }

//       console.log(`[PO ACTION] Action: ${this.actionType}, PO ID: ${poId}, User: ${this.userId}`);

//       if (this.actionType === 'approve') {
//         apiCall = this.dataService.approvePO(poId, this.userId, comments);
//       } else if (this.actionType === 'reject') {
//         apiCall = this.dataService.rejectPO(poId, this.userId, comments);
//       } else if (this.actionType === 'return') {
//         apiCall = this.dataService.returnPOForRevision(poId, this.userId, comments);
//       } else if (this.actionType === 'hold') {
//         apiCall = this.dataService.holdPO(poId, this.userId, comments);
//       } else if (this.actionType === 'release') {
//         apiCall = this.dataService.releasePOHold(poId, this.userId, comments);
//       }
//     }

//     if (!apiCall) {
//       this.messageService.showMessage('error', 'Error', 'Invalid action');
//       this.isSubmitting = false;
//       return;
//     }

//     apiCall.subscribe({
//       next: (response: any) => {
//         const actionLabels: any = {
//           approve: 'approved',
//           reject: 'permanently rejected',
//           return: 'returned for revision',
//           hold: 'put on HOLD',
//           release: 'released from HOLD'
//         };
//         const ref = this.modalMode === 'rfq'
//           ? this.selectedItem.rfqNumber
//           : this.selectedItem.poNumber;
//         this.messageService.showMessage('success', 'Success', `${ref} ${actionLabels[this.actionType]} successfully`);
//         this.closeApprovalModal();
//         this.loadAllData();
//       },
//       error: (error: any) => {
//         const errorMsg = error.error?.message || `Failed to ${this.actionType}`;
//         this.messageService.showMessage('error', 'Error', errorMsg);
//         this.isSubmitting = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== NAVIGATION ====================

//   viewRFQDetailsInNewTab(approval: any): void {
//     this.router.navigate([`/create-rfq/${approval.rfqId}/view`]);
//   }

//   // ✅ FIX: Always resolve the correct PO ID before navigating
//   viewPODetails(approval: any): void {
//     const poId = approval?.poId ?? approval?.purchaseOrderId ?? approval?.id;

//     if (!poId) {
//       this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID');
//       return;
//     }

//     this.router.navigate([`/po-details/${poId}`]);
//   }

//   switchTab(tab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold'): void {
//     this.activeTab = tab;
//     this.searchText = '';
//     this.onSearch();
//     this.cdr.markForCheck();
//   }

//   refresh(): void {
//     this.searchText = '';
//     this.loadAllData();
//   }

//   // ==================== HELPERS ====================

//   get totalBadgeCount(): number {
//     return this.filteredApprovals.length + this.pendingPOCount;
//   }

//   getActionButtonText(): string {
//     const labels: any = {
//       approve: 'Approve',
//       reject: 'Reject Permanently',
//       return: 'Return for Revision',
//       hold: 'Put on HOLD',
//       release: 'Release HOLD'
//     };
//     return labels[this.actionType] || 'Submit';
//   }

//   getActionButtonClass(): string {
//     if (this.actionType === 'approve' || this.actionType === 'release') return 'btn-success';
//     if (this.actionType === 'reject') return 'btn-danger';
//     return 'btn-warning';
//   }

//   getModalTitle(): string {
//     const type = this.modalMode === 'rfq' ? 'RFQ' : 'PO';
//     if (this.actionType === 'approve') return `Approve ${type}`;
//     if (this.actionType === 'reject') return `Reject ${type} (Permanent)`;
//     if (this.actionType === 'return') return `Return ${type} for Revision`;
//     if (this.actionType === 'hold') return `Put ${type} on HOLD`;
//     if (this.actionType === 'release') return `Release ${type} HOLD`;
//     return 'Action';
//   }

//   getModalHeaderClass(): string {
//     if (this.actionType === 'approve' || this.actionType === 'release') return 'bg-success';
//     if (this.actionType === 'reject') return 'bg-danger';
//     return 'bg-warning';
//   }

//   getStatusBadgeClass(status: string): string {
//     const statusMap: any = {
//       'PENDING': 'bg-warning text-dark',
//       'APPROVED': 'bg-success',
//       'REJECTED': 'bg-danger',
//       'RESUBMITTED': 'bg-info',
//       'DRAFT': 'bg-secondary',
//       'AWAITING_APPROVAL': 'bg-warning',
//       'PUBLISHED': 'bg-info',
//       'CLOSED': 'bg-dark',
//       'RETURNED_FOR_REVISION': 'bg-warning text-dark',
//       'HOLD': 'bg-warning text-dark',
//       'PENDING_APPROVAL': 'bg-warning text-dark'
//     };
//     return statusMap[status] || 'bg-light text-dark';
//   }

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try { return new Date(dateString).toLocaleString('en-GB'); } catch { return 'Invalid Date'; }
//   }

//   formatCurrency(value: number): string {
//     if (!value) return '₹0';
//     return '₹' + value.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
//   }
// }

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { RFQService } from '../models/rfq.service';
import { MessageService } from '../../../shared/service/message.service';
import { AuthService } from '../../../shared/service/AuthService';

@Component({
  selector: 'app-pending-approvals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pending-approvals.component.html',
  styleUrls: ['./pending-approvals.component.css']
})
export class PendingApprovalsComponent implements OnInit {

  // ==================== USER INFO ====================
  userId: number = 0;
  userName: string = '';
  userEmail: string = '';
  hierarchyLevelId: number | null = null;
  hierarchyLevelName: string = '';
  hierarchyLevelOrder: number | null = null;
  companyName: string = '';

  // ==================== RFQ DATA ====================
  pendingApprovals: any[] = [];
  filteredApprovals: any[] = [];
  holdApprovals: any[] = [];
  filteredHoldApprovals: any[] = [];
  holdApprovalsCount: number = 0;

  // ==================== PO DATA ====================
  pendingPOApprovals: any[] = [];
  filteredPOApprovals: any[] = [];
  holdPOApprovals: any[] = [];
  filteredHoldPOApprovals: any[] = [];
  pendingPOCount: number = 0;
  holdPOCount: number = 0;

  activeTab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold' = 'rfq-pending';
  isLoading = false;
  searchText = '';

  // ==================== MODAL STATE ====================
  showApprovalModal = false;
  selectedItem: any = null;
  modalMode: 'rfq' | 'po' = 'rfq';
  actionType: 'approve' | 'reject' | 'return' | 'hold' | 'release' = 'approve';
  actionComments = '';
  isSubmitting = false;

  showRFQDetails = false;
  rfqDetails: any = null;
  approvalHistory: any[] = [];

  isLastApprover: boolean = false;
  requiresDates: boolean = false;
  minDueDate: string = '';
  minDeliveryDate: string = '';
  rfqDueDate: string = '';
  rfqDeliveryDate: string = '';
  allowSupplierDownload: boolean = false;

  constructor(
    private dataService: DataService,
    private rfqService: RFQService,
    private messageService: MessageService,
    private authService: AuthService,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeUser();
    this.loadAllData();
    this.setMinDates();
  }

  private initializeUser(): void {
    this.userId             = Number(localStorage.getItem('userId')) || 0;
    this.userName           = localStorage.getItem('fullName') || 'User';
    this.userEmail          = localStorage.getItem('email') || localStorage.getItem('username') || '';
    this.companyName        = localStorage.getItem('companyName') || 'NA';
    this.hierarchyLevelId   = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();
    if (!this.userId) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
    }
  }

  private setMinDates(): void {
    const now = new Date();
    this.minDueDate = this.formatDateForInput(now);
    this.minDeliveryDate = this.formatDateForInput(now);
  }

  private formatDateForInput(date: Date): string {
    const y = date.getFullYear(), m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    const h = String(date.getHours()).padStart(2, '0'), min = String(date.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${d}T${h}:${min}`;
  }

  private loadAllData(): void {
    this.loadPendingApprovals();
    this.loadHoldApprovals();
    this.loadPendingPOApprovals();
    this.loadHoldPOApprovals();
  }

  private resolvePOId(item: any): number | undefined {
    const id = item?.purchaseOrderId ?? item?.poId ?? item?.id;
    return id ? Number(id) : undefined;
  }

  loadPendingApprovals(): void {
    if (!this.userId) return;
    this.isLoading = true;
    this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.pendingApprovals = data;
        this.filteredApprovals = [...data];
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingApprovals = []; this.filteredApprovals = []; this.isLoading = false; }
    });
  }

  loadHoldApprovals(): void {
    if (!this.userId) return;
    this.dataService.getHoldApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        this.holdApprovals = data;
        this.filteredHoldApprovals = [...data];
        this.holdApprovalsCount = data.length;
        this.cdr.markForCheck();
      },
      error: () => { this.holdApprovals = []; this.filteredHoldApprovals = []; }
    });
  }

  loadPendingPOApprovals(): void {
    if (!this.userId) return;
    this.dataService.getPendingPOApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        // ✅ Normalize: ensure poId, currencyCode, currencySymbol are always set
        this.pendingPOApprovals = data.map(item => ({
          ...item,
          poId:           item.purchaseOrderId ?? item.poId ?? item.id,
          currencyCode:   item.currencyCode   || 'INR',
          currencySymbol: item.currencySymbol || '₹'
        }));
        this.filteredPOApprovals = [...this.pendingPOApprovals];
        this.pendingPOCount = this.pendingPOApprovals.length;
        this.cdr.markForCheck();
      },
      error: () => { this.pendingPOApprovals = []; this.filteredPOApprovals = []; }
    });
  }

  loadHoldPOApprovals(): void {
    if (!this.userId) return;
    this.dataService.getHoldPOApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        let data: any[] = [];
        if (response?.success === true && Array.isArray(response.data)) data = response.data;
        else if (Array.isArray(response)) data = response;
        // ✅ Normalize: ensure poId, currencyCode, currencySymbol are always set
        this.holdPOApprovals = data.map(item => ({
          ...item,
          poId:           item.purchaseOrderId ?? item.poId ?? item.id,
          currencyCode:   item.currencyCode   || 'INR',
          currencySymbol: item.currencySymbol || '₹'
        }));
        this.filteredHoldPOApprovals = [...this.holdPOApprovals];
        this.holdPOCount = this.holdPOApprovals.length;
        this.cdr.markForCheck();
      },
      error: () => { this.holdPOApprovals = []; this.filteredHoldPOApprovals = []; }
    });
  }

  onSearch(): void {
    const term = this.searchText.toLowerCase().trim();
    const filter = (arr: any[], fields: string[]) =>
      term ? arr.filter(item => fields.some(f => (item[f] || '').toLowerCase().includes(term))) : [...arr];
    this.filteredApprovals       = filter(this.pendingApprovals, ['rfqNumber', 'rfqTitle', 'buyerName']);
    this.filteredHoldApprovals   = filter(this.holdApprovals, ['rfqNumber', 'rfqTitle', 'buyerName']);
    this.filteredPOApprovals     = filter(this.pendingPOApprovals, ['poNumber', 'supplierName', 'buyerName']);
    this.filteredHoldPOApprovals = filter(this.holdPOApprovals, ['poNumber', 'supplierName', 'buyerName']);
    this.cdr.markForCheck();
  }

  openApprovalModal(item: any, action: 'approve' | 'reject' | 'return' | 'hold' | 'release', mode: 'rfq' | 'po'): void {
    this.selectedItem = item;
    this.actionType   = action;
    this.modalMode    = mode;
    this.actionComments = '';
    this.rfqDueDate = '';
    this.rfqDeliveryDate = '';
    this.isLastApprover = false;
    this.requiresDates  = false;
    this.allowSupplierDownload = item.allowSupplierDownload !== undefined ? item.allowSupplierDownload : true;
    if (mode === 'rfq' && action === 'approve') this.checkIfLastApprover(item.rfqId);
    this.showApprovalModal = true;
    this.cdr.markForCheck();
  }

  private checkIfLastApprover(rfqId: number): void {
    this.dataService.isLastApprover(rfqId, this.userId).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.isLastApprover = response.isLastApprover || false;
          this.requiresDates  = response.requiresDates  || false;
          this.cdr.markForCheck();
        }
      },
      error: () => { this.isLastApprover = false; }
    });
  }

  onDueDateChange(): void {
    if (this.rfqDueDate) {
      this.minDeliveryDate = this.rfqDueDate;
      if (this.rfqDeliveryDate && this.rfqDeliveryDate < this.rfqDueDate) this.rfqDeliveryDate = '';
      this.cdr.markForCheck();
    }
  }

  closeApprovalModal(): void {
    this.showApprovalModal = false;
    this.selectedItem      = null;
    this.actionComments    = '';
    this.isSubmitting      = false;
    this.cdr.markForCheck();
  }

  submitApprovalAction(): void {
    if (!this.selectedItem || !this.userId) { this.messageService.showMessage('error', 'Error', 'Invalid approval data'); return; }

    const requiresComments = ['reject', 'return', 'hold'];
    if (requiresComments.includes(this.actionType) && !this.actionComments.trim()) {
      const msgs: any = { reject: 'Please provide rejection reason', return: 'Please provide revision comments', hold: 'Please provide hold remarks' };
      this.messageService.showMessage('warning', 'Warning', msgs[this.actionType]);
      return;
    }

    if (this.modalMode === 'rfq' && this.actionType === 'approve' && this.isLastApprover) {
      if (!this.rfqDueDate || !this.rfqDeliveryDate) {
        this.messageService.showMessage('error', 'Validation Error', 'As final approver, you must set both RFQ dates'); return;
      }
    }

    this.isSubmitting = true;
    const comments = this.actionComments.trim() || 'No comments provided';
    let apiCall: any;

    if (this.modalMode === 'rfq') {
      const rfqId = this.selectedItem.rfqId;
      if (!rfqId) { this.messageService.showMessage('error', 'Error', 'Invalid RFQ ID'); this.isSubmitting = false; return; }
      if (this.actionType === 'approve') {
        apiCall = this.dataService.approveRFQWithDates(rfqId, this.userId, comments,
          this.rfqDueDate || undefined, this.rfqDeliveryDate || undefined, this.allowSupplierDownload);
      } else if (this.actionType === 'reject')  apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
      else if (this.actionType === 'return')    apiCall = this.dataService.returnRFQForRevision(rfqId, this.userId, comments);
      else if (this.actionType === 'hold')      apiCall = this.dataService.holdRFQ(rfqId, this.userId, comments);
      else if (this.actionType === 'release')   apiCall = this.dataService.releaseHold(rfqId, this.userId, comments);
    } else {
      const poId = this.selectedItem.poId ?? this.selectedItem.purchaseOrderId ?? this.selectedItem.id;
      if (!poId) { this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID'); this.isSubmitting = false; return; }
      if (this.actionType === 'approve')       apiCall = this.dataService.approvePO(poId, this.userId, comments);
      else if (this.actionType === 'reject')   apiCall = this.dataService.rejectPO(poId, this.userId, comments);
      else if (this.actionType === 'return')   apiCall = this.dataService.returnPOForRevision(poId, this.userId, comments);
      else if (this.actionType === 'hold')     apiCall = this.dataService.holdPO(poId, this.userId, comments);
      else if (this.actionType === 'release')  apiCall = this.dataService.releasePOHold(poId, this.userId, comments);
    }

    if (!apiCall) { this.messageService.showMessage('error', 'Error', 'Invalid action'); this.isSubmitting = false; return; }

    apiCall.subscribe({
      next: (response: any) => {
        const labels: any = { approve: 'approved', reject: 'permanently rejected', return: 'returned for revision', hold: 'put on HOLD', release: 'released from HOLD' };
        const ref = this.modalMode === 'rfq' ? this.selectedItem.rfqNumber : this.selectedItem.poNumber;
        this.messageService.showMessage('success', 'Success', `${ref} ${labels[this.actionType]} successfully`);
        this.closeApprovalModal();
        this.loadAllData();
      },
      error: (error: any) => {
        this.messageService.showMessage('error', 'Error', error.error?.message || `Failed to ${this.actionType}`);
        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }

  viewRFQDetailsInNewTab(approval: any): void { this.router.navigate([`/create-rfq/${approval.rfqId}/view`]); }

  viewPODetails(approval: any): void {
    const poId = approval?.poId ?? approval?.purchaseOrderId ?? approval?.id;
    if (!poId) { this.messageService.showMessage('error', 'Error', 'Invalid Purchase Order ID'); return; }
    this.router.navigate([`/po-details/${poId}`]);
  }

  switchTab(tab: 'rfq-pending' | 'rfq-hold' | 'po-pending' | 'po-hold'): void {
    this.activeTab = tab;
    this.searchText = '';
    this.onSearch();
    this.cdr.markForCheck();
  }

  refresh(): void { this.searchText = ''; this.loadAllData(); }

  get totalBadgeCount(): number { return this.filteredApprovals.length + this.pendingPOCount; }

  getActionButtonText(): string {
    const labels: any = { approve: 'Approve', reject: 'Reject Permanently', return: 'Return for Revision', hold: 'Put on HOLD', release: 'Release HOLD' };
    return labels[this.actionType] || 'Submit';
  }

  getActionButtonClass(): string {
    if (this.actionType === 'approve' || this.actionType === 'release') return 'btn-success';
    if (this.actionType === 'reject') return 'btn-danger';
    return 'btn-warning';
  }

  getModalTitle(): string {
    const type = this.modalMode === 'rfq' ? 'RFQ' : 'PO';
    if (this.actionType === 'approve') return `Approve ${type}`;
    if (this.actionType === 'reject')  return `Reject ${type} (Permanent)`;
    if (this.actionType === 'return')  return `Return ${type} for Revision`;
    if (this.actionType === 'hold')    return `Put ${type} on HOLD`;
    if (this.actionType === 'release') return `Release ${type} HOLD`;
    return 'Action';
  }

  getModalHeaderClass(): string {
    if (this.actionType === 'approve' || this.actionType === 'release') return 'bg-success';
    if (this.actionType === 'reject') return 'bg-danger';
    return 'bg-warning';
  }

  getStatusBadgeClass(status: string): string {
    const m: any = {
      'PENDING': 'bg-warning text-dark', 'APPROVED': 'bg-success', 'REJECTED': 'bg-danger',
      'DRAFT': 'bg-secondary', 'AWAITING_APPROVAL': 'bg-warning',
      'RETURNED_FOR_REVISION': 'bg-warning text-dark', 'HOLD': 'bg-warning text-dark',
      'PENDING_APPROVAL': 'bg-warning text-dark'
    };
    return m[status] || 'bg-light text-dark';
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try { return new Date(dateString).toLocaleString('en-GB'); } catch { return 'Invalid Date'; }
  }

  /**
   * ✅ Format currency using the code from the approval object.
   * For PO approvals, currencyCode comes from POApprovalResponse (set from PO's buyer location).
   */
  formatCurrency(value: number, currencyCode?: string): string {
    const code   = currencyCode || 'INR';
    const symbol = this.getSymbolForCode(code);
    if (!value) return `${symbol} 0.00`;
    const formatted = Number(value).toLocaleString('en-IN', {
      minimumFractionDigits: 2, maximumFractionDigits: 2
    });
    const rtlCodes = ['AED', 'SAR', 'QAR', 'KWD', 'BHD', 'OMR', 'IRR', 'IQD', 'JOD', 'LBP'];
    return rtlCodes.includes(code) ? `${formatted} ${symbol}` : `${symbol} ${formatted}`;
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
}