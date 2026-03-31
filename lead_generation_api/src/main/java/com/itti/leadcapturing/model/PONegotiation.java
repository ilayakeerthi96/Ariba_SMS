// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// /**
//  * ✅ PO Negotiation / Review Entity
//  *
//  * Represents the price review & negotiation step that happens BEFORE
//  * the actual Purchase Order is created.
//  *
//  * The RFQ creator can:
//  *  - See the original quoted price
//  *  - Apply a discount % (after negotiation with supplier)
//  *  - Set a finalized price
//  * Then create the PO from these finalized prices.
//  */
// @Entity
// @Table(name = "po_negotiations")
// public class PONegotiation {

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
//     @JoinColumn(name = "final_selection_id")
//     private SupplierFinalSelection finalSelection;

//     @Column(name = "created_by_user_id", nullable = false)
//     private Long createdByUserId;

//     // ===================== LINE ITEMS =====================

//     @OneToMany(mappedBy = "poNegotiation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//     private List<PONegotiationLineItem> lineItems = new ArrayList<>();

//     // ===================== TOTALS =====================

//     @Column(name = "subtotal_quoted", precision = 15, scale = 2)
//     private BigDecimal subtotalQuoted;

//     @Column(name = "subtotal_finalized", precision = 15, scale = 2)
//     private BigDecimal subtotalFinalized;

//     @Column(name = "total_discount_amount", precision = 15, scale = 2)
//     private BigDecimal totalDiscountAmount;

//     @Column(name = "tax_percentage", precision = 5, scale = 2)
//     private BigDecimal taxPercentage;

//     @Column(name = "tax_amount", precision = 15, scale = 2)
//     private BigDecimal taxAmount;

//     @Column(name = "grand_total_quoted", precision = 15, scale = 2)
//     private BigDecimal grandTotalQuoted;

//     @Column(name = "grand_total_finalized", precision = 15, scale = 2)
//     private BigDecimal grandTotalFinalized;

//     // ===================== TERMS =====================

//     @Column(name = "payment_terms", length = 500)
//     private String paymentTerms;

//     @Column(name = "delivery_terms", length = 500)
//     private String deliveryTerms;

//     @Column(name = "other_terms", columnDefinition = "TEXT")
//     private String otherTerms;

//     @Column(name = "buyer_remarks", columnDefinition = "TEXT")
//     private String buyerRemarks;

//     // ===================== STATUS =====================

//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", nullable = false)
//     private NegotiationStatus status = NegotiationStatus.DRAFT;

//     @Column(name = "purchase_order_id")
//     private Long purchaseOrderId; // Set after PO is created

//     // ===================== AUDIT =====================

//     @Column(name = "created_at", updatable = false, nullable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "updated_at", nullable = false)
//     private LocalDateTime updatedAt;

//     public enum NegotiationStatus {
//         DRAFT,       // Being reviewed / edited
//         FINALIZED,   // Prices locked in
//         PO_CREATED   // PO has been generated
//     }

//     public PONegotiation() {
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//     }

//     @PrePersist
//     protected void onCreate() {
//         if (this.createdAt == null) this.createdAt = LocalDateTime.now();
//         if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
//     }

//     @PreUpdate
//     protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

//     public void addLineItem(PONegotiationLineItem item) {
//         if (this.lineItems == null) this.lineItems = new ArrayList<>();
//         item.setPoNegotiation(this);
//         this.lineItems.add(item);
//     }

