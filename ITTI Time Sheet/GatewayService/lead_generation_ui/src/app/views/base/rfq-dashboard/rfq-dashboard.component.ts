

// // import { Component, OnInit } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormsModule } from '@angular/forms';
// // import { Router } from '@angular/router';
// // import { RFQService } from '../models/rfq.service';
// // import { MessageService } from '../../../shared/service/message.service';
// // import { RFQ } from '../models/rfq.model';
// // import { BuyerService } from '../dashboard/buyer-b.service';
// // import { StorageService } from '../../../shared/service/StorageService';
// // import { RfqApprovalService } from '../models/rfq-approval.service';
// // import { DataService } from '../../../shared/service/DataService';

// // @Component({
// //   selector: 'app-rfq-dashboard',
// //   standalone: true,
// //   imports: [CommonModule, FormsModule],
// //   templateUrl: './rfq-dashboard.component.html',
// //   styleUrls: ['./rfq-dashboard.component.css']
// // })
// // export class RFQDashboardComponent implements OnInit {

// //   // ==================== DATA ====================
// //   rfqs: RFQ[] = [];
// //   filteredRFQs: any[] = [];
// //   pagedRFQs: any[] = [];
  
// //   // ==================== LOGGED-IN BUYER ====================
// //   loggedInBuyerId: number | null = null;
  
// //   // ==================== SEARCH & PAGINATION ====================
// //   searchText = '';
// //   currentPage = 1;
// //   pageSize = 5;
  
// //   // ==================== FILTERS ====================
// //   statusFilter = '';
// //   priorityFilter = '';
// //   buyerFilter = '';
  
// //   // ==================== UI STATE ====================
// //   isLoading = false;
// //   Math = Math;
  
// //   // ==================== DROPDOWNS ====================
// //   statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED'];
// //   priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
// //   buyers: any[] = [];

// // constructor(
// //   private rfqService: RFQService,
// //   private buyerService: BuyerService,
// //   private messageService: MessageService,
// //   private dataService: DataService,
// //   public router: Router,
// //   private approvalService: RfqApprovalService  
// // ) {}

// //   ngOnInit(): void {
// //     this.getLoggedInBuyerId();
// //     this.loadRFQs();
// //   }

// //   // ==================== GET LOGGED-IN BUYER ID ====================

// //   /**
// //    * ✅ FIX: Get the logged-in buyer ID from storage
// //    */
// //   private getLoggedInBuyerId(): void {
// //     try {
// //       // Get buyer details from storage
// //       const buyerData = StorageService.getBuyerDetails();
      
// //       console.log('%c[STORAGE BUYER DATA]', 'color: #0066cc; font-weight: bold;', buyerData);

// //       // Extract buyer ID from different possible sources
// //       let buyerId: number | null = null;

// //       if (buyerData?.id && buyerData.id !== 'N/A') {
// //         buyerId = Number(buyerData.id);
// //       } else if (buyerData?.buyerId) {
// //         buyerId = Number(buyerData.buyerId);
// //       } else {
// //         // Fallback to localStorage
// //         const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
// //         if (storedId && storedId !== 'N/A') {
// //           buyerId = Number(storedId);
// //         }
// //       }

// //       if (!buyerId) {
// //         console.error('%c[ERROR] No buyer ID found', 'color: #cc0000; font-weight: bold;');
// //         this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
// //         this.router.navigate(['/buyer-login']);
// //         return;
// //       }

// //       this.loggedInBuyerId = buyerId;
// //       console.log('%c[✅ LOGGED-IN BUYER ID]', 'color: #00aa00; font-weight: bold;', this.loggedInBuyerId);
      
// //     } catch (error) {
// //       console.error('%c[ERROR] Exception in getLoggedInBuyerId', 'color: #cc0000;', error);
// //       this.messageService.showMessage('error', 'Error', 'Failed to get buyer details');
// //       this.router.navigate(['/buyer-login']);
// //     }
// //   }

// //   // ==================== LOAD DATA ====================

// //   /**
// //    * ✅ FIX: Load only RFQs for the logged-in buyer
// //    */
// //   loadRFQs(): void {
// //     if (!this.loggedInBuyerId) {
// //       console.error('%c[ERROR] Cannot load RFQs - No buyer ID', 'color: #cc0000;');
// //       this.messageService.showMessage('error', 'Error', 'Buyer ID not found');
// //       return;
// //     }

// //     this.isLoading = true;
// //     console.log('%c[LOAD RFQs FOR BUYER]', 'color: #ff6600; font-weight: bold;', this.loggedInBuyerId);
    
// //     // ✅ Use getRFQsByBuyer instead of getAllRFQs
// //     this.rfqService.getRFQsByBuyer(this.loggedInBuyerId).subscribe({
// //       next: (response: any) => {
// //         console.log('%c[SUCCESS]', 'color: #00aa00; font-weight: bold;', response.count || response.data?.length || 0);
// //         this.rfqs = response.data || [];
        
// //         console.log('%c[RFQs LOADED]', 'color: #0066cc;', {
// //           count: this.rfqs.length,
// //           buyerId: this.loggedInBuyerId,
// //           statuses: this.rfqs.map((r: any) => r.status)
// //         });
        
// //         this.applyFiltersAndPagination();
// //         this.isLoading = false;
// //       },
// //       error: (error: any) => {
// //         console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);
// //         this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
// //         this.isLoading = false;
// //       }
// //     });
// //   }

// //   /**
// //    * Load buyers (optional - for admin view)
// //    */
// //   loadBuyers(): void {
// //     this.buyerService.getAllBuyers().subscribe({
// //       next: (response: any) => {
// //         if (Array.isArray(response)) {
// //           this.buyers = response;
// //         } else if (response && typeof response === 'object' && 'data' in response) {
// //           const data = (response as any).data;
// //           if (Array.isArray(data)) {
// //             this.buyers = data;
// //           }
// //         }
// //         console.log('%c[✓] Buyers Loaded:', 'color: #00aa00;', this.buyers.length);
// //       },
// //       error: (error: any) => {
// //         console.error('Failed to load buyers:', error);
// //       }
// //     });
// //   }

// //   // ==================== FILTER & TRANSFORM DATA ====================

