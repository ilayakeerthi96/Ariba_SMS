

// // package com.itti.leadcapturing.web;

// // import com.itti.leadcapturing.service.RFQReportService;
// // import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
// // import org.slf4j.Logger;
// // import org.slf4j.LoggerFactory;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.http.HttpHeaders;
// // import org.springframework.http.MediaType;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;

// // import java.io.ByteArrayOutputStream;
// // import java.time.LocalDateTime;
// // import java.time.format.DateTimeFormatter;

// // /**
// //  * RFQ Report Controller
// //  *
// //  * Endpoints:
// //  *   GET /api/reports/rfq/{rfqId}/excel                         → RFQ Summary Excel (BUYER — 12 sheets)
// //  *   GET /api/reports/rfq/{rfqId}/excel/supplier                → RFQ Summary Excel (SUPPLIER — 2 sheets)
// //  *   GET /api/reports/rfq/{rfqId}/pdf                           → RFQ Summary PDF
// //  *   GET /api/reports/rfq/list/excel?buyerId=&status=           → RFQ List Excel
// //  *   GET /api/reports/rfq/{rfqId}/quote-comparison/excel        → Quote Comparison Excel
// //  *   GET /api/reports/rfq/{rfqId}/quote-comparison/pdf          → Quote Comparison PDF
// //  *   GET /api/reports/invoice/{invoiceId}/excel                 → Invoice Summary Excel
// //  *   GET /api/reports/invoice/{invoiceId}/pdf                   → Invoice Summary PDF
// //  *   GET /api/reports/po/{poId}/excel                           → PO Summary Excel
// //  *   GET /api/reports/po/{poId}/pdf                             → PO Summary PDF
// //  *   GET /api/reports/grn/{grnId}/excel                         → GRN Excel
// //  *   GET /api/reports/three-way-match/{matchId}/excel           → 3-Way Match Excel
// //  */
// // @RestController
// // @RequestMapping("/api/reports")
// // @CrossOrigin(origins = "*")
// // public class RFQReportController {

// //     private static final Logger logger = LoggerFactory.getLogger(RFQReportController.class);
// //     private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

// //     @Autowired
// //     private RFQReportService rfqReportService;

