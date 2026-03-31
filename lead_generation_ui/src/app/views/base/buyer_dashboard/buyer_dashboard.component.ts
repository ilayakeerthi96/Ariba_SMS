// // // // File: src/app/features/requisition/components/requisition-dashboard/requisition-dashboard.component.ts
// // // import { Component, OnInit } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { FormsModule } from '@angular/forms';
// // // import { TableModule } from '@coreui/angular';
// // // import { RequisitionService } from '../create-roq/requisition.service';
// // // import { MessageService } from '../../../shared/service/message.service';
// // // import { Router } from '@angular/router';
// // // import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';
// // // import {
// // //   RequisitionResponse,
// // //   RequisitionItem
// // // } from '../create-roq/requisition.model';

// // // @Component({
// // //   selector: 'app-requisition-dashboard',
// // //   standalone: true,
// // //   imports: [CommonModule, FormsModule, TableModule, DownloadExcelComponent],
// // //   templateUrl: './buyer_dashboard.component.html',
// // //   styleUrls: ['./buyer_dashboard.component.css']
// // // })
// // // export class BuyerDashboardComponent implements OnInit {

// // //   // Requisition properties
// // //     requisitions: any[] = [];
// // //   allRequisitions: RequisitionResponse[] = [];
// // //   filteredRequisitions: any[] = [];
// // //   pagedRequisitions: any[] = [];
// // //   requisitionSearchText = '';
// // //   requisitionPage = 1;
// // //   requisitionPageSize = 5;

// // //   // Expanded item details
// // //   expandedRequisitionId: number | null = null;
// // //   expandedItems: RequisitionItem[] = [];

// // //   // Expose Math to template
// // //   Math = Math;

// // //   // Status options for filtering
// // //   statusOptions = ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED'];
// // //   selectedStatus = '';

// // //   constructor(
// // //     private requisitionService: RequisitionService,
// // //     private messageService: MessageService,
// // //     private router: Router
// // //   ) {}

// // //   ngOnInit(): void {
// // //     this.loadRequisitions();
// // //   }

// // //   /**
// // //    * Load all requisitions with full details
// // //    */
// // //   loadRequisitions(): void {
// // //     this.requisitionService.getAllRequisitions().subscribe({
// // //       next: (response: any) => {
// // //         console.log('Requisitions loaded:', response);
        
// // //         if (response.success && response.data) {
// // //           this.allRequisitions = response.data;
// // //         } else if (Array.isArray(response)) {
// // //           this.allRequisitions = response;
// // //         }
        
// // //         this.applyRequisitionSearchAndPagination();
// // //       },
// // //       error: (err: any) => {
// // //         console.error('Error loading requisitions:', err);
// // //         this.messageService.showMessage('error', 'Error', 'Failed to load requisitions');
// // //       }
// // //     });
// // //   }

// // //   /**
// // //    * Transform requisition data for display
// // //    */
// // //   private transformRequisitionDataForDisplay(requisitions: RequisitionResponse[]): any[] {
// // //     const displayRows: any[] = [];

// // //     requisitions.forEach(req => {
// // //       displayRows.push({
// // //         id: req.id,
// // //         title: req.title,
// // //         category: req.category,
// // //         status: req.status,
// // //         departmentId: req.departmentId,
// // //         departmentName: req.department?.departmentName || 'N/A',
// // //         requestedById: req.requestedById,
// // //         requestedByName: req.requestedBy?.firstName + ' ' + req.requestedBy?.lastName || 'N/A',
// // //         currency: req.currency,
// // //         estimatedCost: req.estimatedCost,
// // //         deliveryDate: req.deliveryDate,
// // //         createdAt: req.createdAt,
// // //         itemCount: req.itemCount || (req.items?.length || 0),
// // //         totalItemsCost: req.totalItemsCost || 0,
// // //         items: req.items || [],
// // //         original: req
// // //       });
// // //     });

// // //     console.log('Transformed requisition display rows:', displayRows);
// // //     return displayRows;
// // //   }

// // //   /**
// // //    * Apply search filter and pagination
// // //    */
// // //   applyRequisitionSearchAndPagination(): void {
// // //     let data = this.transformRequisitionDataForDisplay(this.allRequisitions);