//     // ===================== GETTERS & SETTERS =====================

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
//     public RFQ getRfq() { return rfq; }
//     public void setRfq(RFQ rfq) { this.rfq = rfq; }
//     public Supplier getSupplier() { return supplier; }
//     public void setSupplier(Supplier supplier) { this.supplier = supplier; }
//     public SupplierFinalSelection getFinalSelection() { return finalSelection; }
//     public void setFinalSelection(SupplierFinalSelection v) { this.finalSelection = v; }
//     public Long getCreatedByUserId() { return createdByUserId; }
//     public void setCreatedByUserId(Long v) { this.createdByUserId = v; }
//     public List<PONegotiationLineItem> getLineItems() { return lineItems; }
//     public void setLineItems(List<PONegotiationLineItem> v) { this.lineItems = v; }
//     public BigDecimal getSubtotalQuoted() { return subtotalQuoted; }
//     public void setSubtotalQuoted(BigDecimal v) { this.subtotalQuoted = v; }
//     public BigDecimal getSubtotalFinalized() { return subtotalFinalized; }
//     public void setSubtotalFinalized(BigDecimal v) { this.subtotalFinalized = v; }
//     public BigDecimal getTotalDiscountAmount() { return totalDiscountAmount; }
//     public void setTotalDiscountAmount(BigDecimal v) { this.totalDiscountAmount = v; }
//     public BigDecimal getTaxPercentage() { return taxPercentage; }
//     public void setTaxPercentage(BigDecimal v) { this.taxPercentage = v; }
//     public BigDecimal getTaxAmount() { return taxAmount; }
//     public void setTaxAmount(BigDecimal v) { this.taxAmount = v; }
//     public BigDecimal getGrandTotalQuoted() { return grandTotalQuoted; }
//     public void setGrandTotalQuoted(BigDecimal v) { this.grandTotalQuoted = v; }
//     public BigDecimal getGrandTotalFinalized() { return grandTotalFinalized; }
//     public void setGrandTotalFinalized(BigDecimal v) { this.grandTotalFinalized = v; }
//     public String getPaymentTerms() { return paymentTerms; }
//     public void setPaymentTerms(String v) { this.paymentTerms = v; }
//     public String getDeliveryTerms() { return deliveryTerms; }
//     public void setDeliveryTerms(String v) { this.deliveryTerms = v; }
//     public String getOtherTerms() { return otherTerms; }
//     public void setOtherTerms(String v) { this.otherTerms = v; }
//     public String getBuyerRemarks() { return buyerRemarks; }
//     public void setBuyerRemarks(String v) { this.buyerRemarks = v; }
//     public NegotiationStatus getStatus() { return status; }
//     public void setStatus(NegotiationStatus v) { this.status = v; }
//     public Long getPurchaseOrderId() { return purchaseOrderId; }
//     public void setPurchaseOrderId(Long v) { this.purchaseOrderId = v; }
//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ PO Negotiation / Review Entity
 */
@Entity
@Table(name = "po_negotiations")
public class PONegotiation {

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
    @JoinColumn(name = "final_selection_id")
    private SupplierFinalSelection finalSelection;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    // ===================== LINE ITEMS =====================

    @OneToMany(mappedBy = "poNegotiation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PONegotiationLineItem> lineItems = new ArrayList<>();

    // ===================== LINE-LEVEL TOTALS =====================

    @Column(name = "subtotal_quoted", precision = 15, scale = 2)
    private BigDecimal subtotalQuoted;

    @Column(name = "subtotal_finalized", precision = 15, scale = 2)
    private BigDecimal subtotalFinalized;

    @Column(name = "total_discount_amount", precision = 15, scale = 2)
    private BigDecimal totalDiscountAmount;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total_quoted", precision = 15, scale = 2)
    private BigDecimal grandTotalQuoted;

    /** subtotalFinalized + totalTax (before overall discount) */
    @Column(name = "grand_total_finalized", precision = 15, scale = 2)
    private BigDecimal grandTotalFinalized;

    // ===================== ✅ OVERALL / HEADER DISCOUNT =====================

    /**
     * Additional discount applied on top of line-item discounts.
     * Applied on subtotalFinalized (pre-tax).
     */
    @Column(name = "overall_discount_amount", precision = 15, scale = 2)
    private BigDecimal overallDiscountAmount = BigDecimal.ZERO;

    /** Percentage equivalent of overallDiscountAmount / subtotalFinalized */
    @Column(name = "overall_discount_percentage", precision = 5, scale = 2)
    private BigDecimal overallDiscountPercentage = BigDecimal.ZERO;

    /**
     * ✅ NET PAYABLE — the FINAL amount that goes on the PO.
     * = subtotalFinalized − overallDiscountAmount + taxAmount
     */
    @Column(name = "net_payable", precision = 15, scale = 2)
    private BigDecimal netPayable = BigDecimal.ZERO;

    // ===================== TERMS =====================

    @Column(name = "payment_terms", length = 500)
    private String paymentTerms;

    @Column(name = "delivery_terms", length = 500)
    private String deliveryTerms;

    @Column(name = "other_terms", columnDefinition = "TEXT")
    private String otherTerms;

    @Column(name = "buyer_remarks", columnDefinition = "TEXT")
    private String buyerRemarks;

    // ===================== STATUS =====================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NegotiationStatus status = NegotiationStatus.DRAFT;

    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;

    // ===================== AUDIT =====================

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum NegotiationStatus {
        DRAFT,
        FINALIZED,
        PO_CREATED
    }

    public PONegotiation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.overallDiscountAmount     = BigDecimal.ZERO;
        this.overallDiscountPercentage = BigDecimal.ZERO;
        this.netPayable                = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.overallDiscountAmount == null)     this.overallDiscountAmount = BigDecimal.ZERO;
        if (this.overallDiscountPercentage == null) this.overallDiscountPercentage = BigDecimal.ZERO;
        if (this.netPayable == null)                this.netPayable = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public void addLineItem(PONegotiationLineItem item) {
        if (this.lineItems == null) this.lineItems = new ArrayList<>();
        item.setPoNegotiation(this);
        this.lineItems.add(item);
    }

