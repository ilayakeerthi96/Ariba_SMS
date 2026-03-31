// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.HardcodedSupplierRating;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// /**
//  * ✅ Repository for Hardcoded Supplier Ratings
//  */
// @Repository
// public interface HardcodedSupplierRatingRepository extends JpaRepository<HardcodedSupplierRating, Long> {

//     /**
//      * Find all active ratings for a supplier
//      */
//     @Query("SELECT h FROM HardcodedSupplierRating h " +
//        "LEFT JOIN FETCH h.supplier s " +
//        "LEFT JOIN FETCH s.locations " +  // ✅ Eagerly fetch locations
//        "WHERE h.supplier.id = :supplierId AND h.isActive = true " +
//        "ORDER BY h.criterion.displayOrder")
//     List<HardcodedSupplierRating> findBySupplierIdActive(@Param("supplierId") Long supplierId);

//     /**
//      * Find rating for supplier on specific criterion
//      */
//     @Query("SELECT h FROM HardcodedSupplierRating h " +
//            "WHERE h.supplier.id = :supplierId " +
//            "AND h.criterion.id = :criterionId " +
//            "AND h.isActive = true")
//     Optional<HardcodedSupplierRating> findBySupplierAndCriterion(
//         @Param("supplierId") Long supplierId,
//         @Param("criterionId") Long criterionId
//     );

//     /**
//      * Find all ratings for a specific criterion across all suppliers
//      */
//     @Query("SELECT h FROM HardcodedSupplierRating h " +
//            "WHERE h.criterion.id = :criterionId AND h.isActive = true " +
//            "ORDER BY h.rating DESC")
//     List<HardcodedSupplierRating> findByCriterionId(@Param("criterionId") Long criterionId);

//     /**
//      * Find all active ratings
//      */
//     @Query("SELECT h FROM HardcodedSupplierRating h WHERE h.isActive = true")
//     List<HardcodedSupplierRating> findAllActive();

//     /**
//      * Check if hardcoded rating exists for supplier-criterion pair
//      */
//     @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
//            "FROM HardcodedSupplierRating h " +
//            "WHERE h.supplier.id = :supplierId " +
//            "AND h.criterion.id = :criterionId " +
//            "AND h.isActive = true")
//     boolean existsBySupplierAndCriterion(
//         @Param("supplierId") Long supplierId,
//         @Param("criterionId") Long criterionId
//     );
// }