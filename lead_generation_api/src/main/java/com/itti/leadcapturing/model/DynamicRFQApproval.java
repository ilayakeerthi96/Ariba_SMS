package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dynamic_rfq_approvals")
public class DynamicRFQApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

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

    // ==================== ✅ NEW: RFQ DATES (SET BY LOWEST LEVEL APPROVER) ====================
    
    /**
     * Due date for RFQ submission (set by lowest-level approver)
     */
    @Column(name = "rfq_due_date")
    private LocalDateTime rfqDueDate;

    /**
     * Expected delivery date (set by lowest-level approver)
     */
    @Column(name = "rfq_delivery_date")
    private LocalDateTime rfqDeliveryDate;

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

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ✅ NEW: REMINDER TRACKING ====================
    
    /**
     * Date when reminder email was sent to approver
     * Used to track if reminder has been sent and prevent duplicates
     */
    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    /**
     * Number of reminders sent (for future multi-reminder feature)
     */
    @Column(name = "reminder_count", columnDefinition = "INT DEFAULT 0")
    private Integer reminderCount = 0;

    // ==================== CONSTRUCTORS ====================

    public DynamicRFQApproval() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApprovalActionStatus.PENDING;
        this.reminderCount = 0;
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
        if (this.reminderCount == null) {
            this.reminderCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== HELPER METHODS ====================

    public boolean isOnHold() {
        return ApprovalActionStatus.HOLD.equals(this.status);
    }

    public boolean wasHeldAndReleased() {
        return this.holdDate != null && this.releasedDate != null;
    }

    public boolean wasRejected() {
        return ApprovalActionStatus.REJECTED.equals(this.status);
    }

    public String getHoldDisplayInfo() {
        if (this.heldByUser == null) return null;
        
        StringBuilder info = new StringBuilder();
        info.append("HELD by ").append(this.heldByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.holdRemarks != null) {
            info.append(" - Reason: ").append(this.holdRemarks);
        }
        if (this.releasedByUser != null) {
            info.append(" | RELEASED by ").append(this.releasedByUser.getFullName());
            if (this.releaseRemarks != null) {
                info.append(" - ").append(this.releaseRemarks);
            }
        }
        return info.toString();
    }

    public String getRejectDisplayInfo() {
        if (this.rejectedByUser == null) return null;
        
        StringBuilder info = new StringBuilder();
        info.append("REJECTED by ").append(this.rejectedByUser.getFullName());
        info.append(" at ").append(this.hierarchyLevel.getLevelName());
        if (this.rejectRemarks != null) {
            info.append(" - Reason: ").append(this.rejectRemarks);
        }
        return info.toString();
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

    // ✅ NEW: RFQ Date getters/setters
    public LocalDateTime getRfqDueDate() {
        return rfqDueDate;
    }

    public void setRfqDueDate(LocalDateTime rfqDueDate) {
        this.rfqDueDate = rfqDueDate;
    }

    public LocalDateTime getRfqDeliveryDate() {
        return rfqDeliveryDate;
    }

    public void setRfqDeliveryDate(LocalDateTime rfqDeliveryDate) {
        this.rfqDeliveryDate = rfqDeliveryDate;
    }

    // Hold tracking getters/setters
    public HierarchyUser getHeldByUser() {
        return heldByUser;
    }

    public void setHeldByUser(HierarchyUser heldByUser) {
        this.heldByUser = heldByUser;
    }

    public String getHoldRemarks() {
        return holdRemarks;
    }

    public void setHoldRemarks(String holdRemarks) {
        this.holdRemarks = holdRemarks;
    }

    public LocalDateTime getHoldDate() {
        return holdDate;
    }

    public void setHoldDate(LocalDateTime holdDate) {
        this.holdDate = holdDate;
    }

    public HierarchyUser getReleasedByUser() {
        return releasedByUser;
    }

    public void setReleasedByUser(HierarchyUser releasedByUser) {
        this.releasedByUser = releasedByUser;
    }

    public String getReleaseRemarks() {
        return releaseRemarks;
    }

    public void setReleaseRemarks(String releaseRemarks) {
        this.releaseRemarks = releaseRemarks;
    }

    public LocalDateTime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(LocalDateTime releasedDate) {
        this.releasedDate = releasedDate;
    }

    // Reject tracking getters/setters
    public HierarchyUser getRejectedByUser() {
        return rejectedByUser;
    }

    public void setRejectedByUser(HierarchyUser rejectedByUser) {
        this.rejectedByUser = rejectedByUser;
    }

    public String getRejectRemarks() {
        return rejectRemarks;
    }

    public void setRejectRemarks(String rejectRemarks) {
        this.rejectRemarks = rejectRemarks;
    }

    public LocalDateTime getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(LocalDateTime rejectDate) {
        this.rejectDate = rejectDate;
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

    // ==================== ✅ REMINDER TRACKING GETTERS/SETTERS ====================

    /**
     * Get reminder sent date
     */
    public LocalDateTime getReminderSentAt() {
        return reminderSentAt;
    }

    /**
     * Set reminder sent date
     */
    public void setReminderSentAt(LocalDateTime reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }

    /**
     * Get reminder count
     */
    public Integer getReminderCount() {
        return reminderCount;
    }

    /**
     * Set reminder count
     */
    public void setReminderCount(Integer reminderCount) {
        this.reminderCount = reminderCount;
    }

    /**
     * Increment reminder count
     */
    public void incrementReminderCount() {
        if (this.reminderCount == null) {
            this.reminderCount = 0;
        }
        this.reminderCount++;
    }

    /**
     * Check if reminder has been sent
     */
    public boolean hasReminderBeenSent() {
        return this.reminderSentAt != null;
    }
}