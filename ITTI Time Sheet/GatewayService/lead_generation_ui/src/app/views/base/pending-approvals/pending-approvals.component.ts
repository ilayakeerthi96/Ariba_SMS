
// // // import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { FormsModule } from '@angular/forms';
// // // import { Router } from '@angular/router';
// // // import { DataService } from '../../../shared/service/DataService';
// // // import { RFQService } from '../models/rfq.service';
// // // import { MessageService } from '../../../shared/service/message.service';
// // // import { AuthService } from '../../../shared/service/AuthService';

// // // @Component({
// // //   selector: 'app-pending-approvals',
// // //   standalone: true,
// // //   imports: [CommonModule, FormsModule],
// // //   templateUrl: './pending-approvals.component.html',
// // //   styleUrls: ['./pending-approvals.component.css']
// // // })
// // // export class PendingApprovalsComponent implements OnInit {

// // //   // ==================== USER INFO ====================
// // //   userId: number = 0;
// // //   userName: string = '';
// // //   hierarchyLevelId: number | null = null;
// // //   hierarchyLevelName: string = '';
// // //   hierarchyLevelOrder: number | null = null;

// // //   // ==================== DATA ====================
// // //   pendingApprovals: any[] = [];
// // //   filteredApprovals: any[] = [];
// // //   isLoading = false;

// // //   // ==================== SEARCH ====================
// // //   searchText = '';

// // //   // ==================== MODAL STATE ====================
// // //   showApprovalModal = false;
// // //   selectedRFQ: any = null;
// // //   actionType: 'approve' | 'reject' | 'resubmit' = 'approve';
// // //   actionComments = '';
// // //   isSubmitting = false;

// // //   // ==================== RFQ DETAILS ====================
// // //   showRFQDetails = false;
// // //   rfqDetails: any = null;
// // //   approvalHistory: any[] = [];

// // //   constructor(
// // //     private dataService: DataService,
// // //     private rfqService: RFQService,
// // //     private messageService: MessageService,
// // //     private authService: AuthService,
// // //     private router: Router,
// // //     private cdr: ChangeDetectorRef
// // //   ) {}

// // //   ngOnInit(): void {
// // //     this.initializeUser();
// // //     this.loadPendingApprovals();
// // //   }

// // //   // ==================== INITIALIZE USER ====================

// // //   private initializeUser(): void {
// // //     this.userId = Number(localStorage.getItem('userId')) || 0;
// // //     this.userName = localStorage.getItem('fullName') || 'User';
    
// // //     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
// // //     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
// // //     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

// // //     console.log('%c[PENDING APPROVALS INIT]', 'color: #00aa00; font-weight: bold;', {
// // //       userId: this.userId,
// // //       userName: this.userName,
// // //       levelId: this.hierarchyLevelId,
// // //       levelName: this.hierarchyLevelName,
// // //       levelOrder: this.hierarchyLevelOrder
// // //     });

// // //     if (!this.hierarchyLevelId) {
// // //       this.messageService.showMessage('error', 'Error', 'Hierarchy level not found');
// // //       this.router.navigate(['/hierarchy-dashboard']);
// // //       return;
// // //     }
// // //   }

// // //   // ==================== LOAD PENDING APPROVALS ====================

// // // // REPLACE THIS METHOD IN pending-approvals.component.ts

// // // loadPendingApprovals(): void {
// // //   if (!this.userId) {
// // //     return;
// // //   }

// // //   this.isLoading = true;
// // //   console.log('%c[LOADING PENDING APPROVALS]', 'color: #0066cc;', this.userId);

// // //   this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
// // //     next: (response: any) => {
// // //       console.log('%c[PENDING APPROVALS RAW RESPONSE]', 'color: #0066cc;', response);

// // //       // ✅ FIX: Handle both response structures
// // //       if (response.success) {
// // //         // Backend returns: { success: true, data: { approvals: [...], count: 5 } }
// // //         if (response.data && Array.isArray(response.data.approvals)) {
// // //           this.pendingApprovals = response.data.approvals;
// // //         } 
// // //         // Fallback: { success: true, data: [...] }
// // //         else if (Array.isArray(response.data)) {
// // //           this.pendingApprovals = response.data;
// // //         } 
// // //         else {
// // //           this.pendingApprovals = [];
// // //         }
        
// // //         this.filteredApprovals = [...this.pendingApprovals];
        
// // //         console.log(`✅ Found ${this.pendingApprovals.length} pending approvals`);
// // //       } else {
// // //         this.pendingApprovals = [];
// // //         this.filteredApprovals = [];
// // //       }

// // //       this.isLoading = false;
// // //       this.cdr.markForCheck();
// // //     },
// // //     error: (error: any) => {
// // //       console.error('%c[ERROR]', 'color: #cc0000;', error);
// // //       this.messageService.showMessage('error', 'Error', 'Failed to load pending approvals');
// // //       this.isLoading = false;
// // //       this.cdr.markForCheck();
// // //     }
// // //   });
// // // }

// // //   // ==================== SEARCH ====================

// // //   onSearch(): void {
// // //     const term = this.searchText.toLowerCase().trim();

// // //     if (!term) {
// // //       this.filteredApprovals = [...this.pendingApprovals];
// // //     } else {
// // //       this.filteredApprovals = this.pendingApprovals.filter((approval: any) =>
// // //         (approval.rfqNumber || '').toLowerCase().includes(term) ||
// // //         (approval.rfqTitle || '').toLowerCase().includes(term)
// // //       );
// // //     }

// // //     this.cdr.markForCheck();
// // //   }

// // //   // ==================== VIEW RFQ DETAILS ====================

// // //   viewRFQDetails(approval: any): void {
// // //     console.log('%c[VIEW RFQ DETAILS]', 'color: #0066cc;', approval.rfqId);

// // //     this.isLoading = true;

