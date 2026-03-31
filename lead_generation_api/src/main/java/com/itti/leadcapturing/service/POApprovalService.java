
// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.dto.POApprovalResponse;
// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// @Transactional
// public class POApprovalService {

//     private static final Logger logger = LoggerFactory.getLogger(POApprovalService.class);

//     @Autowired private POApprovalRepository poApprovalRepository;
//     @Autowired private PurchaseOrderRepository purchaseOrderRepository;
//     @Autowired private HierarchyLevelRepository hierarchyLevelRepository;
//     @Autowired private HierarchyUserRepository hierarchyUserRepository;
//     @Autowired private EmailService emailService;

//     // ==================== INITIATE WORKFLOW ====================

//     public String initiateApprovalWorkflow(Long poId, Long creatorUserId) {
//         logger.info("🔵 [PO INITIATE WORKFLOW] PO ID: {}, Creator User ID: {}", poId, creatorUserId);

//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + poId));

//         if (!POStatus.DRAFT.equals(po.getStatus())) {
//             throw new RuntimeException("Only DRAFT Purchase Orders can be submitted for approval. Current status: " + po.getStatus());
//         }

//         String companyName = po.getBuyer().getOrganizationCompanyName();
//         if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();

//         List<HierarchyLevel> levels = hierarchyLevelRepository
//                 .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
//         if (levels.isEmpty()) throw new RuntimeException("No active hierarchy levels found for company: " + companyName);

//         HierarchyLevel firstLevel = levels.get(0);
//         List<HierarchyUser> usersAtFirstLevel = hierarchyUserRepository
//                 .findByHierarchyLevelAndIsActiveOrderByIdAsc(firstLevel, true);
//         if (usersAtFirstLevel.isEmpty()) throw new RuntimeException("No active users found at level: " + firstLevel.getLevelName());

//         HierarchyUser firstApprover = usersAtFirstLevel.get(0);

//         POApproval approval = new POApproval();
//         approval.setPurchaseOrder(po);
//         approval.setHierarchyLevel(firstLevel);
//         approval.setApproverUser(firstApprover);
//         approval.setStatus(ApprovalActionStatus.PENDING);
//         approval.setSequenceOrder(1);
//         approval.setCreatedAt(LocalDateTime.now());
//         poApprovalRepository.save(approval);

//         po.setStatus(POStatus.PENDING_APPROVAL);
//         po.setApprovalRequired(true);
//         po.setApprovalStatus(ApprovalStatus.PENDING);
//         purchaseOrderRepository.save(po);

//         try { emailService.sendPOApprovalPendingEmail(po, firstApprover, firstLevel, 1, levels.size()); }
//         catch (Exception e) { logger.error("  ❌ Failed to send email", e); }

//         return "PO approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
//     }

//     // ==================== CHECK IF USER IS LAST APPROVER ====================

//     @Transactional(readOnly = true)
//     public boolean isLastApprover(Long poId, Long userId) {
//         Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
//         if (currentApprovalOpt.isEmpty()) return false;
//         POApproval currentApproval = currentApprovalOpt.get();
//         if (!currentApproval.getApproverUser().getId().equals(userId)) return false;

//         PurchaseOrder po = currentApproval.getPurchaseOrder();
//         String companyName = po.getBuyer().getOrganizationCompanyName();
//         if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();

//         List<HierarchyLevel> allLevels = hierarchyLevelRepository
//                 .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
//         if (allLevels.isEmpty()) return false;
//         HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1);
//         return currentApproval.getHierarchyLevel().getLevelOrder().equals(lastLevel.getLevelOrder());
//     }

//     // ==================== APPROVE PO ====================

//     public String approvePO(Long poId, Long approverId, String comments) {
//         logger.info("🟢 [APPROVE PO] PO ID: {}, Approver ID: {}", poId, approverId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
//         if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

//         POApproval currentApproval = currentApprovalOpt.get();
//         if (!currentApproval.getApproverUser().getId().equals(approverId))
//             throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");

//         HierarchyUser currentApprover = currentApproval.getApproverUser();
//         boolean isLastLevel = isLastApprover(poId, approverId);

//         currentApproval.setStatus(ApprovalActionStatus.APPROVED);
//         currentApproval.setComments(comments);
//         currentApproval.setActionDate(LocalDateTime.now());
//         poApprovalRepository.save(currentApproval);

//         if (isLastLevel) {
//             po.setStatus(POStatus.APPROVED);
//             po.setApprovalStatus(ApprovalStatus.APPROVED);
//             po.setApprovalDate(LocalDateTime.now());
//             po.setApprovedBy(approverId);
//             po.setApprovedByName(currentApprover.getFullName());
//             po.setApprovedByDesignation(currentApprover.getDesignation());
//             purchaseOrderRepository.save(po);
//             try { emailService.sendPOApprovedEmail(po, currentApprover); } catch (Exception e) { logger.error("Email failed", e); }
//             return "Purchase Order " + po.getPoNumber() + " fully approved.";
//         } else {
//             String companyName = po.getBuyer().getOrganizationCompanyName();
//             if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();
//             List<HierarchyLevel> allLevels = hierarchyLevelRepository
//                     .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

//             int currentLevelIndex = -1;
//             for (int i = 0; i < allLevels.size(); i++) {
//                 if (allLevels.get(i).getId().equals(currentApproval.getHierarchyLevel().getId())) {
//                     currentLevelIndex = i; break;
//                 }
//             }

