

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== PO IDENTIFICATION ====================

    @Column(name = "po_number", unique = true, nullable = false)
    private String poNumber;

    @Column(name = "po_date", nullable = false)
    private LocalDateTime poDate;

    @Column(name = "reference_quote_no")
    private String referenceQuoteNo;

    // ==================== BUYER DETAILS ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonIgnoreProperties({"locations", "users", "departments", "createdByOrgAdmin", "hibernateLazyInitializer", "handler"})
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_location_id", nullable = false)
    @JsonIgnoreProperties({"departments", "hibernateLazyInitializer", "handler"})
    private Location buyerLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_location_id", nullable = false)
    @JsonIgnoreProperties({"departments", "hibernateLazyInitializer", "handler"})
    private Location deliveryLocation;

    // ==================== SUPPLIER DETAILS ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnoreProperties({"departments", "supplierLocations", "hibernateLazyInitializer", "handler"})
    private Supplier supplier;

    // ==================== RFQ REFERENCE ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rfq_id", nullable = false)
    @JsonIgnoreProperties({"items", "suppliers", "createdByUser", "buyer", "location", "hibernateLazyInitializer", "handler"})
    private RFQ rfq;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    // ==================== LINE ITEMS ====================

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("po-lineitems")
    private List<POLineItem> lineItems = new ArrayList<>();

    // ==================== FINANCIAL DETAILS ====================

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal grandTotal;

    @Column(name = "amount_in_words", length = 500)
    private String amountInWords;

    // ==================== ✅ NEW: CURRENCY (copied from buyer location) ====================

    @Column(name = "currency_code", length = 10)
    private String currencyCode = "INR";

    @Column(name = "currency_symbol", length = 10)
    private String currencySymbol = "₹";

    // ==================== TERMS & CONDITIONS ====================

    @Column(name = "payment_terms", length = 500)
    private String paymentTerms;

    @Column(name = "delivery_terms", length = 500)
    private String deliveryTerms;

    @Column(name = "other_terms", columnDefinition = "TEXT")
    private String otherTerms;

    @Column(name = "dispatched_through")
    private String dispatchedThrough;

    @Column(name = "mode_of_payment")
    private String modeOfPayment;

    @Column(name = "destination")
    private String destination;

    // ==================== REMARKS ====================

    @Column(name = "buyer_remarks", columnDefinition = "TEXT")
    private String buyerRemarks;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "revision_remarks", length = 1000)
    private String revisionRemarks;

    // ==================== STATUS & WORKFLOW ====================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private POStatus status = POStatus.DRAFT;

    @Column(name = "approval_required")
    private Boolean approvalRequired = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approved_by_name")
    private String approvedByName;

    @Column(name = "approved_by_designation")
    private String approvedByDesignation;

    // ==================== AUDIT FIELDS ====================

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ==================== CONSTRUCTORS ====================

    public PurchaseOrder() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.status = POStatus.DRAFT;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.approvalRequired = false;
        this.lineItems = new ArrayList<>();
        this.currencyCode = "INR";
        this.currencySymbol = "₹";
    }

    // ==================== JPA LIFECYCLE ====================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.poDate == null) this.poDate = LocalDateTime.now();
        if (this.currencyCode == null) this.currencyCode = "INR";
        if (this.currencySymbol == null) this.currencySymbol = "₹";
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // ==================== HELPER METHODS ====================

    public void calculateTotals() {
        if (this.lineItems == null || this.lineItems.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.taxAmount = BigDecimal.ZERO;
            this.grandTotal = BigDecimal.ZERO;
            return;
        }
        this.subtotal = this.lineItems.stream()
                .map(POLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (this.taxPercentage != null && this.taxPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.taxAmount = this.subtotal
                    .multiply(this.taxPercentage)
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.taxAmount = BigDecimal.ZERO;
        }
        this.grandTotal = this.subtotal.add(this.taxAmount);
    }

    public void addLineItem(POLineItem lineItem) {
        if (this.lineItems == null) this.lineItems = new ArrayList<>();
        lineItem.setPurchaseOrder(this);
        this.lineItems.add(lineItem);
        calculateTotals();
    }

    public void removeLineItem(POLineItem lineItem) {
        if (this.lineItems != null) {
            this.lineItems.remove(lineItem);
            lineItem.setPurchaseOrder(null);
            calculateTotals();
        }
    }

    // ==================== GETTERS & SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public LocalDateTime getPoDate() { return poDate; }
    public void setPoDate(LocalDateTime poDate) { this.poDate = poDate; }

    public String getReferenceQuoteNo() { return referenceQuoteNo; }
    public void setReferenceQuoteNo(String referenceQuoteNo) { this.referenceQuoteNo = referenceQuoteNo; }

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public Location getBuyerLocation() { return buyerLocation; }
    public void setBuyerLocation(Location buyerLocation) { this.buyerLocation = buyerLocation; }

    public Location getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(Location deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public List<POLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<POLineItem> lineItems) { this.lineItems = lineItems; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public String getAmountInWords() { return amountInWords; }
    public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }

    // ✅ NEW: Currency getters/setters
    public String getCurrencyCode() { return currencyCode != null ? currencyCode : "INR"; }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = (currencyCode != null && !currencyCode.isEmpty()) ? currencyCode : "INR";
    }

    public String getCurrencySymbol() { return currencySymbol != null ? currencySymbol : "₹"; }
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = (currencySymbol != null && !currencySymbol.isEmpty()) ? currencySymbol : "₹";
    }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }

    public String getOtherTerms() { return otherTerms; }
    public void setOtherTerms(String otherTerms) { this.otherTerms = otherTerms; }

    public String getDispatchedThrough() { return dispatchedThrough; }
    public void setDispatchedThrough(String dispatchedThrough) { this.dispatchedThrough = dispatchedThrough; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getBuyerRemarks() { return buyerRemarks; }
    public void setBuyerRemarks(String buyerRemarks) { this.buyerRemarks = buyerRemarks; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public POStatus getStatus() { return status; }
    public void setStatus(POStatus status) { this.status = status; }

    public Boolean getApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(Boolean approvalRequired) { this.approvalRequired = approvalRequired; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getApprovedByDesignation() { return approvedByDesignation; }
    public void setApprovedByDesignation(String approvedByDesignation) { this.approvedByDesignation = approvedByDesignation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getRevisionRemarks() { return revisionRemarks; }
    public void setRevisionRemarks(String revisionRemarks) { this.revisionRemarks = revisionRemarks; }
}