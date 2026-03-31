// ==================== FILE: src/main/java/com/itti/leadcapturing/model/DynamicRFQApproval.java ====================

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ✅ NEW: Dynamic RFQ Approval Entity
 * Replaces static ApprovalLevel enum with dynamic hierarchy levels
 */
@Entity
@Table(name = "dynamic_rfq_approvals")
public class DynamicRFQApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    /**
     * ✅ NEW: Reference to HierarchyLevel instead of enum
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_level_id", nullable = false)
    private HierarchyLevel hierarchyLevel;

    /**
     * ✅ NEW: Reference to HierarchyUser (the approver)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_user_id")
    private HierarchyUser approverUser;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalActionStatus status = ApprovalActionStatus.PENDING;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public DynamicRFQApproval() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApprovalActionStatus.PENDING;
    }

    public DynamicRFQApproval(RFQ rfq, HierarchyLevel hierarchyLevel, Integer sequence) {
        this();
        this.rfq = rfq;
        this.hierarchyLevel = hierarchyLevel;
        this.sequenceOrder = sequence;
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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RFQ getRfq() {
        return rfq;
    }

    public void setRfq(RFQ rfq) {
        this.rfq = rfq;
    }

    public HierarchyLevel getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(HierarchyLevel hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public HierarchyUser getApproverUser() {
        return approverUser;
    }

    public void setApproverUser(HierarchyUser approverUser) {
        this.approverUser = approverUser;
    }

    public ApprovalActionStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalActionStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
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