// // //     Promise.all([
// // //       this.rfqService.getRFQById(approval.rfqId).toPromise(),
// // //       this.dataService.getApprovalHistory(approval.rfqId).toPromise()
// // //     ]).then(([rfqResponse, historyResponse]: any[]) => {
// // //       this.rfqDetails = rfqResponse?.data;
// // //       this.approvalHistory = historyResponse?.data || [];
// // //       this.showRFQDetails = true;
// // //       this.isLoading = false;
// // //       this.cdr.markForCheck();

// // //       console.log('%c[RFQ DETAILS LOADED]', 'color: #00aa00;', {
// // //         rfq: this.rfqDetails?.rfqNumber,
// // //         historyCount: this.approvalHistory.length
// // //       });
// // //     }).catch((error: any) => {
// // //       console.error('%c[ERROR]', 'color: #cc0000;', error);
// // //       this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
// // //       this.isLoading = false;
// // //       this.cdr.markForCheck();
// // //     });
// // //   }

// // //   closeRFQDetails(): void {
// // //     this.showRFQDetails = false;
// // //     this.rfqDetails = null;
// // //     this.approvalHistory = [];
// // //     this.cdr.markForCheck();
// // //   }

// // //   // ==================== OPEN APPROVAL MODAL ====================

// // //   openApprovalModal(rfq: any, action: 'approve' | 'reject' | 'resubmit'): void {
// // //     this.selectedRFQ = rfq;
// // //     this.actionType = action;
// // //     this.actionComments = '';
// // //     this.showApprovalModal = true;
// // //     this.cdr.markForCheck();

// // //     console.log('%c[OPEN APPROVAL MODAL]', 'color: #0066cc;', {
// // //       rfq: rfq.rfqNumber,
// // //       action: action
// // //     });
// // //   }

// // //   closeApprovalModal(): void {
// // //     this.showApprovalModal = false;
// // //     this.selectedRFQ = null;
// // //     this.actionComments = '';
// // //     this.isSubmitting = false;
// // //     this.cdr.markForCheck();
// // //   }

// // //   // ==================== SUBMIT APPROVAL ACTION ====================

// // //   submitApprovalAction(): void {
// // //     if (!this.selectedRFQ || !this.hierarchyLevelId) {
// // //       return;
// // //     }

// // //     // Validate comments for reject
// // //     if (this.actionType === 'reject' && !this.actionComments.trim()) {
// // //       this.messageService.showMessage('warning', 'Warning', 'Please provide rejection comments');
// // //       return;
// // //     }

// // //     this.isSubmitting = true;
// // //     const rfqId = this.selectedRFQ.rfqId;
// // //     const comments = this.actionComments.trim() || 'No comments provided';

// // //     console.log('%c[SUBMITTING APPROVAL ACTION]', 'color: #ff6600; font-weight: bold;', {
// // //       action: this.actionType,
// // //       rfqId: rfqId,
// // //       userId: this.userId
// // //     });

// // //     let apiCall;

// // //     if (this.actionType === 'approve') {
// // //       apiCall = this.dataService.approveRFQDynamic(rfqId, this.userId, comments);
// // //     } else if (this.actionType === 'reject') {
// // //       apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
// // //     } else {
// // //       apiCall = this.dataService.resubmitRFQDynamic(rfqId, this.userId);
// // //     }

// // //     apiCall.subscribe({
// // //       next: (response: any) => {
// // //         console.log('%c[✅ ACTION SUCCESS]', 'color: #00aa00; font-weight: bold;', response);

// // //         const actionName = this.actionType === 'approve' ? 'approved' : 
// // //                           this.actionType === 'reject' ? 'rejected' : 'resubmitted';

// // //         this.messageService.showMessage('success', 'Success', 
// // //           `RFQ ${this.selectedRFQ.rfqNumber} ${actionName} successfully`);

// // //         this.closeApprovalModal();
// // //         this.loadPendingApprovals();
// // //       },
// // //       error: (error: any) => {
// // //         console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);

// // //         const errorMsg = error.error?.message || `Failed to ${this.actionType} RFQ`;
// // //         this.messageService.showMessage('error', 'Error', errorMsg);

// // //         this.isSubmitting = false;
// // //         this.cdr.markForCheck();
// // //       }
// // //     });
// // //   }

// // //   // ==================== HELPERS ====================

// // //   getActionButtonText(): string {
// // //     if (this.actionType === 'approve') return 'Approve RFQ';
// // //     if (this.actionType === 'reject') return 'Reject RFQ';
// // //     return 'Resubmit RFQ';
// // //   }

// // //   getActionButtonClass(): string {
// // //     if (this.actionType === 'approve') return 'btn-success';
// // //     if (this.actionType === 'reject') return 'btn-danger';
// // //     return 'btn-primary';
// // //   }

// // //   canResubmit(rfq: any): boolean {
// // //     // For now, allow resubmit for all (you can add logic based on status)
// // //     return rfq.status === 'REJECTED';
// // //   }

// // //   getStatusBadgeClass(status: string): string {
// // //     const statusMap: { [key: string]: string } = {
// // //       'PENDING': 'bg-warning text-dark',
// // //       'APPROVED': 'bg-success',
// // //       'REJECTED': 'bg-danger',
// // //       'RESUBMITTED': 'bg-info',
// // //       'SKIPPED': 'bg-secondary'
// // //     };
// // //     return statusMap[status] || 'bg-light text-dark';
// // //   }

// // //   getLevelDisplayName(levelName: string): string {
// // //     return levelName || 'Unknown Level';
// // //   }

// // //   formatDate(dateString: string | null): string {
// // //     if (!dateString) return 'N/A';
// // //     try {
// // //       return new Date(dateString).toLocaleString('en-GB');
// // //     } catch {
// // //       return 'Invalid Date';
// // //     }
// // //   }

