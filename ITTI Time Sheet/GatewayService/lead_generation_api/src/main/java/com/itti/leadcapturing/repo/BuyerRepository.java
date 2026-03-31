
// // // package com.itti.leadcapturing.repo;

// // // import org.springframework.data.jpa.repository.JpaRepository;
// // // import org.springframework.data.jpa.repository.Query;
// // // import org.springframework.data.repository.query.Param;
// // // import org.springframework.stereotype.Repository;
// // // import com.itti.leadcapturing.model.Buyer;
// // // import java.util.List;
// // // import java.util.Optional;

// // // @Repository
// // // public interface BuyerRepository extends JpaRepository<Buyer, Long> {

// // //     // File: src/main/java/com/itti/leadcapturing/repo/BuyerRepository.java
// // // // ✅ ADD THIS METHOD

// // // @Query("SELECT b FROM Buyer b WHERE b.createdByOrgAdmin.id = :adminId AND b.isDeleted = false ORDER BY b.createdAt DESC")
// // // List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);

// // // @Query("SELECT b FROM Buyer b WHERE b.organizationCompanyName = :companyName AND b.isDeleted = false")
// // // List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);
    
// // //     @Query("SELECT b FROM Buyer b WHERE b.isDeleted = false")
// // //     List<Buyer> findAll();

// // //     @Query("SELECT b FROM Buyer b WHERE b.id = :id AND b.isDeleted = false")
// // //     Optional<Buyer> findById(@Param("id") Long id);

// // //     @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Buyer b WHERE b.id = :id AND b.isDeleted = false")
// // //     boolean existsById(@Param("id") Long id);
// // // }


// // package com.itti.leadcapturing.repo;

// // import org.springframework.data.jpa.repository.JpaRepository;
// // import org.springframework.data.jpa.repository.Query;
// // import org.springframework.data.repository.query.Param;
// // import org.springframework.stereotype.Repository;
// // import com.itti.leadcapturing.model.Buyer;
// // import java.util.List;
// // import java.util.Optional;

// // @Repository
// // public interface BuyerRepository extends JpaRepository<Buyer, Long> {

// //     // ============================================
// //     // 🆕 ORGANIZATION ADMIN QUERIES
// //     // ============================================
    
// //     /**
// //      * Find buyers created by specific Organization Admin
// //      */
// //     @Query("SELECT b FROM Buyer b WHERE b.createdByOrgAdmin.id = :adminId AND b.isDeleted = false ORDER BY b.createdAt DESC")
// //     List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);

// //     /**
// //      * Find buyers by organization company name
// //      */
// //     @Query("SELECT b FROM Buyer b WHERE b.organizationCompanyName = :companyName AND b.isDeleted = false ORDER BY b.createdAt DESC")
// //     List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);

// //     // ============================================
// //     // EXISTING QUERIES
// //     // ============================================
    
// //     @Query("SELECT b FROM Buyer b WHERE b.isDeleted = false")
// //     List<Buyer> findAll();

// //     @Query("SELECT b FROM Buyer b WHERE b.id = :id AND b.isDeleted = false")
// //     Optional<Buyer> findById(@Param("id") Long id);

// //     @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Buyer b WHERE b.id = :id AND b.isDeleted = false")
// //     boolean existsById(@Param("id") Long id);
// // }

// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.Buyer;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    
//     // ✅ Find buyers by organization admin ID
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "LEFT JOIN FETCH b.locations l " +
//            "LEFT JOIN FETCH l.departments d " +
//            "LEFT JOIN FETCH d.users u " +
//            "WHERE b.createdByOrgAdmin.id = :adminId " +
//            "AND b.isDeleted = false")
//     List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);
    
//     // ✅ NEW: Find SINGLE buyer by ID AND Org Admin ID (SECURE)
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "LEFT JOIN FETCH b.locations l " +
//            "LEFT JOIN FETCH l.departments d " +
//            "LEFT JOIN FETCH d.users u " +
//            "LEFT JOIN FETCH b.createdByOrgAdmin admin " +
//            "WHERE b.id = :buyerId " +
//            "AND b.createdByOrgAdmin.id = :adminId " +
//            "AND b.isDeleted = false")
//     Optional<Buyer> findByIdAndCreatedByOrgAdminId(
//         @Param("buyerId") Long buyerId, 
//         @Param("adminId") Long adminId
//     );
    
//     // ✅ Find buyers by organization company name
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "LEFT JOIN FETCH b.locations l " +
//            "WHERE b.organizationCompanyName = :companyName " +
//            "AND b.isDeleted = false")
//     List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);
    
//     // ✅ Custom query to fetch buyer with all relationships (ANY admin can access)
//     @Query("SELECT DISTINCT b FROM Buyer b " +
//            "LEFT JOIN FETCH b.locations l " +
//            "LEFT JOIN FETCH l.departments d " +
//            "LEFT JOIN FETCH d.users u " +
//            "LEFT JOIN FETCH b.createdByOrgAdmin admin " +
//            "WHERE b.id = :id " +
//            "AND b.isDeleted = false")
//     Optional<Buyer> findByIdWithAllRelations(@Param("id") Long id);
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

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    
    /**
     * ✅ FIXED: Find buyers by organization admin ID
     * Uses @EntityGraph to eagerly load only the first level (locations)
     * Then we'll manually initialize departments and users in the service
     */
    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.createdByOrgAdmin.id = :adminId " +
           "AND b.isDeleted = false")
    List<Buyer> findByCreatedByOrgAdminId(@Param("adminId") Long adminId);
    
    /**
     * ✅ NEW: Find SINGLE buyer by ID AND Org Admin ID (SECURE)
     */
    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.id = :buyerId " +
           "AND b.createdByOrgAdmin.id = :adminId " +
           "AND b.isDeleted = false")
    Optional<Buyer> findByIdAndCreatedByOrgAdminId(
        @Param("buyerId") Long buyerId, 
        @Param("adminId") Long adminId
    );
    
    /**
     * ✅ Find buyers by organization company name
     */
    @EntityGraph(attributePaths = {"locations"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.organizationCompanyName = :companyName " +
           "AND b.isDeleted = false")
    List<Buyer> findByOrganizationCompanyName(@Param("companyName") String companyName);
    
    /**
     * ✅ Custom query to fetch buyer with all relationships (ANY admin can access)
     */
    @EntityGraph(attributePaths = {"locations", "createdByOrgAdmin"})
    @Query("SELECT DISTINCT b FROM Buyer b " +
           "WHERE b.id = :id " +
           "AND b.isDeleted = false")
    Optional<Buyer> findByIdWithAllRelations(@Param("id") Long id);
}