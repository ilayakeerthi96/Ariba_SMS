package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.POApprovalResponse;
import com.itti.leadcapturing.service.POApprovalService;
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
@RequestMapping("/api/po-approval")
@CrossOrigin(origins = "*")
public class POApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(POApprovalController.class);

    @Autowired
    private POApprovalService poApprovalService;

    // ==================== INITIATE WORKFLOW ====================

    /**
     * POST /api/po-approval/initiate
     * Submit PO for approval — starts from the first (highest) hierarchy level
     *
     * {
     *   "poId": 1,
     *   "creatorUserId": 5
     * }
     */
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiateApprovalWorkflow(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long creatorUserId = Long.valueOf(request.get("creatorUserId").toString());

            logger.info("📥 [INITIATE PO WORKFLOW] PO ID: {}, Creator: {}", poId, creatorUserId);

            String result = poApprovalService.initiateApprovalWorkflow(poId, creatorUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to initiate PO workflow", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== APPROVE PO ====================

    /**
     * POST /api/po-approval/approve
     * Approve PO at current level — moves to next level or fully approves
     *
     * {
     *   "poId": 1,
     *   "approverId": 2,
     *   "comments": "Looks good, approved"
     * }
     */
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approvePO(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String comments = request.get("comments") != null ? request.get("comments").toString() : "";

            logger.info("📥 [APPROVE PO] PO ID: {}, Approver ID: {}", poId, approverId);

            String result = poApprovalService.approvePO(poId, approverId, comments);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "APPROVED");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to approve PO", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== REJECT PO ====================

    /**
     * POST /api/po-approval/reject
     * Permanently reject PO — status set to REJECTED, no resubmission allowed
     *
     * {
     *   "poId": 1,
     *   "rejectorId": 2,
     *   "rejectRemarks": "Budget exceeded current quarter limit"
     * }
     */
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectPO(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long rejectorId = Long.valueOf(request.get("rejectorId").toString());
            String rejectRemarks = request.get("rejectRemarks") != null
                    ? request.get("rejectRemarks").toString() : "";

            logger.info("📥 [REJECT PO] PO ID: {}, Rejector ID: {}", poId, rejectorId);

            String result = poApprovalService.rejectPO(poId, rejectorId, rejectRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "REJECTED");
            response.put("canResubmit", false);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to reject PO", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== RETURN FOR REVISION ====================

    /**
     * POST /api/po-approval/return-for-revision
     * Return PO to creator for changes — PO goes back to DRAFT status
     *
     * {
     *   "poId": 1,
     *   "approverId": 2,
     *   "revisionComments": "Please update payment terms and delivery schedule"
     * }
     */
    @PostMapping("/return-for-revision")
    public ResponseEntity<Map<String, Object>> returnForRevision(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String revisionComments = request.get("revisionComments") != null
                    ? request.get("revisionComments").toString() : "";

            logger.info("📥 [RETURN FOR REVISION] PO ID: {}, Approver ID: {}", poId, approverId);

            String result = poApprovalService.returnForRevision(poId, approverId, revisionComments);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "RETURNED_FOR_REVISION");
            response.put("canResubmit", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to return PO for revision", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== RESUBMIT AFTER REVISION ====================

    /**
     * POST /api/po-approval/resubmit
     * Creator resubmits PO after making revisions
     *
     * {
     *   "poId": 1,
     *   "resubmitterId": 5
     * }
     */
    @PostMapping("/resubmit")
    public ResponseEntity<Map<String, Object>> resubmitPO(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long resubmitterId = Long.valueOf(request.get("resubmitterId").toString());

            logger.info("📥 [RESUBMIT PO] PO ID: {}, Resubmitter ID: {}", poId, resubmitterId);

            String result = poApprovalService.resubmitPO(poId, resubmitterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to resubmit PO", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== HOLD PO ====================

    /**
     * POST /api/po-approval/hold
     * Current approver puts PO on hold
     *
     * {
     *   "poId": 1,
     *   "holderId": 2,
     *   "holdRemarks": "Waiting for board decision on supplier selection"
     * }
     */
    @PostMapping("/hold")
    public ResponseEntity<Map<String, Object>> holdPO(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long holderId = Long.valueOf(request.get("holderId").toString());
            String holdRemarks = request.get("holdRemarks") != null
                    ? request.get("holdRemarks").toString() : "";

            logger.info("📥 [HOLD PO] PO ID: {}, Holder ID: {}", poId, holderId);

            String result = poApprovalService.holdPO(poId, holderId, holdRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "HOLD");
            response.put("canRelease", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to hold PO", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== RELEASE HOLD ====================

    /**
     * POST /api/po-approval/release-hold
     * Release PO from hold — can be done by original holder or higher level user
     *
     * {
     *   "poId": 1,
     *   "releaserId": 2,
     *   "releaseRemarks": "Board decision received, proceed with approval"
     * }
     */
    @PostMapping("/release-hold")
    public ResponseEntity<Map<String, Object>> releaseHold(
            @RequestBody Map<String, Object> request) {
        try {
            Long poId = Long.valueOf(request.get("poId").toString());
            Long releaserId = Long.valueOf(request.get("releaserId").toString());
            String releaseRemarks = request.get("releaseRemarks") != null
                    ? request.get("releaseRemarks").toString() : "";

            logger.info("📥 [RELEASE HOLD] PO ID: {}, Releaser ID: {}", poId, releaserId);

            String result = poApprovalService.releaseHold(poId, releaserId, releaseRemarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("action", "RELEASED");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to release hold", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET APPROVAL HISTORY ====================

    /**
     * GET /api/po-approval/history/{poId}
     * Full approval trail for a PO — shown on PO detail page
     */
    @GetMapping("/history/{poId}")
    public ResponseEntity<Map<String, Object>> getApprovalHistory(@PathVariable Long poId) {
        try {
            logger.info("📥 [GET PO APPROVAL HISTORY] PO ID: {}", poId);
            List<POApprovalResponse> history = poApprovalService.getApprovalHistory(poId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("count", history.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to get PO approval history", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET CURRENT PENDING APPROVAL ====================

    /**
     * GET /api/po-approval/current/{poId}
     * Get the current pending/hold approval step for a PO
     */
    @GetMapping("/current/{poId}")
    public ResponseEntity<Map<String, Object>> getCurrentPendingApproval(@PathVariable Long poId) {
        try {
            Optional<POApprovalResponse> current = poApprovalService.getCurrentPendingApproval(poId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", current.orElse(null));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to get current PO approval", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET PENDING PO APPROVALS FOR USER ====================

    /**
     * GET /api/po-approval/pending/user/{userId}
     * All POs waiting for this user's approval
     */
    @GetMapping("/pending/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPendingApprovalsForUser(@PathVariable Long userId) {
        try {
            List<POApprovalResponse> pending = poApprovalService.getPendingApprovalsForUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pending);
            response.put("count", pending.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to get pending PO approvals for user", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET PENDING COUNT ====================

    /**
     * GET /api/po-approval/pending/user/{userId}/count
     */
    @GetMapping("/pending/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getPendingApprovalCount(@PathVariable Long userId) {
        try {
            Long count = poApprovalService.getPendingApprovalCount(userId);
            return ResponseEntity.ok(Map.of("success", true, "pendingCount", count, "data", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET HOLD APPROVALS FOR USER ====================

    /**
     * GET /api/po-approval/hold/user/{userId}
     * All POs on hold visible to this user
     */
    @GetMapping("/hold/user/{userId}")
    public ResponseEntity<Map<String, Object>> getHoldApprovalsForUser(@PathVariable Long userId) {
        try {
            List<POApprovalResponse> holds = poApprovalService.getHoldApprovalsForUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", holds);
            response.put("count", holds.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Failed to get hold PO approvals for user", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== GET HOLD COUNT ====================

    /**
     * GET /api/po-approval/hold/user/{userId}/count
     */
    @GetMapping("/hold/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getHoldApprovalCount(@PathVariable Long userId) {
        try {
            Long count = poApprovalService.getHoldApprovalCount(userId);
            return ResponseEntity.ok(Map.of("success", true, "holdCount", count, "data", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== CHECK IF LAST APPROVER ====================

    /**
     * GET /api/po-approval/is-last-approver/{poId}/{userId}
     */
    @GetMapping("/is-last-approver/{poId}/{userId}")
    public ResponseEntity<Map<String, Object>> isLastApprover(
            @PathVariable Long poId, @PathVariable Long userId) {
        try {
            boolean isLast = poApprovalService.isLastApprover(poId, userId);
            return ResponseEntity.ok(Map.of("success", true, "isLastApprover", isLast));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}