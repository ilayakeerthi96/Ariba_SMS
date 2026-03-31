
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQ;
import com.itti.leadcapturing.model.RFQStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RFQRepository extends JpaRepository<RFQ, Long> {

    @Query("SELECT r FROM RFQ r WHERE r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findAll();

    @Query("SELECT r FROM RFQ r WHERE r.id = :id AND r.isDeleted = false")
    Optional<RFQ> findById(@Param("id") Long id);

    @Query("SELECT r FROM RFQ r WHERE r.rfqNumber = :rfqNumber AND r.isDeleted = false")
    Optional<RFQ> findByRfqNumber(@Param("rfqNumber") String rfqNumber);

    @Query("SELECT r FROM RFQ r LEFT JOIN FETCH r.selectedSuppliers WHERE r.id = :id AND r.isDeleted = false")
Optional<RFQ> findByIdWithSuppliers(@Param("id") Long id);

    // ==================== ✅ MAIN QUERY: Get RFQs by Buyer ====================
    /**
     * Get all RFQs created for a specific buyer
     * Used by dashboard to show only that buyer's RFQs
     * 
     * Query: SELECT * FROM rfqs WHERE buyer_id = ? AND is_deleted = false
     */
    @Query("SELECT r FROM RFQ r WHERE r.buyer.id = :buyerId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByBuyerId(@Param("buyerId") Long buyerId);

    // ==================== ✅ Filter by Status ====================
    /**
     * Get RFQs by buyer and status
     * Used for status-based filtering in dashboard
     */
    @Query("SELECT r FROM RFQ r WHERE r.buyer.id = :buyerId AND r.status = :status AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") RFQStatus status);

    @Query("SELECT r FROM RFQ r WHERE r.location.id = :locationId AND r.isDeleted = false")
    List<RFQ> findByLocationId(@Param("locationId") Long locationId);

    // ==================== ✅ Get RFQs by Created By User ====================
    /**
     * Get RFQs created by a specific user (for audit/history purposes)
     * Note: This is different from buyer - createdByUser is WHO created it
     */
    @Query("SELECT r FROM RFQ r WHERE r.createdByUser.id = :userId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByCreatedByUserId(@Param("userId") Long userId);
}

