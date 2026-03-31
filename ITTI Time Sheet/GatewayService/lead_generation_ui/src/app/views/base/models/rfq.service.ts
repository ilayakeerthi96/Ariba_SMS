import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {
  RFQ,
  RFQCreatePayload,
  RFQItemPayload,
  RFQResponse,
  RFQListResponse
} from './rfq.model';

@Injectable({
  providedIn: 'root'
})
export class RFQService {
  private API = environment.API_URL + 'leadcapture/api';

  constructor(private http: HttpClient) {}

  // ==================== HEADER MANAGEMENT ====================
  
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ==================== CREATE RFQ ====================

  /**
   * POST /api/rfq/{buyerId}/{locationId}/{userId}
   * Create a new RFQ
   */
  createRFQ(
    buyerId: number,
    locationId: number,
    userId: number,
    payload: RFQCreatePayload
  ): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${buyerId}/${locationId}/${userId}`;
    
    console.log('%c[CREATE RFQ]', 'color: #0066cc; font-weight: bold;', {
      buyerId,
      locationId,
      userId,
      rfqTitle: payload.rfqTitle
    });

    return this.http.post<RFQResponse>(url, payload, { headers: this.getAuthHeaders() }).pipe(
      tap((response) => {
        console.log('%c[✓ RFQ Created]', 'color: #00aa00; font-weight: bold;', response.data?.rfqNumber);
      }),
      catchError((error) => {
        console.error('%c[✗ Create Error]', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== UPDATE RFQ ====================

  /**
   * PUT /api/rfq/{id}
   * Update RFQ details (only in DRAFT status)
   */
  updateRFQ(rfqId: number, payload: RFQCreatePayload): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}`;
    
    console.log('%c[UPDATE RFQ]', 'color: #0066cc; font-weight: bold;', rfqId);

    return this.http.put<RFQResponse>(url, payload, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Updated]', 'color: #00aa00; font-weight: bold;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Update Error]', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== RFQ ITEMS ====================

