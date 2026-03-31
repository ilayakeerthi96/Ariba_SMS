// package com.itti.leadcapturing.web;
// import com.itti.leadcapturing.dto.GRNDto;
// import com.itti.leadcapturing.model.GRN;
// import com.itti.leadcapturing.service.GRNService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// @RestController
// @RequestMapping("/api/grn")
// @RequiredArgsConstructor
// @Slf4j
// @CrossOrigin(origins = "*")
// public class GRNController {

//     private final GRNService grnService;

//     // =========================================================================
//     // PHASE 1 — RECEIVER ENDPOINTS
//     // =========================================================================

//     /**
//      * POST /api/grn
//      *
//      * Create a new GRN with only orderedQty + receivedQty per line.
//      * Status: DRAFT
//      *
//      * Body:
//      * {
//      *   "purchaseOrderId": 1,
//      *   "receivedByUserId": 10,
//      *   "receivedByName": "Ravi Store",
//      *   "receivedDate": "2026-03-10T10:00:00",
//      *   "deliveryChallanNumber": "DC/001",
//      *   "lrNumber": "LR/456",
//      *   "transporterName": "Blue Dart",
//      *   "vehicleNumber": "TN01AB1234",
//      *   "deliveryLocation": "Warehouse A",
//      *   "remarks": "Delivery received at dock 2",
//      *   "lineItems": [
//      *     {
//      *       "poLineItemId": 11,
//      *       "itemOrder": 1,
//      *       "itemCode": "ITM-001",
//      *       "itemDescription": "Laptop Dell XPS",
//      *       "uom": "NOS",
//      *       "orderedQuantity": 10,
//      *       "poUnitRate": 85000,
//      *       "receivedQuantity": 9
//      *     }
//      *   ]
//      * }
//      */
//     @PostMapping
//     public ResponseEntity<?> createGRN(@RequestBody GRNDto.CreateRequest request) {
//         log.info("📦 [API] CREATE GRN — PO: {}", request.getPurchaseOrderId());
//         try {
//             GRNDto.Response grn = grnService.createGRN(request);
//             return ResponseEntity.status(HttpStatus.CREATED)
//                     .body(Map.of("success", true, "message", "GRN created successfully.", "data", grn));
//         } catch (RuntimeException e) {
//             log.error("❌ {}", e.getMessage());
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             log.error("❌ Unexpected error", e);
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to create GRN: " + e.getMessage()));
//         }
//     }

