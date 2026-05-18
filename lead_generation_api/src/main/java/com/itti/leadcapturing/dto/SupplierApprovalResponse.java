// package com.itti.leadcapturing.dto;

// import lombok.*;
// import java.time.LocalDateTime;

// /**
//  * Response DTO for Supplier Approval actions and history.
//  * Supports: APPROVE, REJECT, HOLD, RELEASE_HOLD.
//  * No Return-for-Revision.
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class SupplierApprovalResponse {

//     private Long id;
//     private Long supplierId;
//     private String supplierCompanyName;

//     // Hierarchy level info
//     private Long hierarchyLevelId;
//     private String hierarchyLevelName;
//     private Integer hierarchyLevelOrder;

//     // Approver info
//     private Long approverUserId;
//     private String approverUserName;
//     private String approverUserEmail;

//     // Status and comments
//     private String status;
//     private String comments;
//     private Integer sequenceOrder;
//     private LocalDateTime createdAt;
//     private LocalDateTime actionDate;

//     // ==================== HOLD TRACKING ====================

//     private Long heldByUserId;
//     private String heldByUserName;
//     private String heldByUserEmail;
//     private String holdRemarks;
//     private LocalDateTime holdDate;

//     private Long releasedByUserId;
//     private String releasedByUserName;
//     private String releasedByUserEmail;
//     private String releaseRemarks;
//     private LocalDateTime releasedDate;

//     // ==================== REJECT TRACKING ====================

//     private Long rejectedByUserId;
//     private String rejectedByUserName;
//     private String rejectedByUserEmail;
//     private String rejectRemarks;
//     private LocalDateTime rejectDate;

//     // ==================== DISPLAY HELPERS ====================

//     private String holdDisplayInfo;
//     private String rejectDisplayInfo;
//     private Boolean isOnHold;
//     private Boolean wasHeldAndReleased;
//     private Boolean wasRejected;
// }


package com.itti.leadcapturing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierApprovalResponse {

    private Long id;
    private Long supplierId;
    private String supplierCompanyName;
    private String supplierContactEmail;
    private String supplierContactName;
    private String industrySector;

    // Hierarchy level info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;

    // Approver info
    private Long approverUserId;
    private String approverUserName;
    private String approverUserEmail;

    // Status and comments
    private String status;
    private String comments;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    private LocalDateTime actionDate;

    // ==================== HOLD TRACKING ====================
    private Long heldByUserId;
    private String heldByUserName;
    private String heldByUserEmail;
    private String holdRemarks;
    private LocalDateTime holdDate;

    private Long releasedByUserId;
    private String releasedByUserName;
    private String releasedByUserEmail;
    private String releaseRemarks;
    private LocalDateTime releasedDate;

    // ==================== REJECT TRACKING ====================
    private Long rejectedByUserId;
    private String rejectedByUserName;
    private String rejectedByUserEmail;
    private String rejectRemarks;
    private LocalDateTime rejectDate;

    // ==================== NEED MORE INFO TRACKING ====================
    private Long infoRequestedByUserId;
    private String infoRequestedByUserName;
    private String infoRequestedByUserEmail;
    private String infoRequest;
    private LocalDateTime infoRequestDate;
    private String infoResponse;
    private LocalDateTime infoResponseDate;
    private String needMoreInfoDisplayInfo;
    private Boolean isNeedMoreInfo;

    // ==================== DISPLAY HELPERS ====================
    private String holdDisplayInfo;
    private String rejectDisplayInfo;
    private Boolean isOnHold;
    private Boolean wasHeldAndReleased;
    private Boolean wasRejected;
}