// //   /**
// //    * Transform RFQ data for display in table
// //    */
// //   private transformRFQDataForDisplay(rfqs: RFQ[]): any[] {
// //     return rfqs.map((rfq: any) => {
// //       // Ensure status is properly extracted
// //       const status = rfq.status || 'DRAFT';
// //       const priority = rfq.priority || 'MEDIUM';
      
// //       return {
// //         id: rfq.id,
// //         rfqNumber: rfq.rfqNumber || 'N/A',
// //         rfqTitle: rfq.rfqTitle || 'N/A',
// //         status: status,
// //         priority: priority,
// //         buyerName: rfq.buyer?.companyName || 'N/A',
// //         locationName: rfq.location?.locationName || 'N/A',
// //         itemsCount: rfq.items?.length || 0,
// //         suppliersCount: rfq.selectedSuppliers?.length || 0,
// //         dueDate: rfq.dueDate ? new Date(rfq.dueDate).toLocaleDateString('en-GB') : 'N/A',
// //         createdAt: rfq.createdAt ? new Date(rfq.createdAt).toLocaleDateString('en-GB') : 'N/A',
// //         approvalStatus: rfq.approvalStatus || 'PENDING',
// //         original: rfq
// //       };
// //     });
// //   }

// //   /**
// //    * Apply all filters and pagination
// //    */
// //   applyFiltersAndPagination(): void {
// //     console.log('%c[APPLY FILTERS]', 'color: #0066cc;', {
// //       search: this.searchText,
// //       status: this.statusFilter,
// //       priority: this.priorityFilter,
// //       buyerId: this.loggedInBuyerId
// //     });

// //     let data = this.transformRFQDataForDisplay(this.rfqs);

// //     console.log('%c[TRANSFORMED DATA]', 'color: #0066cc;', {
// //       count: data.length,
// //       statuses: data.map(r => r.status)
// //     });

// //     // Apply search
// //     if (this.searchText.trim()) {
// //       const term = this.searchText.toLowerCase();
// //       data = data.filter((row: any) =>
// //         (row.rfqNumber || '').toLowerCase().includes(term) ||
// //         (row.rfqTitle || '').toLowerCase().includes(term) ||
// //         (row.buyerName || '').toLowerCase().includes(term) ||
// //         (row.locationName || '').toLowerCase().includes(term)
// //       );
// //       console.log('%c[SEARCH FILTER]', 'color: #0066cc;', 'Results:', data.length);
// //     }

// //     // Apply status filter
// //     if (this.statusFilter && this.statusFilter.trim() !== '') {
// //       console.log('%c[STATUS FILTER]', 'color: #0066cc;', 'Filtering by:', this.statusFilter);
// //       const beforeFilter = data.length;
// //       data = data.filter((row: any) => {
// //         const rowStatus = row.status;
// //         const matches = rowStatus === this.statusFilter;
// //         return matches;
// //       });
// //       console.log('%c[STATUS FILTER RESULT]', 'color: #00aa00;', {
// //         before: beforeFilter,
// //         after: data.length
// //       });
// //     }

// //     // Apply priority filter
// //     if (this.priorityFilter && this.priorityFilter.trim() !== '') {
// //       console.log('%c[PRIORITY FILTER]', 'color: #0066cc;', 'Filtering by:', this.priorityFilter);
// //       data = data.filter((row: any) => row.priority === this.priorityFilter);
// //     }

// //     this.filteredRFQs = data;
// //     console.log('%c[FILTERED RESULTS]', 'color: #00aa00;', {
// //       total: this.filteredRFQs.length,
// //       buyerId: this.loggedInBuyerId
// //     });

// //     this.currentPage = 1;
// //     this.updatePagination();
// //   }

// //   /**
// //    * Update pagination for current page
// //    */
// //   updatePagination(): void {
// //     const start = (this.currentPage - 1) * this.pageSize;
// //     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
// //     console.log('%c[PAGINATION]', 'color: #0066cc;', {
// //       page: this.currentPage,
// //       pageSize: this.pageSize,
// //       displayed: this.pagedRFQs.length,
// //       total: this.filteredRFQs.length
// //     });
// //   }

// //   // ==================== ACTIONS ====================

// //   viewRFQ(rfq: any): void {
// //     console.log('%c[VIEW RFQ]', 'color: #0066cc;', rfq.rfqNumber);
// //     this.router.navigate([`/create-rfq/${rfq.id}/view`]);
// //   }

// //   editRFQ(rfq: any): void {
// //     if (rfq.status !== 'DRAFT') {
// //       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited');
// //       return;
// //     }
// //     console.log('%c[EDIT RFQ]', 'color: #0066cc;', rfq.rfqNumber);
// //     this.router.navigate([`/create-rfq/${rfq.id}/edit`]);
// //   }

// //   deleteRFQ(rfq: any): void {
// //     // ✅ Additional check: Ensure user can only delete their own RFQs
// //     if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
// //       this.messageService.showMessage('error', 'Error', 'You can only delete your own RFQs');
// //       return;
// //     }

// //     const confirmMessage = `Are you sure you want to delete RFQ ${rfq.rfqNumber}?\n\nTitle: ${rfq.rfqTitle}\n\nThis action cannot be undone.`;

// //     if (!confirm(confirmMessage)) {
// //       return;
// //     }

// //     this.rfqService.deleteRFQ(rfq.id).subscribe({
// //       next: () => {
// //         this.loadRFQs();
// //         this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
// //       },
// //       error: (err: any) => {
// //         console.error('Delete error:', err);
// //         this.messageService.showMessage('error', 'Error', 'Failed to delete RFQ');
// //       }
// //     });
// //   }
// // publishRFQ(rfq: any): void {
// //   // ✅ Additional check: Ensure user can only publish their own RFQs
// //   if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
// //     this.messageService.showMessage('error', 'Error', 'You can only publish your own RFQs');
// //     return;
// //   }

// //   if (rfq.status !== 'DRAFT') {
// //     this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be published');
// //     return;
// //   }

// //   const confirmMsg = `Submit RFQ ${rfq.rfqNumber} for approval?\n\nThis will start the dynamic approval workflow based on your company's hierarchy.\n\nAfter all approvals are complete, it will be published to suppliers.`;
// //   if (!confirm(confirmMsg)) return;

// //   console.log('%c[INITIATING DYNAMIC APPROVAL WORKFLOW]', 'color: #ff6600; font-weight: bold;', rfq.rfqNumber);