    // ===================== GETTERS & SETTERS =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public SupplierFinalSelection getFinalSelection() { return finalSelection; }
    public void setFinalSelection(SupplierFinalSelection v) { this.finalSelection = v; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long v) { this.createdByUserId = v; }
    public List<PONegotiationLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<PONegotiationLineItem> v) { this.lineItems = v; }
    public BigDecimal getSubtotalQuoted() { return subtotalQuoted; }
    public void setSubtotalQuoted(BigDecimal v) { this.subtotalQuoted = v; }
    public BigDecimal getSubtotalFinalized() { return subtotalFinalized; }
    public void setSubtotalFinalized(BigDecimal v) { this.subtotalFinalized = v; }
    public BigDecimal getTotalDiscountAmount() { return totalDiscountAmount; }
    public void setTotalDiscountAmount(BigDecimal v) { this.totalDiscountAmount = v; }
    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal v) { this.taxPercentage = v; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal v) { this.taxAmount = v; }
    public BigDecimal getGrandTotalQuoted() { return grandTotalQuoted; }
    public void setGrandTotalQuoted(BigDecimal v) { this.grandTotalQuoted = v; }
    public BigDecimal getGrandTotalFinalized() { return grandTotalFinalized; }
    public void setGrandTotalFinalized(BigDecimal v) { this.grandTotalFinalized = v; }

    // ✅ Overall discount getters/setters
    public BigDecimal getOverallDiscountAmount() { return overallDiscountAmount; }
    public void setOverallDiscountAmount(BigDecimal v) {
        this.overallDiscountAmount = (v != null) ? v : BigDecimal.ZERO;
    }
    public BigDecimal getOverallDiscountPercentage() { return overallDiscountPercentage; }
    public void setOverallDiscountPercentage(BigDecimal v) {
        this.overallDiscountPercentage = (v != null) ? v : BigDecimal.ZERO;
    }
    public BigDecimal getNetPayable() { return netPayable; }
    public void setNetPayable(BigDecimal v) { this.netPayable = (v != null) ? v : BigDecimal.ZERO; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String v) { this.paymentTerms = v; }
    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String v) { this.deliveryTerms = v; }
    public String getOtherTerms() { return otherTerms; }
    public void setOtherTerms(String v) { this.otherTerms = v; }
    public String getBuyerRemarks() { return buyerRemarks; }
    public void setBuyerRemarks(String v) { this.buyerRemarks = v; }
    public NegotiationStatus getStatus() { return status; }
    public void setStatus(NegotiationStatus v) { this.status = v; }
    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long v) { this.purchaseOrderId = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}