// // //   // ==================== REFRESH ====================

// // //   refresh(): void {
// // //     this.loadPendingApprovals();
// // //   }
// // // }

// // import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormsModule } from '@angular/forms';
// // import { Router } from '@angular/router';
// // import { DataService } from '../../../shared/service/DataService';
// // import { RFQService } from '../models/rfq.service';
// // import { MessageService } from '../../../shared/service/message.service';
// // import { AuthService } from '../../../shared/service/AuthService';

// // @Component({
// //   selector: 'app-pending-approvals',
// //   standalone: true,
// //   imports: [CommonModule, FormsModule],
// //   templateUrl: './pending-approvals.component.html',
// //   styleUrls: ['./pending-approvals.component.css']
// // })
// // export class PendingApprovalsComponent implements OnInit {

// //   // ==================== USER INFO ====================
// //   userId: number = 0;
// //   userName: string = '';
// //   hierarchyLevelId: number | null = null;
// //   hierarchyLevelName: string = '';
// //   hierarchyLevelOrder: number | null = null;

// //   // ==================== DATA ====================
// //   pendingApprovals: any[] = [];
// //   filteredApprovals: any[] = [];
// //   isLoading = false;

// //   // ==================== SEARCH ====================
// //   searchText = '';

// //   // ==================== MODAL STATE ====================
// //   showApprovalModal = false;
// //   selectedRFQ: any = null;
// //   actionType: 'approve' | 'reject' | 'resubmit' = 'approve';
// //   actionComments = '';
// //   isSubmitting = false;

// //   // ==================== RFQ DETAILS ====================
// //   showRFQDetails = false;
// //   rfqDetails: any = null;
// //   approvalHistory: any[] = [];

// //   constructor(
// //     private dataService: DataService,
// //     private rfqService: RFQService,
// //     private messageService: MessageService,
// //     private authService: AuthService,
// //     public router: Router,
// //     private cdr: ChangeDetectorRef
// //   ) {}

// //   ngOnInit(): void {
// //     this.initializeUser();
// //     this.loadPendingApprovals();
// //   }

// //   // ==================== INITIALIZE USER ====================

// //   private initializeUser(): void {
// //     this.userId = Number(localStorage.getItem('userId')) || 0;
// //     this.userName = localStorage.getItem('fullName') || 'User';
    
// //     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
// //     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
// //     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

// //     console.log('%c[PENDING APPROVALS INIT]', 'color: #00aa00; font-weight: bold;', {
// //       userId: this.userId,
// //       userName: this.userName,
// //       levelId: this.hierarchyLevelId,
// //       levelName: this.hierarchyLevelName,
// //       levelOrder: this.hierarchyLevelOrder
// //     });

// //     if (!this.hierarchyLevelId) {
// //       console.warn('%c[WARNING]', 'color: #ff9800;', 'No hierarchy level found - user may not have approval permissions');
// //       // Don't redirect - just show empty state
// //     }
// //   }

// //   // ==================== LOAD PENDING APPROVALS ====================

// //   loadPendingApprovals(): void {
// //     if (!this.userId) {
// //       console.error('%c[ERROR]', 'color: #cc0000;', 'No user ID found');
// //       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
// //       return;
// //     }

// //     this.isLoading = true;
// //     console.log('%c[LOADING PENDING APPROVALS]', 'color: #0066cc; font-weight: bold;', {
// //       userId: this.userId,
// //       endpoint: `api/dynamic-rfq-approval/pending/user/${this.userId}`
// //     });

// //     this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
// //       next: (response: any) => {
// //         console.log('%c[✅ PENDING APPROVALS RAW RESPONSE]', 'color: #00aa00; font-weight: bold;', response);

// //         try {
// //           // ✅ Handle multiple possible response structures
// //           if (response.success === true) {
// //             // Structure 1: { success: true, data: [...] }
// //             if (Array.isArray(response.data)) {
// //               this.pendingApprovals = response.data;
// //             }
// //             // Structure 2: { success: true, count: 5, data: [...] }
// //             else if (response.data && Array.isArray(response.data)) {
// //               this.pendingApprovals = response.data;
// //             }
// //             // Structure 3: Direct array
// //             else {
// //               this.pendingApprovals = [];
// //             }
// //           } 
// //           // Fallback: Direct array response
// //           else if (Array.isArray(response)) {
// //             this.pendingApprovals = response;
// //           }
// //           else {
// //             this.pendingApprovals = [];
// //           }

// //           this.filteredApprovals = [...this.pendingApprovals];

// //           console.log(`%c[✅ SUCCESS]`, 'color: #00aa00; font-weight: bold;', {
// //             count: this.pendingApprovals.length,
// //             approvals: this.pendingApprovals
// //           });

// //           if (this.pendingApprovals.length === 0) {
// //             console.log('%c[INFO]', 'color: #0066cc;', 'No pending approvals found');
// //           }
// //         } catch (parseError) {
// //           console.error('%c[PARSE ERROR]', 'color: #cc0000;', parseError);
// //           this.pendingApprovals = [];
// //           this.filteredApprovals = [];
// //         }

// //         this.isLoading = false;
// //         this.cdr.markForCheck();
// //       },
// //       error: (error: any) => {
// //         console.error('%c[ERROR LOADING PENDING APPROVALS]', 'color: #cc0000; font-weight: bold;', {
// //           status: error.status,
// //           statusText: error.statusText,
// //           message: error.error?.message || error.message,
// //           error: error
// //         });

// //         let errorMessage = 'Failed to load pending approvals';
        
// //         if (error.status === 500) {
// //           errorMessage = 'Server error - Please check backend logs';
// //         } else if (error.status === 404) {
// //           errorMessage = 'Endpoint not found - Please check API configuration';
// //         } else if (error.status === 403) {
// //           errorMessage = 'Access denied - Please check your permissions';
// //         } else if (error.error?.message) {
// //           errorMessage = error.error.message;
// //         }

