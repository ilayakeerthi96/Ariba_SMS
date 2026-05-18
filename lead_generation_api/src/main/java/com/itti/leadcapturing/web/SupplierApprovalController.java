// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.SupplierApprovalResponse;
// import com.itti.leadcapturing.service.SupplierApprovalService;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/supplier-approval")
// @CrossOrigin(origins = "*")
// @Slf4j
// public class SupplierApprovalController {

//     @Autowired
//     private SupplierApprovalService supplierApprovalService;

//     // ============================================================
//     // POST /api/supplier-approval/initiate
//     // ============================================================
//     @PostMapping("/initiate")
//     public ResponseEntity<?> initiateApprovalWorkflow(@RequestBody Map<String, Object> body) {
//         try {
//             Long supplierId = Long.parseLong(body.get("supplierId").toString());
//             String companyName = body.get("companyName") != null ? body.get("companyName").toString() : null;

//             log.info("[SUPPLIER APPROVAL INITIATE] Supplier ID: {}, Company: {}", supplierId, companyName);

//             String result = supplierApprovalService.initiateApprovalWorkflow(supplierId, companyName);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             log.error("[SUPPLIER APPROVAL INITIATE] Error: {}", e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // POST /api/supplier-approval/approve
//     // ============================================================
//     @PostMapping("/approve")
//     public ResponseEntity<?> approveSupplier(@RequestBody Map<String, Object> body) {
//         try {
//             Long supplierId = Long.parseLong(body.get("supplierId").toString());
//             Long approverId = Long.parseLong(body.get("approverId").toString());
//             String comments = body.get("comments") != null ? body.get("comments").toString() : "Approved";

//             log.info("[SUPPLIER APPROVE] Supplier: {}, Approver: {}", supplierId, approverId);

//             String result = supplierApprovalService.approveSupplier(supplierId, approverId, comments);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             log.error("[SUPPLIER APPROVE] Error: {}", e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // POST /api/supplier-approval/reject
//     // ============================================================
//     @PostMapping("/reject")
//     public ResponseEntity<?> rejectSupplier(@RequestBody Map<String, Object> body) {
//         try {
//             Long supplierId = Long.parseLong(body.get("supplierId").toString());
//             Long rejectorId = Long.parseLong(body.get("rejectorId").toString());
//             String remarks = body.get("rejectRemarks") != null ? body.get("rejectRemarks").toString()
//                            : body.get("comments") != null ? body.get("comments").toString() : "Rejected";

//             log.info("[SUPPLIER REJECT] Supplier: {}, Rejector: {}", supplierId, rejectorId);

//             String result = supplierApprovalService.rejectSupplier(supplierId, rejectorId, remarks);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             log.error("[SUPPLIER REJECT] Error: {}", e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // POST /api/supplier-approval/hold
//     // ============================================================
//     @PostMapping("/hold")
//     public ResponseEntity<?> holdSupplier(@RequestBody Map<String, Object> body) {
//         try {
//             Long supplierId = Long.parseLong(body.get("supplierId").toString());
//             Long holderId   = Long.parseLong(body.get("holderId").toString());
//             String remarks  = body.get("holdRemarks") != null ? body.get("holdRemarks").toString()
//                             : body.get("comments") != null ? body.get("comments").toString() : "";

//             log.info("[SUPPLIER HOLD] Supplier: {}, Holder: {}", supplierId, holderId);

//             String result = supplierApprovalService.holdSupplier(supplierId, holderId, remarks);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             log.error("[SUPPLIER HOLD] Error: {}", e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // POST /api/supplier-approval/release-hold
//     // ============================================================
//     @PostMapping("/release-hold")
//     public ResponseEntity<?> releaseHold(@RequestBody Map<String, Object> body) {
//         try {
//             Long supplierId  = Long.parseLong(body.get("supplierId").toString());
//             Long releaserId  = Long.parseLong(body.get("releaserId").toString());
//             String remarks   = body.get("releaseRemarks") != null ? body.get("releaseRemarks").toString()
//                              : body.get("comments") != null ? body.get("comments").toString() : "";

//             log.info("[SUPPLIER RELEASE HOLD] Supplier: {}, Releaser: {}", supplierId, releaserId);

