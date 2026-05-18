// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.dto.SupplierApprovalResponse;
// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// /**
//  * Supplier Approval Service.
//  *
//  * Supported actions:
//  *   - initiateApprovalWorkflow : called right after supplier creation
//  *   - approveSupplier          : approve at current level, advance or fully approve
//  *   - rejectSupplier           : permanently reject
//  *   - holdSupplier             : put on hold by current approver
//  *   - releaseHold              : release hold (original holder or higher level)
//  *
//  * No "Return for Revision" — per requirement.
//  * No emails — per requirement.
//  */
// @Service
// @Transactional
// @Slf4j
// public class SupplierApprovalService {

//     @Autowired
//     private SupplierApprovalRepository approvalRepository;

//     @Autowired
//     private SupplierRepository supplierRepository;

//     @Autowired
//     private HierarchyLevelRepository hierarchyLevelRepository;

//     @Autowired
//     private HierarchyUserRepository hierarchyUserRepository;

//     // ==================== INITIATE WORKFLOW ====================

//     /**
//      * Called immediately after a supplier is created.
//      * Creates the first approval record (highest hierarchy level).
//      * Sets supplier approvalStatus = PENDING.
//      */
//     public String initiateApprovalWorkflow(Long supplierId, String companyName) {
//         log.info("🔵 [SUPPLIER APPROVAL INITIATE] Supplier ID: {}, Company: {}", supplierId, companyName);

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

//         // Resolve company name: use the createdByCompanyName stored on supplier if not passed
//         String resolvedCompany = (companyName != null && !companyName.isBlank())
//                 ? companyName
//                 : supplier.getCreatedByCompanyName();

//         if (resolvedCompany == null || resolvedCompany.isBlank()) {
//             throw new RuntimeException("Cannot determine company name for approval routing. " +
//                     "Please set createdByCompanyName on the supplier before initiating workflow.");
//         }

//         log.info("  📌 Routing approvals for company: {}", resolvedCompany);

//         List<HierarchyLevel> levels = hierarchyLevelRepository
//                 .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(resolvedCompany, true);

//         if (levels.isEmpty()) {
//             throw new RuntimeException("No active hierarchy levels found for company: " + resolvedCompany);
//         }

//         log.info("  📌 Found {} hierarchy levels", levels.size());

//         HierarchyLevel firstLevel = levels.get(0); // Highest levelOrder first
//         List<HierarchyUser> usersAtFirstLevel = hierarchyUserRepository
//                 .findByHierarchyLevelAndIsActiveOrderByIdAsc(firstLevel, true);

//         if (usersAtFirstLevel.isEmpty()) {
//             throw new RuntimeException("No active users found at hierarchy level: " + firstLevel.getLevelName());
//         }

//         HierarchyUser firstApprover = usersAtFirstLevel.get(0);

//         SupplierApproval approval = new SupplierApproval();
//         approval.setSupplier(supplier);
//         approval.setHierarchyLevel(firstLevel);
//         approval.setApproverUser(firstApprover);
//         approval.setStatus(ApprovalActionStatus.PENDING);
//         approval.setSequenceOrder(1);
//         approval.setCreatedAt(LocalDateTime.now());

//         approvalRepository.save(approval);

//         supplier.setApprovalStatus(ApprovalStatus.PENDING);
//         supplierRepository.save(supplier);

//         log.info("  ✅ First approval created — Level: {} (order: {}), Approver: {}",
//                 firstLevel.getLevelName(), firstLevel.getLevelOrder(), firstApprover.getEmail());

//         return "Supplier approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
//     }

//     // ==================== CHECK IF USER IS LAST APPROVER ====================

//     @Transactional(readOnly = true)
//     public boolean isLastApprover(Long supplierId, Long userId) {
//         Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
//         if (currentOpt.isEmpty()) return false;

//         SupplierApproval current = currentOpt.get();
//         if (!current.getApproverUser().getId().equals(userId)) return false;

//         Supplier supplier = current.getSupplier();
//         String companyName = supplier.getCreatedByCompanyName();

//         List<HierarchyLevel> allLevels = hierarchyLevelRepository
//                 .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

//         if (allLevels.isEmpty()) return false;

//         HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1); // Lowest levelOrder
//         return current.getHierarchyLevel().getLevelOrder().equals(lastLevel.getLevelOrder());
//     }

//     // ==================== APPROVE SUPPLIER ====================

//     public String approveSupplier(Long supplierId, Long approverId, String comments) {
//         log.info("🟢 [APPROVE SUPPLIER] Supplier ID: {}, Approver ID: {}", supplierId, approverId);

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

//         Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
//         if (currentOpt.isEmpty()) {
//             throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);
//         }

//         SupplierApproval current = currentOpt.get();

//         if (!current.getApproverUser().getId().equals(approverId)) {
//             throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");
//         }

//         HierarchyUser currentApprover = current.getApproverUser();
//         boolean isLast = isLastApprover(supplierId, approverId);