// //         this.messageService.showMessage('error', 'Error', errorMessage);
        
// //         this.pendingApprovals = [];
// //         this.filteredApprovals = [];
// //         this.isLoading = false;
// //         this.cdr.markForCheck();
// //       }
// //     });
// //   }

// //   // ==================== SEARCH ====================

// //   onSearch(): void {
// //     const term = this.searchText.toLowerCase().trim();

// //     if (!term) {
// //       this.filteredApprovals = [...this.pendingApprovals];
// //     } else {
// //       this.filteredApprovals = this.pendingApprovals.filter((approval: any) =>
// //         (approval.rfqNumber || '').toLowerCase().includes(term) ||
// //         (approval.rfqTitle || '').toLowerCase().includes(term)
// //       );
// //     }

// //     this.cdr.markForCheck();
// //   }

// //   // ==================== VIEW RFQ DETAILS ====================

// //   viewRFQDetails(approval: any): void {
// //     console.log('%c[VIEW RFQ DETAILS]', 'color: #0066cc; font-weight: bold;', approval.rfqId);

// //     this.isLoading = true;

// //     Promise.all([
// //       this.rfqService.getRFQById(approval.rfqId).toPromise(),
// //       this.dataService.getApprovalHistory(approval.rfqId).toPromise()
// //     ]).then(([rfqResponse, historyResponse]: any[]) => {
// //       this.rfqDetails = rfqResponse?.data;
      
// //       // ✅ Handle approval history response structure
// //       if (historyResponse?.success && Array.isArray(historyResponse.data)) {
// //         this.approvalHistory = historyResponse.data;
// //       } else if (Array.isArray(historyResponse)) {
// //         this.approvalHistory = historyResponse;
// //       } else {
// //         this.approvalHistory = [];
// //       }
      
// //       this.showRFQDetails = true;
// //       this.isLoading = false;
// //       this.cdr.markForCheck();

// //       console.log('%c[✅ RFQ DETAILS LOADED]', 'color: #00aa00;', {
// //         rfq: this.rfqDetails?.rfqNumber,
// //         historyCount: this.approvalHistory.length
// //       });
// //     }).catch((error: any) => {
// //       console.error('%c[ERROR]', 'color: #cc0000;', error);
// //       this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
// //       this.isLoading = false;
// //       this.cdr.markForCheck();
// //     });
// //   }

// //   closeRFQDetails(): void {
// //     this.showRFQDetails = false;
// //     this.rfqDetails = null;
// //     this.approvalHistory = [];
// //     this.cdr.markForCheck();
// //   }

// //   // ==================== OPEN APPROVAL MODAL ====================

// //   openApprovalModal(rfq: any, action: 'approve' | 'reject' | 'resubmit'): void {
// //     this.selectedRFQ = rfq;
// //     this.actionType = action;
// //     this.actionComments = '';
// //     this.showApprovalModal = true;
// //     this.cdr.markForCheck();

// //     console.log('%c[OPEN APPROVAL MODAL]', 'color: #0066cc;', {
// //       rfq: rfq.rfqNumber,
// //       action: action
// //     });
// //   }

// //   closeApprovalModal(): void {
// //     this.showApprovalModal = false;
// //     this.selectedRFQ = null;
// //     this.actionComments = '';
// //     this.isSubmitting = false;
// //     this.cdr.markForCheck();
// //   }

// //   // ==================== SUBMIT APPROVAL ACTION ====================

// //   submitApprovalAction(): void {
// //     if (!this.selectedRFQ || !this.userId) {
// //       this.messageService.showMessage('error', 'Error', 'Invalid approval data');
// //       return;
// //     }

// //     // Validate comments for reject
// //     if (this.actionType === 'reject' && !this.actionComments.trim()) {
// //       this.messageService.showMessage('warning', 'Warning', 'Please provide rejection comments');
// //       return;
// //     }

// //     this.isSubmitting = true;
// //     const rfqId = this.selectedRFQ.rfqId;
// //     const comments = this.actionComments.trim() || 'No comments provided';

// //     console.log('%c[SUBMITTING APPROVAL ACTION]', 'color: #ff6600; font-weight: bold;', {
// //       action: this.actionType,
// //       rfqId: rfqId,
// //       userId: this.userId,
// //       comments: comments
// //     });

// //     let apiCall;

// //     if (this.actionType === 'approve') {
// //       apiCall = this.dataService.approveRFQDynamic(rfqId, this.userId, comments);
// //     } else if (this.actionType === 'reject') {
// //       apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
// //     } else {
// //       apiCall = this.dataService.resubmitRFQDynamic(rfqId, this.userId);
// //     }

// //     apiCall.subscribe({
// //       next: (response: any) => {
// //         console.log('%c[✅ ACTION SUCCESS]', 'color: #00aa00; font-weight: bold;', response);

// //         const actionName = this.actionType === 'approve' ? 'approved' : 
// //                           this.actionType === 'reject' ? 'rejected' : 'resubmitted';

// //         this.messageService.showMessage('success', 'Success', 
// //           `RFQ ${this.selectedRFQ.rfqNumber} ${actionName} successfully`);

// //         this.closeApprovalModal();
// //         this.loadPendingApprovals();
// //       },
// //       error: (error: any) => {
// //         console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);

// //         const errorMsg = error.error?.message || `Failed to ${this.actionType} RFQ`;
// //         this.messageService.showMessage('error', 'Error', errorMsg);

// //         this.isSubmitting = false;
// //         this.cdr.markForCheck();
// //       }
// //     });
// //   }

// //   // ==================== HELPERS ====================

// //   getActionButtonText(): string {
// //     if (this.actionType === 'approve') return 'Approve RFQ';
// //     if (this.actionType === 'reject') return 'Reject RFQ';
// //     return 'Resubmit RFQ';
// //   }

