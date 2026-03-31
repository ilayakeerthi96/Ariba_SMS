
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierScoreRepository extends JpaRepository<SupplierScore, Long> {

    @Query("SELECT ss FROM SupplierScore ss WHERE ss.rfq.id = :rfqId AND ss.supplier.id = :supplierId")
    List<SupplierScore> findByRfqIdAndSupplierId(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT ss FROM SupplierScore ss WHERE ss.rfq.id = :rfqId ORDER BY ss.supplier.id, ss.rfqCriterion.criterion.displayOrder")
    List<SupplierScore> findByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT ss FROM SupplierScore ss WHERE ss.rfq.id = :rfqId AND ss.supplier.id = :supplierId AND ss.rfqCriterion.id = :criterionId")
    Optional<SupplierScore> findByRfqIdAndSupplierIdAndCriterionId(
        @Param("rfqId") Long rfqId,
        @Param("supplierId") Long supplierId,
        @Param("criterionId") Long criterionId
    );

    @Query("SELECT SUM(ss.weightedScore) FROM SupplierScore ss WHERE ss.rfq.id = :rfqId AND ss.supplier.id = :supplierId")
    BigDecimal calculateTotalScoreForSupplier(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    /**
     * ✅ FIXED: Added @Modifying annotation for DELETE query
     */
    @Modifying
    @Query("DELETE FROM SupplierScore ss WHERE ss.rfq.id = :rfqId")
    void deleteByRfqId(@Param("rfqId") Long rfqId);
}