//         // Mark current level as APPROVED
//         current.setStatus(ApprovalActionStatus.APPROVED);
//         current.setComments(comments);
//         current.setActionDate(LocalDateTime.now());
//         approvalRepository.save(current);

//         log.info("  ✅ Approved at level {} (order: {}) by {}",
//                 current.getHierarchyLevel().getLevelName(),
//                 current.getHierarchyLevel().getLevelOrder(),
//                 currentApprover.getFullName());

//         // If last level — fully approve the supplier
//         if (isLast) {
//             supplier.setApprovalStatus(ApprovalStatus.APPROVED);
//             supplier.setApprovalDate(LocalDateTime.now());
//             supplier.setApprovalComments(null);
//             supplierRepository.save(supplier);

//             log.info("  🎉 SUPPLIER FULLY APPROVED: {}", supplier.getCompanyName());
//             return "Supplier '" + supplier.getCompanyName() + "' fully approved and is now active.";
//         }

//         // Not last — find the next level and create a pending approval for it
//         String companyName = supplier.getCreatedByCompanyName();
//         List<HierarchyLevel> allLevels = hierarchyLevelRepository
//                 .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

//         int currentIndex = -1;
//         for (int i = 0; i < allLevels.size(); i++) {
//             if (allLevels.get(i).getId().equals(current.getHierarchyLevel().getId())) {
//                 currentIndex = i;
//                 break;
//             }
//         }

//         if (currentIndex != -1 && currentIndex < allLevels.size() - 1) {
//             HierarchyLevel nextLevel = allLevels.get(currentIndex + 1);
//             List<HierarchyUser> usersAtNextLevel = hierarchyUserRepository
//                     .findByHierarchyLevelAndIsActiveOrderByIdAsc(nextLevel, true);

//             if (!usersAtNextLevel.isEmpty()) {
//                 HierarchyUser nextApprover = usersAtNextLevel.get(0);

//                 SupplierApproval nextApproval = new SupplierApproval();
//                 nextApproval.setSupplier(supplier);
//                 nextApproval.setHierarchyLevel(nextLevel);
//                 nextApproval.setApproverUser(nextApprover);
//                 nextApproval.setStatus(ApprovalActionStatus.PENDING);
//                 nextApproval.setSequenceOrder(current.getSequenceOrder() + 1);
//                 nextApproval.setCreatedAt(LocalDateTime.now());
//                 approvalRepository.save(nextApproval);

//                 supplierRepository.save(supplier);

//                 log.info("  ➡️ Forwarded to next level: {} (order: {})",
//                         nextLevel.getLevelName(), nextLevel.getLevelOrder());

//                 return "Supplier approved at " + current.getHierarchyLevel().getLevelName()
//                         + ". Forwarded to " + nextLevel.getLevelName() + " for approval.";
//             }
//         }

//         // Safety fallback — no more levels found
//         supplier.setApprovalStatus(ApprovalStatus.APPROVED);
//         supplier.setApprovalDate(LocalDateTime.now());
//         supplierRepository.save(supplier);
//         return "Supplier approved at all levels.";
//     }

//     // ==================== REJECT SUPPLIER ====================

//     public String rejectSupplier(Long supplierId, Long rejectorId, String rejectRemarks) {
//         log.info("🔴 [REJECT SUPPLIER] Supplier ID: {}, Rejector ID: {}", supplierId, rejectorId);

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

//         Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
//         if (currentOpt.isEmpty()) {
//             throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);
//         }

//         SupplierApproval current = currentOpt.get();

//         if (!current.getApproverUser().getId().equals(rejectorId)) {
//             throw new RuntimeException("User " + rejectorId + " is not authorized to reject at this level");
//         }

//         HierarchyUser rejector = hierarchyUserRepository.findById(rejectorId)
//                 .orElseThrow(() -> new RuntimeException("Rejector user not found"));

//         String remarks = (rejectRemarks != null && !rejectRemarks.trim().isEmpty())
//                 ? rejectRemarks.trim() : "No remarks provided";

//         current.setStatus(ApprovalActionStatus.REJECTED);
//         current.setComments(remarks);
//         current.setRejectRemarks(remarks);
//         current.setRejectedByUser(rejector);
//         current.setRejectDate(LocalDateTime.now());
//         current.setActionDate(LocalDateTime.now());
//         approvalRepository.saveAndFlush(current);

//         String rejectInfo = String.format("Rejected by %s (%s). Reason: %s",
//                 rejector.getFullName(),
//                 current.getHierarchyLevel().getLevelName(),
//                 remarks);

//         supplier.setApprovalStatus(ApprovalStatus.REJECTED);
//         supplier.setApprovalComments(rejectInfo.length() > 500 ? rejectInfo.substring(0, 500) : rejectInfo);
//         supplierRepository.saveAndFlush(supplier);

//         log.info("  ✅ Supplier REJECTED: {}", supplier.getCompanyName());
//         return String.format("Supplier permanently rejected by %s at %s level. Reason: %s",
//                 rejector.getFullName(),
//                 current.getHierarchyLevel().getLevelName(),
//                 remarks);
//     }

