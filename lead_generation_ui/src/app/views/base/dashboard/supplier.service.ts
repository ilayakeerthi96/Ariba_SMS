

// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { environment } from '../../../environments/environment';
// import { Observable, of } from 'rxjs';
// import { tap, catchError, map } from 'rxjs/operators';
// import { throwError } from 'rxjs';
// import { Supplier } from './supplier.model';

// @Injectable({
//   providedIn: 'root'
// })
// export class SupplierService {
//   // ✅ FIXED: Correct API base URL matching your backend
//   private API = environment.API_URL + 'leadcapture/api';

//   constructor(private http: HttpClient) {}

//   private getAuthHeaders(): HttpHeaders {
//     const token = localStorage.getItem('token') || '';
//     return new HttpHeaders({
//       'Content-Type': 'application/json',
//       'Authorization': `Bearer ${token}`
//     });
//   }

//   // ==================== CREATE SUPPLIER WITH COMPLETE HIERARCHY ====================

//   /**
//    * ✅ FIXED: Create supplier with complete nested hierarchy in single API call
//    * Backend expects: Supplier → Locations[] → Departments[] → Users[]
//    */
//   createCompleteHierarchy(supplier: Supplier): Observable<any> {
//     const url = `${this.API}/supplier`;
//     console.log(`%c[CREATE SUPPLIER] POST ${url}`, 'color: #0066cc; font-weight: bold;');
//     console.log('%c[PAYLOAD]', 'color: #0066cc;', JSON.stringify(supplier, null, 2));
    
//     // ✅ Send complete nested structure to backend
//     return this.http.post(url, supplier, { headers: this.getAuthHeaders() }).pipe(
//       tap(response => {
//         console.log('%c[✓ SUCCESS] Supplier created with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
//       }),
//       map((response: any) => {
//         // ✅ Handle response wrapper: { success: true, message: "...", data: {...} }
//         return response?.data || response;
//       }),
//       catchError(error => {
//         console.error('%c[ERROR] Failed to create supplier', 'color: #cc0000; font-weight: bold;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== UPDATE SUPPLIER WITH COMPLETE HIERARCHY ====================

//   /**
//    * ✅ FIXED: Update supplier with complete nested hierarchy
//    */
//   updateCompleteHierarchy(supplierId: number, supplier: Supplier): Observable<any> {
//     const url = `${this.API}/supplier/${supplierId}`;
//     console.log(`%c[UPDATE SUPPLIER] PUT ${url}`, 'color: #ff9800; font-weight: bold;');
//     console.log('%c[PAYLOAD]', 'color: #ff9800;', JSON.stringify(supplier, null, 2));
    
//     // ✅ Send complete nested structure to backend
//     return this.http.put(url, supplier, { headers: this.getAuthHeaders() }).pipe(
//       tap(response => {
//         console.log('%c[✓ SUCCESS] Supplier updated with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
//       }),
//       map((response: any) => {
//         return response?.data || response;
//       }),
//       catchError(error => {
//         console.error('%c[ERROR] Failed to update supplier', 'color: #cc0000; font-weight: bold;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== GET ALL SUPPLIERS ====================

//   /**
//    * ✅ FIXED: Get all suppliers with complete hierarchy
//    * Backend returns: Array of suppliers directly or wrapped response
//    */
//   getAllSuppliers(): Observable<Supplier[]> {
//     const url = `${this.API}/supplier`;
//     console.log(`%c[GET ALL SUPPLIERS] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
//         // ✅ Handle different response structures
//         let suppliers: Supplier[] = [];
        
//         if (response?.data && Array.isArray(response.data)) {
//           // Wrapped response: { success: true, data: [...] }
//           suppliers = response.data;
//         } else if (Array.isArray(response)) {
//           // Direct array response: [...]
//           suppliers = response;
//         } else {
//           console.warn('Unexpected response structure:', response);
//           suppliers = [];
//         }
        
//         console.log(`%c[✓ SUCCESS] Loaded ${suppliers.length} suppliers`, 'color: #00aa00; font-weight: bold;');
//         return suppliers;
//       }),
//       catchError(error => {
//         console.error('%c[ERROR] Failed to load suppliers', 'color: #cc0000; font-weight: bold;', error);
//         return of([]); // Return empty array instead of throwing
//       })
//     );
//   }

//   // ==================== GET SUPPLIER BY ID ====================

//   /**
//    * ✅ FIXED: Get single supplier with complete hierarchy
//    */
//   getSupplierById(id: number): Observable<Supplier> {
//     const url = `${this.API}/supplier/${id}`;
//     console.log(`%c[GET SUPPLIER] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
//         // ✅ Handle wrapped response
//         let supplier: Supplier;
        
//         if (response?.data) {
//           supplier = response.data;
//         } else if (response?.id) {
//           supplier = response;
//         } else {
//           throw new Error('Invalid response structure');
//         }
        