// //   getActionButtonClass(): string {
// //     if (this.actionType === 'approve') return 'btn-success';
// //     if (this.actionType === 'reject') return 'btn-danger';
// //     return 'btn-primary';
// //   }

// //   canResubmit(rfq: any): boolean {
// //     return rfq.status === 'REJECTED';
// //   }

// //   getStatusBadgeClass(status: string): string {
// //     const statusMap: { [key: string]: string } = {
// //       'PENDING': 'bg-warning text-dark',
// //       'APPROVED': 'bg-success',
// //       'REJECTED': 'bg-danger',
// //       'RESUBMITTED': 'bg-info',
// //       'SKIPPED': 'bg-secondary',
// //       'DRAFT': 'bg-secondary',
// //       'AWAITING_APPROVAL': 'bg-warning',
// //       'PUBLISHED': 'bg-info',
// //       'CLOSED': 'bg-dark'
// //     };
// //     return statusMap[status] || 'bg-light text-dark';
// //   }

// //   getLevelDisplayName(levelName: string): string {
// //     return levelName || 'Unknown Level';
// //   }

// //   formatDate(dateString: string | null): string {
// //     if (!dateString) return 'N/A';
// //     try {
// //       return new Date(dateString).toLocaleString('en-GB');
// //     } catch {
// //       return 'Invalid Date';
// //     }
// //   }

// //   // ==================== REFRESH ====================

// //   refresh(): void {
// //     this.searchText = '';
// //     this.loadPendingApprovals();
// //   }
// // }

// // ==================== FILE: src/app/views/base/pending-approvals/pending-approvals.component.ts ====================

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
//   hierarchyLevelId: number | null = null;
//   hierarchyLevelName: string = '';
//   hierarchyLevelOrder: number | null = null;

//   // ==================== DATA ====================
//   pendingApprovals: any[] = [];
//   filteredApprovals: any[] = [];
//   isLoading = false;

//   // ==================== SEARCH ====================
//   searchText = '';

//   // ==================== MODAL STATE ====================
//   showApprovalModal = false;
//   selectedRFQ: any = null;
//   actionType: 'approve' | 'reject' | 'resubmit' = 'approve';
//   actionComments = '';
//   isSubmitting = false;

//   // ==================== RFQ DETAILS ====================
//   showRFQDetails = false;
//   rfqDetails: any = null;
//   approvalHistory: any[] = [];

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
//     this.loadPendingApprovals();
//   }

//   // ==================== INITIALIZE USER ====================

//   private initializeUser(): void {
//     this.userId = Number(localStorage.getItem('userId')) || 0;
//     this.userName = localStorage.getItem('fullName') || 'User';
    
//     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

//     console.log('%c[PENDING APPROVALS INIT]', 'color: #00aa00; font-weight: bold;', {
//       userId: this.userId,
//       userName: this.userName,
//       levelId: this.hierarchyLevelId,
//       levelName: this.hierarchyLevelName,
//       levelOrder: this.hierarchyLevelOrder
//     });

//     if (!this.hierarchyLevelId) {
//       console.warn('%c[WARNING]', 'color: #ff9800;', 'No hierarchy level found - user may not have approval permissions');
//     }
//   }

//   // ==================== ✅ FIXED: LOAD PENDING APPROVALS ====================

//   loadPendingApprovals(): void {
//     if (!this.userId) {
//       console.error('%c[ERROR]', 'color: #cc0000;', 'No user ID found');
//       this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//       return;
//     }

//     this.isLoading = true;
//     console.log('%c[LOADING PENDING APPROVALS]', 'color: #0066cc; font-weight: bold;', {
//       userId: this.userId,
//       endpoint: `api/dynamic-rfq-approval/pending/user/${this.userId}`
//     });

//     this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
//       next: (response: any) => {
//         console.log('%c[✅ RAW RESPONSE]', 'color: #00aa00; font-weight: bold;', response);

//         try {
//           // ✅ Handle multiple response structures safely
//           let data: any[] = [];
          
//           if (response?.success === true) {
//             if (Array.isArray(response.data)) {
//               data = response.data;
//             } else if (response.data === null || response.data === undefined) {
//               data = [];
//             }
//           } else if (Array.isArray(response)) {
//             data = response;
//           } else if (response?.data && Array.isArray(response.data)) {
//             data = response.data;
//           }

//           this.pendingApprovals = data;
//           this.filteredApprovals = [...this.pendingApprovals];

//           console.log('%c[✅ PARSED SUCCESS]', 'color: #00aa00; font-weight: bold;', {
//             count: this.pendingApprovals.length,
//             sample: this.pendingApprovals.length > 0 ? this.pendingApprovals[0] : null
//           });

//           if (this.pendingApprovals.length === 0) {
//             console.log('%c[INFO]', 'color: #0066cc;', 'No pending approvals found');
//           }

//         } catch (parseError) {
//           console.error('%c[PARSE ERROR]', 'color: #cc0000; font-weight: bold;', parseError);
//           this.pendingApprovals = [];
//           this.filteredApprovals = [];
//           this.messageService.showMessage('error', 'Error', 'Failed to parse approval data');
//         }

//         this.isLoading = false;
//         this.cdr.markForCheck();
//       },
//       error: (error: any) => {
//         console.error('%c[✗ API ERROR]', 'color: #cc0000; font-weight: bold;', {
//           status: error.status,
//           statusText: error.statusText,
//           url: error.url,
//           message: error.error?.message || error.message,
//           fullError: error
//         });

//         let errorMessage = 'Failed to load pending approvals';
        
