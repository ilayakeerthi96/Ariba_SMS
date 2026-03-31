

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.POResponseDTO;
import com.itti.leadcapturing.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ Purchase Order Controller
 *
 * NOTE: The NEW recommended flow for PO creation is:
 *   1. POST /api/supplier-selection/select    → select supplier (with/without justification)
 *   2. POST /api/po-negotiation/init          → open price review screen
 *   3. PUT  /api/po-negotiation/{id}          → edit prices
 *   4. POST /api/po-negotiation/{id}/create-po → create PO from finalized prices
 *   5. POST /api/po-approval/initiate          → start approval flow
 *   6. GET  /api/po-pdf/{poId}                → download ITTI-format PO PDF
 *
 * The /create endpoint below is kept for backward-compatibility (direct quote-based PO).
 */
@RestController
@RequestMapping("/api/purchase-order")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderService poService;

    // ==================== CREATE PO (direct, legacy) ====================

    /**
     * POST /api/purchase-order/create
     * Direct PO creation from quote items (no negotiation step).
     * Prefer using /api/po-negotiation flow for new implementations.
     */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> createPurchaseOrder(@RequestBody Map<String, Object> request) {
        logger.info("📝 [API] CREATE PURCHASE ORDER (Direct)");
        try {
            Long rfqId       = Long.valueOf(request.get("rfqId").toString());
            Long supplierId  = Long.valueOf(request.get("supplierId").toString());
            String buyerRemarks = request.getOrDefault("buyerRemarks", "").toString();
            Long userId      = Long.valueOf(request.get("userId").toString());

            @SuppressWarnings("unchecked")
            List<Integer> quoteItemIdsRaw = (List<Integer>) request.get("selectedQuoteItemIds");
            List<Long> selectedQuoteItemIds = quoteItemIdsRaw.stream()
                    .map(i -> Long.valueOf(i.toString()))
                    .toList();

            POResponseDTO poDto = poService.createPurchaseOrder(
                    rfqId, supplierId, buyerRemarks, selectedQuoteItemIds, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Purchase Order created successfully");
            response.put("data", poDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("  ❌ Unexpected Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to create Purchase Order: " + e.getMessage()));
        }
    }

    // ==================== UPDATE PO ====================

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updatePurchaseOrder(
            @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("✏️ [API] UPDATE PURCHASE ORDER: {}", id);
        try {
            POResponseDTO updated = poService.updatePurchaseOrder(id, updates);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "Purchase Order updated successfully", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update Purchase Order"));
        }
    }

    // ==================== DELETE PO ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable Long id) {
        logger.info("🗑️ [API] DELETE PURCHASE ORDER: {}", id);
        try {
            poService.deletePurchaseOrder(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Purchase Order deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to delete Purchase Order"));
        }
    }

    // ==================== GET PO BY ID ====================

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPOById(@PathVariable Long id) {
        try {
            POResponseDTO po = poService.getPOById(id);
            return ResponseEntity.ok(Map.of("success", true, "data", po));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Purchase Order not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch Purchase Order"));
        }
    }

    // ==================== GET ALL POs ====================

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllPOs() {
        try {
            List<POResponseDTO> pos = poService.getAllPOs();
            return ResponseEntity.ok(Map.of("success", true, "count", pos.size(), "data", pos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch Purchase Orders"));
        }
    }

    // ==================== GET POs BY BUYER ====================

    @GetMapping("/buyer/{buyerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPOsByBuyer(@PathVariable Long buyerId) {
        try {
            List<POResponseDTO> pos = poService.getPOsByBuyer(buyerId);
            return ResponseEntity.ok(Map.of("success", true, "count", pos.size(), "data", pos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch Purchase Orders"));
        }
    }

    // ==================== GET POs BY RFQ ====================

    @GetMapping("/rfq/{rfqId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPOsByRFQ(@PathVariable Long rfqId) {
        try {
            List<POResponseDTO> pos = poService.getPOsByRFQ(rfqId);
            return ResponseEntity.ok(Map.of("success", true, "count", pos.size(), "data", pos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch Purchase Orders"));
        }
    }

    // ==================== GET POs BY COMPANY ====================

    @GetMapping("/company/{companyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPOsByCompany(@PathVariable String companyName) {
        try {
            List<POResponseDTO> pos = poService.getPOsByCompanyName(companyName);
            return ResponseEntity.ok(Map.of("success", true, "count", pos.size(), "data", pos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch Purchase Orders"));
        }
    }

    // ==================== GET APPROVED POs BY SUPPLIER ====================

    @GetMapping("/supplier/{supplierId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPOsBySupplier(@PathVariable Long supplierId) {
        try {
            List<POResponseDTO> pos = poService.getPOsBySupplier(supplierId);
            return ResponseEntity.ok(Map.of("success", true, "data", pos, "total", pos.size()));
        } catch (Exception e) {
            logger.error("❌ Error fetching POs for supplier {}: {}", supplierId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}