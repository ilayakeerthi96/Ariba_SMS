// // // File: src/app/features/requisition/components/create-roq/requisition.service.ts
// // import { Injectable } from '@angular/core';
// // import { HttpClient, HttpHeaders } from '@angular/common/http';
// // import { Observable, throwError } from 'rxjs';
// // import { catchError, tap } from 'rxjs/operators';
// // import {
// //   RequisitionResponse,
// //   RequisitionListResponse,
// //   RequisitionItem,
// //   CreateRequisitionRequest,
// //   UpdateRequisitionRequest,
// //   AddRequisitionItemRequest,
// //   UpdateRequisitionItemRequest,
// //   ApiResponse
// // } from './requisition.model';
// // import { environment } from '../../../environments/environment';

// // @Injectable({
// //   providedIn: 'root'
// // })
// // export class RequisitionService {

// //   // ✅ Using environment.API_URL from your environment file
// //   private baseUrl = environment.API_URL + 'leadcapture/api/requisition';

// //   constructor(private http: HttpClient) {
// //     console.log('🔧 RequisitionService initialized');
// //     console.log('📍 Base URL:', this.baseUrl);
// //   }

// //   /**
// //    * ✅ Get authentication headers with token from localStorage
// //    * Supports both 'token' and 'authToken' keys
// //    */
// //   private getAuthHeaders(): HttpHeaders {
// //     const token = localStorage.getItem('token') || localStorage.getItem('authToken') || '';
    
// //     console.log('🔐 Auth token available:', !!token);
    
// //     return new HttpHeaders({
// //       'Content-Type': 'application/json',
// //       'Authorization': token ? `Bearer ${token}` : ''
// //     });
// //   }

// //   // ============================================
// //   // REQUISITION ENDPOINTS
// //   // ============================================

// //   /**
// //    * Create a new requisition (STEP 1)
// //    * POST /api/requisition
// //    */
// //   createRequisition(request: CreateRequisitionRequest): Observable<any> {
// //     console.log('📝 Creating requisition:', request);
    
// //     return this.http.post<any>(
// //       this.baseUrl,
// //       request,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisition created successfully:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error creating requisition:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get all requisitions
// //    * GET /api/requisition
// //    */
// //   getAllRequisitions(): Observable<any> {
// //     console.log('📋 Fetching all requisitions');
    
// //     return this.http.get<any>(
// //       this.baseUrl,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisitions fetched:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error fetching requisitions:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get requisition by ID
// //    * GET /api/requisition/{id}
// //    */
// //   getRequisitionById(id: number): Observable<any> {
// //     console.log('🔍 Fetching requisition ID:', id);
    
// //     return this.http.get<any>(
// //       `${this.baseUrl}/${id}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisition fetched:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error fetching requisition:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get requisitions by department
// //    * GET /api/requisition/department/{departmentId}
// //    */
// //   getRequisitionsByDepartment(departmentId: number): Observable<any> {
// //     console.log('🏢 Fetching requisitions for department:', departmentId);
    
// //     return this.http.get<any>(
// //       `${this.baseUrl}/department/${departmentId}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       catchError(error => {
// //         console.error('❌ Error fetching requisitions:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get requisitions by user
// //    * GET /api/requisition/user/{userId}
// //    */
// //   getRequisitionsByUser(userId: number): Observable<any> {
// //     console.log('👤 Fetching requisitions for user:', userId);
    
// //     return this.http.get<any>(
// //       `${this.baseUrl}/user/${userId}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       catchError(error => {
// //         console.error('❌ Error fetching requisitions:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get requisitions by status
// //    * GET /api/requisition/status/{status}
// //    */
// //   getRequisitionsByStatus(status: string): Observable<any> {
// //     console.log('📊 Fetching requisitions with status:', status);
    
// //     return this.http.get<any>(
// //       `${this.baseUrl}/status/${status}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       catchError(error => {
// //         console.error('❌ Error fetching requisitions:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Update requisition
// //    * PUT /api/requisition/{id}
// //    */
// //   updateRequisition(id: number, request: UpdateRequisitionRequest): Observable<any> {
// //     console.log('✏️ Updating requisition ID:', id, 'with data:', request);
    
// //     return this.http.put<any>(
// //       `${this.baseUrl}/${id}`,
// //       request,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisition updated:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error updating requisition:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Submit requisition (change status from DRAFT to SUBMITTED)
// //    * POST /api/requisition/{id}/submit
// //    */
// //   submitRequisition(id: number): Observable<any> {
// //     console.log('📤 Submitting requisition ID:', id);
    
// //     return this.http.post<any>(
// //       `${this.baseUrl}/${id}/submit`,
// //       {},
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisition submitted:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error submitting requisition:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Delete requisition (soft delete)
// //    * DELETE /api/requisition/{id}
// //    */
// //   deleteRequisition(id: number): Observable<any> {
// //     console.log('🗑️ Deleting requisition ID:', id);
    
