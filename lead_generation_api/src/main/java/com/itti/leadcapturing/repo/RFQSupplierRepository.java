

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ Repository for RFQ Supplier (COMPLETE - With all methods)
 */
@Repository
public interface RFQSupplierRepository extends JpaRepository<RFQSupplier, Long> {

    // ==================== CORE METHODS ====================

    /**
     * ✅ FIXED: Find by RFQ and Status with all relationships loaded
     * Used in QuoteComparisonService
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status = :status")
    List<RFQSupplier> findByRFQAndStatus(
        @Param("rfqId") Long rfqId, 
        @Param("status") String status
    );

    /**
     * ✅ FIXED: Find by RFQ and Supplier with relationships loaded
     */
    @Query("SELECT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND s.id = :supplierId")
    Optional<RFQSupplier> findByRFQAndSupplier(
        @Param("rfqId") Long rfqId,
        @Param("supplierId") Long supplierId
    );

    /**
     * ✅ ADDED: Find all suppliers for an RFQ (alias for findByRFQ)
     * Used by RFQSupplierController
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId")
    List<RFQSupplier> findByRFQId(@Param("rfqId") Long rfqId);

    /**
     * ✅ FIXED: Find all suppliers for an RFQ
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId")
    List<RFQSupplier> findByRFQ(@Param("rfqId") Long rfqId);

    /**
     * ✅ ADDED: Find all RFQs for a supplier
     * Used by SupplierDashboardService
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE s.id = :supplierId")
    List<RFQSupplier> findBySupplier(@Param("supplierId") Long supplierId);

    // ==================== COUNT METHODS ====================

    /**
     * ✅ ADDED: Count suppliers for an RFQ (alias)
     * Used by RFQSupplierController
     */
    @Query("SELECT COUNT(DISTINCT rs) FROM RFQSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId")
    Long countByRFQId(@Param("rfqId") Long rfqId);

    /**
     * Count suppliers for an RFQ
     */
    @Query("SELECT COUNT(DISTINCT rs) FROM RFQSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId")
    Long countByRFQ(@Param("rfqId") Long rfqId);

    /**
     * ✅ ADDED: Count responded suppliers for an RFQ
     * Used by RFQSupplierController
     */
    @Query("SELECT COUNT(DISTINCT rs) FROM RFQSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status = 'RESPONDED'")
    Long countRespondedByRFQId(@Param("rfqId") Long rfqId);

    /**
     * Count suppliers by status for an RFQ
     */
    @Query("SELECT COUNT(DISTINCT rs) FROM RFQSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status = :status")
    Long countByRFQAndStatus(
        @Param("rfqId") Long rfqId,
        @Param("status") String status
    );

    // ==================== DELETE METHODS ====================

    /**
     * Delete all suppliers for an RFQ
     */
    @Query("DELETE FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId")
    void deleteByRFQ(@Param("rfqId") Long rfqId);

    // ==================== ADDITIONAL QUERY METHODS ====================

    /**
     * Find suppliers by RFQ and status list
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status IN :statuses")
    List<RFQSupplier> findByRFQAndStatusIn(
        @Param("rfqId") Long rfqId,
        @Param("statuses") List<String> statuses
    );

    /**
     * Find suppliers who have not responded yet
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status = 'INVITED'")
    List<RFQSupplier> findPendingByRFQ(@Param("rfqId") Long rfqId);

    /**
     * Find suppliers who have responded
     */
    @Query("SELECT DISTINCT rs FROM RFQSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "WHERE r.id = :rfqId " +
           "AND rs.status = 'RESPONDED'")
    List<RFQSupplier> findRespondedByRFQ(@Param("rfqId") Long rfqId);

    /**
     * Check if supplier is invited to RFQ
     */
    @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN true ELSE false END " +
           "FROM RFQSupplier rs " +
           "JOIN rs.rfq r " +
           "JOIN rs.supplier s " +
           "WHERE r.id = :rfqId " +
           "AND s.id = :supplierId")
    Boolean existsByRFQAndSupplier(
        @Param("rfqId") Long rfqId,
        @Param("supplierId") Long supplierId
    );
}