package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.POPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ✅ Controller: PO PDF Generation
 *
 * Generates a professional PDF matching the ITTI Purchase Order format.
 *
 * Endpoints:
 *  GET /api/po-pdf/{poId}          → Download PO as PDF
 *  GET /api/po-pdf/{poId}/preview  → Preview (inline)
 */
@RestController
@RequestMapping("/api/po-pdf")
@CrossOrigin(origins = "*")
public class POPdfController {

    private static final Logger logger = LoggerFactory.getLogger(POPdfController.class);

    @Autowired
    private POPdfService poPdfService;

    /**
     * GET /api/po-pdf/{poId}
     * Downloads the PO as a PDF file (Content-Disposition: attachment).
     */
    @GetMapping("/{poId}")
    public ResponseEntity<?> downloadPO(@PathVariable Long poId) {
        logger.info("📄 [API] DOWNLOAD PO PDF - PO ID: {}", poId);
        try {
            byte[] pdfBytes = poPdfService.generatePOPdf(poId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "PO-" + poId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            logger.info("  ✅ PDF generated: {} bytes", pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("❌ Error generating PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error generating PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate PO PDF"));
        }
    }

    /**
     * GET /api/po-pdf/{poId}/preview
     * Returns PDF for inline preview in browser (Content-Disposition: inline).
     */
    @GetMapping("/{poId}/preview")
    public ResponseEntity<?> previewPO(@PathVariable Long poId) {
        logger.info("👁️ [API] PREVIEW PO PDF - PO ID: {}", poId);
        try {
            byte[] pdfBytes = poPdfService.generatePOPdf(poId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"PO-" + poId + ".pdf\"");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate preview"));
        }
    }
}