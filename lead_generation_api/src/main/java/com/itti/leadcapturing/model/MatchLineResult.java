package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "match_line_results")
public class MatchLineResult {

    public enum LineMatchStatus {
        MATCHED,
        QTY_SHORT,
        QTY_EXCESS,
        PRICE_VARIANCE,
        NOT_IN_PO,
        NOT_RECEIVED,
        PARTIALLY_MATCHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "three_way_match_id", nullable = false)
    private ThreeWayMatch threeWayMatch;

    // ── Item Reference ───────────────────────────────────────────────────────

    @Column(name = "po_line_item_id")
    private Long poLineItemId;

    @Column(name = "grn_line_item_id")
    private Long grnLineItemId;

    @Column(name = "invoice_line_item_id")
    private Long invoiceLineItemId;

    @Column(name = "item_order")
    private Integer itemOrder;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;

    @Column(name = "uom")
    private String uom;

    // ── Quantity Comparison ──────────────────────────────────────────────────

    @Column(name = "po_ordered_quantity", precision = 15, scale = 3)
    private BigDecimal poOrderedQuantity;

    @Column(name = "grn_accepted_quantity", precision = 15, scale = 3)
    private BigDecimal grnAcceptedQuantity;

    @Column(name = "invoice_quantity", precision = 15, scale = 3)
    private BigDecimal invoiceQuantity;

    @Column(name = "quantity_variance", precision = 15, scale = 3)
    private BigDecimal quantityVariance;            // grnAccepted - invoiceQty

    @Column(name = "quantity_variance_pct", precision = 8, scale = 4)
    private BigDecimal quantityVariancePct;

    // ── Price Comparison ─────────────────────────────────────────────────────

    @Column(name = "po_unit_rate", precision = 15, scale = 2)
    private BigDecimal poUnitRate;

    @Column(name = "invoice_unit_price", precision = 15, scale = 2)
    private BigDecimal invoiceUnitPrice;

    @Column(name = "price_variance", precision = 15, scale = 2)
    private BigDecimal priceVariance;               // invoicePrice - poRate

    @Column(name = "price_variance_pct", precision = 8, scale = 4)
    private BigDecimal priceVariancePct;

    // ── Value Comparison ─────────────────────────────────────────────────────

    @Column(name = "po_line_value", precision = 15, scale = 2)
    private BigDecimal poLineValue;                 // poQty × poRate

    @Column(name = "grn_accepted_value", precision = 15, scale = 2)
    private BigDecimal grnAcceptedValue;            // grnAcceptedQty × poRate

    @Column(name = "invoice_line_value", precision = 15, scale = 2)
    private BigDecimal invoiceLineValue;            // invoiceQty × invoicePrice

    @Column(name = "value_variance", precision = 15, scale = 2)
    private BigDecimal valueVariance;               // invoiceValue - grnAcceptedValue

    // ── Result ───────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "line_match_status", nullable = false)
    private LineMatchStatus lineMatchStatus = LineMatchStatus.MATCHED;

    @Column(name = "within_tolerance")
    private Boolean withinTolerance = true;

    @Column(name = "mismatch_notes", columnDefinition = "TEXT")
    private String mismatchNotes;

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ThreeWayMatch getThreeWayMatch() { return threeWayMatch; }
    public void setThreeWayMatch(ThreeWayMatch threeWayMatch) { this.threeWayMatch = threeWayMatch; }

    public Long getPoLineItemId() { return poLineItemId; }
    public void setPoLineItemId(Long poLineItemId) { this.poLineItemId = poLineItemId; }

    public Long getGrnLineItemId() { return grnLineItemId; }
    public void setGrnLineItemId(Long grnLineItemId) { this.grnLineItemId = grnLineItemId; }

    public Long getInvoiceLineItemId() { return invoiceLineItemId; }
    public void setInvoiceLineItemId(Long invoiceLineItemId) { this.invoiceLineItemId = invoiceLineItemId; }

    public Integer getItemOrder() { return itemOrder; }
    public void setItemOrder(Integer itemOrder) { this.itemOrder = itemOrder; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public BigDecimal getPoOrderedQuantity() { return poOrderedQuantity; }
    public void setPoOrderedQuantity(BigDecimal poOrderedQuantity) { this.poOrderedQuantity = poOrderedQuantity; }

    public BigDecimal getGrnAcceptedQuantity() { return grnAcceptedQuantity; }
    public void setGrnAcceptedQuantity(BigDecimal grnAcceptedQuantity) { this.grnAcceptedQuantity = grnAcceptedQuantity; }

    public BigDecimal getInvoiceQuantity() { return invoiceQuantity; }
    public void setInvoiceQuantity(BigDecimal invoiceQuantity) { this.invoiceQuantity = invoiceQuantity; }

    public BigDecimal getQuantityVariance() { return quantityVariance; }
    public void setQuantityVariance(BigDecimal quantityVariance) { this.quantityVariance = quantityVariance; }

    public BigDecimal getQuantityVariancePct() { return quantityVariancePct; }
    public void setQuantityVariancePct(BigDecimal quantityVariancePct) { this.quantityVariancePct = quantityVariancePct; }

    public BigDecimal getPoUnitRate() { return poUnitRate; }
    public void setPoUnitRate(BigDecimal poUnitRate) { this.poUnitRate = poUnitRate; }

    public BigDecimal getInvoiceUnitPrice() { return invoiceUnitPrice; }
    public void setInvoiceUnitPrice(BigDecimal invoiceUnitPrice) { this.invoiceUnitPrice = invoiceUnitPrice; }

    public BigDecimal getPriceVariance() { return priceVariance; }
    public void setPriceVariance(BigDecimal priceVariance) { this.priceVariance = priceVariance; }

    public BigDecimal getPriceVariancePct() { return priceVariancePct; }
    public void setPriceVariancePct(BigDecimal priceVariancePct) { this.priceVariancePct = priceVariancePct; }

    public BigDecimal getPoLineValue() { return poLineValue; }
    public void setPoLineValue(BigDecimal poLineValue) { this.poLineValue = poLineValue; }

    public BigDecimal getGrnAcceptedValue() { return grnAcceptedValue; }
    public void setGrnAcceptedValue(BigDecimal grnAcceptedValue) { this.grnAcceptedValue = grnAcceptedValue; }

    public BigDecimal getInvoiceLineValue() { return invoiceLineValue; }
    public void setInvoiceLineValue(BigDecimal invoiceLineValue) { this.invoiceLineValue = invoiceLineValue; }

    public BigDecimal getValueVariance() { return valueVariance; }
    public void setValueVariance(BigDecimal valueVariance) { this.valueVariance = valueVariance; }

    public LineMatchStatus getLineMatchStatus() { return lineMatchStatus; }
    public void setLineMatchStatus(LineMatchStatus lineMatchStatus) { this.lineMatchStatus = lineMatchStatus; }

    public Boolean getWithinTolerance() { return withinTolerance; }
    public void setWithinTolerance(Boolean withinTolerance) { this.withinTolerance = withinTolerance; }

    public String getMismatchNotes() { return mismatchNotes; }
    public void setMismatchNotes(String mismatchNotes) { this.mismatchNotes = mismatchNotes; }
}