// // //     // Apply search filter
// // //     const term = this.requisitionSearchText.toLowerCase().trim();
// // //     if (term) {
// // //       data = data.filter(row =>
// // //         (row.title || '').toLowerCase().includes(term) ||
// // //         (row.category || '').toLowerCase().includes(term) ||
// // //         (row.departmentName || '').toLowerCase().includes(term) ||
// // //         (row.requestedByName || '').toLowerCase().includes(term) ||
// // //         (row.status || '').toLowerCase().includes(term)
// // //       );
// // //     }

// // //     // Apply status filter
// // //     if (this.selectedStatus) {
// // //       data = data.filter(row => row.status === this.selectedStatus);
// // //     }

// // //     this.filteredRequisitions = data;
    
// // //     // Apply pagination
// // //     const start = (this.requisitionPage - 1) * this.requisitionPageSize;
// // //     this.pagedRequisitions = data.slice(start, start + this.requisitionPageSize);
// // //   }

// // //   /**
// // //    * Toggle items expansion
// // //    */
// // //   toggleItemsExpansion(row: any): void {
// // //     if (this.expandedRequisitionId === row.id) {
// // //       this.expandedRequisitionId = null;
// // //       this.expandedItems = [];
// // //     } else {
// // //       this.expandedRequisitionId = row.id;
// // //       // Load items from the requisition
// // //       this.loadRequisitionItems(row);
// // //     }
// // //   }

// // //   /**
// // //    * Load items for a specific requisition
// // //    */
// // //   private loadRequisitionItems(row: any): void {
// // //     if (row.items && row.items.length > 0) {
// // //       this.expandedItems = row.items;
// // //     } else {
// // //       // Fetch from backend if not available
// // //       this.requisitionService.getItemsByRequisition(row.id).subscribe({
// // //         next: (response: any) => {
// // //           if (response.success && response.data) {
// // //             this.expandedItems = response.data;
// // //           } else if (Array.isArray(response)) {
// // //             this.expandedItems = response;
// // //           }
// // //         },
// // //         error: (err) => {
// // //           console.error('Error loading items:', err);
// // //           this.messageService.showMessage('error', 'Error', 'Failed to load items');
// // //         }
// // //       });
// // //     }
// // //   }

// // //   /**
// // //    * Get status badge color
// // //    */
// // //   getStatusBadgeClass(status: string): string {
// // //     switch (status) {
// // //       case 'DRAFT':
// // //         return 'bg-warning text-dark';
// // //       case 'SUBMITTED':
// // //         return 'bg-info';
// // //       case 'APPROVED':
// // //         return 'bg-success';
// // //       case 'REJECTED':
// // //         return 'bg-danger';
// // //       default:
// // //         return 'bg-secondary';
// // //     }
// // //   }

// // //   /**
// // //    * Edit requisition
// // //    */
// // //   editRequisition(row: any): void {
// // //     this.router.navigate([`/requisition/edit/${row.id}`]);
// // //   }

// // //   /**
// // //    * View requisition details
// // //    */
// // //   viewRequisition(row: any): void {
// // //     this.router.navigate([`/requisition/view/${row.id}`]);
// // //   }

// // //   /**
// // //    * Delete requisition
// // //    */
// // //   deleteRequisition(row: any): void {
// // //     const confirmMessage = `Are you sure you want to delete "${row.title}"?\n\nCategory: ${row.category}\nStatus: ${row.status}\n\nThis action cannot be undone.`;

// // //     if (!confirm(confirmMessage)) {
// // //       return;
// // //     }

// // //     this.requisitionService.deleteRequisition(row.id).subscribe({
// // //       next: () => {
// // //         this.loadRequisitions();
// // //         this.messageService.showMessage('success', 'Success', 'Requisition deleted successfully');
// // //       },
// // //       error: (err) => {
// // //         console.error('Error deleting requisition:', err);
// // //         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete requisition');
// // //       }
// // //     });
// // //   }

// // //   /**
// // //    * Duplicate requisition
// // //    */
// // //   duplicateRequisition(row: any): void {
// // //     if (confirm(`Duplicate "${row.title}"? A copy will be created in DRAFT status.`)) {
// // //       // Fetch full requisition details
// // //       this.requisitionService.getRequisitionById(row.id).subscribe({
// // //         next: (response: any) => {
// // //           const fullReq = response.data || response;
          
