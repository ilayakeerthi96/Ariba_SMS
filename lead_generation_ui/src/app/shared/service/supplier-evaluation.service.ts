import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SupplierEvaluationService {

  private baseUrl = environment.API_URL + 'leadcapture/api/supplier-evaluation';

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + (localStorage.getItem('token') || ''),
      'Content-Type': 'application/json'
    });
  }

  // ==================== CRITERION MANAGEMENT ====================

  /**
   * Get all active evaluation criteria
   */
  getAllActiveCriteria(): Observable<any> {
    const url = `${this.baseUrl}/criteria`;
    console.log('📋 [GET] Active Criteria:', url);
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Criteria:', response)),
      catchError(error => {
        console.error('❌ Error fetching criteria:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Create new criterion (Admin only)
   */
  createCriterion(criterion: any): Observable<any> {
    const url = `${this.baseUrl}/criteria`;
    console.log('➕ [POST] Create Criterion:', criterion);
    
    return this.http.post(url, criterion, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Criterion created:', response)),
      catchError(error => {
        console.error('❌ Error creating criterion:', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== RFQ CRITERION ASSIGNMENT ====================

  /**
   * Assign criteria to RFQ with weightages
   */
  assignCriteriaToRFQ(rfqId: number, userId: number, criteriaWeightages: Map<number, number>): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/assign-criteria`;
    
    // Convert Map to object
    const weightagesObj: any = {};
    criteriaWeightages.forEach((value, key) => {
      weightagesObj[key] = value;
    });
    
    const payload = {
      userId: userId,
      criteriaWeightages: weightagesObj
    };
    
    console.log('🎯 [POST] Assign Criteria:', payload);
    
    return this.http.post(url, payload, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Criteria assigned:', response)),
      catchError(error => {
        console.error('❌ Error assigning criteria:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Get criteria assigned to RFQ
   */
  getRFQCriteria(rfqId: number): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/criteria`;
    console.log('📋 [GET] RFQ Criteria:', rfqId);
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ RFQ Criteria:', response)),
      catchError(error => {
        console.error('❌ Error fetching RFQ criteria:', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== SUPPLIER SCORING ====================

  /**
   * Score a supplier manually
   */
  scoreSupplier(rfqId: number, supplierId: number, userId: number, scores: Map<number, number>): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/supplier/${supplierId}/score`;
    
    // Convert Map to object
    const scoresObj: any = {};
    scores.forEach((value, key) => {
      scoresObj[key] = value;
    });
    
    const payload = {
      userId: userId,
      scores: scoresObj
    };
    
    console.log('📊 [POST] Score Supplier:', payload);
    
    return this.http.post(url, payload, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Supplier scored:', response)),
      catchError(error => {
        console.error('❌ Error scoring supplier:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Calculate automatic price scores
   */
  calculatePriceScores(rfqId: number, userId: number): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/calculate-price-scores`;
    
    const payload = { userId: userId };
    
    console.log('💰 [POST] Calculate Price Scores:', rfqId);
    
    return this.http.post(url, payload, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Price scores calculated:', response)),
      catchError(error => {
        console.error('❌ Error calculating price scores:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Score all suppliers in batch
   */
  scoreAllSuppliers(rfqId: number, userId: number, supplierScores: any): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/score-all-suppliers`;
    
    const payload = {
      userId: userId,
      supplierScores: supplierScores
    };
    
    console.log('📊 [POST] Batch Score Suppliers:', payload);
    
    return this.http.post(url, payload, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Batch scoring complete:', response)),
      catchError(error => {
        console.error('❌ Error batch scoring:', error);
        return throwError(() => error);
      })
    );
  }

  // ==================== RANKINGS & RECOMMENDATIONS ====================

  /**
   * Get supplier rankings
   */
  getSupplierRankings(rfqId: number): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/rankings`;
    console.log('🏆 [GET] Supplier Rankings:', rfqId);
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Rankings:', response)),
      catchError(error => {
        console.error('❌ Error fetching rankings:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Get detailed scorecard for supplier
   */
  getSupplierScorecard(rfqId: number, supplierId: number): Observable<any> {
    const url = `${this.baseUrl}/rfq/${rfqId}/supplier/${supplierId}/scorecard`;
    console.log('📊 [GET] Supplier Scorecard:', { rfqId, supplierId });
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ Scorecard:', response)),
      catchError(error => {
        console.error('❌ Error fetching scorecard:', error);
        return throwError(() => error);
      })
    );
  }
}