// //     return this.http.delete<any>(
// //       `${this.baseUrl}/${id}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Requisition deleted:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error deleting requisition:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   // ============================================
// //   // REQUISITION ITEM ENDPOINTS
// //   // ============================================

// //   /**
// //    * Add item to requisition (STEP 2 - Called multiple times)
// //    * POST /api/requisition/{requisitionId}/item
// //    */
// //   addItemToRequisition(requisitionId: number, request: AddRequisitionItemRequest): Observable<any> {
// //     console.log(`➕ Adding item to requisition ID ${requisitionId}:`, request);
    
// //     return this.http.post<any>(
// //       `${this.baseUrl}/${requisitionId}/item`,
// //       request,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log(`✅ Item added to requisition ${requisitionId}:`, response);
// //       }),
// //       catchError(error => {
// //         console.error(`❌ Error adding item to requisition ${requisitionId}:`, error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Update requisition item
// //    * PUT /api/requisition/item/{itemId}
// //    */
// //   updateRequisitionItem(itemId: number, request: UpdateRequisitionItemRequest): Observable<any> {
// //     console.log('✏️ Updating item ID:', itemId, 'with data:', request);
    
// //     return this.http.put<any>(
// //       `${this.baseUrl}/item/${itemId}`,
// //       request,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Item updated:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error updating item:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Get items for requisition
// //    * GET /api/requisition/{requisitionId}/items
// //    */
// //   getItemsByRequisition(requisitionId: number): Observable<any> {
// //     console.log('📦 Fetching items for requisition ID:', requisitionId);
    
// //     return this.http.get<any>(
// //       `${this.baseUrl}/${requisitionId}/items`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log(`✅ Items fetched for requisition ${requisitionId}:`, response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error fetching items:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   /**
// //    * Delete item from requisition
// //    * DELETE /api/requisition/item/{itemId}
// //    */
// //   deleteRequisitionItem(itemId: number): Observable<any> {
// //     console.log('🗑️ Deleting item ID:', itemId);
    
// //     return this.http.delete<any>(
// //       `${this.baseUrl}/item/${itemId}`,
// //       { headers: this.getAuthHeaders() }
// //     ).pipe(
// //       tap(response => {
// //         console.log('✅ Item deleted:', response);
// //       }),
// //       catchError(error => {
// //         console.error('❌ Error deleting item:', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }
// // }

// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { environment } from '../../../environments/environment';
// import { Observable } from 'rxjs';
// import { map, catchError, tap } from 'rxjs/operators';
// import { throwError } from 'rxjs';
// import { RFQ, RFQStatus, RFQItem, RFQAttachment } from './requisition.model';

// @Injectable({
//   providedIn: 'root'
// })
// export class RFQService {
//   private API = environment.API_URL + 'leadcapture/api';

//   constructor(private http: HttpClient) {}

//   private getAuthHeaders(): HttpHeaders {
//     const token = localStorage.getItem('token') || '';
//     return new HttpHeaders({
//       'Content-Type': 'application/json',
//       'Authorization': `Bearer ${token}`
//     });
//   }

//   // ==================== RFQ MAIN ENDPOINTS ====================