// // //           // Navigate to create with pre-filled data
// // //           sessionStorage.setItem('duplicateRequisition', JSON.stringify(fullReq));
// // //           this.router.navigate(['/requisition/create']);
// // //           this.messageService.showMessage('info', 'Info', 'Pre-filled form with requisition data');
// // //         },
// // //         error: (err) => {
// // //           this.messageService.showMessage('error', 'Error', 'Failed to fetch requisition details');
// // //         }
// // //       });
// // //     }
// // //   }

// // //   /**
// // //    * Prepare Excel export data
// // //    */
// // //   prepareRequisitionExcelData(): any[] {
// // //     const excelData: any[] = [];

// // //     this.allRequisitions.forEach(req => {
// // //       // Add main requisition row
// // //       excelData.push({
// // //         'Requisition ID': req.id,
// // //         'Title': req.title,
// // //         'Category': req.category,
// // //         'Status': req.status,
// // //         'Department': req.department?.departmentName || 'N/A',
// // //         'Requested By': (req.requestedBy?.firstName || '') + ' ' + (req.requestedBy?.lastName || ''),
// // //         'Currency': req.currency,
// // //         'Estimated Cost': req.estimatedCost,
// // //         'Total Items Cost': req.totalItemsCost || 0,
// // //         'Delivery Date': req.deliveryDate,
// // //         'Delivery Location': req.deliveryLocation,
// // //         'Created Date': this.formatDateTime(req.createdAt),
// // //         'Item Count': req.itemCount || (req.items?.length || 0)
// // //       });

// // //       // Add items as separate rows
// // //       if (req.items && req.items.length > 0) {
// // //         req.items.forEach((item, index) => {
// // //           excelData.push({
// // //             'Requisition ID': req.id,
// // //             'Title': index === 0 ? req.title : '',
// // //             'Category': index === 0 ? req.category : '',
// // //             'Status': index === 0 ? req.status : '',
// // //             'Department': index === 0 ? (req.department?.departmentName || 'N/A') : '',
// // //             'Requested By': index === 0 ? (req.requestedBy?.firstName || '') + ' ' + (req.requestedBy?.lastName || '') : '',
// // //             'Currency': index === 0 ? req.currency : '',
// // //             'Estimated Cost': index === 0 ? req.estimatedCost : '',
// // //             'Total Items Cost': index === 0 ? (req.totalItemsCost || 0) : '',
// // //             'Delivery Date': index === 0 ? req.deliveryDate : '',
// // //             'Delivery Location': index === 0 ? req.deliveryLocation : '',
// // //             'Created Date': index === 0 ? this.formatDateTime(req.createdAt) : '',
// // //             'Item Count': index === 0 ? (req.itemCount || (req.items?.length || 0)) : '',
// // //             '--- Item Name': item.itemName,
// // //             'Item Description': item.itemDescription || 'N/A',
// // //             'Quantity': item.quantity,
// // //             'Unit Price': item.price,
// // //             'Item Total': item.totalPrice || (item.quantity * item.price)
// // //           });
// // //         });
// // //       }
// // //     });

// // //     return excelData;
// // //   }

// // //   /**
// // //    * Get Excel file name
// // //    */
// // //   get requisitionExcelFileName(): string {
// // //     const date = new Date().toISOString().split('T')[0];
// // //     return `Requisitions_Complete_Details_${date}.xlsx`;
// // //   }

// // //   /**
// // //    * Get Excel data
// // //    */
// // //   get requisitionExcelData(): any[] {
// // //     return this.prepareRequisitionExcelData();
// // //   }

// // //   /**
// // //    * Get total pages
// // //    */
// // //   get requisitionTotalPages(): number {
// // //     return Math.ceil(this.filteredRequisitions.length / this.requisitionPageSize);
// // //   }

// // //   /**
// // //    * Change page
// // //    */
// // //   onRequisitionPageChange(page: number): void {
// // //     if (page >= 1 && page <= this.requisitionTotalPages) {
// // //       this.requisitionPage = page;
// // //       this.applyRequisitionSearchAndPagination();
// // //     }
// // //   }

// // //   /**
// // //    * Change page size
// // //    */
// // //   onRequisitionPageSizeChange(size: number): void {
// // //     this.requisitionPageSize = size;
// // //     this.requisitionPage = 1;
// // //     this.applyRequisitionSearchAndPagination();
// // //   }