//             if (currentLevelIndex != -1 && currentLevelIndex < allLevels.size() - 1) {
//                 HierarchyLevel nextLevel = allLevels.get(currentLevelIndex + 1);
//                 List<HierarchyUser> usersAtNextLevel = hierarchyUserRepository
//                         .findByHierarchyLevelAndIsActiveOrderByIdAsc(nextLevel, true);
//                 if (!usersAtNextLevel.isEmpty()) {
//                     HierarchyUser nextApprover = usersAtNextLevel.get(0);
//                     POApproval nextApproval = new POApproval();
//                     nextApproval.setPurchaseOrder(po);
//                     nextApproval.setHierarchyLevel(nextLevel);
//                     nextApproval.setApproverUser(nextApprover);
//                     nextApproval.setStatus(ApprovalActionStatus.PENDING);
//                     nextApproval.setSequenceOrder(currentApproval.getSequenceOrder() + 1);
//                     nextApproval.setCreatedAt(LocalDateTime.now());
//                     poApprovalRepository.save(nextApproval);
//                     purchaseOrderRepository.save(po);
//                     try { emailService.sendPOApprovalForwardedEmail(po, currentApprover, nextApprover, nextLevel, comments); }
//                     catch (Exception e) { logger.error("Email failed", e); }
//                     return "PO approved at " + currentApproval.getHierarchyLevel().getLevelName() + ". Forwarded to " + nextLevel.getLevelName();
//                 }
//             }

//             po.setStatus(POStatus.APPROVED);
//             po.setApprovalStatus(ApprovalStatus.APPROVED);
//             po.setApprovalDate(LocalDateTime.now());
//             po.setApprovedBy(approverId);
//             po.setApprovedByName(currentApprover.getFullName());
//             po.setApprovedByDesignation(currentApprover.getDesignation());
//             purchaseOrderRepository.save(po);
//             return "Purchase Order " + po.getPoNumber() + " approved at all levels.";
//         }
//     }

//     // ==================== REJECT PO ====================

//     public String rejectPO(Long poId, Long rejectorId, String rejectRemarks) {
//         logger.info("🔴 [REJECT PO] PO ID: {}, Rejector ID: {}", poId, rejectorId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
//         if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

//         POApproval currentApproval = currentApprovalOpt.get();
//         if (!currentApproval.getApproverUser().getId().equals(rejectorId))
//             throw new RuntimeException("User " + rejectorId + " is not authorized to reject at this level");

//         HierarchyUser rejector = hierarchyUserRepository.findById(rejectorId)
//                 .orElseThrow(() -> new RuntimeException("Rejector user not found"));
//         String remarks = (rejectRemarks != null && !rejectRemarks.trim().isEmpty()) ? rejectRemarks.trim() : "No remarks";

//         currentApproval.setStatus(ApprovalActionStatus.REJECTED);
//         currentApproval.setComments(remarks);
//         currentApproval.setRejectRemarks(remarks);
//         currentApproval.setRejectedByUser(rejector);
//         currentApproval.setRejectDate(LocalDateTime.now());
//         currentApproval.setActionDate(LocalDateTime.now());
//         poApprovalRepository.saveAndFlush(currentApproval);

//         po.setStatus(POStatus.REJECTED);
//         po.setApprovalStatus(ApprovalStatus.REJECTED);
//         po.setUpdatedAt(LocalDateTime.now());
//         purchaseOrderRepository.saveAndFlush(po);

//         try { emailService.sendPORejectedEmail(po, rejector, remarks); } catch (Exception e) { logger.error("Email failed", e); }
//         return "Purchase Order permanently rejected by " + rejector.getFullName() + ". Reason: " + remarks;
//     }

//     // ==================== RETURN FOR REVISION ====================

//     public String returnForRevision(Long poId, Long approverId, String revisionComments) {
//         logger.info("🔄 [RETURN FOR REVISION] PO ID: {}", poId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
//         if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

//         POApproval currentApproval = currentApprovalOpt.get();
//         if (!currentApproval.getApproverUser().getId().equals(approverId))
//             throw new RuntimeException("User not authorized to return for revision at this level");

//         HierarchyUser returner = currentApproval.getApproverUser();
//         String remarks = (revisionComments != null && !revisionComments.trim().isEmpty()) ? revisionComments.trim() : "Revision needed";

//         currentApproval.setStatus(ApprovalActionStatus.RESUBMITTED);
//         currentApproval.setComments(remarks);
//         currentApproval.setReturnedByUser(returner);
//         currentApproval.setReturnRemarks(remarks);
//         currentApproval.setReturnDate(LocalDateTime.now());
//         currentApproval.setActionDate(LocalDateTime.now());
//         poApprovalRepository.save(currentApproval);

//         po.setStatus(POStatus.DRAFT);
//         po.setApprovalStatus(ApprovalStatus.PENDING);
//         po.setInternalNotes("Returned for revision: " + remarks);
//         po.setUpdatedAt(LocalDateTime.now());
//         purchaseOrderRepository.save(po);

//         try { emailService.sendPOReturnedForRevisionEmail(po, returner, remarks); } catch (Exception e) { logger.error("Email failed", e); }
//         return "PO returned for revision. Creator can make changes and resubmit. Remarks: " + remarks;
//     }

//     // ==================== RESUBMIT ====================

//     public String resubmitPO(Long poId, Long resubmitterId) {
//         logger.info("🔄 [RESUBMIT PO] PO ID: {}", poId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         if (!POStatus.DRAFT.equals(po.getStatus()))
//             throw new RuntimeException("Only DRAFT POs can be resubmitted. Current: " + po.getStatus());

//         List<POApproval> allApprovals = poApprovalRepository.findByPurchaseOrderIdOrderBySequenceOrderAsc(poId);
//         if (!allApprovals.isEmpty()) poApprovalRepository.deleteAll(allApprovals);

//         po.setInternalNotes(null);
//         po.setUpdatedAt(LocalDateTime.now());
//         purchaseOrderRepository.save(po);

//         return initiateApprovalWorkflow(poId, resubmitterId);
//     }

//     // ==================== HOLD PO ====================

//     public String holdPO(Long poId, Long holderId, String holdRemarks) {
//         logger.info("⏸️ [HOLD PO] PO ID: {}", poId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
//         if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

//         POApproval currentApproval = currentApprovalOpt.get();
//         if (!currentApproval.getApproverUser().getId().equals(holderId))
//             throw new RuntimeException("Only the current approver can put this PO on hold");
//         if (holdRemarks == null || holdRemarks.trim().isEmpty()) throw new RuntimeException("Hold remarks are required");

//         HierarchyUser holder = hierarchyUserRepository.findById(holderId)
//                 .orElseThrow(() -> new RuntimeException("Holder user not found"));

