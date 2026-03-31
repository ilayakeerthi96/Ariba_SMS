// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from '../models/rfq.service';
// import { DataService } from '../../../shared/service/DataService';
// import { MessageService } from '../../../shared/service/message.service';
// import { AuthService } from '../../../shared/service/AuthService';

// @Component({
//   selector: 'app-hierarchy-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './hierarchy-dashboard.component.html',
//   styleUrls: ['./hierarchy-dashboard.component.css']
// })
// export class HierarchyDashboardComponent implements OnInit {

//   // ==================== USER INFO ====================
//   userId: number = 0;
//   userName: string = '';
//   userEmail: string = '';
//   hierarchyLevelId: number | null = null;
//   hierarchyLevelName: string = '';
//   hierarchyLevelOrder: number | null = null;
//   companyName: string = '';

//   // ==================== DATA ====================
//   allRFQs: any[] = [];
//   filteredRFQs: any[] = [];
//   pagedRFQs: any[] = [];
  
//   // ==================== PAGINATION & SEARCH ====================
//   searchText = '';
//   currentPage = 1;
//   pageSize = 10;
  
//   // ==================== FILTERS ====================
//   statusFilter = '';
//   approvalStatusFilter = '';
  
//   // ==================== UI STATE ====================
//   isLoading = false;
//   pendingApprovalsCount = 0;
  
//   // ==================== DROPDOWNS ====================
//   statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED'];
//   approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];

//   constructor(
//     private rfqService: RFQService,
//     private dataService: DataService,
//     private messageService: MessageService,
//     private authService: AuthService,
//     public router: Router,
//     private cdr: ChangeDetectorRef
//   ) {}

//   ngOnInit(): void {
//     this.initializeUser();
//     this.loadData();
//   }

//   // ==================== INITIALIZE USER ====================

//   private initializeUser(): void {
//     this.userId = Number(localStorage.getItem('userId')) || 0;
//     this.userName = localStorage.getItem('fullName') || 'User';
//     this.userEmail = localStorage.getItem('username') || '';
//     this.companyName = localStorage.getItem('companyName') || '';
    
//     this.hierarchyLevelId = this.authService.getHierarchyLevelId();
//     this.hierarchyLevelName = this.authService.getHierarchyLevelName() || '';
//     this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

//     console.log('%c[HIERARCHY DASHBOARD INIT]', 'color: #00aa00; font-weight: bold;', {
//       userId: this.userId,
//       userName: this.userName,
//       levelName: this.hierarchyLevelName,
//       levelOrder: this.hierarchyLevelOrder,
//       companyName: this.companyName
//     });

//     if (!this.hierarchyLevelId) {
//       this.messageService.showMessage('error', 'Error', 'Hierarchy level not found');
//       this.router.navigate(['/login']);
//     }
//   }

//   // ==================== LOAD DATA ====================

// // REPLACE THIS METHOD IN hierarchy-dashboard.component.ts

// private loadData(): void {
//   this.isLoading = true;
  
//   // Load all RFQs for the company
//   this.rfqService.getAllRFQs().subscribe({
//     next: (response: any) => {
//       console.log('%c[RFQs LOADED]', 'color: #00aa00;', response);
      
//       let rawRFQs = response.data || [];
      
//       // ✅ FIX: Transform RFQ data to include calculated fields
//       this.allRFQs = rawRFQs.map((rfq: any) => ({
//         ...rfq,
//         suppliersCount: rfq.selectedSuppliers?.length || 0,
//         itemsCount: rfq.items?.length || 0,
//         buyerName: rfq.buyer?.companyName || 'N/A'
//       }));
      
//       // Filter RFQs by company if needed
//       if (this.companyName) {
//         this.allRFQs = this.allRFQs.filter((rfq: any) => 
//           rfq.buyer?.companyName === this.companyName
//         );
//       }
      
//       this.applyFiltersAndPagination();
//       this.loadPendingApprovalCount();
//       this.isLoading = false;
//       this.cdr.markForCheck();
//     },
//     error: (error: any) => {
//       console.error('%c[ERROR]', 'color: #cc0000;', error);
//       this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//       this.isLoading = false;
//       this.cdr.markForCheck();
//     }
//   });
// }

//   private loadPendingApprovalCount(): void {
//     this.dataService.getPendingApprovalCount(this.userId).subscribe({
//       next: (response: any) => {
//         this.pendingApprovalsCount = response.pendingCount || 0;
//         this.cdr.markForCheck();
//       },
//       error: () => {
//         this.pendingApprovalsCount = 0;
//       }
//     });
//   }

