package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * ✅ Report Controller
 *
 * Endpoints:
 *  GET /api/reports/po/{poId}/excel             → PO Excel (4 sheets)
 *  GET /api/reports/po/{poId}/pdf               → PO PDF
 *  GET /api/reports/invoice/{invoiceId}/excel   → Invoice Excel (4 sheets)
 *  GET /api/reports/invoice/{invoiceId}/pdf     → Invoice PDF
 */
@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    // =========================================================================
    // PO REPORTS
    // =========================================================================

    /**
     * GET /api/reports/po/{poId}/excel
     * Download Purchase Order as professional Excel (4 sheets).
     */
    @GetMapping("/po/{poId}/excel")
    public ResponseEntity<?> downloadPOExcel(@PathVariable Long poId) {
        logger.info("📊 [REPORT] PO Excel — PO ID: {}", poId);
        try {
            byte[] data = reportService.generatePOExcel(poId);
            String filename = "PO_Report_" + poId + "_" + LocalDate.now() + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(data.length);

            logger.info("  ✅ PO Excel generated: {} bytes", data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("❌ PO Excel error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ PO Excel unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate PO Excel report"));
        }
    }

    /**
     * GET /api/reports/po/{poId}/pdf
     * Download Purchase Order as professional PDF.
     */
    @GetMapping("/po/{poId}/pdf")
    public ResponseEntity<?> downloadPOPdf(@PathVariable Long poId) {
        logger.info("📄 [REPORT] PO PDF — PO ID: {}", poId);
        try {
            byte[] data = reportService.generatePOPdf(poId);
            String filename = "PO_Report_" + poId + "_" + LocalDate.now() + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(data.length);

            logger.info("  ✅ PO PDF generated: {} bytes", data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("❌ PO PDF error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ PO PDF unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate PO PDF report"));
        }
    }

    /**
     * GET /api/reports/po/{poId}/pdf/preview
     * Preview PO PDF inline in browser.
     */
    @GetMapping("/po/{poId}/pdf/preview")
    public ResponseEntity<?> previewPOPdf(@PathVariable Long poId) {
        logger.info("👁️ [REPORT] PO PDF Preview — PO ID: {}", poId);
        try {
            byte[] data = reportService.generatePOPdf(poId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"PO_" + poId + ".pdf\"");
            headers.setContentLength(data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to preview PO PDF"));
        }
    }

    // =========================================================================
    // INVOICE REPORTS
    // =========================================================================

    /**
     * GET /api/reports/invoice/{invoiceId}/excel
     * Download Invoice as professional Excel (4 sheets).
     */
    @GetMapping("/invoice/{invoiceId}/excel")
    public ResponseEntity<?> downloadInvoiceExcel(@PathVariable Long invoiceId) {
        logger.info("📊 [REPORT] Invoice Excel — Invoice ID: {}", invoiceId);
        try {
            byte[] data = reportService.generateInvoiceExcel(invoiceId);
            String filename = "Invoice_Report_" + invoiceId + "_" + LocalDate.now() + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(data.length);

            logger.info("  ✅ Invoice Excel generated: {} bytes", data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("❌ Invoice Excel error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Invoice Excel unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate Invoice Excel report"));
        }
    }

    /**
     * GET /api/reports/invoice/{invoiceId}/pdf
     * Download Invoice as professional PDF.
     */
    // @GetMapping("/invoice/{invoiceId}/pdf")
    // public ResponseEntity<?> downloadInvoicePdf(@PathVariable Long invoiceId) {
    //     logger.info("📄 [REPORT] Invoice PDF — Invoice ID: {}", invoiceId);
    //     try {
    //         byte[] data = reportService.generateInvoicePdf(invoiceId);
    //         String filename = "Invoice_Report_" + invoiceId + "_" + LocalDate.now() + ".pdf";

    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_PDF);
    //         headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
    //         headers.setContentLength(data.length);

    //         logger.info("  ✅ Invoice PDF generated: {} bytes", data.length);
    //         return new ResponseEntity<>(data, headers, HttpStatus.OK);

    //     } catch (RuntimeException e) {
    //         logger.error("❌ Invoice PDF error: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(Map.of("success", false, "message", e.getMessage()));
    //     } catch (Exception e) {
    //         logger.error("❌ Invoice PDF unexpected error", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(Map.of("success", false, "message", "Failed to generate Invoice PDF report"));
    //     }
    // }

    /**
     * GET /api/reports/invoice/{invoiceId}/pdf/preview
     * Preview Invoice PDF inline in browser.
     */
    @GetMapping("/invoice/{invoiceId}/pdf/preview")
    public ResponseEntity<?> previewInvoicePdf(@PathVariable Long invoiceId) {
        logger.info("👁️ [REPORT] Invoice PDF Preview — Invoice ID: {}", invoiceId);
        try {
            byte[] data = reportService.generateInvoicePdf(invoiceId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Invoice_" + invoiceId + ".pdf\"");
            headers.setContentLength(data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to preview Invoice PDF"));
        }
    }
}