//         console.log(`%c[✓ SUCCESS] Loaded supplier: ${supplier.companyName}`, 'color: #00aa00; font-weight: bold;');
//         return supplier;
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Failed to load supplier ${id}`, 'color: #cc0000; font-weight: bold;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== DELETE SUPPLIER ====================

//   /**
//    * ✅ FIXED: Delete supplier (soft delete - cascades to all related entities)
//    */
//   deleteSupplier(id: number): Observable<any> {
//     const url = `${this.API}/supplier/${id}`;
//     console.log(`%c[DELETE SUPPLIER] DELETE ${url}`, 'color: #cc0000; font-weight: bold;');
    
//     return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
//       tap(response => {
//         console.log(`%c[✓ SUCCESS] Supplier ${id} deleted`, 'color: #00aa00; font-weight: bold;', response);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Failed to delete supplier ${id}`, 'color: #cc0000; font-weight: bold;', error);
//         return throwError(() => error);
//       })
//     );
//   }
// }

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Supplier } from './supplier.model';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
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

  createCompleteHierarchy(supplier: Supplier): Observable<any> {
    const url = `${this.API}/supplier`;
    console.log(`%c[CREATE SUPPLIER] POST ${url}`, 'color: #0066cc; font-weight: bold;');
    console.log('%c[PAYLOAD]', 'color: #0066cc;', JSON.stringify(supplier, null, 2));

    return this.http.post(url, supplier, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log('%c[✓ SUCCESS] Supplier created with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
      }),
      map((response: any) => response?.data || response),
      catchError(error => {
        console.error('%c[ERROR] Failed to create supplier', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== UPDATE SUPPLIER WITH COMPLETE HIERARCHY ====================

  updateCompleteHierarchy(supplierId: number, supplier: Supplier): Observable<any> {
    const url = `${this.API}/supplier/${supplierId}`;
    console.log(`%c[UPDATE SUPPLIER] PUT ${url}`, 'color: #ff9800; font-weight: bold;');

    return this.http.put(url, supplier, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log('%c[✓ SUCCESS] Supplier updated', 'color: #00aa00; font-weight: bold;', response);
      }),
      map((response: any) => response?.data || response),
      catchError(error => {
        console.error('%c[ERROR] Failed to update supplier', 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== GET ALL SUPPLIERS ====================

  getAllSuppliers(): Observable<Supplier[]> {
    const url = `${this.API}/supplier`;
    console.log(`%c[GET ALL SUPPLIERS] GET ${url}`, 'color: #0066cc; font-weight: bold;');

    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        let suppliers: Supplier[] = [];
        if (response?.data && Array.isArray(response.data)) {
          suppliers = response.data;
        } else if (Array.isArray(response)) {
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
        return of([]);
      })
    );
  }

  // ==================== GET SUPPLIER BY ID ====================

  getSupplierById(id: number): Observable<Supplier> {
    const url = `${this.API}/supplier/${id}`;
    console.log(`%c[GET SUPPLIER] GET ${url}`, 'color: #0066cc; font-weight: bold;');

    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
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

  // ==================== LOGO METHODS ====================

  /**
   * ✅ NEW: Get supplier logo as base64 data URL.
   * Backend returns plain text (data:image/...;base64,...) — responseType: 'text' is critical.
   * Mirrors BuyerService.getBuyerLogoBase64() exactly.
   */
  getSupplierLogoBase64(supplierId: number): Observable<string | null> {
    const url = `${this.API}/supplier/${supplierId}/logo/base64`;
    console.log('%c[GET SUPPLIER LOGO BASE64]', 'color:#06c;font-weight:bold', { supplierId, url });

    return this.http.get(url, {
      // ✅ CRITICAL: responseType 'text' — backend returns plain text, not JSON
      responseType: 'text',
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
      })
    }).pipe(
      map((raw: string) => {
        const trimmed = raw ? raw.trim() : '';
        if (!trimmed || trimmed === 'null') {
          console.log('%c[SUPPLIER LOGO]', 'color:#f90', 'No logo for supplier:', supplierId);
          return null;
        }
        console.log('%c[SUPPLIER LOGO OK]', 'color:#0a0',
          `${trimmed.substring(0, 40)}… (${trimmed.length} chars)`);
        return trimmed;
      }),
      catchError((err: HttpErrorResponse) => {
        console.warn('%c[SUPPLIER LOGO ERR]', 'color:#c00', err.status, err.message);
        return of(null); // Silently return null — UI stays graceful
      })
    );
  }

  /**
   * ✅ NEW: Get supplier logo raw image URL (for use in <img [src]="...">)
   */
  getSupplierLogoUrl(supplierId: number): string {
    return `${this.API}/supplier/${supplierId}/logo`;
  }

  /**
   * ✅ Helper to convert File to base64 data URL (shared with create component)
   */
  convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = error => reject(error);
      reader.readAsDataURL(file);
    });
  }

  // ==================== OTHER EXISTING METHODS ====================

  supplierServiceHealth(): Observable<any> {
    const url = `${this.API}/supplier/health`;
    return this.http.get<any>(url, { headers: this.getAuthHeaders() });
  }

  supplierLogin(loginRequest: any): Observable<any> {
    const url = `${this.API}/supplier/login`;
    return this.http.post<any>(url, loginRequest).pipe(
      tap(response => {
        console.log('%c[SUPPLIER LOGIN RESPONSE]', 'color: #00aa00; font-weight: bold;', response);
      }),
      catchError(error => {
        console.error('%c[SUPPLIER LOGIN ERROR]', 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }
}