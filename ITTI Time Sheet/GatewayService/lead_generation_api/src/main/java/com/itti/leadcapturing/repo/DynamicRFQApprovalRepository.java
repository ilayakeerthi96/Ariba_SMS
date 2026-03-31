
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.DynamicRFQApproval;
import com.itti.leadcapturing.model.ApprovalActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicRFQApprovalRepository extends JpaRepository<DynamicRFQApproval, Long> {

    /**
     * ✅ CRITICAL FIX: Use native query with LIMIT 1 to get only first pending approval
     */
    @Query(value = "SELECT * FROM dynamic_rfq_approvals " +
                   "WHERE rfq_id = :rfqId AND status = 'PENDING' " +
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
     * Check if all levels are complete
     */
    @Query("SELECT COUNT(a) FROM DynamicRFQApproval a WHERE a.rfq.id = :rfqId AND a.status = com.itti.leadcapturing.model.ApprovalActionStatus.PENDING")
    Long countPendingApprovals(@Param("rfqId") Long rfqId);
}