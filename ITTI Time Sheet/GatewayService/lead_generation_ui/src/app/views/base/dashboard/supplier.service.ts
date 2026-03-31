

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Supplier } from './supplier.model';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  // ✅ FIXED: Correct API base URL matching your backend
  private API = environment.API_URL + 'leadcapture/api';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ==================== CREATE SUPPLIER WITH COMPLETE HIERARCHY ====================

  /**
   * ✅ FIXED: Create supplier with complete nested hierarchy in single API call
   * Backend expects: Supplier → Locations[] → Departments[] → Users[]
   */
  createCompleteHierarchy(supplier: Supplier): Observable<any> {
    const url = `${this.API}/supplier`;
    console.log(`%c[CREATE SUPPLIER] POST ${url}`, 'color: #0066cc; font-weight: bold;');
    console.log('%c[PAYLOAD]', 'color: #0066cc;', JSON.stringify(supplier, null, 2));
    
    // ✅ Send complete nested structure to backend
    return this.http.post(url, supplier, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log('%c[✓ SUCCESS] Supplier created with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
      }),
      map((response: any) => {
        // ✅ Handle response wrapper: { success: true, message: "...", data: {...} }
        return response?.data || response;
      }),
      catchError(error => {
        console.error('%c[ERROR] Failed to create supplier', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== UPDATE SUPPLIER WITH COMPLETE HIERARCHY ====================

  /**
   * ✅ FIXED: Update supplier with complete nested hierarchy
   */
  updateCompleteHierarchy(supplierId: number, supplier: Supplier): Observable<any> {
    const url = `${this.API}/supplier/${supplierId}`;
    console.log(`%c[UPDATE SUPPLIER] PUT ${url}`, 'color: #ff9800; font-weight: bold;');
    console.log('%c[PAYLOAD]', 'color: #ff9800;', JSON.stringify(supplier, null, 2));
    
    // ✅ Send complete nested structure to backend
    return this.http.put(url, supplier, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log('%c[✓ SUCCESS] Supplier updated with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
      }),
      map((response: any) => {
        return response?.data || response;
      }),
      catchError(error => {
        console.error('%c[ERROR] Failed to update supplier', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== GET ALL SUPPLIERS ====================

  /**
   * ✅ FIXED: Get all suppliers with complete hierarchy
   * Backend returns: Array of suppliers directly or wrapped response
   */
  getAllSuppliers(): Observable<Supplier[]> {
    const url = `${this.API}/supplier`;
    console.log(`%c[GET ALL SUPPLIERS] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
        // ✅ Handle different response structures
        let suppliers: Supplier[] = [];
        
        if (response?.data && Array.isArray(response.data)) {
          // Wrapped response: { success: true, data: [...] }
          suppliers = response.data;
        } else if (Array.isArray(response)) {
          // Direct array response: [...]
          suppliers = response;
        } else {
          console.warn('Unexpected response structure:', response);
          suppliers = [];
        }
        
        console.log(`%c[✓ SUCCESS] Loaded ${suppliers.length} suppliers`, 'color: #00aa00; font-weight: bold;');
        return suppliers;
      }),
      catchError(error => {
        console.error('%c[ERROR] Failed to load suppliers', 'color: #cc0000; font-weight: bold;', error);
        return of([]); // Return empty array instead of throwing
      })
    );
  }

  // ==================== GET SUPPLIER BY ID ====================

  /**
   * ✅ FIXED: Get single supplier with complete hierarchy
   */
  getSupplierById(id: number): Observable<Supplier> {
    const url = `${this.API}/supplier/${id}`;
    console.log(`%c[GET SUPPLIER] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
        // ✅ Handle wrapped response
        let supplier: Supplier;
        
        if (response?.data) {
          supplier = response.data;
        } else if (response?.id) {
          supplier = response;
        } else {
          throw new Error('Invalid response structure');
        }
        
        console.log(`%c[✓ SUCCESS] Loaded supplier: ${supplier.companyName}`, 'color: #00aa00; font-weight: bold;');
        return supplier;
      }),
      catchError(error => {
        console.error(`%c[ERROR] Failed to load supplier ${id}`, 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== DELETE SUPPLIER ====================

  /**
   * ✅ FIXED: Delete supplier (soft delete - cascades to all related entities)
   */
  deleteSupplier(id: number): Observable<any> {
    const url = `${this.API}/supplier/${id}`;
    console.log(`%c[DELETE SUPPLIER] DELETE ${url}`, 'color: #cc0000; font-weight: bold;');
    
    return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log(`%c[✓ SUCCESS] Supplier ${id} deleted`, 'color: #00aa00; font-weight: bold;', response);
      }),
      catchError(error => {
        console.error(`%c[ERROR] Failed to delete supplier ${id}`, 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }
}