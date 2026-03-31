
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    public enum InvoiceStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED,
        REJECTED_CLOSED,
        PAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    // ── Supplier info ────────────────────────────────────────────────────────
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_email")
    private String supplierEmail;

    // ── Buyer / company info ─────────────────────────────────────────────────
    @Column(name = "buyer_company_name")
    private String buyerCompanyName;

    // ── RFQ Creator (direct routing – replaces 3-Way Match) ──────────────────
    @Column(name = "rfq_creator_user_id")
    private Long rfqCreatorUserId;

    @Column(name = "rfq_creator_email")
    private String rfqCreatorEmail;

    @Column(name = "rfq_creator_name")
    private String rfqCreatorName;

    // ── PO / RFQ references ──────────────────────────────────────────────────
    @Column(name = "purchase_order_id", nullable = false)   // ✅ FIXED: was "po_id"
    private Long poId;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "rfq_id")
    private Long rfqId;

    @Column(name = "rfq_number")
    private String rfqNumber;

    // ── Financial details ────────────────────────────────────────────────────
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 10)
    private String currency;

    // ── Bank details ─────────────────────────────────────────────────────────
    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_ifsc_code")
    private String bankIfscCode;

    @Column(name = "bank_swift_code")
    private String bankSwiftCode;

    // ── Status ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    // ── Approval / rejection tracking ────────────────────────────────────────
    @Column(name = "approval_remarks", columnDefinition = "TEXT")
    private String approvalRemarks;

    @Column(name = "approved_rejected_by")
    private String approvedRejectedBy;

    @Column(name = "approved_rejected_at")
    private LocalDateTime approvedRejectedAt;

    // ── Resubmission tracking ────────────────────────────────────────────────
    @Column(name = "resubmit_count")
    private Integer resubmitCount = 0;

    @Column(name = "resubmit_remarks", columnDefinition = "TEXT")
    private String resubmitRemarks;

    @Column(name = "resubmitted_at")
    private LocalDateTime resubmittedAt;

    // ── Payment tracking ─────────────────────────────────────────────────────
    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "paid_by")
    private String paidBy;

    // ── Dates ────────────────────────────────────────────────────────────────
    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Notes & Terms ────────────────────────────────────────────────────────
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_terms", length = 500)
    private String paymentTerms;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    // ── Line items ───────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    // ── Helper ───────────────────────────────────────────────────────────────
    public boolean canResubmit() {
        return status == InvoiceStatus.REJECTED;
    }
}