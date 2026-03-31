// // import { Injectable } from '@angular/core';
// // import { HttpClient, HttpHeaders } from '@angular/common/http';
// // import { environment } from '../../environments/environment';
// // import { Observable } from 'rxjs/internal/Observable';

// // @Injectable({
// //     providedIn: 'root'
// // })
// // export class DataService {

// //     constructor(private http: HttpClient) { }

// //     // ============================================
// //     // UTILITY METHODS
// //     // ============================================
// //     invokeGetAPI(url: any) {
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || '')
// //         });
// //         return this.http.get<{ result: string; data: any }>(url, { headers: reqHeader });
// //     }

// //     invokePostAPI(url: any, input: any) {
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || '')
// //         });
// //         return this.http.post<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader });
// //     }

// //     invokePutAPI(url: any, input: any) {
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
// //             'Content-Type': 'application/json'
// //         });
// //         return this.http.put<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader });
// //     }

// //     invokeDeleteAPI(url: any) {
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || '')
// //         });
// //         return this.http.delete<{ result: string; data: any }>(url, { headers: reqHeader });
// //     }

// //     // ============================================
// //     // 🆕 SUPERADMIN APIs (Creates OrganizationAdmins Only)
// //     // ============================================
// //     superAdminLogin(loginRequest: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/superadmin/auth/login';
// //         return this.http.post<any>(url, loginRequest);
// //     }

// //     superAdminRegister(registerRequest: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/superadmin/auth/register';
// //         return this.http.post<any>(url, registerRequest);
// //     }

// //     // 🆕 SuperAdmin creates OrganizationAdmin
// //     superAdminCreateOrgAdmin(orgAdminData: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/superadmin/create-org-admin';
// //         return this.invokePostAPI(url, orgAdminData);
// //     }

// //     // 🆕 Get all OrganizationAdmins (for SuperAdmin dashboard)
// //     superAdminGetAllOrgAdmins(): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/superadmin/org-admins';
// //         return this.invokeGetAPI(url);
// //     }

// //     // ============================================
// //     // 🆕 ORGANIZATION ADMIN APIs (Full Management Power)
// //     // ============================================
// //     organizationAdminLogin(loginRequest: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/organization-admin/auth/login';
// //         return this.http.post<any>(url, loginRequest);
// //     }

// //     createOrganizationAdmin(orgAdminData: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/organization-admin';
// //         return this.invokePostAPI(url, orgAdminData);
// //     }
// // changeOrganizationAdminPassword(id: number, passwordData: any): Observable<any> {
// //     const url = environment.API_URL + `leadcapture/api/organization-admin/change-password/${id}`;
    
// //     // ✅ Use invokePutAPI which includes Authorization header
// //     return this.invokePutAPI(url, passwordData);
// // }

// // /**
// //  * Change password for Hierarchy User
// //  */
// // changeHierarchyUserPassword(id: number, passwordData: any): Observable<any> {
// //     const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/change-password`;
// //     return this.invokePutAPI(url, passwordData);
// // }
    
// // /*
// //  * Organization Admin creates a buyer with organizationCompanyName
// //  */
// // createBuyerByOrgAdmin(adminId: number, buyerData: any): Observable<any> {
// //     const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyer`;
// //     return this.invokePostAPI(url, buyerData);
// // }
// // getBuyersByOrganizationCompany(companyName: string): Observable<any> {
// //   const url = environment.API_URL + `leadcapture/api/organization-admin/company/${encodeURIComponent(companyName)}/buyers`;
// //   return this.invokeGetAPI(url);
// // }
// // /**
// //  * Get all buyers created by Organization Admin
// //  */
// // getBuyersByOrgAdmin(adminId: number): Observable<any> {
// //     const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyers`;
// //     return this.invokeGetAPI(url);
// // }
// //     getOrganizationAdminById(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     getOrganizationAdminsByCompany(companyName: string): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/company/${companyName}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     getAllOrganizationAdmins(): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/organization-admin/all';
// //         return this.invokeGetAPI(url);
// //     }

    

// //     updateOrganizationAdmin(id: number, data: any): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
// //         return this.invokePutAPI(url, data);
// //     }

// //     deactivateOrganizationAdmin(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/deactivate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     activateOrganizationAdmin(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/activate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     deleteOrganizationAdmin(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
// //         return this.invokeDeleteAPI(url);
// //     }

// //     // ============================================
// //     // HIERARCHY LEVEL APIs
// //     // ============================================
// //     createHierarchyLevel(levelData: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
// //         return this.invokePostAPI(url, levelData);
// //     }

// //     getAllHierarchyLevels(): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
// //         return this.invokeGetAPI(url);
// //     }

// //     getHierarchyLevelsByCompany(companyName: string): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/company/${companyName}`;
// //         return this.invokeGetAPI(url);
// //     }

    

