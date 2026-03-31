
// // import { Injectable } from '@angular/core';
// // import { HttpClient, HttpHeaders } from '@angular/common/http';
// // import { environment } from '../../../environments/environment';
// // import { Observable, forkJoin, of } from 'rxjs';
// // import { switchMap, tap, catchError, map } from 'rxjs/operators';
// // import { throwError } from 'rxjs';
// // import { Buyer, Location, Department, User } from './buyer-b.model';

// // @Injectable({
// //   providedIn: 'root'
// // })
// // export class BuyerService {
// //   // ✅ FIXED: Correct API base URL matching your backend
// //   private API = environment.API_URL + 'leadcapture/api';

// //   constructor(private http: HttpClient) {}

// //   private getAuthHeaders(): HttpHeaders {
// //     const token = localStorage.getItem('token') || '';
// //     return new HttpHeaders({
// //       'Content-Type': 'application/json',
// //       'Authorization': `Bearer ${token}`
// //     });
// //   }

// //   // ==================== CREATE BUYER WITH COMPLETE HIERARCHY ====================

// //   /**
// //    * ✅ FIXED: Create buyer with complete nested hierarchy in single API call
// //    * Backend expects: Buyer → Locations[] → Departments[] → Users[]
// //    */
// //   // createCompleteHierarchy(buyer: Buyer): Observable<any> {
// //   //   const url = `${this.API}/buyer`;
// //   //   console.log(`%c[CREATE BUYER] POST ${url}`, 'color: #0066cc; font-weight: bold;');
// //   //   console.log('%c[PAYLOAD]', 'color: #0066cc;', JSON.stringify(buyer, null, 2));
    
// //   //   // ✅ Send complete nested structure to backend
// //   //   return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
// //   //     tap(response => {
// //   //       console.log('%c[✓ SUCCESS] Buyer created with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
// //   //     }),
// //   //     map((response: any) => {
// //   //       // ✅ Handle response wrapper: { success: true, message: "...", data: {...} }
// //   //       return response?.data || response;
// //   //     }),
// //   //     catchError(error => {
// //   //       console.error('%c[ERROR] Failed to create buyer', 'color: #cc0000; font-weight: bold;', error);
// //   //       return throwError(() => error);
// //   //     })
// //   //   );
// //   // }

// //   // ==================== UPDATE BUYER WITH COMPLETE HIERARCHY ====================


// //   // ==================== CREATE BUYER WITH COMPLETE HIERARCHY ====================

// // /**
// //  * ✅ UPDATED: Organization Admin creates buyer with organizationCompanyName
// //  */
// // createCompleteHierarchy(buyer: Buyer, adminId?: number): Observable<any> {
// //   // ✅ NEW: Use Organization Admin endpoint if adminId is provided
// //   const url = adminId 
// //     ? `${this.API}/organization-admin/${adminId}/buyer`
// //     : `${this.API}/buyer`;
    
// //   console.log(`%c[CREATE BUYER] POST ${url}`, 'color: #0066cc; font-weight: bold;');
// //   console.log('%c[PAYLOAD]', 'color: #0066cc;', JSON.stringify(buyer, null, 2));
  
// //   return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
// //     tap(response => {
// //       console.log('%c[✓ SUCCESS] Buyer created with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
// //     }),
// //     map((response: any) => {
// //       return response?.data || response;
// //     }),
// //     catchError(error => {
// //       console.error('%c[ERROR] Failed to create buyer', 'color: #cc0000; font-weight: bold;', error);
// //       return throwError(() => error);
// //     })
// //   );
// // }

// // /**
// //  * ✅ NEW: Get all buyers created by Organization Admin
// //  */
// // getBuyersByOrgAdmin(adminId: number): Observable<Buyer[]> {
// //   const url = `${this.API}/organization-admin/${adminId}/buyers`;
// //   console.log(`%c[GET ORG ADMIN BUYERS] GET ${url}`, 'color: #0066cc; font-weight: bold;');
  
// //   return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
// //     map(response => {
// //       console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
      
// //       let buyers: Buyer[] = [];
      
// //       if (response?.data && Array.isArray(response.data)) {
// //         buyers = response.data;
// //       } else if (Array.isArray(response)) {
// //         buyers = response;
// //       }
      
