// // rfq-report.service.ts
// // Place this file in: src/app/shared/service/rfq-report.service.ts

// import { Injectable } from '@angular/core';
// import { HttpClient, HttpParams } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { environment } from '../../environments/environment';

// /**
//  * RFQ Report Service
//  * Handles all report download calls to the backend.
//  *
//  * Usage in a component:
//  *   constructor(private reportService: RfqReportService) {}
//  *
//  *   downloadExcel() {
//  *     this.reportService.downloadRFQSummaryExcel(rfqId);
//  *   }
//  */
// @Injectable({ providedIn: 'root' })
// export class RfqReportService {

//   // ── adjust if your base URL differs ──────────────────────────────────────
//   private base = `http://localhost:9092/leadcapture/api/reports`;

//   constructor(private http: HttpClient) {}

//   // =========================================================================
//   //  1.  RFQ Summary — Excel
//   // =========================================================================
//   downloadRFQSummaryExcel(rfqId: number): void {
//     this.triggerDownload(`${this.base}/rfq/${rfqId}/excel`,
//         `RFQ_Summary_${rfqId}.xlsx`);
//   }

//   /** Observable version — use when you need to track loading state */
//   getRFQSummaryExcel(rfqId: number): Observable<Blob> {
//     return this.http.get(`${this.base}/rfq/${rfqId}/excel`, { responseType: 'blob' });
//   }

//   // =========================================================================
//   //  2.  RFQ Summary — PDF
//   // =========================================================================
//   downloadRFQSummaryPDF(rfqId: number): void {
//     this.triggerDownload(`${this.base}/rfq/${rfqId}/pdf`,
//         `RFQ_Summary_${rfqId}.pdf`);
//   }

//   getRFQSummaryPDF(rfqId: number): Observable<Blob> {
//     return this.http.get(`${this.base}/rfq/${rfqId}/pdf`, { responseType: 'blob' });
//   }

//   // =========================================================================
//   //  3.  RFQ List — Excel
//   // =========================================================================
//   downloadRFQListExcel(buyerId: number, status: string = 'ALL'): void {
//     const url = `${this.base}/rfq/list/excel?buyerId=${buyerId}&status=${status}`;
//     this.triggerDownload(url, `RFQ_List_${buyerId}_${status}.xlsx`);
//   }

//   getRFQListExcel(buyerId: number, status: string = 'ALL'): Observable<Blob> {
//     const params = new HttpParams().set('buyerId', buyerId).set('status', status);
//     return this.http.get(`${this.base}/rfq/list/excel`, { responseType: 'blob', params });
//   }

//   // =========================================================================
//   //  4.  Quote Comparison — Excel
//   // =========================================================================
//   downloadQuoteComparisonExcel(rfqId: number): void {
//     this.triggerDownload(`${this.base}/rfq/${rfqId}/quote-comparison/excel`,
//         `Quote_Comparison_${rfqId}.xlsx`);
//   }

//   getQuoteComparisonExcel(rfqId: number): Observable<Blob> {
//     return this.http.get(`${this.base}/rfq/${rfqId}/quote-comparison/excel`,
//         { responseType: 'blob' });
//   }

//   // =========================================================================
//   //  5.  Quote Comparison — PDF
//   // =========================================================================
//   downloadQuoteComparisonPDF(rfqId: number): void {
//     this.triggerDownload(`${this.base}/rfq/${rfqId}/quote-comparison/pdf`,
//         `Quote_Comparison_${rfqId}.pdf`);
//   }

//   getQuoteComparisonPDF(rfqId: number): Observable<Blob> {
//     return this.http.get(`${this.base}/rfq/${rfqId}/quote-comparison/pdf`,
//         { responseType: 'blob' });
//   }

//   // =========================================================================
//   //  PRIVATE: trigger file download via hidden <a> tag
//   // =========================================================================

//   /**
//    * Observable-based download — shows loading state to the caller.
//    *
//    * Usage:
//    *   this.reportService.downloadWithLoader(rfqId, 'rfq-excel').subscribe({
//    *     next: blob => this.reportService.saveBlob(blob, 'report.xlsx'),
//    *     error: () => alert('Download failed')
//    *   });
//    */
//   downloadWithLoader(rfqId: number, type: ReportType): Observable<Blob> {
//     switch (type) {
//       case 'rfq-excel':       return this.getRFQSummaryExcel(rfqId);
//       case 'rfq-pdf':         return this.getRFQSummaryPDF(rfqId);
//       case 'quote-excel':     return this.getQuoteComparisonExcel(rfqId);
//       case 'quote-pdf':       return this.getQuoteComparisonPDF(rfqId);
//       default:                return this.getRFQSummaryExcel(rfqId);
//     }
//   }

//   /** Save a Blob returned by an Observable call */
//   saveBlob(blob: Blob, filename: string): void {
//     const url  = window.URL.createObjectURL(blob);
//     const link = document.createElement('a');
//     link.href  = url;
//     link.download = filename;
//     link.click();
//     window.URL.revokeObjectURL(url);
//   }

//   private triggerDownload(url: string, filename: string): void {
//     const link = document.createElement('a');
//     link.href  = url;
//     link.download = filename;
//     link.target = '_blank';
//     document.body.appendChild(link);
//     link.click();
//     document.body.removeChild(link);
//   }
// }

// export type ReportType = 'rfq-excel' | 'rfq-pdf' | 'quote-excel' | 'quote-pdf' | 'rfq-list-excel';