// //     getHierarchyLevelById(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     updateHierarchyLevel(id: number, levelData: any): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
// //         return this.invokePutAPI(url, levelData);
// //     }

// //     deactivateHierarchyLevel(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/deactivate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     activateHierarchyLevel(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/activate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     deleteHierarchyLevel(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
// //         return this.invokeDeleteAPI(url);
// //     }

// //     reorderHierarchyLevels(companyName: string, levelIds: number[]): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/reorder/${companyName}`;
// //         return this.invokePutAPI(url, levelIds);
// //     }

// //     // ============================================
// //     // HIERARCHY USER APIs
// //     // ============================================
// //     // ✅ FIXED: Corrected endpoint
// //     hierarchyLogin(loginRequest: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/hierarchy-users/auth/login';
// //         return this.http.post<any>(url, loginRequest);
// //     }

// //     createHierarchyUser(userData: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/hierarchy-users';
// //         return this.invokePostAPI(url, userData);
// //     }

// //     getHierarchyUserById(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     getHierarchyUsersByCompany(companyName: string): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/company/${companyName}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     getHierarchyUsersByLevel(levelId: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/level/${levelId}`;
// //         return this.invokeGetAPI(url);
// //     }

// //     updateHierarchyUser(id: number, userData: any): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
// //         return this.invokePutAPI(url, userData);
// //     }

// //     deactivateHierarchyUser(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/deactivate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     activateHierarchyUser(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/activate`;
// //         return this.invokePutAPI(url, {});
// //     }

// //     deleteHierarchyUser(id: number): Observable<any> {
// //         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
// //         return this.invokeDeleteAPI(url);
// //     }

// //     // ============================================
// //     // BUYER LOGIN
// //     // ============================================
// //     buyerLogin(loginRequest: any): Observable<any> {
// //         const url = environment.API_URL + 'leadcapture/api/buyer/login';
// //         return this.http.post<any>(url, loginRequest);
// //     }

// //     // ============================================
// //     // OTHER EXISTING METHODS
// //     // ============================================
// //     signin(input: any) {
// //         var url = environment.API_URL + "api/v1/auth/signin";
// //         return this.invokePostAPI(url, input);
// //     }

// //     getEmployeeDetails(input: any) {
// //         var url = environment.API_URL + "app/getEmployeeDetails";
// //         return this.invokePostAPI(url, input);
// //     }

// //     sendInvite(body: { email: string; name?: string; }): Observable<any> {
// //         var url = environment.API_URL + "onboarding/api/invites";
// //         return this.invokePostAPI(url, body);
// //     }

// //     getInvites() {
// //         var url = environment.API_URL + "onboarding/api/invites";
// //         return this.invokeGetAPI(url);
// //     }

// //     validateToken(token: any) {
// //         var url = environment.API_URL + "onboarding/api/candidate/validate?token=" + token;
// //         return this.invokeGetAPI(url);
// //     }

// //     acceptConsent(input: any): Observable<any> {
// //         var url = environment.API_URL + "onboarding/api/candidate/consent";
// //         return this.invokePostAPI(url, input);
// //     }

// //     submitPersonalInfo(formData: any) {
// //         var url = environment.API_URL + "onboarding/api/onboard/personal-info";
// //         return this.invokePostAPI(url, formData);
// //     }

// //     getSubmission(candidateId: any) {
// //         var url = environment.API_URL + "onboarding/api/submissions/" + candidateId;
// //         return this.invokeGetAPI(url);
// //     }

// //     uploadDocuments(formData: FormData) {
// //         return this.http.post(`/api/onboarding/upload-documents`, formData);
// //     }

// //     loadCandidatePhoto(submissionId: any): Observable<Blob> {
// //         var url = environment.API_URL + "onboarding/api/submissions/" + submissionId + "/photo";
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || '')
// //         });
// //         return this.http.get(url, {
// //             headers: reqHeader,
// //             responseType: 'blob'
// //         });
// //     }

// //     loadDocument(url: any): Observable<Blob> {
// //         var reqHeader = new HttpHeaders({
// //             'Authorization': 'Bearer ' + (localStorage.getItem("token") || '')
// //         });
// //         return this.http.get(url, {
// //             headers: reqHeader,
// //             responseType: 'blob'
// //         });
// //     }


// //     // ADD THESE METHODS TO YOUR EXISTING DataService

// // // ============================================
// // // 🆕 DYNAMIC RFQ APPROVAL APIs
// // // ============================================

// // /**
// //  * Initiate approval workflow (dynamic)
// //  */
// // initiateApprovalWorkflow(rfqId: number, creatorUserId: number): Observable<any> {
// //   const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/initiate';
// //   return this.invokePostAPI(url, { rfqId, creatorUserId });
// // }

// // /**
// //  * Get pending approvals for current user (dynamic)
// //  */
// // getPendingApprovalsForUser(userId: number): Observable<any> {
// //   const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}`;
// //   return this.invokeGetAPI(url);
// // }