//         currentApproval.setStatus(ApprovalActionStatus.HOLD);
//         currentApproval.setHeldByUser(holder);
//         currentApproval.setHoldRemarks(holdRemarks);
//         currentApproval.setHoldDate(LocalDateTime.now());
//         currentApproval.setActionDate(LocalDateTime.now());
//         poApprovalRepository.save(currentApproval);

//         String holdInfo = String.format("On HOLD by %s (%s). Reason: %s",
//                 holder.getFullName(), currentApproval.getHierarchyLevel().getLevelName(), holdRemarks);
//         po.setInternalNotes(holdInfo.length() > 500 ? holdInfo.substring(0, 500) : holdInfo);
//         po.setUpdatedAt(LocalDateTime.now());
//         purchaseOrderRepository.save(po);

//         try { emailService.sendPOOnHoldEmail(po, holder, holdRemarks); } catch (Exception e) { logger.error("Email failed", e); }
//         return "PO put on HOLD by " + holder.getFullName() + ". Reason: " + holdRemarks;
//     }

//     // ==================== RELEASE HOLD ====================

//     public String releaseHold(Long poId, Long releaserId, String releaseRemarks) {
//         logger.info("▶️ [RELEASE HOLD] PO ID: {}", poId);
//         PurchaseOrder po = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         Optional<POApproval> holdApprovalOpt = poApprovalRepository.findHoldApprovalByPoId(poId);
//         if (holdApprovalOpt.isEmpty()) throw new RuntimeException("No HOLD approval found for PO: " + poId);

//         POApproval holdApproval = holdApprovalOpt.get();
//         HierarchyUser releaser = hierarchyUserRepository.findById(releaserId)
//                 .orElseThrow(() -> new RuntimeException("Releaser user not found"));

//         boolean canRelease = false;
//         if (holdApproval.getHeldByUser() != null && holdApproval.getHeldByUser().getId().equals(releaserId)) {
//             canRelease = true;
//         } else if (releaser.getHierarchyLevel() != null && holdApproval.getHierarchyLevel() != null) {
//             String holderCompany  = holdApproval.getHierarchyLevel().getCompanyName();
//             String releaserCompany = releaser.getHierarchyLevel().getCompanyName();
//             if (holderCompany.equals(releaserCompany) &&
//                     releaser.getHierarchyLevel().getLevelOrder() <= holdApproval.getHierarchyLevel().getLevelOrder()) {
//                 canRelease = true;
//             }
//         }
//         if (!canRelease) throw new RuntimeException("User not authorized to release this hold");

//         holdApproval.setStatus(ApprovalActionStatus.PENDING);
//         holdApproval.setReleasedByUser(releaser);
//         holdApproval.setReleaseRemarks(releaseRemarks);
//         holdApproval.setReleasedDate(LocalDateTime.now());
//         poApprovalRepository.save(holdApproval);

//         po.setStatus(POStatus.PENDING_APPROVAL);
//         po.setInternalNotes(null);
//         po.setUpdatedAt(LocalDateTime.now());
//         purchaseOrderRepository.save(po);

//         try { emailService.sendPOHoldReleasedEmail(po, releaser, holdApproval.getApproverUser(), releaseRemarks); }
//         catch (Exception e) { logger.error("Email failed", e); }
//         return "Hold released by " + releaser.getFullName() + ". PO approval continues.";
//     }

//     // ==================== HISTORY / QUERY ====================