//   /**
//    * Create a new RFQ in DRAFT status
//    * POST /leadcapture/api/rfq/{buyerId}/{locationId}/{userId}
//    */
//   createRFQ(buyerId: number, locationId: number, userId: number, rfq: RFQ): Observable<RFQ> {
//     const url = `${this.API}/rfq/${buyerId}/${locationId}/${userId}`;
//     console.log(`%c[CREATE RFQ]`, 'color: #0066cc; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, rfq, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Created]`, 'color: #00aa00;', `ID: ${rfq.id}, Number: ${rfq.rfqNumber}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Create RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get all RFQs for a buyer
//    * GET /leadcapture/api/rfq/buyer/{buyerId}
//    */
//   getRFQsByBuyer(buyerId: number): Observable<RFQ[]> {
//     const url = `${this.API}/rfq/buyer/${buyerId}`;
//     console.log(`%c[FETCH RFQs]`, 'color: #0066cc; font-weight: bold;', 'GET', url);

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         if (response.data && Array.isArray(response.data)) {
//           return response.data;
//         } else if (Array.isArray(response)) {
//           return response;
//         }
//         return [];
//       }),
//       tap((rfqs: RFQ[]) => {
//         console.log(`%c[✓ RFQs Fetched]`, 'color: #00aa00;', `Total: ${rfqs.length}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch RFQs failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get RFQ by ID
//    * GET /leadcapture/api/rfq/{id}
//    */
//   getRFQById(rfqId: number): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}`;
//     console.log(`%c[FETCH RFQ]`, 'color: #0066cc; font-weight: bold;', 'GET', url);

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Fetched]`, 'color: #00aa00;', `ID: ${rfq.id}, Title: ${rfq.rfqTitle}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get all RFQs
//    * GET /leadcapture/api/rfq
//    */
//   getAllRFQs(): Observable<RFQ[]> {
//     const url = `${this.API}/rfq`;
//     console.log(`%c[FETCH ALL RFQs]`, 'color: #0066cc; font-weight: bold;', 'GET', url);

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         if (response.data && Array.isArray(response.data)) {
//           return response.data;
//         } else if (Array.isArray(response)) {
//           return response;
//         }
//         return [];
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch all RFQs failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get RFQs by status
//    * GET /leadcapture/api/rfq/buyer/{buyerId}/status/{status}
//    */
//   getRFQsByStatus(buyerId: number, status: RFQStatus): Observable<RFQ[]> {
//     const url = `${this.API}/rfq/buyer/${buyerId}/status/${status}`;

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         if (response.data && Array.isArray(response.data)) {
//           return response.data;
//         } else if (Array.isArray(response)) {
//           return response;
//         }
//         return [];
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch RFQs by status failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Update RFQ
//    * PUT /leadcapture/api/rfq/{id}
//    */
//   updateRFQ(rfqId: number, rfq: Partial<RFQ>): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}`;
//     console.log(`%c[UPDATE RFQ]`, 'color: #ff9900; font-weight: bold;', 'PUT', url);

//     return this.http.put<any>(url, rfq, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((updated: RFQ) => {
//         console.log(`%c[✓ RFQ Updated]`, 'color: #00aa00;', `ID: ${updated.id}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Update RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Delete RFQ (soft delete)
//    * DELETE /leadcapture/api/rfq/{id}
//    */
//   deleteRFQ(rfqId: number): Observable<any> {
//     const url = `${this.API}/rfq/${rfqId}`;
//     console.log(`%c[DELETE RFQ]`, 'color: #cc0000; font-weight: bold;', 'DELETE', url);

//     return this.http.delete<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       tap(() => {
//         console.log(`%c[✓ RFQ Deleted]`, 'color: #00aa00;', `ID: ${rfqId}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Delete RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== RFQ WORKFLOW ENDPOINTS ====================

//   /**
//    * Publish RFQ (move from DRAFT to PUBLISHED or AWAITING_APPROVAL)
//    * POST /leadcapture/api/rfq/{id}/publish
//    */
//   publishRFQ(rfqId: number): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/publish`;
//     console.log(`%c[PUBLISH RFQ]`, 'color: #ff9900; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, {}, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Published]`, 'color: #00aa00;', `Status: ${rfq.status}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Publish RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Approve RFQ
//    * POST /leadcapture/api/rfq/{id}/approve?approverId={approverId}&comments={comments}
//    */
//   approveRFQ(rfqId: number, approverId: number, comments: string = ''): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/approve?approverId=${approverId}&comments=${comments}`;
//     console.log(`%c[APPROVE RFQ]`, 'color: #ff9900; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, {}, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Approved]`, 'color: #00aa00;', `ID: ${rfq.id}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Approve RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Reject RFQ
//    * POST /leadcapture/api/rfq/{id}/reject?rejectedBy={rejectedBy}&comments={comments}
//    */
//   rejectRFQ(rfqId: number, rejectedBy: number, comments: string = ''): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/reject?rejectedBy=${rejectedBy}&comments=${comments}`;
//     console.log(`%c[REJECT RFQ]`, 'color: #ff9900; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, {}, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Rejected]`, 'color: #00aa00;', `ID: ${rfq.id}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Reject RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Close RFQ
//    * POST /leadcapture/api/rfq/{id}/close
//    */
//   closeRFQ(rfqId: number): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/close`;
//     console.log(`%c[CLOSE RFQ]`, 'color: #ff9900; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, {}, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ RFQ Closed]`, 'color: #00aa00;', `ID: ${rfq.id}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Close RFQ failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== RFQ ITEM ENDPOINTS ====================

//   /**
//    * Add item to RFQ
//    * POST /leadcapture/api/rfq-item/{rfqId}
//    */
//   addItemToRFQ(rfqId: number, item: RFQItem): Observable<RFQItem> {
//     const url = `${this.API}/rfq-item/${rfqId}`;
//     console.log(`%c[ADD ITEM]`, 'color: #0066cc; font-weight: bold;', 'POST', url);

//     return this.http.post<any>(url, item, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((item: RFQItem) => {
//         console.log(`%c[✓ Item Added]`, 'color: #00aa00;', `Item: ${item.itemDescription}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Add item failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get items for RFQ
//    * GET /leadcapture/api/rfq-item/rfq/{rfqId}
//    */
//   getItemsByRFQ(rfqId: number): Observable<RFQItem[]> {
//     const url = `${this.API}/rfq-item/rfq/${rfqId}`;

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         if (response.data && Array.isArray(response.data)) {
//           return response.data;
//         } else if (Array.isArray(response)) {
//           return response;
//         }
//         return [];
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch items failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Update item
//    * PUT /leadcapture/api/rfq-item/{id}
//    */
//   updateRFQItem(itemId: number, item: Partial<RFQItem>): Observable<RFQItem> {
//     const url = `${this.API}/rfq-item/${itemId}`;

//     return this.http.put<any>(url, item, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       catchError(error => {
//         console.error(`%c[ERROR] Update item failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Delete item
//    * DELETE /leadcapture/api/rfq-item/{id}
//    */
//   deleteRFQItem(itemId: number): Observable<any> {
//     const url = `${this.API}/rfq-item/${itemId}`;

//     return this.http.delete<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR] Delete item failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== RFQ ATTACHMENT ENDPOINTS ====================

//   /**
//    * Add attachment to RFQ
//    * POST /leadcapture/api/rfq-attachment/{rfqId}
//    */
//   addAttachmentToRFQ(rfqId: number, attachment: RFQAttachment): Observable<RFQAttachment> {
//     const url = `${this.API}/rfq-attachment/${rfqId}`;

//     return this.http.post<any>(url, attachment, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       catchError(error => {
//         console.error(`%c[ERROR] Add attachment failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Get attachments for RFQ
//    * GET /leadcapture/api/rfq-attachment/rfq/{rfqId}
//    */
//   getAttachmentsByRFQ(rfqId: number): Observable<RFQAttachment[]> {
//     const url = `${this.API}/rfq-attachment/rfq/${rfqId}`;

//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         if (response.data && Array.isArray(response.data)) {
//           return response.data;
//         } else if (Array.isArray(response)) {
//           return response;
//         }
//         return [];
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Fetch attachments failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * Delete attachment
//    * DELETE /leadcapture/api/rfq-attachment/{id}
//    */
//   deleteAttachment(attachmentId: number): Observable<any> {
//     const url = `${this.API}/rfq-attachment/${attachmentId}`;

//     return this.http.delete<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR] Delete attachment failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== SUPPLIER ENDPOINTS ====================

//   /**
//    * Add suppliers to RFQ
//    * POST /leadcapture/api/rfq/{id}/suppliers
//    */
//   addSuppliersToRFQ(rfqId: number, supplierIds: number[]): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/suppliers`;

//     return this.http.post<any>(url, supplierIds, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ Suppliers Added]`, 'color: #00aa00;', `Count: ${rfq.selectedSupplierIds?.length}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Add suppliers failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== APPROVER ENDPOINTS ====================

//   /**
//    * Add approvers to RFQ
//    * POST /leadcapture/api/rfq/{id}/approvers
//    */
//   addApproversToRFQ(rfqId: number, approverIds: number[]): Observable<RFQ> {
//     const url = `${this.API}/rfq/${rfqId}/approvers`;

//     return this.http.post<any>(url, approverIds, { headers: this.getAuthHeaders() }).pipe(
//       map(response => this.extractData(response)),
//       tap((rfq: RFQ) => {
//         console.log(`%c[✓ Approvers Added]`, 'color: #00aa00;', `Count: ${rfq.approverIds?.length}`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Add approvers failed`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== HELPER METHODS ====================

//   /**
//    * Extract data from API response wrapper
//    */
//   private extractData<T>(response: any): T {
//     if (response.data) {
//       return response.data as T;
//     } else if (response.id) {
//       return response as T;
//     }
//     return response as T;
//   }

//   /**
//    * Get status badge color
//    */
//   getStatusBadgeClass(status: RFQStatus | string): string {
//     switch (status) {
//       case RFQStatus.DRAFT:
//         return 'bg-secondary';
//       case RFQStatus.PUBLISHED:
//         return 'bg-primary';
//       case RFQStatus.AWAITING_APPROVAL:
//         return 'bg-warning text-dark';
//       case RFQStatus.APPROVED:
//         return 'bg-success';
//       case RFQStatus.RESPONSES_RECEIVED:
//         return 'bg-info';
//       case RFQStatus.CLOSED:
//         return 'bg-dark';
//       case RFQStatus.CANCELLED:
//         return 'bg-danger';
//       default:
//         return 'bg-secondary';
//     }
//   }

//   /**
//    * Get priority badge color
//    */
//   getPriorityBadgeClass(priority: string): string {
//     switch (priority?.toUpperCase()) {
//       case 'LOW':
//         return 'bg-info';
//       case 'MEDIUM':
//         return 'bg-primary';
//       case 'HIGH':
//         return 'bg-warning text-dark';
//       case 'URGENT':
//         return 'bg-danger';
//       default:
//         return 'bg-secondary';
//     }
//   }
// }