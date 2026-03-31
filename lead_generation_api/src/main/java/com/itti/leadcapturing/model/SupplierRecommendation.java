// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// /**
//  * ✅ NEW: Supplier Recommendation with Combined Score + Price
//  * 
//  * Formula from Excel:
//  * Combined Score = (Total Criteria Score × Quality Weightage) + (Price Score × Quote Weightage)
//  * 
//  * Example:
//  * - Supplier-1: Total Score = 114, Price = 1,20,348.20
//  * - Quality Weightage = 84, Quote Weightage = 30
//  * - Combined = (114 × 84) + (Price Calculation × 30)
//  */
// @Entity
// @Table(name = "supplier_recommendations",
//        uniqueConstraints = @UniqueConstraint(
//            columnNames = {"rfq_id", "supplier_id"}
//        ))
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class SupplierRecommendation {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "rfq_id", nullable = false)
//     private RFQ rfq;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "supplier_id", nullable = false)
//     private Supplier supplier;

//     /**
//      * Total criteria score (sum of all weighted scores)
//      * This is the "Score (E)" column from Excel
//      */
//     @Column(nullable = false, precision = 10, scale = 2)
//     private BigDecimal totalCriteriaScore;

//     /**
//      * Quality/Score weightage (Column C from Excel)
//      */
//     @Column(nullable = false, precision = 5, scale = 2)
//     private BigDecimal qualityWeightage;

//     /**
//      * Quote amount from supplier
//      */
//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal quoteAmount;

//     /**
//      * Price score (normalized based on lowest price)
//      */
//     @Column(precision = 10, scale = 2)
//     private BigDecimal priceScore;

//     /**
//      * Quote/Price weightage (Column D from Excel)
//      */
//     @Column(nullable = false, precision = 5, scale = 2)
//     private BigDecimal quoteWeightage;

//     /**
//      * ✅ FINAL COMBINED SCORE
//      * Formula: (totalCriteriaScore × qualityWeightage) + (priceScore × quoteWeightage)
//      */
//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal finalScore;

//     /**
//      * Rank (1, 2, 3, etc.) - Lower is better
//      */
// @Column(name = "`rank`", nullable = false)
// private Integer rank;

//     /**
//      * Is this in top 5 recommended?
//      */
//     @Column(nullable = false)
//     private Boolean isRecommended = false;

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();

//     @Column(nullable = false)
//     private LocalDateTime updatedAt = LocalDateTime.now();

//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ FIXED: Supplier Recommendation with Combined Score + Price
 * 
 * Formula: Final Score = Total Criteria Score + Quote Weightage
 * Quote Weightage = (NumSuppliers × 10) - (PriceRank - 1) × 10
 * Lower Price = Higher Weightage
 */
@Entity
@Table(name = "supplier_recommendations",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"rfq_id", "supplier_id"}
       ))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /**
     * Total criteria score (sum of all weighted scores)
     * This is the "Score (E)" column from Excel
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCriteriaScore;

    /**
     * Quality/Score weightage (Column C from Excel)
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal qualityWeightage;

    /**
     * Quote amount from supplier
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quoteAmount;

    /**
     * Price score (normalized based on lowest price)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal priceScore;

    /**
     * Quote/Price weightage (Column D from Excel)
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal quoteWeightage;

    /**
     * ✅ FINAL COMBINED SCORE
     * Formula: (totalCriteriaScore × qualityWeightage) + (priceScore × quoteWeightage)
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal finalScore;

    /**
     * ✅ FIXED: Rank (1, 2, 3, etc.) - Lower is better
     * IMPORTANT: `rank` is a MySQL reserved keyword, so it MUST be escaped with backticks
     */
    @Column(name = "`rank`", nullable = false)
    private Integer rank;

    /**
     * Is this in top 5 recommended?
     */
    @Column(nullable = false)
    private Boolean isRecommended = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}