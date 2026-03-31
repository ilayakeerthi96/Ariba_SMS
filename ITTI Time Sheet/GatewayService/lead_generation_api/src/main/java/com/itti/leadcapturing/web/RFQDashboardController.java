
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.RFQDashboardResponse;
import com.itti.leadcapturing.model.RFQ;
import com.itti.leadcapturing.service.RFQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rfq-dashboard")
@CrossOrigin(origins = "*")
public class RFQDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(RFQDashboardController.class);

    @Autowired
    private RFQService rfqService;

    /**
     * ✅ FIXED: Get RFQ dashboard statistics
     */
    @GetMapping("/statistics/{buyerId}/{userId}")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics(
            @PathVariable Long buyerId,
            @PathVariable Long userId
    ) {
        logger.info("📊 [GET DASHBOARD STATS] Buyer ID: {}, User ID: {}", buyerId, userId);

        try {
            List<RFQ> allRFQs = rfqService.getRFQsByBuyer(buyerId);

            long totalRFQs = allRFQs.size();
            long pendingApprovals = allRFQs.stream()
                    .filter(rfq -> "AWAITING_APPROVAL".equals(rfq.getStatus()))
                    .count();
            long approved = allRFQs.stream()
                    .filter(rfq -> "APPROVED".equals(rfq.getApprovalStatus()))
                    .count();
            long rejected = allRFQs.stream()
                    .filter(rfq -> "REJECTED".equals(rfq.getApprovalStatus()))
                    .count();
            long published = allRFQs.stream()
                    .filter(rfq -> "PUBLISHED".equals(rfq.getStatus()))
                    .count();
            long closed = allRFQs.stream()
                    .filter(rfq -> "CLOSED".equals(rfq.getStatus()))
                    .count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRFQs", totalRFQs);
            stats.put("pendingApprovals", pendingApprovals);
            stats.put("approved", approved);
            stats.put("rejected", rejected);
            stats.put("published", published);
            stats.put("closed", closed);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get dashboard statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ✅ FIXED: Get RFQ list with supplier counts
     */
    @GetMapping("/rfqs/{buyerId}")
    public ResponseEntity<Map<String, Object>> getRFQList(
            @PathVariable Long buyerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String status
    ) {
        logger.info("📋 [GET RFQ LIST] Buyer ID: {}", buyerId);

        try {
            List<RFQ> rfqs = rfqService.getRFQsByBuyer(buyerId);

            // Apply filters
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                rfqs = rfqs.stream()
                        .filter(rfq -> 
                            rfq.getRfqNumber().toLowerCase().contains(searchLower) ||
                            rfq.getRfqTitle().toLowerCase().contains(searchLower)
                        )
                        .collect(Collectors.toList());
            }

            if (approvalStatus != null && !approvalStatus.isEmpty()) {
                rfqs = rfqs.stream()
                        .filter(rfq -> approvalStatus.equals(rfq.getApprovalStatus()))
                        .collect(Collectors.toList());
            }

            if (status != null && !status.isEmpty()) {
                rfqs = rfqs.stream()
                        .filter(rfq -> status.equals(rfq.getStatus()))
                        .collect(Collectors.toList());
            }

            // Convert to dashboard response with supplier counts
            List<RFQDashboardResponse> responseList = rfqs.stream()
                    .map(this::toRFQDashboardResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("count", responseList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get RFQ list", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ✅ FIXED: Get RFQ details with full supplier information
     */
    @GetMapping("/rfq/{rfqId}/details")
    public ResponseEntity<Map<String, Object>> getRFQDetails(@PathVariable Long rfqId) {
        logger.info("🔍 [GET RFQ DETAILS] RFQ ID: {}", rfqId);

        try {
            RFQ rfq = rfqService.getRFQById(rfqId);

            RFQDashboardResponse response = toRFQDashboardResponse(rfq);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Failed to get RFQ details", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== HELPER METHOD ====================

    /**
     * ✅ FIXED: Convert RFQ to Dashboard Response with proper supplier counts
     */
    private RFQDashboardResponse toRFQDashboardResponse(RFQ rfq) {
        RFQDashboardResponse response = new RFQDashboardResponse();
        response.setId(rfq.getId());
        response.setRfqNumber(rfq.getRfqNumber());
        response.setRfqTitle(rfq.getRfqTitle());
        response.setRfqDescription(rfq.getRfqDescription());
     response.setStatus(rfq.getStatus().name());  // ✅ Convert enum to String
response.setApprovalStatus(rfq.getApprovalStatus() != null ? rfq.getApprovalStatus().name() : null);  // ✅ Convert enum to String
        response.setDueDate(rfq.getDueDate());
        response.setCreatedAt(rfq.getCreatedAt());

        // ✅ FIXED: Calculate supplier counts properly
        int suppliersCount = (rfq.getSelectedSuppliers() != null) ? 
                             rfq.getSelectedSuppliers().size() : 0;
        
        response.setSuppliersCount(suppliersCount);
        
        // ✅ Calculate items count
        int itemsCount = (rfq.getItems() != null) ? rfq.getItems().size() : 0;
        response.setItemsCount(itemsCount);

        if (rfq.getBuyer() != null) {
            response.setBuyerName(rfq.getBuyer().getCompanyName());
        }

        return response;
    }
}