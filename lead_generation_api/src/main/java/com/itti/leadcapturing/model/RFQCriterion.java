

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ✅ CORRECTED: Links Criteria to specific RFQ with Weightage
 * RFQ Creator selects criteria and assigns weightage (must sum to 100%)
 */
@Entity
@Table(name = "rfq_criteria")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RFQCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    @JsonIgnore  // ✅ Prevents lazy loading errors
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criterion_id", nullable = false)
    private EvaluationCriterion criterion;

    @Column(nullable = false)
    private Double weightage;  // Percentage weightage (e.g., 40.0 for 40%)
    // Total across all RFQCriteria for one RFQ must = 100%

    @Column(name = "created_by")
    private Long createdBy;  // User who assigned this criterion to RFQ

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * ✅ NEW: Indicates if this is an RFQ-specific criterion
     * FALSE = Organization-wide (created by Admin)
     * TRUE = RFQ-specific (created by RFQ Creator for this RFQ only)
     */
    @Column(name = "is_rfq_specific")
    private Boolean isRfqSpecific;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public RFQCriterion() {
        this.createdAt = LocalDateTime.now();
        this.isRfqSpecific = false;  // Default to organization-wide
    }

    /**
     * Constructor for creating RFQ Criterion
     */
    public RFQCriterion(RFQ rfq, EvaluationCriterion criterion, Double weightage, Long createdBy) {
        this.rfq = rfq;
        this.criterion = criterion;
        this.weightage = weightage;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isRfqSpecific = false;  // Default to organization-wide
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RFQ getRfq() {
        return rfq;
    }

    public void setRfq(RFQ rfq) {
        this.rfq = rfq;
    }

    public EvaluationCriterion getCriterion() {
        return criterion;
    }

    public void setCriterion(EvaluationCriterion criterion) {
        this.criterion = criterion;
    }

    public Double getWeightage() {
        return weightage;
    }

    public void setWeightage(Double weightage) {
        this.weightage = weightage;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRfqSpecific() {
        return isRfqSpecific;
    }

    public void setIsRfqSpecific(Boolean isRfqSpecific) {
        this.isRfqSpecific = isRfqSpecific;
    }

    // ==================== UTILITY METHODS ====================

    @Override
    public String toString() {
        return "RFQCriterion{" +
                "id=" + id +
                ", criterionName=" + (criterion != null ? criterion.getCriterionName() : "null") +
                ", weightage=" + weightage +
                ", isRfqSpecific=" + isRfqSpecific +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}