
// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.LocalDateTime;

// /**
//  * ✅ CORRECTED: Stores Manual Ratings & Calculated Scores for Suppliers
//  * 
//  * Flow:
//  * 1. Buyer manually enters rawScore (rating) for each supplier on each criterion
//  * 2. System calculates weightedScore = (rawScore / maxScore) × weightage
//  * 3. Total score = sum of all weightedScores for a supplier
//  * 
//  * Example:
//  * - Criterion: Quality, MaxScore: 10, Weightage: 40%
//  * - Buyer rates Supplier A: 8/10 on Quality
//  * - Weighted Score = (8/10) × 40 = 32 points
//  */
// @Entity
// @Table(name = "supplier_scores", 
//        uniqueConstraints = @UniqueConstraint(
//            columnNames = {"rfq_id", "supplier_id", "rfq_criterion_id"}
//        ))
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class SupplierScore {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "rfq_id", nullable = false)
//     private RFQ rfq;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "supplier_id", nullable = false)
//     private Supplier supplier;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "rfq_criterion_id", nullable = false)
//     private RFQCriterion rfqCriterion;

//     /**
//      * ✅ MANUAL RATING: Entered by buyer (e.g., 8 out of 10)
//      * This is the actual rating score given to the supplier for this criterion
//      */
//     @Column(nullable = false, precision = 5, scale = 2)
//     private BigDecimal rawScore;

//     /**
//      * ✅ AUTO-CALCULATED: Weighted score based on formula
//      * weightedScore = (rawScore / maxScore) × weightage
//      * 
//      * Example: (8/10) × 40 = 32.00
//      */
//     @Column(precision = 7, scale = 2)
//     private BigDecimal weightedScore;

//     @Column(length = 1000)
//     private String comments;  // Optional comments/justification for the rating

//     @Column(name = "scored_by")
//     private Long scoredBy;  // User who gave this rating

//     @Column(name = "scored_at")
//     private LocalDateTime scoredAt = LocalDateTime.now();

//     /**
//      * ✅ Flag to indicate if score was auto-calculated (for AUTO_PRICE criteria)
//      * or manually entered (for MANUAL criteria like Quality, Delivery, etc.)
//      */
//     @Column(nullable = false)
//     private Boolean isAutoCalculated = false;

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();

//     @Column(nullable = false)
//     private LocalDateTime updatedAt = LocalDateTime.now();

//     /**
//      * ✅ AUTO-CALCULATE weighted score based on formula
//      * Call this after setting rawScore or when RFQCriterion weightage changes
//      */
//     public void calculateWeightedScore() {
//         if (this.rawScore == null || this.rfqCriterion == null) {
//             this.weightedScore = BigDecimal.ZERO;
//             return;
//         }

//         Integer maxScore = this.rfqCriterion.getCriterion().getMaxScore();
//         Double weightage = this.rfqCriterion.getWeightage();

//         // Formula: (rawScore / maxScore) × weightage
//         BigDecimal normalizedScore = this.rawScore.divide(
//             new BigDecimal(maxScore), 
//             4, 
//             RoundingMode.HALF_UP
//         );
        
//         this.weightedScore = normalizedScore.multiply(new BigDecimal(weightage))
//             .setScale(2, RoundingMode.HALF_UP);
//     }

//     @PrePersist
//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//         if (this.scoredAt == null) {
//             this.scoredAt = LocalDateTime.now();
//         }
//         // Auto-calculate weighted score before saving
//         calculateWeightedScore();
//     }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * ✅ CORRECTED: Stores Manual Ratings & Calculated Scores for Suppliers
 * MAX SCORE IS ALWAYS 5 (FIXED)
 * 
 * Flow:
 * 1. Buyer manually enters rawScore (rating) for each supplier on each criterion
 * 2. System calculates weightedScore = (rawScore / 5) × weightage
 * 3. Total score = sum of all weightedScores for a supplier
 * 
 * Example:
 * - Criterion: Quality, MaxScore: 5 (FIXED), Weightage: 40%
 * - Buyer rates Supplier A: 4/5 on Quality
 * - Weighted Score = (4/5) × 40 = 32 points
 */
@Entity
@Table(name = "supplier_scores", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"rfq_id", "supplier_id", "rfq_criterion_id"}
       ))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rfq_criterion_id", nullable = false)
    private RFQCriterion rfqCriterion;

    /**
     * ✅ MANUAL RATING: Entered by buyer (e.g., 4 out of 5)
     * This is the actual rating score given to the supplier for this criterion
     * MUST BE BETWEEN 0 AND 5
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rawScore;

    /**
     * ✅ AUTO-CALCULATED: Weighted score based on formula
     * weightedScore = (rawScore / 5) × weightage
     * 
     * Example: (4/5) × 40 = 32.00
     */
    @Column(precision = 7, scale = 2)
    private BigDecimal weightedScore;

    @Column(length = 1000)
    private String comments;

    @Column(name = "scored_by")
    private Long scoredBy;

    @Column(name = "scored_at")
    private LocalDateTime scoredAt = LocalDateTime.now();

    /**
     * ✅ Flag to indicate if score was auto-calculated (for AUTO_PRICE criteria)
     * or manually entered (for MANUAL criteria like Quality, Delivery, etc.)
     */
    @Column(nullable = false)
    private Boolean isAutoCalculated = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * ✅ AUTO-CALCULATE weighted score based on formula
     * MAX SCORE IS ALWAYS 5 (FIXED)
     * Call this after setting rawScore or when RFQCriterion weightage changes
     */
    public void calculateWeightedScore() {
        if (this.rawScore == null || this.rfqCriterion == null) {
            this.weightedScore = BigDecimal.ZERO;
            return;
        }

        // ✅ FIXED: Max score is ALWAYS 5
        final int MAX_SCORE = 5;
        Double weightage = this.rfqCriterion.getWeightage();

        // Formula: (rawScore / 5) × weightage
        BigDecimal normalizedScore = this.rawScore.divide(
            new BigDecimal(MAX_SCORE), 
            4, 
            RoundingMode.HALF_UP
        );
        
        this.weightedScore = normalizedScore.multiply(new BigDecimal(weightage))
            .setScale(2, RoundingMode.HALF_UP);
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.scoredAt == null) {
            this.scoredAt = LocalDateTime.now();
        }
        // Auto-calculate weighted score before saving
        calculateWeightedScore();
    }
}