// // /**
// //  * Approve RFQ (dynamic)
// //  */
// // approveRFQDynamic(rfqId: number, approverId: number, comments: string): Observable<any> {
// //   const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/approve';
// //   return this.invokePostAPI(url, { rfqId, approverId, comments });
// // }

// // /**
// //  * Reject RFQ (dynamic)
// //  */
// // rejectRFQDynamic(rfqId: number, rejectorId: number, comments: string): Observable<any> {
// //   const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/reject';
// //   return this.invokePostAPI(url, { rfqId, rejectorId, comments });
// // }

// // /**
// //  * Resubmit RFQ (dynamic)
// //  */
// // resubmitRFQDynamic(rfqId: number, resubmitterId: number): Observable<any> {
// //   const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/resubmit';
// //   return this.invokePostAPI(url, { rfqId, resubmitterId });
// // }

// // /**
// //  * Get approval history for RFQ
// //  */
// // getApprovalHistory(rfqId: number): Observable<any> {
// //   const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/history/${rfqId}`;
// //   return this.invokeGetAPI(url);
// // }

// // /**
// //  * Get current pending approval level for RFQ
// //  */
// // getCurrentPendingApproval(rfqId: number): Observable<any> {
// //   const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/current/${rfqId}`;
// //   return this.invokeGetAPI(url);
// // }

// // /**
// //  * Get pending approval count for user
// //  */
// // getPendingApprovalCount(userId: number): Observable<any> {
// //   const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}/count`;
// //   return this.invokeGetAPI(url);
// // }


// // }


// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { environment } from '../../environments/environment';
// import { Observable } from 'rxjs';
// import { catchError, tap } from 'rxjs/operators';
// import { throwError } from 'rxjs';

// @Injectable({
//     providedIn: 'root'
// })
// export class DataService {

//     constructor(private http: HttpClient) { }

//     // ============================================
//     // UTILITY METHODS
//     // ============================================
//     invokeGetAPI(url: any) {
//         var reqHeader = new HttpHeaders({
//             'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
//             'Content-Type': 'application/json'
//         });
//         return this.http.get<{ result: string; data: any }>(url, { headers: reqHeader }).pipe(
//             tap(response => console.log('%c[GET API SUCCESS]', 'color: #00aa00;', url, response)),
//             catchError(error => {
//                 console.error('%c[GET API ERROR]', 'color: #cc0000;', url, error);
//                 return throwError(() => error);
//             })
//         );
//     }

//     invokePostAPI(url: any, input: any) {
//         var reqHeader = new HttpHeaders({
//             'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
//             'Content-Type': 'application/json'
//         });
//         return this.http.post<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader }).pipe(
//             tap(response => console.log('%c[POST API SUCCESS]', 'color: #00aa00;', url, response)),
//             catchError(error => {
//                 console.error('%c[POST API ERROR]', 'color: #cc0000;', url, error);
//                 return throwError(() => error);
//             })
//         );
//     }

//     invokePutAPI(url: any, input: any) {
//         var reqHeader = new HttpHeaders({
//             'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
//             'Content-Type': 'application/json'
//         });
//         return this.http.put<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader }).pipe(
//             tap(response => console.log('%c[PUT API SUCCESS]', 'color: #00aa00;', url, response)),
//             catchError(error => {
//                 console.error('%c[PUT API ERROR]', 'color: #cc0000;', url, error);
//                 return throwError(() => error);
//             })
//         );
//     }

//     invokeDeleteAPI(url: any) {
//         var reqHeader = new HttpHeaders({
//             'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
//             'Content-Type': 'application/json'
//         });
//         return this.http.delete<{ result: string; data: any }>(url, { headers: reqHeader }).pipe(
//             tap(response => console.log('%c[DELETE API SUCCESS]', 'color: #00aa00;', url, response)),
//             catchError(error => {
//                 console.error('%c[DELETE API ERROR]', 'color: #cc0000;', url, error);
//                 return throwError(() => error);
//             })
//         );
//     }

//     // ============================================
//     // 🆕 DYNAMIC RFQ APPROVAL APIs - FIXED
//     // ============================================

//     /**
//      * ✅ FIXED: Initiate approval workflow (dynamic)
//      * POST /api/dynamic-rfq-approval/initiate
//      */
//     initiateApprovalWorkflow(rfqId: number, creatorUserId: number): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/initiate';
//         const payload = { rfqId, creatorUserId };
        
//         console.log('%c[INITIATE APPROVAL WORKFLOW]', 'color: #ff6600; font-weight: bold;', payload);
        
//         return this.invokePostAPI(url, payload);
//     }

//     /**
//      * ✅ FIXED: Get pending approvals for current user (dynamic)
//      * GET /api/dynamic-rfq-approval/pending/user/{userId}
//      */
//     getPendingApprovalsForUser(userId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}`;
        
