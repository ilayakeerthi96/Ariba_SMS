package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SupplierDepartmentRepository extends JpaRepository<SupplierDepartment, Long> {
    
    @Query("SELECT sd FROM SupplierDepartment sd WHERE sd.location.id = :locationId AND sd.isDeleted = false")
    List<SupplierDepartment> findByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT sd FROM SupplierDepartment sd WHERE sd.id = :id AND sd.isDeleted = false")
    Optional<SupplierDepartment> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(sd) > 0 THEN true ELSE false END FROM SupplierDepartment sd WHERE sd.id = :id AND sd.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}