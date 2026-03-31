// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// public class RFQApprovalService {

//     @Autowired
//     private RFQApprovalRepository approvalRepository;

//     @Autowired
//     private RFQRepository rfqRepository;

//     // ==================== INITIATE APPROVAL WORKFLOW ====================

//     /**
//      * Initialize approval workflow when RFQ is submitted
//      * Creates approval records for all 4 levels
//      */
//     @Transactional
//     public void initiateApprovalWorkflow(Long rfqId) {
//         System.out.println("=".repeat(80));
//         System.out.println("[WORKFLOW] Initiating approval workflow for RFQ ID: " + rfqId);

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found"));

//         // Validate RFQ can be submitted
//         if (!RFQStatus.DRAFT.equals(rfq.getStatus())) {
//             throw new RuntimeException("Only DRAFT RFQs can be submitted for approval");
//         }

//         // Validate suppliers and items exist
//         if (rfq.getSelectedSuppliers() == null || rfq.getSelectedSuppliers().isEmpty()) {
//             throw new RuntimeException("RFQ must have at least one supplier selected");
//         }

//         if (rfq.getItems() == null || rfq.getItems().isEmpty()) {
//             throw new RuntimeException("RFQ must have at least one item");
//         }

//         // Create approval records for all levels
//         int sequence = 1;
//         for (ApprovalLevel level : ApprovalLevel.values()) {
//             RFQApproval approval = new RFQApproval(rfq, level, sequence);
//             approvalRepository.save(approval);
//             System.out.println("  [✓] Created approval level: " + level.getDisplayName() + " (Sequence: " + sequence + ")");
//             sequence++;
//         }

//         // Update RFQ status
//         rfq.setStatus(RFQStatus.AWAITING_APPROVAL);
//         rfq.setUpdatedAt(LocalDateTime.now());
//         rfqRepository.save(rfq);

//         System.out.println("[✓] Approval workflow initiated - Status: AWAITING_APPROVAL");
//         System.out.println("=".repeat(80));
//     }

//     // ==================== APPROVE ====================

//     /**
//      * Approve RFQ at current level
//      * Moves to next level or completes workflow
//      */
//     @Transactional
//     public RFQ approveRFQ(Long rfqId, Long approverId, ApprovalLevel approvalLevel, String comments) {
//         System.out.println("=".repeat(80));
//         System.out.println("[APPROVE] Processing approval");
//         System.out.println("  RFQ ID: " + rfqId);
//         System.out.println("  Approver ID: " + approverId);
//         System.out.println("  Level: " + approvalLevel.getDisplayName());

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found"));

//         // Validate RFQ is awaiting approval
//         if (!RFQStatus.AWAITING_APPROVAL.equals(rfq.getStatus())) {
//             throw new RuntimeException("RFQ is not awaiting approval. Current status: " + rfq.getStatus());
//         }

//         // Get current pending approval
//         RFQApproval currentApproval = approvalRepository.findCurrentPendingApproval(rfqId)
//                 .orElseThrow(() -> new RuntimeException("No pending approval found"));

//         // Validate approver is at correct level
//         if (!currentApproval.getApprovalLevel().equals(approvalLevel)) {
//             throw new RuntimeException("Invalid approval level. Current level: " + currentApproval.getApprovalLevel().getDisplayName());
//         }

//         // Update approval record
//         currentApproval.setStatus(ApprovalActionStatus.APPROVED);
//         currentApproval.setApproverUserId(approverId);
//         currentApproval.setComments(comments);
//         currentApproval.setActionDate(LocalDateTime.now());
//         approvalRepository.save(currentApproval);

//         System.out.println("  [✓] Level approved: " + approvalLevel.getDisplayName());

//         // Check if this is the last level (FINANCE)
//         if (approvalLevel.isLastLevel()) {
//             // All approvals complete - send to suppliers
//             rfq.setStatus(RFQStatus.PUBLISHED);
//             rfq.setApprovalStatus(ApprovalStatus.APPROVED);
//             System.out.println("  [✓✓✓] ALL APPROVALS COMPLETE - RFQ PUBLISHED TO SUPPLIERS");
//         } else {
//             // Move to next level
//             ApprovalLevel nextLevel = approvalLevel.getNext();
//             System.out.println("  [→] Moving to next level: " + nextLevel.getDisplayName());
//         }

//         rfq.setUpdatedAt(LocalDateTime.now());
//         RFQ updated = rfqRepository.save(rfq);

//         System.out.println("=".repeat(80));
//         return updated;
//     }

//     // ==================== REJECT ====================

//     /**
//      * Reject RFQ at current level
//      * Different behavior based on level:
//      * - PROCUREMENT/COO/CEO: Returns to creator for resubmission
//      * - FINANCE: Closes the RFQ
//      */
//     @Transactional
//     public RFQ rejectRFQ(Long rfqId, Long rejectorId, ApprovalLevel approvalLevel, String comments) {
//         System.out.println("=".repeat(80));
//         System.out.println("[REJECT] Processing rejection");
//         System.out.println("  RFQ ID: " + rfqId);
//         System.out.println("  Rejector ID: " + rejectorId);
//         System.out.println("  Level: " + approvalLevel.getDisplayName());

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found"));