// // //   /**
// // //    * Get formatted date
// // //    */
// // //   formatDate(date: string): string {
// // //     if (!date) return '—';
// // //     return new Date(date).toLocaleDateString('en-IN', {
// // //       year: 'numeric',
// // //       month: 'short',
// // //       day: 'numeric'
// // //     });
// // //   }

// // //   /**
// // //    * Get formatted date and time
// // //    */
// // //   formatDateTime(dateTime: string): string {
// // //     if (!dateTime) return '—';
// // //     return new Date(dateTime).toLocaleString('en-IN', {
// // //       year: 'numeric',
// // //       month: 'short',
// // //       day: 'numeric',
// // //       hour: '2-digit',
// // //       minute: '2-digit'
// // //     });
// // //   }

// // //   /**
// // //    * Get status count
// // //    */
// // //   getStatusCount(status: string): number {
// // //     return this.allRequisitions.filter(req => req.status === status).length;
// // //   }

// // //   /**
// // //    * Create new requisition
// // //    */
// // //   createNewRequisition(): void {
// // //     sessionStorage.removeItem('duplicateRequisition');
// // //     this.router.navigate(['/requisition/create']);
// // //   }
// // // }


// // File: src/app/features/requisition/components/requisition-dashboard/requisition-dashboard.component.ts
// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { TableModule } from '@coreui/angular';
// import { RequisitionService } from '../create-roq/requisition.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Router } from '@angular/router';
// import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';
// import { RequisitionResponse, RequisitionItem } from '../create-roq/requisition.model';

// @Component({
//   selector: 'app-requisition-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule, TableModule, DownloadExcelComponent],
//   templateUrl: './buyer_dashboard.component.html',
//   styleUrls: ['./buyer_dashboard.component.css']
// })
// export class BuyerDashboardComponent implements OnInit {

//   // Requisition properties
//   requisitions: any[] = [];
//   allRequisitions: RequisitionResponse[] = [];
//   filteredRequisitions: any[] = [];
//   pagedRequisitions: any[] = [];
//   requisitionSearchText = '';
//   requisitionPage = 1;
//   requisitionPageSize = 5;

//   // Expanded item details
//   expandedRequisitionId: number | null = null;
//   expandedItems: RequisitionItem[] = [];

//   // Expose Math to template
//   Math = Math;

//   // Status options for filtering
//   statusOptions = ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED'];
//   selectedStatus = '';

//   constructor(
//     private requisitionService: RequisitionService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.loadRequisitions();
//   }

//   /** Load all requisitions */
//   loadRequisitions(): void {
//     this.requisitionService.getAllRequisitions().subscribe({
//       next: (response: any) => {
//         if (response.success && response.data) {
//           this.allRequisitions = response.data;
//         } else if (Array.isArray(response)) {
//           this.allRequisitions = response;
//         }
//         this.applyRequisitionSearchAndPagination();
//       },
//       error: (err) => {
//         console.error('Error loading requisitions:', err);
//         this.messageService.showMessage('error', 'Error', 'Failed to load requisitions');
//       }
//     });
//   }

//   /** Transform requisition data for display */
//   private transformRequisitionDataForDisplay(requisitions: RequisitionResponse[]): any[] {
//     return requisitions.map(req => ({
//       id: req.id,
//       title: req.title,
//       category: req.category,
//       status: req.status,
//       departmentId: req.departmentId,
//       departmentName: req.department?.departmentName || 'N/A',
//       requestedById: req.requestedById,
//       requestedByName: req.requestedBy ? `${req.requestedBy.firstName} ${req.requestedBy.lastName}` : 'N/A',
//       currency: req.currency,
//       estimatedCost: req.estimatedCost,
//       deliveryDate: req.deliveryDate,
//       createdAt: req.createdAt,
//       itemCount: req.itemCount || (req.items?.length || 0),
//       totalItemsCost: req.totalItemsCost || 0,
//       items: req.items || [],
//       original: req
//     }));
//   }

//   /** Apply search, filter, and pagination */
//   applyRequisitionSearchAndPagination(): void {
//     let data = this.transformRequisitionDataForDisplay(this.allRequisitions);

//     const term = this.requisitionSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(row =>
//         (row.title || '').toLowerCase().includes(term) ||
//         (row.category || '').toLowerCase().includes(term) ||
//         (row.departmentName || '').toLowerCase().includes(term) ||
//         (row.requestedByName || '').toLowerCase().includes(term) ||
//         (row.status || '').toLowerCase().includes(term)
//       );
//     }