//             String result = supplierApprovalService.releaseHold(supplierId, releaserId, remarks);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             log.error("[SUPPLIER RELEASE HOLD] Error: {}", e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/history/{supplierId}
//     // ============================================================
//     @GetMapping("/history/{supplierId}")
//     public ResponseEntity<?> getApprovalHistory(@PathVariable Long supplierId) {
//         try {
//             List<SupplierApprovalResponse> history = supplierApprovalService.getApprovalHistory(supplierId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", history);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/current/{supplierId}
//     // ============================================================
//     @GetMapping("/current/{supplierId}")
//     public ResponseEntity<?> getCurrentPendingApproval(@PathVariable Long supplierId) {
//         try {
//             Optional<SupplierApprovalResponse> current = supplierApprovalService.getCurrentPendingApproval(supplierId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", current.orElse(null));
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/pending/user/{userId}
//     // ============================================================
//     @GetMapping("/pending/user/{userId}")
//     public ResponseEntity<?> getPendingApprovalsForUser(@PathVariable Long userId) {
//         try {
//             List<SupplierApprovalResponse> approvals = supplierApprovalService.getPendingApprovalsForUser(userId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", approvals);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/pending/user/{userId}/count
//     // ============================================================
//     @GetMapping("/pending/user/{userId}/count")
//     public ResponseEntity<?> getPendingApprovalCount(@PathVariable Long userId) {
//         try {
//             Long count = supplierApprovalService.getPendingApprovalCount(userId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", count);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/hold/user/{userId}
//     // ============================================================
//     @GetMapping("/hold/user/{userId}")
//     public ResponseEntity<?> getHoldApprovalsForUser(@PathVariable Long userId) {
//         try {
//             List<SupplierApprovalResponse> approvals = supplierApprovalService.getHoldApprovalsForUser(userId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", approvals);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/hold/user/{userId}/count
//     // ============================================================
//     @GetMapping("/hold/user/{userId}/count")
//     public ResponseEntity<?> getHoldApprovalCount(@PathVariable Long userId) {
//         try {
//             Long count = supplierApprovalService.getHoldApprovalCount(userId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", count);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ============================================================
//     // GET /api/supplier-approval/is-last-approver/{supplierId}/{userId}
//     // ============================================================
//     @GetMapping("/is-last-approver/{supplierId}/{userId}")
//     public ResponseEntity<?> isLastApprover(@PathVariable Long supplierId, @PathVariable Long userId) {
//         try {
//             boolean isLast = supplierApprovalService.isLastApprover(supplierId, userId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("isLastApprover", isLast);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }
// }


package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.SupplierApprovalResponse;
import com.itti.leadcapturing.service.SupplierApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/supplier-approval")
@CrossOrigin(origins = "*")
@Slf4j
public class SupplierApprovalController {

    @Autowired
    private SupplierApprovalService supplierApprovalService;