//         console.log('%c[GET PENDING APPROVALS]', 'color: #0066cc; font-weight: bold;', {
//             userId: userId,
//             url: url
//         });
        
//         return this.invokeGetAPI(url);
//     }

//     /**
//      * ✅ FIXED: Approve RFQ (dynamic)
//      * POST /api/dynamic-rfq-approval/approve
//      */
//     approveRFQDynamic(rfqId: number, approverId: number, comments: string): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/approve';
//         const payload = { 
//             rfqId: rfqId, 
//             approverId: approverId, 
//             comments: comments || 'Approved' 
//         };
        
//         console.log('%c[APPROVE RFQ]', 'color: #00aa00; font-weight: bold;', payload);
        
//         return this.invokePostAPI(url, payload);
//     }

//     /**
//      * ✅ FIXED: Reject RFQ (dynamic)
//      * POST /api/dynamic-rfq-approval/reject
//      */
//     rejectRFQDynamic(rfqId: number, rejectorId: number, comments: string): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/reject';
//         const payload = { 
//             rfqId: rfqId, 
//             rejectorId: rejectorId, 
//             comments: comments 
//         };
        
//         console.log('%c[REJECT RFQ]', 'color: #cc0000; font-weight: bold;', payload);
        
//         return this.invokePostAPI(url, payload);
//     }

//     /**
//      * ✅ FIXED: Resubmit RFQ (dynamic)
//      * POST /api/dynamic-rfq-approval/resubmit
//      */
//     resubmitRFQDynamic(rfqId: number, resubmitterId: number): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/resubmit';
//         const payload = { 
//             rfqId: rfqId, 
//             resubmitterId: resubmitterId 
//         };
        
//         console.log('%c[RESUBMIT RFQ]', 'color: #0066cc; font-weight: bold;', payload);
        
//         return this.invokePostAPI(url, payload);
//     }

//     /**
//      * ✅ Get approval history for RFQ
//      * GET /api/dynamic-rfq-approval/history/{rfqId}
//      */
//     getApprovalHistory(rfqId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/history/${rfqId}`;
        
//         console.log('%c[GET APPROVAL HISTORY]', 'color: #0066cc;', rfqId);
        
//         return this.invokeGetAPI(url);
//     }

//     /**
//      * ✅ Get current pending approval level for RFQ
//      * GET /api/dynamic-rfq-approval/current/{rfqId}
//      */
//     getCurrentPendingApproval(rfqId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/current/${rfqId}`;
        
//         console.log('%c[GET CURRENT PENDING APPROVAL]', 'color: #0066cc;', rfqId);
        
//         return this.invokeGetAPI(url);
//     }

//     /**
//      * ✅ FIXED: Get pending approval count for user
//      * GET /api/dynamic-rfq-approval/pending/user/{userId}/count
//      */
//     getPendingApprovalCount(userId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}/count`;
        
//         console.log('%c[GET PENDING COUNT]', 'color: #0066cc;', userId);
        
//         return this.invokeGetAPI(url);
//     }

//     /**
//      * ✅ NEW: Get approval statistics for dashboard
//      */
//     getApprovalStatistics(userId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/statistics/user/${userId}`;
//         return this.invokeGetAPI(url);
//     }

//     // ============================================
//     // SUPERADMIN APIs
//     // ============================================
//     superAdminLogin(loginRequest: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/superadmin/auth/login';
//         return this.http.post<any>(url, loginRequest);
//     }

//     superAdminRegister(registerRequest: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/superadmin/auth/register';
//         return this.http.post<any>(url, registerRequest);
//     }

//     superAdminCreateOrgAdmin(orgAdminData: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/superadmin/create-org-admin';
//         return this.invokePostAPI(url, orgAdminData);
//     }

//     superAdminGetAllOrgAdmins(): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/superadmin/org-admins';
//         return this.invokeGetAPI(url);
//     }

//     // ============================================
//     // ORGANIZATION ADMIN APIs
//     // ============================================
//     organizationAdminLogin(loginRequest: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/organization-admin/auth/login';
//         return this.http.post<any>(url, loginRequest);
//     }

//     createOrganizationAdmin(orgAdminData: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/organization-admin';
//         return this.invokePostAPI(url, orgAdminData);
//     }

//     changeOrganizationAdminPassword(id: number, passwordData: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/change-password/${id}`;
//         return this.invokePutAPI(url, passwordData);
//     }

//     changeHierarchyUserPassword(id: number, passwordData: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/change-password`;
//         return this.invokePutAPI(url, passwordData);
//     }

//     createBuyerByOrgAdmin(adminId: number, buyerData: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyer`;
//         return this.invokePostAPI(url, buyerData);
//     }

//     getBuyersByOrganizationCompany(companyName: string): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/company/${encodeURIComponent(companyName)}/buyers`;
//         return this.invokeGetAPI(url);
//     }

