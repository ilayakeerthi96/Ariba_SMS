package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ Tracks the RFQ Creator's final supplier selection decision.
 *
 * Two cases:
 * 1. System-recommended supplier selected  → justification optional (remarks only)
 * 2. Non-recommended supplier selected      → justification MANDATORY
 *
 * After selection, a price negotiation review is done before creating the PO.
 */
@Entity
@Table(name = "supplier_final_selections",
       uniqueConstraints = @UniqueConstraint(columnNames = {"rfq_id"}))
public class SupplierFinalSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================== RELATIONSHIPS =====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // ===================== SYSTEM RECOMMENDATION INFO =====================

    @Column(name = "is_system_recommended", nullable = false)
    private Boolean isSystemRecommended = false;

    @Column(name = "system_recommended_rank")
    private Integer systemRecommendedRank;

    @Column(name = "system_final_score", precision = 10, scale = 2)
    private BigDecimal systemFinalScore;

    // ===================== SELECTION DECISION =====================

    /**
     * MANDATORY when isSystemRecommended = false.
     * Why the creator chose a different supplier over the system recommendation.
     */
    @Column(name = "justification", length = 2000)
    private String justification;

    /** OPTIONAL remarks for any selection. */
    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "selected_by_user_id", nullable = false)
    private Long selectedByUserId;

    @Column(name = "selected_by_user_name")
    private String selectedByUserName;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_status", nullable = false)
    private SelectionStatus selectionStatus = SelectionStatus.SELECTED;

    @Column(name = "selected_at")
    private LocalDateTime selectedAt;

    // ===================== NEGOTIATION / PO LINK =====================

    @Column(name = "po_negotiation_id")
    private Long poNegotiationId;

    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;

    // ===================== AUDIT =====================

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SupplierFinalSelection() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.selectedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.selectedAt == null) this.selectedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public Boolean getIsSystemRecommended() { return isSystemRecommended; }
    public void setIsSystemRecommended(Boolean v) { this.isSystemRecommended = v; }
    public Integer getSystemRecommendedRank() { return systemRecommendedRank; }
    public void setSystemRecommendedRank(Integer v) { this.systemRecommendedRank = v; }
    public BigDecimal getSystemFinalScore() { return systemFinalScore; }
    public void setSystemFinalScore(BigDecimal v) { this.systemFinalScore = v; }
    public String getJustification() { return justification; }
    public void setJustification(String v) { this.justification = v; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String v) { this.remarks = v; }
    public Long getSelectedByUserId() { return selectedByUserId; }
    public void setSelectedByUserId(Long v) { this.selectedByUserId = v; }
    public String getSelectedByUserName() { return selectedByUserName; }
    public void setSelectedByUserName(String v) { this.selectedByUserName = v; }
    public SelectionStatus getSelectionStatus() { return selectionStatus; }
    public void setSelectionStatus(SelectionStatus v) { this.selectionStatus = v; }
    public LocalDateTime getSelectedAt() { return selectedAt; }
    public void setSelectedAt(LocalDateTime v) { this.selectedAt = v; }
    public Long getPoNegotiationId() { return poNegotiationId; }
    public void setPoNegotiationId(Long v) { this.poNegotiationId = v; }
    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long v) { this.purchaseOrderId = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}