//   // ==================== FILTER & PAGINATION ====================

//   applyFiltersAndPagination(): void {
//     let data = [...this.allRFQs];

//     // Search filter
//     if (this.searchText.trim()) {
//       const term = this.searchText.toLowerCase();
//       data = data.filter((rfq: any) =>
//         (rfq.rfqNumber || '').toLowerCase().includes(term) ||
//         (rfq.rfqTitle || '').toLowerCase().includes(term) ||
//         (rfq.buyer?.companyName || '').toLowerCase().includes(term)
//       );
//     }

//     // Status filter
//     if (this.statusFilter) {
//       data = data.filter((rfq: any) => rfq.status === this.statusFilter);
//     }

//     // Approval status filter
//     if (this.approvalStatusFilter) {
//       data = data.filter((rfq: any) => rfq.approvalStatus === this.approvalStatusFilter);
//     }

//     this.filteredRFQs = data;
//     this.currentPage = 1;
//     this.updatePagination();
//   }

//   private updatePagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
//     this.cdr.markForCheck();
//   }

//   get totalPages(): number {
//     return Math.ceil(this.filteredRFQs.length / this.pageSize);
//   }

//   onPageChange(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.updatePagination();
//     }
//   }

//   onPageSizeChange(size: number): void {
//     this.pageSize = size;
//     this.currentPage = 1;
//     this.updatePagination();
//   }

//   // ==================== NAVIGATION ====================

//   viewRFQDetails(rfq: any): void {
//     this.router.navigate([`/create-rfq/${rfq.id}/view`]);
//   }

//   navigateToPendingApprovals(): void {
//     this.router.navigate(['/pending-approvals']);
//   }

//   // ==================== HELPERS ====================

//   formatDate(dateString: string | null): string {
//     if (!dateString) return 'N/A';
//     try {
//       return new Date(dateString).toLocaleDateString('en-GB');
//     } catch {
//       return 'Invalid Date';
//     }
//   }

//   getStatusClass(status: string): string {
//     const statusMap: { [key: string]: string } = {
//       'DRAFT': 'bg-secondary',
//       'AWAITING_APPROVAL': 'bg-warning',
//       'PUBLISHED': 'bg-info',
//       'RESPONSES_RECEIVED': 'bg-success',
//       'CLOSED': 'bg-danger',
//       'CANCELLED': 'bg-dark'
//     };
//     return statusMap[status] || 'bg-light text-dark';
//   }

//   getApprovalStatusClass(status: string): string {
//     const statusMap: { [key: string]: string } = {
//       'PENDING': 'bg-warning',
//       'APPROVED': 'bg-success',
//       'REJECTED': 'bg-danger'
//     };
//     return statusMap[status] || 'bg-secondary';
//   }

//   getStatusLabel(status: string): string {
//     return status.replace(/_/g, ' ');
//   }

//   // ==================== REFRESH ====================

//   refresh(): void {
//     this.loadData();
//   }

//   resetFilters(): void {
//     this.searchText = '';
//     this.statusFilter = '';
//     this.approvalStatusFilter = '';
//     this.applyFiltersAndPagination();
//   }
// }


import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RFQService } from '../models/rfq.service';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import { AuthService } from '../../../shared/service/AuthService';

// ✅ Add custom pipe for filtering
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
  standalone: true
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], field: string, value: any): any[] {
    if (!items || !field) {
      return items;
    }
    return items.filter(item => item[field] === value);
  }
}

@Component({
  selector: 'app-hierarchy-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterPipe],
  templateUrl: './hierarchy-dashboard.component.html',
  styleUrls: ['./hierarchy-dashboard.component.css']
})
export class HierarchyDashboardComponent implements OnInit {

  // ==================== USER INFO ====================
  userId: number = 0;
  userName: string = '';
  userEmail: string = '';
  hierarchyLevelId: number | null = null;
  hierarchyLevelName: string = '';
  hierarchyLevelOrder: number | null = null;
  companyName: string = '';

  // ==================== DATA ====================
  allRFQs: any[] = [];
  filteredRFQs: any[] = [];
  pagedRFQs: any[] = [];
  
  // ==================== PAGINATION & SEARCH ====================
  searchText = '';
  currentPage = 1;
  pageSize = 10;
  
  // ==================== FILTERS ====================
  statusFilter = '';
  approvalStatusFilter = '';
  
  // ==================== UI STATE ====================
  isLoading = false;
  pendingApprovalsCount = 0;
  
