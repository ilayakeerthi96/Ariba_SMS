
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // =========================================================================
    // SUPPLIER — CREATE / EDIT / SUBMIT
    // =========================================================================

    @PostMapping("/create/{supplierId}/{poId}")
    public ResponseEntity<Map<String, Object>> createInvoice(
            @PathVariable Long supplierId,
            @PathVariable Long poId,
            @RequestBody Map<String, Object> invoiceData) {
        Map<String, Object> invoice = invoiceService.createInvoice(supplierId, poId, invoiceData);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    @PutMapping("/{invoiceId}/update/{supplierId}")
    public ResponseEntity<Map<String, Object>> updateInvoice(
            @PathVariable Long invoiceId,
            @PathVariable Long supplierId,
            @RequestBody Map<String, Object> invoiceData) {
        Map<String, Object> invoice = invoiceService.updateInvoice(supplierId, invoiceId, invoiceData);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    @PutMapping("/{invoiceId}/submit/{supplierId}")
    public ResponseEntity<Map<String, Object>> submitInvoice(
            @PathVariable Long invoiceId,
            @PathVariable Long supplierId) {
        Map<String, Object> invoice = invoiceService.submitInvoice(supplierId, invoiceId);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    @PutMapping("/{invoiceId}/resubmit/{supplierId}")
    public ResponseEntity<Map<String, Object>> resubmitInvoice(
            @PathVariable Long invoiceId,
            @PathVariable Long supplierId,
            @RequestBody(required = false) Map<String, Object> body) {
        String remarks = body != null ? (String) body.getOrDefault("resubmitRemarks", "") : "";
        Map<String, Object> invoice = invoiceService.resubmitInvoice(supplierId, invoiceId, remarks);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    // =========================================================================
    // SUPPLIER — READ
    // =========================================================================

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getInvoicesBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", invoiceService.getInvoicesBySupplier(supplierId)));
    }

    @GetMapping("/supplier/{supplierId}/approved-pos")
    public ResponseEntity<Map<String, Object>> getApprovedPOsForSupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(invoiceService.getApprovedPOsForSupplier(supplierId));
    }

    @GetMapping("/po-details/{supplierId}/{poId}")
    public ResponseEntity<Map<String, Object>> getPODetailsForInvoice(
            @PathVariable Long supplierId,
            @PathVariable Long poId) {
        return ResponseEntity.ok(invoiceService.getPODetailsForInvoice(supplierId, poId));
    }

    // =========================================================================
    // BUYER — READ
    // =========================================================================

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Map<String, Object>> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", invoiceService.getInvoiceById(invoiceId)));
    }

    @GetMapping("/buyer/user/{userId}")
    public ResponseEntity<Map<String, Object>> getInvoicesByRfqCreator(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", invoiceService.getInvoicesByRfqCreator(userId)));
    }

    @GetMapping("/buyer/company/{companyName}")
    public ResponseEntity<Map<String, Object>> getInvoicesByBuyerCompany(
            @PathVariable String companyName) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", invoiceService.getInvoicesByBuyerCompany(companyName)));
    }

    // =========================================================================
    // BUYER — ACTIONS
    // =========================================================================

    /**
     * PUT /api/invoice/{invoiceId}/approve
     *
     * Approve the invoice. Will fail with a clear error if 3-Way Match
     * has not been performed and passed first.
     *
     * Body:
     * {
     *   "approvedBy": "Priya Buyer",
     *   "remarks": "All matched. Approved for payment."
     * }
     */
    @PutMapping("/{invoiceId}/approve")
    public ResponseEntity<Map<String, Object>> approveInvoice(
            @PathVariable Long invoiceId,
            @RequestBody(required = false) Map<String, Object> body) {
        String approvedBy = body != null
                ? body.getOrDefault("approvedBy", body.getOrDefault("buyerName", "Buyer")).toString()
                : "Buyer";
        String remarks = body != null ? body.getOrDefault("remarks", "").toString() : "";
        try {
            Map<String, Object> invoice = invoiceService.approveInvoice(invoiceId, approvedBy, remarks);
            return ResponseEntity.ok(Map.of("success", true, "data", invoice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * PUT /api/invoice/{invoiceId}/request-resubmission
     *
     * Ask supplier to correct and resubmit the invoice.
     * Auto-escalates to REJECTED_CLOSED if supplier has already resubmitted once.
     *
     * Body:
     * {
     *   "buyerName": "Priya Buyer",
     *   "remarks": "Unit price does not match PO rate for item 2."
     * }
     */
    @PutMapping("/{invoiceId}/request-resubmission")
    public ResponseEntity<Map<String, Object>> requestResubmission(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, Object> body) {
        String buyerName = body.getOrDefault("buyerName", "Buyer").toString();
        String remarks   = body.getOrDefault("remarks", "").toString();
        Map<String, Object> invoice = invoiceService.requestResubmission(invoiceId, buyerName, remarks);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    /**
     * PUT /api/invoice/{invoiceId}/reject
     * Alias for request-resubmission — kept for backward compatibility.
     */
    @PutMapping("/{invoiceId}/reject")
    public ResponseEntity<Map<String, Object>> rejectInvoice(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, Object> body) {
        String buyerName = body.getOrDefault("buyerName", "Buyer").toString();
        String remarks   = body.getOrDefault("remarks", "").toString();
        Map<String, Object> invoice = invoiceService.requestResubmission(invoiceId, buyerName, remarks);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }

    // =========================================================================
    // FINANCE — MARK PAID
    // =========================================================================

    /**
     * PUT /api/invoice/{invoiceId}/mark-paid
     *
     * Body:
     * {
     *   "paidBy": "Finance Team",
     *   "paymentReference": "NEFT/UTR123456"
     * }
     */
    @PutMapping("/{invoiceId}/mark-paid")
    public ResponseEntity<Map<String, Object>> markInvoicePaid(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, Object> body) {
        String paidBy    = body.getOrDefault("paidBy", body.getOrDefault("buyerName", "Finance")).toString();
        String reference = body.getOrDefault("paymentReference", "").toString();
        Map<String, Object> invoice = invoiceService.markInvoicePaid(invoiceId, paidBy, reference);
        return ResponseEntity.ok(Map.of("success", true, "data", invoice));
    }
}