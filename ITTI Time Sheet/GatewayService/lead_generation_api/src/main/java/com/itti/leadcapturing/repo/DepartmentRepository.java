
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    @Query("SELECT d FROM Department d WHERE d.location.id = :locationId AND d.isDeleted = false")
    List<Department> findByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT d FROM Department d WHERE d.id = :id AND d.isDeleted = false")
    Optional<Department> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d WHERE d.id = :id AND d.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}