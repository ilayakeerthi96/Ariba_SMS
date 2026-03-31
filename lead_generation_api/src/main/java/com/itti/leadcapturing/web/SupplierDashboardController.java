package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.SupplierDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ✅ NEW: Supplier Dashboard Controller
 * Handles all supplier-side RFQ viewing and response operations
 */
@RestController
@RequestMapping("/api/supplier-dashboard")
@CrossOrigin(origins = "*")
public class SupplierDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDashboardController.class);

    @Autowired
    private SupplierDashboardService supplierDashboardService;

    // ==================== DASHBOARD STATISTICS ====================

    /**
     * GET /api/supplier-dashboard/statistics/{supplierId}
     * Get dashboard statistics for supplier
     * 
     * Returns:
     * - Total RFQs
     * - Pending RFQs
     * - Responded RFQs
     * - Selected RFQs
     * - Rejected RFQs
     * - Status breakdown
     */
    @GetMapping("/statistics/{supplierId}")
    public ResponseEntity<?> getDashboardStatistics(@PathVariable Long supplierId) {
        logger.info("📊 [GET SUPPLIER DASHBOARD STATS] Supplier ID: {}", supplierId);

        try {
            Map<String, Object> stats = supplierDashboardService.getSupplierDashboardStats(supplierId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);

            logger.info("  ✅ Statistics fetched successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch statistics");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RFQs ====================

    /**
     * GET /api/supplier-dashboard/rfqs/{supplierId}
     * Get all RFQs sent to supplier with optional filters
     * 
     * Query Parameters:
     * - status: Filter by status (PENDING, SENT, RESPONDED, SELECTED, REJECTED, ALL)
     * - search: Search in RFQ number or title
     * 
     * Example: GET /api/supplier-dashboard/rfqs/5?status=PENDING&search=RFQ-123
     */
    @GetMapping("/rfqs/{supplierId}")
    public ResponseEntity<?> getSupplierRFQs(
            @PathVariable Long supplierId,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "") String search) {
        
        logger.info("📋 [GET SUPPLIER RFQs] Supplier ID: {}, Status: {}, Search: {}", 
                   supplierId, status, search);

        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("status", status);
            filters.put("search", search);

            Map<String, Object> result = supplierDashboardService.getSupplierRFQs(supplierId, filters);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", result.get("total"));
            response.put("data", result.get("rfqs"));

            logger.info("  ✅ Fetched {} RFQs", result.get("total"));
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQs");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RFQ DETAILS ====================

    /**
     * GET /api/supplier-dashboard/rfq/{supplierId}/{rfqId}
     * Get complete RFQ details for supplier
     * 
     * Returns:
     * - RFQ basic info (number, title, description, dates)
     * - Buyer information
     * - Location/delivery details
     * - Payment & delivery terms
     * - All items with specifications
     * - Supplier's current response status
     */
    @GetMapping("/rfq/{supplierId}/{rfqId}")
    public ResponseEntity<?> getRFQDetails(
            @PathVariable Long supplierId,
            @PathVariable Long rfqId) {
        
        logger.info("🔍 [GET RFQ DETAILS] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);

        try {
            Map<String, Object> details = supplierDashboardService.getSupplierRFQDetails(supplierId, rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", details);

            logger.info("  ✅ RFQ details fetched successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQ details");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== SUBMIT QUOTE ====================

    /**
     * POST /api/supplier-dashboard/submit-quote/{supplierId}/{rfqId}
     * Submit quote for RFQ
     * 
     * Request Body:
     * {
     *   "quoteAmount": 50000.00,
     *   "notes": "Includes delivery and installation. Valid for 30 days."
     * }
     */
    @PostMapping("/submit-quote/{supplierId}/{rfqId}")
    public ResponseEntity<?> submitQuote(
            @PathVariable Long supplierId,
            @PathVariable Long rfqId,
            @RequestBody Map<String, Object> quoteData) {
        
        logger.info("📝 [SUBMIT QUOTE] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);
        logger.info("  Quote Data: {}", quoteData);

        try {
            String result = supplierDashboardService.submitQuote(supplierId, rfqId, quoteData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Quote submitted successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to submit quote");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE QUOTE ====================

    /**
     * PUT /api/supplier-dashboard/update-quote/{supplierId}/{rfqId}
     * Update previously submitted quote
     * 
     * Request Body:
     * {
     *   "quoteAmount": 48000.00,
     *   "notes": "Updated quote - reduced price for bulk order"
     * }
     */
    @PutMapping("/update-quote/{supplierId}/{rfqId}")
    public ResponseEntity<?> updateQuote(
            @PathVariable Long supplierId,
            @PathVariable Long rfqId,
            @RequestBody Map<String, Object> quoteData) {
        
        logger.info("✏️ [UPDATE QUOTE] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);
        logger.info("  Updated Data: {}", quoteData);

        try {
            String result = supplierDashboardService.updateQuote(supplierId, rfqId, quoteData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Quote updated successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update quote");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET PENDING RFQs ====================

    /**
     * GET /api/supplier-dashboard/pending/{supplierId}
     * Get only RFQs that need supplier response (PENDING or SENT status)
     */
    @GetMapping("/pending/{supplierId}")
    public ResponseEntity<?> getPendingRFQs(@PathVariable Long supplierId) {
        logger.info("⏳ [GET PENDING RFQs] Supplier ID: {}", supplierId);

        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("status", "PENDING,SENT");
            filters.put("search", "");

            Map<String, Object> result = supplierDashboardService.getSupplierRFQs(supplierId, filters);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", result.get("total"));
            response.put("data", result.get("rfqs"));

            logger.info("  ✅ Fetched {} pending RFQs", result.get("total"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch pending RFQs");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RESPONDED RFQs ====================

    /**
     * GET /api/supplier-dashboard/responded/{supplierId}
     * Get RFQs where supplier has already responded
     */
    @GetMapping("/responded/{supplierId}")
    public ResponseEntity<?> getRespondedRFQs(@PathVariable Long supplierId) {
        logger.info("✅ [GET RESPONDED RFQs] Supplier ID: {}", supplierId);

        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("status", "RESPONDED");
            filters.put("search", "");

            Map<String, Object> result = supplierDashboardService.getSupplierRFQs(supplierId, filters);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", result.get("total"));
            response.put("data", result.get("rfqs"));

            logger.info("  ✅ Fetched {} responded RFQs", result.get("total"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch responded RFQs");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}