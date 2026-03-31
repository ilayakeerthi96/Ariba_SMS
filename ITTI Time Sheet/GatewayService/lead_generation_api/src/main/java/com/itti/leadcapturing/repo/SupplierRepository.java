package com.itti.leadcapturing.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.itti.leadcapturing.model.Supplier;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false ORDER BY s.id DESC")
    List<Supplier> findAll();

    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Supplier> findById(@Param("id") Long id);

    @Query("SELECT s FROM Supplier s WHERE s.id = :id")
    Optional<Supplier> findByIdIncludingDeleted(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Supplier s WHERE s.id = :id AND s.isDeleted = false")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isDeleted = false")
    List<Supplier> findByCompanyNameContaining(@Param("name") String name);

    @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isDeleted = false")
    List<Supplier> findByCompanyNameContainingIgnoreCaseAndIsDeletedFalse(@Param("name") String name);

    @Query("SELECT s FROM Supplier s WHERE s.contactPersonEmail = :email AND s.isDeleted = false")
    Optional<Supplier> findByContactPersonEmail(@Param("email") String email);

    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false")
    List<Supplier> findByIsDeletedFalse();

    @Query("SELECT s FROM Supplier s WHERE s.industrySector = :sector AND s.isDeleted = false")
    List<Supplier> findByIndustrySectorAndIsDeletedFalse(@Param("sector") String sector);

    @Query("SELECT s FROM Supplier s WHERE s.state = :state AND s.isDeleted = false")
    List<Supplier> findByStateAndIsDeletedFalse(@Param("state") String state);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isDeleted = false")
    Long countByIsDeletedFalse();
}