package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierApproval;
import com.itti.leadcapturing.model.ApprovalActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierApprovalRepository extends JpaRepository<SupplierApproval, Long> {

    /**
     * Find the current active (PENDING or HOLD) approval for a supplier — lowest sequence first.
     */
    @Query(value = "SELECT * FROM supplier_approvals " +
                   "WHERE supplier_id = :supplierId " +
                   "AND (status = 'PENDING' OR status = 'HOLD') " +
                   "ORDER BY sequence_order ASC LIMIT 1",
           nativeQuery = true)
    Optional<SupplierApproval> findFirstCurrentPendingApproval(@Param("supplierId") Long supplierId);

    /**
     * Get all approvals for a supplier ordered by sequence (for history).
     */
    @Query("SELECT a FROM SupplierApproval a WHERE a.supplier.id = :supplierId ORDER BY a.sequenceOrder ASC")
    List<SupplierApproval> findBySupplierIdOrderBySequenceOrderAsc(@Param("supplierId") Long supplierId);

    /**
     * Find the HOLD approval record for a supplier.
     */
    @Query("SELECT a FROM SupplierApproval a " +
           "WHERE a.supplier.id = :supplierId " +
           "AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD")
    Optional<SupplierApproval> findHoldApprovalBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * Get all pending approvals assigned to a specific hierarchy user.
     */
    @Query("SELECT a FROM SupplierApproval a " +
           "WHERE a.approverUser.id = :userId " +
           "AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING " +
           "ORDER BY a.createdAt DESC")
    List<SupplierApproval> findPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * Get all HOLD approvals visible to a user at or below their hierarchy level.
     */
    @Query("SELECT a FROM SupplierApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName " +
           "ORDER BY a.holdDate DESC")
    List<SupplierApproval> findVisibleHoldApprovals(
            @Param("userLevelOrder") Integer userLevelOrder,
            @Param("companyName") String companyName);

    /**
     * Count pending approvals for a user.
     */
    @Query("SELECT COUNT(a) FROM SupplierApproval a " +
           "WHERE a.approverUser.id = :userId " +
           "AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovalsByUser(@Param("userId") Long userId);

    /**
     * Count visible HOLD approvals for a user.
     */
    @Query("SELECT COUNT(a) FROM SupplierApproval a " +
           "WHERE a.status = com.itti.leadcapturing.model.ApprovalActionStatus.HOLD " +
           "AND a.hierarchyLevel.levelOrder >= :userLevelOrder " +
           "AND a.hierarchyLevel.companyName = :companyName")
    Long countVisibleHoldApprovals(
            @Param("userLevelOrder") Integer userLevelOrder,
            @Param("companyName") String companyName);

    /**
     * Count pending approvals remaining for a supplier (used to check if fully approved).
     */
    @Query("SELECT COUNT(a) FROM SupplierApproval a " +
           "WHERE a.supplier.id = :supplierId " +
           "AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovals(@Param("supplierId") Long supplierId);
}