package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grns")
public class GRN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grn_number", unique = true, nullable = false)
    private String grnNumber;                       

    @Column(name = "received_date", nullable = false)
    private LocalDateTime receivedDate;

    @Column(name = "purchase_order_id", nullable = false)
    private Long purchaseOrderId;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "invoice_id")
    private Long invoiceId;                         // linked after invoice is submitted

    @Column(name = "invoice_number")
    private String invoiceNumber;

    // ── Supplier ─────────────────────────────────────────────────────────────

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    // ── Delivery Metadata ────────────────────────────────────────────────────

    @Column(name = "delivery_challan_number")
    private String deliveryChallanNumber;

    @Column(name = "lr_number")
    private String lrNumber;                        // Lorry Receipt / Airway Bill

    @Column(name = "transporter_name")
    private String transporterName;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "delivery_location")
    private String deliveryLocation;               // warehouse / store location

    // ── Receiver Info ────────────────────────────────────────────────────────

    @Column(name = "received_by_user_id", nullable = false)
    private Long receivedByUserId;

    @Column(name = "received_by_name")
    private String receivedByName;

    @Column(name = "inspected_by_name")
    private String inspectedByName;

    // ── Status ───────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GRNStatus status = GRNStatus.DRAFT;

    @Column(name = "approved_by_name")
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // ── Remarks ──────────────────────────────────────────────────────────────

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    // ── Totals ───────────────────────────────────────────────────────────────

    @Column(name = "total_ordered_value", precision = 15, scale = 2)
    private BigDecimal totalOrderedValue;           // sum of PO line totals

    @Column(name = "total_received_value", precision = 15, scale = 2)
    private BigDecimal totalReceivedValue;          // sum of (acceptedQty × unitRate)

    @Column(name = "total_rejected_value", precision = 15, scale = 2)
    private BigDecimal totalRejectedValue;

    // ── Audit ────────────────────────────────────────────────────────────────

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Line Items ───────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GRNLineItem> lineItems = new ArrayList<>();

    // ── Constructors / Lifecycle ─────────────────────────────────────────────

    public GRN() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    public void addLineItem(GRNLineItem item) {
        item.setGrn(this);
        this.lineItems.add(item);
    }

    /**
     * Recalculate GRN totals from line items.
     * Call this after all line items are populated.
     */
    public void recalculateTotals() {
        BigDecimal received = BigDecimal.ZERO;
        BigDecimal rejected = BigDecimal.ZERO;
        for (GRNLineItem li : this.lineItems) {
            if (li.getAcceptedValue() != null) received = received.add(li.getAcceptedValue());
            if (li.getRejectedValue() != null) rejected = rejected.add(li.getRejectedValue());
        }
        this.totalReceivedValue = received;
        this.totalRejectedValue = rejected;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public LocalDateTime getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDateTime receivedDate) { this.receivedDate = receivedDate; }

    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getDeliveryChallanNumber() { return deliveryChallanNumber; }
    public void setDeliveryChallanNumber(String v) { this.deliveryChallanNumber = v; }

    public String getLrNumber() { return lrNumber; }
    public void setLrNumber(String lrNumber) { this.lrNumber = lrNumber; }

    public String getTransporterName() { return transporterName; }
    public void setTransporterName(String transporterName) { this.transporterName = transporterName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public Long getReceivedByUserId() { return receivedByUserId; }
    public void setReceivedByUserId(Long receivedByUserId) { this.receivedByUserId = receivedByUserId; }

    public String getReceivedByName() { return receivedByName; }
    public void setReceivedByName(String receivedByName) { this.receivedByName = receivedByName; }

    public String getInspectedByName() { return inspectedByName; }
    public void setInspectedByName(String inspectedByName) { this.inspectedByName = inspectedByName; }

    public GRNStatus getStatus() { return status; }
    public void setStatus(GRNStatus status) { this.status = status; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public BigDecimal getTotalOrderedValue() { return totalOrderedValue; }
    public void setTotalOrderedValue(BigDecimal totalOrderedValue) { this.totalOrderedValue = totalOrderedValue; }

    public BigDecimal getTotalReceivedValue() { return totalReceivedValue; }
    public void setTotalReceivedValue(BigDecimal totalReceivedValue) { this.totalReceivedValue = totalReceivedValue; }

    public BigDecimal getTotalRejectedValue() { return totalRejectedValue; }
    public void setTotalRejectedValue(BigDecimal totalRejectedValue) { this.totalRejectedValue = totalRejectedValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<GRNLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<GRNLineItem> lineItems) { this.lineItems = lineItems; }
}