//     // ==================== HOLD SUPPLIER ====================

//     public String holdSupplier(Long supplierId, Long holderId, String holdRemarks) {
//         log.info("⏸️ [HOLD SUPPLIER] Supplier ID: {}, Holder ID: {}", supplierId, holderId);

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

//         Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
//         if (currentOpt.isEmpty()) {
//             throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);
//         }

//         SupplierApproval current = currentOpt.get();

//         if (!current.getApproverUser().getId().equals(holderId)) {
//             throw new RuntimeException("Only the current approver can put this supplier on hold");
//         }

//         if (holdRemarks == null || holdRemarks.trim().isEmpty()) {
//             throw new RuntimeException("Hold remarks are required");
//         }

//         HierarchyUser holder = hierarchyUserRepository.findById(holderId)
//                 .orElseThrow(() -> new RuntimeException("Holder user not found"));

//         current.setStatus(ApprovalActionStatus.HOLD);
//         current.setHeldByUser(holder);
//         current.setHoldRemarks(holdRemarks);
//         current.setHoldDate(LocalDateTime.now());
//         current.setActionDate(LocalDateTime.now());
//         approvalRepository.save(current);

//         String holdInfo = String.format("On HOLD by %s (%s). Reason: %s",
//                 holder.getFullName(), current.getHierarchyLevel().getLevelName(), holdRemarks);

//         supplier.setApprovalStatus(ApprovalStatus.PENDING); // Still pending — just paused
//         supplier.setApprovalComments(holdInfo.length() > 500 ? holdInfo.substring(0, 500) : holdInfo);
//         supplierRepository.save(supplier);

//         log.info("  ✅ Supplier ON HOLD: {}", supplier.getCompanyName());
//         return String.format("Supplier put on HOLD by %s at %s level. Reason: %s",
//                 holder.getFullName(), current.getHierarchyLevel().getLevelName(), holdRemarks);
//     }

//     // ==================== RELEASE HOLD ====================

//     public String releaseHold(Long supplierId, Long releaserId, String releaseRemarks) {
//         log.info("▶️ [RELEASE SUPPLIER HOLD] Supplier ID: {}, Releaser ID: {}", supplierId, releaserId);

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

//         Optional<SupplierApproval> holdOpt = approvalRepository.findHoldApprovalBySupplierId(supplierId);
//         if (holdOpt.isEmpty()) {
//             throw new RuntimeException("No HOLD approval found for Supplier ID: " + supplierId);
//         }

//         SupplierApproval holdApproval = holdOpt.get();

//         HierarchyUser releaser = hierarchyUserRepository.findById(releaserId)
//                 .orElseThrow(() -> new RuntimeException("Releaser user not found"));

//         // Authorization: original holder OR same/higher level in same company
//         boolean canRelease = false;
//         if (holdApproval.getHeldByUser() != null &&
//                 holdApproval.getHeldByUser().getId().equals(releaserId)) {
//             canRelease = true;
//         } else if (releaser.getHierarchyLevel() != null && holdApproval.getHierarchyLevel() != null) {
//             String holderCompany = holdApproval.getHierarchyLevel().getCompanyName();
//             String releaserCompany = releaser.getHierarchyLevel().getCompanyName();
//             if (holderCompany.equals(releaserCompany) &&
//                     releaser.getHierarchyLevel().getLevelOrder()
//                             <= holdApproval.getHierarchyLevel().getLevelOrder()) {
//                 canRelease = true;
//             }
//         }

//         if (!canRelease) {
//             throw new RuntimeException("User not authorized to release this hold");
//         }

//         holdApproval.setStatus(ApprovalActionStatus.PENDING);
//         holdApproval.setReleasedByUser(releaser);
//         holdApproval.setReleaseRemarks(releaseRemarks);
//         holdApproval.setReleasedDate(LocalDateTime.now());
//         approvalRepository.save(holdApproval);

//         supplier.setApprovalComments(null);
//         supplierRepository.save(supplier);

//         log.info("  ✅ Supplier hold released: {}", supplier.getCompanyName());
//         return "Hold released by " + releaser.getFullName() + ". Supplier approval continues.";
//     }

//     // ==================== QUERY METHODS ====================