//     getBuyersByOrgAdmin(adminId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyers`;
//         return this.invokeGetAPI(url);
//     }

//     getOrganizationAdminById(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
//         return this.invokeGetAPI(url);
//     }

//     getOrganizationAdminsByCompany(companyName: string): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/company/${companyName}`;
//         return this.invokeGetAPI(url);
//     }

//     getAllOrganizationAdmins(): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/organization-admin/all';
//         return this.invokeGetAPI(url);
//     }

//     updateOrganizationAdmin(id: number, data: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
//         return this.invokePutAPI(url, data);
//     }

//     deactivateOrganizationAdmin(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/deactivate`;
//         return this.invokePutAPI(url, {});
//     }

//     activateOrganizationAdmin(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/activate`;
//         return this.invokePutAPI(url, {});
//     }

//     deleteOrganizationAdmin(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
//         return this.invokeDeleteAPI(url);
//     }

//     // ============================================
//     // HIERARCHY LEVEL APIs
//     // ============================================
//     createHierarchyLevel(levelData: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
//         return this.invokePostAPI(url, levelData);
//     }

//     getAllHierarchyLevels(): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
//         return this.invokeGetAPI(url);
//     }

//     getHierarchyLevelsByCompany(companyName: string): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/company/${companyName}`;
//         return this.invokeGetAPI(url);
//     }

//     getHierarchyLevelById(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
//         return this.invokeGetAPI(url);
//     }

//     updateHierarchyLevel(id: number, levelData: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
//         return this.invokePutAPI(url, levelData);
//     }

//     deactivateHierarchyLevel(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/deactivate`;
//         return this.invokePutAPI(url, {});
//     }

//     activateHierarchyLevel(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/activate`;
//         return this.invokePutAPI(url, {});
//     }

//     deleteHierarchyLevel(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
//         return this.invokeDeleteAPI(url);
//     }

//     reorderHierarchyLevels(companyName: string, levelIds: number[]): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-levels/reorder/${companyName}`;
//         return this.invokePutAPI(url, levelIds);
//     }

//     // ============================================
//     // HIERARCHY USER APIs
//     // ============================================
//     hierarchyLogin(loginRequest: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/hierarchy-users/auth/login';
//         return this.http.post<any>(url, loginRequest);
//     }

//     createHierarchyUser(userData: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/hierarchy-users';
//         return this.invokePostAPI(url, userData);
//     }

//     getHierarchyUserById(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
//         return this.invokeGetAPI(url);
//     }

//     getHierarchyUsersByCompany(companyName: string): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/company/${companyName}`;
//         return this.invokeGetAPI(url);
//     }

//     getHierarchyUsersByLevel(levelId: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/level/${levelId}`;
//         return this.invokeGetAPI(url);
//     }

//     updateHierarchyUser(id: number, userData: any): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
//         return this.invokePutAPI(url, userData);
//     }

//     deactivateHierarchyUser(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/deactivate`;
//         return this.invokePutAPI(url, {});
//     }

//     activateHierarchyUser(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/activate`;
//         return this.invokePutAPI(url, {});
//     }

//     deleteHierarchyUser(id: number): Observable<any> {
//         const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
//         return this.invokeDeleteAPI(url);
//     }

//     // ============================================
//     // BUYER LOGIN
//     // ============================================
//     buyerLogin(loginRequest: any): Observable<any> {
//         const url = environment.API_URL + 'leadcapture/api/buyer/login';
//         return this.http.post<any>(url, loginRequest);
//     }
// }