// //       console.log(`%c[✓ SUCCESS] Loaded ${buyers.length} buyers for org admin`, 'color: #00aa00; font-weight: bold;');
// //       return buyers;
// //     }),
// //     catchError(error => {
// //       console.error('%c[ERROR] Failed to load org admin buyers', 'color: #cc0000; font-weight: bold;', error);
// //       return of([]);
// //     })
// //   );
// // }
// //   /**
// //    * ✅ FIXED: Update buyer with complete nested hierarchy
// //    */
// //   updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
// //     const url = `${this.API}/buyer/${buyerId}`;
// //     console.log(`%c[UPDATE BUYER] PUT ${url}`, 'color: #ff9800; font-weight: bold;');
// //     console.log('%c[PAYLOAD]', 'color: #ff9800;', JSON.stringify(buyer, null, 2));
    
// //     // ✅ Send complete nested structure to backend
// //     return this.http.put(url, buyer, { headers: this.getAuthHeaders() }).pipe(
// //       tap(response => {
// //         console.log('%c[✓ SUCCESS] Buyer updated with complete hierarchy', 'color: #00aa00; font-weight: bold;', response);
// //       }),
// //       map((response: any) => {
// //         return response?.data || response;
// //       }),
// //       catchError(error => {
// //         console.error('%c[ERROR] Failed to update buyer', 'color: #cc0000; font-weight: bold;', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   // ==================== GET ALL BUYERS ====================

// //   /**
// //    * ✅ FIXED: Get all buyers with complete hierarchy
// //    * Backend returns: { success: true, message: "...", count: N, data: [...] }
// //    */
// //   getAllBuyers(): Observable<Buyer[]> {
// //     const url = `${this.API}/buyer`;
// //     console.log(`%c[GET ALL BUYERS] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
// //     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
// //       map(response => {
// //         console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
// //         // ✅ Handle different response structures
// //         let buyers: Buyer[] = [];
        
// //         if (response?.data && Array.isArray(response.data)) {
// //           // Wrapped response: { success: true, data: [...] }
// //           buyers = response.data;
// //         } else if (Array.isArray(response)) {
// //           // Direct array response: [...]
// //           buyers = response;
// //         } else {
// //           console.warn('Unexpected response structure:', response);
// //           buyers = [];
// //         }
        
// //         console.log(`%c[✓ SUCCESS] Loaded ${buyers.length} buyers`, 'color: #00aa00; font-weight: bold;');
// //         return buyers;
// //       }),
// //       catchError(error => {
// //         console.error('%c[ERROR] Failed to load buyers', 'color: #cc0000; font-weight: bold;', error);
// //         return of([]); // Return empty array instead of throwing
// //       })
// //     );
// //   }

// //   // ==================== GET BUYER BY ID ====================

// //   /**
// //    * ✅ FIXED: Get single buyer with complete hierarchy
// //    */
// //   getBuyerById(id: number): Observable<Buyer> {
// //     const url = `${this.API}/buyer/${id}`;
// //     console.log(`%c[GET BUYER] GET ${url}`, 'color: #0066cc; font-weight: bold;');
    
// //     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
// //       map(response => {
// //         console.log('%c[RAW RESPONSE]', 'color: #0066cc;', response);
        
// //         // ✅ Handle wrapped response
// //         let buyer: Buyer;
        
// //         if (response?.data) {
// //           buyer = response.data;
// //         } else if (response?.id) {
// //           buyer = response;
// //         } else {
// //           throw new Error('Invalid response structure');
// //         }
        
// //         console.log(`%c[✓ SUCCESS] Loaded buyer: ${buyer.companyName}`, 'color: #00aa00; font-weight: bold;');
// //         return buyer;
// //       }),
// //       catchError(error => {
// //         console.error(`%c[ERROR] Failed to load buyer ${id}`, 'color: #cc0000; font-weight: bold;', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }

// //   // ==================== DELETE BUYER ====================

// //   /**
// //    * ✅ FIXED: Delete buyer (soft delete - cascades to all related entities)
// //    */
// //   deleteBuyer(id: number): Observable<any> {
// //     const url = `${this.API}/buyer/${id}`;
// //     console.log(`%c[DELETE BUYER] DELETE ${url}`, 'color: #cc0000; font-weight: bold;');
    
// //     return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
// //       tap(response => {
// //         console.log(`%c[✓ SUCCESS] Buyer ${id} deleted`, 'color: #00aa00; font-weight: bold;', response);
// //       }),
// //       catchError(error => {
// //         console.error(`%c[ERROR] Failed to delete buyer ${id}`, 'color: #cc0000; font-weight: bold;', error);
// //         return throwError(() => error);
// //       })
// //     );
// //   }
// // }

// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { environment } from '../../../environments/environment';
// import { Observable, forkJoin, of } from 'rxjs';
// import { switchMap, tap, catchError, map } from 'rxjs/operators';
// import { throwError } from 'rxjs';
// import { Buyer, Location, Department, User } from './buyer-b.model';

// @Injectable({
//   providedIn: 'root'
// })
// export class BuyerService {
//   private API = environment.API_URL + 'leadcapture/api';

//   constructor(private http: HttpClient) {}

//   private getAuthHeaders(): HttpHeaders {
//     const token = localStorage.getItem('token') || '';
//     return new HttpHeaders({
//       'Content-Type': 'application/json',
//       'Authorization': `Bearer ${token}`
//     });
//   }

//   // ==================== CREATE BUYER WITH COMPLETE HIERARCHY ====================

//   /**
//    * ✅ FIXED: Organization Admin creates buyer
//    * - Always uses admin endpoint when adminId is provided
//    * - Ensures organizationCompanyName is set
//    */
//   createCompleteHierarchy(buyer: Buyer, adminId?: number): Observable<any> {
//     console.log('========================================');
//     console.log('🔵 CREATE BUYER SERVICE CALL');
//     console.log('========================================');
//     console.log('Admin ID:', adminId);
//     console.log('Organization Company:', buyer.organizationCompanyName);
    
//     // ✅ CRITICAL: If adminId is provided, ALWAYS use admin endpoint
//     if (adminId) {
//       console.log('✅ Using Organization Admin endpoint');
      
//       // ✅ Validate organizationCompanyName is present
//       if (!buyer.organizationCompanyName) {
//         console.error('❌ organizationCompanyName is missing!');
//         return throwError(() => new Error('organizationCompanyName is required for admin-created buyers'));
//       }
      
//       const url = `${this.API}/organization-admin/${adminId}/buyer`;
      
//       console.log('📍 URL:', url);
//       console.log('📦 Payload:', {
//         companyName: buyer.companyName,
//         organizationCompanyName: buyer.organizationCompanyName,
//         locationsCount: buyer.locations?.length || 0
//       });
      
//       return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
//         tap(response => {
//           console.log('========================================');
//           console.log('✅ BUYER CREATED BY ORG ADMIN');
//           console.log('========================================');
//           console.log('Response:', response);
//         }),
//         map((response: any) => response?.data || response),
//         catchError(error => {
//           console.error('========================================');
//           console.error('❌ ERROR CREATING BUYER');
//           console.error('========================================');
//           console.error('Status:', error.status);
//           console.error('Message:', error.error?.message || error.message);
//           return throwError(() => error);
//         })
//       );
//     } else {
//       // ❌ DEPRECATED: Direct buyer creation (should not be used)
//       console.warn('⚠️  Direct buyer creation is deprecated!');
//       const url = `${this.API}/buyer`;
      
//       return this.http.post(url, buyer, { headers: this.getAuthHeaders() }).pipe(
//         tap(response => {
//           console.log('✅ Buyer created (deprecated endpoint):', response);
//         }),
//         map((response: any) => response?.data || response),
//         catchError(error => {
//           console.error('❌ Error creating buyer:', error);
//           return throwError(() => error);
//         })
//       );
//     }
//   }

//   /**
//    * ✅ Get all buyers created by Organization Admin
//    */
//   getBuyersByOrgAdmin(adminId: number): Observable<Buyer[]> {
//     const url = `${this.API}/organization-admin/${adminId}/buyers`;
//     console.log('📥 Fetching buyers for Org Admin:', adminId);
    
//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         let buyers: Buyer[] = [];
        
//         if (response?.data && Array.isArray(response.data)) {
//           buyers = response.data;
//         } else if (Array.isArray(response)) {
//           buyers = response;
//         }
        
//         console.log(`✅ Loaded ${buyers.length} buyers for org admin`);
//         return buyers;
//       }),
//       catchError(error => {
//         console.error('❌ Failed to load org admin buyers:', error);
//         return of([]);
//       })
//     );
//   }

//   /**
//    * ✅ Update buyer with complete nested hierarchy
//    */
//   updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
//     const url = `${this.API}/buyer/${buyerId}`;
//     console.log('🔄 Updating buyer:', buyerId);
    