// //     // =========================================================================
// //     //  1. RFQ SUMMARY — EXCEL (BUYER version — 12 sheets)
// //     // =========================================================================
// //     @GetMapping("/rfq/{rfqId}/excel")
// //     public ResponseEntity<byte[]> downloadRFQSummaryExcel(@PathVariable Long rfqId) {
// //         logger.info("📊 [REPORT] RFQ Summary Excel (Buyer) — RFQ ID: {}", rfqId);
// //         try {
// //             byte[] data = rfqReportService.generateRFQSummaryExcel(rfqId, false);
// //             String filename = "RFQ_Summary_" + rfqId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ RFQ Summary Excel (Buyer) failed for RFQ {}: {}", rfqId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  2. RFQ SUMMARY — EXCEL (SUPPLIER version — 2 sheets only)
// //     // =========================================================================
// //     @GetMapping("/rfq/{rfqId}/excel/supplier")
// //     public ResponseEntity<byte[]> downloadRFQSummaryExcelForSupplier(@PathVariable Long rfqId) {
// //         logger.info("📊 [REPORT] RFQ Summary Excel (Supplier) — RFQ ID: {}", rfqId);
// //         try {
// //             byte[] data = rfqReportService.generateRFQSummaryExcel(rfqId, true);
// //             String filename = "RFQ_Summary_" + rfqId + "_Supplier_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ RFQ Summary Excel (Supplier) failed for RFQ {}: {}", rfqId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  3. RFQ SUMMARY — PDF
// //     // =========================================================================
// //     @GetMapping("/rfq/{rfqId}/pdf")
// //     public ResponseEntity<byte[]> downloadRFQSummaryPDF(@PathVariable Long rfqId) {
// //         logger.info("📄 [REPORT] RFQ Summary PDF — RFQ ID: {}", rfqId);
// //         try {
// //             String html = rfqReportService.generateRFQSummaryHTML(rfqId);
// //             byte[] pdfBytes = renderHtmlToPdf(html);
// //             String filename = "RFQ_Summary_" + rfqId + "_" + ts() + ".pdf";
// //             return pdfResponse(pdfBytes, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ RFQ Summary PDF failed for RFQ {}: {}", rfqId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  4. RFQ LIST — EXCEL
// //     // =========================================================================
// //     @GetMapping("/rfq/list/excel")
// //     public ResponseEntity<byte[]> downloadRFQListExcel(
// //             @RequestParam Long buyerId,
// //             @RequestParam(defaultValue = "ALL") String status) {
// //         logger.info("📊 [REPORT] RFQ List Excel — Buyer ID: {}, Status: {}", buyerId, status);
// //         try {
// //             byte[] data = rfqReportService.generateRFQListExcel(buyerId, status);
// //             String filename = "RFQ_List_Buyer" + buyerId + "_" + status + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ RFQ List Excel failed for Buyer {}: {}", buyerId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  5. QUOTE COMPARISON — EXCEL
// //     // =========================================================================
// //     @GetMapping("/rfq/{rfqId}/quote-comparison/excel")
// //     public ResponseEntity<byte[]> downloadQuoteComparisonExcel(@PathVariable Long rfqId) {
// //         logger.info("📊 [REPORT] Quote Comparison Excel — RFQ ID: {}", rfqId);
// //         try {
// //             byte[] data = rfqReportService.generateQuoteComparisonExcel(rfqId);
// //             String filename = "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ Quote Comparison Excel failed for RFQ {}: {}", rfqId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  6. QUOTE COMPARISON — PDF
// //     // =========================================================================
// //     @GetMapping("/rfq/{rfqId}/quote-comparison/pdf")
// //     public ResponseEntity<byte[]> downloadQuoteComparisonPDF(@PathVariable Long rfqId) {
// //         logger.info("📄 [REPORT] Quote Comparison PDF — RFQ ID: {}", rfqId);
// //         try {
// //             String html = rfqReportService.generateQuoteComparisonHTML(rfqId);
// //             byte[] pdfBytes = renderHtmlToPdf(html);
// //             String filename = "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".pdf";
// //             return pdfResponse(pdfBytes, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ Quote Comparison PDF failed for RFQ {}: {}", rfqId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  7. INVOICE — EXCEL
// //     // =========================================================================
// //     @GetMapping("/invoice/{invoiceId}/excel")
// //     public ResponseEntity<byte[]> downloadInvoiceExcel(@PathVariable Long invoiceId) {
// //         logger.info("📊 [REPORT] Invoice Excel — Invoice ID: {}", invoiceId);
// //         try {
// //             byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
// //             String filename = "Invoice_" + invoiceId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ Invoice Excel failed for Invoice {}: {}", invoiceId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  8. INVOICE — PDF
// //     // =========================================================================
// //     @GetMapping("/invoice/{invoiceId}/pdf")
// //     public ResponseEntity<byte[]> downloadInvoicePDF(@PathVariable Long invoiceId) {
// //         logger.info("📄 [REPORT] Invoice PDF — Invoice ID: {}", invoiceId);
// //         try {
// //             byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
// //             String filename = "Invoice_" + invoiceId + "_" + ts() + ".pdf";
// //             return pdfResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ Invoice PDF failed for Invoice {}: {}", invoiceId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  9. PO SUMMARY — EXCEL
// //     // =========================================================================
// //     @GetMapping("/po/{poId}/excel")
// //     public ResponseEntity<byte[]> downloadPOSummaryExcel(@PathVariable Long poId) {
// //         logger.info("📊 [REPORT] PO Summary Excel — PO ID: {}", poId);
// //         try {
// //             byte[] data = rfqReportService.generatePOSummaryExcel(poId);
// //             String filename = "PO_Summary_" + poId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ PO Summary Excel failed for PO {}: {}", poId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  10. PO SUMMARY — PDF
// //     // =========================================================================
// //     @GetMapping("/po/{poId}/pdf")
// //     public ResponseEntity<byte[]> downloadPOSummaryPDF(@PathVariable Long poId) {
// //         logger.info("📄 [REPORT] PO Summary PDF — PO ID: {}", poId);
// //         try {
// //             byte[] data = rfqReportService.generatePOSummaryExcel(poId);
// //             String filename = "PO_Summary_" + poId + "_" + ts() + ".pdf";
// //             return pdfResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ PO Summary PDF failed for PO {}: {}", poId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  11. GRN — EXCEL
// //     // =========================================================================
// //     @GetMapping("/grn/{grnId}/excel")
// //     public ResponseEntity<byte[]> downloadGRNExcel(@PathVariable Long grnId) {
// //         logger.info("📊 [REPORT] GRN Excel — GRN ID: {}", grnId);
// //         try {
// //             byte[] data = rfqReportService.generateGRNExcel(grnId);
// //             String filename = "GRN_" + grnId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ GRN Excel failed for GRN {}: {}", grnId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  12. 3-WAY MATCH — EXCEL
// //     // =========================================================================
// //     @GetMapping("/three-way-match/{matchId}/excel")
// //     public ResponseEntity<byte[]> downloadThreeWayMatchExcel(@PathVariable Long matchId) {
// //         logger.info("📊 [REPORT] 3-Way Match Excel — Match ID: {}", matchId);
// //         try {
// //             byte[] data = rfqReportService.generateThreeWayMatchExcel(matchId);
// //             String filename = "ThreeWayMatch_" + matchId + "_" + ts() + ".xlsx";
// //             return excelResponse(data, filename);
// //         } catch (Exception e) {
// //             logger.error("❌ 3-Way Match Excel failed for Match {}: {}", matchId, e.getMessage(), e);
// //             return ResponseEntity.internalServerError().build();
// //         }
// //     }

