package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_line_items")
@Data
@NoArgsConstructor
public class InvoiceLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Reference to original PO line item (optional, for traceability)
    @Column(name = "po_line_item_id")
    private Long poLineItemId;

    @Column(name = "item_order")
    private Integer itemOrder;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "item_description", nullable = false, columnDefinition = "TEXT")
    private String itemDescription;

    @Column(name = "item_description_detailed", columnDefinition = "TEXT")
    private String itemDescriptionDetailed;

    @Column(name = "uom", length = 50)
    private String uom;

    @Column(name = "quantity", precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "line_total", precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "hsn_sac_code", length = 20)
    private String hsnSacCode;   // HSN/SAC code for GST compliance

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}