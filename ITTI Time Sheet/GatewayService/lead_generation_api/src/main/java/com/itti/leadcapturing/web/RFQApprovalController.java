// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.service.RFQApprovalService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.*;
// import java.util.*;

// @RestController
// @RequestMapping("/api/rfq-approval")
// @CrossOrigin(origins = "*")
// public class RFQApprovalController {

//     @Autowired
//     private RFQApprovalService approvalService;

//     // ==================== INITIATE WORKFLOW ====================

//     /**
//      * POST /api/rfq-approval/initiate/{rfqId}
//      * Initiate approval workflow for an RFQ
//      */
//     @PostMapping("/initiate/{rfqId}")
//     public ResponseEntity<?> initiateApprovalWorkflow(@PathVariable Long rfqId) {
//         try {
//             approvalService.initiateApprovalWorkflow(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Approval workflow initiated successfully");
//             response.put("rfqId", rfqId);

//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== APPROVE ====================

//     /**
//      * POST /api/rfq-approval/approve/{rfqId}
//      * Approve RFQ at current level
//      * 
//      * Body: {
//      *   "approverId": 123,
//      *   "approvalLevel": "PROCUREMENT",
//      *   "comments": "Approved - budget looks good"
//      * }
//      */
//     @PostMapping("/approve/{rfqId}")
//     public ResponseEntity<?> approveRFQ(
//             @PathVariable Long rfqId,
//             @RequestBody Map<String, Object> requestBody) {
//         try {
//             Long approverId = Long.valueOf(requestBody.get("approverId").toString());
//             String levelStr = requestBody.get("approvalLevel").toString();
//             String comments = requestBody.getOrDefault("comments", "").toString();

//             ApprovalLevel level = ApprovalLevel.valueOf(levelStr);

//             RFQ approved = approvalService.approveRFQ(rfqId, approverId, level, comments);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "RFQ approved at " + level.getDisplayName());
//             response.put("data", convertToDto(approved));

//             return ResponseEntity.ok(response);

//         } catch (IllegalArgumentException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Invalid approval level. Valid values: PROCUREMENT, COO, CEO, FINANCE");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

//         } catch (RuntimeException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== REJECT ====================

//     /**
//      * POST /api/rfq-approval/reject/{rfqId}
//      * Reject RFQ at current level
//      * 
//      * Body: {
//      *   "rejectorId": 123,
//      *   "approvalLevel": "COO",
//      *   "comments": "Budget exceeds limit - please revise"
//      * }
//      */
//     @PostMapping("/reject/{rfqId}")
//     public ResponseEntity<?> rejectRFQ(
//             @PathVariable Long rfqId,
//             @RequestBody Map<String, Object> requestBody) {
//         try {
//             Long rejectorId = Long.valueOf(requestBody.get("rejectorId").toString());
//             String levelStr = requestBody.get("approvalLevel").toString();
//             String comments = requestBody.get("comments").toString();

//             if (comments == null || comments.trim().isEmpty()) {
//                 throw new RuntimeException("Comments are required for rejection");
//             }

//             ApprovalLevel level = ApprovalLevel.valueOf(levelStr);

//             RFQ rejected = approvalService.rejectRFQ(rfqId, rejectorId, level, comments);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "RFQ rejected at " + level.getDisplayName());
//             response.put("data", convertToDto(rejected));

//             return ResponseEntity.ok(response);

//         } catch (IllegalArgumentException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Invalid approval level or missing required fields");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

//         } catch (RuntimeException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== RESUBMIT ====================

//     /**
//      * POST /api/rfq-approval/resubmit/{rfqId}
//      * Resubmit rejected RFQ
//      * 
//      * Body: {
//      *   "resubmitterId": 123
//      * }
//      */
//     @PostMapping("/resubmit/{rfqId}")
//     public ResponseEntity<?> resubmitRFQ(
//             @PathVariable Long rfqId,
//             @RequestBody Map<String, Object> requestBody) {
//         try {
//             Long resubmitterId = Long.valueOf(requestBody.get("resubmitterId").toString());

//             RFQ resubmitted = approvalService.resubmitRFQ(rfqId, resubmitterId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "RFQ resubmitted successfully - workflow restarted");
//             response.put("data", convertToDto(resubmitted));

//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== GET APPROVAL HISTORY ====================

