package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRecommendationRepository extends JpaRepository<SupplierRecommendation, Long> {

    /**
     * Get all recommendations for an RFQ, ordered by rank
     */
    @Query("SELECT sr FROM SupplierRecommendation sr " +
           "WHERE sr.rfq.id = :rfqId " +
           "ORDER BY sr.rank ASC")
    List<SupplierRecommendation> findByRfqIdOrderByRank(@Param("rfqId") Long rfqId);

    /**
     * Get top 5 recommended suppliers
     */
    @Query("SELECT sr FROM SupplierRecommendation sr " +
           "WHERE sr.rfq.id = :rfqId " +
           "AND sr.isRecommended = true " +
           "ORDER BY sr.rank ASC")
    List<SupplierRecommendation> findTop5RecommendedByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Get recommendation for specific supplier
     */
    @Query("SELECT sr FROM SupplierRecommendation sr " +
           "WHERE sr.rfq.id = :rfqId " +
           "AND sr.supplier.id = :supplierId")
    Optional<SupplierRecommendation> findByRfqIdAndSupplierId(
        @Param("rfqId") Long rfqId,
        @Param("supplierId") Long supplierId
    );

    /**
     * Delete all recommendations for an RFQ
     */
    @Modifying
    @Query("DELETE FROM SupplierRecommendation sr WHERE sr.rfq.id = :rfqId")
    void deleteByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Count recommendations for an RFQ
     */
    @Query("SELECT COUNT(sr) FROM SupplierRecommendation sr WHERE sr.rfq.id = :rfqId")
    Long countByRfqId(@Param("rfqId") Long rfqId);
}