// //   // ✅ FIX: Get buyer ID and use it as creator ID
// //   // Since buyers don't have hierarchy user IDs, backend will detect this is a buyer
// //   const buyerId = this.loggedInBuyerId;
// //   const storedUserId = localStorage.getItem('userId');
// //   const creatorUserId = storedUserId ? Number(storedUserId) : (buyerId || 0);

// //   console.log('%c[CREATOR INFO]', 'color: #0066cc;', {
// //     buyerId: buyerId,
// //     storedUserId: storedUserId,
// //     creatorUserId: creatorUserId
// //   });

// //   // ✅ Additional validation: Ensure we have a valid user ID
// //   if (!creatorUserId || creatorUserId === 0) {
// //     this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
// //     console.error('%c[ERROR] Invalid user ID', 'color: #cc0000; font-weight: bold;', {
// //       storedUserId: storedUserId,
// //       loggedInBuyerId: this.loggedInBuyerId,
// //       creatorUserId: creatorUserId
// //     });
// //     return;
// //   }

// //   // Step 1: Initiate dynamic approval workflow
// //   this.dataService.initiateApprovalWorkflow(rfq.id, creatorUserId).subscribe({
// //     next: (response: any) => {
// //       console.log('%c[✅ DYNAMIC APPROVAL WORKFLOW INITIATED]', 'color: #00aa00; font-weight: bold;', response);

// //       this.messageService.showMessage('success', 'Success', 
// //         `RFQ ${rfq.rfqNumber} submitted for approval!\n\nWorkflow started based on your company hierarchy.`);

// //       this.loadRFQs();
// //     },
// //     error: (err: any) => {
// //       console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', err);
// //       this.messageService.showMessage('error', 'Error', 
// //         err.error?.message || 'Failed to initiate approval workflow');
// //     }
// //   });
// // }

// //   closeRFQ(rfq: any): void {
// //     // ✅ Additional check: Ensure user can only close their own RFQs
// //     if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
// //       this.messageService.showMessage('error', 'Error', 'You can only close your own RFQs');
// //       return;
// //     }

// //     const confirmMsg = `Close RFQ ${rfq.rfqNumber}? This RFQ will no longer accept responses.`;
// //     if (!confirm(confirmMsg)) return;

// //     this.rfqService.closeRFQ(rfq.id).subscribe({
// //       next: () => {
// //         this.loadRFQs();
// //         this.messageService.showMessage('success', 'Success', 'RFQ closed successfully');
// //       },
// //       error: (err: any) => {
// //         console.error('Close error:', err);
// //         this.messageService.showMessage('error', 'Error', 'Failed to close RFQ');
// //       }
// //     });
// //   }

// //   // ==================== PAGINATION ====================

// //   get totalPages(): number {
// //     return Math.ceil(this.filteredRFQs.length / this.pageSize);
// //   }

// //   onPageChange(page: number): void {
// //     if (page >= 1 && page <= this.totalPages) {
// //       this.currentPage = page;
// //       this.updatePagination();
// //     }
// //   }

// //   onPageSizeChange(size: number): void {
// //     this.pageSize = size;
// //     this.currentPage = 1;
// //     this.updatePagination();
// //   }

// //   // ==================== STATUS STYLING ====================

// //   getStatusClass(status: string): string {
// //     const statusMap: { [key: string]: string } = {
// //       'DRAFT': 'bg-secondary',
// //       'AWAITING_APPROVAL': 'bg-warning',
// //       'PUBLISHED': 'bg-info',
// //       'RESPONSES_RECEIVED': 'bg-success',
// //       'CLOSED': 'bg-danger',
// //       'CANCELLED': 'bg-dark'
// //     };
// //     return statusMap[status] || 'bg-light';
// //   }

// //   getPriorityClass(priority: string): string {
// //     const priorityMap: { [key: string]: string } = {
// //       'LOW': 'bg-light text-dark',
// //       'MEDIUM': 'bg-info text-white',
// //       'HIGH': 'bg-warning text-dark',
// //       'URGENT': 'bg-danger text-white'
// //     };
// //     return priorityMap[priority] || 'bg-light text-dark';
// //   }

// //   getApprovalStatusClass(status: string): string {
// //     const statusMap: { [key: string]: string } = {
// //       'PENDING': 'text-warning',
// //       'APPROVED': 'text-success',
// //       'REJECTED': 'text-danger'
// //     };
// //     return statusMap[status] || 'text-muted';
// //   }

// //   // ==================== HELPERS ====================

// //   getStatusLabel(status: string): string {
// //     return status.replace(/_/g, ' ');
// //   }

// //   canEdit(rfq: any): boolean {
// //     return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
// //   }

// //   canPublish(rfq: any): boolean {
// //     return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
// //   }

// //   canClose(rfq: any): boolean {
// //     return ['PUBLISHED', 'RESPONSES_RECEIVED'].includes(rfq.status) && 
// //            rfq.original.buyer?.id === this.loggedInBuyerId;
// //   }

// //   // ==================== RESET FILTERS ====================

// //   resetFilters(): void {
// //     this.searchText = '';
// //     this.statusFilter = '';
// //     this.priorityFilter = '';
// //     this.buyerFilter = '';
// //     this.currentPage = 1;
// //     this.applyFiltersAndPagination();
// //   }
// // }


//   import { Component, OnInit } from '@angular/core';
//   import { CommonModule } from '@angular/common';
//   import { FormsModule } from '@angular/forms';
//   import { Router } from '@angular/router';
//   import { RFQService } from '../models/rfq.service';
//   import { MessageService } from '../../../shared/service/message.service';
//   import { RFQ } from '../models/rfq.model';
//   import { BuyerService } from '../dashboard/buyer-b.service';
//   import { StorageService } from '../../../shared/service/StorageService';
//   import { RfqApprovalService } from '../models/rfq-approval.service';
//   import { DataService } from '../../../shared/service/DataService';

//   @Component({
//     selector: 'app-rfq-dashboard',
//     standalone: true,
//     imports: [CommonModule, FormsModule],
//     templateUrl: './rfq-dashboard.component.html',
//     styleUrls: ['./rfq-dashboard.component.css']
//   })
//   export class RFQDashboardComponent implements OnInit {