//     if (this.selectedStatus) {
//       data = data.filter(row => row.status === this.selectedStatus);
//     }

//     this.filteredRequisitions = data;
//     const start = (this.requisitionPage - 1) * this.requisitionPageSize;
//     this.pagedRequisitions = data.slice(start, start + this.requisitionPageSize);
//   }

//   /** Toggle items expansion */
//   toggleItemsExpansion(row: any): void {
//     if (this.expandedRequisitionId === row.id) {
//       this.expandedRequisitionId = null;
//       this.expandedItems = [];
//     } else {
//       this.expandedRequisitionId = row.id;
//       this.loadRequisitionItems(row);
//     }
//   }

//   /** Load items for a specific requisition */
//   private loadRequisitionItems(row: any): void {
//     if (row.items && row.items.length > 0) {
//       this.expandedItems = row.items;
//     } else {
//       this.requisitionService.getItemsByRequisition(row.id).subscribe({
//         next: (response: any) => {
//           this.expandedItems = response.success && response.data ? response.data : response || [];
//         },
//         error: (err) => {
//           console.error('Error loading items:', err);
//           this.messageService.showMessage('error', 'Error', 'Failed to load items');
//         }
//       });
//     }
//   }

//   /** Get currently expanded requisition */
//   get expandedRequisition(): any | null {
//     if (!this.expandedRequisitionId) return null;
//     return this.pagedRequisitions.find(r => r.id === this.expandedRequisitionId) || null;
//   }

//   /** Status badge */
//   getStatusBadgeClass(status: string): string {
//     switch (status) {
//       case 'DRAFT': return 'bg-warning text-dark';
//       case 'SUBMITTED': return 'bg-info';
//       case 'APPROVED': return 'bg-success';
//       case 'REJECTED': return 'bg-danger';
//       default: return 'bg-secondary';
//     }
//   }

//   /** Edit, View, Delete, Duplicate, Create */
//   editRequisition(row: any): void { this.router.navigate([`/requisition/edit/${row.id}`]); }
//   viewRequisition(row: any): void { this.router.navigate([`/requisition/view/${row.id}`]); }
//   deleteRequisition(row: any): void {
//     if (!confirm(`Are you sure you want to delete "${row.title}"?`)) return;
//     this.requisitionService.deleteRequisition(row.id).subscribe({
//       next: () => {
//         this.loadRequisitions();
//         this.messageService.showMessage('success', 'Success', 'Requisition deleted successfully');
//       },
//       error: (err) => this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete requisition')
//     });
//   }
//   duplicateRequisition(row: any): void {
//     if (!confirm(`Duplicate "${row.title}"? A copy will be created in DRAFT status.`)) return;
//     this.requisitionService.getRequisitionById(row.id).subscribe({
//       next: (response: any) => {
//         const fullReq = response.data || response;
//         sessionStorage.setItem('duplicateRequisition', JSON.stringify(fullReq));
//         this.router.navigate(['/requisition/create']);
//         this.messageService.showMessage('info', 'Info', 'Pre-filled form with requisition data');
//       },
//       error: () => this.messageService.showMessage('error', 'Error', 'Failed to fetch requisition details')
//     });
//   }
//   createNewRequisition(): void {
//     sessionStorage.removeItem('duplicateRequisition');
//     this.router.navigate(['/requisition/create']);
//   }

