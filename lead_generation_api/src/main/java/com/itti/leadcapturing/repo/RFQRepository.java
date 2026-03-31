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

    // ==================== ✅ FIXED: NO MORE DUPLICATES ====================
    /**
     * Get RFQ with basic details and buyer/location/user
     * Items and suppliers will be loaded separately to avoid duplicates
     */
    @Query("SELECT DISTINCT r FROM RFQ r " +
           "LEFT JOIN FETCH r.buyer " +
           "LEFT JOIN FETCH r.location " +
           "LEFT JOIN FETCH r.createdByUser " +
           "WHERE r.id = :id AND r.isDeleted = false")
    Optional<RFQ> findByIdWithAllDetails(@Param("id") Long id);

    /**
     * Load items separately
     */
    @Query("SELECT DISTINCT r FROM RFQ r " +
           "LEFT JOIN FETCH r.items " +
           "WHERE r.id = :id AND r.isDeleted = false")
    Optional<RFQ> findByIdWithItems(@Param("id") Long id);

    /**
     * Load suppliers separately
     */
    @Query("SELECT DISTINCT r FROM RFQ r " +
           "LEFT JOIN FETCH r.selectedSuppliers " +
           "WHERE r.id = :id AND r.isDeleted = false")
    Optional<RFQ> findByIdWithSuppliers(@Param("id") Long id);

    // ==================== Get RFQs by Buyer ====================
    @Query("SELECT r FROM RFQ r WHERE r.buyer.id = :buyerId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByBuyerId(@Param("buyerId") Long buyerId);

    // ==================== Filter by Status ====================
    @Query("SELECT r FROM RFQ r WHERE r.buyer.id = :buyerId AND r.status = :status AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") RFQStatus status);

    @Query("SELECT r FROM RFQ r WHERE r.location.id = :locationId AND r.isDeleted = false")
    List<RFQ> findByLocationId(@Param("locationId") Long locationId);

    // ==================== Get RFQs by Created By User ====================
    @Query("SELECT r FROM RFQ r WHERE r.createdByUser.id = :userId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<RFQ> findByCreatedByUserId(@Param("userId") Long userId);
}