//     // ==================== DATA ====================
//     rfqs: RFQ[] = [];
//     filteredRFQs: any[] = [];
//     pagedRFQs: any[] = [];
    
//     // ==================== LOGGED-IN BUYER ====================
//     loggedInBuyerId: number | null = null;
    
//     // ==================== SEARCH & PAGINATION ====================
//     searchText = '';
//     currentPage = 1;
//     pageSize = 5;
    
//     // ==================== FILTERS ====================
//     statusFilter = '';
//     priorityFilter = '';
//     buyerFilter = '';
    
//     // ==================== UI STATE ====================
//     isLoading = false;
//     Math = Math;
    
//     // ==================== DROPDOWNS ====================
//     statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED'];
//     priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
//     buyers: any[] = [];

//   constructor(
//     private rfqService: RFQService,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private dataService: DataService,
//     public router: Router,
//     private approvalService: RfqApprovalService  
//   ) {}

//     ngOnInit(): void {
//       this.getLoggedInBuyerId();
//       this.loadRFQs();
//     }

//     // ==================== GET LOGGED-IN BUYER ID ====================

//     /**
//      * ✅ FIX: Get the logged-in buyer ID from storage
//      */
//     private getLoggedInBuyerId(): void {
//       try {
//         // Get buyer details from storage
//         const buyerData = StorageService.getBuyerDetails();
        
//         console.log('%c[STORAGE BUYER DATA]', 'color: #0066cc; font-weight: bold;', buyerData);

//         // Extract buyer ID from different possible sources
//         let buyerId: number | null = null;

//         if (buyerData?.id && buyerData.id !== 'N/A') {
//           buyerId = Number(buyerData.id);
//         } else if (buyerData?.buyerId) {
//           buyerId = Number(buyerData.buyerId);
//         } else {
//           // Fallback to localStorage
//           const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
//           if (storedId && storedId !== 'N/A') {
//             buyerId = Number(storedId);
//           }
//         }

//         if (!buyerId) {
//           console.error('%c[ERROR] No buyer ID found', 'color: #cc0000; font-weight: bold;');
//           this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
//           this.router.navigate(['/login']);
//           return;
//         }

//         this.loggedInBuyerId = buyerId;
//         console.log('%c[✅ LOGGED-IN BUYER ID]', 'color: #00aa00; font-weight: bold;', this.loggedInBuyerId);
        
//       } catch (error) {
//         console.error('%c[ERROR] Exception in getLoggedInBuyerId', 'color: #cc0000;', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to get buyer details');
//         this.router.navigate(['/login']);
//       }
//     }

//     // ==================== LOAD DATA ====================

//     /**
//      * ✅ FIX: Load only RFQs for the logged-in buyer
//      */
//     loadRFQs(): void {
//       if (!this.loggedInBuyerId) {
//         console.error('%c[ERROR] Cannot load RFQs - No buyer ID', 'color: #cc0000;');
//         this.messageService.showMessage('error', 'Error', 'Buyer ID not found');
//         return;
//       }

//       this.isLoading = true;
//       console.log('%c[LOAD RFQs FOR BUYER]', 'color: #ff6600; font-weight: bold;', this.loggedInBuyerId);
      
//       // ✅ Use getRFQsByBuyer instead of getAllRFQs
//       this.rfqService.getRFQsByBuyer(this.loggedInBuyerId).subscribe({
//         next: (response: any) => {
//           console.log('%c[SUCCESS]', 'color: #00aa00; font-weight: bold;', response.count || response.data?.length || 0);
//           this.rfqs = response.data || [];
          
//           console.log('%c[RFQs LOADED]', 'color: #0066cc;', {
//             count: this.rfqs.length,
//             buyerId: this.loggedInBuyerId,
//             statuses: this.rfqs.map((r: any) => r.status)
//           });
          
//           this.applyFiltersAndPagination();
//           this.isLoading = false;
//         },
//         error: (error: any) => {
//           console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);
//           this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//           this.isLoading = false;
//         }
//       });
//     }

//     /**
//      * Load buyers (optional - for admin view)
//      */
//     loadBuyers(): void {
//       this.buyerService.getBuyersByOrganizationAdmin().subscribe({
//         next: (response: any) => {
//           if (Array.isArray(response)) {
//             this.buyers = response;
//           } else if (response && typeof response === 'object' && 'data' in response) {
//             const data = (response as any).data;
//             if (Array.isArray(data)) {
//               this.buyers = data;
//             }
//           }
//           console.log('%c[✓] Buyers Loaded:', 'color: #00aa00;', this.buyers.length);
//         },
//         error: (error: any) => {
//           console.error('Failed to load buyers:', error);
//         }
//       });
//     }

//     // ==================== FILTER & TRANSFORM DATA ====================

//     /**
//      * Transform RFQ data for display in table
//      */
//     private transformRFQDataForDisplay(rfqs: RFQ[]): any[] {
//       return rfqs.map((rfq: any) => {
//         // Ensure status is properly extracted
//         const status = rfq.status || 'DRAFT';
//         const priority = rfq.priority || 'MEDIUM';
        
//         return {
//           id: rfq.id,
//           rfqNumber: rfq.rfqNumber || 'N/A',
//           rfqTitle: rfq.rfqTitle || 'N/A',
//           status: status,
//           priority: priority,
//           buyerName: rfq.buyer?.companyName || 'N/A',
//           locationName: rfq.location?.locationName || 'N/A',
//           itemsCount: rfq.items?.length || 0,
//           suppliersCount: rfq.selectedSuppliers?.length || 0,
//           dueDate: rfq.dueDate ? new Date(rfq.dueDate).toLocaleDateString('en-GB') : 'N/A',
//           createdAt: rfq.createdAt ? new Date(rfq.createdAt).toLocaleDateString('en-GB') : 'N/A',
//           approvalStatus: rfq.approvalStatus || 'PENDING',
//           original: rfq
//         };
//       });
//     }

//     /**
//      * Apply all filters and pagination
//      */
//     applyFiltersAndPagination(): void {
//       console.log('%c[APPLY FILTERS]', 'color: #0066cc;', {
//         search: this.searchText,
//         status: this.statusFilter,
//         priority: this.priorityFilter,
//         buyerId: this.loggedInBuyerId
//       });

//       let data = this.transformRFQDataForDisplay(this.rfqs);

