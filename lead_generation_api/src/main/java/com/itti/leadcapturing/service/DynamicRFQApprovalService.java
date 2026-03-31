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
import java.util.Set; 
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

    @Autowired
    private EmailService emailService;

    // ==================== INITIATE WORKFLOW ====================

    public String initiateApprovalWorkflow(Long rfqId, Long creatorUserId) {
        logger.info("🔵 [INITIATE WORKFLOW] RFQ ID: {}, Creator User ID: {}", rfqId, creatorUserId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }

        logger.info("  📌 Company: {}", companyName);

        List<HierarchyLevel> levels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

        if (levels.isEmpty()) {
            throw new RuntimeException("No active hierarchy levels found for company: " + companyName);
        }

        logger.info("  📌 Found {} hierarchy levels", levels.size());
        logger.info("  📌 First level (highest): {} (order: {})", 
            levels.get(0).getLevelName(), levels.get(0).getLevelOrder());

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

        // ✅ SEND EMAIL TO FIRST APPROVER
        try {
            emailService.sendApprovalPendingEmail(
                rfq,
                firstApprover,
                firstLevel,
                1,
                levels.size()
            );
            logger.info("  📧 Email sent to first approver: {}", firstApprover.getEmail());
        } catch (Exception e) {
            logger.error("  ❌ Failed to send email to first approver", e);
        }

        logger.info("✅ [WORKFLOW INITIATED] First level approval created");

        return "Approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
    }

    // ==================== CHECK IF USER IS LAST APPROVER ====================
    
    @Transactional(readOnly = true)
    public boolean isLastApprover(Long rfqId, Long userId) {
        logger.info("🔍 [CHECK LAST APPROVER] RFQ ID: {}, User ID: {}", rfqId, userId);
        
        Optional<DynamicRFQApproval> currentApprovalOpt = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);
        
        if (currentApprovalOpt.isEmpty()) {
            logger.warn("  ⚠️ No pending approval found");
            return false;
        }
        
        DynamicRFQApproval currentApproval = currentApprovalOpt.get();
        
        if (!currentApproval.getApproverUser().getId().equals(userId)) {
            logger.warn("  ⚠️ User {} is not the current approver", userId);
            return false;
        }
        
        RFQ rfq = currentApproval.getRfq();
        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }
        
        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
        
        if (allLevels.isEmpty()) {
            logger.warn("  ⚠️ No hierarchy levels found");
            return false;
        }
        
        HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1);
        Integer currentLevelOrder = currentApproval.getHierarchyLevel().getLevelOrder();
        Integer lastLevelOrder = lastLevel.getLevelOrder();
        
        boolean isLast = currentLevelOrder.equals(lastLevelOrder);
        
        logger.info("  📊 Current Level: {} (order: {})", 
                   currentApproval.getHierarchyLevel().getLevelName(), 
                   currentLevelOrder);
        logger.info("  📊 Last Level: {} (order: {})", 
                   lastLevel.getLevelName(), 
                   lastLevelOrder);
        logger.info("  ✅ Is Last Approver: {}", isLast);
        
        return isLast;
    }
    
    // ==================== APPROVE WITH DATES ====================
    
// ==================== ✅ UPDATED: APPROVE WITH DATES + DOWNLOAD PERMISSION ====================

/**
 * Approve RFQ with optional dates and download permission
 * @param allowSupplierDownload - If true, suppliers can download RFQ as PDF
 */