//         // Validate RFQ is awaiting approval
//         if (!RFQStatus.AWAITING_APPROVAL.equals(rfq.getStatus())) {
//             throw new RuntimeException("RFQ is not awaiting approval. Current status: " + rfq.getStatus());
//         }

//         // Get current pending approval
//         RFQApproval currentApproval = approvalRepository.findCurrentPendingApproval(rfqId)
//                 .orElseThrow(() -> new RuntimeException("No pending approval found"));

//         // Validate rejector is at correct level
//         if (!currentApproval.getApprovalLevel().equals(approvalLevel)) {
//             throw new RuntimeException("Invalid approval level. Current level: " + currentApproval.getApprovalLevel().getDisplayName());
//         }

//         // Update approval record
//         currentApproval.setStatus(ApprovalActionStatus.REJECTED);
//         currentApproval.setApproverUserId(rejectorId);
//         currentApproval.setComments(comments);
//         currentApproval.setActionDate(LocalDateTime.now());
//         approvalRepository.save(currentApproval);

//         System.out.println("  [✗] Level rejected: " + approvalLevel.getDisplayName());

//         // Handle rejection based on level
//         if (approvalLevel == ApprovalLevel.FINANCE) {
//             // Finance rejection = RFQ CLOSED permanently
//             rfq.setStatus(RFQStatus.CLOSED);
//             rfq.setApprovalStatus(ApprovalStatus.REJECTED);
//             System.out.println("  [✗✗✗] FINANCE REJECTED - RFQ CLOSED");
//         } else {
//             // Other levels: Return to DRAFT for resubmission
//             rfq.setStatus(RFQStatus.DRAFT);
//             rfq.setApprovalStatus(ApprovalStatus.REJECTED);
//             System.out.println("  [←] Returned to DRAFT for resubmission");
//         }

//         rfq.setUpdatedAt(LocalDateTime.now());
//         RFQ updated = rfqRepository.save(rfq);

//         System.out.println("=".repeat(80));
//         return updated;
//     }

//     // ==================== RESUBMIT ====================

//     /**
//      * Resubmit RFQ after rejection
//      * Only available for PROCUREMENT, COO, CEO
//      * Restarts workflow from beginning
//      */
//     @Transactional
//     public RFQ resubmitRFQ(Long rfqId, Long resubmitterId) {
//         System.out.println("=".repeat(80));
//         System.out.println("[RESUBMIT] Processing resubmission");
//         System.out.println("  RFQ ID: " + rfqId);
//         System.out.println("  Resubmitter ID: " + resubmitterId);

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found"));

//         // Validate RFQ is in DRAFT and was rejected
//         if (!RFQStatus.DRAFT.equals(rfq.getStatus())) {
//             throw new RuntimeException("Only DRAFT RFQs can be resubmitted. Current status: " + rfq.getStatus());
//         }

//         if (!ApprovalStatus.REJECTED.equals(rfq.getApprovalStatus())) {
//             throw new RuntimeException("RFQ must be rejected before resubmission");
//         }

//         // Delete old approval records
//         List<RFQApproval> oldApprovals = approvalRepository.findByRfqIdOrderBySequence(rfqId);
//         approvalRepository.deleteAll(oldApprovals);
//         System.out.println("  [✓] Cleared old approval records");

//         // Re-initiate approval workflow
//         int sequence = 1;
//         for (ApprovalLevel level : ApprovalLevel.values()) {
//             RFQApproval approval = new RFQApproval(rfq, level, sequence);
//             approvalRepository.save(approval);
//             sequence++;
//         }
//         System.out.println("  [✓] Created new approval records");

//         // Update RFQ status
//         rfq.setStatus(RFQStatus.AWAITING_APPROVAL);
//         rfq.setApprovalStatus(ApprovalStatus.PENDING);
//         rfq.setUpdatedAt(LocalDateTime.now());
//         RFQ updated = rfqRepository.save(rfq);

//         System.out.println("  [✓✓✓] RFQ RESUBMITTED - Back to PROCUREMENT");
//         System.out.println("=".repeat(80));
//         return updated;
//     }

//     // ==================== READ OPERATIONS ====================

//     /**
//      * Get approval history for an RFQ
//      */
//     public List<RFQApproval> getApprovalHistory(Long rfqId) {
//         return approvalRepository.findByRfqIdOrderBySequence(rfqId);
//     }

//     /**
//      * Get current pending approval level
//      */
//     public Optional<RFQApproval> getCurrentPendingApproval(Long rfqId) {
//         return approvalRepository.findCurrentPendingApproval(rfqId);
//     }

//     /**
//      * Check if all approvals are completed
//      */
//     public boolean areAllApprovalsCompleted(Long rfqId) {
//         return approvalRepository.areAllApprovalsCompleted(rfqId);
//     }

//     /**
//      * Get pending approvals for a specific level
//      */
//     public List<RFQApproval> getPendingApprovalsByLevel(ApprovalLevel level) {
//         return approvalRepository.findByLevelAndStatus(level, ApprovalActionStatus.PENDING);
//     }

//     /**
//      * Count pending approvals at a level
//      */
//     public long countPendingApprovalsByLevel(ApprovalLevel level) {
//         return approvalRepository.countPendingByLevel(level);
//     }
// }