//       console.log('%c[TRANSFORMED DATA]', 'color: #0066cc;', {
//         count: data.length,
//         statuses: data.map(r => r.status)
//       });

//       // Apply search
//       if (this.searchText.trim()) {
//         const term = this.searchText.toLowerCase();
//         data = data.filter((row: any) =>
//           (row.rfqNumber || '').toLowerCase().includes(term) ||
//           (row.rfqTitle || '').toLowerCase().includes(term) ||
//           (row.buyerName || '').toLowerCase().includes(term) ||
//           (row.locationName || '').toLowerCase().includes(term)
//         );
//         console.log('%c[SEARCH FILTER]', 'color: #0066cc;', 'Results:', data.length);
//       }

//       // Apply status filter
//       if (this.statusFilter && this.statusFilter.trim() !== '') {
//         console.log('%c[STATUS FILTER]', 'color: #0066cc;', 'Filtering by:', this.statusFilter);
//         const beforeFilter = data.length;
//         data = data.filter((row: any) => {
//           const rowStatus = row.status;
//           const matches = rowStatus === this.statusFilter;
//           return matches;
//         });
//         console.log('%c[STATUS FILTER RESULT]', 'color: #00aa00;', {
//           before: beforeFilter,
//           after: data.length
//         });
//       }

//       // Apply priority filter
//       if (this.priorityFilter && this.priorityFilter.trim() !== '') {
//         console.log('%c[PRIORITY FILTER]', 'color: #0066cc;', 'Filtering by:', this.priorityFilter);
//         data = data.filter((row: any) => row.priority === this.priorityFilter);
//       }

//       this.filteredRFQs = data;
//       console.log('%c[FILTERED RESULTS]', 'color: #00aa00;', {
//         total: this.filteredRFQs.length,
//         buyerId: this.loggedInBuyerId
//       });

//       this.currentPage = 1;
//       this.updatePagination();
//     }

//     /**
//      * Update pagination for current page
//      */
//     updatePagination(): void {
//       const start = (this.currentPage - 1) * this.pageSize;
//       this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
//       console.log('%c[PAGINATION]', 'color: #0066cc;', {
//         page: this.currentPage,
//         pageSize: this.pageSize,
//         displayed: this.pagedRFQs.length,
//         total: this.filteredRFQs.length
//       });
//     }

//     // ==================== ACTIONS ====================

//     viewRFQ(rfq: any): void {
//       console.log('%c[VIEW RFQ]', 'color: #0066cc;', rfq.rfqNumber);
//       this.router.navigate([`/create-rfq/${rfq.id}/view`]);
//     }

//     editRFQ(rfq: any): void {
//       if (rfq.status !== 'DRAFT') {
//         this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited');
//         return;
//       }
//       console.log('%c[EDIT RFQ]', 'color: #0066cc;', rfq.rfqNumber);
//       this.router.navigate([`/create-rfq/${rfq.id}/edit`]);
//     }

//     deleteRFQ(rfq: any): void {
//       // ✅ Additional check: Ensure user can only delete their own RFQs
//       if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
//         this.messageService.showMessage('error', 'Error', 'You can only delete your own RFQs');
//         return;
//       }

//       const confirmMessage = `Are you sure you want to delete RFQ ${rfq.rfqNumber}?\n\nTitle: ${rfq.rfqTitle}\n\nThis action cannot be undone.`;

//       if (!confirm(confirmMessage)) {
//         return;
//       }

//       this.rfqService.deleteRFQ(rfq.id).subscribe({
//         next: () => {
//           this.loadRFQs();
//           this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
//         },
//         error: (err: any) => {
//           console.error('Delete error:', err);
//           this.messageService.showMessage('error', 'Error', 'Failed to delete RFQ');
//         }
//       });
//     }

//     // ============================================
//     // ✅ FIXED: PUBLISH RFQ WITH DYNAMIC APPROVAL
//     // ============================================
//     publishRFQ(rfq: any): void {
//       // ✅ Additional check: Ensure user can only publish their own RFQs
//       if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
//         this.messageService.showMessage('error', 'Error', 'You can only publish your own RFQs');
//         return;
//       }

//       if (rfq.status !== 'DRAFT') {
//         this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be published');
//         return;
//       }

//       const confirmMsg = `Submit RFQ ${rfq.rfqNumber} for approval?\n\nThis will start the dynamic approval workflow based on your company's hierarchy.\n\nAfter all approvals are complete, it will be published to suppliers.`;
//       if (!confirm(confirmMsg)) return;

//       console.log('%c[INITIATING DYNAMIC APPROVAL WORKFLOW]', 'color: #ff6600; font-weight: bold;', rfq.rfqNumber);

//       // ✅ CRITICAL FIX: Use the buyer user's ID (from User table)
//       // The backend will detect that this is a buyer (not hierarchy user) and use ALL hierarchy levels
//       const createdByUserId = rfq.original.createdByUser?.id || this.getUserId();

//       console.log('%c[CREATOR INFO]', 'color: #0066cc;', {
//         rfqId: rfq.id,
//         createdByUserId: createdByUserId,
//         buyerId: this.loggedInBuyerId
//       });

//       // ✅ Validation: Ensure we have a valid user ID
//       if (!createdByUserId || createdByUserId === 0) {
//         this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
//         console.error('%c[ERROR] Invalid user ID', 'color: #cc0000; font-weight: bold;', {
//           createdByUserId: createdByUserId,
//           loggedInBuyerId: this.loggedInBuyerId
//         });
//         return;
//       }

//       // Step 1: Initiate dynamic approval workflow
//       this.dataService.initiateApprovalWorkflow(rfq.id, createdByUserId).subscribe({
//         next: (response: any) => {
//           console.log('%c[✅ DYNAMIC APPROVAL WORKFLOW INITIATED]', 'color: #00aa00; font-weight: bold;', response);

//           this.messageService.showMessage('success', 'Success', 
//             `RFQ ${rfq.rfqNumber} submitted for approval!\n\nWorkflow started based on your company hierarchy.`);

//           this.loadRFQs();
//         },
//         error: (err: any) => {
//           console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', err);
//           this.messageService.showMessage('error', 'Error', 
//             err.error?.message || 'Failed to initiate approval workflow');
//         }
//       });
//     }