//     return this.http.put(url, buyer, { headers: this.getAuthHeaders() }).pipe(
//       tap(response => {
//         console.log('✅ Buyer updated:', response);
//       }),
//       map((response: any) => response?.data || response),
//       catchError(error => {
//         console.error('❌ Failed to update buyer:', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * ✅ Get all buyers with complete hierarchy
//    */
//   // getAllBuyers(): Observable<Buyer[]> {
//   //   const url = `${this.API}/buyer`;
    
//   //   return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//   //     map(response => {
//   //       let buyers: Buyer[] = [];
        
//   //       if (response?.data && Array.isArray(response.data)) {
//   //         buyers = response.data;
//   //       } else if (Array.isArray(response)) {
//   //         buyers = response;
//   //       }
        
//   //       console.log(`✅ Loaded ${buyers.length} buyers`);
//   //       return buyers;
//   //     }),
//   //     catchError(error => {
//   //       console.error('❌ Failed to load buyers:', error);
//   //       return of([]);
//   //     })
//   //   );
//   // }
//   getBuyersByOrganizationAdmin(adminId: number): Observable<Buyer[]> {
//   const url = `${this.API}/organization-admin/${adminId}/buyers`;
//   console.log(`📥 Fetching buyers for Organization Admin ID: ${adminId}`);

//   return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//     map(response => {
//       let buyers: Buyer[] = [];

//       // Handle wrapped response
//       if (response.data && Array.isArray(response.data)) {
//         buyers = response.data;
//       } else if (Array.isArray(response)) {
//         buyers = response;
//       }

//       console.log('✅ Buyers fetched successfully:', buyers.length);
//       return buyers;
//     }),
//     catchError(error => {
//       console.error(
//         `%c[ERROR GET /organization-admin/${adminId}/buyers]`,
//         'color: #cc0000;',
//         error
//       );
//       return throwError(() => error);
//     })
//   );
// }

//   /**
//    * ✅ Get single buyer with complete hierarchy
//    */
//   getBuyerById(id: number): Observable<Buyer> {
//     const url = `${this.API}/buyer/${id}`;
    
//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         let buyer: Buyer;
        
//         if (response?.data) {
//           buyer = response.data;
//         } else if (response?.id) {
//           buyer = response;
//         } else {
//           throw new Error('Invalid response structure');
//         }
        
//         console.log(`✅ Loaded buyer: ${buyer.companyName}`);
//         return buyer;
//       }),
//       catchError(error => {
//         console.error(`❌ Failed to load buyer ${id}:`, error);
//         return throwError(() => error);
//       })
//     );
//   }

//   /**
//    * ✅ Delete buyer (soft delete)
//    */
//   deleteBuyer(id: number): Observable<any> {
//     const url = `${this.API}/buyer/${id}`;
    
//     return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
//       tap(response => {
//         console.log(`✅ Buyer ${id} deleted`);
//       }),
//       catchError(error => {
//         console.error(`❌ Failed to delete buyer ${id}:`, error);
//         return throwError(() => error);
//       })
//     );
//   }
// }

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, forkJoin, of } from 'rxjs';
import { switchMap, tap, catchError, map } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Buyer, Location, Department, User } from './buyer-b.model';

@Injectable({
  providedIn: 'root'
})
export class BuyerService {
//   private API = environment.API_URL + 'leadcapture/api';
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

