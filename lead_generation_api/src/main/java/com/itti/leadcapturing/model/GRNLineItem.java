package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "grn_line_items")
public class GRNLineItem {

    public enum ItemCondition {
        GOOD, PARTIAL, DAMAGED, WRONG_ITEM, SHORT_DELIVERY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GRN grn;

    // ── PO References (denormalized for auditability) ────────────────────────

    @Column(name = "po_line_item_id", nullable = false)
    private Long poLineItemId;

    @Column(name = "item_order")
    private Integer itemOrder;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "item_description", nullable = false, columnDefinition = "TEXT")
    private String itemDescription;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "brand_make_model")
    private String brandMakeModel;

    @Column(name = "uom", nullable = false)
    private String uom;

    // ── Phase 1: PO Quantities (reference) ──────────────────────────────────

    @Column(name = "ordered_quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal orderedQuantity;             // from PO

    @Column(name = "po_unit_rate", precision = 15, scale = 2)
    private BigDecimal poUnitRate;                  // PO price per unit

    @Column(name = "po_line_total", precision = 15, scale = 2)
    private BigDecimal poLineTotal;                 // orderedQty × poUnitRate

    // ── Phase 1: Received Quantity (filled by receiver at GRN creation) ──────

    @Column(name = "received_quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal receivedQuantity;            // physically arrived

    // ── Phase 2: QA Review Quantities (filled by QA team) ───────────────────

    /**
     * How many units the QA team found to be defective/damaged.
     * Set during QA_REVIEW phase. Zero until QA is performed.
     */
    @Column(name = "defective_quantity", precision = 15, scale = 3)
    private BigDecimal defectiveQuantity = BigDecimal.ZERO;

    /**
     * Formally rejected quantity (usually = defectiveQuantity, but QA may
     * choose to accept some defective units with a waiver).
     * Set during QA_REVIEW phase.
     */
    @Column(name = "rejected_quantity", precision = 15, scale = 3)
    private BigDecimal rejectedQuantity = BigDecimal.ZERO;

    /**
     * Auto-computed: receivedQuantity - rejectedQuantity.
     * Locked after GRN is APPROVED.
     */
    @Column(name = "accepted_quantity", precision = 15, scale = 3)
    private BigDecimal acceptedQuantity = BigDecimal.ZERO;

    /**
     * QA remarks / reason for rejection per line item.
     * Mandatory when rejectedQuantity > 0.
     */
    @Column(name = "qa_remarks", columnDefinition = "TEXT")
    private String qaRemarks;

    // ── Phase 2: Computed Values (after QA) ─────────────────────────────────

    @Column(name = "accepted_value", precision = 15, scale = 2)
    private BigDecimal acceptedValue;               // acceptedQty × poUnitRate

    @Column(name = "rejected_value", precision = 15, scale = 2)
    private BigDecimal rejectedValue;               // rejectedQty × poUnitRate

    // ── Condition & Remarks ──────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition")
    private ItemCondition itemCondition = ItemCondition.GOOD;

    /**
     * Legacy field — kept for backward compatibility.
     * Use qaRemarks for new QA-driven rejections.
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // ── Audit ────────────────────────────────────────────────────────────────

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public GRNLineItem() {
        this.createdAt          = LocalDateTime.now();
        this.updatedAt          = LocalDateTime.now();
        this.rejectedQuantity   = BigDecimal.ZERO;
        this.acceptedQuantity   = BigDecimal.ZERO;
        this.defectiveQuantity  = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null)        this.createdAt         = LocalDateTime.now();
        if (this.updatedAt == null)        this.updatedAt         = LocalDateTime.now();
        if (this.rejectedQuantity == null) this.rejectedQuantity  = BigDecimal.ZERO;
        if (this.acceptedQuantity == null) this.acceptedQuantity  = BigDecimal.ZERO;
        if (this.defectiveQuantity == null) this.defectiveQuantity = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // ── Phase 1: initialise after GRN creation ───────────────────────────────

    /**
     * Called after Phase 1 (GRN creation).
     * acceptedQuantity = receivedQuantity (optimistic — QA not done yet).
     * All rejection fields remain zero.
     */
    public void initialiseAfterCreation() {
        this.rejectedQuantity  = BigDecimal.ZERO;
        this.defectiveQuantity = BigDecimal.ZERO;
        // Optimistically set accepted = received; QA will adjust
        this.acceptedQuantity  = this.receivedQuantity != null ? this.receivedQuantity : BigDecimal.ZERO;
        this.itemCondition     = ItemCondition.GOOD;
        recalculateValues();
    }

    // ── Phase 2: recalculate after QA review ────────────────────────────────

    /**
     * Called after Phase 2 (QA review submission).
     * Derives:
     *   rejectedQuantity = defectiveQuantity (QA decides — caller may override)
     *   acceptedQuantity = receivedQuantity  - rejectedQuantity
     *   acceptedValue    = acceptedQuantity  × poUnitRate
     *   rejectedValue    = rejectedQuantity  × poUnitRate
     *   itemCondition    = auto-derived
     */
    public void recalculateAfterQA() {
        // Ensure rejected qty is set (defaults to defective qty if not explicitly set)
        if (this.rejectedQuantity == null) {
            this.rejectedQuantity = this.defectiveQuantity != null ? this.defectiveQuantity : BigDecimal.ZERO;
        }
        if (this.defectiveQuantity == null) {
            this.defectiveQuantity = BigDecimal.ZERO;
        }

        // acceptedQty = receivedQty - rejectedQty (never negative)
        if (this.receivedQuantity != null) {
            this.acceptedQuantity = this.receivedQuantity
                    .subtract(this.rejectedQuantity)
                    .max(BigDecimal.ZERO);
        }

        recalculateValues();
        deriveCondition();
    }

    /**
     * Legacy method — kept for any callers that use the old API.
     * Prefer recalculateAfterQA() for new code.
     */
    public void recalculate() {
        recalculateAfterQA();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void recalculateValues() {
        if (this.poUnitRate != null) {
            if (this.acceptedQuantity != null)
                this.acceptedValue = this.acceptedQuantity.multiply(this.poUnitRate)
                        .setScale(2, RoundingMode.HALF_UP);
            if (this.rejectedQuantity != null)
                this.rejectedValue = this.rejectedQuantity.multiply(this.poUnitRate)
                        .setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void deriveCondition() {
        if (this.rejectedQuantity == null || this.acceptedQuantity == null) return;

        if (this.rejectedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.itemCondition = ItemCondition.GOOD;
        } else if (this.acceptedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.itemCondition = ItemCondition.DAMAGED;
        } else {
            this.itemCondition = ItemCondition.PARTIAL;
        }

        // Short-delivery check (takes priority for condition display)
        if (this.orderedQuantity != null && this.receivedQuantity != null
                && this.receivedQuantity.compareTo(this.orderedQuantity) < 0) {
            // Only set SHORT_DELIVERY if there are no rejections on top
            if (this.rejectedQuantity.compareTo(BigDecimal.ZERO) == 0) {
                this.itemCondition = ItemCondition.SHORT_DELIVERY;
            }
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GRN getGrn() { return grn; }
    public void setGrn(GRN grn) { this.grn = grn; }

    public Long getPoLineItemId() { return poLineItemId; }
    public void setPoLineItemId(Long poLineItemId) { this.poLineItemId = poLineItemId; }

    public Integer getItemOrder() { return itemOrder; }
    public void setItemOrder(Integer itemOrder) { this.itemOrder = itemOrder; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getBrandMakeModel() { return brandMakeModel; }
    public void setBrandMakeModel(String brandMakeModel) { this.brandMakeModel = brandMakeModel; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public BigDecimal getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(BigDecimal orderedQuantity) { this.orderedQuantity = orderedQuantity; }

    public BigDecimal getPoUnitRate() { return poUnitRate; }
    public void setPoUnitRate(BigDecimal poUnitRate) { this.poUnitRate = poUnitRate; }

    public BigDecimal getPoLineTotal() { return poLineTotal; }
    public void setPoLineTotal(BigDecimal poLineTotal) { this.poLineTotal = poLineTotal; }

    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public BigDecimal getDefectiveQuantity() { return defectiveQuantity; }
    public void setDefectiveQuantity(BigDecimal defectiveQuantity) {
        this.defectiveQuantity = defectiveQuantity != null ? defectiveQuantity : BigDecimal.ZERO;
    }

    public BigDecimal getRejectedQuantity() { return rejectedQuantity; }
    public void setRejectedQuantity(BigDecimal rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity != null ? rejectedQuantity : BigDecimal.ZERO;
    }

    public BigDecimal getAcceptedQuantity() { return acceptedQuantity; }
    public void setAcceptedQuantity(BigDecimal acceptedQuantity) { this.acceptedQuantity = acceptedQuantity; }

    public String getQaRemarks() { return qaRemarks; }
    public void setQaRemarks(String qaRemarks) { this.qaRemarks = qaRemarks; }

    public BigDecimal getAcceptedValue() { return acceptedValue; }
    public void setAcceptedValue(BigDecimal acceptedValue) { this.acceptedValue = acceptedValue; }

    public BigDecimal getRejectedValue() { return rejectedValue; }
    public void setRejectedValue(BigDecimal rejectedValue) { this.rejectedValue = rejectedValue; }

    public ItemCondition getItemCondition() { return itemCondition; }
    public void setItemCondition(ItemCondition itemCondition) { this.itemCondition = itemCondition; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}