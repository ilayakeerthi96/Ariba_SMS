// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// /**
//  * Supplier Approval entity.
//  * Actions supported: APPROVE, REJECT, HOLD, RELEASE_HOLD.
//  * No Return-for-Revision — supplier approvals are simpler than RFQ/PO.
//  */
// @Entity
// @Table(name = "supplier_approvals")
// public class SupplierApproval {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "supplier_id", nullable = false)
//     private Supplier supplier;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "hierarchy_level_id", nullable = false)
//     private HierarchyLevel hierarchyLevel;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "approver_user_id")
//     private HierarchyUser approverUser;

//     @Column(name = "status", nullable = false)
//     @Enumerated(EnumType.STRING)
//     private ApprovalActionStatus status = ApprovalActionStatus.PENDING;

//     @Column(name = "comments", length = 1000)
//     private String comments;

//     @Column(name = "action_date")
//     private LocalDateTime actionDate;

//     @Column(name = "sequence_order", nullable = false)
//     private Integer sequenceOrder;

//     // ==================== HOLD TRACKING ====================

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "held_by_user_id")
//     private HierarchyUser heldByUser;

//     @Column(name = "hold_remarks", length = 1000)
//     private String holdRemarks;

//     @Column(name = "hold_date")
//     private LocalDateTime holdDate;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "released_by_user_id")
//     private HierarchyUser releasedByUser;

//     @Column(name = "release_remarks", length = 1000)
//     private String releaseRemarks;

//     @Column(name = "released_date")
//     private LocalDateTime releasedDate;

//     // ==================== REJECT TRACKING ====================

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "rejected_by_user_id")
//     private HierarchyUser rejectedByUser;

//     @Column(name = "reject_remarks", length = 1000)
//     private String rejectRemarks;

//     @Column(name = "reject_date")
//     private LocalDateTime rejectDate;

//     // ==================== AUDIT ====================

//     @Column(name = "created_at", nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;

//     // ==================== CONSTRUCTORS ====================

//     public SupplierApproval() {
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//         this.status = ApprovalActionStatus.PENDING;
//     }

//     public SupplierApproval(Supplier supplier, HierarchyLevel hierarchyLevel, Integer sequence) {
//         this();
//         this.supplier = supplier;
//         this.hierarchyLevel = hierarchyLevel;
//         this.sequenceOrder = sequence;
//     }

//     // ==================== JPA CALLBACKS ====================

//     @PrePersist
//     protected void onCreate() {
//         if (this.createdAt == null) this.createdAt = LocalDateTime.now();
//         if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
//     }

//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }

//     // ==================== HELPER METHODS ====================

//     public boolean isOnHold() {
//         return ApprovalActionStatus.HOLD.equals(this.status);
//     }

//     public boolean wasRejected() {
//         return ApprovalActionStatus.REJECTED.equals(this.status);
//     }

//     public boolean wasHeldAndReleased() {
//         return this.holdDate != null && this.releasedDate != null;
//     }

//     public String getHoldDisplayInfo() {
//         if (this.heldByUser == null) return null;
//         StringBuilder info = new StringBuilder();
//         info.append("HELD by ").append(this.heldByUser.getFullName());
//         info.append(" at ").append(this.hierarchyLevel.getLevelName());
//         if (this.holdRemarks != null) info.append(" - Reason: ").append(this.holdRemarks);
//         if (this.releasedByUser != null) {
//             info.append(" | RELEASED by ").append(this.releasedByUser.getFullName());
//             if (this.releaseRemarks != null) info.append(" - ").append(this.releaseRemarks);
//         }
//         return info.toString();
//     }

//     public String getRejectDisplayInfo() {
//         if (this.rejectedByUser == null) return null;
//         StringBuilder info = new StringBuilder();
//         info.append("REJECTED by ").append(this.rejectedByUser.getFullName());
//         info.append(" at ").append(this.hierarchyLevel.getLevelName());
//         if (this.rejectRemarks != null) info.append(" - Reason: ").append(this.rejectRemarks);
//         return info.toString();
//     }

//     // ==================== GETTERS AND SETTERS ====================

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public Supplier getSupplier() { return supplier; }
//     public void setSupplier(Supplier supplier) { this.supplier = supplier; }

//     public HierarchyLevel getHierarchyLevel() { return hierarchyLevel; }
//     public void setHierarchyLevel(HierarchyLevel hierarchyLevel) { this.hierarchyLevel = hierarchyLevel; }

//     public HierarchyUser getApproverUser() { return approverUser; }
//     public void setApproverUser(HierarchyUser approverUser) { this.approverUser = approverUser; }

