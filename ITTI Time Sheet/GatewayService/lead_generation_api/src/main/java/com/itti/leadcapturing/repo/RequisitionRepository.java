package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Long> {

    @Query("SELECT r FROM Requisition r WHERE r.isDeleted = false ORDER BY r.createdAt DESC")
    List<Requisition> findAll();

    @Query("SELECT r FROM Requisition r WHERE r.id = :id AND r.isDeleted = false")
    Optional<Requisition> findById(@Param("id") Long id);

    @Query("SELECT r FROM Requisition r WHERE r.department.id = :departmentId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<Requisition> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT r FROM Requisition r WHERE r.requestedBy.id = :userId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<Requisition> findByRequestedById(@Param("userId") Long userId);

    @Query("SELECT r FROM Requisition r WHERE r.status = :status AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<Requisition> findByStatus(@Param("status") String status);

    @Query("SELECT r FROM Requisition r WHERE r.department.id = :departmentId AND r.status = :status AND r.isDeleted = false")
    List<Requisition> findByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, @Param("status") String status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Requisition r WHERE r.id = :id AND r.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}