package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ThreeWayMatch — PO ↔ GRN ↔ Invoice reconciliation record.
 *
 * One record per (invoice, grn) pair. Multiple match attempts on the same
 * invoice are versioned via matchVersion (1, 2, 3…).
 *
 * tolerance_percentage: allowable variance % before flagging a mismatch.
 * Default: 2% (configurable per organization / PO category).
 *
 * Outcome:
 *  ─ MATCHED           → send invoice to next approval stage automatically.
 *  ─ Any mismatch      → buyer chooses a MatchResolution; system enforces it.
 */
@Entity
@Table(name = "three_way_matches")
public class ThreeWayMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Document References ───────────────────────────────────────────────────

    @Column(name = "purchase_order_id", nullable = false)
    private Long purchaseOrderId;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "grn_id", nullable = false)
    private Long grnId;

    @Column(name = "grn_number")
    private String grnNumber;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    // ── Match Configuration ──────────────────────────────────────────────────

    /**
     * Allowable variance percentage before flagging a mismatch.
     * Applies to both quantity and price comparisons.
     * Default: 2.00 (i.e., 2%).
     */
    @Column(name = "tolerance_percentage", precision = 5, scale = 2)
    private BigDecimal tolerancePercentage = new BigDecimal("2.00");

    @Column(name = "match_version")
    private Integer matchVersion = 1;               // increments on re-match

    // ── Overall Match Result ─────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false)
    private ThreeWayMatchStatus matchStatus = ThreeWayMatchStatus.PENDING;

    @Column(name = "match_summary", columnDefinition = "TEXT")
    private String matchSummary;                    // human-readable summary

    // ── Financial Comparison ─────────────────────────────────────────────────

    @Column(name = "po_total_value", precision = 15, scale = 2)
    private BigDecimal poTotalValue;                // total PO value

    @Column(name = "grn_accepted_value", precision = 15, scale = 2)
    private BigDecimal grnAcceptedValue;            // value of accepted goods

    @Column(name = "invoice_total_value", precision = 15, scale = 2)
    private BigDecimal invoiceTotalValue;           // total invoice amount

    @Column(name = "variance_amount", precision = 15, scale = 2)
    private BigDecimal varianceAmount;              // invoiceTotal - grnAcceptedValue

    @Column(name = "variance_percentage", precision = 8, scale = 4)
    private BigDecimal variancePercentage;

    // ── Mismatch Flags ───────────────────────────────────────────────────────

    @Column(name = "has_quantity_mismatch")
    private Boolean hasQuantityMismatch = false;

    @Column(name = "has_price_mismatch")
    private Boolean hasPriceMismatch = false;

    @Column(name = "has_item_mismatch")
    private Boolean hasItemMismatch = false;

    @Column(name = "has_excess_delivery")
    private Boolean hasExcessDelivery = false;

    @Column(name = "total_mismatch_lines")
    private Integer totalMismatchLines = 0;

    @Column(name = "total_matched_lines")
    private Integer totalMatchedLines = 0;

    // ── Resolution ───────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution")
    private MatchResolution resolution = MatchResolution.PENDING_RESOLUTION;

    @Column(name = "resolution_remarks", columnDefinition = "TEXT")
    private String resolutionRemarks;

    @Column(name = "resolved_by_name")
    private String resolvedByName;

    @Column(name = "resolved_by_user_id")
    private Long resolvedByUserId;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * Approved invoice value after resolution (may differ from original invoice
     * e.g. when partial payment is approved).
     */
    @Column(name = "approved_payment_amount", precision = 15, scale = 2)
    private BigDecimal approvedPaymentAmount;

    // ── Audit ────────────────────────────────────────────────────────────────

    @Column(name = "performed_by_user_id", nullable = false)
    private Long performedByUserId;

    @Column(name = "performed_by_name")
    private String performedByName;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Line Results ─────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "threeWayMatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MatchLineResult> lineResults = new ArrayList<>();

    // ── Constructors / Lifecycle ─────────────────────────────────────────────

    public ThreeWayMatch() {
        this.performedAt = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.performedAt == null) this.performedAt = LocalDateTime.now();
        if (this.updatedAt == null)   this.updatedAt   = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public void addLineResult(MatchLineResult result) {
        result.setThreeWayMatch(this);
        this.lineResults.add(result);
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getTolerancePercentage() { return tolerancePercentage; }
    public void setTolerancePercentage(BigDecimal tolerancePercentage) { this.tolerancePercentage = tolerancePercentage; }

    public Integer getMatchVersion() { return matchVersion; }
    public void setMatchVersion(Integer matchVersion) { this.matchVersion = matchVersion; }

    public ThreeWayMatchStatus getMatchStatus() { return matchStatus; }
    public void setMatchStatus(ThreeWayMatchStatus matchStatus) { this.matchStatus = matchStatus; }

    public String getMatchSummary() { return matchSummary; }
    public void setMatchSummary(String matchSummary) { this.matchSummary = matchSummary; }

    public BigDecimal getPoTotalValue() { return poTotalValue; }
    public void setPoTotalValue(BigDecimal poTotalValue) { this.poTotalValue = poTotalValue; }

    public BigDecimal getGrnAcceptedValue() { return grnAcceptedValue; }
    public void setGrnAcceptedValue(BigDecimal grnAcceptedValue) { this.grnAcceptedValue = grnAcceptedValue; }

    public BigDecimal getInvoiceTotalValue() { return invoiceTotalValue; }
    public void setInvoiceTotalValue(BigDecimal invoiceTotalValue) { this.invoiceTotalValue = invoiceTotalValue; }

    public BigDecimal getVarianceAmount() { return varianceAmount; }
    public void setVarianceAmount(BigDecimal varianceAmount) { this.varianceAmount = varianceAmount; }

    public BigDecimal getVariancePercentage() { return variancePercentage; }
    public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }

    public Boolean getHasQuantityMismatch() { return hasQuantityMismatch; }
    public void setHasQuantityMismatch(Boolean hasQuantityMismatch) { this.hasQuantityMismatch = hasQuantityMismatch; }

    public Boolean getHasPriceMismatch() { return hasPriceMismatch; }
    public void setHasPriceMismatch(Boolean hasPriceMismatch) { this.hasPriceMismatch = hasPriceMismatch; }

    public Boolean getHasItemMismatch() { return hasItemMismatch; }
    public void setHasItemMismatch(Boolean hasItemMismatch) { this.hasItemMismatch = hasItemMismatch; }

    public Boolean getHasExcessDelivery() { return hasExcessDelivery; }
    public void setHasExcessDelivery(Boolean hasExcessDelivery) { this.hasExcessDelivery = hasExcessDelivery; }

    public Integer getTotalMismatchLines() { return totalMismatchLines; }
    public void setTotalMismatchLines(Integer totalMismatchLines) { this.totalMismatchLines = totalMismatchLines; }

    public Integer getTotalMatchedLines() { return totalMatchedLines; }
    public void setTotalMatchedLines(Integer totalMatchedLines) { this.totalMatchedLines = totalMatchedLines; }

    public MatchResolution getResolution() { return resolution; }
    public void setResolution(MatchResolution resolution) { this.resolution = resolution; }

    public String getResolutionRemarks() { return resolutionRemarks; }
    public void setResolutionRemarks(String resolutionRemarks) { this.resolutionRemarks = resolutionRemarks; }

    public String getResolvedByName() { return resolvedByName; }
    public void setResolvedByName(String resolvedByName) { this.resolvedByName = resolvedByName; }

    public Long getResolvedByUserId() { return resolvedByUserId; }
    public void setResolvedByUserId(Long resolvedByUserId) { this.resolvedByUserId = resolvedByUserId; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public BigDecimal getApprovedPaymentAmount() { return approvedPaymentAmount; }
    public void setApprovedPaymentAmount(BigDecimal approvedPaymentAmount) { this.approvedPaymentAmount = approvedPaymentAmount; }

    public Long getPerformedByUserId() { return performedByUserId; }
    public void setPerformedByUserId(Long performedByUserId) { this.performedByUserId = performedByUserId; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }

    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<MatchLineResult> getLineResults() { return lineResults; }
    public void setLineResults(List<MatchLineResult> lineResults) { this.lineResults = lineResults; }
}