  // ✅ NEW: Statistics
  statistics = {
    totalRFQs: 0,
    pendingApprovals: 0,
    approved: 0,
    rejected: 0,
    published: 0,
    closed: 0,
    draft: 0,
    awaitingApproval: 0
  };
  
  // ==================== DROPDOWNS ====================
  statuses = ['DRAFT', 'AWAITING_APPROVAL', 'PUBLISHED', 'RESPONSES_RECEIVED', 'CLOSED', 'CANCELLED'];
  approvalStatuses = ['PENDING', 'APPROVED', 'REJECTED'];

  constructor(
    private rfqService: RFQService,
    private dataService: DataService,
    private messageService: MessageService,
    private authService: AuthService,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeUser();
    this.loadData();
  }

  // ==================== INITIALIZE USER ====================

  private initializeUser(): void {
    this.userId = Number(localStorage.getItem('userId')) || 0;
    this.userName = localStorage.getItem('fullName') || 'User';
    this.userEmail = localStorage.getItem('username') || '';
    this.companyName = localStorage.getItem('companyName') || '';
    
    this.hierarchyLevelId = this.authService.getHierarchyLevelId();
    this.hierarchyLevelName = this.authService.getHierarchyLevelName() || 'Hierarchy User';
    this.hierarchyLevelOrder = this.authService.getHierarchyLevelOrder();

    console.log('%c[HIERARCHY DASHBOARD INIT]', 'color: #00aa00; font-weight: bold;', {
      userId: this.userId,
      userName: this.userName,
      levelName: this.hierarchyLevelName,
      levelOrder: this.hierarchyLevelOrder,
      companyName: this.companyName
    });

    if (!this.userId) {
      this.messageService.showMessage('error', 'Error', 'User ID not found. Please login again.');
      this.router.navigate(['/login']);
    }
  }

  // ==================== LOAD DATA ====================