//   /** Excel export */
//   prepareRequisitionExcelData(): any[] {
//     const excelData: any[] = [];
//     this.allRequisitions.forEach(req => {
//       excelData.push({
//         'Requisition ID': req.id,
//         'Title': req.title,
//         'Category': req.category,
//         'Status': req.status,
//         'Department': req.department?.departmentName || 'N/A',
//         'Requested By': (req.requestedBy?.firstName || '') + ' ' + (req.requestedBy?.lastName || ''),
//         'Currency': req.currency,
//         'Estimated Cost': req.estimatedCost,
//         'Total Items Cost': req.totalItemsCost || 0,
//         'Delivery Date': req.deliveryDate,
//         'Delivery Location': req.deliveryLocation,
//         'Created Date': this.formatDateTime(req.createdAt),
//         'Item Count': req.itemCount || (req.items?.length || 0)
//       });
//       req.items?.forEach((item, index) => {
//         excelData.push({
//           'Requisition ID': req.id,
//           'Title': index === 0 ? req.title : '',
//           'Category': index === 0 ? req.category : '',
//           'Status': index === 0 ? req.status : '',
//           'Department': index === 0 ? (req.department?.departmentName || 'N/A') : '',
//           'Requested By': index === 0 ? (req.requestedBy?.firstName || '') + ' ' + (req.requestedBy?.lastName || '') : '',
//           'Currency': index === 0 ? req.currency : '',
//           'Estimated Cost': index === 0 ? req.estimatedCost : '',
//           'Total Items Cost': index === 0 ? (req.totalItemsCost || 0) : '',
//           'Delivery Date': index === 0 ? req.deliveryDate : '',
//           'Delivery Location': index === 0 ? req.deliveryLocation : '',
//           'Created Date': index === 0 ? this.formatDateTime(req.createdAt) : '',
//           'Item Count': index === 0 ? (req.itemCount || (req.items?.length || 0)) : '',
//           '--- Item Name': item.itemName,
//           'Item Description': item.itemDescription || 'N/A',
//           'Quantity': item.quantity,
//           'Unit Price': item.price,
//           'Item Total': item.totalPrice || (item.quantity * item.price)
//         });
//       });
//     });
//     return excelData;
//   }

//   get requisitionExcelFileName(): string {
//     const date = new Date().toISOString().split('T')[0];
//     return `Requisitions_Complete_Details_${date}.xlsx`;
//   }
//   get requisitionExcelData(): any[] { return this.prepareRequisitionExcelData(); }

//   /** Pagination helpers */
//   get requisitionTotalPages(): number {
//     return Math.ceil(this.filteredRequisitions.length / this.requisitionPageSize) || 1;
//   }
//   onRequisitionPageChange(page: number): void {
//     if (page >= 1 && page <= this.requisitionTotalPages) {
//       this.requisitionPage = page;
//       this.applyRequisitionSearchAndPagination();
//     }
//   }
//   onRequisitionPageSizeChange(size: number): void {
//     this.requisitionPageSize = size;
//     this.requisitionPage = 1;
//     this.applyRequisitionSearchAndPagination();
//   }

//   /** Format dates */
//   formatDate(date: string): string {
//     if (!date) return '—';
//     return new Date(date).toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
//   }
//   formatDateTime(dateTime: string): string {
//     if (!dateTime) return '—';
//     return new Date(dateTime).toLocaleString('en-IN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
//   }

//   /** Status summary */
//   getStatusCount(status: string): number {
//     return this.allRequisitions.filter(req => req.status === status).length;
//   }
// }


// // File: src/app/views/base/buyer_dashboard/buyer_dashboard.component.ts
// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from '../create-roq/requisition.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Subject } from 'rxjs';
// import { takeUntil } from 'rxjs/operators';

// @Component({
//   selector: 'app-buyer-dashboard',
//   standalone: true,
//   imports: [CommonModule, FormsModule, ReactiveFormsModule],
//   templateUrl: './buyer_dashboard.component.html',
//   styleUrls: ['./buyer_dashboard.component.css']
// })
// export class BuyerDashboardComponent implements OnInit, OnDestroy {

//   // ==================== REQUISITIONS ====================
//   allRequisitions: any[] = [];
//   filteredRequisitions: any[] = [];
//   pagedRequisitions: any[] = [];
//   requisitionSearchText = '';
//   requisitionPage = 1;
//   requisitionPageSize = 5;
//   selectedRequisitionStatus = '';
//   expandedRequisitionId: number | null = null;
//   expandedItems: any[] = [];

//   // ==================== RFQs ====================
//   allRFQs: any[] = [];
//   filteredRFQs: any[] = [];
//   pagedRFQs: any[] = [];
//   rfqSearchText = '';
//   rfqPage = 1;
//   rfqPageSize = 5;
//   selectedRFQStatus = '';
//   expandedRFQId: number | null = null;
//   expandedRFQSuppliers: any[] = [];

//   // ==================== UI STATE ====================
//   activeTab = 'rfqs'; // Default to RFQs tab
//   isLoading = false;
//   Math = Math;

//   // Status filters
//   requisitionStatuses = ['DRAFT', 'SUBMITTED'];
//   rfqStatuses = ['DRAFT', 'SUBMITTED', 'SENT_TO_SUPPLIERS', 'RESPONSES_RECEIVED', 'COMPLETED'];

