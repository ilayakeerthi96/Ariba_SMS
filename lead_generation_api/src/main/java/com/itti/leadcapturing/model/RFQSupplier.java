// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import java.time.LocalDateTime;
// import java.math.BigDecimal;

// @Entity
// @Table(name = "rfq_suppliers")
// public class RFQSupplier {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "rfq_id", nullable = false)
//     @JsonBackReference("rfq-suppliers")
//     private RFQ rfq;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "supplier_id", nullable = false)
//     private Supplier supplier;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "supplier_department_id")
//     private SupplierDepartment supplierDepartment;

//     @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
//     private String status = "PENDING"; // PENDING, SENT, RESPONDED, SELECTED, REJECTED

//     @Column(name = "sent_at")
//     private LocalDateTime sentAt;

//     @Column(name = "responded_at")
//     private LocalDateTime respondedAt;

//     @Column(name = "quote_amount", precision = 15, scale = 2)
//     private BigDecimal quoteAmount;

//     @Column(name = "notes", columnDefinition = "TEXT")
//     private String notes;

//     @Column(name = "created_at", nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;

//     @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
//     private Boolean isDeleted = false;

//     @PrePersist
//     protected void onCreate() {
//         createdAt = LocalDateTime.now();
//         updatedAt = LocalDateTime.now();
//     }

//     @PreUpdate
//     protected void onUpdate() {
//         updatedAt = LocalDateTime.now();
//     }

//     // Constructors
//     public RFQSupplier() {}

//     public RFQSupplier(RFQ rfq, Supplier supplier, SupplierDepartment supplierDepartment) {
//         this.rfq = rfq;
//         this.supplier = supplier;
//         this.supplierDepartment = supplierDepartment;
//         this.status = "PENDING";
//         this.isDeleted = false;
//     }

//     // Getters and Setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public RFQ getRfq() { return rfq; }
//     public void setRfq(RFQ rfq) { this.rfq = rfq; }

//     public Supplier getSupplier() { return supplier; }
//     public void setSupplier(Supplier supplier) { this.supplier = supplier; }

//     public SupplierDepartment getSupplierDepartment() { return supplierDepartment; }
//     public void setSupplierDepartment(SupplierDepartment supplierDepartment) { this.supplierDepartment = supplierDepartment; }

//     public String getStatus() { return status; }
//     public void setStatus(String status) { this.status = status; }

//     public LocalDateTime getSentAt() { return sentAt; }
//     public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

//     public LocalDateTime getRespondedAt() { return respondedAt; }
//     public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

//     public BigDecimal getQuoteAmount() { return quoteAmount; }
//     public void setQuoteAmount(BigDecimal quoteAmount) { this.quoteAmount = quoteAmount; }

//     public String getNotes() { return notes; }
//     public void setNotes(String notes) { this.notes = notes; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

//     public Boolean getIsDeleted() { return isDeleted; }
//     public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "rfq_suppliers")
public class RFQSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rfq_id", nullable = false)
    @JsonBackReference("rfq-suppliers")
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_department_id")
    private SupplierDepartment supplierDepartment;

    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    private String status = "PENDING"; // PENDING, SENT, RESPONDED, SELECTED, REJECTED

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "quote_amount", precision = 15, scale = 2)
    private BigDecimal quoteAmount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

@Column(name = "created_at", updatable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
private LocalDateTime createdAt;


@Column(name = "updated_at",
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public RFQSupplier() {}

    public RFQSupplier(RFQ rfq, Supplier supplier, SupplierDepartment supplierDepartment) {
        this.rfq = rfq;
        this.supplier = supplier;
        this.supplierDepartment = supplierDepartment;
        this.status = "PENDING";
        this.isDeleted = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public SupplierDepartment getSupplierDepartment() { return supplierDepartment; }
    public void setSupplierDepartment(SupplierDepartment supplierDepartment) { this.supplierDepartment = supplierDepartment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

    public BigDecimal getQuoteAmount() { return quoteAmount; }
    public void setQuoteAmount(BigDecimal quoteAmount) { this.quoteAmount = quoteAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}