

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.DynamicRFQApprovalResponse;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DynamicRFQApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRFQApprovalService.class);

    @Autowired
    private DynamicRFQApprovalRepository approvalRepository;

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private HierarchyLevelRepository hierarchyLevelRepository;

    @Autowired
    private HierarchyUserRepository hierarchyUserRepository;

    // ==================== INITIATE WORKFLOW ====================

    /**
     * ✅ Create approval ONLY for FIRST level (HIGHEST level_order)
     * Example: If levels are CEO(100), COO(80), Manager(30), Employee(10)
     * This creates approval ONLY for CEO(100)
     */
    public String initiateApprovalWorkflow(Long rfqId, Long creatorUserId) {
        logger.info("🔵 [INITIATE WORKFLOW] RFQ ID: {}, Creator User ID: {}", rfqId, creatorUserId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }

        logger.info("  📌 Company: {}", companyName);

        // ✅ Get levels in DESCENDING order: 100 → 80 → 30 → 10
        List<HierarchyLevel> levels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

        if (levels.isEmpty()) {
            throw new RuntimeException("No active hierarchy levels found for company: " + companyName);
        }

        logger.info("  📌 Found {} hierarchy levels", levels.size());
        logger.info("  📌 First level (highest): {} (order: {})", 
            levels.get(0).getLevelName(), levels.get(0).getLevelOrder());

        // ✅ CREATE APPROVAL ONLY FOR FIRST LEVEL (highest level_order)
        HierarchyLevel firstLevel = levels.get(0);
        
        List<HierarchyUser> usersAtFirstLevel = hierarchyUserRepository
                .findByHierarchyLevelAndIsActiveOrderByIdAsc(firstLevel, true);

        if (usersAtFirstLevel.isEmpty()) {
            throw new RuntimeException("No active users found at level: " + firstLevel.getLevelName());
        }

        HierarchyUser firstApprover = usersAtFirstLevel.get(0);

        DynamicRFQApproval approval = new DynamicRFQApproval();
        approval.setRfq(rfq);
        approval.setHierarchyLevel(firstLevel);
        approval.setApproverUser(firstApprover);
        approval.setStatus(ApprovalActionStatus.PENDING);
        approval.setSequenceOrder(1);
        approval.setCreatedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        logger.info("  ✅ Created approval: Level={} (order={}), Approver={}", 
            firstLevel.getLevelName(), firstLevel.getLevelOrder(), firstApprover.getEmail());

        rfq.setStatus(RFQStatus.AWAITING_APPROVAL);
        rfq.setApprovalStatus(ApprovalStatus.PENDING);
        rfqRepository.save(rfq);

        logger.info("✅ [WORKFLOW INITIATED] First level approval created");

        return "Approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
    }

    // ==================== APPROVE RFQ ====================

    /**
     * ✅ After approval, create NEXT level's approval (next lower level_order)
     * Example: CEO(100) approves → create approval for COO(80)
     *          COO(80) approves → create approval for Manager(30)
     *          Manager(30) approves → create approval for Employee(10)
     *          Employee(10) approves → PUBLISHED
     */
    public String approveRFQ(Long rfqId, Long approverId, String comments) {
        logger.info("🟢 [APPROVE RFQ] RFQ ID: {}, Approver ID: {}", rfqId, approverId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        Optional<DynamicRFQApproval> currentApprovalOpt = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);

        if (currentApprovalOpt.isEmpty()) {
            throw new RuntimeException("No pending approval found for RFQ ID: " + rfqId);
        }

        DynamicRFQApproval currentApproval = currentApprovalOpt.get();

        logger.info("  📌 Current Level: {} (order: {}), Sequence: {}", 
            currentApproval.getHierarchyLevel().getLevelName(),
            currentApproval.getHierarchyLevel().getLevelOrder(),
            currentApproval.getSequenceOrder());

        if (!currentApproval.getApproverUser().getId().equals(approverId)) {
            throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");
        }

        // Mark current level as approved
        currentApproval.setStatus(ApprovalActionStatus.APPROVED);
        currentApproval.setComments(comments);
        currentApproval.setActionDate(LocalDateTime.now());
        approvalRepository.save(currentApproval);

        logger.info("  ✅ Level approved: {} (order: {})", 
            currentApproval.getHierarchyLevel().getLevelName(),
            currentApproval.getHierarchyLevel().getLevelOrder());

        // ✅ GET NEXT LEVEL (next lower level_order) AND CREATE ITS APPROVAL
        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }

        // ✅ Get all levels in DESCENDING order: 100 → 80 → 30 → 10
        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

        // Find current level's position
        int currentLevelIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getId().equals(currentApproval.getHierarchyLevel().getId())) {
                currentLevelIndex = i;
                break;
            }
        }

        logger.info("  📌 Current level index: {} out of {}", currentLevelIndex, allLevels.size());

        // Check if there's a next level (next in descending order)
        if (currentLevelIndex != -1 && currentLevelIndex < allLevels.size() - 1) {
            // ✅ CREATE APPROVAL FOR NEXT LEVEL
            HierarchyLevel nextLevel = allLevels.get(currentLevelIndex + 1);
            
            logger.info("  📌 Next level: {} (order: {})", nextLevel.getLevelName(), nextLevel.getLevelOrder());
            
            List<HierarchyUser> usersAtNextLevel = hierarchyUserRepository
                    .findByHierarchyLevelAndIsActiveOrderByIdAsc(nextLevel, true);

            if (!usersAtNextLevel.isEmpty()) {
                HierarchyUser nextApprover = usersAtNextLevel.get(0);

                DynamicRFQApproval nextApproval = new DynamicRFQApproval();
                nextApproval.setRfq(rfq);
                nextApproval.setHierarchyLevel(nextLevel);
                nextApproval.setApproverUser(nextApprover);
                nextApproval.setStatus(ApprovalActionStatus.PENDING);
                nextApproval.setSequenceOrder(currentApproval.getSequenceOrder() + 1);
                nextApproval.setCreatedAt(LocalDateTime.now());

                approvalRepository.save(nextApproval);

                logger.info("  ✅ Created next level approval: Level={} (order={}), Approver={}", 
                    nextLevel.getLevelName(), nextLevel.getLevelOrder(), nextApprover.getEmail());

                return "RFQ approved at " + currentApproval.getHierarchyLevel().getLevelName() + 
                       " level. Forwarded to " + nextLevel.getLevelName() + " for approval.";
            } else {
                logger.warn("  ⚠️ No users found at next level: {}", nextLevel.getLevelName());
            }
        }

        // ✅ All levels complete - publish RFQ
        rfq.setStatus(RFQStatus.PUBLISHED);
        rfq.setApprovalStatus(ApprovalStatus.APPROVED);
        rfq.setApprovalDate(LocalDateTime.now());
        rfqRepository.save(rfq);

        logger.info("✅ [ALL APPROVALS COMPLETE] RFQ Published: {}", rfq.getRfqNumber());
        return "RFQ approved at all levels and published successfully";
    }

    // ==================== REJECT RFQ ====================

    public String rejectRFQ(Long rfqId, Long rejectorId, String comments) {
        logger.info("🔴 [REJECT RFQ] RFQ ID: {}, Rejector ID: {}", rfqId, rejectorId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        Optional<DynamicRFQApproval> currentApprovalOpt = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);

        if (currentApprovalOpt.isEmpty()) {
            throw new RuntimeException("No pending approval found for RFQ ID: " + rfqId);
        }

        DynamicRFQApproval currentApproval = currentApprovalOpt.get();

        if (!currentApproval.getApproverUser().getId().equals(rejectorId)) {
            throw new RuntimeException("User " + rejectorId + " is not authorized to reject at this level");
        }

        currentApproval.setStatus(ApprovalActionStatus.REJECTED);
        currentApproval.setComments(comments);
        currentApproval.setActionDate(LocalDateTime.now());
        approvalRepository.save(currentApproval);

        rfq.setStatus(RFQStatus.DRAFT);
        rfq.setApprovalStatus(ApprovalStatus.REJECTED);
        rfqRepository.save(rfq);

        logger.info("✅ [RFQ REJECTED] Returned to DRAFT for revision");

        return "RFQ rejected at " + currentApproval.getHierarchyLevel().getLevelName() + " level. Returned to DRAFT.";
    }

    // ==================== RESUBMIT RFQ ====================

    public String resubmitRFQ(Long rfqId, Long resubmitterId) {
        logger.info("🔄 [RESUBMIT RFQ] RFQ ID: {}, Resubmitter ID: {}", rfqId, resubmitterId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT RFQs can be resubmitted");
        }

        List<DynamicRFQApproval> oldApprovals = 
            approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);
        approvalRepository.deleteAll(oldApprovals);

        logger.info("  🗑️ Deleted {} old approval records", oldApprovals.size());

        return initiateApprovalWorkflow(rfqId, resubmitterId);
    }

    // ==================== GET APPROVAL HISTORY ====================

    @Transactional(readOnly = true)
    public List<DynamicRFQApprovalResponse> getApprovalHistory(Long rfqId) {
        logger.info("📜 [GET APPROVAL HISTORY] RFQ ID: {}", rfqId);

        List<DynamicRFQApproval> approvals = 
            approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);

        return approvals.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== GET CURRENT PENDING APPROVAL ====================

    @Transactional(readOnly = true)
    public Optional<DynamicRFQApprovalResponse> getCurrentPendingApproval(Long rfqId) {
        logger.info("🔍 [GET CURRENT PENDING] RFQ ID: {}", rfqId);

        Optional<DynamicRFQApproval> approval = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);

        return approval.map(this::toResponse);
    }

    // ==================== GET PENDING APPROVALS FOR USER ====================

    @Transactional(readOnly = true)
    public List<DynamicRFQApprovalResponse> getPendingApprovalsForUser(Long userId) {
        logger.info("📋 [GET PENDING FOR USER] User ID: {}", userId);

        List<DynamicRFQApproval> approvals = 
            approvalRepository.findPendingApprovalsByUser(userId);

        logger.info("  ✅ Found {} pending approvals", approvals.size());

        return approvals.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== GET PENDING COUNT ====================

    @Transactional(readOnly = true)
    public Long getPendingApprovalCount(Long userId) {
        logger.info("🔢 [GET PENDING COUNT] User ID: {}", userId);

        Long count = approvalRepository.countPendingApprovalsByUser(userId);

        logger.info("  ✅ Pending count: {}", count);

        return count;
    }

    // ==================== HELPER METHODS ====================

    private DynamicRFQApprovalResponse toResponse(DynamicRFQApproval approval) {
        DynamicRFQApprovalResponse response = new DynamicRFQApprovalResponse();
        response.setId(approval.getId());
        response.setRfqId(approval.getRfq().getId());
        response.setRfqNumber(approval.getRfq().getRfqNumber());
        response.setRfqTitle(approval.getRfq().getRfqTitle());
        response.setRfqDescription(approval.getRfq().getRfqDescription());
        response.setHierarchyLevelId(approval.getHierarchyLevel().getId());
        response.setHierarchyLevelName(approval.getHierarchyLevel().getLevelName());
        response.setHierarchyLevelOrder(approval.getHierarchyLevel().getLevelOrder());
        response.setStatus(approval.getStatus().name());
        response.setComments(approval.getComments());
        response.setSequenceOrder(approval.getSequenceOrder());
        response.setCreatedAt(approval.getCreatedAt());
        response.setActionDate(approval.getActionDate());

        if (approval.getApproverUser() != null) {
            response.setApproverUserId(approval.getApproverUser().getId());
            response.setApproverUserName(approval.getApproverUser().getFullName());
            response.setApproverUserEmail(approval.getApproverUser().getEmail());
        }

        if (approval.getRfq().getBuyer() != null) {
            response.setBuyerName(approval.getRfq().getBuyer().getCompanyName());
        }

        return response;
    }
}