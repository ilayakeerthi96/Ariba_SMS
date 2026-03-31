package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RFQDashboardService {

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private DynamicRFQApprovalRepository approvalRepository;

    @Autowired
    private HierarchyUserRepository hierarchyUserRepository;

    // ==================== DASHBOARD STATISTICS ====================

    /**
     * ✅ Get complete dashboard statistics for a manager/buyer
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStatistics(Long buyerId, Long userId) {
        System.out.println("=".repeat(80));
        System.out.println("[DASHBOARD STATS] Buyer ID: " + buyerId + ", User ID: " + userId);

        Map<String, Object> stats = new HashMap<>();

        // Total RFQs
        List<RFQ> allRFQs = rfqRepository.findByBuyerId(buyerId);
        stats.put("totalRFQs", allRFQs.size());
        System.out.println("  Total RFQs: " + allRFQs.size());

        // Pending Approvals (for this specific user)
        long pendingCount = approvalRepository.countPendingByUser(userId);
        stats.put("pendingApprovals", pendingCount);
        System.out.println("  Pending Approvals: " + pendingCount);

        // Approved RFQs
        long approvedCount = allRFQs.stream()
                .filter(rfq -> RFQStatus.PUBLISHED.equals(rfq.getStatus()) || 
                              RFQStatus.APPROVED.equals(rfq.getStatus()))
                .count();
        stats.put("approvedRFQs", approvedCount);
        System.out.println("  Approved RFQs: " + approvedCount);

        // Published RFQs (sent to suppliers)
        long publishedCount = allRFQs.stream()
                .filter(rfq -> RFQStatus.PUBLISHED.equals(rfq.getStatus()))
                .count();
        stats.put("publishedRFQs", publishedCount);
        System.out.println("  Published RFQs: " + publishedCount);

        // RFQs by Status
        Map<String, Long> byStatus = allRFQs.stream()
                .collect(Collectors.groupingBy(
                    rfq -> rfq.getStatus().toString(),
                    Collectors.counting()
                ));
        stats.put("rfqsByStatus", byStatus);

        // RFQs Awaiting Approval (not yet approved/rejected)
        long awaitingApproval = allRFQs.stream()
                .filter(rfq -> RFQStatus.AWAITING_APPROVAL.equals(rfq.getStatus()))
                .count();
        stats.put("awaitingApproval", awaitingApproval);

        // Draft RFQs
        long draftCount = allRFQs.stream()
                .filter(rfq -> RFQStatus.DRAFT.equals(rfq.getStatus()))
                .count();
        stats.put("draftRFQs", draftCount);

        System.out.println("[✓] Dashboard statistics compiled");
        System.out.println("=".repeat(80));

        return stats;
    }

    // ==================== RFQ LIST WITH FILTERS ====================

    /**
     * ✅ Get RFQs with comprehensive filters
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRFQsWithFilters(Long buyerId, Map<String, String> filters) {
        System.out.println("=".repeat(80));
        System.out.println("[GET RFQs] Buyer ID: " + buyerId);
        System.out.println("  Filters: " + filters);

        List<RFQ> allRFQs = rfqRepository.findByBuyerId(buyerId);
        System.out.println("  Total RFQs: " + allRFQs.size());

        // Apply filters
        List<RFQ> filtered = allRFQs;

        // Filter by search term
        String search = filters.get("search");
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            filtered = filtered.stream()
                    .filter(rfq -> 
                        rfq.getRfqNumber().toLowerCase().contains(searchLower) ||
                        rfq.getRfqTitle().toLowerCase().contains(searchLower) ||
                        (rfq.getRfqDescription() != null && 
                         rfq.getRfqDescription().toLowerCase().contains(searchLower))
                    )
                    .collect(Collectors.toList());
            System.out.println("  After search filter: " + filtered.size());
        }

        // Filter by approval status
        String approvalStatus = filters.get("approvalStatus");
        if (approvalStatus != null && !approvalStatus.equals("ALL")) {
            filtered = filtered.stream()
                    .filter(rfq -> rfq.getApprovalStatus() != null && 
                                  rfq.getApprovalStatus().toString().equals(approvalStatus))
                    .collect(Collectors.toList());
            System.out.println("  After approval status filter: " + filtered.size());
        }

        // Filter by RFQ status
        String status = filters.get("status");
        if (status != null && !status.equals("ALL")) {
            filtered = filtered.stream()
                    .filter(rfq -> rfq.getStatus().toString().equals(status))
                    .collect(Collectors.toList());
            System.out.println("  After status filter: " + filtered.size());
        }

        // Build response with detailed info
        List<Map<String, Object>> rfqList = new ArrayList<>();
        for (RFQ rfq : filtered) {
            Map<String, Object> rfqData = buildRFQSummary(rfq);
            rfqList.add(rfqData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", filtered.size());
        response.put("rfqs", rfqList);

        System.out.println("[✓] Filtered RFQs compiled");
        System.out.println("=".repeat(80));

        return response;
    }

    // ==================== RFQ DETAIL WITH SUPPLIERS ====================

    /**
     * ✅ Get complete RFQ details including supplier info
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRFQDetails(Long rfqId) {
        System.out.println("=".repeat(80));
        System.out.println("[GET RFQ DETAILS] ID: " + rfqId);

        RFQ rfq = rfqRepository.findByIdWithSuppliers(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        Map<String, Object> details = buildRFQSummary(rfq);

        // ✅ Add supplier details
        Set<Supplier> suppliers = rfq.getSelectedSuppliers();
        List<Map<String, String>> supplierList = new ArrayList<>();
        
        for (Supplier supplier : suppliers) {
            Map<String, String> supplierInfo = new HashMap<>();
            supplierInfo.put("id", supplier.getId().toString());
            supplierInfo.put("companyName", supplier.getCompanyName());
            supplierInfo.put("contactEmail", supplier.getContactPersonEmail());
            supplierInfo.put("contactPhone", supplier.getContactPersonPhone());
            supplierList.add(supplierInfo);
        }

        details.put("suppliers", supplierList);
        details.put("totalSuppliers", supplierList.size());

        // ✅ Add approval chain info
        List<DynamicRFQApproval> approvals = approvalRepository.findByRfqIdOrderBySequence(rfqId);
        List<Map<String, Object>> approvalChain = new ArrayList<>();
        
        for (DynamicRFQApproval approval : approvals) {
            Map<String, Object> approvalInfo = new HashMap<>();
            approvalInfo.put("levelName", approval.getHierarchyLevel().getLevelName());
            approvalInfo.put("levelOrder", approval.getHierarchyLevel().getLevelOrder());
            approvalInfo.put("status", approval.getStatus().toString());
            approvalInfo.put("sequence", approval.getSequenceOrder());
            
            if (approval.getApproverUser() != null) {
                approvalInfo.put("approverName", approval.getApproverUser().getFullName());
                approvalInfo.put("approverEmail", approval.getApproverUser().getEmail());
            }
            
            approvalInfo.put("comments", approval.getComments());
            approvalInfo.put("actionDate", approval.getActionDate());
            
            approvalChain.add(approvalInfo);
        }

        details.put("approvalChain", approvalChain);

        System.out.println("[✓] RFQ details compiled");
        System.out.println("=".repeat(80));

        return details;
    }

    // ==================== SUPPLIER STATISTICS ====================

    /**
     * ✅ Get supplier statistics for an RFQ
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierStatistics(Long rfqId) {
        System.out.println("=".repeat(80));
        System.out.println("[SUPPLIER STATS] RFQ ID: " + rfqId);

        RFQ rfq = rfqRepository.findByIdWithSuppliers(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        Map<String, Object> stats = new HashMap<>();

        // Selected suppliers count
        int selectedSuppliers = rfq.getSelectedSuppliers() != null ? 
                               rfq.getSelectedSuppliers().size() : 0;
        stats.put("selectedSuppliers", selectedSuppliers);

        // Supplier details
        List<Map<String, String>> supplierList = new ArrayList<>();
        if (rfq.getSelectedSuppliers() != null) {
            for (Supplier supplier : rfq.getSelectedSuppliers()) {
                Map<String, String> supplierInfo = new HashMap<>();
                supplierInfo.put("id", supplier.getId().toString());
                supplierInfo.put("companyName", supplier.getCompanyName());
                supplierInfo.put("contactEmail", supplier.getContactPersonEmail());
                supplierList.add(supplierInfo);
            }
        }
        stats.put("suppliers", supplierList);

        System.out.println("  Selected Suppliers: " + selectedSuppliers);
        System.out.println("[✓] Supplier statistics compiled");
        System.out.println("=".repeat(80));

        return stats;
    }

    // ==================== HELPER METHODS ====================

    /**
     * ✅ Build RFQ summary with all needed fields
     */
    private Map<String, Object> buildRFQSummary(RFQ rfq) {
        Map<String, Object> summary = new HashMap<>();
        
        // Basic info
        summary.put("id", rfq.getId());
        summary.put("rfqNumber", rfq.getRfqNumber());
        summary.put("rfqTitle", rfq.getRfqTitle());
        summary.put("rfqDescription", rfq.getRfqDescription());
        
        // Dates
        summary.put("requestedDate", rfq.getCreatedAt());
        summary.put("releaseDate", rfq.getIssueDate());
        summary.put("dueDate", rfq.getDueDate());
        
        // Status
        summary.put("status", rfq.getStatus().toString());
        summary.put("approvalStatus", rfq.getApprovalStatus() != null ? 
                                     rfq.getApprovalStatus().toString() : "N/A");
        
        // Counts (safely access collections)
        try {
            summary.put("suppliersSelected", rfq.getSelectedSuppliers() != null ? 
                                            rfq.getSelectedSuppliers().size() : 0);
            summary.put("totalSuppliers", rfq.getSelectedSuppliers() != null ? 
                                         rfq.getSelectedSuppliers().size() : 0);
            summary.put("itemsCount", rfq.getItems() != null ? rfq.getItems().size() : 0);
        } catch (Exception e) {
            summary.put("suppliersSelected", 0);
            summary.put("totalSuppliers", 0);
            summary.put("itemsCount", 0);
        }
        
        // Responses received (placeholder - you can expand this)
        summary.put("responsesReceived", 0);
        
        // Buyer info
        if (rfq.getBuyer() != null) {
            summary.put("buyerName", rfq.getBuyer().getCompanyName());
        }
        
        // Created by
        if (rfq.getCreatedByUser() != null) {
            summary.put("createdBy", rfq.getCreatedByUser().getEmail());
        }
        
        return summary;
    }
}