//         if (error.status === 500) {
//           errorMessage = 'Server error occurred. Please check backend logs or contact administrator.';
//         } else if (error.status === 404) {
//           errorMessage = 'API endpoint not found. Please verify API configuration.';
//         } else if (error.status === 403) {
//           errorMessage = 'Access denied. Please check your permissions.';
//         } else if (error.status === 0) {
//           errorMessage = 'Cannot connect to server. Please check if backend is running.';
//         } else if (error.error?.message) {
//           errorMessage = error.error.message;
//         }

//         this.messageService.showMessage('error', 'Error', errorMessage);
        
//         this.pendingApprovals = [];
//         this.filteredApprovals = [];
//         this.isLoading = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== SEARCH ====================

//   onSearch(): void {
//     const term = this.searchText.toLowerCase().trim();

//     if (!term) {
//       this.filteredApprovals = [...this.pendingApprovals];
//     } else {
//       this.filteredApprovals = this.pendingApprovals.filter((approval: any) =>
//         (approval.rfqNumber || '').toLowerCase().includes(term) ||
//         (approval.rfqTitle || '').toLowerCase().includes(term) ||
//         (approval.buyerName || '').toLowerCase().includes(term)
//       );
//     }

//     this.cdr.markForCheck();
//   }

//   // ==================== VIEW RFQ DETAILS ====================

//   viewRFQDetails(approval: any): void {
//     console.log('%c[VIEW RFQ DETAILS]', 'color: #0066cc; font-weight: bold;', approval.rfqId);

//     this.isLoading = true;

//     Promise.all([
//       this.rfqService.getRFQById(approval.rfqId).toPromise(),
//       this.dataService.getApprovalHistory(approval.rfqId).toPromise()
//     ]).then(([rfqResponse, historyResponse]: any[]) => {
//       this.rfqDetails = rfqResponse?.data;
      
//       if (historyResponse?.success && Array.isArray(historyResponse.data)) {
//         this.approvalHistory = historyResponse.data;
//       } else if (Array.isArray(historyResponse)) {
//         this.approvalHistory = historyResponse;
//       } else {
//         this.approvalHistory = [];
//       }
      
//       this.showRFQDetails = true;
//       this.isLoading = false;
//       this.cdr.markForCheck();

//       console.log('%c[✅ RFQ DETAILS LOADED]', 'color: #00aa00;', {
//         rfq: this.rfqDetails?.rfqNumber,
//         historyCount: this.approvalHistory.length
//       });
//     }).catch((error: any) => {
//       console.error('%c[ERROR]', 'color: #cc0000;', error);
//       this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
//       this.isLoading = false;
//       this.cdr.markForCheck();
//     });
//   }

//   closeRFQDetails(): void {
//     this.showRFQDetails = false;
//     this.rfqDetails = null;
//     this.approvalHistory = [];
//     this.cdr.markForCheck();
//   }

//   // ==================== OPEN APPROVAL MODAL ====================

//   openApprovalModal(rfq: any, action: 'approve' | 'reject' | 'resubmit'): void {
//     this.selectedRFQ = rfq;
//     this.actionType = action;
//     this.actionComments = '';
//     this.showApprovalModal = true;
//     this.cdr.markForCheck();

//     console.log('%c[OPEN APPROVAL MODAL]', 'color: #0066cc;', {
//       rfq: rfq.rfqNumber,
//       action: action
//     });
//   }

//   closeApprovalModal(): void {
//     this.showApprovalModal = false;
//     this.selectedRFQ = null;
//     this.actionComments = '';
//     this.isSubmitting = false;
//     this.cdr.markForCheck();
//   }

//   // ==================== SUBMIT APPROVAL ACTION ====================

//   submitApprovalAction(): void {
//     if (!this.selectedRFQ || !this.userId) {
//       this.messageService.showMessage('error', 'Error', 'Invalid approval data');
//       return;
//     }

//     if (this.actionType === 'reject' && !this.actionComments.trim()) {
//       this.messageService.showMessage('warning', 'Warning', 'Please provide rejection comments');
//       return;
//     }

//     this.isSubmitting = true;
//     const rfqId = this.selectedRFQ.rfqId;
//     const comments = this.actionComments.trim() || 'No comments provided';

//     console.log('%c[SUBMITTING APPROVAL ACTION]', 'color: #ff6600; font-weight: bold;', {
//       action: this.actionType,
//       rfqId: rfqId,
//       userId: this.userId,
//       comments: comments
//     });

//     let apiCall;

//     if (this.actionType === 'approve') {
//       apiCall = this.dataService.approveRFQDynamic(rfqId, this.userId, comments);
//     } else if (this.actionType === 'reject') {
//       apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
//     } else {
//       apiCall = this.dataService.resubmitRFQDynamic(rfqId, this.userId);
//     }

//     apiCall.subscribe({
//       next: (response: any) => {
//         console.log('%c[✅ ACTION SUCCESS]', 'color: #00aa00; font-weight: bold;', response);

//         const actionName = this.actionType === 'approve' ? 'approved' : 
//                           this.actionType === 'reject' ? 'rejected' : 'resubmitted';

//         this.messageService.showMessage('success', 'Success', 
//           `RFQ ${this.selectedRFQ.rfqNumber} ${actionName} successfully`);

//         this.closeApprovalModal();
//         this.loadPendingApprovals();
//       },
//       error: (error: any) => {
//         console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);

//         const errorMsg = error.error?.message || `Failed to ${this.actionType} RFQ`;
//         this.messageService.showMessage('error', 'Error', errorMsg);

//         this.isSubmitting = false;
//         this.cdr.markForCheck();
//       }
//     });
//   }

//   // ==================== HELPERS ====================

//   getActionButtonText(): string {
//     if (this.actionType === 'approve') return 'Approve RFQ';
//     if (this.actionType === 'reject') return 'Reject RFQ';
//     return 'Resubmit RFQ';
//   }