//   // Subscription cleanup
//   private destroy$ = new Subject<void>();

//   constructor(
//     private rfqService: RFQService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     console.log('🚀 BuyerDashboardComponent initialized');
//     this.loadAllData();
//   }

//   ngOnDestroy(): void {
//     this.destroy$.next();
//     this.destroy$.complete();
//   }

//   /**
//    * Load all requisitions and RFQs
//    */
//   private loadAllData(): void {
//     this.isLoading = true;
    
//     this.rfqService.getAllRFQs()
//       .pipe(takeUntil(this.destroy$))
//       .subscribe({
//         next: (response: any) => {
//           console.log('Response:', response);
//           this.allRFQs = response.data || response || [];
//           console.log('✅ RFQs loaded:', this.allRFQs.length);
//           this.applyRFQFilters();
//           this.isLoading = false;
//         },
//         error: (err: any) => {
//           console.error('❌ Error loading RFQs:', err);
//           this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//           this.isLoading = false;
//         }
//       });
//   }

//   // ==================== REQUISITIONS ====================

//   applyRequisitionFilters(): void {
//     let data = this.allRequisitions;

//     const term = this.requisitionSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(r =>
//         (r.title || '').toLowerCase().includes(term) ||
//         (r.category || '').toLowerCase().includes(term) ||
//         (r.status || '').toLowerCase().includes(term)
//       );
//     }

//     if (this.selectedRequisitionStatus) {
//       data = data.filter(r => r.status === this.selectedRequisitionStatus);
//     }

//     this.filteredRequisitions = data;
//     this.requisitionPage = 1;
//     this.updateRequisitionPagination();
//   }

//   updateRequisitionPagination(): void {
//     const start = (this.requisitionPage - 1) * this.requisitionPageSize;
//     this.pagedRequisitions = this.filteredRequisitions.slice(start, start + this.requisitionPageSize);
//   }

//   toggleRequisitionExpansion(requisitionId: number): void {
//     if (this.expandedRequisitionId === requisitionId) {
//       this.expandedRequisitionId = null;
//       this.expandedItems = [];
//     } else {
//       this.expandedRequisitionId = requisitionId;
//       const requisition = this.allRequisitions.find(r => r.id === requisitionId);
//       if (requisition && requisition.items) {
//         this.expandedItems = requisition.items;
//       }
//     }
//   }

//   createRFQFromRequisition(requisition: any): void {
//     if (requisition.status !== 'SUBMITTED') {
//       this.messageService.showMessage('warning', 'Warning', 'Only SUBMITTED requisitions can be converted to RFQ');
//       return;
//     }
//     this.router.navigate(['/rfq/create', requisition.id]);
//   }

//   editRequisition(requisition: any): void {
//     if (requisition.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT requisitions can be edited');
//       return;
//     }
//     this.router.navigate(['/requisition/edit', requisition.id]);
//   }

//   deleteRequisition(requisition: any): void {
//     if (!confirm(`Delete "${requisition.title}"? This action cannot be undone.`)) {
//       return;
//     }
//     // TODO: Implement delete via service
//   }

//   get requisitionTotalPages(): number {
//     return Math.ceil(this.filteredRequisitions.length / this.requisitionPageSize) || 1;
//   }

//   changeRequisitionPage(page: number): void {
//     if (page >= 1 && page <= this.requisitionTotalPages) {
//       this.requisitionPage = page;
//       this.updateRequisitionPagination();
//     }
//   }

//   changeRequisitionPageSize(size: number): void {
//     this.requisitionPageSize = size;
//     this.requisitionPage = 1;
//     this.applyRequisitionFilters();
//   }

//   // ==================== RFQs ====================

//   applyRFQFilters(): void {
//     let data = this.allRFQs;

//     const term = this.rfqSearchText.toLowerCase().trim();
//     if (term) {
//       data = data.filter(r =>
//         (r.title || '').toLowerCase().includes(term) ||
//         (r.rfqNumber || '').toLowerCase().includes(term) ||
//         (r.category || '').toLowerCase().includes(term)
//       );
//     }

//     if (this.selectedRFQStatus) {
//       data = data.filter(r => r.status === this.selectedRFQStatus);
//     }

//     this.filteredRFQs = data;
//     this.rfqPage = 1;
//     this.updateRFQPagination();
//   }

