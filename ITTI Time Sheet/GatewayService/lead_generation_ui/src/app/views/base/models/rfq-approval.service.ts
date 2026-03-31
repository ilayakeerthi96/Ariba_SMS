import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ApprovalHistoryItem {
  id: number;
  level: string;
  levelName: string;
  status: string;
  approverId: number | null;
  comments: string | null;
  actionDate: string | null;
  sequenceOrder: number;
}

export interface CurrentApproval {
  id: number;
  level: string;
  levelName: string;
  status: string;
  sequenceOrder: number;
}

export interface PendingApprovalRFQ {
  id: number;
  rfqId: number;
  rfqNumber: string;
  rfqTitle: string;
  level: string;
  levelName: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class RfqApprovalService {

  private baseUrl = environment.API_URL + 'leadcapture/api/rfq-approval';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + (localStorage.getItem('token') || ''),
      'Content-Type': 'application/json'
    });
  }

  // ==================== INITIATE WORKFLOW ====================

  /**
   * Initiate approval workflow for an RFQ
   * POST /api/rfq-approval/initiate/{rfqId}
   */
  initiateApprovalWorkflow(rfqId: number): Observable<any> {
    const url = `${this.baseUrl}/initiate/${rfqId}`;
    return this.http.post(url, {}, { headers: this.getHeaders() });
  }

  // ==================== APPROVE ====================

  /**
   * Approve RFQ at current level
   * POST /api/rfq-approval/approve/{rfqId}
   */
  approveRFQ(rfqId: number, approverId: number, approvalLevel: string, comments: string): Observable<any> {
    const url = `${this.baseUrl}/approve/${rfqId}`;
    const body = {
      approverId: approverId,
      approvalLevel: approvalLevel,
      comments: comments
    };
    return this.http.post(url, body, { headers: this.getHeaders() });
  }

  // ==================== REJECT ====================

  /**
   * Reject RFQ at current level
   * POST /api/rfq-approval/reject/{rfqId}
   */
  rejectRFQ(rfqId: number, rejectorId: number, approvalLevel: string, comments: string): Observable<any> {
    const url = `${this.baseUrl}/reject/${rfqId}`;
    const body = {
      rejectorId: rejectorId,
      approvalLevel: approvalLevel,
      comments: comments
    };
    return this.http.post(url, body, { headers: this.getHeaders() });
  }

  // ==================== RESUBMIT ====================

  /**
   * Resubmit rejected RFQ
   * POST /api/rfq-approval/resubmit/{rfqId}
   */
  resubmitRFQ(rfqId: number, resubmitterId: number): Observable<any> {
    const url = `${this.baseUrl}/resubmit/${rfqId}`;
    const body = {
      resubmitterId: resubmitterId
    };
    return this.http.post(url, body, { headers: this.getHeaders() });
  }

  // ==================== GET APPROVAL HISTORY ====================

  /**
   * Get approval history for an RFQ
   * GET /api/rfq-approval/history/{rfqId}
   */
  getApprovalHistory(rfqId: number): Observable<any> {
    const url = `${this.baseUrl}/history/${rfqId}`;
    return this.http.get(url, { headers: this.getHeaders() });
  }

  // ==================== GET CURRENT PENDING APPROVAL ====================

  /**
   * Get current pending approval level for an RFQ
   * GET /api/rfq-approval/current/{rfqId}
   */
  getCurrentPendingApproval(rfqId: number): Observable<any> {
    const url = `${this.baseUrl}/current/${rfqId}`;
    return this.http.get(url, { headers: this.getHeaders() });
  }

  // ==================== GET PENDING APPROVALS BY LEVEL ====================

  /**
   * Get all pending approvals at a specific level
   * GET /api/rfq-approval/pending/{level}
   */
  getPendingApprovalsByLevel(level: string): Observable<any> {
    const url = `${this.baseUrl}/pending/${level}`;
    return this.http.get(url, { headers: this.getHeaders() });
  }

  // ==================== HELPER METHODS ====================

  /**
   * Get user's approval level based on role
   */
  getUserApprovalLevel(): string | null {
    const role = localStorage.getItem('role');
    
    if (role === 'PROCUREMENT') return 'PROCUREMENT';
    if (role === 'COO') return 'COO';
    if (role === 'CEO') return 'CEO';
    if (role === 'FINANCE') return 'FINANCE';
    
    return null;
  }

  /**
   * Check if user can approve at this level
   */
  canUserApprove(): boolean {
    return this.getUserApprovalLevel() !== null;
  }

  /**
   * Get level display name
   */
  getLevelDisplayName(level: string): string {
    const levelNames: { [key: string]: string } = {
      'PROCUREMENT': 'Procurement Team',
      'COO': 'Chief Operating Officer',
      'CEO': 'Chief Executive Officer',
      'FINANCE': 'Finance Team'
    };
    return levelNames[level] || level;
  }

  /**
   * Get status badge class
   */
  getStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'bg-warning text-dark',
      'APPROVED': 'bg-success',
      'REJECTED': 'bg-danger',
      'RESUBMITTED': 'bg-info',
      'SKIPPED': 'bg-secondary'
    };
    return statusMap[status] || 'bg-light text-dark';
  }
}