// //     // =========================================================================
// //     //  PRIVATE: shared response builders
// //     // =========================================================================

// //     private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
// //         return ResponseEntity.ok()
// //                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
// //                 .header(HttpHeaders.CONTENT_TYPE,
// //                         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
// //                 .body(data);
// //     }

// //     private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
// //         return ResponseEntity.ok()
// //                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
// //                 .contentType(MediaType.APPLICATION_PDF)
// //                 .body(data);
// //     }

// //     private byte[] renderHtmlToPdf(String html) throws Exception {
// //         try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
// //             PdfRendererBuilder builder = new PdfRendererBuilder();
// //             builder.useFastMode();
// //             builder.withHtmlContent(html, null);
// //             builder.toStream(out);
// //             builder.run();
// //             return out.toByteArray();
// //         }
// //     }

// //     private String ts() {
// //         return LocalDateTime.now().format(TS_FMT);
// //     }
// // }


// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.service.RFQReportService;
// import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.io.ByteArrayOutputStream;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// @RestController
// @RequestMapping("/api/reports")
// @CrossOrigin(origins = "*")
// public class RFQReportController {

//     private static final Logger logger = LoggerFactory.getLogger(RFQReportController.class);
//     private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

//     @Autowired
//     private RFQReportService rfqReportService;

