
// package com.itti.leadcapturing.dto;

// import lombok.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class POApprovalResponse {

//     private Long id;
//     private Long purchaseOrderId;
//     private String poNumber;
//     private BigDecimal grandTotal;

//     // ✅ Currency from buyer's location (set during PO creation)
//     private String currencyCode;
//     private String currencySymbol;

//     // Hierarchy Level Info
//     private Long hierarchyLevelId;
//     private String hierarchyLevelName;
//     private Integer hierarchyLevelOrder;

//     // Approver Info
//     private Long approverUserId;
//     private String approverUserName;
//     private String approverUserEmail;

//     // Status
//     private String status;
//     private String comments;
//     private LocalDateTime actionDate;
//     private Integer sequenceOrder;
//     private LocalDateTime createdAt;

//     // Buyer / Supplier info
//     private String buyerName;
//     private String supplierName;
//     private String rfqNumber;

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

//     // ==================== RETURN FOR REVISION TRACKING ====================
//     private Long returnedByUserId;
//     private String returnedByUserName;
//     private String returnedByUserEmail;
//     private String returnRemarks;
//     private LocalDateTime returnDate;

//     // ==================== DISPLAY HELPERS ====================
//     private String holdDisplayInfo;
//     private String rejectDisplayInfo;
//     private String returnDisplayInfo;
//     private Boolean isOnHold;
//     private Boolean wasHeldAndReleased;
//     private Boolean wasRejected;

//     private List<POApprovalResponse> approvalHistory;
// }


package com.itti.leadcapturing.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POApprovalResponse {

    private Long id;
    private Long purchaseOrderId;
    private String poNumber;
    private BigDecimal grandTotal;

    // ✅ Currency from buyer's location (set during PO creation)
    private String currencyCode;
    private String currencySymbol;

    // Hierarchy Level Info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;

    // Approver Info
    private Long approverUserId;
    private String approverUserName;
    private String approverUserEmail;

    // Status
    private String status;
    private String comments;
    private LocalDateTime actionDate;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;

    // Buyer / Supplier info
    private String buyerName;
    private String supplierName;
    private String rfqNumber;

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

    // ==================== RETURN FOR REVISION TRACKING ====================
    private Long returnedByUserId;
    private String returnedByUserName;
    private String returnedByUserEmail;
    private String returnRemarks;
    private LocalDateTime returnDate;

    // ==================== DISPLAY HELPERS ====================
    private String holdDisplayInfo;
    private String rejectDisplayInfo;
    private String returnDisplayInfo;
    private Boolean isOnHold;
    private Boolean wasHeldAndReleased;
    private Boolean wasRejected;

    private List<POApprovalResponse> approvalHistory;
}