//   updateRFQPagination(): void {
//     const start = (this.rfqPage - 1) * this.rfqPageSize;
//     this.pagedRFQs = this.filteredRFQs.slice(start, start + this.rfqPageSize);
//   }

//   toggleRFQExpansion(rfqId: number): void {
//     if (this.expandedRFQId === rfqId) {
//       this.expandedRFQId = null;
//       this.expandedRFQSuppliers = [];
//     } else {
//       this.expandedRFQId = rfqId;
//       const rfq = this.allRFQs.find(r => r.id === rfqId);
//       if (rfq && rfq.selectedSuppliers) {
//         this.expandedRFQSuppliers = rfq.selectedSuppliers;
//       }
//     }
//   }

//   viewRFQDetails(rfq: any): void {
//     this.router.navigate(['/rfq/view', rfq.id]);
//   }

//   deleteRFQ(rfq: any): void {
//     if (rfq.status !== 'DRAFT') {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be deleted');
//       return;
//     }

//     if (!confirm(`Delete RFQ "${rfq.title}"? This action cannot be undone.`)) {
//       return;
//     }

//     this.rfqService.deleteRFQ(rfq.id)
//       .pipe(takeUntil(this.destroy$))
//       .subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
//           this.loadAllData();
//         },
//         error: (err: any) => {
//           this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete');
//         }
//       });
//   }

//   get rfqTotalPages(): number {
//     return Math.ceil(this.filteredRFQs.length / this.rfqPageSize) || 1;
//   }

//   changeRFQPage(page: number): void {
//     if (page >= 1 && page <= this.rfqTotalPages) {
//       this.rfqPage = page;
//       this.updateRFQPagination();
//     }
//   }

//   changeRFQPageSize(size: number): void {
//     this.rfqPageSize = size;
//     this.rfqPage = 1;
//     this.applyRFQFilters();
//   }

//   // ==================== HELPERS ====================

//   getStatusBadgeClass(status: string): string {
//     const statusMap: {[key: string]: string} = {
//       'DRAFT': 'bg-warning text-dark',
//       'SUBMITTED': 'bg-info',
//       'SENT_TO_SUPPLIERS': 'bg-primary',
//       'RESPONSES_RECEIVED': 'bg-secondary',
//       'COMPLETED': 'bg-success'
//     };
//     return statusMap[status] || 'bg-secondary';
//   }

//   getSupplierStatusBadge(status: string): string {
//     const statusMap: {[key: string]: string} = {
//       'SENT': 'bg-info',
//       'RESPONDED': 'bg-success',
//       'NOT_RESPONDED': 'bg-warning text-dark',
//       'SELECTED': 'bg-success',
//       'REJECTED': 'bg-danger'
//     };
//     return statusMap[status] || 'bg-secondary';
//   }

//   formatDate(dateString: string): string {
//     if (!dateString) return '—';
//     return new Date(dateString).toLocaleDateString('en-IN', {
//       year: 'numeric',
//       month: 'short',
//       day: 'numeric'
//     });
//   }

//   formatDateTime(dateTimeString: string): string {
//     if (!dateTimeString) return '—';
//     return new Date(dateTimeString).toLocaleString('en-IN', {
//       year: 'numeric',
//       month: 'short',
//       day: 'numeric',
//       hour: '2-digit',
//       minute: '2-digit'
//     });
//   }

//   getRequisitionStats(): {[key: string]: number} {
//     const stats = {
//       total: this.allRequisitions.length,
//       draft: this.allRequisitions.filter(r => r.status === 'DRAFT').length,
//       submitted: this.allRequisitions.filter(r => r.status === 'SUBMITTED').length
//     };
//     return stats;
//   }

//   getRFQStats(): {[key: string]: number} {
//     const stats = {
//       total: this.allRFQs.length,
//       draft: this.allRFQs.filter(r => r.status === 'DRAFT').length,
//       sent: this.allRFQs.filter(r => r.status === 'SENT_TO_SUPPLIERS').length,
//       responses: this.allRFQs.filter(r => r.status === 'RESPONSES_RECEIVED').length,
//       completed: this.allRFQs.filter(r => r.status === 'COMPLETED').length
//     };
//     return stats;
//   }

//   createNewRequisition(): void {
//     this.router.navigate(['/requisition/create']);
//   }

//   refreshData(): void {
//     this.loadAllData();
//   }
// }