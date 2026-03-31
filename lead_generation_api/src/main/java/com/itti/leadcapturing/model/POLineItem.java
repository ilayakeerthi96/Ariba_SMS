
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "po_line_items")
public class POLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @JsonBackReference("po-lineitems")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rfq_item_id")
    @JsonIgnoreProperties({"rfq", "hibernateLazyInitializer", "handler"})
    private RFQItem rfqItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_quote_item_id")
    @JsonIgnoreProperties({"rfqSupplier", "supplier", "rfq", "hibernateLazyInitializer", "handler"})
    private SupplierQuoteItem supplierQuoteItem;

    @Column(name = "sl_no")
    private Integer slNo;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "item_description", nullable = false, columnDefinition = "TEXT")
    private String itemDescription;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "brand_make_model")
    private String brandMakeModel;

    @Column(name = "quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal quantity;

    @Column(name = "uom", nullable = false)
    private String uom;

    /** Finalized unit rate (after discount — this is the price on the PO) */
    @Column(name = "unit_rate", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitRate;

    /** Pre-tax line total = quantity × unitRate */
    @Column(name = "line_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    // ✅ Negotiation tracking fields

    /** Original quoted price before discount (for reference/display) */
    @Column(name = "original_quoted_price", precision = 15, scale = 2)
    private BigDecimal originalQuotedPrice;

    /** Discount % that was applied during negotiation (0 if none) */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    /** Tax % applied to this line item */
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    /** Tax amount = lineTotal × taxPercentage / 100 */
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "delivery_days")
    private Integer deliveryDays;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public POLineItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.discountPercentage = BigDecimal.ZERO;
        this.taxPercentage      = BigDecimal.ZERO;
        this.taxAmount          = BigDecimal.ZERO;
    }

    public POLineItem(String itemDescription, BigDecimal quantity, String uom, BigDecimal unitRate) {
        this.itemDescription = itemDescription;
        this.quantity        = quantity;
        this.uom             = uom;
        this.unitRate        = unitRate;
        this.lineTotal       = quantity.multiply(unitRate);
        this.discountPercentage = BigDecimal.ZERO;
        this.taxPercentage      = BigDecimal.ZERO;
        this.taxAmount          = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.discountPercentage == null) this.discountPercentage = BigDecimal.ZERO;
        if (this.taxPercentage == null)      this.taxPercentage = BigDecimal.ZERO;
        if (this.taxAmount == null)          this.taxAmount = BigDecimal.ZERO;
        calculateLineTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateLineTotal();
    }

    public void calculateLineTotal() {
        if (this.quantity != null && this.unitRate != null) {
            this.lineTotal = this.quantity.multiply(this.unitRate).setScale(2, RoundingMode.HALF_UP);
        }
        // Recalculate tax amount
        if (this.lineTotal != null && this.taxPercentage != null
                && this.taxPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.taxAmount = this.lineTotal
                    .multiply(this.taxPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            this.taxAmount = BigDecimal.ZERO;
        }
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder po) { this.purchaseOrder = po; }

    public RFQItem getRfqItem() { return rfqItem; }
    public void setRfqItem(RFQItem rfqItem) { this.rfqItem = rfqItem; }

    public SupplierQuoteItem getSupplierQuoteItem() { return supplierQuoteItem; }
    public void setSupplierQuoteItem(SupplierQuoteItem sqi) { this.supplierQuoteItem = sqi; }

    public Integer getSlNo() { return slNo; }
    public void setSlNo(Integer slNo) { this.slNo = slNo; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getBrandMakeModel() { return brandMakeModel; }
    public void setBrandMakeModel(String brandMakeModel) { this.brandMakeModel = brandMakeModel; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; calculateLineTotal(); }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public BigDecimal getUnitRate() { return unitRate; }
    public void setUnitRate(BigDecimal unitRate) { this.unitRate = unitRate; calculateLineTotal(); }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public BigDecimal getOriginalQuotedPrice() { return originalQuotedPrice; }
    public void setOriginalQuotedPrice(BigDecimal v) { this.originalQuotedPrice = v; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal v) { this.discountPercentage = (v != null) ? v : BigDecimal.ZERO; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal v) {
        this.taxPercentage = (v != null) ? v : BigDecimal.ZERO;
        calculateLineTotal(); // recalculate taxAmount
    }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal v) { this.taxAmount = (v != null) ? v : BigDecimal.ZERO; }

    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer deliveryDays) { this.deliveryDays = deliveryDays; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}