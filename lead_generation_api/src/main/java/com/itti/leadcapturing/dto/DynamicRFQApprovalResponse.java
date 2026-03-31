package com.itti.leadcapturing.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ UPDATED: Enhanced with full hold/reject tracking
 * DTO for Dynamic RFQ Approval Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicRFQApprovalResponse {

    private Long id;
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    private String status;
    private String comments;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    private LocalDateTime actionDate;
    
    // Approver info
    private Long approverUserId;
    private String approverUserName;
    private String approverUserEmail;
    private String buyerName;

    // ==================== ✅ ENHANCED: HOLD TRACKING ====================
    
    /**
     * User who put RFQ on hold
     */
    private Long heldByUserId;
    private String heldByUserName;
    private String heldByUserEmail;
    
    /**
     * Hold remarks and dates
     */
    private String holdRemarks;
    private LocalDateTime holdDate;
    
    /**
     * User who released the hold
     */
    private Long releasedByUserId;
    private String releasedByUserName;
    private String releasedByUserEmail;
    
    /**
     * Release remarks and date
     */
    private String releaseRemarks;
    private LocalDateTime releasedDate;

    // ==================== ✅ ENHANCED: REJECT TRACKING ====================
    
    /**
     * User who rejected the RFQ
     */
    private Long rejectedByUserId;
    private String rejectedByUserName;
    private String rejectedByUserEmail;
    
    /**
     * Reject remarks and date
     */
    private String rejectRemarks;
    private LocalDateTime rejectDate;

    // ==================== ✅ DISPLAY HELPERS ====================
    
    /**
     * Formatted hold information for display
     */
    private String holdDisplayInfo;
    
    /**
     * Formatted reject information for display
     */
    private String rejectDisplayInfo;
    
    /**
     * Check if currently on hold
     */
    private Boolean isOnHold;
    
    /**
     * Check if was held and released
     */
    private Boolean wasHeldAndReleased;
    
    /**
     * Check if was rejected
     */
    private Boolean wasRejected;

    @Override
    public String toString() {
        return "DynamicRFQApprovalResponse{" +
                "id=" + id +
                ", rfqNumber='" + rfqNumber + '\'' +
                ", hierarchyLevelName='" + hierarchyLevelName + '\'' +
                ", status='" + status + '\'' +
                ", isOnHold=" + isOnHold +
                ", heldBy='" + heldByUserName + '\'' +
                ", releasedBy='" + releasedByUserName + '\'' +
                ", rejectedBy='" + rejectedByUserName + '\'' +
                '}';
    }
}