  /**
   * ✅ FIXED: Organization Admin creates buyer
   * - Always uses admin endpoint when adminId is provided
   * - Ensures organizationCompanyName is set
   */
/**
 * ✅ FIXED: Organization Admin creates buyer
 * - Always uses admin endpoint when adminId is provided
 * - Ensures organizationCompanyName is set
 */
createCompleteHierarchy(buyer: Buyer, adminId?: number): Observable<any> {
  console.log('========================================');
  console.log('🔵 CREATE BUYER SERVICE CALL');
  console.log('========================================');
  console.log('Admin ID:', adminId);
  console.log('Organization Company:', buyer.organizationCompanyName);
  
  // ✅ CRITICAL: If adminId is provided, ALWAYS use admin endpoint
  if (adminId) {
    console.log('✅ Using Organization Admin endpoint');
    
    // ✅ Validate organizationCompanyName is present
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
        console.error('Full Error:', error);
        return throwError(() => error);
      })
    );
  } else {
    // ❌ DEPRECATED: Direct buyer creation
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

/**
 * ✅ MAIN METHOD: Get all buyers created by Organization Admin
 */
getBuyersByOrganizationAdmin(adminId: number): Observable<Buyer[]> {
  const url = `${this.API}/organization-admin/${adminId}/buyers`;
  console.log(`📥 Fetching buyers for Organization Admin ID: ${adminId}`);
  console.log(`📍 URL: ${url}`);

  return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
    map(response => {
      console.log('📦 Raw Response:', response);
      
      let buyers: Buyer[] = [];

      // Handle wrapped response
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
      console.error(
        `%c[ERROR GET /organization-admin/${adminId}/buyers]`,
        'color: #cc0000; font-weight: bold;',
        error
      );
      console.error('Status:', error.status);
      console.error('Message:', error.error?.message || error.message);
      console.error('Full Error:', error);
      return throwError(() => error);
    })
  );
}

  /**
   * ✅ ALIAS: Same as getBuyersByOrganizationAdmin (for backward compatibility)
   * @deprecated Use getBuyersByOrganizationAdmin instead
   */
  getBuyersByOrgAdmin(adminId: number): Observable<Buyer[]> {
    console.log('⚠️ getBuyersByOrgAdmin is deprecated. Use getBuyersByOrganizationAdmin instead.');
    return this.getBuyersByOrganizationAdmin(adminId);
  }

  /**
   * ✅ Get all buyers (for SuperAdmin only)
   * This endpoint should only be used by SuperAdmin to see ALL buyers
   */
  getAllBuyers(): Observable<Buyer[]> {
    const url = `${this.API}/buyer`;
    console.log('📥 Fetching ALL buyers (SuperAdmin)');
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        let buyers: Buyer[] = [];
        
        if (response?.data && Array.isArray(response.data)) {
          buyers = response.data;
        } else if (Array.isArray(response)) {
          buyers = response;
        }
        
        console.log(`✅ Loaded ${buyers.length} buyers (all organizations)`);
        return buyers;
      }),
      catchError(error => {
        console.error('❌ Failed to load all buyers:', error);
        return of([]);
      })
    );
  }

  /**
   * ✅ Get buyers by organization company name
   */
  getBuyersByOrganizationCompanyName(companyName: string): Observable<Buyer[]> {
    const url = `${this.API}/organization-admin/company/${companyName}/buyers`;
    console.log(`📥 Fetching buyers for organization: ${companyName}`);
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        let buyers: Buyer[] = [];
        
        if (response?.data && Array.isArray(response.data)) {
          buyers = response.data;
        } else if (Array.isArray(response)) {
          buyers = response;
        }
        
        console.log(`✅ Loaded ${buyers.length} buyers for ${companyName}`);
        return buyers;
      }),
      catchError(error => {
        console.error(`❌ Failed to load buyers for ${companyName}:`, error);
        return of([]);
      })
    );
  }

  // ==================== SINGLE BUYER ====================

  /**
   * ✅ Get single buyer with complete hierarchy
   */
  getBuyerById(id: number): Observable<Buyer> {
    const url = `${this.API}/buyer/${id}`;
    console.log(`📥 Fetching buyer ID: ${id}`);
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        let buyer: Buyer;
        
        if (response?.data) {
          buyer = response.data;
        } else if (response?.id) {
          buyer = response;
        } else {
          throw new Error('Invalid response structure');
        }
        
        console.log(`✅ Loaded buyer: ${buyer.companyName}`);
        return buyer;
      }),
      catchError(error => {
        console.error(`❌ Failed to load buyer ${id}:`, error);
        return throwError(() => error);
      })
    );
  }

  // ==================== UPDATE BUYER ====================

  /**
   * ✅ Update buyer with complete nested hierarchy
   */
  updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
    const url = `${this.API}/buyer/${buyerId}`;
    console.log('🔄 Updating buyer:', buyerId);
    
    return this.http.put(url, buyer, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log('✅ Buyer updated:', response);
      }),
      map((response: any) => response?.data || response),
      catchError(error => {
        console.error('❌ Failed to update buyer:', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== DELETE BUYER ====================

  /**
   * ✅ Delete buyer (soft delete)
   */
  deleteBuyer(id: number): Observable<any> {
    const url = `${this.API}/buyer/${id}`;
    console.log(`🗑️ Deleting buyer ID: ${id}`);
    
    return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
      tap(response => {
        console.log(`✅ Buyer ${id} deleted`);
      }),
      catchError(error => {
        console.error(`❌ Failed to delete buyer ${id}:`, error);
        return throwError(() => error);
      })
    );
  }
}