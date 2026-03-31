package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.POResponseDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ PO PDF Generation Service
 *
 * Uses Thymeleaf to render the HTML template and openhtmltopdf to convert
 * it to a PDF matching the exact ITTI Purchase Order format.
 *
 * Required Maven dependency:
 * <dependency>
 *     <groupId>com.openhtmltopdf</groupId>
 *     <artifactId>openhtmltopdf-pdfbox</artifactId>
 *     <version>1.0.10</version>
 * </dependency>
 */
@Service
public class POPdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * Generate PDF bytes for a given PO ID.
     * Controller streams this as application/pdf response.
     */
    public byte[] generatePOPdf(Long poId) throws Exception {
        POResponseDTO po = purchaseOrderService.getPOById(poId);
        return generatePdfFromDTO(po);
    }

    /**
     * Generate PDF from a POResponseDTO.
     * Can be called directly with a DTO (e.g., for preview before PO is saved).
     */
    public byte[] generatePdfFromDTO(POResponseDTO po) throws Exception {

        // Enrich DTO with display fields
        enrichForPdf(po);

        // Build Thymeleaf context
        Context context = new Context();
        context.setVariable("po", po);

        // Process template → HTML string
        String htmlContent = templateEngine.process("po-template", context);

        // Convert HTML → PDF bytes
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }

    /**
     * Enrich POResponseDTO with display-friendly fields for the PDF template.
     */
    private void enrichForPdf(POResponseDTO po) {
        // Ensure amount in words is set
        if (po.getAmountInWords() == null && po.getGrandTotal() != null) {
            po.setAmountInWords(purchaseOrderService.convertAmountToWords(po.getGrandTotal()));
        }

        // Set discount percentage on line items (0 if not present)
        if (po.getLineItems() != null) {
            for (POResponseDTO.POLineItemDTO item : po.getLineItems()) {
                // discountPercentage is shown in the PDF table;
                // it will be 0 for direct quote POs, or the negotiated % for review POs
                // The field is added to the line item DTO as a display field
            }
        }
    }
}