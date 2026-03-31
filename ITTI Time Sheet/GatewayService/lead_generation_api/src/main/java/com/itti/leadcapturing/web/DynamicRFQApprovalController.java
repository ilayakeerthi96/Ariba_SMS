
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.DynamicRFQApprovalResponse;
import com.itti.leadcapturing.service.DynamicRFQApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * POST /api/dynamic-rfq-approval/initiate
     * Initiate approval workflow for an RFQ
     * 
     * Request Body: { "rfqId": 1, "creatorUserId": 2 }
     */
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

    /**
     * POST /api/dynamic-rfq-approval/approve
     * Approve RFQ at current level
     * 
     * Request Body: { "rfqId": 1, "approverId": 2, "comments": "Approved" }
     */
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

    // ==================== REJECT RFQ ====================

    /**
     * POST /api/dynamic-rfq-approval/reject
     * Reject RFQ at current level
     * 
     * Request Body: { "rfqId": 1, "rejectorId": 2, "comments": "Needs revision" }
     */
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectRFQ(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            Long rejectorId = Long.valueOf(request.get("rejectorId").toString());
            String comments = request.get("comments") != null ? request.get("comments").toString() : "";

            logger.info("📥 [REJECT REQUEST] RFQ ID: {}, Rejector ID: {}", rfqId, rejectorId);

            String result = approvalService.rejectRFQ(rfqId, rejectorId, comments);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to reject RFQ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== RESUBMIT RFQ ====================

    /**
     * POST /api/dynamic-rfq-approval/resubmit
     * Resubmit rejected RFQ (restart workflow)
     * 
     * Request Body: { "rfqId": 1, "resubmitterId": 2 }
     */
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

    // ==================== GET APPROVAL HISTORY ====================

    /**
     * GET /api/dynamic-rfq-approval/history/{rfqId}
     * Get approval history for an RFQ
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

    /**
     * GET /api/dynamic-rfq-approval/current/{rfqId}
     * Get current pending approval level for an RFQ
     */
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

    /**
     * GET /api/dynamic-rfq-approval/pending/user/{userId}
     * Get all pending approvals for a user
     */
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

    // ==================== GET PENDING APPROVAL COUNT ====================

    /**
     * GET /api/dynamic-rfq-approval/pending/user/{userId}/count
     * Get count of pending approvals for a user
     */
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
}