import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DataService {

    constructor(private http: HttpClient) { }

    // ============================================
    // UTILITY METHODS
    // ============================================
    invokeGetAPI(url: any) {
        var reqHeader = new HttpHeaders({
            'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
            'Content-Type': 'application/json'
        });
        return this.http.get<{ result: string; data: any }>(url, { headers: reqHeader }).pipe(
            tap(response => console.log('%c[GET API SUCCESS]', 'color: #00aa00;', url, response)),
            catchError(error => {
                console.error('%c[GET API ERROR]', 'color: #cc0000;', url, error);
                return throwError(() => error);
            })
        );
    }

    invokePostAPI(url: any, input: any) {
        var reqHeader = new HttpHeaders({
            'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
            'Content-Type': 'application/json'
        });
        return this.http.post<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader }).pipe(
            tap(response => console.log('%c[POST API SUCCESS]', 'color: #00aa00;', url, response)),
            catchError(error => {
                console.error('%c[POST API ERROR]', 'color: #cc0000;', url, error);
                return throwError(() => error);
            })
        );
    }

    invokePutAPI(url: any, input: any) {
        var reqHeader = new HttpHeaders({
            'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
            'Content-Type': 'application/json'
        });
        return this.http.put<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader }).pipe(
            tap(response => console.log('%c[PUT API SUCCESS]', 'color: #00aa00;', url, response)),
            catchError(error => {
                console.error('%c[PUT API ERROR]', 'color: #cc0000;', url, error);
                return throwError(() => error);
            })
        );
    }

    invokeDeleteAPI(url: any) {
        var reqHeader = new HttpHeaders({
            'Authorization': 'Bearer ' + (localStorage.getItem("token") || ''),
            'Content-Type': 'application/json'
        });
        return this.http.delete<{ result: string; data: any }>(url, { headers: reqHeader }).pipe(
            tap(response => console.log('%c[DELETE API SUCCESS]', 'color: #00aa00;', url, response)),
            catchError(error => {
                console.error('%c[DELETE API ERROR]', 'color: #cc0000;', url, error);
                return throwError(() => error);
            })
        );
    }

    // ============================================
    // 🆕 DYNAMIC RFQ APPROVAL APIs - COMPLETE & FIXED
    // ============================================

    /**
     * ✅ FIXED: Initiate approval workflow (dynamic)
     * POST /api/dynamic-rfq-approval/initiate
     * 
     * Backend expects: { "rfqId": number, "creatorUserId": number }
     */
    initiateApprovalWorkflow(rfqId: number, creatorUserId: number): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/initiate';
        const payload = { rfqId, creatorUserId };
        
        console.log('%c[INITIATE APPROVAL WORKFLOW]', 'color: #ff6600; font-weight: bold;', payload);
        
        return this.invokePostAPI(url, payload);
    }

    /**
     * ✅ FIXED: Get pending approvals for current user (dynamic)
     * GET /api/dynamic-rfq-approval/pending/user/{userId}
     * 
     * ⚠️ CRITICAL: This endpoint returns List<DynamicRFQApproval> directly
     * Backend response structure:
     * {
     *   "success": true,
     *   "data": [ ... array of approvals ... ]
     * }
     */
    getPendingApprovalsForUser(userId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}`;
        
        console.log('%c[GET PENDING APPROVALS]', 'color: #0066cc; font-weight: bold;', {
            userId: userId,
            url: url
        });
        
        return this.invokeGetAPI(url).pipe(
            map((response: any) => {
                console.log('%c[PENDING APPROVALS RAW RESPONSE]', 'color: #9c27b0;', response);
                
                // ✅ Handle different response structures
                if (response && response.success === true) {
                    // Response has success: true wrapper
                    return {
                        success: true,
                        data: Array.isArray(response.data) ? response.data : []
                    };
                } else if (Array.isArray(response)) {
                    // Response is array directly
                    return {
                        success: true,
                        data: response
                    };
                } else {
                    // Unknown structure
                    return {
                        success: true,
                        data: []
                    };
                }
            }),
            catchError((error: any) => {
                console.error('%c[PENDING APPROVALS ERROR]', 'color: #cc0000; font-weight: bold;', {
                    status: error.status,
                    message: error.error?.message || error.message,
                    url: url
                });
                return throwError(() => error);
            })
        );
    }

    /**
     * ✅ FIXED: Approve RFQ (dynamic)
     * POST /api/dynamic-rfq-approval/approve
     * 
     * Backend expects: { "rfqId": number, "approverId": number, "comments": string }
     */
    approveRFQDynamic(rfqId: number, approverId: number, comments: string): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/approve';
        const payload = { 
            rfqId: rfqId, 
            approverId: approverId, 
            comments: comments || 'Approved' 
        };
        
        console.log('%c[APPROVE RFQ]', 'color: #00aa00; font-weight: bold;', payload);
        
        return this.invokePostAPI(url, payload);
    }

    /**
     * ✅ FIXED: Reject RFQ (dynamic)
     * POST /api/dynamic-rfq-approval/reject
     * 
     * Backend expects: { "rfqId": number, "rejectorId": number, "comments": string }
     */
    rejectRFQDynamic(rfqId: number, rejectorId: number, comments: string): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/reject';
        const payload = { 
            rfqId: rfqId, 
            rejectorId: rejectorId, 
            comments: comments 
        };
        
        console.log('%c[REJECT RFQ]', 'color: #cc0000; font-weight: bold;', payload);
        
        return this.invokePostAPI(url, payload);
    }

    /**
     * ✅ FIXED: Resubmit RFQ (dynamic)
     * POST /api/dynamic-rfq-approval/resubmit
     * 
     * Backend expects: { "rfqId": number, "resubmitterId": number }
     */
    resubmitRFQDynamic(rfqId: number, resubmitterId: number): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/dynamic-rfq-approval/resubmit';
        const payload = { 
            rfqId: rfqId, 
            resubmitterId: resubmitterId 
        };
        
        console.log('%c[RESUBMIT RFQ]', 'color: #0066cc; font-weight: bold;', payload);
        
        return this.invokePostAPI(url, payload);
    }

    /**
     * ✅ Get approval history for RFQ
     * GET /api/dynamic-rfq-approval/history/{rfqId}
     */
    getApprovalHistory(rfqId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/history/${rfqId}`;
        
        console.log('%c[GET APPROVAL HISTORY]', 'color: #0066cc;', rfqId);
        
        return this.invokeGetAPI(url);
    }

    /**
     * ✅ Get current pending approval level for RFQ
     * GET /api/dynamic-rfq-approval/current/{rfqId}
     */
    getCurrentPendingApproval(rfqId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/current/${rfqId}`;
        
        console.log('%c[GET CURRENT PENDING APPROVAL]', 'color: #0066cc;', rfqId);
        
        return this.invokeGetAPI(url);
    }

    /**
     * ✅ FIXED: Get pending approval count for user
     * GET /api/dynamic-rfq-approval/pending/user/{userId}/count
     */
    getPendingApprovalCount(userId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/pending/user/${userId}/count`;
        
        console.log('%c[GET PENDING COUNT]', 'color: #0066cc;', userId);
        
        return this.invokeGetAPI(url).pipe(
            map((response: any) => {
                console.log('%c[PENDING COUNT RAW]', 'color: #9c27b0;', response);
                
                // ✅ Extract count from different response structures
                let count = 0;
                
                if (response && typeof response.data === 'number') {
                    count = response.data;
                } else if (response && typeof response.count === 'number') {
                    count = response.count;
                } else if (response && typeof response.pendingCount === 'number') {
                    count = response.pendingCount;
                } else if (typeof response === 'number') {
                    count = response;
                }
                
                return {
                    success: true,
                    pendingCount: count
                };
            })
        );
    }

    /**
     * ✅ NEW: Get approval statistics for dashboard
     */
    getApprovalStatistics(userId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/dynamic-rfq-approval/statistics/user/${userId}`;
        return this.invokeGetAPI(url);
    }

    // ============================================
    // RFQ DASHBOARD APIs
    // ============================================

    /**
     * ✅ Get RFQ dashboard statistics
     * GET /api/rfq-dashboard/statistics/{buyerId}/{userId}
     */
    getRFQDashboardStatistics(buyerId: number, userId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/rfq-dashboard/statistics/${buyerId}/${userId}`;
        console.log('%c[GET DASHBOARD STATS]', 'color: #0066cc;', { buyerId, userId });
        return this.invokeGetAPI(url);
    }

    /**
     * ✅ Get filtered RFQ list for dashboard
     * GET /api/rfq-dashboard/rfqs/{buyerId}?search=...&approvalStatus=...&status=...
     */
    /**
 * ✅ NEW: Get RFQ dashboard list with supplier counts
 */
