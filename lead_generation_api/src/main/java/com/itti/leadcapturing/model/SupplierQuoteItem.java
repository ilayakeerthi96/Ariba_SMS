
package com.itti.leadcapturing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ FIXED: Supplier Quote Item with proper JSON serialization
 */
@Entity
@Table(name = "supplier_quote_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SupplierQuoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELATIONSHIPS ====================
    
    /**
     * Link to RFQSupplier (which supplier submitted this quote)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "rfq_supplier_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "rfq_id", referencedColumnName = "rfq_id", nullable = false),
        @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id", nullable = false)
    })
    @JsonIgnoreProperties({"rfq", "supplier", "hibernateLazyInitializer", "handler"})
    private RFQSupplier rfqSupplier;

    /**
     * Link to RFQItem (which item this quote is for)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_item_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"rfq", "hibernateLazyInitializer", "handler"})
    private RFQItem rfqItem;

    // ==================== PRICING FIELDS ====================
    
    @Column(name = "unit_rate", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitRate;

    @Column(name = "quoted_quantity", precision = 10, scale = 2)
    private BigDecimal quotedQuantity;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal;

    // ==================== SUPPLIER DETAILS ====================
    
    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "delivery_days")
    private Integer deliveryDays;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "brand_offered")
    private String brandOffered;

    @Column(name = "make_model")
    private String makeModel;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "payment_terms")
    private String paymentTerms;

    // ==================== STATUS & SELECTION ====================
    
    @Column(name = "is_selected", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSelected = false;

    @Column(name = "selected_date")
    private LocalDateTime selectedDate;

    @Column(name = "selection_remarks")
    private String selectionRemarks;

    // ==================== AUDIT FIELDS ====================
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public SupplierQuoteItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isSelected = false;
    }

    // ==================== JPA CALLBACKS ====================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.isSelected == null) {
            this.isSelected = false;
        }
        calculateAmounts();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateAmounts();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Calculate total amounts based on unit rate and quantity
     */
    public void calculateAmounts() {
        if (this.unitRate != null && this.quotedQuantity != null) {
            this.totalAmount = this.unitRate.multiply(this.quotedQuantity);
            
            if (this.taxPercentage != null && this.taxPercentage.compareTo(BigDecimal.ZERO) > 0) {
                this.taxAmount = this.totalAmount
                    .multiply(this.taxPercentage)
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            } else {
                this.taxAmount = BigDecimal.ZERO;
            }
            
            this.grandTotal = this.totalAmount.add(this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO);
        }
    }

    /**
     * Mark this quote as selected
     */
    public void selectQuote(String remarks) {
        this.isSelected = true;
        this.selectedDate = LocalDateTime.now();
        this.selectionRemarks = remarks;
    }

    /**
     * Unmark selection
     */
    public void unselectQuote() {
        this.isSelected = false;
        this.selectedDate = null;
        this.selectionRemarks = null;
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RFQSupplier getRfqSupplier() {
        return rfqSupplier;
    }

    public void setRfqSupplier(RFQSupplier rfqSupplier) {
        this.rfqSupplier = rfqSupplier;
    }

    public RFQItem getRfqItem() {
        return rfqItem;
    }

    public void setRfqItem(RFQItem rfqItem) {
        this.rfqItem = rfqItem;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public void setUnitRate(BigDecimal unitRate) {
        this.unitRate = unitRate;
        calculateAmounts();
    }

    public BigDecimal getQuotedQuantity() {
        return quotedQuantity;
    }

    public void setQuotedQuantity(BigDecimal quotedQuantity) {
        this.quotedQuantity = quotedQuantity;
        calculateAmounts();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
        calculateAmounts();
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrandOffered() {
        return brandOffered;
    }

    public void setBrandOffered(String brandOffered) {
        this.brandOffered = brandOffered;
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public LocalDateTime getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDateTime selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectionRemarks() {
        return selectionRemarks;
    }

    public void setSelectionRemarks(String selectionRemarks) {
        this.selectionRemarks = selectionRemarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}