//     // =========================================================================
//     //  1. RFQ SUMMARY — EXCEL
//     //     Optional params for hierarchy users → adds "Hierarchy Approval View" sheet
//     //     userId      = hierarchy user's ID
//     //     levelOrder  = their hierarchy level number (e.g. 20)
//     //     levelName   = their level name (e.g. "COO")
//     //     viewerName  = their full name
//     //     viewerEmail = their email
//     // =========================================================================
//     @GetMapping("/rfq/{rfqId}/excel")
//     public ResponseEntity<byte[]> downloadRFQSummaryExcel(
//             @PathVariable Long rfqId,
//             @RequestParam(required = false) Long userId,
//             @RequestParam(required = false) Integer levelOrder,
//             @RequestParam(required = false) String levelName,
//             @RequestParam(required = false) String viewerName,
//             @RequestParam(required = false) String viewerEmail) {
//         logger.info("📊 [REPORT] RFQ Summary Excel — RFQ ID: {}, User ID: {}, Level: {}", rfqId, userId, levelOrder);
//         try {
//             byte[] data = rfqReportService.generateRFQSummaryExcel(
//                     rfqId, false, userId, levelOrder, levelName, viewerName, viewerEmail);
//             String filename = "RFQ_Summary_" + rfqId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ RFQ Summary Excel failed for RFQ {}: {}", rfqId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  2. RFQ SUMMARY — EXCEL SUPPLIER version (2 sheets only)
//     // =========================================================================
//     @GetMapping("/rfq/{rfqId}/excel/supplier")
//     public ResponseEntity<byte[]> downloadRFQSummaryExcelForSupplier(@PathVariable Long rfqId) {
//         logger.info("📊 [REPORT] RFQ Summary Excel (Supplier) — RFQ ID: {}", rfqId);
//         try {
//             byte[] data = rfqReportService.generateRFQSummaryExcel(
//                     rfqId, true, null, null, null, null, null);
//             String filename = "RFQ_Summary_" + rfqId + "_Supplier_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ RFQ Summary Excel (Supplier) failed for RFQ {}: {}", rfqId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  3. RFQ SUMMARY — PDF
//     // =========================================================================
//     @GetMapping("/rfq/{rfqId}/pdf")
//     public ResponseEntity<byte[]> downloadRFQSummaryPDF(@PathVariable Long rfqId) {
//         logger.info("📄 [REPORT] RFQ Summary PDF — RFQ ID: {}", rfqId);
//         try {
//             String html = rfqReportService.generateRFQSummaryHTML(rfqId);
//             byte[] pdfBytes = renderHtmlToPdf(html);
//             String filename = "RFQ_Summary_" + rfqId + "_" + ts() + ".pdf";
//             return pdfResponse(pdfBytes, filename);
//         } catch (Exception e) {
//             logger.error("❌ RFQ Summary PDF failed for RFQ {}: {}", rfqId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  4. RFQ LIST — EXCEL
//     // =========================================================================
//     @GetMapping("/rfq/list/excel")
//     public ResponseEntity<byte[]> downloadRFQListExcel(
//             @RequestParam Long buyerId,
//             @RequestParam(defaultValue = "ALL") String status) {
//         logger.info("📊 [REPORT] RFQ List Excel — Buyer ID: {}, Status: {}", buyerId, status);
//         try {
//             byte[] data = rfqReportService.generateRFQListExcel(buyerId, status);
//             String filename = "RFQ_List_Buyer" + buyerId + "_" + status + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ RFQ List Excel failed for Buyer {}: {}", buyerId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  5. QUOTE COMPARISON — EXCEL
//     // =========================================================================
//     @GetMapping("/rfq/{rfqId}/quote-comparison/excel")
//     public ResponseEntity<byte[]> downloadQuoteComparisonExcel(@PathVariable Long rfqId) {
//         logger.info("📊 [REPORT] Quote Comparison Excel — RFQ ID: {}", rfqId);
//         try {
//             byte[] data = rfqReportService.generateQuoteComparisonExcel(rfqId);
//             String filename = "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ Quote Comparison Excel failed for RFQ {}: {}", rfqId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  6. QUOTE COMPARISON — PDF
//     // =========================================================================
//     @GetMapping("/rfq/{rfqId}/quote-comparison/pdf")
//     public ResponseEntity<byte[]> downloadQuoteComparisonPDF(@PathVariable Long rfqId) {
//         logger.info("📄 [REPORT] Quote Comparison PDF — RFQ ID: {}", rfqId);
//         try {
//             String html = rfqReportService.generateQuoteComparisonHTML(rfqId);
//             byte[] pdfBytes = renderHtmlToPdf(html);
//             String filename = "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".pdf";
//             return pdfResponse(pdfBytes, filename);
//         } catch (Exception e) {
//             logger.error("❌ Quote Comparison PDF failed for RFQ {}: {}", rfqId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  7. INVOICE — EXCEL
//     // =========================================================================
//     @GetMapping("/invoice/{invoiceId}/excel")
//     public ResponseEntity<byte[]> downloadInvoiceExcel(@PathVariable Long invoiceId) {
//         logger.info("📊 [REPORT] Invoice Excel — Invoice ID: {}", invoiceId);
//         try {
//             byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
//             String filename = "Invoice_" + invoiceId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ Invoice Excel failed for Invoice {}: {}", invoiceId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  8. INVOICE — PDF
//     // =========================================================================
//     @GetMapping("/invoice/{invoiceId}/pdf")
//     public ResponseEntity<byte[]> downloadInvoicePDF(@PathVariable Long invoiceId) {
//         logger.info("📄 [REPORT] Invoice PDF — Invoice ID: {}", invoiceId);
//         try {
//             byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
//             String filename = "Invoice_" + invoiceId + "_" + ts() + ".pdf";
//             return pdfResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ Invoice PDF failed for Invoice {}: {}", invoiceId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  9. PO SUMMARY — EXCEL
//     // =========================================================================
//     @GetMapping("/po/{poId}/excel")
//     public ResponseEntity<byte[]> downloadPOSummaryExcel(@PathVariable Long poId) {
//         logger.info("📊 [REPORT] PO Summary Excel — PO ID: {}", poId);
//         try {
//             byte[] data = rfqReportService.generatePOSummaryExcel(poId);
//             String filename = "PO_Summary_" + poId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ PO Summary Excel failed for PO {}: {}", poId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  10. PO SUMMARY — PDF
//     // =========================================================================
//     @GetMapping("/po/{poId}/pdf")
//     public ResponseEntity<byte[]> downloadPOSummaryPDF(@PathVariable Long poId) {
//         logger.info("📄 [REPORT] PO Summary PDF — PO ID: {}", poId);
//         try {
//             byte[] data = rfqReportService.generatePOSummaryExcel(poId);
//             String filename = "PO_Summary_" + poId + "_" + ts() + ".pdf";
//             return pdfResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ PO Summary PDF failed for PO {}: {}", poId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  11. GRN — EXCEL
//     // =========================================================================
//     @GetMapping("/grn/{grnId}/excel")
//     public ResponseEntity<byte[]> downloadGRNExcel(@PathVariable Long grnId) {
//         logger.info("📊 [REPORT] GRN Excel — GRN ID: {}", grnId);
//         try {
//             byte[] data = rfqReportService.generateGRNExcel(grnId);
//             String filename = "GRN_" + grnId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ GRN Excel failed for GRN {}: {}", grnId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  12. 3-WAY MATCH — EXCEL
//     // =========================================================================
//     @GetMapping("/three-way-match/{matchId}/excel")
//     public ResponseEntity<byte[]> downloadThreeWayMatchExcel(@PathVariable Long matchId) {
//         logger.info("📊 [REPORT] 3-Way Match Excel — Match ID: {}", matchId);
//         try {
//             byte[] data = rfqReportService.generateThreeWayMatchExcel(matchId);
//             String filename = "ThreeWayMatch_" + matchId + "_" + ts() + ".xlsx";
//             return excelResponse(data, filename);
//         } catch (Exception e) {
//             logger.error("❌ 3-Way Match Excel failed for Match {}: {}", matchId, e.getMessage(), e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     // =========================================================================
//     //  PRIVATE helpers
//     // =========================================================================
//     private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .header(HttpHeaders.CONTENT_TYPE,
//                         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//                 .body(data);
//     }

