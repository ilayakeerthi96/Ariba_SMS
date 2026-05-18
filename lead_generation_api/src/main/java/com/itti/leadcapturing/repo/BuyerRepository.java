
// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.Buyer;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface BuyerRepository extends JpaRepository<Buyer, Long> {

//     @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "WHERE b.createdByOrgAdmin.id = :adminId " +
//            "AND b.isDeleted = false")
//     List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);

//     @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "WHERE b.id = :buyerId " +
//            "AND b.createdByOrgAdmin.id = :adminId " +
//            "AND b.isDeleted = false")
//     Optional<Buyer> findByIdAndCreatedByOrgAdminId(
//         @Param("buyerId") Long buyerId,
//         @Param("adminId") Long adminId
//     );

//     @EntityGraph(attributePaths = {"locations"})
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "WHERE b.organizationCompanyName = :companyName " +
//            "AND b.isDeleted = false")
//     List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);

//     @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "WHERE b.id = :id " +
//            "AND b.isDeleted = false")
//     Optional<Buyer> findByIdWithAllRelations(@Param("id") Long id);

//     // ✅ NEW: Native query that forces loading of LONGBLOB logoData column
//     // JPA JPQL sometimes skips LONGBLOB fields in lazy contexts — native SQL forces it
//     @Query(value = "SELECT * FROM buyers WHERE id = :id AND is_deleted = false", nativeQuery = true)
//     Optional<Buyer> findByIdWithLogoData(@Param("id") Long id);
// }

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MODIFIED: Removed findByIdWithLogoData (no longer needed — logo comes from OrganizationAdmin).
 */
@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.createdByOrgAdmin.id = :adminId " +
           "AND b.isDeleted = false")
    List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);

    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.id = :buyerId " +
           "AND b.createdByOrgAdmin.id = :adminId " +
           "AND b.isDeleted = false")
    Optional<Buyer> findByIdAndCreatedByOrgAdminId(
        @Param("buyerId") Long buyerId,
        @Param("adminId") Long adminId
    );

    @EntityGraph(attributePaths = {"locations"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.organizationCompanyName = :companyName " +
           "AND b.isDeleted = false")
    List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);

    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.id = :id " +
           "AND b.isDeleted = false")
    Optional<Buyer> findByIdWithAllRelations(@Param("id") Long id);
}