  private loadData(): void {
    this.isLoading = true;
    
    console.log('%c[LOADING DASHBOARD DATA]', 'color: #0066cc; font-weight: bold;');
    
    // Load all RFQs for the company
    // this.rfqService.getAllRFQs().subscribe({
    //   next: (response: any) => {
    //     console.log('%c[✅ RFQs LOADED]', 'color: #00aa00;', response);
        
    //     let rawRFQs = response.data || [];
        
    //     // ✅ Transform RFQ data to include calculated fields
    //     this.allRFQs = rawRFQs.map((rfq: any) => ({
    //       ...rfq,
    //       suppliersCount: rfq.selectedSuppliers?.length || 0,
    //       itemsCount: rfq.items?.length || 0,
    //       buyerName: rfq.buyer?.companyName || 'N/A'
    //     }));
        
    //     // ✅ Filter RFQs by company if needed
    //     if (this.companyName) {
    //       this.allRFQs = this.allRFQs.filter((rfq: any) => 
    //         rfq.buyer?.companyName === this.companyName ||
    //         rfq.buyer?.organizationCompanyName === this.companyName
    //       );
    //     }
        
    //     // ✅ Calculate statistics
    //     this.calculateStatistics();
        
    //     this.applyFiltersAndPagination();
    //     this.loadPendingApprovalCount();
    //     this.isLoading = false;
    //     this.cdr.markForCheck();
    //   },
    //   error: (error: any) => {
    //     console.error('%c[ERROR]', 'color: #cc0000;', error);
    //     this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
    //     this.isLoading = false;
    //     this.cdr.markForCheck();
    //   }
    // });
      this.dataService.getRFQDashboardList(this.userId, {}).subscribe({
      next: (response: any) => {
        console.log('%c[✅ RFQs LOADED]', 'color: #00aa00;', response);
        
        let rawRFQs = response.data || [];
        
        // ✅ The supplier counts are now included in the response from backend
        this.allRFQs = rawRFQs.map((rfq: any) => ({
          ...rfq,
          suppliersCount: rfq.suppliersCount || 0,  // ✅ Now comes from backend
          itemsCount: rfq.itemsCount || 0,
          buyerName: rfq.buyerName || 'N/A'
        }));
        
        // ✅ Filter RFQs by company if needed
        if (this.companyName) {
          this.allRFQs = this.allRFQs.filter((rfq: any) => 
            rfq.buyerName === this.companyName
          );
        }
        
        // ✅ Calculate statistics
        this.calculateStatistics();
        
        this.applyFiltersAndPagination();
        this.loadPendingApprovalCount();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('%c[ERROR]', 'color: #cc0000;', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private loadPendingApprovalCount(): void {
    if (!this.userId) {
      return;
    }

    this.dataService.getPendingApprovalCount(this.userId).subscribe({
      next: (response: any) => {
        console.log('%c[PENDING COUNT RESPONSE]', 'color: #0066cc;', response);
        
        // ✅ Handle different response structures
        if (response.success && typeof response.pendingCount === 'number') {
          this.pendingApprovalsCount = response.pendingCount;
        } else if (typeof response.pendingCount === 'number') {
          this.pendingApprovalsCount = response.pendingCount;
        } else if (typeof response.count === 'number') {
          this.pendingApprovalsCount = response.count;
        } else if (typeof response.data === 'number') {
          this.pendingApprovalsCount = response.data;
        } else {
          this.pendingApprovalsCount = 0;
        }

        // ✅ Update statistics
        this.statistics.pendingApprovals = this.pendingApprovalsCount;
        
        console.log('%c[✅ PENDING COUNT]', 'color: #00aa00;', this.pendingApprovalsCount);
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('%c[ERROR LOADING PENDING COUNT]', 'color: #cc0000;', error);
        this.pendingApprovalsCount = 0;
        this.cdr.markForCheck();
      }
    });
  }

  // ==================== CALCULATE STATISTICS ====================

  private calculateStatistics(): void {
    this.statistics = {
      totalRFQs: this.allRFQs.length,
      pendingApprovals: this.pendingApprovalsCount, // Will be updated by loadPendingApprovalCount
      approved: this.allRFQs.filter(rfq => rfq.approvalStatus === 'APPROVED').length,
      rejected: this.allRFQs.filter(rfq => rfq.approvalStatus === 'REJECTED').length,
      published: this.allRFQs.filter(rfq => rfq.status === 'PUBLISHED').length,
      closed: this.allRFQs.filter(rfq => rfq.status === 'CLOSED').length,
      draft: this.allRFQs.filter(rfq => rfq.status === 'DRAFT').length,
      awaitingApproval: this.allRFQs.filter(rfq => rfq.status === 'AWAITING_APPROVAL').length
    };

    console.log('%c[STATISTICS CALCULATED]', 'color: #9c27b0; font-weight: bold;', this.statistics);
  }

  // ==================== FILTER & PAGINATION ====================

  applyFiltersAndPagination(): void {
    let data = [...this.allRFQs];

    // Search filter
    if (this.searchText.trim()) {
      const term = this.searchText.toLowerCase();
      data = data.filter((rfq: any) =>
        (rfq.rfqNumber || '').toLowerCase().includes(term) ||
        (rfq.rfqTitle || '').toLowerCase().includes(term) ||
        (rfq.buyer?.companyName || '').toLowerCase().includes(term)
      );
    }

    // Status filter
    if (this.statusFilter) {
      data = data.filter((rfq: any) => rfq.status === this.statusFilter);
    }

    // Approval status filter
    if (this.approvalStatusFilter) {
      data = data.filter((rfq: any) => rfq.approvalStatus === this.approvalStatusFilter);
    }

    this.filteredRFQs = data;
    this.currentPage = 1;
    this.updatePagination();
  }

  private updatePagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedRFQs = this.filteredRFQs.slice(start, start + this.pageSize);
    this.cdr.markForCheck();
  }

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

  // ==================== NAVIGATION ====================

  viewRFQDetails(rfq: any): void {
    this.router.navigate([`/create-rfq/${rfq.id}/view`]);
  }

  navigateToPendingApprovals(): void {
    this.router.navigate(['/pending-approvals']);
  }

  // ==================== HELPERS ====================

  formatDate(dateString: string | null): string {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('en-GB');
    } catch {
      return 'Invalid Date';
    }
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'DRAFT': 'bg-secondary',
      'AWAITING_APPROVAL': 'bg-warning',
      'PUBLISHED': 'bg-info',
      'RESPONSES_RECEIVED': 'bg-success',
      'CLOSED': 'bg-danger',
      'CANCELLED': 'bg-dark'
    };
    return statusMap[status] || 'bg-light text-dark';
  }

  getApprovalStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'bg-warning',
      'APPROVED': 'bg-success',
      'REJECTED': 'bg-danger'
    };
    return statusMap[status] || 'bg-secondary';
  }

  getStatusLabel(status: string): string {
    return status.replace(/_/g, ' ');
  }

  // ==================== REFRESH ====================

  refresh(): void {
    this.loadData();
  }

  resetFilters(): void {
    this.searchText = '';
    this.statusFilter = '';
    this.approvalStatusFilter = '';
    this.applyFiltersAndPagination();
  }
}