//     public ApprovalActionStatus getStatus() { return status; }
//     public void setStatus(ApprovalActionStatus status) { this.status = status; }

//     public String getComments() { return comments; }
//     public void setComments(String comments) { this.comments = comments; }

//     public LocalDateTime getActionDate() { return actionDate; }
//     public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

//     public Integer getSequenceOrder() { return sequenceOrder; }
//     public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

//     public HierarchyUser getHeldByUser() { return heldByUser; }
//     public void setHeldByUser(HierarchyUser heldByUser) { this.heldByUser = heldByUser; }

//     public String getHoldRemarks() { return holdRemarks; }
//     public void setHoldRemarks(String holdRemarks) { this.holdRemarks = holdRemarks; }

//     public LocalDateTime getHoldDate() { return holdDate; }
//     public void setHoldDate(LocalDateTime holdDate) { this.holdDate = holdDate; }

//     public HierarchyUser getReleasedByUser() { return releasedByUser; }
//     public void setReleasedByUser(HierarchyUser releasedByUser) { this.releasedByUser = releasedByUser; }

//     public String getReleaseRemarks() { return releaseRemarks; }
//     public void setReleaseRemarks(String releaseRemarks) { this.releaseRemarks = releaseRemarks; }

//     public LocalDateTime getReleasedDate() { return releasedDate; }
//     public void setReleasedDate(LocalDateTime releasedDate) { this.releasedDate = releasedDate; }

//     public HierarchyUser getRejectedByUser() { return rejectedByUser; }
//     public void setRejectedByUser(HierarchyUser rejectedByUser) { this.rejectedByUser = rejectedByUser; }

//     public String getRejectRemarks() { return rejectRemarks; }
//     public void setRejectRemarks(String rejectRemarks) { this.rejectRemarks = rejectRemarks; }

//     public LocalDateTime getRejectDate() { return rejectDate; }
//     public void setRejectDate(LocalDateTime rejectDate) { this.rejectDate = rejectDate; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
// }


package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Supplier Approval entity.
 * Actions supported: APPROVE, REJECT, HOLD, RELEASE_HOLD, NEED_MORE_INFO.
 */
@Entity
@Table(name = "supplier_approvals")
public class SupplierApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_level_id", nullable = false)
    private HierarchyLevel hierarchyLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_user_id")
    private HierarchyUser approverUser;

   @Column(name = "status", nullable = false, length = 30)
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

    // ==================== NEED MORE INFO TRACKING ====================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "info_requested_by_user_id")
    private HierarchyUser infoRequestedByUser;

    @Column(name = "info_request", length = 2000)
    private String infoRequest;

    @Column(name = "info_request_date")
    private LocalDateTime infoRequestDate;

    @Column(name = "info_response", length = 2000)
    private String infoResponse;

    @Column(name = "info_response_date")
    private LocalDateTime infoResponseDate;

    // ==================== AUDIT ====================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public SupplierApproval() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApprovalActionStatus.PENDING;
    }

    public SupplierApproval(Supplier supplier, HierarchyLevel hierarchyLevel, Integer sequence) {
        this();
        this.supplier = supplier;
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

    public boolean isNeedMoreInfo() {
        return ApprovalActionStatus.NEED_MORE_INFO.equals(this.status);
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

    public String getNeedMoreInfoDisplayInfo() {
        if (this.infoRequestedByUser == null) return null;
        StringBuilder info = new StringBuilder();
        info.append("INFO REQUESTED by ").append(this.infoRequestedByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.infoRequest != null) info.append(" - Query: ").append(this.infoRequest);
        if (this.infoResponse != null) info.append(" | RESPONSE: ").append(this.infoResponse);
        return info.toString();
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

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

    public HierarchyUser getInfoRequestedByUser() { return infoRequestedByUser; }
    public void setInfoRequestedByUser(HierarchyUser infoRequestedByUser) { this.infoRequestedByUser = infoRequestedByUser; }

    public String getInfoRequest() { return infoRequest; }
    public void setInfoRequest(String infoRequest) { this.infoRequest = infoRequest; }

    public LocalDateTime getInfoRequestDate() { return infoRequestDate; }
    public void setInfoRequestDate(LocalDateTime infoRequestDate) { this.infoRequestDate = infoRequestDate; }

    public String getInfoResponse() { return infoResponse; }
    public void setInfoResponse(String infoResponse) { this.infoResponse = infoResponse; }

    public LocalDateTime getInfoResponseDate() { return infoResponseDate; }
    public void setInfoResponseDate(LocalDateTime infoResponseDate) { this.infoResponseDate = infoResponseDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}