    // ============================================================
    // POST /api/supplier-approval/initiate
    // ============================================================
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateApprovalWorkflow(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId  = Long.parseLong(body.get("supplierId").toString());
            String companyName = body.get("companyName") != null ? body.get("companyName").toString() : null;
            String result    = supplierApprovalService.initiateApprovalWorkflow(supplierId, companyName);
            return ok(result);
        } catch (Exception e) {
            log.error("[INITIATE] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // POST /api/supplier-approval/approve
    // ============================================================
    @PostMapping("/approve")
    public ResponseEntity<?> approveSupplier(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId  = Long.parseLong(body.get("supplierId").toString());
            Long approverId  = Long.parseLong(body.get("approverId").toString());
            String comments  = body.get("comments") != null ? body.get("comments").toString() : "Approved";
            String result    = supplierApprovalService.approveSupplier(supplierId, approverId, comments);
            return ok(result);
        } catch (Exception e) {
            log.error("[APPROVE] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // POST /api/supplier-approval/reject
    // ============================================================
    @PostMapping("/reject")
    public ResponseEntity<?> rejectSupplier(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId  = Long.parseLong(body.get("supplierId").toString());
            Long rejectorId  = Long.parseLong(body.get("rejectorId").toString());
            String remarks   = body.get("rejectRemarks") != null ? body.get("rejectRemarks").toString()
                             : body.get("comments") != null ? body.get("comments").toString() : "Rejected";
            String result    = supplierApprovalService.rejectSupplier(supplierId, rejectorId, remarks);
            return ok(result);
        } catch (Exception e) {
            log.error("[REJECT] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // POST /api/supplier-approval/hold
    // ============================================================
    @PostMapping("/hold")
    public ResponseEntity<?> holdSupplier(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId = Long.parseLong(body.get("supplierId").toString());
            Long holderId   = Long.parseLong(body.get("holderId").toString());
            String remarks  = body.get("holdRemarks") != null ? body.get("holdRemarks").toString()
                            : body.get("comments") != null ? body.get("comments").toString() : "";
            String result   = supplierApprovalService.holdSupplier(supplierId, holderId, remarks);
            return ok(result);
        } catch (Exception e) {
            log.error("[HOLD] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // POST /api/supplier-approval/release-hold
    // ============================================================
    @PostMapping("/release-hold")
    public ResponseEntity<?> releaseHold(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId  = Long.parseLong(body.get("supplierId").toString());
            Long releaserId  = Long.parseLong(body.get("releaserId").toString());
            String remarks   = body.get("releaseRemarks") != null ? body.get("releaseRemarks").toString()
                             : body.get("comments") != null ? body.get("comments").toString() : "";
            String result    = supplierApprovalService.releaseHold(supplierId, releaserId, remarks);
            return ok(result);
        } catch (Exception e) {
            log.error("[RELEASE HOLD] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // ✅ NEW: POST /api/supplier-approval/need-more-info
    // ============================================================
    @PostMapping("/need-more-info")
    public ResponseEntity<?> requestMoreInfo(@RequestBody Map<String, Object> body) {
        try {
            Long supplierId  = Long.parseLong(body.get("supplierId").toString());
            Long requesterId = Long.parseLong(body.get("requesterId").toString());
            String infoRequest = body.get("infoRequest") != null
                    ? body.get("infoRequest").toString()
                    : body.get("comments") != null ? body.get("comments").toString() : "";

            if (infoRequest.isBlank())
                return error("Info request message is required");

            String result = supplierApprovalService.requestMoreInfo(supplierId, requesterId, infoRequest);
            return ok(result);
        } catch (Exception e) {
            log.error("[NEED MORE INFO] Error: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/history/{supplierId}
    // ============================================================
    @GetMapping("/history/{supplierId}")
    public ResponseEntity<?> getApprovalHistory(@PathVariable Long supplierId) {
        try {
            List<SupplierApprovalResponse> history = supplierApprovalService.getApprovalHistory(supplierId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/current/{supplierId}
    // ============================================================
    @GetMapping("/current/{supplierId}")
    public ResponseEntity<?> getCurrentPendingApproval(@PathVariable Long supplierId) {
        try {
            Optional<SupplierApprovalResponse> current = supplierApprovalService.getCurrentPendingApproval(supplierId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", current.orElse(null));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/pending/user/{userId}
    // ============================================================
    @GetMapping("/pending/user/{userId}")
    public ResponseEntity<?> getPendingApprovalsForUser(@PathVariable Long userId) {
        try {
            List<SupplierApprovalResponse> approvals = supplierApprovalService.getPendingApprovalsForUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", approvals);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/pending/user/{userId}/count
    // ============================================================
    @GetMapping("/pending/user/{userId}/count")
    public ResponseEntity<?> getPendingApprovalCount(@PathVariable Long userId) {
        try {
            Long count = supplierApprovalService.getPendingApprovalCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/hold/user/{userId}
    // ============================================================
    @GetMapping("/hold/user/{userId}")
    public ResponseEntity<?> getHoldApprovalsForUser(@PathVariable Long userId) {
        try {
            List<SupplierApprovalResponse> approvals = supplierApprovalService.getHoldApprovalsForUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", approvals);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/hold/user/{userId}/count
    // ============================================================
    @GetMapping("/hold/user/{userId}/count")
    public ResponseEntity<?> getHoldApprovalCount(@PathVariable Long userId) {
        try {
            Long count = supplierApprovalService.getHoldApprovalCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ============================================================
    // GET /api/supplier-approval/is-last-approver/{supplierId}/{userId}
    // ============================================================
    @GetMapping("/is-last-approver/{supplierId}/{userId}")
    public ResponseEntity<?> isLastApprover(@PathVariable Long supplierId, @PathVariable Long userId) {
        try {
            boolean isLast = supplierApprovalService.isLastApprover(supplierId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isLastApprover", isLast);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ==================== HELPERS ====================

    private ResponseEntity<Map<String, Object>> ok(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", message);
        return ResponseEntity.ok(res);
    }

    private ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}