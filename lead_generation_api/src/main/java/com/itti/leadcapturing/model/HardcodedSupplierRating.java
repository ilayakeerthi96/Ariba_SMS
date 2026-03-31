// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// /**
//  * ✅ NEW: Hardcoded Supplier Ratings Table
//  * 
//  * Purpose:
//  * - Store initial/default ratings for suppliers on common criteria
//  * - Used as fallback when no manual ratings exist
//  * - Later replaced with actual post-delivery ratings
//  * 
//  * Use Case:
//  * When evaluating suppliers, if buyer hasn't manually rated a criterion yet,
//  * system can use these hardcoded ratings as baseline/historical performance
//  * 
//  * Future Enhancement:
//  * After product delivery, replace these with actual performance ratings
//  */
// @Entity
// @Table(name = "hardcoded_supplier_ratings",
//        uniqueConstraints = @UniqueConstraint(
//            columnNames = {"supplier_id", "criterion_id"}
//        ))
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class HardcodedSupplierRating {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "supplier_id", nullable = false)
//      @JsonIgnoreProperties({"locations", "hibernateLazyInitializer", "handler"})
//     private Supplier supplier;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "criterion_id", nullable = false)
//       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
//     private EvaluationCriterion criterion;

//     /**
//      * Hardcoded rating value (out of criterion's maxScore)
//      * Example: If maxScore=10, rating could be 7.5
//      */
//     @Column(nullable = false, precision = 5, scale = 2)
//     private BigDecimal rating;

//     @Column(length = 500)
//     private String remarks;  // Notes about this rating (e.g., "Based on last 3 orders")

//     /**
//      * Rating source/type
//      * - HISTORICAL: Based on past performance
//      * - DEFAULT: System default rating
//      * - ESTIMATED: Estimated based on similar suppliers
//      */
//     @Column(length = 50)
//     private String ratingSource = "HISTORICAL";

//     @Column(nullable = false)
//     private Boolean isActive = true;

//     @Column(name = "created_by")
//     private Long createdBy;

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();

//     @Column(nullable = false)
//     private LocalDateTime updatedAt = LocalDateTime.now();

//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }
// }