//     /**
//      * Get current user ID from localStorage
//      */
//     private getUserId(): number {
//       const userId = localStorage.getItem('userId');
//       return userId ? Number(userId) : 0;
//     }

//     closeRFQ(rfq: any): void {
//       // ✅ Additional check: Ensure user can only close their own RFQs
//       if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
//         this.messageService.showMessage('error', 'Error', 'You can only close your own RFQs');
//         return;
//       }

//       const confirmMsg = `Close RFQ ${rfq.rfqNumber}? This RFQ will no longer accept responses.`;
//       if (!confirm(confirmMsg)) return;

//       this.rfqService.closeRFQ(rfq.id).subscribe({
//         next: () => {
//           this.loadRFQs();
//           this.messageService.showMessage('success', 'Success', 'RFQ closed successfully');
//         },
//         error: (err: any) => {
//           console.error('Close error:', err);
//           this.messageService.showMessage('error', 'Error', 'Failed to close RFQ');
//         }
//       });
//     }

//     // ==================== PAGINATION ====================

//     get totalPages(): number {
//       return Math.ceil(this.filteredRFQs.length / this.pageSize);
//     }

//     onPageChange(page: number): void {
//       if (page >= 1 && page <= this.totalPages) {
//         this.currentPage = page;
//         this.updatePagination();
//       }
//     }

//     onPageSizeChange(size: number): void {
//       this.pageSize = size;
//       this.currentPage = 1;
//       this.updatePagination();
//     }

//     // ==================== STATUS STYLING ====================

//     getStatusClass(status: string): string {
//       const statusMap: { [key: string]: string } = {
//         'DRAFT': 'bg-secondary',
//         'AWAITING_APPROVAL': 'bg-warning',
//         'PUBLISHED': 'bg-info',
//         'RESPONSES_RECEIVED': 'bg-success',
//         'CLOSED': 'bg-danger',
//         'CANCELLED': 'bg-dark'
//       };
//       return statusMap[status] || 'bg-light';
//     }

//     getPriorityClass(priority: string): string {
//       const priorityMap: { [key: string]: string } = {
//         'LOW': 'bg-light text-dark',
//         'MEDIUM': 'bg-info text-white',
//         'HIGH': 'bg-warning text-dark',
//         'URGENT': 'bg-danger text-white'
//       };
//       return priorityMap[priority] || 'bg-light text-dark';
//     }

//     getApprovalStatusClass(status: string): string {
//       const statusMap: { [key: string]: string } = {
//         'PENDING': 'text-warning',
//         'APPROVED': 'text-success',
//         'REJECTED': 'text-danger'
//       };
//       return statusMap[status] || 'text-muted';
//     }

//     // ==================== HELPERS ====================

//     getStatusLabel(status: string): string {
//       return status.replace(/_/g, ' ');
//     }

//     canEdit(rfq: any): boolean {
//       return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
//     }

//     canPublish(rfq: any): boolean {
//       return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
//     }

//     canClose(rfq: any): boolean {
//       return ['PUBLISHED', 'RESPONSES_RECEIVED'].includes(rfq.status) && 
//             rfq.original.buyer?.id === this.loggedInBuyerId;
//     }

//     // ==================== RESET FILTERS ====================

//     resetFilters(): void {
//       this.searchText = '';
//       this.statusFilter = '';
//       this.priorityFilter = '';
//       this.buyerFilter = '';
//       this.currentPage = 1;
//       this.applyFiltersAndPagination();
//     }
//   }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RFQService } from '../models/rfq.service';
import { MessageService } from '../../../shared/service/message.service';
import { RFQ } from '../models/rfq.model';
import { BuyerService } from '../dashboard/buyer-b.service';
import { StorageService } from '../../../shared/service/StorageService';
import { RfqApprovalService } from '../models/rfq-approval.service';
import { DataService } from '../../../shared/service/DataService';

@Component({
  selector: 'app-rfq-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rfq-dashboard.component.html',
  styleUrls: ['./rfq-dashboard.component.css']
})
export class RFQDashboardComponent implements OnInit {

  // ==================== DATA ====================
  rfqs: RFQ[] = [];
  filteredRFQs: any[] = [];
  pagedRFQs: any[] = [];
  
  // ==================== LOGGED-IN BUYER ====================
  loggedInBuyerId: number | null = null;
  
  // ==================== SEARCH & PAGINATION ====================
  searchText = '';
  currentPage = 1;
  pageSize = 5;
  
  // ==================== FILTERS ====================
  statusFilter = '';
  priorityFilter = '';
  buyerFilter = '';
  
  // ==================== UI STATE ====================
  isLoading = false;
  Math = Math;
  
  // ==================== DROPDOWNS ====================
  statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  buyers: any[] = [];

  constructor(
    private rfqService: RFQService,
    private buyerService: BuyerService,
    private messageService: MessageService,
    private dataService: DataService,
    public router: Router,
    private approvalService: RfqApprovalService  
  ) {}

  ngOnInit(): void {
    this.getLoggedInBuyerId();
    this.loadRFQs();
  }

  // ==================== GET LOGGED-IN BUYER ID ====================

  /**
   * ✅ FIX: Get the logged-in buyer ID from storage
   */
  private getLoggedInBuyerId(): void {
    try {
      console.log('%c[GET LOGGED-IN BUYER ID]', 'color: #0066cc; font-weight: bold;');
      
      // Get buyer details from storage
      const buyerData = StorageService.getBuyerDetails();
      
      console.log('%c[STORAGE BUYER DATA]', 'color: #0066cc; font-weight: bold;', buyerData);

      // Extract buyer ID from different possible sources
      let buyerId: number | null = null;

      if (buyerData?.id && buyerData.id !== 'N/A') {
        buyerId = Number(buyerData.id);
      } else if (buyerData?.buyerId) {
        buyerId = Number(buyerData.buyerId);
      } else {
        // Fallback to localStorage
        const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
        if (storedId && storedId !== 'N/A') {
          buyerId = Number(storedId);
        }
      }

      if (!buyerId || isNaN(buyerId) || buyerId <= 0) {
        console.error('%c[ERROR] No valid buyer ID found', 'color: #cc0000; font-weight: bold;');
        this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
        this.router.navigate(['/login']);
        return;
      }

      this.loggedInBuyerId = buyerId;
      console.log('%c[✅ LOGGED-IN BUYER ID]', 'color: #00aa00; font-weight: bold;', this.loggedInBuyerId);
      
    } catch (error) {
      console.error('%c[ERROR] Exception in getLoggedInBuyerId', 'color: #cc0000;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to get buyer details');
      this.router.navigate(['/login']);
    }
  }