//   getActionButtonClass(): string {
//     if (this.actionType === 'approve') return 'btn-success';
//     if (this.actionType === 'reject') return 'btn-danger';
//     return 'btn-primary';
//   }

//   canResubmit(rfq: any): boolean {
//     return rfq.status === 'REJECTED';
//   }

//   getStatusBadgeClass(status: string): string {
//     const statusMap: { [key: string]: string } = {
//       'PENDING': 'bg-warning text-dark',
//       'APPROVED': 'bg-success',
//       'REJECTED': 'bg-danger',
//       'RESUBMITTED': 'bg-info',
//       'SKIPPED': 'bg-secondary',
//       'DRAFT': 'bg-secondary',
//       'AWAITING_APPROVAL': 'bg-warning',
//       'PUBLISHED': 'bg-info',
//       'CLOSED': 'bg-dark'
//     };
//     return statusMap[status] || 'bg-light text-dark';
//   }

//   getLevelDisplayName(levelName: string): string {
//     return levelName || 'Unknown Level';
//   }

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try {
//       return new Date(dateString).toLocaleString('en-GB');
//     } catch {
//       return 'Invalid Date';
//     }
//   }

//   // ==================== REFRESH ====================

//   refresh(): void {
//     this.searchText = '';
//     this.loadPendingApprovals();
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
  hierarchyLevelId: number | null = null;
  hierarchyLevelName: string = '';
  hierarchyLevelOrder: number | null = null;

  // ==================== DATA ====================
  pendingApprovals: any[] = [];
  filteredApprovals: any[] = [];
  isLoading = false;

  // ==================== SEARCH ====================
  searchText = '';

  // ==================== MODAL STATE ====================
  showApprovalModal = false;
  selectedRFQ: any = null;
  actionType: 'approve' | 'reject' | 'resubmit' = 'approve';
  actionComments = '';
  isSubmitting = false;

  // ==================== RFQ DETAILS ====================
  showRFQDetails = false;
  rfqDetails: any = null;
  approvalHistory: any[] = [];

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
    this.loadPendingApprovals();
  }

  // ==================== INITIALIZE USER ====================

  private initializeUser(): void {
    this.userId = Number(localStorage.getItem('userId')) || 0;
    this.userName = localStorage.getItem('fullName') || 'User';
    
    this.hierarchyLevelId = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

    console.log('%c[PENDING APPROVALS INIT]', 'color: #00aa00; font-weight: bold;', {
      userId: this.userId,
      userName: this.userName,
      levelId: this.hierarchyLevelId,
      levelName: this.hierarchyLevelName,
      levelOrder: this.hierarchyLevelOrder
    });

    if (!this.userId) {
      console.error('%c[ERROR]', 'color: #cc0000;', 'No user ID found');
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
      return;
    }

    if (!this.hierarchyLevelId) {
      console.warn('%c[WARNING]', 'color: #ff9800;', 'No hierarchy level found - user may not have approval permissions');
    }
  }

  // ==================== ✅ FIXED: LOAD PENDING APPROVALS ====================

  loadPendingApprovals(): void {
    if (!this.userId) {
      console.error('%c[ERROR]', 'color: #cc0000;', 'No user ID found');
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      return;
    }

    this.isLoading = true;
    console.log('%c[LOADING PENDING APPROVALS]', 'color: #0066cc; font-weight: bold;', {
      userId: this.userId,
      endpoint: `api/dynamic-rfq-approval/pending/user/${this.userId}`
    });

    this.dataService.getPendingApprovalsForUser(this.userId).subscribe({
      next: (response: any) => {
        console.log('%c[✅ RAW RESPONSE]', 'color: #00aa00; font-weight: bold;', response);

        try {
          // ✅ Handle multiple response structures safely
          let data: any[] = [];
          
          if (response?.success === true) {
            if (Array.isArray(response.data)) {
              data = response.data;
            } else if (response.data === null || response.data === undefined) {
              data = [];
            }
          } else if (Array.isArray(response)) {
            data = response;
          } else if (response?.data && Array.isArray(response.data)) {
            data = response.data;
          }

          this.pendingApprovals = data;
          this.filteredApprovals = [...this.pendingApprovals];

          console.log('%c[✅ PARSED SUCCESS]', 'color: #00aa00; font-weight: bold;', {
            count: this.pendingApprovals.length,
            sample: this.pendingApprovals.length > 0 ? this.pendingApprovals[0] : null
          });

          if (this.pendingApprovals.length === 0) {
            console.log('%c[INFO]', 'color: #0066cc;', 'No pending approvals found');
          }

        } catch (parseError) {
          console.error('%c[PARSE ERROR]', 'color: #cc0000; font-weight: bold;', parseError);
          this.pendingApprovals = [];
          this.filteredApprovals = [];
          this.messageService.showMessage('error', 'Error', 'Failed to parse approval data');
        }

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('%c[✗ API ERROR]', 'color: #cc0000; font-weight: bold;', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.error?.message || error.message,
          fullError: error
        });

        let errorMessage = 'Failed to load pending approvals';
        
        if (error.status === 500) {
          errorMessage = 'Server error occurred. Please check backend logs or contact administrator.';
        } else if (error.status === 404) {
          errorMessage = 'API endpoint not found. Please verify API configuration.';
        } else if (error.status === 403) {
          errorMessage = 'Access denied. Please check your permissions.';
        } else if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if backend is running.';
        } else if (error.error?.message) {
          errorMessage = error.error.message;
        }

        this.messageService.showMessage('error', 'Error', errorMessage);
        
        this.pendingApprovals = [];
        this.filteredApprovals = [];
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // ==================== SEARCH ====================

  onSearch(): void {
    const term = this.searchText.toLowerCase().trim();

    if (!term) {
      this.filteredApprovals = [...this.pendingApprovals];
    } else {
      this.filteredApprovals = this.pendingApprovals.filter((approval: any) =>
        (approval.rfqNumber || '').toLowerCase().includes(term) ||
        (approval.rfqTitle || '').toLowerCase().includes(term) ||
        (approval.buyerName || '').toLowerCase().includes(term)
      );
    }

    this.cdr.markForCheck();
  }

  // ==================== VIEW RFQ DETAILS ====================

  viewRFQDetails(approval: any): void {
    console.log('%c[VIEW RFQ DETAILS]', 'color: #0066cc; font-weight: bold;', approval.rfqId);

    this.isLoading = true;

    Promise.all([
      this.rfqService.getRFQById(approval.rfqId).toPromise(),
      this.dataService.getApprovalHistory(approval.rfqId).toPromise()
    ]).then(([rfqResponse, historyResponse]: any[]) => {
      this.rfqDetails = rfqResponse?.data;
      
      if (historyResponse?.success && Array.isArray(historyResponse.data)) {
        this.approvalHistory = historyResponse.data;
      } else if (Array.isArray(historyResponse)) {
        this.approvalHistory = historyResponse;
      } else if (historyResponse?.data && Array.isArray(historyResponse.data)) {
        this.approvalHistory = historyResponse.data;
      } else {
        this.approvalHistory = [];
      }
      
      this.showRFQDetails = true;
      this.isLoading = false;
      this.cdr.markForCheck();

      console.log('%c[✅ RFQ DETAILS LOADED]', 'color: #00aa00;', {
        rfq: this.rfqDetails?.rfqNumber,
        historyCount: this.approvalHistory.length
      });
    }).catch((error: any) => {
      console.error('%c[ERROR]', 'color: #cc0000;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
      this.isLoading = false;
      this.cdr.markForCheck();
    });
  }

  closeRFQDetails(): void {
    this.showRFQDetails = false;
    this.rfqDetails = null;
    this.approvalHistory = [];
    this.cdr.markForCheck();
  }

  // ==================== OPEN APPROVAL MODAL ====================

  openApprovalModal(rfq: any, action: 'approve' | 'reject' | 'resubmit'): void {
    this.selectedRFQ = rfq;
    this.actionType = action;
    this.actionComments = '';
    this.showApprovalModal = true;
    this.cdr.markForCheck();

    console.log('%c[OPEN APPROVAL MODAL]', 'color: #0066cc;', {
      rfq: rfq.rfqNumber,
      action: action
    });
  }

  closeApprovalModal(): void {
    this.showApprovalModal = false;
    this.selectedRFQ = null;
    this.actionComments = '';
    this.isSubmitting = false;
    this.cdr.markForCheck();
  }

  // ==================== SUBMIT APPROVAL ACTION ====================

  submitApprovalAction(): void {
    if (!this.selectedRFQ || !this.userId) {
      this.messageService.showMessage('error', 'Error', 'Invalid approval data');
      return;
    }

    if (this.actionType === 'reject' && !this.actionComments.trim()) {
      this.messageService.showMessage('warning', 'Warning', 'Please provide rejection comments');
      return;
    }

    this.isSubmitting = true;
    const rfqId = this.selectedRFQ.rfqId;
    const comments = this.actionComments.trim() || 'No comments provided';

    console.log('%c[SUBMITTING APPROVAL ACTION]', 'color: #ff6600; font-weight: bold;', {
      action: this.actionType,
      rfqId: rfqId,
      userId: this.userId,
      comments: comments
    });

    let apiCall;

    if (this.actionType === 'approve') {
      apiCall = this.dataService.approveRFQDynamic(rfqId, this.userId, comments);
    } else if (this.actionType === 'reject') {
      apiCall = this.dataService.rejectRFQDynamic(rfqId, this.userId, comments);
    } else {
      apiCall = this.dataService.resubmitRFQDynamic(rfqId, this.userId);
    }

    apiCall.subscribe({
      next: (response: any) => {
        console.log('%c[✅ ACTION SUCCESS]', 'color: #00aa00; font-weight: bold;', response);

        const actionName = this.actionType === 'approve' ? 'approved' : 
                          this.actionType === 'reject' ? 'rejected' : 'resubmitted';

        this.messageService.showMessage('success', 'Success', 
          `RFQ ${this.selectedRFQ.rfqNumber} ${actionName} successfully`);

        this.closeApprovalModal();
        this.loadPendingApprovals();
      },
      error: (error: any) => {
        console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);

        const errorMsg = error.error?.message || `Failed to ${this.actionType} RFQ`;
        this.messageService.showMessage('error', 'Error', errorMsg);

        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }

  // ==================== HELPERS ====================

  getActionButtonText(): string {
    if (this.actionType === 'approve') return 'Approve RFQ';
    if (this.actionType === 'reject') return 'Reject RFQ';
    return 'Resubmit RFQ';
  }

  getActionButtonClass(): string {
    if (this.actionType === 'approve') return 'btn-success';
    if (this.actionType === 'reject') return 'btn-danger';
    return 'btn-primary';
  }

  canResubmit(rfq: any): boolean {
    return rfq.status === 'REJECTED';
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'bg-warning text-dark',
      'APPROVED': 'bg-success',
      'REJECTED': 'bg-danger',
      'RESUBMITTED': 'bg-info',
      'SKIPPED': 'bg-secondary',
      'DRAFT': 'bg-secondary',
      'AWAITING_APPROVAL': 'bg-warning',
      'PUBLISHED': 'bg-info',
      'CLOSED': 'bg-dark'
    };
    return statusMap[status] || 'bg-light text-dark';
  }

  getLevelDisplayName(levelName: string): string {
    return levelName || 'Unknown Level';
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleString('en-GB');
    } catch {
      return 'Invalid Date';
    }
  }

  // ==================== REFRESH ====================

  refresh(): void {
    this.searchText = '';
    this.loadPendingApprovals();
  }
}