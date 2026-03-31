package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RFQSupplierRepository extends JpaRepository<RFQSupplier, Long> {

    @Query("SELECT rs FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.isDeleted = false ORDER BY rs.createdAt DESC")
    List<RFQSupplier> findByRFQId(@Param("rfqId") Long rfqId);

    @Query("SELECT rs FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.supplier.id = :supplierId AND rs.isDeleted = false")
    Optional<RFQSupplier> findByRFQAndSupplier(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT rs FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.status = :status AND rs.isDeleted = false")
    List<RFQSupplier> findByRFQAndStatus(@Param("rfqId") Long rfqId, @Param("status") String status);

    @Query("SELECT rs FROM RFQSupplier rs WHERE rs.supplier.id = :supplierId AND rs.isDeleted = false")
    List<RFQSupplier> findBySupplier(@Param("supplierId") Long supplierId);

    @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN true ELSE false END FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.supplier.id = :supplierId AND rs.isDeleted = false")
    boolean existsByRFQAndSupplier(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(rs) FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.isDeleted = false")
    long countByRFQId(@Param("rfqId") Long rfqId);

    @Query("SELECT COUNT(rs) FROM RFQSupplier rs WHERE rs.rfq.id = :rfqId AND rs.status = 'RESPONDED' AND rs.isDeleted = false")
    long countRespondedByRFQId(@Param("rfqId") Long rfqId);
}