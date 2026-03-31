// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.RFQApproval;
// import com.itti.leadcapturing.model.ApprovalLevel;
// import com.itti.leadcapturing.model.ApprovalActionStatus;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface RFQApprovalRepository extends JpaRepository<RFQApproval, Long> {

//     /**
//      * Get all approvals for an RFQ ordered by sequence
//      */
//     @Query("SELECT a FROM RFQApproval a WHERE a.rfq.id = :rfqId ORDER BY a.sequenceOrder ASC")
//     List<RFQApproval> findByRfqIdOrderBySequence(@Param("rfqId") Long rfqId);

//     /**
//      * Get current pending approval for an RFQ
//      */
//     @Query("SELECT a FROM RFQApproval a WHERE a.rfq.id = :rfqId AND a.status = 'PENDING' ORDER BY a.sequenceOrder ASC")
//     Optional<RFQApproval> findCurrentPendingApproval(@Param("rfqId") Long rfqId);

//     /**
//      * Get approval by RFQ and level
//      */
//     @Query("SELECT a FROM RFQApproval a WHERE a.rfq.id = :rfqId AND a.approvalLevel = :level")
//     Optional<RFQApproval> findByRfqAndLevel(@Param("rfqId") Long rfqId, @Param("level") ApprovalLevel level);

//     /**
//      * Check if all approvals are completed for RFQ
//      */
//     @Query("SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END FROM RFQApproval a WHERE a.rfq.id = :rfqId AND a.status = 'PENDING'")
//     boolean areAllApprovalsCompleted(@Param("rfqId") Long rfqId);

//     /**
//      * Get approvals by level and status
//      */
//     @Query("SELECT a FROM RFQApproval a WHERE a.approvalLevel = :level AND a.status = :status")
//     List<RFQApproval> findByLevelAndStatus(@Param("level") ApprovalLevel level, @Param("status") ApprovalActionStatus status);

//     /**
//      * Count pending approvals for a user at specific level
//      */
//     @Query("SELECT COUNT(a) FROM RFQApproval a WHERE a.approvalLevel = :level AND a.status = 'PENDING'")
//     long countPendingByLevel(@Param("level") ApprovalLevel level);
// }