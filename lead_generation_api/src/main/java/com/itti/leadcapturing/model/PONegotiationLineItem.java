
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "po_negotiation_line_items")
public class PONegotiationLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_negotiation_id", nullable = false)
    private PONegotiation poNegotiation;

    @Column(name = "rfq_item_id")
    private Long rfqItemId;

    @Column(name = "supplier_quote_item_id")
    private Long supplierQuoteItemId;

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

    /** Original quoted price from supplier (read-only reference) */
    @Column(name = "actual_quoted_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal actualQuotedPrice;

    /** Negotiated discount percentage (0–100) */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    /** Calculated: actualQuotedPrice × discountPercentage / 100 */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** Finalized price per unit after discount */
    @Column(name = "finalized_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal finalizedPrice;

    /** Pre-tax line total = quantity × finalizedPrice */
    @Column(name = "line_total", precision = 15, scale = 2)
    private BigDecimal lineTotal;

    // ✅ NEW: Per-line Tax fields

    /** Tax percentage applied to this specific line item (0–100) */
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    /** Calculated: lineTotal × taxPercentage / 100 */
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

    public PONegotiationLineItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.discountPercentage = BigDecimal.ZERO;
        this.discountAmount     = BigDecimal.ZERO;
        this.taxPercentage      = BigDecimal.ZERO;
        this.taxAmount          = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.taxPercentage == null) this.taxPercentage = BigDecimal.ZERO;
        if (this.taxAmount == null)     this.taxAmount = BigDecimal.ZERO;
        calculatePrices();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculatePrices();
    }

    /**
     * Calculates:
     *   1. discountAmount from discountPercentage (if not using manual finalizedPrice)
     *   2. finalizedPrice = actualQuotedPrice - discountAmount
     *   3. lineTotal      = quantity × finalizedPrice   (PRE-TAX)
     *   4. taxAmount      = lineTotal × taxPercentage / 100
     */
    public void calculatePrices() {
        if (this.actualQuotedPrice == null) return;

        // --- Discount ---
        if (this.discountPercentage != null && this.discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.discountAmount = this.actualQuotedPrice
                    .multiply(this.discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.finalizedPrice = this.actualQuotedPrice.subtract(this.discountAmount);
        } else {
            this.discountAmount = BigDecimal.ZERO;
            if (this.finalizedPrice == null) {
                this.finalizedPrice = this.actualQuotedPrice;
            }
        }

        // --- Pre-tax line total ---
        if (this.quantity != null && this.finalizedPrice != null) {
            this.lineTotal = this.quantity.multiply(this.finalizedPrice).setScale(2, RoundingMode.HALF_UP);
        }

        // --- Tax amount (per line) ---
        BigDecimal taxPct = (this.taxPercentage != null) ? this.taxPercentage : BigDecimal.ZERO;
        if (this.lineTotal != null) {
            this.taxAmount = this.lineTotal
                    .multiply(taxPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Set finalized price manually (overrides discount-based calculation).
     * Recalculates discount %, discountAmount, lineTotal, taxAmount.
     */
    public void setFinalizedPriceManually(BigDecimal manualFinalizedPrice) {
        this.finalizedPrice = manualFinalizedPrice;

        if (this.actualQuotedPrice != null && this.actualQuotedPrice.compareTo(BigDecimal.ZERO) > 0) {
            this.discountAmount     = this.actualQuotedPrice.subtract(manualFinalizedPrice);
            this.discountPercentage = this.discountAmount
                    .multiply(new BigDecimal("100"))
                    .divide(this.actualQuotedPrice, 2, RoundingMode.HALF_UP);
        }

        if (this.quantity != null) {
            this.lineTotal = this.quantity.multiply(this.finalizedPrice).setScale(2, RoundingMode.HALF_UP);
        }

        // Recalculate tax with the new lineTotal
        BigDecimal taxPct = (this.taxPercentage != null) ? this.taxPercentage : BigDecimal.ZERO;
        if (this.lineTotal != null) {
            this.taxAmount = this.lineTotal
                    .multiply(taxPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PONegotiation getPoNegotiation() { return poNegotiation; }
    public void setPoNegotiation(PONegotiation v) { this.poNegotiation = v; }

    public Long getRfqItemId() { return rfqItemId; }
    public void setRfqItemId(Long v) { this.rfqItemId = v; }

    public Long getSupplierQuoteItemId() { return supplierQuoteItemId; }
    public void setSupplierQuoteItemId(Long v) { this.supplierQuoteItemId = v; }

    public Integer getSlNo() { return slNo; }
    public void setSlNo(Integer v) { this.slNo = v; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String v) { this.itemCode = v; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String v) { this.itemDescription = v; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String v) { this.specifications = v; }

    public String getBrandMakeModel() { return brandMakeModel; }
    public void setBrandMakeModel(String v) { this.brandMakeModel = v; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal v) { this.quantity = v; }

    public String getUom() { return uom; }
    public void setUom(String v) { this.uom = v; }

    public BigDecimal getActualQuotedPrice() { return actualQuotedPrice; }
    public void setActualQuotedPrice(BigDecimal v) { this.actualQuotedPrice = v; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal v) { this.discountPercentage = v; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal v) { this.discountAmount = v; }

    public BigDecimal getFinalizedPrice() { return finalizedPrice; }
    public void setFinalizedPrice(BigDecimal v) { this.finalizedPrice = v; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal v) { this.lineTotal = v; }

    // ✅ NEW getters/setters
    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal v) {
        this.taxPercentage = (v != null) ? v : BigDecimal.ZERO;
    }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal v) {
        this.taxAmount = (v != null) ? v : BigDecimal.ZERO;
    }

    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer v) { this.deliveryDays = v; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer v) { this.warrantyMonths = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}