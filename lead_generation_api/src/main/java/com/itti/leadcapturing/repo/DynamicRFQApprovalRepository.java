package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.DynamicRFQApproval;
import com.itti.leadcapturing.model.ApprovalActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicRFQApprovalRepository extends JpaRepository<DynamicRFQApproval, Long> {

    @Query(value = "SELECT * FROM dynamic_rfq_approvals " +
                   "WHERE rfq_id = :rfqId AND (status = 'PENDING' OR status = 'HOLD') " +
                   "ORDER BY sequence_order ASC LIMIT 1", 
           nativeQuery = true)
    Optional<DynamicRFQApproval> findFirstCurrentPendingApproval(@Param("rfqId") Long rfqId);

    /**
     * Get all pending approvals for an RFQ
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING ORDER BY a.sequenceOrder ASC")
    List<DynamicRFQApproval> findAllPendingApprovals(@Param("rfqId") Long rfqId);

    /**
     * Get all approvals for an RFQ (for history)
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId ORDER BY a.sequenceOrder ASC")
    List<DynamicRFQApproval> findByRfqIdOrderBySequenceOrderAsc(@Param("rfqId") Long rfqId);

    /**
     * ✅ ALIAS for backward compatibility
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId ORDER BY a.sequenceOrder ASC")
    List<DynamicRFQApproval> findByRfqIdOrderBySequence(@Param("rfqId") Long rfqId);

    /**
     * Get pending approvals for a specific user
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING ORDER BY a.createdAt DESC")
    List<DynamicRFQApproval> findPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * ✅ ALIAS for backward compatibility
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING ORDER BY a.createdAt DESC")
    List<DynamicRFQApproval> getPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * ✅ Get HOLD approvals for a specific user (who held them)
     * Returns all RFQs currently on HOLD by this user
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.heldByUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD ORDER BY a.holdDate DESC")
    List<DynamicRFQApproval> findHoldApprovalsByUser(@Param("userId") Long userId);

    /**
     * ✅ NEW: Get all HOLD approvals visible to user (current level or lower)
     * Higher hierarchy can see holds from lower levels
     */
    @Query("SELECT a FROM DynamicRFQApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName " +
           "ORDER BY a.holdDate DESC")
    List<DynamicRFQApproval> findVisibleHoldApprovals(
        @Param("userLevelOrder") Integer userLevelOrder,
        @Param("companyName") String companyName
    );

    /**
     * Count pending approvals for a user
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * ✅ ALIAS for backward compatibility
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingByUser(@Param("userId") Long userId);

    /**
     * ✅ Count HOLD approvals for a user (held by them)
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.heldByUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD")
    Long countHoldApprovalsByUser(@Param("userId") Long userId);

    /**
     * ✅ NEW: Count visible HOLD approvals for user (can release)
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName")
    Long countVisibleHoldApprovals(
        @Param("userLevelOrder") Integer userLevelOrder,
        @Param("companyName") String companyName
    );

    /**
     * Check if all levels are complete
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovals(@Param("rfqId") Long rfqId);

    /**
     * ✅ Check if RFQ is on HOLD
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD")
    Long countHoldApprovals(@Param("rfqId") Long rfqId);

    /**
     * ✅ Find who is holding the RFQ
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD")
    Optional<DynamicRFQApproval> findHoldApprovalByRfqId(@Param("rfqId") Long rfqId);

    /**
     * ✅ NEW: Check if user can release hold (same or higher level)
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DynamicRFQApproval a " +
           "JOIN HierarchyUser hu ON hu.id = :userId " +
           "WHERE a.rfq.id = :rfqId " +
           "AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= hu.hierarchyLevel.levelOrder " +
           "AND a.hierarchyLevel.companyName = hu.hierarchyLevel.companyName")
    Boolean canUserReleaseHold(@Param("rfqId") Long rfqId, @Param("userId") Long userId);
    // ==================== ADD THIS METHOD TO DynamicRFQApprovalRepository.java ====================

    /**
     * ✅ NEW: Find all approvals with specific status
     * Used by reminder scheduler
     */
    @Query("SELECT a FROM DynamicRFQApproval a WHERE a.status = :status ORDER BY a.createdAt ASC")
    List<DynamicRFQApproval> findByStatus(@Param("status") ApprovalActionStatus status);

    /**
     * ✅ NEW: Find approvals pending for more than X days (with reminder not sent)
     * More optimized query for reminder checking
     */
    @Query("SELECT a FROM DynamicRFQApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING " +
           "AND a.reminderSentAt IS NULL " +
           "AND a.createdAt < :beforeDate " +
           "ORDER BY a.createdAt ASC")
    List<DynamicRFQApproval> findPendingApprovalsWithoutReminder(@Param("beforeDate") LocalDateTime beforeDate);
}