  /**
   * POST /api/rfq-item/{rfqId}
   * Add an item to RFQ
   */
  addItemToRFQ(rfqId: number, item: RFQItemPayload): Observable<any> {
    const url = `${this.API}/rfq-item/${rfqId}`;
    
    console.log('%c[ADD ITEM]', 'color: #0066cc;', item.itemDescription);

    return this.http.post<any>(url, item, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ Item Added]', 'color: #00aa00;', item.itemDescription);
      }),
      catchError((error) => {
        console.error('%c[✗ Item Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * PUT /api/rfq-item/{itemId}
   * Update RFQ item
   */
  updateRFQItem(itemId: number, item: RFQItemPayload): Observable<any> {
    const url = `${this.API}/rfq-item/${itemId}`;
    
    console.log('%c[UPDATE ITEM]', 'color: #0066cc;', itemId);

    return this.http.put<any>(url, item, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ Item Updated]', 'color: #00aa00;', itemId);
      }),
      catchError((error) => {
        console.error('%c[✗ Update Item Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * DELETE /api/rfq-item/{itemId}
   * Delete RFQ item
   */
  deleteRFQItem(itemId: number): Observable<any> {
    const url = `${this.API}/rfq-item/${itemId}`;
    
    console.log('%c[DELETE ITEM]', 'color: #cc0000;', itemId);

    return this.http.delete<any>(url, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ Item Deleted]', 'color: #00aa00;', itemId);
      }),
      catchError((error) => {
        console.error('%c[✗ Delete Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * GET /api/rfq-item/rfq/{rfqId}
   * Get all items for an RFQ
   */
  getRFQItems(rfqId: number): Observable<any> {
    const url = `${this.API}/rfq-item/rfq/${rfqId}`;
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      catchError((error) => {
        console.error('%c[✗ Get Items Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== SUPPLIERS ====================

  /**
   * POST /api/rfq/{id}/suppliers
   * Add suppliers to RFQ
   */
  addSuppliersToRFQ(rfqId: number, supplierIds: number[]): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/suppliers`;
    
    console.log('%c[ADD SUPPLIERS]', 'color: #0066cc;', supplierIds);

    return this.http.post<RFQResponse>(url, supplierIds, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ Suppliers Added]', 'color: #00aa00;', supplierIds.length);
      }),
      catchError((error) => {
        console.error('%c[✗ Suppliers Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== APPROVERS ====================

  /**
   * POST /api/rfq/{id}/approvers
   * Add approvers to RFQ
   */
  addApproversToRFQ(rfqId: number, approverIds: number[]): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/approvers`;
    
    console.log('%c[ADD APPROVERS]', 'color: #0066cc;', approverIds);

    return this.http.post<RFQResponse>(url, approverIds, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ Approvers Added]', 'color: #00aa00;', approverIds.length);
      }),
      catchError((error) => {
        console.error('%c[✗ Approvers Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== WORKFLOW ====================

  /**
   * POST /api/rfq/{id}/publish
   * Publish RFQ to suppliers
   */
  publishRFQ(rfqId: number): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/publish`;
    
    console.log('%c[PUBLISH RFQ]', 'color: #0066cc; font-weight: bold;', rfqId);

    return this.http.post<RFQResponse>(url, {}, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Published]', 'color: #00aa00; font-weight: bold;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Publish Error]', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * POST /api/rfq/{id}/approve
   * Approve RFQ (for approval workflow)
   */
  approveRFQ(
    rfqId: number,
    approverId: number,
    comments?: string
  ): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/approve`;
    
    const params = new URLSearchParams();
    params.set('approverId', approverId.toString());
    if (comments) {
      params.set('comments', comments);
    }
    
    console.log('%c[APPROVE RFQ]', 'color: #00aa00; font-weight: bold;', rfqId);

    return this.http.post<RFQResponse>(
      `${url}?${params}`,
      {},
      { headers: this.getAuthHeaders() }
    ).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Approved]', 'color: #00aa00; font-weight: bold;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Approve Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * POST /api/rfq/{id}/reject
   * Reject RFQ (for approval workflow)
   */
  rejectRFQ(
    rfqId: number,
    rejectedBy: number,
    comments: string
  ): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/reject`;
    
    const params = new URLSearchParams();
    params.set('rejectedBy', rejectedBy.toString());
    params.set('comments', comments);
    
    console.log('%c[REJECT RFQ]', 'color: #cc0000; font-weight: bold;', rfqId);

    return this.http.post<RFQResponse>(
      `${url}?${params}`,
      {},
      { headers: this.getAuthHeaders() }
    ).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Rejected]', 'color: #00aa00;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Reject Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * POST /api/rfq/{id}/close
   * Close RFQ
   */
  closeRFQ(rfqId: number): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}/close`;
    
    console.log('%c[CLOSE RFQ]', 'color: #cc0000; font-weight: bold;', rfqId);

    return this.http.post<RFQResponse>(url, {}, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Closed]', 'color: #00aa00;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Close Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== READ OPERATIONS ====================

  /**
   * GET /api/rfq/{id}
   * Get RFQ by ID with full details
   */
  getRFQById(rfqId: number): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}`;
    
    console.log('%c[GET RFQ]', 'color: #0066cc;', rfqId);

    return this.http.get<RFQResponse>(url, { headers: this.getAuthHeaders() }).pipe(
      tap((response) => {
        console.log('%c[✓ RFQ Loaded]', 'color: #00aa00;', response.data?.rfqNumber);
      }),
      catchError((error) => {
        console.error('%c[✗ Load Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * GET /api/rfq
   * Get all RFQs
   */
  getAllRFQs(): Observable<RFQListResponse> {
    const url = `${this.API}/rfq`;
    
    console.log('%c[GET ALL RFQs]', 'color: #0066cc;');

    return this.http.get<RFQListResponse>(url, { headers: this.getAuthHeaders() }).pipe(
      tap((response) => {
        console.log('%c[✓ RFQs Loaded]', 'color: #00aa00;', response.count || 0);
      }),
      catchError((error) => {
        console.error('%c[✗ Load Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * GET /api/rfq/buyer/{buyerId}
   * Get all RFQs for a buyer
   */
  getRFQsByBuyer(buyerId: number): Observable<RFQListResponse> {
    const url = `${this.API}/rfq/buyer/${buyerId}`;
    
    console.log('%c[GET RFQs BY BUYER]', 'color: #0066cc;', buyerId);

    return this.http.get<RFQListResponse>(url, { headers: this.getAuthHeaders() }).pipe(
      catchError((error) => {
        console.error('%c[✗ Load Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * GET /api/rfq/buyer/{buyerId}/status/{status}
   * Get RFQs by buyer and status
   */
  getRFQsByBuyerAndStatus(buyerId: number, status: string): Observable<RFQListResponse> {
    const url = `${this.API}/rfq/buyer/${buyerId}/status/${status}`;
    
    console.log('%c[GET RFQs BY STATUS]', 'color: #0066cc;', { buyerId, status });

    return this.http.get<RFQListResponse>(url, { headers: this.getAuthHeaders() }).pipe(
      catchError((error) => {
        console.error('%c[✗ Load Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== DELETE ====================

  /**
   * DELETE /api/rfq/{id}
   * Delete RFQ (soft delete)
   */
  deleteRFQ(rfqId: number): Observable<RFQResponse> {
    const url = `${this.API}/rfq/${rfqId}`;
    
    console.log('%c[DELETE RFQ]', 'color: #cc0000; font-weight: bold;', rfqId);

    return this.http.delete<RFQResponse>(url, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log('%c[✓ RFQ Deleted]', 'color: #00aa00;', rfqId);
      }),
      catchError((error) => {
        console.error('%c[✗ Delete Error]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }
}