//     @Transactional(readOnly = true)
//     public List<POApprovalResponse> getApprovalHistory(Long poId) {
//         return poApprovalRepository.findByPurchaseOrderIdOrderBySequenceOrderAsc(poId)
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public Optional<POApprovalResponse> getCurrentPendingApproval(Long poId) {
//         return poApprovalRepository.findFirstCurrentPendingApproval(poId).map(this::toResponse);
//     }

//     @Transactional(readOnly = true)
//     public List<POApprovalResponse> getPendingApprovalsForUser(Long userId) {
//         return poApprovalRepository.findPendingApprovalsByUser(userId)
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public List<POApprovalResponse> getHoldApprovalsForUser(Long userId) {
//         HierarchyUser user = hierarchyUserRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (user.getHierarchyLevel() == null) return List.of();
//         return poApprovalRepository.findVisibleHoldApprovals(
//                 user.getHierarchyLevel().getLevelOrder(),
//                 user.getHierarchyLevel().getCompanyName())
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public Long getPendingApprovalCount(Long userId) {
//         return poApprovalRepository.countPendingApprovalsByUser(userId);
//     }

//     @Transactional(readOnly = true)
//     public Long getHoldApprovalCount(Long userId) {
//         HierarchyUser user = hierarchyUserRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (user.getHierarchyLevel() == null) return 0L;
//         return poApprovalRepository.countVisibleHoldApprovals(
//                 user.getHierarchyLevel().getLevelOrder(),
//                 user.getHierarchyLevel().getCompanyName());
//     }

//     // ==================== MAPPER ====================

//     private POApprovalResponse toResponse(POApproval approval) {
//         POApprovalResponse response = new POApprovalResponse();

//         response.setId(approval.getId());
//         response.setPurchaseOrderId(approval.getPurchaseOrder().getId());
//         response.setPoNumber(approval.getPurchaseOrder().getPoNumber());
//         response.setGrandTotal(approval.getPurchaseOrder().getGrandTotal());

//         // ✅ Set currency from PO (which was copied from buyer's location at PO creation time)
//         response.setCurrencyCode(
//             approval.getPurchaseOrder().getCurrencyCode() != null
//                 ? approval.getPurchaseOrder().getCurrencyCode() : "INR");
//         response.setCurrencySymbol(
//             approval.getPurchaseOrder().getCurrencySymbol() != null
//                 ? approval.getPurchaseOrder().getCurrencySymbol() : "₹");

//         response.setHierarchyLevelId(approval.getHierarchyLevel().getId());
//         response.setHierarchyLevelName(approval.getHierarchyLevel().getLevelName());
//         response.setHierarchyLevelOrder(approval.getHierarchyLevel().getLevelOrder());

//         response.setStatus(approval.getStatus().name());
//         response.setSequenceOrder(approval.getSequenceOrder());
//         response.setCreatedAt(approval.getCreatedAt());
//         response.setActionDate(approval.getActionDate());

//         String comments = approval.getComments();
//         if (comments == null || comments.trim().isEmpty()) {
//             if (approval.getRejectRemarks() != null) comments = approval.getRejectRemarks();
//             else if (approval.getHoldRemarks() != null) comments = approval.getHoldRemarks();
//             else if (approval.getReturnRemarks() != null) comments = approval.getReturnRemarks();
//         }
//         response.setComments(comments);

//         if (approval.getApproverUser() != null) {
//             response.setApproverUserId(approval.getApproverUser().getId());
//             response.setApproverUserName(approval.getApproverUser().getFullName());
//             response.setApproverUserEmail(approval.getApproverUser().getEmail());
//         }
//         if (approval.getPurchaseOrder().getBuyer() != null)
//             response.setBuyerName(approval.getPurchaseOrder().getBuyer().getCompanyName());
//         if (approval.getPurchaseOrder().getSupplier() != null)
//             response.setSupplierName(approval.getPurchaseOrder().getSupplier().getCompanyName());
//         if (approval.getPurchaseOrder().getRfq() != null)
//             response.setRfqNumber(approval.getPurchaseOrder().getRfq().getRfqNumber());

//         // Hold tracking
//         if (approval.getHeldByUser() != null) {
//             response.setHeldByUserId(approval.getHeldByUser().getId());
//             response.setHeldByUserName(approval.getHeldByUser().getFullName());
//             response.setHeldByUserEmail(approval.getHeldByUser().getEmail());
//         }
//         response.setHoldRemarks(approval.getHoldRemarks());
//         response.setHoldDate(approval.getHoldDate());
//         if (approval.getReleasedByUser() != null) {
//             response.setReleasedByUserId(approval.getReleasedByUser().getId());
//             response.setReleasedByUserName(approval.getReleasedByUser().getFullName());
//             response.setReleasedByUserEmail(approval.getReleasedByUser().getEmail());
//         }
//         response.setReleaseRemarks(approval.getReleaseRemarks());
//         response.setReleasedDate(approval.getReleasedDate());

//         // Reject tracking
//         if (approval.getRejectedByUser() != null) {
//             response.setRejectedByUserId(approval.getRejectedByUser().getId());
//             response.setRejectedByUserName(approval.getRejectedByUser().getFullName());
//             response.setRejectedByUserEmail(approval.getRejectedByUser().getEmail());
//         }
//         response.setRejectRemarks(approval.getRejectRemarks());
//         response.setRejectDate(approval.getRejectDate());

//         // Return tracking
//         if (approval.getReturnedByUser() != null) {
//             response.setReturnedByUserId(approval.getReturnedByUser().getId());
//             response.setReturnedByUserName(approval.getReturnedByUser().getFullName());
//             response.setReturnedByUserEmail(approval.getReturnedByUser().getEmail());
//         }
//         response.setReturnRemarks(approval.getReturnRemarks());
//         response.setReturnDate(approval.getReturnDate());

//         response.setHoldDisplayInfo(approval.getHoldDisplayInfo());
//         response.setRejectDisplayInfo(approval.getRejectDisplayInfo());
//         response.setReturnDisplayInfo(approval.getReturnDisplayInfo());
//         response.setIsOnHold(approval.isOnHold());
//         response.setWasHeldAndReleased(approval.wasHeldAndReleased());
//         response.setWasRejected(approval.wasRejected());

//         return response;
//     }
// }

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.POApprovalResponse;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class POApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(POApprovalService.class);

    // ====================================================================
    // ✅ APPROVAL SLAB THRESHOLD
    //
    // PO grandTotal <= APPROVAL_THRESHOLD  → SHORT FLOW: 40 → (30,20 SKIPPED) → 10
    // PO grandTotal >  APPROVAL_THRESHOLD  → FULL  FLOW: 40 → 30 → 20 → 10
    //
    // To change the threshold later, just update this value.
    // ====================================================================
    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("200000.00"); // ₹2,00,000

    @Autowired private POApprovalRepository poApprovalRepository;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired private HierarchyLevelRepository hierarchyLevelRepository;
    @Autowired private HierarchyUserRepository hierarchyUserRepository;
    @Autowired private EmailService emailService;

    // ==================== INITIATE WORKFLOW ====================
    // ✅ NO CHANGE from your original code

    public String initiateApprovalWorkflow(Long poId, Long creatorUserId) {
        logger.info("🔵 [PO INITIATE WORKFLOW] PO ID: {}, Creator User ID: {}", poId, creatorUserId);

        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + poId));

        if (!POStatus.DRAFT.equals(po.getStatus())) {
            throw new RuntimeException("Only DRAFT Purchase Orders can be submitted for approval. Current status: " + po.getStatus());
        }

        String companyName = po.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();

        List<HierarchyLevel> levels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
        if (levels.isEmpty()) throw new RuntimeException("No active hierarchy levels found for company: " + companyName);

        HierarchyLevel firstLevel = levels.get(0); // Highest levelOrder e.g. 40
        List<HierarchyUser> usersAtFirstLevel = hierarchyUserRepository
                .findByHierarchyLevelAndIsActiveOrderByIdAsc(firstLevel, true);
        if (usersAtFirstLevel.isEmpty()) throw new RuntimeException("No active users found at level: " + firstLevel.getLevelName());

        HierarchyUser firstApprover = usersAtFirstLevel.get(0);

        POApproval approval = new POApproval();
        approval.setPurchaseOrder(po);
        approval.setHierarchyLevel(firstLevel);
        approval.setApproverUser(firstApprover);
        approval.setStatus(ApprovalActionStatus.PENDING);
        approval.setSequenceOrder(1);
        approval.setCreatedAt(LocalDateTime.now());
        poApprovalRepository.save(approval);

        po.setStatus(POStatus.PENDING_APPROVAL);
        po.setApprovalRequired(true);
        po.setApprovalStatus(ApprovalStatus.PENDING);
        purchaseOrderRepository.save(po);

        try { emailService.sendPOApprovalPendingEmail(po, firstApprover, firstLevel, 1, levels.size()); }
        catch (Exception e) { logger.error("  ❌ Failed to send email", e); }

        logger.info("  ✅ Workflow initiated. First approver: {} at level {}",
                firstApprover.getFullName(), firstLevel.getLevelName());

        return "PO approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
    }

    // ==================== CHECK IF USER IS LAST APPROVER ====================
    // ✅ NO CHANGE from your original code

    @Transactional(readOnly = true)
    public boolean isLastApprover(Long poId, Long userId) {
        Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
        if (currentApprovalOpt.isEmpty()) return false;
        POApproval currentApproval = currentApprovalOpt.get();
        if (!currentApproval.getApproverUser().getId().equals(userId)) return false;

        PurchaseOrder po = currentApproval.getPurchaseOrder();
        String companyName = po.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();

        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
        if (allLevels.isEmpty()) return false;

        // Last level = lowest levelOrder = last element in descending list [40,30,20,10] → 10
        HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1);
        return currentApproval.getHierarchyLevel().getLevelOrder().equals(lastLevel.getLevelOrder());
    }

    // ==================== APPROVE PO ====================
    // ✅ THIS IS THE ONLY METHOD WITH NEW SLAB LOGIC
    //
    // FULL FLOW  (grandTotal > ₹2L): 40 → 30 → 20 → 10  (unchanged)
    // SHORT FLOW (grandTotal ≤ ₹2L): 40 → SKIPPED(30,20) → 10
    //
    // The slab check happens ONLY when level 40 approves and there are middle levels.
    // All other steps (30→20, 20→10 in full flow, 10 final) are exactly your original logic.

    public String approvePO(Long poId, Long approverId, String comments) {
        logger.info("🟢 [APPROVE PO] PO ID: {}, Approver ID: {}", poId, approverId);

        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));

        Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
        if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

        POApproval currentApproval = currentApprovalOpt.get();
        if (!currentApproval.getApproverUser().getId().equals(approverId))
            throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");

        HierarchyUser currentApprover = currentApproval.getApproverUser();
        boolean isLastLevel = isLastApprover(poId, approverId);

        // Mark current approval as APPROVED
        currentApproval.setStatus(ApprovalActionStatus.APPROVED);
        currentApproval.setComments(comments);
        currentApproval.setActionDate(LocalDateTime.now());
        poApprovalRepository.save(currentApproval);

        logger.info("  ✅ Approved at level {} ({}) by {}",
                currentApproval.getHierarchyLevel().getLevelOrder(),
                currentApproval.getHierarchyLevel().getLevelName(),
                currentApprover.getFullName());

        // ── If this is the last level → fully approve the PO ──
        if (isLastLevel) {
            po.setStatus(POStatus.APPROVED);
            po.setApprovalStatus(ApprovalStatus.APPROVED);
            po.setApprovalDate(LocalDateTime.now());
            po.setApprovedBy(approverId);
            po.setApprovedByName(currentApprover.getFullName());
            po.setApprovedByDesignation(currentApprover.getDesignation());
            purchaseOrderRepository.save(po);
            try { emailService.sendPOApprovedEmail(po, currentApprover); }
            catch (Exception e) { logger.error("Email failed", e); }
            logger.info("  🎉 PO {} FULLY APPROVED", po.getPoNumber());
            return "Purchase Order " + po.getPoNumber() + " fully approved.";
        }

        // ── Not the last level — find all levels ──
        String companyName = po.getBuyer().getOrganizationCompanyName();
        if (companyName == null || companyName.isEmpty()) companyName = po.getBuyer().getCompanyName();

        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);
        // allLevels in descending order e.g. [40, 30, 20, 10]

        int currentLevelIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getId().equals(currentApproval.getHierarchyLevel().getId())) {
                currentLevelIndex = i;
                break;
            }
        }

        if (currentLevelIndex == -1) {
            // Safety fallback — should not happen
            logger.warn("  ⚠️ Could not find current level in hierarchy — auto completing PO");
            po.setStatus(POStatus.APPROVED);
            po.setApprovalStatus(ApprovalStatus.APPROVED);
            po.setApprovalDate(LocalDateTime.now());
            po.setApprovedBy(approverId);
            po.setApprovedByName(currentApprover.getFullName());
            po.setApprovedByDesignation(currentApprover.getDesignation());
            purchaseOrderRepository.save(po);
            return "Purchase Order " + po.getPoNumber() + " approved at all levels.";
        }

        // ── ✅ SLAB LOGIC ──
        // Check: is this the FIRST level (index 0 = highest levelOrder e.g. 40)?
        // AND is PO amount ≤ threshold?
        // AND are there middle levels to skip (more than 2 levels total)?
        boolean isFirstLevel = (currentLevelIndex == 0);
        BigDecimal poAmount = po.getGrandTotal() != null ? po.getGrandTotal() : BigDecimal.ZERO;
        boolean isShortFlow = poAmount.compareTo(APPROVAL_THRESHOLD) <= 0;

        logger.info("  💰 PO Amount: {} | Threshold: {} | isFirstLevel: {} | Flow: {}",
                poAmount, APPROVAL_THRESHOLD, isFirstLevel,
                (isShortFlow && isFirstLevel && allLevels.size() > 2)
                        ? "SHORT (skip middle → jump to last)"
                        : "FULL (next sequential level)");

        if (isShortFlow && isFirstLevel && allLevels.size() > 2) {
            // ── SHORT FLOW ──
            // 1. Create SKIPPED records for all middle levels (index 1 to size-2)
            // 2. Create PENDING for last level (index size-1)

            HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1); // e.g. level 10
            int sequence = currentApproval.getSequenceOrder() + 1;

            // Create SKIPPED for middle levels (e.g. 30, 20)
            for (int i = 1; i <= allLevels.size() - 2; i++) {
                HierarchyLevel middleLevel = allLevels.get(i);
                List<HierarchyUser> middleUsers = hierarchyUserRepository
                        .findByHierarchyLevelAndIsActiveOrderByIdAsc(middleLevel, true);

                POApproval skippedApproval = new POApproval();
                skippedApproval.setPurchaseOrder(po);
                skippedApproval.setHierarchyLevel(middleLevel);
                skippedApproval.setApproverUser(middleUsers.isEmpty() ? null : middleUsers.get(0));
                skippedApproval.setStatus(ApprovalActionStatus.SKIPPED);
                skippedApproval.setSequenceOrder(sequence++);
                skippedApproval.setComments("Auto-skipped: PO amount ₹" + poAmount
                        + " ≤ approval threshold ₹" + APPROVAL_THRESHOLD);
                skippedApproval.setActionDate(LocalDateTime.now());
                skippedApproval.setCreatedAt(LocalDateTime.now());
                poApprovalRepository.save(skippedApproval);

                logger.info("  ⏭️  SKIPPED level {} ({})",
                        middleLevel.getLevelOrder(), middleLevel.getLevelName());
            }

            // Create PENDING for last level (e.g. level 10)
            List<HierarchyUser> usersAtLastLevel = hierarchyUserRepository
                    .findByHierarchyLevelAndIsActiveOrderByIdAsc(lastLevel, true);

            if (usersAtLastLevel.isEmpty()) {
                // No user at last level — auto fully approve
                logger.warn("  ⚠️ No users at last level {} — auto completing PO", lastLevel.getLevelName());
                po.setStatus(POStatus.APPROVED);
                po.setApprovalStatus(ApprovalStatus.APPROVED);
                po.setApprovalDate(LocalDateTime.now());
                po.setApprovedBy(approverId);
                po.setApprovedByName(currentApprover.getFullName());
                po.setApprovedByDesignation(currentApprover.getDesignation());
                purchaseOrderRepository.save(po);
                return "Purchase Order " + po.getPoNumber() + " approved at all levels.";
            }

            HierarchyUser lastApprover = usersAtLastLevel.get(0);

            POApproval lastApproval = new POApproval();
            lastApproval.setPurchaseOrder(po);
            lastApproval.setHierarchyLevel(lastLevel);
            lastApproval.setApproverUser(lastApprover);
            lastApproval.setStatus(ApprovalActionStatus.PENDING);
            lastApproval.setSequenceOrder(sequence);
            lastApproval.setCreatedAt(LocalDateTime.now());
            poApprovalRepository.save(lastApproval);

            purchaseOrderRepository.save(po);

            logger.info("  ✅ SHORT FLOW: level {} → SKIPPED middle levels → PENDING at level {}",
                    currentApproval.getHierarchyLevel().getLevelOrder(), lastLevel.getLevelOrder());

            try { emailService.sendPOApprovalForwardedEmail(po, currentApprover, lastApprover, lastLevel, comments); }
            catch (Exception e) { logger.error("Email failed", e); }

            return "PO approved at " + currentApproval.getHierarchyLevel().getLevelName()
                    + " (SHORT flow — amount ≤ ₹2 Lakhs). Middle levels skipped. Forwarded directly to "
                    + lastLevel.getLevelName();
        }

        // ── FULL FLOW: Move to the very next level in sequence ──
        if (currentLevelIndex < allLevels.size() - 1) {
            HierarchyLevel nextLevel = allLevels.get(currentLevelIndex + 1);
            List<HierarchyUser> usersAtNextLevel = hierarchyUserRepository
                    .findByHierarchyLevelAndIsActiveOrderByIdAsc(nextLevel, true);

            if (!usersAtNextLevel.isEmpty()) {
                HierarchyUser nextApprover = usersAtNextLevel.get(0);

                POApproval nextApproval = new POApproval();
                nextApproval.setPurchaseOrder(po);
                nextApproval.setHierarchyLevel(nextLevel);
                nextApproval.setApproverUser(nextApprover);
                nextApproval.setStatus(ApprovalActionStatus.PENDING);
                nextApproval.setSequenceOrder(currentApproval.getSequenceOrder() + 1);
                nextApproval.setCreatedAt(LocalDateTime.now());
                poApprovalRepository.save(nextApproval);

                purchaseOrderRepository.save(po);

                logger.info("  ➡️ FULL FLOW: Forwarded to level {} ({})",
                        nextLevel.getLevelOrder(), nextLevel.getLevelName());

                try { emailService.sendPOApprovalForwardedEmail(po, currentApprover, nextApprover, nextLevel, comments); }
                catch (Exception e) { logger.error("Email failed", e); }

                return "PO approved at " + currentApproval.getHierarchyLevel().getLevelName()
                        + ". Forwarded to " + nextLevel.getLevelName();
            }
        }

        // Safety fallback — no next level or no users found
        po.setStatus(POStatus.APPROVED);
        po.setApprovalStatus(ApprovalStatus.APPROVED);
        po.setApprovalDate(LocalDateTime.now());
        po.setApprovedBy(approverId);
        po.setApprovedByName(currentApprover.getFullName());
        po.setApprovedByDesignation(currentApprover.getDesignation());
        purchaseOrderRepository.save(po);
        return "Purchase Order " + po.getPoNumber() + " approved at all levels.";
    }

    // ==================== REJECT PO ====================
    // ✅ NO CHANGE from your original code

    public String rejectPO(Long poId, Long rejectorId, String rejectRemarks) {
        logger.info("🔴 [REJECT PO] PO ID: {}, Rejector ID: {}", poId, rejectorId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
        Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
        if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

        POApproval currentApproval = currentApprovalOpt.get();
        if (!currentApproval.getApproverUser().getId().equals(rejectorId))
            throw new RuntimeException("User " + rejectorId + " is not authorized to reject at this level");

        HierarchyUser rejector = hierarchyUserRepository.findById(rejectorId)
                .orElseThrow(() -> new RuntimeException("Rejector user not found"));
        String remarks = (rejectRemarks != null && !rejectRemarks.trim().isEmpty()) ? rejectRemarks.trim() : "No remarks";

        currentApproval.setStatus(ApprovalActionStatus.REJECTED);
        currentApproval.setComments(remarks);
        currentApproval.setRejectRemarks(remarks);
        currentApproval.setRejectedByUser(rejector);
        currentApproval.setRejectDate(LocalDateTime.now());
        currentApproval.setActionDate(LocalDateTime.now());
        poApprovalRepository.saveAndFlush(currentApproval);

        po.setStatus(POStatus.REJECTED);
        po.setApprovalStatus(ApprovalStatus.REJECTED);
        po.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.saveAndFlush(po);

        try { emailService.sendPORejectedEmail(po, rejector, remarks); }
        catch (Exception e) { logger.error("Email failed", e); }
        return "Purchase Order permanently rejected by " + rejector.getFullName() + ". Reason: " + remarks;
    }

    // ==================== RETURN FOR REVISION ====================
    // ✅ NO CHANGE from your original code

    public String returnForRevision(Long poId, Long approverId, String revisionComments) {
        logger.info("🔄 [RETURN FOR REVISION] PO ID: {}", poId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
        Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
        if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

        POApproval currentApproval = currentApprovalOpt.get();
        if (!currentApproval.getApproverUser().getId().equals(approverId))
            throw new RuntimeException("User not authorized to return for revision at this level");

        HierarchyUser returner = currentApproval.getApproverUser();
        String remarks = (revisionComments != null && !revisionComments.trim().isEmpty()) ? revisionComments.trim() : "Revision needed";

        currentApproval.setStatus(ApprovalActionStatus.RESUBMITTED);
        currentApproval.setComments(remarks);
        currentApproval.setReturnedByUser(returner);
        currentApproval.setReturnRemarks(remarks);
        currentApproval.setReturnDate(LocalDateTime.now());
        currentApproval.setActionDate(LocalDateTime.now());
        poApprovalRepository.save(currentApproval);

        po.setStatus(POStatus.DRAFT);
        po.setApprovalStatus(ApprovalStatus.PENDING);
        po.setInternalNotes("Returned for revision: " + remarks);
        po.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);

        try { emailService.sendPOReturnedForRevisionEmail(po, returner, remarks); }
        catch (Exception e) { logger.error("Email failed", e); }
        return "PO returned for revision. Creator can make changes and resubmit. Remarks: " + remarks;
    }

    // ==================== RESUBMIT ====================
    // ✅ NO CHANGE from your original code

    public String resubmitPO(Long poId, Long resubmitterId) {
        logger.info("🔄 [RESUBMIT PO] PO ID: {}", poId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
        if (!POStatus.DRAFT.equals(po.getStatus()))
            throw new RuntimeException("Only DRAFT POs can be resubmitted. Current: " + po.getStatus());

        // Delete all existing approval records (including any SKIPPED ones from previous run)
        List<POApproval> allApprovals = poApprovalRepository.findByPurchaseOrderIdOrderBySequenceOrderAsc(poId);
        if (!allApprovals.isEmpty()) poApprovalRepository.deleteAll(allApprovals);

        po.setInternalNotes(null);
        po.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);

        return initiateApprovalWorkflow(poId, resubmitterId);
    }

    // ==================== HOLD PO ====================
    // ✅ NO CHANGE from your original code

    public String holdPO(Long poId, Long holderId, String holdRemarks) {
        logger.info("⏸️ [HOLD PO] PO ID: {}", poId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
        Optional<POApproval> currentApprovalOpt = poApprovalRepository.findFirstCurrentPendingApproval(poId);
        if (currentApprovalOpt.isEmpty()) throw new RuntimeException("No pending approval found for PO: " + poId);

        POApproval currentApproval = currentApprovalOpt.get();
        if (!currentApproval.getApproverUser().getId().equals(holderId))
            throw new RuntimeException("Only the current approver can put this PO on hold");
        if (holdRemarks == null || holdRemarks.trim().isEmpty())
            throw new RuntimeException("Hold remarks are required");

        HierarchyUser holder = hierarchyUserRepository.findById(holderId)
                .orElseThrow(() -> new RuntimeException("Holder user not found"));

        currentApproval.setStatus(ApprovalActionStatus.HOLD);
        currentApproval.setHeldByUser(holder);
        currentApproval.setHoldRemarks(holdRemarks);
        currentApproval.setHoldDate(LocalDateTime.now());
        currentApproval.setActionDate(LocalDateTime.now());
        poApprovalRepository.save(currentApproval);

        String holdInfo = String.format("On HOLD by %s (%s). Reason: %s",
                holder.getFullName(), currentApproval.getHierarchyLevel().getLevelName(), holdRemarks);
        po.setInternalNotes(holdInfo.length() > 500 ? holdInfo.substring(0, 500) : holdInfo);
        po.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);

        try { emailService.sendPOOnHoldEmail(po, holder, holdRemarks); }
        catch (Exception e) { logger.error("Email failed", e); }
        return "PO put on HOLD by " + holder.getFullName() + ". Reason: " + holdRemarks;
    }

    // ==================== RELEASE HOLD ====================
    // ✅ NO CHANGE from your original code

    public String releaseHold(Long poId, Long releaserId, String releaseRemarks) {
        logger.info("▶️ [RELEASE HOLD] PO ID: {}", poId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
        Optional<POApproval> holdApprovalOpt = poApprovalRepository.findHoldApprovalByPoId(poId);
        if (holdApprovalOpt.isEmpty()) throw new RuntimeException("No HOLD approval found for PO: " + poId);

        POApproval holdApproval = holdApprovalOpt.get();
        HierarchyUser releaser = hierarchyUserRepository.findById(releaserId)
                .orElseThrow(() -> new RuntimeException("Releaser user not found"));

        boolean canRelease = false;
        if (holdApproval.getHeldByUser() != null && holdApproval.getHeldByUser().getId().equals(releaserId)) {
            canRelease = true;
        } else if (releaser.getHierarchyLevel() != null && holdApproval.getHierarchyLevel() != null) {
            String holderCompany   = holdApproval.getHierarchyLevel().getCompanyName();
            String releaserCompany = releaser.getHierarchyLevel().getCompanyName();
            if (holderCompany.equals(releaserCompany) &&
                    releaser.getHierarchyLevel().getLevelOrder() <= holdApproval.getHierarchyLevel().getLevelOrder()) {
                canRelease = true;
            }
        }
        if (!canRelease) throw new RuntimeException("User not authorized to release this hold");

        holdApproval.setStatus(ApprovalActionStatus.PENDING);
        holdApproval.setReleasedByUser(releaser);
        holdApproval.setReleaseRemarks(releaseRemarks);
        holdApproval.setReleasedDate(LocalDateTime.now());
        poApprovalRepository.save(holdApproval);

        po.setStatus(POStatus.PENDING_APPROVAL);
        po.setInternalNotes(null);
        po.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);

        try { emailService.sendPOHoldReleasedEmail(po, releaser, holdApproval.getApproverUser(), releaseRemarks); }
        catch (Exception e) { logger.error("Email failed", e); }
        return "Hold released by " + releaser.getFullName() + ". PO approval continues.";
    }

    // ==================== HISTORY / QUERY ====================
    // ✅ NO CHANGE from your original code

    @Transactional(readOnly = true)
    public List<POApprovalResponse> getApprovalHistory(Long poId) {
        return poApprovalRepository.findByPurchaseOrderIdOrderBySequenceOrderAsc(poId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<POApprovalResponse> getCurrentPendingApproval(Long poId) {
        return poApprovalRepository.findFirstCurrentPendingApproval(poId).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<POApprovalResponse> getPendingApprovalsForUser(Long userId) {
        return poApprovalRepository.findPendingApprovalsByUser(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<POApprovalResponse> getHoldApprovalsForUser(Long userId) {
        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getHierarchyLevel() == null) return List.of();
        return poApprovalRepository.findVisibleHoldApprovals(
                user.getHierarchyLevel().getLevelOrder(),
                user.getHierarchyLevel().getCompanyName())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getPendingApprovalCount(Long userId) {
        return poApprovalRepository.countPendingApprovalsByUser(userId);
    }

    @Transactional(readOnly = true)
    public Long getHoldApprovalCount(Long userId) {
        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getHierarchyLevel() == null) return 0L;
        return poApprovalRepository.countVisibleHoldApprovals(
                user.getHierarchyLevel().getLevelOrder(),
                user.getHierarchyLevel().getCompanyName());
    }

    // ==================== MAPPER ====================
    // ✅ NO CHANGE from your original code

    private POApprovalResponse toResponse(POApproval approval) {
        POApprovalResponse response = new POApprovalResponse();

        response.setId(approval.getId());
        response.setPurchaseOrderId(approval.getPurchaseOrder().getId());
        response.setPoNumber(approval.getPurchaseOrder().getPoNumber());
        response.setGrandTotal(approval.getPurchaseOrder().getGrandTotal());

        response.setCurrencyCode(
            approval.getPurchaseOrder().getCurrencyCode() != null
                ? approval.getPurchaseOrder().getCurrencyCode() : "INR");
        response.setCurrencySymbol(
            approval.getPurchaseOrder().getCurrencySymbol() != null
                ? approval.getPurchaseOrder().getCurrencySymbol() : "₹");

        response.setHierarchyLevelId(approval.getHierarchyLevel().getId());
        response.setHierarchyLevelName(approval.getHierarchyLevel().getLevelName());
        response.setHierarchyLevelOrder(approval.getHierarchyLevel().getLevelOrder());

        response.setStatus(approval.getStatus().name());
        response.setSequenceOrder(approval.getSequenceOrder());
        response.setCreatedAt(approval.getCreatedAt());
        response.setActionDate(approval.getActionDate());

        String comments = approval.getComments();
        if (comments == null || comments.trim().isEmpty()) {
            if (approval.getRejectRemarks() != null) comments = approval.getRejectRemarks();
            else if (approval.getHoldRemarks() != null) comments = approval.getHoldRemarks();
            else if (approval.getReturnRemarks() != null) comments = approval.getReturnRemarks();
        }
        response.setComments(comments);

        if (approval.getApproverUser() != null) {
            response.setApproverUserId(approval.getApproverUser().getId());
            response.setApproverUserName(approval.getApproverUser().getFullName());
            response.setApproverUserEmail(approval.getApproverUser().getEmail());
        }
        if (approval.getPurchaseOrder().getBuyer() != null)
            response.setBuyerName(approval.getPurchaseOrder().getBuyer().getCompanyName());
        if (approval.getPurchaseOrder().getSupplier() != null)
            response.setSupplierName(approval.getPurchaseOrder().getSupplier().getCompanyName());
        if (approval.getPurchaseOrder().getRfq() != null)
            response.setRfqNumber(approval.getPurchaseOrder().getRfq().getRfqNumber());

        // Hold tracking
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

        // Reject tracking
        if (approval.getRejectedByUser() != null) {
            response.setRejectedByUserId(approval.getRejectedByUser().getId());
            response.setRejectedByUserName(approval.getRejectedByUser().getFullName());
            response.setRejectedByUserEmail(approval.getRejectedByUser().getEmail());
        }
        response.setRejectRemarks(approval.getRejectRemarks());
        response.setRejectDate(approval.getRejectDate());

        // Return tracking
        if (approval.getReturnedByUser() != null) {
            response.setReturnedByUserId(approval.getReturnedByUser().getId());
            response.setReturnedByUserName(approval.getReturnedByUser().getFullName());
            response.setReturnedByUserEmail(approval.getReturnedByUser().getEmail());
        }
        response.setReturnRemarks(approval.getReturnRemarks());
        response.setReturnDate(approval.getReturnDate());

        response.setHoldDisplayInfo(approval.getHoldDisplayInfo());
        response.setRejectDisplayInfo(approval.getRejectDisplayInfo());
        response.setReturnDisplayInfo(approval.getReturnDisplayInfo());
        response.setIsOnHold(approval.isOnHold());
        response.setWasHeldAndReleased(approval.wasHeldAndReleased());
        response.setWasRejected(approval.wasRejected());

        return response;
    }
}