  // ==================== LOAD DATA ====================

  /**
   * ✅ FIX: Load only RFQs for the logged-in buyer
   */
  loadRFQs(): void {
    if (!this.loggedInBuyerId) {
      console.error('%c[ERROR] Cannot load RFQs - No buyer ID', 'color: #cc0000;');
      this.messageService.showMessage('error', 'Error', 'Buyer ID not found');
      return;
    }

    this.isLoading = true;
    console.log('%c[LOAD RFQs FOR BUYER]', 'color: #ff6600; font-weight: bold;', this.loggedInBuyerId);
    
    // ✅ Use getRFQsByBuyer instead of getAllRFQs
    this.rfqService.getRFQsByBuyer(this.loggedInBuyerId).subscribe({
      next: (response: any) => {
        console.log('%c[SUCCESS]', 'color: #00aa00; font-weight: bold;', response.count || response.data?.length || 0);
        this.rfqs = response.data || [];
        
        console.log('%c[RFQs LOADED]', 'color: #0066cc;', {
          count: this.rfqs.length,
          buyerId: this.loggedInBuyerId,
          statuses: this.rfqs.map((r: any) => r.status)
        });
        
        this.applyFiltersAndPagination();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
        this.isLoading = false;
      }
    });
  }

  /**
   * Load buyers (optional - for admin view)
   */
  loadBuyers(): void {
    this.buyerService.getAllBuyers().subscribe({
      next: (response: any) => {
        if (Array.isArray(response)) {
          this.buyers = response;
        } else if (response && typeof response === 'object' && 'data' in response) {
          const data = (response as any).data;
          if (Array.isArray(data)) {
            this.buyers = data;
          }
        }
        console.log('%c[✓] Buyers Loaded:', 'color: #00aa00;', this.buyers.length);
      },
      error: (error: any) => {
        console.error('Failed to load buyers:', error);
      }
    });
  }

  // ==================== FILTER & TRANSFORM DATA ====================

  /**
   * Transform RFQ data for display in table
   */
  private transformRFQDataForDisplay(rfqs: RFQ[]): any[] {
    return rfqs.map((rfq: any) => {
      // Ensure status is properly extracted
      const status = rfq.status || 'DRAFT';
      const priority = rfq.priority || 'MEDIUM';
      
      return {
        id: rfq.id,
        rfqNumber: rfq.rfqNumber || 'N/A',
        rfqTitle: rfq.rfqTitle || 'N/A',
        status: status,
        priority: priority,
        buyerName: rfq.buyer?.companyName || 'N/A',
        locationName: rfq.location?.locationName || 'N/A',
        itemsCount: rfq.items?.length || 0,
        suppliersCount: rfq.selectedSuppliers?.length || 0,
        dueDate: rfq.dueDate ? new Date(rfq.dueDate).toLocaleDateString('en-GB') : 'N/A',
        createdAt: rfq.createdAt ? new Date(rfq.createdAt).toLocaleDateString('en-GB') : 'N/A',
        approvalStatus: rfq.approvalStatus || 'PENDING',
        original: rfq
      };
    });
  }

  /**
   * Apply all filters and pagination
   */
  applyFiltersAndPagination(): void {
    console.log('%c[APPLY FILTERS]', 'color: #0066cc;', {
      search: this.searchText,
      status: this.statusFilter,
      priority: this.priorityFilter,
      buyerId: this.loggedInBuyerId
    });

    let data = this.transformRFQDataForDisplay(this.rfqs);

    console.log('%c[TRANSFORMED DATA]', 'color: #0066cc;', {
      count: data.length,
      statuses: data.map(r => r.status)
    });

    // Apply search
    if (this.searchText.trim()) {
      const term = this.searchText.toLowerCase();
      data = data.filter((row: any) =>
        (row.rfqNumber || '').toLowerCase().includes(term) ||
        (row.rfqTitle || '').toLowerCase().includes(term) ||
        (row.buyerName || '').toLowerCase().includes(term) ||
        (row.locationName || '').toLowerCase().includes(term)
      );
      console.log('%c[SEARCH FILTER]', 'color: #0066cc;', 'Results:', data.length);
    }

    // Apply status filter
    if (this.statusFilter && this.statusFilter.trim() !== '') {
      console.log('%c[STATUS FILTER]', 'color: #0066cc;', 'Filtering by:', this.statusFilter);
      const beforeFilter = data.length;
      data = data.filter((row: any) => {
        const rowStatus = row.status;
        const matches = rowStatus === this.statusFilter;
        return matches;
      });
      console.log('%c[STATUS FILTER RESULT]', 'color: #00aa00;', {
        before: beforeFilter,
        after: data.length
      });
    }

    // Apply priority filter
    if (this.priorityFilter && this.priorityFilter.trim() !== '') {
      console.log('%c[PRIORITY FILTER]', 'color: #0066cc;', 'Filtering by:', this.priorityFilter);
      data = data.filter((row: any) => row.priority === this.priorityFilter);
    }

    this.filteredRFQs = data;
    console.log('%c[FILTERED RESULTS]', 'color: #00aa00;', {
      total: this.filteredRFQs.length,
      buyerId: this.loggedInBuyerId
    });

    this.currentPage = 1;
    this.updatePagination();
  }

