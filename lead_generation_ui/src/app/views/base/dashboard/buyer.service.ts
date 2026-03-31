
// // import { Injectable } from '@angular/core';
// // import { HttpClient, HttpHeaders } from '@angular/common/http';
// // import { environment } from '../../../environments/environment';


// // @Injectable({
// //   providedIn: 'root'
// // })
// // export class BuyerService {

// //   private API = environment.API_URL + 'leadcapture/api/buyer';

// //   constructor(private http: HttpClient) {}

// //   private getAuthHeaders() {
// //     const token = localStorage.getItem('token');

// //     return new HttpHeaders({
// //       'Content-Type': 'application/json',
// //       'Authorization': `Bearer ${token}`
// //     });
// //   }

// //   // CREATE
// //   createBuyer(payload: any) {
// //     return this.http.post(this.API, payload, {
// //       headers: this.getAuthHeaders()
// //     });
// //   }

// //   // UPDATE
// //   updateBuyer(id: number, payload: any) {
// //     return this.http.put(`${this.API}/${id}`, payload, {
// //       headers: this.getAuthHeaders()
// //     });
// //   }

// //   // GET ALL
// //   getBuyers() {
// //     return this.http.get(this.API, {
// //       headers: this.getAuthHeaders()
// //     });
// //   }

// //   // GET BY ID
// //   getBuyerById(id: number) {
// //     return this.http.get(`${this.API}/${id}`, {
// //       headers: this.getAuthHeaders()
// //     });
// //   }

// //   // DELETE
// //   deleteBuyer(id: number) {
// //     return this.http.delete(`${this.API}/${id}`, {
// //       headers: this.getAuthHeaders(),
// //       responseType: 'text'
// //     });
// //   }
// // }


// // ==================== buyer-b.service.ts FIX ====================
// // ISSUE: API returns {success, data, count} but we're not extracting data.id properly

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

//   // ==================== CREATE BUYER WITH HIERARCHY ====================

//   createCompleteHierarchy(buyer: Buyer): Observable<any> {
//     console.log(`%c[START] Creating buyer hierarchy`, 'color: #ff6600; font-weight: bold; font-size: 13px;');
    
//     const buyerPayload = {
//       companyName: buyer.companyName,
//       companyType: buyer.companyType,
//       contactPersonName: buyer.contactPersonName,
//       contactPersonDesignation: buyer.contactPersonDesignation,
//       contactPersonEmail: buyer.contactPersonEmail,
//       contactPersonPhone: buyer.contactPersonPhone,
//       addressLine1: buyer.addressLine1,
//       addressLine2: buyer.addressLine2,
//       city: buyer.city,
//       state: buyer.state,
//       postalCode: buyer.postalCode,
//       country: buyer.country,
//       gstNumber: buyer.gstNumber,
//       panNumber: buyer.panNumber,
//       cinNumber: buyer.cinNumber,
//       website: buyer.website
//     };

//     return this.createBuyer(buyerPayload).pipe(
//       tap(response => {
//         console.log(`%c[RESPONSE] Buyer API Response:`, 'color: #0066cc;', response);
//       }),
//       switchMap((response: any) => {
//         // ✅ FIX: Extract buyer ID from response wrapper
//         const buyerId = response?.data?.id || response?.id;
        
//         if (!buyerId) {
//           console.error('%c[ERROR] No buyer ID in response!', 'color: #cc0000;', response);
//           return throwError(() => new Error('Failed to get buyer ID from response'));
//         }

//         console.log(`%c[STEP 1/4] ✓ Buyer created with ID: ${buyerId}`, 'color: #00aa00; font-weight: bold;');
        
//         if (!buyer.locations || buyer.locations.length === 0) {
//           return of({ id: buyerId, ...response?.data });
//         }

//         console.log(`%c[STEP 2/4] Creating ${buyer.locations.length} location(s)`, 'color: #ff6600; font-weight: bold;');
        
