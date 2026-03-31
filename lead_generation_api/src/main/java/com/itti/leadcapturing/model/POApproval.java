package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "po_approvals")
public class POApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_level_id", nullable = false)
    private HierarchyLevel hierarchyLevel;

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

    // ==================== HOLD TRACKING ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "held_by_user_id")
    private HierarchyUser heldByUser;

    @Column(name = "hold_remarks", length = 1000)
    private String holdRemarks;

    @Column(name = "hold_date")
    private LocalDateTime holdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "released_by_user_id")
    private HierarchyUser releasedByUser;

    @Column(name = "release_remarks", length = 1000)
    private String releaseRemarks;

    @Column(name = "released_date")
    private LocalDateTime releasedDate;

    // ==================== REJECT TRACKING ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rejected_by_user_id")
    private HierarchyUser rejectedByUser;

    @Column(name = "reject_remarks", length = 1000)
    private String rejectRemarks;

    @Column(name = "reject_date")
    private LocalDateTime rejectDate;

    // ==================== RETURN FOR REVISION TRACKING ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "returned_by_user_id")
    private HierarchyUser returnedByUser;

    @Column(name = "return_remarks", length = 1000)
    private String returnRemarks;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    // ==================== AUDIT ====================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public POApproval() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApprovalActionStatus.PENDING;
    }

    public POApproval(PurchaseOrder purchaseOrder, HierarchyLevel hierarchyLevel, Integer sequence) {
        this();
        this.purchaseOrder = purchaseOrder;
        this.hierarchyLevel = hierarchyLevel;
        this.sequenceOrder = sequence;
    }

    // ==================== JPA CALLBACKS ====================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== HELPER METHODS ====================

    public boolean isOnHold() {
        return ApprovalActionStatus.HOLD.equals(this.status);
    }

    public boolean wasRejected() {
        return ApprovalActionStatus.REJECTED.equals(this.status);
    }

    public boolean wasHeldAndReleased() {
        return this.holdDate != null && this.releasedDate != null;
    }

    public String getHoldDisplayInfo() {
        if (this.heldByUser == null) return null;
        StringBuilder info = new StringBuilder();
        info.append("HELD by ").append(this.heldByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.holdRemarks != null) info.append(" - Reason: ").append(this.holdRemarks);
        if (this.releasedByUser != null) {
            info.append(" | RELEASED by ").append(this.releasedByUser.getFullName());
            if (this.releaseRemarks != null) info.append(" - ").append(this.releaseRemarks);
        }
        return info.toString();
    }

    public String getRejectDisplayInfo() {
        if (this.rejectedByUser == null) return null;
        StringBuilder info = new StringBuilder();
        info.append("REJECTED by ").append(this.rejectedByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.rejectRemarks != null) info.append(" - Reason: ").append(this.rejectRemarks);
        return info.toString();
    }

    public String getReturnDisplayInfo() {
        if (this.returnedByUser == null) return null;
        StringBuilder info = new StringBuilder();
        info.append("RETURNED FOR REVISION by ").append(this.returnedByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.returnRemarks != null) info.append(" - Reason: ").append(this.returnRemarks);
        return info.toString();
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public HierarchyLevel getHierarchyLevel() { return hierarchyLevel; }
    public void setHierarchyLevel(HierarchyLevel hierarchyLevel) { this.hierarchyLevel = hierarchyLevel; }

    public HierarchyUser getApproverUser() { return approverUser; }
    public void setApproverUser(HierarchyUser approverUser) { this.approverUser = approverUser; }

    public ApprovalActionStatus getStatus() { return status; }
    public void setStatus(ApprovalActionStatus status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

    public Integer getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public HierarchyUser getHeldByUser() { return heldByUser; }
    public void setHeldByUser(HierarchyUser heldByUser) { this.heldByUser = heldByUser; }

    public String getHoldRemarks() { return holdRemarks; }
    public void setHoldRemarks(String holdRemarks) { this.holdRemarks = holdRemarks; }

    public LocalDateTime getHoldDate() { return holdDate; }
    public void setHoldDate(LocalDateTime holdDate) { this.holdDate = holdDate; }

    public HierarchyUser getReleasedByUser() { return releasedByUser; }
    public void setReleasedByUser(HierarchyUser releasedByUser) { this.releasedByUser = releasedByUser; }

    public String getReleaseRemarks() { return releaseRemarks; }
    public void setReleaseRemarks(String releaseRemarks) { this.releaseRemarks = releaseRemarks; }

    public LocalDateTime getReleasedDate() { return releasedDate; }
    public void setReleasedDate(LocalDateTime releasedDate) { this.releasedDate = releasedDate; }

    public HierarchyUser getRejectedByUser() { return rejectedByUser; }
    public void setRejectedByUser(HierarchyUser rejectedByUser) { this.rejectedByUser = rejectedByUser; }

    public String getRejectRemarks() { return rejectRemarks; }
    public void setRejectRemarks(String rejectRemarks) { this.rejectRemarks = rejectRemarks; }

    public LocalDateTime getRejectDate() { return rejectDate; }
    public void setRejectDate(LocalDateTime rejectDate) { this.rejectDate = rejectDate; }

    public HierarchyUser getReturnedByUser() { return returnedByUser; }
    public void setReturnedByUser(HierarchyUser returnedByUser) { this.returnedByUser = returnedByUser; }

    public String getReturnRemarks() { return returnRemarks; }
    public void setReturnRemarks(String returnRemarks) { this.returnRemarks = returnRemarks; }

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}