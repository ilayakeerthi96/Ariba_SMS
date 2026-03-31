package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SupplierLocationRepository extends JpaRepository<SupplierLocation, Long> {
    
    @Query("SELECT sl FROM SupplierLocation sl WHERE sl.supplier.id = :supplierId AND sl.isDeleted = false")
    List<SupplierLocation> findBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT sl FROM SupplierLocation sl WHERE sl.id = :id AND sl.isDeleted = false")
    Optional<SupplierLocation> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(sl) > 0 THEN true ELSE false END FROM SupplierLocation sl WHERE sl.id = :id AND sl.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}