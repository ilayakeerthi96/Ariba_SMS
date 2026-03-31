

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ✅ CORRECTED: Master Evaluation Criteria (Company-wide)
 * Admin creates these criteria WITHOUT weightage
 * Weightage is assigned when criteria are selected for specific RFQ
 */
@Entity
@Table(name = "evaluation_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String criterionName;  // e.g., "Quality", "Delivery Time", "Price"

    @Column(length = 500)
    private String description;  // Detailed description of what this criterion evaluates

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriterionType criterionType = CriterionType.MANUAL;  // MANUAL or AUTO_PRICE

    @Column(nullable = false)
    private Integer maxScore = 10;  // Maximum score for this criterion (default: 10)

    @Column(nullable = false)
    private Boolean isActive = true;  // Active/Inactive status

    @Column(nullable = false)
    private Integer displayOrder = 0;  // Order for display in UI

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}