//     /**
//      * PUT /api/grn/{id}
//      *
//      * Update a DRAFT GRN (receiver corrects quantities before submission).
//      * Same body structure as create.
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateGRN(@PathVariable Long id,
//                                         @RequestBody GRNDto.CreateRequest request) {
//         log.info("✏️ [API] UPDATE GRN — ID: {}", id);
//         try {
//             GRNDto.Response grn = grnService.updateGRN(id, request);
//             return ResponseEntity.ok(Map.of("success", true, "message", "GRN updated.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to update GRN."));
//         }
//     }

//     /**
//      * PUT /api/grn/{id}/submit
//      *
//      * Receiver confirms physical count. Sends GRN to QA team.
//      * Transition: DRAFT → SUBMITTED
//      */
//     @PutMapping("/{id}/submit")
//     public ResponseEntity<?> submitGRN(@PathVariable Long id) {
//         log.info("📋 [API] SUBMIT GRN — ID: {}", id);
//         try {
//             GRNDto.Response grn = grnService.submitGRN(id);
//             return ResponseEntity.ok(Map.of("success", true,
//                     "message", "GRN submitted for QA review.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to submit GRN."));
//         }
//     }

//     // =========================================================================
//     // PHASE 2 — QA TEAM ENDPOINTS
//     // =========================================================================

//     /**
//      * PUT /api/grn/{id}/start-qa-review
//      *
//      * QA team takes ownership of the GRN for inspection.
//      * Transition: SUBMITTED → QA_REVIEW
//      *
//      * Body:
//      * {
//      *   "inspectedByName": "Priya QA"
//      * }
//      */
//     @PutMapping("/{id}/start-qa-review")
//     public ResponseEntity<?> startQAReview(@PathVariable Long id,
//                                             @RequestBody(required = false) Map<String, Object> body) {
//         String inspectedBy = body != null
//                 ? body.getOrDefault("inspectedByName", "QA Team").toString()
//                 : "QA Team";
//         log.info("🔬 [API] START QA REVIEW — GRN: {} by {}", id, inspectedBy);
//         try {
//             GRNDto.Response grn = grnService.startQAReview(id, inspectedBy);
//             return ResponseEntity.ok(Map.of("success", true,
//                     "message", "QA Review started.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to start QA review."));
//         }
//     }

//     /**
//      * PUT /api/grn/{id}/qa-review
//      *
//      * QA team saves inspection results (defective qty, rejected qty, remarks per line).
//      * acceptedQty is auto-computed = receivedQty - rejectedQty.
//      * Status stays QA_REVIEW — supervisor must call /approve to finalise.
//      *
//      * Body:
//      * {
//      *   "grnId": 1,
//      *   "inspectedByName": "Priya QA",
//      *   "qaOverallRemarks": "3 units found damaged in transit",
//      *   "lineItems": [
//      *     {
//      *       "grnLineItemId": 101,
//      *       "defectiveQuantity": 2,
//      *       "rejectedQuantity": 2,
//      *       "qaRemarks": "Screen cracked — rejected"
//      *     },
//      *     {
//      *       "grnLineItemId": 102,
//      *       "defectiveQuantity": 0,
//      *       "rejectedQuantity": 0,
//      *       "qaRemarks": ""
//      *     }
//      *   ]
//      * }
//      */
//     @PutMapping("/{id}/qa-review")
//     public ResponseEntity<?> submitQAReview(@PathVariable Long id,
//                                              @RequestBody GRNDto.QAReviewRequest request) {
//         request.setGrnId(id);
//         log.info("🔬 [API] SUBMIT QA REVIEW — GRN: {}", id);
//         try {
//             GRNDto.Response grn = grnService.submitQAReview(request);
//             return ResponseEntity.ok(Map.of("success", true,
//                     "message", "QA review saved. Awaiting supervisor approval.", "data", grn));
//         } catch (RuntimeException e) {
//             log.error("❌ {}", e.getMessage());
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             log.error("❌ Unexpected error", e);
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to save QA review."));
//         }
//     }

//     /**
//      * PUT /api/grn/{id}/approve
//      *
//      * Supervisor/store-head approves QA results. Locks the GRN.
//      * GRN is now ready for 3-Way Match.
//      * Transition: QA_REVIEW → APPROVED
//      *
//      * Body:
//      * {
//      *   "approvedByName": "Suresh Manager"
//      * }
//      */
//     @PutMapping("/{id}/approve")
//     public ResponseEntity<?> approveGRN(@PathVariable Long id,
//                                          @RequestBody(required = false) Map<String, Object> body) {
//         String approvedBy = body != null
//                 ? body.getOrDefault("approvedByName", "Store Manager").toString()
//                 : "Store Manager";
//         log.info("✅ [API] APPROVE GRN — ID: {} by {}", id, approvedBy);
//         try {
//             GRNDto.Response grn = grnService.approveGRN(id, approvedBy);
//             return ResponseEntity.ok(Map.of("success", true,
//                     "message", "GRN approved. Ready for 3-Way Match.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to approve GRN."));
//         }
//     }

//     // =========================================================================
//     // POST 3-WAY MATCH — CLOSE GRN
//     // =========================================================================

//     /**
//      * PUT /api/grn/{id}/close
//      *
//      * Closes the GRN after 3-Way Match is resolved.
//      * Transition: APPROVED → CLOSED
//      */
//     @PutMapping("/{id}/close")
//     public ResponseEntity<?> closeGRN(@PathVariable Long id) {
//         log.info("🔒 [API] CLOSE GRN — ID: {}", id);
//         try {
//             GRNDto.Response grn = grnService.closeGRN(id);
//             return ResponseEntity.ok(Map.of("success", true,
//                     "message", "GRN closed.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to close GRN."));
//         }
//     }

//     // =========================================================================
//     // MANAGEMENT ENDPOINTS
//     // =========================================================================

//     /**
//      * PUT /api/grn/{id}/link-invoice/{invoiceId}
//      * Link an invoice to this GRN (can be done at any non-CANCELLED status).
//      */
//     @PutMapping("/{id}/link-invoice/{invoiceId}")
//     public ResponseEntity<?> linkInvoice(@PathVariable Long id, @PathVariable Long invoiceId) {
//         log.info("🔗 [API] LINK Invoice {} → GRN {}", invoiceId, id);
//         try {
//             GRNDto.Response grn = grnService.linkInvoice(id, invoiceId);
//             return ResponseEntity.ok(Map.of("success", true, "message", "Invoice linked to GRN.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to link invoice."));
//         }
//     }

//     /**
//      * PUT /api/grn/{id}/cancel
//      *
//      * Body:
//      * {
//      *   "reason": "Wrong PO reference"
//      * }
//      */
//     @PutMapping("/{id}/cancel")
//     public ResponseEntity<?> cancelGRN(@PathVariable Long id,
//                                         @RequestBody(required = false) Map<String, Object> body) {
//         String reason = body != null
//                 ? body.getOrDefault("reason", "No reason provided").toString()
//                 : "";
//         log.info("❌ [API] CANCEL GRN — ID: {}", id);
//         try {
//             GRNDto.Response grn = grnService.cancelGRN(id, reason);
//             return ResponseEntity.ok(Map.of("success", true, "message", "GRN cancelled.", "data", grn));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to cancel GRN."));
//         }
//     }

//     // =========================================================================
//     // READ ENDPOINTS
//     // =========================================================================

//     @GetMapping("/{id}")
//     public ResponseEntity<?> getGRN(@PathVariable Long id) {
//         try {
//             return ResponseEntity.ok(Map.of("success", true, "data", grnService.getGRNById(id)));
//         } catch (RuntimeException e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(Map.of("success", false, "message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to fetch GRN."));
//         }
//     }

//     /** Approved GRNs for a PO — used by 3-Way Match selection UI. */
//     @GetMapping("/po/{poId}")
//     public ResponseEntity<?> getApprovedGRNsForPO(@PathVariable Long poId) {
//         try {
//             List<GRNDto.Response> list = grnService.getApprovedGRNsByPO(poId);
//             return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to fetch GRNs for PO."));
//         }
//     }

//     /** All GRNs (any status) for a PO — for management/history view. */
//     @GetMapping("/po/{poId}/all")
//     public ResponseEntity<?> getAllGRNsForPO(@PathVariable Long poId) {
//         try {
//             List<GRNDto.Response> list = grnService.getAllGRNsByPO(poId);
//             return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to fetch GRNs."));
//         }
//     }

//     @GetMapping("/invoice/{invoiceId}")
//     public ResponseEntity<?> getGRNsByInvoice(@PathVariable Long invoiceId) {
//         try {
//             List<GRNDto.Response> list = grnService.getGRNsByInvoice(invoiceId);
//             return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to fetch GRNs for invoice."));
//         }
//     }

//     @GetMapping("/user/{userId}")
//     public ResponseEntity<?> getGRNsByUser(@PathVariable Long userId) {
//         try {
//             List<GRNDto.Response> list = grnService.getGRNsByUser(userId);
//             return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body(Map.of("success", false, "message", "Failed to fetch GRNs for user."));
//         }
//     }
    
//     @PutMapping("/{id}/revert-to-draft")
// public ResponseEntity<?> revertToDraft(@PathVariable Long id) {
//     try {
//         GRNDto.Response grn = grnService.revertToDraft(id);
//         return ResponseEntity.ok(Map.of(
//             "success", true,
//             "message", "GRN reverted to DRAFT successfully",
//             "data", grn
//         ));
//     } catch (RuntimeException e) {
//         return ResponseEntity.badRequest().body(Map.of(
//             "success", false,
//             "message", e.getMessage()
//         ));
//     } catch (Exception e) {
//         return ResponseEntity.internalServerError()
//             .body(Map.of("success", false, "message", "Failed to revert GRN to draft."));
//     }
// }

// }

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.GRNDto;
import com.itti.leadcapturing.service.GRNService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grn")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GRNController {

    private final GRNService grnService;

    // =========================================================================
    // PHASE 1 — RECEIVER ENDPOINTS
    // =========================================================================

    /**
     * POST /api/grn
     *
     * Create a new GRN for a PO delivery batch.
     *
     * FULL DELIVERY (ordered 10, received 10):
     * {
     *   "purchaseOrderId": 1,
     *   "invoiceId": 5,               ← link invoice at creation
     *   "receivedByUserId": 10,
     *   "receivedByName": "Ravi Store",
     *   "receivedDate": "2026-03-10T10:00:00",
     *   "deliveryChallanNumber": "DC/001",
     *   "lrNumber": "LR/456",
     *   "transporterName": "Blue Dart",
     *   "vehicleNumber": "TN01AB1234",
     *   "deliveryLocation": "Warehouse A",
     *   "remarks": "All 10 units received",
     *   "lineItems": [
     *     {
     *       "poLineItemId": 11,
     *       "itemOrder": 1,
     *       "itemCode": "ITM-001",
     *       "itemDescription": "Laptop Dell XPS",
     *       "uom": "NOS",
     *       "orderedQuantity": 10,
     *       "poUnitRate": 85000,
     *       "receivedQuantity": 10     ← full quantity
     *     }
     *   ]
     * }
     *
     * PARTIAL DELIVERY - BATCH 1 (ordered 10, received 8):
     * Same body but receivedQuantity: 8
     *
     * PARTIAL DELIVERY - BATCH 2 (remaining 2 received, SAME invoiceId):
     * Same body with:
     *   "invoiceId": 5,               ← SAME invoice as batch 1
     *   "remarks": "Remaining 2 units received",
     *   "lineItems": [{ ..., "receivedQuantity": 2 }]
     *
     * Each call creates a NEW GRN with a unique GRN number.
     * The 3-Way Match automatically tracks previously accepted quantities.
     */
    @PostMapping
    public ResponseEntity<?> createGRN(@RequestBody GRNDto.CreateRequest request) {
        log.info("📦 [API] CREATE GRN — PO: {}, Invoice: {}", request.getPurchaseOrderId(), request.getInvoiceId());
        try {
            GRNDto.Response grn = grnService.createGRN(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "GRN created successfully.", "data", grn));
        } catch (RuntimeException e) {
            log.error("❌ {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to create GRN: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/grn/{id}
     * Update a DRAFT GRN. Same body as create.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGRN(@PathVariable Long id,
                                        @RequestBody GRNDto.CreateRequest request) {
        log.info("✏️ [API] UPDATE GRN — ID: {}", id);
        try {
            GRNDto.Response grn = grnService.updateGRN(id, request);
            return ResponseEntity.ok(Map.of("success", true, "message", "GRN updated.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to update GRN."));
        }
    }

    /**
     * PUT /api/grn/{id}/submit
     * Transition: DRAFT → SUBMITTED
     */
    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitGRN(@PathVariable Long id) {
        log.info("📋 [API] SUBMIT GRN — ID: {}", id);
        try {
            GRNDto.Response grn = grnService.submitGRN(id);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "GRN submitted for QA review.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to submit GRN."));
        }
    }

    // =========================================================================
    // PHASE 2 — QA TEAM ENDPOINTS
    // =========================================================================

    /**
     * PUT /api/grn/{id}/start-qa-review
     * Transition: SUBMITTED → QA_REVIEW
     *
     * Body: { "inspectedByName": "Priya QA" }
     */
    @PutMapping("/{id}/start-qa-review")
    public ResponseEntity<?> startQAReview(@PathVariable Long id,
                                            @RequestBody(required = false) Map<String, Object> body) {
        String inspectedBy = body != null
                ? body.getOrDefault("inspectedByName", "QA Team").toString()
                : "QA Team";
        log.info("🔬 [API] START QA REVIEW — GRN: {} by {}", id, inspectedBy);
        try {
            GRNDto.Response grn = grnService.startQAReview(id, inspectedBy);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "QA Review started.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to start QA review."));
        }
    }

    /**
     * PUT /api/grn/{id}/qa-review
     * QA team saves inspection results. Status stays QA_REVIEW.
     *
     * Body:
     * {
     *   "grnId": 1,
     *   "inspectedByName": "Priya QA",
     *   "qaOverallRemarks": "3 units found damaged",
     *   "lineItems": [
     *     {
     *       "grnLineItemId": 101,
     *       "defectiveQuantity": 2,
     *       "rejectedQuantity": 2,
     *       "qaRemarks": "Screen cracked — rejected"
     *     }
     *   ]
     * }
     */
    @PutMapping("/{id}/qa-review")
    public ResponseEntity<?> submitQAReview(@PathVariable Long id,
                                             @RequestBody GRNDto.QAReviewRequest request) {
        request.setGrnId(id);
        log.info("🔬 [API] SUBMIT QA REVIEW — GRN: {}", id);
        try {
            GRNDto.Response grn = grnService.submitQAReview(request);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "QA review saved. Awaiting supervisor approval.", "data", grn));
        } catch (RuntimeException e) {
            log.error("❌ {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to save QA review."));
        }
    }

    /**
     * PUT /api/grn/{id}/approve
     * Transition: QA_REVIEW → APPROVED. GRN is now ready for 3-Way Match.
     *
     * Body: { "approvedByName": "Suresh Manager" }
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveGRN(@PathVariable Long id,
                                         @RequestBody(required = false) Map<String, Object> body) {
        String approvedBy = body != null
                ? body.getOrDefault("approvedByName", "Store Manager").toString()
                : "Store Manager";
        log.info("✅ [API] APPROVE GRN — ID: {} by {}", id, approvedBy);
        try {
            GRNDto.Response grn = grnService.approveGRN(id, approvedBy);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "GRN approved. Ready for 3-Way Match.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to approve GRN."));
        }
    }

    // =========================================================================
    // POST 3-WAY MATCH — CLOSE GRN
    // =========================================================================

    /**
     * PUT /api/grn/{id}/close
     * Transition: APPROVED → CLOSED
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeGRN(@PathVariable Long id) {
        log.info("🔒 [API] CLOSE GRN — ID: {}", id);
        try {
            GRNDto.Response grn = grnService.closeGRN(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "GRN closed.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to close GRN."));
        }
    }

    // =========================================================================
    // MANAGEMENT ENDPOINTS
    // =========================================================================

    /**
     * PUT /api/grn/{id}/link-invoice/{invoiceId}
     * Link an invoice to this GRN.
     * The same invoice can be linked to multiple GRNs (partial delivery batches).
     */
    @PutMapping("/{id}/link-invoice/{invoiceId}")
    public ResponseEntity<?> linkInvoice(@PathVariable Long id, @PathVariable Long invoiceId) {
        log.info("🔗 [API] LINK Invoice {} → GRN {}", invoiceId, id);
        try {
            GRNDto.Response grn = grnService.linkInvoice(id, invoiceId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Invoice linked to GRN.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to link invoice."));
        }
    }

    /**
     * PUT /api/grn/{id}/cancel
     * Body: { "reason": "Wrong PO reference" }
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelGRN(@PathVariable Long id,
                                        @RequestBody(required = false) Map<String, Object> body) {
        String reason = body != null
                ? body.getOrDefault("reason", "No reason provided").toString()
                : "";
        log.info("❌ [API] CANCEL GRN — ID: {}", id);
        try {
            GRNDto.Response grn = grnService.cancelGRN(id, reason);
            return ResponseEntity.ok(Map.of("success", true, "message", "GRN cancelled.", "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to cancel GRN."));
        }
    }

    /**
     * PUT /api/grn/{id}/revert-to-draft
     */
    @PutMapping("/{id}/revert-to-draft")
    public ResponseEntity<?> revertToDraft(@PathVariable Long id) {
        try {
            GRNDto.Response grn = grnService.revertToDraft(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "GRN reverted to DRAFT successfully",
                    "data", grn));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to revert GRN to draft."));
        }
    }

    // =========================================================================
    // READ ENDPOINTS
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getGRN(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", grnService.getGRNById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRN."));
        }
    }

    /** Approved GRNs for a PO — used by 3-Way Match selection UI. */
    @GetMapping("/po/{poId}")
    public ResponseEntity<?> getApprovedGRNsForPO(@PathVariable Long poId) {
        try {
            List<GRNDto.Response> list = grnService.getApprovedGRNsByPO(poId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRNs for PO."));
        }
    }

    /** All GRNs (any status) for a PO. */
    @GetMapping("/po/{poId}/all")
    public ResponseEntity<?> getAllGRNsForPO(@PathVariable Long poId) {
        try {
            List<GRNDto.Response> list = grnService.getAllGRNsByPO(poId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRNs."));
        }
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<?> getGRNsByInvoice(@PathVariable Long invoiceId) {
        try {
            List<GRNDto.Response> list = grnService.getGRNsByInvoice(invoiceId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRNs for invoice."));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getGRNsByUser(@PathVariable Long userId) {
        try {
            List<GRNDto.Response> list = grnService.getGRNsByUser(userId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRNs for user."));
        }
    }

    /**
     * GET /api/grn/po/{poId}/invoice/{invoiceId}
     *
     * All GRN delivery batches for a specific PO + Invoice combination.
     * Returns batches in chronological order (oldest first).
     *
     * Use this endpoint to:
     *  1. Show the buyer all delivery batches for a PO+Invoice
     *  2. Determine how much quantity has been received so far
     *  3. Identify if all ordered quantity has been received
     *
     * Example response for partial delivery (ordered 10):
     * [
     *   { "grnNumber": "ITTI/GRN/26/001", "status": "APPROVED",
     *     "lineItems": [{ "receivedQuantity": 8, "acceptedQuantity": 8 }] },
     *   { "grnNumber": "ITTI/GRN/26/002", "status": "DRAFT",
     *     "lineItems": [{ "receivedQuantity": 2, "acceptedQuantity": 0 }] }
     * ]
     */
    @GetMapping("/po/{poId}/invoice/{invoiceId}")
    public ResponseEntity<?> getGRNsByPoAndInvoice(
            @PathVariable Long poId,
            @PathVariable Long invoiceId) {
        log.info("📦 [API] GET GRNs — PO: {}, Invoice: {}", poId, invoiceId);
        try {
            List<GRNDto.Response> list = grnService.getGRNsByPoAndInvoice(poId, invoiceId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", list.size(),
                    "message", list.size() + " GRN batch(es) found for this PO + Invoice.",
                    "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch GRNs for PO+Invoice."));
        }
    }
}