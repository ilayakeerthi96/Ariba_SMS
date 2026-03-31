
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { switchMap, tap, catchError, map } from 'rxjs/operators';
import { Buyer, Location, Department, User } from './buyer-b.model';

@Injectable({
  providedIn: 'root'
})
export class BuyerService {
  private API = 'http://localhost:9092/leadcapture/api';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ==================== CREATE BUYER WITH COMPLETE HIERARCHY ====================

  createCompleteHierarchy(buyer: Buyer, adminId?: number): Observable<any> {
    console.log('========================================');
    console.log('🔵 CREATE BUYER SERVICE CALL');
    console.log('========================================');
    console.log('Admin ID:', adminId);
    console.log('Organization Company:', buyer.organizationCompanyName);

    if (adminId) {
      console.log('✅ Using Organization Admin endpoint');

      if (!buyer.organizationCompanyName) {
        console.error('❌ organizationCompanyName is missing!');
        return throwError(() => new Error('organizationCompanyName is required'));
      }

      const url = `${this.API}/organization-admin/${adminId}/buyer`;

      console.log('📍 URL:', url);
      console.log('📦 Payload:', {
        companyName: buyer.companyName,
        organizationCompanyName: buyer.organizationCompanyName,
        locationsCount: buyer.locations?.length || 0
      });

      return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
        tap(response => {
          console.log('========================================');
          console.log('✅ BUYER CREATED BY ORG ADMIN');
          console.log('========================================');
          console.log('Response:', response);
        }),
        map((response: any) => response?.data || response),
        catchError(error => {
          console.error('========================================');
          console.error('❌ ERROR CREATING BUYER');
          console.error('========================================');
          console.error('Status:', error.status);
          console.error('Message:', error.error?.message || error.message);
          return throwError(() => error);
        })
      );
    } else {
      console.warn('⚠️ Direct buyer creation is deprecated!');
      const url = `${this.API}/buyer`;

      return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
        tap(response => console.log('✅ Buyer created:', response)),
        map((response: any) => response?.data || response),
        catchError(error => {
          console.error('❌ Error creating buyer:', error);
          return throwError(() => error);
        })
      );
    }
  }

  // ==================== GET BUYERS ====================

  getBuyersByOrganizationAdmin(adminId: number): Observable<Buyer[]> {
    const url = `${this.API}/organization-admin/${adminId}/buyers`;
    console.log(`📥 Fetching buyers for Organization Admin ID: ${adminId}`);

    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        let buyers: Buyer[] = [];
        if (response?.data && Array.isArray(response.data)) {
          buyers = response.data;
        } else if (Array.isArray(response)) {
          buyers = response;
        } else {
          console.warn('⚠️ Unexpected response structure:', response);
          buyers = [];
        }
        console.log(`✅ Buyers fetched: ${buyers.length}`);
        return buyers;
      }),
      catchError(error => {
        console.error(`❌ Error fetching buyers for admin ${adminId}:`, error);
        return throwError(() => error);
      })
    );
  }

  /** @deprecated Use getBuyersByOrganizationAdmin instead */
  getBuyersByOrgAdmin(adminId: number): Observable<Buyer[]> {
    return this.getBuyersByOrganizationAdmin(adminId);
  }

  getAllBuyers(): Observable<Buyer[]> {
    const url = `${this.API}/buyer`;
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        if (response?.data && Array.isArray(response.data)) return response.data;
        if (Array.isArray(response)) return response;
        return [];
      }),
      catchError(() => of([]))
    );
  }

  getBuyersByOrganizationCompanyName(companyName: string): Observable<Buyer[]> {
    const url = `${this.API}/organization-admin/company/${companyName}/buyers`;
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        if (response?.data && Array.isArray(response.data)) return response.data;
        if (Array.isArray(response)) return response;
        return [];
      }),
      catchError(() => of([]))
    );
  }

  // ==================== SINGLE BUYER ====================

  getBuyerById(id: number): Observable<Buyer> {
    const url = `${this.API}/buyer/${id}`;
    console.log(`📥 Fetching buyer ID: ${id}`);

    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        if (response?.data) return response.data;
        if (response?.id) return response;
        throw new Error('Invalid response structure');
      }),
      catchError(error => {
        console.error(`❌ Failed to load buyer ${id}:`, error);
        return throwError(() => error);
      })
    );
  }

  // ==================== UPDATE / DELETE ====================

  updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
    const url = `${this.API}/buyer/${buyerId}`;
    return this.http.put(url, buyer, { headers: this.getAuthHeaders() }).pipe(
      tap(response => console.log('✅ Buyer updated:', response)),
      map((response: any) => response?.data || response),
      catchError(error => {
        console.error('❌ Failed to update buyer:', error);
        return throwError(() => error);
      })
    );
  }

  deleteBuyer(id: number): Observable<any> {
    const url = `${this.API}/buyer/${id}`;
    return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
      tap(() => console.log(`✅ Buyer ${id} deleted`)),
      catchError(error => {
        console.error(`❌ Failed to delete buyer ${id}:`, error);
        return throwError(() => error);
      })
    );
  }

  // ==================== LOGO METHODS ====================

  /**
   * ✅ FIXED: Get buyer logo as base64 data URL.
   * Backend returns plain text (data:image/...;base64,...) — must use responseType: 'text'.
   */
  getBuyerLogoBase64(buyerId: number): Observable<string | null> {
    const url = `${this.API}/buyer/${buyerId}/logo/base64`;
    console.log('📥 Getting logo base64 for buyer:', buyerId);

    return this.http.get(url, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
        // ✅ No Content-Type needed for GET; responseType handles it
      }),
      responseType: 'text'   // ✅ CRITICAL FIX: backend returns plain text, not JSON
    }).pipe(
      map((raw: string) => {
        const trimmed = raw ? raw.trim() : '';
        if (!trimmed || trimmed === 'null') {
          console.log('⚠️ No logo data for buyer:', buyerId);
          return null;
        }
        console.log('✅ Logo loaded for buyer:', buyerId,
          `(${trimmed.substring(0, 30)}… ${trimmed.length} chars)`);
        return trimmed;
      }),
      catchError(err => {
        console.warn('⚠️ Failed to get logo for buyer:', buyerId, err.status);
        return of(null);   // ✅ Silently return null — UI stays graceful
      })
    );
  }

  /**
   * ✅ Get raw logo image URL (for use in <img [src]="...">)
   */
  getBuyerLogoUrl(buyerId: number): string {
    return `${this.API}/buyer/${buyerId}/logo`;
  }

  /**
   * ✅ Helper to convert File to base64 data URL
   */
  convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = error => reject(error);
      reader.readAsDataURL(file);
    });
  }
}