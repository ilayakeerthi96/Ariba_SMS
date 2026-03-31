package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.POApproval;
import com.itti.leadcapturing.model.ApprovalActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface POApprovalRepository extends JpaRepository<POApproval, Long> {

    /**
     * Find the first current pending or on-hold approval for a PO (lowest sequence)
     */
    @Query(value = "SELECT * FROM po_approvals " +
                   "WHERE purchase_order_id = :poId " +
                   "AND (status = 'PENDING' OR status = 'HOLD') " +
                   "ORDER BY sequence_order ASC LIMIT 1",
           nativeQuery = true)
    Optional<POApproval> findFirstCurrentPendingApproval(@Param("poId") Long poId);

    /**
     * Get all approvals for a PO ordered by sequence
     */
    @Query("SELECT a FROM POApproval a WHERE a.purchaseOrder.id = :poId ORDER BY a.sequenceOrder ASC")
    List<POApproval> findByPurchaseOrderIdOrderBySequenceOrderAsc(@Param("poId") Long poId);

    /**
     * Find HOLD approval for a PO
     */
    @Query("SELECT a FROM POApproval a WHERE a.purchaseOrder.id = :poId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD")
    Optional<POApproval> findHoldApprovalByPoId(@Param("poId") Long poId);

    /**
     * Get pending approvals for a specific user
     */
    @Query("SELECT a FROM POApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING ORDER BY a.createdAt DESC")
    List<POApproval> findPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * Get HOLD approvals visible to a user (same or higher hierarchy)
     */
    @Query("SELECT a FROM POApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName " +
           "ORDER BY a.holdDate DESC")
    List<POApproval> findVisibleHoldApprovals(
        @Param("userLevelOrder") Integer userLevelOrder,
        @Param("companyName") String companyName
    );

    /**
     * Count pending approvals for a user
     */
    @Query("SELECT COUNT(a) FROM POApproval a WHERE a.approverUser.id = :userId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * Count visible HOLD approvals for a user
     */
    @Query("SELECT COUNT(a) FROM POApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName")
    Long countVisibleHoldApprovals(
        @Param("userLevelOrder") Integer userLevelOrder,
        @Param("companyName") String companyName
    );

    /**
     * Check if PO has any pending approvals
     */
    @Query("SELECT COUNT(a) FROM POApproval a WHERE a.purchaseOrder.id = :poId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovals(@Param("poId") Long poId);
}