//         return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations).pipe(
//           switchMap(() => of({ id: buyerId, ...response?.data }))
//         );
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR] Failed to create hierarchy`, 'color: #cc0000; font-weight: bold;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== HELPER: Create Locations Sequentially ====================

//   private createLocationsWithDepartmentsSequentially(buyerId: number, locations: Location[]): Observable<any> {
//     if (locations.length === 0) {
//       return of(null);
//     }

//     const [firstLocation, ...restLocations] = locations;
    
//     return this.createLocationWithDepartments(buyerId, firstLocation).pipe(
//       switchMap(() => {
//         if (restLocations.length === 0) {
//           return of(null);
//         }
//         return this.createLocationsWithDepartmentsSequentially(buyerId, restLocations);
//       })
//     );
//   }

//   private createLocationWithDepartments(buyerId: number, location: Location): Observable<any> {
//     const locationPayload = { 
//       locationName: location.locationName,
//       locationType: location.locationType,
//       locationContactName: location.locationContactName,
//       locationContactEmail: location.locationContactEmail,
//       locationContactPhone: location.locationContactPhone,
//       addressLine1: location.addressLine1,
//       addressLine2: location.addressLine2,
//       city: location.city,
//       state: location.state,
//       postalCode: location.postalCode,
//       country: location.country,
//       landlineNumber: location.landlineNumber,
//       faxNumber: location.faxNumber
//     };
    
//     return this.createLocation(buyerId, locationPayload).pipe(
//       tap((response: any) => {
//         const locationId = response?.data?.id || response?.id;
//         console.log(`%c[✓ Location created: ${locationId}]`, 'color: #00aa00;', location.locationName);
//       }),
//       switchMap((response: any) => {
//         const locationId = response?.data?.id || response?.id;
        
//         if (!locationId) {
//           return throwError(() => new Error('Failed to get location ID from response'));
//         }

//         if (!location.departments || location.departments.length === 0) {
//           return of(response);
//         }

//         return this.createDepartmentsWithUsersSequentially(locationId, location.departments).pipe(
//           switchMap(() => of(response))
//         );
//       })
//     );
//   }

//   private createDepartmentsWithUsersSequentially(locationId: number, departments: Department[]): Observable<any> {
//     if (departments.length === 0) {
//       return of(null);
//     }

//     const [firstDept, ...restDepts] = departments;
//     return this.createDepartmentWithUsers(locationId, firstDept).pipe(
//       switchMap(() => {
//         if (restDepts.length === 0) {
//           return of(null);
//         }
//         return this.createDepartmentsWithUsersSequentially(locationId, restDepts);
//       })
//     );
//   }

//   private createDepartmentWithUsers(locationId: number, department: Department): Observable<any> {
//     const departmentPayload = { 
//       departmentName: department.departmentName,
//       departmentDescription: department.departmentDescription
//     };
    
//     return this.createDepartment(locationId, departmentPayload).pipe(
//       tap((response: any) => {
//         const deptId = response?.data?.id || response?.id;
//         console.log(`%c[✓ Department created: ${deptId}]`, 'color: #00aa00;', department.departmentName);
//       }),
//       switchMap((response: any) => {
//         const departmentId = response?.data?.id || response?.id;
        
//         if (!departmentId) {
//           return throwError(() => new Error('Failed to get department ID from response'));
//         }

//         if (!department.users || department.users.length === 0) {
//           return of(response);
//         }

//         return this.createUsersSequentially(departmentId, department.users).pipe(
//           switchMap(() => of(response))
//         );
//       })
//     );
//   }

//   private createUsersSequentially(departmentId: number, users: User[]): Observable<any> {
//     if (users.length === 0) {
//       return of(null);
//     }

//     const [firstUser, ...restUsers] = users;
//     return this.createUserForDepartment(departmentId, firstUser).pipe(
//       switchMap(() => {
//         if (restUsers.length === 0) {
//           return of(null);
//         }
//         return this.createUsersSequentially(departmentId, restUsers);
//       })
//     );
//   }

//   // ==================== API ENDPOINTS ====================

//   private createBuyer(payload: any): Observable<any> {
//     const url = `${this.API}/buyer`;
//     console.log(`%c[API POST]`, 'color: #0066cc; font-weight: bold;', url);
    
//     return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR POST /buyer]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   private createLocation(buyerId: number, payload: any): Observable<any> {
//     const url = `${this.API}/location/${buyerId}`;
//     console.log(`%c[API POST Location]`, 'color: #0066cc;', url);
    
//     return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR POST /location]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   private createDepartment(locationId: number, payload: any): Observable<any> {
//     const url = `${this.API}/department/${locationId}`;
    
//     return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR POST /department]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   private createUserForDepartment(departmentId: number, user: User): Observable<any> {
//     const url = `${this.API}/users/${departmentId}`;
    
//     const userPayload = {
//       firstName: user.firstName,
//       lastName: user.lastName,
//       email: user.email,
//       phone: user.phone,
//       designation: user.designation,
//       employeeId: user.employeeId,
//       gender: user.gender,
//       dateOfBirth: user.dateOfBirth,
//       addressLine1: user.addressLine1,
//       addressLine2: user.addressLine2,
//       city: user.city,
//       state: user.state,
//       postalCode: user.postalCode,
//       password: user.password
//     };

//     return this.http.post(url, userPayload, { headers: this.getAuthHeaders() }).pipe(
//       tap((response: any) => {
//         console.log(`%c[✓ User created]`, 'color: #00aa00;', user.email);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR POST /users]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== GET BUYERS ====================

//  getAllBuyers(): Observable<Buyer[]> {
//   const url = `${this.API}/buyer`;
//   console.log('📥 Fetching all buyers with complete hierarchy...');
  
//   return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//     tap(rawResponse => {
//       // ✅ DEBUG: Log the raw response BEFORE any transformation
//       console.log('%c[RAW API RESPONSE]', 'color: #ff0000; font-weight: bold; font-size: 14px;', rawResponse);
//       console.log('Response keys:', Object.keys(rawResponse));
//       console.log('Response.data type:', typeof rawResponse.data);
//       console.log('Is response.data array?', Array.isArray(rawResponse.data));
      
//       if (rawResponse.data && rawResponse.data.length > 0) {
//         console.log('First buyer:', rawResponse.data[0]);
//         console.log('First buyer has locations?', 'locations' in rawResponse.data[0]);
//         console.log('Locations count:', rawResponse.data[0].locations?.length || 'NO LOCATIONS');
//       }
//     }),
//     map(response => {
//       let buyers: Buyer[] = [];
      
//       // Handle response structure
//       if (response.data && Array.isArray(response.data)) {
//         buyers = response.data;
//       } else if (Array.isArray(response)) {
//         buyers = response;
//       } else {
//         buyers = [];
//       }
      
//       console.log('✅ Extracted buyers count:', buyers.length);
//       buyers.forEach((buyer: any, idx: number) => {
//         console.log(`  [${idx}] ${buyer.companyName}`);
//         console.log(`       Locations: ${buyer.locations?.length || 'NONE'}`);
//         if (buyer.locations && buyer.locations.length > 0) {
//           buyer.locations.forEach((loc: any) => {
//             console.log(`         - ${loc.locationName}`);
//           });
//         }
//       });
      
//       return buyers;
//     }),
//     catchError(error => {
//       console.error(`%c[ERROR GET /buyer]`, 'color: #cc0000;', error);
//       return throwError(() => error);
//     })
//   );
// }

//   getBuyerById(id: number): Observable<Buyer> {
//     const url = `${this.API}/buyer/${id}`;
//     console.log(`📥 Fetching buyer ${id}...`);
    
//     return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
//       map(response => {
//         let buyer: Buyer;
        
//         if (response.data) {
//           buyer = response.data;
//         } else if (response.id) {
//           buyer = response;
//         } else {
//           throw new Error('Invalid response structure');
//         }
        
//         console.log(`✅ Buyer ${id} fetched`);
//         return buyer;
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR GET /buyer/${id}]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   updateBuyer(id: number, payload: any): Observable<any> {
//     const url = `${this.API}/buyer/${id}`;
//     return this.http.put(url, payload, { headers: this.getAuthHeaders() }).pipe(
//       catchError(error => {
//         console.error(`%c[ERROR PUT /buyer/${id}]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   deleteBuyer(id: number): Observable<any> {
//     const url = `${this.API}/buyer/${id}`;
//     console.log(`%c[API DELETE]`, 'color: #cc0000; font-weight: bold;', url);
    
//     return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
//       tap(() => {
//         console.log(`%c[✓ DELETE SUCCESS]`, 'color: #00aa00; font-weight: bold;', `Buyer ${id} deleted`);
//       }),
//       catchError(error => {
//         console.error(`%c[ERROR DELETE /buyer/${id}]`, 'color: #cc0000;', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   // ==================== UPDATE COMPLETE HIERARCHY ====================

//   updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
//     console.log('Updating buyer hierarchy for ID:', buyerId);
    
//     const buyerPayload = {
//       companyName: buyer.companyName,
//       companyType: buyer.companyType,
//       contactPersonName: buyer.contactPersonName,
//       contactPersonDesignation: buyer.contactPersonDesignation,
//       contactPersonEmail: buyer.contactPersonEmail,
//       contactPersonPhone: buyer.contactPersonPhone,
//       addressLine1: buyer.addressLine1,
//       addressLine2: buyer.addressLine2,
//       city: buyer.city,
//       state: buyer.state,
//       postalCode: buyer.postalCode,
//       country: buyer.country,
//       gstNumber: buyer.gstNumber,
//       panNumber: buyer.panNumber,
//       cinNumber: buyer.cinNumber,
//       website: buyer.website
//     };

//     return this.updateBuyer(buyerId, buyerPayload).pipe(
//       tap(() => console.log('Buyer info updated')),
//       switchMap(() => this.getBuyerById(buyerId)),
//       switchMap((existingBuyer: any) => {
//         if (existingBuyer.locations && existingBuyer.locations.length > 0) {
//           const deleteObservables = existingBuyer.locations.map((loc: any) =>
//             this.deleteLocation(loc.id)
//           );
//           return forkJoin(deleteObservables).pipe(
//             switchMap(() => {
//               if (!buyer.locations || buyer.locations.length === 0) {
//                 return of(null);
//               }
//               return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations);
//             })
//           );
//         } else {
//           if (!buyer.locations || buyer.locations.length === 0) {
//             return of(null);
//           }
//           return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations);
//         }
//       }),
//       catchError(error => {
//         console.error('Error updating buyer hierarchy:', error);
//         return throwError(() => error);
//       })
//     );
//   }

//   private deleteLocation(id: number): Observable<any> {
//     const url = `${this.API}/location/${id}`;
//     return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
//       tap(() => console.log('Location deleted:', id)),
//       catchError(error => {
//         console.error(`Error deleting location ${id}:`, error);
//         return throwError(() => error);
//       })
//     );
//   }
// }

// // ==================== Same fixes apply to supplier.service.ts ====================
// // Replace response?.data?.id || response?.id pattern throughout
// // The key fix is extracting the ID from the nested response structure

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
  private API = environment.API_URL + 'leadcapture/api';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ==================== BUYER ENDPOINTS ====================

  /**
   * Create a new buyer with complete hierarchy
   */
  createCompleteHierarchy(buyer: Buyer): Observable<any> {
    console.log(`%c[START] Creating buyer hierarchy`, 'color: #ff6600; font-weight: bold; font-size: 13px;');
    
    const buyerPayload = {
      companyName: buyer.companyName,
      companyType: buyer.companyType,
      contactPersonName: buyer.contactPersonName,
      contactPersonDesignation: buyer.contactPersonDesignation,
      contactPersonEmail: buyer.contactPersonEmail,
      contactPersonPhone: buyer.contactPersonPhone,
      addressLine1: buyer.addressLine1,
      addressLine2: buyer.addressLine2,
      city: buyer.city,
      state: buyer.state,
      postalCode: buyer.postalCode,
      country: buyer.country,
      gstNumber: buyer.gstNumber,
      panNumber: buyer.panNumber,
      cinNumber: buyer.cinNumber,
      website: buyer.website
    };

    return this.createBuyer(buyerPayload).pipe(
      tap(createdBuyer => {
        console.log(`%c[STEP 1/4] ✓ Buyer created with ID: ${createdBuyer.id}`, 'color: #00aa00; font-weight: bold;');
      }),
      switchMap((createdBuyer: any) => {
        const buyerId = createdBuyer.id;
        
        if (!buyer.locations || buyer.locations.length === 0) {
          return of(createdBuyer);
        }

        console.log(`%c[STEP 2/4] Creating ${buyer.locations.length} location(s)`, 'color: #ff6600; font-weight: bold;');
        
        return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations).pipe(
          switchMap(() => of(createdBuyer))
        );
      }),
      catchError(error => {
        console.error(`%c[ERROR] Failed to create hierarchy`, 'color: #cc0000; font-weight: bold;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Create locations sequentially and handle their departments
   */
  private createLocationsWithDepartmentsSequentially(buyerId: number, locations: Location[]): Observable<any> {
    if (locations.length === 0) {
      return of(null);
    }

    const [firstLocation, ...restLocations] = locations;
    
    return this.createLocationWithDepartments(buyerId, firstLocation).pipe(
      switchMap(() => {
        if (restLocations.length === 0) {
          return of(null);
        }
        return this.createLocationsWithDepartmentsSequentially(buyerId, restLocations);
      })
    );
  }

  /**
   * Create a location and then create all its departments with users
   */
  private createLocationWithDepartments(buyerId: number, location: Location): Observable<any> {
    const locationPayload = { 
      locationName: location.locationName,
      locationType: location.locationType,
      locationContactName: location.locationContactName,
      locationContactEmail: location.locationContactEmail,
      locationContactPhone: location.locationContactPhone,
      addressLine1: location.addressLine1,
      addressLine2: location.addressLine2,
      city: location.city,
      state: location.state,
      postalCode: location.postalCode,
      country: location.country,
      landlineNumber: location.landlineNumber,
      faxNumber: location.faxNumber
    };
    
    return this.createLocation(buyerId, locationPayload).pipe(
      tap((createdLocation: any) => {
        console.log(`%c[✓ Location created: ${createdLocation.id}]`, 'color: #00aa00;', createdLocation.locationName);
      }),
      switchMap((createdLocation: any) => {
        const locationId = createdLocation.id;
        if (!location.departments || location.departments.length === 0) {
          return of(createdLocation);
        }
        return this.createDepartmentsWithUsersSequentially(buyerId, locationId, location.departments).pipe(
          switchMap(() => of(createdLocation))
        );
      })
    );
  }

  /**
   * ✅ FIXED: Create departments sequentially for a location
   * Added buyerId parameter
   */
  private createDepartmentsWithUsersSequentially(buyerId: number, locationId: number, departments: Department[]): Observable<any> {
    if (departments.length === 0) {
      return of(null);
    }

    const [firstDept, ...restDepts] = departments;
    return this.createDepartmentWithUsers(buyerId, locationId, firstDept).pipe(
      switchMap(() => {
        if (restDepts.length === 0) {
          return of(null);
        }
        return this.createDepartmentsWithUsersSequentially(buyerId, locationId, restDepts);
      })
    );
  }

  /**
   * ✅ FIXED: Create a department and then create all its users
   * Added buyerId parameter
   */
  private createDepartmentWithUsers(buyerId: number, locationId: number, department: Department): Observable<any> {
    const departmentPayload = { 
      departmentName: department.departmentName,
      departmentDescription: department.departmentDescription
    };
    
    return this.createDepartment(buyerId, locationId, departmentPayload).pipe(
      tap((createdDept: any) => {
        console.log(`%c[✓ Department created: ${createdDept.id}]`, 'color: #00aa00;', createdDept.departmentName);
      }),
      switchMap((createdDepartment: any) => {
        const departmentId = createdDepartment.id;
        if (!department.users || department.users.length === 0) {
          return of(createdDepartment);
        }
        return this.createUsersSequentially(buyerId, locationId, departmentId, department.users).pipe(
          switchMap(() => of(createdDepartment))
        );
      })
    );
  }

  /**
   * ✅ FIXED: Create users sequentially for a department
   * Added buyerId and locationId parameters
   */
  private createUsersSequentially(buyerId: number, locationId: number, departmentId: number, users: User[]): Observable<any> {
    if (users.length === 0) {
      return of(null);
    }

    const [firstUser, ...restUsers] = users;
    return this.createUserForDepartment(buyerId, locationId, departmentId, firstUser).pipe(
      switchMap(() => {
        if (restUsers.length === 0) {
          return of(null);
        }
        return this.createUsersSequentially(buyerId, locationId, departmentId, restUsers);
      })
    );
  }

  /**
   * POST /leadcapture/api/buyer
   * Create a new buyer
   */
  // private createBuyer(payload: any): Observable<any> {
  //   const url = `${this.API}/buyer`;
  //   console.log(`%c[API POST]`, 'color: #0066cc; font-weight: bold;', url, payload);
    
  //   return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
  //     catchError(error => {
  //       console.error(`%c[ERROR POST /buyer]`, 'color: #cc0000;', error);
  //       return throwError(() => error);
  //     })
  //   );
  // }

  /**
   * PUT /leadcapture/api/buyer/{id}
   * Update buyer by ID
   */
  updateCompleteHierarchy(buyerId: number, buyer: Buyer): Observable<any> {
    console.log('Updating buyer hierarchy for ID:', buyerId);
    
    const buyerPayload = {
      companyName: buyer.companyName,
      companyType: buyer.companyType,
      contactPersonName: buyer.contactPersonName,
      contactPersonDesignation: buyer.contactPersonDesignation,
      contactPersonEmail: buyer.contactPersonEmail,
      contactPersonPhone: buyer.contactPersonPhone,
      addressLine1: buyer.addressLine1,
      addressLine2: buyer.addressLine2,
      city: buyer.city,
      state: buyer.state,
      postalCode: buyer.postalCode,
      country: buyer.country,
      gstNumber: buyer.gstNumber,
      panNumber: buyer.panNumber,
      cinNumber: buyer.cinNumber,
      website: buyer.website
    };

    return this.updateBuyer(buyerId, buyerPayload).pipe(
      tap(() => console.log('Buyer info updated')),
      switchMap(() => {
        return this.getBuyerById(buyerId).pipe(
          switchMap((existingBuyer: any) => {
            if (existingBuyer.locations && existingBuyer.locations.length > 0) {
              const deleteObservables = existingBuyer.locations.map((loc: any) =>
                this.deleteLocation(loc.id)
              );
              return forkJoin(deleteObservables).pipe(
                switchMap(() => {
                  if (!buyer.locations || buyer.locations.length === 0) {
                    return of(null);
                  }
                  return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations);
                })
              );
            } else {
              if (!buyer.locations || buyer.locations.length === 0) {
                return of(null);
              }
              return this.createLocationsWithDepartmentsSequentially(buyerId, buyer.locations);
            }
          })
        );
      }),
      catchError(error => {
        console.error('Error updating buyer hierarchy:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * DELETE /leadcapture/api/location/{id}
   */
  private deleteLocation(id: number): Observable<any> {
    const url = `${this.API}/location/${id}`;
    console.log('Deleting location:', id);
    
    return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
      tap(() => console.log('Location deleted:', id)),
      catchError(error => {
        console.error(`Error deleting location ${id}:`, error);
        return throwError(() => error);
      })
    );
  }

  private updateBuyer(id: number, payload: any): Observable<any> {
    const url = `${this.API}/buyer/${id}`;
    return this.http.put(url, payload, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error(`%c[ERROR PUT /buyer/${id}]`, 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * ✅ FIX: GET /leadcapture/api/buyer
   * Extract data from response wrapper if it exists
   */
  // getAllBuyers(): Observable<Buyer[]> {
  //   const url = `${this.API}/buyer`;
  //   console.log('📥 Fetching all buyers with complete hierarchy...');
    
  //   return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
  //     map(response => {
  //       // Handle response wrapper (success, count, data)
  //       let buyers: Buyer[] = [];
        
  //       if (response.data && Array.isArray(response.data)) {
  //         buyers = response.data;
  //       } else if (Array.isArray(response)) {
  //         buyers = response;
  //       } else {
  //         buyers = [];
  //       }
        
  //       console.log('✅ Buyers fetched successfully:');
  //       console.log('  - Total buyers:', buyers.length);
  //       buyers.forEach((buyer: any, idx: number) => {
  //         console.log(`  [${idx}] ${buyer.companyName}:`);
  //         console.log(`      - Locations: ${buyer.locations?.length || 0}`);
  //         buyer.locations?.forEach((loc: any, locIdx: number) => {
  //           console.log(`        [${locIdx}] ${loc.locationName} - Departments: ${loc.departments?.length || 0}`);
  //           loc.departments?.forEach((dept: any, deptIdx: number) => {
  //             console.log(`          [${deptIdx}] ${dept.departmentName} - Users: ${dept.users?.length || 0}`);
  //           });
  //         });
  //       });
        
  //       return buyers;
  //     }),
  //     catchError(error => {
  //       console.error(`%c[ERROR GET /buyer]`, 'color: #cc0000;', error);
  //       return throwError(() => error);
  //     })
  //   );
  // }
  /**
 * GET /leadcapture/api/organization-admin/{adminId}/buyers
 * Fetch all buyers for a specific organization admin
 */
getBuyersByOrganizationAdmin(adminId: number): Observable<Buyer[]> {
  const url = `${this.API}/organization-admin/${adminId}/buyers`;
  console.log(`📥 Fetching buyers for Organization Admin ID: ${adminId}`);

  return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
    map(response => {
      let buyers: Buyer[] = [];

      // Handle wrapped response
      if (response.data && Array.isArray(response.data)) {
        buyers = response.data;
      } else if (Array.isArray(response)) {
        buyers = response;
      }

      console.log('✅ Buyers fetched successfully:', buyers.length);
      return buyers;
    }),
    catchError(error => {
      console.error(
        `%c[ERROR GET /organization-admin/${adminId}/buyers]`,
        'color: #cc0000;',
        error
      );
      return throwError(() => error);
    })
  );
}


  /**
   * ✅ FIX: GET /leadcapture/api/buyer/{id}
   * Extract data from response wrapper and ensure complete hierarchy
   */
  getBuyerById(id: number): Observable<Buyer> {
    const url = `${this.API}/buyer/${id}`;
    console.log(`📥 Fetching buyer ${id} with complete hierarchy...`);
    
    return this.http.get<any>(url, { headers: this.getAuthHeaders() }).pipe(
      map(response => {
        // Handle response wrapper (success, data)
        let buyer: Buyer;
        
        if (response.data) {
          buyer = response.data;
        } else if (response.id) {
          buyer = response;
        } else {
          throw new Error('Invalid response structure');
        }
        
        console.log(`✅ Buyer ${id} fetched:`, {
          companyName: buyer.companyName,
          locations: buyer.locations?.length || 0,
          totalDepartments: buyer.locations?.reduce((sum: number, loc: any) => sum + (loc.departments?.length || 0), 0),
          totalUsers: buyer.locations?.reduce((sum: number, loc: any) => 
            sum + (loc.departments?.reduce((dSum: number, d: any) => dSum + (d.users?.length || 0), 0) || 0), 0)
        });
        
        return buyer;
      }),
      catchError(error => {
        console.error(`%c[ERROR GET /buyer/${id}]`, 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * DELETE /leadcapture/api/buyer/{id}
   */
  deleteBuyer(id: number): Observable<any> {
    const url = `${this.API}/buyer/${id}`;
    console.log(`%c[API DELETE]`, 'color: #cc0000; font-weight: bold;', url);
    
    return this.http.delete(url, { headers: this.getAuthHeaders() }).pipe(
      tap(() => {
        console.log(`%c[✓ DELETE SUCCESS]`, 'color: #00aa00; font-weight: bold;', `Buyer ${id} deleted`);
      }),
      catchError(error => {
        console.error(`%c[ERROR DELETE /buyer/${id}]`, 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== LOCATION ENDPOINTS ====================

  /**
   * ✅ CRITICAL FIX: POST /leadcapture/api/location/{buyerId}
   * Create a location for a buyer
   */
  private createLocation(buyerId: number, payload: any): Observable<any> {
    const url = `${this.API}/location/${buyerId}`;
    console.log(`%c[API POST]`, 'color: #0066cc;', url);
    
    return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
      tap((response: any) => {
        console.log(`%c[✓ Location Created]`, 'color: #00aa00;', response);
      }),
      catchError(error => {
        console.error(`%c[ERROR POST /location/${buyerId}]`, 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== DEPARTMENT ENDPOINTS ====================

  /**
   * ✅ FIXED: POST /leadcapture/api/department/{buyerId}/{locationId}
   * Create a department for a location
   */
  private createDepartment(buyerId: number, locationId: number, payload: any): Observable<any> {
    const url = `${this.API}/department/${buyerId}/${locationId}`;
    console.log(`%c[API POST]`, 'color: #0066cc;', url);
    
    return this.http.post(url, payload, { headers: this.getAuthHeaders() }).pipe(
      tap((response: any) => {
        console.log(`%c[✓ Department Created]`, 'color: #00aa00;', response);
      }),
      catchError(error => {
        console.error(`%c[ERROR POST /department/${buyerId}/${locationId}]`, 'color: #cc0000;', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== USER ENDPOINTS ====================

  /**
   * ✅ FIXED: POST /leadcapture/api/user/{buyerId}/{locationId}/{departmentId}
   * Create a user in a department
   */
  private createUserForDepartment(buyerId: number, locationId: number, departmentId: number, user: User): Observable<any> {
    const url = `${this.API}/user/${buyerId}/${locationId}/${departmentId}`;
    
    const userPayload = {
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      phone: user.phone,
      designation: user.designation,
      employeeId: user.employeeId,
      gender: user.gender,
      dateOfBirth: user.dateOfBirth,
      addressLine1: user.addressLine1,
      addressLine2: user.addressLine2,
      city: user.city,
      state: user.state,
      postalCode: user.postalCode,
      password: user.password
    };

    console.log(`%c[Creating User]`, 'color: #0066cc;', `Buyer: ${buyerId}, Location: ${locationId}, Department: ${departmentId}`, userPayload);

    return this.http.post(url, userPayload, { headers: this.getAuthHeaders() }).pipe(
      tap((response: any) => {
        console.log(`%c[✓ User created]`, 'color: #00aa00;', response.email || user.email);
      }),
      catchError(error => {
        console.error(`%c[ERROR POST /user/${buyerId}/${locationId}/${departmentId}]`, 'color: #cc0000;', error);
        // Don't fail entire hierarchy creation if one user fails
        return of(null);
      })
    );
  }
}