//     /**
//      * GET /api/rfq-approval/history/{rfqId}
//      * Get approval history for an RFQ
//      */
//     @GetMapping("/history/{rfqId}")
//     public ResponseEntity<?> getApprovalHistory(@PathVariable Long rfqId) {
//         try {
//             List<RFQApproval> history = approvalService.getApprovalHistory(rfqId);

//             List<Map<String, Object>> historyData = new ArrayList<>();
//             for (RFQApproval approval : history) {
//                 Map<String, Object> item = new HashMap<>();
//                 item.put("id", approval.getId());
//                 item.put("level", approval.getApprovalLevel());
//                 item.put("levelName", approval.getApprovalLevel().getDisplayName());
//                 item.put("status", approval.getStatus());
//                 item.put("approverId", approval.getApproverUserId());
//                 item.put("comments", approval.getComments());
//                 item.put("actionDate", approval.getActionDate());
//                 item.put("sequenceOrder", approval.getSequenceOrder());
//                 historyData.add(item);
//             }

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("count", historyData.size());
//             response.put("data", historyData);

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch approval history");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== GET CURRENT PENDING APPROVAL ====================

//     /**
//      * GET /api/rfq-approval/current/{rfqId}
//      * Get current pending approval level
//      */
//     @GetMapping("/current/{rfqId}")
//     public ResponseEntity<?> getCurrentPendingApproval(@PathVariable Long rfqId) {
//         try {
//             Optional<RFQApproval> currentApproval = approvalService.getCurrentPendingApproval(rfqId);

//             if (currentApproval.isPresent()) {
//                 RFQApproval approval = currentApproval.get();
//                 Map<String, Object> data = new HashMap<>();
//                 data.put("id", approval.getId());
//                 data.put("level", approval.getApprovalLevel());
//                 data.put("levelName", approval.getApprovalLevel().getDisplayName());
//                 data.put("status", approval.getStatus());
//                 data.put("sequenceOrder", approval.getSequenceOrder());

//                 Map<String, Object> response = new HashMap<>();
//                 response.put("success", true);
//                 response.put("data", data);
//                 return ResponseEntity.ok(response);
//             } else {
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("success", true);
//                 response.put("message", "No pending approval found - workflow completed");
//                 response.put("data", null);
//                 return ResponseEntity.ok(response);
//             }

//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch current approval");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== GET PENDING APPROVALS BY LEVEL ====================

//     /**
//      * GET /api/rfq-approval/pending/{level}
//      * Get all pending approvals at a specific level
//      * Example: /api/rfq-approval/pending/PROCUREMENT
//      */
//     @GetMapping("/pending/{level}")
//     public ResponseEntity<?> getPendingApprovalsByLevel(@PathVariable String level) {
//         try {
//             ApprovalLevel approvalLevel = ApprovalLevel.valueOf(level);
//             List<RFQApproval> pending = approvalService.getPendingApprovalsByLevel(approvalLevel);

//             List<Map<String, Object>> pendingData = new ArrayList<>();
//             for (RFQApproval approval : pending) {
//                 Map<String, Object> item = new HashMap<>();
//                 item.put("id", approval.getId());
//                 item.put("rfqId", approval.getRfq().getId());
//                 item.put("rfqNumber", approval.getRfq().getRfqNumber());
//                 item.put("rfqTitle", approval.getRfq().getRfqTitle());
//                 item.put("level", approval.getApprovalLevel());
//                 item.put("levelName", approval.getApprovalLevel().getDisplayName());
//                 item.put("status", approval.getStatus());
//                 pendingData.add(item);
//             }

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("level", level);
//             response.put("count", pendingData.size());
//             response.put("data", pendingData);

//             return ResponseEntity.ok(response);

//         } catch (IllegalArgumentException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Invalid approval level. Valid values: PROCUREMENT, COO, CEO, FINANCE");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== HELPER METHOD ====================

//     private Map<String, Object> convertToDto(RFQ rfq) {
//         Map<String, Object> dto = new HashMap<>();
//         dto.put("id", rfq.getId());
//         dto.put("rfqNumber", rfq.getRfqNumber());
//         dto.put("rfqTitle", rfq.getRfqTitle());
//         dto.put("status", rfq.getStatus());
//         dto.put("approvalStatus", rfq.getApprovalStatus());
//         dto.put("updatedAt", rfq.getUpdatedAt());
//         return dto;
//     }
// }