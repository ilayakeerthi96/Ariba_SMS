package com.itti.leadcapturing.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.itti.leadcapturing.model.SupplierUser;
import java.util.List;
import java.util.Optional;

public interface SupplierUserRepository extends JpaRepository<SupplierUser, Long> {
    
    @Query("SELECT su FROM SupplierUser su WHERE su.isDeleted = false")
    List<SupplierUser> findAll();

    @Query("SELECT su FROM SupplierUser su WHERE su.id = :id AND su.isDeleted = false")
    Optional<SupplierUser> findById(@Param("id") Long id);

    @Query("SELECT su FROM SupplierUser su WHERE su.id = :id")
    Optional<SupplierUser> findByIdIncludingDeleted(@Param("id") Long id);

    @Query("SELECT su FROM SupplierUser su WHERE su.email = :email AND su.isDeleted = false")
    Optional<SupplierUser> findByEmail(@Param("email") String email);

    @Query("SELECT su FROM SupplierUser su WHERE su.email = :email AND su.isDeleted = false")
    Optional<SupplierUser> findByEmailAndIsDeletedFalse(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END FROM SupplierUser su WHERE su.email = :email AND su.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END FROM SupplierUser su WHERE su.id = :id AND su.isDeleted = false")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT su FROM SupplierUser su WHERE su.department.id = :departmentId AND su.isDeleted = false")
    List<SupplierUser> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT su FROM SupplierUser su WHERE su.department.id = :departmentId")
    List<SupplierUser> findByDepartmentIdIncludingDeleted(@Param("departmentId") Long departmentId);

    @Query("SELECT su FROM SupplierUser su WHERE su.firstName LIKE %:name% OR su.lastName LIKE %:name% AND su.isDeleted = false")
    List<SupplierUser> findByNameContaining(@Param("name") String name);

    @Query("SELECT su FROM SupplierUser su WHERE su.isDeleted = false")
    List<SupplierUser> findByIsDeletedFalse();

    @Query("SELECT COUNT(su) FROM SupplierUser su WHERE su.department.id = :departmentId AND su.isDeleted = false")
    Long countByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT su FROM SupplierUser su WHERE su.employeeId = :employeeId AND su.isDeleted = false")
    Optional<SupplierUser> findByEmployeeId(@Param("employeeId") String employeeId);
}