package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.SupplierFinalSelectionDTO;
import com.itti.leadcapturing.service.SupplierFinalSelectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ✅ Controller: Supplier Final Selection
 *
 * Endpoints:
 *  GET  /api/supplier-selection/rfq/{rfqId}/status     → recommendation status (for UI highlighting)
 *  POST /api/supplier-selection/select                  → select supplier (with/without justification)
 *  GET  /api/supplier-selection/rfq/{rfqId}             → get current selection for an RFQ
 */
@RestController
@RequestMapping("/api/supplier-selection")
@CrossOrigin(origins = "*")
public class SupplierFinalSelectionController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierFinalSelectionController.class);

    @Autowired
    private SupplierFinalSelectionService selectionService;

    // ===================== GET RECOMMENDATION STATUS =====================

    /**
     * GET /api/supplier-selection/rfq/{rfqId}/status
     *
     * Returns:
     *  - systemRecommendedSupplierId (for highlighting in comparative statement)
     *  - All rankings (for showing rank badges)
     *  - Whether creator has already made a selection
     *
     * Frontend uses this to:
     *  - Highlight the #1 ranked supplier in the comparative table
     *  - Show "System Recommended" badge
     *  - Show existing selection if any
     */
    @GetMapping("/rfq/{rfqId}/status")
    public ResponseEntity<?> getRecommendationStatus(@PathVariable Long rfqId) {
        logger.info("📊 [API] GET RECOMMENDATION STATUS - RFQ: {}", rfqId);
        try {
            Map<String, Object> status = selectionService.getRecommendationStatus(rfqId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ===================== SELECT SUPPLIER =====================

    /**
     * POST /api/supplier-selection/select
     *
     * Request Body:
     * {
     *   "rfqId": 1,
     *   "supplierId": 5,
     *   "isSystemRecommended": false,          ← was this the system-recommended one?
     *   "justification": "Better delivery time", ← MANDATORY if isSystemRecommended=false
     *   "remarks": "Verified with management",   ← always optional
     *   "selectedByUserId": 13,
     *   "selectedByUserName": "John Doe"
     * }
     *
     * Validation:
     *  - If isSystemRecommended = false → justification MUST be provided
     *  - If isSystemRecommended = true  → justification ignored, remarks optional
     */
    @PostMapping("/select")
    public ResponseEntity<?> selectSupplier(@RequestBody SupplierFinalSelectionDTO.Request request) {
        logger.info("🎯 [API] SELECT SUPPLIER - RFQ: {}, Supplier: {}, SystemRec: {}",
                request.getRfqId(), request.getSupplierId(), request.getIsSystemRecommended());
        try {
            SupplierFinalSelectionDTO.Response result = selectionService.selectSupplier(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", Boolean.TRUE.equals(request.getIsSystemRecommended())
                    ? "System-recommended supplier selected successfully."
                    : "Supplier selected with justification recorded.");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to select supplier"));
        }
    }

    // ===================== GET CURRENT SELECTION =====================

    /**
     * GET /api/supplier-selection/rfq/{rfqId}
     * Get the current supplier selection for an RFQ.
     */
    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<?> getSelectionByRfq(@PathVariable Long rfqId) {
        logger.info("📋 [API] GET SELECTION - RFQ: {}", rfqId);
        try {
            SupplierFinalSelectionDTO.Response result = selectionService.getSelectionByRfqId(rfqId);
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get selection"));
        }
    }

    // ===================== CHECK IF SELECTION EXISTS =====================

    /**
     * GET /api/supplier-selection/rfq/{rfqId}/exists
     */
    @GetMapping("/rfq/{rfqId}/exists")
    public ResponseEntity<?> checkSelectionExists(@PathVariable Long rfqId) {
        try {
            boolean exists = selectionService.hasSelectionForRfq(rfqId);
            return ResponseEntity.ok(Map.of("success", true, "exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}