//     @Transactional(readOnly = true)
//     public List<SupplierApprovalResponse> getApprovalHistory(Long supplierId) {
//         log.info("📜 [SUPPLIER APPROVAL HISTORY] Supplier ID: {}", supplierId);
//         return approvalRepository.findBySupplierIdOrderBySequenceOrderAsc(supplierId)
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public Optional<SupplierApprovalResponse> getCurrentPendingApproval(Long supplierId) {
//         return approvalRepository.findFirstCurrentPendingApproval(supplierId).map(this::toResponse);
//     }

//     @Transactional(readOnly = true)
//     public List<SupplierApprovalResponse> getPendingApprovalsForUser(Long userId) {
//         log.info("📋 [PENDING SUPPLIER APPROVALS] User ID: {}", userId);
//         return approvalRepository.findPendingApprovalsByUser(userId)
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public List<SupplierApprovalResponse> getHoldApprovalsForUser(Long userId) {
//         log.info("⏸️ [HOLD SUPPLIER APPROVALS] User ID: {}", userId);
//         HierarchyUser user = hierarchyUserRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (user.getHierarchyLevel() == null) return List.of();
//         return approvalRepository.findVisibleHoldApprovals(
//                         user.getHierarchyLevel().getLevelOrder(),
//                         user.getHierarchyLevel().getCompanyName())
//                 .stream().map(this::toResponse).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public Long getPendingApprovalCount(Long userId) {
//         return approvalRepository.countPendingApprovalsByUser(userId);
//     }

//     @Transactional(readOnly = true)
//     public Long getHoldApprovalCount(Long userId) {
//         HierarchyUser user = hierarchyUserRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (user.getHierarchyLevel() == null) return 0L;
//         return approvalRepository.countVisibleHoldApprovals(
//                 user.getHierarchyLevel().getLevelOrder(),
//                 user.getHierarchyLevel().getCompanyName());
//     }

//     // ==================== MAPPER ====================

//     private SupplierApprovalResponse toResponse(SupplierApproval a) {
//         SupplierApprovalResponse r = new SupplierApprovalResponse();
//         r.setId(a.getId());
//         r.setSupplierId(a.getSupplier().getId());
//         r.setSupplierCompanyName(a.getSupplier().getCompanyName());
//         r.setHierarchyLevelId(a.getHierarchyLevel().getId());
//         r.setHierarchyLevelName(a.getHierarchyLevel().getLevelName());
//         r.setHierarchyLevelOrder(a.getHierarchyLevel().getLevelOrder());
//         r.setStatus(a.getStatus().name());
//         r.setSequenceOrder(a.getSequenceOrder());
//         r.setCreatedAt(a.getCreatedAt());
//         r.setActionDate(a.getActionDate());

//         // Resolve comments — fall back to rejectRemarks or holdRemarks if comments is blank
//         String comments = a.getComments();
//         if (comments == null || comments.isBlank()) {
//             if (a.getRejectRemarks() != null) comments = a.getRejectRemarks();
//             else if (a.getHoldRemarks() != null) comments = a.getHoldRemarks();
//         }
//         r.setComments(comments);

//         if (a.getApproverUser() != null) {
//             r.setApproverUserId(a.getApproverUser().getId());
//             r.setApproverUserName(a.getApproverUser().getFullName());
//             r.setApproverUserEmail(a.getApproverUser().getEmail());
//         }

//         // Hold tracking
//         if (a.getHeldByUser() != null) {
//             r.setHeldByUserId(a.getHeldByUser().getId());
//             r.setHeldByUserName(a.getHeldByUser().getFullName());
//             r.setHeldByUserEmail(a.getHeldByUser().getEmail());
//         }
//         r.setHoldRemarks(a.getHoldRemarks());
//         r.setHoldDate(a.getHoldDate());

//         if (a.getReleasedByUser() != null) {
//             r.setReleasedByUserId(a.getReleasedByUser().getId());
//             r.setReleasedByUserName(a.getReleasedByUser().getFullName());
//             r.setReleasedByUserEmail(a.getReleasedByUser().getEmail());
//         }
//         r.setReleaseRemarks(a.getReleaseRemarks());
//         r.setReleasedDate(a.getReleasedDate());

//         // Reject tracking
//         if (a.getRejectedByUser() != null) {
//             r.setRejectedByUserId(a.getRejectedByUser().getId());
//             r.setRejectedByUserName(a.getRejectedByUser().getFullName());
//             r.setRejectedByUserEmail(a.getRejectedByUser().getEmail());
//         }
//         r.setRejectRemarks(a.getRejectRemarks());
//         r.setRejectDate(a.getRejectDate());

//         // Display helpers
//         r.setHoldDisplayInfo(a.getHoldDisplayInfo());
//         r.setRejectDisplayInfo(a.getRejectDisplayInfo());
//         r.setIsOnHold(a.isOnHold());
//         r.setWasHeldAndReleased(a.wasHeldAndReleased());
//         r.setWasRejected(a.wasRejected());

//         return r;
//     }
// }


package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.SupplierApprovalResponse;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SupplierApprovalService {

    @Autowired private SupplierApprovalRepository approvalRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private HierarchyLevelRepository hierarchyLevelRepository;
    @Autowired private HierarchyUserRepository hierarchyUserRepository;
    @Autowired private EmailService emailService;

    // ==================== INITIATE WORKFLOW ====================

    public String initiateApprovalWorkflow(Long supplierId, String companyName) {
        log.info("🔵 [SUPPLIER APPROVAL INITIATE] Supplier ID: {}, Company: {}", supplierId, companyName);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

        String resolvedCompany = (companyName != null && !companyName.isBlank())
                ? companyName : supplier.getCreatedByCompanyName();

        if (resolvedCompany == null || resolvedCompany.isBlank()) {
            throw new RuntimeException("Cannot determine company name for approval routing.");
        }

        List<HierarchyLevel> levels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(resolvedCompany, true);

        if (levels.isEmpty())
            throw new RuntimeException("No active hierarchy levels found for company: " + resolvedCompany);

        HierarchyLevel firstLevel = levels.get(0);
        List<HierarchyUser> usersAtFirstLevel = hierarchyUserRepository
                .findByHierarchyLevelAndIsActiveOrderByIdAsc(firstLevel, true);

        if (usersAtFirstLevel.isEmpty())
            throw new RuntimeException("No active users at hierarchy level: " + firstLevel.getLevelName());

        HierarchyUser firstApprover = usersAtFirstLevel.get(0);

        SupplierApproval approval = new SupplierApproval();
        approval.setSupplier(supplier);
        approval.setHierarchyLevel(firstLevel);
        approval.setApproverUser(firstApprover);
        approval.setStatus(ApprovalActionStatus.PENDING);
        approval.setSequenceOrder(1);
        approval.setCreatedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        supplier.setApprovalStatus(ApprovalStatus.PENDING);
        supplierRepository.save(supplier);

        // ✅ EMAIL 1: Notify buyer/org admin that a new supplier has registered
        notifyBuyerOfSupplierRegistration(supplier, resolvedCompany, firstApprover, firstLevel);

        log.info("✅ Approval workflow initiated — Approver: {}, Level: {}",
                firstApprover.getEmail(), firstLevel.getLevelName());

        return "Supplier approval workflow initiated. Pending approval from " + firstLevel.getLevelName();
    }

    // ==================== EMAIL: NOTIFY BUYER ON SUPPLIER REGISTRATION ====================

    private void notifyBuyerOfSupplierRegistration(Supplier supplier, String companyName,
                                                    HierarchyUser firstApprover, HierarchyLevel firstLevel) {
        try {
            // Notify the first approver in hierarchy about the new supplier
            emailService.sendSupplierRegistrationToBuyer(supplier, firstApprover, firstLevel);
            log.info("📧 Registration email sent to buyer/approver: {}", firstApprover.getEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send registration email", e);
        }
    }

    // ==================== CHECK LAST APPROVER ====================

    @Transactional(readOnly = true)
    public boolean isLastApprover(Long supplierId, Long userId) {
        Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
        if (currentOpt.isEmpty()) return false;

        SupplierApproval current = currentOpt.get();
        if (!current.getApproverUser().getId().equals(userId)) return false;

        Supplier supplier = current.getSupplier();
        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(supplier.getCreatedByCompanyName(), true);

        if (allLevels.isEmpty()) return false;
        HierarchyLevel lastLevel = allLevels.get(allLevels.size() - 1);
        return current.getHierarchyLevel().getLevelOrder().equals(lastLevel.getLevelOrder());
    }

    // ==================== APPROVE SUPPLIER ====================

    public String approveSupplier(Long supplierId, Long approverId, String comments) {
        log.info("🟢 [APPROVE SUPPLIER] Supplier ID: {}, Approver ID: {}", supplierId, approverId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
        if (currentOpt.isEmpty())
            throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);

        SupplierApproval current = currentOpt.get();

        if (!current.getApproverUser().getId().equals(approverId))
            throw new RuntimeException("User " + approverId + " is not authorized to approve at this level");

        boolean isLast = isLastApprover(supplierId, approverId);

        current.setStatus(ApprovalActionStatus.APPROVED);
        current.setComments(comments);
        current.setActionDate(LocalDateTime.now());
        approvalRepository.save(current);

        log.info("✅ Approved at level: {}", current.getHierarchyLevel().getLevelName());

        if (isLast) {
            supplier.setApprovalStatus(ApprovalStatus.APPROVED);
            supplier.setApprovalDate(LocalDateTime.now());
            supplier.setApprovalComments(null);
            supplierRepository.save(supplier);

            // ✅ EMAIL 2: Notify supplier they have been fully approved
            emailService.sendSupplierFullyApproved(supplier, current.getApproverUser(), current.getHierarchyLevel());

            log.info("🎉 SUPPLIER FULLY APPROVED: {}", supplier.getCompanyName());
            return "Supplier '" + supplier.getCompanyName() + "' fully approved and is now active.";
        }

        // Advance to next level
        String companyName = supplier.getCreatedByCompanyName();
        List<HierarchyLevel> allLevels = hierarchyLevelRepository
                .findByCompanyNameAndIsActiveOrderByLevelOrderDesc(companyName, true);

        int currentIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getId().equals(current.getHierarchyLevel().getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1 && currentIndex < allLevels.size() - 1) {
            HierarchyLevel nextLevel = allLevels.get(currentIndex + 1);
            List<HierarchyUser> usersAtNextLevel = hierarchyUserRepository
                    .findByHierarchyLevelAndIsActiveOrderByIdAsc(nextLevel, true);

            if (!usersAtNextLevel.isEmpty()) {
                HierarchyUser nextApprover = usersAtNextLevel.get(0);

                SupplierApproval nextApproval = new SupplierApproval();
                nextApproval.setSupplier(supplier);
                nextApproval.setHierarchyLevel(nextLevel);
                nextApproval.setApproverUser(nextApprover);
                nextApproval.setStatus(ApprovalActionStatus.PENDING);
                nextApproval.setSequenceOrder(current.getSequenceOrder() + 1);
                nextApproval.setCreatedAt(LocalDateTime.now());
                approvalRepository.save(nextApproval);

                supplierRepository.save(supplier);

                // Notify next approver
                emailService.sendSupplierApprovalForwarded(supplier, current.getApproverUser(),
                        nextApprover, nextLevel, comments);

                log.info("➡️ Forwarded to: {} ({})", nextLevel.getLevelName(), nextApprover.getEmail());
                return "Supplier approved at " + current.getHierarchyLevel().getLevelName()
                        + ". Forwarded to " + nextLevel.getLevelName() + " for approval.";
            }
        }

        // Safety fallback
        supplier.setApprovalStatus(ApprovalStatus.APPROVED);
        supplier.setApprovalDate(LocalDateTime.now());
        supplierRepository.save(supplier);
        emailService.sendSupplierFullyApproved(supplier, current.getApproverUser(), current.getHierarchyLevel());
        return "Supplier approved at all levels.";
    }

    // ==================== REJECT SUPPLIER ====================

    public String rejectSupplier(Long supplierId, Long rejectorId, String rejectRemarks) {
        log.info("🔴 [REJECT SUPPLIER] Supplier ID: {}, Rejector ID: {}", supplierId, rejectorId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
        if (currentOpt.isEmpty())
            throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);

        SupplierApproval current = currentOpt.get();

        if (!current.getApproverUser().getId().equals(rejectorId))
            throw new RuntimeException("User " + rejectorId + " is not authorized to reject at this level");

        HierarchyUser rejector = hierarchyUserRepository.findById(rejectorId)
                .orElseThrow(() -> new RuntimeException("Rejector user not found"));

        String remarks = (rejectRemarks != null && !rejectRemarks.trim().isEmpty())
                ? rejectRemarks.trim() : "No remarks provided";

        current.setStatus(ApprovalActionStatus.REJECTED);
        current.setComments(remarks);
        current.setRejectRemarks(remarks);
        current.setRejectedByUser(rejector);
        current.setRejectDate(LocalDateTime.now());
        current.setActionDate(LocalDateTime.now());
        approvalRepository.saveAndFlush(current);

        String rejectInfo = String.format("Rejected by %s (%s). Reason: %s",
                rejector.getFullName(), current.getHierarchyLevel().getLevelName(), remarks);

        supplier.setApprovalStatus(ApprovalStatus.REJECTED);
        supplier.setApprovalComments(rejectInfo.length() > 500 ? rejectInfo.substring(0, 500) : rejectInfo);
        supplierRepository.saveAndFlush(supplier);

        // ✅ EMAIL: Notify supplier they were rejected
        emailService.sendSupplierRejected(supplier, rejector, current.getHierarchyLevel(), remarks);

        log.info("✅ Supplier REJECTED: {}", supplier.getCompanyName());
        return String.format("Supplier permanently rejected by %s at %s. Reason: %s",
                rejector.getFullName(), current.getHierarchyLevel().getLevelName(), remarks);
    }

    // ==================== HOLD SUPPLIER ====================

    public String holdSupplier(Long supplierId, Long holderId, String holdRemarks) {
        log.info("⏸️ [HOLD SUPPLIER] Supplier ID: {}, Holder ID: {}", supplierId, holderId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
        if (currentOpt.isEmpty())
            throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);

        SupplierApproval current = currentOpt.get();

        if (!current.getApproverUser().getId().equals(holderId))
            throw new RuntimeException("Only the current approver can put this supplier on hold");

        if (holdRemarks == null || holdRemarks.trim().isEmpty())
            throw new RuntimeException("Hold remarks are required");

        HierarchyUser holder = hierarchyUserRepository.findById(holderId)
                .orElseThrow(() -> new RuntimeException("Holder user not found"));

        current.setStatus(ApprovalActionStatus.HOLD);
        current.setHeldByUser(holder);
        current.setHoldRemarks(holdRemarks);
        current.setHoldDate(LocalDateTime.now());
        current.setActionDate(LocalDateTime.now());
        approvalRepository.save(current);

        String holdInfo = String.format("On HOLD by %s (%s). Reason: %s",
                holder.getFullName(), current.getHierarchyLevel().getLevelName(), holdRemarks);

        supplier.setApprovalStatus(ApprovalStatus.PENDING);
        supplier.setApprovalComments(holdInfo.length() > 500 ? holdInfo.substring(0, 500) : holdInfo);
        supplierRepository.save(supplier);

        // ✅ EMAIL: Notify supplier their approval is on hold
        emailService.sendSupplierOnHold(supplier, holder, current.getHierarchyLevel(), holdRemarks);

        log.info("✅ Supplier ON HOLD: {}", supplier.getCompanyName());
        return String.format("Supplier put on HOLD by %s at %s. Reason: %s",
                holder.getFullName(), current.getHierarchyLevel().getLevelName(), holdRemarks);
    }

    // ==================== RELEASE HOLD ====================

    public String releaseHold(Long supplierId, Long releaserId, String releaseRemarks) {
        log.info("▶️ [RELEASE HOLD] Supplier ID: {}, Releaser ID: {}", supplierId, releaserId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        Optional<SupplierApproval> holdOpt = approvalRepository.findHoldApprovalBySupplierId(supplierId);
        if (holdOpt.isEmpty())
            throw new RuntimeException("No HOLD approval found for Supplier ID: " + supplierId);

        SupplierApproval holdApproval = holdOpt.get();

        HierarchyUser releaser = hierarchyUserRepository.findById(releaserId)
                .orElseThrow(() -> new RuntimeException("Releaser user not found"));

        boolean canRelease = false;
        if (holdApproval.getHeldByUser() != null
                && holdApproval.getHeldByUser().getId().equals(releaserId)) {
            canRelease = true;
        } else if (releaser.getHierarchyLevel() != null && holdApproval.getHierarchyLevel() != null) {
            String holderCompany  = holdApproval.getHierarchyLevel().getCompanyName();
            String releaserCompany = releaser.getHierarchyLevel().getCompanyName();
            if (holderCompany.equals(releaserCompany) &&
                    releaser.getHierarchyLevel().getLevelOrder()
                            <= holdApproval.getHierarchyLevel().getLevelOrder()) {
                canRelease = true;
            }
        }

        if (!canRelease)
            throw new RuntimeException("User not authorized to release this hold");

        holdApproval.setStatus(ApprovalActionStatus.PENDING);
        holdApproval.setReleasedByUser(releaser);
        holdApproval.setReleaseRemarks(releaseRemarks);
        holdApproval.setReleasedDate(LocalDateTime.now());
        approvalRepository.save(holdApproval);

        supplier.setApprovalComments(null);
        supplierRepository.save(supplier);

        log.info("✅ Hold released for supplier: {}", supplier.getCompanyName());
        return "Hold released by " + releaser.getFullName() + ". Supplier approval continues.";
    }

    // ==================== NEED MORE INFO ====================

    public String requestMoreInfo(Long supplierId, Long requesterId, String infoRequest) {
        log.info("❓ [NEED MORE INFO] Supplier ID: {}, Requester ID: {}", supplierId, requesterId);

        if (infoRequest == null || infoRequest.trim().isEmpty())
            throw new RuntimeException("Info request message is required");

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        Optional<SupplierApproval> currentOpt = approvalRepository.findFirstCurrentPendingApproval(supplierId);
        if (currentOpt.isEmpty())
            throw new RuntimeException("No pending approval found for Supplier ID: " + supplierId);

        SupplierApproval current = currentOpt.get();

        if (!current.getApproverUser().getId().equals(requesterId))
            throw new RuntimeException("Only the current approver can request more information");

        HierarchyUser requester = hierarchyUserRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester user not found"));

        // Keep status as PENDING but record the info request
        current.setInfoRequestedByUser(requester);
        current.setInfoRequest(infoRequest.trim());
        current.setInfoRequestDate(LocalDateTime.now());
        current.setStatus(ApprovalActionStatus.NEED_MORE_INFO);
        current.setActionDate(LocalDateTime.now());
        approvalRepository.save(current);

        String infoComment = String.format("More info requested by %s (%s): %s",
                requester.getFullName(), current.getHierarchyLevel().getLevelName(), infoRequest);
        supplier.setApprovalComments(infoComment.length() > 500
                ? infoComment.substring(0, 500) : infoComment);
        supplierRepository.save(supplier);

        // ✅ EMAIL: Send need-more-info email to supplier's contact
        emailService.sendNeedMoreInfoToSupplier(supplier, requester, current.getHierarchyLevel(), infoRequest);

        log.info("✅ Need more info sent to supplier: {}", supplier.getContactPersonEmail());
        return String.format("Information request sent to %s (%s) at %s.",
                supplier.getCompanyName(), supplier.getContactPersonEmail(),
                current.getHierarchyLevel().getLevelName());
    }

    // ==================== QUERY METHODS ====================

    @Transactional(readOnly = true)
    public List<SupplierApprovalResponse> getApprovalHistory(Long supplierId) {
        return approvalRepository.findBySupplierIdOrderBySequenceOrderAsc(supplierId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SupplierApprovalResponse> getCurrentPendingApproval(Long supplierId) {
        return approvalRepository.findFirstCurrentPendingApproval(supplierId).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SupplierApprovalResponse> getPendingApprovalsForUser(Long userId) {
        return approvalRepository.findPendingApprovalsByUser(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierApprovalResponse> getHoldApprovalsForUser(Long userId) {
        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getHierarchyLevel() == null) return List.of();
        return approvalRepository.findVisibleHoldApprovals(
                        user.getHierarchyLevel().getLevelOrder(),
                        user.getHierarchyLevel().getCompanyName())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getPendingApprovalCount(Long userId) {
        return approvalRepository.countPendingApprovalsByUser(userId);
    }

    @Transactional(readOnly = true)
    public Long getHoldApprovalCount(Long userId) {
        HierarchyUser user = hierarchyUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getHierarchyLevel() == null) return 0L;
        return approvalRepository.countVisibleHoldApprovals(
                user.getHierarchyLevel().getLevelOrder(),
                user.getHierarchyLevel().getCompanyName());
    }

    // ==================== MAPPER ====================

    private SupplierApprovalResponse toResponse(SupplierApproval a) {
        SupplierApprovalResponse r = new SupplierApprovalResponse();
        r.setId(a.getId());
        r.setSupplierId(a.getSupplier().getId());
        r.setSupplierCompanyName(a.getSupplier().getCompanyName());
        r.setSupplierContactEmail(a.getSupplier().getContactPersonEmail());
        r.setSupplierContactName(a.getSupplier().getContactPersonName());

        // Try to get industrySector if available
        try { r.setIndustrySector(a.getSupplier().getIndustrySector()); }
        catch (Exception ignored) {}

        r.setHierarchyLevelId(a.getHierarchyLevel().getId());
        r.setHierarchyLevelName(a.getHierarchyLevel().getLevelName());
        r.setHierarchyLevelOrder(a.getHierarchyLevel().getLevelOrder());
        r.setStatus(a.getStatus().name());
        r.setSequenceOrder(a.getSequenceOrder());
        r.setCreatedAt(a.getCreatedAt());
        r.setActionDate(a.getActionDate());

        // Resolve comments
        String comments = a.getComments();
        if (comments == null || comments.isBlank()) {
            if (a.getRejectRemarks() != null)   comments = a.getRejectRemarks();
            else if (a.getHoldRemarks() != null) comments = a.getHoldRemarks();
            else if (a.getInfoRequest() != null) comments = a.getInfoRequest();
        }
        r.setComments(comments);

        if (a.getApproverUser() != null) {
            r.setApproverUserId(a.getApproverUser().getId());
            r.setApproverUserName(a.getApproverUser().getFullName());
            r.setApproverUserEmail(a.getApproverUser().getEmail());
        }

        // Hold tracking
        if (a.getHeldByUser() != null) {
            r.setHeldByUserId(a.getHeldByUser().getId());
            r.setHeldByUserName(a.getHeldByUser().getFullName());
            r.setHeldByUserEmail(a.getHeldByUser().getEmail());
        }
        r.setHoldRemarks(a.getHoldRemarks());
        r.setHoldDate(a.getHoldDate());

        if (a.getReleasedByUser() != null) {
            r.setReleasedByUserId(a.getReleasedByUser().getId());
            r.setReleasedByUserName(a.getReleasedByUser().getFullName());
            r.setReleasedByUserEmail(a.getReleasedByUser().getEmail());
        }
        r.setReleaseRemarks(a.getReleaseRemarks());
        r.setReleasedDate(a.getReleasedDate());

        // Reject tracking
        if (a.getRejectedByUser() != null) {
            r.setRejectedByUserId(a.getRejectedByUser().getId());
            r.setRejectedByUserName(a.getRejectedByUser().getFullName());
            r.setRejectedByUserEmail(a.getRejectedByUser().getEmail());
        }
        r.setRejectRemarks(a.getRejectRemarks());
        r.setRejectDate(a.getRejectDate());

        // Need more info tracking
        if (a.getInfoRequestedByUser() != null) {
            r.setInfoRequestedByUserId(a.getInfoRequestedByUser().getId());
            r.setInfoRequestedByUserName(a.getInfoRequestedByUser().getFullName());
            r.setInfoRequestedByUserEmail(a.getInfoRequestedByUser().getEmail());
        }
        r.setInfoRequest(a.getInfoRequest());
        r.setInfoRequestDate(a.getInfoRequestDate());
        r.setInfoResponse(a.getInfoResponse());
        r.setInfoResponseDate(a.getInfoResponseDate());
        r.setNeedMoreInfoDisplayInfo(a.getNeedMoreInfoDisplayInfo());
        r.setIsNeedMoreInfo(a.isNeedMoreInfo());

        // Display helpers
        r.setHoldDisplayInfo(a.getHoldDisplayInfo());
        r.setRejectDisplayInfo(a.getRejectDisplayInfo());
        r.setIsOnHold(a.isOnHold());
        r.setWasHeldAndReleased(a.wasHeldAndReleased());
        r.setWasRejected(a.wasRejected());

        return r;
    }
}