getRFQDashboardList(userId: number, params?: any): Observable<any> {
    // For hierarchy users, we fetch all RFQs for their company
    // The backend will include supplier counts
    const url = environment.API_URL + 'leadcapture/api/rfq';
    
    console.log('%c[GET DASHBOARD LIST]', 'color: #0066cc;', url);
    return this.invokeGetAPI(url);
}
    // getRFQDashboardList(buyerId: number, params?: any): Observable<any> {
    //     let url = environment.API_URL + `leadcapture/api/rfq-dashboard/rfqs/${buyerId}`;
        
    //     if (params) {
    //         const queryParams = new URLSearchParams();
    //         if (params.search) queryParams.set('search', params.search);
    //         if (params.approvalStatus) queryParams.set('approvalStatus', params.approvalStatus);
    //         if (params.status) queryParams.set('status', params.status);
            
    //         if (queryParams.toString()) {
    //             url += '?' + queryParams.toString();
    //         }
    //     }
        
    //     console.log('%c[GET DASHBOARD LIST]', 'color: #0066cc;', url);
    //     return this.invokeGetAPI(url);
    // }

    /**
     * ✅ Get RFQ details with full information
     * GET /api/rfq-dashboard/rfq/{rfqId}/details
     */
    getRFQDetails(rfqId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/rfq-dashboard/rfq/${rfqId}/details`;
        console.log('%c[GET RFQ DETAILS]', 'color: #0066cc;', rfqId);
        return this.invokeGetAPI(url);
    }

    // ============================================
    // SUPERADMIN APIs (PRESERVED FROM OLD CODE)
    // ============================================
    superAdminLogin(loginRequest: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/superadmin/auth/login';
        return this.http.post<any>(url, loginRequest);
    }

    superAdminRegister(registerRequest: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/superadmin/auth/register';
        return this.http.post<any>(url, registerRequest);
    }

    superAdminCreateOrgAdmin(orgAdminData: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/superadmin/create-org-admin';
        return this.invokePostAPI(url, orgAdminData);
    }

    superAdminGetAllOrgAdmins(): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/superadmin/org-admins';
        return this.invokeGetAPI(url);
    }

    // ============================================
    // ORGANIZATION ADMIN APIs
    // ============================================
    organizationAdminLogin(loginRequest: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/organization-admin/auth/login';
        return this.http.post<any>(url, loginRequest);
    }

    createOrganizationAdmin(orgAdminData: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/organization-admin';
        return this.invokePostAPI(url, orgAdminData);
    }

    changeOrganizationAdminPassword(id: number, passwordData: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/change-password/${id}`;
        return this.invokePutAPI(url, passwordData);
    }

    createBuyerByOrgAdmin(adminId: number, buyerData: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyer`;
        return this.invokePostAPI(url, buyerData);
    }

    getBuyersByOrganizationCompany(companyName: string): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/company/${encodeURIComponent(companyName)}/buyers`;
        return this.invokeGetAPI(url);
    }

    getBuyersByOrgAdmin(adminId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${adminId}/buyers`;
        return this.invokeGetAPI(url);
    }

    getOrganizationAdminById(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
        return this.invokeGetAPI(url);
    }

    getOrganizationAdminsByCompany(companyName: string): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/company/${companyName}`;
        return this.invokeGetAPI(url);
    }

    getAllOrganizationAdmins(): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/organization-admin/all';
        return this.invokeGetAPI(url);
    }

    updateOrganizationAdmin(id: number, data: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
        return this.invokePutAPI(url, data);
    }

    deactivateOrganizationAdmin(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/deactivate`;
        return this.invokePutAPI(url, {});
    }

    activateOrganizationAdmin(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${id}/activate`;
        return this.invokePutAPI(url, {});
    }

    deleteOrganizationAdmin(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/organization-admin/${id}`;
        return this.invokeDeleteAPI(url);
    }

    // ============================================
    // HIERARCHY LEVEL APIs
    // ============================================
    createHierarchyLevel(levelData: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
        return this.invokePostAPI(url, levelData);
    }

    getAllHierarchyLevels(): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/hierarchy-levels';
        return this.invokeGetAPI(url);
    }

    getHierarchyLevelsByCompany(companyName: string): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/company/${companyName}`;
        return this.invokeGetAPI(url);
    }

    getHierarchyLevelById(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
        return this.invokeGetAPI(url);
    }

    updateHierarchyLevel(id: number, levelData: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
        return this.invokePutAPI(url, levelData);
    }

    deactivateHierarchyLevel(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/deactivate`;
        return this.invokePutAPI(url, {});
    }

    activateHierarchyLevel(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}/activate`;
        return this.invokePutAPI(url, {});
    }

    deleteHierarchyLevel(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/${id}`;
        return this.invokeDeleteAPI(url);
    }

    reorderHierarchyLevels(companyName: string, levelIds: number[]): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-levels/reorder/${companyName}`;
        return this.invokePutAPI(url, levelIds);
    }

    // ============================================
    // HIERARCHY USER APIs
    // ============================================
    hierarchyLogin(loginRequest: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/hierarchy-users/auth/login';
        return this.http.post<any>(url, loginRequest).pipe(
            tap((response) => {
                console.log('%c[HIERARCHY LOGIN RESPONSE]', 'color: #00aa00; font-weight: bold;', response);
            }),
            catchError((error) => {
                console.error('%c[HIERARCHY LOGIN ERROR]', 'color: #cc0000;', error);
                return throwError(() => error);
            })
        );
    }

    createHierarchyUser(userData: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/hierarchy-users';
        return this.invokePostAPI(url, userData);
    }

    getHierarchyUserById(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
        return this.invokeGetAPI(url);
    }

    getHierarchyUsersByCompany(companyName: string): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/company/${companyName}`;
        return this.invokeGetAPI(url);
    }

    getHierarchyUsersByLevel(levelId: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/level/${levelId}`;
        return this.invokeGetAPI(url);
    }

    updateHierarchyUser(id: number, userData: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
        return this.invokePutAPI(url, userData);
    }

    deactivateHierarchyUser(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/deactivate`;
        return this.invokePutAPI(url, {});
    }

    activateHierarchyUser(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/activate`;
        return this.invokePutAPI(url, {});
    }

    deleteHierarchyUser(id: number): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}`;
        return this.invokeDeleteAPI(url);
    }

    changeHierarchyUserPassword(id: number, passwordData: any): Observable<any> {
        const url = environment.API_URL + `leadcapture/api/hierarchy-users/${id}/change-password`;
        return this.invokePutAPI(url, passwordData);
    }

    // ============================================
    // BUYER LOGIN
    // ============================================
    buyerLogin(loginRequest: any): Observable<any> {
        const url = environment.API_URL + 'leadcapture/api/buyer/login';
        return this.http.post<any>(url, loginRequest).pipe(
            tap((response) => {
                console.log('%c[BUYER LOGIN RESPONSE]', 'color: #00aa00; font-weight: bold;', response);
            }),
            catchError((error) => {
                console.error('%c[BUYER LOGIN ERROR]', 'color: #cc0000;', error);
                return throwError(() => error);
            })
        );
    }
}