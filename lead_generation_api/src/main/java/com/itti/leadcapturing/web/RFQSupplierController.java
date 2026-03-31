package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.RFQSupplier;
import com.itti.leadcapturing.model.RFQ;
import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.repo.RFQSupplierRepository;
import com.itti.leadcapturing.repo.RFQRepository;
import com.itti.leadcapturing.repo.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ✅ NEW: RFQ-Supplier Management APIs
 * Handles RFQSupplier entity and supplier statistics
 */
@RestController
@RequestMapping("/api/rfq-supplier")
@CrossOrigin(origins = "*")
public class RFQSupplierController {

    @Autowired
    private RFQSupplierRepository rfqSupplierRepository;

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ==================== GET SUPPLIERS FOR RFQ ====================

    /**
     * GET /api/rfq-supplier/rfq/{rfqId}
     * Get all suppliers associated with an RFQ
     * 
     * Response includes:
     * - Supplier details
     * - Status (PENDING, SENT, RESPONDED, SELECTED, REJECTED)
     * - Quote amount
     * - Response dates
     */
    @GetMapping("/rfq/{rfqId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getSuppliersByRFQ(@PathVariable Long rfqId) {
        try {
            System.out.println("=== GET SUPPLIERS FOR RFQ ===");
            System.out.println("RFQ ID: " + rfqId);

            List<RFQSupplier> rfqSuppliers = rfqSupplierRepository.findByRFQId(rfqId);
            System.out.println("Found " + rfqSuppliers.size() + " suppliers");

            List<Map<String, Object>> supplierList = new ArrayList<>();
            for (RFQSupplier rfqSupplier : rfqSuppliers) {
                Map<String, Object> supplierData = new HashMap<>();
                
                // Basic info
                supplierData.put("id", rfqSupplier.getId());
                supplierData.put("supplierId", rfqSupplier.getSupplier().getId());
                supplierData.put("companyName", rfqSupplier.getSupplier().getCompanyName());
                supplierData.put("contactEmail", rfqSupplier.getSupplier().getContactPersonEmail());
                supplierData.put("contactPhone", rfqSupplier.getSupplier().getContactPersonPhone());
                
                // Status info
                supplierData.put("status", rfqSupplier.getStatus());
                supplierData.put("sentAt", rfqSupplier.getSentAt());
                supplierData.put("respondedAt", rfqSupplier.getRespondedAt());
                supplierData.put("quoteAmount", rfqSupplier.getQuoteAmount());
                supplierData.put("notes", rfqSupplier.getNotes());
                
                supplierList.add(supplierData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", supplierList.size());
            response.put("data", supplierList);

            System.out.println("=== SUCCESS ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch suppliers");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET SUPPLIER COUNTS ====================

    /**
     * GET /api/rfq-supplier/rfq/{rfqId}/counts
     * Get supplier count statistics
     * 
     * Returns:
     * - Total suppliers selected
     * - Count by status (PENDING, SENT, RESPONDED, etc.)
     */
    @GetMapping("/rfq/{rfqId}/counts")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getSupplierCounts(@PathVariable Long rfqId) {
        try {
            System.out.println("=== GET SUPPLIER COUNTS ===");
            System.out.println("RFQ ID: " + rfqId);

            long totalSuppliers = rfqSupplierRepository.countByRFQId(rfqId);
            long respondedSuppliers = rfqSupplierRepository.countRespondedByRFQId(rfqId);

            List<RFQSupplier> allSuppliers = rfqSupplierRepository.findByRFQId(rfqId);
            
            // Count by status
            Map<String, Long> statusCounts = new HashMap<>();
            for (RFQSupplier rfqSupplier : allSuppliers) {
                String status = rfqSupplier.getStatus();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("totalSuppliers", totalSuppliers);
            data.put("respondedSuppliers", respondedSuppliers);
            data.put("pendingResponses", totalSuppliers - respondedSuppliers);
            data.put("statusCounts", statusCounts);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            System.out.println("Total: " + totalSuppliers + ", Responded: " + respondedSuppliers);
            System.out.println("=== SUCCESS ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch supplier counts");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== SEND RFQ TO SUPPLIERS ====================

    /**
     * POST /api/rfq-supplier/rfq/{rfqId}/send
     * Mark RFQ as sent to all selected suppliers
     * Updates status from PENDING to SENT
     */
    @PostMapping("/rfq/{rfqId}/send")
    @Transactional
    public ResponseEntity<?> sendRFQToSuppliers(@PathVariable Long rfqId) {
        try {
            System.out.println("=== SEND RFQ TO SUPPLIERS ===");
            System.out.println("RFQ ID: " + rfqId);

            List<RFQSupplier> rfqSuppliers = rfqSupplierRepository.findByRFQId(rfqId);
            
            int sentCount = 0;
            for (RFQSupplier rfqSupplier : rfqSuppliers) {
                if ("PENDING".equals(rfqSupplier.getStatus())) {
                    rfqSupplier.setStatus("SENT");
                    rfqSupplier.setSentAt(LocalDateTime.now());
                    rfqSupplierRepository.save(rfqSupplier);
                    sentCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ sent to " + sentCount + " suppliers");
            response.put("sentCount", sentCount);

            System.out.println("Sent to " + sentCount + " suppliers");
            System.out.println("=== SUCCESS ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send RFQ");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== UPDATE SUPPLIER STATUS ====================

    /**
     * PUT /api/rfq-supplier/{id}/status
     * Update supplier response status
     * 
     * Body:
     * {
     *   "status": "RESPONDED",
     *   "quoteAmount": 50000.00,
     *   "notes": "Quote includes installation"
     * }
     */
    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateSupplierStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updateData) {
        try {
            System.out.println("=== UPDATE SUPPLIER STATUS ===");
            System.out.println("RFQSupplier ID: " + id);

            RFQSupplier rfqSupplier = rfqSupplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("RFQSupplier not found"));

            // Update status
            if (updateData.containsKey("status")) {
                String newStatus = updateData.get("status").toString();
                rfqSupplier.setStatus(newStatus);
                
                if ("RESPONDED".equals(newStatus)) {
                    rfqSupplier.setRespondedAt(LocalDateTime.now());
                }
            }

            // Update quote amount
            if (updateData.containsKey("quoteAmount")) {
                Double quoteAmount = Double.parseDouble(updateData.get("quoteAmount").toString());
                rfqSupplier.setQuoteAmount(java.math.BigDecimal.valueOf(quoteAmount));
            }

            // Update notes
            if (updateData.containsKey("notes")) {
                rfqSupplier.setNotes(updateData.get("notes").toString());
            }

            RFQSupplier updated = rfqSupplierRepository.save(rfqSupplier);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier status updated successfully");
            response.put("data", buildRFQSupplierData(updated));

            System.out.println("Status updated to: " + updated.getStatus());
            System.out.println("=== SUCCESS ===");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update supplier status");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RFQs BY SUPPLIER ====================

    /**
     * GET /api/rfq-supplier/supplier/{supplierId}
     * Get all RFQs sent to a specific supplier
     * Useful for supplier portal
     */
    @GetMapping("/supplier/{supplierId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRFQsBySupplier(@PathVariable Long supplierId) {
        try {
            System.out.println("=== GET RFQs BY SUPPLIER ===");
            System.out.println("Supplier ID: " + supplierId);

            List<RFQSupplier> rfqSuppliers = rfqSupplierRepository.findBySupplier(supplierId);
            System.out.println("Found " + rfqSuppliers.size() + " RFQs");

            List<Map<String, Object>> rfqList = new ArrayList<>();
            for (RFQSupplier rfqSupplier : rfqSuppliers) {
                Map<String, Object> rfqData = new HashMap<>();
                
                RFQ rfq = rfqSupplier.getRfq();
                rfqData.put("rfqId", rfq.getId());
                rfqData.put("rfqNumber", rfq.getRfqNumber());
                rfqData.put("rfqTitle", rfq.getRfqTitle());
                rfqData.put("rfqDescription", rfq.getRfqDescription());
                rfqData.put("dueDate", rfq.getDueDate());
                rfqData.put("status", rfqSupplier.getStatus());
                rfqData.put("sentAt", rfqSupplier.getSentAt());
                rfqData.put("respondedAt", rfqSupplier.getRespondedAt());
                rfqData.put("quoteAmount", rfqSupplier.getQuoteAmount());
                
                rfqList.add(rfqData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", rfqList.size());
            response.put("data", rfqList);

            System.out.println("=== SUCCESS ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQs");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== HELPER METHOD ====================

    private Map<String, Object> buildRFQSupplierData(RFQSupplier rfqSupplier) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", rfqSupplier.getId());
        data.put("supplierId", rfqSupplier.getSupplier().getId());
        data.put("companyName", rfqSupplier.getSupplier().getCompanyName());
        data.put("status", rfqSupplier.getStatus());
        data.put("sentAt", rfqSupplier.getSentAt());
        data.put("respondedAt", rfqSupplier.getRespondedAt());
        data.put("quoteAmount", rfqSupplier.getQuoteAmount());
        data.put("notes", rfqSupplier.getNotes());
        return data;
    }
}