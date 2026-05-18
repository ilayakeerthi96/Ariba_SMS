
// package com.itti.leadcapturing.repo;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import com.itti.leadcapturing.model.Supplier;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
//     @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false ORDER BY s.id DESC")
//     List<Supplier> findAll();

//     @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.isDeleted = false")
//     Optional<Supplier> findById(@Param("id") Long id);

//     @Query("SELECT s FROM Supplier s WHERE s.id = :id")
//     Optional<Supplier> findByIdIncludingDeleted(@Param("id") Long id);

//     @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Supplier s WHERE s.id = :id AND s.isDeleted = false")
//     boolean existsById(@Param("id") Long id);

//     @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isDeleted = false")
//     List<Supplier> findByCompanyNameContaining(@Param("name") String name);

//     @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isDeleted = false")
//     List<Supplier> findByCompanyNameContainingIgnoreCaseAndIsDeletedFalse(@Param("name") String name);

//     @Query("SELECT s FROM Supplier s WHERE s.contactPersonEmail = :email AND s.isDeleted = false")
//     Optional<Supplier> findByContactPersonEmail(@Param("email") String email);

//     @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false")
//     List<Supplier> findByIsDeletedFalse();

//     @Query("SELECT s FROM Supplier s WHERE s.industrySector = :sector AND s.isDeleted = false")
//     List<Supplier> findByIndustrySectorAndIsDeletedFalse(@Param("sector") String sector);

//     @Query("SELECT s FROM Supplier s WHERE s.state = :state AND s.isDeleted = false")
//     List<Supplier> findByStateAndIsDeletedFalse(@Param("state") String state);

//     @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isDeleted = false")
//     Long countByIsDeletedFalse();

//     // ✅ NEW: Native query that forces loading of LONGBLOB logoData column
//     // JPA JPQL sometimes skips LONGBLOB fields in lazy contexts — native SQL forces it
//     @Query(value = "SELECT * FROM suppliers WHERE id = :id AND is_deleted = false", nativeQuery = true)
//     Optional<Supplier> findByIdWithLogoData(@Param("id") Long id);
    
// }


package com.itti.leadcapturing.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.model.ApprovalStatus;
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

    // ✅ Force-load LONGBLOB logoData via native SQL
    @Query(value = "SELECT * FROM suppliers WHERE id = :id AND is_deleted = false", nativeQuery = true)
    Optional<Supplier> findByIdWithLogoData(@Param("id") Long id);

    // ✅ NEW: Only APPROVED suppliers (used in dashboard table + RFQ supplier selection)
    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false AND s.approvalStatus = com.itti.leadcapturing.model.ApprovalStatus.APPROVED ORDER BY s.id DESC")
    List<Supplier> findAllApproved();

    // ✅ NEW: Only APPROVED suppliers matching company name search
    @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isDeleted = false AND s.approvalStatus = com.itti.leadcapturing.model.ApprovalStatus.APPROVED")
    List<Supplier> findApprovedByCompanyNameContaining(@Param("name") String name);

    // ✅ NEW: Count by approval status (for dashboard badges)
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isDeleted = false AND s.approvalStatus = :status")
    Long countByApprovalStatus(@Param("status") ApprovalStatus status);

    // ✅ NEW: Find by approval status (admin view)
    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false AND s.approvalStatus = :status ORDER BY s.id DESC")
    List<Supplier> findByApprovalStatus(@Param("status") ApprovalStatus status);
}