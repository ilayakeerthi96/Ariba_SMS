package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierFinalSelection;
import com.itti.leadcapturing.model.SelectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierFinalSelectionRepository extends JpaRepository<SupplierFinalSelection, Long> {

    @Query("SELECT s FROM SupplierFinalSelection s WHERE s.rfq.id = :rfqId")
    Optional<SupplierFinalSelection> findByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT s FROM SupplierFinalSelection s WHERE s.rfq.id = :rfqId AND s.supplier.id = :supplierId")
    Optional<SupplierFinalSelection> findByRfqIdAndSupplierId(
            @Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT s FROM SupplierFinalSelection s WHERE s.selectedByUserId = :userId ORDER BY s.selectedAt DESC")
    List<SupplierFinalSelection> findBySelectedByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SupplierFinalSelection s WHERE s.rfq.id = :rfqId")
    Boolean existsByRfqId(@Param("rfqId") Long rfqId);
}