  /**
   * Update pagination for current page
   */
  updatePagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
    console.log('%c[PAGINATION]', 'color: #0066cc;', {
      page: this.currentPage,
      pageSize: this.pageSize,
      displayed: this.pagedRFQs.length,
      total: this.filteredRFQs.length
    });
  }

  // ==================== ACTIONS ====================

  viewRFQ(rfq: any): void {
    console.log('%c[VIEW RFQ]', 'color: #0066cc;', rfq.rfqNumber);
    this.router.navigate([`/create-rfq/${rfq.id}/view`]);
  }

  editRFQ(rfq: any): void {
    if (rfq.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited');
      return;
    }
    console.log('%c[EDIT RFQ]', 'color: #0066cc;', rfq.rfqNumber);
    this.router.navigate([`/create-rfq/${rfq.id}/edit`]);
  }

  deleteRFQ(rfq: any): void {
    // ✅ Additional check: Ensure user can only delete their own RFQs
    if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
      this.messageService.showMessage('error', 'Error', 'You can only delete your own RFQs');
      return;
    }

    const confirmMessage = `Are you sure you want to delete RFQ ${rfq.rfqNumber}?\n\nTitle: ${rfq.rfqTitle}\n\nThis action cannot be undone.`;

    if (!confirm(confirmMessage)) {
      return;
    }

    this.rfqService.deleteRFQ(rfq.id).subscribe({
      next: () => {
        this.loadRFQs();
        this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
      },
      error: (err: any) => {
        console.error('Delete error:', err);
        this.messageService.showMessage('error', 'Error', 'Failed to delete RFQ');
      }
    });
  }

  // ============================================
  // ✅ FIXED: PUBLISH RFQ WITH DYNAMIC APPROVAL
  // ============================================
  publishRFQ(rfq: any): void {
    // ✅ Additional check: Ensure user can only publish their own RFQs
    if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
      this.messageService.showMessage('error', 'Error', 'You can only publish your own RFQs');
      return;
    }

    if (rfq.status !== 'DRAFT') {
      this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be published');
      return;
    }

    const confirmMsg = `Submit RFQ ${rfq.rfqNumber} for approval?\n\nThis will start the dynamic approval workflow based on your company's hierarchy.\n\nAfter all approvals are complete, it will be published to suppliers.`;
    if (!confirm(confirmMsg)) return;

    console.log('%c[INITIATING DYNAMIC APPROVAL WORKFLOW]', 'color: #ff6600; font-weight: bold;', rfq.rfqNumber);

    // ✅ CRITICAL FIX: Use the buyer user's ID (from User table)
    // The backend will detect that this is a buyer (not hierarchy user) and use ALL hierarchy levels
    const createdByUserId = rfq.original.createdByUser?.id || this.getUserId();

    console.log('%c[CREATOR INFO]', 'color: #0066cc;', {
      rfqId: rfq.id,
      createdByUserId: createdByUserId,
      buyerId: this.loggedInBuyerId
    });

    // ✅ Validation: Ensure we have a valid user ID
    if (!createdByUserId || createdByUserId === 0) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      console.error('%c[ERROR] Invalid user ID', 'color: #cc0000; font-weight: bold;', {
        createdByUserId: createdByUserId,
        loggedInBuyerId: this.loggedInBuyerId
      });
      return;
    }

    // Step 1: Initiate dynamic approval workflow
    this.dataService.initiateApprovalWorkflow(rfq.id, createdByUserId).subscribe({
      next: (response: any) => {
        console.log('%c[✅ DYNAMIC APPROVAL WORKFLOW INITIATED]', 'color: #00aa00; font-weight: bold;', response);

        this.messageService.showMessage('success', 'Success', 
          `RFQ ${rfq.rfqNumber} submitted for approval!\n\nWorkflow started based on your company hierarchy.`);

        this.loadRFQs();
      },
      error: (err: any) => {
        console.error('%c[ERROR]', 'color: #cc0000; font-weight: bold;', err);
        this.messageService.showMessage('error', 'Error', 
          err.error?.message || 'Failed to initiate approval workflow');
      }
    });
  }

  /**
   * Get current user ID from localStorage
   */
  private getUserId(): number {
    const userId = localStorage.getItem('userId');
    return userId ? Number(userId) : 0;
  }

  closeRFQ(rfq: any): void {
    // ✅ Additional check: Ensure user can only close their own RFQs
    if (rfq.original.buyer?.id !== this.loggedInBuyerId) {
      this.messageService.showMessage('error', 'Error', 'You can only close your own RFQs');
      return;
    }

    const confirmMsg = `Close RFQ ${rfq.rfqNumber}? This RFQ will no longer accept responses.`;
    if (!confirm(confirmMsg)) return;

    this.rfqService.closeRFQ(rfq.id).subscribe({
      next: () => {
        this.loadRFQs();
        this.messageService.showMessage('success', 'Success', 'RFQ closed successfully');
      },
      error: (err: any) => {
        console.error('Close error:', err);
        this.messageService.showMessage('error', 'Error', 'Failed to close RFQ');
      }
    });
  }

  // ==================== PAGINATION ====================

  get totalPages(): number {
    return Math.ceil(this.filteredRFQs.length / this.pageSize);
  }

  onPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  onPageSizeChange(size: number): void {
    this.pageSize = size;
    this.currentPage = 1;
    this.updatePagination();
  }

  // ==================== STATUS STYLING ====================

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'DRAFT': 'bg-secondary',
      'AWAITING_APPROVAL': 'bg-warning',
      'PUBLISHED': 'bg-info',
      'RESPONSES_RECEIVED': 'bg-success',
      'CLOSED': 'bg-danger',
      'CANCELLED': 'bg-dark'
    };
    return statusMap[status] || 'bg-light';
  }

  getPriorityClass(priority: string): string {
    const priorityMap: { [key: string]: string } = {
      'LOW': 'bg-light text-dark',
      'MEDIUM': 'bg-info text-white',
      'HIGH': 'bg-warning text-dark',
      'URGENT': 'bg-danger text-white'
    };
    return priorityMap[priority] || 'bg-light text-dark';
  }

  getApprovalStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'text-warning',
      'APPROVED': 'text-success',
      'REJECTED': 'text-danger'
    };
    return statusMap[status] || 'text-muted';
  }

  // ==================== HELPERS ====================

  getStatusLabel(status: string): string {
    return status.replace(/_/g, ' ');
  }

  canEdit(rfq: any): boolean {
    return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
  }

  canPublish(rfq: any): boolean {
    return rfq.status === 'DRAFT' && rfq.original.buyer?.id === this.loggedInBuyerId;
  }

  canClose(rfq: any): boolean {
    return ['PUBLISHED', 'RESPONSES_RECEIVED'].includes(rfq.status) && 
           rfq.original.buyer?.id === this.loggedInBuyerId;
  }

  // ==================== RESET FILTERS ====================

  resetFilters(): void {
    this.searchText = '';
    this.statusFilter = '';
    this.priorityFilter = '';
    this.buyerFilter = '';
    this.currentPage = 1;
    this.applyFiltersAndPagination();
  }
}