public String approveWithDates(Long rfqId, Long approverId, String comments,
                               LocalDateTime rfqDueDate, LocalDateTime rfqDeliveryDate,
                               Boolean allowSupplierDownload) {
    logger.info("🟢 [APPROVE WITH DATES + DOWNLOAD PERMISSION] RFQ ID: {}, Approver ID: {}", rfqId, approverId);
    logger.info("  📥 Allow Supplier Download: {}", allowSupplierDownload);
    
    RFQ rfq = rfqRepository.findById(rfqId)
            .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));
    
    Optional<DynamicRFQApproval> currentApprovalOpt = 
        approvalRepository.findFirstCurrentPendingApproval(rfqId);
    
    if (currentApprovalOpt.isEmpty()) {
        throw new RuntimeException("No pending approval found for RFQ ID: " + rfqId);
    }
    
    DynamicRFQApproval currentApproval = currentApprovalOpt.get();
    
    if (!currentApproval.getApproverUser().getId().equals(approverId)) {
        throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");
    }
    
    HierarchyUser currentApprover = currentApproval.getApproverUser();
    
    boolean isLastLevel = isLastApprover(rfqId, approverId);
    
    if (isLastLevel) {
        logger.info("  📌 This is the LAST approval level - dates required");
        
        if (rfqDueDate == null || rfqDeliveryDate == null) {
            throw new RuntimeException("RFQ Due Date and Expected Delivery Date are required for final approval");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (rfqDueDate.isBefore(now)) {
            throw new RuntimeException("RFQ Due Date cannot be in the past");
        }
        
        if (rfqDeliveryDate.isBefore(now)) {
            throw new RuntimeException("Expected Delivery Date cannot be in the past");
        }
        
        if (rfqDeliveryDate.isBefore(rfqDueDate)) {
            throw new RuntimeException("Expected Delivery Date cannot be earlier than RFQ Due Date");
        }
        
        logger.info("  ✅ Date validation passed");
        logger.info("     RFQ Due Date: {}", rfqDueDate);
        logger.info("     Delivery Date: {}", rfqDeliveryDate);
        
        currentApproval.setRfqDueDate(rfqDueDate);
        currentApproval.setRfqDeliveryDate(rfqDeliveryDate);
        
        rfq.setDueDate(rfqDueDate);
        rfq.setItemRequiredDate(rfqDeliveryDate);
        
        logger.info("  ✅ RFQ dates updated in RFQ entity");
    } else {
        logger.info("  📌 Not the last level - dates not required");
        
        if (rfqDueDate != null || rfqDeliveryDate != null) {
            logger.warn("  ⚠️ Dates provided but not at last level - ignoring");
        }
    }
    
    // ✅ NEW: Set download permission
    // Default to true if not provided
    if (allowSupplierDownload == null) {
        allowSupplierDownload = true;
        logger.info("  📥 Download permission not specified - defaulting to TRUE");
    }
    
    rfq.setAllowSupplierDownload(allowSupplierDownload);
    logger.info("  ✅ Supplier download permission set to: {}", allowSupplierDownload);
    
    currentApproval.setStatus(ApprovalActionStatus.APPROVED);
    currentApproval.setComments(comments);
    currentApproval.setActionDate(LocalDateTime.now());
    approvalRepository.save(currentApproval);
    
    logger.info("  ✅ Level approved: {} (order: {})", 
               currentApproval.getHierarchyLevel().getLevelName(),
               currentApproval.getHierarchyLevel().getLevelOrder());
    
    if (isLastLevel) {
        rfq.setStatus(RFQStatus.PUBLISHED);
        rfq.setApprovalStatus(ApprovalStatus.APPROVED);
        rfq.setApprovalDate(LocalDateTime.now());
        rfqRepository.save(rfq);
        
        logger.info("✅ [RFQ PUBLISHED WITH DATES AND DOWNLOAD PERMISSION]");
        logger.info("   RFQ Number: {}", rfq.getRfqNumber());
        logger.info("   Due Date: {}", rfq.getDueDate());
        logger.info("   Delivery Date: {}", rfq.getItemRequiredDate());
        logger.info("   Download Enabled: {}", rfq.getAllowSupplierDownload());
        logger.info("   Status: PUBLISHED");
        
        // ✅ SEND PUBLICATION EMAILS
        try {
            logger.info("  📧 [EMAIL PREP] Preparing publication emails...");
            
            // 🔥 CRITICAL: Force load all lazy collections BEFORE async call
            Set<Supplier> suppliers = rfq.getSelectedSuppliers();
            if (suppliers != null) {
                int suppliersCount = suppliers.size();
                logger.info("  📧 Loaded {} suppliers", suppliersCount);
                
                for (Supplier supplier : suppliers) {
                    supplier.getCompanyName();
                    supplier.getContactPersonEmail();
                }
            }
            
            List<RFQItem> items = rfq.getItems();
            if (items != null) {
                int itemsCount = items.size();
                logger.info("  📧 Loaded {} items", itemsCount);
            }
            
            Set<User> approvers = rfq.getApprovers();
            if (approvers != null) {
                int approversCount = approvers.size();
                logger.info("  📧 Loaded {} approvers", approversCount);
                
                for (User approver : approvers) {
                    approver.getEmail();
                    approver.getFirstName();
                }
            }
            
            if (rfq.getBuyer() != null) {
                rfq.getBuyer().getCompanyName();
                logger.info("  📧 Loaded buyer data");
            }
            
            if (rfq.getCreatedByUser() != null) {
                rfq.getCreatedByUser().getEmail();
                rfq.getCreatedByUser().getFirstName();
                logger.info("  📧 Loaded creator data");
            }
            
            logger.info("  📧 All data loaded. Calling async email service...");
            
            emailService.sendRFQPublishedEmail(
                rfq,
                currentApprover,
                suppliers
            );
            
            logger.info("  📧 Publication email service called (async processing)");
            
        } catch (Exception e) {
            logger.error("  ❌ Failed to send publication emails", e);
            e.printStackTrace();
        }
        
        String downloadMessage = allowSupplierDownload 
            ? "Suppliers can download RFQ as PDF." 
            : "PDF download disabled for suppliers.";
        
        return "RFQ approved with dates and published successfully to suppliers. " + downloadMessage;
        
    } else {
        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }
        
        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
        
        int currentLevelIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getId().equals(currentApproval.getHierarchyLevel().getId())) {
                currentLevelIndex = i;
                break;
            }
        }
        
        if (currentLevelIndex != -1 && currentLevelIndex < allLevels.size() - 1) {
            HierarchyLevel nextLevel = allLevels.get(currentLevelIndex + 1);
            
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
                
                // ✅ Save RFQ with download permission (even if not last level)
                rfqRepository.save(rfq);
                
                logger.info("  ✅ Forwarded to next level: {} (order: {})", 
                           nextLevel.getLevelName(), 
                           nextLevel.getLevelOrder());
                logger.info("  ✅ Download permission saved: {}", allowSupplierDownload);
                
                // ✅ SEND EMAIL TO NEXT APPROVER
                try {
                    emailService.sendApprovalForwardedEmail(
                        rfq,
                        currentApprover,
                        nextApprover,
                        nextLevel,
                        comments
                    );
                    logger.info("  📧 Email sent to next approver: {}", nextApprover.getEmail());
                } catch (Exception e) {
                    logger.error("  ❌ Failed to send email to next approver", e);
                }
                
                boolean nextIsLast = nextLevel.getLevelOrder().equals(
                    allLevels.get(allLevels.size() - 1).getLevelOrder()
                );
                
                String message = "RFQ approved at " + currentApproval.getHierarchyLevel().getLevelName() + 
                               " level. Forwarded to " + nextLevel.getLevelName() + " for approval.";
                
                if (nextIsLast) {
                    message += " (FINAL APPROVAL LEVEL - Dates will be required)";
                }
                
                return message;
            }
        }
        
        rfq.setStatus(RFQStatus.PUBLISHED);
        rfq.setApprovalStatus(ApprovalStatus.APPROVED);
        rfq.setApprovalDate(LocalDateTime.now());
        rfqRepository.save(rfq);
        
        return "RFQ approved at all levels and published";
    }
}