//     private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .body(data);
//     }

//     private byte[] renderHtmlToPdf(String html) throws Exception {
//         try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             PdfRendererBuilder builder = new PdfRendererBuilder();
//             builder.useFastMode();
//             builder.withHtmlContent(html, null);
//             builder.toStream(out);
//             builder.run();
//             return out.toByteArray();
//         }
//     }

//     private String ts() {
//         return LocalDateTime.now().format(TS_FMT);
//     }
// }

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.RFQReportService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class RFQReportController {

    private static final Logger logger = LoggerFactory.getLogger(RFQReportController.class);
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Autowired
    private RFQReportService rfqReportService;

    /**
     * GET /api/reports/rfq/{rfqId}/excel
     *
     * BUYER (no extra params)       → 12 sheets full report
     * HIERARCHY (with userId etc.)  → 4 sheets only:
     *     1. RFQ Overview
     *     2. Line Items
     *     3. Invited Suppliers
     *     4. Approval History  (rich hierarchy approval view)
     */
    @GetMapping("/rfq/{rfqId}/excel")
    public ResponseEntity<byte[]> downloadRFQSummaryExcel(
            @PathVariable Long rfqId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer levelOrder,
            @RequestParam(required = false) String levelName,
            @RequestParam(required = false) String viewerName,
            @RequestParam(required = false) String viewerEmail) {

        logger.info("RFQ Excel — RFQ:{} userId:{} level:{}", rfqId, userId, levelOrder);
        try {
            byte[] data = rfqReportService.generateRFQSummaryExcel(
                    rfqId, false, userId, levelOrder, levelName, viewerName, viewerEmail);
            return excelResponse(data, "RFQ_Summary_" + rfqId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("RFQ Excel failed — RFQ:{}", rfqId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Supplier-only copy — 2 sheets */
    @GetMapping("/rfq/{rfqId}/excel/supplier")
    public ResponseEntity<byte[]> downloadRFQSummaryExcelForSupplier(@PathVariable Long rfqId) {
        logger.info("RFQ Excel (Supplier) — RFQ:{}", rfqId);
        try {
            byte[] data = rfqReportService.generateRFQSummaryExcel(
                    rfqId, true, null, null, null, null, null);
            return excelResponse(data, "RFQ_Summary_" + rfqId + "_Supplier_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("RFQ Excel (Supplier) failed — RFQ:{}", rfqId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rfq/{rfqId}/pdf")
    public ResponseEntity<byte[]> downloadRFQSummaryPDF(@PathVariable Long rfqId) {
        logger.info("RFQ PDF — RFQ:{}", rfqId);
        try {
            String html = rfqReportService.generateRFQSummaryHTML(rfqId);
            return pdfResponse(renderHtmlToPdf(html), "RFQ_Summary_" + rfqId + "_" + ts() + ".pdf");
        } catch (Exception e) {
            logger.error("RFQ PDF failed — RFQ:{}", rfqId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rfq/list/excel")
    public ResponseEntity<byte[]> downloadRFQListExcel(
            @RequestParam Long buyerId,
            @RequestParam(defaultValue = "ALL") String status) {
        logger.info("RFQ List Excel — buyer:{} status:{}", buyerId, status);
        try {
            byte[] data = rfqReportService.generateRFQListExcel(buyerId, status);
            return excelResponse(data, "RFQ_List_Buyer" + buyerId + "_" + status + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("RFQ List Excel failed — buyer:{}", buyerId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rfq/{rfqId}/quote-comparison/excel")
    public ResponseEntity<byte[]> downloadQuoteComparisonExcel(@PathVariable Long rfqId) {
        logger.info("Quote Comparison Excel — RFQ:{}", rfqId);
        try {
            byte[] data = rfqReportService.generateQuoteComparisonExcel(rfqId);
            return excelResponse(data, "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("Quote Comparison Excel failed — RFQ:{}", rfqId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rfq/{rfqId}/quote-comparison/pdf")
    public ResponseEntity<byte[]> downloadQuoteComparisonPDF(@PathVariable Long rfqId) {
        logger.info("Quote Comparison PDF — RFQ:{}", rfqId);
        try {
            String html = rfqReportService.generateQuoteComparisonHTML(rfqId);
            return pdfResponse(renderHtmlToPdf(html), "Quote_Comparison_RFQ" + rfqId + "_" + ts() + ".pdf");
        } catch (Exception e) {
            logger.error("Quote Comparison PDF failed — RFQ:{}", rfqId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invoice/{invoiceId}/excel")
    public ResponseEntity<byte[]> downloadInvoiceExcel(@PathVariable Long invoiceId) {
        logger.info("Invoice Excel — Invoice:{}", invoiceId);
        try {
            byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
            return excelResponse(data, "Invoice_" + invoiceId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("Invoice Excel failed — Invoice:{}", invoiceId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invoice/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePDF(@PathVariable Long invoiceId) {
        logger.info("Invoice PDF — Invoice:{}", invoiceId);
        try {
            byte[] data = rfqReportService.generateInvoiceExcel(invoiceId);
            return pdfResponse(data, "Invoice_" + invoiceId + "_" + ts() + ".pdf");
        } catch (Exception e) {
            logger.error("Invoice PDF failed — Invoice:{}", invoiceId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/po/{poId}/excel")
    public ResponseEntity<byte[]> downloadPOSummaryExcel(@PathVariable Long poId) {
        logger.info("PO Excel — PO:{}", poId);
        try {
            byte[] data = rfqReportService.generatePOSummaryExcel(poId);
            return excelResponse(data, "PO_Summary_" + poId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("PO Excel failed — PO:{}", poId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/po/{poId}/pdf")
    public ResponseEntity<byte[]> downloadPOSummaryPDF(@PathVariable Long poId) {
        logger.info("PO PDF — PO:{}", poId);
        try {
            byte[] data = rfqReportService.generatePOSummaryExcel(poId);
            return pdfResponse(data, "PO_Summary_" + poId + "_" + ts() + ".pdf");
        } catch (Exception e) {
            logger.error("PO PDF failed — PO:{}", poId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/grn/{grnId}/excel")
    public ResponseEntity<byte[]> downloadGRNExcel(@PathVariable Long grnId) {
        logger.info("GRN Excel — GRN:{}", grnId);
        try {
            byte[] data = rfqReportService.generateGRNExcel(grnId);
            return excelResponse(data, "GRN_" + grnId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("GRN Excel failed — GRN:{}", grnId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/three-way-match/{matchId}/excel")
    public ResponseEntity<byte[]> downloadThreeWayMatchExcel(@PathVariable Long matchId) {
        logger.info("3-Way Match Excel — Match:{}", matchId);
        try {
            byte[] data = rfqReportService.generateThreeWayMatchExcel(matchId);
            return excelResponse(data, "ThreeWayMatch_" + matchId + "_" + ts() + ".xlsx");
        } catch (Exception e) {
            logger.error("3-Way Match Excel failed — Match:{}", matchId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    private byte[] renderHtmlToPdf(String html) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        }
    }

    private String ts() {
        return LocalDateTime.now().format(TS_FMT);
    }
}