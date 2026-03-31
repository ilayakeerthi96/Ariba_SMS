package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.DynamicRFQApprovalResponse;
import com.itti.leadcapturing.service.DynamicRFQApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dynamic-rfq-approval")
@CrossOrigin(origins = "*")
public class DynamicRFQApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRFQApprovalController.class);

    @Autowired
    private DynamicRFQApprovalService approvalService;

    // ==================== INITIATE WORKFLOW ====================

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiateApprovalWorkflow(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long creatorUserId = Long.valueOf(request.get("creatorUserId").toString());

            logger.info("📥 [INITIATE WORKFLOW REQUEST] RFQ ID: {}, Creator User ID: {}", rfqId, creatorUserId);

            String result = approvalService.initiateApprovalWorkflow(rfqId, creatorUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to initiate workflow", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== APPROVE RFQ ====================

    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveRFQ(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String comments = request.get("comments") != null ? request.get("comments").toString() : "";

            logger.info("📥 [APPROVE REQUEST] RFQ ID: {}, Approver ID: {}", rfqId, approverId);

            String result = approvalService.approveRFQ(rfqId, approverId, comments);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to approve RFQ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== ✅ ENHANCED: REJECT RFQ ====================

    /**
     * POST /api/dynamic-rfq-approval/reject
     * Reject RFQ permanently with enhanced tracking
     * 
     * Request Body:
     * {
     *   "rfqId": 1,
     *   "rejectorId": 2,
     *   "rejectRemarks": "Budget not approved for this quarter"
     * }
     */
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectRFQ(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long rejectorId = Long.valueOf(request.get("rejectorId").toString());
            String rejectRemarks = request.get("rejectRemarks") != null ? 
                                  request.get("rejectRemarks").toString() : "";

            logger.info("📥 [REJECT REQUEST - PERMANENT] RFQ ID: {}, Rejector ID: {}", rfqId, rejectorId);

            String result = approvalService.rejectRFQ(rfqId, rejectorId, rejectRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "REJECTED");
            response.put("canResubmit", false);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to reject RFQ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== RETURN FOR REVISION ====================

    @PostMapping("/return-for-revision")
    public ResponseEntity<Map<String, Object>> returnForRevision(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String revisionComments = request.get("revisionComments") != null ? 
                                     request.get("revisionComments").toString() : "";

            logger.info("📥 [RETURN FOR REVISION REQUEST] RFQ ID: {}, Approver ID: {}", rfqId, approverId);

            String result = approvalService.returnForRevision(rfqId, approverId, revisionComments);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "RETURNED_FOR_REVISION");
            response.put("canResubmit", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to return for revision", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== RESUBMIT RFQ ====================

    @PostMapping("/resubmit")
    public ResponseEntity<Map<String, Object>> resubmitRFQ(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long resubmitterId = Long.valueOf(request.get("resubmitterId").toString());

            logger.info("📥 [RESUBMIT REQUEST] RFQ ID: {}, Resubmitter ID: {}", rfqId, resubmitterId);

            String result = approvalService.resubmitRFQ(rfqId, resubmitterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to resubmit RFQ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== ✅ ENHANCED: HOLD RFQ ====================

    /**
     * POST /api/dynamic-rfq-approval/hold
     * Put RFQ on HOLD by current approver with enhanced tracking
     * 
     * Request Body:
     * {
     *   "rfqId": 1,
     *   "holderId": 2,
     *   "holdRemarks": "Waiting for budget clarification from finance team"
     * }
     */
    @PostMapping("/hold")
    public ResponseEntity<Map<String, Object>> holdRFQ(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long holderId = Long.valueOf(request.get("holderId").toString());
            String holdRemarks = request.get("holdRemarks") != null ? 
                                request.get("holdRemarks").toString() : "";

            logger.info("📥 [HOLD REQUEST] RFQ ID: {}, Holder ID: {}", rfqId, holderId);

            String result = approvalService.holdRFQ(rfqId, holderId, holdRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "HOLD");
            response.put("canRelease", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to hold RFQ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== ✅ ENHANCED: RELEASE HOLD ====================

    /**
     * POST /api/dynamic-rfq-approval/release-hold
     * Release RFQ from HOLD status
     * 
     * Can be released by:
     * 1. The user who put it on hold
     * 2. Any user at a HIGHER hierarchy level
     * 
     * Request Body:
     * {
     *   "rfqId": 1,
     *   "releaserId": 3,
     *   "releaseRemarks": "Budget approved, continuing with approval"
     * }
     */
    @PostMapping("/release-hold")
    public ResponseEntity<Map<String, Object>> releaseHold(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long releaserId = Long.valueOf(request.get("releaserId").toString());
            String releaseRemarks = request.get("releaseRemarks") != null ? 
                                    request.get("releaseRemarks").toString() : "";

            logger.info("📥 [RELEASE HOLD REQUEST] RFQ ID: {}, Releaser ID: {}", rfqId, releaserId);

            String result = approvalService.releaseHold(rfqId, releaserId, releaseRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "RELEASED");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to release hold", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== GET APPROVAL HISTORY ====================

    /**
     * GET /api/dynamic-rfq-approval/history/{rfqId}
     * Get complete approval history with hold/reject tracking
     */
    @GetMapping("/history/{rfqId}")
    public ResponseEntity<Map<String, Object>> getApprovalHistory(
            @PathVariable Long rfqId
    ) {
        try {
            logger.info("📥 [GET HISTORY REQUEST] RFQ ID: {}", rfqId);

            List<DynamicRFQApprovalResponse> history = approvalService.getApprovalHistory(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("count", history.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get approval history", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== GET CURRENT PENDING APPROVAL ====================

    @GetMapping("/current/{rfqId}")
    public ResponseEntity<Map<String, Object>> getCurrentPendingApproval(
            @PathVariable Long rfqId
    ) {
        try {
            logger.info("📥 [GET CURRENT PENDING REQUEST] RFQ ID: {}", rfqId);

            Optional<DynamicRFQApprovalResponse> currentApproval = approvalService.getCurrentPendingApproval(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", currentApproval.orElse(null));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get current pending approval", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== GET PENDING APPROVALS FOR USER ====================

    @GetMapping("/pending/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPendingApprovalsForUser(
            @PathVariable Long userId
    ) {
        try {
            logger.info("📥 [GET PENDING FOR USER REQUEST] User ID: {}", userId);

            List<DynamicRFQApprovalResponse> pendingApprovals = approvalService.getPendingApprovalsForUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pendingApprovals);
            response.put("count", pendingApprovals.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get pending approvals for user", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== ✅ ENHANCED: GET HOLD APPROVALS FOR USER ====================

    /**
     * GET /api/dynamic-rfq-approval/hold/user/{userId}
     * Get all RFQs on HOLD visible to this user
     * 
     * Includes:
     * - RFQs held by this user
     * - RFQs held at lower hierarchy levels (if user is at higher level)
     */
    @GetMapping("/hold/user/{userId}")
    public ResponseEntity<Map<String, Object>> getHoldApprovalsForUser(
            @PathVariable Long userId
    ) {
        try {
            logger.info("📥 [GET HOLD APPROVALS FOR USER REQUEST] User ID: {}", userId);

            List<DynamicRFQApprovalResponse> holdApprovals = approvalService.getHoldApprovalsForUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", holdApprovals);
            response.put("count", holdApprovals.size());
            response.put("message", holdApprovals.size() + " RFQ(s) on hold visible to user");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get hold approvals for user", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== GET PENDING APPROVAL COUNT ====================

    @GetMapping("/pending/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getPendingApprovalCount(
            @PathVariable Long userId
    ) {
        try {
            logger.info("📥 [GET PENDING COUNT REQUEST] User ID: {}", userId);

            Long count = approvalService.getPendingApprovalCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pendingCount", count);
            response.put("data", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get pending approval count", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    // ==================== ✅ NEW: CHECK IF USER IS LAST APPROVER ====================
    
    /**
     * GET /api/dynamic-rfq-approval/is-last-approver/{rfqId}/{userId}
     * Check if the current user is at the last/lowest approval level
     */
    @GetMapping("/is-last-approver/{rfqId}/{userId}")
    public ResponseEntity<Map<String, Object>> isLastApprover(
            @PathVariable Long rfqId,
            @PathVariable Long userId
    ) {
        try {
            logger.info("📥 [CHECK LAST APPROVER] RFQ ID: {}, User ID: {}", rfqId, userId);
            
            boolean isLast = approvalService.isLastApprover(rfqId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isLastApprover", isLast);
            response.put("requiresDates", isLast);
            
            logger.info("  ✅ Result: {}", isLast);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to check last approver", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // ==================== ✅ NEW: APPROVE WITH DATES ====================
    
// ==================== ✅ UPDATED: APPROVE WITH DATES + DOWNLOAD PERMISSION ====================

/**
 * POST /api/dynamic-rfq-approval/approve-with-dates
 * Approve RFQ with optional date setting and download permission (required for last level)
 */
@PostMapping("/approve-with-dates")
public ResponseEntity<Map<String, Object>> approveWithDates(
        @RequestBody Map<String, Object> request
) {
    try {
        Long rfqId = Long.valueOf(request.get("rfqId").toString());
        Long approverId = Long.valueOf(request.get("approverId").toString());
        String comments = request.get("comments") != null ? request.get("comments").toString() : "";
        
        LocalDateTime rfqDueDate = null;
        LocalDateTime rfqDeliveryDate = null;
        
        // ✅ NEW: Parse download permission (default to true if not provided)
        Boolean allowSupplierDownload = true;
        if (request.get("allowSupplierDownload") != null) {
            allowSupplierDownload = Boolean.parseBoolean(request.get("allowSupplierDownload").toString());
        }
        
        // Parse dates if provided
        if (request.get("rfqDueDate") != null && !request.get("rfqDueDate").toString().isEmpty()) {
            rfqDueDate = LocalDateTime.parse(request.get("rfqDueDate").toString());
        }
        
        if (request.get("rfqDeliveryDate") != null && !request.get("rfqDeliveryDate").toString().isEmpty()) {
            rfqDeliveryDate = LocalDateTime.parse(request.get("rfqDeliveryDate").toString());
        }
        
        logger.info("📥 [APPROVE WITH DATES REQUEST] RFQ ID: {}, Approver ID: {}", rfqId, approverId);
        logger.info("  📥 Allow Supplier Download: {}", allowSupplierDownload);
        
        if (rfqDueDate != null && rfqDeliveryDate != null) {
            logger.info("  📅 RFQ Due Date: {}", rfqDueDate);
            logger.info("  📅 Delivery Date: {}", rfqDeliveryDate);
            
            // Backend validation: Delivery date >= Due date
            if (rfqDeliveryDate.isBefore(rfqDueDate)) {
                logger.warn("❌ Validation failed: Delivery date is before due date");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Expected Delivery Date cannot be earlier than RFQ Due Date");
                errorResponse.put("validationError", "DELIVERY_BEFORE_DUE");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Backend validation: Dates must not be in the past
            LocalDateTime now = LocalDateTime.now();
            if (rfqDueDate.isBefore(now)) {
                logger.warn("❌ Validation failed: Due date is in the past");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "RFQ Due Date cannot be in the past");
                errorResponse.put("validationError", "DUE_DATE_PAST");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (rfqDeliveryDate.isBefore(now)) {
                logger.warn("❌ Validation failed: Delivery date is in the past");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Expected Delivery Date cannot be in the past");
                errorResponse.put("validationError", "DELIVERY_DATE_PAST");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            logger.info("  ✅ Date validation passed");
        }
        
        // ✅ UPDATED: Pass download permission to service
        String result = approvalService.approveWithDates(
            rfqId, 
            approverId, 
            comments,
            rfqDueDate,
            rfqDeliveryDate,
            allowSupplierDownload  // ✅ NEW parameter
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);
        response.put("action", "APPROVED");
        response.put("allowSupplierDownload", allowSupplierDownload);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("❌ [ERROR] Failed to approve with dates", e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
    // ==================== ✅ ENHANCED: GET HOLD APPROVAL COUNT ====================

    /**
     * GET /api/dynamic-rfq-approval/hold/user/{userId}/count
     * Get count of RFQs on HOLD visible to user
     */
    @GetMapping("/hold/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getHoldApprovalCount(
            @PathVariable Long userId
    ) {
        try {
            logger.info("📥 [GET HOLD COUNT REQUEST] User ID: {}", userId);

            Long count = approvalService.getHoldApprovalCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("holdCount", count);
            response.put("data", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get hold approval count", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}