// ==================== APPROVE RFQ (LEGACY) - OVERLOAD WITH DOWNLOAD PERMISSION ====================

/**
 * Legacy approve method - now with download permission support
 */
public String approveRFQ(Long rfqId, Long approverId, String comments) {
    logger.info("🟢 [APPROVE RFQ - LEGACY] RFQ ID: {}, Approver ID: {}", rfqId, approverId);
    logger.warn("  ⚠️ Using legacy approve method - defaulting download permission to TRUE");
    
    // Default download permission to true for backward compatibility
    return approveWithDates(rfqId, approverId, comments, null, null, true);
}
    // ==================== REJECT RFQ ====================

    public String rejectRFQ(Long rfqId, Long rejectorId, String rejectRemarks) {
        logger.info("🔴 [REJECT RFQ - PERMANENT] RFQ ID: {}, Rejector ID: {}", rfqId, rejectorId);

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

        HierarchyUser rejector = hierarchyUserRepository.findById(rejectorId)
                .orElseThrow(() -> new RuntimeException("Rejector user not found"));

        String remarks = (rejectRemarks != null && !rejectRemarks.trim().isEmpty()) 
                        ? rejectRemarks.trim() 
                        : "No remarks provided";
        
        logger.info("  📝 Reject Remarks: '{}'", remarks);

        currentApproval.setStatus(ApprovalActionStatus.REJECTED);
        currentApproval.setComments(remarks);
        currentApproval.setRejectRemarks(remarks);
        currentApproval.setRejectedByUser(rejector);
        currentApproval.setRejectDate(LocalDateTime.now());
        currentApproval.setActionDate(LocalDateTime.now());
        
        currentApproval = approvalRepository.saveAndFlush(currentApproval);
        
        logger.info("  ✅ Rejection recorded");

        String rejectInfo = String.format("Rejected by %s (%s) on %s. Reason: %s",
            rejector.getFullName(),
            currentApproval.getHierarchyLevel().getLevelName(),
            LocalDateTime.now(),
            remarks);
        
        rfq.setStatus(RFQStatus.CLOSED);
        rfq.setApprovalStatus(ApprovalStatus.REJECTED);
        rfq.setApprovalComments(rejectInfo.length() > 500 ? rejectInfo.substring(0, 500) : rejectInfo);
        rfq.setUpdatedAt(LocalDateTime.now());
        rfqRepository.saveAndFlush(rfq);

        // ✅ SEND REJECTION EMAIL
        try {
            emailService.sendRFQRejectedEmail(rfq, rejector, remarks);
            logger.info("  📧 Rejection email sent to creator");
        } catch (Exception e) {
            logger.error("  ❌ Failed to send rejection email", e);
        }

        logger.info("✅ [RFQ PERMANENTLY REJECTED]");
        
        return String.format("RFQ permanently rejected by %s at %s level. Reason: %s",
            rejector.getFullName(),
            currentApproval.getHierarchyLevel().getLevelName(),
            remarks);
    }

    // ==================== RETURN FOR REVISION ====================

    public String returnForRevision(Long rfqId, Long approverId, String revisionComments) {
        logger.info("🔄 [RETURN FOR REVISION] RFQ ID: {}, Approver ID: {}", rfqId, approverId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        Optional<DynamicRFQApproval> currentApprovalOpt = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);

        if (currentApprovalOpt.isEmpty()) {
            throw new RuntimeException("No pending approval found for RFQ ID: " + rfqId);
        }

        DynamicRFQApproval currentApproval = currentApprovalOpt.get();

        if (!currentApproval.getApproverUser().getId().equals(approverId)) {
            throw new RuntimeException("User " + approverId + " is not authorized to return for revision at this level");
        }

        HierarchyUser returner = currentApproval.getApproverUser();

        String remarks = (revisionComments != null && !revisionComments.trim().isEmpty()) 
                        ? revisionComments.trim() 
                        : "Revision needed";
        
        logger.info("  📝 Revision Comments: '{}'", remarks);

        currentApproval.setStatus(ApprovalActionStatus.RESUBMITTED);
        currentApproval.setComments(remarks);
        currentApproval.setActionDate(LocalDateTime.now());
        approvalRepository.save(currentApproval);
        
        rfq.setStatus(RFQStatus.RETURNED_FOR_REVISION);
        rfq.setApprovalStatus(ApprovalStatus.PENDING);
        rfq.setApprovalComments(remarks.length() > 500 ? remarks.substring(0, 500) : remarks);
        rfq.setUpdatedAt(LocalDateTime.now());
        rfqRepository.save(rfq);

        // ✅ SEND REVISION EMAIL
        try {
            emailService.sendRFQReturnedForRevisionEmail(rfq, returner, remarks);
            logger.info("  📧 Revision email sent to creator");
        } catch (Exception e) {
            logger.error("  ❌ Failed to send revision email", e);
        }

        logger.info("✅ [RFQ RETURNED FOR REVISION]");
        
        return "RFQ returned for revision by " + currentApproval.getHierarchyLevel().getLevelName() + 
               ". Buyer can now make changes and resubmit. Revision request: " + remarks;
    }

    // ==================== RESUBMIT RFQ ====================

    public String resubmitRFQ(Long rfqId, Long resubmitterId) {
        logger.info("🔄 [RESUBMIT RFQ] RFQ ID: {}, Resubmitter ID: {}", rfqId, resubmitterId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        if (rfq.getStatus() != RFQStatus.RETURNED_FOR_REVISION) {
            throw new RuntimeException("Only RFQs with status RETURNED_FOR_REVISION can be resubmitted. Current status: " + rfq.getStatus());
        }

        if (!rfq.getCreatedByUser().getId().equals(resubmitterId)) {
            throw new RuntimeException("Only the original RFQ creator can resubmit");
        }

        List<DynamicRFQApproval> allApprovals = 
            approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);
        
        if (!allApprovals.isEmpty()) {
            approvalRepository.deleteAll(allApprovals);
            logger.info("  🗑️ Deleted {} approval records", allApprovals.size());
        }

        rfq.setApprovalComments(null);
        rfq.setUpdatedAt(LocalDateTime.now());
        rfqRepository.save(rfq);

        String result = initiateApprovalWorkflow(rfqId, resubmitterId);
        
        logger.info("✅ [RFQ RESUBMITTED]");
        return "RFQ resubmitted successfully after revision. " + result;
    }

    // ==================== HOLD RFQ ====================

    public String holdRFQ(Long rfqId, Long holderId, String holdRemarks) {
        logger.info("⏸️ [HOLD RFQ] RFQ ID: {}, Holder ID: {}", rfqId, holderId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        Optional<DynamicRFQApproval> currentApprovalOpt = 
            approvalRepository.findFirstCurrentPendingApproval(rfqId);

        if (currentApprovalOpt.isEmpty()) {
            throw new RuntimeException("No pending approval found for RFQ ID: " + rfqId);
        }

        DynamicRFQApproval currentApproval = currentApprovalOpt.get();

        if (!currentApproval.getApproverUser().getId().equals(holderId)) {
            throw new RuntimeException("Only the current approver can hold this RFQ");
        }

        if (holdRemarks == null || holdRemarks.trim().isEmpty()) {
            throw new RuntimeException("Hold remarks are required");
        }

        HierarchyUser holder = hierarchyUserRepository.findById(holderId)
                .orElseThrow(() -> new RuntimeException("Holder user not found"));

        currentApproval.setStatus(ApprovalActionStatus.HOLD);
        currentApproval.setHeldByUser(holder);
        currentApproval.setHoldRemarks(holdRemarks);
        currentApproval.setHoldDate(LocalDateTime.now());
        currentApproval.setActionDate(LocalDateTime.now());
        approvalRepository.save(currentApproval);

        String holdInfo = String.format("On HOLD by %s (%s) on %s. Reason: %s",
            holder.getFullName(),
            currentApproval.getHierarchyLevel().getLevelName(),
            LocalDateTime.now(),
            holdRemarks);
        
        rfq.setStatus(RFQStatus.HOLD);
        rfq.setApprovalStatus(ApprovalStatus.PENDING);
        rfq.setApprovalComments(holdInfo.length() > 500 ? holdInfo.substring(0, 500) : holdInfo);
        rfq.setUpdatedAt(LocalDateTime.now());
        rfqRepository.save(rfq);

        // ✅ SEND HOLD EMAIL
        try {
            emailService.sendRFQOnHoldEmail(rfq, holder, holdRemarks);
            logger.info("  📧 Hold email sent to creator");
        } catch (Exception e) {
            logger.error("  ❌ Failed to send hold email", e);
        }

        logger.info("✅ [RFQ PUT ON HOLD]");

        return String.format("RFQ put on HOLD by %s at %s level. Reason: %s",
            holder.getFullName(),
            currentApproval.getHierarchyLevel().getLevelName(),
            holdRemarks);
    }

    // ==================== RELEASE HOLD ====================

    public String releaseHold(Long rfqId, Long releaserId, String releaseRemarks) {
        logger.info("▶️ [RELEASE HOLD] RFQ ID: {}, Releaser ID: {}", rfqId, releaserId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        if (!RFQStatus.HOLD.equals(rfq.getStatus())) {
            throw new RuntimeException("RFQ is not on HOLD. Current status: " + rfq.getStatus());
        }

        Optional<DynamicRFQApproval> holdApprovalOpt = 
            approvalRepository.findHoldApprovalByRfqId(rfqId);

        if (holdApprovalOpt.isEmpty()) {
            throw new RuntimeException("No HOLD approval found for RFQ ID: " + rfqId);
        }

        DynamicRFQApproval holdApproval = holdApprovalOpt.get();

        HierarchyUser releaser = hierarchyUserRepository.findById(releaserId)
                .orElseThrow(() -> new RuntimeException("Releaser user not found"));

        boolean canRelease = false;
        String releaseAuthority = "";
        HierarchyLevel releaserLevel = releaser.getHierarchyLevel();

        if (holdApproval.getHeldByUser() != null && 
            holdApproval.getHeldByUser().getId().equals(releaserId)) {
            canRelease = true;
            releaseAuthority = "original holder";
        } 
        else if (releaserLevel != null && holdApproval.getHierarchyLevel() != null) {
            String holderCompany = holdApproval.getHierarchyLevel().getCompanyName();
            String releaserCompany = releaserLevel.getCompanyName();
            
            if (holderCompany.equals(releaserCompany)) {
                if (releaserLevel.getLevelOrder() < holdApproval.getHierarchyLevel().getLevelOrder()) {
                    canRelease = true;
                    releaseAuthority = "higher hierarchy level";
                } else if (releaserLevel.getLevelOrder().equals(holdApproval.getHierarchyLevel().getLevelOrder())) {
                    canRelease = true;
                    releaseAuthority = "same hierarchy level";
                }
            }
        }

        if (!canRelease) {
            throw new RuntimeException("User not authorized to release this hold");
        }

        String companyName = rfq.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) {
            companyName = rfq.getBuyer().getCompanyName();
        }

        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

        HierarchyUser nextApprover;

        if (releaserLevel.getLevelOrder() < holdApproval.getHierarchyLevel().getLevelOrder()) {
            approvalRepository.delete(holdApproval);
            
            List<HierarchyUser> usersAtReleaserLevel = hierarchyUserRepository
                    .findByHierarchyLevelAndIsActiveOrderByIdAsc(releaserLevel, true);

            if (usersAtReleaserLevel.isEmpty()) {
                throw new RuntimeException("No active users found at releaser's level");
            }

            nextApprover = usersAtReleaserLevel.get(0);

            DynamicRFQApproval newApproval = new DynamicRFQApproval();
            newApproval.setRfq(rfq);
            newApproval.setHierarchyLevel(releaserLevel);
            newApproval.setApproverUser(nextApprover);
            newApproval.setStatus(ApprovalActionStatus.PENDING);
            newApproval.setSequenceOrder(holdApproval.getSequenceOrder());
            newApproval.setCreatedAt(LocalDateTime.now());
            
            String combinedComments = String.format(
                "Released from HOLD by %s (%s - %s) on %s. Original hold by %s. %s",
                releaser.getFullName(),
                releaserLevel.getLevelName(),
                releaseAuthority,
                LocalDateTime.now(),
                holdApproval.getHeldByUser().getFullName(),
                releaseRemarks != null ? "Release reason: " + releaseRemarks : ""
            );
            newApproval.setComments(combinedComments);
            
            approvalRepository.save(newApproval);
        } else {
            holdApproval.setStatus(ApprovalActionStatus.PENDING);
            holdApproval.setReleasedByUser(releaser);
            holdApproval.setReleaseRemarks(releaseRemarks);
            holdApproval.setReleasedDate(LocalDateTime.now());
            
            String combinedComments = holdApproval.getComments();
            if (releaseRemarks != null && !releaseRemarks.trim().isEmpty()) {
                combinedComments = (combinedComments != null ? combinedComments + " | " : "") +
                                  String.format("RELEASED by %s (%s) on %s: %s", 
                                              releaser.getFullName(), 
                                              releaseAuthority,
                                              LocalDateTime.now(),
                                              releaseRemarks);
            }
            holdApproval.setComments(combinedComments);
            
            approvalRepository.save(holdApproval);
            nextApprover = holdApproval.getApproverUser();
        }

        rfq.setStatus(RFQStatus.AWAITING_APPROVAL);
        rfq.setApprovalComments(null);
        rfq.setUpdatedAt(LocalDateTime.now());
        rfqRepository.save(rfq);

        // ✅ SEND RELEASE EMAIL
        try {
            emailService.sendRFQHoldReleasedEmail(rfq, releaser, nextApprover, releaseRemarks);
            logger.info("  📧 Release email sent");
        } catch (Exception e) {
            logger.error("  ❌ Failed to send release email", e);
        }

        logger.info("✅ [HOLD RELEASED]");

        String currentLevelName = releaserLevel.getLevelOrder() < holdApproval.getHierarchyLevel().getLevelOrder()
            ? releaserLevel.getLevelName()
            : holdApproval.getHierarchyLevel().getLevelName();

        return String.format("Hold released by %s (%s). RFQ now at %s level for approval. %s",
            releaser.getFullName(),
            releaseAuthority,
            currentLevelName,
            releaseRemarks != null ? "Reason: " + releaseRemarks : "");
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

    // ==================== GET HOLD APPROVALS FOR USER ====================

    @Transactional(readOnly = true)
    public List<DynamicRFQApprovalResponse> getHoldApprovalsForUser(Long userId) {
        logger.info("⏸️ [GET HOLD APPROVALS FOR USER] User ID: {}", userId);

        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getHierarchyLevel() == null) {
            logger.warn("  ⚠️ User has no hierarchy level");
            return List.of();
        }

        Integer userLevelOrder = user.getHierarchyLevel().getLevelOrder();
        String companyName = user.getHierarchyLevel().getCompanyName();

        List<DynamicRFQApproval> approvals = 
            approvalRepository.findVisibleHoldApprovals(userLevelOrder, companyName);

        logger.info("  ✅ Found {} visible HOLD approvals", approvals.size());

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

    // ==================== GET HOLD COUNT ====================

    @Transactional(readOnly = true)
    public Long getHoldApprovalCount(Long userId) {
        logger.info("🔢 [GET HOLD COUNT] User ID: {}", userId);

        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getHierarchyLevel() == null) {
            return 0L;
        }

        Integer userLevelOrder = user.getHierarchyLevel().getLevelOrder();
        String companyName = user.getHierarchyLevel().getCompanyName();

        Long count = approvalRepository.countVisibleHoldApprovals(userLevelOrder, companyName);

        logger.info("  ✅ Hold count: {}", count);

        return count;
    }

    // ==================== HELPER METHOD ====================

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
        
        String comments = approval.getComments();
        if (comments == null || comments.trim().isEmpty()) {
            if (approval.getRejectRemarks() != null && !approval.getRejectRemarks().trim().isEmpty()) {
                comments = approval.getRejectRemarks();
            } else if (approval.getHoldRemarks() != null && !approval.getHoldRemarks().trim().isEmpty()) {
                comments = approval.getHoldRemarks();
            }
        }
        response.setComments(comments);
        
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

        if (approval.getHeldByUser() != null) {
            response.setHeldByUserId(approval.getHeldByUser().getId());
            response.setHeldByUserName(approval.getHeldByUser().getFullName());
            response.setHeldByUserEmail(approval.getHeldByUser().getEmail());
        }
        response.setHoldRemarks(approval.getHoldRemarks());
        response.setHoldDate(approval.getHoldDate());

        if (approval.getReleasedByUser() != null) {
            response.setReleasedByUserId(approval.getReleasedByUser().getId());
            response.setReleasedByUserName(approval.getReleasedByUser().getFullName());
            response.setReleasedByUserEmail(approval.getReleasedByUser().getEmail());
        }
        response.setReleaseRemarks(approval.getReleaseRemarks());
        response.setReleasedDate(approval.getReleasedDate());

        if (approval.getRejectedByUser() != null) {
            response.setRejectedByUserId(approval.getRejectedByUser().getId());
            response.setRejectedByUserName(approval.getRejectedByUser().getFullName());
            response.setRejectedByUserEmail(approval.getRejectedByUser().getEmail());
        }
        
        String rejectRemarks = approval.getRejectRemarks();
        if (rejectRemarks == null || rejectRemarks.trim().isEmpty()) {
            rejectRemarks = approval.getComments();
        }
        if (rejectRemarks == null || rejectRemarks.trim().isEmpty()) {
            rejectRemarks = "No remarks provided";
        }
        
        response.setRejectRemarks(rejectRemarks);
        response.setRejectDate(approval.getRejectDate());

        response.setHoldDisplayInfo(approval.getHoldDisplayInfo());
        response.setRejectDisplayInfo(approval.getRejectDisplayInfo());
        response.setIsOnHold(approval.isOnHold());
        response.setWasHeldAndReleased(approval.wasHeldAndReleased());
        response.setWasRejected(approval.wasRejected());

        return response;
    }
}