
// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.EvaluationCriterion;
// import com.itti.leadcapturing.model.RFQCriterion;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface RFQCriterionRepository extends JpaRepository<RFQCriterion, Long> {

//     @Query("SELECT rc FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId ORDER BY rc.criterion.displayOrder ASC")
//     List<RFQCriterion> findByRfqId(@Param("rfqId") Long rfqId);

//     /**
//      * ✅ NEW: Fetch with JOIN FETCH to avoid lazy loading issues
//      */
//     @Query("SELECT rc FROM RFQCriterion rc " +
//            "JOIN FETCH rc.criterion " +
//            "WHERE rc.rfq.id = :rfqId " +
//            "ORDER BY rc.criterion.displayOrder ASC")
//     List<RFQCriterion> findByRfqIdWithCriterion(@Param("rfqId") Long rfqId);

//     @Query("SELECT SUM(rc.weightage) FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId")
//     Double sumWeightageByRfqId(@Param("rfqId") Long rfqId);

//     /**
//      * ✅ FIXED: Added @Modifying annotation for DELETE query
//      */
//     @Modifying
//     @Query("DELETE FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId")
//     void deleteByRfqId(@Param("rfqId") Long rfqId);

//     /**
//      * ✅ Find all RFQ criteria that use a specific criterion
//      */
//     List<RFQCriterion> findByCriterion(EvaluationCriterion criterion);
// }

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.EvaluationCriterion;
import com.itti.leadcapturing.model.RFQCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ✅ COMPLETE: RFQCriterion Repository
 * Includes JOIN FETCH to avoid lazy loading issues
 */
@Repository
public interface RFQCriterionRepository extends JpaRepository<RFQCriterion, Long> {

    /**
     * Find criteria by RFQ ID (may cause lazy loading issues)
     */
    @Query("SELECT rc FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId ORDER BY rc.criterion.displayOrder ASC")
    List<RFQCriterion> findByRfqId(@Param("rfqId") Long rfqId);

    /**
     * ✅ RECOMMENDED: Find with JOIN FETCH to avoid lazy loading
     */
    @Query("SELECT rc FROM RFQCriterion rc " +
           "JOIN FETCH rc.criterion " +
           "WHERE rc.rfq.id = :rfqId " +
           "ORDER BY rc.criterion.displayOrder ASC")
    List<RFQCriterion> findByRfqIdWithCriterion(@Param("rfqId") Long rfqId);

    /**
     * Sum all weightages for an RFQ
     */
    @Query("SELECT SUM(rc.weightage) FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId")
    Double sumWeightageByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Delete all criteria for an RFQ
     */
    @Modifying
    @Query("DELETE FROM RFQCriterion rc WHERE rc.rfq.id = :rfqId")
    void deleteByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Find all RFQs using a specific criterion
     */
    List<RFQCriterion> findByCriterion(EvaluationCriterion criterion);

    /**
     * ✅ NEW: Count how many RFQs use this criterion (for delete validation)
     */
    long countByCriterionId(Long criterionId);
}